package com.easymusic.service.impl;

import com.easymusic.api.ImageCreateApi;
import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.constants.Constants;
import com.easymusic.entity.dto.ImageCreateResultDTO;
import com.easymusic.entity.dto.PromptAssistResultDTO;
import com.easymusic.entity.enums.MusicCoverSourceEnum;
import com.easymusic.entity.enums.MusicCoverStatusEnum;
import com.easymusic.entity.enums.MusicStatusEnum;
import com.easymusic.entity.enums.ResponseCodeEnum;
import com.easymusic.entity.enums.UserIntegralRecordTypeEnum;
import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.po.MusicCreation;
import com.easymusic.entity.po.MusicInfo;
import com.easymusic.entity.po.PromptOptimizeRecord;
import com.easymusic.entity.po.SysDict;
import com.easymusic.entity.query.MusicCreationQuery;
import com.easymusic.entity.query.MusicInfoQuery;
import com.easymusic.exception.BusinessException;
import com.easymusic.mappers.MusicCoverCreationMapper;
import com.easymusic.mappers.MusicCreationMapper;
import com.easymusic.mappers.MusicInfoMapper;
import com.easymusic.mappers.PromptOptimizeRecordMapper;
import com.easymusic.redis.RedisComponent;
import com.easymusic.service.ExternalAiConfigService;
import com.easymusic.service.MusicCoverService;
import com.easymusic.service.SysDictService;
import com.easymusic.service.UserIntegralRecordService;
import com.easymusic.utils.FileUtils;
import com.easymusic.utils.JsonUtils;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("musicCoverService")
@Slf4j
public class MusicCoverServiceImpl implements MusicCoverService {

    private static final String COVER_PRICE_DICT_CODE = "cover_price";

    private static final String COVER_PRICE_DEFAULT_CODE = "default";

    @Resource
    private MusicInfoMapper<MusicInfo, MusicInfoQuery> musicInfoMapper;

    @Resource
    private MusicCreationMapper<MusicCreation, MusicCreationQuery> musicCreationMapper;

    @Resource
    private PromptOptimizeRecordMapper promptOptimizeRecordMapper;

    @Resource
    private MusicCoverCreationMapper musicCoverCreationMapper;

    @Resource
    private ImageCreateApi imageCreateApi;

    @Resource
    private FileUtils fileUtils;

    @Resource
    private UserIntegralRecordService userIntegralRecordService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private SysDictService sysDictService;

    @Resource
    private ExternalAiConfigService externalAiConfigService;

    @Override
    public MusicCoverCreation generateCover(String userId, String musicId) {
        MusicInfo musicInfo = musicInfoMapper.selectByMusicId(musicId);
        if (musicInfo == null || !userId.equals(musicInfo.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (!MusicStatusEnum.CREATED.getStatus().equals(musicInfo.getMusicStatus())) {
            throw new BusinessException("作品尚未生成完成，不能生成AI封面");
        }

        MusicCreation musicCreation = musicCreationMapper.selectByCreationId(musicInfo.getCreationId());
        CoverPromptContext promptContext = buildPromptContext(musicInfo, musicCreation);
        Integer coverIntegral = resolveCoverIntegral();

        String coverId = StringTools.getRandomString(Constants.LENGTH_15);
        Date createTime = new Date();

        MusicCoverCreation coverRecord = new MusicCoverCreation();
        coverRecord.setCoverId(coverId);
        coverRecord.setMusicId(musicInfo.getMusicId());
        coverRecord.setUserId(userId);
        coverRecord.setCreationId(musicInfo.getCreationId());
        coverRecord.setPrompt(promptContext.prompt());
        coverRecord.setStyle(promptContext.style());
        coverRecord.setModel(resolvePlannedModel());
        coverRecord.setIntegral(coverIntegral);
        coverRecord.setStatus(MusicCoverStatusEnum.CREATING.getStatus());
        coverRecord.setCreateTime(createTime);
        musicCoverCreationMapper.insert(coverRecord);

        boolean integralCharged = false;
        try {
            if (coverIntegral > 0) {
                userIntegralRecordService.changeUserIntegral(UserIntegralRecordTypeEnum.CREATE_COVER, coverId, userId, -coverIntegral, null);
                integralCharged = true;
            }
            ImageCreateResultDTO imageResult = imageCreateApi.generate(promptContext.prompt(), promptContext.style(), promptContext.title());
            String storedCoverPath = saveCoverFile(coverId, imageResult);

            MusicInfo updateMusicInfo = new MusicInfo();
            updateMusicInfo.setCover(appendTimestamp(storedCoverPath));
            updateMusicInfo.setCoverSource(MusicCoverSourceEnum.AI_GENERATED.getSource());
            updateMusicInfo.setCoverGenerateCount((musicInfo.getCoverGenerateCount() == null ? 0 : musicInfo.getCoverGenerateCount()) + 1);
            musicInfoMapper.updateByMusicId(updateMusicInfo, musicId);

            MusicCoverCreation updateRecord = new MusicCoverCreation();
            updateRecord.setTaskId(imageResult.getTaskId());
            updateRecord.setCoverUrl(storedCoverPath);
            updateRecord.setModel(imageResult.getModel());
            updateRecord.setStatus(MusicCoverStatusEnum.SUCCESS.getStatus());
            updateRecord.setFinishTime(new Date());
            musicCoverCreationMapper.updateByCoverId(updateRecord, coverId);

            coverRecord.setTaskId(imageResult.getTaskId());
            coverRecord.setCoverUrl(storedCoverPath);
            coverRecord.setModel(imageResult.getModel());
            coverRecord.setStatus(MusicCoverStatusEnum.SUCCESS.getStatus());
            coverRecord.setFinishTime(updateRecord.getFinishTime());
            return coverRecord;
        } catch (RuntimeException e) {
            if (integralCharged) {
                try {
                    userIntegralRecordService.changeUserIntegral(UserIntegralRecordTypeEnum.CREATE_COVER_BACK, coverId, userId, coverIntegral, null);
                } catch (RuntimeException refundException) {
                    log.error("Refund cover integral failed, coverId: {}", coverId, refundException);
                }
            }
            MusicCoverCreation updateRecord = new MusicCoverCreation();
            updateRecord.setStatus(MusicCoverStatusEnum.FAIL.getStatus());
            updateRecord.setFailReason(cutMessage(e.getMessage()));
            updateRecord.setFinishTime(new Date());
            musicCoverCreationMapper.updateByCoverId(updateRecord, coverId);
            throw e;
        }
    }

    private CoverPromptContext buildPromptContext(MusicInfo musicInfo, MusicCreation musicCreation) {
        String title = resolveTitle(musicInfo, musicCreation);
        String style = "音乐封面插画";
        String prompt = null;

        if (musicCreation != null && !StringTools.isEmpty(musicCreation.getPromptRecordId())) {
            PromptOptimizeRecord promptRecord = promptOptimizeRecordMapper.selectByRecordId(musicCreation.getPromptRecordId());
            if (promptRecord != null && !StringTools.isEmpty(promptRecord.getStructuredResult())) {
                try {
                    PromptAssistResultDTO promptAssistResult = JsonUtils.convertJson2Obj(promptRecord.getStructuredResult(), PromptAssistResultDTO.class);
                    if (!StringTools.isEmpty(promptAssistResult.getImagePrompt())) {
                        prompt = promptAssistResult.getImagePrompt();
                    }
                    if (!StringTools.isEmpty(promptAssistResult.getVisualStyle())) {
                        style = promptAssistResult.getVisualStyle();
                    }
                    if (StringTools.isEmpty(musicInfo.getMusicTitle())
                            && promptAssistResult.getTitleSuggestions() != null
                            && !promptAssistResult.getTitleSuggestions().isEmpty()
                            && !StringTools.isEmpty(promptAssistResult.getTitleSuggestions().get(0))) {
                        title = promptAssistResult.getTitleSuggestions().get(0);
                    }
                } catch (RuntimeException e) {
                    log.warn("Parse prompt assist result failed, cover fallback prompt enabled: {}", e.getMessage());
                }
            }
        }

        if (StringTools.isEmpty(prompt)) {
            prompt = buildFallbackPrompt(title, style, musicInfo, musicCreation);
        }
        return new CoverPromptContext(prompt, style, title);
    }

    private String buildFallbackPrompt(String title, String style, MusicInfo musicInfo, MusicCreation musicCreation) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("为歌曲《").append(title).append("》生成专辑封面，整体风格为").append(style).append("。");
        if (musicCreation != null && !StringTools.isEmpty(musicCreation.getOriginPrompt())) {
            promptBuilder.append("创作意图：").append(musicCreation.getOriginPrompt()).append("。");
        } else if (musicCreation != null && !StringTools.isEmpty(musicCreation.getPrompt())) {
            promptBuilder.append("音乐提示词：").append(musicCreation.getPrompt()).append("。");
        }
        if (musicInfo != null && !StringTools.isEmpty(musicInfo.getLyrics())) {
            String lyricPreview = musicInfo.getLyrics();
            if (lyricPreview.length() > 120) {
                lyricPreview = lyricPreview.substring(0, 120);
            }
            promptBuilder.append("参考歌词氛围：").append(lyricPreview).append("。");
        }
        promptBuilder.append("要求画面中心构图、适合作品发布页展示。");
        return promptBuilder.toString();
    }

    private String resolveTitle(MusicInfo musicInfo, MusicCreation musicCreation) {
        if (musicInfo != null && !StringTools.isEmpty(musicInfo.getMusicTitle())) {
            return musicInfo.getMusicTitle().trim();
        }
        if (musicCreation != null && !StringTools.isEmpty(musicCreation.getOriginPrompt())) {
            String originPrompt = musicCreation.getOriginPrompt().trim();
            return originPrompt.length() > 18 ? originPrompt.substring(0, 18) : originPrompt;
        }
        if (musicCreation != null && !StringTools.isEmpty(musicCreation.getPrompt())) {
            String prompt = musicCreation.getPrompt().trim();
            return prompt.length() > 18 ? prompt.substring(0, 18) : prompt;
        }
        return "EasyMusic";
    }

    private Integer resolveCoverIntegral() {
        List<SysDict> dictList = redisComponent.getDictSubList(COVER_PRICE_DICT_CODE);
        if (dictList == null || dictList.isEmpty()) {
            sysDictService.getDictFromCache();
            dictList = redisComponent.getDictSubList(COVER_PRICE_DICT_CODE);
        }
        if (dictList == null || dictList.isEmpty()) {
            return 0;
        }
        Optional<SysDict> defaultOption = dictList.stream().filter(item -> COVER_PRICE_DEFAULT_CODE.equals(item.getDictCode())).findFirst();
        SysDict selected = defaultOption.orElse(dictList.get(0));
        if (selected == null || StringTools.isEmpty(selected.getDictValue())) {
            return 0;
        }
        try {
            return Integer.parseInt(selected.getDictValue());
        } catch (NumberFormatException e) {
            log.warn("Invalid cover integral config: {}", selected.getDictValue());
            return 0;
        }
    }

    private String resolvePlannedModel() {
        ExternalAiChannelConfig imageConfig = externalAiConfigService.getImageAiConfig();
        if (imageConfig == null || StringTools.isEmpty(imageConfig.getModel())) {
            return "fallback";
        }
        return imageConfig.getModel();
    }

    private String saveCoverFile(String coverId, ImageCreateResultDTO imageResult) {
        String suffix = StringTools.isEmpty(imageResult.getFileSuffix()) ? Constants.IMAGE_SUFFIX : imageResult.getFileSuffix();
        String fileName = coverId + suffix;
        if (imageResult.getImageBytes() != null && imageResult.getImageBytes().length > 0) {
            return fileUtils.uploadBytes(imageResult.getImageBytes(), null, fileName);
        }
        if (!StringTools.isEmpty(imageResult.getImageUrl())) {
            return fileUtils.downloadFile(imageResult.getImageUrl(), suffix);
        }
        throw new BusinessException("封面生成结果为空");
    }

    private String appendTimestamp(String filePath) {
        if (StringTools.isEmpty(filePath)) {
            return filePath;
        }
        return filePath + (filePath.contains("?") ? "&" : "?") + "t=" + System.currentTimeMillis();
    }

    private String cutMessage(String message) {
        if (StringTools.isEmpty(message)) {
            return "cover generate failed";
        }
        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    private record CoverPromptContext(String prompt, String style, String title) {
    }
}

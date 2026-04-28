package com.easymusic.service.impl;

import com.easymusic.api.ImageCreateApi;
import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.dto.ImageCreateResultDTO;
import com.easymusic.entity.dto.PromptAssistResultDTO;
import com.easymusic.entity.enums.MusicCoverSourceEnum;
import com.easymusic.entity.enums.MusicCoverStatusEnum;
import com.easymusic.entity.enums.MusicStatusEnum;
import com.easymusic.entity.enums.UserIntegralRecordTypeEnum;
import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.po.MusicCreation;
import com.easymusic.entity.po.MusicInfo;
import com.easymusic.entity.po.PromptOptimizeRecord;
import com.easymusic.entity.po.SysDict;
import com.easymusic.entity.query.MusicCreationQuery;
import com.easymusic.entity.query.MusicInfoQuery;
import com.easymusic.mappers.MusicCoverCreationMapper;
import com.easymusic.mappers.MusicCreationMapper;
import com.easymusic.mappers.MusicInfoMapper;
import com.easymusic.mappers.PromptOptimizeRecordMapper;
import com.easymusic.redis.RedisComponent;
import com.easymusic.service.ExternalAiConfigService;
import com.easymusic.service.SysDictService;
import com.easymusic.service.UserIntegralRecordService;
import com.easymusic.utils.FileUtils;
import com.easymusic.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicCoverServiceImplTest {

    @Mock
    private MusicInfoMapper<MusicInfo, MusicInfoQuery> musicInfoMapper;

    @Mock
    private MusicCreationMapper<MusicCreation, MusicCreationQuery> musicCreationMapper;

    @Mock
    private PromptOptimizeRecordMapper promptOptimizeRecordMapper;

    @Mock
    private MusicCoverCreationMapper musicCoverCreationMapper;

    @Mock
    private ImageCreateApi imageCreateApi;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private UserIntegralRecordService userIntegralRecordService;

    @Mock
    private RedisComponent redisComponent;

    @Mock
    private SysDictService sysDictService;

    @Mock
    private ExternalAiConfigService externalAiConfigService;

    @InjectMocks
    private MusicCoverServiceImpl musicCoverService;

    @Test
    void generateCover_shouldChargeUpdateAndPersistSuccessRecord() {
        AtomicReference<MusicCoverCreation> insertedSnapshotRef = new AtomicReference<>();
        MusicInfo musicInfo = buildMusicInfo();
        musicInfo.setMusicTitle(null);
        musicInfo.setCoverGenerateCount(2);
        MusicCreation musicCreation = buildMusicCreation();
        musicCreation.setPromptRecordId("record-1");
        PromptOptimizeRecord promptRecord = new PromptOptimizeRecord();
        promptRecord.setRecordId("record-1");
        promptRecord.setStructuredResult(JsonUtils.convertObj2Json(buildPromptAssistResult()));
        ImageCreateResultDTO imageResult = new ImageCreateResultDTO();
        imageResult.setImageBytes(new byte[]{1, 2, 3});
        imageResult.setFileSuffix(".png");
        imageResult.setTaskId("task-1");
        imageResult.setModel("image-model-online");
        ExternalAiChannelConfig imageConfig = new ExternalAiChannelConfig();
        imageConfig.setModel("image-model-plan");

        when(musicInfoMapper.selectByMusicId("music-1")).thenReturn(musicInfo);
        when(musicCreationMapper.selectByCreationId("creation-1")).thenReturn(musicCreation);
        when(promptOptimizeRecordMapper.selectByRecordId("record-1")).thenReturn(promptRecord);
        when(redisComponent.getDictSubList("cover_price")).thenReturn(List.of(buildCoverPriceDict("10")));
        when(externalAiConfigService.getImageAiConfig()).thenReturn(imageConfig);
        when(imageCreateApi.generate("海边落日插画封面", "赛博手绘", "暮色海岸")).thenReturn(imageResult);
        when(fileUtils.uploadBytes(eq(imageResult.getImageBytes()), eq(null), anyString())).thenReturn("202604/cover.png");
        doAnswer(invocation -> {
            MusicCoverCreation bean = invocation.getArgument(0);
            insertedSnapshotRef.set(copyCoverRecord(bean));
            return 1;
        }).when(musicCoverCreationMapper).insert(any(MusicCoverCreation.class));

        MusicCoverCreation result = musicCoverService.generateCover("user-1", "music-1");

        verify(musicCoverCreationMapper).insert(any(MusicCoverCreation.class));
        MusicCoverCreation insertedRecord = insertedSnapshotRef.get();
        assertNotNull(insertedRecord);
        assertEquals("music-1", insertedRecord.getMusicId());
        assertEquals("user-1", insertedRecord.getUserId());
        assertEquals("creation-1", insertedRecord.getCreationId());
        assertEquals("海边落日插画封面", insertedRecord.getPrompt());
        assertEquals("赛博手绘", insertedRecord.getStyle());
        assertEquals("image-model-plan", insertedRecord.getModel());
        assertEquals(10, insertedRecord.getIntegral());
        assertEquals(MusicCoverStatusEnum.CREATING.getStatus(), insertedRecord.getStatus());
        assertNotNull(insertedRecord.getCreateTime());

        verify(userIntegralRecordService).changeUserIntegral(
                UserIntegralRecordTypeEnum.CREATE_COVER,
                insertedRecord.getCoverId(),
                "user-1",
                -10,
                null
        );
        verify(fileUtils).uploadBytes(eq(imageResult.getImageBytes()), eq(null), argThat(fileName -> fileName.endsWith(".png")));

        ArgumentCaptor<MusicInfo> musicUpdateCaptor = ArgumentCaptor.forClass(MusicInfo.class);
        verify(musicInfoMapper).updateByMusicId(musicUpdateCaptor.capture(), eq("music-1"));
        MusicInfo updatedMusic = musicUpdateCaptor.getValue();
        assertTrue(updatedMusic.getCover().startsWith("202604/cover.png?t="));
        assertEquals(MusicCoverSourceEnum.AI_GENERATED.getSource(), updatedMusic.getCoverSource());
        assertEquals(3, updatedMusic.getCoverGenerateCount());

        ArgumentCaptor<MusicCoverCreation> successUpdateCaptor = ArgumentCaptor.forClass(MusicCoverCreation.class);
        verify(musicCoverCreationMapper).updateByCoverId(successUpdateCaptor.capture(), eq(insertedRecord.getCoverId()));
        MusicCoverCreation successUpdate = successUpdateCaptor.getValue();
        assertEquals("task-1", successUpdate.getTaskId());
        assertEquals("202604/cover.png", successUpdate.getCoverUrl());
        assertEquals("image-model-online", successUpdate.getModel());
        assertEquals(MusicCoverStatusEnum.SUCCESS.getStatus(), successUpdate.getStatus());
        assertNotNull(successUpdate.getFinishTime());

        verify(userIntegralRecordService, never()).changeUserIntegral(
                eq(UserIntegralRecordTypeEnum.CREATE_COVER_BACK),
                anyString(),
                anyString(),
                anyInt(),
                any()
        );

        assertEquals(insertedRecord.getCoverId(), result.getCoverId());
        assertEquals(MusicCoverStatusEnum.SUCCESS.getStatus(), result.getStatus());
        assertEquals("202604/cover.png", result.getCoverUrl());
        assertEquals("task-1", result.getTaskId());
    }

    @Test
    void generateCover_shouldRefundAndMarkFailedRecordWhenImageGenerationFails() {
        AtomicReference<MusicCoverCreation> insertedSnapshotRef = new AtomicReference<>();
        MusicInfo musicInfo = buildMusicInfo();
        MusicCreation musicCreation = buildMusicCreation();
        ExternalAiChannelConfig imageConfig = new ExternalAiChannelConfig();
        imageConfig.setModel("image-model-plan");

        when(musicInfoMapper.selectByMusicId("music-1")).thenReturn(musicInfo);
        when(musicCreationMapper.selectByCreationId("creation-1")).thenReturn(musicCreation);
        when(redisComponent.getDictSubList("cover_price")).thenReturn(List.of(buildCoverPriceDict("10")));
        when(externalAiConfigService.getImageAiConfig()).thenReturn(imageConfig);
        when(imageCreateApi.generate(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("remote image error"));
        doAnswer(invocation -> {
            MusicCoverCreation bean = invocation.getArgument(0);
            insertedSnapshotRef.set(copyCoverRecord(bean));
            return 1;
        }).when(musicCoverCreationMapper).insert(any(MusicCoverCreation.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> musicCoverService.generateCover("user-1", "music-1"));
        assertEquals("remote image error", exception.getMessage());

        verify(musicCoverCreationMapper).insert(any(MusicCoverCreation.class));
        MusicCoverCreation insertedRecord = insertedSnapshotRef.get();
        assertNotNull(insertedRecord);
        String coverId = insertedRecord.getCoverId();

        verify(userIntegralRecordService).changeUserIntegral(
                UserIntegralRecordTypeEnum.CREATE_COVER,
                coverId,
                "user-1",
                -10,
                null
        );
        verify(userIntegralRecordService).changeUserIntegral(
                UserIntegralRecordTypeEnum.CREATE_COVER_BACK,
                coverId,
                "user-1",
                10,
                null
        );
        verify(musicInfoMapper, never()).updateByMusicId(any(), anyString());

        ArgumentCaptor<MusicCoverCreation> failUpdateCaptor = ArgumentCaptor.forClass(MusicCoverCreation.class);
        verify(musicCoverCreationMapper).updateByCoverId(failUpdateCaptor.capture(), eq(coverId));
        MusicCoverCreation failedUpdate = failUpdateCaptor.getValue();
        assertEquals(MusicCoverStatusEnum.FAIL.getStatus(), failedUpdate.getStatus());
        assertEquals("remote image error", failedUpdate.getFailReason());
        assertNotNull(failedUpdate.getFinishTime());
    }

    private MusicInfo buildMusicInfo() {
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setMusicId("music-1");
        musicInfo.setUserId("user-1");
        musicInfo.setCreationId("creation-1");
        musicInfo.setMusicStatus(MusicStatusEnum.CREATED.getStatus());
        musicInfo.setMusicTitle("夜航");
        musicInfo.setLyrics("城市灯光流过窗边");
        musicInfo.setCoverGenerateCount(0);
        return musicInfo;
    }

    private MusicCreation buildMusicCreation() {
        MusicCreation musicCreation = new MusicCreation();
        musicCreation.setCreationId("creation-1");
        musicCreation.setPrompt("lofi sunset beat");
        musicCreation.setOriginPrompt("写一首黄昏海边的放松歌曲");
        return musicCreation;
    }

    private PromptAssistResultDTO buildPromptAssistResult() {
        PromptAssistResultDTO result = new PromptAssistResultDTO();
        result.setImagePrompt("海边落日插画封面");
        result.setVisualStyle("赛博手绘");
        result.setTitleSuggestions(List.of("暮色海岸"));
        return result;
    }

    private SysDict buildCoverPriceDict(String price) {
        SysDict sysDict = new SysDict();
        sysDict.setDictCode("default");
        sysDict.setDictValue(price);
        return sysDict;
    }

    private MusicCoverCreation copyCoverRecord(MusicCoverCreation source) {
        MusicCoverCreation target = new MusicCoverCreation();
        target.setCoverId(source.getCoverId());
        target.setMusicId(source.getMusicId());
        target.setUserId(source.getUserId());
        target.setCreationId(source.getCreationId());
        target.setPrompt(source.getPrompt());
        target.setStyle(source.getStyle());
        target.setModel(source.getModel());
        target.setIntegral(source.getIntegral());
        target.setTaskId(source.getTaskId());
        target.setCoverUrl(source.getCoverUrl());
        target.setStatus(source.getStatus());
        target.setFailReason(source.getFailReason());
        target.setCreateTime(source.getCreateTime());
        target.setFinishTime(source.getFinishTime());
        return target;
    }
}

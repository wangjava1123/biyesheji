package com.easymusic.api;

import com.alibaba.fastjson2.JSONPath;
import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.dto.PromptAssistResultDTO;
import com.easymusic.service.ExternalAiConfigService;
import com.easymusic.utils.JsonUtils;
import com.easymusic.utils.OKHttpUtils;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("promptGenerateApi")
@Slf4j
public class PromptGenerateApiOpenAiImpl implements PromptGenerateApi {

    private static final String DEFAULT_PROVIDER = "conversation-ai";

    @Resource
    private ExternalAiConfigService externalAiConfigService;

    @Override
    public PromptAssistResultDTO generate(String rawPrompt) {
        String safePrompt = normalizeText(rawPrompt);
        ExternalAiChannelConfig aiConfig = externalAiConfigService.getConversationAiConfig();
        if (aiConfig == null || !aiConfig.isReady()) {
            return buildFallbackResult(safePrompt, "local-fallback", "fallback");
        }
        try {
            return requestRemoteResult(safePrompt, aiConfig);
        } catch (Exception e) {
            log.warn("Prompt assist remote request failed, fallback enabled: {}", e.getMessage());
            return buildFallbackResult(safePrompt, aiConfig.getModel(), "fallback");
        }
    }

    private PromptAssistResultDTO requestRemoteResult(String rawPrompt, ExternalAiChannelConfig aiConfig) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", formatAuthorization(aiConfig.getKey()));
        headers.put("Content-Type", "application/json; charset=utf-8");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system",
                "You are a music prompt assistant. Return JSON only with fields rawPrompt,musicPrompt,imagePrompt,titleSuggestions,musicGenre,musicEmotion,musicSex,tags,visualStyle."));
        messages.add(createMessage("user", rawPrompt));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("temperature", 0.4);
        requestBody.put("messages", messages);

        String response = OKHttpUtils.postRequest4Json(resolveChatCompletionUrl(aiConfig.getUrl()), headers, JsonUtils.convertObj2Json(requestBody));
        String content = (String) JSONPath.eval(response, "$.choices[0].message.content");
        if (StringTools.isEmpty(content)) {
            throw new IllegalStateException("Prompt assist response content is empty");
        }
        PromptAssistResultDTO result = JsonUtils.convertJson2Obj(extractJson(content), PromptAssistResultDTO.class);
        return normalizeResult(result, rawPrompt, aiConfig.getModel(), "remote");
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String formatAuthorization(String key) {
        if (StringTools.isEmpty(key)) {
            return "";
        }
        if (key.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return key;
        }
        return "Bearer " + key;
    }

    private String resolveChatCompletionUrl(String baseUrl) {
        String url = normalizeText(baseUrl);
        if (url.endsWith("/chat/completions")) {
            return url;
        }
        if (url.endsWith("/")) {
            return url + "chat/completions";
        }
        return url + "/chat/completions";
    }

    private String extractJson(String content) {
        String text = content.trim();
        if (text.startsWith("```")) {
            int firstLineBreak = text.indexOf('\n');
            if (firstLineBreak > -1) {
                text = text.substring(firstLineBreak + 1);
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
        }
        int startIndex = text.indexOf('{');
        int endIndex = text.lastIndexOf('}');
        if (startIndex > -1 && endIndex > startIndex) {
            return text.substring(startIndex, endIndex + 1);
        }
        return text;
    }

    private PromptAssistResultDTO normalizeResult(PromptAssistResultDTO result, String rawPrompt, String model, String source) {
        if (result == null) {
            return buildFallbackResult(rawPrompt, model, source);
        }
        if (StringTools.isEmpty(result.getRawPrompt())) {
            result.setRawPrompt(rawPrompt);
        }
        if (StringTools.isEmpty(result.getMusicGenre())) {
            result.setMusicGenre(inferGenre(rawPrompt));
        }
        if (StringTools.isEmpty(result.getMusicEmotion())) {
            result.setMusicEmotion(inferEmotion(rawPrompt));
        }
        if (StringTools.isEmpty(result.getMusicSex())) {
            result.setMusicSex(inferVoice(rawPrompt));
        }
        if (StringTools.isEmpty(result.getVisualStyle())) {
            result.setVisualStyle(inferVisualStyle(rawPrompt));
        }
        if (StringTools.isEmpty(result.getMusicPrompt())) {
            result.setMusicPrompt(buildMusicPrompt(rawPrompt, result.getMusicGenre(), result.getMusicEmotion(), result.getMusicSex()));
        }
        if (StringTools.isEmpty(result.getImagePrompt())) {
            result.setImagePrompt(buildImagePrompt(rawPrompt, result.getVisualStyle(), result.getMusicGenre(), result.getMusicEmotion()));
        }
        if (result.getTitleSuggestions() == null || result.getTitleSuggestions().isEmpty()) {
            result.setTitleSuggestions(buildTitleSuggestions(rawPrompt));
        }
        if (result.getTags() == null || result.getTags().isEmpty()) {
            result.setTags(buildTags(rawPrompt, result.getMusicGenre(), result.getMusicEmotion()));
        }
        result.setProvider(DEFAULT_PROVIDER);
        result.setModel(model);
        result.setResponseSource(source);
        return result;
    }

    private PromptAssistResultDTO buildFallbackResult(String rawPrompt, String model, String source) {
        String genre = inferGenre(rawPrompt);
        String emotion = inferEmotion(rawPrompt);
        String voice = inferVoice(rawPrompt);
        String visualStyle = inferVisualStyle(rawPrompt);
        PromptAssistResultDTO result = new PromptAssistResultDTO();
        result.setRawPrompt(rawPrompt);
        result.setMusicGenre(genre);
        result.setMusicEmotion(emotion);
        result.setMusicSex(voice);
        result.setVisualStyle(visualStyle);
        result.setMusicPrompt(buildMusicPrompt(rawPrompt, genre, emotion, voice));
        result.setImagePrompt(buildImagePrompt(rawPrompt, visualStyle, genre, emotion));
        result.setTitleSuggestions(buildTitleSuggestions(rawPrompt));
        result.setTags(buildTags(rawPrompt, genre, emotion));
        result.setProvider(DEFAULT_PROVIDER);
        result.setModel(model);
        result.setResponseSource(source);
        return result;
    }

    private String buildMusicPrompt(String rawPrompt, String genre, String emotion, String voice) {
        return rawPrompt + "；风格：" + genre + "；情绪：" + emotion + "；人声：" + voice + "；要求旋律完整、结构清晰、适合正式发布。";
    }

    private String buildImagePrompt(String rawPrompt, String visualStyle, String genre, String emotion) {
        return "Album cover for " + rawPrompt + ", style " + visualStyle + ", genre " + genre + ", emotion " + emotion + ", high detail, centered composition.";
    }

    private List<String> buildTitleSuggestions(String rawPrompt) {
        String shortText = rawPrompt.length() > 12 ? rawPrompt.substring(0, 12) : rawPrompt;
        List<String> titleList = new ArrayList<>();
        titleList.add(shortText);
        titleList.add("关于" + shortText);
        titleList.add(shortText + "之夜");
        return titleList;
    }

    private List<String> buildTags(String rawPrompt, String genre, String emotion) {
        Set<String> tagSet = new LinkedHashSet<>();
        tagSet.add(genre);
        tagSet.add(emotion);
        for (String keyword : splitKeywords(rawPrompt)) {
            tagSet.add(keyword);
            if (tagSet.size() >= 4) {
                break;
            }
        }
        return new ArrayList<>(tagSet);
    }

    private List<String> splitKeywords(String rawPrompt) {
        String[] parts = rawPrompt.split("[,，。；;、\\s]+");
        List<String> keywords = new ArrayList<>();
        for (String part : parts) {
            String text = part.trim();
            if (StringTools.isEmpty(text)) {
                continue;
            }
            keywords.add(text.length() > 12 ? text.substring(0, 12) : text);
        }
        if (keywords.isEmpty()) {
            keywords.add(rawPrompt.length() > 12 ? rawPrompt.substring(0, 12) : rawPrompt);
        }
        return keywords;
    }

    private String inferGenre(String rawPrompt) {
        String lowerCasePrompt = rawPrompt.toLowerCase();
        if (rawPrompt.contains("国风") || rawPrompt.contains("古风")) {
            return "国风";
        }
        if (rawPrompt.contains("摇滚") || lowerCasePrompt.contains("rock")) {
            return "摇滚";
        }
        if (rawPrompt.contains("民谣") || lowerCasePrompt.contains("folk")) {
            return "民谣";
        }
        if (rawPrompt.contains("说唱") || lowerCasePrompt.contains("rap")) {
            return "说唱";
        }
        if (rawPrompt.contains("电子") || lowerCasePrompt.contains("edm")) {
            return "电子";
        }
        if (rawPrompt.contains("爵士") || lowerCasePrompt.contains("jazz")) {
            return "爵士";
        }
        return "流行";
    }

    private String inferEmotion(String rawPrompt) {
        String lowerCasePrompt = rawPrompt.toLowerCase();
        if (rawPrompt.contains("伤感") || rawPrompt.contains("失恋") || lowerCasePrompt.contains("sad")) {
            return "伤感";
        }
        if (rawPrompt.contains("治愈") || rawPrompt.contains("温暖")) {
            return "治愈";
        }
        if (rawPrompt.contains("热血") || rawPrompt.contains("燃")) {
            return "热血";
        }
        if (rawPrompt.contains("浪漫") || rawPrompt.contains("甜")) {
            return "浪漫";
        }
        if (rawPrompt.contains("安静") || rawPrompt.contains("宁静")) {
            return "安静";
        }
        return "氛围感";
    }

    private String inferVoice(String rawPrompt) {
        String lowerCasePrompt = rawPrompt.toLowerCase();
        if (rawPrompt.contains("纯音乐") || lowerCasePrompt.contains("instrumental")) {
            return "纯音乐";
        }
        if (rawPrompt.contains("男声")) {
            return "男声";
        }
        if (rawPrompt.contains("合唱") || rawPrompt.contains("对唱")) {
            return "合唱";
        }
        if (rawPrompt.contains("童声")) {
            return "童声";
        }
        return "女声";
    }

    private String inferVisualStyle(String rawPrompt) {
        if (rawPrompt.contains("赛博")) {
            return "赛博霓虹";
        }
        if (rawPrompt.contains("国风") || rawPrompt.contains("古风")) {
            return "国风插画";
        }
        if (rawPrompt.contains("动漫")) {
            return "动漫海报";
        }
        if (rawPrompt.contains("写实")) {
            return "写实摄影";
        }
        return "电影感插画";
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }
}

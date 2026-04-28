package com.easymusic.api;

import com.alibaba.fastjson2.JSONPath;
import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.constants.Constants;
import com.easymusic.entity.dto.ImageCreateResultDTO;
import com.easymusic.service.ExternalAiConfigService;
import com.easymusic.utils.JsonUtils;
import com.easymusic.utils.OKHttpUtils;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("imageCreateApi")
@Slf4j
public class ImageCreateApiOpenAiImpl implements ImageCreateApi {

    private static final String DEFAULT_PROVIDER = "image-ai";

    @Resource
    private ExternalAiConfigService externalAiConfigService;

    @Override
    public ImageCreateResultDTO generate(String prompt, String style, String title) {
        String finalPrompt = normalizePrompt(prompt, style, title);
        ExternalAiChannelConfig aiConfig = externalAiConfigService.getImageAiConfig();
        if (aiConfig == null || !aiConfig.isReady()) {
            return buildFallbackResult(finalPrompt, title, style, "local-fallback", "fallback");
        }
        try {
            return requestRemoteResult(finalPrompt, title, style, aiConfig);
        } catch (Exception e) {
            log.warn("Image create remote request failed, fallback enabled: {}", e.getMessage());
            return buildFallbackResult(finalPrompt, title, style, aiConfig.getModel(), "fallback");
        }
    }

    private ImageCreateResultDTO requestRemoteResult(String prompt, String title, String style, ExternalAiChannelConfig aiConfig) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", formatAuthorization(aiConfig.getKey()));
        headers.put("Content-Type", "application/json; charset=utf-8");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        requestBody.put("response_format", "url");

        String response = OKHttpUtils.postRequest4Json(resolveImageGenerationUrl(aiConfig.getUrl()), headers, JsonUtils.convertObj2Json(requestBody));
        String imageUrl = (String) JSONPath.eval(response, "$.data[0].url");
        if (!StringTools.isEmpty(imageUrl)) {
            ImageCreateResultDTO result = new ImageCreateResultDTO();
            result.setImageUrl(imageUrl);
            result.setFileSuffix(Constants.IMAGE_SUFFIX);
            result.setProvider(DEFAULT_PROVIDER);
            result.setModel(aiConfig.getModel());
            result.setResponseSource("remote");
            result.setTaskId(extractTaskId(response));
            return result;
        }
        String imageBase64 = (String) JSONPath.eval(response, "$.data[0].b64_json");
        if (!StringTools.isEmpty(imageBase64)) {
            ImageCreateResultDTO result = new ImageCreateResultDTO();
            result.setImageBytes(Base64.getDecoder().decode(imageBase64));
            result.setFileSuffix(Constants.IMAGE_SUFFIX);
            result.setProvider(DEFAULT_PROVIDER);
            result.setModel(aiConfig.getModel());
            result.setResponseSource("remote");
            result.setTaskId(extractTaskId(response));
            return result;
        }
        throw new IllegalStateException("Image create response url is empty");
    }

    private String extractTaskId(String response) {
        Object taskId = JSONPath.eval(response, "$.id");
        return taskId == null ? null : taskId.toString();
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

    private String resolveImageGenerationUrl(String baseUrl) {
        String url = normalizeText(baseUrl);
        if (url.endsWith("/images/generations")) {
            return url;
        }
        if (url.endsWith("/")) {
            return url + "images/generations";
        }
        return url + "/images/generations";
    }

    private ImageCreateResultDTO buildFallbackResult(String prompt, String title, String style, String model, String source) {
        ImageCreateResultDTO result = new ImageCreateResultDTO();
        result.setImageBytes(renderFallbackCover(title, prompt, style));
        result.setFileSuffix(".png");
        result.setProvider(DEFAULT_PROVIDER);
        result.setModel(model);
        result.setResponseSource(source);
        result.setTaskId("fallback-" + System.currentTimeMillis());
        return result;
    }

    private byte[] renderFallbackCover(String title, String prompt, String style) {
        try {
            int width = 1024;
            int height = 1024;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            graphics.setPaint(new GradientPaint(0, 0, Color.decode("#172033"), width, height, Color.decode("#d9744f")));
            graphics.fillRect(0, 0, width, height);

            graphics.setColor(new Color(255, 255, 255, 36));
            graphics.fillOval(120, 120, 320, 320);
            graphics.fillOval(640, 180, 220, 220);
            graphics.fillOval(520, 640, 260, 260);

            graphics.setColor(new Color(10, 16, 28, 170));
            graphics.fillRoundRect(88, 96, 848, 832, 48, 48);

            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("SansSerif", Font.BOLD, 68));
            int titleY = 210;
            for (String line : splitLines(resolveTitle(title), 12, 2)) {
                graphics.drawString(line, 124, titleY);
                titleY += 86;
            }

            graphics.setColor(new Color(255, 233, 204));
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 32));
            graphics.drawString("AI Cover Draft", 126, 380);

            graphics.setColor(new Color(237, 240, 245));
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 30));
            int promptY = 470;
            for (String line : splitLines(normalizeText(prompt), 24, 8)) {
                graphics.drawString(line, 126, promptY);
                promptY += 52;
            }

            graphics.setColor(new Color(255, 211, 155));
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 28));
            for (String line : splitLines("风格: " + resolveStyle(style), 26, 2)) {
                graphics.drawString(line, 126, 850);
            }
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Render fallback cover failed", e);
        }
    }

    private List<String> splitLines(String text, int maxChars, int maxLines) {
        String value = normalizeText(text);
        List<String> lines = new ArrayList<>();
        if (StringTools.isEmpty(value)) {
            lines.add("");
            return lines;
        }
        int index = 0;
        while (index < value.length() && lines.size() < maxLines) {
            int end = Math.min(index + maxChars, value.length());
            lines.add(value.substring(index, end));
            index = end;
        }
        return lines;
    }

    private String normalizePrompt(String prompt, String style, String title) {
        StringBuilder builder = new StringBuilder();
        if (!StringTools.isEmpty(title)) {
            builder.append("Album cover for ").append(title.trim()).append(". ");
        }
        if (!StringTools.isEmpty(prompt)) {
            builder.append(prompt.trim()).append(". ");
        }
        if (!StringTools.isEmpty(style)) {
            builder.append("Visual style: ").append(style.trim()).append(". ");
        }
        builder.append("High detail, centered composition, suitable for music release cover.");
        return normalizeText(builder.toString());
    }

    private String resolveTitle(String title) {
        if (!StringTools.isEmpty(title)) {
            return title.trim();
        }
        return "EasyMusic";
    }

    private String resolveStyle(String style) {
        if (!StringTools.isEmpty(style)) {
            return style.trim();
        }
        return "音乐封面插画";
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim().replaceAll("\\s+", " ");
    }
}

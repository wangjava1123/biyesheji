package com.easymusic.service.impl;

import com.easymusic.entity.config.AppConfig;
import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.config.ExternalAiConfig;
import com.easymusic.service.ExternalAiConfigService;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("externalAiConfigService")
@Slf4j
public class ExternalAiConfigServiceImpl implements ExternalAiConfigService {

    private static final String AI_CONFIG_FILE_NAME = "api的url和key.txt";

    @Resource
    private AppConfig appConfig;

    @Override
    public ExternalAiConfig getExternalAiConfig() {
        Path configPath = resolveConfigPath();
        if (configPath == null) {
            log.warn("AI config file not found: {}", AI_CONFIG_FILE_NAME);
            return new ExternalAiConfig();
        }
        try {
            return parseConfig(Files.readAllLines(configPath, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Read AI config file failed: {}", configPath, e);
            return new ExternalAiConfig();
        }
    }

    @Override
    public ExternalAiChannelConfig getConversationAiConfig() {
        return getExternalAiConfig().getConversationAi();
    }

    @Override
    public ExternalAiChannelConfig getImageAiConfig() {
        return getExternalAiConfig().getImageAi();
    }

    private Path resolveConfigPath() {
        List<Path> candidates = new ArrayList<>();
        if (!StringTools.isEmpty(appConfig.getAiConfigFilePath())) {
            Path customPath = Path.of(appConfig.getAiConfigFilePath());
            if (!customPath.isAbsolute()) {
                customPath = Path.of(System.getProperty("user.dir")).resolve(customPath).normalize();
            }
            candidates.add(customPath);
        }
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        for (int i = 0; i < 4 && current != null; i++) {
            candidates.add(current.resolve(AI_CONFIG_FILE_NAME));
            current = current.getParent();
        }
        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private ExternalAiConfig parseConfig(List<String> lines) {
        ExternalAiConfig config = new ExternalAiConfig();
        Map<String, ExternalAiChannelConfig> blockMap = new LinkedHashMap<>();
        String currentSection = null;
        for (String rawLine : lines) {
            if (rawLine == null) {
                continue;
            }
            String line = rawLine.trim();
            if (StringTools.isEmpty(line) || line.startsWith("#")) {
                continue;
            }
            if (line.endsWith("：") || line.endsWith(":")) {
                currentSection = normalizeSectionName(line);
                blockMap.computeIfAbsent(currentSection, key -> new ExternalAiChannelConfig());
                blockMap.get(currentSection).setName(currentSection);
                continue;
            }
            if (currentSection == null) {
                continue;
            }
            int splitIndex = line.indexOf('=');
            if (splitIndex <= 0) {
                splitIndex = line.indexOf(':');
            }
            if (splitIndex <= 0) {
                continue;
            }
            String key = line.substring(0, splitIndex).trim();
            String value = line.substring(splitIndex + 1).trim();
            fillConfig(blockMap.get(currentSection), key, value);
        }

        List<ExternalAiChannelConfig> blockList = new ArrayList<>(blockMap.values());
        config.setConversationAi(blockMap.getOrDefault("conversation", blockList.size() > 0 ? blockList.get(0) : null));
        config.setImageAi(blockMap.getOrDefault("image", blockList.size() > 1 ? blockList.get(1) : null));
        return config;
    }

    private String normalizeSectionName(String rawSection) {
        String sectionName = rawSection.replace("：", "").replace(":", "").trim().toLowerCase();
        if (sectionName.contains("对话")) {
            return "conversation";
        }
        if (sectionName.contains("图片")) {
            return "image";
        }
        return sectionName;
    }

    private void fillConfig(ExternalAiChannelConfig config, String key, String value) {
        if ("url".equalsIgnoreCase(key)) {
            config.setUrl(value);
            return;
        }
        if ("key".equalsIgnoreCase(key)) {
            config.setKey(value);
            return;
        }
        if ("model".equalsIgnoreCase(key)) {
            config.setModel(value);
        }
    }
}

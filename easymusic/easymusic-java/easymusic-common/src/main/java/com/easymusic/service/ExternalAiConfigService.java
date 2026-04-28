package com.easymusic.service;

import com.easymusic.entity.config.ExternalAiChannelConfig;
import com.easymusic.entity.config.ExternalAiConfig;

public interface ExternalAiConfigService {

    ExternalAiConfig getExternalAiConfig();

    ExternalAiChannelConfig getConversationAiConfig();

    ExternalAiChannelConfig getImageAiConfig();
}

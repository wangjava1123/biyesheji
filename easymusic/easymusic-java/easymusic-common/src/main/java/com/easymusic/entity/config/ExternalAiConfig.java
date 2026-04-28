package com.easymusic.entity.config;

public class ExternalAiConfig {

    private ExternalAiChannelConfig conversationAi;

    private ExternalAiChannelConfig imageAi;

    public ExternalAiChannelConfig getConversationAi() {
        return conversationAi;
    }

    public void setConversationAi(ExternalAiChannelConfig conversationAi) {
        this.conversationAi = conversationAi;
    }

    public ExternalAiChannelConfig getImageAi() {
        return imageAi;
    }

    public void setImageAi(ExternalAiChannelConfig imageAi) {
        this.imageAi = imageAi;
    }
}

package com.easymusic.service;

import com.easymusic.entity.dto.PromptAssistResultDTO;

public interface PromptAssistService {

    PromptAssistResultDTO generatePrompt(String userId, String rawPrompt, String bizType);
}

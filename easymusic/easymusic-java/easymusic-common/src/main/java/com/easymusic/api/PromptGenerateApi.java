package com.easymusic.api;

import com.easymusic.entity.dto.PromptAssistResultDTO;

public interface PromptGenerateApi {

    PromptAssistResultDTO generate(String rawPrompt);
}

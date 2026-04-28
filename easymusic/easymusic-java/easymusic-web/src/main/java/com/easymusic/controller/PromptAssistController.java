package com.easymusic.controller;

import com.easymusic.annotation.GlobalInterceptor;
import com.easymusic.entity.dto.PromptAssistResultDTO;
import com.easymusic.entity.dto.TokenUserInfoDTO;
import com.easymusic.entity.vo.ResponseVO;
import com.easymusic.service.PromptAssistService;
import com.easymusic.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promptAssist")
@Validated
public class PromptAssistController extends ABaseController {

    @Resource
    private PromptAssistService promptAssistService;

    @RequestMapping("/generate")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO generate(@NotEmpty @Size(max = 300) String rawPrompt,
                               @Size(max = 30) String bizType) {
        TokenUserInfoDTO tokenUserInfoDTO = getTokenUserInfo(null);
        String finalBizType = StringTools.isEmpty(bizType) ? "MUSIC_CREATE" : bizType.trim();
        PromptAssistResultDTO result = promptAssistService.generatePrompt(tokenUserInfoDTO.getUserId(), rawPrompt.trim(), finalBizType);
        return getSuccessResponseVO(result);
    }
}

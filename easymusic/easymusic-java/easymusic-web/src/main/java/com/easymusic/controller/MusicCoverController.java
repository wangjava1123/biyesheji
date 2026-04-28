package com.easymusic.controller;

import com.easymusic.annotation.GlobalInterceptor;
import com.easymusic.entity.dto.TokenUserInfoDTO;
import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.vo.ResponseVO;
import com.easymusic.service.MusicCoverService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/musicCover")
@Validated
public class MusicCoverController extends ABaseController {

    @Resource
    private MusicCoverService musicCoverService;

    @RequestMapping("/generate")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO generate(@NotEmpty String musicId) {
        TokenUserInfoDTO tokenUserInfoDTO = getTokenUserInfo(null);
        MusicCoverCreation result = musicCoverService.generateCover(tokenUserInfoDTO.getUserId(), musicId.trim());
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/records")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO records(@NotEmpty String musicId, Integer limit) {
        TokenUserInfoDTO tokenUserInfoDTO = getTokenUserInfo(null);
        List<MusicCoverCreation> result = musicCoverService.listCoverRecords(tokenUserInfoDTO.getUserId(), musicId.trim(), limit);
        return getSuccessResponseVO(result);
    }
}

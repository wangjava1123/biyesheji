package com.easymusic.controller;

import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.query.MusicCoverCreationQuery;
import com.easymusic.entity.vo.MusicCoverSummaryVO;
import com.easymusic.entity.vo.PaginationResultVO;
import com.easymusic.entity.vo.ResponseVO;
import com.easymusic.service.MusicCoverService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/musicCover")
@Slf4j
public class MusicCoverAdminController extends ABaseController {

    @Resource
    private MusicCoverService musicCoverService;

    @RequestMapping("/loadCoverRecordList")
    public ResponseVO loadCoverRecordList(MusicCoverCreationQuery query) {
        if (query == null) {
            query = new MusicCoverCreationQuery();
        }
        query.setOrderBy("c.create_time desc");
        PaginationResultVO<MusicCoverCreation> resultVO = musicCoverService.findListByPage(query);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/loadCoverSummary")
    public ResponseVO loadCoverSummary(MusicCoverCreationQuery query) {
        MusicCoverSummaryVO resultVO = musicCoverService.loadAdminSummary(query);
        return getSuccessResponseVO(resultVO);
    }
}

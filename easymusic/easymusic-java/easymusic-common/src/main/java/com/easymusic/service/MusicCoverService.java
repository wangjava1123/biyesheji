package com.easymusic.service;

import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.query.MusicCoverCreationQuery;
import com.easymusic.entity.vo.MusicCoverSummaryVO;
import com.easymusic.entity.vo.PaginationResultVO;

import java.util.List;

public interface MusicCoverService {

    MusicCoverCreation generateCover(String userId, String musicId);

    List<MusicCoverCreation> listCoverRecords(String userId, String musicId, Integer limit);

    PaginationResultVO<MusicCoverCreation> findListByPage(MusicCoverCreationQuery param);

    MusicCoverSummaryVO loadAdminSummary(MusicCoverCreationQuery param);
}

package com.easymusic.service;

import com.easymusic.entity.po.MusicCoverCreation;

import java.util.List;

public interface MusicCoverService {

    MusicCoverCreation generateCover(String userId, String musicId);

    List<MusicCoverCreation> listCoverRecords(String userId, String musicId, Integer limit);
}

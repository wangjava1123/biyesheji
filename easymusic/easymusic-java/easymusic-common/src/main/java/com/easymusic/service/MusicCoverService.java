package com.easymusic.service;

import com.easymusic.entity.po.MusicCoverCreation;

public interface MusicCoverService {

    MusicCoverCreation generateCover(String userId, String musicId);
}

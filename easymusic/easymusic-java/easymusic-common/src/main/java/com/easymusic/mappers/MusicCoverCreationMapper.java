package com.easymusic.mappers;

import com.easymusic.entity.po.MusicCoverCreation;
import org.apache.ibatis.annotations.Param;

public interface MusicCoverCreationMapper {

    Integer insert(@Param("bean") MusicCoverCreation bean);

    Integer updateByCoverId(@Param("bean") MusicCoverCreation bean, @Param("coverId") String coverId);

    MusicCoverCreation selectByCoverId(@Param("coverId") String coverId);
}

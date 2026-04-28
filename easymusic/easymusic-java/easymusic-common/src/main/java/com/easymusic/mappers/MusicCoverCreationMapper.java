package com.easymusic.mappers;

import com.easymusic.entity.query.MusicCoverCreationQuery;
import com.easymusic.entity.po.MusicCoverCreation;
import com.easymusic.entity.vo.MusicCoverSummaryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MusicCoverCreationMapper extends BaseMapper<MusicCoverCreation, MusicCoverCreationQuery> {

    Integer insert(@Param("bean") MusicCoverCreation bean);

    Integer updateByCoverId(@Param("bean") MusicCoverCreation bean, @Param("coverId") String coverId);

    MusicCoverCreation selectByCoverId(@Param("coverId") String coverId);

    List<MusicCoverCreation> selectRecentByMusicIdAndUserId(@Param("musicId") String musicId,
                                                            @Param("userId") String userId,
                                                            @Param("limit") Integer limit);

    MusicCoverSummaryVO selectAdminSummary(@Param("query") MusicCoverCreationQuery query);
}

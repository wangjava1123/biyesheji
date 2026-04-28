package com.easymusic.mappers;

import com.easymusic.entity.vo.AdminDashboardCreatorVO;
import com.easymusic.entity.vo.AdminDashboardModelVO;
import com.easymusic.entity.vo.AdminDashboardMusicVO;
import com.easymusic.entity.vo.AdminDashboardVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminDashboardMapper {

    AdminDashboardVO selectOverview();

    List<AdminDashboardModelVO> selectTopModels(@Param("limit") Integer limit);

    List<AdminDashboardMusicVO> selectHotMusics(@Param("limit") Integer limit);

    List<AdminDashboardCreatorVO> selectHotCreators(@Param("limit") Integer limit);
}

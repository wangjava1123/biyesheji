package com.easymusic.service.impl;

import com.easymusic.entity.vo.AdminDashboardCreatorVO;
import com.easymusic.entity.vo.AdminDashboardModelVO;
import com.easymusic.entity.vo.AdminDashboardTrendVO;
import com.easymusic.entity.vo.AdminDashboardVO;
import com.easymusic.mappers.AdminDashboardMapper;
import com.easymusic.service.AdminDashboardService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Service("adminDashboardService")
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final int MODEL_LIMIT = 5;

    private static final int MUSIC_LIMIT = 6;

    private static final int CREATOR_LIMIT = 6;

    @Resource
    private AdminDashboardMapper adminDashboardMapper;

    @Override
    public AdminDashboardVO loadDashboard() {
        AdminDashboardVO dashboard = adminDashboardMapper.selectOverview();
        if (dashboard == null) {
            dashboard = new AdminDashboardVO();
        }
        fillDefaultValues(dashboard);
        dashboard.setMusicSuccessRate(calculateRate(dashboard.getSuccessMusicCount(), dashboard.getTotalMusicCount()));
        dashboard.setCoverSuccessRate(calculateRate(dashboard.getSuccessCoverTaskCount(), dashboard.getTotalCoverTaskCount()));
        List<AdminDashboardTrendVO> last7DaysTrend = defaultList(adminDashboardMapper.selectLast7DaysTrend());
        for (AdminDashboardTrendVO trend : last7DaysTrend) {
            fillTrendDefaultValues(trend);
        }
        dashboard.setLast7DaysTrend(last7DaysTrend);

        List<AdminDashboardModelVO> topModels = adminDashboardMapper.selectTopModels(MODEL_LIMIT);
        if (topModels == null) {
            topModels = Collections.emptyList();
        }
        for (AdminDashboardModelVO model : topModels) {
            model.setTotalCount(defaultZero(model.getTotalCount()));
            model.setSuccessCount(defaultZero(model.getSuccessCount()));
            model.setSuccessRate(calculateRate(model.getSuccessCount(), model.getTotalCount()));
        }
        dashboard.setTopModels(topModels);
        dashboard.setHotMusics(defaultList(adminDashboardMapper.selectHotMusics(MUSIC_LIMIT)));
        List<AdminDashboardCreatorVO> hotCreators = defaultList(adminDashboardMapper.selectHotCreators(CREATOR_LIMIT));
        for (AdminDashboardCreatorVO creator : hotCreators) {
            creator.setMusicCount(defaultZero(creator.getMusicCount()));
            creator.setTotalPlayCount(defaultZero(creator.getTotalPlayCount()));
            creator.setTotalGoodCount(defaultZero(creator.getTotalGoodCount()));
        }
        dashboard.setHotCreators(hotCreators);
        return dashboard;
    }

    private void fillDefaultValues(AdminDashboardVO dashboard) {
        dashboard.setTotalUserCount(defaultZero(dashboard.getTotalUserCount()));
        dashboard.setTodayNewUserCount(defaultZero(dashboard.getTodayNewUserCount()));
        dashboard.setActiveCreatorCount(defaultZero(dashboard.getActiveCreatorCount()));
        dashboard.setTotalCreationCount(defaultZero(dashboard.getTotalCreationCount()));
        dashboard.setTodayCreationCount(defaultZero(dashboard.getTodayCreationCount()));
        dashboard.setTotalMusicCount(defaultZero(dashboard.getTotalMusicCount()));
        dashboard.setSuccessMusicCount(defaultZero(dashboard.getSuccessMusicCount()));
        dashboard.setTodaySuccessMusicCount(defaultZero(dashboard.getTodaySuccessMusicCount()));
        dashboard.setCreatingMusicCount(defaultZero(dashboard.getCreatingMusicCount()));
        dashboard.setFailedMusicCount(defaultZero(dashboard.getFailedMusicCount()));
        dashboard.setPublishedMusicCount(defaultZero(dashboard.getPublishedMusicCount()));
        dashboard.setDraftMusicCount(defaultZero(dashboard.getDraftMusicCount()));
        dashboard.setHiddenMusicCount(defaultZero(dashboard.getHiddenMusicCount()));
        dashboard.setTotalPlayCount(defaultZero(dashboard.getTotalPlayCount()));
        dashboard.setTotalGoodCount(defaultZero(dashboard.getTotalGoodCount()));
        dashboard.setTotalCoverTaskCount(defaultZero(dashboard.getTotalCoverTaskCount()));
        dashboard.setSuccessCoverTaskCount(defaultZero(dashboard.getSuccessCoverTaskCount()));
        dashboard.setTodayCoverTaskCount(defaultZero(dashboard.getTodayCoverTaskCount()));
        dashboard.setAiPromptCount(defaultZero(dashboard.getAiPromptCount()));
        dashboard.setManualPromptCount(defaultZero(dashboard.getManualPromptCount()));
        dashboard.setAiCoverMusicCount(defaultZero(dashboard.getAiCoverMusicCount()));
        dashboard.setManualCoverMusicCount(defaultZero(dashboard.getManualCoverMusicCount()));
        dashboard.setTodayIntegralConsume(defaultZero(dashboard.getTodayIntegralConsume()));
        dashboard.setTodayRechargeAmount(defaultDecimal(dashboard.getTodayRechargeAmount()));
    }

    private void fillTrendDefaultValues(AdminDashboardTrendVO trend) {
        trend.setNewUserCount(defaultZero(trend.getNewUserCount()));
        trend.setCreationCount(defaultZero(trend.getCreationCount()));
        trend.setSuccessMusicCount(defaultZero(trend.getSuccessMusicCount()));
        trend.setPublishCount(defaultZero(trend.getPublishCount()));
        trend.setCoverTaskCount(defaultZero(trend.getCoverTaskCount()));
        trend.setIntegralConsume(defaultZero(trend.getIntegralConsume()));
        trend.setRechargeAmount(defaultDecimal(trend.getRechargeAmount()));
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private double calculateRate(Integer successCount, Integer totalCount) {
        int total = defaultZero(totalCount);
        if (total == 0) {
            return 0D;
        }
        return BigDecimal.valueOf(defaultZero(successCount) * 100D / total)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private <T> List<T> defaultList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}

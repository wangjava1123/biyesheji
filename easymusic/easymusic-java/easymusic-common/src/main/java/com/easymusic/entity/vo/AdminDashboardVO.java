package com.easymusic.entity.vo;

import java.math.BigDecimal;
import java.util.List;

public class AdminDashboardVO {

    private Integer totalUserCount;

    private Integer todayNewUserCount;

    private Integer activeCreatorCount;

    private Integer totalCreationCount;

    private Integer todayCreationCount;

    private Integer totalMusicCount;

    private Integer successMusicCount;

    private Integer todaySuccessMusicCount;

    private Integer creatingMusicCount;

    private Integer failedMusicCount;

    private Integer publishedMusicCount;

    private Integer draftMusicCount;

    private Integer hiddenMusicCount;

    private Integer totalPlayCount;

    private Integer totalGoodCount;

    private Integer totalCoverTaskCount;

    private Integer successCoverTaskCount;

    private Integer todayCoverTaskCount;

    private Integer aiPromptCount;

    private Integer manualPromptCount;

    private Integer aiCoverMusicCount;

    private Integer manualCoverMusicCount;

    private Integer todayIntegralConsume;

    private BigDecimal todayRechargeAmount;

    private Double musicSuccessRate;

    private Double coverSuccessRate;

    private List<AdminDashboardModelVO> topModels;

    private List<AdminDashboardMusicVO> hotMusics;

    private List<AdminDashboardCreatorVO> hotCreators;

    public Integer getTotalUserCount() {
        return totalUserCount;
    }

    public void setTotalUserCount(Integer totalUserCount) {
        this.totalUserCount = totalUserCount;
    }

    public Integer getTodayNewUserCount() {
        return todayNewUserCount;
    }

    public void setTodayNewUserCount(Integer todayNewUserCount) {
        this.todayNewUserCount = todayNewUserCount;
    }

    public Integer getActiveCreatorCount() {
        return activeCreatorCount;
    }

    public void setActiveCreatorCount(Integer activeCreatorCount) {
        this.activeCreatorCount = activeCreatorCount;
    }

    public Integer getTotalCreationCount() {
        return totalCreationCount;
    }

    public void setTotalCreationCount(Integer totalCreationCount) {
        this.totalCreationCount = totalCreationCount;
    }

    public Integer getTodayCreationCount() {
        return todayCreationCount;
    }

    public void setTodayCreationCount(Integer todayCreationCount) {
        this.todayCreationCount = todayCreationCount;
    }

    public Integer getTotalMusicCount() {
        return totalMusicCount;
    }

    public void setTotalMusicCount(Integer totalMusicCount) {
        this.totalMusicCount = totalMusicCount;
    }

    public Integer getSuccessMusicCount() {
        return successMusicCount;
    }

    public void setSuccessMusicCount(Integer successMusicCount) {
        this.successMusicCount = successMusicCount;
    }

    public Integer getTodaySuccessMusicCount() {
        return todaySuccessMusicCount;
    }

    public void setTodaySuccessMusicCount(Integer todaySuccessMusicCount) {
        this.todaySuccessMusicCount = todaySuccessMusicCount;
    }

    public Integer getCreatingMusicCount() {
        return creatingMusicCount;
    }

    public void setCreatingMusicCount(Integer creatingMusicCount) {
        this.creatingMusicCount = creatingMusicCount;
    }

    public Integer getFailedMusicCount() {
        return failedMusicCount;
    }

    public void setFailedMusicCount(Integer failedMusicCount) {
        this.failedMusicCount = failedMusicCount;
    }

    public Integer getPublishedMusicCount() {
        return publishedMusicCount;
    }

    public void setPublishedMusicCount(Integer publishedMusicCount) {
        this.publishedMusicCount = publishedMusicCount;
    }

    public Integer getDraftMusicCount() {
        return draftMusicCount;
    }

    public void setDraftMusicCount(Integer draftMusicCount) {
        this.draftMusicCount = draftMusicCount;
    }

    public Integer getHiddenMusicCount() {
        return hiddenMusicCount;
    }

    public void setHiddenMusicCount(Integer hiddenMusicCount) {
        this.hiddenMusicCount = hiddenMusicCount;
    }

    public Integer getTotalPlayCount() {
        return totalPlayCount;
    }

    public void setTotalPlayCount(Integer totalPlayCount) {
        this.totalPlayCount = totalPlayCount;
    }

    public Integer getTotalGoodCount() {
        return totalGoodCount;
    }

    public void setTotalGoodCount(Integer totalGoodCount) {
        this.totalGoodCount = totalGoodCount;
    }

    public Integer getTotalCoverTaskCount() {
        return totalCoverTaskCount;
    }

    public void setTotalCoverTaskCount(Integer totalCoverTaskCount) {
        this.totalCoverTaskCount = totalCoverTaskCount;
    }

    public Integer getSuccessCoverTaskCount() {
        return successCoverTaskCount;
    }

    public void setSuccessCoverTaskCount(Integer successCoverTaskCount) {
        this.successCoverTaskCount = successCoverTaskCount;
    }

    public Integer getTodayCoverTaskCount() {
        return todayCoverTaskCount;
    }

    public void setTodayCoverTaskCount(Integer todayCoverTaskCount) {
        this.todayCoverTaskCount = todayCoverTaskCount;
    }

    public Integer getAiPromptCount() {
        return aiPromptCount;
    }

    public void setAiPromptCount(Integer aiPromptCount) {
        this.aiPromptCount = aiPromptCount;
    }

    public Integer getManualPromptCount() {
        return manualPromptCount;
    }

    public void setManualPromptCount(Integer manualPromptCount) {
        this.manualPromptCount = manualPromptCount;
    }

    public Integer getAiCoverMusicCount() {
        return aiCoverMusicCount;
    }

    public void setAiCoverMusicCount(Integer aiCoverMusicCount) {
        this.aiCoverMusicCount = aiCoverMusicCount;
    }

    public Integer getManualCoverMusicCount() {
        return manualCoverMusicCount;
    }

    public void setManualCoverMusicCount(Integer manualCoverMusicCount) {
        this.manualCoverMusicCount = manualCoverMusicCount;
    }

    public Integer getTodayIntegralConsume() {
        return todayIntegralConsume;
    }

    public void setTodayIntegralConsume(Integer todayIntegralConsume) {
        this.todayIntegralConsume = todayIntegralConsume;
    }

    public BigDecimal getTodayRechargeAmount() {
        return todayRechargeAmount;
    }

    public void setTodayRechargeAmount(BigDecimal todayRechargeAmount) {
        this.todayRechargeAmount = todayRechargeAmount;
    }

    public Double getMusicSuccessRate() {
        return musicSuccessRate;
    }

    public void setMusicSuccessRate(Double musicSuccessRate) {
        this.musicSuccessRate = musicSuccessRate;
    }

    public Double getCoverSuccessRate() {
        return coverSuccessRate;
    }

    public void setCoverSuccessRate(Double coverSuccessRate) {
        this.coverSuccessRate = coverSuccessRate;
    }

    public List<AdminDashboardModelVO> getTopModels() {
        return topModels;
    }

    public void setTopModels(List<AdminDashboardModelVO> topModels) {
        this.topModels = topModels;
    }

    public List<AdminDashboardMusicVO> getHotMusics() {
        return hotMusics;
    }

    public void setHotMusics(List<AdminDashboardMusicVO> hotMusics) {
        this.hotMusics = hotMusics;
    }

    public List<AdminDashboardCreatorVO> getHotCreators() {
        return hotCreators;
    }

    public void setHotCreators(List<AdminDashboardCreatorVO> hotCreators) {
        this.hotCreators = hotCreators;
    }
}

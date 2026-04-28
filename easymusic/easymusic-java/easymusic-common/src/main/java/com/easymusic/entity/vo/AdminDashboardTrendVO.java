package com.easymusic.entity.vo;

import java.math.BigDecimal;

public class AdminDashboardTrendVO {

    private String trendDate;

    private Integer newUserCount;

    private Integer creationCount;

    private Integer successMusicCount;

    private Integer publishCount;

    private Integer coverTaskCount;

    private Integer integralConsume;

    private BigDecimal rechargeAmount;

    public String getTrendDate() {
        return trendDate;
    }

    public void setTrendDate(String trendDate) {
        this.trendDate = trendDate;
    }

    public Integer getNewUserCount() {
        return newUserCount;
    }

    public void setNewUserCount(Integer newUserCount) {
        this.newUserCount = newUserCount;
    }

    public Integer getCreationCount() {
        return creationCount;
    }

    public void setCreationCount(Integer creationCount) {
        this.creationCount = creationCount;
    }

    public Integer getSuccessMusicCount() {
        return successMusicCount;
    }

    public void setSuccessMusicCount(Integer successMusicCount) {
        this.successMusicCount = successMusicCount;
    }

    public Integer getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(Integer publishCount) {
        this.publishCount = publishCount;
    }

    public Integer getCoverTaskCount() {
        return coverTaskCount;
    }

    public void setCoverTaskCount(Integer coverTaskCount) {
        this.coverTaskCount = coverTaskCount;
    }

    public Integer getIntegralConsume() {
        return integralConsume;
    }

    public void setIntegralConsume(Integer integralConsume) {
        this.integralConsume = integralConsume;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(BigDecimal rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
}

package com.easymusic.entity.vo;

public class AdminDashboardCreatorVO {

    private String userId;

    private String nickName;

    private String avatar;

    private Integer musicCount;

    private Integer totalPlayCount;

    private Integer totalGoodCount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(Integer musicCount) {
        this.musicCount = musicCount;
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
}

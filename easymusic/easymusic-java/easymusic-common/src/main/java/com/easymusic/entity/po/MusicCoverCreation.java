package com.easymusic.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class MusicCoverCreation implements Serializable {

    private String coverId;

    private String musicId;

    private String userId;

    private String creationId;

    private String prompt;

    private String style;

    private String model;

    private Integer integral;

    private String taskId;

    private String coverUrl;

    private Integer status;

    private String failReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    private String musicTitle;

    private String nickName;

    private Integer musicStatus;

    private Integer publishStatus;

    private String currentCover;

    private Integer coverSource;

    private Integer coverGenerateCount;

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreationId() {
        return creationId;
    }

    public void setCreationId(String creationId) {
        this.creationId = creationId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getMusicStatus() {
        return musicStatus;
    }

    public void setMusicStatus(Integer musicStatus) {
        this.musicStatus = musicStatus;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getCurrentCover() {
        return currentCover;
    }

    public void setCurrentCover(String currentCover) {
        this.currentCover = currentCover;
    }

    public Integer getCoverSource() {
        return coverSource;
    }

    public void setCoverSource(Integer coverSource) {
        this.coverSource = coverSource;
    }

    public Integer getCoverGenerateCount() {
        return coverGenerateCount;
    }

    public void setCoverGenerateCount(Integer coverGenerateCount) {
        this.coverGenerateCount = coverGenerateCount;
    }
}

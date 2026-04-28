package com.easymusic.entity.po;

import com.easymusic.entity.enums.DateTimePatternEnum;
import com.easymusic.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 音乐信息
 */
public class MusicInfo implements Serializable {


    /**
     * 音乐ID
     */
    private String musicId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 创作ID
     */
    private String creationId;

    /**
     * 标题
     */
    private String musicTitle;

    /**
     * 封面
     */
    private String cover;

    /**
     * 音乐地址
     */
    private String audioPath;

    /**
     * 持续时间
     */
    private Integer duration;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 播放数量
     */
    private Integer playCount;

    /**
     * 点赞数
     */
    private Integer goodCount;

    /**
     * 0:未推荐 1:已推荐
     */
    private Integer commendType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 0:生成音乐中 1:生成完毕
     */
    private Integer musicStatus;

    /**
     * 音乐类型 0:音乐 1:纯音乐
     */
    private Integer musicType;

    /**
     * 发布状态 0:草稿 1:已发布 2:已隐藏
     */
    private Integer publishStatus;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /**
     * 封面来源 0:手动上传 1:AI生成
     */
    private Integer coverSource;

    /**
     * AI封面生成次数
     */
    private Integer coverGenerateCount;

    /**
     * 最终音乐提示词
     */
    private String creationPrompt;

    /**
     * 原始一句话需求
     */
    private String originPrompt;

    /**
     * 提示词来源 0:手写 1:AI增强
     */
    private Integer promptSourceType;

    /**
     * 提示词增强记录ID
     */
    private String promptRecordId;

    /**
     * 创作模型
     */
    private String creationModel;

    /**
     * 创作模式 0:简单模式 1:高级模式
     */
    private Integer creationModeType;

    private String avatar;

    private String nickName;

    private Boolean doGood;

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicId() {
        return this.musicId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setCreationId(String creationId) {
        this.creationId = creationId;
    }

    public String getCreationId() {
        return this.creationId;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicTitle() {
        return this.musicTitle;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return this.cover;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return this.lyrics;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public Integer getPlayCount() {
        return this.playCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getGoodCount() {
        return this.goodCount;
    }

    public void setCommendType(Integer commendType) {
        this.commendType = commendType;
    }

    public Integer getCommendType() {
        return this.commendType;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setMusicStatus(Integer musicStatus) {
        this.musicStatus = musicStatus;
    }

    public Integer getMusicStatus() {
        return this.musicStatus;
    }

    public void setMusicType(Integer musicType) {
        this.musicType = musicType;
    }

    public Integer getMusicType() {
        return this.musicType;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() {
        return publishTime;
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

    public String getCreationPrompt() {
        return creationPrompt;
    }

    public void setCreationPrompt(String creationPrompt) {
        this.creationPrompt = creationPrompt;
    }

    public String getOriginPrompt() {
        return originPrompt;
    }

    public void setOriginPrompt(String originPrompt) {
        this.originPrompt = originPrompt;
    }

    public Integer getPromptSourceType() {
        return promptSourceType;
    }

    public void setPromptSourceType(Integer promptSourceType) {
        this.promptSourceType = promptSourceType;
    }

    public String getPromptRecordId() {
        return promptRecordId;
    }

    public void setPromptRecordId(String promptRecordId) {
        this.promptRecordId = promptRecordId;
    }

    public String getCreationModel() {
        return creationModel;
    }

    public void setCreationModel(String creationModel) {
        this.creationModel = creationModel;
    }

    public Integer getCreationModeType() {
        return creationModeType;
    }

    public void setCreationModeType(Integer creationModeType) {
        this.creationModeType = creationModeType;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getDoGood() {
        return doGood;
    }

    public void setDoGood(Boolean doGood) {
        this.doGood = doGood;
    }

    @Override
    public String toString() {
        return "音乐ID:" + (musicId == null ? "空" : musicId) + "，用户ID:" + (userId == null ? "空" : userId) + "，任务ID:" + (taskId == null ? "空" : taskId) + "，创作ID:" + (creationId == null ? "空" : creationId) + "，标题:" + (musicTitle == null ? "空" : musicTitle) + "，封面:" + (cover == null ? "空" : cover) + "，音乐地址:" + (audioPath == null ? "空" : audioPath) + "，持续时间:" + (duration == null ? "空" : duration) + "，歌词:" + (lyrics == null ? "空" : lyrics) + "，播放数量:" + (playCount == null ? "空" : playCount) + "，点赞数:" + (goodCount == null ? "空" : goodCount) + "，0:未推荐 1:已推荐:" + (commendType == null ? "空" : commendType) + "，创建时间:" + (createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，0:生成音乐中 1:生成完毕:" + (musicStatus == null ? "空" : musicStatus) + "，音乐类型 0:音乐 1:纯音乐:" + (musicType == null ? "空" : musicType) + "，发布状态:" + (publishStatus == null ? "空" : publishStatus) + "，发布时间:" + (publishTime == null ? "空" : DateUtil.format(publishTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，封面来源:" + (coverSource == null ? "空" : coverSource) + "，AI封面生成次数:" + (coverGenerateCount == null ? "空" : coverGenerateCount) + "，最终音乐提示词:" + (creationPrompt == null ? "空" : creationPrompt) + "，原始一句话需求:" + (originPrompt == null ? "空" : originPrompt) + "，提示词来源:" + (promptSourceType == null ? "空" : promptSourceType) + "，提示词增强记录ID:" + (promptRecordId == null ? "空" : promptRecordId) + "，创作模型:" + (creationModel == null ? "空" : creationModel) + "，创作模式:" + (creationModeType == null ? "空" : creationModeType);
    }
}

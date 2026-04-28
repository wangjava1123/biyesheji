package com.easymusic.entity.enums;

public enum MusicCoverStatusEnum {
    CREATING(0, "封面生成中"),
    SUCCESS(1, "封面生成成功"),
    FAIL(2, "封面生成失败");

    private final Integer status;

    private final String desc;

    MusicCoverStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}

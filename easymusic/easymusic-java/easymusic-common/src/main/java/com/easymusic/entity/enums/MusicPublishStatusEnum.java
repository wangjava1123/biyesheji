package com.easymusic.entity.enums;

public enum MusicPublishStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    HIDDEN(2, "已隐藏");

    private final Integer status;

    private final String desc;

    MusicPublishStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static MusicPublishStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (MusicPublishStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}

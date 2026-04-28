package com.easymusic.entity.enums;

public enum MusicCoverSourceEnum {
    MANUAL_UPLOAD(0, "手动上传"),
    AI_GENERATED(1, "AI生成");

    private final Integer source;

    private final String desc;

    MusicCoverSourceEnum(Integer source, String desc) {
        this.source = source;
        this.desc = desc;
    }

    public Integer getSource() {
        return source;
    }

    public String getDesc() {
        return desc;
    }
}

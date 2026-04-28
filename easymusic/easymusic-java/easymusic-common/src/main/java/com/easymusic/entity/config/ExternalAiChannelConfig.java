package com.easymusic.entity.config;

import com.easymusic.utils.StringTools;

public class ExternalAiChannelConfig {

    private String name;

    private String url;

    private String key;

    private String model;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isReady() {
        return !StringTools.isEmpty(url) && !StringTools.isEmpty(key) && !StringTools.isEmpty(model);
    }
}

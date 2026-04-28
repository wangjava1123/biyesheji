package com.easymusic.entity.dto;

import java.util.List;

public class PromptAssistResultDTO {

    private String recordId;

    private String rawPrompt;

    private String musicPrompt;

    private String imagePrompt;

    private List<String> titleSuggestions;

    private String musicGenre;

    private String musicEmotion;

    private String musicSex;

    private List<String> tags;

    private String visualStyle;

    private String provider;

    private String model;

    private String responseSource;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRawPrompt() {
        return rawPrompt;
    }

    public void setRawPrompt(String rawPrompt) {
        this.rawPrompt = rawPrompt;
    }

    public String getMusicPrompt() {
        return musicPrompt;
    }

    public void setMusicPrompt(String musicPrompt) {
        this.musicPrompt = musicPrompt;
    }

    public String getImagePrompt() {
        return imagePrompt;
    }

    public void setImagePrompt(String imagePrompt) {
        this.imagePrompt = imagePrompt;
    }

    public List<String> getTitleSuggestions() {
        return titleSuggestions;
    }

    public void setTitleSuggestions(List<String> titleSuggestions) {
        this.titleSuggestions = titleSuggestions;
    }

    public String getMusicGenre() {
        return musicGenre;
    }

    public void setMusicGenre(String musicGenre) {
        this.musicGenre = musicGenre;
    }

    public String getMusicEmotion() {
        return musicEmotion;
    }

    public void setMusicEmotion(String musicEmotion) {
        this.musicEmotion = musicEmotion;
    }

    public String getMusicSex() {
        return musicSex;
    }

    public void setMusicSex(String musicSex) {
        this.musicSex = musicSex;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getVisualStyle() {
        return visualStyle;
    }

    public void setVisualStyle(String visualStyle) {
        this.visualStyle = visualStyle;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResponseSource() {
        return responseSource;
    }

    public void setResponseSource(String responseSource) {
        this.responseSource = responseSource;
    }
}

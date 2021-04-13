package com.glitchcam.vepromei.mimodemo.common.template.model;

//记录模板额外数据信息
public class TemplateExtraDataInfo {
    public String getPreviewVideoPath() {
        return previewVideoPath;
    }

    public void setPreviewVideoPath(String previewVideoPath) {
        this.previewVideoPath = previewVideoPath;
    }
    public String getCoverFilePath() {
        return coverFilePath;
    }

    public void setCoverFilePath(String coverFilePath) {
        this.coverFilePath = coverFilePath;
    }
    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    private String previewVideoPath;//模板预览视频路径
    private String coverFilePath;//封面文件路径
    private String musicFilePath;//音乐文件路径
    private String templateDirectory;//模板目录路径
}

package com.glitchcam.vepromei.glitter;

/**
 * 闪光特效实体类
 */
public class GlitterEffectBean {

    private String mEffectFilePath;
    private String mEffectIcon;
    private String mEffectName;

    public GlitterEffectBean(String effectFilePath, String effectIcon, String effectName) {
        this.mEffectFilePath = effectFilePath;
        this.mEffectIcon = effectIcon;
        this.mEffectName = effectName;
    }

    public String getEffectName() {
        return mEffectName;
    }

    public void setEffectName(String effectName) {
        this.mEffectName = effectName;
    }

    public String getEffectIcon() {
        return mEffectIcon;
    }

    public void setEffectIcon(String effectIcon) {
        this.mEffectIcon = effectIcon;
    }

    public String getEffectFilePath() {
        return mEffectFilePath;
    }

    public void setEffectFilePath(String effectFilePath) {
        this.mEffectFilePath = effectFilePath;
    }
}

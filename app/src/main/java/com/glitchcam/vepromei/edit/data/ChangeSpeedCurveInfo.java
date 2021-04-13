package com.glitchcam.vepromei.edit.data;


import android.graphics.drawable.Drawable;


public class ChangeSpeedCurveInfo extends BaseInfo {

    public String mEffectName;

    public int index;

    public String speed;

    public String speedOriginal;
    public String imagePath;
    public Drawable image_drawable;

    public ChangeSpeedCurveInfo() {
    }

    public ChangeSpeedCurveInfo(String name) {
        super(name);
    }


    public ChangeSpeedCurveInfo(String name, String iconUrl, int iconRcsId, int effectType) {
        super(name, iconUrl, iconRcsId, effectType);
    }

    public ChangeSpeedCurveInfo(String name, String iconUrl, int iconRcsId, int effectType, int effectMode, String packageId) {
        super(name, iconUrl, iconRcsId, effectType, effectMode, packageId);
    }

    public String getName() {
        return getName();
    }

    public String getEffectName() {
        return mEffectName;
    }

    public void setEffectName(String effectName) {
        mEffectName = effectName;
    }
}

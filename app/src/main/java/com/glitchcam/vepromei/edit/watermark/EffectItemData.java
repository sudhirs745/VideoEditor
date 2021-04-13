package com.glitchcam.vepromei.edit.watermark;

/**
 * @author liupanfeng
 * @desc
 * @date 2020/10/15 15:00
 */
public class EffectItemData {

    public static final int TYPE_MOSAIC = 1;

    public static final int TYPE_BLUR = 2;


    private int mType;

    private int mImagePath;

    private String mName;


    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getImagePath() {
        return mImagePath;
    }

    public void setImagePath(int mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}

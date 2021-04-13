package com.glitchcam.vepromei.edit.background;

import com.google.gson.annotations.SerializedName;

public class MultiColorInfo {

    public float r;
    public float g;
    public float b;

    private String mColorValue;
    @SerializedName("isSelect")
    private boolean mSelected;
    private String mFilePath;

    public String getColorValue() {
        return mColorValue;
    }

    public void setColorValue(String mColorValue) {
        this.mColorValue = mColorValue;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    public void setFilePath(String filePath) {
        mFilePath =  filePath;
    }

    public String getFilePath() {
        return mFilePath;
    }
}

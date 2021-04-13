package com.glitchcam.vepromei.bean;

import java.util.HashMap;
import java.util.Map;

import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;

/**
 * author：yangtailin on 2020/7/27 16:00
 */
public class CutData {

    private Map<String, Float> mTransformData = new HashMap<>();
    private int mRatio;
    private float mRatioValue;
    private boolean mIsOldData = false;//设置是否是老数据

    public CutData() {
        mTransformData.put(STORYBOARD_KEY_SCALE_X, 1.0F);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
    }

    public Map<String, Float> getTransformData() {
        return mTransformData;
    }

    public void setTransformData(Map<String, Float> transformData) {
        this.mTransformData = transformData;
    }

    public void putTransformData(String key, float value) {
        mTransformData.put(key, value);
    }

    public float getTransformData(String key) {
        return mTransformData.get(key);
    }

    public int getRatio() {
        return mRatio;
    }

    public void setRatio(int ratio) {
        this.mRatio = ratio;
    }

    public float getRatioValue() {
        return mRatioValue;
    }

    public void setRatioValue(float ratioValue) {
        this.mRatioValue = ratioValue;
    }

    public void setIsOldData(boolean mIsOldData) {
        this.mIsOldData = mIsOldData;
    }

    public boolean isOldData() {
        return mIsOldData;
    }

    @Override
    public String toString() {
        return "CutData{" +
                "mTransformData=" + mTransformData +
                ", mRatio=" + mRatio +
                ", mRatioValue=" + mRatioValue +
                ", mIsOldData=" + mIsOldData +
                '}';
    }
}

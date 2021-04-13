package com.glitchcam.vepromei.bean.makeup;

public class MakeupEffect<T> extends BaseBeautyData {
    private T effectContent;

    public T getEffectContent() {
        return effectContent;
    }

    public void setEffectContent(T effectContent) {
        this.effectContent = effectContent;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }
}

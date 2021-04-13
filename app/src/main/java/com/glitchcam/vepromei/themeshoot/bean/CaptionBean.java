package com.glitchcam.vepromei.themeshoot.bean;

import java.io.Serializable;

public class CaptionBean implements Serializable {
    String text;//显示文字
    int countIndex;//位置标识

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(int countIndex) {
        this.countIndex = countIndex;
    }
}

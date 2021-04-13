package com.glitchcam.vepromei.themeshoot.bean;

import android.graphics.Bitmap;

import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;

public class ThemePreviewBean {
    public static final int THEME_TYPE_START = 100;
    public static final int THEME_TYPE_NORMAL = 101;
    public static final int THEME_TYPE_END = 102;

    String name;//名称
    String bgUrl;//背景图地址
    Bitmap bitmap;//显示图Bitmap
    long duration;//总时长
    long startDuration;//开始时长
    String comText;//字幕文字
    String filterId;//滤镜id
    int filterIndex;
    int transCount;//转场添加位置
    int type = THEME_TYPE_NORMAL;//片头 片尾

    NvsTimelineCompoundCaption compoundCaption;//字幕
    NvsTimelineVideoFx videoFx;//添加的特效，用于替换时使用

    public NvsTimelineVideoFx getVideoFx() {
        return videoFx;
    }

    public int getType() {
        return type;
    }

    public NvsTimelineCompoundCaption getCompoundCaption() {
        return compoundCaption;
    }

    public void setCompoundCaption(NvsTimelineCompoundCaption compoundCaption) {
        this.compoundCaption = compoundCaption;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTransCount() {
        return transCount;
    }

    public void setTransCount(int transCount) {
        this.transCount = transCount;
    }

    public void setVideoFx(NvsTimelineVideoFx videoFx) {
        this.videoFx = videoFx;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartDuration() {
        return startDuration;
    }

    public void setStartDuration(long startDuration) {
        this.startDuration = startDuration;
    }

    public String getComText() {
        return comText;
    }

    public void setComText(String comText) {
        this.comText = comText;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public int getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(int filterIndex) {
        this.filterIndex = filterIndex;
    }
}

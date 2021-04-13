package com.glitchcam.vepromei.mimodemo.mediapaker.bean;

import android.net.Uri;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.MeicamContextWrap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CaoZhiChao on 2018/6/5 11:07
 */
public class MediaData {
    private int id;
    private int type;
    private String path;
    private String thumbPath;
    private Uri thumbPathUri;
    private long duration;
    private long data;
    private String displayName;
    private boolean state;
    //记录点击时显示的大小的位置
    private int position;

    public MediaData() {
    }

    /**
     * 图片是视频的区别就是没有duration
     * @param thumbPathUri 该字段主要是为了在Andoird Q使用,在其以下为空
     */
    public MediaData(int id, int type, String path, String thumbPath,Uri thumbPathUri, long data, String displayName, boolean state) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.thumbPath = thumbPath;
        this.thumbPathUri = thumbPathUri;
        this.data = data;
        this.displayName = displayName;
        this.state = state;
        this.duration = -1;
    }


    /**
     * 视频
     * @param thumbPathUri 该字段主要是为了在Andoird Q使用,在其以下为空
     */
    public MediaData(int id, int type, String path, String thumbPath, Uri thumbPathUri, long duration, long data, String displayName, boolean state) {
        this.id = id;
        this.type = type;
        this.path = path;
        this.thumbPath = thumbPath;
        this.thumbPathUri = thumbPathUri;
        this.duration = duration;
        this.data = data;
        this.displayName = displayName;
        this.state = state;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDate() {
        String yearMonthDate = MeicamContextWrap.getInstance().getContext().getResources().getString(R.string.yearMonthDate);
        return new SimpleDateFormat(yearMonthDate)
                .format(new Date(data));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path == null ? "" : path;
    }

    public String getThumbPath() {
        return thumbPath == null ? "" : thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath == null ? "" : thumbPath;
    }

    public Uri getThumbPathUri() {
        return thumbPathUri;
    }

    public void setThumbPathUri(Uri thumbBitmapPath) {
        this.thumbPathUri = thumbBitmapPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName;
    }
}

package com.glitchcam.vepromei.mimodemo.common.template.model;

import java.util.List;

//镜头片段数据类
public class ShotInfo {
    public int getShot() {
        return shot;
    }

    public void setShot(int shot) {
        this.shot = shot;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getMainTrackFilter() {
        return mainTrackFilter;
    }

    public void setMainTrackFilter(String mainTrackFilter) {
        this.mainTrackFilter = mainTrackFilter;
    }

    public List<SubTrackFilterInfo> getSubTrackFilter() {
        return subTrackFilter;
    }

    public void setSubTrackFilter(List<SubTrackFilterInfo> subTrackFilter) {
        this.subTrackFilter = subTrackFilter;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<SpeedInfo> getSpeed() {
        return speed;
    }

    public void setSpeed(List<SpeedInfo> speed) {
        this.speed = speed;
    }

    public List<RepeatInfo> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<RepeatInfo> repeat) {
        this.repeat = repeat;
    }

    private int shot;//镜头号
    private String filter;//片段滤镜效果，只作用于这一个片段
    private String trans;//转场，不写效果默认为硬切

    //分屏不同画面的滤镜，mainTrackFilter为最底层画面，subTrackFilter为在底层画面上增加的画面
    private String mainTrackFilter;//主轨道滤镜
    private List<SubTrackFilterInfo> subTrackFilter;//子轨道滤镜
    private float duration;//片段持续时间,单位是毫秒，变速后的时长，需要计算变速前的视频片段的时长

    private boolean reverse;//表示片段视频是否倒放，默认为false
    private List<SpeedInfo> speed;//变速
    private List<RepeatInfo> repeat;//画面反复

    ////////////////////////////////////////////////////////////////////////////////////
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCanPlaced() {
        return mCanPlaced;
    }

    public void setCanPlaced(boolean mCanPlaced) {
        this.mCanPlaced = mCanPlaced;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(long fileDuration) {
        this.fileDuration = fileDuration;
    }

    public long getNeedDuration() {
        return needDuration;
    }

    public void setNeedDuration(long needDuration) {
        this.needDuration = needDuration;
    }

    public String getTransLen() {
        return transLen;
    }

    public void setTransLen(String transLen) {
        this.transLen = transLen;
    }

    public String getCompoundCaption() {
        return compoundCaption;
    }

    public void setCompoundCaption(String compoundCaption) {
        this.compoundCaption = compoundCaption;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }
    private long trimIn;//裁剪入点
    private String source;
    private boolean mCanPlaced;
    private long fileDuration;//视频文件时长，单位为毫秒
    private long needDuration;//实际需要的时长，单位为毫秒
    private String transLen;
    private String compoundCaption;

    ////////////////////////////////额外添加的参数信息/////////////////////////////////////////
//    public List<List<VideoClipInfo>> getTrackClipInfos() {
//        return trackClipInfos;
//    }
//
//    public void setTrackClipInfos(List<List<VideoClipInfo>> trackClipInfos) {
//        this.trackClipInfos = trackClipInfos;
//    }
//    /*
//        trackClipInfos表示轨道视频片段信息，包括主轨道，子轨道片段数据信息,trackClipInfos.size 表示模板所带镜头数。
//        trackClipInfos里的一个表示当前镜头里面要添加到clip上可能需要的片段数据，片段视频路径相同，
//        只是可能trimIn，trimOut，speed0等属性值不同
//    */
//    private List<List<VideoClipInfo>> trackClipInfos;//
}

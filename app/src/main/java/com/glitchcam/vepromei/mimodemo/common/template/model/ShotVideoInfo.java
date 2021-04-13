package com.glitchcam.vepromei.mimodemo.common.template.model;

import android.text.TextUtils;


import com.glitchcam.vepromei.mimodemo.common.template.utils.TemplateFileUtils;

import java.util.List;

//存储镜头视频数据信息
public class ShotVideoInfo {
    public void resetShotVideoInfo(){
        fileDuration = 0;
        //如果视频源素材是时间线默认扫换源，数据不重置
        if (!TextUtils.isEmpty(videoClipPath) && !videoClipPath.contains(TemplateFileUtils.PATH_ASSETS)){
            videoClipPath = null;
        }
        converClipPath = null;
        updateClipTrimIn(0);
    }

    public void updateClipTrimIn(long trimIn){
        if (trackClipInfos == null || trackClipInfos.isEmpty()){
            return;
        }
        this.trimIn = trimIn;
        long newTrimIn = trimIn;
        int clipCount = trackClipInfos.size();
        for (int clipIndex = 0;clipIndex < clipCount;clipIndex++){
            TrackClipInfo trackClipInfo = trackClipInfos.get(clipIndex);
            if (trackClipInfo == null){
                continue;
            }
            boolean isReverse = trackClipInfo.isReverse();
            if (isReverse){
                //需要倒放操作的片段，跳过更新裁剪入点
                continue;
            }
            int reapeatFlag = trackClipInfo.getRepeatFlag();
            if (reapeatFlag > 0 && reapeatFlag % 3 == 0){
                //反复里面第二个正放片段特殊处理
                long curRealNeedDuration = trackClipInfo.getRealNeedDuration();
                trackClipInfo.setTrimIn(newTrimIn - curRealNeedDuration);
                continue;
            }
            trackClipInfo.setTrimIn(newTrimIn);
            long realNeedDuration = trackClipInfo.getRealNeedDuration();
            newTrimIn += realNeedDuration;
        }
    }
    public ShotVideoInfo(List<TrackClipInfo> trackClipInfos,
                         long realNeedDuration,
                         long durationBySpeed,
                         int shot,
                         int trackIndex,
                         boolean isConvertFlag){
        this.trackClipInfos = trackClipInfos;
        this.realNeedDuration = realNeedDuration;
        this.durationBySpeed = durationBySpeed;
        this.shot = shot;
        this.trackIndex = trackIndex;
        this.isConvertFlag = isConvertFlag;
    }

    public int getShot() {
        return shot;
    }

    public void setShot(int shot) {
        this.shot = shot;
    }

    public String getVideoClipPath() {
        return videoClipPath;
    }

    public void setVideoClipPath(String videoClipPath) {
        this.videoClipPath = videoClipPath;
    }

    public long getFileDuration() {
        return fileDuration;
    }

    public void setFileDuration(long fileDuration) {
        this.fileDuration = fileDuration;
    }

    public long getRealNeedDuration() {
        return realNeedDuration;
    }

    public void setRealNeedDuration(long realNeedDuration) {
        this.realNeedDuration = realNeedDuration;
    }

    public long getDurationBySpeed() {
        return durationBySpeed;
    }

    public void setDurationBySpeed(long durationBySpeed) {
        this.durationBySpeed = durationBySpeed;
    }

    public String getConverClipPath() {
        return converClipPath;
    }

    public void setConverClipPath(String converClipPath) {
        this.converClipPath = converClipPath;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public List<TrackClipInfo> getTrackClipInfos() {
        return trackClipInfos;
    }

    public void setTrackClipInfos(List<TrackClipInfo> trackClipInfos) {
        this.trackClipInfos = trackClipInfos;
    }

    public boolean isConvertFlag() {
        return isConvertFlag;
    }

    public void setConvertFlag(boolean convertFlag) {
        isConvertFlag = convertFlag;
    }
    public long getTrimIn() {
        return trimIn;
    }

    private int shot;
    private String videoClipPath;//源视频文件路径
    private long fileDuration;//源视频文件时长
    private long realNeedDuration;//实际需要的时长
    private long durationBySpeed;//变速后的时长
    private String converClipPath;//转码后的视频文件路径
    private int trackIndex;//轨道索引
    //根据变速列表拆分的视频片段列表，存储视频片段信息
    private List<TrackClipInfo> trackClipInfos;//当前片段需要
    private boolean isConvertFlag;//是否转码标识
    private long trimIn;//对源视频的裁剪入点值
}

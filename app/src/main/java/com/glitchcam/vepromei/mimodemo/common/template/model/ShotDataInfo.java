package com.glitchcam.vepromei.mimodemo.common.template.model;

import java.util.List;

//存储镜头里的数据信息，包括主轨道，子轨道数据
public class ShotDataInfo {
    public ShotVideoInfo getMainTrackVideoInfo() {
        return mainTrackVideoInfo;
    }

    public void setMainTrackVideoInfo(ShotVideoInfo mainTrackVideoInfo) {
        this.mainTrackVideoInfo = mainTrackVideoInfo;
    }

    public List<ShotVideoInfo> getSubTrackVideoInfos() {
        return subTrackVideoInfos;
    }

    public void setSubTrackVideoInfos(List<ShotVideoInfo> subTrackVideoInfos) {
        this.subTrackVideoInfos = subTrackVideoInfos;
    }

    private ShotVideoInfo mainTrackVideoInfo;//主轨视频信息
    private List<ShotVideoInfo> subTrackVideoInfos;//子轨视频信息，可能包含多个子轨道
}

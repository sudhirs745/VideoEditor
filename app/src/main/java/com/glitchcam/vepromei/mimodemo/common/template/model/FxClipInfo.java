package com.glitchcam.vepromei.mimodemo.common.template.model;


import com.glitchcam.vepromei.mimodemo.common.dataInfo.ClipInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TransitionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.VideoClipFxInfo;

/**
 * 带特效的Clip
 */
public class FxClipInfo extends ClipInfo {
    //滤镜
    private VideoClipFxInfo mVideoClipFxInfo;
    //转场
    private TransitionInfo mTransitionInfo;

    public VideoClipFxInfo getVideoClipFxInfo() {
        return mVideoClipFxInfo;
    }

    public void setVideoClipFxInfo(VideoClipFxInfo videoClipFxInfo) {
        this.mVideoClipFxInfo = videoClipFxInfo;
    }

    public TransitionInfo getTransitionInfo() {
        return mTransitionInfo;
    }

    public void setTransitionInfo(TransitionInfo transitionInfo) {
        this.mTransitionInfo = transitionInfo;
    }
}

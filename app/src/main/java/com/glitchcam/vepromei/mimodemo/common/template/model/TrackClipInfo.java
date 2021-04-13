package com.glitchcam.vepromei.mimodemo.common.template.model;


import com.glitchcam.vepromei.mimodemo.common.Constants;

//根据镜头里的变速列表拆分的视频片段信息
public class TrackClipInfo {
    public TrackClipInfo(){}
    public TrackClipInfo(int trachIndex,
                         float speed0,
                         float speed1,
                         String trans,
                         String filter,
                         String trackFilter,
                         boolean isReverse,
                         int repeatFlag,
                         long trimIn,
                         long realNeedDuration){
        this.trackIndex = trachIndex;
        this.speed0 = speed0;
        this.speed1 = speed1;
        this.trans = trans;
        this.filter = filter;
        this.trackFilter = trackFilter;
        this.isReverse = isReverse;
        this.repeatFlag = repeatFlag;
        this.trimIn = trimIn;
        this.realNeedDuration = realNeedDuration;
    }

    public long getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(long trimIn) {
        this.trimIn = trimIn;
    }


    public long getRealNeedDuration() {
        return realNeedDuration;
    }

    public void setRealNeedDuration(long realNeedDuration) {
        this.realNeedDuration = realNeedDuration;
    }

    public float getSpeed0() {
        return speed0;
    }

    public void setSpeed0(float speed0) {
        this.speed0 = speed0;
    }

    public float getSpeed1() {
        return speed1;
    }
    public void setSpeed1(float speed1) {
        this.speed1 = speed1;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }
    public String getTrackFilter() {
        return trackFilter;
    }

    public void setTrackFilter(String trackFilter) {
        this.trackFilter = trackFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }
    public int getTrackIndex() {
        return trackIndex;
    }
    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }
    public int getRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(int repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public float getDurationRatio() {
        return durationRatio;
    }

    public void setDurationRatio(float durationRatio) {
        this.durationRatio = durationRatio;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private long trimIn;//裁剪入点，单位为微秒
    private long realNeedDuration;//当前片段需要的时长，单位为微秒

    private float speed0 = Constants.DEFAULT_SPEED_VALUE;//起始速度值
    private float speed1 = Constants.DEFAULT_SPEED_VALUE;//终止速度值
    private String trans;//转场
    private String trackFilter;//轨道滤镜，trackFilter与filter可能不同时存在
    private String filter;//滤镜
    private boolean isReverse = false;//是否需要倒放标识。false标识正放，true则倒放
    /*
        trackIndex 默认是0，表示当前片段是主轨道片段。
        如果大于0，则表示是子轨道，1表示第一子轨道，2表示第二子轨道，依次递推
     */
    private int trackIndex = 0;
    /*
        反复片段的标识，默认值是0。值大于0表示反复片段。
        值是1表示第一个正放片段，值是2表示倒放,值是3表示第二个正放片段，依次类比
    */
    private int repeatFlag = 0;
    /*
         时长比例值，默认值是1。durationRatio = 源视频文件时长 / 当前片段真正需要的时长，
         用于在轨道添加片段时计算新的speed0 和 speed1
     */
    float durationRatio = Constants.DEFAULT_DURATION_RATIO;;
}

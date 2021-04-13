package com.glitchcam.vepromei.mimodemo.common.template.model;


import com.glitchcam.vepromei.mimodemo.common.Constants;

public class SpeedInfo {
    private float start;//变速片段起始时间，单位是毫秒，当前值代表变速后的时长
    private float end;////变速片段终止时间，单位是毫秒，当前值代表变速后的时长
    private float speed0 = Constants.DEFAULT_SPEED_VALUE;//起始速度值
    private float speed1 = Constants.DEFAULT_SPEED_VALUE;//终止速度值
    public SpeedInfo(){}
    public SpeedInfo(float start,float end,float speed0,float speed1){
        this.start = start;
        this.end = end;
        this.speed0 = speed0;
        this.speed1 = speed1;
    }
    long needDuration;//
    public long getNeedDuration() {
        return needDuration;
    }

    public void setNeedDuration(long needDuration) {
        this.needDuration = needDuration;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
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
}

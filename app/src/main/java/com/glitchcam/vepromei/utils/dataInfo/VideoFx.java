package com.glitchcam.vepromei.utils.dataInfo;

public class VideoFx {

    public static final String ATTACHMENT_KEY_SUB_TYPE = "subType";
    protected String classType = "videoFx";
    protected int index;
    // builtin package
    protected String type;
    protected String subType;
    protected String desc = "Storyboard";


    private float mTransX;

    private float mTransY;

    private float mScaleX;

    private float mScaleY;

    private float mRotation;


    //强度
    protected float intensity = 1;

    private float[] region;

    private float unitSize;

    private float radius;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }


    public void setTransX(float transX) {
        this.mTransX = transX;
    }

    public void setTransY(float transY) {
        this.mTransY = transY;
    }

    public void setScaleX(float scaleX) {
        this.mScaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.mScaleY = scaleY;
    }

    public void setRotation(float rotation) {
        this.mRotation = rotation;
    }


    public float getScaleX() {
        return mScaleX;
    }


    public float getScaleY() {
        return mScaleY;
    }


    public float getTransX() {
        return mTransX;
    }


    public float getTransY() {
        return mTransY;
    }


    public float getRotation() {
        return mRotation;
    }


    public float[] getRegion() {
        return region;
    }

    public void setRegion(float[] region) {
        this.region = region;
    }

    public void setUnitSize(float unitSize) {
        this.unitSize = unitSize;
    }

    public float getUnitSize() {
        return unitSize;
    }

    public void setRadius(float radius) {
        this.radius=radius;
    }

    public float getRadius() {
        return radius;
    }

}

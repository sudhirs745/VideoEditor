package com.glitchcam.vepromei.mimodemo.common.template.model;


import com.glitchcam.vepromei.mimodemo.common.Constants;

public enum RadioEnum {

    RADIO16V9("16v9", Constants.AspectRatio.AspectRatio_16v9),
    POINT1V1("1v1", Constants.AspectRatio.AspectRatio_1v1),
    POINT9V16("9v16", Constants.AspectRatio.AspectRatio_9v16),
    POINT3V4("3v4", Constants.AspectRatio.AspectRatio_3v4),
    POINT4V3("4v3", Constants.AspectRatio.AspectRatio_4v3),
    POINT9V18("9v18", Constants.AspectRatio.AspectRatio_9v18),
    POINT18V9("18v9", Constants.AspectRatio.AspectRatio_18v9),
    POINT2D39V1("2.39v1", Constants.AspectRatio.AspectRatio_2d39v1),
    POINT2D55V1("2.55v1", Constants.AspectRatio.AspectRatio_2d55v1);

    private RadioEnum(String value, int key) {
        this.value = value;
        this.key = key;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    //根据key获取value的值
    public static String getStringRadio(int key) {
        for (RadioEnum s : RadioEnum.values()) {
            if (s.getKey() == key) {
                return s.getValue();
            }
        }
        return "";
    }


    //根据匹配value的值获取key
    public static int getIntRadio(String channelName) {
        for (RadioEnum s : RadioEnum.values()) {
            if (channelName.equals(s.getValue())) {
                return s.getKey();
            }
        }
        return 0;
    }
}

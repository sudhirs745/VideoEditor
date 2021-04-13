package com.glitchcam.vepromei.utils.dataInfo;

import android.annotation.SuppressLint;



public class FxParam<T> {
    private final static String TAG = "FxParam";
    public final static String TYPE_STRING = "string";
    public final static String TYPE_STRING_OLD = "String";
    public final static String TYPE_BOOLEAN = "boolean";
    public final static String TYPE_FLOAT = "float";
    public final static String TYPE_OBJECT = "Object";
    // object 或其他
    String type;
    String key;
    
    /**
     * 这个可能是float 或者 boolean 或者是一组数字（region）
     */
    T value;

    @SuppressLint("NewApi")
    public FxParam(String type, String key, T value) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}

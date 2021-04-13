package com.glitchcam.vepromei.utils.dataInfo;

import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.glitchcam.vepromei.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.glitchcam.vepromei.utils.dataInfo.FxParam.TYPE_BOOLEAN;
import static com.glitchcam.vepromei.utils.dataInfo.FxParam.TYPE_FLOAT;
import static com.glitchcam.vepromei.utils.dataInfo.FxParam.TYPE_STRING;
import static com.glitchcam.vepromei.utils.dataInfo.FxParam.TYPE_STRING_OLD;


public class StoryboardInfo extends VideoFx{
    private static String TAG="StoryboardInfo";

    public final static String SUB_TYPE_BACKGROUND = "background";
    public final static String SUB_TYPE_CROPPER = "cropper";
    public final static String SUB_TYPE_CROPPER_TRANSFROM = "cropper_transform";
    public final static String DESC_TYPE = "Storyboard";
    public final static String STORYBOARD_KEY_TYPE = "No Background";
    public final static String DESC_KEY_TYPE = "Description String";
    public final static String RESOURCE_KEY_TYPE = "Resource Dir";

    private Map<String, Float> clipTrans = new HashMap<>();
    private String storyDesc;
    private String source;
    private String sourceDir;

    private int backgroundType;


    public int getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(int backgroundType) {
        this.backgroundType = backgroundType;
    }

    protected Map<String, FxParam> mMeicamFxParam = new TreeMap<>();

    public String getStoryDesc() {
        return storyDesc;
    }

    public void setStoryDesc(String backgroundStory) {
        this.storyDesc = backgroundStory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public Map<String, Float> getClipTrans() {
        return clipTrans;
    }

    public void setClipTrans(Map<String, Float> clipTrans) {
        this.clipTrans = clipTrans;
    }


    public void setStringVal(String key, String value) {
        FxParam<String> param = new FxParam<>(TYPE_STRING, key, value);
        mMeicamFxParam.put(param.getKey(), param);
//        NvsVideoFx object = getObject();
//        if (object != null) {
//            object.setStringVal(key, value);
//        }
    }

    public String getStringVal(String key) {
        FxParam meicamFxParam = mMeicamFxParam.get(key);
        if (meicamFxParam == null) {
            return null;
        }
        if (TYPE_STRING.equals(meicamFxParam.getType())) {
            return (String) meicamFxParam.getValue();
        }
        return null;
    }


    public float getFloatVal(String key) {
        FxParam meicamFxParam = mMeicamFxParam.get(key);
        if (meicamFxParam == null) {
            return -1;
        }
        if (TYPE_FLOAT.equals(meicamFxParam.getType())) {
            Object value = meicamFxParam.getValue();
            if (value instanceof Float) {
                return (float) value;
            } else if (value instanceof Double) {
                double resultD = (double) value;
                return (float) resultD;
            }
        }
        return -1;
    }

    public void setBooleanVal(String key, boolean value) {
        FxParam<Boolean> param = new FxParam<>(TYPE_BOOLEAN, key, value);
        mMeicamFxParam.put(param.getKey(), param);
//        NvsVideoFx object = getObject();
//        if (object != null) {
//            object.setBooleanVal(key, value);
//        }
    }

    public void setFloatVal(String key, float value) {
        FxParam<Float> param = new FxParam<>(TYPE_FLOAT, key, value);
        mMeicamFxParam.put(param.getKey(), param);
//        NvsVideoFx object = getObject();
//        if (object != null) {
//            object.setFloatVal(key, value);
//        }
    }



    public NvsVideoFx getStoryboardFx(NvsVideoClip videoClip, String type) {
        int fxCount = videoClip.getFxCount();
        for (int index = 0; index < fxCount; index++) {
            NvsVideoFx clipFx = videoClip.getFxByIndex(index);
            Object attachment = clipFx.getAttachment(ATTACHMENT_KEY_SUB_TYPE);
            if (attachment != null && attachment instanceof String) {
                String subType = (String)attachment;
                if (subType.equals(type)) {
                    return clipFx;
                }
            }
        }
        return null;
    }


    public NvsVideoFx bindToTimelineByType(NvsVideoClip videoClip, String type) {
        if (videoClip == null) {
            return null;
        }

        NvsVideoFx nvsVideoFx = getStoryboardFx(videoClip, type);
        if (nvsVideoFx == null) {
            nvsVideoFx = videoClip.appendBuiltinFx(getDesc()); //Storyboard
        }
        if (nvsVideoFx==null){
            Logger.e(TAG,"bindToTimelineByType nvsVideoFx is null!");
            return null;
        }
        Set<String> keySet = mMeicamFxParam.keySet();
        for (String key : keySet) {
            FxParam meicamFxParam = mMeicamFxParam.get(key);
            if (TYPE_STRING.equals(meicamFxParam.getType()) || TYPE_STRING_OLD.equals(meicamFxParam.getType())) {
                nvsVideoFx.setStringVal(key, (String) meicamFxParam.getValue());
            } else if (TYPE_BOOLEAN.equals(meicamFxParam.getType())) {
                nvsVideoFx.setBooleanVal(key, (Boolean) meicamFxParam.getValue());
            }
        }
        if (nvsVideoFx != null) {
            nvsVideoFx.setAttachment(ATTACHMENT_KEY_SUB_TYPE, type);
            setIndex(nvsVideoFx.getIndex());
        }
        return nvsVideoFx;
    }

}

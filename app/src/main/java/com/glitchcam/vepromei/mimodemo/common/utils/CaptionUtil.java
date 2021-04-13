package com.glitchcam.vepromei.mimodemo.common.utils;

import android.graphics.PointF;

import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.CompoundCaptionInfo;

public class CaptionUtil {
    //保存组合字幕数据
    public static CompoundCaptionInfo saveCompoundCaptionData(NvsTimelineCompoundCaption caption, CompoundCaptionInfo compoundCaptionInfo) {
        if (caption == null || compoundCaptionInfo == null)
            return null;
        CompoundCaptionInfo captionInfo = compoundCaptionInfo.clone();
        long inPoint = caption.getInPoint();
        captionInfo.setInPoint(inPoint);
        long outPoint = caption.getOutPoint();
        captionInfo.setOutPoint(outPoint);
        int captionCount = caption.getCaptionCount();
        for (int idx = 0;idx < captionCount;idx++){
            CompoundCaptionInfo.CompoundCaptionAttr compoundCaptionAttr = new CompoundCaptionInfo.CompoundCaptionAttr();
            compoundCaptionAttr.setCaptionFontName(compoundCaptionInfo.getCaptionFontName());
            compoundCaptionAttr.setCaptionText(caption.getText(idx));
            captionInfo.addCaptionAttributeList(compoundCaptionAttr);
        }
        captionInfo.setCaptionZVal((int) caption.getZValue());
        captionInfo.setAnchor(caption.getAnchorPoint());
        PointF pointF = caption.getCaptionTranslation();
        captionInfo.setTranslation(pointF);
        return captionInfo;
    }
}

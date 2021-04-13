package com.glitchcam.vepromei.capture;

import java.util.ArrayList;

public class MakeupDataItem extends BeautyShapeDataItem {
    private ArrayList<BeautyShapeDataItem> mSubData;

    public ArrayList<BeautyShapeDataItem> getSubData() {
        return mSubData;
    }

    public void setSubData(ArrayList<BeautyShapeDataItem> subData) {
        this.mSubData = subData;
    }
}

package com.glitchcam.vepromei.bean.makeup;

import android.content.Context;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.utils.ColorUtil;

public class NullBeautyItem implements BeautyData{
    public static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    @Override
    public String getName(Context context) {
        return context.getString(R.string.makeup_null);
    }

    @Override
    public Object getImageResource() {
        return ASSETS_MAKEUP_PATH + "/makeup_null.png";
    }

    @Override
    public void setFolderPath(String folderPath) {

    }

    @Override
    public String getFolderPath() {
        return null;
    }

    @Override
    public boolean isBuildIn() {
        return true;
    }

    @Override
    public void setIsBuildIn(boolean isBuildIn) {

    }

    @Override
    public int getBackgroundColor() {
        return ColorUtil.MAKEUP_DEFAULT_TEXT_BG;
    }
}

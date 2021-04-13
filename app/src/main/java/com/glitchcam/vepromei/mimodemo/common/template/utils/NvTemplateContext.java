package com.glitchcam.vepromei.mimodemo.common.template.utils;

import android.content.Context;

import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.template.model.TemplateInfo;

import java.util.List;


/**
 * 模板上下文，用于提供模板数据
 */
public class NvTemplateContext {
    private static volatile NvTemplateContext mInstance;
    private Context mContext;
    private int mSelectListIndex = 0;
    private MiMoLocalData mSelectedMimoData;
    private List<TemplateInfo> mTemplateList;

    private NvTemplateContext(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (NvTemplateContext.class) {
                if (mInstance == null) {
                    mInstance = new NvTemplateContext(context);
                }
            }
        }
    }

    public static NvTemplateContext getInstance() {
        return mInstance;
    }

    public void setSelectListIndex(int selectListIndex) {
        this.mSelectListIndex = selectListIndex;
    }

    public void setSelectedMimoData(MiMoLocalData miMoLocalData) {
        this.mSelectedMimoData = miMoLocalData;
    }

    public MiMoLocalData getSelectedMimoData() {
        return mSelectedMimoData;
    }
}

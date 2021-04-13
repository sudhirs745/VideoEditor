package com.glitchcam.vepromei.mimodemo.common.utils;

import android.content.Context;

public class MeicamContextWrap {
    private Context mContext;
    private static MeicamContextWrap mMeicamContextWrap;

    public Context getContext() {
        return mContext;
    }

    public static MeicamContextWrap getInstance() {
        if (mMeicamContextWrap == null) {
            synchronized (MeicamContextWrap.class) {
                if (mMeicamContextWrap == null) {
                    mMeicamContextWrap = new MeicamContextWrap();
                }
            }
        }
        return mMeicamContextWrap;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}

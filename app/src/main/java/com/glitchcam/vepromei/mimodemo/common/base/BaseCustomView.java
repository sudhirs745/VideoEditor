package com.glitchcam.vepromei.mimodemo.common.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public abstract class BaseCustomView extends RelativeLayout {
    protected Context mContext;
    protected View mRootView;
    public BaseCustomView(Context context) {
        super(context);
    }

    public BaseCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRootView = LayoutInflater.from(mContext).inflate(initRootView(), this);
        initView();
        initData(attrs);
    }

    protected abstract void initData(AttributeSet attrs);

    protected abstract void initView();

    protected abstract int initRootView();
}

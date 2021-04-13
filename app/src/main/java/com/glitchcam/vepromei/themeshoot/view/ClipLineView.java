package com.glitchcam.vepromei.themeshoot.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.ScreenUtils;

import java.util.List;


public class ClipLineView extends View {
    private static final String TAG = "ClipLineView";

    private Context mContext;
    private int mClipIndex = 0;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mInterval;
    private int mCorners;

    private List<Double> mRatios;


    public ClipLineView(Context context) {
        super(context);
        init(context);
    }

    public ClipLineView(Context context, @Nullable AttributeSet attributes) {
        super(context, attributes);
        init(context);

    }

    private void init(Context context) {
        this.mContext = context;
        mInterval = ScreenUtils.dip2px(mContext, 5);
        mCorners = ScreenUtils.dip2px(mContext, 3);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(context.getResources().getColor(R.color.white));
    }

    public void setRatios(List<Double> ratios) {
        this.mRatios = ratios;
        invalidate();
    }

    public int getmRatiosSize() {
        if (mRatios != null) {
            return mRatios.size();
        }
        return 0;
    }

    public void setClipIndex(int index) {
        this.mClipIndex = index;
        setVisibility(VISIBLE);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRatios != null && mRatios.size() > 0) {
            Double all = 0d;
            for (Double mRatio : mRatios) {
                all += mRatio;
            }
            int ratiosSize = mRatios.size();
            float left = 0;
            float right = 0;
            double drawWidth = (mWidth - (ratiosSize - 1) * mInterval) / all;
            for (int i = 0; i < ratiosSize; i++) {
                if (i < mClipIndex) {
                    mPaint.setColor(mContext.getResources().getColor(R.color.ms_blue));
                    mPaint.setAlpha(0xff);
                } else {
                    mPaint.setColor(mContext.getResources().getColor(R.color.white));
                    mPaint.setAlpha(0xb3);
                }
                if (i == (ratiosSize - 1)) {
                    right = mWidth;
                } else {
                    right = left + (int) Math.floor(drawWidth * mRatios.get(i));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(left, 0, right, mHeight, mCorners, mCorners, mPaint);
                } else {
                    canvas.drawRect(left, 0, right, mHeight, mPaint);
                }
                left = right + mInterval - 1;
            }
        }

    }
}

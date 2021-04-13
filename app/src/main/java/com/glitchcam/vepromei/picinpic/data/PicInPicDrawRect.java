package com.glitchcam.vepromei.picinpic.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.glitchcam.vepromei.utils.ScreenUtils;

/**
 * Created by czl on 2018/8/6.
 */
public class PicInPicDrawRect extends View {
    private RectF mViewRectF = new RectF();
    private Paint mRectPaint = new Paint();
    private Context mContext;
    public PicInPicDrawRect(Context context) {
        this(context, null);
    }

    public PicInPicDrawRect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRectPaint();
    }

    public void setDrawRect(RectF rectF) {
        mViewRectF = rectF;
        invalidate();
    }

    private void initRectPaint(){
        /*
         * 设置颜色
         * Set color
         * */
        mRectPaint.setColor(Color.parseColor("#4A90E2"));
        /*
         * 设置抗锯齿
         * Setting up anti-aliasing
         * */
        mRectPaint.setAntiAlias(true);
        /*
         * 设置线宽
         * Set line width
         * */
        mRectPaint.setStrokeWidth(ScreenUtils.dip2px(mContext,3));
        /*
         * 设置非填充
         * Set non-fill
         * */
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        *  绘制矩形框
        * draw the rectangle
        * */
        canvas.drawRect(mViewRectF, mRectPaint);
    }
}

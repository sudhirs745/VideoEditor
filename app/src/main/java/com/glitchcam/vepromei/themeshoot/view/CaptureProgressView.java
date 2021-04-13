package com.glitchcam.vepromei.themeshoot.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.utils.ScreenUtils;

public class CaptureProgressView extends View {

    private Context mContext;
    private int progress = 0;
    private int mWidth;
    private int mHeight;
    private int progressWidth;
    private String mText;
    private Paint drawPaint;
    private Paint progressPaint;
    private     Paint noneProgressPaint;
    private Paint textPaint;

    public CaptureProgressView(Context context) {
        super(context);
        init(context);
    }

    public CaptureProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressWidth = ScreenUtils.dip2px(mContext, 3);
        progressPaint.setColor(mContext.getResources().getColor(R.color.ms_blue));
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setStyle(Paint.Style.STROKE);

        noneProgressPaint = new Paint();
        noneProgressPaint.setColor(mContext.getResources().getColor(R.color.nv_colorTranslucent));
        noneProgressPaint.setAntiAlias(true);
        noneProgressPaint.setStrokeWidth(progressWidth);
        noneProgressPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(ScreenUtils.sp2px(mContext, 13));
        textPaint.setColor(mContext.getResources().getColor(R.color.ms_blue));
    }

    private void initPaint() {

    }

    public void setTextAndProgress(String text, int progress) {
        this.progress = progress;
        this.mText = text;
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
        drawProgress(canvas);
        drawText(canvas);
    }

    private void drawProgress(Canvas canvas) {
        RectF oval = new RectF(progressWidth, progressWidth, mWidth - progressWidth, mHeight - progressWidth);
//        RectF oval = new RectF(0, 0,  mWidth, mHeight);
        if (progress >= 100) {
            progress = 100;
        }
        int degree = progress * 360 / 100;
        canvas.drawArc(oval, -90, degree, false, progressPaint);
        canvas.drawArc(oval, -90 + degree, 360 - degree, false, noneProgressPaint);
    }

    private void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(mText)) {
            float txtWidth = textPaint.measureText(mText, 0, mText.length());
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float txtHeight = fm.bottom - fm.top;
            //计算长宽
            int x = (int) (getMeasuredWidth() / 2.0f - txtWidth/ 2.0f);
            int y = (int) (getMeasuredHeight() / 2.0f + txtHeight / 4.0f);
            canvas.drawText(mText, x, y, textPaint);
        }
    }
}

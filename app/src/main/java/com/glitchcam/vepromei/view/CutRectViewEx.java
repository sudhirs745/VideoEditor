package com.glitchcam.vepromei.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.glitchcam.vepromei.R;

/**
 * author：yangtailin on 2020/7/1 17:15
 */
public class CutRectViewEx extends View {
    private final static String TAG = "CutRectViewEx";
    private final static int RECT_L_T = 1;
    private final static int RECT_L_B = 2;
    private final static int RECT_R_T = 3;
    private final static int RECT_R_B = 4;
    private final static int ANGEL_LENGTH = 30;
    private final static int PADDING = 0;
    private final static int ONE_FINGER = 1;
    private final static int TWO_FINGER = 2;
    private final static int TOUCH_RECT_SIZE = 100; //触摸区域的范围
    private Rect mDrawRect = new Rect();
    private Path mPath = new Path();
    private PorterDuffXfermode xFermode;
    private int mTouchRect = -1;
    private Paint mBgPaint;
    private Paint mPaint;
    private Paint mCornerPaint;
    private int mPaintColor = Color.WHITE;
    private int mPaintBgColor;
    private int mPadding = PADDING;
    private int mAngelLength = ANGEL_LENGTH;
    private int mStrokeWidth = 4;
    private OnTransformListener mOnTransformListener;
    private float mOldTouchX = 0;
    private float mOldTouchY = 0;

    private boolean mIsTwoFingerEvent = false;
    private double mTwoFingerStartLength;
    private PointF mTwoFingerOldPoint = new PointF();
    private double mTwoFingerEndLength;

    private float mWidthHeightRatio = -1;//宽高比，如果是-1，代表自由宽高比


    private int mRectWidth;
    private int mRectHeight;

    public CutRectViewEx(Context context) {
        super(context);
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.FILL);

        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mCornerPaint = new Paint();
        mCornerPaint.setColor(mPaintColor);
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(6);
        initView(context);
    }

    public CutRectViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mCornerPaint = new Paint();
        mCornerPaint.setColor(mPaintColor);
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(6);
        initView(context);
    }


    private void initView(Context context) {
        xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        mPaintBgColor = context.getResources().getColor(R.color.adjust_rect_shadow_color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制矩形区域
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        //绘制外围背景
        mBgPaint.setColor(mPaintBgColor);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        mBgPaint.setXfermode(xFermode);
        mBgPaint.setColor(Color.RED);
        mBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mDrawRect, mBgPaint);
        mBgPaint.setXfermode(null);
        canvas.restoreToCount(layerId);

        mPath.reset();
        //绘制边框
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPath.moveTo(mDrawRect.left, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.top);
        mPath.lineTo(mDrawRect.right, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.bottom);
        mPath.lineTo(mDrawRect.left, mDrawRect.top);
        canvas.drawPath(mPath, mPaint);

        //绘制中线
        int width = mDrawRect.right - mDrawRect.left;
        int height = mDrawRect.bottom - mDrawRect.top;
        //竖线
        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.top);
        mPath.lineTo(mDrawRect.left + (width) * 1.0F / 3 * 2, mDrawRect.bottom);
        canvas.drawPath(mPath, mPaint);

        //横线
        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3 * 2);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3 * 2);
        canvas.drawPath(mPath, mPaint);

        mPath.moveTo(mDrawRect.left, mDrawRect.top + (height) * 1.0F / 3);
        mPath.lineTo(mDrawRect.right, mDrawRect.top + (height) * 1.0F / 3);
        canvas.drawPath(mPath, mPaint);

        mAngelLength = ANGEL_LENGTH;
        if (mAngelLength > width) {
            mAngelLength = width;
        }
        if (mAngelLength > height) {
            mAngelLength = height;
        }
        //绘制左上角
        mPath.reset();
        mPath.moveTo(mDrawRect.left + mAngelLength + mStrokeWidth / 2, mDrawRect.top + mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.left + mStrokeWidth / 2, mDrawRect.top + mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.left + mStrokeWidth / 2, mDrawRect.top + mAngelLength + +mStrokeWidth / 2);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右上角
        mPath.moveTo(mDrawRect.right - mAngelLength - mStrokeWidth / 2, mDrawRect.top + mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.right - mStrokeWidth / 2, mDrawRect.top + mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.right - mStrokeWidth / 2, mDrawRect.top + mAngelLength + mStrokeWidth / 2);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制右下角
        mPath.moveTo(mDrawRect.right - mStrokeWidth / 2, mDrawRect.bottom - mStrokeWidth / 2 - mAngelLength);
        mPath.lineTo(mDrawRect.right - mStrokeWidth / 2, mDrawRect.bottom - mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.right - mStrokeWidth / 2 - mAngelLength, mDrawRect.bottom - mStrokeWidth / 2);
        canvas.drawPath(mPath, mCornerPaint);

        //绘制左下角
        mPath.moveTo(mDrawRect.left + mStrokeWidth / 2, mDrawRect.bottom - mStrokeWidth / 2 - mAngelLength);
        mPath.lineTo(mDrawRect.left + mStrokeWidth / 2, mDrawRect.bottom - mStrokeWidth / 2);
        mPath.lineTo(mDrawRect.left + mStrokeWidth / 2 + mAngelLength, mDrawRect.bottom - mStrokeWidth / 2);
        canvas.drawPath(mPath, mCornerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount > TWO_FINGER) {
            return false;
        }

        if (((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) && (pointerCount == ONE_FINGER)) {
            mIsTwoFingerEvent = false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        if (pointerCount == TWO_FINGER) {
            //oneFingerActionUp();
            return true;
        } else {
            return oneFingerTouch(event);
        }
    }

    public void setWidthHeightRatio(float ratio) {
        this.mWidthHeightRatio = ratio;
    }

    private boolean oneFingerTouch(MotionEvent event) {
        if (mIsTwoFingerEvent) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTwoFingerEvent = false;
                mOldTouchX = 0;
                mOldTouchY = 0;
            }
            return false;
        }
        int action = event.getAction();
        float touchX = event.getRawX();
        float touchY = event.getRawY();
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchRect = getTouchRect(event);
            mOldTouchX = event.getRawX();
            mOldTouchY = event.getRawY();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            int eventX = (int) event.getX();
            int eventY = (int) event.getY();
            if (mTouchRect == RECT_L_T) {
                mDrawRect.right = getWidth() - (getWidth() - getRectWidth()) / 2 - mPadding;
                mDrawRect.bottom = getHeight() - (getHeight() - getRectHeight()) / 2 - mPadding;
                if (mWidthHeightRatio > 0) {
                    mDrawRect.left = mDrawRect.right - (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                    mDrawRect.top = eventY;
                } else {
                    mDrawRect.left = eventX;
                }
                mDrawRect.top = eventY;
                correctRect();
            } else if (mTouchRect == RECT_L_B) {
                mDrawRect.top = (getHeight() - getRectHeight()) / 2 + mPadding;
                mDrawRect.right = getWidth() - (getWidth() - getRectWidth()) / 2 - mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.left = mDrawRect.right - (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.left = eventX;
                }
                mDrawRect.bottom = eventY;
                correctRect();
            } else if (mTouchRect == RECT_R_T) {
                mDrawRect.left = (getWidth() - getRectWidth()) / 2 + mPadding;
                mDrawRect.bottom = getHeight() - (getHeight() - getRectHeight()) / 2 - mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.right = mDrawRect.left + (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.right = eventX;
                }
                mDrawRect.top = eventY;

                correctRect();
            } else if (mTouchRect == RECT_R_B) {

                mDrawRect.left = (getWidth() - getRectWidth()) / 2 + mPadding;
                mDrawRect.top = (getHeight() - getRectHeight()) / 2 + mPadding;

                if (mWidthHeightRatio > 0) {
                    mDrawRect.right = mDrawRect.left + (int) ((mDrawRect.bottom - mDrawRect.top) * 1.0F * mWidthHeightRatio);
                } else {
                    mDrawRect.right = eventX;
                }
                mDrawRect.bottom = eventY;
                correctRect();
            }
            if (mTouchRect > 0) {
                invalidate();
            } else {
                if (mOnTransformListener != null) {
                    if (mOldTouchX != 0) {
                        mOnTransformListener.onTrans(mOldTouchX - touchX, mOldTouchY - touchY);
                    }
                }
                mOldTouchX = touchX;
                mOldTouchY = touchY;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            oneFingerActionUp();
        }
        return super.onTouchEvent(event);
    }

    private void oneFingerActionUp() {
        if (mWidthHeightRatio > 0) {
            if (mTouchRect > 0) {
                float scale = getRectWidth() * 1.0F / Math.abs(mDrawRect.right - mDrawRect.left);
                float scaleH = getRectHeight() * 1.0F / Math.abs(mDrawRect.bottom - mDrawRect.top);
                if (scale > scaleH) {
                    scale = scaleH;
                }
                Point anchorBefore = getAnchorOnScreen();
                setDrawRectSize(mRectWidth, mRectHeight);
                invalidate();
                Point anchorAfter = getAnchorOnScreen();

                if (mOnTransformListener != null) {
                    Point point = new Point();
                    point.x = (anchorAfter.x - anchorBefore.x);
                    point.y = (anchorAfter.y - anchorBefore.y);
                    mOnTransformListener.onRectMoved(scale, point, anchorBefore);
                }
            }
        } else {
            scaleAndMoveRectToCenter();
        }
        mOldTouchX = 0;
        mOldTouchY = 0;
    }

    /**
     * 纠正边界
     */
    private void correctRect() {
        if (mDrawRect.top > mDrawRect.bottom) {
            mDrawRect.top = mDrawRect.bottom;
        }
        if (mDrawRect.right < mDrawRect.left) {
            mDrawRect.right = mDrawRect.left;
        }

        if (mDrawRect.top < mPadding) {
            mDrawRect.top = mPadding;
        }
        if (mDrawRect.bottom > getHeight() - mPadding) {
            mDrawRect.bottom = getHeight() - mPadding;
        }
    }

    private void scaleAndMoveRectToCenter() {
        Point anchorBefore = getAnchorOnScreen();
        int width = mDrawRect.right - mDrawRect.left;
        int height = mDrawRect.bottom - mDrawRect.top;
        Point size = getFreeCutRectSize(width, height);
        setDrawRectSize(size.x, size.y);
        Point anchorAfter = getAnchorOnScreen();

        if (mOnTransformListener != null) {
            float scale = getRectWidth() * 1.0F / width;
            float scaleH = getRectHeight() * 1.0F / height;
            if (scale < scaleH) {
                scale = scaleH;
            }
            Point point = new Point();
            point.x = (anchorAfter.x - anchorBefore.x);
            point.y = (anchorAfter.y - anchorBefore.y);
            mOnTransformListener.onRectMoved(scale, point, anchorBefore);
        }
    }

    private Point getFreeCutRectSize(int rectWidth, int rectHeight) {
        float ratio = rectWidth * 1.0F / rectHeight;
        int width = getWidth();
        int height = getHeight();
        float layoutRatio = width * 1.0F / height;
        Point rectSize = new Point();
        if (ratio > layoutRatio) { //宽对齐
            rectSize.x = width;
            rectSize.y = (int) (width * 1.0F / ratio);
        } else {//高对齐
            rectSize.y = height;
            rectSize.x = (int) (height * ratio);
        }
        return rectSize;
    }

    private boolean twoFingerTouch(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
            float xLen = event.getX(0) - event.getX(1);
            float yLen = event.getY(0) - event.getY(1);
            mTwoFingerStartLength = Math.sqrt((xLen * xLen) + (yLen * yLen));
            mTwoFingerOldPoint.set(xLen, yLen);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            float xLen = event.getX(0) - event.getX(1);
            float yLen = event.getY(0) - event.getY(1);
            float oldDegree = (float) Math.toDegrees(Math.atan2(mTwoFingerOldPoint.x, mTwoFingerOldPoint.y));
            float newDegree = (float) Math.toDegrees(Math.atan2((event.getX(0) - event.getX(1)), (event.getY(0) - event.getY(1))));
            mTwoFingerEndLength = Math.sqrt(xLen * xLen + yLen * yLen);

            float scalePercent = (float) (mTwoFingerEndLength / mTwoFingerStartLength);
            float degree = newDegree - oldDegree;

            if (mOnTransformListener != null) {
                mOnTransformListener.onScaleAndRotate(scalePercent, degree);
            }
            mTwoFingerStartLength = mTwoFingerEndLength;
            mTwoFingerOldPoint.set(xLen, yLen);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mOnTransformListener != null) {
                float scale = getWidth() * 1.0F / mDrawRect.right - mDrawRect.left;
                mOnTransformListener.onTransEnd(scale, new float[]{mDrawRect.right - mDrawRect.left, mDrawRect.bottom - mDrawRect.top});
            }
        }
        return super.onTouchEvent(event);
    }

    public int getTouchRect(MotionEvent event) {
        if (isInLeftTop(event)) {
            return RECT_L_T;
        } else if (isInLeftBottom(event)) {
            return RECT_L_B;
        } else if (isInRightBottom(event)) {
            return RECT_R_B;
        } else if (isInRightTop(event)) {
            return RECT_R_T;
        }
        return -1;
    }

    /**
     * 获取屏幕坐标系下的锚点
     *
     * @return
     */
    public Point getAnchorOnScreen() {
        Point anchor = new Point();
        int[] location = new int[2];
        getLocationOnScreen(location);
        if (mTouchRect == RECT_L_T) {
            anchor.x = location[0] + mDrawRect.right;
            anchor.y = location[1] + mDrawRect.bottom;
        } else if (mTouchRect == RECT_L_B) {
            anchor.x = location[0] + mDrawRect.right;
            anchor.y = location[1] + mDrawRect.top;
        } else if (mTouchRect == RECT_R_T) {
            anchor.x = location[0] + mDrawRect.left;
            anchor.y = location[1] + mDrawRect.bottom;
        } else if (mTouchRect == RECT_R_B) {
            anchor.x = location[0] + mDrawRect.left;
            anchor.y = location[1] + mDrawRect.top;
        }
        return anchor;
    }


    public int getRectWidth() {
        return mRectWidth;
    }

    public int getRectHeight() {
        return mRectHeight;
    }

    public Rect getDrawRect() {
        return mDrawRect;
    }


    public void setDrawRectSize(int width, int height) {
        mDrawRect.left = (int) ((getWidth() - width) * 1.0F / 2) + mPadding;
        mDrawRect.right = mDrawRect.left + width;
        mDrawRect.top = (int) ((getHeight() - height) * 1.0F / 2) + mPadding;
        mDrawRect.bottom = mDrawRect.top + height;
        mRectWidth = width;
        mRectHeight = height;
        invalidate();
    }

    public void setDrawRectColor(int color) {
        mPaintColor = color;
        mPaint.setColor(mPaintColor);
        invalidate();
    }

    public int getPadding() {
        return mPadding;
    }

    private boolean isInLeftTop(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= mDrawRect.left && touchX <= TOUCH_RECT_SIZE + mDrawRect.left && touchY >= mDrawRect.top && touchY <= TOUCH_RECT_SIZE + mDrawRect.top);
    }

    private boolean isInRightTop(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= (mDrawRect.right - TOUCH_RECT_SIZE) && touchX <= mDrawRect.right && touchY >= mDrawRect.top && touchY <= TOUCH_RECT_SIZE + mDrawRect.top);
    }

    private boolean isInLeftBottom(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= mDrawRect.left && touchX <= TOUCH_RECT_SIZE + mDrawRect.left
                && touchY >= (mDrawRect.bottom - TOUCH_RECT_SIZE) && touchY <= mDrawRect.bottom);
    }

    private boolean isInRightBottom(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        return (touchX >= (mDrawRect.right - TOUCH_RECT_SIZE) && touchX <= mDrawRect.right
                && touchY >= (mDrawRect.bottom - TOUCH_RECT_SIZE) && touchY <= mDrawRect.bottom);
    }

    public void setOnTransformListener(OnTransformListener listener) {
        mOnTransformListener = listener;
    }

    public interface OnTransformListener {
        void onTrans(float deltaX, float deltaY);

        void onScaleAndRotate(float scale, float degree);

        void onTransEnd(float scale, float[] size);

        void onRectMoved(float scale, Point anchorInWindow, Point distance);
    }
}

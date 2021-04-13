package com.glitchcam.vepromei.edit.background.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TransformView extends View {

    private final static String TAG = "TransformView";

    private final static int ONE_FINGER = 1;
    private final static int TWO_FINGER = 2;

    private boolean mIsTwoFingerEvent = false;
    private OnTransformTouchEventListener mOnPipTouchEventListener;
    private double mTwoFingerStartLength;
    private double mTwoFingerEndLength;
    private double mClickMoveDistance = 0.0D;
    private float mOneFingerTargetX;
    private float mOneFingerTargetY;
    private long mDownTime;
    private PointF mTwoFingerOldPoint = new PointF();
    private PointF mDownPointF = new PointF(0, 0);
    private PointF mMovePointF = new PointF(0, 0);

    public TransformView(Context context) {
        super(context);
    }

    public TransformView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnPipTouchListener(OnTransformTouchEventListener listener) {
        mOnPipTouchEventListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnPipTouchEventListener == null) {
            return false;
        }

        int pointerCount = event.getPointerCount();
        if (pointerCount > TWO_FINGER) {
            return false;
        }

        if (((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) && (pointerCount == ONE_FINGER)) {
            mIsTwoFingerEvent = false;
        }

        if (pointerCount == TWO_FINGER) {
            mIsTwoFingerEvent = true;
            twoFingerTouch(event);
        } else {
            oneFingerTouch(event);
        }
        return true;
    }

    private void oneFingerTouch(MotionEvent event) {

        if (mIsTwoFingerEvent) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsTwoFingerEvent = false;
                mOnPipTouchEventListener.onTouchUp(new PointF(event.getX(), event.getY()));
            }
            return;
        }
        mOneFingerTargetX = event.getX();
        mOneFingerTargetY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownTime = System.currentTimeMillis();
                mDownPointF.set(mOneFingerTargetX, mOneFingerTargetY);
                mOnPipTouchEventListener.onTouchDown(new PointF(mOneFingerTargetX, mOneFingerTargetY));
                break;
            }
            case MotionEvent.ACTION_UP: {
                PointF actionUpPoint = new PointF(mOneFingerTargetX, mOneFingerTargetY);
                mOnPipTouchEventListener.onTouchUp(actionUpPoint);
                mClickMoveDistance = 0.0D;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mClickMoveDistance = mClickMoveDistance + Math.sqrt(Math.pow(mOneFingerTargetX - mDownPointF.x, 2)
                        + Math.pow(mOneFingerTargetY - mDownPointF.y, 2));
                mMovePointF.set(mOneFingerTargetX, mOneFingerTargetY);
                mOnPipTouchEventListener.onDrag(mDownPointF, mMovePointF);
                mDownPointF.set(mOneFingerTargetX, mOneFingerTargetY);
            }
            break;
            default:
                break;
        }
    }

    private void twoFingerTouch(MotionEvent event) {
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

            if(mOnPipTouchEventListener != null) {
                mOnPipTouchEventListener.onScaleAndRotate(scalePercent, degree);
            }

            mTwoFingerStartLength = mTwoFingerEndLength;
            mTwoFingerOldPoint.set(xLen, yLen);
        }
    }

    public interface OnTransformTouchEventListener {

        void onTouchDown(PointF curPoint);

        void onScaleAndRotate(float scale, float degree);

        void onDrag(PointF prePointF, PointF nowPointF);

        void onTouchUp(PointF curPoint);
    }
}

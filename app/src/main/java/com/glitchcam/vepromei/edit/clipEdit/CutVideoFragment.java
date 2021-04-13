package com.glitchcam.vepromei.edit.clipEdit;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsLiveWindowExt;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.bean.CutData;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.TimeFormatUtil;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.view.CutRectLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_ROTATION_Z;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_SCALE_X;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_SCALE_Y;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;


public class CutVideoFragment extends Fragment {
    public static final int ONSCALE = 0X100;
    public static final int ONROTATE = 0X200;
    private static final String TAG = "CutVideoFragment";
    private static final String PARAM_MAX_DURATION = "max_duration";
    private static final int MESSAGE_RESET_PLATBACK_STATE = 100;
    private NvsLiveWindowExt mLiveWindow;
    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private RelativeLayout mPlayerLayout;
    private TextView mCurrentPlaytimeView;
    private TextView mTotalDurationView;
    private SeekBar mSeekBar;
    private ImageView mPlayButtonImage;
    private CutRectLayout mCutView;
    private long mStartTime;
    private long mMaxDuration;
    private OnPlayProgressChangeListener mOnPlayProgessChangeListener;
    private OnFragmentLoadFinisedListener mFragmentLoadFinisedListener;
    private VideoFragmentListener mVideoFragmentCallBack;
    private OnCutRectChangedListener mOnCutRectChangedListener;
    private float mMinLiveWindowScale = 1.0F;
    private float[] mSize = new float[2];
    private Point mOriginalSize;
    private Map<String, Float> mTransformData = new HashMap<>();
    private FloatPoint mCenterPoint = new FloatPoint();
    private int mRatio = NvAsset.AspectRatio_NoFitRatio;
    private float mRatioValue = -1F;
    private RectF mRegionData;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == MESSAGE_RESET_PLATBACK_STATE) {
                //playVideo(mStartTime, mStartTime + getDuration());
                seekTimeline(0, 0);
            }
            return false;
        }
    });

    public void setOnCutRectChangelisener(OnCutRectChangedListener listener) {
        this.mOnCutRectChangedListener = listener;
    }

    public void setOnPlayProgressChangeListener(OnPlayProgressChangeListener onPlayProgessChangeListener) {
        this.mOnPlayProgessChangeListener = onPlayProgessChangeListener;
    }

    public CutVideoFragment() {
        mTransformData.put(STORYBOARD_KEY_SCALE_X, 1.0F);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
    }

    public static CutVideoFragment newInstance(long maxDuration) {
        CutVideoFragment fragment = new CutVideoFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_MAX_DURATION, maxDuration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mMaxDuration = getArguments().getLong(PARAM_MAX_DURATION);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cut_video, container, false);
        mLiveWindow = rootView.findViewById(R.id.liveWindow);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTCROP);
        mPlayerLayout = rootView.findViewById(R.id.playerLayout);
        mCutView = rootView.findViewById(R.id.cut_view);
        mCurrentPlaytimeView = rootView.findViewById(R.id.currentPlaytime);
        mTotalDurationView = rootView.findViewById(R.id.totalDuration);
        mSeekBar = rootView.findViewById(R.id.playSeekBar);
        mPlayButtonImage = rootView.findViewById(R.id.playImage);
        initListener();
        return rootView;
    }

    private void initListener() {
        mPlayButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                } else {
                    if (mTimeline == null) {
                        return;
                    }
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    long alreadyPlayDuration = startTime - mStartTime;//已经在指定片段上播放的时长
                    long endTime = startTime + getDuration() - (alreadyPlayDuration);
                    playVideo(startTime, endTime);
                    if (mOnPlayProgessChangeListener != null) {
                        mOnPlayProgessChangeListener.onPlayStateChanged(true);
                    }
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private long currentTime = 0L;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentTime = getDuration() * progress / 100 + mStartTime; //绝对时间
                    seekTimeline(currentTime, 0);
                    updateCurPlayTime(currentTime);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playVideo(currentTime, currentTime + getDuration());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
    }

    public void initData() {
        mTotalDurationView.setText(" / " + TimeFormatUtil.formatUsToString2(mTimeline.getDuration()));
        final boolean[] updateView = {false};
        mCutView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!updateView[0]) {
                    Point size = getOriginalLiveWindowLayoutParam();
                    mSize[0] = size.x;
                    mSize[1] = size.y;
                    mCutView.setDrawRectColor(getResources().getColor(R.color.adjust_ratio_rect_color));
                    setCutRectViewSize(size);
                    setLiveWindowSize(size);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            initLiveWindowCenterPoint();
                            Point size = changeCutRectViewNoScale(mRatio);
                            mMinLiveWindowScale = getSuitLiveWindowScale(size);
                            mTransformData = parseToViewTransData(mTransformData);
                            mLiveWindow.setRotation(mTransformData.get(STORYBOARD_KEY_ROTATION_Z));
                            scaleLiveWindow(mTransformData.get(STORYBOARD_KEY_SCALE_X));
                            mLiveWindow.setTranslationX(mTransformData.get(STORYBOARD_KEY_TRANS_X));
                            mLiveWindow.setTranslationY(mTransformData.get(STORYBOARD_KEY_TRANS_Y));

                            FloatPoint pointLT = new FloatPoint();
                            FloatPoint pointRB = new FloatPoint();
                            float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
                            float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;
                            pointLT.x = mCenterPoint.x - halfWidth;
                            pointLT.y = mCenterPoint.y - halfHeight;

                            pointRB.x = mCenterPoint.x + halfWidth;
                            pointRB.y = mCenterPoint.y + halfHeight;

                            float oldScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
                            float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z);
                            pointLT = transformData(pointLT, mCenterPoint, oldScale, degree,
                                    mTransformData.get(STORYBOARD_KEY_TRANS_X), mTransformData.get(STORYBOARD_KEY_TRANS_Y));
                            pointRB = transformData(pointRB, mCenterPoint, oldScale, degree,
                                    mTransformData.get(STORYBOARD_KEY_TRANS_X), mTransformData.get(STORYBOARD_KEY_TRANS_Y));

                            mCenterPoint.x = (pointLT.x + pointRB.x) / 2;
                            mCenterPoint.y = (pointLT.y + pointRB.y) / 2;
                            mRegionData = updateRegionData();
                        }
                    });
                    updateView[0] = true;
                }
            }
        });
        connectTimelineWithLiveWindow();
        mCutView.setOnTransformListener(new CutRectLayout.OnTransformListener() {
            @Override
            public void onTrans(float deltaX, float deltaY) {
                if (deltaX == 0 || deltaY == 0) {
                    return;
                }
                float oldTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X);
                float oldTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y);
                float newTransX = oldTransX;
                float newTransY = oldTransY;
                if (canTrans(deltaX, 0, 0F)) {
                    newTransX = oldTransX - deltaX;
                    mCenterPoint.x -= deltaX;
                }
                if (canTrans(0, deltaY, 0F)) {
                    newTransY = oldTransY - deltaY;
                    mCenterPoint.y -= deltaY;
                }
                transLiveWindow(newTransX, newTransY);
            }

            @Override
            public void onScaleAndRotate(float scale, float degree) {
                if (scale < 1.0F && !canTrans(0, 0, -degree)) {
                    return;
                }
                float newDegree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z) - degree;
                if (newDegree > 45 && degree < 0) {
                    return;
                }
                if (newDegree < -45 && degree > 0) {
                    return;
                }
                newDegree = (int) newDegree;
                //双指缩放时不需要影响旋转角度
                // mLiveWindow.setRotation(newDegree);
                // mTransformData.put(STORYBOARD_KEY_ROTATION_Z, newDegree);
                double scaleValue = computeScale(newDegree, scale);
                double newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;

                Logger.d(TAG, "onScaleAndRotate: newScale = " + newScale + ",scaleValue =  " + scaleValue + ", scale = " + scale);

                if (newScale < scaleValue && scaleValue > 1.0F) {
                    newScale = (float) scaleValue;
                }
                if (scaleValue != 1.0F && scale < 1.0F) {
                    return;
                }
                Logger.d(TAG, "onScaleAndRotate: scaleResult  = " + newScale);

                if (newScale < mMinLiveWindowScale) {
                    newScale = mMinLiveWindowScale;
                }
                scaleLiveWindow((float) newScale);
                if (mOnCutRectChangedListener != null) {
                    mOnCutRectChangedListener.onScaleAndRotate((float) newScale, newDegree, ONSCALE);
                }
            }

            @Override
            public void onTransEnd(float scale, float[] size) {
                if (scale < 0) {
                    rotateVideo(mTransformData.get(STORYBOARD_KEY_ROTATION_Z));
                } else {
                    float newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;
                    scaleLiveWindow(newScale);
                    mTransformData.put(STORYBOARD_KEY_SCALE_X, newScale);
                    mTransformData.put(STORYBOARD_KEY_SCALE_Y, newScale);
                }

                if (size != null) {
                    mSize[0] = size[0];
                    mSize[1] = size[1];
                }
            }

            @Override
            public void onRectMoved(float scale, Point distance, Point anchor) {
                if (mOriginalSize == null) {
                    mOriginalSize = new Point();
                }
                mOriginalSize.x = mCutView.getRectWidth();
                mOriginalSize.y = mCutView.getRectHeight();
                mMinLiveWindowScale = getSuitLiveWindowScale(mOriginalSize);

                float newScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * scale;
                double minScale = computeScale(mTransformData.get(STORYBOARD_KEY_ROTATION_Z), 1.0F);
                if (newScale < minScale) {
                    newScale = (float) minScale;
                    scale = newScale / mTransformData.get(STORYBOARD_KEY_SCALE_X);
                }
                scaleLiveWindow(newScale);

                FloatPoint pointAfter = new FloatPoint();
                pointAfter.x = anchor.x;
                pointAfter.y = anchor.y;
                transformData(pointAfter, mCenterPoint, scale, 0);

                float deltaX = anchor.x - pointAfter.x + distance.x;
                float deltaY = anchor.y - pointAfter.y + distance.y;
                translateLiveWindow(deltaX, deltaY);
                mRegionData = updateRegionData();
            }
        });
    }

    private void updateCurPlayTime(long currentTime) {
        mCurrentPlaytimeView.setText(TimeFormatUtil.formatUsToString2(currentTime - mStartTime));
    }

    private void updatePlayProgress(long curPosition) {
        float progress = ((float) (curPosition - mStartTime) / (float) getDuration());
        mSeekBar.setProgress((int) (progress * 100));
        updateCurPlayTime(curPosition);
    }

    private void translateLiveWindow(float deltaX, float deltaY) {
        float newTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X) + deltaX;
        float newTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y) + deltaY;
        mLiveWindow.setTranslationX(newTransX);
        mLiveWindow.setTranslationY(newTransY);
        mCenterPoint.x += deltaX;
        mCenterPoint.y += deltaY;
        mTransformData.put(STORYBOARD_KEY_TRANS_X, newTransX);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, newTransY);
    }

    private Point getFreeCutRectSize(float[] size) {
        float ratio = size[0] / size[1];
        int width = mCutView.getWidth();
        int height = mCutView.getHeight();
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


    public void setCutData(CutData cutData) {
        if (cutData == null) {
            return;
        }
        mRatio = cutData.getRatio();
        mRatioValue = cutData.getRatioValue();
        Map<String, Float> transformData = cutData.getTransformData();
        Set<String> keySet = transformData.keySet();
        for (String key : keySet) {
            Float aFloat = transformData.get(key);
            if (aFloat != null) {
                mTransformData.put(key, aFloat);
            }
        }
    }

    private boolean canTrans(float deltaX, float deltaY, float deltaDegree) {
        //获取4个角的顶点数据，并进行坐标转换
        FloatPoint pointLT = new FloatPoint();
        FloatPoint pointLB = new FloatPoint();
        FloatPoint pointRT = new FloatPoint();
        FloatPoint pointRB = new FloatPoint();

        float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
        float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;

        FloatPoint centerPoint = new FloatPoint();
        centerPoint.x = mCenterPoint.x - deltaX;
        centerPoint.y = mCenterPoint.y - deltaY;

        pointLT.x = centerPoint.x - halfWidth;
        pointLT.y = centerPoint.y - halfHeight;

        pointLB.x = centerPoint.x - halfWidth;
        pointLB.y = centerPoint.y + halfHeight;

        pointRT.x = centerPoint.x + halfWidth;
        pointRT.y = centerPoint.y - halfHeight;

        pointRB.x = centerPoint.x + halfWidth;
        pointRB.y = centerPoint.y + halfHeight;

        float degree = mTransformData.get(STORYBOARD_KEY_ROTATION_Z) + deltaDegree;
        float scale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
        pointLT = transformData(pointLT, centerPoint, scale, degree);
        pointLB = transformData(pointLB, centerPoint, scale, degree);
        pointRT = transformData(pointRT, centerPoint, scale, degree);
        pointRB = transformData(pointRB, centerPoint, scale, degree);

        //判断四个顶点是否超出LiveWindow坐标范围外
        int[] location = new int[2];
        mCutView.getLocationOnScreen(location);
        int rectLeft = location[0] + mCutView.getDrawRectViewLeft();
        int rectTop = location[1] + mCutView.getDrawRectViewTop();
        int rectRight = rectLeft + mCutView.getRectWidth();
        int rectBottom = rectTop + mCutView.getRectHeight();
        //左上角
        FloatPoint point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectTop;
        boolean inRectLT = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //右上角
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectTop;
        boolean inRectRT = isInRect(pointLT, pointRT, pointRB, pointLB, point);

        //右下角
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectBottom;
        boolean inRectRB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        //左下角
        point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectBottom;
        boolean inRectLB = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        return (inRectLT && inRectLB && inRectRT && inRectRB);
    }

    /**
     * 计算缩放值
     *
     * @param newDegree
     * @return
     */
    public double computeScale(float newDegree, float deltaScale) {
        //获取4个角的顶点数据， 并进行坐标转换
        FloatPoint pointLT = new FloatPoint();
        FloatPoint pointLB = new FloatPoint();
        FloatPoint pointRT = new FloatPoint();
        FloatPoint pointRB = new FloatPoint();

        float halfWidth = mLiveWindow.getWidth() * 1.0F / 2;
        float halfHeight = mLiveWindow.getHeight() * 1.0F / 2;

        pointLT.x = mCenterPoint.x - halfWidth;
        pointLT.y = mCenterPoint.y - halfHeight;

        pointLB.x = mCenterPoint.x - halfWidth;
        pointLB.y = mCenterPoint.y + halfHeight;

        pointRT.x = mCenterPoint.x + halfWidth;
        pointRT.y = mCenterPoint.y - halfHeight;

        pointRB.x = mCenterPoint.x + halfWidth;
        pointRB.y = mCenterPoint.y + halfHeight;

        float oldScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) * deltaScale;

        pointLT = transformData(pointLT, mCenterPoint, oldScale, newDegree);
        pointLB = transformData(pointLB, mCenterPoint, oldScale, newDegree);
        pointRT = transformData(pointRT, mCenterPoint, oldScale, newDegree);
        pointRB = transformData(pointRB, mCenterPoint, oldScale, newDegree);

        int[] location = new int[2];
        mCutView.getLocationOnScreen(location);
        int rectLeft = location[0] + mCutView.getDrawRectViewLeft();
        int rectTop = location[1] + mCutView.getDrawRectViewTop();
        int rectRight = rectLeft + mCutView.getRectWidth();
        int rectBottom = rectTop + mCutView.getRectHeight();

        //左上角
        FloatPoint point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectTop;

        boolean inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        double scale = 1.0F;
        double scaleTemp = 1.0F;
        if (!inRect) {
            double pointToLineLR = getPointToLine(point, pointLT, pointRT);
            double pointToLineTB = getPointToLine(point, pointLT, pointLB);
            if ((!Double.isNaN(pointToLineLR)) && (!Double.isNaN(pointToLineTB))) {
                if (newDegree == 0) {
                    if (pointToLineLR > pointToLineTB) {
                        scale = (pointToLineLR + (halfHeight * oldScale)) / halfHeight;
                    } else {
                        scale = (pointToLineTB + halfWidth * oldScale) / halfWidth;
                    }
                } else {
                    if (pointToLineLR > pointToLineTB) {
                        scale = (pointToLineTB + halfWidth * oldScale) / halfWidth;
                    } else {
                        scale = (pointToLineLR + (halfHeight * oldScale)) / halfHeight;
                    }
                }
            }
        }
        scaleTemp = scale;

        //右上角
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectTop;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        if (!inRect) {
            double pointToLine1 = getPointToLine(point, pointRT, pointRB);
            double pointToLine2 = getPointToLine(point, pointRT, pointLT);
            if ((!Double.isNaN(pointToLine1)) && (!Double.isNaN(pointToLine2))) {
                if (pointToLine1 > pointToLine2) {
                    scaleTemp = (pointToLine2 + halfHeight * oldScale) / halfHeight;
                } else {
                    scaleTemp = (pointToLine1 + halfWidth * oldScale) / halfWidth;
                }
            }
        }
        if (scale < scaleTemp) {
            scale = scaleTemp;
        }

        //右下角
        point = new FloatPoint();
        point.x = rectRight;
        point.y = rectBottom;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        if (!inRect) {
            double pointToLineRL = getPointToLine(point, pointRB, pointLB);
            double pointToLineTB = getPointToLine(point, pointRB, pointRT);
            if ((!Double.isNaN(pointToLineRL)) && (!Double.isNaN(pointToLineTB))) {
                if (pointToLineRL < pointToLineTB) {
                    scaleTemp = (pointToLineRL + halfHeight * oldScale) / halfHeight;
                } else {
                    scaleTemp = (pointToLineTB + halfWidth * oldScale) / halfWidth;
                }
            }
        }

        if (scale < scaleTemp) {
            scale = scaleTemp;
        }

        //左下角
        point = new FloatPoint();
        point.x = rectLeft;
        point.y = rectBottom;

        inRect = isInRect(pointLT, pointRT, pointRB, pointLB, point);
        if (!inRect) {
            double pointToLineTB = getPointToLine(point, pointLT, pointLB);
            double pointToLineLR = getPointToLine(point, pointLB, pointRB);
            if ((!Double.isNaN(pointToLineTB)) && (!Double.isNaN(pointToLineLR))) {
                if (pointToLineTB < pointToLineLR) {
                    scaleTemp = (pointToLineTB + halfWidth * oldScale) / halfWidth;
                } else {
                    scaleTemp = (pointToLineLR + halfHeight * oldScale) / halfHeight;
                }
            }
        }

        if (scale < scaleTemp) {
            scale = scaleTemp;
        }
        return scale;
    }

    //判断点p是否在p1p2p3p4的矩形内
    public boolean isInRect(FloatPoint p1, FloatPoint p2, FloatPoint p3, FloatPoint p4, FloatPoint p) {
        boolean isPointIn = getCross(p1, p2, p) * getCross(p3, p4, p) >= 0 && getCross(p2, p3, p) * getCross(p4, p1, p) >= 0;
        return isPointIn;
    }

    // 计算 |p1 p2| X |p1 p|
    private float getCross(FloatPoint p1, FloatPoint p2, FloatPoint p) {
        return (p2.x - p1.x) * (p.y - p1.y) - (p.x - p1.x) * (p2.y - p1.y);
    }

    private FloatPoint transformData(FloatPoint point, FloatPoint centerPoint, float scale, float degree) {
        float[] src = new float[]{point.x, point.y};
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        point.x = Math.round(src[0]);
        point.y = Math.round(src[1]);
        return point;
    }

    private FloatPoint transformData(FloatPoint point, FloatPoint centerPoint, float scale, float degree, float transX, float transY) {
        float[] src = new float[]{point.x, point.y};
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setScale(scale, scale, centerPoint.x, centerPoint.y);
        matrix.mapPoints(src);
        matrix.setTranslate(transX, transY);
        matrix.mapPoints(src);
        point.x = src[0];
        point.y = src[1];
        return point;
    }

    /**
     * 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x0
     * @param y0
     * @return
     */
    private double pointToLine(float x1, float y1, float x2, float y2, float x0, float y0) {
        double space = 0;
        float a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        if (a > 0) {
            space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        } else {
            space = 0;
        }
        return space;
    }


    // 计算两点之间的距离
    private float lineSpace(float x1, float y1, float x2, float y2) {
        float lineLength = 0;
        lineLength = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2));
        return lineLength;
    }

    /**
     * 点到直线的距离
     *
     * @param anchor
     * @param pointA
     * @param pointB
     * @return
     */
    private double getPointToLine(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        return pointToLine(pointA.x, pointA.y, pointB.x, pointB.y, anchor.x, anchor.y);
    }

    /**
     * 点的X坐标到直线的距离
     *
     * @param anchor
     * @param pointA
     * @param pointB
     * @return
     */
    private float getPointToLineX(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        if (pointA.y - pointB.y <= 0.000001) {
            return 0;
        }
        float deltaX = (pointA.x - pointB.x) / (pointA.y - pointB.y) * (pointA.y - anchor.y);
        return Math.abs(deltaX);

    }

    /**
     * 点的Y坐标到直线的距离
     *
     * @param anchor
     * @param pointA
     * @param pointB
     * @return
     */
    private double getPointToLineY(FloatPoint anchor, FloatPoint pointA, FloatPoint pointB) {
        if (pointA.x - pointB.x <= 0.000001) {
            return 0;
        }
        float deltaY = (pointA.x - anchor.x) * (pointA.y - pointB.y) / (pointA.x - pointB.x);
        return Math.abs(deltaY);
    }


    public void setStartTime(long startTime) {
        this.mStartTime = startTime;
    }

    public long getDuration() {
        if (mMaxDuration > 0L) {
            return mMaxDuration;
        }
        if (mTimeline == null) {
            return 0L;
        }
        return mTimeline.getDuration();
    }

    private void setLiveWindowRatio(int ratio) {
        if (null == mTimeline) {
            return;
        }
        Point size = null;
        if (ratio == NvAsset.AspectRatio_NoFitRatio) {
            size = getOriginalLiveWindowLayoutParam();
        } else {
            size = getLiveWindowSizeByRatio(ratio);
        }
        setLiveWindowSize(size);
        setCutRectViewSize(size);
        if (ratio == NvAsset.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
    }

    private void setLiveWindowSize(Point size) {
        ViewGroup.LayoutParams layoutParams = mLiveWindow.getLayoutParams();
        layoutParams.width = size.x;
        layoutParams.height = size.y;
        mLiveWindow.setLayoutParams(layoutParams);
    }

    private void setCutRectViewSize(Point size) {
        mCutView.setDrawRectSize(size.x, size.y);
    }

    private void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null) {
            return;
        }
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playStopped(nvsTimeline);
                }
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                mHandler.sendEmptyMessage(MESSAGE_RESET_PLATBACK_STATE);
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
            }
        });
        mStreamingContext.setSeekingCallback(new NvsStreamingContext.SeekingCallback() {
            @Override
            public void onSeekingTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                updatePlayProgress(cur_position);
            }
        });
        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayButtonImage.setImageResource(R.mipmap.icon_pause);
                } else {
                    mPlayButtonImage.setImageResource(R.mipmap.icon_play);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

        mStreamingContext.connectTimelineWithLiveWindowExt(mTimeline, mLiveWindow);
        mCutView.setDrawRectSize(mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    private Point getOriginalLiveWindowLayoutParam() {
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        Point size = new Point();
        int screenWidth = mPlayerLayout.getWidth();
        int newHeight = mPlayerLayout.getHeight();
        int imageWidth = videoRes.imageWidth;
        int imageHeight = videoRes.imageHeight;
        float viewRatio = screenWidth * 1.0F / newHeight;
        float timelineRation = imageWidth * 1.0F / imageHeight;
        float ratio = 1.0F;
        if (timelineRation > viewRatio) {//宽对齐
            size.x = screenWidth;
            ratio = screenWidth * 1.0F / imageWidth;
            size.y = (int) (ratio * imageHeight);
        } else {//高对齐
            size.y = newHeight;
            ratio = newHeight * 1.0F / imageHeight;
            size.x = (int) (ratio * imageWidth);
        }
        return size;
    }

    private Point getLiveWindowSizeByRatio(int ratio) {
        int screenWidth = mPlayerLayout.getWidth();
        int newHeight = mPlayerLayout.getHeight();
        Point size = new Point();
        switch (ratio) {
            case NvAsset.AspectRatio_9v16:
                size.x = (int) (newHeight * 9.0 / 16);
                size.y = newHeight;
                break;
            case NvAsset.AspectRatio_16v9:
                size.x = screenWidth;
                size.y = (int) (screenWidth * 9.0 / 16);
                break;
            case NvAsset.AspectRatio_3v4:
                size.x = (int) (newHeight * 3.0 / 4);
                size.y = newHeight;
                break;
            case NvAsset.AspectRatio_4v3:
                size.x = screenWidth;
                size.y = (int) (screenWidth * 3.0 / 4);
                break;
            case NvAsset.AspectRatio_9v18:
                size.x = (int) (newHeight * 9.0 / 18);
                size.y = newHeight;
                break;
            case NvAsset.AspectRatio_18v9:
                size.x = screenWidth;
                size.y = (int) (screenWidth * 9.0 / 18);
                break;
            case NvAsset.AspectRatio_9v21:
                size.x = (int) (newHeight * 9.0 / 21);
                size.y = newHeight;
                break;
            case NvAsset.AspectRatio_21v9:
                size.x = screenWidth;
                size.y = (int) (screenWidth * 9.0 / 21);
                break;
            case NvAsset.AspectRatio_1v1:
                size.x = screenWidth;
                size.y = screenWidth;
                if (newHeight < screenWidth) {
                    size.x = newHeight;
                    size.y = newHeight;
                }
                break;
            default:
                size.x = screenWidth;
                size.y = (int) (screenWidth * 9.0 / 16);
                break;
        }
        return size;
    }

    private Point getLiveWindowSizeByRatio(float ratio) {
        int screenWidth = mPlayerLayout.getWidth();
        int screenHeight = mPlayerLayout.getHeight();
        Point size = new Point();
        if (ratio >= 1.0F) {
            size.x = screenWidth;
            size.y = (int) (screenWidth / ratio);
        } else {
            size.x = (int) (screenHeight * ratio);
            size.y = screenHeight;
        }

        if (size.x > screenWidth) {
            size.x = screenWidth;
        }
        if (size.y > screenHeight) {
            size.y = screenHeight;
        }

        return size;
    }

    public void rotateVideo(float degree) {
        mLiveWindow.setRotation(degree * 1.0F);
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, degree);
        float scaleValue = (float) computeScale(degree, 1.0F);
        float scale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
        if (scaleValue > scale) {
            scaleLiveWindow(scaleValue);
        }
    }

    private void scaleLiveWindow(float scaleValue) {
        mLiveWindow.setScaleX(scaleValue);
        mLiveWindow.setScaleY(scaleValue);
        mTransformData.put(STORYBOARD_KEY_SCALE_X, scaleValue);
        mTransformData.put(STORYBOARD_KEY_SCALE_Y, scaleValue);
    }

    private void transLiveWindow(float transX, float transY) {
        mLiveWindow.setTranslationX(transX);
        mLiveWindow.setTranslationY(transY);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, transX);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, transY);
    }

    public void reset() {
        mLiveWindow.setTranslationX(0);
        mLiveWindow.setTranslationY(0);
        mLiveWindow.setRotation(0);
        scaleLiveWindow(1.0F);
        setLiveWindowRatio(NvAsset.AspectRatio_NoFitRatio);
        mOriginalSize = null;
        mTransformData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_X, 0F);
        mTransformData.put(STORYBOARD_KEY_TRANS_Y, 0F);
        initLiveWindowCenterPoint();
        mRatio = NvAsset.AspectRatio_NoFitRatio;
        mMinLiveWindowScale = 1.0F;
        mRegionData = updateRegionData();
    }

    public void changeCutRectView(final int ratio) {
        //LiveWindow和 CutRectView需要分别设置，
        // LiveWindow适配CutRectView大小，需要和CutRectView宽和高对齐，
        // 如果LiveWindow宽或高有一边没有对齐，LiveWindow需要进行缩放
        Point size = null;
        if (ratio == NvAsset.AspectRatio_NoFitRatio) {
            if (mOriginalSize == null) {
                size = getOriginalLiveWindowLayoutParam();
                mOriginalSize = size;
            } else {
                size = mOriginalSize;
            }
        } else {
            size = getLiveWindowSizeByRatio(ratio);
        }
        setCutRectViewSize(size);
        if (ratio == NvAsset.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
        mMinLiveWindowScale = getSuitLiveWindowScale(size);
        mRatio = ratio;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRegionData = updateRegionData();
                float rotation = mTransformData.get(STORYBOARD_KEY_ROTATION_Z);
                if (rotation == 0) {
                    float oldScale = mTransformData.get(STORYBOARD_KEY_SCALE_X);
                    if (mMinLiveWindowScale > oldScale) {
                        scaleLiveWindow(mMinLiveWindowScale);
                    } else {
                        rotateVideo(rotation);
                    }
                } else {
                    rotateVideo(rotation);
                }
            }
        });
    }

    private RectF updateRegionData() {
        return getRectEx(mCutView.getRectWidth(), mCutView.getRectHeight(), mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    public Point changeCutRectViewNoScale(int ratio) {
        //LiveWindow和 CutRectView需要分别设置，
        // LiveWindow适配CutRectView大小，需要和CutRectView宽和高对齐，
        // 如果LiveWindow宽或高有一边没有对齐，LiveWindow需要进行缩放
        Point size = null;
        if (mRatioValue > 0) {
            size = getLiveWindowSizeByRatio(mRatioValue);
        } else {
            if (ratio == NvAsset.AspectRatio_NoFitRatio) {
                size = getOriginalLiveWindowLayoutParam();
            } else {
                size = getLiveWindowSizeByRatio(ratio);
            }
        }
        setCutRectViewSize(size);
        if (ratio == NvAsset.AspectRatio_NoFitRatio) {
            mCutView.setWidthHeightRatio(-1);
        } else {
            mCutView.setWidthHeightRatio(size.x * 1.0F / size.y);
        }
        mRatio = ratio;
        if (mOnCutRectChangedListener != null) {
            mOnCutRectChangedListener.onSizeChanged(size);
        }
        return size;
    }

    private void initLiveWindowCenterPoint() {
        int[] location = new int[2];
        mLiveWindow.getLocationOnScreen(location);
        int locationX = location[0];
        int locationY = location[1];
        mCenterPoint.x = locationX + mLiveWindow.getWidth() * 1.0F / 2;
        mCenterPoint.y = locationY + mLiveWindow.getHeight() * 1.0F / 2;
    }

    private float getSuitLiveWindowScale(Point rectSize) {
        float scale = -1;
        int liveWindowWidth = mLiveWindow.getWidth();
        int liveWindowHeight = mLiveWindow.getHeight();
        float widthScale = rectSize.x * 1.0F / liveWindowWidth;
        float heightScale = rectSize.y * 1.0F / liveWindowHeight;
        scale = widthScale;
        if (scale < heightScale) {
            scale = heightScale;
        }
        if (scale < 1) {//只放大，不缩小
            return -1;
        }
        return scale;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setTimeLine(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public void playVideoButtonClick() {
        if (mTimeline == null) {
            return;
        }
        long endTime = getDuration();
        playVideoButtonClick(0, endTime);
    }


    public void playVideoButtonClick(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
    }

    public void playVideo(long startTime, long endTime) {
        // 播放视频
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void playVideoFromStartPosition() {
        // 播放视频
        mStreamingContext.playbackTimeline(mTimeline, mStartTime, mStartTime + getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    //预览
    public void seekTimeline(long timestamp, int seekShowMode) {
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    // 获取当前引擎状态
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    //停止引擎
    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();//停止播放
        }
    }


    public NvsTimeline getTimeLine() {
        return mTimeline;
    }

    public float[] getRegionData(float[] size) {
        if (mRegionData == null) {
            return null;
        }
        RectF rectF = new RectF();
        rectF.top = mRegionData.top * size[1];
        rectF.bottom = mRegionData.bottom * size[1];
        rectF.left = mRegionData.left * size[0];
        rectF.right = mRegionData.right * size[0];
        float[] regionData = null;
        if (mRatio == NvAsset.AspectRatio_NoFitRatio) {
            regionData = new float[10];
            regionData[0] = rectF.left;
            regionData[1] = rectF.top;
            regionData[2] = rectF.right;
            regionData[3] = rectF.top;
            regionData[4] = rectF.right;
            regionData[5] = rectF.bottom;
            regionData[6] = rectF.left;
            regionData[7] = rectF.bottom;
            regionData[8] = rectF.left;
            regionData[9] = 0;
        } else {
            regionData = new float[8];
            regionData[0] = rectF.left;
            regionData[1] = rectF.top;
            regionData[2] = rectF.right;
            regionData[3] = rectF.top;
            regionData[4] = rectF.right;
            regionData[5] = rectF.bottom;
            regionData[6] = rectF.left;
            regionData[7] = rectF.bottom;
        }
        return regionData;
    }

    private RectF getRect(int timelineWidth, int timelineHeight, int imageWidth, int imageHeight, float[] size) {
        if (size == null) {
            size = new float[2];
            size[0] = 1.0F;
            size[1] = 1.0F;
        }
        RectF rectF = new RectF();
        float imageRatio = imageWidth * 1.0F / imageHeight;
        float timelineRatio = timelineWidth * 1.0F / timelineHeight;
        if (imageRatio > timelineRatio) {//宽对齐
            float scale = timelineWidth * 1.0F / imageWidth;
            float timelineImageHeight = imageHeight * scale;
            rectF.top = timelineImageHeight / timelineHeight * size[1];
            rectF.bottom = -rectF.top;
            rectF.right = 1 * size[0];
            rectF.left = -rectF.right;
        } else {// 高对齐
            float scale = timelineHeight * 1.0F / imageHeight;
            float timelineImageWidth = imageWidth * scale;

            rectF.top = 1.0F * size[1];
            rectF.bottom = -rectF.top;
            rectF.right = timelineImageWidth / timelineWidth * size[0];
            rectF.left = -rectF.right;
        }
        return rectF;
    }

    private RectF getRectEx(int rectWidth, int rectHeight, int imageWidth, int imageHeight) {
        imageWidth = (int) (imageWidth * mMinLiveWindowScale);
        imageHeight = (int) (imageHeight * mMinLiveWindowScale);
        RectF rectF = new RectF();
        float imageRatio = imageWidth * 1.0F / imageHeight;
        float rectRatio = rectWidth * 1.0F / rectHeight;
        if (rectRatio > imageRatio) {//宽对齐
            rectF.right = rectWidth * 1.0F / imageWidth;
            rectF.left = -rectF.right;

            float scale = rectWidth * 1.0F / imageWidth;
            float timelineImageHeight = imageHeight * scale;

            rectF.top = rectHeight * 1.0F / timelineImageHeight;
            rectF.bottom = -rectF.top;

        } else {// 高对齐
            rectF.top = rectHeight * 1.0F / imageHeight;
            rectF.bottom = -rectF.top;

            float scale = rectHeight * 1.0F / imageHeight;
            float timelineImageWidth = imageWidth * scale;
            rectF.right = rectWidth * 1.0F / timelineImageWidth;
            rectF.left = -rectF.right;
        }
        return rectF;
    }

    public Map<String, Float> getTransFromData(int originalTimelineWidth, int originalTimelineHeight) {
        return parseToTimelineTransData(originalTimelineWidth, originalTimelineHeight);
    }

    public int[] getSize() {
        int[] size = new int[2];
        size[0] = (int) (mLiveWindow.getWidth() * mMinLiveWindowScale);
        size[1] = (int) (mLiveWindow.getHeight() * mMinLiveWindowScale);
        return size;
    }

    public int[] getRectViewSize() {
        return new int[]{mCutView.getRectWidth(), mCutView.getRectHeight()};
    }

    private Map<String, Float> parseToTimelineTransData(int originalTimelineWidth, int originalTimelineHeight) {
        Map<String, Float> result = new HashMap<>();
        float realScale = mTransformData.get(STORYBOARD_KEY_SCALE_X) / mMinLiveWindowScale;
        float realTransX = mTransformData.get(STORYBOARD_KEY_TRANS_X);
        float realTransY = mTransformData.get(STORYBOARD_KEY_TRANS_Y);
        result.put(STORYBOARD_KEY_SCALE_X, realScale);
        result.put(STORYBOARD_KEY_SCALE_Y, realScale);
        result.put(STORYBOARD_KEY_TRANS_X, realTransX);
        result.put(STORYBOARD_KEY_TRANS_Y, realTransY);
        result.put(STORYBOARD_KEY_ROTATION_Z, mTransformData.get(STORYBOARD_KEY_ROTATION_Z));
        return result;
    }

    private Map<String, Float> parseToViewTransData(Map<String, Float> originalData) {
        Map<String, Float> result = new HashMap<>();
        float viewScale = originalData.get(STORYBOARD_KEY_SCALE_X) * mMinLiveWindowScale;
        float viewTransX = originalData.get(STORYBOARD_KEY_TRANS_X);
        float viewTransY = originalData.get(STORYBOARD_KEY_TRANS_Y);
        result.put(STORYBOARD_KEY_SCALE_X, viewScale);
        result.put(STORYBOARD_KEY_SCALE_Y, viewScale);
        result.put(STORYBOARD_KEY_TRANS_X, viewTransX);
        result.put(STORYBOARD_KEY_TRANS_Y, viewTransY);
        result.put(STORYBOARD_KEY_ROTATION_Z, originalData.get(STORYBOARD_KEY_ROTATION_Z));
        return result;
    }


    //LiveWindowd点击回调
    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }

    //Fragment加载完成回调
    public interface OnFragmentLoadFinisedListener {
        void onLoadFinished();
    }

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinisedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public void setVideoFragmentCallBack(VideoFragmentListener videoFragmentCallBack) {
        this.mVideoFragmentCallBack = videoFragmentCallBack;
    }

    public interface OnPlayProgressChangeListener {
        void onPlayProgressChanged(long curTime);

        void onPlayStateChanged(boolean isPlaying);
    }

    public interface VideoFragmentListener {

        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }

    public interface OnCutRectChangedListener {
        void onScaleAndRotate(float scale, float degree, int type);

        void onSizeChanged(Point size);
    }

    class FloatPoint {
        public float x;
        public float y;

        @Override
        public String toString() {
            return "FloatPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}

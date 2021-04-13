package com.glitchcam.vepromei.edit.background;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.edit.VideoFragment;
import com.glitchcam.vepromei.edit.anim.AnimationClipAdapter;
import com.glitchcam.vepromei.edit.anim.view.AnimationBottomView;
import com.glitchcam.vepromei.edit.background.view.BackgroundBottomView;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.watermark.SingleClickActivity;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.ImageUtils;
import com.glitchcam.vepromei.utils.StoryboardUtil;
import com.glitchcam.vepromei.utils.TimelineUtil;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.StoryboardInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.glitchcam.vepromei.utils.MediaConstant.SINGLE_PICTURE_PATH;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_BACKGROUND_TYPE_COLOR;
import static com.glitchcam.vepromei.utils.dataInfo.StoryboardInfo.SUB_TYPE_BACKGROUND;


/**
 * @author lpf
 * @desc 背景页面
 * 分别包含 背景颜色 背景样式  背景模糊
 */
public class BackgroundActivity extends BaseActivity {
    private static final String TAG = "BackgroundActivity";

    public final static int TYPE_BACKGROUND_COLOR = 1;
    public final static int TYPE_BACKGROUND_STYLE = 2;
    public final static int TYPE_BACKGROUND_BLUR = 3;

    private static int REQUEST_CODE_BACKGROUND = 100;
    public final static String STORYBOARD_KEY_SCALE_X = "scaleX";
    public final static String STORYBOARD_KEY_SCALE_Y = "scaleY";
    public final static String STORYBOARD_KEY_ROTATION_Z = "rotationZ";
    public final static String STORYBOARD_KEY_TRANS_X = "transX";
    public final static String STORYBOARD_KEY_TRANS_Y = "transY";

    private int mBackgroundType;
    private CustomTitleBar mTitleBar;
    private NvsTimeline mTimeline;
    private VideoFragment mVideoFragment;
    private RecyclerView mBackgroundClipRecyclerView;
    private BackgroundBottomView mBackgroundBottomView;
    private AnimationClipAdapter mAnimationClipAdapter;
    private AnimationBottomView mAnimationBottomView;
    private NvsStreamingContext mStreamingContext;
    private LinearLayout mBackgroundColor, mBackgroundStyle, mBackgroundBlur;
    private NvsVideoTrack mVideoTrack;
    private NvsVideoClip mCurrentNvsVideoClip;
    private ClipInfo mCurrentClipInfo;

    private int mSelectedClipPosition = 0;

    private ImageView ivSelectColor, ivSelectStyle, ivSelectBlur;
    private TextView tvSelectColor, tvSelectStyle, tvSelectBlur;


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_background;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);

        mBackgroundClipRecyclerView = (RecyclerView) findViewById(R.id.clip_list);
        mBackgroundStyle = findViewById(R.id.ll_background_style);
        mBackgroundBlur = findViewById(R.id.ll_background_blur);
        mBackgroundColor = findViewById(R.id.ll_background_color);
        mAnimationBottomView = findViewById(R.id.animation_bottom);
        mBackgroundBottomView = findViewById(R.id.background_bottom_view);
        mBackgroundBottomView.setVisibility(View.GONE);

        ivSelectColor = findViewById(R.id.iv_select_color);
        ivSelectStyle = findViewById(R.id.iv_select_bg);
        ivSelectBlur = findViewById(R.id.iv_select_blur);

        tvSelectColor = findViewById(R.id.tv_select_color);
        tvSelectStyle = findViewById(R.id.tv_select_bg);
        tvSelectBlur = findViewById(R.id.tv_select_blur);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.background);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initTimeline();
        initVideoFragment();
        initAnimationClipList();
        initCurrentClip();
        initBackgroundData();
        //设置默认的背景
        setDefaultBackgroundIfNull();

        mBackgroundType = TYPE_BACKGROUND_COLOR;
        updateBgTitleScheme();

        mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_COLOR);
        mBackgroundBottomView.setSelectColor(mCurrentClipInfo);
    }


    private void initCurrentClip() {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return;
        }
        mCurrentNvsVideoClip = nvsVideoClip;

        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        if (clipInfoData != null && clipInfoData.size() > 0) {
            mCurrentClipInfo = clipInfoData.get(mSelectedClipPosition);
        }

    }


    private void initTimeline() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
    }

    private void initBackgroundData() {
        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        if (null == clipInfoData || null == mTimeline) {
            return;
        }

        int size = clipInfoData.size();
        for (int i = 0; i < size; i++) {
            ClipInfo clipInfo = clipInfoData.get(i);
            StoryboardInfo backgroundInfo = clipInfo.getBackgroundInfo();
            if (backgroundInfo != null) {
                updateBackground(clipInfo, i, mTimeline);
            }
        }
    }

    public void updateBackground(ClipInfo clipInfo, int position, NvsTimeline timeline) {
        if (clipInfo == null) {
            return;
        }
        NvsVideoTrack videoTrackByIndex = timeline.getVideoTrackByIndex(0);
        NvsVideoClip clipByIndex = videoTrackByIndex.getClipByIndex(position);

        NvsVideoResolution videoRes = timeline.getVideoRes();
        StoryboardInfo backgroundInfo = clipInfo.getBackgroundInfo();
        if (backgroundInfo == null) {
            return;
        }
        Map<String, Float> clipTrans = backgroundInfo.getClipTrans();
        String backgroundStory = null;
        int type = backgroundInfo.getBackgroundType();
        if (type == BackgroundActivity.TYPE_BACKGROUND_BLUR) {
            String filePath = clipInfo.getFilePath();
            backgroundStory = StoryboardUtil.getBlurBackgroundStory(videoRes.imageWidth, videoRes.imageHeight, filePath, backgroundInfo.getIntensity(), clipTrans);
        } else {
            backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), videoRes.imageWidth, videoRes.imageHeight, clipTrans);
        }
        backgroundInfo.setStringVal("Description String", backgroundStory);
        backgroundInfo.bindToTimelineByType(clipByIndex, backgroundInfo.getSubType());
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setIsBackgroundView(true);
        Bundle bundle = new Bundle();
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.video_layout, mVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    private void initAnimationClipList() {
        mAnimationClipAdapter = new AnimationClipAdapter(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBackgroundClipRecyclerView.setLayoutManager(layoutManager);
        final ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        mAnimationClipAdapter.setClipInfoList(clipInfoData);
        mAnimationClipAdapter.setTimeLine(mTimeline);
        mBackgroundClipRecyclerView.setAdapter(mAnimationClipAdapter);

        mAnimationClipAdapter.setOnItemClickListener(new AnimationClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 单端播放
                if (position >= 0 && position < clipInfoData.size()) {
                    playCurrentClip(position);

                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                    if (videoTrack != null) {
                        mCurrentNvsVideoClip = videoTrack.getClipByIndex(position);
                    }

                    mCurrentClipInfo = clipInfoData.get(position);
                    if (mBackgroundBottomView != null && mBackgroundBottomView.getVisibility() == View.VISIBLE) {
                        mBackgroundBottomView.setSelectColor(mCurrentClipInfo);
                    }

                    if (mCurrentClipInfo == null) {
                        return;
                    }

                    mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
                    mVideoFragment.setTransformViewVisible(View.VISIBLE);
                }
            }
        });
    }

    private void updateBgTitleScheme(){
        ivSelectColor.setColorFilter(Color.argb(255, 255, 255, 255)); // white
        ivSelectStyle.setColorFilter(Color.argb(255, 255, 255, 255)); // white
        ivSelectBlur.setColorFilter(Color.argb(255, 255, 255, 255)); // white

        tvSelectColor.setTextColor(Color.argb(255, 255, 255, 255));
        tvSelectStyle.setTextColor(Color.argb(255, 255, 255, 255));
        tvSelectBlur.setTextColor(Color.argb(255, 255, 255, 255));

        switch (mBackgroundType){
            case TYPE_BACKGROUND_COLOR:
                ivSelectColor.setColorFilter(getColor(R.color.bezier_line)); // blue
                tvSelectColor.setTextColor(getColor(R.color.bezier_line)); // blue
                break;
            case TYPE_BACKGROUND_STYLE:
                ivSelectStyle.setColorFilter(getColor(R.color.bezier_line)); // blue
                tvSelectStyle.setTextColor(getColor(R.color.bezier_line)); // blue
                break;
            case TYPE_BACKGROUND_BLUR:
                ivSelectBlur.setColorFilter(getColor(R.color.bezier_line)); // blue
                tvSelectBlur.setTextColor(getColor(R.color.bezier_line)); // blue
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSelectedClipPosition = 0;
    }

    @Override
    protected void initListener() {
        mBackgroundStyle.setOnClickListener(this);
        mBackgroundBlur.setOnClickListener(this);
        mBackgroundColor.setOnClickListener(this);

        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //播放片段，通过片段时间控制
                        if (null != mVideoFragment) {
                            playCurrentClip(mSelectedClipPosition);
                        }
                        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                    }
                };
                Handler handler = new Handler();
                handler.postDelayed(r, 100);
            }
        });

        mBackgroundBottomView.setOnBackgroundBottomItemClickListener(new BackgroundBottomView.OnBackgroundBottomItemClickListener() {
            @Override
            public void onColorItemClick(View view, MultiColorInfo colorInfo) {
                //颜色背景
                if (colorInfo == null) {
                    return;
                }
                if (mCurrentClipInfo == null) {
                    return;
                }

                String colorValue = colorInfo.getColorValue();
                View colorView = view.findViewById(R.id.iv_color);
                String colorPath = ImageUtils.parseViewToBitmap(mContext, colorView, colorValue);//将view转成png文件
                mCurrentClipInfo.setBackgroundValue(colorValue);
                if (!TextUtils.isEmpty(colorPath)) {
                    File file = new File(colorPath);
                    if (!file.exists()) {
                        return;
                    }

                    String fileName = file.getName();
                    String fileDir = file.getParentFile().getAbsolutePath();

                    addBackgroundStory(mCurrentClipInfo, mCurrentNvsVideoClip, fileName, fileDir, TYPE_BACKGROUND_COLOR, 1);
                }
            }

            @Override
            public void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo) {
                if (position == 0) {
                    //素材选择
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_PICTURE_FROM_BACKGROUND);
                    AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), SingleClickActivity.class, bundle, REQUEST_CODE_BACKGROUND);
                    return;
                }

                if (position == 1) {
                    NvsVideoResolution videoRes = mTimeline.getVideoRes();
                    if (videoRes == null) {
                        return;
                    }
                    setDefaultBackground(mCurrentClipInfo, mCurrentNvsVideoClip);
                    return;
                }
                //样式背景
                if (backgroundStyleInfo == null) {
                    return;
                }

                String filePath = backgroundStyleInfo.getFilePath();
                String fileName = filePath;
                String fileDir = "assets:/background/image";

                mCurrentClipInfo.setBackgroundValue(filePath);
                if (!TextUtils.isEmpty(fileName)) {
                    addBackgroundStory(mCurrentClipInfo, mCurrentNvsVideoClip, fileName, fileDir, TYPE_BACKGROUND_STYLE, 1);
                }
            }

            @Override
            public void onBlurItemClick(View view, float strength) {
                if (strength == 0) {
                    mCurrentClipInfo.setBackgroundValue(String.valueOf(strength));
                    deleteBackground();
                    return;
                }

                mCurrentClipInfo.setBackgroundValue(String.valueOf(strength));

                String fileDir = "assets:/background";
                addBackgroundStory(mCurrentClipInfo, mCurrentNvsVideoClip, null, fileDir, TYPE_BACKGROUND_BLUR, strength);
            }

            @Override
            public void onConfirmClick() {
                mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
                mVideoFragment.setTransformViewVisible(View.GONE);

                removeTimeline();
                quitActivity();
            }

            @Override
            public void onStyleApplyAll(String filePath) {
                if (TextUtils.isEmpty(filePath)) {
                    return;
                }
                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        String fileName = filePath;
                        String fileDir = "assets:/background/image";
                        clipInfo.setBackgroundValue(filePath);
                        if (!TextUtils.isEmpty(fileName)) {
                            addBackgroundStory(clipInfo, videoTrack.getClipByIndex(i), fileName, fileDir, TYPE_BACKGROUND_STYLE, 1);
                        }
                    }
                }
            }

            @Override
            public void onColorApplyAll(String colorValue) {
                if (TextUtils.isEmpty(colorValue)) {
                    return;
                }

                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        StoryboardInfo backgroundInfo = mCurrentClipInfo.getBackgroundInfo();
                        if (backgroundInfo == null) {
                            break;
                        }
                        String fileName = backgroundInfo.getSource();
                        String fileDir = backgroundInfo.getSourceDir();
                        clipInfo.setBackgroundValue(colorValue);
                        if (!TextUtils.isEmpty(fileName)) {
                            addBackgroundStory(clipInfo, videoTrack.getClipByIndex(i), fileName, fileDir, TYPE_BACKGROUND_COLOR, 1);
                        }
                    }
                }
            }

            @Override
            public void onBlurApplyAll() {

                if (mTimeline == null) {
                    return;
                }

                NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                if (videoTrack == null) {
                    return;
                }

                ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
                if (clipInfoData != null && clipInfoData.size() > 0) {
                    for (int i = 0; i < clipInfoData.size(); i++) {
                        ClipInfo clipInfo = clipInfoData.get(i);
                        if (clipInfo == mCurrentClipInfo) {
                            continue;
                        }
                        StoryboardInfo backgroundInfo = mCurrentClipInfo.getBackgroundInfo();
                        if (backgroundInfo == null) {
                            break;
                        }
                        float strength = backgroundInfo.getIntensity();
                        clipInfo.setBackgroundValue(String.valueOf(strength));
                        String fileDir = "assets:/background";
                        addBackgroundStory(clipInfo, videoTrack.getClipByIndex(i), null, fileDir, TYPE_BACKGROUND_BLUR, strength);
                    }
                }
            }
        });


        mVideoFragment.setOnBackgroundChangedListener(new VideoFragment.OnBackgroundChangedListener() {
            @Override
            public void onBackgroundChanged() {
                updateBackground();
            }
        });
    }


    private void quitActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }


    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }


    @Override
    public void onClick(View view) {
        mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
        mVideoFragment.setTransformViewVisible(View.VISIBLE);

        switch (view.getId()) {
            case R.id.ll_background_color:
                mBackgroundType = TYPE_BACKGROUND_COLOR;
                updateBgTitleScheme();
                mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_COLOR);
                mBackgroundBottomView.setSelectColor(mCurrentClipInfo);
                break;
            case R.id.ll_background_blur:
                mBackgroundType = TYPE_BACKGROUND_BLUR;
                updateBgTitleScheme();
                mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_BLUR);
                mBackgroundBottomView.setSelectBlur(mCurrentClipInfo);
                break;
            case R.id.ll_background_style:
                mBackgroundType = TYPE_BACKGROUND_STYLE;
                updateBgTitleScheme();
                mBackgroundBottomView.showView(BackgroundBottomView.TYPE_BACKGROUND_STYLE);
                mBackgroundBottomView.setSelectStyle(mCurrentClipInfo);
                break;
            default:
                break;
        }
    }


    public void deleteBackground() {
        if (mCurrentClipInfo == null) {
            return;
        }
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        StoryboardInfo oldBackground = mCurrentClipInfo.getBackgroundInfo();
        if (oldBackground == null) {
            oldBackground = new StoryboardInfo();
            Map<String, Float> clipTrans = new HashMap<>();
            clipTrans.put(STORYBOARD_KEY_SCALE_X, 1f);
            clipTrans.put(STORYBOARD_KEY_SCALE_Y, 1f);
            clipTrans.put(STORYBOARD_KEY_ROTATION_Z, 0f);
            clipTrans.put(STORYBOARD_KEY_TRANS_X, 0f);
            clipTrans.put(STORYBOARD_KEY_TRANS_Y, 0f);
            oldBackground.setClipTrans(clipTrans);
        }
        mCurrentClipInfo.removeBackground(mCurrentNvsVideoClip);
        setDefaultBackground(videoRes.imageWidth, videoRes.imageHeight, oldBackground.getClipTrans());
        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }

    private void setDefaultBackgroundIfNull() {
        if (mCurrentClipInfo == null) {
            return;
        }

        StoryboardInfo oldBackground = mCurrentClipInfo.getBackgroundInfo();
        if (oldBackground != null) {
            return;
        }
        Map<String, Float> clipTrans = new HashMap<>();
        clipTrans.put(STORYBOARD_KEY_SCALE_X, 1f);
        clipTrans.put(STORYBOARD_KEY_SCALE_Y, 1f);
        clipTrans.put(STORYBOARD_KEY_ROTATION_Z, 0f);
        clipTrans.put(STORYBOARD_KEY_TRANS_X, 0f);
        clipTrans.put(STORYBOARD_KEY_TRANS_Y, 0f);
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        setDefaultBackground(videoRes.imageWidth, videoRes.imageHeight, clipTrans);
    }

    private void setDefaultBackground(int timelineWidth, int timelineHeight, Map<String, Float> clipTrans) {
        StoryboardInfo backgroundInfo = new StoryboardInfo();
        backgroundInfo.setSource("nobackground.png");
        backgroundInfo.setSourceDir("assets:/background");
        backgroundInfo.setClipTrans(clipTrans);
        String backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), timelineWidth, timelineHeight, clipTrans);
        backgroundInfo.setStringVal("Description String", backgroundStory);
        backgroundInfo.setStringVal("Resource Dir", backgroundInfo.getSourceDir());
        backgroundInfo.setBooleanVal("No Background", true);
        backgroundInfo.setBackgroundType(STORYBOARD_BACKGROUND_TYPE_COLOR);
        backgroundInfo.setSubType(SUB_TYPE_BACKGROUND);
        backgroundInfo.bindToTimelineByType(mCurrentNvsVideoClip, SUB_TYPE_BACKGROUND);
        mCurrentClipInfo.addStoryboardInfo(SUB_TYPE_BACKGROUND, backgroundInfo);
    }


    /**
     * 播放视频
     * Play video
     */
    private void playCurrentClip(int mSelectedClipPosition) {
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if (mSelectedClipPosition >= 0 && mSelectedClipPosition < clipCount) {
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            //播放时针对timeline 的
            if (playEndPoint > playStartPoint) {
                //除了第一条片段，其他片段延0.5s播放时间，从开始向前
                //设置进度条显示 0 - duration
                if (mSelectedClipPosition > 0) {
                    playStartPoint -= 0.5 * 1000 * 1000;
                    playEndPoint -= 0.5 * 1000 * 1000;
                }
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint, playEndPoint, playStartPoint, mTimeline.getDuration());
                mVideoFragment.playVideoButtonCilck(playStartPoint, playEndPoint);
            }
        }
        //播放全部视频
        else if (mSelectedClipPosition == -1) {
            playStartPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            playEndPoint = mTimeline.getDuration();
            //播放全部视频时 duration = end
            //设置进度条显示
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0, playEndPoint, playStartPoint, playEndPoint);
            if (playEndPoint > playStartPoint) {
                mVideoFragment.playVideoButtonCilck(playStartPoint, playEndPoint);
            }
        }
    }


    /**
     * 获取当前选择的片段的起始位置
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipStartTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipInPoint = nvsVideoClip.getInPoint();
        return clipInPoint;
    }

    /**
     * 获取当前选择的片段的起始位置
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipEndTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return clipOutPoint;
    }


    public void updateBackground() {
        if (mCurrentClipInfo == null) {
            return;
        }
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        StoryboardInfo backgroundInfo = mCurrentClipInfo.getBackgroundInfo();
        if (backgroundInfo == null) {
            return;
        }
        Map<String, Float> clipTrans = backgroundInfo.getClipTrans();
        String backgroundStory = null;
        int type = backgroundInfo.getBackgroundType();
        if (type == TYPE_BACKGROUND_BLUR) {
            String filePath = mCurrentClipInfo.getFilePath();
            backgroundStory = StoryboardUtil.getBlurBackgroundStory(videoRes.imageWidth, videoRes.imageHeight, filePath, backgroundInfo.getIntensity(), clipTrans);
        } else {
            backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), videoRes.imageWidth, videoRes.imageHeight, clipTrans);
        }
        backgroundInfo.setStringVal("Description String", backgroundStory);
        backgroundInfo.bindToTimelineByType(mCurrentNvsVideoClip, backgroundInfo.getSubType());

        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }


    public void setDefaultBackground(ClipInfo videoClip, NvsVideoClip nvsVideoClip) {

        if (videoClip == null) {
            return;
        }
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        StoryboardInfo backgroundInfo = videoClip.getBackgroundInfo();
        if (backgroundInfo == null) {
            return;
        }
        /**
         * 设置选择项是通过这个值，设置默认的时候清理掉
         */
        videoClip.setBackgroundValue(null);
        Map<String, Float> clipTrans = backgroundInfo.getClipTrans();
        String backgroundStory = null;

        backgroundInfo.setSource("nobackground.png");
        backgroundInfo.setSourceDir("assets:/background");
        backgroundInfo.setStringVal("Resource Dir", backgroundInfo.getSourceDir());
        backgroundInfo.setBooleanVal("No Background", true);

        backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), videoRes.imageWidth, videoRes.imageHeight, clipTrans);
        backgroundInfo.setStringVal("Description String", backgroundStory);
        backgroundInfo.setBackgroundType(STORYBOARD_BACKGROUND_TYPE_COLOR);
        backgroundInfo.bindToTimelineByType(nvsVideoClip, backgroundInfo.getSubType());
        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }


    private void addBackgroundStory(ClipInfo clipInfo, NvsVideoClip nvsVideoClip, String source, String sourceDir, int type, float strength) {
        NvsVideoResolution videoRes = mTimeline.getVideoRes();

        if (clipInfo == null) {
            return;
        }
        StoryboardInfo backgroundInfo = clipInfo.getBackgroundInfo();
        if (backgroundInfo == null) {
            backgroundInfo = new StoryboardInfo();
            clipInfo.addStoryboardInfo(SUB_TYPE_BACKGROUND, backgroundInfo);
        }
        Map<String, Float> clipTrans = backgroundInfo.getClipTrans();
        if (clipTrans == null || clipTrans.size() == 0) {
            clipTrans = new HashMap<>();
            clipTrans.put(STORYBOARD_KEY_SCALE_X, 1.0F);
            clipTrans.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
            clipTrans.put(STORYBOARD_KEY_ROTATION_Z, 0F);
            clipTrans.put(STORYBOARD_KEY_TRANS_X, 0F);
            clipTrans.put(STORYBOARD_KEY_TRANS_Y, 0F);
            backgroundInfo.setClipTrans(clipTrans);
        }
        String backgroundStory = null;
        if (type == TYPE_BACKGROUND_BLUR) {
            String filePath = clipInfo.getFilePath();   //  /storage/emulated/0/Pictures/WeiXin/wx_camera_1602833532732.mp4
            backgroundStory = StoryboardUtil.getBlurBackgroundStory(videoRes.imageWidth, videoRes.imageHeight, filePath, strength, clipTrans);
        } else {
            backgroundStory = StoryboardUtil.getImageBackgroundStory(source, videoRes.imageWidth, videoRes.imageHeight, clipTrans);
        }

        if (!TextUtils.isEmpty(sourceDir)) {
            backgroundInfo.setStringVal("Resource Dir", sourceDir);
        }
        backgroundInfo.setBooleanVal("No Background", true);
        backgroundInfo.setStringVal("Description String", backgroundStory);

        backgroundInfo.setStoryDesc(backgroundStory);
        backgroundInfo.setSubType(SUB_TYPE_BACKGROUND);  //background
        backgroundInfo.setBackgroundType(type);   //0
        backgroundInfo.setIntensity(strength); //1.0
        backgroundInfo.setSource(source);   //#ffeb5f5a.png
        backgroundInfo.setSourceDir(sourceDir); // /storage/emulated/0/Android/data/com.meishe.myvideoapp/files/imageBackground


        backgroundInfo.bindToTimelineByType(nvsVideoClip, backgroundInfo.getSubType());
        if (clipInfo != null) {
            clipInfo.addStoryboardInfo(backgroundInfo.getSubType(), backgroundInfo);
        }

        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BACKGROUND) {
            if (data != null) {
                String filePath = data.getStringExtra(SINGLE_PICTURE_PATH);
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        return;
                    }
                    String fileDir = file.getParentFile().getAbsolutePath();
                    mCurrentClipInfo.setBackgroundValue(filePath);
                    if (!TextUtils.isEmpty(filePath)) {
                        addBackgroundStory(mCurrentClipInfo, mCurrentNvsVideoClip, filePath, fileDir, TYPE_BACKGROUND_STYLE, 1);
                    }
                }
            }
        }
    }
}
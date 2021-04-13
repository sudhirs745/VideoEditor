package com.glitchcam.vepromei.edit.clipEdit.adjust;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsPanAndScan;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.bean.AdjustRation;
import com.glitchcam.vepromei.bean.CutData;
import com.glitchcam.vepromei.edit.clipEdit.CutVideoFragment;
import com.glitchcam.vepromei.edit.data.BackupData;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.StoryboardUtil;
import com.glitchcam.vepromei.utils.TimelineUtil;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.StoryboardInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.glitchcam.vepromei.view.MagicProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_X;
import static com.glitchcam.vepromei.utils.StoryboardUtil.STORYBOARD_KEY_TRANS_Y;

public class AdjustActivity extends BaseActivity {

    private static final int MAXPOINT = 90;
    public static final int[] RATIOARRAY = {
            NvAsset.AspectRatio_NoFitRatio,
            NvAsset.AspectRatio_9v16,
            NvAsset.AspectRatio_3v4,
            NvAsset.AspectRatio_9v18,
            NvAsset.AspectRatio_9v21,
            NvAsset.AspectRatio_1v1,
            NvAsset.AspectRatio_16v9,
            NvAsset.AspectRatio_4v3,
            NvAsset.AspectRatio_18v9,
            NvAsset.AspectRatio_21v9,
    };

    public static final String[] RATIOSTRINGARRAY = {
            "9:16",
            "3:4",
            "9:18",
            "9:21",
            "1:1",
            "16:9",
            "4:3",
            "18:9",
            "21:9",
    };
    private CustomTitleBar mTitleBar;
    private LinearLayout mBottomLayout;
    private RelativeLayout mHorizLayout;
    private RelativeLayout mVerticLayout;
    private RelativeLayout mRotateLayout;
    private RelativeLayout mResetLayout;
    private ImageView mAdjustFinish;
    private MagicProgress mMpAdjust;
    private CutVideoFragment mClipFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoFx mVideoFx;
    private NvsVideoClip mVideoClip;
    private ArrayList<ClipInfo> mClipArrayList;
    private CutData mCutData;
    private int mOriginalTimelineWidth;
    private int mOriginalTimelineHeight;

    private List<AdjustRation> mAdjustRations = new ArrayList<>();
    private RecyclerView mRvAdjustRation;
    private AdjustRatioAdapter ratioAdapter;

    private int mCurClipIndex = 0;
    private int mScaleX = 1;
    private int mScaleY = 1;
    private int mRotateAngle = 0;


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_adjust;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mBottomLayout = findViewById(R.id.bottomLayout);
        mHorizLayout = findViewById(R.id.horizLayout);
        mVerticLayout = findViewById(R.id.verticLayout);
        mRotateLayout = findViewById(R.id.rotateLayout);
        mResetLayout = findViewById(R.id.resetLayout);
        mAdjustFinish = findViewById(R.id.adjustFinish);
        mRvAdjustRation = findViewById(R.id.rv_adjust_ratio);
        mMpAdjust = findViewById(R.id.mp_adjust);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.adjust);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();

        mOriginalTimelineWidth = TimelineData.instance().getVideoResolution().imageWidth;
        mOriginalTimelineHeight = TimelineData.instance().getVideoResolution().imageHeight;

        initAdjustRation();

        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size()) {
            return;
        }
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        int scaleX = clipInfo.getScaleX();
        int scaleY = clipInfo.getScaleY();
        if (scaleX >= -1) {
            mScaleX = scaleX;
        }
        if (scaleY >= -1) {
            mScaleY = scaleY;
        }
        mRotateAngle = clipInfo.getRotateAngle();
        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        NvsVideoResolution resolution = TimelineUtil.getVideoEditResolutionByClip(clipInfo.getFilePath(), 1080);
        mTimeline.changeVideoSize(resolution.imageWidth, resolution.imageHeight);

        if (mTimeline == null) {
            return;
        }

        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        mVideoClip = videoTrack.getClipByIndex(0);
        if (mVideoClip == null) {
            return;
        }
        mCutData = getCutData();
        initVideoFragment();
        if (mCutData != null) {
            int currentIndex = 0;
            mMpAdjust.setProgress((int) mCutData.getTransformData(StoryboardUtil.STORYBOARD_KEY_ROTATION_Z) + (MAXPOINT / 2));
            for (int index = 0; index < mAdjustRations.size(); index++) {
                AdjustRation adjustRation = mAdjustRations.get(index);
                if (adjustRation == null) {
                    continue;
                }
                adjustRation.setSelectd(mCutData.getRatio() == adjustRation.getId());
                if (mCutData.getRatio() == adjustRation.getId()) {
                    currentIndex = index;
                }
            }
            ratioAdapter.notifyDataSetChanged();
            mRvAdjustRation.scrollToPosition(currentIndex);
        }
        adjustClip();
    }

    @SuppressLint("Recycle")
    private void initAdjustRation() {
        mMpAdjust.setPointEnable(false);
        mMpAdjust.setMax(MAXPOINT);
        mMpAdjust.setBreakProgress(MAXPOINT / 2);
        mMpAdjust.setProgress(MAXPOINT / 2);
        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.adjudt_ration);
        TypedArray typedArraySelected = mContext.getResources().obtainTypedArray(R.array.adjudt_ration_selected);
        for (int i = 0; i < RATIOARRAY.length; i++) {
            AdjustRation adjustRation = new AdjustRation();
            adjustRation.setId(RATIOARRAY[i]);
            adjustRation.setName((i == 0) ? getResources().getString(R.string.free) : RATIOSTRINGARRAY[i - 1]);
            adjustRation.setSelectedIcon(typedArraySelected.getResourceId(i, -1));
            adjustRation.setUnSelectedIcon(typedArray.getResourceId(i, -1));
            adjustRation.setSelectd(i == 0);
            mAdjustRations.add(adjustRation);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ratioAdapter = new AdjustRatioAdapter(this, mAdjustRations);
        mRvAdjustRation.setLayoutManager(layoutManager);
        mRvAdjustRation.setAdapter(ratioAdapter);
    }

    private void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mClipFragment = CutVideoFragment.newInstance(0L);
        mClipFragment.setCutData(mCutData);
        mClipFragment.setTimeLine(mTimeline);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mClipFragment.initData();
                seekTimeline(0);
            }
        });
        fragmentManager.beginTransaction().add(R.id.spaceLayout, mClipFragment).commit();
        fragmentManager.beginTransaction().show(mClipFragment);
        mClipFragment.setOnCutRectChangelisener(new CutVideoFragment.OnCutRectChangedListener() {
            @Override
            public void onScaleAndRotate(float scale, float degree, int type) {
                if (type == CutVideoFragment.ONSCALE) {
                    return;
                }
                mMpAdjust.setProgress((int) (degree + (MAXPOINT / 2)));
            }

            @Override
            public void onSizeChanged(Point size) {
                if ((mCutData != null) && (!mCutData.isOldData())) {
                    ClipInfo meicamClipInfo = mClipArrayList.get(mCurClipIndex);
                    Map<String, Float> transFromData = parseTransToView(mOriginalTimelineWidth, mOriginalTimelineHeight, meicamClipInfo.getFilePath(),
                            new int[]{size.x, size.y}, mCutData.getTransformData());
                    mCutData.setTransformData(transFromData);
                    mClipFragment.setCutData(mCutData);
                }
            }
        });
    }

    @Override
    protected void initListener() {
        mHorizLayout.setOnClickListener(this);
        mVerticLayout.setOnClickListener(this);
        mMpAdjust.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (!fromUser && (mClipFragment != null)) {
                    mClipFragment.rotateVideo(progress - (MAXPOINT / 2f));
                }
            }
        });
        ratioAdapter.setRationChangeListener(new AdjustRatioAdapter.OnAdjustRationChangeListener() {
            @Override
            public void onAdjustRationChange(int position) {
                if (mAdjustRations.isEmpty() || position < 0) {
                    return;
                }
                for (int i = 0; i < mAdjustRations.size(); i++) {
                    if (i != position) {
                        mAdjustRations.get(i).setSelectd(false);
                    }
                }
                ratioAdapter.notifyDataSetChanged();

                mClipFragment.changeCutRectView(mAdjustRations.get(position).getId());
            }
        });
        mRotateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rotateAngle = mVideoClip.getExtraVideoRotation();
                switch (rotateAngle) {
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_90;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_90:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_180;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_180:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_270;
                        break;
                    case NvsVideoClip.ClIP_EXTRAVIDEOROTATION_270:
                        mRotateAngle = NvsVideoClip.ClIP_EXTRAVIDEOROTATION_0;
                        break;
                    default:
                        break;
                }
                rotateClip();
                mClipArrayList.get(mCurClipIndex).setRotateAngle(mRotateAngle);
                if (mClipFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }
        });
        mResetLayout.setOnClickListener(this);
        mAdjustFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.horizLayout:
                mScaleX = mScaleX > 0 ? -1 : 1;
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
                mClipArrayList.get(mCurClipIndex).setScaleX(mScaleX);
                break;
            case R.id.verticLayout:
                mScaleY = mScaleY > 0 ? -1 : 1;
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
                mClipArrayList.get(mCurClipIndex).setScaleY(mScaleY);
                break;
            case R.id.resetLayout:
                mScaleX = 1;
                mScaleY = 1;
                mRotateAngle = 0;
                rotateClip();

                mClipFragment.reset();
                mMpAdjust.setProgress(MAXPOINT / 2);
                ratioAdapter.setSelection(0);
                mRvAdjustRation.scrollToPosition(0);

                mVideoClip.setPanAndScan(0.0f, 0.0f);
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
                mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
                mClipArrayList.get(mCurClipIndex).setScaleX(mScaleX);
                mClipArrayList.get(mCurClipIndex).setScaleY(mScaleY);
                mClipArrayList.get(mCurClipIndex).setRotateAngle(mRotateAngle);
                break;
            case R.id.adjustFinish:
                NvsPanAndScan panAndScan = mVideoClip.getPanAndScan();
                mClipArrayList.get(mCurClipIndex).setPan(panAndScan.pan);
                mClipArrayList.get(mCurClipIndex).setScan(panAndScan.scan);

                saveCutInfoData();

                BackupData.instance().setClipInfoData(mClipArrayList);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
                break;
            default:
                break;
        }
        if (mClipFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        }
    }

    private void saveCutInfoData() {
        ClipInfo meicamClipInfo = mClipArrayList.get(mCurClipIndex);
        if (meicamClipInfo == null) {
            return;
        }

        int[] liveWindowSize = mClipFragment.getSize();
        Map<String, Float> transFromData = mClipFragment.getTransFromData(mOriginalTimelineWidth, mOriginalTimelineHeight);
        transFromData = parseTransToTimeline(mOriginalTimelineWidth, mOriginalTimelineHeight, meicamClipInfo.getFilePath(),
                mClipFragment.getRectViewSize(), transFromData);
        String transform2DStory = StoryboardUtil.getTransform2DStory(mOriginalTimelineWidth, mOriginalTimelineHeight,
                transFromData);
        StoryboardInfo transformStoryInfo = new StoryboardInfo();
        transformStoryInfo.setSubType(StoryboardInfo.SUB_TYPE_CROPPER_TRANSFROM);
        transformStoryInfo.setStoryDesc(transform2DStory);
        transformStoryInfo.setBooleanVal("No Background", true);
        transformStoryInfo.setStringVal("Description String", transform2DStory);
        transformStoryInfo.bindToTimelineByType(mVideoClip, transformStoryInfo.getSubType());
        meicamClipInfo.addStoryboardInfo(transformStoryInfo.getSubType(), transformStoryInfo);

        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        float[] size = getRelativeSize(videoRes.imageWidth, videoRes.imageHeight, mOriginalTimelineWidth, mOriginalTimelineHeight);
        StoryboardInfo cropperStoryInfo = new StoryboardInfo();
        cropperStoryInfo.setSubType(StoryboardInfo.SUB_TYPE_CROPPER);
        String cropperStory = StoryboardUtil.getCropperStory(liveWindowSize[0], liveWindowSize[1],
                mClipFragment.getRegionData(size));
        cropperStoryInfo.setStoryDesc(cropperStory);
        cropperStoryInfo.setBooleanVal("No Background", true);
        cropperStoryInfo.setStringVal("Description String", cropperStory);
        cropperStoryInfo.bindToTimelineByType(mVideoClip, cropperStoryInfo.getSubType());
        meicamClipInfo.addStoryboardInfo(cropperStoryInfo.getSubType(), cropperStoryInfo);
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void rotateClip() {
        mVideoClip.setExtraVideoRotation(mRotateAngle);
    }

    private void adjustClip() {
        rotateClip();
        int fxCount = mVideoClip.getFxCount();
        for (int index = 0; index < fxCount; ++index) {
            NvsVideoFx videoFx = mVideoClip.getFxByIndex(index);
            if (videoFx == null) {
                continue;
            }
            if (videoFx.getBuiltinVideoFxName().compareTo(Constants.FX_TRANSFORM_2D) == 0) {
                mVideoFx = videoFx;
                break;
            }
        }
        if (mVideoFx == null) {
            mVideoFx = mVideoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
        }
        if (mVideoFx == null) {
            return;
        }
        if (mScaleX >= -1) {
            mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, mScaleX);
        }
        if (mScaleY >= -1) {
            mVideoFx.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, mScaleY);
        }
    }

    private void seekTimeline(long timeStamp) {
        mClipFragment.seekTimeline(timeStamp, 0);
    }

    private CutData getCutData() {
        ClipInfo meicamClipInfo = mClipArrayList.get(mCurClipIndex);
        if (meicamClipInfo == null) {
            return null;
        }
        String transformDescription = null;
        String croperDescription = null;
        StoryboardInfo videoFx = meicamClipInfo.getStoryboardInfos().get(StoryboardInfo.SUB_TYPE_CROPPER_TRANSFROM);
        if (videoFx != null) {
            transformDescription = videoFx.getStringVal("Description String");
        }
        StoryboardInfo cropperFx = meicamClipInfo.getStoryboardInfos().get(StoryboardInfo.SUB_TYPE_CROPPER);
        if (cropperFx != null) {
            croperDescription = cropperFx.getStringVal("Description String");
        }
        NvsVideoResolution videoRes = mTimeline.getVideoRes();
        float[] size = getRelativeSize(videoRes.imageWidth, videoRes.imageHeight, mOriginalTimelineWidth, mOriginalTimelineHeight);
        return StoryboardUtil.parseStoryToCatData(croperDescription, transformDescription, size);

    }

    private float[] getRelativeSize(int imageWidth, int imageHeight, int timelineWidth, int timelineHeight) {
        float[] size = new float[2];
        float timelineRatio = timelineWidth * 1.0F / timelineHeight;
        float imageRatio = imageWidth * 1.0F / imageHeight;
        if (imageRatio > timelineRatio) {//宽对齐
            size[0] = 1.0F;
            float ratio = timelineWidth * 1.0F / imageWidth;
            size[1] = (imageHeight * ratio) / timelineHeight;
        } else {
            size[1] = 1.0F;
            float ratio = timelineHeight * 1.0F / imageHeight;
            size[0] = imageWidth * ratio / timelineWidth;
        }
        return size;
    }

    /**
     * 转换transform 数据为timeline范围内的transfrom
     *
     * @param timelineWidth  时间线的宽
     * @param timelineHeight 时间线的高
     * @param filePath       文件路径
     * @param rectSize       裁剪区域的宽高
     * @param transFormData  转换数据
     * @return
     */
    public Map<String, Float> parseTransToTimeline(int timelineWidth, int timelineHeight, String filePath, int[] rectSize, Map<String, Float> transFormData) {
        float transXInView = transFormData.get(STORYBOARD_KEY_TRANS_X);
        float transYInView = transFormData.get(STORYBOARD_KEY_TRANS_Y);
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(filePath);
        int videoStreamRotation = avFileInfo.getVideoStreamRotation(0);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int heigth = 0;
        int width = 0;
        if (videoStreamRotation % 2 == 0) {
            heigth = dimension.height;
            width = dimension.width;
        } else {
            width = dimension.height;
            heigth = dimension.width;
        }

        float fileRatio = width * 1F / heigth;
        float timelineRatio = timelineWidth * 1F / timelineHeight;

        float fileWidthInTimeline = 0;
        float fileHeightInTimeline = 0;
        if (fileRatio > timelineRatio) {//文件宽对齐
            fileWidthInTimeline = timelineWidth;
            fileHeightInTimeline = fileWidthInTimeline / fileRatio;
        } else {//高对齐
            fileHeightInTimeline = timelineHeight;
            fileWidthInTimeline = fileHeightInTimeline * fileRatio;
        }
        float rectWidthInTimeline = 0;
        float rectHeightInTimeline = 0;
        float rectRatio = rectSize[0] * 1F / rectSize[1];
        if (rectRatio > fileRatio) {//裁剪区域宽对齐
            rectWidthInTimeline = fileWidthInTimeline;
            rectHeightInTimeline = rectWidthInTimeline / rectRatio;
        } else {
            rectHeightInTimeline = fileHeightInTimeline;
            rectWidthInTimeline = rectHeightInTimeline * rectRatio;
        }

        float transXInTimeline = transXInView / rectSize[0] * rectWidthInTimeline;
        float transYInTimeline = transYInView / rectSize[1] * rectHeightInTimeline;
        transFormData.put(STORYBOARD_KEY_TRANS_X, transXInTimeline);
        transFormData.put(STORYBOARD_KEY_TRANS_Y, transYInTimeline);
        return transFormData;
    }

    /**
     * 转换transform 数据为timeline范围内的transfrom
     *
     * @param timelineWidth  时间线的宽
     * @param timelineHeight 时间线的高
     * @param filePath       文件路径
     * @param rectSize       裁剪区域的宽高
     * @param transFormData  转换数据
     * @return
     */
    public Map<String, Float> parseTransToView(int timelineWidth, int timelineHeight, String filePath, int[] rectSize, Map<String, Float> transFormData) {
        float transXInTimeline = transFormData.get(STORYBOARD_KEY_TRANS_X);
        float transYInTimeline = transFormData.get(STORYBOARD_KEY_TRANS_Y);
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(filePath);
        int videoStreamRotation = avFileInfo.getVideoStreamRotation(0);
        NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
        int heigth = 0;
        int width = 0;
        if (videoStreamRotation % 2 == 0) {
            heigth = dimension.height;
            width = dimension.width;
        } else {
            width = dimension.height;
            heigth = dimension.width;
        }

        float fileRatio = width * 1F / heigth;
        float timelineRatio = timelineWidth * 1F / timelineHeight;

        float fileWidthInTimeline = 0;
        float fileHeightInTimeline = 0;
        if (fileRatio > timelineRatio) {//文件宽对齐
            fileWidthInTimeline = timelineWidth;
            fileHeightInTimeline = fileWidthInTimeline / fileRatio;
        } else {//高对齐
            fileHeightInTimeline = timelineHeight;
            fileWidthInTimeline = fileHeightInTimeline * fileRatio;
        }
        float rectWidthInTimeline = 0;
        float rectHeightInTimeline = 0;
        float rectRatio = rectSize[0] * 1F / rectSize[1];
        if (rectRatio > fileRatio) {//裁剪区域宽对齐
            rectWidthInTimeline = fileWidthInTimeline;
            rectHeightInTimeline = rectWidthInTimeline / rectRatio;
        } else {
            rectHeightInTimeline = fileHeightInTimeline;
            rectWidthInTimeline = rectHeightInTimeline * rectRatio;
        }
        float transXInView = transXInTimeline / rectWidthInTimeline * rectSize[0];
        float transYInView = transYInTimeline / rectHeightInTimeline * rectSize[1];
        transFormData.put(STORYBOARD_KEY_TRANS_X, transXInView);
        transFormData.put(STORYBOARD_KEY_TRANS_Y, transYInView);
        return transFormData;
    }
}

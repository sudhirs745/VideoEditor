package com.glitchcam.vepromei.mimodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsMediaFileConvertor;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.base.BaseEditActivity;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.utils.AppManager;
import com.glitchcam.vepromei.mimodemo.common.utils.PathUtils;
import com.glitchcam.vepromei.mimodemo.common.utils.ScreenUtils;
import com.glitchcam.vepromei.mimodemo.common.utils.TimeFormatUtil;
import com.glitchcam.vepromei.mimodemo.common.utils.TimelineUtil;
import com.glitchcam.vepromei.mimodemo.common.view.CustomTitleBar;
import com.glitchcam.vepromei.mimodemo.common.view.timelineEditor.NvsTimelineEditor;
import com.glitchcam.vepromei.mimodemo.mediapaker.SelectMediaActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrimEditActivity extends BaseEditActivity {

    private static final String TAG = "TrimEditActivity";

    public static final String INTENT_KEY_FROM_WHAT = "from_result";
    public static final int MESSAGE_SCROLLING_FINISH = 100;
    public static final int MESSAGE_DELAY = 20;
    private NvsTimelineEditor mTimelineEditor;
    private TextView mTrimInText;
    private TextView mTrimOutText;
    private View mTimeLineMask;
    private int mSelectPosition = 0;//选中裁剪的视频索引值
    private long mTrimIn = 0;
    private long mRealNeedDuration = 0;
    private boolean mIsFromResult;
    private long mCurrentTimeStamp;
    private float mLastX = -1;
    private NvsMediaFileConvertor mFileConvertor = new NvsMediaFileConvertor();
    private RelativeLayout mWaitLayout;
    private ShotVideoInfo mSelectShotVideoInfo;
    private Handler mScrollHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SCROLLING_FINISH:
                    float scaleX = mTimelineEditor.getMultiThumbnailSequenceView().getScrollX();
                    if (mLastX == scaleX) {//停止滑动
                        long trimOut = mCurrentTimeStamp + mSelectShotVideoInfo.getRealNeedDuration() * Constants.US_TIME_BASE;
                        mVideoFragment.playVideo(mCurrentTimeStamp, trimOut);
                    } else {//滑动未停止,继续发送消息，直到滑动停止
                        mLastX = scaleX;
                        mScrollHandler.sendEmptyMessageDelayed(MESSAGE_SCROLLING_FINISH, MESSAGE_DELAY);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private CustomTitleBar mTitle;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int initRootView() {
        return R.layout.mimo_activity_trim_edit;
    }

    protected void initViews() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mSelectPosition = extras.getInt(Constants.IntentKey.INTENT_KEY_SHOT_ID);
                mIsFromResult = extras.getBoolean(INTENT_KEY_FROM_WHAT, true);
            }
        }
        MiMoLocalData templateInfo = NvTemplateContext.getInstance().getSelectedMimoData();
        if (templateInfo == null) {
            return;
        }
        List<ShotVideoInfo> shotVideoInfos = templateInfo.getTotalShotVideoInfos();
        if (shotVideoInfos.isEmpty() && shotVideoInfos.size() <= mSelectPosition) {
            return;
        }
        mSelectShotVideoInfo = shotVideoInfos.get(mSelectPosition);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.timeline_editor);
        mTrimInText = (TextView) findViewById(R.id.tv_trim_in);
        mTimeLineMask = findViewById(R.id.timeLine_mask);
        mTrimOutText = (TextView) findViewById(R.id.tv_trim_out);
        mTitle = (CustomTitleBar) findViewById(R.id.title);
        mWaitLayout = (RelativeLayout) findViewById(R.id.waitLayout);
        mWaitLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    @Override
    protected void initTitle() {
        if(mTitle != null) {
            mTitle.setTextCenter(getResources().getString(R.string.title_text_trim));
        }
    }

    @Override
    protected void initEditData() {
        //修改trimIn
        mTrimIn = mSelectShotVideoInfo.getTrimIn();
        initMultiSequence();
        long trimOut = mTrimIn + mRealNeedDuration;
        //播放时也从trimIn位置开始
        mCurrentTimeStamp = mTrimIn;
        mTrimOutText.setText(TimeFormatUtil.formatMsToString(trimOut));

    }

    @Override
    protected void initListener() {
        findViewById(R.id.tv_replace).setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_confirm).setOnClickListener(this);
        mTimelineEditor.getMultiThumbnailSequenceView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    mLastX = mTimelineEditor.getMultiThumbnailSequenceView().getScrollX();//初始化滑动位置，用于判断是否停止滑动
                    mScrollHandler.sendEmptyMessageDelayed(MESSAGE_SCROLLING_FINISH, MESSAGE_DELAY);
                }
                return false;
            }
        });
        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                mVideoFragment.setStartTime(0);
                mCurrentTimeStamp = timeStamp;
                long trimIn = timeStamp;
                long trimOut = trimIn + mSelectShotVideoInfo.getRealNeedDuration();
                mTrimIn = trimIn;
                mTrimOutText.setText(TimeFormatUtil.formatMsToString(trimOut));
                mTrimInText.setText(TimeFormatUtil.formatMsToString(trimIn));//单位是毫秒
                mVideoFragment.stopEngine();
            }
        });
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mWaitLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //播放片段，通过片段时间控制
                        if(null != mVideoFragment){
                            mVideoFragment.seekTimeline(0, 0);
                            mVideoFragment.playVideoFromStartPosition();
                        }

                    }
                }, 100);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_replace:
                if (mIsFromResult) {
                    intent.putExtra(SelectMediaActivity.INTENT_KEY_OPERATE_CODE, SelectMediaActivity.OPERATE_CODE_REPLACE);
                    setResult(RESULT_OK, intent);
                } else {
                    clearOldSelectData();
                    intent.setClass(this, SelectMediaActivity.class);
                    intent.putExtra(Constants.IntentKey.INTENT_KEY_SHOT_ID, mSelectPosition);
                    intent.putExtra(SelectMediaActivity.INTENT_KEY_OPERATE_CODE, SelectMediaActivity.OPERATE_CODE_REPLACE);
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                }
                AppManager.getInstance().finishActivity();
                break;
            case R.id.iv_close:
                if (mStreamingContext != null && mTimeline != null) {
                    mStreamingContext.stop();
                    //mTimeline = null;
                }
                intent.putExtra(SelectMediaActivity.INTENT_KEY_OPERATE_CODE, SelectMediaActivity.OPERATE_CODE_CACEL);
                setResult(RESULT_OK, intent);
                //AppManager.getInstance().finishActivity();
                finish();
                break;
            case R.id.iv_confirm:
                if (mStreamingContext != null && mTimeline != null) {
                    mStreamingContext.removeTimeline(mTimeline);
                    mTimeline = null;
                }
                if (mSelectShotVideoInfo != null) {
                    mSelectShotVideoInfo.updateClipTrimIn(mTrimIn);
                }

                boolean isConvertFlag = mSelectShotVideoInfo.isConvertFlag();
                if (isConvertFlag) {
                    convertVideoFile();
                    return;
                }
                intent.putExtra(SelectMediaActivity.INTENT_KEY_OPERATE_CODE, SelectMediaActivity.OPERATE_CODE_TRIM);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }


    private void convertVideoFile() {
        mWaitLayout.setVisibility(View.VISIBLE);
        mFileConvertor.setMeidaFileConvertorCallback(new NvsMediaFileConvertor.MeidaFileConvertorCallback() {
            @Override
            public void onProgress(long l, float v) {

            }

            @Override
            public void onFinish(long l, String s, String s1, int errorCode) {
                Log.d(TAG, "onFinish = " + Thread.currentThread() + ",destFilePth = " + s1);
                if (errorCode == NvsMediaFileConvertor.CONVERTOR_ERROR_CODE_NO_ERROR) {
                    mSelectShotVideoInfo.setConverClipPath(s1);
                }
                mWaitLayout.setVisibility(View.GONE);
                AppManager.getInstance().finishActivity();
            }

            @Override
            public void notifyAudioMuteRage(long l, long l1, long l2) {

            }
        }, true);
        convertVideo();
    }

    private void convertVideo() {
        String srcFilePath = mSelectShotVideoInfo.getVideoClipPath();
        if (TextUtils.isEmpty(srcFilePath)) {
            return;
        }
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            return;
        }
        String contverVideoDir = PathUtils.getVideoConvertDirPath();
        if (TextUtils.isEmpty(contverVideoDir)) {
            return;
        }
        String fileName = srcFile.getName();
        String destFilePath = contverVideoDir + File.separator + fileName;
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            destFile.delete();
        }
        long fromPostion = mSelectShotVideoInfo.getTrimIn();
        long toPostion = fromPostion + mSelectShotVideoInfo.getRealNeedDuration();
        mFileConvertor.convertMeidaFile(srcFilePath, destFilePath, true, fromPostion, toPostion, null);
    }


    private void clearOldSelectData() {
        List<ShotInfo> shotInfos = NvTemplateContext.getInstance().getSelectedMimoData().getShotInfos();
        shotInfos.get(mSelectPosition).setSource(null);
        shotInfos.get(mSelectPosition).setTrimIn(0);
    }

    private void initMultiSequence() {
        if (mTimeline == null) {
            return;
        }
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null)
            return;
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null)
                continue;

            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescs.onlyDecodeKeyFrame = true;
            sequenceDescsArray.add(sequenceDescs);
        }

        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        ViewGroup.LayoutParams layoutParams = mTimeLineMask.getLayoutParams();
        RelativeLayout.LayoutParams editorLayoutParams = (RelativeLayout.LayoutParams) mTimelineEditor.getLayoutParams();
        int halfMaskWidth = layoutParams.width / 2;
        int leftPadding = halfScreenWidth - halfMaskWidth - editorLayoutParams.leftMargin;
        int rightPadding = halfScreenWidth - halfMaskWidth - editorLayoutParams.rightMargin;
        mTimelineEditor.setSequencLeftPadding(leftPadding);
        mTimelineEditor.setSequencRightPadding(rightPadding);
        mTimelineEditor.getMultiThumbnailSequenceView().setThumbnailImageFillMode(NvsMultiThumbnailSequenceView.THUMBNAIL_IMAGE_FILLMODE_ASPECTCROP);
        int innerWith = layoutParams.width - ScreenUtils.dip2px(this, 2);
        mRealNeedDuration = mSelectShotVideoInfo.getRealNeedDuration();
        double pixelPerMicrosecond = (double) innerWith / mRealNeedDuration;
        mTimelineEditor.setPixelPerMicrosecond(pixelPerMicrosecond);//每毫秒的像素值
        long duration = mTimeline.getDuration();
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration, mTrimIn);
        int sequenceWidth = mTimelineEditor.getSequenceWidth();
        if (sequenceWidth < innerWith) { //没有填充满，则放大到填充为止
            double scalFactor = (double) innerWith / (double) sequenceWidth;
            mTimelineEditor.ZoomInSequence(scalFactor);
        }
    }

    @Override
    protected NvsTimeline initTimeLine() {

        //如果是图片，时长为最后的时长，缩略图时长是最后的时长，也就是Timeline的时长
        //如果是短视频，时长是延长后的时长，缩略图的长度是真实的视频的时长
        //如果是长视频，时长是延长后的时长，缩略图的长度是真实的长视频的时长
        if (mSelectShotVideoInfo == null) {
            return null;
        }

        String videoFilePath = mSelectShotVideoInfo.getVideoClipPath();
        return TimelineUtil.createTimeline(videoFilePath);
    }

    @Override
    protected long getVideoDuration() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

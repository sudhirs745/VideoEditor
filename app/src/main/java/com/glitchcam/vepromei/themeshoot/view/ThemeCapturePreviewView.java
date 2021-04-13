package com.glitchcam.vepromei.themeshoot.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.themeshoot.utlils.ThemeShootUtil;
import com.glitchcam.vepromei.themeshoot.view.ThemePreviewLiveWindow;
import com.glitchcam.vepromei.utils.asset.NvAsset;

import java.util.List;

public class ThemeCapturePreviewView extends LinearLayout {
    private int ratioType;


    private Context mContext;
    private final String TAG = getClass().getName();
    private ImageView mCloseView;
    private Button mStartView;
    private TextView mThemeName, mThemeClipNum, mThemeDuration;
    private OnThemePreviewOperationListener mOnThemePreviewOperationListener;

    private ThemeModel mThemeModel;
    private ImageView ivCheck16;
    private ImageView ivCheck9;
    private ThemePreviewLiveWindow mLiveWindow;
    private NvsStreamingContext nvsStreamingContext;
    private NvsTimeline mTimeLine9;
    private NvsTimeline mTimeLine16;
    private LinearLayout mHorizontalCaptureLayout;
    private LinearLayout mVerticalCaptureLayout;
    private TextView mVertivalTv;
    private TextView mHorizontalTv;

    public ThemeCapturePreviewView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public ThemeCapturePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void updateThemeModelView(ThemeModel themeModel) {
        this.mThemeModel = themeModel;
        mThemeName.setText(mThemeModel.getName());
        String clipNumTip = String.format(getResources().getString(R.string.theme_clip_num), mThemeModel.getShotsNumber());
        mThemeClipNum.setText(clipNumTip);
        String duration = ThemeShootUtil.formatUsToString(mThemeModel.getMusicDuration() * 1000);
        String durationTip = String.format(getResources().getString(R.string.theme_duration), duration);
        mThemeDuration.setText(durationTip);
        String supportRatio = mThemeModel.getSupportedAspectRatio();
        if(!TextUtils.isEmpty(supportRatio)) {
            if(!supportRatio.contains("9v16")){
                mVerticalCaptureLayout.setClickable(false);
                ivCheck9.setBackgroundResource(R.mipmap.vertical_rect);
                ivCheck9.setAlpha(0.5f);
                mVertivalTv.setAlpha(0.5f);
            }
            if(!supportRatio.contains("16v9")){
                ivCheck16.setBackgroundResource(R.mipmap.horizental_rect);
                ivCheck16.setEnabled(false);
                ratioType = NvAsset.AspectRatio_9v16;
            }
        }
        updateTimeLine(mThemeModel.getPreview());
    }

    private void updateTimeLine(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            Log.e("pathError", "videoPath==null");
            return;
        }
        String video9Url = ThemeShootUtil.get9V16PathByPath(videoUrl);
        String video16Url = videoUrl.replace("cover9v16.mp4", "cover.mp4");
        createTimeLine9(video9Url);
        createTimeLine16(video16Url);
        if (ratioType == NvAsset.AspectRatio_9v16) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9, mLiveWindow);
            playTimeline(mTimeLine9, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine9), mTimeLine9.getDuration());
        } else if (ratioType == NvAsset.AspectRatio_16v9) {
            nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine16, mLiveWindow);
            playTimeline(mTimeLine16, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine16), mTimeLine16.getDuration());
        }
    }

    private void initView() {
        final View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_theme_preview, this);
        mLiveWindow = rootView.findViewById(R.id.preview_theme_live_window);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
        mThemeName = rootView.findViewById(R.id.theme_name);
        mThemeClipNum = rootView.findViewById(R.id.theme_clip_num);
        mThemeDuration = rootView.findViewById(R.id.theme_duration);
        mCloseView = rootView.findViewById(R.id.iv_close);
        mStartView = rootView.findViewById(R.id.start_capture);
        ivCheck16 = rootView.findViewById(R.id.preview_check_16_iv);
        ivCheck9 = rootView.findViewById(R.id.preview_check_9_iv);
        mVertivalTv = rootView.findViewById(R.id.tv_nine_sixteen);
        mHorizontalTv = rootView.findViewById(R.id.tv_sixteen_nine);
        mStartView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnThemePreviewOperationListener != null) {
                    mOnThemePreviewOperationListener.onEnterButtonPressed(ratioType);
                }
            }
        });
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nvsStreamingContext != null) {
                    nvsStreamingContext.stop();
                }
                if (mOnThemePreviewOperationListener != null) {
                    mOnThemePreviewOperationListener.onPreviewClosed();
                }
            }
        });
        mVerticalCaptureLayout = rootView.findViewById(R.id.preview_check_9);
        mVerticalCaptureLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_9v16) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_9v16;
                ivCheck9.setBackgroundResource(R.mipmap.vertical_rect_selected);
                ivCheck16.setBackgroundResource(R.mipmap.horizental_rect);
                if (mTimeLine9 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video9Url = videoUrl.replace("cover.mp4", "cover9v16.mp4");
                    mThemeModel.setPreview(video9Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine9, mLiveWindow);
                    playTimeline(mTimeLine9, 0, mTimeLine9.getDuration());
                }
            }
        });
        mHorizontalCaptureLayout = rootView.findViewById(R.id.preview_check_16);
        mHorizontalCaptureLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratioType == NvAsset.AspectRatio_16v9) {
                    return;
                }
                ratioType = NvAsset.AspectRatio_16v9;
                ivCheck9.setBackgroundResource(R.mipmap.vertical_rect);
                ivCheck16.setBackgroundResource(R.mipmap.horizental_rect_selected);
                if (mTimeLine16 != null) {
                    String videoUrl = mThemeModel.getPreview();
                    String video16Url = videoUrl.replace("cover9v16.mp4", "cover.mp4");
                    mThemeModel.setPreview(video16Url);
                    nvsStreamingContext.connectTimelineWithLiveWindow(mTimeLine16, mLiveWindow);
                    playTimeline(mTimeLine16, 0, mTimeLine16.getDuration());
                }
            }
        });
        rootView.findViewById(R.id.preview_check_16).callOnClick();
        nvsStreamingContext = NvsStreamingContext.getInstance();
        nvsStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                playTimeline(nvsTimeline, 0, nvsTimeline.getDuration());
            }
        });
    }

    private void seekTimeline(NvsTimeline mTimeLine, long timestamp, int seekShowMode) {
        nvsStreamingContext.seekTimeline(mTimeLine, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
                seekShowMode | NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);

    }


    private void createTimeLine9(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据高判断
        int width = (int) (videoHeight / 16f * 9f);
        int height = videoHeight;
        if (width > videoWidth) {
            //根据宽判断
            width = videoWidth;
            height = (int) (videoHeight * 16f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine9 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine9 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine9.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    private void createTimeLine16(String videoUrl) {
        NvsAVFileInfo fileInfo = nvsStreamingContext.getAVFileInfo(videoUrl);
        if (fileInfo == null) {
            return;
        }
        int videoStreamRotation = fileInfo.getVideoStreamRotation(0);
        int videoWidth = fileInfo.getVideoStreamDimension(0).width;
        int videoHeight = fileInfo.getVideoStreamDimension(0).height;
        int rotation = fileInfo.getVideoStreamRotation(0);
        Log.d(TAG, "createTimeLine9: =================videoWidth=" + videoWidth +  " videoHeight:" + videoHeight + "  rotation:" + rotation);
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        //先根据宽判断
        int height = (int) (videoWidth / 16f * 9f);
        int width = videoWidth;
        if (height > videoHeight) {
            //根据高判断
            height = videoHeight;
            width = (int) (videoHeight * 16f / 9f);
        }
        videoEditRes.imageWidth = width - width % 4;
        videoEditRes.imageHeight = height - height % 2;
        Log.e(TAG, "initTimeline16 video: timeline16 size: " + width + " * " + height);
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;
        mTimeLine16 = nvsStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (mTimeLine16 == null) {
            Log.e(TAG, "failed to create timeline");
            return;
        }
        NvsVideoTrack mVideoTrack = mTimeLine16.appendVideoTrack();
        mVideoTrack.appendClip(videoUrl);
        if (mVideoTrack == null) {
            Log.e(TAG, "videoTrack is null");
            return;
        }
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            mVideoTrack.getClipByIndex(i).setPanAndScan(0, 1);
        }
    }

    public void playTimeline(NvsTimeline mTimeLine, long startTime, long endTime) {
        nvsStreamingContext.playbackTimeline(mTimeLine, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true,
                NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME | NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_LOW_PIPELINE_SIZE);

    }

    public void setOnThemePreviewOperationListener(OnThemePreviewOperationListener onThemePreviewOperationListener) {
        this.mOnThemePreviewOperationListener = onThemePreviewOperationListener;
    }

    public void onResume() {
        if (mTimeLine9 != null && mTimeLine16 != null) {
            if (ratioType == NvAsset.AspectRatio_16v9) {
                playTimeline(mTimeLine16, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine16), mTimeLine16.getDuration());
            } else if (ratioType == NvAsset.AspectRatio_9v16) {
                playTimeline(mTimeLine9, nvsStreamingContext.getTimelineCurrentPosition(mTimeLine9), mTimeLine9.getDuration());
            }
        }
    }

    public interface OnThemePreviewOperationListener {
        void onPreviewClosed();

        void onEnterButtonPressed(int ratio_type);
    }

    public void clear() {
        if (nvsStreamingContext != null) {
            nvsStreamingContext.stop();
        }
        mTimeLine16 = null;
        mTimeLine9 = null;
        getRootView().findViewById(R.id.preview_check_16).callOnClick();
    }
}

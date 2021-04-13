package com.glitchcam.vepromei.photoalbum;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meishe.photoalbum.NvPhotoAlbumHelper;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BasePermissionActivity;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.edit.view.dialog.CompileDialog;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.view.dialog.TipsDialog;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.FileUtils;
import com.glitchcam.vepromei.utils.MediaScannerUtil;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.VideoCompileUtil;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PhotoAlbumPreviewActivity extends BasePermissionActivity {
    private final String TAG = "PhotoAlbumPreview";
    private ArrayList<String> mCaptionList = new ArrayList<String>() {{
        add("美摄科技");
        add("2020");
    }};
    private String mMp4Path;
    private Context mContext;
    private NvsLiveWindow mLiveWindow;
    private CustomTitleBar mTitleBar;
    private ImageButton mPlayBtn;
    private ProgressBar mProgressBar;
    private NvsTimeline mTimeline;
    private CompileDialog mCompileDialog;
    private TipsDialog mTipsDialog;

    private NvsStreamingContext.PlaybackCallback mPlaybackCallbackListener;
    private NvsStreamingContext.PlaybackCallback2 mPlaybackCallback2Listener;
    private NvsStreamingContext.StreamingEngineCallback mStreamingEngineCallbackListener;
    private NvsStreamingContext.CompileCallback mCompileCallback;
    private NvsStreamingContext.CompileCallback2 mCompileCallback2;

    private CountDownTimer mHidePlayBtnTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mPlayBtn.setVisibility(View.GONE);
        }
    };

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    protected int initRootView() {
        mContext = this;
        return R.layout.activity_photoalbum_preview;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mLiveWindow = (NvsLiveWindow) findViewById(R.id.nvsLivewidow);
        mPlayBtn = (ImageButton) findViewById(R.id.playBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCompileDialog = new CompileDialog(this);
        mTipsDialog = new TipsDialog(this);

        mCompileDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mCompileDialog.setProgress(0);
                mStreamingContext.stop();
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
        mTitleBar.setFinishActivity(false);
    }

    @Override
    protected void onDestroy() {
        mStreamingContext.stop();
        mStreamingContext.removeTimeline(mTimeline);
        mStreamingContext.setPlaybackCallback(null);
        mStreamingContext.setPlaybackCallback2(null);
        mStreamingContext.setStreamingEngineCallback(null);
        mStreamingContext.setCompileCallback(null);
        mStreamingContext.setCompileCallback2(null);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mTipsDialog.show();
    }

    @Override
    protected void initData() {
        if (PhotoAlbumConstants.albumData != null) {
            mTitleBar.setTextCenter(PhotoAlbumConstants.albumData.photosAlbumName);
        }

        initTimeline();
    }

    @Override
    protected void initListener() {
        mPlayBtn.setOnClickListener(this);
        mLiveWindow.setOnClickListener(this);

        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                mTipsDialog.show();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                mMp4Path = PathUtils.getPhotoAlbumVideoPath();
                VideoCompileUtil.compileVideo(mStreamingContext, mTimeline, mMp4Path, 0, mTimeline.getDuration());

                mCompileDialog.show();
            }
        });

        mPlaybackCallbackListener = new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                Log.e(TAG, "onPlaybackStopped: ");
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                Log.e(TAG, "onPlaybackEOF: ");
                seekTimeline(0);
                mProgressBar.setProgress(0);
            }
        };

        mCompileDialog.setOnBtnClickListener(new CompileDialog.OnBtnClickListener() {
            @Override
            public void OnCancelBtnClicked(View view) {
                mStreamingContext.stop();
                mCompileDialog.dismiss();
            }
        });

        mTipsDialog.setOnBtnClickListener(new TipsDialog.OnBtnClickListener() {
            @Override
            public void OnConfirmBtnClicked() {
                mTipsDialog.dismiss();
                AppManager.getInstance().finishActivity();
            }

            @Override
            public void OnCancelBtnClicked() {
                mTipsDialog.dismiss();
            }
        });

        mPlaybackCallback2Listener = new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long l) {
                mProgressBar.setProgress((int) l);
            }
        };

        mStreamingEngineCallbackListener = new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.photo_ablum_preview_pause));
                    hidePlayBtnTimerWork(true);
                } else {
                    mPlayBtn.setVisibility(View.VISIBLE);
                    hidePlayBtnTimerWork(false);
                    mPlayBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.photo_ablum_preview_play));
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        };

        mCompileCallback = new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                mCompileDialog.setProgress(i);
            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                /*
                 * 添加到媒体库
                 * Add to media library
                 * */
                MediaScannerUtil.scanFile(mMp4Path, "video/mp4");
                mCompileDialog.dismiss();
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                mCompileDialog.dismiss();
            }
        };

        mCompileCallback2 = new NvsStreamingContext.CompileCallback2() {
            @Override
            public void onCompileCompleted(NvsTimeline nvsTimeline, boolean cancel) {
                if (cancel) { // 人为取消

                }
                mCompileDialog.dismiss();
                mCompileDialog.setProgress(0);
            }
        };


        mStreamingContext.setPlaybackCallback(mPlaybackCallbackListener);
        mStreamingContext.setPlaybackCallback2(mPlaybackCallback2Listener);
        mStreamingContext.setStreamingEngineCallback(mStreamingEngineCallbackListener);
        mStreamingContext.setCompileCallback(mCompileCallback);
        mStreamingContext.setCompileCallback2(mCompileCallback2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBtn:
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mStreamingContext.stop();
                } else {
                    playVideo(mStreamingContext.getTimelineCurrentPosition(mTimeline), -1);
                }
                break;
            case R.id.nvsLivewidow:
                if (mPlayBtn.getVisibility() != View.VISIBLE) {
                    hidePlayBtnTimerWork(false);
                    hidePlayBtnTimerWork(true);
                } else {
                    mPlayBtn.performClick();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化时间线
     * Initialize the timeline
     */
    private void initTimeline() {
        if (PhotoAlbumConstants.albumData == null) {
            return;
        }
        String xmlZip = PhotoAlbumConstants.albumData.filePath;
        String licPath = PhotoAlbumConstants.albumData.licPath;
        ArrayList<String> list = new ArrayList();
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        if (clipInfos != null && !clipInfos.isEmpty()) {
            for (ClipInfo clipInfo : clipInfos) {
                if (clipInfo == null) {
                    continue;
                }

                String path = clipInfo.getFilePath();
                if (FileUtils.isContent(path)) {
                    path = FileUtils.contentPath2AbsPath(getContentResolver(), clipInfo.getFilePath());
                }
                list.add(path);
            }
        }
        generatePhotoAlbum(xmlZip, licPath, PhotoAlbumConstants.albumData.sourceDir, list);
    }

    private void generatePhotoAlbum(String xmlZip, String licPath, String sourceDir, ArrayList pics) {

        mTimeline = NvPhotoAlbumHelper.createPhotoAlbumTimeline(this, xmlZip, licPath, sourceDir, pics, mCaptionList);
        if (mTimeline == null) {
            Log.e(TAG, "initTimeline： timeline create failed");
            return;
        }
        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);

        mProgressBar.setMax((int) mTimeline.getDuration());

        playVideo(0, -1);
        Log.e(TAG, "initTimeline： timeline duration: " + mTimeline.getDuration());
    }

    private void initTimelineXY() {
        if (PhotoAlbumConstants.albumData == null) {
            return;
        }
        final String xmlZip = PhotoAlbumConstants.albumData.filePath;
        final String licPath = PhotoAlbumConstants.albumData.licPath;
        final ArrayList<String> list = new ArrayList();
        ArrayList<String> grabList = new ArrayList();
//        final Map<Long, String> grabMap = new HashMap<>();
        final Map<Long, String> grabMap = new TreeMap<Long, String>(
                new Comparator<Long>() {
                    public int compare(Long obj1, Long obj2) {
                        // 降序排序
                        return (int) (obj1 - obj2);
                    }
                });
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        NvsTimeline grabImageTimeline = getGrabImageTimeline();
        if (clipInfos != null && !clipInfos.isEmpty() && grabImageTimeline != null) {
            NvsVideoTrack videoTrack = grabImageTimeline.appendVideoTrack();
            if (videoTrack != null) {
                for (ClipInfo clipInfo : clipInfos) {
                    if (clipInfo == null) {
                        continue;
                    }
                    String strPhoto = clipInfo.getFilePath();
                    if (strPhoto == null || strPhoto.isEmpty()) {
                        continue;
                    }
                    list.add(strPhoto);
                    NvsVideoClip clip = videoTrack.appendClip(strPhoto);
                    if (clip != null) {
                        clip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
                        clip.setImageMotionAnimationEnabled(false);
                        clip.setSourceBackgroundMode(NvsVideoClip.ClIP_BACKGROUNDMODE_BLUR);
                    }
                }

                mStreamingContext.setImageGrabberCallback(new NvsStreamingContext.ImageGrabberCallback() {
                    @Override
                    public void onImageGrabbedArrived(Bitmap bitmap, long l) {
                        String jpgPath = PathUtils.getPhotoAlbumPicturePath();
                        boolean save_ret = Util.saveBitmapToSD(bitmap, jpgPath);
                        if (save_ret) {
                            grabMap.put(l, jpgPath);
                        }
                        if (grabMap.size() == list.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    generatePhotoAlbum(xmlZip, licPath, PhotoAlbumConstants.albumData.sourceDir, new ArrayList(grabMap.values()));
                                }
                            });
                        }
                    }
                });
                for (int i = 0; i < videoTrack.getClipCount(); ++i) {
                    videoTrack.setBuiltinTransition(i, "");

                    NvsVideoClip clip = videoTrack.getClipByIndex(i);
                    String path = clip.getFilePath();
                    NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(path);
                    if (avFileInfo != null) {
                        if (avFileInfo.getVideoStreamDimension(0).width * 16 == avFileInfo.getVideoStreamDimension(0).height * 9) {
                            grabMap.put(clip.getInPoint(), path);
                            continue;
                        }
                    }
                    mStreamingContext.seekTimeline(grabImageTimeline, clip.getInPoint(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                    mStreamingContext.grabImageFromTimelineAsync(grabImageTimeline, clip.getInPoint(), new NvsRational(1, 1),
                            NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                }

                if (grabMap.size() == list.size()) {
                    generatePhotoAlbum(xmlZip, licPath, PhotoAlbumConstants.albumData.sourceDir, new ArrayList(grabMap.values()));
                }
            }
        }
    }

    /**
     * 播放视频
     * Play video
     */
    public void playVideo(long start, long end) {
        if (mStreamingContext == null) {
            Log.e(TAG, "playVideo: mStreamingContext is null");
            return;
        }
        if (mTimeline == null) {
            Log.e(TAG, "playVideo: mTimeline is null");
            return;
        }
        mStreamingContext.playbackTimeline(mTimeline, start, end,
                NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    /**
     * seek时间线到指定时刻
     * seek timeline to the specified time
     */
    private void seekTimeline(long timestamp) {
        if (mStreamingContext == null) {
            Log.e(TAG, "seekTimeline: mStreamingContext is null");
            return;
        }
        if (mTimeline == null) {
            Log.e(TAG, "seekTimeline: mTimeline is null");
            return;
        }
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
    }

    private void hidePlayBtnTimerWork(boolean iswork) {
        if (iswork) {
            mHidePlayBtnTimer.start();
        } else {
            mPlayBtn.setVisibility(View.VISIBLE);
            mHidePlayBtnTimer.cancel();
        }
    }

    private NvsTimeline getGrabImageTimeline() {
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        videoEditRes.imageWidth = 720;
        videoEditRes.imageHeight = 1280;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline nvsTimeline = mStreamingContext.createTimeline(videoEditRes, videoFps, audioEditRes);
        if (nvsTimeline == null) {
            Log.e(TAG, "getGrabImageTimeline： timeline create failed");
            return null;
        }
        return nvsTimeline;
    }
}

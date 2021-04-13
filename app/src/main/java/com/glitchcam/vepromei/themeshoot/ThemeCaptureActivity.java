package com.glitchcam.vepromei.themeshoot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.edit.view.dialog.ThemeDialog;
import com.glitchcam.vepromei.mimodemo.common.template.utils.TemplateFileUtils;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.themeshoot.utlils.ThemeShootUtil;
import com.glitchcam.vepromei.themeshoot.view.CaptureProgressView;
import com.glitchcam.vepromei.themeshoot.view.ClipLineView;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.utils.asset.NvAssetManager;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.MusicInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeCaptureActivity extends BaseActivity implements NvsStreamingContext.CaptureRecordingDurationCallback {
    private static final String TAG = ThemeCaptureActivity.class.getSimpleName();

    private static final int STOP_RECORDING = 101;
    private static final int STOP_RECORDING_FAILED = 104;
    private static final int STOP_MUSIC_PLAYER = 103;
    private static final int STOP_MUSIC_PLAYER_FAILED = 105;
    private static final int UPDATE_RECORDING_TIME = 102;

    private ClipLineView mClipLinesView;
    private CaptureProgressView mCaptureProgressView;
    private NvsLiveWindow mLiveWindow;
    private ImageView mBackView;
    private View mDeleteView, mPreviewView;

    private ThemeModel mThemeModel;
    private List<ThemeModel.ShotInfo> mShotInfos;
    private int mCurrentShotIndex = 0;
    private ArrayList<String> mRecordFileList = new ArrayList<>();
    private String mCurRecordVideoPath;
    private boolean isInRecording = false;
    //单位us
    private long mCurrentClipDuration;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_RECORDING:
                    mHandler.removeMessages(STOP_RECORDING);
                    mHandler.removeMessages(STOP_RECORDING_FAILED);
                    stopRecording(true);
                    break;
                case STOP_RECORDING_FAILED:
                    mHandler.removeMessages(STOP_RECORDING);
                    mHandler.removeMessages(STOP_RECORDING_FAILED);
                    stopRecording(false);
                    break;
                case STOP_MUSIC_PLAYER:
                    mHandler.removeMessages(STOP_MUSIC_PLAYER_FAILED);
                    mHandler.removeMessages(STOP_MUSIC_PLAYER);
                    AudioPlayer.getInstance(ThemeCaptureActivity.this).pause();
                    playPosition = AudioPlayer.getInstance(ThemeCaptureActivity.this).getNowPlayPosition();
                    break;
                case STOP_MUSIC_PLAYER_FAILED:
                    mHandler.removeMessages(STOP_MUSIC_PLAYER_FAILED);
                    mHandler.removeMessages(STOP_MUSIC_PLAYER);
                    AudioPlayer.getInstance(ThemeCaptureActivity.this).stopPlay();
                    break;
                case UPDATE_RECORDING_TIME:
                    long time = msg.getData().getLong("time");
                    int progress = 100 - (int) (time * 100 / mCurrentClipDuration);
                    String timeTip = ThemeShootUtil.formatUsToStr(time);
                    if (isInRecording) {
                        mCaptureProgressView.setTextAndProgress(timeTip, progress);
                    }
                    Log.d(TAG, "UPDATE_RECORDING_TIME|isInRecording：" + isInRecording + " |time:" + time + "|timeTip:" + timeTip);
                    break;
                default:
                    break;
            }
        }
    };
    private int ratioType;
    //闪光灯
    private ImageView ivFlash;
    private NvsStreamingContext.CaptureDeviceCapability mCapability;
    private TextView tvInfo;
    private ImageView ivInfo;
    private long sumDuration;
    private ThemeDialog mConfirmDialog;
    private View iVSwitch;
    private int playPosition = 0;

    @Override
    protected int initRootView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ratioType = bundle.getInt("ratioType");
                if (ratioType == NvAsset.AspectRatio_9v16) {
                    // 设置为竖屏模式
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    return R.layout.activity_theme_capture;
                } else {
                    // 设置为横屏模式
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    return R.layout.activity_theme_capture_landscape;
                }
            }
        }
        return R.layout.activity_theme_capture;
    }

    @Override
    protected void initViews() {

        iVSwitch = findViewById(R.id.iv_switch);
        ivFlash = findViewById(R.id.iv_theme_flash);
        mLiveWindow = (NvsLiveWindow) findViewById(R.id.liveWindow);
        mBackView = findViewById(R.id.iv_back);
        mDeleteView = findViewById(R.id.delete_layout);
        mPreviewView = findViewById(R.id.preview_layout);
        mPreviewView.setEnabled(false);
        mClipLinesView = findViewById(R.id.clip_lines);
        mCaptureProgressView = findViewById(R.id.startRecordingImage);
        mCaptureProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInRecording || getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    return;
                }
                if (mRecordFileList.size() >= mClipLinesView.getmRatiosSize()) {
                    //已经录制满了
                    return;
                }
                mCurRecordVideoPath = ThemeShootUtil.getVlogCacheFilePath(ThemeCaptureActivity.this, mRecordFileList.size());
                Log.d(TAG, "onClick: path ===============" + mCurRecordVideoPath);
                if (mCurRecordVideoPath == null || mShotInfos == null || mCurrentShotIndex >= mShotInfos.size()) {
                    return;
                }

                mCaptureProgressView.setEnabled(false);
                if (!mStreamingContext.startRecording(mCurRecordVideoPath)) {
                    return;
                }
                mCurrentClipDuration = mShotInfos.get(mCurrentShotIndex).getNeedDuration() * Constants.US_TIME_BASE;
                Log.d(TAG, "mCurrentClipDuration=" + mCurrentClipDuration);
                tvInfo.setVisibility(View.GONE);
                ivInfo.setVisibility(View.GONE);
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                if (shotInfo.canPlaced()) {
                    updateFilterVideoFx(shotInfo.getFilter());
                }
//                AudioPlayer.getInstance(ThemeCaptureActivity.this).startPlay((int) sumDuration);
                AudioPlayer.getInstance(ThemeCaptureActivity.this).startPlay(playPosition);
                isInRecording = true;
                updateRecordingViewState(true);
            }
        });
        tvInfo = findViewById(R.id.theme_tv_info);
        ivInfo = findViewById(R.id.theme_iv_info);
    }

    /**
     * 下一个视频片段信息
     *
     * @return
     */
    private void getNextCurrentShotIndex() {
        if (mShotInfos != null) {
            while (mCurrentShotIndex < mShotInfos.size() - 1) {
                mCurrentShotIndex++;
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                //如果不是空镜头返回
                if (shotInfo.canPlaced()) {
                    sumDuration += mShotInfos.get(mCurrentShotIndex).getDuration();
                    return;
                }

            }
        }
    }

    /**
     * 上一个视频片段信息
     *
     * @return
     */
    private void getPreCurrentShotIndex() {
        if (mShotInfos != null) {
            while (mCurrentShotIndex > 0) {
                mCurrentShotIndex--;
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
                //如果不是空镜头返回
                if (shotInfo.canPlaced()) {
                    sumDuration -= mShotInfos.get(mCurrentShotIndex).getDuration();
                    setInfoView(shotInfo);
                    return;
                }
            }
        }
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mThemeModel = (ThemeModel) bundle.get("ThemeModel");
                initThemeModelDada();
            }
        }
    }

    private void showConfirmDialog(boolean exit) {
        if (mConfirmDialog == null) {
            mConfirmDialog = new ThemeDialog(this);
        }
        if (mConfirmDialog.isShowing()) {
            return;
        }
        if (exit) {
            mConfirmDialog.setOnBtnClickListener(new ThemeDialog.OnBtnClickListener() {
                @Override
                public void OnConfirmClick(View view) {
                    finish();
                }
            });
        } else {
            mConfirmDialog.setOnBtnClickListener(new ThemeDialog.OnBtnClickListener() {
                @Override
                public void OnConfirmClick(View view) {
                    deleteClip();
                }
            });
        }
        mConfirmDialog.show();
        if (exit) {
            mConfirmDialog.setTittleText(getResources().getText(R.string.exit_capture_confirm) + "");
        } else {
            mConfirmDialog.setTittleText(getResources().getText(R.string.delete_this_video_confirm) + "");
        }
    }

    @Override
    protected void initListener() {
        mBackView.setOnClickListener(this);
        mDeleteView.setOnClickListener(this);
        mPreviewView.setOnClickListener(this);
        ivFlash.setOnClickListener(this);
        iVSwitch.setOnClickListener(this);
    }

    private int flasyType = 0;
    private static final int FLASH_TYPE_ON = 100;
    private static final int FLASH_TYPE_OFF = 101;
    private int cameraIndex = 0;

    @Override
    public void onBackPressed() {
        showConfirmDialog(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                showConfirmDialog(true);
                break;
            case R.id.delete_layout:
                if (mRecordFileList != null && mRecordFileList.size() > 0) {
                    showConfirmDialog(false);
                }
                break;
            case R.id.preview_layout:
                jumpToPreview();
                break;
            case R.id.iv_theme_flash:
                changeFlash();
                break;
            case R.id.iv_switch:
                switchCamera();
                break;
            default:
                break;
        }
    }

    /**
     * 切换摄像头
     */
    private void switchCamera() {
        if (isInRecording) {
            return;
        }
        if (cameraIndex == 0) {
            cameraIndex = 1;
        } else {
            cameraIndex = 0;
        }
        startCapturePreview(true);
        mCapability = mStreamingContext.getCaptureDeviceCapability(cameraIndex);
    }

    /**
     * 删除一段视频
     */
    private void deleteClip() {
        if (mRecordFileList != null && mRecordFileList.size() > 0) {
            mRecordFileList.remove(mRecordFileList.size() - 1);
            getPreCurrentShotIndex();
        }
        updateRecordingViewState(false);

    }

    private void changeFlash() {
        if (flasyType == FLASH_TYPE_ON) {
            flasyType = FLASH_TYPE_OFF;
            ivFlash.setImageResource(R.mipmap.theme_flash_close);
            if (mStreamingContext != null) {
                mStreamingContext.toggleFlash(false);
            }
        } else {
            flasyType = FLASH_TYPE_ON;
            ivFlash.setImageResource(R.mipmap.theme_flash_open);
            if (mStreamingContext != null) {
                mStreamingContext.toggleFlash(true);
            }
        }
//        } else {
//            ToastUtil.showToast(getApplicationContext(), "不支持闪光灯切换");
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCaptureEnv();
    }

    private void initThemeModelDada() {
        if (mThemeModel == null || TextUtils.isEmpty(mThemeModel.getFolderPath())) {
            return;
        }
        //安装包裹
        List<String> packagePaths = mThemeModel.getPackagePaths();
        if (packagePaths != null) {
            for (int i = 0; i < packagePaths.size(); i++) {
                String packagePath = packagePaths.get(i);
                if (!TextUtils.isEmpty(packagePath)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    boolean b = installAssetPackage(packagePath, stringBuilder);
                    Log.d(TAG, "installAssetPackage result:" + b + "||id:" + stringBuilder.toString());
                }
            }
        }
        String assetFilePath = mThemeModel.getFolderPath();
        NvAssetManager nvAssetManager = getNvAssetManager();
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_FILTER, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_COMPOUND_CAPTION, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_VIDEO_TRANSITION, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_FONT, assetFilePath);
        nvAssetManager.searchAssetInLocalPath(NvAsset.ASSET_ANIMATED_STICKER, assetFilePath);
        addMusicInfoData();
        mShotInfos = mThemeModel.getShotInfos();
        List<Double> ratios = new ArrayList<>();
        for (int i = 0; i < mShotInfos.size(); i++) {
            ThemeModel.ShotInfo shotInfo = mShotInfos.get(i);
            Log.d(TAG, "initData -> shotInfo getSource= " + shotInfo.getSource());
            Log.d(TAG, "initData -> shotInfo canPlaced= " + shotInfo.canPlaced());
            if (!shotInfo.canPlaced()) {
                if (ratioType == NvAsset.AspectRatio_9v16) {
                    mShotInfos.get(i).setSource(ThemeShootUtil.get9V16PathByPath(mShotInfos.get(i).getSource()));
                }
                continue;
            }
//            else {
//                if (mCurrentClipDuration <= 0) {
//                    mCurrentClipDuration = mShotInfos.get(i).getNeedDuration() * Constants.US_TIME_BASE;
//                }
//            }
            double ratio = ((double) mShotInfos.get(i).getNeedDuration());
            ratios.add(ratio);
        }
        if (ratios.size() <= 0) {
            return;
        }
        mClipLinesView.setRatios(ratios);

        if (mShotInfos.size() > 0) {
            for (int i = 0; i < mShotInfos.size(); i++) {
                ThemeModel.ShotInfo shotInfo = mShotInfos.get(i);
                if (shotInfo != null && shotInfo.canPlaced()) {
                    setInfoView(shotInfo);
                    break;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if (isInRecording) {
            mHandler.sendEmptyMessage(STOP_MUSIC_PLAYER_FAILED);
            mHandler.sendEmptyMessage(STOP_RECORDING_FAILED);
        }
        super.onStop();
    }


    /**
     * 设置提示信息及图片
     *
     * @param shotInfo
     */
    private void setInfoView(ThemeModel.ShotInfo shotInfo) {
        if (shotInfo != null) {
            //提示信息
            List<ThemeModel.ShotInfo.AlertInfo> alertInfos = shotInfo.getAlertInfo();
            tvInfo.setVisibility(View.GONE);
            if (alertInfos != null && alertInfos.size() > 0) {
                ThemeModel.ShotInfo.AlertInfo alertInfo = alertInfos.get(0);
                if (alertInfo != null) {
                    String targetLanguage = alertInfo.getTargetLanguage();
                    String info = alertInfo.getOriginalText();
                    if(Util.isZh(mContext)){
                        info = alertInfo.getTargetText();
                    }else{
                        info = alertInfo.getOriginalText();
                    }
                    if (!TextUtils.isEmpty(info)) {
                        tvInfo.setText(info + "");
                        tvInfo.setVisibility(View.VISIBLE);
                    }
                }
            }
            //提示图片
            String infoImgUrl = mThemeModel.getFolderPath() + File.separator + shotInfo.getAlertImage();
            if (TextUtils.isEmpty(infoImgUrl)) {
                ivInfo.setVisibility(View.GONE);
            } else {
                Glide.with(this).asBitmap().load(infoImgUrl).into(ivInfo);
                ivInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initCaptureEnv() {
        if (null == mStreamingContext) {
            return;
        }
        /*
         *给Streaming Context设置回调接口
         *Set callback interface for Streaming Context
         * */
        mStreamingContext.setCaptureRecordingDurationCallback(this);
        if (mStreamingContext.getCaptureDeviceCount() == 0) {
            return;
        }

        /*
         * 将采集预览输出连接到LiveWindow控件
         * Connect the capture preview output to the LiveWindow control
         * */
        if (!mStreamingContext.connectCapturePreviewWithLiveWindow(mLiveWindow)) {
            Log.e(TAG, "Failed to connect capture preview with livewindow!");
            return;
        }

        try {
            startCapturePreview(false);
            mCapability = mStreamingContext.getCaptureDeviceCapability(cameraIndex);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startCapturePreviewException: initCapture failed,under 6.0 device may has no access to camera");
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         */
        int captureResolutionGrade = NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH;
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            if (!mStreamingContext.startCapturePreview(cameraIndex, captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE, null)) {
                Log.e(TAG, "Failed to start capture preview!");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }


    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        Log.d(TAG, "onCaptureRecordingDuration l:" + l + "||mCurrentClipDuration:" + mCurrentClipDuration);
        Message msg = Message.obtain();
        msg.what = UPDATE_RECORDING_TIME;
        Bundle bundle = new Bundle();
        long time = mCurrentClipDuration - l;
        if (time < 0) {
            time = 0;
        }
        bundle.putLong("time", time);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        if (l >= mCurrentClipDuration) {
            mHandler.sendEmptyMessage(STOP_MUSIC_PLAYER);
            mHandler.sendEmptyMessage(STOP_RECORDING);
            return;
        }
    }

    /**
     * 停止录制
     *
     * @param addRecord
     */
    private void stopRecording(boolean addRecord) {
        isInRecording = false;
        Log.d(TAG, "0=================onStop stopRecording=" + isInRecording + "|addRecord=" + addRecord);
        AudioPlayer.getInstance(ThemeCaptureActivity.this).pause();
        mStreamingContext.stopRecording();
        mStreamingContext.removeAllCaptureVideoFx();
        if (addRecord) {
            mRecordFileList.add(mCurRecordVideoPath);
            ThemeModel.ShotInfo shotInfo = mShotInfos.get(mCurrentShotIndex);
            if (shotInfo != null) {
                shotInfo.setSource(mCurRecordVideoPath);
                shotInfo.setFileDuration(shotInfo.getNeedDuration());
            }
            getNextCurrentShotIndex();
            if (mCurrentShotIndex < mShotInfos.size() && mCurrentShotIndex >= 0 && mRecordFileList.size() < mClipLinesView.getmRatiosSize()) {
                setInfoView(mShotInfos.get(mCurrentShotIndex));
            }
        }
        updateRecordingViewState(false);

    }

    private void updateRecordingViewState(boolean isRecording) {
        int show = View.VISIBLE;
        mPreviewView.setVisibility(View.INVISIBLE);
        if (isRecording) {
            mCaptureProgressView.setBackgroundResource(R.drawable.theme_white_ball_bg);
            show = View.INVISIBLE;
            mClipLinesView.setVisibility(View.GONE);
        } else {
            mCaptureProgressView.setTextAndProgress("", 0);
            mCaptureProgressView.setBackgroundResource(R.drawable.theme_capture_button);
            mCaptureProgressView.setEnabled(true);
            int recordFileListSize = mRecordFileList.size();
            if (recordFileListSize >= mClipLinesView.getmRatiosSize()) {
                mPreviewView.setEnabled(true);
                mPreviewView.setVisibility(View.VISIBLE);
            }
//            else {
//                mCurrentClipDuration = mShotInfos.get(mCurrentShotIndex).getNeedDuration() * Constants.US_TIME_BASE;
//            }
            mClipLinesView.setClipIndex(recordFileListSize);
            mClipLinesView.setVisibility(View.VISIBLE);
        }
//        mClipLinesView.setVisibility(show);
        mDeleteView.setVisibility(show);
//        mPreviewView.setVisibility(show);
        iVSwitch.setVisibility(show);
        mBackView.setVisibility(show);
        ivFlash.setVisibility(show);
        Log.d(TAG, "updateRecordingViewState VISIBLE=0 isRecording:" + isRecording + " show:" + show);
    }

    private void updateFilterVideoFx(String filterPackageId) {
        if (!TextUtils.isEmpty(filterPackageId)) {
            NvsCaptureVideoFx curCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterPackageId);
            if (curCaptureVideoFx != null) {
                curCaptureVideoFx.setFilterIntensity(1.0f);
            }
        }
    }

    private void jumpToPreview() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (int i = 0; i < mRecordFileList.size(); i++) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(mRecordFileList.get(i));
            pathList.add(clipInfo);
        }
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(pathList.get(0).getFilePath());
        if (avFileInfo == null) {
            return;
        }
        TimelineData.instance().clear();//数据清空
        NvsSize size = avFileInfo.getVideoStreamDimension(0);
        int rotation = avFileInfo.getVideoStreamRotation(0);
        if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;
        }
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(ratioType));
        TimelineData.instance().setMakeRatio(ratioType);
        TimelineData.instance().setClipInfoData(pathList);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ThemeModel", mThemeModel);
        AppManager.getInstance().jumpActivity(ThemeCaptureActivity.this, ThemePreviewActivity.class, bundle);
    }

    private void addMusicInfoData() {
        ArrayList<MusicInfo> musicInfos = new ArrayList<>();
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setFilePath(
                TemplateFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(), mThemeModel.getMusic(), mThemeModel.isBuildInTemp()));
        musicInfo.setIsAsset(mThemeModel.isBuildInTemp());
        musicInfo.setAssetPath(TemplateFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(), mThemeModel.getMusic(), !mThemeModel.isBuildInTemp()));
        musicInfo.setTrimIn(0);
        musicInfo.setInPoint(0);
        if (mThemeModel.getNeedControlMusicFading() == 1) {
            musicInfo.setFadeDuration(mThemeModel.getMusicFadingTime() * Constants.US_TIME_BASE);
        }
        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(
                TemplateFileUtils.getTemplateAssetsFilePath(mThemeModel.getFolderPath(),
                        mThemeModel.getMusic(),
                        mThemeModel.isBuildInTemp()));
        if (avFileInfo != null) {
            musicInfo.setTrimOut(mThemeModel.getMusicDuration() * Constants.US_TIME_BASE);
        }
        AudioPlayer.getInstance(ThemeCaptureActivity.this).setCurrentMusic(musicInfo, false);
        musicInfos.add(musicInfo);
        TimelineData.instance().setMusicList(musicInfos);
    }

    private boolean installAssetPackage(String filterPackageFilePath, StringBuilder packageId) {
        int type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        if (filterPackageFilePath.endsWith(".videofx")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        }
        if (filterPackageFilePath.endsWith(".compoundcaption")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
        }
        if (filterPackageFilePath.endsWith(".videotransition")) {
            type = NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION;
        }
        int error = mStreamingContext.getAssetPackageManager().installAssetPackage(filterPackageFilePath, null, type, true, packageId);
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR
                || error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
                mStreamingContext.getAssetPackageManager().upgradeAssetPackage(filterPackageFilePath, null, type, true, packageId);
            }
            return true;
        } else {
            Logger.e(TAG, "theme installAssetPackage Failed = " + packageId.toString());
        }
        return false;
    }
}

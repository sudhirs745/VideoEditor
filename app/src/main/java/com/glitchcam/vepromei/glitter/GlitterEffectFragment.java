package com.glitchcam.vepromei.glitter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.capturescene.NvsStreamingContextUtil;
import com.glitchcam.vepromei.utils.ParameterSettingValues;
import com.glitchcam.vepromei.utils.permission.PermissionsActivity;
import com.glitchcam.vepromei.utils.permission.PermissionsChecker;

import java.util.ArrayList;
import java.util.List;

import static com.meicam.sdk.NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_CAPTURE_AUDIO;
import static com.glitchcam.vepromei.MainActivity.REQUEST_CAMERA_PERMISSION_CODE;
import static com.glitchcam.vepromei.utils.permission.PermissionDialog.noPermissionDialog;
import static com.glitchcam.vepromei.utils.permission.PermissionsActivity.EXTRA_PERMISSIONS;

/**
 * 闪光特效拍摄fragment
 * Flash effect shooting fragment
 */
public class GlitterEffectFragment extends Fragment implements
        NvsStreamingContext.CaptureDeviceCallback,
        NvsStreamingContext.CaptureRecordingDurationCallback,
        NvsStreamingContext.CaptureRecordingStartedCallback,
        NvsStreamingContext.StreamingEngineCallback {

    private final String TAG = "GlitterEffectFragment";

    private NvsLiveWindow mLiveWindow;
    private NvsStreamingContext mStreamingContext;
    private int mCurrentDeviceIndex = 0;
    private List<String> mAllRequestPermission = new ArrayList<>( );
    private GlitterEffectInterface mFlashEffectInterface;
    private View autoFocusView;
    private Animation mFocusAnimation;
    private boolean openAutoFocusAndExposure = false;
    private boolean mSupportAutoFocus = false;
    private boolean mSupportFlash = false;
    private NvsStreamingContext.CaptureDeviceCapability mCapability = null;
    private boolean mSupportExposure;
    private int mMinExpose;
    private int mMaxExpose;
    private NvsCaptureVideoFx mGlitterFx = null;

    public void openAutoFocusAndExposure(boolean open, View view, Animation animation) {
        this.openAutoFocusAndExposure = open;
        if ((view != null) && (animation != null)) {
            this.autoFocusView = view;
            this.mFocusAnimation = animation;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GlitterEffectInterface) {
            mFlashEffectInterface = (GlitterEffectInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_record, container, false);
        mLiveWindow = rootView.findViewById(R.id.liveWindow);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStreamingContext = NvsStreamingContext.getInstance( );
        initListener( );
        initCapture( );
        if (mFlashEffectInterface != null) {
            mFlashEffectInterface.onFragmentLoadFinished( );
        }
    }

    public int setExposureCompensation(int progress) {
        mStreamingContext.setExposureCompensation(progress + mMinExpose);
        return progress + mMinExpose;
    }

    public int getMaxExpose() {
        return mMaxExpose - mMinExpose;
    }

    public int getCurExpose() {
        return mStreamingContext.getExposureCompensation( ) - mMinExpose;
    }

    public boolean isSupportExposure() {
        return mSupportExposure;
    }

    private void updateSettingsWithCapability(int deviceIndex) {
        /*
        * 获取采集设备能力描述对象，设置自动聚焦，曝光补偿，缩放
        * Get acquisition device capability description object, set auto focus, exposure compensation, zoom
        * */
        mCapability = mStreamingContext.getCaptureDeviceCapability(deviceIndex);
        if (null == mCapability) {
            return;
        }
        mSupportFlash = mCapability.supportFlash;
        mSupportAutoFocus = mCapability.supportAutoFocus;
        mSupportExposure = mCapability.supportExposureCompensation;
        mMinExpose = mCapability.minExposureCompensation;
        mMaxExpose = mCapability.maxExposureCompensation;
    }

    public NvsCaptureVideoFx setGlitterVideoFx(String effectFilePath) {
        if (mStreamingContext != null) {
            if (mGlitterFx == null) {
                mGlitterFx = mStreamingContext.appendBuiltinCaptureVideoFx("Glitter");
            }
            if (mGlitterFx != null) {
                mGlitterFx.setStringVal("Source Path", effectFilePath);
            }
        }
        return mGlitterFx;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mLiveWindow.setOnTouchListener(new View.OnTouchListener( ) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (openAutoFocusAndExposure && mSupportAutoFocus && autoFocusView != null && mFocusAnimation != null) {
                    float rectHalfWidth = autoFocusView.getWidth( ) / 2;
                    if (event.getX( ) - rectHalfWidth >= 0 && event.getX( ) + rectHalfWidth <= mLiveWindow.getWidth( )
                            && event.getY( ) - rectHalfWidth >= 0 && event.getY( ) + rectHalfWidth <= mLiveWindow.getHeight( )) {
                        autoFocusView.setX(event.getX( ) - rectHalfWidth);
                        autoFocusView.setY(event.getY( ) - rectHalfWidth);
                        RectF rectFrame = new RectF( );
                        rectFrame.set(autoFocusView.getX( ), autoFocusView.getY( ),
                                autoFocusView.getX( ) + autoFocusView.getWidth( ),
                                autoFocusView.getY( ) + autoFocusView.getHeight( ));
                        /*
                        * 启动自动聚焦
                        * Start autofocus
                        * */
                        autoFocusView.startAnimation(mFocusAnimation);
                        initAutoFocusAndExposure(rectFrame);
                    }
                }
                return false;
            }
        });
    }

    private void initAutoFocusAndExposure(RectF rectF) {
        if (openAutoFocusAndExposure) {
            mStreamingContext.startAutoFocus(new RectF(rectF));
        }
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        mStreamingContext.setCaptureDeviceCallback(this);
        mStreamingContext.setCaptureRecordingDurationCallback(this);
        mStreamingContext.setCaptureRecordingStartedCallback(this);
        mStreamingContext.setStreamingEngineCallback(this);
        if (mStreamingContext.getCaptureDeviceCount( ) == 0) {
            return;
        }
        if (!mStreamingContext.connectCapturePreviewWithLiveWindow(mLiveWindow)) {
            Log.e(TAG, "Failed to connect capture preview with liveWindow!");
            return;
        }
        checkNeedPermission( );
    }

    private boolean checkNeedPermission() {
        PermissionsChecker mPermissionsChecker = new PermissionsChecker(getContext( ));
        mAllRequestPermission.add(Manifest.permission.CAMERA);
        mAllRequestPermission = mPermissionsChecker.checkPermission(mAllRequestPermission);
        return mAllRequestPermission.isEmpty( );
    }

    private int getCodeInPermission(String permission) {
        int code = 0;
        if (permission.equals(Manifest.permission.CAMERA)) {
            code = REQUEST_CAMERA_PERMISSION_CODE;
        }
        return code;
    }

    private void startCapturePreview(boolean deviceChanged) {
        int captureResolutionGrade = ParameterSettingValues.instance( ).getCaptureResolutionGrade( );
        if ((deviceChanged || NvsStreamingContextUtil.getInstance( ).getEngineState( ) != com.meicam.sdk.NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW)) {
            if (!NvsStreamingContextUtil.getInstance( ).getmStreamingContext( ).startCapturePreview(mCurrentDeviceIndex,
                    captureResolutionGrade,
                    com.meicam.sdk.NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_GRAB_CAPTURED_VIDEO_FRAME |
                            com.meicam.sdk.NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER
                            | STREAMING_ENGINE_CAPTURE_FLAG_DONT_CAPTURE_AUDIO, null)) {
                Log.e(TAG, "Failed to start capture preview!");
            }
        }
    }

    public void changeCurrentDeviceIndex() {
        this.mCurrentDeviceIndex = mCurrentDeviceIndex == 0 ? 1 : 0;
        startCapturePreview(true);
    }

    public boolean startRecord(String path) {
        if (!isRecording( )) {
            return mStreamingContext.startRecording(path);
        }
        return false;
    }

    public boolean stopRecord() {
        if (isRecording( )) {
            mStreamingContext.stopRecording( );
            return true;
        }
        return false;
    }

    public boolean isRecording() {
        return NvsStreamingContextUtil.getInstance( ).getEngineState( ) == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING;
    }

    public void changeFlash() {
        if (mSupportFlash) {
            mStreamingContext.toggleFlash(!isFlashOn( ));
        }
    }

    public boolean isFlashOn() {
        return mStreamingContext.isFlashOn( );
    }

    @Override
    public void onCaptureDeviceCapsReady(int i) {
        updateSettingsWithCapability(i);

    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {

    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        mFlashEffectInterface.onCaptureDevicePreviewStarted(i);
    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {

    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        mFlashEffectInterface.onCaptureRecordingFinished(i);
    }

    @Override
    public void onCaptureRecordingError(int i) {
        mFlashEffectInterface.onCaptureRecordingError(i);
    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        mFlashEffectInterface.onCaptureRecordingDuration(i, l);
    }

    @Override
    public void onCaptureRecordingStarted(int i) {
        mFlashEffectInterface.onCaptureRecordingStarted(i);
    }

    @Override
    public void onPause() {
        super.onPause( );
        if (mStreamingContext != null) {
            mStreamingContext.stop( );
        }
    }

    @Override
    public void onResume() {
        super.onResume( );
        if (permissionResult == PermissionsActivity.PERMISSIONS_No_PROMPT) {
            noPermissionDialog(getContext( ));
        } else {
            if (mAllRequestPermission.isEmpty( )) {
                startCapturePreview(false);
            } else if (permissionResult == PermissionsActivity.PERMISSIONS_GRANTED) {
                startCapturePreview(false);
            } else {
                int code = getCodeInPermission(mAllRequestPermission.get(0));
                startPermissionsActivity(code, mAllRequestPermission.get(0));
            }
        }
    }

    private void startPermissionsActivity(int code, String... permission) {
        Intent intent = new Intent(getActivity( ), PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permission);
        startActivityForResult(intent, code);
    }

    private int permissionResult = -1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionResult = resultCode;
    }

    public boolean isSupportFlash() {
        return mSupportFlash;
    }

    @Override
    public void onStreamingEngineStateChanged(int i) {
        mFlashEffectInterface.onStreamingEngineStateChanged(i);
    }

    @Override
    public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

    }
}

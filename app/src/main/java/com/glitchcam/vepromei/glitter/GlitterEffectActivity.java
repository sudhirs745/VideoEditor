package com.glitchcam.vepromei.glitter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsStreamingContext;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.utils.MediaScannerUtil;
import com.glitchcam.vepromei.utils.OnSeekBarChangeListenerAbs;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.TimeFormatUtil;
import com.glitchcam.vepromei.utils.ToastUtil;
import com.glitchcam.vepromei.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 闪光特效页面
 */
public class GlitterEffectActivity extends BaseActivity implements GlitterEffectInterface {

    private static final String TAG = "GlitterEffectActivity";

    private static final long TIMEBASE = 1000000L;
    private static final int EXPOSE_TYPE = 100;
    private static final int STRENGTH_TYPE = 200;

    private NvRecordButton mStartRecordingButton;
    private ImageView mFlashEffectClose;
    private ImageView mFlashEffectFlash;
    private ImageView mFlashEffectSwitch;
    private TextView mRecordTimeTextView;
    private GlitterEffectFragment mPreviewFragment;

    private String mRecordFilePath;
    private List<GlitterEffectBean> flashEffectBeans;
    private int mCurSeekBarType;
    private DecimalFormat mFnum = new DecimalFormat("#0.0");

    private NvsCaptureVideoFx mCurCaptureVideoFx;
    private TextView mStyleTextView;
    private TextView mStrengthTextView;
    private View mExposeView;
    private View mStyleView;
    private RecyclerView mStyleRecyclerView;
    private GlitterStyleAdapter mGlitterStyleAdapter;
    private AlertDialog mDisplayDialog;
    private SeekBar mProgressSeekBar;
    private TextView mProgressRightText;
    private TextView mProgressLeftText;
    private TextView mProgressBottomText;

    @Override
    protected int initRootView() {
        return R.layout.activity_flash_effect;
    }

    @Override
    protected void initViews() {
        mStartRecordingButton = (NvRecordButton) findViewById(R.id.startRecording_button);
        mFlashEffectClose = (ImageView) findViewById(R.id.glitter_effect_close);
        mFlashEffectFlash = (ImageView) findViewById(R.id.glitter_effect_expose);
        mFlashEffectSwitch = (ImageView) findViewById(R.id.glitter_effect_switch);
        mRecordTimeTextView = (TextView) findViewById(R.id.record_time_textView);
        mStyleTextView = (TextView) findViewById(R.id.glitter_effect_style_tv);
        mStrengthTextView = (TextView) findViewById(R.id.glitter_effect_strength_tv);
        /*
        * 初始化fragment
        * Initialization fragment
        * */
        mPreviewFragment = (GlitterEffectFragment) getSupportFragmentManager( ).findFragmentByTag(TAG);
        if (mPreviewFragment == null) {
            mPreviewFragment = new GlitterEffectFragment( );
            getSupportFragmentManager( ).beginTransaction( )
                    .add(R.id.glitter_effect_rootView, mPreviewFragment, TAG)
                    .commit( );
            getSupportFragmentManager( ).beginTransaction( ).show(mPreviewFragment).commit( );
        }
        /*
        * 曝光和强度布局
        * Exposure and intensity layout
        * */
        mExposeView = LayoutInflater.from(this).inflate(R.layout.flash_effect_seekbar_layout, null);
        mProgressSeekBar = mExposeView.findViewById(R.id.glitter_seekBar);
        mProgressRightText = mExposeView.findViewById(R.id.glitter_seekProgress_right);
        mProgressLeftText = mExposeView.findViewById(R.id.glitter_seekProgress_left);
        mProgressBottomText = mExposeView.findViewById(R.id.glitter_seekProgress_bottom);
        /*
        * 样式布局
        * Style layout
        * */
        mStyleView = LayoutInflater.from(this).inflate(R.layout.flash_effect_style_layout, null);
        mStyleRecyclerView = mStyleView.findViewById(R.id.style_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(GlitterEffectActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mStyleRecyclerView.setLayoutManager(layoutManager);
        mDisplayDialog = new AlertDialog.Builder(this).create( );
        mDisplayDialog.setOnCancelListener(new DialogInterface.OnCancelListener( ) {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeDialogView(mDisplayDialog);
            }
        });
    }

    @Override
    protected void initListener() {
        mStartRecordingButton.setOnClickListener(this);
        mFlashEffectClose.setOnClickListener(this);
        mFlashEffectFlash.setOnClickListener(this);
        mFlashEffectSwitch.setOnClickListener(this);
        mStyleTextView.setOnClickListener(this);
        mStrengthTextView.setOnClickListener(this);
        mGlitterStyleAdapter.setStyleClickListener(new GlitterStyleAdapter.StyleClickListener( ) {
            @Override
            public void onItemClick(View view, int pos, GlitterEffectBean glitterEffectBean) {
                if (Util.isFastClick( )) {
                    return;
                }
                mCurCaptureVideoFx = mPreviewFragment.setGlitterVideoFx(glitterEffectBean.getEffectFilePath( ));
            }

            @Override
            public void onSameItemClick(int pos) {

            }
        });
        mProgressSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs( ) {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mCurSeekBarType == STRENGTH_TYPE) {
                        if (mCurCaptureVideoFx != null) {
                            mCurCaptureVideoFx.setFloatVal("Quantity", ((float) progress) / 100);
                            mProgressRightText.setText(mFnum.format(((float) progress) / 100));
                        }
                    } else if (mCurSeekBarType == EXPOSE_TYPE) {
                        int curProgress = mPreviewFragment.setExposureCompensation(progress);
                        mProgressBottomText.setText(curProgress + "");
                    }
                }
            }
        });
        mStartRecordingButton.setOnRecordStateChangedListener(new NvRecordButton.OnRecordStateChangedListener( ) {
            @Override
            public void onRecordStart() {
                /*
                *  当前在录制状态，可停止视频录制
                * Currently in recording state, you can stop video recording
                * */
                if (mPreviewFragment.stopRecord( )) {

                } else {
                    if (!startRecord( )) {
                        mStartRecordingButton.reset( );
                    }
                }
            }

            @Override
            public void onRecordStop() {
                mPreviewFragment.stopRecord( );
            }
        });
    }

    private void setSeekBarTextState(int seekBarType) {
        if (seekBarType == STRENGTH_TYPE) {
            mProgressRightText.setVisibility(View.VISIBLE);
            mProgressLeftText.setVisibility(View.VISIBLE);
            mProgressBottomText.setVisibility(View.INVISIBLE);
        } else {
            mProgressRightText.setVisibility(View.INVISIBLE);
            mProgressLeftText.setVisibility(View.INVISIBLE);
            mProgressBottomText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (Util.isFastClick( )) {
            return;
        }
        switch (v.getId( )) {
            case R.id.startRecording_button:
                if (mPreviewFragment.stopRecord( )) {
                    return;
                }
                startRecord( );
                break;
            case R.id.glitter_effect_close:
                finish( );
                break;
            case R.id.glitter_effect_expose:
                if (mPreviewFragment.isSupportExposure( )) {
                    mCurSeekBarType = EXPOSE_TYPE;
                    mProgressSeekBar.setMax(mPreviewFragment.getMaxExpose( ));
                    mProgressSeekBar.setProgress(mPreviewFragment.getCurExpose( ));
                    mProgressBottomText.setText(mStreamingContext.getExposureCompensation( ) + "");
                    mProgressSeekBar.setThumb(ContextCompat.getDrawable(GlitterEffectActivity.this, R.mipmap.icon_block));
                    setSeekBarTextState(mCurSeekBarType);
                    showDialogView(mDisplayDialog, mExposeView);
                } else {
                    //
                }
                break;
            case R.id.glitter_effect_switch:
                mPreviewFragment.changeCurrentDeviceIndex( );
                break;
            case R.id.glitter_effect_style_tv:
                showDialogView(mDisplayDialog, mStyleView);
                break;
            case R.id.glitter_effect_strength_tv:
                mCurSeekBarType = STRENGTH_TYPE;
                mProgressSeekBar.setMax(100);
                int quantity = (int) (mCurCaptureVideoFx.getFloatVal("Quantity") * 100);
                mProgressRightText.setText(mFnum.format(mCurCaptureVideoFx.getFloatVal("Quantity")));
                mProgressSeekBar.setProgress(quantity);
                mProgressSeekBar.setThumb(ContextCompat.getDrawable(GlitterEffectActivity.this, R.mipmap.glitter_seekbar_thumb_icon));
                setSeekBarTextState(mCurSeekBarType);
                showDialogView(mDisplayDialog, mExposeView);
                break;
            default:
                break;
        }
    }

    private boolean startRecord() {
        layoutChangeOnRecordStateChange(true);
        mRecordFilePath = PathUtils.getFlashEffectRecordingDirectory( );
        if (!mPreviewFragment.startRecord(mRecordFilePath)) {
            return false;
        }
        return true;
    }

    private void layoutChangeOnRecordStateChange(boolean isStart) {
        mFlashEffectClose.setVisibility(isStart ? View.INVISIBLE : View.VISIBLE);
        mFlashEffectFlash.setVisibility(isStart ? View.INVISIBLE : View.VISIBLE);
        mFlashEffectSwitch.setVisibility(isStart ? View.INVISIBLE : View.VISIBLE);
        mStyleTextView.setVisibility(isStart ? View.INVISIBLE : View.VISIBLE);
        mStrengthTextView.setVisibility(isStart ? View.INVISIBLE : View.VISIBLE);
        mRecordTimeTextView.setVisibility(isStart ? View.VISIBLE : View.INVISIBLE);
    }

    private void parameterChangeStateChange(boolean isSet) {
        mFlashEffectClose.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
        mFlashEffectFlash.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
        mFlashEffectSwitch.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
        mStyleTextView.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
        mStrengthTextView.setVisibility(isSet ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void initData() {
        /*
        * 自动聚焦
        * Autofocus
        * */
        mPreviewFragment.openAutoFocusAndExposure(true, null, null);
        /*
        *  初始化特效数据
        * Initialize effect data
        * */
        flashEffectBeans = new ArrayList<>( );
        flashEffectBeans.add(new GlitterEffectBean("assets:/glitter/star3.png", "file:///android_asset/glitter/star3.png", "start3"));
        flashEffectBeans.add(new GlitterEffectBean("assets:/glitter/star4.png", "file:///android_asset/glitter/star4.png", "start4"));
        flashEffectBeans.add(new GlitterEffectBean("assets:/glitter/star5.png", "file:///android_asset/glitter/star5.png", "start5"));
        /*
        * 适配器
        * Adapter
        * */
        mGlitterStyleAdapter = new GlitterStyleAdapter(GlitterEffectActivity.this, flashEffectBeans);
        mStyleRecyclerView.setAdapter(mGlitterStyleAdapter);
        mGlitterStyleAdapter.selectPos(0);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    public void onCaptureRecordingStarted(int i) {

    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        if (mRecordTimeTextView != null) {
            mRecordTimeTextView.setText(TimeFormatUtil.formatUsToString2(l));
        }
    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        layoutChangeOnRecordStateChange(false);
        MediaScannerUtil.scanFile(mRecordFilePath, "video/mp4");
        ToastUtil.showToastCenterWithBg(GlitterEffectActivity.this,
                "  " + getString(R.string.glitter_save_success_tip) + "  ", "#333333", R.drawable.glitter_save_toast_bg);
    }

    @Override
    public void onCaptureRecordingError(int i) {
        layoutChangeOnRecordStateChange(false);
    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        layoutChangeOnRecordStateChange(false);
    }

    @Override
    public void onFragmentLoadFinished() {
        mCurCaptureVideoFx = mPreviewFragment.setGlitterVideoFx("assets:/glitter/star3.png");
    }

    @Override
    public void onStreamingEngineStateChanged(int i) {
        if (i != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
            mStartRecordingButton.reset( );
        }
    }

    /**
     * 显示窗口
     * Show window
     */
    private void showDialogView(AlertDialog dialog, View view) {
        dialog.show( );
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow( ).getAttributes( );
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow( ).setGravity(Gravity.BOTTOM);
        params.dimAmount = 0.0f;
        dialog.getWindow( ).setAttributes(params);
        dialog.getWindow( ).setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow( ).setWindowAnimations(R.style.fx_dlg_style);
        parameterChangeStateChange(true);
    }

    /**
     * 关闭窗口
     * close the window
     */
    private void closeDialogView(AlertDialog dialog) {
        dialog.dismiss( );
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        /*
        * 动画时间300毫秒
        * 300 ms animation time
        * */
        translate.setDuration(300);
        /*
        * 动画出来控件可以点击
        * Animated controls can be clicked
        * */
        translate.setFillAfter(false);
        parameterChangeStateChange(false);
    }

    @Override
    protected void onPause() {
        super.onPause( );
        mPreviewFragment.stopRecord( );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx( );
        }
    }
}

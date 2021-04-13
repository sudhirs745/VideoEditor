package com.glitchcam.vepromei.mimodemo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.CommonUtil;
import com.glitchcam.vepromei.mimodemo.common.utils.Logger;
import com.glitchcam.vepromei.mimodemo.common.utils.MediaScannerUtil;
import com.glitchcam.vepromei.mimodemo.common.utils.VideoCompileUtil;

public class CompileVideoFragment extends Fragment {
    private final String TAG = "CompileVideoFragment";
    private LinearLayout mCompileVideoRect;
    private TextView mCompileVideoFinished;
    private String mCompileVideoPath;
    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private NvsTimeline mTimeline;
    private OnCompileVideoListener mCompileVideoListener;
    private NvsTimelineAnimatedSticker mLogoSticker;
    public void setCompileVideoListener(OnCompileVideoListener compileVideoListener) {
        mCompileVideoListener = compileVideoListener;
    }

    //视频播放相关回调
    public interface OnCompileVideoListener {
        //video compile
        void compileProgress(NvsTimeline timeline, int progress);
        void compileFinished(NvsTimeline timeline);
        void compileFailed(NvsTimeline timeline);
        void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled);
        void compileVideoCancel();
    }

    public void setTimeline(NvsTimeline timeline) {
        mTimeline = timeline;
    }
    //生成视频
    public void compileVideo() {
        initCompileCallBack();
        mCompileVideoRect.setVisibility(View.VISIBLE);
        mCompileVideoFinished.setVisibility(View.GONE);
        mCompileVideoPath = VideoCompileUtil.getCompileVideoPath();
        if(mCompileVideoPath == null)
            return;
//        addLogoWaterMark();//添加美摄logo
        VideoCompileUtil.compileVideo(mStreamingContext,
                mTimeline,
                mCompileVideoPath,
                0,
                mTimeline.getDuration()
        );
    }
    private void removeLogoSticker(){
        if(mLogoSticker != null){
            mTimeline.removeAnimatedSticker(mLogoSticker);
            mLogoSticker = null;
        }
    }
    //停止引擎
    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();//停止播放
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compile_video, container, false);
        mCompileVideoRect = (LinearLayout)rootView.findViewById(R.id.compileVideoRect);
        mCompileVideoFinished = (TextView)rootView.findViewById(R.id.compileVideoFinished);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
    }

    public void initCompileCallBack(){
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {}

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                mStreamingContext.setCompileConfigurations(null);
                //加入到媒体库
                MediaScannerUtil.scanFile(mCompileVideoPath, "video/mp4");
                if(mCompileVideoListener != null)
                    mCompileVideoListener.compileFinished(nvsTimeline);
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                String[] tipsName = getResources().getStringArray(R.array.compile_video_failed_tips);
                CommonUtil.showDialog(getActivity(), tipsName[0], tipsName[1], null);
                if(mCompileVideoListener != null)
                    mCompileVideoListener.compileFailed(nvsTimeline);
                removeLogoSticker();
            }
        });
        mStreamingContext.setCompileCallback2(new NvsStreamingContext.CompileCallback2() {
            @Override
            public void onCompileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                if (!isCanceled) {
                    mStreamingContext.setCompileConfigurations(null);
                    mCompileVideoRect.setVisibility(View.GONE);
                    mCompileVideoFinished.setVisibility(View.VISIBLE);
                    //加入到媒体库
                    MediaScannerUtil.scanFile(mCompileVideoPath, "video/mp4");

                    String[] tipsName = getResources().getStringArray(R.array.compile_video_success_tips);
                    StringBuilder successTips = new StringBuilder();
                    successTips.append(tipsName[0]);
                    successTips.append("\n");
                    successTips.append(mCompileVideoPath);
                    Logger.e(TAG,"successTips" + successTips.toString());
                    CommonUtil.showToast(getActivity(), successTips.toString());
                }
                removeLogoSticker();
                if(mCompileVideoListener != null)
                    mCompileVideoListener.compileCompleted(nvsTimeline,isCanceled);

            }
        });
    }
}
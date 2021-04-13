package com.glitchcam.vepromei.mimodemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoResolution;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.mimodemo.base.BaseEditActivity;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.CompoundCaptionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TimelineData;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.utils.AppManager;
import com.glitchcam.vepromei.mimodemo.common.utils.TimelineUtil;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.BottomMenuViewHolder;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.IAdapterLifeCircle;
import com.glitchcam.vepromei.mimodemo.mediapaker.view.SelectBottomMenu;

import java.util.ArrayList;
import java.util.List;

import static com.glitchcam.vepromei.mimodemo.mediapaker.SelectMediaActivity.REQUEST_CODE_EDIT_CLIP;


public class EditActivity extends BaseEditActivity {
    private static final String TAG = "EditActivity";
    private static final int REQUEST_CODE_CAPTION_EDIT = 101;
    private static final int REQUEST_CODE_VIDEO_TRIM = 102;
    private final String FragmentTag = "CompileVideoFragment";
    private SelectBottomMenu mBottomMenu;
    private CustomTitleBar mTitle;
    private TextView mSwitchHint;
    private Switch mSwitch;
    private CompileVideoFragment mCompileVideoFragment;
    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;
    private NvsTimelineCompoundCaption mCurCaption;
    private NvsTimelineCompoundCaption mAddComCaption;
    private boolean mIsForResult = false;
    //跳转到trim编辑视频的页面选择的position
    private int selectedToTrimPosition = -1;
    //避免重复点击编辑，创建多个编辑页面
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimelineUtil.rebuildTimelineByTemplate(mTimeline);
        if (mVideoFragment != null) {
            mVideoFragment.initData();
            mBottomMenu.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVideoFragment.playVideo(0, mTimeline.getDuration());
                }
            }, 100);
        }
    }

    @Override
    protected int initRootView() {
        return R.layout.mimo_activity_edit;
    }

    @Override
    protected void initViews() {
        mBottomMenu = (SelectBottomMenu) findViewById(R.id.bottom_menu);
        mTitle = (CustomTitleBar) findViewById(R.id.title);
        mSwitchHint = (TextView) findViewById(R.id.tv_switch_water_hint);
        mSwitch = (Switch) findViewById(R.id.switch_water_filter);
        mSwitch.setChecked(true);
        mBottomMenu.setIsEditable(false);
        mBottomMenu.addAdapterLifeCircle(new IAdapterLifeCircle() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
                View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mimo_select_menu_item_big_layout, viewGroup, false);
                return new BottomMenuViewHolder(rootView);
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitle.setTextRight(getResources().getString(R.string.generate));
        mTitle.setTextRightVisible(View.VISIBLE);
        mTitle.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {//生成
                showFragment();
                mCompileVideoFragment.setTimeline(mTimeline);
                mCompileVideoFragment.compileVideo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTION_EDIT) {
            mCaptionDataListClone = TimelineData.instance().getCompoundCaptionArray();
            TimelineUtil.setCompoundCaption(mTimeline, mCaptionDataListClone);
            long curSeekPos = TimelineData.instance().getCurSeekTimelinePos();
            seekTimeline(curSeekPos);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    selectCaptionAndTimeSpan();
                    mVideoFragment.setDrawRectVisible(true);//重新绘制，解决不能点击的问题
                }
            });
        } else if (requestCode == REQUEST_CODE_EDIT_CLIP) {
            if (data == null) {
                return;
            }
        }else if(REQUEST_CODE_VIDEO_TRIM == requestCode){
            //刷新页面,
            mBottomMenu.upDataForPosition(selectedToTrimPosition);
        }
    }

    @Override
    protected void initEditData() {
        initCompileVideoFragment();
    }

    @Override
    protected void initListener() {
        mBottomMenu.setOnItemClickListener(new SelectBottomMenu.OnItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                //连续两次操作的时间间隔
                Long currentTime = System.currentTimeMillis();
                long duration = currentTime - lastClickTime;
                lastClickTime = currentTime;
                //200ms暂定，如果觉得间隔小，继续加大
                if(duration <= 200 ){
                    return;
                }

                selectedToTrimPosition = index;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.IntentKey.INTENT_KEY_SHOT_ID, index);
                bundle.putBoolean(TrimEditActivity.INTENT_KEY_FROM_WHAT, true);
                AppManager.getInstance().jumpActivityForResult(EditActivity.this, TrimEditActivity.class, bundle, REQUEST_CODE_VIDEO_TRIM);

            }
        });
        setOnPlayProgressChangeListener();
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mVideoFragment.stopEngine();
                String hint = isChecked ? EditActivity.this.getResources().getString(R.string.hint_open_last_water) :
                        EditActivity.this.getResources().getString(R.string.hint_close_last_water);
                mSwitchHint.setText(hint);
                if (!isChecked) {
                    List<NvsTimelineAnimatedSticker> stickerList = mTimeline.getAnimatedStickersByTimelinePosition(mTimeline.getDuration() - 1000);
                } else {
                    TimelineUtil.setSticker(mTimeline, TimelineData.instance().getStickerData());
                }
            }
        });
    }


    private void setOnPlayProgressChangeListener() {
        mVideoFragment.setOnPlayProgressChangeListener(new VideoFragment.OnPlayProgressChangeListener() {
            @Override
            public void onPlayProgressChanged(long curTime) {
                updatePlayState(curTime / Constants.US_TIME_BASE);
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (mVideoFragment.isDrawRectVisible() && isPlaying) {
                    mVideoFragment.setDrawRectVisible(false);
                }
            }
        });
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    private void updatePlayState(long curTime) {
        mBottomMenu.updatePosition(curTime);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected NvsTimeline initTimeLine() {
        NvsVideoResolution videoResolution = TimelineData.instance().getVideoResolution();
        if (videoResolution == null) {
            return null;
        }
        NvsTimeline timeline = TimelineUtil.newTimeline(videoResolution);
        if (timeline == null) {
            return null;
        }
        getCurCaption(timeline);
        return timeline;
    }

    @Override
    protected long getVideoDuration() {
        return 0L;
    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
            @Override
            public void compileProgress(NvsTimeline timeline, int progress) {
            }

            @Override
            public void compileFinished(NvsTimeline timeline) {
                hideFragment();
            }

            @Override
            public void compileFailed(NvsTimeline timeline) {
                hideFragment();
            }

            @Override
            public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                hideFragment();
            }

            @Override
            public void compileVideoCancel() {
                hideFragment();
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.compilePage, mCompileVideoFragment, FragmentTag).commit();
        hideFragment();
    }

    private void showFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FragmentTag);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(R.id.compilePage, mCompileVideoFragment, FragmentTag).commit();
        } else {
            fragmentManager.beginTransaction().show(fragment).commit();
        }
    }

    private void hideFragment() {
        getSupportFragmentManager().beginTransaction().hide(mCompileVideoFragment).commit();
    }

    private void selectCaptionAndTimeSpan() {
        selectCaption();
        updateComCaptionBoundingRect();
    }

    private void selectCaption() {
        if (mTimeline == null) {
            return;
        }
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal > zVal) {
                    zVal = tmpZVal;
                    index = i;
                }
            }
            mCurCaption = captionList.get(index);
        } else {
            mCurCaption = null;
        }
    }

    private void updateComCaptionBoundingRect() {
        mVideoFragment.setCurCompoundCaption(mCurCaption);
        mVideoFragment.updateCompoundCaptionCoordinate(mCurCaption);
        if (mAddComCaption == null) {
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mVideoFragment.changeCompoundCaptionRectVisible();
        }
    }

    private void getCurCaption(NvsTimeline timeline) {
        mAddComCaption = timeline.getFirstCompoundCaption();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空数据

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null) {
            return;
        }

        context.clearCachedResources(false);

        clearSelectData();
    }

    private void clearSelectData() {
        MiMoLocalData template = NvTemplateContext.getInstance().getSelectedMimoData();
        if (template == null) {
            return;
        }
        List<ShotInfo> shotInfos = template.getShotInfos();
        for (int index = 0; index < shotInfos.size(); index++) {
            ShotInfo shotInfo = shotInfos.get(index);
            if (!shotInfo.isCanPlaced()) {
                continue;
            }
            shotInfo.setSource(null);
            shotInfo.setTrimIn(0);
        }
        template.setShotInfos(shotInfos);
    }

}

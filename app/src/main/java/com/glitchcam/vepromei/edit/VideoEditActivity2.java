package com.glitchcam.vepromei.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.edit.Caption.CaptionActivity;
import com.glitchcam.vepromei.edit.adapter.AssetRecyclerViewAdapter;
import com.glitchcam.vepromei.edit.adapter.SpaceItemDecoration;
import com.glitchcam.vepromei.edit.anim.AnimationActivity;
import com.glitchcam.vepromei.edit.animatesticker.AnimateStickerActivity;
import com.glitchcam.vepromei.edit.background.BackgroundActivity;
import com.glitchcam.vepromei.edit.compoundcaption.CompoundCaptionActivity;
import com.glitchcam.vepromei.edit.data.AssetInfoDescription;
import com.glitchcam.vepromei.edit.data.BackupData;
import com.glitchcam.vepromei.edit.data.BitmapData;
import com.glitchcam.vepromei.edit.data.FilterItem;
import com.glitchcam.vepromei.edit.filter.FilterActivity;
import com.glitchcam.vepromei.edit.filter.GlitchAdapter;
import com.glitchcam.vepromei.edit.interfaces.OnItemClickListener;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.edit.models.FiltersModel;
import com.glitchcam.vepromei.edit.music.MusicActivity;
import com.glitchcam.vepromei.edit.timelineEditor.NvsTimelineEditor;
import com.glitchcam.vepromei.edit.timelineEditor.NvsTimelineTimeSpan;
import com.glitchcam.vepromei.edit.transition.TransitionActivity;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.watermark.WaterMarkActivity;
import com.glitchcam.vepromei.edit.watermark.WaterMarkUtil;
import com.glitchcam.vepromei.interfaces.TipsButtonClickListener;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.AssetFxUtil;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.ScreenUtils;
import com.glitchcam.vepromei.utils.TimeFormatUtil;
import com.glitchcam.vepromei.utils.TimelineUtil;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.utils.asset.NvAssetManager;
import com.glitchcam.vepromei.utils.dataInfo.CaptionInfo;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.CompoundCaptionInfo;
import com.glitchcam.vepromei.utils.dataInfo.KeyFrameInfo;
import com.glitchcam.vepromei.utils.dataInfo.MusicInfo;
import com.glitchcam.vepromei.utils.dataInfo.StickerInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.glitchcam.vepromei.utils.dataInfo.TransitionInfo;
import com.glitchcam.vepromei.utils.dataInfo.VideoClipFxInfo;
import com.glitchcam.vepromei.utils.dataInfo.VideoFx;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.glitchcam.vepromei.utils.Constants.ROTATION_Z;
import static com.glitchcam.vepromei.utils.Constants.SCALE_X;
import static com.glitchcam.vepromei.utils.Constants.SCALE_Y;
import static com.glitchcam.vepromei.utils.Constants.TRANS_X;
import static com.glitchcam.vepromei.utils.Constants.TRANS_Y;
import static com.glitchcam.vepromei.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.glitchcam.vepromei.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;
import static com.glitchcam.vepromei.utils.dataInfo.VideoClipFxInfo.FXMODE_BUILTIN;

/**
 * VideoEditActivity class
 *
 * @author czl
 * @date 2018-05-28
 */
public class VideoEditActivity2 extends BaseActivity {

    private static final String TAG = "VideoEditActivity";

    public static final int REQUESTRESULT_GLITCH = 1002;
    public static final int REQUESTRESULT_FILTER = 1003;
    public static final int REQUESTRESULT_STICKER = 1004;
    public static final int REQUESTRESULT_CAPTION = 1005;
    public static final int REQUESTRESULT_TRANSITION = 1006;
    public static final int REQUESTRESULT_MUSIC = 1007;
    public static final int REQUESTRESULT_WATERMARK = 1009;
    public static final int REQUESTRESULT_COMPOUND_CAPTION = 1010;
    public static final int REQUESTRESULT_ANIMATION = 1011;
    public static final int REQUESTRESULT_BACKGROUND = 1012;
    private static final int VIDEOPLAYTOEOF = 105;

    private CustomTitleBar mTitleBar;

    private RelativeLayout mBottomLayout;
    private RecyclerView mAssetRecycleView;
    private AssetRecyclerViewAdapter mAssetRecycleAdapter;
    private ArrayList<AssetInfoDescription> mArrayAssetInfo;
    private LinearLayout mVolumeUpLayout;
    private SeekBar mVideoVoiceSeekBar;
    private SeekBar mMusicVoiceSeekBar;
    private SeekBar mDubbingSeekBarSeekBar;
    private TextView mVideoVoiceSeekBarValue;
    private TextView mMusicVoiceSeekBarValue;
    private TextView mDubbingSeekBarSeekBarValue;
    private ImageView mSetVoiceFinish;
    private RelativeLayout mCompilePage;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicTrack;
    private NvsAudioTrack mRecordAudioTrack;
    private VideoFragment mVideoFragment;
    private CompileVideoFragment mCompileVideoFragment;
    private boolean m_waitFlag = false;
    private long mThemeClipDuration;

    private NvsTimelineEditor mTimelineEditor;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;
    private TextView mPlayCurTime;
    private List<TimeSpanInfo> mTimeSpanInfoList = new ArrayList<>();
    private CaptionHandler m_handler = new CaptionHandler(this);
    private NvsTimelineCaption mCurCaption;
    private ArrayList<CaptionInfo> mCaptionDataListClone;
    private boolean mIsSeekTimeline = true;
    private boolean mIsPlaying = false;
    private boolean mAddKeyFrame;
    private long startTimePoint, curTimeStamp;

    private RecyclerView rcvFilter;
    private ArrayList<FilterItem> mFilterItemArrayList;
    private NvAssetManager mAssetManager;
    protected VideoClipFxInfo mVideoClipFxInfo;
    private int mAssetType = NvAsset.ASSET_FILTER;
    private GlitchAdapter mGlitchAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int mSelectedPos = 0;
    private Button mPlayBtn;
    private RelativeLayout mPlayBtnLayout;

    private static final int GLITCH = 441;
    private static final int FILTER = 442;

    private int filterSelType = GLITCH;

    private List<FiltersModel> filters = new ArrayList<>();

    int[] videoEditImageId = {
        R.mipmap.icon_glitch,
        R.mipmap.icon_edit_filter,
        R.mipmap.icon_edit_caption,
        R.mipmap.icon_compound_caption,
        R.mipmap.icon_edit_sticker,
        R.mipmap.icon_edit_music,
        R.mipmap.icon_edit_animation,
        R.mipmap.icon_edit_transition,
        R.mipmap.icon_watermark
    };

    @Override
    protected int initRootView() {
        return R.layout.activity_video_edit2;
    }

    @Override
    protected void initViews() {
        mTitleBar = findViewById(R.id.title_bar);
        mAssetRecycleView = findViewById(R.id.assetRecycleList);
        mBottomLayout = findViewById(R.id.bottomLayout);
        mVolumeUpLayout = findViewById(R.id.volumeUpLayout);
        mVideoVoiceSeekBar = findViewById(R.id.videoVoiceSeekBar);
        mMusicVoiceSeekBar = findViewById(R.id.musicVoiceSeekBar);
        mDubbingSeekBarSeekBar = findViewById(R.id.dubbingSeekBar);
        mVideoVoiceSeekBarValue = findViewById(R.id.videoVoiceSeekBarValue);
        mMusicVoiceSeekBarValue = findViewById(R.id.musicVoiceSeekBarValue);
        mDubbingSeekBarSeekBarValue = findViewById(R.id.dubbingSeekBarValue);
        mSetVoiceFinish = findViewById(R.id.finish);
        mCompilePage = findViewById(R.id.compilePage);

        mTimelineEditor = findViewById(R.id.nvs_timeline_editor);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();
        mPlayCurTime = findViewById(R.id.play_cur_time);

        rcvFilter = findViewById(R.id.rcv_filters);
        mPlayBtn = findViewById(R.id.play_btn);
        mPlayBtnLayout = findViewById(R.id.play_btn_layout);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.videoEdit);
        mTitleBar.setTextRight(R.string.compile);
        mTitleBar.setTextRightVisible(View.VISIBLE);
    }

    @Override
    protected void initData() {

        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }

        mStreamingContext = NvsStreamingContext.getInstance();
        mStreamingContext.setDefaultCaptionFade(false);

        mCaptionDataListClone = TimelineData.instance().cloneCaptionData();

        filters = TimelineData.instance().getRuntimeFilters();

        initVideoFragment();
        updatePlaytimeText(0);
        initMultiSequence();
        addAllTimeSpan();
        initAssetManager();
        initFilterRecyclerView();
        initCompileVideoFragment();
        initAssetInfo();
        initAssetRecycleAdapter();
        initVoiceSeekBar();
        loadVideoClipFailTips();
    }

    private void initAssetManager() {
        initCaptionInfo();

        mVideoClipFxInfo = initClipFxInfo();
        mAssetManager = getNvAssetManager();
        mAssetManager.searchLocalAssets(mAssetType);

        mAssetType = NvAsset.ASSET_FILTER;

        initFilterDataList();
    }

    private void initCaptionInfo(){
        if (mTimeline == null) {
            return ;
        }

        TimelineUtil.applyTheme(mTimeline, null);
        /*
         * 移除主题，则需要删除字幕，然后重新添加，防止带片头主题删掉字幕
         * To remove a topic, you need to delete the subtitle and then add it again to prevent the title from deleting the subtitle
         * */
        TimelineUtil.setCaption(mTimeline, mCaptionDataListClone);
    }

    private void initFilterDataList() {
        if(filterSelType == GLITCH){
            String bundlePath = "glitch";
            mAssetManager.searchReservedAssets(mAssetType, bundlePath);
            mFilterItemArrayList = AssetFxUtil.getGlitchFilterData(this,
                    getLocalData(), null,true,true);
        }else{
            String bundlePath = "filter";
            mAssetManager.searchReservedAssets(mAssetType, bundlePath);
            mFilterItemArrayList = AssetFxUtil.getFilterData(this,
                    getLocalData(), null,true,true);
        }
    }

    private ArrayList<NvAsset> getLocalData() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private VideoClipFxInfo initClipFxInfo() {
        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        if (videoClipFxData == null) {
            videoClipFxData = new VideoClipFxInfo();
        }
        return videoClipFxData;
    }

    private void processFiltering(int position){
        mGlitchAdapter.setSelectPosAndRefresh(position);

        int count = mFilterItemArrayList.size();
        if (position < 0 || position >= count) {
            return;
        }
        if (mSelectedPos != position) {
            if (mVideoClipFxInfo.getKeyFrameInfoMap().isEmpty()) {
                // 没有关键帧 直接替换
                mSelectedPos = position;
                if (mSelectedPos == 0) {
                    mVideoClipFxInfo.setFxMode(FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(null);
                } else {
                    FilterItem filterItem = mFilterItemArrayList.get(mSelectedPos);
                    int filterMode = filterItem.getFilterMode();
                    if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                        String filterName = filterItem.getFilterName();
                        mVideoClipFxInfo.setFxMode(FXMODE_BUILTIN);
                        mVideoClipFxInfo.setFxId(filterName);
                        mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                        mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                        mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
                    } else {
                        String packageId = filterItem.getPackageId();
                        mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                        mVideoClipFxInfo.setFxId(packageId);
                    }
                    mVideoClipFxInfo.setFxIntensity(1.0f);
                }
                onFilterChanged(mTimeline, mVideoClipFxInfo);
                if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    long endTime = mTimeline.getDuration();
                    mVideoFragment.playVideo(startTime, endTime);
                }
            }
        }
    }

    public void initFilterRecyclerView() {
        mSelectedPos = AssetFxUtil.getSelectedFilterPos(mFilterItemArrayList, mVideoClipFxInfo);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mGlitchAdapter = new GlitchAdapter(this);
        mGlitchAdapter.setFilterDataList(mFilterItemArrayList);
        mGlitchAdapter.setOnItemClickListener(new GlitchAdapter.OnItemClickListener() {
            @Override
            public void onItemClickDown(int position) {
                long curTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);

                filters = TimelineData.instance().getRuntimeFilters();
                int count = filters.size();

                if(count == 0){
                    FiltersModel filter = new FiltersModel();
                    filter.filterIndex = position;
                    filter.startTimeStamp = 0;
                    filter.endTimeTimeStamp = mTimeline.getDuration();
                    filters.add(filter);

                    TimelineData.instance().setRuntimeFilters(filters);

                }
                else if(count == 1){
                    filters.get(0).endTimeTimeStamp = curTime;

                    FiltersModel filter = new FiltersModel();
                    filter.filterIndex = position;
                    filter.startTimeStamp = curTime + 1;
                    filter.endTimeTimeStamp = mTimeline.getDuration();
                    filters.add(filter);

                    TimelineData.instance().setRuntimeFilters(filters);
                }else{
                    int index = 1; boolean chk = false;
                    for(int i = 0; i < filters.size(); i++){
                        if(!chk){
                            if(curTime >= filters.get(i).startTimeStamp && filters.get(i).endTimeTimeStamp >= curTime){
                                index = i;
                                chk = true;
                            }
                        }
                        if(chk){
                            if(i > index){
                                filters.remove(i);
                            }
                        }
                    }
                    filters.get(index).endTimeTimeStamp = curTime;

                    FiltersModel filter = new FiltersModel();
                    filter.filterIndex = position;
                    filter.startTimeStamp = curTime + 1;
                    filter.endTimeTimeStamp = mTimeline.getDuration();
                    filters.add(filter);

                    TimelineData.instance().setRuntimeFilters(filters);
                }

                processFiltering(position);
            }

            @Override
            public void onItemClickUp(int position) {

            }
        });

        rcvFilter.setLayoutManager(mLinearLayoutManager);
        rcvFilter.setAdapter(mGlitchAdapter);
    }

    private void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter) {
        TimelineUtil.buildTimelineFilter(timeline, changedClipFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
        if (mTimeline != null) {
            mMusicTrack = mTimeline.getAudioTrackByIndex(0);
            mRecordAudioTrack = mTimeline.getAudioTrackByIndex(1);
        }
        filters = TimelineData.instance().getRuntimeFilters();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getInstance().finishActivity();
    }

    private void loadVideoClipFailTips() {
        /*
         * 导入视频无效，提示
         * The imported video is invalid
         * */
        if (mTimeline == null || (mTimeline.getDuration() <= 0)) {
            String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
            Util.showDialog(VideoEditActivity2.this, versionName[0], versionName[1], new TipsButtonClickListener() {
                @Override
                public void onTipsButtoClick(View view) {
                    removeTimeline();
                    AppManager.getInstance().finishActivity();
                }
            });
        }
    }

    /**
     * 清空数据
     * Clear data
     */
    private void clearData() {
        TimelineData.instance().clear();
        BackupData.instance().clear();
        BitmapData.instance().clear();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    private void initVoiceSeekBar() {
        mVideoVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mMusicVoiceSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        mDubbingSeekBarSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        if (mVideoTrack == null) {
            return;
        }
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateVideoVoiceSeekBar(volumeVal);
        updateMusicVoiceSeekBar(volumeVal);
        updateDubbingVoiceSeekBar(volumeVal);
    }

    private void updateVideoVoiceSeekBar(int volumeVal) {
        mVideoVoiceSeekBar.setProgress(volumeVal);
        mVideoVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateMusicVoiceSeekBar(int volumeVal) {
        mMusicVoiceSeekBar.setProgress(volumeVal);
        mMusicVoiceSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void updateDubbingVoiceSeekBar(int volumeVal) {
        mDubbingSeekBarSeekBar.setProgress(volumeVal);
        mDubbingSeekBarSeekBarValue.setText(String.valueOf(volumeVal));
    }

    private void setVideoVoice(int voiceVal) {
        if (mVideoTrack == null) {
            return;
        }
        updateVideoVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mVideoTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setOriginVideoVolume(volumeVal);
    }

    private void setMusicVoice(int voiceVal) {
        if (mMusicTrack == null) {
            return;
        }
        updateMusicVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mMusicTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setMusicVolume(volumeVal);
    }

    private void setDubbingVoice(int voiceVal) {
        if (mRecordAudioTrack == null) {
            return;
        }
        updateDubbingVoiceSeekBar(voiceVal);
        float volumeVal = voiceVal * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
        mRecordAudioTrack.setVolumeGain(volumeVal, volumeVal);
        TimelineData.instance().setRecordVolume(volumeVal);
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setAutoPlay(false);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", true);
        bundle.putBoolean("voiceButtonVisible", true);
        mVideoFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().add(R.id.video_layout, mVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    private void initCompileVideoFragment() {
        mCompileVideoFragment = new CompileVideoFragment();
        mCompileVideoFragment.setTimeline(mTimeline);
        getFragmentManager().beginTransaction().add(R.id.compilePage, mCompileVideoFragment).commit();
        getFragmentManager().beginTransaction().show(mCompileVideoFragment);
    }

    private void initAssetInfo() {
        mArrayAssetInfo = new ArrayList<>();
        String[] assetName = getResources().getStringArray(R.array.videoEdit);
        for (int i = 0; i < assetName.length; i++) {
            mArrayAssetInfo.add(new AssetInfoDescription(assetName[i], videoEditImageId[i]));
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoEditActivity2.this, LinearLayoutManager.HORIZONTAL, false);
        mAssetRecycleView.setLayoutManager(layoutManager);
        mAssetRecycleAdapter = new AssetRecyclerViewAdapter(VideoEditActivity2.this);
        mAssetRecycleAdapter.updateData(mArrayAssetInfo);
        mAssetRecycleView.setAdapter(mAssetRecycleAdapter);
        mAssetRecycleView.addItemDecoration(new SpaceItemDecoration(8, 8));
        mAssetRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (m_waitFlag) {
                    return;
                }
                mStreamingContext.stop();
                String tag = (String) view.getTag();
                if (tag.equals(getStringResourse(R.string.glitch))) {
                    filterSelType = GLITCH;
//                    onItemClickToActivity(FilterActivity.class, VideoEditActivity2.REQUESTRESULT_GLITCH);
                } else if (tag.equals(getStringResourse(R.string.filter))) {
                    filterSelType = FILTER;
                    onItemClickToActivity(FilterActivity.class, VideoEditActivity2.REQUESTRESULT_FILTER);
                } else if (tag.equals(getStringResourse(R.string.animatedSticker))) {
                    onItemClickToActivity(AnimateStickerActivity.class, VideoEditActivity2.REQUESTRESULT_STICKER);
                } else if (tag.equals(getStringResourse(R.string.animation))) {
                    onItemClickToActivity(AnimationActivity.class, VideoEditActivity2.REQUESTRESULT_ANIMATION);
                } else if (tag.equals(getStringResourse(R.string.caption))) {
                    onItemClickToActivity(CaptionActivity.class, VideoEditActivity2.REQUESTRESULT_CAPTION);
                } else if (tag.equals(getStringResourse(R.string.comcaption))) {
                    onItemClickToActivity(CompoundCaptionActivity.class, VideoEditActivity2.REQUESTRESULT_COMPOUND_CAPTION);
                } else if (tag.equals(getStringResourse(R.string.background))) {
                    onItemClickToActivity(BackgroundActivity.class, VideoEditActivity2.REQUESTRESULT_BACKGROUND);
                } else if (tag.equals(getStringResourse(R.string.watermark))) {
                    onItemClickToActivity(WaterMarkActivity.class, VideoEditActivity2.REQUESTRESULT_WATERMARK);
                } else if (tag.equals(getStringResourse(R.string.transition))) {
                    if (mTimeline != null) {
                        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
                        if (videoTrack != null) {
                            int clipCount = videoTrack.getClipCount();
                            if (clipCount <= 1) {
                                String[] transitionTipsInfo = getResources().getStringArray(R.array.transition_tips);
                                Util.showDialog(VideoEditActivity2.this, transitionTipsInfo[0], transitionTipsInfo[1]);
                                return;
                            }
                        }
                    }
                    onItemClickToActivity(TransitionActivity.class, VideoEditActivity2.REQUESTRESULT_TRANSITION);
                } else if (tag.equals(getStringResourse(R.string.music))) {
                    onItemClickToActivity(MusicActivity.class, VideoEditActivity2.REQUESTRESULT_MUSIC);
                } else {
                    String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                    Util.showDialog(VideoEditActivity2.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
                }
            }
        });
    }

    private void onItemClickToActivity(Class<? extends Activity> cls, int requstcode) {
        m_waitFlag = true;
        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), cls, null, requstcode);
    }

    private String getStringResourse(int id) {
        return getApplicationContext().getResources().getString(id);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mPlayBtn.setOnClickListener(this);

        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {

            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {
                prepareCompile();
            }
        });

        mVideoVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVideoVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMusicVoiceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setMusicVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mDubbingSeekBarSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setDubbingVoice(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSetVoiceFinish.setOnClickListener(this);

        mCompilePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        if (mCompileVideoFragment != null) {
            mCompileVideoFragment.setCompileVideoListener(new CompileVideoFragment.OnCompileVideoListener() {
                @Override
                public void compileProgress(NvsTimeline timeline, int progress) {
                    int progressInt = progress;
                }

                @Override
                public void compileFinished(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileFailed(NvsTimeline timeline) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                    mCompilePage.setVisibility(View.GONE);
                }

                @Override
                public void compileVideoCancel() {
                    mCompilePage.setVisibility(View.GONE);
                }
            });
        }

        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                if (timeStamp < 0) {
                    return;
                }

                CaptionInfo captionInfo;
                Map<Long, KeyFrameInfo> keyFrameMap = null;
                if (mCurCaption != null) {
                    captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        keyFrameMap = captionInfo.getKeyFrameInfo();
                        if (!mIsPlaying) {
                            updateKeyFrameView(timeStamp, keyFrameMap);
                        }
                    }
                }
                if (!mIsSeekTimeline) {
                    return;
                }
                if (mCurCaption != null && (timeStamp > mCurCaption.getOutPoint() + 1000 || timeStamp < mCurCaption.getInPoint() - 1000)) {
                    // 当前字幕时间之外隐藏框 禁用所有关键帧按钮
                    mVideoFragment.setDrawRectVisible(View.GONE);
                } else if (mCurCaption != null) {
                    // seek到关键帧的位置的时候，选中一下当前的关键帧，使以后所有的操作，在当前帧下操作.
                    boolean hasKeyFrame = false;
                    long currentKeyFrameStamp = -1;
                    if (keyFrameMap != null) {
                        for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
                            if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                                hasKeyFrame = true;
                                currentKeyFrameStamp = entry.getKey();
                                break;
                            }
                        }
                    }
                    if (hasKeyFrame) {
                        mCurCaption.setCurrentKeyFrameTime(currentKeyFrameStamp - mCurCaption.getInPoint());
                    }
                }
                if (mTimeline != null) {
                    updatePlaytimeText(timeStamp);
                    seekTimeline(timeStamp);
                }
            }
        });

        mMultiSequenceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mVideoFragment.setDrawRectVisible(View.GONE);
                        mTimelineEditor.unSelectAllTimeSpan();
                        selectCaption();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
//                        handlePlayStop();
                        return true;
                }
                mIsSeekTimeline = true;
                return false;
            }
        });

        if (mVideoFragment != null) {
            mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
                @Override
                public void onVideoVolume() {
                    mVolumeUpLayout.setVisibility(View.VISIBLE);
                }
            });

            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    handlePlayStop();
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    updatePlaytimeText(stamp);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mTimelineEditor.unSelectAllTimeSpan();
                    selectCaption();
                    if (mMultiSequenceView != null) {
                        int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
                        mMultiSequenceView.smoothScrollTo(x, 0);
                    }
                }

                @Override
                public void streamingEngineStateChanged(int state) {
                    if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                        mIsSeekTimeline = false;
                        mIsPlaying = true;
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                    } else {
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                        mIsSeekTimeline = true;
                        mIsPlaying = false;
                        if (mCurCaption != null) {
                            CaptionInfo captionInfo = getCurrentCaptionInfo();
                            if (captionInfo != null) {
                                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                                updateKeyFrameView(mStreamingContext.getTimelineCurrentPosition(mTimeline), keyFrameInfo);
                            }
                        }
                    }
                }
            });
        }

        mVolumeUpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void prepareCompile() {

//        List<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
//        filters = TimelineData.instance().getRuntimeFilters();
//
//        if(filters.size() == 1){
//            for(int i=0; i<clipInfos.size(); i++){
//                clipInfos.get(i).setVideoClipFxInfo(setVideoClipFxInfo(filters.get(0).filterIndex));
//            }
//        }else{
//            long[] asixs = new long[filters.size()+1];
//            asixs[0] = 0;
//            for(int i=0; i<filters.size(); i++){
//                asixs[i+1] = filters.get(i).endTimeTimeStamp;
//            }
//
//            for(int i = 1; i < asixs.length; i++){
//                for(int j = 0; j < clipInfos.size(); j++){
//
//                    if(asixs[i] == clipInfos.get(j).getTrimOut()){
//                        if(asixs[i-1] == clipInfos.get(j).getTrimIn()){
//                            clipInfos.get(j).setVideoClipFxInfo(setVideoClipFxInfo(filters.get(i-1).filterIndex));
//                        }else{
//                            if(asixs[i-1] > clipInfos.get(j).getTrimIn()){
//
//                            }else{
//
//                            }
//                        }
//                    }else if(asixs[i] < clipInfos.get(j).getTrimOut()){
//                        if(asixs[i-1] == clipInfos.get(j).getTrimIn()){
//                            long newSpiltPoint = asixs[i];
//                            ClipInfo clipInfoFst = getNewClipInfo(clipInfos.get(j));
//                            clipInfoFst.changeTrimOut(newSpiltPoint);
//                            clipInfoFst.setVideoClipFxInfo(setVideoClipFxInfo(filters.get(i-1).filterIndex));
//
//                            ClipInfo clipInfoSec = getNewClipInfo(clipInfos.get(j));
//                            clipInfoSec.changeTrimIn(newSpiltPoint);
//
//                            clipInfos.remove(i);
//                            clipInfos.add(i, clipInfoSec);
//                            clipInfos.add(i, clipInfoFst);
//                        }else if(asixs[i-1] > clipInfos.get(j).getTrimIn()){
//
//                        }else{
//
//                        }
//                        long newSpiltPoint = asixs[i];
//                        ClipInfo clipInfoFst = getNewClipInfo(clipInfos.get(j));
//                        clipInfoFst.changeTrimIn(newSpiltPoint);
//                        clipInfoFst.changeTrimOut(asixs[j-1]);
//                        clipInfoFst.setVideoClipFxInfo(setVideoClipFxInfo(filters.get(asixIndex-1).filterIndex));
//
//                        ClipInfo clipInfoSec = getNewClipInfo(clipInfos.get(i));
//                        clipInfoSec.changeTrimIn(newSpiltPoint);
//                        clipInfoFst.setVideoClipFxInfo(setVideoClipFxInfo(filters.get(j).filterIndex));
//
//                        clipInfos.remove(i);
//                        clipInfos.add(i, clipInfoSec);
//                        clipInfos.add(i, clipInfoFst);
//                    }else{
//
//                    }
//                }
//            }
//        }
//
//        mClipArrayList.remove(mCurClipIndex);
//        mClipArrayList.add(mCurClipIndex, clipInfoSec);
//        mClipArrayList.add(mCurClipIndex, clipInfoFst);
//        Bitmap bitmap = Util.getBitmapFromClipInfo(this, clipInfoSec);
//        BitmapData.instance().insertBitmap(mCurClipIndex + 1, bitmap);
//        BackupData.instance().setClipInfoData(mClipArrayList);
//        removeTimeline();
//        Intent intent = new Intent();
//        intent.putExtra("spiltPosition", mCurClipIndex);
//        setResult(RESULT_OK, intent);
//        AppManager.getInstance().finishActivity();

        mCompilePage.setVisibility(View.VISIBLE);
        mCompileVideoFragment.compileVideo();
    }

    private List<ClipInfo> splitClipInfo(long in, long splitPoint, long out, ClipInfo clip, int firstClipFxIndex){
        List<ClipInfo> infos = new ArrayList<>();

        ClipInfo clipInfoFst = getNewClipInfo(clip);
        clipInfoFst.changeTrimIn(in);
        clipInfoFst.changeTrimOut(splitPoint);
        clipInfoFst.setVideoClipFxInfo(setVideoClipFxInfo(filters.get(firstClipFxIndex).filterIndex));

        ClipInfo clipInfoSec = getNewClipInfo(clip);
        clipInfoSec.changeTrimIn(splitPoint);
        clipInfoSec.changeTrimOut(out);
        clipInfoFst.setVideoClipFxInfo(setVideoClipFxInfo(filters.get(firstClipFxIndex+1).filterIndex));

        return infos;
    }

    private VideoClipFxInfo setVideoClipFxInfo(int filterIndex){
        VideoClipFxInfo _mVideoClipFxInfo = new VideoClipFxInfo();
        if (filterIndex == 0) {
            _mVideoClipFxInfo.setFxMode(FXMODE_BUILTIN);
            _mVideoClipFxInfo.setFxId(null);
        } else {
            FilterItem filterItem = mFilterItemArrayList.get(filterIndex);
            int filterMode = filterItem.getFilterMode();
            if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                String filterName = filterItem.getFilterName();
                _mVideoClipFxInfo.setFxMode(FXMODE_BUILTIN);
                _mVideoClipFxInfo.setFxId(filterName);
                _mVideoClipFxInfo.setIsCartoon(filterItem.getIsCartoon());
                _mVideoClipFxInfo.setGrayScale(filterItem.getGrayScale());
                _mVideoClipFxInfo.setStrokenOnly(filterItem.getStrokenOnly());
            } else {
                String packageId = filterItem.getPackageId();
                _mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                _mVideoClipFxInfo.setFxId(packageId);
            }
            _mVideoClipFxInfo.setFxIntensity(1.0f);
        }
        return _mVideoClipFxInfo;
    }

    private ClipInfo getNewClipInfo(ClipInfo _newClipInfo) {
        ClipInfo newClipInfo = new ClipInfo();
        newClipInfo.isRecFile = _newClipInfo.isRecFile;
        newClipInfo.rotation = _newClipInfo.rotation;
        newClipInfo.setFilePath(_newClipInfo.getFilePath());
        newClipInfo.changeTrimIn(_newClipInfo.getTrimIn());
        newClipInfo.changeTrimOut(_newClipInfo.getTrimOut());
        newClipInfo.setMute(_newClipInfo.getMute());
        newClipInfo.setSpeed(_newClipInfo.getSpeed());
        newClipInfo.setKeepAudioPitch(_newClipInfo.isKeepAudioPitch());
        newClipInfo.setmCurveSpeed(_newClipInfo.getmCurveSpeed());
        newClipInfo.setBrightnessVal(_newClipInfo.getBrightnessVal());
        newClipInfo.setSaturationVal(_newClipInfo.getSaturationVal());
        newClipInfo.setContrastVal(_newClipInfo.getContrastVal());
        newClipInfo.setmHighLight(_newClipInfo.getmHighLight());
        newClipInfo.setmShadow(_newClipInfo.getmShadow());
        newClipInfo.setTemperature(_newClipInfo.getTemperature());
        newClipInfo.setTint(_newClipInfo.getTint());
        newClipInfo.setFade(_newClipInfo.getFade());
        newClipInfo.setDensity(_newClipInfo.getDensity());
        newClipInfo.setDenoiseDensity(_newClipInfo.getDenoiseDensity());

        return newClipInfo;
    }

    private CaptionInfo getCurrentCaptionInfo() {
        if (mCurCaption == null) {
            return null;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }

    private int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mCaptionDataListClone.get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void updateKeyFrameView(long timeStamp, Map<Long, KeyFrameInfo> keyFrameMap) {
        if (keyFrameMap == null || keyFrameMap.isEmpty()) {

        } else {
            Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameMap.entrySet();
            Set<Long> keyFrameKeySet = keyFrameMap.keySet();
            Object[] objects = keyFrameKeySet.toArray();
            //上一帧
            long beforeKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key < timeStamp) {
                    // 找到距离当前位置 向前最近的一个时间点
                    beforeKeyFrame = key;
                }
            }
            if (beforeKeyFrame == -1 || ((objects != null) && ((long) (objects[0]) == timeStamp))) {

            } else {

            }

            // 下一帧
            long nextKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key > timeStamp) {
                    // 找到距离当前位置 向后最近的一个时间点
                    nextKeyFrame = key;
                    break;
                }
            }

            if (nextKeyFrame == -1 || ((objects != null) && ((long) (objects[objects.length - 1]) == timeStamp))) {

            } else {

            }

            // 增加或者删除
            boolean hasKeyFrame = false;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                    hasKeyFrame = true;
                    break;
                }
            }
            // keyFramePoint
            TimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                if (mAddKeyFrame) {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, keyFrameMap);
                } else {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, null);
                }
            }
        }
    }

    private void updatePlaytimeText(long playTime) {
        curTimeStamp = playTime;
        if (mTimeline != null) {
            long totalDuaration = mTimeline.getDuration();
            String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
            String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
            StringBuilder mShowCurrentDuration = new StringBuilder();
            mShowCurrentDuration.setLength(0);
            mShowCurrentDuration.append(strCurrentDuration);
            mShowCurrentDuration.append("/");
            mShowCurrentDuration.append(strTotalDuration);
            mPlayCurTime.setText(mShowCurrentDuration.toString());
        }

        checkIncludeInStampArray(curTimeStamp);
    }

    private void checkIncludeInStampArray(long timestamp){
        Log.d("filter count ===> ", filters.size() + "");
        filters = TimelineData.instance().getRuntimeFilters();

        if(filters.size() == 0){
            FiltersModel filter = new FiltersModel();
            filter.filterIndex = mSelectedPos;
            filter.startTimeStamp = 0;
            filter.endTimeTimeStamp = mTimeline.getDuration();

            filters.add(filter);
        }
        else{
            for (FiltersModel model: filters) {
                if(timestamp >= model.startTimeStamp && timestamp <= model.endTimeTimeStamp){
                    if(model.filterIndex != mSelectedPos){
                        processFiltering(model.filterIndex);
                    }
                }
            }
        }
    }

    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null) {
                continue;
            }
            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }

        long duration = mTimeline.getDuration();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayBtnLayout.getLayoutParams();
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, 0);
    }

    private void playVideo() {
        if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long endTime = mTimeline.getDuration();
            mVideoFragment.playVideo(startTime, endTime);
        } else {
            mVideoFragment.stopEngine();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish) {
            mVolumeUpLayout.setVisibility(View.GONE);
        }else if(id == R.id.play_btn){
            playVideo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case REQUESTRESULT_GLITCH:
                break;
            case REQUESTRESULT_FILTER:
                VideoClipFxInfo videoClipFxInfo = TimelineData.instance().getVideoClipFxData();
                TimelineUtil.buildTimelineFilter(mTimeline, videoClipFxInfo);
                break;
            case REQUESTRESULT_STICKER:
                ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
                TimelineUtil.setSticker(mTimeline, stickerArray);
                break;
            case REQUESTRESULT_CAPTION:
                updateCaption(mThemeClipDuration);
                break;
            case REQUESTRESULT_COMPOUND_CAPTION:
                updateCompoundCaption();
                break;
            case REQUESTRESULT_TRANSITION:
                ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();
                if ((transitionInfoArray != null) && !transitionInfoArray.isEmpty()) {
                    TimelineUtil.setTransition(mTimeline, transitionInfoArray);
                }
                break;
            case REQUESTRESULT_MUSIC:
                List<MusicInfo> musicInfos = TimelineData.instance().getMusicData();
                TimelineUtil.buildTimelineMusic(mTimeline, musicInfos);
                break;
            case REQUESTRESULT_WATERMARK:
                Logger.e(TAG, "水印界面");
                TimelineUtil.checkAndDeleteExitFX(mTimeline);
                boolean cleanWaterMark = data.getBooleanExtra(WaterMarkActivity.WATER_CLEAN, true);
                if (cleanWaterMark) {
                    mTimeline.deleteWatermark();
                } else {
                    WaterMarkUtil.setWaterMark(mTimeline, TimelineData.instance().getWaterMarkData());
                }
                //添加临时解决水印中效果不能去除问题，导致问题的原因大概率为每次操作的都不是同一个timeline
                boolean hasEffect = data.getBooleanExtra(WaterMarkActivity.EFFECT_CLEAN, true);
                if (!hasEffect) {
                    NvsTimelineVideoFx lastFx = mTimeline.getLastTimelineVideoFx();
                    while (lastFx != null) {
                        String fxName = lastFx.getBuiltinTimelineVideoFxName();
                        if (TextUtils.equals(fxName, "Mosaic")
                                || TextUtils.equals(fxName, "Gaussian Blur")) {
                            mTimeline.removeTimelineVideoFx(lastFx);
                            break;
                        }
                        lastFx = mTimeline.getPrevTimelineVideoFx(lastFx);
                    }
                }
                VideoFx videoFx = TimelineData.instance().getVideoFx();
                mVideoFragment.setEffectByData(videoFx);
                mVideoFragment.refreshLiveWindowFrame();

                break;
            case REQUESTRESULT_ANIMATION:
                // TODO: 2020/8/25 设置动画
                TimelineUtil.buildTimelineAnimation(mTimeline, TimelineData.instance().getmAnimationFxMap());
                break;
        }
        mVideoFragment.updateTotalDuarationText();
    }

    private void updateCaption(long themeDuration) {
        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        TimelineUtil.setCaption(mTimeline, captionArray, themeDuration);
    }

    private void updateCompoundCaption() {
        ArrayList<CompoundCaptionInfo> captionArray = TimelineData.instance().getCompoundCaptionArray();
        TimelineUtil.setCompoundCaption(mTimeline, captionArray);
    }

    private void resetView() {
        updatePlaytimeText(0);
        seekTimeline(0);
        mMultiSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
    }

    private void handlePlayStop() {
        selectCaption();

        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
        }

        changeRectVisible();

        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }

    private void addAllTimeSpan() {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            int capCategory = caption.getCategory();
            int roleTheme = caption.getRoleInTheme();
            /*
             * capCategory值为0是默认字幕即未使用字幕样式的字幕，
             * 值为1表示是用户自定义种类即使用字幕样式的字幕，值为2是主题字幕
             * A capCategory value of 0 is the default caption, that is, a caption with no subtitle style.
             * A value of 1 is a user-defined category, that is, a caption with subtitle style.
             * A value of 2 is the theme caption
             * */
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作编辑处理
                caption = mTimeline.getNextCaption(caption);
                continue;
            }

            long inPoint = caption.getInPoint();
            long outPoint = caption.getOutPoint();
            NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);

            if (timeSpan != null) {
                TimeSpanInfo timeSpanInfo = new TimeSpanInfo(caption, timeSpan);
                timeSpanInfo.setTraditional(!caption.isModular());
                if (!timeSpanInfo.isTraditional) {
                    timeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
                }
                mTimeSpanInfoList.add(timeSpanInfo);
            }
            caption = mTimeline.getNextCaption(caption);
        }
    }

    private NvsTimelineTimeSpan addTimeSpan(long inPoint, long outPoint) {
        /*
         * warning: 使用addTimeSpanExt()之前必须设置setTimeSpanType()
         * warning: setTimeSpanType () must be set before using addTimeSpanExt ()
         * */
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        NvsTimelineTimeSpan timelineTimeSpan = mTimelineEditor.addTimeSpan(inPoint, outPoint);
        if (timelineTimeSpan == null) {
            Log.e(TAG, "addTimeSpan: " + " 添加TimeSpan失败!");
            return null;
        }
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {

                seekTimeline(timeStamp);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();

                NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (currentCaptionInfo != null && mAddKeyFrame) {
                    currentTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }

                if (isDragEnd && mCurCaption != null) {
                    Logger.e(TAG, "TrimInChange1212->" + timeStamp);
                    mCurCaption.changeInPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setInPoint(timeStamp);
                    }
                    seekMultiThumbnailSequenceView();

                    // 移动左边缘 松手之后 1.更新上层数据结构中记录的关键帧信息  2.更新底层关键帧位置信息
                    if (mCurCaption != null && currentCaptionInfo != null && mAddKeyFrame) {
                        // 1.step one
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentCaptionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        long currentTimeLinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            if (next.getKey() < currentTimeLinePosition) {
                                iterator.remove();
                            }
                        }
                        if (mAddKeyFrame) {
                            currentTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                        }
                        // 2.step two 底层移除之前添加的关键帧信息 根据上层数据结构重新添加关键帧
                        boolean removeStickerTransXSuccess = mCurCaption.removeAllKeyframe(TRANS_X);
                        boolean removeStickerTransYSuccess = mCurCaption.removeAllKeyframe(TRANS_Y);
                        boolean removeStickerScaleX = mCurCaption.removeAllKeyframe(SCALE_X);
                        boolean removeStickerScaleY = mCurCaption.removeAllKeyframe(SCALE_Y);
                        boolean removeStickerRotZ = mCurCaption.removeAllKeyframe(ROTATION_Z);
                        if (removeStickerTransXSuccess && removeStickerTransYSuccess && removeStickerScaleX && removeStickerScaleY && removeStickerRotZ) {
                            Log.d(TAG, "timelineTimeSpan.setOnChangeListener onChangeLeft  removeAllKeyframe success");
                        }
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = currentCaptionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                        for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                            KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                            mCurCaption.setCurrentKeyFrameTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_X, captionKeyFrameInfo.getTranslation().x, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_Y, captionKeyFrameInfo.getTranslation().y, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_X, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_Y, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(ROTATION_Z, captionKeyFrameInfo.getRotationZ(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                        }
                    }

                }
                changeRectVisible();
            }
        });

        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                /*
                 * outPoint是开区间，seekTimeline时，需要往前平移一帧即0.04秒，转换成微秒即40000微秒
                 * outPoint is an open interval. In seekTimeline, you need to pan one frame, that is, 0.04 seconds, and convert it to microseconds, that is, 40,000 microseconds.
                 * */
                seekTimeline(timeStamp - 40000);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                if (isDragEnd && mCurCaption != null) {
                    Logger.e(TAG, "TrimInChange5454->" + timeStamp);
                    mCurCaption.changeOutPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOutPoint(timeStamp);
                    }
                    seekMultiThumbnailSequenceView();

                    // 若覆盖了关键帧 则移除覆盖的关键帧信息
                    CaptionInfo captionInfo = getCurrentCaptionInfo();
                    if (captionInfo != null && mAddKeyFrame) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            // 这里比较的是timeline上的时间
                            if (next.getKey() > mStreamingContext.getTimelineCurrentPosition(mTimeline)) {
                                iterator.remove();
                            }
                        }

                        // 2.step two 底层移除之前添加的关键帧信息 根据上层数据结构重新添加关键帧
                        boolean removeStickerTransXSuccess = mCurCaption.removeAllKeyframe(TRANS_X);
                        boolean removeStickerTransYSuccess = mCurCaption.removeAllKeyframe(TRANS_Y);
                        boolean removeStickerScaleX = mCurCaption.removeAllKeyframe(SCALE_X);
                        boolean removeStickerScaleY = mCurCaption.removeAllKeyframe(SCALE_Y);
                        boolean removeStickerRotZ = mCurCaption.removeAllKeyframe(ROTATION_Z);
                        if (removeStickerTransXSuccess && removeStickerTransYSuccess && removeStickerScaleX && removeStickerScaleY && removeStickerRotZ) {
                            Log.d(TAG, "timelineTimeSpan.setOnChangeListener onChangeLeft  removeAllKeyframe success");
                        }
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                        for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                            KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                            mCurCaption.setCurrentKeyFrameTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_X, captionKeyFrameInfo.getTranslation().x, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_Y, captionKeyFrameInfo.getTranslation().y, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_X, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_Y, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(ROTATION_Z, captionKeyFrameInfo.getRotationZ(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                        }
                    }
                }

                changeRectVisible();
            }
        });

        return timelineTimeSpan;
    }

    private NvsTimelineTimeSpan getCurrentTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                return mTimeSpanInfoList.get(i).mTimeSpan;
            }
        }
        return null;
    }

    private void seekMultiThumbnailSequenceView() {
        if (mMultiSequenceView != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    private void selectTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            TimeSpanInfo captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (mCurCaption != null && captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    NvsTimelineTimeSpan timeSpan = mTimeSpanInfoList.get(i).mTimeSpan;
                    if (timeSpan != null) {
                        mTimelineEditor.selectTimeSpan(timeSpan);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 这个方法修改
     * 1.添加移除关键帧的操作是为了播放时如果字幕移动的话，字幕外边框跟随移动
     * 2.现在播放过程中字幕移动外边框隐藏 不需要显示 取消关键帧操作
     */
    private void changeRectVisible() {
        if (mCurCaption != null) {
            boolean addAndDelete = true;
            if (mCurCaption != null) {
                CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                if (captionInfo != null) {
                    Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                    if (keyFrameInfo == null || keyFrameInfo.isEmpty()) {
                        addAndDelete = false;
                    }
                }
            }
            if (addAndDelete) {
                long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                mVideoFragment.setCurCaption(mCurCaption);
                mVideoFragment.updateCaptionCoordinate(mCurCaption);
                mVideoFragment.changeCaptionRectVisible();
                mCurCaption.setCurrentKeyFrameTime(duration);
                mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
                mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
                mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
                mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
                mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);
            }
        }
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        mVideoFragment.changeCaptionRectVisible();
    }

    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();

        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal >= zVal) {
                    index = i;
                    break;
                }
            }
            TimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }
            if (mAddKeyFrame) {
                if (mCurCaption == null) {
                    mCurCaption = captionList.get(index);
                }
            } else {
                mCurCaption = captionList.get(index);
            }

            if (mAddKeyFrame) {
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                spanInfo = getCurrentTimeSpanInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
            }

            if (mCurCaption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                    && mCurCaption.getRoleInTheme() != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {
                mCurCaption = null;
            } else {
            }
        } else {
            TimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }
            mCurCaption = null;
        }
    }

    private TimeSpanInfo getCurrentTimeSpanInfo() {
        TimeSpanInfo captionTimeSpanInfo;
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    return captionTimeSpanInfo;
                }
            }
        }
        return null;
    }

    private CaptionInfo getCaptionInfo(int curZValue) {
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            CaptionInfo captionInfo = mCaptionDataListClone.get(i);
            if (captionInfo != null && captionInfo.getCaptionZVal() == curZValue) {
                return captionInfo;
            }
        }
        return null;
    }

    static class CaptionHandler extends Handler {
        WeakReference<VideoEditActivity2> mWeakReference;

        public CaptionHandler(VideoEditActivity2 activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final VideoEditActivity2 activity = mWeakReference.get();
            if (activity != null) {
                if (msg.what == VIDEOPLAYTOEOF) {
                    activity.resetView();
                }
            }
        }
    }

    private class TimeSpanInfo {
        private boolean isTraditional = true;
        public NvsTimelineCaption mCaption;
        public NvsTimelineTimeSpan mTimeSpan;

        public TimeSpanInfo(NvsTimelineCaption caption, NvsTimelineTimeSpan timeSpan) {
            this.mCaption = caption;
            this.mTimeSpan = timeSpan;
        }

        public boolean isTraditionanl() {
            return isTraditional;
        }

        public void setTraditional(boolean traditional) {
            isTraditional = traditional;
        }
    }
}



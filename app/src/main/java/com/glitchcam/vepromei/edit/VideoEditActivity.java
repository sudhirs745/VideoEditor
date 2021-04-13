package com.glitchcam.vepromei.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.edit.adapter.AssetRecyclerViewAdapter;
import com.glitchcam.vepromei.edit.adapter.SpaceItemDecoration;
import com.glitchcam.vepromei.edit.background.BackgroundActivity;
import com.glitchcam.vepromei.edit.clipEdit.adjust.AdjustActivity;
import com.glitchcam.vepromei.edit.clipEdit.speed.SpeedActivity;
import com.glitchcam.vepromei.edit.clipEdit.spilt.SpiltActivity;
import com.glitchcam.vepromei.edit.clipEdit.trim.TrimActivity;
import com.glitchcam.vepromei.edit.clipEdit.volume.VolumeActivity;
import com.glitchcam.vepromei.edit.data.AssetInfoDescription;
import com.glitchcam.vepromei.edit.data.BackupData;
import com.glitchcam.vepromei.edit.data.BitmapData;
import com.glitchcam.vepromei.edit.interfaces.OnGrallyItemClickListener;
import com.glitchcam.vepromei.edit.interfaces.OnItemClickListener;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.interfaces.TipsButtonClickListener;
import com.glitchcam.vepromei.selectmedia.SelectMediaActivity;
import com.glitchcam.vepromei.selectmedia.adapter.SelectedMediaDatasUI;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.TimelineUtil;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.dataInfo.AnimationInfo;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.glitchcam.vepromei.utils.dataInfo.TransitionInfo;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.glitchcam.vepromei.utils.Constants.POINT3V4;
import static com.glitchcam.vepromei.utils.Constants.POINT9V16;
import static com.glitchcam.vepromei.utils.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.glitchcam.vepromei.utils.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;

/**
 * VideoEditActivity class
 *
 * @author czl
 * @date 2018-05-28
 */
public class VideoEditActivity extends BaseActivity {

    /*
     * 裁剪
     * Clip trim
     * */
    public static final int CLIPTRIM_REQUESTCODE = 101;
    /*
     * 分割
     * Clip spilt
     * */
    public static final int CLIPSPILTPOINT_REQUESTCODE = 102;
    /*
     * 调整
     * Screen ratio
     * */
    public static final int CLIPADJUST_REQUESTCODE = 104;
    /*
     * 速度
     * speed
     * */
    public static final int CLIPSPEED_REQUESTCODE = 105;
    /*
     * 音量
     * volume
     * */
    public static final int CLIPVOLUME_REQUESTCODE = 106;
    /*
     * 添加视频
     * Add video
     * */
    public static final int ADDVIDEO_REQUESTCODE = 107;

    public static final int REQUESTRESULT_BACKGROUND = 1012;

    int[] videoEditImageId = {
            R.drawable.trim,
            R.drawable.ratio,
            R.drawable.copy,
            R.drawable.background,
            R.drawable.speed,
            R.drawable.division,
            R.drawable.volume,
    };

    String[] assetName;

    private AssetRecyclerViewAdapter mAssetRecycleAdapter;
    private ArrayList<AssetInfoDescription> mArrayAssetInfo;

    private ArrayList<TransitionInfo> mTransitionInfoArray;
    private ArrayList<ClipInfo> mClipInfoArray = new ArrayList<>();
    private int mAddVideoPostion = 0, mCurrentPos = 0;

    private RecyclerView selectedRecyclerView;
    private SelectedMediaDatasUI selectedClipUI;
    private ImageView ivAddMedia;

    private CustomTitleBar mTitleBar;

    private RelativeLayout mBottomLayout;
    private RecyclerView mAssetRecycleView;
    private LinearLayout mVolumeUpLayout;
    private SeekBar mVideoVoiceSeekBar;
    private SeekBar mMusicVoiceSeekBar;
    private SeekBar mDubbingSeekBarSeekBar;
    private TextView mVideoVoiceSeekBarValue;
    private TextView mMusicVoiceSeekBarValue;
    private TextView mDubbingSeekBarSeekBarValue;
    private ImageView mSetVoiceFinish;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicTrack;
    private NvsAudioTrack mRecordAudioTrack;
    private VideoFragment mVideoFragment;

    private ClipInfo mCurrentClipInfo;
    private NvsVideoClip mCurrentNvsVideoClip;
    private CardView cvNext;

    private boolean m_waitFlag = false;

    /**
     *
     * clip 对应的动画集合 当前添加或删除 等操作 需要同步操作这个集合 以保证数据的同步
     * 这个集合是当前页面级的
     */
    private ConcurrentHashMap<Integer, AnimationInfo> mVideoClipFxMap = new ConcurrentHashMap<>();

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_video_edit;
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

        ivAddMedia = findViewById(R.id.iv_add_media);
        selectedRecyclerView = findViewById(R.id.ve_selected_media_rv);
        cvNext = findViewById(R.id.cv_next);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.videoEdit);
        mTitleBar.setTextCenter("");
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

        NvsVideoClip nvsVideoClip = mVideoTrack.getClipByIndex(mCurrentPos);
        if (nvsVideoClip == null) {
            return;
        }
        mCurrentNvsVideoClip = nvsVideoClip;

        mTransitionInfoArray = TimelineData.instance().cloneTransitionsData();
        mClipInfoArray = TimelineData.instance().cloneClipInfoData();

        mCurrentPos = 0;
        BackupData.instance().setClipIndex(0);
        BackupData.instance().setClipInfoData(mClipInfoArray);

        setVideoClip();

        //clip 对应的动画集合
        ConcurrentHashMap<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        mVideoClipFxMap.putAll(fxMap);

        ivAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reAddMediaAsset();
            }
        });

        initVideoFragment();
        initAssetInfo();
        initAssetRecycleAdapter();
        initVoiceSeekBar();
        loadVideoClipFailTips();
        initSelectedRecyclerView();
    }

    private void setVideoClip() {
        if (mClipInfoArray.get(mCurrentPos) != null && mClipInfoArray.size() > 0) {
            mCurrentClipInfo = mClipInfoArray.get(mCurrentPos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
        if (mTimeline != null) {
            mMusicTrack = mTimeline.getAudioTrackByIndex(0);
            mRecordAudioTrack = mTimeline.getAudioTrackByIndex(1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeTimeline();
        clearData();
        AppManager.getInstance().finishActivity();
    }

    private void initSelectedRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedRecyclerView.setLayoutManager(linearLayoutManager);
        selectedClipUI = new SelectedMediaDatasUI(getApplicationContext(), new ArrayList<MediaData>(), mClipInfoArray, false, new SelectedMediaDatasUI.OnClickSelectedItem() {
            @Override
            public void onClickCellData(int position) {
                mCurrentPos = position;
                BackupData.instance().setClipIndex(mCurrentPos);

                mCurrentClipInfo = mClipInfoArray.get(position);

                if (position >= 0 && position < mClipInfoArray.size()) {
                    playCurrentClip(position);

                    NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(mCurrentPos);
                    if (videoTrack != null) {
                        mCurrentNvsVideoClip = videoTrack.getClipByIndex(position);
                    }

                    mVideoFragment.setVideoClipInfo(mCurrentClipInfo, mCurrentNvsVideoClip);
                    mVideoFragment.setTransformViewVisible(View.VISIBLE);
                }
            }

            @Override
            public void onClickRemoveData(int position) {}
        });
        selectedRecyclerView.setAdapter(selectedClipUI);

        ItemTouchHelper.Callback callback = new com.glitchcam.vepromei.edit.grallyRecyclerView.ItemTouchHelper(selectedClipUI);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(selectedRecyclerView);

        selectedClipUI.setOnItemSelectedListener(new OnGrallyItemClickListener() {
            @Override
            public void onLeftItemClick(View view, int pos) {}

            @Override
            public void onRightItemClick(View view, int pos) {}

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                mCurrentPos = toPosition;
                BackupData.instance().setClipIndex(mCurrentPos);
                mCurrentClipInfo = mClipInfoArray.get(mCurrentPos);

                Collections.swap(mClipInfoArray, fromPosition, toPosition);
                selectedClipUI.setClipInfoArray(mClipInfoArray);
                selectedClipUI.notifyDataSetChanged();
                swapAnimationInfo(fromPosition,toPosition);
            }

            @Override
            public void onItemDismiss(int position) {}

            @Override
            public void removeall() {}

        });
    }

    /**
     * 播放视频
     * Play video
     */
    private void playCurrentClip(int mSelectedClipPosition) {
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if (mSelectedClipPosition >= 0 && mSelectedClipPosition < clipCount) {
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            //播放时针对timeline 的
            if (playEndPoint > playStartPoint) {
                //除了第一条片段，其他片段延0.5s播放时间，从开始向前
                //设置进度条显示 0 - duration
                if (mSelectedClipPosition > 0) {
                    playStartPoint -= 0.5 * 1000 * 1000;
                    playEndPoint -= 0.5 * 1000 * 1000;
                }
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint, playEndPoint, playStartPoint, mTimeline.getDuration());
                mVideoFragment.playVideoButtonCilck(playStartPoint, playEndPoint);
            }
        }
        //播放全部视频
        else if (mSelectedClipPosition == -1) {
            playStartPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            playEndPoint = mTimeline.getDuration();
            //播放全部视频时 duration = end
            //设置进度条显示
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0, playEndPoint, playStartPoint, playEndPoint);
            if (playEndPoint > playStartPoint) {
                mVideoFragment.playVideoButtonCilck(playStartPoint, playEndPoint);
            }
        }
    }

    /**
     * 获取当前选择的片段的起始位置
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipStartTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            return 0;
        }
        long clipInPoint = nvsVideoClip.getInPoint();
        return clipInPoint;
    }

    /**
     * 获取当前选择的片段的起始位置
     *
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipEndTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            return 0;
        }
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return clipOutPoint;
    }

    private void loadVideoClipFailTips() {
        /*
         * 导入视频无效，提示
         * The imported video is invalid
         * */
        if (mTimeline == null || (mTimeline.getDuration() <= 0)) {
            String[] versionName = getResources().getStringArray(R.array.clip_load_failed_tips);
            Util.showDialog(VideoEditActivity.this, versionName[0], versionName[1], new TipsButtonClickListener() {
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
        mVideoFragment.setAutoPlay(true);
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

    private void initAssetInfo() {
        mArrayAssetInfo = new ArrayList<>();
        assetName = getResources().getStringArray(R.array.effectNamesVideo);
        for (int i = 0; i < assetName.length; i++) {
            mArrayAssetInfo.add(new AssetInfoDescription(assetName[i], videoEditImageId[i]));
        }
    }

    private void initAssetRecycleAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoEditActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mAssetRecycleView.setLayoutManager(layoutManager);
        mAssetRecycleAdapter = new AssetRecyclerViewAdapter(VideoEditActivity.this);
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

                switch (pos) {
                    case 0://trim
                        m_waitFlag = true;
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                TrimActivity.class, null, VideoEditActivity.CLIPTRIM_REQUESTCODE);
                        break;
                    case 1: // ratio
                        m_waitFlag = true;
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                AdjustActivity.class, null, VideoEditActivity.CLIPADJUST_REQUESTCODE);
                        break;
                    case 2: // copy media asset
                        m_waitFlag = true;
                        copyMediaAsset();
                        break;
                    case 3: // background
                        m_waitFlag = true;
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                BackgroundActivity.class, null, VideoEditActivity.REQUESTRESULT_BACKGROUND);
                        break;
                    case 4: // speed
                        m_waitFlag = true;
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                SpeedActivity.class, null, VideoEditActivity.CLIPSPEED_REQUESTCODE);
                        break;
                    case 5: // split
                        m_waitFlag = true;
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                SpiltActivity.class, null, VideoEditActivity.CLIPSPILTPOINT_REQUESTCODE);
                        break;
                    case 6: // volume
                        AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                VolumeActivity.class, null, VideoEditActivity.CLIPVOLUME_REQUESTCODE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void copyMediaAsset() {
        if (mClipInfoArray.size() == 0) {
            return;
        }

        int count = mClipInfoArray.size();
        if (mCurrentPos < 0 || mCurrentPos > count) {
            return;
        }
        mClipInfoArray.add(mCurrentPos, mClipInfoArray.get(mCurrentPos).clone());
        mCurrentPos++;
        BackupData.instance().setClipIndex(mCurrentPos);

        //添加一个动画的对象
        addNewAnimationInfo(true, mCurrentPos,1);

        /*
         * 添加转场
         * Add transition
         * */
        if (mTransitionInfoArray != null && mTransitionInfoArray.size() >= mCurrentPos) {
            mTransitionInfoArray.add(mCurrentPos, new TransitionInfo());
        }

        selectedClipUI.setSelectPos(mCurrentPos);
        selectedClipUI.setClipInfoArray(mClipInfoArray);
        selectedClipUI.notifyDataSetChanged();
        /*
         * 复制移动到下一个位置
         * Copy move to next position
         * */
        selectedRecyclerView.smoothScrollToPosition(mCurrentPos);

        BackupData.instance().setClipInfoData(mClipInfoArray);

        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(POINT9V16));
        TimelineData.instance().setClipInfoData(mClipInfoArray);
        TimelineData.instance().setMakeRatio(POINT9V16);

        resetVideoFragment();

        m_waitFlag = false;
        //添加一份
    }

    /**
     * 添加一个动画item
     * 添加的逻辑就是构建一个新的集合，遍历旧的集合的数据，key+size 作为新的key
     * 如果需要添加一个动画特效对象，则添加到mCurrentPosition 位置
     *
     * @param addItem 是否需要添加特效
     * @param mCurrentPos 当前选择要添加的位置
     * @param size 操作的数量
     */
    private void addNewAnimationInfo(boolean addItem ,int mCurrentPos,int size) {
        //如果当前就有动画效果，则复制一份
        if(mVideoClipFxMap != null && mVideoClipFxMap.size()>0 ){
            //构建一个新的集合存储新的数据;
            ConcurrentHashMap<Integer, AnimationInfo> tempMap = new ConcurrentHashMap<>();
            Set<Integer> keySet = mVideoClipFxMap.keySet();
            for(Integer key : keySet){
                if(key >= mCurrentPos){
                    //遍历旧的集合的数据，key+size 作为新的key
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put((key+size),animationInfo);
                }else{
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put(key,animationInfo);
                }
            }
            if(addItem){
                AnimationInfo animationInfo = mVideoClipFxMap.get(mCurrentPos);
                AnimationInfo newOne = new AnimationInfo();
                newOne.setmAssetType(animationInfo.getmAssetType());
                newOne.setmAnimationIn(animationInfo.getmAnimationIn());
                newOne.setmAnimationOut(animationInfo.getmAnimationOut());
                newOne.setmPackageId(animationInfo.getmPackageId());
                tempMap.put(mCurrentPos,newOne);
            }
            //然后重新赋值
            mVideoClipFxMap.clear();
            mVideoClipFxMap.putAll(tempMap);
        }

    }

    @Override
    protected void initListener() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {
                removeTimeline();
                clearData();
            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

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

        if (mVideoFragment != null) {
            mVideoFragment.setVideoVolumeListener(new VideoFragment.VideoVolumeListener() {
                @Override
                public void onVideoVolume() {
                    mVolumeUpLayout.setVisibility(View.VISIBLE);
                }
            });
        }

        cvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getInstance().jumpActivity(VideoEditActivity.this, VideoEditActivity2.class, null);
            }
        });

        mVolumeUpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * 交换两个item 位置
     * @param fromPosition 起点位置
     * @param toPosition 终点位置
     */
    private void swapAnimationInfo(int fromPosition, int toPosition) {
        //如果当前就有动画效果，则复制一份
        if(null != mVideoClipFxMap && mVideoClipFxMap.size()>0 && mVideoClipFxMap.containsKey(fromPosition) &&mVideoClipFxMap.containsKey(toPosition) ){
            AnimationInfo animationInfoFrom = mVideoClipFxMap.get(fromPosition);
            AnimationInfo animationInfoTo = mVideoClipFxMap.get(toPosition);
            mVideoClipFxMap.put(fromPosition,animationInfoTo);
            mVideoClipFxMap.put(toPosition,animationInfoFrom);
        }
    }

    private void reAddMediaAsset() {
        mAddVideoPostion = mCurrentPos+1;
        Bundle bundle = new Bundle();
        bundle.putInt("visitMethod", Constants.FROMCLIPEDITACTIVITYTOVISIT);
        BackupData.instance().clearAddClipInfoList();
        AppManager.getInstance().jumpActivityForResult(VideoEditActivity.this, SelectMediaActivity.class, bundle, ADDVIDEO_REQUESTCODE);
    }

    private void processAddMediaResult(){
        mClipInfoArray = BackupData.instance().getClipInfoData();
        ArrayList<ClipInfo> addCipInfoList = BackupData.instance().getAddClipInfoList();

        if (addCipInfoList.size() > 0) {
            //此处是从媒体库在选择资源  ， 修改存储的动画的特效数据结构
            addNewAnimationInfo(false, mAddVideoPostion, addCipInfoList.size());

            mClipInfoArray.addAll(mAddVideoPostion, addCipInfoList);
            BackupData.instance().setClipInfoData(mClipInfoArray);
            BackupData.instance().clearAddClipInfoList();


            TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(POINT3V4));
            TimelineData.instance().setClipInfoData(mClipInfoArray);
            TimelineData.instance().setMakeRatio(POINT3V4);

            TimelineData.instance().setClipInfoData(mClipInfoArray);

            /*
             * 为新增的素材添加默认转场
             * Add default transitions for new material
             */
            if (mTransitionInfoArray != null) {
                ArrayList<TransitionInfo> temp = new ArrayList<>();
                int maxTransitionCount = mClipInfoArray.size() - mTransitionInfoArray.size() - 1;
                for (int i = 0; i < maxTransitionCount; i++) {
                    TransitionInfo transitionInfo = new TransitionInfo();
                    temp.add(transitionInfo);
                }
                if (mAddVideoPostion <= mTransitionInfoArray.size()) {
                    mTransitionInfoArray.addAll(mAddVideoPostion, temp);
                }
            }
        }

        mCurrentPos = mClipInfoArray.size()-1;
        selectedClipUI.setClipInfoArray(mClipInfoArray);
        selectedClipUI.setSelectPos(mCurrentPos);
        selectedClipUI.notifyDataSetChanged();
        selectedRecyclerView.smoothScrollToPosition(mCurrentPos);

        BackupData.instance().setClipIndex(mCurrentPos);

        resetVideoFragment();
    }

    private void resetVideoFragment() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }

        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }

        NvsVideoClip nvsVideoClip = mVideoTrack.getClipByIndex(mCurrentPos);
        if (nvsVideoClip == null) {
            return;
        }

        mCurrentNvsVideoClip = nvsVideoClip;
        mTransitionInfoArray = TimelineData.instance().cloneTransitionsData();

        //clip 对应的动画集合
        ConcurrentHashMap<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        mVideoClipFxMap.putAll(fxMap);

        initVideoFragment();
    }

    private void refreshClipInfos(){
        /*
         * 为新增的素材添加默认转场
         * Add default transitions for new material
         */
        if (mTransitionInfoArray != null) {
            ArrayList<TransitionInfo> temp = new ArrayList<>();
            int maxTransitionCount = mClipInfoArray.size() - mTransitionInfoArray.size() - 1;
            for (int i = 0; i < maxTransitionCount; i++) {
                TransitionInfo transitionInfo = new TransitionInfo();
                temp.add(transitionInfo);
            }
            if (mAddVideoPostion <= mTransitionInfoArray.size()) {
                mTransitionInfoArray.addAll(mAddVideoPostion, temp);
            }
        }

        selectedClipUI.setClipInfoArray(mClipInfoArray);
        selectedClipUI.setSelectPos(mCurrentPos);
        selectedClipUI.notifyDataSetChanged();
        selectedRecyclerView.smoothScrollToPosition(mCurrentPos);

        resetVideoFragment();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish) {
            mVolumeUpLayout.setVisibility(View.GONE);
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

        mClipInfoArray = BackupData.instance().getClipInfoData();
        mCurrentPos = BackupData.instance().getClipIndex();

        switch (requestCode) {
            case CLIPTRIM_REQUESTCODE:  // trim result
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                refreshClipInfos();
                break;

            case CLIPADJUST_REQUESTCODE:  // ratio result
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                refreshClipInfos();
                break;

            case ADDVIDEO_REQUESTCODE:  // copy result
                processAddMediaResult();
                break;

            case REQUESTRESULT_BACKGROUND:  // background result
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                refreshClipInfos();
                break;

            case CLIPSPEED_REQUESTCODE:  // speed result
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                refreshClipInfos();
                break;

            case CLIPSPILTPOINT_REQUESTCODE:  // split result
                int spiltPosition = data.getIntExtra("spiltPosition", -1);
                if (spiltPosition != -1) {
                    if ((mTransitionInfoArray != null) && (!mTransitionInfoArray.isEmpty())) {
                        if (spiltPosition <= mTransitionInfoArray.size()) {
                            mTransitionInfoArray.add(spiltPosition, new TransitionInfo());
                        }
                    }
                    mCurrentPos = spiltPosition;
                    BackupData.instance().setClipIndex(mCurrentPos);

                    TimelineData.instance().setClipInfoData(mClipInfoArray);
                    refreshClipInfos();
                }
                break;

            case CLIPVOLUME_REQUESTCODE:
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                refreshClipInfos();
                break;
        }
    }
}



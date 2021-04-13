package com.glitchcam.vepromei.musicLyrics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.flipcaption.FlipCaptionActivity;
import com.glitchcam.vepromei.photoalbum.PhotoAlbumConstants;
import com.glitchcam.vepromei.photoalbum.PhotoAlbumPreviewActivity;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.selectmedia.fragment.MediaFragment;
import com.glitchcam.vepromei.selectmedia.interfaces.OnTotalNumChangeForActivity;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.MediaConstant;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;

import java.util.ArrayList;
import java.util.List;

import static com.glitchcam.vepromei.utils.Constants.POINT9V16;
import static com.glitchcam.vepromei.utils.MediaConstant.KEY_CLICK_TYPE;
import static com.glitchcam.vepromei.utils.MediaConstant.LIMIT_COUNT_MAX;

public class MultiVideoSelectActivity extends BaseActivity implements OnTotalNumChangeForActivity {
    private final String TAG = "MultiVideoSelectActivity";
    private CustomTitleBar mTitleBar;
    private TextView sigleTvStartEdit;
    private List<MediaData> mMediaDataList;
    private int fromWhat = Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS;
    private int mLimiteMediaCountMax = -1;
    private int mLimiteMediaCountMin = -1;
    private int mMediaType = MediaConstant.VIDEO;
    private TextView mAblumTipsText;

    @Override
    protected int initRootView() {
        return R.layout.activity_single_click;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        sigleTvStartEdit = (TextView) findViewById(R.id.sigle_tv_startEdit);
        mAblumTipsText = (TextView) findViewById(R.id.albumTipsText);

        sigleTvStartEdit.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (selectCreateRatio(POINT9V16)) {
                    AppManager.getInstance( ).finishActivity( );
                }
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.selectMedia);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent( );
        if (intent != null) {
            Bundle bundle = intent.getExtras( );
            if (bundle != null) {
                fromWhat = bundle.getInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS);
                mLimiteMediaCountMax = bundle.getInt(MediaConstant.LIMIT_COUNT_MAX, -1);
                mLimiteMediaCountMin = bundle.getInt(MediaConstant.LIMIT_COUNT_MIN, -1);
                mMediaType = bundle.getInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                if (fromWhat == Constants.SELECT_VIDEO_FROM_PHOTO_ALBUM) {
                    mTitleBar.setTextCenter(R.string.select_assets);
                    if (PhotoAlbumConstants.albumData != null) {
                        mAblumTipsText.setVisibility(View.VISIBLE);
                        mAblumTipsText.setText(PhotoAlbumConstants.albumData.photosAlbumTips);
                    }
                } else {
                    mAblumTipsText.setVisibility(View.GONE);
                    mTitleBar.setTextCenter(R.string.select_video);
                }
            }
        }

        initVideoFragment(R.id.single_contain);
    }

    private boolean selectCreateRatio(int makeRatio) {
        ArrayList<ClipInfo> pathList = getClipInfoList( );
        TimelineData.instance( ).setVideoResolution(Util.getVideoEditResolution(makeRatio));
        TimelineData.instance( ).setClipInfoData(pathList);
        TimelineData.instance( ).setMakeRatio(makeRatio);
        if (fromWhat == Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS) {
            AppManager.getInstance( ).jumpActivity(MultiVideoSelectActivity.this, MusicLyricsActivity.class, null);
            return true;
        } else if (fromWhat == Constants.SELECT_VIDEO_FROM_FLIP_CAPTION) {
            AppManager.getInstance( ).jumpActivity(MultiVideoSelectActivity.this, FlipCaptionActivity.class, null);
            return true;
        } else if (fromWhat == Constants.SELECT_VIDEO_FROM_PHOTO_ALBUM) {
            if (pathList != null && pathList.size( ) >= mLimiteMediaCountMin) {
                AppManager.getInstance( ).jumpActivity(MultiVideoSelectActivity.this, PhotoAlbumPreviewActivity.class, null);
                return true;
            }
        }
        return false;
    }

    private ArrayList<ClipInfo> getClipInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>( );
        if (mMediaDataList != null) {
            for (MediaData mediaData : mMediaDataList) {
                ClipInfo clipInfo = new ClipInfo( );
                clipInfo.setImgDispalyMode(Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY);
                clipInfo.setOpenPhotoMove(false);
                clipInfo.setFilePath(mediaData.getPath( ));
                pathList.add(clipInfo);
            }
        }
        return pathList;
    }

    private void initVideoFragment(int layoutId) {
        MediaFragment mediaFragment = new MediaFragment( );
        Bundle bundle = new Bundle( );
        bundle.putInt(MediaConstant.MEDIA_TYPE, mMediaType);
        bundle.putInt(LIMIT_COUNT_MAX, mLimiteMediaCountMax);
        bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_MULTIPLE);
        mediaFragment.setArguments(bundle);
        getSupportFragmentManager( ).beginTransaction( )
                .add(layoutId, mediaFragment)
                .commit( );
        getSupportFragmentManager( ).beginTransaction( ).show(mediaFragment);
    }

    @Override
    protected void onStop() {
        super.onStop( );
        Logger.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        Logger.e(TAG, "onDestroy");
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTotalNumChangeForActivity(List selectList, Object tag) {
        mMediaDataList = selectList;
        sigleTvStartEdit.setVisibility(selectList.size( ) > 0 ? View.VISIBLE : View.GONE);
    }

}

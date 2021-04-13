package com.glitchcam.vepromei.selectmedia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.dialog.DurationDialog;
import com.glitchcam.vepromei.edit.VideoEditActivity;
import com.glitchcam.vepromei.edit.VideoEditActivity2;
import com.glitchcam.vepromei.edit.data.BackupData;
import com.glitchcam.vepromei.edit.interfaces.OnGrallyItemClickListener;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.picinpic.PictureInPictureActivity;
import com.glitchcam.vepromei.selectmedia.adapter.SelectedMediaDatasUI;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.selectmedia.fragment.SelectMediaDataFragment;
import com.glitchcam.vepromei.selectmedia.fragment.SelectMediaDataFragment.OnClickMediaDataListener;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.MediaConstant;
import com.glitchcam.vepromei.utils.MediaUtils;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.dataInfo.AnimationInfo;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static com.glitchcam.vepromei.utils.Constants.POINT3V4;
import static com.glitchcam.vepromei.utils.Constants.POINT9V16;

public class SelectMediaActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private TabLayout mediaTapLayout;
    private ViewPager mediaViewPager;
    private ViewPagerAdapter adapter;

    private List<Fragment> fragmentLists = new ArrayList<>();
    private final List<String> fragmentTabTitles = new ArrayList<>();

    private FloatingActionButton fbtNext;

    private final Integer[] fragmentTotalNumber = {0, 0};

    private int visitMethod = Constants.FROMMAINACTIVITYTOVISIT;
    private int nowFragmentPosition = 0;

    private TextView tv_selected_medias;

    private RecyclerView selectedRecyclerView;
    private SelectedMediaDatasUI selectedMediaUI;
    private List<MediaData> frgTotalSelectedList = new ArrayList<>();
    int mCurrentPos = 0;
    /**
     *
     * clip 对应的动画集合 当前添加或删除 等操作 需要同步操作这个集合 以保证数据的同步
     * 这个集合是当前页面级的
     */
    private ConcurrentHashMap<Integer, AnimationInfo> mVideoClipFxMap = new ConcurrentHashMap<>();

    private int rv_width;

    OnClickMediaDataListener onClickMediaListener = new OnClickMediaDataListener() {
        @Override
        public void onCLickMediaData(final MediaData mediaData, int position) {

            List<MediaData> selectedLists = getSelectedByType(mediaData.getType());
            fragmentTotalNumber[nowFragmentPosition] = selectedLists.size();

            if (visitMethod != Constants.FROMPICINPICACTIVITYTOVISIT) {
                fbtNext.setVisibility((Collections.max(Arrays.asList(fragmentTotalNumber))) > 0 ? View.VISIBLE : View.GONE);
            }

            if(mediaData.isState()){
                frgTotalSelectedList.add(mediaData);
            }else{
                frgTotalSelectedList.removeIf(new Predicate<MediaData>() {
                    @Override
                    public boolean test(MediaData media) {
                        return (media.getPath().equals(mediaData.getPath()));
                    }
                });
            }

            for (int i=0; i < frgTotalSelectedList.size(); i++) {
                frgTotalSelectedList.get(i).setPosition(i);
            }

            mCurrentPos = frgTotalSelectedList.size()-1;
            if(mCurrentPos < 0) mCurrentPos = 0;

            selectedMediaUI.setMediaInfoArray(frgTotalSelectedList);
            selectedMediaUI.setSelectPos(mCurrentPos);
            selectedMediaUI.notifyDataSetChanged();

            selectedRecyclerView.smoothScrollToPosition(mCurrentPos);

            setSelectedMediaCounts(frgTotalSelectedList);
        }
    };

    @Override
    protected int initRootView() {
        return R.layout.activity_select_media;
    }

    @Override
    protected void initViews() {

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                visitMethod = bundle.getInt("visitMethod", Constants.FROMMAINACTIVITYTOVISIT);
            }
        }

        //clip 对应的动画集合
        ConcurrentHashMap<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        mVideoClipFxMap.putAll(fxMap);

        mTitleBar = findViewById(R.id.title_bar);

        mediaTapLayout = findViewById(R.id.tl_select_media);
        mediaViewPager = findViewById(R.id.vp_select_media);

        tv_selected_medias = findViewById(R.id.tv_selected_medias);
        String tmpStr = 0 + " " + getString(R.string.video_) + " " + 0 + " " + getString(R.string.photo_) + " " + getString(R.string.selected);
        tv_selected_medias.setText(tmpStr);

        selectedRecyclerView = findViewById(R.id.selectedMediaRecycleer);

        fbtNext = findViewById(R.id.floasting_next);
        fbtNext.setVisibility(View.GONE);
        fbtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (visitMethod == Constants.FROMMAINACTIVITYTOVISIT) {
                selectCreateRatio(POINT3V4);
            }
            else if (visitMethod == Constants.FROMCLIPEDITACTIVITYTOVISIT) {
                ArrayList<ClipInfo> clipInfos = getSelectedInfoList();
                BackupData.instance().setAddClipInfoList(clipInfos);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
            else if (visitMethod == Constants.FROMPICINPICACTIVITYTOVISIT) {
                ArrayList<String> picInpicArray = getPicInPicVideoList();
                if (picInpicArray.size() <= 1) {
                    String[] selectVideoTips = getResources().getStringArray(R.array.select_video_tips);
                    Util.showDialog(SelectMediaActivity.this, selectVideoTips[0], selectVideoTips[1]);
                    return;
                }
                BackupData.instance().setPicInPicVideoArray(picInpicArray);
                AppManager.getInstance().jumpActivity(SelectMediaActivity.this, PictureInPictureActivity.class, null);
                AppManager.getInstance().finishActivity();
            }
            }
        });
    }

    @Override @SuppressLint("RestrictedApi")
    protected void initData() {
        String[] tabList = getResources().getStringArray(R.array.select_media);
        Collections.addAll(fragmentTabTitles, tabList);

        if (visitMethod == Constants.FROMPICINPICACTIVITYTOVISIT) {
            fbtNext.setVisibility(View.VISIBLE);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rv_width = (displayMetrics.widthPixels - 24) / 3;

        fragmentLists = getSupportFragmentManager().getFragments();
        if (fragmentLists.size() == 0) {
            fragmentLists = new ArrayList<>();
            List<MediaData> mArr = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                SelectMediaDataFragment mediaFragment = new SelectMediaDataFragment(mArr, MediaConstant.MEDIATYPECOUNT[i], rv_width, onClickMediaListener);
                fragmentLists.add(mediaFragment);
            }
        }

        /*
         * 禁止预加载
         * Disable preload
         * */
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentLists);
        mediaViewPager.setAdapter(adapter);
        mediaViewPager.setOffscreenPageLimit(2);
        mediaViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pageIndex) {
                nowFragmentPosition = pageIndex;
                refreshPageView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mediaTapLayout.setupWithViewPager(mediaViewPager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedRecyclerView.setLayoutManager(linearLayoutManager);
        selectedMediaUI = new SelectedMediaDatasUI(getApplicationContext(), frgTotalSelectedList, new ArrayList<ClipInfo>(), true, new SelectedMediaDatasUI.OnClickSelectedItem() {
            @Override
            public void onClickCellData(int position) {
                mCurrentPos = position;
                selectedMediaUI.setSelectPos(mCurrentPos);
                selectedMediaUI.notifyDataSetChanged();

                final MediaData media = frgTotalSelectedList.get(position);
                if(media.getType() == MediaConstant.MEDIATYPECOUNT[1]){
                    DurationDialog durationDialog = new DurationDialog(SelectMediaActivity.this, media.getDuration(), new DurationDialog.DurationDlgCallback() {
                        @Override
                        public void onClickOk(long process) {
                            media.setDuration(process);
                            SelectMediaDataFragment fragment = (SelectMediaDataFragment) fragmentLists.get(media.getType());
                            fragment.setDuration(media);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    durationDialog.show();
                }
            }

            @Override
            public void onClickRemoveData(int position) {
                MediaData media = frgTotalSelectedList.get(position);
                SelectMediaDataFragment fragment = (SelectMediaDataFragment) fragmentLists.get(media.getType());
                fragment.unSetState(media);

                frgTotalSelectedList.remove(position);
                adapter.notifyDataSetChanged();

                for (int i=0; i < frgTotalSelectedList.size(); i++) {
                    frgTotalSelectedList.get(i).setPosition(i);
                }

                if(mCurrentPos > position){
                    mCurrentPos--;
                }
                if(mCurrentPos == position && mCurrentPos >= frgTotalSelectedList.size()){
                    mCurrentPos = frgTotalSelectedList.size()-1;
                    if(mCurrentPos < 0) mCurrentPos = 0;
                }
                selectedMediaUI.setMediaInfoArray(frgTotalSelectedList);
                selectedMediaUI.setSelectPos(mCurrentPos);
                selectedMediaUI.notifyDataSetChanged();

                setSelectedMediaCounts(frgTotalSelectedList);
            }
        });
        selectedRecyclerView.setAdapter(selectedMediaUI);

        ItemTouchHelper.Callback callback = new com.glitchcam.vepromei.edit.grallyRecyclerView.ItemTouchHelper(selectedMediaUI);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(selectedRecyclerView);

        selectedMediaUI.setOnItemSelectedListener(new OnGrallyItemClickListener() {
            @Override
            public void onLeftItemClick(View view, int pos) {}

            @Override
            public void onRightItemClick(View view, int pos) {}

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                Collections.swap(frgTotalSelectedList, fromPosition, toPosition);
                for (int i=0; i < frgTotalSelectedList.size(); i++) {
                    frgTotalSelectedList.get(i).setPosition(i);
                }

                selectedMediaUI.setMediaInfoArray(frgTotalSelectedList);
                selectedMediaUI.notifyDataSetChanged();

                swapAnimationInfo(fromPosition,toPosition);
            }

            @Override
            public void onItemDismiss(int position) {

            }

            @Override
            public void removeall() {

            }
        });

        /*
         * 要判断是否有权限，无权限的时候不能去读写SD卡
         * To determine whether you have permission, you cannot read or write to the SD card without permission
         * */
        MediaUtils.getAllVideoInfos(SelectMediaActivity.this, new MediaUtils.LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(List<MediaData> allMediaTemp) {

                SelectMediaDataFragment frg = (SelectMediaDataFragment) fragmentLists.get(0);
                frg.setMediaList(allMediaTemp);
                frg.refreshMediaDataList();
                adapter.notifyDataSetChanged();

                MediaUtils.getAllPhotoInfo(SelectMediaActivity.this, new MediaUtils.LocalMediaCallback() {
                    @Override
                    public void onLocalMediaCallback(List<MediaData> allMediaTemp) {
                        SelectMediaDataFragment frg1 = (SelectMediaDataFragment) fragmentLists.get(1);
                        frg1.setMediaList(allMediaTemp);
                        frg1.refreshMediaDataList();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.selectMedia);
    }

    @Override
    protected void initListener() {}

    @Override
    public void onClick(View view) {}

    private void setSelectedMediaCounts(List<MediaData> mMediaList){
        int v_count = 0, p_count = 0;
        for (MediaData data: mMediaList) {
            if(data.getType() == MediaConstant.VIDEO) v_count++;
            else p_count++;
        }

        String v_str = v_count > 1? getString(R.string.videos) : getString(R.string.video_);
        String p_str = p_count > 1? getString(R.string.photos) : getString(R.string.photo_);

        String tmpStr = v_count + " " + v_str + " " + p_count + " " + p_str + " " + getString(R.string.selected);
        tv_selected_medias.setText(tmpStr);
    }

    public List<MediaData> getSelectedByType(int mediatype){
        List<MediaData> tmp = new ArrayList<>();
        SelectMediaDataFragment fragment = (SelectMediaDataFragment) fragmentLists.get(mediatype);
        if (fragment != null) {
            tmp = fragment.getSelectedList();
        }
        return tmp;
    }

    public List<MediaData> getAllSelectedMediaDataList() {
        List<MediaData> selected = new ArrayList<>();
        for(int i=0; i<2 ;i++){
            SelectMediaDataFragment fragment = (SelectMediaDataFragment) fragmentLists.get(i);
            if (fragment != null) {
                List<MediaData> tmp = fragment.getSelectedList();
                for (MediaData media: tmp) {
                    selected.add(media);
                }
            }
        }
        return selected;
    }

    public void refreshPageView(){
        SelectMediaDataFragment frg = (SelectMediaDataFragment) fragmentLists.get(nowFragmentPosition);
        frg.refreshMediaDataList();
        adapter.notifyDataSetChanged();
    }

    private void selectCreateRatio(int makeRatio) {
        ArrayList<ClipInfo> pathList = getSelectedInfoList();
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
        TimelineData.instance().setClipInfoData(pathList);
        TimelineData.instance().setMakeRatio(makeRatio);
        AppManager.getInstance().jumpActivity(SelectMediaActivity.this, VideoEditActivity.class, null);
//        AppManager.getInstance().jumpActivity(SelectMediaActivity.this, VideoEditActivity2.class, null);
        AppManager.getInstance().finishActivity();
    }

    private ArrayList<ClipInfo> getSelectedInfoList() {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (MediaData mediaData : frgTotalSelectedList) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(mediaData.getPath());
            pathList.add(clipInfo);
        }
        return pathList;
    }

    private ArrayList<String> getPicInPicVideoList() {
        ArrayList<String> pathList = new ArrayList<>();
        for (MediaData mediaData : getAllSelectedMediaDataList()) {
            pathList.add(mediaData.getPath());
        }
        return pathList;
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nowFragmentPosition = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < fragmentLists.size(); i++) {
            fragmentLists.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> frgs;

        public ViewPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments) {
            super(supportFragmentManager);
            frgs = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return frgs.get(i);
        }
        @Override
        public int getCount() {
            return frgs.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTabTitles.get(position);
        }
    }
}

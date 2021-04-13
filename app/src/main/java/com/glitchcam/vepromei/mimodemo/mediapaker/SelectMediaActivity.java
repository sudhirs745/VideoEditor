package com.glitchcam.vepromei.mimodemo.mediapaker;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meicam.sdk.NvsMediaFileConvertor;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.EditActivity;
import com.glitchcam.vepromei.mimodemo.TrimEditActivity;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.base.BaseActivity;
import com.glitchcam.vepromei.mimodemo.common.base.BaseFragmentPagerAdapter;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TimelineData;
import com.glitchcam.vepromei.mimodemo.common.template.model.RadioEnum;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotDataInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.TrackClipInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.utils.AppManager;
import com.glitchcam.vepromei.mimodemo.common.utils.CommonUtil;
import com.glitchcam.vepromei.mimodemo.common.utils.Logger;
import com.glitchcam.vepromei.mimodemo.common.utils.PathUtils;
import com.glitchcam.vepromei.mimodemo.common.utils.TimelineUtil;
import com.glitchcam.vepromei.mimodemo.common.view.CustomTitleBar;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.BottomMenuViewHolder;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.IAdapterLifeCircle;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.RatioGridItemAdapter;
import com.glitchcam.vepromei.mimodemo.mediapaker.interfaces.OnClipAdd;
import com.glitchcam.vepromei.mimodemo.mediapaker.interfaces.OnTotalNumChangeForActivity;
import com.glitchcam.vepromei.mimodemo.mediapaker.utils.MediaConstant;
import com.glitchcam.vepromei.mimodemo.mediapaker.view.SelectBottomMenu;
import com.glitchcam.vepromei.selectmedia.adapter.AgendaSimpleSectionAdapter;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.selectmedia.fragment.MediaFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.glitchcam.vepromei.mimodemo.common.Constants.POINT16V9;
import static com.glitchcam.vepromei.mimodemo.mediapaker.utils.MediaConstant.KEY_CLICK_TYPE;
import static com.glitchcam.vepromei.mimodemo.mediapaker.utils.MediaConstant.LIMIT_COUNT;


public class SelectMediaActivity extends BaseActivity implements OnTotalNumChangeForActivity {
    private String TAG = getClass().getName();
    public static final int DEFAULT_INDEX = 0;
    public static final int REQUEST_CODE_EDIT_CLIP = 100;
    public static final String INTENT_KEY_OPERATE_CODE = "operate_code";
    public static final int OPERATE_CODE_REPLACE = 100;
    public static final int OPERATE_CODE_CACEL = 101;
    public static final int OPERATE_CODE_TRIM = 102;
    private CustomTitleBar mTitleBar;
    private TabLayout tlSelectMedia;
    private ViewPager vpSelectMedia;
    private List<Fragment> fragmentLists = new ArrayList<>();
    private List<String> fragmentTabTitles = new ArrayList<>();
    private BaseFragmentPagerAdapter fragmentPagerAdapter;
    private List<MediaData> mMediaDataList = new ArrayList<>();
    private Integer[] fragmentTotalNumber = {0, 0, 0};
    private int nowFragmentPosition = 0;
    private int mLimiteMediaCount = -1;
    private SelectBottomMenu mBottomMenu;
    private int mIndex = DEFAULT_INDEX;
    private int mOperateCode;
    private NvsMediaFileConvertor mFileConvertor = new NvsMediaFileConvertor();
    private List<ShotVideoInfo> mShotVideoInfos = new ArrayList<>();
    private int mShotVideoIndex = 0;
    private LinearLayout mRatioLayout;
    private GridView mRatioGridView;
    private RelativeLayout mWaitLayout;
    private RatioGridItemAdapter mRatioGridItemAdapter;
    private List<String> mRatioDataList = new ArrayList<>();

    @Override
    protected int initRootView() {
        return R.layout.mimo_activity_select_media;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        tlSelectMedia = (TabLayout) findViewById(R.id.tl_select_media);
        vpSelectMedia = (ViewPager) findViewById(R.id.vp_select_media);
        mBottomMenu = (SelectBottomMenu) findViewById(R.id.bottom_menu);
        mWaitLayout = (RelativeLayout) findViewById(R.id.waitLayout);
        mRatioLayout = (LinearLayout) findViewById(R.id.pop_container);
        mRatioGridView = (GridView) findViewById(R.id.grid_view);
        mWaitLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initRatioGridView();
        mBottomMenu.addAdapterLifeCircle(new IAdapterLifeCircle() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
                View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.select_menu_item_layout, viewGroup, false);
                return new BottomMenuViewHolder(rootView);
            }
        });
        mBottomMenu.setOnNextClickListener(new SelectBottomMenu.OnNextClickListener() {
            @Override
            public void onNextClicked() {
                String supportedAspectRatio = TimelineUtil.getSupportedAspectRatio();
                if (TextUtils.isEmpty(supportedAspectRatio)) {
                    selectCreateRatio(POINT16V9);
                } else {
                    supportedAspectRatio = supportedAspectRatio.replaceAll("d", ".");
                    String[] aspectRatios = supportedAspectRatio.toLowerCase().split("\\|");
                    final List<String> ratioData = new ArrayList<>(Arrays.asList(aspectRatios));
                    if(ratioData.size() == 1){
                        selectCreateRatio(RadioEnum.getIntRadio(ratioData.get(0)));
                        return;
                    }
                    if (ratioData.size() % 2 != 0) {
                        ratioData.add("");
                    }
                    mRatioLayout.setVisibility(View.VISIBLE);
                    mRatioDataList.clear();
                    mRatioDataList.addAll(ratioData);
                    mRatioGridItemAdapter.setRatioList(ratioData);
                }
            }
        });
        mBottomMenu.setOnItemClickListener(new SelectBottomMenu.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.IntentKey.INTENT_KEY_SHOT_ID, position);
                bundle.putBoolean("from_result", true);
                AppManager.getInstance().jumpActivityForResult(SelectMediaActivity.this, TrimEditActivity.class, bundle, REQUEST_CODE_EDIT_CLIP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EDIT_CLIP:
                if (data == null) {
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    int code = extras.getInt(INTENT_KEY_OPERATE_CODE);
                    if (code == OPERATE_CODE_REPLACE) {
                        mBottomMenu.removeClipPath();
                    } else if (code == OPERATE_CODE_TRIM) {
                        //mBottomMenu.goNextPosition();
                    } else if (code == OPERATE_CODE_CACEL) {

                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.selectMedia);
        mTitleBar.setTextCenter(getResources().getString(R.string.title_text_select_media));
    }

    public void setTitleText(int count) {
        mTitleBar.setTextCenter(R.string.selectMedia);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initData() {
        TimelineData.init();
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mLimiteMediaCount = bundle.getInt("limitMediaCount", -1);
                mIndex = bundle.getInt(Constants.IntentKey.INTENT_KEY_SHOT_ID, DEFAULT_INDEX);
                mOperateCode = bundle.getInt(SelectMediaActivity.INTENT_KEY_OPERATE_CODE, 0);
            }
        }
        String[] tabList = getResources().getStringArray(R.array.select_media);
        checkDataCountAndTypeCount(tabList, MediaConstant.MEDIATYPECOUNT);
        fragmentLists = getSupportFragmentManager().getFragments();
        if (fragmentLists == null || fragmentLists.size() == 0) {
            fragmentLists = new ArrayList<>();
            for (int i = 0; i < tabList.length; i++) {
                MediaFragment mediaFragment = new MediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.MEDIATYPECOUNT[i]);
                bundle.putInt(LIMIT_COUNT, mLimiteMediaCount);
                bundle.putInt(KEY_CLICK_TYPE, MediaConstant.TYPE_ITEMCLICK_MULTIPLE);
                mediaFragment.setArguments(bundle);
                mediaFragment.setOnClipAddListener(new OnClipAdd() {
                    @Override
                    public void onClipAdd(String path, long duration) {
                        mBottomMenu.addClipPath(path, duration);
                    }
                });
                fragmentLists.add(mediaFragment);
            }
        }
        for (int i = 0; i < tabList.length; i++) {
            fragmentTabTitles.add(tabList[i]);
        }

        //禁止预加载
        vpSelectMedia.setOffscreenPageLimit(3);
        //测试提交
        fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragmentLists, fragmentTabTitles);
        vpSelectMedia.setAdapter(fragmentPagerAdapter);
        vpSelectMedia.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowFragmentPosition = position;
                for (int i = 0; i < fragmentLists.size(); i++) {
                    MediaFragment mediaFragment = (MediaFragment) fragmentLists.get(i);
                    List<Integer> list = Arrays.asList(fragmentTotalNumber);
                    if (!list.isEmpty()) {
                        mediaFragment.setTotalSize(Collections.max(list));
                    }
                }
                notifyFragmentDataSetChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tlSelectMedia.setupWithViewPager(vpSelectMedia);
        mBottomMenu.setPosition(mIndex);
    }

    private void resetUIVisible() {
        mShotVideoIndex = 0;
        mShotVideoInfos.clear();
        mWaitLayout.setVisibility(View.GONE);
        mRatioLayout.setVisibility(View.GONE);
    }

    private void initRatioGridView() {
        mRatioGridItemAdapter = new RatioGridItemAdapter(this.getApplicationContext());
        mRatioGridView.setAdapter(mRatioGridItemAdapter);
        mRatioGridItemAdapter.setRatioList(mRatioDataList);
        mRatioGridItemAdapter.setOnItemClickListener(new RatioGridItemAdapter.OnItemCilickListener() {
            @Override
            public void onItemClick(int position) {
                int ratioCount = mRatioDataList.size();
                if (position < 0 || position >= ratioCount) {
                    return;
                }
                String ratioName = mRatioDataList.get(position);
                if (TextUtils.isEmpty(ratioName)) {
                    return;
                }
                selectCreateRatio(RadioEnum.getIntRadio(ratioName));
            }
        });
    }

    /**
     * 校验一次数据，使得item标注的数据统一
     *
     * @param position 碎片对应位置0.1.2
     */
    private void notifyFragmentDataSetChanged(int position) {
        MediaFragment fragment = (MediaFragment) fragmentLists.get(position);
        List<MediaData> currentFragmentList = checkoutSelectList(fragment);
        fragment.getAdapter().setSelectList(currentFragmentList);
        setTitleText(fragment.getAdapter().getSelectList().size());
        Logger.e(TAG, "onPageSelected: " + fragment.getAdapter().getSelectList().size());
    }

    private List<MediaData> checkoutSelectList(MediaFragment fragment) {
        List<MediaData> currentFragmentList = fragment.getAdapter().getSelectList();
        List<MediaData> totalSelectList = getMediaDataList();
        for (MediaData mediaData : currentFragmentList) {
            for (MediaData data : totalSelectList) {
                if (data.getPath().equals(mediaData.getPath()) && data.isState() == mediaData.isState()) {
                    mediaData.setPosition(data.getPosition());
                }
            }
        }
        return currentFragmentList;
    }

    private void checkDataCountAndTypeCount(String[] tabList, int[] mediaTypeCount) {
        if (tabList.length != mediaTypeCount.length) {
            return;
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断如果同意的情况下就去 吧权限请求设置给当前fragment的
        for (int i = 0; i < fragmentLists.size(); i++) {
            fragmentLists.get(i).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onTotalNumChangeForActivity(List selectList, Object tag) {
        int index = (int) tag;
        fragmentTotalNumber[index] = selectList.size();
        Logger.e("onTotalNumChangeForActivity", "对应的碎片：  " + index + "    个数：" + selectList.size());
        for (int i = 0; i < fragmentLists.size(); i++) {
            if (i != index) {
                MediaFragment fragment = (MediaFragment) fragmentLists.get(i);
                fragment.refreshSelect(selectList, index);
            }
        }

        if (index == nowFragmentPosition) {
            setTitleText(selectList.size());
        }
    }


    public List<MediaData> getMediaDataList() {
        if (mMediaDataList == null) {
            return new ArrayList<>();
        }
        MediaFragment fragment = (MediaFragment) fragmentLists.get(0);
        if (fragment != null) {
            AgendaSimpleSectionAdapter adapter = fragment.getAdapter();
            if (adapter != null) {
                return adapter.getSelectList();
            }
        }
        return new ArrayList<>();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
//        setTotal(0);
        nowFragmentPosition = 0;
        super.onDestroy();
        Logger.e(TAG, "onDestroy");
    }

    private void selectCreateRatio(int makeRatio) {
        TimelineData.instance().setVideoResolution(CommonUtil.getVideoEditResolution(makeRatio));
        TimelineData.instance().setMakeRatio(makeRatio);
//        TemplateInfo templateInfo = NvTemplateContext.getInstance().getSelectTemplate();
        MiMoLocalData templateInfo = NvTemplateContext.getInstance().getSelectedMimoData();
        List<ShotDataInfo> shotDataInfos = templateInfo.getShotDataInfos();
        int shotDataCount = shotDataInfos.size();
        for (int videoIndex = 0; videoIndex < shotDataCount; videoIndex++) {
            ShotDataInfo shotDataInfo = shotDataInfos.get(videoIndex);
            if (shotDataInfo == null) {
                continue;
            }
            ShotVideoInfo shotVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if (shotVideoInfo != null && shotVideoInfo.isConvertFlag()) {
                mShotVideoInfos.add(shotVideoInfo);
            }
            List<ShotVideoInfo> subTrackVideoInfos = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfos != null && !subTrackVideoInfos.isEmpty()) {
                for (int subVideoIndex = 0; subVideoIndex < subTrackVideoInfos.size(); subVideoIndex++) {
                    ShotVideoInfo subShotVideoInfo = subTrackVideoInfos.get(subVideoIndex);
                    if (subShotVideoInfo == null) {
                        continue;
                    }
                    if (subShotVideoInfo.isConvertFlag()) {
                        mShotVideoInfos.add(subShotVideoInfo);
                    }
                }
            }
        }
        if (mShotVideoInfos.isEmpty()) {
            goNextActivity();
            return;
        }
        convertVideoFile();
    }

    private void goNextActivity() {
        Intent intent = new Intent(SelectMediaActivity.this, EditActivity.class);
        if (mOperateCode > 0) {
            intent.putExtra(INTENT_KEY_OPERATE_CODE, mOperateCode);
            setResult(RESULT_OK, intent);
        } else {
            startActivity(intent);
        }
        AppManager.getInstance().finishActivity();
    }

    private void convertVideoFile() {
        mWaitLayout.setVisibility(View.VISIBLE);
        mFileConvertor.setMeidaFileConvertorCallback(new NvsMediaFileConvertor.MeidaFileConvertorCallback() {
            @Override
            public void onProgress(long l, float v) {

            }

            @Override
            public void onFinish(long l, String s, String s1, int errorCode) {
                Log.d(TAG, "onFinish = " + Thread.currentThread() + ",destFilePth = " + s1);
                if (errorCode == NvsMediaFileConvertor.CONVERTOR_ERROR_CODE_NO_ERROR) {
                    mShotVideoInfos.get(mShotVideoIndex).setConverClipPath(s1);
                }
                ++mShotVideoIndex;
                int videoCout = mShotVideoInfos.size();
                if (mShotVideoIndex >= videoCout) {
                    resetUIVisible();
                    goNextActivity();
                    return;
                }
                convertVideo();
            }

            @Override
            public void notifyAudioMuteRage(long l, long l1, long l2) {

            }
        }, true);
        convertVideo();
    }

    private void convertVideo() {
        if (mShotVideoInfos.isEmpty()) {
            return;
        }
        if (mShotVideoIndex < 0) {
            return;
        }
        int videoCount = mShotVideoInfos.size();
        if (mShotVideoIndex >= videoCount) {
            return;
        }
        ShotVideoInfo shotVideoInfo = mShotVideoInfos.get(mShotVideoIndex);
        if (shotVideoInfo == null) {
            return;
        }
        List<TrackClipInfo> trackClipInfos = shotVideoInfo.getTrackClipInfos();
        if (trackClipInfos == null || trackClipInfos.isEmpty()) {
            return;
        }
        String srcFilePath = shotVideoInfo.getVideoClipPath();
        if(srcFilePath.startsWith(ContentResolver.SCHEME_CONTENT)) {
            srcFilePath =getPath(this,Uri.parse(shotVideoInfo.getVideoClipPath()));
        }
        if (TextUtils.isEmpty(srcFilePath)) {
            return;
        }
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            return;
        }
        String contverVideoDir = PathUtils.getVideoConvertDirPath();
        if (TextUtils.isEmpty(contverVideoDir)) {
            return;
        }
        String fileName = srcFile.getName();
        String destFilePath = contverVideoDir + File.separator + fileName;
        File destFile = new File(destFilePath);
        if (destFile.exists()) {
            destFile.delete();
        }
        long fromPostion = shotVideoInfo.getTrimIn();
        long toPostion = fromPostion + shotVideoInfo.getRealNeedDuration();
        mFileConvertor.convertMeidaFile(srcFilePath, destFilePath, true, fromPostion, toPostion, null);
    }

    private String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}

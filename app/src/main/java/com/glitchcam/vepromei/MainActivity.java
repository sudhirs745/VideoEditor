package com.glitchcam.vepromei;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meicam.sdk.NvsStreamingContext;
import com.glitchcam.vepromei.base.BaseFragmentPagerAdapter;
import com.glitchcam.vepromei.base.BasePermissionActivity;
import com.glitchcam.vepromei.boomrang.BoomRangActivity;
import com.glitchcam.vepromei.capture.CaptureActivity;
import com.glitchcam.vepromei.capturescene.CaptureSceneActivity;
import com.glitchcam.vepromei.capturescene.httputils.NetWorkUtil;
import com.glitchcam.vepromei.capturescene.httputils.OkHttpClientManager;
import com.glitchcam.vepromei.capturescene.httputils.ResultCallback;
import com.glitchcam.vepromei.dialog.PrivacyPolicyDialog;
import com.glitchcam.vepromei.douvideo.DouVideoCaptureActivity;
import com.glitchcam.vepromei.edit.data.BackupData;
import com.glitchcam.vepromei.edit.watermark.SingleClickActivity;
import com.glitchcam.vepromei.feedback.FeedBackActivity;
import com.glitchcam.vepromei.glitter.GlitterEffectActivity;
import com.glitchcam.vepromei.main.MainViewPagerFragment;
import com.glitchcam.vepromei.main.MainViewPagerFragmentData;
import com.glitchcam.vepromei.main.MainWebViewActivity;
import com.glitchcam.vepromei.main.OnItemClickListener;
import com.glitchcam.vepromei.main.SpannerViewpagerAdapter;
import com.glitchcam.vepromei.main.bean.AdBeansFormUrl;
import com.glitchcam.vepromei.mimodemo.MyStoryActivity;
import com.glitchcam.vepromei.mimodemo.common.utils.MeicamContextWrap;
import com.glitchcam.vepromei.musicLyrics.MultiVideoSelectActivity;
import com.glitchcam.vepromei.particle.ParticleCaptureActivity;
import com.glitchcam.vepromei.photoalbum.PhotoAlbumActivity;
import com.glitchcam.vepromei.selectmedia.SelectMediaActivity;
import com.glitchcam.vepromei.superzoom.SuperZoomActivity;
import com.glitchcam.vepromei.themeshoot.ThemeSelectActivity;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.FileUtils;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.MediaConstant;
import com.glitchcam.vepromei.utils.ParameterSettingValues;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.ScreenUtils;
import com.glitchcam.vepromei.utils.SharedPreferencesUtils;
import com.glitchcam.vepromei.utils.SpUtil;
import com.glitchcam.vepromei.utils.SystemUtils;
import com.glitchcam.vepromei.utils.ToastUtil;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.glitchcam.vepromei.utils.license.LicenseInfo;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.glitchcam.vepromei.utils.Constants.BUILD_HUMAN_AI_TYPE_FU;
import static com.glitchcam.vepromei.utils.Constants.BUILD_HUMAN_AI_TYPE_MS;
import static com.glitchcam.vepromei.utils.Constants.BUILD_HUMAN_AI_TYPE_MS_ST;
import static com.glitchcam.vepromei.utils.Constants.BUILD_HUMAN_AI_TYPE_MS_ST_SUPER;
import static com.glitchcam.vepromei.utils.Constants.HUMAN_AI_TYPE_MS;
import static com.glitchcam.vepromei.utils.Constants.HUMAN_AI_TYPE_NONE;


/**
 * MainActivity class
 * 主页面
 *
 * @author gexinyu
 * @date 2018-05-24
 */
public class MainActivity extends BasePermissionActivity implements OnItemClickListener {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 200;
    public static final int INIT_ARSCENE_COMPLETE_CODE = 201;
    public static final int INIT_ARSCENE_FAILURE_CODE = 202;
    public static final int AD_SPANNER_CHANGE_CODE = 203;

    private ImageView mIvSetting;
    private RelativeLayout layoutVideoCapture;
    private RelativeLayout layoutVideoEdit;
    private TextView mainVersionNumber;
    private View clickedView = null;

    /*
     * 人脸初始化完成的标识
     * Face initialization completed logo
     * */
    private boolean arSceneFinished = false;
    /*
     * 记录人脸模块正在初始化
     * Recording face module is initializing
     * */
    private boolean initARSceneing = true;
    /*
     * 防止页面重复点击标识
     * Prevent pages from repeatedly clicking on logos
     * */
    private boolean isClickRepeat = false;

    /**
     * SDK普通版
     * <p>
     * SDK Normal Edition
     */
    private int mCanUseARFaceType = HUMAN_AI_TYPE_NONE;

    private HandlerThread mHandlerThread;

    private final MainActivityHandler mHandler = new MainActivityHandler(this);

    @SuppressLint("HandlerLeak")
    class MainActivityHandler extends Handler {
        WeakReference<MainActivity> mWeakReference;

        public MainActivityHandler(MainActivity mainActivityContext) {
            mWeakReference = new WeakReference<>(mainActivityContext);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case INIT_ARSCENE_COMPLETE_CODE:
                        /*
                         *  初始化ARScene 完成
                         * Initialization of ARScene completed
                         * */
                        arSceneFinished = true;
                        initARSceneing = false;
                        break;
                    case INIT_ARSCENE_FAILURE_CODE:
                        /*
                         *  初始化ARScene 失败
                         * Initializing ARScene failed
                         * */
                        arSceneFinished = false;
                        initARSceneing = false;
                        break;
                    default:
                        break;

                }
            }
        }
    }

    @Override
    protected int initRootView() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return R.layout.activity_main;
        }
        return R.layout.activity_main;
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initViews() {

        mIvSetting = findViewById(R.id.iv_main_setting);
        layoutVideoCapture = findViewById(R.id.layout_video_capture);
        layoutVideoEdit = findViewById(R.id.layout_video_edit);
        mainVersionNumber = findViewById(R.id.main_versionNumber);
    }

    @Override
    protected void initData() {
        MeicamContextWrap.getInstance().setContext(this.getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int alwaysFinish = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
            if (alwaysFinish == 1) {
                Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.no_back_activity_message)
                    .setNegativeButton(R.string.no_back_activity_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.no_back_activity_setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            startActivity(intent);
                        }
                    }).create();
                dialog.show();
            }
        }

        ParameterSettingValues parameterValues = (ParameterSettingValues) SpUtil.getObjectFromShare(getApplicationContext(), Constants.KEY_PARAMTER);
        /*
         * 本地没有存储设置的参数，设置默认值
         * There is no parameter stored locally, set the default value
         * */
        if (parameterValues != null) {
            ParameterSettingValues.setParameterValues(parameterValues);
        }

        initFragmentAndView();
        NvsStreamingContext.SdkVersion sdkVersion = NvsStreamingContext.getInstance().getSdkVersion();
//        mainVersionNumber.setText(String.format(getResources().getString(R.string.versionNumber), sdkVersion.majorVersion + "." + sdkVersion.minorVersion + "." + sdkVersion.revisionNumber));
        mainVersionNumber.setText("V 1.0.0");
    }

    private void updateLicenseFile() {
        if (!NetWorkUtil.isNetworkConnected(mContext)) {
            ToastUtil.showToast(MainActivity.this, R.string.network_not_available);
            return;
        }

        String url = Constants.LICENSE_FILE_URL;
        OkHttpClientManager.getAsyn(url, new ResultCallback<LicenseInfo>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(LicenseInfo response) {
                if (response == null) {
                    return;
                }
                int code = response.getCode();
                if (code != 1) {
                    return;
                }
                LicenseInfo.LicInfo data = response.getData();
                if (data == null) {
                    return;
                }

                SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_START_TIMESTAMP, data.getStartTimestamp());
                SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_END_TIMESTAMP, data.getEndTimestamp());
                SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_URL, data.getAuthorizationFileUrl());

                Log.d(TAG, "授权文件数据更新成功");

                String authorizationFileUrl = data.getAuthorizationFileUrl();
                if (TextUtils.isEmpty(authorizationFileUrl)) {
                    return;
                }
                downloadAuthorFile(authorizationFileUrl);
            }
        });
    }

    private void downloadAuthorFile(String fileUrl) {
        //进行数据下载
        OkHttpClientManager.downloadAsyn(fileUrl, PathUtils.getLicenseFileFolder(), new ResultCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "授权文件下载成功 " + "onResponse------------------------------" + response);
                if (response == null) {
                    return;
                }
                SharedPreferencesUtils.setParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_PATH, response);
                //下载成功之后进行授权
                Log.d(TAG, "开始授权 " + "response------------------------------" + response);
                initARSceneEffect(response);
            }
        });
    }

    private void initFragmentAndView() {
        /*
         * 按照每页个数，生成索引和名称，关系映射
         * According to the number of pages, generate the index and name, and the relationship mapping
         * */
        Map<Integer, List<String>> map = subListByItemCount();
        List<Fragment> mFragmentList = getSupportFragmentManager().getFragments();
        if (mFragmentList == null || mFragmentList.size() == 0) {
            mFragmentList = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                List<String> nameList = map.get(i);
                MainViewPagerFragment mediaFragment = new MainViewPagerFragment();
                Bundle bundle = new Bundle();
                /*
                 *  功能图标，实体类集合
                 * Function icon, entity class collection
                 * */
                ArrayList<MainViewPagerFragmentData> list = initFragmentDataById(nameList, i);
                bundle.putParcelableArrayList("list", list);
                mediaFragment.setArguments(bundle);
                mFragmentList.add(mediaFragment);
            }
        }
    }

    /**
     * 生成每页显示的功能标题集合
     * <p>
     * Generate a collection of feature titles displayed per page
     *
     * @return 索引和对应索引页的标题集合；Index and title set of corresponding index pages
     */
    private Map<Integer, List<String>> subListByItemCount() {
        String[] fragmentItems = getResources().getStringArray(R.array.main_fragment_item);
        Map<Integer, List<String>> map = new HashMap<>();
        List<String> list = Arrays.asList(fragmentItems);
        int spanCount = 8;
        int count = list.size() / spanCount + 1;
        for (int i = 0; i < count; i++) {
            int endTime = Math.min(list.size(), (i + 1) * spanCount);
            int startTime = i == 0 ? i : i * spanCount;
            List<String> childList = list.subList(startTime, endTime);
            map.put(i, childList);
        }
        return map;
    }

    private RadioButton initRadioButton(int i) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(getResources().getIdentifier("main_radioButton" + i, "id", getPackageName()));
        radioButton.setBackground(getResources().getDrawable(R.drawable.activity_main_checkbox_background));
        radioButton.setButtonDrawable(null);
        radioButton.setChecked(i == 0);
        return radioButton;
    }

    /**
     * @param names
     * @param fragmentCount 当前功能页索引；Index of current feature page
     * @return
     */
    private ArrayList<MainViewPagerFragmentData> initFragmentDataById(List<String> names, int fragmentCount) {
        /*
         * 当前页功能模块背景
         * Function page background of current page
         * */
        String[] fragmentItemsBackGround = getResources().getStringArray(R.array.main_fragment_background);
        List<String> listBackground = Arrays.asList(fragmentItemsBackGround);

        /*
         * 当前页功能模块图标
         * Current page function module icon
         * */
        String[] fragmentItemsImage = getResources().getStringArray(R.array.main_fragment_image);
        List<String> listImage = Arrays.asList(fragmentItemsImage);

        /*
         * 生成当前页面功能模块，实体类集合
         * Generate current page function module, entity class collection
         * */
        ArrayList<MainViewPagerFragmentData> list1 = new ArrayList<>();
        for (int i = 0, size = names.size(); i < size; i++) {
            int backGroundId = getResources().getIdentifier(listBackground.get(fragmentCount * 8 + i), "drawable", getPackageName());
            int imageId = getResources().getIdentifier(listImage.get(fragmentCount * 8 + i), "drawable", getPackageName());
            if (backGroundId != 0 && imageId != 0) {
                list1.add(new MainViewPagerFragmentData(backGroundId, names.get(i), imageId));
            }
        }
        return list1;
    }

    private void setRadioButtonState(int position) {
        RadioButton radioButton = (RadioButton) findViewById(getResources().getIdentifier("main_radioButton" + position, "id", getPackageName()));
        radioButton.setChecked(true);
    }

    @Override
    protected void initListener() {
        mIvSetting.setOnClickListener(this);
        layoutVideoCapture.setOnClickListener(this);
        layoutVideoEdit.setOnClickListener(this);
        checkPermissions();
        if (hasAllPermission()) {
            checkAuthorization();
            showPrivacyDialog();
        }
    }

    /**
     * 检查授权的方法，不同类型使用不同的授权。
     * 商汤的授权使用同一个授权文件，不区分普通或者高级
     */
    private void checkAuthorization() {
        if (BuildConfig.HUMAN_AI_TYPE.contains(BUILD_HUMAN_AI_TYPE_MS_ST)) {
            //商汤授权
            long param = (long) SharedPreferencesUtils.getParam(mContext, Constants.KEY_SHARED_END_TIMESTAMP, 0L);
            long currentTimeMillis = System.currentTimeMillis();
            if (param == 0) {
                Log.d(TAG, "需要更新,更新完直接下载 param=" + param);
                updateLicenseFile();
            } else {
                if (currentTimeMillis < param) {
                    String licenseFilePath = (String) SharedPreferencesUtils.getParam(mContext, Constants.KEY_SHARED_AUTHOR_FILE_PATH, "");
                    Log.d(TAG, "不需要更新授权文件 直接进行授权 licenseFilePath:" + licenseFilePath);
                    if (TextUtils.isEmpty(licenseFilePath)) {
                        updateLicenseFile();
                        return;
                    }
                    File file = new File(licenseFilePath);
                    if (!file.exists()) {
                        updateLicenseFile();
                        return;
                    }
                    initARSceneEffect(licenseFilePath);
                } else {
                    Log.d(TAG, "需要更新,更新完直接下载 param=" + param);
                    updateLicenseFile();
                }
            }
        } else {
            //非商汤授权
            /*
             * 初始化人脸Model
             * Initialize Face Model
             * */
            initARSceneEffect();
        }
    }

    @Override
    public void onClick(View view) {
        if (isClickRepeat) {
            return;
        }
        isClickRepeat = true;
        /*
         * 设置
         * Set up
         * */
        if (view.getId() == R.id.iv_main_setting) {
            AppManager.getInstance().jumpActivity(this, ParameterSettingActivity.class, null);
            return;
        }
        /*
         * 没有权限，则请求权限
         * No permission, request permission
         * */
        if (!hasAllPermission()) {
            clickedView = view;
            checkPermissions();
        } else {
            doClick(view);
        }
    }

    private void initARSceneEffect() {
        initARSceneEffect("");
    }

    private void doClick(View view) {
        if (view == null)
            return;
        switch (view.getId()) {
            case R.id.iv_main_setting://setting
                AppManager.getInstance().jumpActivity(this, ParameterSettingActivity.class, null);
                break;

            case R.id.layout_video_capture://Shoot
                if (!initARSceneing) {
                    Bundle captureBundle = new Bundle();
                    captureBundle.putBoolean("initArScene", arSceneFinished);
                    AppManager.getInstance().jumpActivity(this, CaptureActivity.class, captureBundle);
                } else {
                    isClickRepeat = false;
                    ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                }
                break;

            case R.id.layout_video_edit://Edit
                Bundle editBundle = new Bundle();
                editBundle.putInt("visitMethod", Constants.FROMMAINACTIVITYTOVISIT);
                editBundle.putInt("limitMediaCount", -1);//-1表示无限可选择素材
                AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, editBundle);
                break;
            default:
                String tag = (String) view.getTag();
                if (tag.equals(getResources().getString(R.string.douYinEffects))) {
                    if (!initARSceneing) {
                        Bundle douyinBundle = new Bundle();
                        douyinBundle.putBoolean("initArScene", arSceneFinished);
                        douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_STRENGTH, 75);
                        if (arSceneFinished) {
                            douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_CHEEK, 150);
                            douyinBundle.putInt(DouVideoCaptureActivity.INTENT_KEY_EYE, 150);
                        }
                        AppManager.getInstance().jumpActivity(this, DouVideoCaptureActivity.class, douyinBundle);
                    } else {
                        isClickRepeat = false;
                        ToastUtil.showToast(MainActivity.this, R.string.initArsence);
                    }
                } else if (tag.equals(getResources().getString(R.string.particleEffects))) {
                    AppManager.getInstance().jumpActivity(this, ParticleCaptureActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.captureScene))) {
                    AppManager.getInstance().jumpActivity(this, CaptureSceneActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.picInPic))) {
                    Bundle pipBundle = new Bundle();
                    pipBundle.putInt("visitMethod", Constants.FROMPICINPICACTIVITYTOVISIT);
                    /*
                     * 2表示选择两个素材
                     * 2 means select two materials
                     * */
                    pipBundle.putInt("limitMediaCount", 2); //
                    AppManager.getInstance().jumpActivity(this, SelectMediaActivity.class, pipBundle);
                } else if (tag.equals(getResources().getString(R.string.makingCover))) {
                    Bundle makeCoverBundle = new Bundle();
                    makeCoverBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_IMAGE_FROM_MAKE_COVER);
                    AppManager.getInstance().jumpActivity(this, SingleClickActivity.class, makeCoverBundle);
                } else if (tag.equals(getResources().getString(R.string.flipSubtitles))) {
                    Bundle flipBundle = new Bundle();
                    flipBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_FLIP_CAPTION);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    flipBundle.putInt("limitMediaCount", -1);
                    flipBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, flipBundle);
                } else if (tag.equals(getResources().getString(R.string.musicLyrics))) {
                    Bundle musicBundle = new Bundle();
                    musicBundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_MUSIC_LYRICS);
                    /*
                     * -1表示无限可选择素材
                     * -1 means unlimited selectable material
                     * */
                    musicBundle.putInt("limitMediaCount", -1);
                    musicBundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.VIDEO);
                    AppManager.getInstance().jumpActivity(this, MultiVideoSelectActivity.class, musicBundle);
                } else if (tag.equals(getResources().getString(R.string.boomRang))) {
                    AppManager.getInstance().jumpActivity(this, BoomRangActivity.class);
                } else if (tag.equals(getResources().getString(R.string.pushMirrorFilm))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        AppManager.getInstance().jumpActivity(this, SuperZoomActivity.class);
                    } else {
                        String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                        Util.showDialog(MainActivity.this, tipsInfo[0], getString(R.string.versionBelowTip));
                    }
                } else if (tag.equals(getResources().getString(R.string.photosAlbum))) {
                    AppManager.getInstance().jumpActivity(this, PhotoAlbumActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.flashEffect))) {
                    AppManager.getInstance().jumpActivity(this, GlitterEffectActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.mimo))) {
                    AppManager.getInstance().jumpActivity(this, MyStoryActivity.class, null);
                } else if (tag.equals(getResources().getString(R.string.theme_shoot))) {
                    AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), ThemeSelectActivity.class, null);
                } else {
                    String[] tipsInfo = getResources().getStringArray(R.array.edit_function_tips);
                    Util.showDialog(MainActivity.this, tipsInfo[0], tipsInfo[1], tipsInfo[2]);
                }
                break;
        }
    }

    private void initARSceneEffect(final String stLicenseFilePath) {
      /*  SenseArMaterialService.setServerType(SenseArServerType.DomesticServer);
        SenseArMaterialService.shareInstance().fetchAllGroups(new SenseArMaterialService.FetchGroupsListener() {
            @Override
            public void onSuccess(List<SenseArMaterialGroupId> list) {
                Log.e(TAG,"onSuccess  == s"+list.size());
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG,"onFailure  == s"+s);
            }
        });
        SenseArMaterialService.shareInstance().
        SenseArMaterialService.shareInstance().initialize(this);
        byte[] licData = SenseArMaterialService.shareInstance().getLicenseData();
        Log.e(TAG,"licData  =="+licData);*/
        mCanUseARFaceType = NvsStreamingContext.hasARModule();
        /*
         *  初始化AR Scene，全局只需一次
         * Initialize AR Scene, only once globally
         * */
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && !arSceneFinished) {
            if (mHandlerThread == null) {
                mHandlerThread = new HandlerThread("handlerThread");
                mHandlerThread.start();
            }
            Handler initHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String modelPath = null;
                    String licensePath = null;
                    String faceModelName = null;
                    String className = null;
                    if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
                        modelPath = "/facemode/ms/ms_face_v1.1.3.model";
                        faceModelName = "ms_face_v1.1.3.model";
                        className = "facemode/ms";
                        licensePath = "";
                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST)) {
                        modelPath = "/facemode/st/106/M_SenseME_Face_Video_5.3.4.model";
                        faceModelName = "M_SenseME_Face_Video_5.3.4.model";
                        className = "facemode/st/106";
                        licensePath = stLicenseFilePath;

                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_FU)) {
                        modelPath = "/facemode/fu/fu_face_v3.model";
                        faceModelName = "fu_face_v3.model";
                        className = "facemode/fu";
                        licensePath = "assets:/facemode/fu/fu_face_v3.license";
                    } else if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST_SUPER)) {
                        modelPath = "/facemode/st/advance/M_SenseME_Face_Video_7.1.0.model";
                        faceModelName = "M_SenseME_Face_Video_7.1.0.model";
                        className = "facemode/st/advance";
                        licensePath = stLicenseFilePath;
                    }

                    boolean copySuccess = FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, className);
                    Logger.e(TAG, "copySuccess-->" + copySuccess);

                    File rootDir = getApplicationContext().getExternalFilesDir(null);
                    String destModelDir = rootDir + modelPath;
                    boolean initSuccess = NvsStreamingContext.initHumanDetection(MSApplication.getmContext(),
                            destModelDir, licensePath,
                            NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK | NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION);
                    Logger.e(TAG, "initSuccess-->" + initSuccess);
                    if (BuildConfig.FACE_MODEL == 240) {
                        modelPath = rootDir + "/facemode/st/240/M_SenseME_Face_Extra_Advanced_6.0.8.model";
                        faceModelName = "M_SenseME_Face_Extra_Advanced_6.0.8.model";
                        String className240 = "facemode/st/240";
                        FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, className240);
                        boolean initHumanDetectionExt = NvsStreamingContext.initHumanDetectionExt(MSApplication.getmContext(),
                                modelPath,
                                null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_EXTRA);
                        Log.e(TAG, "handleMessage: initHumanDetectionExt " + initHumanDetectionExt);
                    }
                    if (BuildConfig.HUMAN_AI_TYPE.contains(BUILD_HUMAN_AI_TYPE_MS_ST)) {
                        modelPath = rootDir + "/facemode/st/common/M_SenseME_Segment_4.12.11.model";
                        faceModelName = "M_SenseME_Segment_4.12.11.model";
                        String segmentModel = "facemode/st/common";
                        boolean copySuccess2 = FileUtils.copyFileIfNeed(MainActivity.this, faceModelName, segmentModel);
                        NvsStreamingContext.initHumanDetectionExt(MSApplication.getmContext(),
                                modelPath,
                                null,
                                NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEGMENTATION_BACKGROUND);
                    }

                    String fakefacePath = "assets:/facemode/common/fakeface.dat";
                    boolean fakefaceSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_FAKE_FACE, fakefacePath);
                    Logger.e(TAG, "fakefaceSuccess-->" + fakefaceSuccess);
                    String maleupPath = "assets:/facemode/common/makeup_v1.0.0.dat";
                    if (BuildConfig.FACE_MODEL == 240) {
                        maleupPath = "assets:/facemode/st/240/makeup240_v1.0.0.dat";
                    }
                    boolean makeupSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP, maleupPath);
                    Logger.e(TAG, "makeupSuccess-->" + makeupSuccess);
                    //240设置的
                    if (BuildConfig.FACE_MODEL == 240) {
                        String pePath = "assets:/facemode/st/240/pe240_st_v1.0.0.dat";
                        boolean peSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240, pePath);
                        Logger.e(TAG, "peSuccess-->" + peSuccess);
                    }
                    if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS_ST_SUPER)) {
                        String pePath = "assets:/facemode/st/advance/pe106_advanced_st_v1.0.0.dat";
                        boolean peSuccess = NvsStreamingContext.setupHumanDetectionData(NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE106, pePath);
                        Logger.e(TAG, "peSuccess-->" + peSuccess);
                    }
                    if (initSuccess) {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_COMPLETE_CODE);
                    } else {
                        mHandler.sendEmptyMessage(INIT_ARSCENE_FAILURE_CODE);
                    }
                    return false;
                }
            });
            initHandler.sendEmptyMessage(1);
        } else {
            initARSceneing = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (hasAllPermission()) {
            Util.clearRecordAudioData();
        }
        // 退出清理
        if (mStreamingContext != null) {
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                NvsStreamingContext.closeHumanDetection();
            }
            NvsStreamingContext.close();
            mStreamingContext = null;
            TimelineData.instance().clear();
            BackupData.instance().clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClickRepeat = false;
    }

    /**
     * 获取activity需要的权限列表
     * Get the list of permissions required by the activity
     *
     * @return 权限列表;Permission list
     */
    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    /**
     * 获取权限
     * Get permission
     */
    @Override
    protected void hasPermission() {
        Log.e(TAG, "hasPermission: 所有权限都有了");
        checkAuthorization();
        doClick(clickedView);
        showPrivacyDialog();
    }

    /**
     * 没有允许权限
     * No permission
     */
    @Override
    protected void nonePermission() {
        Log.e(TAG, "hasPermission: 没有允许权限");
    }

    /**
     * 用户选择了不再提示
     * The user chose not to prompt again
     */
    @Override
    protected void noPromptPermission() {
        Log.e(TAG, "hasPermission: 用户选择了不再提示");
        startAppSettings();
    }

    /*
     * 启动应用的设置
     * Launch app settings
     * */
    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MobclickAgent.onKillProcess(this);
    }

    private void showPrivacyDialog() {
        final SpUtil spUtil = SpUtil.getInstance(getApplicationContext());
        boolean isAgreePrivacy = spUtil.getBoolean(Constants.KEY_AGREE_PRIVACY, false);
        if (!isAgreePrivacy) {
            PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(MainActivity.this, R.style.dialog);
            privacyPolicyDialog.setOnButtonClickListener(new PrivacyPolicyDialog.OnPrivacyClickListener() {
                @Override
                public void onButtonClick(boolean isAgree) {
                    spUtil.putBoolean(Constants.KEY_AGREE_PRIVACY, isAgree);
                    if (!isAgree) {
                        AppManager.getInstance().finishActivity();
                    }
                }

                @Override
                public void pageJumpToWeb(String clickTextContent) {
                    String serviceAgreement = getString(R.string.service_agreement);
                    String privacyPolicy = getString(R.string.privacy_policy);
                    String visitUrl = "";
                    if (clickTextContent.contains(serviceAgreement)) {
                        visitUrl = Constants.SERVICE_AGREEMENT_URL;
                    } else if (clickTextContent.contains(privacyPolicy)) {
                        visitUrl = Constants.PRIVACY_POLICY_URL;
                    }
                    if (TextUtils.isEmpty(visitUrl)) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", visitUrl);
                    AppManager.getInstance().jumpActivity(MainActivity.this, MainWebViewActivity.class, bundle);
                }
            });
            privacyPolicyDialog.show();
        }
    }
}

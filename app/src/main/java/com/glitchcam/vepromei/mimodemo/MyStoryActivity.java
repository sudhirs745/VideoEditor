package com.glitchcam.vepromei.mimodemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.meicam.sdk.NvsTimeline;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.capturescene.httputils.NetWorkUtil;
import com.glitchcam.vepromei.capturescene.httputils.OkHttpClientManager;
import com.glitchcam.vepromei.capturescene.httputils.ResultCallback;
import com.glitchcam.vepromei.capturescene.httputils.download.DownLoadResultCallBack;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.view.dialog.DownloadDialog;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.bean.MimoOnlineData;
import com.glitchcam.vepromei.mimodemo.common.template.model.TempJsonInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.TemplateInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.utils.ParseJsonFile;
import com.glitchcam.vepromei.mimodemo.common.utils.TimelineUtil;
import com.glitchcam.vepromei.mimodemo.common.utils.asset.NvAsset;
import com.glitchcam.vepromei.mimodemo.common.utils.asset.NvAssetManager;
import com.glitchcam.vepromei.mimodemo.interf.OnTemplateSelectListener;
import com.glitchcam.vepromei.mimodemo.mediapaker.SelectMediaActivity;
import com.glitchcam.vepromei.utils.FileUtils;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.MimoFileDataUtil;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.ToastUtil;
import com.glitchcam.vepromei.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class MyStoryActivity extends BaseActivity {
    private String TAG = "MyStoryActivity";
    private static final int DOWN_LOAD_TYPE_ZIP = 201;
    private static final int DOWN_LOAD_TYPE_VIDEO = 202;
    private VideoFragment mVideoFragment;
    private CustomTitleBar customTitleBar;
    private final String URL = "https://vsapi.meishesdk.com/app/index.php?command=listMimoMaterial&page=0&pageSize=1000";
    private String mDownloadPath;
    private List<MimoOnlineData.MimoOnlineDataDetails> onlineDataDetails;
    private TemplateListFragment templateListFragment;
    private DownloadDialog mDownloadDialog;
    private MiMoLocalData mCurrentData;
    private RelativeLayout mLoadingView;
    private Map<String, MiMoLocalData> downloadingMp4 = new HashMap<>();

    /**
     * 重置数据，清除用户选择数据，保留模板数据
     */
    private void resetTemplateData() {
        MiMoLocalData selectTemplate = NvTemplateContext.getInstance().getSelectedMimoData();
        if (selectTemplate == null) {
            return;
        }
        selectTemplate.resetTemplateVideoInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuildTimeLineForPlayer();
        resetTemplateData();
    }

    @Override
    protected int initRootView() {
        return R.layout.activity_my_story;
    }

    @Override
    protected void initViews() {
        NvAssetManager.init(this);
        customTitleBar = findViewById(R.id.title);
        mLoadingView = findViewById(R.id.loading_layout);
        customTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {
            @Override
            public void OnBackImageClick() {

            }

            @Override
            public void OnCenterTextClick() {

            }

            @Override
            public void OnRightTextClick() {

            }
        });

    }

    private void initBottomFragment(List<MiMoLocalData> templateInfos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        templateListFragment = new TemplateListFragment(templateInfos);
        fragmentManager.beginTransaction().add(R.id.bottom_layout, templateListFragment).commit();
        fragmentManager.beginTransaction().show(templateListFragment);
        templateListFragment.setOnTemplateSelectListener(new OnTemplateSelectListener() {
            @Override
            public void onTemplateSelected(int position) {
                rebuildTimeLineForPlayer();
            }

            @Override
            public void onTemplateConfirm() {
                mCurrentData = templateListFragment.getCurrentData();
                if(mCurrentData == null) {
                    Logger.e(TAG, "MIMO template data is null");
                    return;
                }
                if (mCurrentData.isLocal()) {
                    NvTemplateContext.getInstance().setSelectedMimoData(mCurrentData);
                    installPackageSource(mCurrentData);
                    goSelectMedia();
                } else {
                    downloadPackage(mCurrentData.getPackageUrl(), DOWN_LOAD_TYPE_ZIP);
                }
            }
        });
        if (mDataListLocals != null && mDataListLocals.size() > 0) {
            mCurrentData = mDataListLocals.get(0);
        }
    }

    private void installPackageSource(MiMoLocalData moLocalData) {
        if (moLocalData == null) {
            return;
        }
        String rootPath = moLocalData.getSourceDir();
        if (TextUtils.isEmpty(rootPath)) {
            return;
        }
        File file = new File(rootPath);
        if (!file.exists()) {
            return;
        }

        File[] subFile = file.listFiles();
        if (subFile == null) {
            return;
        }
        for (int i = 0; i < subFile.length; i++) {
            String packagePath = subFile[i].getAbsolutePath().trim();
            if(TextUtils.isEmpty(packagePath)){
                continue;
            }
            if(packagePath.endsWith(NvAsset.SUFFIX_ANIMATED_STICKER)){
                NvAssetManager.sharedInstance().installAssetPackage(packagePath,NvAsset.ASSET_ANIMATED_STICKER,false);
            }else if(packagePath.endsWith(NvAsset.SUFFIX_VIDEO_FX)){
                NvAssetManager.sharedInstance().installAssetPackage(packagePath,NvAsset.ASSET_FILTER,false);
            }else if(packagePath.endsWith(NvAsset.SUFFIX_VIDEOTRANSITION)){
                NvAssetManager.sharedInstance().installAssetPackage(packagePath,NvAsset.ASSET_VIDEO_TRANSITION,false);
            } else if (packagePath.endsWith(NvAsset.SUFFIX_COMPOUNDCAPTION)){
                NvAssetManager.sharedInstance().installAssetPackage(packagePath,NvAsset.ASSET_COMPOUND_CAPTION,false);
            }
        }
    }

    /**
     * 跳转素材选择界面
     */
    private void goSelectMedia() {
        Intent intent = new Intent(MyStoryActivity.this, SelectMediaActivity.class);
        startActivity(intent);
    }

    private void rebuildTimeLineForPlayer() {
        mVideoFragment.stopEngine();
        boolean isSucesss = resetEngine();
        if(!isSucesss) {
            mVideoFragment.removeTimeLine();
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mVideoFragment.playVideoFromStartPosition();
            }
        });
    }

    private void initVideoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mVideoFragment = VideoFragment.newInstance(0L);
        fragmentManager.beginTransaction().add(R.id.videoLayout, mVideoFragment).commit();
        fragmentManager.beginTransaction().show(mVideoFragment);
    }

    private boolean resetEngine() {
        mCurrentData = templateListFragment.getCurrentData();
        if (mCurrentData == null) {
            return false;
        }
        NvsTimeline timeline;
        String videoPath = mCurrentData.getVideoPath();
        if(TextUtils.isEmpty(videoPath)) {
            String videoUrl = mCurrentData.getVideoUrl();
            String cachePath = PathUtils.getMimoPreviewVideoPath(MyStoryActivity.this, videoUrl);
            File cacheVideoFile = new File(cachePath);
            if(cacheVideoFile.exists()) {
                mCurrentData.setVideoPath(cachePath);
                videoPath = cachePath;
            } else {
                downloadingMp4.put(cachePath, mCurrentData);
                downloadPackage(videoUrl, DOWN_LOAD_TYPE_VIDEO);
                return false;
            }
        }
        if (mVideoFragment.getTimeLine() == null) {
            timeline = TimelineUtil.createTimeline(videoPath);
        } else {
            timeline = TimelineUtil.reBuildSingleVideoTrack(mVideoFragment.getTimeLine(), videoPath);
        }

        mVideoFragment.setTimeLine(timeline);
        mVideoFragment.initData();
        return true;
    }


    private void installFont() {
        String fontJsonPath = "font/info_mimo.json";
        String fontJsonText = ParseJsonFile.readAssetJsonFile(this, fontJsonPath);
        if (TextUtils.isEmpty(fontJsonText)) {
            return;
        }
        TempJsonInfo fontJsonInfo = ParseJsonFile.fromJson(fontJsonText, TempJsonInfo.class);
        if (fontJsonInfo == null) {
            return;
        }
        List<TempJsonInfo.JsonInfo> fontList = fontJsonInfo.getJsonList();
        if (fontList == null || fontList.isEmpty()) {
            return;
        }
        int fontCount = fontList.size();
        for (int idx = 0; idx < fontCount; idx++) {
            TempJsonInfo.JsonInfo fontInfo = fontList.get(idx);
            if (fontInfo == null) {
                continue;
            }
            String fontAssetPath = "assets:/font/" + fontInfo.jsonPath;
            String fontName = mStreamingContext.registerFontByFilePath(fontAssetPath);
            Log.d(TAG, "fontName = " + fontName);
        }
    }

    @Override
    protected void initTitle() {
    }

    /**
     * 取消弹窗
     */
    private void cancelDownloads() {
        for (String url : downloadingURL.keySet()) {
            OkHttpClientManager.cancelTag(url);
        }
    }

    @Override
    protected void initData() {
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads();
            }
        });
        //拿到保存本地的文件路径
        mDownloadPath = PathUtils.getAssetDownloadPath(com.glitchcam.vepromei.utils.asset.NvAsset.ASSET_MIMO) + File.separator;
        installFont();
        NvTemplateContext.init(this.getApplicationContext());
        initVideoFragment();
        getLocalData();
        initBottomFragment(mDataListLocals);
        getHttpData();
    }

    private List<MiMoLocalData> mDataListLocals = new ArrayList<>();

    private void getLocalData() {
        if (mDownloadPath == null || mDownloadPath.isEmpty()) {
            return;
        }
        File file = new File(mDownloadPath);
        if (!file.exists()) {
            return;
        }
        File[] subFile = file.listFiles();
        if (subFile == null) {
            return;
        }
        mDataListLocals.clear();
        for (int i = 0; i < subFile.length; ++i) {
            File dir = subFile[i];
            if (!dir.isDirectory()) {
                continue;
            }
            String dirName = dir.getName();
            MiMoLocalData localData = getMimoLolalDataFromInfo(dir.getAbsolutePath());
            if (localData != null) {
                localData.setId(dirName);
                localData.setSourceDir(dir.getAbsolutePath());
                localData.setLocal(true);
                MimoFileDataUtil.updateShotClipInfos(localData);
                localData.updateTotalShotVideoInfos();
                mDataListLocals.add(localData);
            }
        }
    }

    private MiMoLocalData getMimoLolalDataFromInfo(String dirPath) {
        MiMoLocalData miMoLocalData = null;
        File infoFile = new File(dirPath, "info.json");
        if(!infoFile.exists()) {
            return miMoLocalData;
        }
        try {
            String read_json = Util.loadFromSDFile(infoFile.getPath());
            miMoLocalData = mGson.fromJson(read_json, MiMoLocalData.class);
            if(miMoLocalData != null) {
                miMoLocalData.setCoverUrl(dirPath + File.separator + miMoLocalData.getCover());
                miMoLocalData.setVideoPath(dirPath + File.separator + miMoLocalData.getPreview());
                miMoLocalData.setMusicFilePath(dirPath + File.separator + miMoLocalData.getMusic());
            }
        } catch (Exception e) {
            Log.e(TAG, "phase info.json exception!");
        }
        return miMoLocalData;
    }

    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            initCacheDatas();
            mLoadingView.setVisibility(View.GONE);
            if(mDataListLocals.size() == 0) {
                ToastUtil.showToast(this, R.string.check_network);
            }
            return;
        }
        OkHttpClientManager.getAsyn(URL, new ResultCallback<MimoOnlineData>() {
            @Override
            public void onError(Request request, Exception e) {
                mLoadingView.setVisibility(View.GONE);
                ToastUtil.showToast(MyStoryActivity.this, R.string.network_strayed_try);
            }

            @Override
            public void onResponse(MimoOnlineData response) {
                if (response != null) {
                    String path = PathUtils.getMimoCacheFolderPath(MyStoryActivity.this);
                    File file = new File(path, "info.json");
                    if(!file.exists()) {
                        ParseJsonFile.saveObjectByJson(file, response);
                    }
                    onlineDataDetails = response.getList();
                    initOnlineDatas();
                }
                mLoadingView.setVisibility(View.GONE);
            }
        });
    }

    private Gson mGson = new Gson();

    private void initCacheDatas() {
        try {
            String path = PathUtils.getMimoCacheFolderPath(MyStoryActivity.this);
            File file = new File(path, "info.json");
            if(!file.exists()) {
               return;
            }
            MimoOnlineData mimoOnlineData = (MimoOnlineData)ParseJsonFile.getObjectByJson(file, MimoOnlineData.class);
            onlineDataDetails = mimoOnlineData.getList();
            initOnlineDatas();
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.toString());
        }
    }

    /**
     * 根据线上显示初始化
     */
    private void initOnlineDatas() {
        if (onlineDataDetails == null || onlineDataDetails.isEmpty()) {
            return;
        }
        List<TemplateInfo> templateData = new ArrayList<>();
        for (int i = 0; i < onlineDataDetails.size(); i++) {
            MimoOnlineData.MimoOnlineDataDetails dataDetail = onlineDataDetails.get(i);
            if (!isExistInLocal(dataDetail.getUuid())) {
                String miMoJson = dataDetail.getPackageInfo();
                try {
                    MiMoLocalData miMoLocalData = mGson.fromJson(miMoJson, MiMoLocalData.class);
                    if (miMoLocalData == null) {
                        continue;
                    }
                    miMoLocalData.setPackageUrl(dataDetail.getPackageUrl());
                    miMoLocalData.setUuid(dataDetail.getUuid());
                    miMoLocalData.setVideoUrl(dataDetail.getVideoUrl());
                    miMoLocalData.setCoverUrl(dataDetail.getCoverUrl());
                    miMoLocalData.setLocal(false);
                    mDataListLocals.add(miMoLocalData);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        refreshDatas();
        if(mCurrentData == null) {
            rebuildTimeLineForPlayer();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshDatas() {
        templateListFragment.setNewDatas(mDataListLocals);
    }

    /**
     * 是否存在本地
     */
    private boolean isExistInLocal(String id) {
        if (!TextUtils.isEmpty(id)) {
            for (MiMoLocalData localData : mDataListLocals) {
                if (localData == null) {
                    continue;
                }
                if (id.equals(localData.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>();

    private void downloadPackage(final String packageUrl, final int loadType) {
        String downloadPath = mDownloadPath;
        if(loadType == DOWN_LOAD_TYPE_VIDEO) {
            mLoadingView.setVisibility(View.VISIBLE);
            downloadPath = PathUtils.getMimoCacheFolderPath(MyStoryActivity.this);
        } else {
            if (mDownloadDialog != null) {
                mDownloadDialog.show();
            }
            downloadingURL.put(packageUrl, mDownloadPath + mCurrentData.getId());
        }
        OkHttpClientManager.downloadAsyn(packageUrl, downloadPath, new DownLoadResultCallBack<String>() {
            @Override
            public void onProgress(long now, long total, int progress) {
                if(loadType == DOWN_LOAD_TYPE_ZIP) {
                    mDownloadDialog.setProgress(progress);
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Logger.e(TAG, "downloadPackageOnError: " + e.toString());
                if(loadType == DOWN_LOAD_TYPE_VIDEO) {
                    mLoadingView.setVisibility(View.GONE);
                } else {
                    mDownloadDialog.dismiss();
                    mDownloadDialog.setProgress(0);
                    deleteFiles(downloadingURL.get(packageUrl));
                    downloadingURL.remove(packageUrl);
                }
                ToastUtil.showToast(MyStoryActivity.this, R.string.check_network);
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                Logger.e(TAG, "download  onResponse: " +response  + "  loadType " + loadType);
                if(loadType == DOWN_LOAD_TYPE_VIDEO) {
                    MiMoLocalData miMoLocalData = downloadingMp4.get(response);
                    if(miMoLocalData != null) {
                        downloadingMp4.remove(response);
                        miMoLocalData.setVideoPath(response);
                        if(miMoLocalData == mCurrentData) {
                            rebuildTimeLineForPlayer();
                        }
                    }
                    mLoadingView.setVisibility(View.GONE);
                } else {
                    mDownloadDialog.dismiss();
                    mDownloadDialog.setProgress(0);
                    Logger.e(TAG, "onResponse: " + response);
                    downloadingURL.remove(packageUrl);

                    PathUtils.unZipFile(response, mDownloadPath);

                    String onePackageDir = FileUtils.getFileNameNoEx(response);
                    File file = new File(onePackageDir);
                    MiMoLocalData localData = getMimoLolalDataFromInfo(onePackageDir);
                    if (localData != null) {
                        localData.setId(file.getName());
                        localData.setSourceDir(onePackageDir);
                        localData.setLocal(true);
                        MimoFileDataUtil.updateShotClipInfos(localData);
                        localData.updateTotalShotVideoInfos();
                        NvTemplateContext.getInstance().setSelectedMimoData(localData);
                        for (int j = 0; j < mDataListLocals.size(); j++) {
                            MiMoLocalData tempData = mDataListLocals.get(j);
                            if(tempData.getId().equals(file.getName())) {
                                mDataListLocals.remove(j);
                                mDataListLocals.add(j, localData);
                                refreshDatas();
                                break;
                            }
                        }
                    }
                    installPackageSource(localData);
                    deleteFiles(response);
                    goSelectMedia();
                }
            }
        });
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoFragment != null) {
            mVideoFragment.stopEngine();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NvTemplateContext.getInstance().setSelectListIndex(0);
    }
}

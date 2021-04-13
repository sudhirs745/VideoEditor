package com.glitchcam.vepromei.themeshoot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.capturescene.httputils.NetWorkUtil;
import com.glitchcam.vepromei.capturescene.httputils.OkHttpClientManager;
import com.glitchcam.vepromei.capturescene.httputils.ResultCallback;
import com.glitchcam.vepromei.capturescene.httputils.download.DownLoadResultCallBack;
import com.glitchcam.vepromei.download.SquareDecoration;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.view.dialog.DownloadDialog;
import com.glitchcam.vepromei.themeshoot.bean.ThemeOnlineBean;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.themeshoot.utlils.ThemeShootUtil;
import com.glitchcam.vepromei.themeshoot.view.ThemeCapturePreviewView;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.FileUtils;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.PathNameUtil;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.asset.NvAsset;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class ThemeSelectActivity extends BaseActivity {
    private final String TAG = getClass().getName();
    private CustomTitleBar mEditCustomTitleBar;
    private RecyclerView mRecyclerView;
    //    private AssetDownloadListAdapter mAssetListAdapter;
    private ThemeListAdapt mThemeListAdapt;
    private ArrayList<NvAsset> mAssetDatalist = new ArrayList<>();
    private AlertDialog mAlertDialog;
    private ThemeCapturePreviewView mThemeCapturePreviewView;
    private List<ThemeModel> mThemeModelList;
    private final String URL = "https://vsapi.meishesdk.com/app/index.php?command=listVlogMaterial&page=0&pageSize=1000";
    private String mDownloadPath;
    private DownloadDialog mDownloadDialog;
    private ThemeModel mThemeData;

    @Override
    protected int initRootView() {
        return R.layout.activity_theme_preview;
    }

    @Override
    protected void initViews() {
        mEditCustomTitleBar = (CustomTitleBar) findViewById(R.id.title);
        mRecyclerView = findViewById(R.id.theme_template_list);
       /* mAssetListAdapter = new AssetDownloadListAdapter(this);
        mAssetListAdapter.setExtraViewType(AssetDownloadListAdapter.TYPE_THEME_CAPTURE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.addItemDecoration(new SquareDecoration());
        mRecyclerView.setAdapter(mAssetListAdapter);
        NvAsset test = new NvAsset();
        test.name = "测试";
        test.coverUrl = "http://www.juimg.com/tuku/yulantu/130903/328112-130Z315142229.jpg";
        mAssetDatalist.add(test);
        NvAsset test1 = new NvAsset();
        test1.name = "测试";
        test1.coverUrl = "http://www.juimg.com/tuku/yulantu/130903/328112-130Z315142229.jpg";
        mAssetDatalist.add(test1);
        mAssetListAdapter.setAssetDatalist(mAssetDatalist);
        mAssetListAdapter.notifyDataSetChanged();
        mAssetListAdapter.setDownloadClickerListener(new AssetDownloadListAdapter.OnDownloadClickListener() {
            @Override
            public void onItemDownloadClick(RecyclerView.ViewHolder holder, int pos) {
                showPreviewPage(mAlertDialog, mThemeCapturePreviewView);
            }
        });*/

        mThemeListAdapt = new ThemeListAdapt(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.addItemDecoration(new SquareDecoration());
        mRecyclerView.setAdapter(mThemeListAdapt);
        mThemeListAdapt.setOnItemClickListener(new ThemeListAdapt.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mThemeData = mThemeModelList.get(position);
                if (mThemeData.isLocal()) {
                    showPreviewSetPage(mThemeData);
                } else {
                    downloadPackage(mThemeData.getDownLoadPackageUrl());
                }
            }
        });
        initPreviewDialog();
        getLocalData();
        getHttpData();
    }

    private void showPreviewSetPage(ThemeModel themeModel) {
        mThemeCapturePreviewView = new ThemeCapturePreviewView(this);
        mThemeCapturePreviewView.setOnThemePreviewOperationListener(new ThemeCapturePreviewView.OnThemePreviewOperationListener() {
            @Override
            public void onPreviewClosed() {
                closeDialogView(mAlertDialog);
            }

            @Override
            public void onEnterButtonPressed(int ratioType) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ThemeModel", mThemeData);
                bundle.putSerializable("ratioType", ratioType);
                AppManager.getInstance().jumpActivity(AppManager.getInstance().currentActivity(), ThemeCaptureActivity.class, bundle);
            }
        });
        mThemeCapturePreviewView.updateThemeModelView(themeModel);
        showPreviewPage(mAlertDialog, mThemeCapturePreviewView);
    }

    /**
     * 本地获取资源
     */
    private void getLocalData() {
        mThemeModelList = ThemeShootUtil.getThemeModelListFromSdCard(this);
        if (mThemeModelList == null) {
            mThemeModelList = new ArrayList<>();
        }
    }

    /**
     * 获取网络资源
     */
    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            return;
        }
        OkHttpClientManager.getAsyn(URL, new ResultCallback<ThemeOnlineBean>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e(this.getClass().getName() + "getHttpData", "onError: get http theme data error!");
            }

            @Override
            public void onResponse(ThemeOnlineBean response) {
                Log.e(this.getClass().getName() + "getHttpData URL=", URL + "");
                //todo 网络资源尚未转义，转义完刷新数据 以服务器为准
                if (response != null) {
                    List<ThemeOnlineBean.ThemeOnlineDetail> onlineDetails = response.getList();
                    //todo 以服务器为准
                    compareLocalAndOnlineData(onlineDetails);
                }
                refreshData();
            }
        });
    }

    /**
     * 比较服务器与本地数据，以服务器数据为准，显示出来
     *
     * @param onlineDetails
     */
    private void compareLocalAndOnlineData(List<ThemeOnlineBean.ThemeOnlineDetail> onlineDetails) {
        if (onlineDetails == null) {
            return;
        }
        for (int i = 0; i < onlineDetails.size(); i++) {
            ThemeOnlineBean.ThemeOnlineDetail detail = onlineDetails.get(i);
            if (!isLocalHas(detail.getId())) {
                ThemeModel themeModel = getOnlineJsonModel(detail.getPackageInfo());
                if (themeModel == null) {
                    themeModel = new ThemeModel();
                }
                themeModel.setPreview(detail.getVideoUrl());
                themeModel.setCover(detail.getCoverUrl());
                themeModel.setLocal(false);
                themeModel.setDownLoadPackageUrl(detail.getPackageUrl());
                mThemeModelList.add(themeModel);
            }
        }
    }

    /**
     * 通过json获取themeModel
     *
     * @param infoJson
     * @return
     */
    private ThemeModel getOnlineJsonModel(String infoJson) {
        if (TextUtils.isEmpty(infoJson)) {
            return null;
        }
        try {
            ThemeModel jsonModel = new Gson().fromJson(infoJson, ThemeModel.class);
            return jsonModel;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断本地是否有线上item
     *
     * @param sign
     * @return
     */
    private boolean isLocalHas(String sign) {
        if (mThemeModelList == null || TextUtils.isEmpty(sign)) {
            return false;
        }
        for (ThemeModel themeModel : mThemeModelList) {
            if (sign.equals(themeModel.getId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>();

    /**
     * 下载网络包数据
     *
     * @param packageUrl
     */
    private void downloadPackage(final String packageUrl) {
        mDownloadDialog.show();
        downloadingURL.put(packageUrl, mDownloadPath + PathNameUtil.getPathNameWithSuffix(packageUrl));
        OkHttpClientManager.downloadAsyn(packageUrl, mDownloadPath, new DownLoadResultCallBack<String>() {
            @Override
            public void onProgress(long now, long total, int progress) {
                mDownloadDialog.setProgress(progress);
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Logger.e(TAG, "downloadPackageOnError: " + e.toString());
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                deleteFiles(mDownloadPath);
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                mDownloadDialog.dismiss();
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);

                PathUtils.unZipFile(response, mDownloadPath);
                deleteFiles(response);
                ThemeShootUtil.refreshThemeData(mThemeData, ThemeShootUtil.getLocalThemeModelByPath(FileUtils.getFileNameNoEx(response)));
                refreshData();
//                showPreviewSetPage(mThemeData);
            }
        });
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    private void refreshData() {
        mThemeListAdapt.setAssetDatalist(mThemeModelList);
        mThemeListAdapt.notifyDataSetChanged();
    }

    @Override
    protected void initTitle() {
        mEditCustomTitleBar.setTextCenter(getResources().getString(R.string.theme_shoot));
    }

    @Override
    protected void initData() {
        mDownloadPath = ThemeShootUtil.getThemeSDPath() + File.separator;
        if (mThemeModelList != null) {
            Log.d("TAG", "initData: =================themeModelList=" + mThemeModelList.size());
            mThemeListAdapt.setAssetDatalist(mThemeModelList);
            mThemeListAdapt.notifyDataSetChanged();
        }
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads();
            }
        });
    }

    private void cancelDownloads() {
        for (String url : downloadingURL.keySet()) {
            OkHttpClientManager.cancelTag(url);
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(View v) {

    }

    private void initPreviewDialog() {
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closeDialogView(mAlertDialog);
            }
        });
    }

    private void showPreviewPage(AlertDialog dialog, View view) {
        if (view == null || view == null) {
            return;
        }
        dialog.show();
        dialog.setContentView(view);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mThemeCapturePreviewView != null) {
                    mThemeCapturePreviewView.clear();
                    mThemeCapturePreviewView = null;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_fading_style);
//        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
    }

    private void closeDialogView(AlertDialog dialog) {
        if (dialog == null) {
            return;
        }
        dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mThemeCapturePreviewView != null) {
            mThemeCapturePreviewView.onResume();
        }
    }
}

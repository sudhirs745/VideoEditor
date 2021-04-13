package com.glitchcam.vepromei.photoalbum;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BasePermissionActivity;
import com.glitchcam.vepromei.capturescene.httputils.NetWorkUtil;
import com.glitchcam.vepromei.capturescene.httputils.OkHttpClientManager;
import com.glitchcam.vepromei.capturescene.httputils.ResultCallback;
import com.glitchcam.vepromei.capturescene.httputils.download.DownLoadResultCallBack;
import com.glitchcam.vepromei.edit.view.CustomTitleBar;
import com.glitchcam.vepromei.edit.view.dialog.DownloadDialog;
import com.glitchcam.vepromei.musicLyrics.MultiVideoSelectActivity;
import com.glitchcam.vepromei.photoalbum.grallyRecyclerView.CardScaleHelper;
import com.glitchcam.vepromei.photoalbum.grallyRecyclerView.GalleryAdapter;
import com.glitchcam.vepromei.photoalbum.grallyRecyclerView.GalleryRecyclerView;
import com.glitchcam.vepromei.utils.AppManager;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.FileUtils;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.MediaConstant;
import com.glitchcam.vepromei.utils.PathNameUtil;
import com.glitchcam.vepromei.utils.PathUtils;
import com.glitchcam.vepromei.utils.SystemUtils;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.asset.NvAsset;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;

public class PhotoAlbumActivity extends BasePermissionActivity {
    private final String TAG = "PhotoAlbumActivity";
    private CustomTitleBar mTitleBar;
    private GalleryRecyclerView mAlbumRv;
    private GalleryAdapter mGalleryAdapter;
    private CardScaleHelper mCardScaleHelper;
    private TextView mAlbumNameText, mAlbumTipsText, mAlbumIndexText;
    private SimpleExoPlayerWrapper playerWrapper;
    private Button mAlbumUseBtn;
    private String mDownloadPath;
    private ArrayList<PhotoAlbumData> mDataListLocal = new ArrayList<>( );
    private PhotoAlbumData mCurrentData;
    private final Gson mGson = new Gson( );
    private final String URL = "https://vsapi.meishesdk.com/app/index.php?command=listPhotoAlbumMaterial&page=0&pageSize=1000";
    private DownloadDialog mDownloadDialog;
    /**
     * 记录下载地址的数组，维护下载的完整性。
     * An array of download addresses to maintain download integrity.
     */
    private Map<String, String> downloadingURL = new HashMap<>( );

    private Context mContext = MSApplication.getmContext();
    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList( );
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    protected int initRootView() {
        return R.layout.activity_photoalbum;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAlbumRv = (GalleryRecyclerView) findViewById(R.id.albumRv);
        mAlbumNameText = (TextView) findViewById(R.id.albumNameText);
        mAlbumTipsText = (TextView) findViewById(R.id.albumTipsText);
        mAlbumUseBtn = (Button) findViewById(R.id.albumUseBtn);
        mAlbumIndexText = (TextView) findViewById(R.id.albumIndexText);
        mDownloadDialog = new DownloadDialog(this);

        mDownloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener( ) {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDownloadDialog.setProgress(0);
                cancelDownloads( );
            }
        });
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.photosAlbum);
    }

    @Override
    protected void initData() {
        playerWrapper = new SimpleExoPlayerWrapper(this);

        mDownloadPath = PathUtils.getAssetDownloadPath(NvAsset.ASSET_PHOTO_ALBUM) + File.separator;

        getLoadData( );
        getHttpData( );

        initRecyclerView( );
    }

    @Override
    protected void initListener() {
        mAlbumUseBtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mCurrentData != null && !mCurrentData.isLocal) {
                    mDownloadDialog.show( );
                    downloadPackage(mCurrentData.packageUrl);
                } else {
                    gotoSelectPictures( );
                }
            }
        });

        playerWrapper.setPlayerEventListener(new SimpleExoPlayerWrapper.PlayerEventListener( ) {
            @Override
            public void onRenderedFirstFrame() {
                if (mGalleryAdapter != null) {
                    mGalleryAdapter.updatePlayItem(mCurrentData);
                }
            }

            @Override
            public void onPlayCountChanged(int playCount) {

            }

            @Override
            public void onStateBuffering() {

            }

            @Override
            public void onStateReady() {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );

        clearDownloads( );

        PhotoAlbumConstants.albumData = null;

        if (playerWrapper != null) {
            playerWrapper.destroyPlayer( );
        }
    }

    @Override
    protected void onPause() {
        super.onPause( );

        if (playerWrapper != null) {
            playerWrapper.pause( );
        }
    }

    private void gotoSelectPictures() {
        PhotoAlbumConstants.albumData = mCurrentData;
        Bundle bundle = new Bundle( );
        bundle.putInt(Constants.SELECT_MEDIA_FROM, Constants.SELECT_VIDEO_FROM_PHOTO_ALBUM);
        if (mCurrentData != null && !TextUtils.isEmpty(mCurrentData.photosAlbumReplaceMax) && !TextUtils.isEmpty(mCurrentData.photosAlbumReplaceMin)) {
            int iMax = Integer.valueOf(mCurrentData.photosAlbumReplaceMax);
            int iMin = Integer.valueOf(mCurrentData.photosAlbumReplaceMin);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MAX, iMax);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MIN, iMin);
        } else {
            bundle.putInt(MediaConstant.LIMIT_COUNT_MAX, 10);
            bundle.putInt(MediaConstant.LIMIT_COUNT_MIN, 1);
        }
        bundle.putInt(MediaConstant.MEDIA_TYPE, MediaConstant.PICTURE);
        AppManager.getInstance( ).jumpActivity(PhotoAlbumActivity.this, MultiVideoSelectActivity.class, bundle);
    }

    private void downloadPackage(final String packageUrl) {
        downloadingURL.put(packageUrl, mDownloadPath + PathNameUtil.getPathNameWithSuffix(packageUrl));
        OkHttpClientManager.downloadAsyn(packageUrl, mDownloadPath, new DownLoadResultCallBack<String>( ) {
            @Override
            public void onProgress(long now, long total, int progress) {
                mDownloadDialog.setProgress(progress);
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                Logger.e(TAG, "downloadPackageOnError: " + e.toString( ));
                mDownloadDialog.dismiss( );
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);
                deleteFiles(mDownloadPath);
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                mDownloadDialog.dismiss( );
                mDownloadDialog.setProgress(0);
                downloadingURL.remove(packageUrl);

                PathUtils.unZipFile(response, mDownloadPath);

                String onePackageDir = FileUtils.getFileNameNoEx(response);
                File file = new File(onePackageDir);
                mCurrentData.sourceDir = file.getAbsolutePath( );
                mCurrentData.isLocal = true;
                getEachFileLocal(mCurrentData, file.listFiles( ));

                deleteFiles(response);

                if (mGalleryAdapter != null) {
                    mGalleryAdapter.updateItemData(mCurrentData);
                }
                gotoSelectPictures( );
            }
        });
    }

    private void getLoadData() {
        if (mDownloadPath == null || mDownloadPath.isEmpty( )) {
            return;
        }
        File file = new File(mDownloadPath);
        if (!file.exists( )) {
            return;
        }
        File[] subFile = file.listFiles( );
        if (subFile == null) {
            return;
        }

        mDataListLocal.clear( );
        for (int i = 0; i < subFile.length; ++i) {
            File dir = subFile[i];
            if (!dir.isDirectory( )) {
                continue;
            }
            if (dir.getName( ).contains("MACOSX")) {
                continue;
            }
            PhotoAlbumData oneItem = new PhotoAlbumData( );
            oneItem.id = mDataListLocal.size( );
            oneItem.sourceDir = dir.getAbsolutePath( );
            oneItem.isLocal = true;

            getEachFileLocal(oneItem, dir.listFiles( ));
            if (oneItem.isExist( )) {
                mDataListLocal.add(oneItem);
            }
        }
        upadteAdapter( );
    }

    private void upadteAdapter() {
        if (mGalleryAdapter != null) {
            mGalleryAdapter.setData(mDataListLocal);
            if (mGalleryAdapter.getCount( ) > 0) {
                initCardScale( );
            }
        }
    }

    private void getEachFileLocal(PhotoAlbumData oneItem, File[] oneZip) {
        if (oneItem == null || oneZip == null) {
            return;
        }
        for (int k = 0; k < oneZip.length; ++k) {
            File hitFile = oneZip[k];
            String filePath = hitFile.getPath( );
            String fileName = hitFile.getName( );
            if (fileName.toLowerCase(Locale.US).endsWith("msphotoalbum")) {
                oneItem.filePath = filePath;
            } else if (fileName.toLowerCase(Locale.US).endsWith("lic")) {
                oneItem.licPath = filePath;
            }
            if (fileName.equals("cover.jpg")) {
                oneItem.coverImageUrl = filePath;
            } else if (fileName.equals("cover.mp4")) {
                oneItem.coverVideoUrl = filePath;
            } else if (fileName.equals("info.json")) {
                try {
                    String read_json = Util.loadFromSDFile(filePath);
                    JSONObject jsonObject = new JSONObject(read_json);
                    if (jsonObject != null) {
                        String sName = jsonObject.getString("photosAlbumName");
                        String sTips = jsonObject.getString("photosAlbumTips");
                        String sMax = jsonObject.getString("photosAlbumReplaceMax");
                        String sMin = jsonObject.getString("photosAlbumReplaceMin");
                        oneItem.photosAlbumName = parseStringContent(sName);
                        oneItem.photosAlbumTips = parseStringContent(sTips);
                        oneItem.photosAlbumReplaceMax = sMax;
                        oneItem.photosAlbumReplaceMin = sMin;
                    }
                } catch (Exception e) {
                    Log.d(TAG, "phase info.json exception!");
                }
            }
        }
    }

    private String parseStringContent(String strContent){
        if (TextUtils.isEmpty(strContent)){
            return "";
        }
        //转义字符
        String[] parseStrArray = strContent.split("\\|");
        if (parseStrArray == null || parseStrArray.length <= 1){
            return strContent;
        }
        boolean isChinese = SystemUtils.isZh(mContext);
        return isChinese ? parseStrArray[0] : parseStrArray[1];
    }
    private void getHttpData() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            return;
        }
        OkHttpClientManager.getAsyn(URL, new ResultCallback<PhotoAlbumOnlineData>( ) {
            @Override
            public void onError(Request request, Exception e) {
                Log.e(TAG, "onError: get http photoalbum data error!");

            }

            @Override
            public void onResponse(PhotoAlbumOnlineData response) {
                if (response != null) {
                    List<PhotoAlbumOnlineData.PhotoAlbumDetails> photoAlbumDataList = response.getList( );
                    if (photoAlbumDataList != null) {
                        for (PhotoAlbumOnlineData.PhotoAlbumDetails photoAlbumDetail : photoAlbumDataList) {
                            if (photoAlbumDetail == null) {
                                continue;
                            }
                            Log.i(TAG,"data----->>>"+photoAlbumDetail.toString());
                            if (!isExistInLocal(photoAlbumDetail.getUuid( ))) {
                                String jsonResponse = photoAlbumDetail.getPackageInfo( );
                                try {
                                    PhotoAlbumData oneItem = mGson.fromJson(jsonResponse, PhotoAlbumData.class);
                                    if (oneItem != null) {
                                        oneItem.id = mDataListLocal.size( );
                                        String albumName = oneItem.photosAlbumName;
                                        oneItem.photosAlbumName = parseStringContent(albumName);
                                        String albumTips = oneItem.photosAlbumTips;
                                        oneItem.photosAlbumTips = parseStringContent(albumTips);
                                        oneItem.coverImageUrl = photoAlbumDetail.getCoverUrl( );
                                        oneItem.coverVideoUrl = photoAlbumDetail.getVideoUrl( );
                                        oneItem.packageUrl = photoAlbumDetail.getPackageUrl( );
                                        oneItem.isLocal = false;
                                        mDataListLocal.add(oneItem);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace( );
                                }
                            }
                        }
                    }
                }
                upadteAdapter( );
            }
        });
    }

    private boolean isExistInLocal(String id) {
        for (PhotoAlbumData photoAlbumData : mDataListLocal) {
            if (photoAlbumData == null) {
                continue;
            }
            if (photoAlbumData.sourceDir != null && photoAlbumData.sourceDir.endsWith(id)) {
                return true;
            }
        }
        return false;
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAlbumRv.setLayoutManager(linearLayoutManager);
        mGalleryAdapter = new GalleryAdapter(this, mAlbumRv);
        mAlbumRv.setAdapter(mGalleryAdapter);

        mGalleryAdapter.setOnGrallyItemSelectListener(new GalleryAdapter.OnGrallyItemSelectListener( ) {
            @Override
            public void onItemSelect(int pos, PhotoAlbumData itemData, PlayerView view) {
                if (itemData == null || view == null) {
                    return;
                }
                Log.e(TAG, "onItemSelect: " + pos + " isLocal: " + itemData.isLocal);
                mCurrentData = itemData;
                view.setPlayer(playerWrapper.getPlayer( ));
                playerWrapper.resetPlayer(itemData.coverVideoUrl);
//                playerWrapper.seekTo(0);
                playerWrapper.start( );

                mAlbumNameText.setText(itemData.photosAlbumName);
                mAlbumTipsText.setText(itemData.photosAlbumTips);
                String sIndex = (pos + 1) + "/" + mDataListLocal.size( );
                mAlbumIndexText.setText(sIndex);
            }
        });
    }

    private void initCardScale() {
        /*
        * mRecyclerView绑定scale效果
        * mRecyclerView bound scale effect
        * */
        mCardScaleHelper = new CardScaleHelper( );

        mCardScaleHelper.setCurrentItemPos(0);

        mCardScaleHelper.setOnGrallyItemSelectListener(new CardScaleHelper.OnGrallyItemSelectListener( ) {
            @Override
            public void onItemSelect(int pos) {

            }

            @Override
            public void onScrolling() {

            }
        });
        mCardScaleHelper.attachToRecyclerView(mAlbumRv);
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists( )) {
            file.delete( );
        }
    }

    private void clearDownloads() {
        cancelDownloads( );
        for (String filePath : downloadingURL.values( )) {
            deleteFiles(filePath);
        }
        downloadingURL.clear( );
    }

    private void cancelDownloads() {
        for (String url : downloadingURL.keySet( )) {
            OkHttpClientManager.cancelTag(url);
        }
    }
}

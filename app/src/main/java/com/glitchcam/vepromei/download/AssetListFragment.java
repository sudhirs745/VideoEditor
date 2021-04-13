package com.glitchcam.vepromei.download;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.utils.KeyBoardUtil;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.utils.asset.NvAssetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.glitchcam.vepromei.download.AssetDownloadListAdapter.TYPE_FILTER;
import static com.glitchcam.vepromei.download.AssetDownloadListAdapter.TYPE_PROPS;

/**
 * Created by czl on 2018/6/25.
 * 素材下载列表Fragment
 * Download List Fragment
 */
public class AssetListFragment extends Fragment implements NvAssetManager.NvAssetManagerListener {
    private static final String TAG = "AssetListFragment";
    private static final int GETASSETLISTSUCCESS = 200;
    private static final int GETASSETLISTFAIL = 201;
    private static final int DOWNLOADASSETINPROGRESS = 202;
    private static final int SEARCH_ASSET_LIST_SUCCESS = 203;
    private static final int mPageSize = 20;
    private int mCurrentRequestPage = 0;
    private LinearLayout mPreLoadingLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mAssetRrecyclerViewList;
    private LinearLayout mLoadFailedLayout;
    private Button mReloadAsset;
    private LinearLayout ll_search;
    private EditText et_search;
    private TextView tv_cancel;
    private boolean searchContentEmpty = true;
    private AssetDownloadListAdapter mAssetListAdapter;
    private NvAssetManager mAssetManager;
    private List<NvAsset> mAssetDataList = new ArrayList<>();
    private int mAssetType = 0;
    private int mCategoryId = 0;
    private boolean isSearching = false;
    /*
     * 首次请求标识
     * First request identification
     * */
    private boolean mIsFirstRequest = true;
    /*
     * 刷新状态
     * Refresh status flag
     * */
    private boolean mIsRefreshFlag = false;
    /*
     * 加载状态
     * Loading state
     * */
    private boolean mIsLoadingMoreFlag = false;
    private boolean mHasNext = true;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GETASSETLISTSUCCESS:
                    if (mIsFirstRequest) {
                        mIsFirstRequest = false;
                        mPreLoadingLayout.setVisibility(View.GONE);
                        mLoadFailedLayout.setVisibility(View.GONE);
                    }
                    //refresh
                    closeRefresh();
                    if (mAssetDataList != null && mAssetDataList.size() > 0) {
                        mAssetListAdapter.setAssetDatalist(mAssetDataList);
                        if (mHasNext) {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_COMPLETE);
                        } else {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_END);
                        }
                    }
                    break;
                case GETASSETLISTFAIL:
                    if (mIsFirstRequest) {
                        mPreLoadingLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        mLoadFailedLayout.setVisibility(View.VISIBLE);
                    }
                    closeRefresh();
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_FAILED);
                    break;
                case DOWNLOADASSETINPROGRESS:
                    mAssetListAdapter.updateDownloadItems();
                    break;
                case SEARCH_ASSET_LIST_SUCCESS:
                    closeRefresh();
                    if (mAssetDataList != null) {
                        mAssetListAdapter.setAssetDatalist(mAssetDataList);
                        if (mHasNext) {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_COMPLETE);
                        } else {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_END);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.asset_download_list_fragment, container, false);
        mPreLoadingLayout = (LinearLayout) rootParent.findViewById(R.id.preloadingLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootParent.findViewById(R.id.swipe_refresh_layout);
        mAssetRrecyclerViewList = (RecyclerView) rootParent.findViewById(R.id.asset_recyclerviewList);
        mLoadFailedLayout = (LinearLayout) rootParent.findViewById(R.id.loadFailedLayout);
        mReloadAsset = (Button) rootParent.findViewById(R.id.reloadAsset);
        et_search = rootParent.findViewById(R.id.et_search);
        ll_search = rootParent.findViewById(R.id.ll_search);
        tv_cancel = rootParent.findViewById(R.id.tv_cancel);
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.setManagerlistener(this);

        return rootParent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        initData();
        setListener();
        //startProgressTimer();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //关闭软键盘
                    KeyBoardUtil.hideSoftKeyBroad(et_search,et_search.getContext());
                    //do something
                    //doSearch();
                    String searchContent = et_search.getText().toString().trim();
                    if(!TextUtils.isEmpty(searchContent)){
                        mCurrentRequestPage = 0;
                        searchDataByContent(searchContent);
                        isSearching = true;
                    }
                    return true;
                }
                return false;
            }
        });
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    tv_cancel.setVisibility(View.VISIBLE);
                }else{
                    tv_cancel.setVisibility(View.GONE);
                }
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchContent = s.toString().trim();
                if(TextUtils.isEmpty(searchContent)){
                    //隐藏清空搜索懒得按钮

                    et_search.setCompoundDrawables(null,null,null,null);
                    searchContentEmpty = true;
                    isSearching = false;
                }else{
                    //如果当前不是空，但是之前是空的，此时显示出清空图标
                    if(searchContentEmpty){
                        Drawable drawable = getResources().getDrawable(
                                R.mipmap.filter_search_close);
                        // / 这一步必须要做,否则不会显示.
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                drawable.getMinimumHeight());
                        et_search.setCompoundDrawables(null, null, drawable,null);
                    }
                    tv_cancel.setVisibility(View.VISIBLE);
                    searchContentEmpty = false;

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //显示光标 
                et_search.setCursorVisible(true);
                //拿到drawableRight
                Drawable drawable = et_search.getCompoundDrawables()[2];
                if(null == drawable){
                    return false;
                }
                if(event.getAction() != MotionEvent.ACTION_UP){
                    return false;
                }

                if(event.getX() > et_search.getWidth() -et_search.getPaddingRight() - drawable.getIntrinsicWidth()){
                    et_search.setText("");
                }
                return false;
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消搜索
                et_search.setText("");
                et_search.setCursorVisible(false);
                //关闭软键盘
                KeyBoardUtil.hideSoftKeyBroad(et_search,et_search.getContext());
                tv_cancel.setVisibility(View.GONE);
                isSearching = false;
                mCurrentRequestPage = 0;
                assetDataRequest();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        /*
         * 存储素材数据线程
         * Store material data thread
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(mAssetType);
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        //stopProgressTimer();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
    }

    /*
     * 素材请求与下载
     * Material Request and Download
     * */
    @Override
    public void onRemoteAssetsChanged(boolean hasNext) {
        if(isSearching){
            return;
        }
        mHasNext = hasNext;
        Log.e(TAG, "mHasNext = " + mHasNext);
        if (!mIsLoadingMoreFlag && !mIsRefreshFlag) {
            //refresh
            ArrayList<NvAsset> arrayList = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            if (arrayList.size() > 0) {
                mAssetDataList = arrayList;
            }

        } else if (!mIsLoadingMoreFlag && mIsRefreshFlag) {
            //refresh
            mIsRefreshFlag = false;
            ArrayList<NvAsset> arrayList = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            if (arrayList.size() > 0) {
                mAssetDataList = arrayList;
            }
        } else if (!mIsRefreshFlag && mIsLoadingMoreFlag) {
            mIsLoadingMoreFlag = false;
            ArrayList<NvAsset> assetDataListPerPage = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            mAssetDataList.addAll(assetDataListPerPage);
        }

        //next page
        if(mHasNext){

            ++mCurrentRequestPage;
        }
        Message msg = m_handler.obtainMessage();
        if (msg == null)
            msg = new Message();
        msg.what = GETASSETLISTSUCCESS;
        m_handler.sendMessage(msg);
    }

    /**
     * 搜索返回的数据
     * @param assetDataList
     * @param hasNext
     */
    @Override
    public void onRemoteAssetsChanged(List<NvAsset> assetDataList, boolean hasNext) {
        if(isSearching){
            mHasNext = hasNext;
            if (null != assetDataList) {
                mAssetDataList = assetDataList;
            }
            Message msg = m_handler.obtainMessage();
            if (msg == null)
                msg = new Message();
            msg.what = SEARCH_ASSET_LIST_SUCCESS;
            m_handler.sendMessage(msg);
        }
    }


    @Override
    public void onGetRemoteAssetsFailed() {
        Message msg = m_handler.obtainMessage();
        if (msg == null)
            msg = new Message();
        msg.what = GETASSETLISTFAIL;
        m_handler.sendMessage(msg);
    }

    @Override
    public void onDownloadAssetProgress(final String uuid, final int progress) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAssetListAdapter.updateItemProgress(uuid, progress);
                }
            });
        }
    }

    @Override
    public void onDonwloadAssetFailed(String uuid) {
        for (int index = 0; index < mAssetDataList.size(); ++index) {
            if (mAssetDataList.get(index).uuid.compareTo(uuid) == 0) {
                break;
            }
        }
    }

    @Override
    public void onDonwloadAssetSuccess(final String uuid) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAssetListAdapter.updateItemProgress(uuid, -1);
                }
            });
        }
    }

    @Override
    public void onFinishAssetPackageInstallation(String uuid) {

    }

    @Override
    public void onFinishAssetPackageUpgrading(String uuid) {

    }



    private String mComeFrom;

    private void initData() {
        Bundle bundle = getArguments();
        int curRatio = NvAsset.AspectRatio_16v9;
        if (bundle != null) {
            mAssetType = bundle.getInt("assetType");
            mCategoryId = bundle.getInt("categoryId", 0);
            curRatio = bundle.getInt("ratio", NvAsset.AspectRatio_16v9);
            mComeFrom = bundle.getString("from", "");
        }
        mIsFirstRequest = true;
        /*
         * 设置刷新控件颜色
         * Set refresh control color
         * */
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4DB6AC"));
        mAssetListAdapter = new AssetDownloadListAdapter(getActivity());
        mAssetListAdapter.setCurTimelineRatio(curRatio);
        mAssetListAdapter.setAssetType(mAssetType);
        if (!TextUtils.isEmpty(mComeFrom)) {
            if ("capture_props".equals(mComeFrom)) {
                mAssetListAdapter.setExtraViewType(TYPE_PROPS);
            } else if ("capture_filter".equals(mComeFrom) || "edit_filter".equals(mComeFrom)||"cover_filter".equals(mComeFrom)) {
                mAssetListAdapter.setExtraViewType(TYPE_FILTER);
                //目前只在拍摄的更多滤镜下使用搜索功能
                ll_search.setVisibility(View.VISIBLE);
            }
            mAssetRrecyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mAssetRrecyclerViewList.addItemDecoration(new SquareDecoration());
            mAssetRrecyclerViewList.setBackgroundColor(getActivity().getResources().getColor(R.color.white_20));
        } else {
            mAssetRrecyclerViewList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAssetRrecyclerViewList.addItemDecoration(new AssetListDecoration(getActivity(), AssetListDecoration.VERTICAL_LIST));
        }

        mAssetListAdapter.setAssetDatalist(mAssetDataList);
        mAssetRrecyclerViewList.setAdapter(mAssetListAdapter);
        /*
         * 下拉刷新
         * Pull down to refresh
         * */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsRefreshFlag = true;
                mCurrentRequestPage = 0;
                String searchKey = et_search.getText().toString().trim();
                if(TextUtils.isEmpty(searchKey)){
                    assetDataRequest();
                }else{
                    searchDataByContent(searchKey);
                }
            }
        });

        /*
         *  加载更多监听
         * Add listeners for loading more
         * */
        mAssetRrecyclerViewList.addOnScrollListener(new AssetListOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "mHasNext = " + mHasNext);
                if (mHasNext) {
                    mIsLoadingMoreFlag = true;
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING);
                    String searchKey = et_search.getText().toString().trim();
                    if(TextUtils.isEmpty(searchKey)){

                        assetDataRequest();
                    }else{
                        searchDataByContent(searchKey);
                    }
                } else {
                    /*
                     * 显示加载到底的提示
                     * Show loading tips
                     * */
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_END);
                }
            }
        });

        mAssetListAdapter.setDownloadClickerListener(new AssetDownloadListAdapter.OnDownloadClickListener() {
            @Override
            public void onItemDownloadClick(RecyclerView.ViewHolder holder, int pos) {
                int size = mAssetDataList.size();
                if (pos >= size)
                    return;
                if (size > 0) {
                    mAssetManager.downloadAsset(mAssetType, mAssetDataList.get(pos).uuid);
                }
            }
        });

        mReloadAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadFailedLayout.setVisibility(View.GONE);
                mPreLoadingLayout.setVisibility(View.VISIBLE);
                assetDataRequest();
            }
        });
        mAssetManager.searchLocalAssets(mAssetType);
        assetDataRequest();
    }

    /*
     * Network request
     * 网络请求
     * */
    private void assetDataRequest() {
        mAssetManager.downloadRemoteAssetsInfo(mAssetType, NvAsset.AspectRatio_All, mCategoryId, mCurrentRequestPage, mPageSize);
    }

    /**
     * 搜索的方法
     * @param searchContent
     */
    private void searchDataByContent(String searchContent){
        mAssetManager.downloadRemoteAssetsInfo(searchContent,mAssetType, NvAsset.AspectRatio_All, mCategoryId, mCurrentRequestPage, mPageSize);
    }

    private void closeRefresh() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startProgressTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = DOWNLOADASSETINPROGRESS;
                m_handler.sendMessage(msg);
            }
        };
        mTimer.schedule(mTimerTask, 0, 300);
    }

    private void stopProgressTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

}

package com.glitchcam.vepromei.edit.background.view;

import android.content.Context;
import android.content.res.AssetManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.background.BackgroundStyleInfo;
import com.glitchcam.vepromei.edit.background.SpaceItemDecoration;
import com.glitchcam.vepromei.utils.ScreenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liupanfeng
 * @desc 样式背景 view
 * @date 2020/10/21 10:45
 */
public class BackgroundStyleView extends LinearLayout {

    private final static String IMAGE_ASSETS_PATH = "background/image";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private BackgroundStyleAdapter mBackgroundStyleAdapter;
    private List<BackgroundStyleInfo> mData;

    private int mOnSelectPosition;
    private OnBackgroundStyleItemClickListener mOnBackgroundStyleItemClickListener;


    public BackgroundStyleView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundStyleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundStyleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_background_style_view, this);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        initData();
        initRecyclerView();
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBackgroundStyleAdapter = new BackgroundStyleAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mBackgroundStyleAdapter);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dp2px(mContext, 3), ScreenUtils.dp2px(getContext(), 12)));
    }

    private void initData() {
        mData = new ArrayList<>();
        mData = getBackgroundImageList();
    }

    public void setSelectPosition(int position) {
        if (position == mOnSelectPosition){
            return;
        }
        int oldPosition = mOnSelectPosition;
        mOnSelectPosition = position;
        mBackgroundStyleAdapter.notifyItemChanged(oldPosition);
        mBackgroundStyleAdapter.notifyItemChanged(mOnSelectPosition);
    }


    private class BackgroundStyleAdapter extends RecyclerView.Adapter<StyleHolder> {

        @Override
        public StyleHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(mContext).inflate(R.layout.item_background_style, null);
            return new StyleHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StyleHolder styleHolder, int i) {
            final int position = i;
            final BackgroundStyleInfo backgroundStyleInfo = mData.get(i);
            if (backgroundStyleInfo == null) {
                return;
            }
            String filePath = backgroundStyleInfo.getFilePath();
            Glide.with(mContext.getApplicationContext())
                    .asBitmap()
                    .load(TextUtils.isEmpty(filePath) ? backgroundStyleInfo.getIconRcsId() : "file:///android_asset/background/image/" + filePath)
                    .apply(styleHolder.mOptions)
                    .into(styleHolder.mIcon);
            Glide.with(mContext.getApplicationContext())
                    .asBitmap()
                    .load(TextUtils.isEmpty(filePath) ? backgroundStyleInfo.getIconRcsId() : "file:///android_asset/background/image/" + filePath)
                    .apply(styleHolder.mOptions)
                    .into(styleHolder.mMask);

            styleHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnBackgroundStyleItemClickListener != null) {
                        mOnBackgroundStyleItemClickListener.onStyleItemClick(view, position, backgroundStyleInfo);
                    }
                    if (mOnSelectPosition == position) {
                        return;
                    }
                    notifyItemChanged(mOnSelectPosition);
                    mOnSelectPosition = position;
                    notifyItemChanged(mOnSelectPosition);
                }
            });

            if (position == 0) {
                styleHolder.cv_mask.setVisibility(View.GONE);
            } else {
                styleHolder.cv_mask.setVisibility(mOnSelectPosition == position ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

    }


    private class StyleHolder extends RecyclerView.ViewHolder {

        private RequestOptions mOptions = new RequestOptions();
        private ImageView mIcon;
        private ImageView mMask;
        private CardView cv_mask;

        public StyleHolder(@NonNull View itemView) {
            super(itemView);
            mOptions.skipMemoryCache(false);
            mIcon = itemView.findViewById(R.id.iv_pic);
            mMask = itemView.findViewById(R.id.iv_mark);
            cv_mask = itemView.findViewById(R.id.cv_bg_mask);
        }
    }

    public List<BackgroundStyleInfo> getBackgroundImageList() {
        AssetManager assets = mContext.getAssets();
        try {
            String[] list = assets.list(IMAGE_ASSETS_PATH);
            if ((list == null) || (list.length <= 0)) {
                return null;
            }
            List<BackgroundStyleInfo> result = new ArrayList<>();
            BackgroundStyleInfo more = new BackgroundStyleInfo();
            more.setIconRcsId(R.mipmap.icon_background_style_more1);
            result.add(more);
            BackgroundStyleInfo nullInfo = new BackgroundStyleInfo();
            nullInfo.setIconRcsId(R.mipmap.icon_background_style_no1);
            result.add(nullInfo);
            for (String s : list) {
                BackgroundStyleInfo canvasStyleInfo = new BackgroundStyleInfo();
                canvasStyleInfo.setFilePath(s);
                result.add(canvasStyleInfo);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BackgroundStyleInfo> getData() {
        return mData;
    }

    public BackgroundStyleInfo getSelectData() {
        if (mOnSelectPosition <= 1) {
            return null;
        }
        return mData.get(mOnSelectPosition);

    }


    public void setOnBackgroundStyleItemClickListener(OnBackgroundStyleItemClickListener onBackgroundStyleItemClickListener) {
        this.mOnBackgroundStyleItemClickListener = onBackgroundStyleItemClickListener;
    }

    public interface OnBackgroundStyleItemClickListener {

        /**
         * 样式背景点击回调
         *
         * @param view
         * @param backgroundStyleInfo
         */
        void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo);

    }

}

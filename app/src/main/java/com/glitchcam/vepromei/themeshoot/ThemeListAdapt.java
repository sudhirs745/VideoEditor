package com.glitchcam.vepromei.themeshoot;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.themeshoot.utlils.ThemeShootUtil;

import java.util.ArrayList;
import java.util.List;

public class ThemeListAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ThemeModel> mAssetDataList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mClickListener;

    public ThemeListAdapt(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_theme_shoot, viewGroup, false);
        return new ThemeCaptureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dealThemeCaptureItem(holder, position);
    }

    public void setAssetDatalist(List<ThemeModel> assetDataList) {
        this.mAssetDataList = assetDataList;
        Log.e("Datalist", "DataCount = " + mAssetDataList.size());
    }

    /**
     * 主题拍摄
     */
    private void dealThemeCaptureItem(RecyclerView.ViewHolder holder, final int position) {
        if (mAssetDataList.size() > 0 && position < mAssetDataList.size()) {
            final ThemeModel themeModel = mAssetDataList.get(position);
            final ThemeCaptureViewHolder themeCaptureViewHolder = (ThemeCaptureViewHolder) holder;
            themeCaptureViewHolder.mTvName.setText(themeModel.getName());
            themeCaptureViewHolder.mTvNum.setText(themeModel.getShotsNumber());
            String duration = ThemeShootUtil.formatUsToString(themeModel.getMusicDuration() * 1000);
            themeCaptureViewHolder.mTvDuration.setText(duration);
            String priewPath = themeModel.getCover();
            if (themeModel.isLocal()) {
                themeCaptureViewHolder.itemDown.setVisibility(View.GONE);
            } else {
                themeCaptureViewHolder.itemDown.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext)
                    .asBitmap()
                    .load(priewPath)
                    .into(themeCaptureViewHolder.mIvCover);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mAssetDataList == null) {
            return 0;
        }
        return mAssetDataList.size();
    }

    public class ThemeCaptureViewHolder extends RecyclerView.ViewHolder {

        View mItemLayout;
        ImageView mIvCover;
        TextView mTvName;
        TextView mTvNum;
        TextView mTvDuration;
        View itemDown;

        public ThemeCaptureViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.theme_item_layout);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvNum = itemView.findViewById(R.id.tv_clip_num);
            mTvDuration = itemView.findViewById(R.id.tv_theme_duration);
            itemDown = itemView.findViewById(R.id.item_net_down);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}

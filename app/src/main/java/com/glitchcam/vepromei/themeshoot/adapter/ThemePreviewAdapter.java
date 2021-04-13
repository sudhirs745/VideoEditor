package com.glitchcam.vepromei.themeshoot.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.themeshoot.bean.ThemePreviewBean;

import java.util.List;

public class ThemePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ThemePreviewBean> mDatas;
    private Context context;
    private int mCurrentPosition;

    public ThemePreviewAdapter(List<ThemePreviewBean> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    private OnThemePreviewOnClickListener onItemClickListener;

    public void setOnItemClickListener(OnThemePreviewOnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_theme_preview, viewGroup, false);
        return new ThemePreviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final int position = i;
        ThemePreviewBean previewBean = mDatas.get(position);
        ThemePreviewHolder holder = (ThemePreviewHolder) viewHolder;
        holder.itemTvName.setText(previewBean.getName());
        if (!TextUtils.isEmpty(previewBean.getBgUrl())) {
            Glide.with(context).asBitmap().load(previewBean.getBgUrl()).into(holder.itemIvBg);
        } else if (previewBean.getBitmap() != null) {
            holder.itemIvBg.setImageBitmap(previewBean.getBitmap());
        } else {
            holder.itemIvBg.setVisibility(View.INVISIBLE);
        }
        if (mCurrentPosition == i) {
            holder.itemTvEdit.setVisibility(View.VISIBLE);
            holder.itemIvEdit.setVisibility(View.VISIBLE);
            holder.itemIvChecked.setVisibility(View.VISIBLE);
        } else {
            holder.itemTvEdit.setVisibility(View.INVISIBLE);
            holder.itemIvEdit.setVisibility(View.INVISIBLE);
            holder.itemIvChecked.setVisibility(View.GONE);
        }
        holder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPosition = position;
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public void refreshCurrentPosition(int position) {
        if (mCurrentPosition == position || mDatas == null || position >= mDatas.size()) {
            return;
        }
        mCurrentPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    public class ThemePreviewHolder extends RecyclerView.ViewHolder {
        View itemContent;
        TextView itemTvName;
        ImageView itemIvBg;
        ImageView itemIvChecked;
        ImageView itemIvEdit;
        TextView itemTvEdit;

        public ThemePreviewHolder(@NonNull View itemView) {
            super(itemView);
            itemContent = itemView.findViewById(R.id.item_theme_content);
            itemTvName = itemView.findViewById(R.id.item_tv_name);
            itemIvBg = itemView.findViewById(R.id.item_iv_bg);
            itemIvEdit = itemView.findViewById(R.id.item_iv_edit);
            itemTvEdit = itemView.findViewById(R.id.item_tv_edit);
            itemIvChecked = itemView.findViewById(R.id.item_iv_bg_checked);
        }
    }

    public interface OnThemePreviewOnClickListener {
        void onItemClick(int position);
    }
}

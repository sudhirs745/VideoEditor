package com.glitchcam.vepromei.mimodemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.interf.OnTemplateSelectListener;
import com.glitchcam.vepromei.utils.AssetFxUtil;

import java.util.List;

public class TemplateListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "TemplateListAdapter";
    private List<MiMoLocalData> mTemplateList;
    private int mCurrentSelect = 0;
    private static final float TIME_BASE = 1000F;
    private static final String TIME_SECOND = "S";
    private OnTemplateSelectListener mOnTemplateSelectListener;
    private Context mContext;

    public void setOnTemplateSelectListener(OnTemplateSelectListener onTemplateSelectListener) {
        this.mOnTemplateSelectListener = onTemplateSelectListener;
    }

    public TemplateListAdapter(Context context, List<MiMoLocalData> filterList) {
        mContext = context;
        mTemplateList = filterList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_item_layout, viewGroup, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        if (mTemplateList == null || mTemplateList.isEmpty()) {
            return;
        }
        final Holder holder = (Holder) viewHolder;
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = layoutParams.height;
        holder.itemView.setLayoutParams(layoutParams);
        final MiMoLocalData templateInfo = mTemplateList.get(position);
        String name = templateInfo.getName();
        List<MiMoLocalData.Translation> translation = templateInfo.getTranslation();
        if (translation != null && !translation.isEmpty()) {
            if(AssetFxUtil.isZh(mContext)) {
                name = translation.get(0).getTargetText();
            }
        }
        holder.mNameTextView.setText(name);
        holder.mShotNumView.setText(templateInfo.getShotsNumber() + " SHOT");
        holder.mDurationTextView.setText(templateInfo.getMusicDuration() / 1000.0 + "S");
        String templateFilePath = "";
        templateFilePath = templateInfo.getCoverUrl();
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        options.error(R.mipmap.template_default_cover);
        options.placeholder(R.mipmap.template_default_cover);
        Glide.with(holder.mCoverImageView.getContext().getApplicationContext())
                .asBitmap()
                .load(templateFilePath)
                .apply(options)
                .into(holder.mCoverImageView);

        holder.mCoverMaskImageView.setVisibility(mCurrentSelect == position ? View.VISIBLE : View.INVISIBLE);
        holder.mConfirmView.setVisibility(mCurrentSelect == position ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSelect == position) {
                    return;
                }
                mCurrentSelect = position;
                notifyDataSetChanged();
                if (mOnTemplateSelectListener != null) {
                    mOnTemplateSelectListener.onTemplateSelected(position);
                }
            }
        });
        holder.mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTemplateSelectListener != null) {
                    mOnTemplateSelectListener.onTemplateConfirm();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTemplateList == null ? 0 : mTemplateList.size();
    }

    public void setSelectPosition(int position) {
        if (mCurrentSelect != position) {
            mCurrentSelect = position;
            notifyDataSetChanged();
        }
    }

    public void setNewDatas(List<MiMoLocalData> mDataListLocals) {
        mTemplateList = mDataListLocals;
        notifyDataSetChanged();
    }

    public MiMoLocalData getCurrentData() {
        if (mTemplateList != null && mCurrentSelect >= 0 && mCurrentSelect < mTemplateList.size()) {
            return mTemplateList.get(mCurrentSelect);
        }
        return null;
    }

    class Holder extends RecyclerView.ViewHolder {

        public ImageView mCoverMaskImageView;
        public TextView mNameTextView;
        public TextView mShotNumView;
        public TextView mDurationTextView;
        public ImageView mCoverImageView;
        public View mConfirmView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mCoverImageView = (ImageView) itemView.findViewById(R.id.cover);
            mCoverMaskImageView = (ImageView) itemView.findViewById(R.id.cover_mask);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            mShotNumView = (TextView) itemView.findViewById(R.id.tv_shot_num);
            mDurationTextView = (TextView) itemView.findViewById(R.id.tv_duration);
            mConfirmView = itemView.findViewById(R.id.tv_confirm);
        }
    }

    private String getDurationTime(long time) {
        StringBuilder sb = new StringBuilder();
        float result = (float) time / TIME_BASE;
        sb.append(result).append(TIME_SECOND);
        return sb.toString();
    }
}

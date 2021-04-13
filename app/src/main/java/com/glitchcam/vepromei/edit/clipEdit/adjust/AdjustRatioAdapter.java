package com.glitchcam.vepromei.edit.clipEdit.adjust;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.bean.AdjustRation;

import java.util.List;

/**
 * @author LiFei
 * @version 1.0
 * @title
 * @description 该类主要功能描述
 * @company 美摄
 * @created 2020/12/1 15:00
 * @changeRecord [修改记录] <br/>
 */
public class AdjustRatioAdapter extends RecyclerView.Adapter<AdjustRatioAdapter.AdjustRatioViewHolder> {
    private Context mContext;
    private List<AdjustRation> mRations;
    private OnAdjustRationChangeListener mRationChangeListener;

    public AdjustRatioAdapter(Context context, List<AdjustRation> rations) {
        mContext = context;
        mRations = rations;
    }

    @NonNull
    @Override
    public AdjustRatioAdapter.AdjustRatioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_adjust_ration, parent, false);
        return new AdjustRatioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdjustRatioAdapter.AdjustRatioViewHolder holder, final int position) {
        final AdjustRation ration = mRations.get(position);
        holder.ratioName.setText(ration.getName());
        if (ration.isSelectd()) {
            holder.ratioName.setTextColor(mContext.getResources().getColor(R.color.ms_blue));
            holder.radioIcon.setImageResource(ration.getSelectedIcon());
        } else {
            holder.ratioName.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.radioIcon.setImageResource(ration.getUnSelectedIcon());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ration.isSelectd()) {
                    ration.setSelectd(true);
                    if (mRationChangeListener != null) {
                        mRationChangeListener.onAdjustRationChange(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mRations == null) ? 0 : mRations.size();
    }

    static class AdjustRatioViewHolder extends RecyclerView.ViewHolder {
        private ImageView radioIcon;
        private TextView ratioName;

        public AdjustRatioViewHolder(@NonNull View itemView) {
            super(itemView);
            radioIcon = itemView.findViewById(R.id.radio_icon);
            ratioName = itemView.findViewById(R.id.ratio_name);
        }
    }

    public void setSelection(int position) {
        if ((position < 0) || mRations.isEmpty()) {
            return;
        }
        for (int i = 0; i < mRations.size(); i++) {
            AdjustRation ration = mRations.get(i);
            if (ration == null) {
                continue;
            }
            ration.setSelectd(position == i);
        }
        notifyDataSetChanged();
    }

    public void setRationChangeListener(OnAdjustRationChangeListener rationChangeListener) {
        mRationChangeListener = rationChangeListener;
    }

    interface OnAdjustRationChangeListener {
        void onAdjustRationChange(int position);
    }
}

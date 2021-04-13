package com.glitchcam.vepromei.edit.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.data.ChangeSpeedCurveInfo;
import com.glitchcam.vepromei.edit.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by admin on 2018/5/25.
 */

public class CurveSpeedViewAdapter extends RecyclerView.Adapter<CurveSpeedViewAdapter.ViewHolder>  {

    private Context m_mContext;

    private List<ChangeSpeedCurveInfo> m_assetInfolist;
    private OnItemClickListener m_onItemClickListener = null;
    private int selectedPosition = 0;

    public CurveSpeedViewAdapter(Context context) {
        m_mContext = context;
    }

    public void updateData(List<ChangeSpeedCurveInfo> assetInfoList) {
        m_assetInfolist = assetInfoList;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(m_mContext).inflate(R.layout.item_curve_asset, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mImageAsset.setImageDrawable(m_assetInfolist.get(position).image_drawable);
        holder.mAssetName.setText(m_assetInfolist.get(position).mName);
        if(selectedPosition == position){
            holder.mAssetName.setTextColor(m_mContext.getResources().getColor(R.color.change_speed_selected));
            if(position != 0){
                holder.rl_selected.setVisibility(View.VISIBLE);
            }else{
                holder.rl_selected.setVisibility(View.GONE);
                holder.mImageAsset.setImageResource(R.mipmap.change_speed_none);
            }
        }else{
            holder.mAssetName.setTextColor(m_mContext.getResources().getColor(R.color.ccffffff));
            holder.rl_selected.setVisibility(View.GONE);
        }
        if(m_onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    m_onItemClickListener.onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return m_assetInfolist.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.m_onItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageAsset;
        TextView mAssetName;
        RelativeLayout rl_selected;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageAsset = (ImageView) itemView.findViewById(R.id.imageAsset);
            mAssetName = (TextView) itemView.findViewById(R.id.assetName);
            rl_selected = (RelativeLayout) itemView.findViewById(R.id.iv_select_bg);
        }
    }
}

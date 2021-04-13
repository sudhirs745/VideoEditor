package com.glitchcam.vepromei.mimodemo.mediapaker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class RatioGridItemAdapter extends BaseAdapter {
    private Context mContext;
    private OnItemCilickListener mOnItemClickListener;
    private List<String> mRatioList = new ArrayList<>();
    private LayoutInflater layoutInflater;

    class ViewHolder {
        TextView text;
    }
    public interface OnItemCilickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemCilickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public void setRatioList(List<String> ratioList) {
        if (ratioList == null || ratioList.isEmpty()){
            return;
        }
        this.mRatioList = ratioList;
        notifyDataSetChanged();
    }
    public RatioGridItemAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mRatioList == null || mRatioList.isEmpty()){
            return 0;
        }
        return mRatioList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mRatioList == null || mRatioList.isEmpty()){
            return null;
        }
        return mRatioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RatioGridItemAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dynamic_pop_item, null);
            holder = new RatioGridItemAdapter.ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
            ViewGroup.LayoutParams layoutParams = holder.text.getLayoutParams();
            layoutParams.height = ScreenUtils.dip2px(mContext, 80);
            holder.text.setLayoutParams(layoutParams);
        } else {
            holder = (RatioGridItemAdapter.ViewHolder) convertView.getTag();
        }

        String provinceBean = mRatioList.get(position);
        provinceBean = provinceBean.replace("v",":");
        holder.text.setText(provinceBean);
        holder.text.setTextColor(Color.WHITE);
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.douyin_speed_text_select_color));
        return convertView;
    }
}

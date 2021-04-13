package com.glitchcam.vepromei.selectmedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.grallyRecyclerView.ItemTouchListener;
import com.glitchcam.vepromei.edit.interfaces.OnGrallyItemClickListener;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectedMediaDatasUI extends RecyclerView.Adapter<SelectedMediaDatasUI.ViewHolder> implements ItemTouchListener {

    private final Context context;

    private List<MediaData> mMediaInfoArray;
    private List<ClipInfo> mClipInfoArray;

    private OnClickSelectedItem mediaClickListener;
    private OnGrallyItemClickListener scaleHelperListener = null;

    private int currentPos = 0;

    private boolean showCloseBtn;

    public SelectedMediaDatasUI(Context _context, List<MediaData> _mediaInfoArray, List<ClipInfo> _mClipInfoArray, boolean _showCloseBtn, OnClickSelectedItem _onItemClickListener) {
        context = _context;
        mMediaInfoArray = _mediaInfoArray;
        mClipInfoArray = _mClipInfoArray;
        showCloseBtn = _showCloseBtn;
        mediaClickListener = _onItemClickListener;
    }

    public void setMediaInfoArray(List<MediaData> mediaInfoArray) {
        this.mMediaInfoArray = mediaInfoArray;
    }

    public void setClipInfoArray(List<ClipInfo> clipInfoArray) {
        this.mClipInfoArray = clipInfoArray;
    }

    public void setOnItemSelectedListener(OnGrallyItemClickListener _scaleHelperListener) {
        scaleHelperListener = _scaleHelperListener;
    }

    public void setSelectPos(int _currentPos){
        currentPos = _currentPos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_media_data, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        setBindViewHolder(holder, position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            setBindViewHolder(holder, position);
        }
    }

    private void setBindViewHolder(final ViewHolder holder, final int position) {
        String filePath;
        if(showCloseBtn){
            filePath = mMediaInfoArray.get(position).getThumbPath();
        }else{
            filePath = mClipInfoArray.get(position).getFilePath();
        }

        RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.edit_clip_default_bg);

        Glide.with(context)
            .load(filePath)
            .apply(options)
            .into(holder.mImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = position;
                mediaClickListener.onClickCellData(position);
                notifyDataSetChanged();
            }
        });

        if(showCloseBtn){
            holder.cv_close.setVisibility(View.VISIBLE);
            holder.cv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaClickListener.onClickRemoveData(position);
                }
            });
        }else{
            holder.cv_close.setVisibility(View.GONE);
        }
        if(position == currentPos){
            holder.iv_border.setVisibility(View.VISIBLE);
        }else{
            holder.iv_border.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(showCloseBtn)
            return mMediaInfoArray.size();
        else
            return mClipInfoArray.size();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        currentPos = toPosition;
        if (scaleHelperListener != null)
            scaleHelperListener.onItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void removeAll() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView, iv_border;
        CardView cv_close;

        ViewHolder(final View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_media_cell);
            iv_border = itemView.findViewById(R.id.iv_purple_border);
            cv_close = itemView.findViewById(R.id.cv_close);
        }
    }

    public interface OnClickSelectedItem {
        void onClickCellData(int position);
        void onClickRemoveData(int position);
    }
}

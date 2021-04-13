package com.glitchcam.vepromei.mimodemo.mediapaker.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;


import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;

import java.util.ArrayList;
import java.util.List;

public class SelectMenuAdapter extends RecyclerView.Adapter{
    private List<ShotVideoInfo> mData = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    public int mCurrentSelectPosition = 0;
    private boolean mIsEditable = true;
    private boolean mIsByUser = false;
    private IAdapterLifeCircle mAdapterLifeCircle;

    public SelectMenuAdapter() {
    }

    public void setData(List<ShotVideoInfo> data) {
        if (data == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void upDataResource(ShotVideoInfo shotVideoInfo,int position){
        if(null != shotVideoInfo && position>=0 && position < mData.size()){
            mData.set(position,shotVideoInfo);
            notifyDataSetChanged();
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mAdapterLifeCircle == null) {
            return null;
        }
        return mAdapterLifeCircle.onCreateViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder == null) {
            return;
        }
        final BaseRecyclerHolder holder = (BaseRecyclerHolder) viewHolder;
        viewHolder.setIsRecyclable(false);
        final ShotVideoInfo clipInfo = mData.get(position);
        holder.bind(clipInfo,mCurrentSelectPosition == position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsByUser = true;
                holder.onClick(v);
                mCurrentSelectPosition = position;
                notifyDataSetChanged();
                String source = mData.get(position).getVideoClipPath();

                if (source != null && !TextUtils.isEmpty(source) && holder.canClickable()) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.OnItemClicked(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    public void goNextPosition() {
        if (mCurrentSelectPosition < mData.size() - 1) {
            mCurrentSelectPosition++;
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void addClipPath(String path, long duration) {
        if (mData == null) {
            return;
        }
        if (mCurrentSelectPosition >= mData.size())
            return;
        
        ShotVideoInfo clipInfo = mData.get(mCurrentSelectPosition);
        if (clipInfo != null) {
//            if (!TextUtils.isEmpty(clipInfo.getVideoClipPath())) {
//                return;
//            }
            clipInfo.setVideoClipPath(path);
            clipInfo.setFileDuration(duration * Constants.BASE_MILLISECOND);
        }
        if (mIsByUser) {//用户手动点击的，需要查找未选则的位置
            jumpToEmptyIndex(0);
            mIsByUser = false;
        } else {//非用户手动选择，跳转到下一个位置
            if (mCurrentSelectPosition < mData.size() - 1) {
                jumpToEmptyIndex(mCurrentSelectPosition + 1);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 跳到未选择的index上
     *
     * @param startIndex
     */
    private void jumpToEmptyIndex(int startIndex) {
        if (startIndex > mData.size() - 1) {
            return;
        }
        if (mCurrentSelectPosition >= mData.size() - 1) {
            return;
        }
        if (TextUtils.isEmpty(mData.get(startIndex).getVideoClipPath())) {
            mCurrentSelectPosition = startIndex;
            return;
        }
        jumpToEmptyIndex(++startIndex);
    }

    public void removeClipPath() {
        if (mData == null) {
            return;
        }
        mData.get(mCurrentSelectPosition).setVideoClipPath(null);
        notifyDataSetChanged();
    }

    public void setIsEditable(boolean isEditable) {
        this.mIsEditable = isEditable;
        if (!mIsEditable) {
            mCurrentSelectPosition = 0;
        }
    }

    public boolean isFull() {
        for (int index = 0; index < mData.size(); index++) {
            if (TextUtils.isEmpty(mData.get(index).getVideoClipPath())) {
                return false;
            }
        }
        return true;
    }

    public void updatePosition(int curPosition) {
        if (curPosition != mCurrentSelectPosition) {
            mCurrentSelectPosition = curPosition;
            notifyDataSetChanged();
        }
    }

    public void upDataForPosition(int position){

    }

    public void setPosition(int mIndex) {
        mCurrentSelectPosition = findPositionByShotIndex(mIndex);
        notifyDataSetChanged();
    }

    private int findPositionByShotIndex(int index) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getShot() == index) {
                return i;
            }
        }
        return 0;
    }

    public void addLifeCircle(IAdapterLifeCircle adapterLifeCircle) {
        mAdapterLifeCircle = adapterLifeCircle;
    }

    public interface OnItemClickListener {
        void OnItemClicked(int position);
    }
}

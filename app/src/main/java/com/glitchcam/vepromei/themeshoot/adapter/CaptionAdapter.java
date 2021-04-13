package com.glitchcam.vepromei.themeshoot.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.themeshoot.bean.CaptionBean;

import java.util.List;

public class CaptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CaptionBean> mDatas;
    private Context context;
    private OnCaptionItemClick onCaptionItemClick;

    public void setOnCaptionItemClick(OnCaptionItemClick onCaptionItemClick) {
        this.onCaptionItemClick = onCaptionItemClick;
    }

    public CaptionAdapter(List<CaptionBean> mDatas, Context context) {
        this.mDatas = mDatas;
        this.context = context;
    }

    public void setNewDatas(List<CaptionBean> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public CaptionBean getCaptionBeanByPosition(int position) {
        if (mDatas != null && mDatas.size() > position && position >= 0) {
            return mDatas.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theme_caption, viewGroup, false);
        return new CaptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final int position = i;
        final CaptionBean captionBean = mDatas.get(position);
        CaptionViewHolder holder = (CaptionViewHolder) viewHolder;
        if (captionBean != null) {
            holder.tvInfo.setText(captionBean.getText());
        }
        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCaptionItemClick != null) {
                    onCaptionItemClick.onItemEditClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    public class CaptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo;
        View btEdit;

        public CaptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.item_caption_tv_info);
            btEdit = itemView.findViewById(R.id.item_caption_bt_edit);
        }
    }

    public interface OnCaptionItemClick {
        void onItemEditClick(int position);
    }
}

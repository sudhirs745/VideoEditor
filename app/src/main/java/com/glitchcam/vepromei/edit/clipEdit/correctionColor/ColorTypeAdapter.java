package com.glitchcam.vepromei.edit.clipEdit.correctionColor;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.glitchcam.vepromei.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyj on 2018/10/11 0011.
 */

public class ColorTypeAdapter extends RecyclerView.Adapter<ColorTypeAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private List<ColorTypeItem> mColorTypeList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, ColorTypeItem colorTypeItem);
    }

    public ColorTypeAdapter(Context context, List<ColorTypeItem> colorTypeList) {
        mContext = context;
        this.mColorTypeList = colorTypeList;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name;
        private ImageView item_icon;
        public ViewHolder(View view) {
            super(view);
            item_name = (TextView) view.findViewById(R.id.name);
            item_icon = (ImageView) view.findViewById(R.id.iv_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_type_adjust, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ColorTypeItem colorTypeItem = mColorTypeList.get(position);
        if(colorTypeItem == null) {
            return;
        }
        holder.item_name.setText(colorTypeItem.getColorAtrubuteText());

        if(colorTypeItem.isSelected()) {
            holder.item_name.setTextColor(ContextCompat.getColor(mContext, R.color.ms994a90e2));
            holder.item_icon.setImageResource(colorTypeItem.getSelectedIcon());
        } else {
            holder.item_name.setTextColor(ContextCompat.getColor(mContext, R.color.ccffffff));
            holder.item_icon.setImageResource(colorTypeItem.getIcon());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < mColorTypeList.size(); ++i) {
                    if(i == position) {
                        mColorTypeList.get(i).setSelected(true);
                    } else {
                        mColorTypeList.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();

                if(mClickListener != null) {
                    mClickListener.onItemClick(view, colorTypeItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mColorTypeList.size();
    }
}

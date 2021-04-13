package com.glitchcam.vepromei.glitter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Glitter Style Adapter
 */
public class GlitterStyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<GlitterEffectBean> mGlitterEffectBeanList = new ArrayList<>( );
    private int mSelectPos;
    private StyleClickListener mStyleClickListener;

    public GlitterStyleAdapter(Context context, List<GlitterEffectBean> glitterEffectBeanList) {
        this.mContext = context;
        this.mGlitterEffectBeanList = glitterEffectBeanList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void updateGlitterStyleAdapter(List<GlitterEffectBean> data) {
        this.mGlitterEffectBeanList = data;
        notifyDataSetChanged( );
    }

    public void setStyleClickListener(StyleClickListener styleClickListener) {
        this.mStyleClickListener = styleClickListener;
    }

    public void selectPos(int pos) {
        this.mSelectPos = pos;
        /*
        * notifyItemChanged 放到onBindViewHolder中
        * notifyItemChanged into onBindViewHolder()
        * */
        notifyDataSetChanged( );
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.glitter_style_item_layout, viewGroup, false);
        return new StyleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int pos) {
        if ((mGlitterEffectBeanList != null) && (pos >= 0) && (pos < mGlitterEffectBeanList.size( ))) {
            final GlitterEffectBean glitterEffectBean = mGlitterEffectBeanList.get(pos);
            if (glitterEffectBean != null) {
                StyleViewHolder styleViewHolder = (StyleViewHolder) viewHolder;
                styleViewHolder.nameAsset.setText(glitterEffectBean.getEffectName( ));
                RequestOptions options = new RequestOptions( );
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                options.centerInside( );
                if (mContext != null) {
                    Glide.with(mContext)
                            .asBitmap( )
                            .load(glitterEffectBean.getEffectIcon( ))
                            .apply(options)
                            .into(styleViewHolder.imageAsset);
                }
                if (mSelectPos == pos) {
                    styleViewHolder.nameAsset.setTextColor(Color.parseColor("#3699FF"));
                    styleViewHolder.layoutAsset.setBackgroundResource(R.drawable.fx_item_radius_shape_select);
                } else {
                    styleViewHolder.nameAsset.setTextColor(Color.parseColor("#FFFFFF"));
                    styleViewHolder.layoutAsset.setBackgroundResource(R.drawable.fx_item_radius_shape_unselect);
                }
                styleViewHolder.assetItem.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {
                        if (mSelectPos == pos) {
                            if (mStyleClickListener != null) {
                                mStyleClickListener.onSameItemClick(mSelectPos);
                            }
                            return;
                        }
                        notifyItemChanged(mSelectPos);
                        mSelectPos = pos;
                        notifyItemChanged(mSelectPos);
                        if (mStyleClickListener != null) {
                            mStyleClickListener.onItemClick(v, pos, glitterEffectBean);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mGlitterEffectBeanList != null) {
            return mGlitterEffectBeanList.size( );
        } else {
            return 0;
        }
    }

    private class StyleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout assetItem;
        RelativeLayout layoutAsset;
        ImageView imageAsset;
        TextView nameAsset;

        public StyleViewHolder(View itemView) {
            super(itemView);
            assetItem = itemView.findViewById(R.id.assetItem);
            layoutAsset = itemView.findViewById(R.id.layoutAsset);
            imageAsset = itemView.findViewById(R.id.imageAsset);
            nameAsset = itemView.findViewById(R.id.nameAsset);
        }
    }

    public interface StyleClickListener {
        void onItemClick(View view, int pos, GlitterEffectBean glitterEffectBean);

        void onSameItemClick(int pos);
    }
}

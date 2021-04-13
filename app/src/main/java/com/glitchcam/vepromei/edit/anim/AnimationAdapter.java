package com.glitchcam.vepromei.edit.anim;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.data.FilterItem;

import java.util.ArrayList;

/**
 * Created by jml on 2020/08/24
 */

public class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private ArrayList<FilterItem> mThemeDataList = new ArrayList<>();
    private int mSelectPos = 0;
    RequestOptions mOptions = new RequestOptions();

    public int getSelectedPosition() {
        return mSelectPos;
    }

    /**
     * 获取当前选中的Item
     *
     * @return
     */
    public FilterItem getSelectFilterItem() {
        if (mThemeDataList != null && mThemeDataList.size() > mSelectPos) {
            return mThemeDataList.get(mSelectPos);
        }
        return null;
    }

    /**
     * 设置选中的Item
     *
     * @param selectFilterItem
     */
    public void setSelectItem(FilterItem selectFilterItem) {
        if (selectFilterItem == null) {
            return;
        }
        if (mThemeDataList != null) {
            for (int i = 0; i < mThemeDataList.size(); i++) {
                FilterItem filterItem = mThemeDataList.get(i);
                if (!TextUtils.isEmpty(filterItem.getPackageId()) && TextUtils.equals(selectFilterItem.getPackageId(), filterItem.getPackageId())) {
                    mSelectPos = i;
                    return;
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public AnimationAdapter(Context context) {
        mContext = context;
        mOptions.centerCrop();
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.default_filter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setAnimationDataList(ArrayList<FilterItem> themeDataList) {
        this.mThemeDataList = themeDataList;
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_assetLayout;
        private SimpleDraweeView item_assetImage;
        private TextView item_assetName;
        private View view_selected;

        public ViewHolder(View view) {
            super(view);
            item_assetLayout = (RelativeLayout) view.findViewById(R.id.layoutAsset);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (SimpleDraweeView) view.findViewById(R.id.imageAsset);
            view_selected = view.findViewById(R.id.view_selected);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animation, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        FilterItem itemData = mThemeDataList.get(position);
        holder.item_assetName.setText(itemData.getFilterName());
        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0)
                holder.item_assetImage.setImageResource(imageId);
        } else {
            /**
             * 加载webp图片
             */
            String imageUrl = itemData.getImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(itemData.getImageUrl())
                        .setAutoPlayAnimations(true)
                        .setOldController(holder.item_assetImage.getController())
                        .build();
                holder.item_assetImage.setController(controller);
            } else {
                holder.item_assetImage.setImageResource(R.mipmap.default_theme);
            }
        }

        if (mSelectPos == position) {
            //holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_shape_select));
            holder.view_selected.setVisibility(View.VISIBLE);
            holder.item_assetName.setTextColor(Color.parseColor("#994a90e2"));
        } else {
            //holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_shape_unselect));
            holder.view_selected.setVisibility(View.GONE);
            holder.item_assetName.setTextColor(Color.parseColor("#CCffffff"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mThemeDataList.size();
    }

    private static void setCircle(SimpleDraweeView imageView) {
        RoundingParams roundingParams = imageView.getHierarchy().getRoundingParams();
        roundingParams.setRoundAsCircle(true);
        imageView.getHierarchy().setRoundingParams(roundingParams);
    }

}

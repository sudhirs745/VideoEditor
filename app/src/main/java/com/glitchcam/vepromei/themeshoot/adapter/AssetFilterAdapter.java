package com.glitchcam.vepromei.themeshoot.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseConstants;
import com.glitchcam.vepromei.edit.data.FilterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyj on 2017/12/19 0019.
 */

public class AssetFilterAdapter extends RecyclerView.Adapter<AssetFilterAdapter.ViewHolder> {
    private Context mContext;
    private Boolean mIsArface = false;
    private OnItemClickListener mClickListener;
    private List<FilterItem> mFilterDataList = new ArrayList<>();
    RequestOptions mOptions = new RequestOptions();
    private int mSelectPos = 0;
    private int mSpecialCount = 0;
    private final int ITEM_TYPE_NONE = 0;
    private final int ITEM_TYPE_SPLIT = 1;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public AssetFilterAdapter(Context context) {
        mContext = context;
        mOptions.centerCrop();
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.default_filter);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setFilterDataList(List<FilterItem> filterDataList) {
        this.mFilterDataList = filterDataList;
        mSpecialCount = 0;
        for (int i = 0; i < mFilterDataList.size(); i++) {
            if (mFilterDataList.get(i).isSpecialFilter()) {
                mSpecialCount++;
            }
        }
    }

    public void setmSelectPos(int mSelectPos) {
        this.mSelectPos = mSelectPos;
        notifyDataSetChanged();
    }

    public int getSpecialFilterCount() {
        return mSpecialCount;
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
    }

    public int getSelectPos() {
        return mSelectPos;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View item_assetShadow;
        private ImageView item_assetImage;
        private TextView item_assetName;
        private ImageView mProp3DImage;

        public ViewHolder(View view) {
            super(view);
            item_assetShadow = view.findViewById(R.id.assetShadow);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            mProp3DImage = view.findViewById(R.id.prop_3d_image);
        }
    }

    public void isArface(Boolean isArface) {
        mIsArface = isArface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE_NONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset_filter, parent, false);
        } else {
            view = new View(parent.getContext());
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public List<FilterItem> getFilterDataList() {
        return mFilterDataList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mSpecialCount > 0 && position == (mSpecialCount + 1)) {
            return;
        }
        FilterItem itemData = mFilterDataList.get(position);
        if (itemData == null)
            return;
        String name = itemData.getFilterName();
        if (name != null && !mIsArface) {
            holder.item_assetName.setText(name);
        }
        if (mIsArface) {
            holder.item_assetName.setText("");
        }

        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0)
                holder.item_assetImage.setImageResource(imageId);
        } else {
            String imageUrl = itemData.getImageUrl();
            if (imageUrl != null) {
                //加载图片
                Glide.with(mContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(mOptions)
                        .into(holder.item_assetImage);
            }
        }

        if (mSelectPos == position) {
            holder.item_assetShadow.setBackgroundColor(ContextCompat.getColor(mContext, R.color.fx_select));
            holder.item_assetName.setTextColor(ContextCompat.getColor(mContext, R.color.ms994a90e2));
        } else {
            holder.item_assetShadow.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nv_colorTranslucent));
            holder.item_assetName.setTextColor(ContextCompat.getColor(mContext, R.color.ccffffff));
        }
        if ("00C96B57-3E1E-4E3D-A4D8-D1E3BB3589BA".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("233C8731-7D9E-4D6D-85B6-87D104FC3CCF".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("7269C2C7-6249-4ABF-9329-325898DAD9E6".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_2D);
        } else if ("11526CF9-BFA0-4A19-B7B2-1A879CF58FF1".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("B2187FB5-A8B3-4E87-A5CD-F8EA6B3456D4".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("7242B80E-A804-4CB5-B7DD-DFACC1B6BF6F".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("3A66960A-E129-4040-B523-1C87544FB008".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("DD133FD6-75F4-4584-8206-BBF257D92D44".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("084A6EC1-43AB-40EF-BBD5-D83F692B011B".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_2D);
        } else if ("289829A7-EA10-423E-96EA-5BBB23A1B86D".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        }

        if (itemData.getCategoryId() <= BaseConstants.PROP_IMAGES.length && itemData.getCategoryId() - 1 >= 0) {
            holder.mProp3DImage.setVisibility(View.VISIBLE);
            holder.mProp3DImage.setBackground(mContext.getResources().getDrawable(BaseConstants.PROP_IMAGES[itemData.getCategoryId() - 1]));
        } else {
            holder.mProp3DImage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, position);
                }

                if (mSelectPos == position) {
                    return;
                }

                notifyItemChanged(mSelectPos);
                mSelectPos = position;
                notifyItemChanged(mSelectPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mSpecialCount > 0 && position == (mSpecialCount + 1)) {
            return ITEM_TYPE_SPLIT;
        } else {
            return ITEM_TYPE_NONE;
        }
    }
}

package com.glitchcam.vepromei.edit.background.view;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.meicam.sdk.NvsColor;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.background.MultiColorInfo;
import com.glitchcam.vepromei.edit.data.ParseJsonFile;
import com.glitchcam.vepromei.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupanfeng
 * @desc 横线颜色控件
 * @date 2020/10/20 17:24
 */
public class MultiColorView extends LinearLayout {

    private static final String COLOR_ASSETS_PATH = "background/color/colorAxis.json";
    private RecyclerView mRecyclerView;
    private ArrayList<MultiColorInfo> mColorList;
    private Context mContext;
    private MultiColorAdapter mMultiColorAdapter;
    private OnMultiColorItemClickListener mOnMultiColorItemClickListener;

    private int mOnSelectPosition = -1;

    public MultiColorView(Context context) {
        super(context);
        init(context);
    }

    public MultiColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_multi_color_view, this);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        initData();
        initRecyclerView();
    }

    private void initData() {
        mColorList = new ArrayList<>();
        initCaptionColorList();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mMultiColorAdapter = new MultiColorAdapter();
        mRecyclerView.setAdapter(mMultiColorAdapter);
    }


    private void initCaptionColorList() {
        List<MultiColorInfo> colorList = getBackgroundColorList();
        for (MultiColorInfo colorInfo : colorList) {
            colorInfo.setColorValue(ColorUtil.nvsColorToHexString(new NvsColor(colorInfo.r, colorInfo.g, colorInfo.b, 1.0F)));
            mColorList.add(colorInfo);
        }
    }

    public void setSelectPosition(int position) {
        if (position == mOnSelectPosition) {
            return;
        }
        int pos = mOnSelectPosition;
        mOnSelectPosition = position;
        mMultiColorAdapter.notifyItemChanged(pos);
        mMultiColorAdapter.notifyItemChanged(mOnSelectPosition);
    }

    public MultiColorInfo getSelectData() {
        if (mOnSelectPosition >= 0) {
            return mColorList.get(mOnSelectPosition);
        }
        return null;
    }

    private class MultiColorAdapter extends RecyclerView.Adapter<MultiHolder> {

        @NonNull
        @Override
        public MultiHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(mContext).inflate(R.layout.view_multi_color_item, null);
            return new MultiHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MultiHolder multiHolder, int i) {
            final int position = i;
            final MultiColorInfo captionColorInfo = mColorList.get(position);
            multiHolder.mIvColor.setBackgroundColor(Color.parseColor(captionColorInfo.getColorValue()));
            multiHolder.mMask.setBackgroundColor(Color.parseColor(captionColorInfo.getColorValue()));

            multiHolder.cv_mask.setVisibility(mOnSelectPosition == position ? View.VISIBLE : View.GONE);

            multiHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMultiColorItemClickListener != null) {
                        mOnMultiColorItemClickListener.onItemClick(view, captionColorInfo);
                    }
                    if (mOnSelectPosition == position) {
                        return;
                    }
                    notifyItemChanged(mOnSelectPosition);
                    mOnSelectPosition = position;
                    notifyItemChanged(mOnSelectPosition);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mColorList == null ? 0 : mColorList.size();
        }

    }

    private class MultiHolder extends RecyclerView.ViewHolder {

        private ImageView mIvColor;
        private ImageView mMask;
        private CardView cv_color;
        private CardView cv_mask;

        public MultiHolder(@NonNull View itemView) {
            super(itemView);
            mIvColor = (ImageView) itemView.findViewById(R.id.iv_color);
            mMask = (ImageView) itemView.findViewById(R.id.iv_color_mask);
            cv_color = (CardView) itemView.findViewById(R.id.card_color);
            cv_mask = (CardView) itemView.findViewById(R.id.card_color_mask);
        }
    }

    /**
     * 从json本地文件 转换成 List集合的数据  反序列化
     *
     * @return
     */
    private List<MultiColorInfo> getBackgroundColorList() {
        String colorJson = ParseJsonFile.readAssetJsonFile(mContext, COLOR_ASSETS_PATH);
        if (TextUtils.isEmpty(colorJson)) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        TypeToken<ArrayList<MultiColorInfo>> typeToken = new TypeToken<ArrayList<MultiColorInfo>>() {
        };
        return gson.fromJson(colorJson, typeToken.getType());
    }


    public ArrayList<MultiColorInfo> getColorList() {
        return mColorList;
    }

    public void setOnMultiColorItemClickListener(OnMultiColorItemClickListener onMultiColorItemClickListener) {
        this.mOnMultiColorItemClickListener = onMultiColorItemClickListener;
    }

    public interface OnMultiColorItemClickListener {

        /**
         * 颜色Item点击回调
         *
         * @param view
         * @param colorInfo
         */
        void onItemClick(View view, MultiColorInfo colorInfo);

    }

}

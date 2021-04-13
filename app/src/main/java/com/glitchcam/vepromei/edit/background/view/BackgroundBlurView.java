package com.glitchcam.vepromei.edit.background.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.background.BackgroundBlurInfo;
import com.glitchcam.vepromei.edit.background.SpaceItemDecoration;
import com.glitchcam.vepromei.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupanfeng
 * @desc
 * @date 2020/10/21 10:45
 */
public class BackgroundBlurView extends LinearLayout {

    private final int MAX_BLUR_STRENGTH = 50;//模糊最大值
    private final static int GRADE_COUNT = 4;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private BackgroundBlurAdapter mBackgroundBlurAdapter;
    private List<BackgroundBlurInfo> mData;

    private int[] blurs = {
      R.mipmap.ic_blur_no,
      R.mipmap.blur_2,
      R.mipmap.blur_3,
      R.mipmap.blur_4,
      R.mipmap.blur_5
    };

    private int mOnSelectPosition;
    private OnBackgroundBlurItemClickListener mOnBackgroundBlurItemClickListener;

    public BackgroundBlurView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundBlurView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundBlurView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_background_blur_view, this);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        initData();
        initRecyclerView();
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mBackgroundBlurAdapter = new BackgroundBlurAdapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mBackgroundBlurAdapter);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dp2px(mContext, 3), ScreenUtils.dp2px(getContext(), 12)));
    }

    private void initData() {
        mData = new ArrayList<>();
        mData = new ArrayList<>();
        for (int i = 1; i < GRADE_COUNT + 1; i++) {
            BackgroundBlurInfo backgroundBlurInfo = new BackgroundBlurInfo(i + "");
            mData.add(backgroundBlurInfo);
        }
        mData.add(0, new BackgroundBlurInfo(mContext.getResources().getString(R.string.background_blur_no)));
    }

    public void setSelectPosition(int position) {
        if (position == mOnSelectPosition) {
            return;
        }
        int oldPosition = mOnSelectPosition;
        mOnSelectPosition = position;
        mBackgroundBlurAdapter.notifyItemChanged(oldPosition);
        mBackgroundBlurAdapter.notifyItemChanged(mOnSelectPosition);
    }


    private class BackgroundBlurAdapter extends RecyclerView.Adapter<BlurHolder> {

        @Override
        public BlurHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = null;
            view = LayoutInflater.from(mContext).inflate(R.layout.item_background_blur, null);
            return new BlurHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BlurHolder blurHolder, int i) {
            final int position = i;
            BackgroundBlurInfo backgroundBlurInfo = mData.get(i);
            if (backgroundBlurInfo == null) {
                return;
            }

            blurHolder.mIvContent.setBackgroundResource(blurs[position]);
            blurHolder.mIvMask.setBackgroundResource(blurs[position]);

            blurHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnBackgroundBlurItemClickListener != null) {
                        mOnBackgroundBlurItemClickListener.onBlurItemClick(view, getStrength(position));
                    }
                    if (mOnSelectPosition == position) {
                        return;
                    }
                    notifyItemChanged(mOnSelectPosition);
                    mOnSelectPosition = position;
                    notifyItemChanged(mOnSelectPosition);
                }
            });

            blurHolder.cvMask.setVisibility(mOnSelectPosition == position ? View.VISIBLE : View.GONE);

        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

    }


    private class BlurHolder extends RecyclerView.ViewHolder {

        private CardView cvMask;
        private ImageView mIvContent;
        private ImageView mIvMask;

        public BlurHolder(@NonNull View itemView) {
            super(itemView);
            cvMask = itemView.findViewById(R.id.cv_blur_mask);
            mIvContent = itemView.findViewById(R.id.iv_blur);
            mIvMask = itemView.findViewById(R.id.iv_blur_mask);
        }

    }

    public float getStrength(int position) {
        float strength = position * 1.0F / GRADE_COUNT;
        strength *= MAX_BLUR_STRENGTH;
        return strength;
    }

    public List<BackgroundBlurInfo> getBlurData() {
        return mData;
    }

    public void setOnBackgroundBlurItemClickListener(OnBackgroundBlurItemClickListener onBackgroundBlurItemClickListener) {
        this.mOnBackgroundBlurItemClickListener = onBackgroundBlurItemClickListener;
    }


    public interface OnBackgroundBlurItemClickListener {

        /**
         * 模糊点击回调
         *
         * @param view
         * @param strength 模糊强度
         */
        void onBlurItemClick(View view, float strength);

    }

}

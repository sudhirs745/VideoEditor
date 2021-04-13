package com.glitchcam.vepromei.edit.watermark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseFragment;
import com.glitchcam.vepromei.edit.adapter.SpaceItemDecoration;
import com.glitchcam.vepromei.utils.ScreenUtils;
import com.glitchcam.vepromei.utils.SystemUtils;
import com.glitchcam.vepromei.utils.dataInfo.TimelineData;
import com.glitchcam.vepromei.utils.dataInfo.VideoFx;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * @author lpf
 * 效果页面
 */
@SuppressLint("ValidFragment")
public class EffectFragment extends BaseFragment {

    private Context mContext;
    private RecyclerView mRecyclerEffect;
    private OnItemClickListener mOnItemClickListener;
    private RecyclerView.Adapter mEffectAdapter;
    private List<EffectItemData> mData = new ArrayList<>();

    private SeekBar mSeekBarViewLevel, mSeekBarViewNum, mSeekBarLevelBlur;
    private LinearLayout linearBlur, linearMosic;

    private OnEffectSeekBarListener mOnEffectSeekBarListener;


    private int mSelectPosition = -1;

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void refreshSelectPosition() {
        int position = mSelectPosition;
        mSelectPosition = -1;
        mEffectAdapter.notifyItemChanged(position);
    }

    public EffectFragment(Context context, OnItemClickListener listener) {
        this.mContext = context;
        mOnItemClickListener = listener;
    }


    @Override
    protected int initRootView() {
        return R.layout.fragment_effect;
    }

    @Override
    protected void initArguments(Bundle arguments) {
    }

    @Override
    protected void initView() {
        mRecyclerEffect = (RecyclerView) mRootView.findViewById(R.id.recycler_watermark);

        mSeekBarViewLevel = mRootView.findViewById(R.id.view_seek_bar_level);
        mSeekBarViewNum = mRootView.findViewById(R.id.view_seek_bar_num);
        mSeekBarLevelBlur = mRootView.findViewById(R.id.view_seek_bar_level_blur);
        linearBlur = mRootView.findViewById(R.id.linear_blur);
        linearMosic = mRootView.findViewById(R.id.linear_mosic);

    }

    @Override
    protected void onLazyLoad() {
        mData.clear();

        EffectItemData effectItemData = new EffectItemData();
        effectItemData.setType(EffectItemData.TYPE_MOSAIC);
        effectItemData.setName(getResources().getString(R.string.effect_name_mosaic));
        effectItemData.setImagePath(R.mipmap.icon_effect_masaike);
        mData.add(effectItemData);

        effectItemData = new EffectItemData();
        effectItemData.setType(EffectItemData.TYPE_BLUR);
        effectItemData.setName(getResources().getString(R.string.effect_name_blur));
        effectItemData.setImagePath(R.mipmap.icon_effect_blur);
        mData.add(effectItemData);

    }

    @Override
    protected void initListener() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(HORIZONTAL);
        mRecyclerEffect.addItemDecoration(new SpaceItemDecoration(0, ScreenUtils.dip2px(getActivity(), 29)));
        mRecyclerEffect.setLayoutManager(linearLayoutManager);
        mRecyclerEffect.setAdapter(mEffectAdapter = new EffectAdapter());


        mSeekBarViewLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float level = progress / 100.0f;
                if (mOnEffectSeekBarListener != null) {
                    mOnEffectSeekBarListener.onViewLevelChange(level);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarViewNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float num = progress / 1000.0f;
                if (mOnEffectSeekBarListener != null) {
                    mOnEffectSeekBarListener.onViewNumberChange(num);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarLevelBlur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float level = progress * 0.64f;
                if (mOnEffectSeekBarListener != null) {
                    mOnEffectSeekBarListener.onViewBlurChange(level);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(
                    mContext).inflate(R.layout.item_effect_recycleview, parent,
                    false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            boolean isChinese = SystemUtils.isZh(MSApplication.getmContext());
            EffectItemData effectItemData = mData.get(position);

            if (effectItemData != null) {
                int imagePath = effectItemData.getImagePath();
                Glide.with(mContext).load(imagePath).into(holder.imageView);
                holder.mName.setText(effectItemData.getName());
            }

            holder.itemWatermarkCover.setVisibility(mSelectPosition == position ? View.VISIBLE : View.INVISIBLE);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEffectAdapter.notifyItemChanged(mSelectPosition);
                    mSelectPosition = position;
                    mEffectAdapter.notifyItemChanged(position);
                    mOnItemClickListener.onEffectClick(holder, position, mData.get(position));

                    if (position == 0) {
                        linearBlur.setVisibility(View.INVISIBLE);
                        linearMosic.setVisibility(View.VISIBLE);
                    } else {
                        linearBlur.setVisibility(View.VISIBLE);
                        linearMosic.setVisibility(View.INVISIBLE);
                    }

                    updateSeekBarStatus();
                }
            });
        }

        private void updateSeekBarStatus() {
            VideoFx videoFx = TimelineData.instance().getVideoFx();
            if (videoFx == null) {
                return;
            }
            mSeekBarViewLevel.setProgress((int) (videoFx.getIntensity() * 100));
            mSeekBarViewNum.setProgress((int) (videoFx.getUnitSize() * 1000.0f));
            mSeekBarLevelBlur.setProgress((int) (videoFx.getRadius() / 0.64));
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView itemWatermarkCover;
            TextView mName;

            MyViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.item_watermark_image);
                itemWatermarkCover = (ImageView) view.findViewById(R.id.item_watermark_cover);
                mName = view.findViewById(R.id.tv_name);

            }
        }
    }

    public void setOnEffectSeekBarListener(OnEffectSeekBarListener onEffectSeekBarListener) {
        this.mOnEffectSeekBarListener = onEffectSeekBarListener;
    }

    public interface OnEffectSeekBarListener {

        /**
         * 马赛克
         *
         * @param value
         */
        void onViewLevelChange(float value);


        /**
         * 马赛克 数量
         *
         * @param value
         */
        void onViewNumberChange(float value);


        /**
         * 模糊程度
         *
         * @param value
         */
        void onViewBlurChange(float value);

    }
}

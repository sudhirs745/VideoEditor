package com.glitchcam.vepromei.edit.background.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.background.BackgroundBlurInfo;
import com.glitchcam.vepromei.edit.background.BackgroundStyleInfo;
import com.glitchcam.vepromei.edit.background.MultiColorInfo;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liupanfeng
 * @desc
 * @date 2020/10/21 10:24
 */
public class BackgroundBottomView extends LinearLayout {

    public static final int TYPE_BACKGROUND_COLOR = 1;
    public static final int TYPE_BACKGROUND_STYLE = 2;
    public static final int TYPE_BACKGROUND_BLUR = 3;
    private int mType;

    private Context mContext;
    private ImageView mIvFinish;

    private BackgroundBlurView mBackgroundBlurView;
    private BackgroundStyleView mBackgroundStyleView;
    private MultiColorView mBackgroundColorView;

    private OnBackgroundBottomItemClickListener mOnBackgroundBottomItemClickListener;
    private RelativeLayout mApplyAllContainerView;


    public BackgroundBottomView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundBottomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_background_bottom_view, this);
        mBackgroundBlurView = view.findViewById(R.id.background_blur_view);
        mBackgroundStyleView = view.findViewById(R.id.background_style_view);
        mBackgroundColorView = view.findViewById(R.id.background_color_view);
        mApplyAllContainerView = view.findViewById(R.id.rl_apply_all_container);

        mIvFinish = view.findViewById(R.id.iv_finish);
        initListener();
    }

    private void initListener() {

        mApplyAllContainerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mType) {
                    case TYPE_BACKGROUND_BLUR:
                        if (mOnBackgroundBottomItemClickListener != null) {
                            mOnBackgroundBottomItemClickListener.onBlurApplyAll();
                        }
                        break;
                    case TYPE_BACKGROUND_STYLE:
                        if (mOnBackgroundBottomItemClickListener != null) {
                            BackgroundStyleInfo selectData = mBackgroundStyleView.getSelectData();
                            if (selectData == null) {
                                return;
                            }
                            mOnBackgroundBottomItemClickListener.onStyleApplyAll(selectData.getFilePath());
                        }
                        break;
                    case TYPE_BACKGROUND_COLOR:
                        if (mOnBackgroundBottomItemClickListener != null) {
                            MultiColorInfo selectData = mBackgroundColorView.getSelectData();
                            if (selectData == null) {
                                return;
                            }
                            mOnBackgroundBottomItemClickListener.onColorApplyAll(selectData.getColorValue());
                        }
                        break;
                    default:

                        break;
                }

            }
        });

        mIvFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnBackgroundBottomItemClickListener != null) {
                    mOnBackgroundBottomItemClickListener.onConfirmClick();
                }
            }
        });

        mBackgroundColorView.setOnMultiColorItemClickListener(new MultiColorView.OnMultiColorItemClickListener() {
            @Override
            public void onItemClick(View view, MultiColorInfo colorInfo) {
                if (mOnBackgroundBottomItemClickListener != null) {
                    mOnBackgroundBottomItemClickListener.onColorItemClick(view, colorInfo);
                }
            }
        });


        mBackgroundStyleView.setOnBackgroundStyleItemClickListener(new BackgroundStyleView.OnBackgroundStyleItemClickListener() {
            @Override
            public void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo) {
                if (mOnBackgroundBottomItemClickListener != null) {
                    mOnBackgroundBottomItemClickListener.onStyleItemClick(view, position, backgroundStyleInfo);
                }
            }
        });

        mBackgroundBlurView.setOnBackgroundBlurItemClickListener(new BackgroundBlurView.OnBackgroundBlurItemClickListener() {
            @Override
            public void onBlurItemClick(View view, float strength) {
                if (mOnBackgroundBottomItemClickListener != null) {
                    mOnBackgroundBottomItemClickListener.onBlurItemClick(view, strength);
                }
            }
        });
    }

    public void showView(int type) {
        mType = type;
        BackgroundBottomView.this.setVisibility(VISIBLE);
        switch (type) {
            case TYPE_BACKGROUND_COLOR:
                mBackgroundColorView.setVisibility(VISIBLE);
                mBackgroundBlurView.setVisibility(GONE);
                mBackgroundStyleView.setVisibility(GONE);
                break;
            case TYPE_BACKGROUND_BLUR:
                mBackgroundColorView.setVisibility(GONE);
                mBackgroundBlurView.setVisibility(VISIBLE);
                mBackgroundStyleView.setVisibility(GONE);
                break;
            case TYPE_BACKGROUND_STYLE:
                mBackgroundColorView.setVisibility(GONE);
                mBackgroundBlurView.setVisibility(GONE);
                mBackgroundStyleView.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    public void setOnBackgroundBottomItemClickListener(OnBackgroundBottomItemClickListener onBackgroundBottomItemClickListener) {
        this.mOnBackgroundBottomItemClickListener = onBackgroundBottomItemClickListener;
    }

    public void setSelectColor(ClipInfo clipInfo) {
        if (clipInfo == null) {
            return;
        }
        ArrayList<MultiColorInfo> colorList = mBackgroundColorView.getColorList();
        int position = -1;
        for (int i = 0; i < colorList.size(); i++) {
            MultiColorInfo multiColorInfo = colorList.get(i);
            if (multiColorInfo == null) {
                continue;
            }
            if (multiColorInfo.getColorValue().equals(clipInfo.getBackgroundValue())) {
                position = i;
                break;
            }
        }
        mBackgroundColorView.setSelectPosition(position);
    }

    public void setSelectBlur(ClipInfo clipInfo) {
        if (clipInfo == null) {
            return;
        }
        List<BackgroundBlurInfo> blurInfos = mBackgroundBlurView.getBlurData();
        int position = -1;
        for (int i = 0; i < blurInfos.size(); i++) {
            BackgroundBlurInfo multiColorInfo = blurInfos.get(i);
            if (multiColorInfo == null) {
                continue;
            }
            float strength= mBackgroundBlurView.getStrength(i);
            if (String.valueOf(strength).equals(clipInfo.getBackgroundValue())) {
                position = i;
                break;
            }
        }
        mBackgroundBlurView.setSelectPosition(position);
    }

    public void setSelectStyle(ClipInfo clipInfo) {
        if (clipInfo == null) {
            return;
        }
        List<BackgroundStyleInfo> styleInfos = mBackgroundStyleView.getData();
        int position = 1;
        String nowClipBackgroundValue = clipInfo.getBackgroundValue();
        for (int i = 0; i < styleInfos.size(); i++) {
            BackgroundStyleInfo backgroundStyleInfo = styleInfos.get(i);
            if (backgroundStyleInfo == null) {
                continue;
            }
            String filePath = backgroundStyleInfo.getFilePath();
            if (TextUtils.isEmpty(filePath) && TextUtils.isEmpty(nowClipBackgroundValue)) {
                break;
            } else if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(nowClipBackgroundValue)
                    && filePath.equals(clipInfo.getBackgroundValue())) {
                position = i;
                break;
            }
        }
        mBackgroundStyleView.setSelectPosition(position);
    }

    public interface OnBackgroundBottomItemClickListener {

        /**
         * 颜色控件点击回调
         *
         * @param view
         * @param colorInfo
         */
        void onColorItemClick(View view, MultiColorInfo colorInfo);

        /**
         * 背景样式选择
         *
         * @param view
         * @param backgroundStyleInfo
         */
        void onStyleItemClick(View view, int position, BackgroundStyleInfo backgroundStyleInfo);

        /**
         * 模糊条目点击
         *
         * @param view
         * @param strength
         */
        void onBlurItemClick(View view, float strength);

        /**
         * 确认按钮点击回调
         */
        void onConfirmClick();

        /**
         * 样式类背景的应用全部
         *
         * @param filePath
         */
        void onStyleApplyAll(String filePath);

        /**
         * 颜色背景应用于全部
         *
         * @param colorValue
         */
        void onColorApplyAll(String colorValue);

        /**
         * 模糊应用到全部
         */
        void onBlurApplyAll();
    }

}

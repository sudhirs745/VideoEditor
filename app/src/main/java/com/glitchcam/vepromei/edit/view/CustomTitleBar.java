package com.glitchcam.vepromei.edit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.interfaces.OnTitleBarClickListener;
import com.glitchcam.vepromei.utils.AppManager;

/**
 * Created by CaoZhiChao on 2018/5/28 15:10
 */
public class CustomTitleBar extends LinearLayout implements View.OnClickListener {
    private boolean finishActivity = true;

    private TextView textCenter, textRight;
    private ImageView backLayoutImageView;
    private RelativeLayout backLayout;
    private RelativeLayout forwardLayout;
    private RelativeLayout mainLayout;

    OnTitleBarClickListener onTitleBarClickListener;

    public CustomTitleBar(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);

        @SuppressLint("InflateParams")
        View viewParent = mInflater.inflate(R.layout.view_titlebar, null);
        addView(viewParent);

        mainLayout = viewParent.findViewById(R.id.main_layout);
        backLayout = viewParent.findViewById(R.id.backLayout);
        textCenter = viewParent.findViewById(R.id.text_center);
        textRight = viewParent.findViewById(R.id.text_right);
        forwardLayout = viewParent.findViewById(R.id.forwardLayout);
        backLayoutImageView = viewParent.findViewById(R.id.back_layout_imageView);

        textCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources( ).getDimension(R.dimen.title_textSize));
        textRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources( ).getDimension(R.dimen.title_textSize));

        textCenter.getPaint().setFakeBoldText(true);
        textRight.getPaint().setFakeBoldText(true);

        textCenter.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        forwardLayout.setOnClickListener(this);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setFinishActivity(boolean finishActivity) {
        this.finishActivity = finishActivity;
    }

    public void setTextCenterColor(int color) {
        textCenter.setTextColor(color);
    }

    public void setTextCenter(String msg) {
        textCenter.setText(msg);
    }

    public void setTextCenter(@StringRes int resid) {
        textCenter.setText(getContext( ).getResources( ).getText(resid));
    }

    public void setTextRight(String msg) {
        textRight.setText(msg);
    }

    public void setTextRight(@StringRes int resid) {
        textRight.setText(getContext( ).getResources( ).getText(resid));
    }

    public void setTextRightVisible(int visible) {
        textRight.setVisibility(visible);
        forwardLayout.setClickable(visible == View.VISIBLE);
    }

    public void setBackImageVisible(int visible) {
        backLayout.setVisibility(visible);
    }

    public void setBackImageIcon(int resid) {
        backLayoutImageView.setImageResource(resid);
    }

    public void setMainLayoutResource(int resid) {
        mainLayout.setBackgroundResource(resid);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId( )) {
            case R.id.backLayout:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnBackImageClick( );
                }
                if (finishActivity) {
                    AppManager.getInstance( ).finishActivity( );
                }
                break;
            case R.id.text_center:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnCenterTextClick( );
                }
                break;
            case R.id.forwardLayout:
                if (onTitleBarClickListener != null) {
                    onTitleBarClickListener.OnRightTextClick( );
                }
                break;
            default:
                break;
        }
    }
}

package com.glitchcam.vepromei.mimodemo.common.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.AppManager;


public class CustomTitleBar extends LinearLayout implements View.OnClickListener {
    TextView textCenter, textRight;
    private ImageView backLayout;
    OnTitleBarClickListener onTitleBarClickListener;

    public CustomTitleBar(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View viewParent = mInflater.inflate(R.layout.mimo_view_titlebar, null);
        addView(viewParent);
        backLayout = (ImageView) viewParent.findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        textCenter = (TextView) viewParent.findViewById(R.id.text_center);
        textCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_text_size));
        textCenter.getPaint().setFakeBoldText(true);
        textCenter.setOnClickListener(this);
        textRight = (TextView) viewParent.findViewById(R.id.text_right);
        textRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.title_text_size));
        textRight.getPaint().setFakeBoldText(true);
        textRight.setOnClickListener(this);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setTextCenter(String msg) {
        textCenter.setText(msg);
    }

    public void setTextCenter(@StringRes int resid) {
        textCenter.setText(getContext().getResources().getText(resid));
    }

    public void setTextRight(String msg) {
        textRight.setText(msg);
    }

    public void setTextRight(@StringRes int resid) {
        textRight.setText(getContext().getResources().getText(resid));
    }

    public void setTextRightVisible(int visible) {
        textRight.setVisibility(visible);
    }

    public void setBackImageVisible(int visible) {
        backLayout.setVisibility(visible);
    }

    public OnTitleBarClickListener getOnTitleBarClickListener() {
        return onTitleBarClickListener;
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
        this.onTitleBarClickListener = onTitleBarClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.backLayout) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.OnBackImageClick();
            }
            AppManager.getInstance().finishActivity();
        } else if (i == R.id.text_center) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.OnCenterTextClick();
            }
        } else if (i == R.id.text_right) {
            if (onTitleBarClickListener != null) {
                onTitleBarClickListener.OnRightTextClick();
            }
        }
    }

    public interface OnTitleBarClickListener {
        void OnBackImageClick();

        void OnCenterTextClick();

        void OnRightTextClick();
    }
}

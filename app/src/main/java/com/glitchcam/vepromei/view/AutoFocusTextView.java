package com.glitchcam.vepromei.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

public class AutoFocusTextView extends androidx.appcompat.widget.AppCompatTextView {
    public AutoFocusTextView(Context context) {
        super(context);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        //textView 在recyclerview中实现滚动效果，需要获取焦点，
        return true;
    }

}

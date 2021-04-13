package com.glitchcam.vepromei.themeshoot;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.base.BaseActivity;
import com.glitchcam.vepromei.themeshoot.bean.CaptionBean;

/**
 * 字幕编辑
 */
public class ThemeEditCaptionActivity extends BaseActivity {

    private CaptionBean captionBean;
    private EditText etCaption;

    @Override
    protected int initRootView() {
        return R.layout.activity_theme_edit_caption;
    }

    @Override
    protected void initViews() {
        etCaption = findViewById(R.id.et_caption);

    }

    private void initCaptionBean(CaptionBean captionBean) {
        etCaption.setText(captionBean.getText());
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            captionBean = (CaptionBean) getIntent().getExtras().getSerializable("CaptionBean");
            if (captionBean != null) {
                initCaptionBean(captionBean);
            }
        }
    }

    @Override
    protected void initListener() {
        findViewById(R.id.tv_caption_compile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                captionBean.setText(etCaption.getText().toString());
                intent.putExtra("CaptionBean", captionBean);
                setResult(2, intent);
                finish();
            }
        });
        findViewById(R.id.tv_caption_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
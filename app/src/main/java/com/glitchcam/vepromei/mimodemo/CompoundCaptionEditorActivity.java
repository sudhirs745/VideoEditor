package com.glitchcam.vepromei.mimodemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.base.BaseActivity;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.CompoundCaptionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TimelineData;
import com.glitchcam.vepromei.mimodemo.common.utils.AppManager;
import com.glitchcam.vepromei.mimodemo.common.view.CustomTitleBar;

import java.util.ArrayList;

public class CompoundCaptionEditorActivity extends BaseActivity {
    public static final String INTENT_KEY_CAPTION_INDEX = "captionIndex";
    public static final String INTENT_KEY_CAPTION_TEXT = "captionText";
    private static final int DELAY_TIME_SHOW_INPUT = 100;
    private EditText mCaptionInput;
    private ImageView mCancel;
    private ImageView mConfirm;
    private CustomTitleBar mTitleBar;
    private int mCaptionIndex;
    private String mCaptionText;
    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;

    @Override
    protected int initRootView() {
        return R.layout.activity_compound_caption_editor;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mCaptionInput = (EditText) findViewById(R.id.captionInput);
        mCancel = (ImageView) findViewById(R.id.cancel);
        mConfirm = (ImageView) findViewById(R.id.confirm);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setBackImageVisible(View.VISIBLE);
        mTitleBar.setTextCenter(getString(R.string.caption_edit_title));
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mCaptionIndex = bundle.getInt(INTENT_KEY_CAPTION_INDEX);
                mCaptionText = bundle.getString(INTENT_KEY_CAPTION_TEXT);
            }
        }
        if (!TextUtils.isEmpty(mCaptionText)) {
            mCaptionInput.setHint(mCaptionText);
        }
        mCaptionInput.setText(mCaptionText);
        mCaptionInput.setSelection(mCaptionInput.getText().length());
        showInputMethod();
        mCaptionDataListClone = TimelineData.instance().cloneCompoundCaptionData();
    }

    @Override
    protected void initListener() {
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finishActivity();
                break;
            case R.id.confirm:
                String captionText = mCaptionInput.getText().toString();
                if (captionText.isEmpty()) {
                    captionText = mCaptionText;
                }
                CompoundCaptionInfo.CompoundCaptionAttr captionAttr = getCaptionAttr();
                if (captionAttr != null) {
                    captionAttr.setCaptionText(captionText);
                }
                TimelineData.instance().setCompoundCaptionArray(mCaptionDataListClone);
                Intent intent = new Intent();
                intent.putExtra(INTENT_KEY_CAPTION_INDEX, mCaptionIndex);
                setResult(RESULT_OK, intent);
                finishActivity();
                break;
        }
    }

    private CompoundCaptionInfo.CompoundCaptionAttr getCaptionAttr() {
        CompoundCaptionInfo captionInfo = getCurCaptionInfo();
        if (captionInfo == null) {
            return null;
        }
        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttributeList = captionInfo.getCaptionAttributeList();
        if (captionAttributeList == null) {
            return null;
        }
        int captionAttrCount = captionAttributeList.size();
        if (captionAttrCount == 0 || mCaptionIndex < 0 || mCaptionIndex >= captionAttrCount) {
            return null;
        }
        return captionAttributeList.get(mCaptionIndex);
    }

    private CompoundCaptionInfo getCurCaptionInfo() {
        int curCaptionZVal = TimelineData.instance().getCaptionZVal();
        int captionCount = mCaptionDataListClone.size();
        for (int idx = 0; idx < captionCount; idx++) {
            CompoundCaptionInfo captionInfo = mCaptionDataListClone.get(idx);
            if (captionInfo == null) {
                continue;
            }
            int captionZVal = captionInfo.getCaptionZVal();
            if (curCaptionZVal == captionZVal) {
                return captionInfo;
            }
        }
        return null;
    }

    private void showInputMethod() {
        //弹出键盘
        mCaptionInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCaptionInput.requestFocus();//获取焦点
                InputMethodManager inputManager = (InputMethodManager) mCaptionInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mCaptionInput, InputMethodManager.SHOW_FORCED);
            }
        }, DELAY_TIME_SHOW_INPUT);
    }

    private void hideInputMethod() {
        //隐藏键盘
        InputMethodManager inputManager = (InputMethodManager) mCaptionInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mCaptionInput.getWindowToken(), 0);
    }

    private void finishActivity() {
        hideInputMethod();
        AppManager.getInstance().finishActivity();
    }
}

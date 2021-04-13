package com.glitchcam.vepromei.mimodemo.common.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.widget.Toast;

import com.meicam.sdk.NvsVideoResolution;
import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.view.CommonDialog;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    private final static String TAG = "CommonUtil";

    public static void showDialog(Context context, final String title, final String first_tip, final CommonDialog.TipsButtonClickListener tipsButtonClickListener) {
        final CommonDialog dialog = new CommonDialog(context, 1);
        dialog.setOnCreateListener(new CommonDialog.OnCreateListener() {
            @Override
            public void OnCreated() {
                dialog.setTitleTxt(title);
                dialog.setFirstTipsTxt(first_tip);
            }
        });
        dialog.setOnBtnClickListener(new CommonDialog.OnBtnClickListener() {
            @Override
            public void OnOkBtnClicked(View view) {
                dialog.dismiss();
                if (tipsButtonClickListener != null)
                    tipsButtonClickListener.onTipsButtoClick(view);
            }

            @Override
            public void OnCancelBtnClicked(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public static NvsVideoResolution getVideoEditResolution(int ratio) {
        int compileRes = 720;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        if (ratio == Constants.AspectRatio.AspectRatio_16v9) {
            size.set(compileRes * 16 / 9, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_1v1) {
            size.set(compileRes, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_9v16) {
            size.set(compileRes, compileRes * 16 / 9);
        } else if (ratio == Constants.AspectRatio.AspectRatio_3v4) {
            size.set(compileRes, compileRes * 4 / 3);
        } else if (ratio == Constants.AspectRatio.AspectRatio_4v3) {
            size.set(compileRes * 4 / 3, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_9v18) {
            size.set(compileRes, compileRes * 18 / 9);
        } else if (ratio == Constants.AspectRatio.AspectRatio_18v9) {
            size.set(compileRes * 18 / 9, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_2d39v1) {
            size.set(((int) (compileRes * 2.39) + 1) / 2 * 2, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_2d55v1) {
            size.set(((int)(compileRes * 2.55) + 1) / 2 * 2, compileRes);
        } else {
            size.set(1280, 720);
        }
        videoEditRes.imageWidth = size.x;
        videoEditRes.imageHeight = size.y;
        Logger.e("getVideoEditResolution", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;
    }

    public static NvsVideoResolution getVideoEditResolutionEx(int ratio) {
        int compileRes = 1080;
        NvsVideoResolution videoEditRes = new NvsVideoResolution();
        Point size = new Point();
        if (ratio == Constants.AspectRatio.AspectRatio_16v9) {
            size.set(compileRes * 16 / 9, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_1v1) {
            size.set(compileRes, compileRes);
        } else if (ratio == Constants.AspectRatio.AspectRatio_9v16) {
            size.set(compileRes, compileRes * 16 / 9);
        } else if (ratio == Constants.AspectRatio.AspectRatio_3v4) {
            size.set(compileRes, compileRes * 4 / 3);
        } else if (ratio == Constants.AspectRatio.AspectRatio_4v3) {
            size.set(compileRes * 4 / 3, compileRes);
        } else {
            size.set(1280, 720);
        }
        videoEditRes.imageWidth = size.x;
        videoEditRes.imageHeight = size.y;
        Logger.e("getVideoEditResolution", videoEditRes.imageWidth + "     " + videoEditRes.imageHeight);
        return videoEditRes;
    }

    //获取所有权限列表(相机权限，麦克风权限，存储权限)
    public static List<String> getAllPermissionsList() {
        ArrayList<String> newList = new ArrayList<>();
        newList.add(Manifest.permission.CAMERA);
        newList.add(Manifest.permission.RECORD_AUDIO);
        newList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        newList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return newList;
    }

    public static void showToast(Context context, String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }
}

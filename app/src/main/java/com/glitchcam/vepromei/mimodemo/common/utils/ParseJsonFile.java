package com.glitchcam.vepromei.mimodemo.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by admin on 2018/11/28.
 */
public class ParseJsonFile {
    private static final String TAG = "ParseJsonFile";

    /**
     * Json转Java对象
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return new Gson().fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }
    public static String readAssetJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(jsonFilePath);
            if (inputStream == null)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static String readSdCardJsonFile(String jsonFilePath) {
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(new File(jsonFilePath));
            if (inputStream == null)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static void saveObjectByJson(File file, Object o) {
        String json = new Gson().toJson(o);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObjectByJson(File file, Class c) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuilder stringBuilder = new StringBuilder();
            int l = 0;
            byte[] bs = new byte[1024];
            while ((l = fileInputStream.read(bs)) != -1) {
                stringBuilder.append(new String(bs, 0, l));
            }
            fileInputStream.close();
            return new Gson().fromJson(stringBuilder.toString(), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

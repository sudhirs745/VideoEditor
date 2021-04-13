package com.glitchcam.vepromei.mimodemo.mediapaker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ${gexinyu} on 2018/5/29.
 */

public class FileManagerUtils {


    /**
     * 判断当前文件是否存在
     *
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 适配Android Q
     * @param context
     * @param uri
     * @return
     */
    public static boolean isContentUriExists(Context context, Uri uri){
        if (null == context) {
            return false;
        }
        ContentResolver cr = context.getContentResolver();
        try {
            AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
            if (null == afd) {
                return false;
            } else {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }
}

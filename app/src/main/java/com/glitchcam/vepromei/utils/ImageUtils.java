package com.glitchcam.vepromei.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static void setImageByPath(ImageView imageView, String path) {

        RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.mipmap.icon_feed_back_pic)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }


    public static void setImageByPathAndWidth(ImageView imageView, String path, int width) {
        RequestOptions options = new RequestOptions().centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }

    /**
     * 图片转化成base64字符串,将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param imgFile
     * @return
     */
    public static String getImageBase64Str(String imgFile) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        return zipBase64(Base64.encodeToString(data, Base64.NO_CLOSE));
    }


    /**
     * 字符串的压缩
     *
     * @param base64 待压缩的字符串
     * @return 返回压缩后的字符串
     * @throws IOException
     */
    public static String zipBase64(String base64) {
        if (null == base64 || base64.length() <= 0) {
            return base64;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            // 将 b.length 个字节写入此输出流
            gzip.write(base64.getBytes());
            gzip.close();
            // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
            return out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getColorImageByColor(Context context, String color) {
        String colorFolder = PathUtils.getColorPath(context);
        String colorFilePath = colorFolder + File.separator + color + ".png";
        File file = new File(colorFilePath);
        if (file.exists()){
            return file.getAbsolutePath();
        }
        return null;
    }

    public static String parseViewToBitmap(Context context, View view,  String color) {
        String colorFolder = PathUtils.getColorPath(context);
        String colorFilePath = colorFolder + File.separator + color + ".png";
        File file = new File(colorFilePath);
        if (file.exists()){
            return file.getAbsolutePath();
        }
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        view.draw(c);
        saveBitmap(bmp, colorFilePath);
        return colorFilePath;
    }

    public static void saveBitmap(Bitmap bitmap,String filePath) {
        FileOutputStream fos;
        try {
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap,String filePath, boolean needClear) {
        FileOutputStream fos;
        try {
            File file = new File(filePath);
            if (!file.exists() && !file.mkdirs()) {
                return;
            }
            if (needClear) {
                clearDir(file.getParentFile(), ".png");
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.e(TAG, "saveBitmap -> error: "+e.fillInStackTrace());
        }
    }

    private static void clearDir(File file,String suffix) {
        if (!file.isDirectory()) {
            return;
        }
        File list[] = file.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.getName().contains(suffix)) {
                    Logger.d(TAG, "clearDir: name = "+f.getName());
                    f.delete();
                }
            }
        }
    }
}

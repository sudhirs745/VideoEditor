package com.glitchcam.vepromei.mimodemo.mediapaker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoFrameRetriever;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.Constants;

import java.io.File;
import java.math.BigDecimal;

public abstract class BaseRecyclerHolder<T> extends RecyclerView.ViewHolder{
    private static final String TIME_SECOND = "S";

    public BaseRecyclerHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T data, boolean isSelected);

    protected Bitmap getBitMap(String videoFilePath, Context context) {
        NvsVideoFrameRetriever videoFrameRetriever = NvsStreamingContext.getInstance().createVideoFrameRetriever(videoFilePath);
        if (videoFrameRetriever == null) {
            return null;
        }
        int height = (int) context.getResources().getDimension(R.dimen.bottom_menu_item_image_height);
        height = (int) (height / 16) * 16;
        //需要传入可以被16整除的数字
        return videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight(0, height);
    }

    protected String getFormatDuration(long time) {
        float timeF = (float) time / Constants.NS_TIME_BASE;
        StringBuilder sb = new StringBuilder();
        BigDecimal b = new BigDecimal(timeF);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return sb.append(f1).append(TIME_SECOND).toString();
    }

    protected void setImageByFile(Context context, String iamgeFile, ImageView imageView, int width) {
        File file = new File(iamgeFile);
        RequestOptions options = new RequestOptions().centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        if (!TextUtils.isEmpty(iamgeFile)) {
            Glide.with(context)
                    .asBitmap()
                    .load(file)
                    .apply(options)
                    .into(imageView);
        }
    }

    public abstract boolean canClickable();

    public void onClick(View v) {

    }
}

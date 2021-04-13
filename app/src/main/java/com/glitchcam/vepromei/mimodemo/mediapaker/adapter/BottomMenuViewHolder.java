package com.glitchcam.vepromei.mimodemo.mediapaker.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.utils.GlidUtils;
import com.glitchcam.vepromei.utils.ScreenUtils;

public class BottomMenuViewHolder extends BaseRecyclerHolder<ShotVideoInfo> {
    private final ImageView mCoverImage;
    public final TextView mDurationText;
    private final ImageView mMaskImage;

    public BottomMenuViewHolder(@NonNull View itemView) {
        super(itemView);
        mDurationText = (TextView) itemView.findViewById(R.id.text_duration);
        mCoverImage = (ImageView) itemView.findViewById(R.id.image_cover);
        mMaskImage = (ImageView) itemView.findViewById(R.id.image_mask);
    }

    @Override
    public void bind(ShotVideoInfo shotVideoInfo, boolean isSelected) {
        if (shotVideoInfo == null){
            return;
        }
        long realNeedDuration = shotVideoInfo.getRealNeedDuration();
        mDurationText.setText(getFormatDuration(realNeedDuration));
        mMaskImage.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        mCoverImage.setImageDrawable(null);
        String videoFilePath = shotVideoInfo.getVideoClipPath();
        if (!TextUtils.isEmpty(videoFilePath)) {

            //这里需要改为加载trimin的点对应的第一帧图
            long trimIn = shotVideoInfo.getTrimIn();
            if(trimIn == 0 || isImage(videoFilePath)){
                GlidUtils.setImageByPathAndWidth(mCoverImage, videoFilePath, ScreenUtils.dip2px(mCoverImage.getContext(), 45));
            }else{

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(MSApplication.getmContext(), Uri.parse(videoFilePath));
                Bitmap bitmap = mmr.getFrameAtTime(trimIn, MediaMetadataRetriever.OPTION_CLOSEST);
                mCoverImage.setImageBitmap(bitmap);
            }


            mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
            if (shotVideoInfo.getFileDuration() >= realNeedDuration) {
                mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
            } else {
                mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.msffd0021b));
            }
        } else {
            mCoverImage.setImageDrawable(mCoverImage.getContext().getResources().getDrawable(R.drawable.image_cover_default_drawable));//占位图
            mDurationText.setTextColor(mDurationText.getResources().getColor(R.color.white));
        }
    }

    private boolean isImage(String videoFilePath) {
        if(!TextUtils.isEmpty(videoFilePath)){
            if(videoFilePath.endsWith("png")||videoFilePath.endsWith("jpg")){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canClickable() {
        return mCoverImage.getDrawable() != null;
    }
}

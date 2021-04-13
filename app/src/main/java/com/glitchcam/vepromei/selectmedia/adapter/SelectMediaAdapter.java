package com.glitchcam.vepromei.selectmedia.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.selectmedia.fragment.SelectMediaDataFragment.FragCallback;
import com.glitchcam.vepromei.selectmedia.utils.TimeUtil;
import com.glitchcam.vepromei.utils.MediaConstant;

import java.util.List;


public class SelectMediaAdapter extends RecyclerView.Adapter<SelectMediaAdapter.MediaItemViewHolder> {

    private List<MediaData> mediaDataList;
    private FragCallback onClickListener;
    private int rv_width = 78;

    public SelectMediaAdapter(List<MediaData> _allMediaData, int _rv_width, FragCallback callbackFromAdapter) {
        mediaDataList = _allMediaData;
        rv_width = _rv_width;
        onClickListener = callbackFromAdapter;
    }

    @Override
    public int getItemCount() {
        return mediaDataList.size();
    }

    @Override
    public MediaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_media_adapter, parent, false);
        view.setTag("1");

        return new MediaItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaItemViewHolder holder, final int position) {
        holder.render(mediaDataList.get(position), position, onClickListener);
    }

    public class MediaItemViewHolder extends RecyclerView.ViewHolder{

        TextView mediaType, mediaName;
        ImageView iv_item_image, iv_item_cover;
        ImageView iv_check, iv_check_back;

        public MediaItemViewHolder(View itemView) {
            super(itemView);

            mediaType = itemView.findViewById(R.id.tv_media_type);
            mediaName = itemView.findViewById(R.id.tv_media_name);
            iv_item_image = itemView.findViewById(R.id.iv_item_image);
            iv_item_cover = itemView.findViewById(R.id.iv_item_cover);
            iv_check = itemView.findViewById(R.id.iv_check);
            iv_check_back = itemView.findViewById(R.id.iv_check_back);
        }

        public void render(final MediaData mediaData, final int position, final FragCallback onClickListener) {
            if (mediaData.getType() == MediaConstant.VIDEO) {
                mediaType.setVisibility(View.VISIBLE);
                mediaType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                mediaType.getPaint().setAntiAlias(true);//抗锯齿
                mediaType.setText(TimeUtil.secToTime(Math.max((int) (mediaData.getDuration() / 1000), 1)));
            }
            else {
                mediaType.setVisibility(View.GONE);
            }

            if (mediaData.isState()){
                iv_item_cover.setVisibility(View.VISIBLE);
                iv_check.setVisibility(View.VISIBLE);
                iv_check_back.setVisibility(View.VISIBLE);
            }else {
                iv_item_cover.setVisibility(View.GONE);
                iv_check.setVisibility(View.GONE);
                iv_check_back.setVisibility(View.GONE);
            }

            mediaName.setText(mediaData.getDisplayName());
            setImageByFile(mediaData.getPath());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                mediaData.setState(!mediaData.isState());
                onClickListener.onClickedMediaItem(mediaData, position);
                }
            });
        }

        private void setImageByFile(String iamgeFile) {
            RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.drawable.bank_thumbnail_local)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(rv_width);
            Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(iamgeFile)
                .apply(options)
                .into(iv_item_image);
        }
    }
}

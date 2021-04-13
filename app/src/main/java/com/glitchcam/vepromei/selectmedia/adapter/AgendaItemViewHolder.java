/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.glitchcam.vepromei.selectmedia.adapter;

import android.graphics.Paint;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;
import com.glitchcam.vepromei.selectmedia.interfaces.OnItemClick;
import com.glitchcam.vepromei.selectmedia.utils.TimeUtil;
import com.glitchcam.vepromei.utils.MediaConstant;
import com.glitchcam.vepromei.utils.ScreenUtils;

import static com.glitchcam.vepromei.selectmedia.fragment.MediaFragment.GRIDITEMCOUNT;

public class AgendaItemViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    ImageView iv_item_image;
    ImageView iv_check, iv_check_back;

    public AgendaItemViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv_media_type);
        iv_item_image = itemView.findViewById(R.id.iv_item_image);
        iv_check = itemView.findViewById(R.id.iv_check);
        iv_check_back = itemView.findViewById(R.id.iv_check_back);
    }


    public void render(MediaData mediaData, final int se, final int position, final OnItemClick onItemClick,boolean isGone) {
        /*
        * 设置当前的图片为正方形
        * Set the current picture to square
        * */
        int marginSizeLeftAndRight = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_recycle_marginLeftAndRight);
        int width = ScreenUtils.getWindowWidth(MSApplication.getmContext()) - marginSizeLeftAndRight * 2;
        int marginSizeStart = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_item_start_end);
        int marginSizeMiddle = (int) MSApplication.getmContext().getResources().getDimension(R.dimen.select_item_between);
        int itemWidth = (width - marginSizeStart * 2 - marginSizeMiddle * (GRIDITEMCOUNT - 1)) / GRIDITEMCOUNT;

        RecyclerView.LayoutParams param = new RecyclerView.LayoutParams(itemWidth, itemWidth);
        int columnMarginStartAndEnd = 0;
        int columnMarginMiddle = 0;
        int marginSizeTopAndEnd = ScreenUtils.dip2px(MSApplication.getmContext(), 4) / 2;
        if (position < GRIDITEMCOUNT) {
            marginSizeTopAndEnd = 0;
        }
        param.setMargins(columnMarginStartAndEnd, marginSizeTopAndEnd, columnMarginMiddle, marginSizeTopAndEnd);
        itemView.setLayoutParams(param);
        if (mediaData.getType() == MediaConstant.VIDEO) {
            textView.setVisibility(View.VISIBLE);
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            textView.getPaint().setAntiAlias(true);//抗锯齿
            textView.setText(TimeUtil.secToTime(Math.max((int) (mediaData.getDuration() / 1000), 1)));
        } else {
            textView.setVisibility(View.GONE);
        }

        if (isGone){
            iv_check.setImageResource(mediaData.isState() ? R.drawable.ic_unchecked : R.drawable.ic_unchecked);
            iv_check_back.setVisibility(View.GONE);
        }else {
            iv_check_back.setVisibility(mediaData.isState() ? View.VISIBLE : View.GONE);
            iv_check.setImageResource(mediaData.isState() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
        }

        setImageByFile(mediaData.getPath(), itemWidth);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.OnItemClick(itemView, se, position);
            }
        });
    }

    private void setImageByFile(String iamgeFile, int width) {
        RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.drawable.bank_thumbnail_local)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(iamgeFile)
                .apply(options)
                .into(iv_item_image);
    }
}

package com.glitchcam.vepromei.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.glitchcam.vepromei.MSApplication;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.main.bean.AdBeansFormUrl;
import com.glitchcam.vepromei.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class SpannerViewpagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<AdBeansFormUrl.AdInfo> mAdList = new ArrayList<>( );

    private SpannerClickCallback mSpannerClickCallback;

    public SpannerViewpagerAdapter(Context context, List<AdBeansFormUrl.AdInfo> adList) {
        this.mContext = context;
        this.mAdList = adList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setAdapterData(List<AdBeansFormUrl.AdInfo> data) {
        mAdList = data;
        notifyDataSetChanged( );
    }

    @Override
    public int getCount() {
        if ((mAdList == null) || mAdList.isEmpty( )) {
            return 0;
        }
        if (mAdList.size( ) == 1) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int newPosition = position % (mAdList.size( ));
        View inflate = mLayoutInflater.inflate(R.layout.banner_main_viewpager_layout, null);
        ImageView imageView = inflate.findViewById(R.id.spanner_image_display_view);
        final LinearLayout background = inflate.findViewById(R.id.spanner_image_display_background);
        if (mAdList.get(newPosition) != null) {
            RequestOptions optionsBk = new RequestOptions( );
            optionsBk.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            optionsBk.centerCrop( );
            Glide.with(mContext).asBitmap( ).load(mAdList.get(newPosition).getCoverUrl( )).apply(optionsBk)
                    .into(new SimpleTarget<Bitmap>( ) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                background.setBackground(new BitmapDrawable(resource));
                            }
                        }
                    });
            // 前景
            RequestOptions optionsFr = new RequestOptions( );
            optionsFr.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            optionsFr.centerInside( );
            Glide.with(mContext)
                    .asBitmap( )
                    .load(SystemUtils.isZh(MSApplication.getmContext( )) ? mAdList.get(newPosition).getCoverUrl2( ) : mAdList.get(newPosition).getCoverUrl3( ))
                    .apply(optionsFr)
                    .into(imageView);
            container.addView(inflate);
        }
        inflate.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mSpannerClickCallback != null) {
                    if (newPosition < mAdList.size( )) {
                        mSpannerClickCallback.spannerClick(newPosition, mAdList.get(newPosition));
                    }
                }
            }
        });
        return inflate;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public interface SpannerClickCallback {
        void spannerClick(int position, AdBeansFormUrl.AdInfo adInfo);
    }

    public void setSpannerClickCallback(SpannerClickCallback spannerClickCallback) {
        this.mSpannerClickCallback = spannerClickCallback;
    }
}

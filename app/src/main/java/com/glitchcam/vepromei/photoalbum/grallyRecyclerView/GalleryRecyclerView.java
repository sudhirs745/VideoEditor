package com.glitchcam.vepromei.photoalbum.grallyRecyclerView;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 控制fling速度的RecyclerView
 * RecyclerView to control the speed of fling
 */
public class GalleryRecyclerView extends RecyclerView {
    /*
    * 减速因子
    * Deceleration factor
    * */
    private static final float FLING_SCALE_DOWN_FACTOR = 0.5f;
    /*
    * 最大顺时滑动速度
    * Maximum clockwise sliding speed
    * */
    private static final int FLING_MAX_VELOCITY = 8000; //

    public GalleryRecyclerView(Context context) {
        super(context);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX = solveVelocity(velocityX);
        velocityY = solveVelocity(velocityY);
        return super.fling(velocityX, velocityY);
    }

    private int solveVelocity(int velocity) {
        if (velocity > 0) {
            return Math.min(velocity, FLING_MAX_VELOCITY);
        } else {
            return Math.max(velocity, -FLING_MAX_VELOCITY);
        }
    }
}

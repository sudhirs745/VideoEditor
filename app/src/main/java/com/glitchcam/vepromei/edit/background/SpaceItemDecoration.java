package com.glitchcam.vepromei.edit.background;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * @author lpf
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;
    private int rightSpace;

    public SpaceItemDecoration(int left, int right) {
        this.leftSpace = left;
        this.rightSpace = right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = leftSpace;
        outRect.right = rightSpace;
    }

}
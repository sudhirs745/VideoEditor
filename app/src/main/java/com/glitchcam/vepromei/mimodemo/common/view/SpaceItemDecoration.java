package com.glitchcam.vepromei.mimodemo.common.view;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
    private int leftSpace;
    private int rightSpace;

    public SpaceItemDecoration(int left,int right) {
        this.leftSpace = left;
        this.rightSpace = right;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
        if(pos != 0) {
            outRect.left = leftSpace;
        } else {
            outRect.left = 0;
        }
        outRect.right = rightSpace;
    }
}

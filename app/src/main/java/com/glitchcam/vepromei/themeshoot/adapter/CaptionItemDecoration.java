package com.glitchcam.vepromei.themeshoot.adapter;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * 字幕编辑
 */
public class CaptionItemDecoration extends RecyclerView.ItemDecoration {

    private int paddingDecoration;

    public CaptionItemDecoration(int paddingDecoration) {
        this.paddingDecoration = paddingDecoration;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        outRect.top = paddingDecoration;
        outRect.bottom = paddingDecoration;

    }
}

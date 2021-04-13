package com.glitchcam.vepromei.photoalbum.grallyRecyclerView;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.photoalbum.PhotoAlbumData;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<PhotoAlbumData> mList = new ArrayList<>();
    private Context mContext;
    RequestOptions mOptions = new RequestOptions();
    private int mSelectPos;
    private OnGrallyItemSelectListener mSelectListener;
    private RecyclerView mRecyclerView;

    private AdapterMeasureHelper mCardAdapterHelper = new AdapterMeasureHelper();

    public GalleryAdapter(Context ctx, RecyclerView view) {
        this.mContext = ctx;
        this.mRecyclerView = view;

        mOptions.fitCenter();
    }

    public void setData(ArrayList<PhotoAlbumData> mList) {
        this.mList.clear();
        this.mList.addAll(mList);
        notifyDataSetChanged();
    }

    public void updatePlayItem(PhotoAlbumData itemData) {
        if (itemData == null) {
            return;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (mSelectPos - firstItemPosition >= 0) {

            View view = mRecyclerView.getChildAt(mSelectPos - firstItemPosition);
            if (view == null) {
                return;
            }
            final ViewHolder viewHolder = (ViewHolder)mRecyclerView.getChildViewHolder(view);
            if (viewHolder == null) {
                return;
            }
            if (viewHolder.coverImageView.getVisibility() == View.VISIBLE) {
                viewHolder.coverImageView.setVisibility(View.INVISIBLE);

                ViewGroup.LayoutParams lp1 = viewHolder.coverVideoView.getLayoutParams();
                lp1.width = mCardAdapterHelper.galleryItemW;
                lp1.height = mCardAdapterHelper.galleryItemH;
                viewHolder.coverVideoView.setLayoutParams(lp1);
            }
        }
    }

    public void updateItemData(PhotoAlbumData itemData) {
        if (itemData == null) {
            return;
        }
        PhotoAlbumData oldItem = findItemByID(itemData.id);
        if (oldItem != null) {
            oldItem.isLocal = itemData.isLocal;
            oldItem.coverImageUrl = itemData.coverImageUrl;
            oldItem.coverVideoUrl = itemData.coverVideoUrl;
            oldItem.filePath = itemData.filePath;
            oldItem.licPath = itemData.licPath;
            oldItem.sourceDir = itemData.sourceDir;
        }
    }

    private PhotoAlbumData findItemByID(int id) {
        for (PhotoAlbumData oldItem: mList) {
            if (oldItem == null) {
                continue;
            }
            if (oldItem.id == id) {
                return oldItem;
            }
        }
        return null;
    }


    public void setSelectPos(int pos) {
        if (mSelectPos == pos) {
            return;
        }
        mSelectPos = pos;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mList.size();
    }

    public interface OnGrallyItemSelectListener {
        void onItemSelect(int pos, PhotoAlbumData itemData, PlayerView view);
    }
    public void setOnGrallyItemSelectListener(OnGrallyItemSelectListener listener) {
        mSelectListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_ablum_item, parent, false);

        PlayerView playView = itemView.findViewById(R.id.playerView);
        playView.setUseController(false);
        playView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        return new ViewHolder(itemView);
    }

    private PlayerView getPlayerView() {
        PlayerView playView = new PlayerView(mContext);
        playView.setUseController(false);
        playView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playView.setPlayer(null);
        return playView;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());

        Log.e("===>", "onBindViewHolder: " + position);
        PhotoAlbumData ablumData = mList.get(position);
        if (ablumData == null) {
            return;
        }

        if (holder.coverImageView.getVisibility() != View.VISIBLE) {
            holder.coverImageView.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext)
                .load(ablumData.coverImageUrl)
                .apply(mOptions)
                .into(holder.coverImageView);

        if (position != mSelectPos) {
            holder.coverVideoView.setPlayer(null);

        } else {
            if (mSelectListener != null) {
                mSelectListener.onItemSelect(position, ablumData, holder.coverVideoView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverImageView;
        private PlayerView coverVideoView;

        public ViewHolder(final View view) {
            super(view);
            coverImageView = (ImageView) view.findViewById(R.id.coverView);
            coverVideoView = (PlayerView) view.findViewById(R.id.playerView);
        }
    }
}

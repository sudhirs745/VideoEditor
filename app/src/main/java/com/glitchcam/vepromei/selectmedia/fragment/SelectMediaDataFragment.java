package com.glitchcam.vepromei.selectmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.selectmedia.adapter.SelectMediaAdapter;
import com.glitchcam.vepromei.selectmedia.bean.MediaData;

import java.util.ArrayList;
import java.util.List;


public class SelectMediaDataFragment extends Fragment {

    private int mediaType;

    //提示加载媒体资源
    private SelectMediaAdapter recyclerAdapter;
    private List<MediaData> mediaDataList;
    private int rv_width;

    StaggeredGridLayoutManager mGridLayoutManager;

    OnClickMediaDataListener onClickMediaListener;
    FragCallback callbackFromAdapter = new FragCallback() {
        @Override
        public void onClickedMediaItem(MediaData mediaData, int position) {
            if((int)mediaDataList.get(position).getDuration() < 0)
                mediaDataList.get(position).setDuration(3);

            recyclerAdapter.notifyDataSetChanged();
            onClickMediaListener.onCLickMediaData(mediaData, position);
        }
    };

    public SelectMediaDataFragment(List<MediaData> _mediadata, int _mediaType, int _rv_width, OnClickMediaDataListener onClickListener) {
        mediaDataList = _mediadata;
        mediaType = _mediaType;
        rv_width = _rv_width;
        onClickMediaListener = onClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        initUIView(view);

        return view;
    }

    private void initUIView(View view) {
        RecyclerView mediaRecycler = view.findViewById(R.id.media_recycleView);

        recyclerAdapter = new SelectMediaAdapter(mediaDataList, rv_width, callbackFromAdapter);
        mGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        mediaRecycler.setLayoutManager(mGridLayoutManager);
        mediaRecycler.setAdapter(recyclerAdapter);
    }

    public void refreshMediaDataList(){
        if(recyclerAdapter != null){
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    public void setMediaList(List<MediaData> _mediaList){
        mediaDataList = _mediaList;
    }

    public List<MediaData> getSelectedList(){
        List<MediaData> selectedList = new ArrayList<>();
        for (MediaData media: mediaDataList) {
            if(media.isState()){
                selectedList.add(media);
            }
        }
        return selectedList;
    }

    public void unSetState(MediaData media){
        int index = mediaDataList.indexOf(media);
        mediaDataList.get(index).setState(false);
        recyclerAdapter.notifyDataSetChanged();
    }

    public void setDuration(MediaData media){
        int index = mediaDataList.indexOf(media);
        mediaDataList.get(index).setDuration((int) media.getDuration());
        recyclerAdapter.notifyDataSetChanged();
    }

    public interface FragCallback {
        void onClickedMediaItem(MediaData mediaData, int position);
    }

    public interface OnClickMediaDataListener {
        void onCLickMediaData(MediaData mediaData, int position);
    }
}

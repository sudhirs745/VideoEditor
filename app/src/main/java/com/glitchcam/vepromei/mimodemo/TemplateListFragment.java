package com.glitchcam.vepromei.mimodemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.adapter.TemplateListAdapter;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.utils.ScreenUtils;
import com.glitchcam.vepromei.mimodemo.common.view.SpaceItemDecoration;
import com.glitchcam.vepromei.mimodemo.interf.OnTemplateSelectListener;

import java.util.List;

public class TemplateListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<MiMoLocalData> mTemplateList;
    private TemplateListAdapter mAdapter;

    private OnTemplateSelectListener mOnTemplateSelectListener;

    public void setOnTemplateSelectListener(OnTemplateSelectListener onTemplateSelectListener) {
        this.mOnTemplateSelectListener = onTemplateSelectListener;
    }

    public TemplateListFragment() {
    }

    @SuppressLint("ValidFragment")
    public TemplateListFragment(List<MiMoLocalData> value) {
        mTemplateList = value;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_template_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.filter_list);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(this.getContext(), 4),
                ScreenUtils.dip2px(this.getContext(), 4)));
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TemplateListAdapter(getContext(), mTemplateList);
        mAdapter.setOnTemplateSelectListener(mOnTemplateSelectListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setSelectPosition(int position) {
        mAdapter.setSelectPosition(position);
    }

    public void setNewDatas(List<MiMoLocalData> mDataListLocals) {
        if (mDataListLocals == null || mAdapter == null) {
            return;
        }
        mAdapter.setNewDatas(mDataListLocals);
    }

    public MiMoLocalData getCurrentData() {
        return mAdapter.getCurrentData();
    }
}

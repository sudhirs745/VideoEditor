package com.glitchcam.vepromei.mimodemo.mediapaker.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.base.BaseCustomView;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.utils.ScreenUtils;
import com.glitchcam.vepromei.mimodemo.common.view.SpaceItemDecoration;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.IAdapterLifeCircle;
import com.glitchcam.vepromei.mimodemo.mediapaker.adapter.SelectMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectBottomMenu extends BaseCustomView {
    private static final int ITEM_WIDTH_DP = 50;
    private static final int ITEM_DECORATION_LEFT = 1;
    private static final int ITEM_DECORATION_RIGHT = 1;
    private RecyclerView mSelectMenu;
    private SelectMenuAdapter mMenuAdapter;
    private View mNextButton;
    private boolean mIsEditable = true;
    private OnNextClickListener mOnNextClickListener;
    private OnItemClickListener mOnItemClickListener;
    private LinearLayoutManager mLayoutManager;
    private int mCurrentPosition;
    private List<Long> mIntervalPoints = null;

    public void setOnNextClickListener(OnNextClickListener onNextClickListener) {
        this.mOnNextClickListener = onNextClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public SelectBottomMenu(Context context) {
        super(context);
    }

    public SelectBottomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsEditable = true;
    }

    public void setIsEditable(boolean isEditable) {
        this.mIsEditable = isEditable;
        mMenuAdapter.setIsEditable(mIsEditable);
        if (!mIsEditable) {
            mNextButton.setVisibility(GONE);
        }
    }

    @Override
    protected void initData(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SelectBottomMenu);
        int itemPaddingLeft = (int) typedArray.getDimension(R.styleable.SelectBottomMenu_item_padding_left, 0);
        int itemPaddingRight = (int) typedArray.getDimension(R.styleable.SelectBottomMenu_item_padding_right, 0);
        if (itemPaddingLeft == 0) {
            itemPaddingLeft = ScreenUtils.dip2px(mContext,ITEM_DECORATION_LEFT);
        }
        if (itemPaddingRight == 0) {
            itemPaddingRight = ScreenUtils.dip2px(mContext,ITEM_DECORATION_RIGHT);
        }
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mSelectMenu.setLayoutManager(mLayoutManager);
        mSelectMenu.addItemDecoration(new SpaceItemDecoration(itemPaddingLeft,
                itemPaddingRight));
        mMenuAdapter = new SelectMenuAdapter();
        mMenuAdapter.setOnItemClickListener(new SelectMenuAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int shotIndex) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.OnItemClicked(shotIndex);
                }
            }
        });
        if (mIsEditable) {
            mNextButton.setVisibility(VISIBLE);
        }
        updateShotClip();
    }

    @Override
    protected void initView() {
        mSelectMenu = (RecyclerView) mRootView.findViewById(R.id.select_menu);
        mNextButton = mRootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMenuAdapter.isFull()) {
                    Toast.makeText(mContext, "缺少素材，请添加素材到空缺位置", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mOnNextClickListener != null) {
                    mOnNextClickListener.onNextClicked();
                }
            }
        });
    }

    @Override
    protected int initRootView() {
        return R.layout.selelct_bottom_menu_layout;
    }

    public void updateShotClip(){
        MiMoLocalData templateInfo = NvTemplateContext.getInstance().getSelectedMimoData();
        if (templateInfo == null){
            return;
        }
        List<ShotVideoInfo> shotVideoInfos = templateInfo.getTotalShotVideoInfos();
        mMenuAdapter.setData(shotVideoInfos);
    }
    public void addClipPath(String path, long duration) {
        if (mMenuAdapter == null) {
            return;
        }
        mMenuAdapter.addClipPath(path, duration);
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition < mMenuAdapter.mCurrentSelectPosition + 1 ) {
            mSelectMenu.smoothScrollBy(ScreenUtils.dip2px(mContext, ITEM_WIDTH_DP * 2), 0);//移动两个item的宽度距离
        }
    }

    public void removeClipPath() {
        if (mMenuAdapter == null) {
            return;
        }
        mMenuAdapter.removeClipPath();

    }

    public void goNextPosition() {
        if (mMenuAdapter == null) {
            return;
        }
        mMenuAdapter.goNextPosition();
    }

    public void updatePosition(long curTime) {
        if (mMenuAdapter == null) {
            return;
        }
        int positionInCurTime = findPositionInCurTime(curTime);
        if (mCurrentPosition != positionInCurTime) {
            mSelectMenu.smoothScrollToPosition(positionInCurTime);
            mCurrentPosition = positionInCurTime;
            mMenuAdapter.updatePosition(positionInCurTime);
        }
    }

    private int findPositionInCurTime(long curTime) {
        if (mIntervalPoints == null) {
            mIntervalPoints = getIntervalPoints();//首次获取进度间隔点
        }
        if(mIntervalPoints.size()>mMenuAdapter.mCurrentSelectPosition + 1){
            if (curTime >= mIntervalPoints.get(mMenuAdapter.mCurrentSelectPosition + 1)) {//从左向右对比，用于正常播放或用户正向拖动进度条
                return mMenuAdapter.mCurrentSelectPosition + 1;
            }
        }
        if (curTime == 0 ) {
            return 0;
        }
        if(mIntervalPoints.size()>mMenuAdapter.mCurrentSelectPosition + 1){

            if (curTime <= mIntervalPoints.get(mMenuAdapter.mCurrentSelectPosition)) {//从右向左对比，用于用户反向拖动进度条
                return  mMenuAdapter.mCurrentSelectPosition - 1;
            }
        }
        return mMenuAdapter.mCurrentSelectPosition;
    }

    /**
     * 获取可编辑视频片段的时间点，不包括默认视频片段
     * @return
     */
    private List<Long> getIntervalPoints() {
        try {
            List<Long> points = new ArrayList<>();
            MiMoLocalData templateInfo = NvTemplateContext.getInstance().getSelectedMimoData();
            long totalTime = 0L;
            points.add(totalTime);
            List<ShotInfo> shotInfos = templateInfo.getShotInfos();
            for (int index = 0; index < shotInfos.size(); index++) {
                ShotInfo shotInfo = shotInfos.get(index);
                totalTime += shotInfo.getDuration();
                if (!shotInfo.isCanPlaced()) {
                    continue;
                }
                points.add(totalTime);
            }
            return points;
        } catch (Exception e) {}
        return null;
    }

    public void setPosition(int mIndex) {
        if (mMenuAdapter == null) {
            return;
        }
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition < mMenuAdapter.mCurrentSelectPosition + 1 ) {
            mSelectMenu.smoothScrollBy(ScreenUtils.dip2px(mContext, ITEM_WIDTH_DP * 2), 0);//移动两个item的宽度距离
        }
        mMenuAdapter.setPosition(mIndex);
    }

    public void addAdapterLifeCircle(IAdapterLifeCircle adapterLifeCircle) {
        if(adapterLifeCircle != null) {
            mMenuAdapter.addLifeCircle(adapterLifeCircle);
        }
        mSelectMenu.setAdapter(mMenuAdapter);
    }

    public void upDataForPosition(int mCurrentPosition){
        //updateShotClip();
        MiMoLocalData templateInfo = NvTemplateContext.getInstance().getSelectedMimoData();
        if (templateInfo == null){
            return;
        }
        List<ShotVideoInfo> shotVideoInfos = templateInfo.getTotalShotVideoInfos();

        mMenuAdapter.upDataResource(shotVideoInfos.get(mCurrentPosition),mCurrentPosition);
    }
    public interface OnNextClickListener{
        void onNextClicked();
    }

    public interface OnItemClickListener {
        void OnItemClicked(int index);
    }
}

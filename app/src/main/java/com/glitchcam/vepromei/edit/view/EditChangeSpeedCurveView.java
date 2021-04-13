package com.glitchcam.vepromei.edit.view;

import android.content.Context;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.edit.data.ChangeSpeedCurveInfo;

import java.util.List;

public class EditChangeSpeedCurveView extends RelativeLayout {
    /**
     * 页面使用到的view
     * used view in page
     */
    private LinearLayout linearPoint;
    private TextView tvPoint, tvReset,  tvPointCover;
    private ImageView imagePoint, imageConfirm;
    private NvBezierSpeedView bezerView;
    private int currSelected = -1;

    private ChangeSpeedCurveInfo info;
    private boolean mIsPalying = true;
    private OnFunctionListener onFunctionListener;

    public EditChangeSpeedCurveView(Context context){
        this(context,null);

    }
    public EditChangeSpeedCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initListener();
    }

    protected void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_edit_chang_speed_curve, this);
        linearPoint = rootView.findViewById(R.id.linear_point);
        tvPoint = rootView.findViewById(R.id.tv_point);
        imagePoint = rootView.findViewById(R.id.image_point);
        bezerView = rootView.findViewById(R.id.bizer_view);
        tvPointCover = rootView.findViewById(R.id.tv_point_cover);
    }



    protected void initListener() {
        linearPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bezerView.addOrDeletePoint(currSelected);
                if(null != onFunctionListener){
                    onFunctionListener.onChangePoint(currSelected == -1);
                }
            }
        });



        bezerView.setOnBezierListener(new NvBezierSpeedView.OnBezierListener() {
            @Override
            public void onSpeedChanged(String speed,long timePoint) {
               if(null != onFunctionListener){
                   onFunctionListener.onSpeedChanged(speed,timePoint);
               }
            }

            @Override
            public void onSelectedPoint(int position) {
                currSelected = position;
                List list = bezerView.getList();
                if (position != -1) {
                    if (position == 0 || position == list.size() - 1) {
                        tvPointCover.setVisibility(VISIBLE);
                        linearPoint.setClickable(false);
                    } else {
                        tvPointCover.setVisibility(GONE);
                        linearPoint.setClickable(true);
                    }
                    tvPoint.setText(getResources().getString(R.string.tv_point_remove));
                    imagePoint.setImageResource(R.mipmap.icon_remove_point);
                } else {
                    linearPoint.setClickable(true);
                    tvPointCover.setVisibility(GONE);
                    tvPoint.setText(getResources().getString(R.string.tv_point_add));
                    imagePoint.setImageResource(R.mipmap.icon_add_point);
                }
            }

            @Override
            public void seekPosition(long position) {
                if(null != onFunctionListener){
                    onFunctionListener.onTimelineMove(position);
                }
            }

            @Override
            public void onActionDown() {
                if(null != onFunctionListener){
                    onFunctionListener.onActionDown();
                }
            }

            @Override
            public void onActionUp(long timePoint) {
                if(null != onFunctionListener){
                    onFunctionListener.onActionUp(timePoint);
                }
            }

            @Override
            public void onSelectPoint() {
                if(null != onFunctionListener){
                    onFunctionListener.onSelectPoint();
                }
            }
        });

    }

    /**
     * 播放更新基准线位置
     * @param progress
     */
    public void upDataPlayProgress(long progress){
        if(null != bezerView){

            bezerView.setUpdeteBaseLine(progress);
        }
    }

    public ChangeSpeedCurveInfo getInfo() {
        return info;
    }

    /**
     * 设置变速信息和片段时长
     * @param info
     */
    public void setInfo(ChangeSpeedCurveInfo info) {
        this.info = info;

        bezerView.setSpeedPoint(info.speed);
        bezerView.setSpeedOriginal(info.speedOriginal);
    }

    /**
     * 设置片段时长
     * @param duration
     */
    public void setClipDuration(long duration){
        bezerView.setDuring(duration);
    }

    public void setOnFunctionListener(OnFunctionListener onFunctionListener){
        this.onFunctionListener = onFunctionListener;
    }


    public interface OnFunctionListener{
        void onChangePoint(boolean addPoint);
        void onTimelineMove(long timePoint);
        void onSpeedChanged(String speed,long timePoint);
        void onActionDown();
        void onSelectPoint();
        void onActionUp(long timePoint);
    }
}

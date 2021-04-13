package com.glitchcam.vepromei.edit.view;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.glitchcam.vepromei.R;
import com.warkiz.widget.SizeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author :Jml
 * @date :2020/7/27 14:42
 * @des : 进度拖拽，计算当数据，取消吸附效果
 */
public class EditChangeSpeedScrollView extends RelativeLayout {
    private static final String TAG="EditChangeSpeedScrollView";
    private OnSpeedChangedListener onSpeedChangedListener;
    //
    private LinearLayout rl_root;
    private LinearLayout ll_data;
    private View view_shadow;
    private LinearLayout view_mask;
    private View view_background;
    //onTouch 事件使用变量
    private float downX,downY;
    private float currentX;
    //判断为去响应点击事件
    boolean doClick  =false;
    //shadow是否跟随手指移动
    boolean doScrollByOnTouch = false;

    private List<SpeedParam> itemDataList = new ArrayList<>();
    //节点对应x坐标集合
    private List<Integer>ItemPointX = new ArrayList<>();
    //每个节点之间的距离
    private int itemWidth;
    private int marginLeft;
    private int marginRight;
    private SpeedParam mSelected = null;
    //当前的倍速
    private float currentSpeed = 1.0f;

    private TextView tv_speed;

    public EditChangeSpeedScrollView(Context context){
        this(context,null);

    }
    public EditChangeSpeedScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setListener();
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.nv_layout_custom_compile_params,this);
        rl_root = rootView.findViewById(R.id.rl_root);
        ll_data = rootView.findViewById(R.id.data);
        view_shadow = rootView.findViewById(R.id.view_shadow);
        view_mask = rootView.findViewById(R.id.view_mask);
        view_background = rootView.findViewById(R.id.view_background);
        marginLeft = SizeUtils.dp2px(context,10);
        marginRight = SizeUtils.dp2px(context,10);
        tv_speed = rootView.findViewById(R.id.tv_speed);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        view_shadow.setX(0);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListener(){
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            downX = event.getRawX();
            downY = event.getRawY();
            currentX = downX;
            float shadowX = view_shadow.getX() + marginLeft;
            int width = view_shadow.getWidth();
            //起始点落在shadowView上，随着移动去执行滑动
            if (downX > shadowX - 50 && downX < shadowX + width + 50) {
                doScrollByOnTouch = true;
                doClick = false;
            }/*else{
                            //落在其他选项上，随手指抬起去响应点击事件
                            doClick = true;
                            doScrollByOnTouch = false;
                        }*/
            Log.e(TAG, "onTouch ACTION_DOWN");
        } else if (action == MotionEvent.ACTION_MOVE) {//如果手指落下时点击的是滑块范围，需要随手指移动
            if(tv_speed.getVisibility() == View.INVISIBLE){

                tv_speed.setVisibility(VISIBLE);
            }
            if (doScrollByOnTouch) {
                float currentMoveX = event.getRawX();
                //滑动shadowView
                float shadowCurrentX = view_shadow.getX();
                float speed = itemDataList.get(0).value;
                //计算需要滑动的距离
                float targetPositionX = shadowCurrentX + currentMoveX - currentX;
                //最左变得距离是经典按钮左侧位置
                if (targetPositionX <= view_background.getLeft()) {
                    targetPositionX = view_background.getLeft();
                    //计算当前变速滑动的位置对应的值
                    speed = itemDataList.get(0).value;
                    Log.e(TAG, "getCurrentSpeedByPosition == " + speed);

                }
                //最右侧的位置
                else if (targetPositionX > view_background.getRight()) {
                    targetPositionX = view_background.getRight() - view_shadow.getWidth() / 2;
                    //计算当前变速滑动的位置对应的值
                    speed = itemDataList.get(itemDataList.size() - 1).value;
                    Log.e(TAG, "getCurrentSpeedByPosition == " + speed);

                } else {
                    //计算当前变速滑动的位置对应的值
                    speed = getCurrentSpeedByPosition(targetPositionX + view_shadow.getWidth() / 2);
                    Log.e(TAG, "getCurrentSpeedByPosition == " + speed);
                }
                BigDecimal b = new BigDecimal(speed);
                float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                if (null != onSpeedChangedListener) {

                    onSpeedChangedListener.onSpeedChanged(f1);
                }

                tv_speed.setText(f1 + "X");
                doMoveToTargetPosition((int) targetPositionX);

                currentX = currentMoveX;
            }
            Log.e(TAG, "onTouch ACTION_MOVE");
        } else if (action == MotionEvent.ACTION_UP) {//滑动事件，去操作，手指滑动出当前控件已然响应
            if (doScrollByOnTouch) {
                //手指抬起时 shadowView 的 x坐标
                float currentUpX = view_shadow.getX();
                //这里要添加一个吸附效果  暂时设置为距离左右节点的距离小于10吸附过去
                buildAdsorbentPointDis((int) currentUpX+view_shadow.getWidth()/2);

            }
            Log.e(TAG, "onTouch ACTION_UP");
            doScrollByOnTouch = false;
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_speed.setVisibility(INVISIBLE);
                }
            },2000);

        } else if (action == MotionEvent.ACTION_CANCEL) {
            Log.e(TAG, "onTouch ACTION_CANCEL");
        }
        return true;
    }

    /**
     * 计算当前的变速值
     * @param targetPositionX
     * @return
     */
    private float getCurrentSpeedByPosition(float targetPositionX) {
        //当前位置左侧的节点是第几个节点
        int leftIndex = (int) (targetPositionX/itemWidth);
        //如果当前节点是最后一个节点
        if(leftIndex == itemDataList.size()-1){
            return itemDataList.get(leftIndex).value;
        }
        //获取这个节点对应的速度值
        float baseSpeedValue = itemDataList.get(leftIndex).value;
        //当前所在位置相对左节点的距离
        float leftIndexOffsetValue = targetPositionX-itemWidth*leftIndex;
        //获取当前位置相对左节点的数据
        float rightIndexValue = itemDataList.get(leftIndex+1).value;
        float valueToLeftIndex = (rightIndexValue-baseSpeedValue)*leftIndexOffsetValue/itemWidth;
        return valueToLeftIndex+baseSpeedValue;
    }


    /**
     * shadow移动到目标位置
     * @param targetPosition  移动的目标位置x坐标
     */
    private void doMoveToTargetPosition( int targetPosition){
        if(targetPosition>view_background.getRight()-view_shadow.getWidth()/2){
            targetPosition = view_background.getRight()- view_shadow.getWidth()/2;
        }
        //绘制进度
        LayoutParams lp = (LayoutParams) view_shadow.getLayoutParams();
        lp.setMargins(targetPosition,0,0,0);
        view_shadow.setLayoutParams(lp);
        //view_shadow.setX(targetPosition);
        //绘制显示当前速度的textview
        //最左侧位置向右移动一点，避免显示不全
        if(targetPosition <= view_background.getLeft() + view_shadow.getWidth()){
            targetPosition = targetPosition + view_shadow.getWidth();
        }
        tv_speed.setX(targetPosition-view_shadow.getWidth());
    }

    /**
     * 计算当前位置的吸附位置  并返回其坐标
     * 吸附范围是 dis 差值 < 10
     * @param targetPosition  当前坐标 中心点的位置
     * @return
     */
    @SuppressLint("LongLogTag")
    private void buildAdsorbentPointDis(int targetPosition) {
        int targetX = targetPosition;
        int adsDis = view_shadow.getWidth()/2;
        int baseAdsDis = 15;
        if(null != ItemPointX && ItemPointX.size()>0){

            for(int i =0; i < ItemPointX.size()-1; i++){

                //5x 10x不做吸附
                if(i>2){
                    return;
                }
                int indexValue = ItemPointX.get(i);
                if(Math.abs(indexValue - targetX) < baseAdsDis/(i+1)){
                    //这里还需要设置显示的速度值
                    tv_speed.setText(itemDataList.get(i).value+"X");
                    doMoveToTargetPosition(indexValue-adsDis);
                    break;
                }
            }
        }
    }

    public void setSelectedData(List<SpeedParam>selectedDataList){
        if(null != selectedDataList && selectedDataList.size()>0){
            this.itemDataList.addAll(selectedDataList);
            ItemPointX = new ArrayList<>(itemDataList.size());
            itemWidth =(view_background.getRight()-view_background.getLeft())/(itemDataList.size()-1);
            for(int i=0 ; i< itemDataList.size(); i++){
                SpeedParam speedParam = itemDataList.get(i);
                //添加对应的节点
                ItemPointX.add(i*itemWidth);

                //添加view显示数据
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.ccffffff));
                textView.setTextSize(10);
                textView.setText(speedParam.showValue);

                //添加节点对应的小图标
                View view = new View(getContext());
                int viewWidth = SizeUtils.dp2px(getContext(),8);
                view.setLayoutParams(new LinearLayout.LayoutParams(viewWidth,viewWidth));
                int viewX = 0;
                if(i == 0){
                    textView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth/2,LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.LEFT);
                    viewX = 0;
                }else if(i == itemDataList.size()-1){
                    textView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth/2,LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.RIGHT);
                    viewX = itemWidth*i-viewWidth*(i+1);
                }else{
                    textView.setLayoutParams(new LinearLayout.LayoutParams(itemWidth,LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.CENTER);
                    viewX = itemWidth*i-viewWidth*i-viewWidth/2;
                }
                ll_data.addView(textView);


                //设置view的位置
                view.setX(viewX);
                view.setBackground(getResources().getDrawable(R.drawable.nv_compile_progress));
                view_mask.addView(view);
            }

            //默认移动到初始化的推荐位置 , 如果当前选择的有值，设置到这个值得位置
            setCurrentSpeedPosition(currentSpeed);

        }
    }

    /**
     * 根据传入的倍速 设置 位置
     * @param currentSpeed
     */
    private void setCurrentSpeedPosition(float currentSpeed) {
        int targetLeftIndex = 0;
        //找到所在位置之后计算x值
        float targetX = 0;
        if(null != itemDataList && itemDataList.size()>0){
            //第一个节点和最后一个节点
            if(currentSpeed == itemDataList.get(0).value){
                targetX =0;
            }else if(currentSpeed == itemDataList.get(itemDataList.size()-1).value){
                targetX = view_background.getRight();
            }else{
                //中间节点
                for(int i = 1; i < itemDataList.size()-1; i++){
                    //如果不是第0个节 则从第一个节点开始比对，获取当前变速值所在的区间 的左节点是哪一个
                    float indexValue = itemDataList.get(i).value;

                    if(currentSpeed <= indexValue){
                        targetLeftIndex = i-1;
                        break;
                    }
                }
                //当前节点区间左右节点对应的值
                float baseSpeedIndexValue = itemDataList.get(targetLeftIndex).value;
                float baseSpeedNextIndexValue = itemDataList.get(targetLeftIndex+1).value;
                targetX = (currentSpeed-baseSpeedIndexValue)/(baseSpeedNextIndexValue-baseSpeedIndexValue)*itemWidth + itemWidth*targetLeftIndex;
            }
        }
        //这里计算出来的targetX是左边x，处理一下按拖拽按钮显示居中
        doMoveToTargetPosition((int) targetX - view_shadow.getWidth()/2);
    }

    /**
     * 根据当前的速度获取到拖拽节点的x坐标 只能应用于 吸附过程
     * @param speed
     * @return
     */
    private int getPointXBySpeed(float speed){
        int targetX = 0;
        if(speed == itemDataList.get(0).value){
            targetX =0;
        }else if(speed == itemDataList.get(itemDataList.size()-1).value){
            targetX = view_background.getRight();
        }else{
            //中间节点，找到节点的位置
            int targetIndex = 1;
            for(int i = 1; i < itemDataList.size()-1; i++){
                //如果不是第0个节 则从第一个节点开始比对，获取当前变速值所在的区间 的左节点是哪一个
                float indexValue = itemDataList.get(i).value;

                if(speed == indexValue){
                    targetIndex = i;
                    break;
                }
            }
            targetX = itemWidth*targetIndex;
        }
        return targetX;
    }

    public void setOnSpeedChangedListener(OnSpeedChangedListener onSpeedChangedListener) {
        this.onSpeedChangedListener = onSpeedChangedListener;
    }

    public void setCurrentSpeed(float speed) {
        this.currentSpeed = speed;
    }


    public interface OnSpeedChangedListener{
        void onSpeedChanged(float speed);
    }

    public static class SpeedParam{
        public float value;
        public String showValue;
        public SpeedParam(float value){
            this.value = value;
            this.showValue = value+"X";
        }

    }
}

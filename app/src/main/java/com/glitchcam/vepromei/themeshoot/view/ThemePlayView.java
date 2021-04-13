package com.glitchcam.vepromei.themeshoot.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.mimodemo.common.utils.TimeUtils;
import com.glitchcam.vepromei.view.ThemeProgress;

public class ThemePlayView extends ConstraintLayout {

    private View rootView;
    private ImageView ivPlay;
    private TextView tvTime;
    private TextView tvAllTime;
    private ThemeProgress timeProgress;
    private int progressMax;
    private int currentProgress;
    private boolean isPlaying = false;
    private OnPlayClickListener onPlayClickListener;

    public boolean isPlaying() {
        return isPlaying;
    }

    public ThemePlayView(Context context) {
        this(context, null);
    }

    public ThemePlayView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemePlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = LayoutInflater.from(context).inflate(R.layout.theme_play_view, this);
        initView();
        initData();
    }


    private void initView() {
        ivPlay = rootView.findViewById(R.id.theme_iv_play);
        tvTime = rootView.findViewById(R.id.theme_tv_now_time);
        tvAllTime = rootView.findViewById(R.id.theme_tv_all_time);
        timeProgress = rootView.findViewById(R.id.theme_time_progress);
        setProgressEnable(true);
        timeProgress.setTouchRatio(3);
        timeProgress.setOnProgressChangeListener(new ThemeProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                if (onPlayClickListener != null) {
                    onPlayClickListener.onProgressChange(fromUser, progress);
                }
            }

            @Override
            public void onThumbTouchDown() {
                setPlaying(false);
            }

            @Override
            public void onThumbTouchUp() {
                setPlaying(true);
            }
        });
    }

    private void initData() {
        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    ivPlay.setImageResource(R.mipmap.theme_view_pause);
                } else {
                    ivPlay.setImageResource(R.mipmap.theme_view_play);
                }
                if (onPlayClickListener != null) {
                    onPlayClickListener.onPlayClick(isPlaying);
                }
            }
        });
    }

    public void setProgressMax(int progressMax) {
        this.progressMax = progressMax;
        if (timeProgress != null) {
            timeProgress.setMax(progressMax);
        }
        if (tvAllTime != null) {
            tvAllTime.setText(changeProgressToTimeStr(progressMax));
        }
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        if (timeProgress != null) {
            timeProgress.setProgress(currentProgress);
        }
        //set time str
        if (tvTime != null) {
            tvTime.setText(changeProgressToTimeStr(currentProgress));
        }
    }

    private String changeProgressToTimeStr(int currentProgress) {
        return TimeUtils.formatTimeStrWithUs(currentProgress);
    }

    /**
     * 设置播放状态
     *
     * @param playing
     */
    public void setPlaying(boolean playing) {
        isPlaying = playing;
        if (isPlaying) {
            ivPlay.setImageResource(R.mipmap.theme_view_pause);
        } else {
            ivPlay.setImageResource(R.mipmap.theme_view_play);
        }
        if (onPlayClickListener != null) {
            onPlayClickListener.onPlayClick(isPlaying);
        }
    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    public void onResume() {
        setPlaying(isPlaying);
    }

    public interface OnPlayClickListener {
        void onPlayClick(boolean isPlaying);

        void onProgressChange(boolean fromUser, int progressPosition);
    }

    public void setProgressEnable(boolean progressEnable) {
        if (timeProgress != null) {
            timeProgress.setEnabled(progressEnable);
        }
    }
}

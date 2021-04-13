package com.glitchcam.vepromei.themeshoot;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.glitchcam.vepromei.R;
import com.glitchcam.vepromei.utils.Util;
import com.glitchcam.vepromei.utils.dataInfo.MusicInfo;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioPlayer {

    private final String TAG = "AudioPlayer";
    private static AudioPlayer mMusicPlayer;

    private MediaPlayer mMediaPlayer;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private MusicInfo mCurrentMusic;
    private Context mContext;

    private Handler m_handler;

    private AudioPlayer(Context context) {
        mContext = context;
        mCurrentMusic = null;
    }

    public static AudioPlayer getInstance(Context context) {
        if (mMusicPlayer == null) {
            synchronized (AudioPlayer.class) {
                if (mMusicPlayer == null) {
                    mMusicPlayer = new AudioPlayer(context);
                }
            }
        }
        return mMusicPlayer;
    }

    private void setM_handler(Handler handler) {
        m_handler = handler;
    }

    public void setCurrentMusic(MusicInfo audioInfo, boolean autoPlay) {
        if (audioInfo == null) {
            return;
        }
        mCurrentMusic = audioInfo;
        mCurrentMusic.setPrepare(false);
        resetMediaPlayer(autoPlay);
    }

    /*
     * 重置MediaPlayer
     * Reset MediaPlayer
     * */
    private void resetMediaPlayer(final boolean autoPlay) {
        stopMusicTimer();
        if (mCurrentMusic == null) {
            if (mMediaPlayer == null)
                return;
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "stop & release: null");
            }
            mMediaPlayer = null;
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

//            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener( ) {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    if (mCurrentMusic != null) {
//                        int trimIn = (int) mCurrentMusic.getTrimIn( ) / 1000;
//                        if (trimIn > 0)
//                            mMediaPlayer.seekTo(trimIn);
//
//                        if (mCurrentMusic.isPrepare( )) {
//                            if (!Util.isBackground(mContext))
//                                startPlay( );
//                        }
//                    }
//                }
//            });

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (mCurrentMusic != null) {
                        mCurrentMusic.setPrepare(true);
                        mMediaPlayer.seekTo((int) mCurrentMusic.getTrimIn() / 1000);
                    }
                    if (!Util.isBackground(mContext) && autoPlay)
                        startPlay();
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.e(TAG, "MediaPlayer onError:" + i);
                    Toast.makeText(mContext, mContext.getString(R.string.play_error), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "stop & release: null");
        }
        try {
            String url = "";
            if (mCurrentMusic.isHttpMusic())
                url = mCurrentMusic.getFileUrl();
            else
                url = mCurrentMusic.getFilePath();
            if (url != null) {
                Log.e(TAG, "url:" + url);
                if (mCurrentMusic.isAsset()) {
                    Log.e(TAG, "path:" + mCurrentMusic.getAssetPath());
                    AssetFileDescriptor musicfd = mContext.getAssets().openFd(mCurrentMusic.getAssetPath());
                    Log.e(TAG, "musicfd:" + musicfd);
                    Log.e(TAG, "length:" + musicfd.getLength());
                    mMediaPlayer.setDataSource(musicfd.getFileDescriptor(), musicfd.getStartOffset(), musicfd.getLength());
                } else {
                    mMediaPlayer.setDataSource(url);
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroyPlayer() {
        if (mMediaPlayer == null) {
            return;
        }
        stopMusicTimer();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
//        m_handler.removeCallbacksAndMessages(null);
    }

    public void startPlay() {
//        stopMusicTimer( );
        if (mCurrentMusic == null || mMediaPlayer == null) {
            return;
        }
        if (mCurrentMusic.isPrepare()) {
            try {
                mMediaPlayer.start();
//                startMusicTimer( );
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "start Exception");
            }
        }
//        if (mListener != null) {
//            mListener.onMusicPlay( );
//        }
    }

    public void startPlay(int startTimePosition) {
//        stopMusicTimer( );
        if (mCurrentMusic == null || mMediaPlayer == null) {
            return;
        }
        if (mCurrentMusic.isPrepare()) {
            try {
                mMediaPlayer.seekTo(startTimePosition);
                mMediaPlayer.start();
//                startMusicTimer( );
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "start Exception");
            }
        }
//        if (mListener != null) {
//            mListener.onMusicPlay( );
//        }
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
//            stopMusicTimer( );
//            if (mListener != null) {
//                mListener.onMusicStop( );
//            }
        }
    }
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stopPlay(boolean isBackSeek) {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (isBackSeek && currentPosition > 2000) {
                mMediaPlayer.seekTo(currentPosition - 2000);
                Log.e(TAG, "================current time2:" + mMediaPlayer.getCurrentPosition());
            }
//            stopMusicTimer( );
//            if (mListener != null) {
//                mListener.onMusicStop( );
//            }
        }
    }

    /*
     * 开启刷新进度定时器
     * */
    private void startMusicTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//                    m_handler.sendEmptyMessage(UPDATE_TIME);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 100);
    }

    /*
     * 停止刷新进度定时器
     * Stop refreshing the progress timer
     * */
    private void stopMusicTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public int getNowPlayPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }
}

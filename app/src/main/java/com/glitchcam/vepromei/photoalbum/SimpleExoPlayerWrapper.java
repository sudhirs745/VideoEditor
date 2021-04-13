package com.glitchcam.vepromei.photoalbum;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.IOException;

import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_PERIOD_TRANSITION;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class SimpleExoPlayerWrapper {

    private static final String TAG = "SimpleExoPlayerWrapper";
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private String mPath;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mPlayCount;
    private PlayerEventListener mPlayerEventListener = null;

    private AnalyticsListener mAnalyticsListener = new AnalyticsListener() {
        @Override
        public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
            Log.d(TAG, " playWhenReady: " + playWhenReady);
            switch (playbackState)
            {
                case STATE_IDLE:
                    Log.d(TAG, "onPlayerStateChanged: STATE_IDLE");
                    break;
                case STATE_BUFFERING:
                    Log.d(TAG, "onPlayerStateChanged: STATE_BUFFERING");
                    if (mPlayerEventListener != null) {
                        mPlayerEventListener.onStateBuffering();
                    }
                    break;
                case STATE_READY:
                    Log.d(TAG, "onPlayerStateChanged: STATE_READY");
                    if (mPlayerEventListener != null) {
                        mPlayerEventListener.onStateReady();
                    }
                    break;
                case STATE_ENDED:
                    Log.d(TAG, "onPlayerStateChanged: STATE_ENDED");
                    break;
            }
        }

        @Override
        public void onTimelineChanged(EventTime eventTime, int i) {
            Log.d(TAG, "onTimelineChanged: ");
        }

        @Override
        public void onPositionDiscontinuity(EventTime eventTime, int reason) {
            Log.d(TAG, "onPositionDiscontinuity: " + eventTime.currentPlaybackPositionMs);
            switch (reason) {
                case DISCONTINUITY_REASON_PERIOD_TRANSITION:
                    Log.d(TAG, "onPositionDiscontinuity: DISCONTINUITY_REASON_PERIOD_TRANSITION");

                    break;
                case Player.DISCONTINUITY_REASON_SEEK:
                    Log.d(TAG, "onPositionDiscontinuity: DISCONTINUITY_REASON_SEEK");
                    break;
                case Player.DISCONTINUITY_REASON_SEEK_ADJUSTMENT:
                    Log.d(TAG, "onPositionDiscontinuity: DISCONTINUITY_REASON_SEEK_ADJUSTMENT");
                    break;
                case Player.DISCONTINUITY_REASON_AD_INSERTION:
                    Log.d(TAG, "onPositionDiscontinuity: DISCONTINUITY_REASON_AD_INSERTION");
                    break;
                case Player.DISCONTINUITY_REASON_INTERNAL:
                    Log.d(TAG, "onPositionDiscontinuity: DISCONTINUITY_REASON_INTERNAL");
                    break;
            }
        }

        @Override
        public void onSeekStarted(EventTime eventTime) {
            Log.d(TAG, "onSeekStarted: ");
        }

        @Override
        public void onSeekProcessed(EventTime eventTime) {
            Log.d(TAG, "onSeekProcessed: ");
        }

        @Override
        public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {
            Log.d(TAG, "onPlaybackParametersChanged: ");
        }

        @Override
        public void onRepeatModeChanged(EventTime eventTime, int i) {
            Log.d(TAG, "onRepeatModeChanged: ");
        }

        @Override
        public void onShuffleModeChanged(EventTime eventTime, boolean b) {
            Log.d(TAG, "onShuffleModeChanged: ");
        }

        @Override
        public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
            Log.d(TAG, "onLoadingChanged: " + isLoading);
        }

        @Override
        public void onPlayerError(EventTime eventTime, ExoPlaybackException e) {

        }

        @Override
        public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
            Log.d(TAG, "onTracksChanged: ");
        }

        @Override
        public void onLoadStarted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            Log.d(TAG, "onLoadStarted: ");
        }

        @Override
        public void onLoadCompleted(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            Log.d(TAG, "onLoadCompleted: ");
        }

        @Override
        public void onLoadCanceled(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            Log.d(TAG, "onLoadCanceled: ");
        }

        @Override
        public void onLoadError(EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException e, boolean b) {

        }

        @Override
        public void onDownstreamFormatChanged(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            Log.d(TAG, "onDownstreamFormatChanged: ");
        }

        @Override
        public void onUpstreamDiscarded(EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
            Log.d(TAG, "onUpstreamDiscarded: ");
        }

        @Override
        public void onMediaPeriodCreated(EventTime eventTime) {
            Log.d(TAG, "onMediaPeriodCreated: ");
        }

        @Override
        public void onMediaPeriodReleased(EventTime eventTime) {
            Log.d(TAG, "onMediaPeriodReleased: ");
        }

        @Override
        public void onReadingStarted(EventTime eventTime) {
            mPlayCount++;
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onPlayCountChanged(mPlayCount);
            }
            Log.d(TAG, "onReadingStarted: ");
        }

        @Override
        public void onBandwidthEstimate(EventTime eventTime, int i, long l, long l1) {
            Log.d(TAG, "onBandwidthEstimate: ");
        }

        @Override
        public void onViewportSizeChange(EventTime eventTime, int i, int i1) {
            Log.d(TAG, "onViewportSizeChange: ");
        }

        @Override
        public void onNetworkTypeChanged(EventTime eventTime, @Nullable NetworkInfo networkInfo) {

        }

        @Override
        public void onMetadata(EventTime eventTime, Metadata metadata) {
            Log.d(TAG, "onMetadata: ");
        }

        @Override
        public void onDecoderEnabled(EventTime eventTime, int i, DecoderCounters decoderCounters) {
            Log.d(TAG, "onDecoderEnabled: ");
        }

        @Override
        public void onDecoderInitialized(EventTime eventTime, int i, String s, long l) {
            Log.d(TAG, "onDecoderInitialized: ");
        }

        @Override
        public void onDecoderInputFormatChanged(EventTime eventTime, int i, Format format) {
            Log.d(TAG, "onDecoderInputFormatChanged: ");
        }

        @Override
        public void onDecoderDisabled(EventTime eventTime, int i, DecoderCounters decoderCounters) {
            Log.d(TAG, "onDecoderDisabled: ");
        }

        @Override
        public void onAudioSessionId(EventTime eventTime, int i) {

        }

        @Override
        public void onAudioUnderrun(EventTime eventTime, int i, long l, long l1) {

        }

        @Override
        public void onDroppedVideoFrames(EventTime eventTime, int i, long l) {

        }

        @Override
        public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            mVideoWidth = width;
            mVideoHeight = height;
            Log.d(TAG, "onVideoSizeChanged: ");
        }

        @Override
        public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onRenderedFirstFrame();
            }
            Log.d(TAG, "onRenderedFirstFrame: ");
        }

        @Override
        public void onDrmKeysLoaded(EventTime eventTime) {
            Log.d(TAG, "onDrmKeysLoaded: ");
        }

        @Override
        public void onDrmSessionManagerError(EventTime eventTime, Exception e) {

        }

        @Override
        public void onDrmKeysRestored(EventTime eventTime) {
            Log.d(TAG, "onDrmKeysRestored: ");
        }

        @Override
        public void onDrmKeysRemoved(EventTime eventTime) {
            Log.d(TAG, "onDrmKeysRemoved: ");
        }
    };

    public SimpleExoPlayerWrapper(Context context) {
        mContext = context;
        mPath = "";
        mVideoWidth = 0;
        mVideoHeight = 0;
        mPlayCount = 0;
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(factory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        mExoPlayer.addAnalyticsListener(mAnalyticsListener);
    }

    public void setPlayerEventListener(PlayerEventListener listener){
        mPlayerEventListener = listener;
    }

    public SimpleExoPlayer getPlayer() {
        return mExoPlayer;
    }

    public String getPath() {
        return mPath;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void resetPlayer(String path) {
        if ((path == null) || path.isEmpty()) {
            return;
        }
        mPath = path;
        try {
            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory factory = new DefaultDataSourceFactory(mContext, com.google.android.exoplayer2.util.Util.getUserAgent(mContext, "jdy"), defaultBandwidthMeter);
            Uri uri = Uri.parse(path);

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(uri, factory, extractorsFactory, null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            mExoPlayer.setPlayWhenReady(false);
        } catch (Exception e) {
            Log.e(TAG,"Exception",e);
        }
    }

    public void start() {
        mExoPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        mExoPlayer.setPlayWhenReady(false);
    }

    public boolean isPlaying() {
        return (mExoPlayer.getPlaybackState() == STATE_READY) && mExoPlayer.getPlayWhenReady();
    }

    public void seekTo(long time) {
        mExoPlayer.seekTo(time);
    }

    public long getCurrentPosition() {
        return mExoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return mExoPlayer.getDuration();
    }

    public void setPlayerIsGone(){
        pause();
        seekTo(0);
        mExoPlayer.clearVideoSurface();
    }

    public void destroyPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public interface PlayerEventListener{
        void onRenderedFirstFrame();
        void onPlayCountChanged(int playCount);
        void onStateBuffering();
        void onStateReady();
    }
}

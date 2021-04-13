package com.glitchcam.vepromei.themeshoot.utlils;

import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.glitchcam.vepromei.themeshoot.bean.ThemePreviewBean;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.utils.Constants;
import com.glitchcam.vepromei.utils.NumberUtils;

import java.io.File;
import java.util.List;

public class ThemeTimelineUtils {
    private static String TAG = "ThemeTimelineUtils";

    /**
     * 添加背景音乐
     *
     * @param mTimeline
     * @param mThemeModel
     */
    public static void addMusic(NvsTimeline mTimeline, ThemeModel mThemeModel) {
        //添加音乐
        String music = mThemeModel.getMusic();
        if (!TextUtils.isEmpty(music)) {
            NvsAudioTrack nvsAudioTrack = mTimeline.appendAudioTrack();
            if (nvsAudioTrack == null) {
                Log.e(TAG, "mTimeline.appendAudioTrack failed");
            }
//            NvsAudioClip nvsAudioClip = nvsAudioTrack.addClip(mThemeModel.getFolderPath() + File.separator + music, 0, 0, mThemeModel.getMusicDuration());
            NvsAudioClip nvsAudioClip = nvsAudioTrack.addClip(mThemeModel.getFolderPath() + File.separator + music, 0);
            if (nvsAudioClip == null) {
                Log.e(TAG, "AudioTrack.addClip failed nvsAudioClip==null!!!");
            }
            if ("1".equals(mThemeModel.getNeedControlMusicFading()+"")) {
                //todo 音量减弱
                long musicFadingTime = mThemeModel.getMusicFadingTime();
                nvsAudioClip.setFadeOutDuration(musicFadingTime * Constants.US_TIME_BASE);
            }
        }
    }

    /**
     * 设置转场
     *
     * @param nvsVideoTrack
     * @param shotInfos
     */
    public static void setVideoClipTrans(NvsVideoTrack nvsVideoTrack, List<ThemeModel.ShotInfo> shotInfos) {
        if (nvsVideoTrack == null || shotInfos == null) {
            Log.e(TAG, "setVideoClipTrans failed nvsVideoTrack == null || shotInfos == null!!!");
            return;
        }
        // 处理转场
        int currentClip = 0;
        for (int i = 0; i < shotInfos.size(); i++) {
            ThemeModel.ShotInfo shotInfo = shotInfos.get(i);
            int addClip = 0;

            if (shotInfo != null) {
                String trans = shotInfo.getTrans();
                if (!TextUtils.isEmpty(trans)) {
                    // 内建与包裹转场(未加)
                    nvsVideoTrack.setPackagedTransition(currentClip + addClip, trans);
                } else {
                    nvsVideoTrack.setBuiltinTransition(currentClip + addClip, null);
                }
                List<ThemeModel.ShotInfo.SpeedInfo> speed = shotInfo.getSpeed();
                if (speed == null || speed.isEmpty()) {
                    //非空镜头 只有一个视频片段
                } else {
                    int clipCount = ThemeShootUtil.getClipCountFromSpeed(speed, shotInfo.getDuration());
                    //分成多段添加
                    if (clipCount > 0) {
                        for (int j = 0; j < clipCount; j++) {
                            if (j != 0) {
                                nvsVideoTrack.setPackagedTransition(currentClip + addClip, "");
                            }
                            addClip++;
                        }
                    }
                }
                currentClip++;
            }
        }
    }

    /**
     * 添加视频片段（包含变速）
     *
     * @param mTimeline
     * @param nvsVideoTrack
     * @param previewBean
     * @param shotInfo
     * @param mThemeModel
     */
    public static void appendVideoClip(NvsTimeline mTimeline, NvsVideoTrack nvsVideoTrack, ThemePreviewBean previewBean, ThemeModel.ShotInfo shotInfo, ThemeModel mThemeModel) {
        String shotFilePath = shotInfo.canPlaced() ? shotInfo.getSource() : mThemeModel.getFolderPath() + File.separator + shotInfo.getSource();
        // 如果有变速 添加片段时 选取时长，循环添加
        List<ThemeModel.ShotInfo.SpeedInfo> speeds = shotInfo.getSpeed();
        if (speeds != null && speeds.size() > 0) {
            //需要追加几次
            long startSpeed = 0;//当前追加位置
            for (int j = 0; j < speeds.size(); j++) {
                ThemeModel.ShotInfo.SpeedInfo speedInfo = speeds.get(j);
                if (speedInfo != null) {
                    //变速开始位置
                    long speedStart = NumberUtils.parseString2Long(speedInfo.getStart());
                    long totalSpeedTime = (NumberUtils.parseString2Long(speedInfo.getEnd()) -
                            NumberUtils.parseString2Long(speedInfo.getStart())) *
                            (NumberUtils.parseString2Long(speedInfo.getSpeed1())
                                    + NumberUtils.parseString2Long(speedInfo.getSpeed0())) / 2;
                    if (mTimeline.getDuration() - previewBean.getStartDuration() < speedStart) {
                        //追加原视频
                        long addDuration = speedStart - (mTimeline.getDuration() - previewBean.getStartDuration());
                        NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                                startSpeed * Constants.US_TIME_BASE, (startSpeed + addDuration) * Constants.US_TIME_BASE);
                        if (nvsVideoClip == null) {
                            Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                            continue;
                        }
                        if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                            nvsVideoClip.setVolumeGain(0, 0);
                        }
                        long duration = mTimeline.getDuration();
                        startSpeed += addDuration;
                        Log.d(TAG, "duration:" + startSpeed);
                    }
                    //追加变速视频
                    NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                            startSpeed * Constants.US_TIME_BASE, (startSpeed + totalSpeedTime) * Constants.US_TIME_BASE);
                    if (nvsVideoClip == null) {
                        Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                        continue;
                    }
                    if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                        nvsVideoClip.setVolumeGain(0, 0);
                    }
                    nvsVideoClip.changeVariableSpeed(NumberUtils.parseString2Double(speedInfo.getSpeed0())
                            , NumberUtils.parseString2Double(speedInfo.getSpeed1()), true);
                    long duration = mTimeline.getDuration() - previewBean.getStartDuration();
                    startSpeed += totalSpeedTime;
                    Log.d(TAG, "duration:" + startSpeed);
                }
            }
            if (startSpeed < shotInfo.getNeedDuration()) {
                //追加原视频
                NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                        startSpeed * Constants.US_TIME_BASE, shotInfo.getNeedDuration() * Constants.US_TIME_BASE);
                if (nvsVideoClip == null) {
                    Log.e(TAG, "createTimeline VideoTrack.appendClip changeSpeed failed!");
                    return;
                }
                if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                    nvsVideoClip.setVolumeGain(0, 0);
                }
                long duration = mTimeline.getDuration() - previewBean.getStartDuration();
                Log.d(TAG, "duration:" + duration);
            }
        } else {
            NvsVideoClip nvsVideoClip = nvsVideoTrack.appendClip(shotFilePath,
                    0, shotInfo.getNeedDuration() * Constants.US_TIME_BASE);
            if (nvsVideoClip == null) {
                Log.e(TAG, "createTimeline VideoTrack.appendClip failed!");
                return;
            }
            if (!TextUtils.isEmpty(mThemeModel.getMusic())) {
                nvsVideoClip.setVolumeGain(0, 0);
            }
        }
    }
}

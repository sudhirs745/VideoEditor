package com.glitchcam.vepromei.mimodemo.common.utils;

import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;

import java.util.Hashtable;

/**
 * Created by admin on 2018/11/13.
 */

public class VideoCompileUtil {
    public static String getCompileVideoPath() {
        String compilePath = PathUtils.getVideoCompileDirPath();
        if (compilePath == null)
            return null;
        long currentMilis = System.currentTimeMillis();
        String videoName = "/meicam_" + String.valueOf(currentMilis) + ".mp4";
        compilePath += videoName;
        return compilePath;
    }

    public static void compileVideo(NvsStreamingContext context,
                                    NvsTimeline timeline,
                                    String compileVideoPath,
                                    long startTime,
                                    long endTime) {
        if (context == null || timeline == null || compileVideoPath.isEmpty()) {
            return;
        }
        context.stop();
        context.setCompileConfigurations(null);//之前配置清空
        double bitrate = 0;
        if (bitrate != 0) {
            Hashtable<String, Object> config = new Hashtable<>();
            config.put(NvsStreamingContext.COMPILE_BITRATE, bitrate * 1000000);
            context.setCompileConfigurations(config);
        }
        int encoderFlag = 0;
      /*  if (ParameterSettingValues.instance().disableDeviceEncorder()) {
            encoderFlag = NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_DISABLE_HARDWARE_ENCODER;
        }*/
        context.setCustomCompileVideoHeight(timeline.getVideoRes().imageHeight);
        context.compileTimeline(timeline, startTime, endTime, compileVideoPath, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, encoderFlag);
    }
}

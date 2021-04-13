package com.glitchcam.vepromei.mimodemo.common.utils;

public class TimeUtils {
    public static String formatTimeStrWithUs(long us) {
        double secondD = (us / 1000000.0);
        int second = (int) Math.round(secondD);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }
}

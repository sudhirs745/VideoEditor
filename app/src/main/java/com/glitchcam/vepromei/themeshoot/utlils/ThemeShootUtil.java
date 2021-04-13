package com.glitchcam.vepromei.themeshoot.utlils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.meicam.sdk.NvsAssetPackageManager;
import com.glitchcam.vepromei.edit.data.ParseJsonFile;
import com.glitchcam.vepromei.themeshoot.model.ThemeModel;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.NumberUtils;
import com.glitchcam.vepromei.utils.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemeShootUtil {
    private static final String PATH_ASSETS = "assets:/";
    private static final String LOCAL_THEME_MODEL_PATH = "NvStreamingSdk" + File.separator + "Asset" + File.separator + "ThemeModel";
    private static final String SUFFIX_JSON_FILE = ".json";

    public static String getThemeSDPath() {
        return Environment.getExternalStorageDirectory() + File.separator + LOCAL_THEME_MODEL_PATH;
    }

    public static String getThemeAssetsFilePath(String folderPath, String fileName, boolean isBuildIn) {
        StringBuilder sb = new StringBuilder();
        if (isBuildIn) {
            sb.append(PATH_ASSETS).append(folderPath).append(File.separator).append(fileName);
        } else {
            sb.append(folderPath).append(File.separator).append(fileName);
        }
        return sb.toString();
    }

    public static List<ThemeModel> getThemeModelListFromSdCard(Context context) {
        File file = new File(Environment.getExternalStorageDirectory(), LOCAL_THEME_MODEL_PATH);
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }
        String[] list = file.list();
        if (list == null || list.length <= 0) {
            return null;
        }
        String parentPath = file.getAbsolutePath();
        List<ThemeModel> themeModelList = new ArrayList<>();
        for (int index = 0; index < list.length; index++) {
            String childPath = parentPath + File.separator + list[index];
            File childFolder = new File(childPath);
            if (!childFolder.isDirectory())
                continue;
            ThemeModel themeModel = getLocalThemeModelByPath(childPath);
            if (themeModel != null) {
                themeModelList.add(themeModel);
            }
        }
        return themeModelList;
//            File[] childlist = childFolder.listFiles();
//
//            for (int i = 0; i < childlist.length; i++) {
//                File onfile = childlist[i];
//                String childFileName = onfile.getName();
//                if (childFileName.endsWith(SUFFIX_JSON_FILE)) {
//                    String jsonFile = ParseJsonFile.readSdCardJsonFile(childPath + File.separator + childFileName);
//                    if (jsonFile == null) {
//                        continue;
//                    }
//                    themeModel = ParseJsonFile.fromJson(jsonFile, ThemeModel.class);
//                    if (themeModel == null) {
//                        continue;
//                    }
//                    computeNeedTime(themeModel);//计算实际需要的时间
//                    themeModel.setFolderPath(childPath);
//                    themeModel.setIsBuildInTemp(false);
//                    themeModel.setLocal(true);
//
//                }
//            }
//            if (themeModel == null) {
//                continue;
//            }
//            List<ThemeModel.ShotInfo> shotInfos = themeModel.getShotInfos();
//            if (shotInfos != null) {
//                for (int j = 0; j < shotInfos.size(); j++) {
//                    ThemeModel.ShotInfo shotInfo = shotInfos.get(j);
//                    if (shotInfo != null) {
//                        if (!TextUtils.isEmpty(shotInfo.getAlertImage())) {
//                            shotInfo.setAlertImage(childFolder + File.separator + shotInfo.getAlertImage());
//                        }
//                        if (!shotInfo.canPlaced()) {
//                            shotInfo.setSource(childFolder + File.separator + shotInfo.getSource());
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < childlist.length; i++) {
//                File oneFile = childlist[i];
//                String childFileName = oneFile.getName();
//                String filePath = oneFile.getPath();
//                if (childFileName.equals("cover.mp4")) {
//                    themeModel.setPreview(filePath);
//                } else if (childFileName.equals("cover.png")) {
//                    themeModel.setCover(filePath);
//                }
//            }
//            themeModel.setId(childFolder.getName());
//            themeModelList.add(themeModel);
//        }
//
//        return themeModelList;
    }

    private static void computeNeedTime(ThemeModel themeModel) {
        List<ThemeModel.ShotInfo> shotInfos = themeModel.getShotInfos();
        if (shotInfos == null) {
            return;
        }

        for (ThemeModel.ShotInfo shotInfo : shotInfos) {
            List<ThemeModel.ShotInfo.SpeedInfo> speed = shotInfo.getSpeed();
            //是否是空镜头
            if (TextUtils.isEmpty(shotInfo.getSource())) {
                shotInfo.setCanPlaced(true);
            } else {
                shotInfo.setCanPlaced(false);
            }
            if (speed == null || speed.isEmpty()) {
                shotInfo.setNeedDuration(shotInfo.getDuration());
                continue;
            }
            long speedTime = 0L;//变速区间需要的时长
            long oriTime = 0;//变速区间对应原录制时长
            for (ThemeModel.ShotInfo.SpeedInfo speedInfo : speed) {
                long needDuration = (long) ((Long.parseLong(speedInfo.getEnd()) - Long.parseLong(speedInfo.getStart())) *
                        (Double.parseDouble(speedInfo.getSpeed0()) + Double.parseDouble(speedInfo.getSpeed1())) / 2);
                speedTime += needDuration;
                oriTime += Long.parseLong(speedInfo.getEnd()) - Long.parseLong(speedInfo.getStart());
            }
            shotInfo.setNeedDuration(speedTime + (shotInfo.getDuration() - oriTime));
        }
    }

    /**
     * 格式化时间串00:00:00
     * Format time string 0:00:00
     */
    public static String formatUsToString(long us) {
        int second = (int) (us / 1000000.0 + 0.5);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        int uu = ((int) us % 1000000) / 100000;
        String timeStr;
        if (us == 0) {
            return "00:00";
        }
//        if (hh > 0) {
//            timeStr = String.format("%02d:%02d:%02d:%01d", hh, mm, ss, uu);
//        } else if(mm > 0){
//            timeStr = String.format("%02d:%02d:%01d", mm, ss, uu);
//        } else {
//            timeStr = String.format("00:%02d:%01d", ss, uu);
//        }
        if (hh > 0) {
            timeStr = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else if (mm > 0) {
            timeStr = String.format("%02d:%02d", mm, ss);
        } else {
            timeStr = String.format("00:%02d", ss);
        }
        return timeStr;
    }

    /**
     * 格式化时间串00:00:00
     * Format time string 0:00:00
     */
    public static String formatUsToStr(long us) {
        int second = (int) (us / 1000000.0 + 0.5);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        int uu = ((int) us % 1000000) / 100000;
        String timeStr;
        if (us == 0) {
            return "0s";
        }
        if (hh > 0) {
            timeStr = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else if (mm > 0) {
            timeStr = String.format("%02d:%02d", mm, ss);
        } else {
            timeStr = String.format("%02d.%01d", ss, uu) + "s";
        }
        return timeStr;
    }

    public static String getVlogCacheFilePath(Context context, int count) {
        File folder = new File(context.getCacheDir(), "mimo");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = "vlog_cache_" + count + ".mp4";
        File file = new File(folder, fileName);
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }

    public static String getCompileVideoPath(){
        String compilePath = PathUtils.getVideoCompileDirPath();
        if (compilePath == null)
            return null;
        long currentMilis = System.currentTimeMillis();
        String videoName = "/themeShoot_" + String.valueOf(currentMilis) + ".mp4";
        compilePath += videoName;
        return compilePath;
    }

    public static ThemeModel getLocalThemeModelByPath(String modelFilePath) {
        try {
            File childFolder = new File(modelFilePath);
            ThemeModel themeModel = null;
            if (!childFolder.isDirectory())
                return null;
            File[] childlist = childFolder.listFiles();
            for (int i = 0; i < childlist.length; i++) {
                File onfile = childlist[i];
                String childFileName = onfile.getName();
                if (childFileName.endsWith(SUFFIX_JSON_FILE)) {
                    String jsonFile = ParseJsonFile.readSdCardJsonFile(modelFilePath + File.separator + childFileName);
                    if (jsonFile == null) {
                        continue;
                    }
                    themeModel = ParseJsonFile.fromJson(jsonFile, ThemeModel.class);
                    if (themeModel == null) {
                        continue;
                    }
                    computeNeedTime(themeModel);//计算实际需要的时间
                    themeModel.setFolderPath(modelFilePath);
                    themeModel.setIsBuildInTemp(false);
                    themeModel.setLocal(true);
                }

            }
            if (themeModel == null) {
                return null;
            }
            List<String> filterPaths = new ArrayList<>();
            for (int i = 0; i < childlist.length; i++) {
                File oneFile = childlist[i];
                String childFileName = oneFile.getName();
                String filePath = oneFile.getPath();
                if (childFileName.equals("cover.mp4")) {
                    themeModel.setPreview(filePath);
                } else if (childFileName.equals("cover.png")) {
                    themeModel.setCover(filePath);
                } else if (childFileName.endsWith(".videofx") ||
                        childFileName.endsWith(".compoundcaption") ||
                        childFileName.endsWith(".videotransition")) {
                    filterPaths.add(filePath);
                }
            }
            themeModel.setPackagePaths(filterPaths);
            themeModel.setFolderPath(modelFilePath);
            themeModel.setId(childFolder.getName());
            return themeModel;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转9V16地址
     *
     * @param filePath
     * @return
     */
    public static String get9V16PathByPath(String filePath) {
        if (!TextUtils.isEmpty(filePath) && !filePath.contains("9v16.")) {
            String[] split = filePath.split("\\.");
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < split.length; i++) {
                if (i == split.length - 1) {
                    stringBuffer.append("9v16.").append(split[i]);
                } else {
                    stringBuffer.append(split[i]);
                }
            }
            return stringBuffer.toString();
        }
        return filePath;
    }

    public static int getClipCountFromSpeed(List<ThemeModel.ShotInfo.SpeedInfo> speeds, long duration) {
        int start = 0;
        int count = 0;
        if (speeds != null) {
            for (int i = 0; i < speeds.size(); i++) {
                ThemeModel.ShotInfo.SpeedInfo speedInfo = speeds.get(i);
                long speedStart = NumberUtils.parseString2Long(speedInfo.getStart());
                long speedEnd = NumberUtils.parseString2Long(speedInfo.getEnd());
                //添加原视频
                if (start < speedStart) {
                    count++;
                    start += speedStart;
                }
                //添加变速视频
                count++;
                start += (speedEnd - speedStart);
            }
            //添加原视频
            if (start < duration) {
                count++;
            }
        }
        return count;
    }

    public static void refreshThemeData(ThemeModel mThemeData, ThemeModel oldModel) {
        if(mThemeData==null||oldModel==null){
            return;
        }
        mThemeData.setCover(oldModel.getCover());
        mThemeData.setPreview(oldModel.getPreview());
        mThemeData.setFolderPath(oldModel.getFolderPath());
        mThemeData.setLocal(true);
        mThemeData.setPackagePaths(oldModel.getPackagePaths());
        mThemeData.setShotInfos(oldModel.getShotInfos());
    }
}

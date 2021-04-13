package com.glitchcam.vepromei.mimodemo.bean;

import android.text.TextUtils;

import com.glitchcam.vepromei.mimodemo.common.template.model.ShotDataInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.TemplateFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * "id": "1921D41E-6B3D-4D84-9B1B-592CDA3D9409",
 * "childrenIDs": ["7310D72B-57E5-4B4F-877F-94FD87E38067", "6DEE7B8F-5804-47A1-97AD-214D27CB8E96", "7DE5C971-D0E8-4099-A28E-3D2EC27B3FC7", "F62386FF-DF8A-4348-BA22-09DD145A0F13"],
 * "name": "FUNNY",
 * "cover": "cover.png",
 * "preview": "cover.mp4",
 * "shotsNumber": 5,
 * "music": "music.mp3",
 * "musicDuration": 19000,
 * "endingFilter": "7310D72B-57E5-4B4F-877F-94FD87E38067",
 * "endingFilterLen": 2000,
 * "endingWatermark": "6DEE7B8F-5804-47A1-97AD-214D27CB8E96",
 * "timelineFilter": "7DE5C971-D0E8-4099-A28E-3D2EC27B3FC7",
 * "translation": [{
 * "originalText": "FUNNY",
 * "targetLanguage": "zh_CN",
 * "targetText": "\u4e50\u8da3"
 * }],
 * "shotInfos": [{
 * "shot": 0,
 * "source": "black.mp4",
 * "duration": 840
 * }, {
 * "shot": 1,
 * "filter": "F62386FF-DF8A-4348-BA22-09DD145A0F13",
 * "duration": 2440
 * }, {
 * "shot": 2,
 * "source": "black.mp4",
 * "duration": 880
 * }, {
 * "shot": 3,
 * "filter": "F62386FF-DF8A-4348-BA22-09DD145A0F13",
 * "duration": 2200
 * }, {
 * "shot": 4,
 * "source": "black.mp4",
 * "duration": 520
 * }, {
 * "shot": 5,
 * "filter": "F62386FF-DF8A-4348-BA22-09DD145A0F13",
 * "duration": 2560
 * }, {
 * "shot": 6,
 * "source": "black.mp4",
 * "duration": 1120
 * }, {
 * "shot": 7,
 * "filter": "F62386FF-DF8A-4348-BA22-09DD145A0F13",
 * "duration": 2440
 * }, {
 * "shot": 8,
 * "source": "black.mp4",
 * "duration": 880
 * }, {
 * "shot": 9,
 * "filter": "F62386FF-DF8A-4348-BA22-09DD145A0F13",
 * "duration": 5120
 * }]
 * }
 */
public class MiMoLocalData {

    private String id;
    private String sourceDir;//绝对路径

    private boolean isLocal;//是否已经保存在本地
    private String filePath;//文件地址？
    private List<String> childrenIDs;
    private String name;
    private String cover;
    private String preview;
    private String shotsNumber;
    private String music;
    private int musicDuration;
    private String endingFilter;
    private int endingFilterLen;
    private String endingWatermark;
    private String timelineFilter;
    private List<Translation> translation;
    private List<ShotInfo> shotInfos;
    private List<ShotDataInfo> shotDataInfos;//镜头视频数据,可能包含主轨道，子轨道数据
    List<ShotVideoInfo> totalShotVideoInfos = new ArrayList<>();//当前镜头所有的视频数据
    private String musicFilePath;
    private String timeLineTransSource;//时间线扫换，镜头单轨是，添加多轨默认素材源名称
    private String transSourcePath;//扫换源素材路径
    private boolean isTimelineTrans;//是否是时间线扫换模板
    private String titleFilter;//片头水印贴纸Id
    private float titleFilterDuration;//片头水印贴纸持续时间
    private String titleCaption;//片头字幕
    private float titleCaptionDuration;//片头字幕持续时间
    private String supportedAspectRatio;//模板支持的画幅

    private String uuid;
    private String coverUrl;
    private String videoUrl;
    private String videoPath;
    private String packageUrl;
    private String licPath;

    public String getLicPath() {
        return licPath;
    }

    public void setLicPath(String licPath) {
        this.licPath = licPath;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public class Translation {
        private String originalText;
        private String targetLanguage;
        private String targetText;

        public String getOriginalText() {
            return originalText;
        }

        public void setOriginalText(String originalText) {
            this.originalText = originalText;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }

        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }

        public String getTargetText() {
            return targetText;
        }

        public void setTargetText(String targetText) {
            this.targetText = targetText;
        }
    }

    public boolean isExist() {
        if (filePath != null && !filePath.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getChildrenIDs() {
        return childrenIDs;
    }

    public void setChildrenIDs(List<String> childrenIDs) {
        this.childrenIDs = childrenIDs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getShotsNumber() {
        return shotsNumber;
    }

    public void setShotsNumber(String shotsNumber) {
        this.shotsNumber = shotsNumber;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getEndingFilter() {
        return endingFilter;
    }

    public void setEndingFilter(String endingFilter) {
        this.endingFilter = endingFilter;
    }

    public int getEndingFilterLen() {
        return endingFilterLen;
    }

    public void setEndingFilterLen(int endingFilterLen) {
        this.endingFilterLen = endingFilterLen;
    }

    public String getEndingWatermark() {
        return endingWatermark;
    }

    public void setEndingWatermark(String endingWatermark) {
        this.endingWatermark = endingWatermark;
    }

    public String getTimelineFilter() {
        return timelineFilter;
    }

    public void setTimelineFilter(String timelineFilter) {
        this.timelineFilter = timelineFilter;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }

    public List<ShotInfo> getShotInfos() {
        return shotInfos;
    }

    public void setShotInfos(List<ShotInfo> shotInfos) {
        this.shotInfos = shotInfos;
    }

    public List<ShotDataInfo> getShotDataInfos() {
        return shotDataInfos;
    }

    public void setShotDataInfos(List<ShotDataInfo> shotDataInfos) {
        this.shotDataInfos = shotDataInfos;
    }

    //获取模板里面所有的镜头视频信息列表
    public List<ShotVideoInfo> getTotalShotVideoInfos(){
        return totalShotVideoInfos;
    }

    private void clearTotalShotVideoInfos(){
        totalShotVideoInfos.clear();
    }

    /**
     * 更新模板里面所有的镜头视频信息列表
     */
    public void updateTotalShotVideoInfos(){
        clearTotalShotVideoInfos();
        if (shotDataInfos == null || shotDataInfos.isEmpty()){
            return;
        }
        int shotCount = shotDataInfos.size();
        for (int shotIndex = 0;shotIndex < shotCount;shotIndex++){
            ShotDataInfo shotDataInfo = shotDataInfos.get(shotIndex);
            if (shotDataInfo == null){
                continue;
            }
            ShotVideoInfo mainTrackVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if ((mainTrackVideoInfo == null)) {
                continue;
            }
            if ((shotInfos != null) && (!shotInfos.isEmpty())) {
                ShotInfo shotInfo = shotInfos.get(shotIndex);
                if (shotInfo != null) {
                    if (!TextUtils.isEmpty(shotInfo.getSource())) {
                        mainTrackVideoInfo.setVideoClipPath( sourceDir + File.separator + shotInfo.getSource());
                        continue;
                    }
                }
            }
            totalShotVideoInfos.add(mainTrackVideoInfo);
            List<ShotVideoInfo> subTrackVideoInfo = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfo != null && !subTrackVideoInfo.isEmpty()){
                boolean isTimelineTransSource = false;
                String clipPath = subTrackVideoInfo.get(0).getVideoClipPath();
                if (!TextUtils.isEmpty(clipPath) && clipPath.contains(TemplateFileUtils.PATH_ASSETS)){
                    isTimelineTransSource = true;
                }
                //跳过时间线扫换默认素材源
                if (isTimelineTransSource){
                    continue;
                }
                totalShotVideoInfos.addAll(subTrackVideoInfo);
            }
        }
    }

    public String getMusicFilePath() {
        return musicFilePath;
    }

    public void setMusicFilePath(String musicFilePath) {
        this.musicFilePath = musicFilePath;
    }

    public String getTimeLineTransSource() {
        return timeLineTransSource;
    }

    public String getTransSourcePath() {
        return transSourcePath;
    }

    public void setTransSourcePath(String transSourcePath) {
        this.transSourcePath = transSourcePath;
    }

    public boolean isTimelineTrans() {
        return isTimelineTrans;
    }

    public void setTimelineTrans(boolean timelineTrans) {
        isTimelineTrans = timelineTrans;
    }

    public String getTitleFilter() {
        return titleFilter;
    }

    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }

    public float getTitleFilterDuration() {
        return titleFilterDuration;
    }

    public void setTitleFilterDuration(float titleFilterDuration) {
        this.titleFilterDuration = titleFilterDuration;
    }

    public String getTitleCaption() {
        return titleCaption;
    }

    public void setTitleCaption(String titleCaption) {
        this.titleCaption = titleCaption;
    }

    public float getTitleCaptionDuration() {
        return titleCaptionDuration;
    }

    public void setTitleCaptionDuration(float titleCaptionDuration) {
        this.titleCaptionDuration = titleCaptionDuration;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    /**
     * 重置模板选择的视频数据信息
     */
    public void resetTemplateVideoInfo() {
        if (shotDataInfos == null || shotDataInfos.isEmpty()){
            return;
        }
        int shotCount = shotDataInfos.size();
        for (int shotIndex = 0;shotIndex < shotCount;shotIndex++){
            ShotDataInfo shotDataInfo = shotDataInfos.get(shotIndex);
            if (shotDataInfo == null){
                continue;
            }
            ShotVideoInfo mainTrackVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if (mainTrackVideoInfo != null){
                mainTrackVideoInfo.resetShotVideoInfo();
            }
            List<ShotVideoInfo> subTrackVideoInfo = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfo != null && !subTrackVideoInfo.isEmpty()){
                int videoCount = subTrackVideoInfo.size();
                for (int videoIndex = 0; videoIndex < videoCount; videoIndex++) {
                    ShotVideoInfo shotVideoInfo = subTrackVideoInfo.get(videoIndex);
                    if (shotVideoInfo == null){
                        continue;
                    }
                    shotVideoInfo.resetShotVideoInfo();
                }
            }
        }
    }
}

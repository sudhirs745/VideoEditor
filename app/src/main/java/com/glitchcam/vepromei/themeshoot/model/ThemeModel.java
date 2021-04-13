package com.glitchcam.vepromei.themeshoot.model;

import java.io.Serializable;
import java.util.List;

public class ThemeModel implements Serializable {
    //线上
    private boolean isLocal;
    private String downLoadPackageUrl;
    //本地
    private List<String> childrenIDs;
    private String id;
    private String cover;//封面
    private String music;//mp3
    private long musicDuration;
    private String name;//名称
    private int needControlMusicFading;
    private String preview;//播放视频
    private String tag;//
    private String shotsNumber;
    private String titleFilter;
    private String titleFilterDuration;
    private List<Translation> translation;
    //未知
    private long musicFadingTime;
    private String titleCover;
    private String titleCaption;
    private long titleCaptionDuration;
    private String endingFilter;
    private String endingFilterLen;
    private String endingCover;
    private String supportedAspectRatio;
    private List<ShotInfo> shotInfos;
    private String mFolderPath;
    private boolean mIsBuildInTemp;

    public String getTitleCover() {
        return titleCover;
    }

    public void setTitleCover(String titleCover) {
        this.titleCover = titleCover;
    }

    public String getEndingCover() {
        return endingCover;
    }

    public void setEndingCover(String endingCover) {
        this.endingCover = endingCover;
    }

    //自定义 包裹路径
    private List<String> packagePaths;

    public List<String> getPackagePaths() {
        return packagePaths;
    }

    public void setPackagePaths(List<String> packagePaths) {
        this.packagePaths = packagePaths;
    }

    public List<String> getChildrenIDs() {
        return childrenIDs;
    }

    public void setChildrenIDs(List<String> childrenIDs) {
        this.childrenIDs = childrenIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public void setMusicDuration(long musicDuration) {
        this.musicDuration = musicDuration;
    }

    public void setNeedControlMusicFading(int needControlMusicFading) {
        this.needControlMusicFading = needControlMusicFading;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setShotsNumber(String shotsNumber) {
        this.shotsNumber = shotsNumber;
    }

    public String getTitleFilter() {
        return titleFilter;
    }

    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }

    public String getTitleFilterDuration() {
        return titleFilterDuration;
    }

    public void setTitleFilterDuration(String titleFilterDuration) {
        this.titleFilterDuration = titleFilterDuration;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    public void setTranslation(List<Translation> translation) {
        this.translation = translation;
    }

    public void setMusicFadingTime(long musicFadingTime) {
        this.musicFadingTime = musicFadingTime;
    }

    public String getTitleCaption() {
        return titleCaption;
    }

    public void setTitleCaption(String titleCaption) {
        this.titleCaption = titleCaption;
    }

    public long getTitleCaptionDuration() {
        return titleCaptionDuration;
    }

    public void setTitleCaptionDuration(long titleCaptionDuration) {
        this.titleCaptionDuration = titleCaptionDuration;
    }

    public String getEndingFilter() {
        return endingFilter;
    }

    public void setEndingFilter(String endingFilter) {
        this.endingFilter = endingFilter;
    }

    public String getEndingFilterLen() {
        return endingFilterLen;
    }

    public void setEndingFilterLen(String endingFilterLen) {
        this.endingFilterLen = endingFilterLen;
    }

    public void setSupportedAspectRatio(String supportedAspectRatio) {
        this.supportedAspectRatio = supportedAspectRatio;
    }

    public String getmFolderPath() {
        return mFolderPath;
    }

    public void setmFolderPath(String mFolderPath) {
        this.mFolderPath = mFolderPath;
    }

    public boolean ismIsBuildInTemp() {
        return mIsBuildInTemp;
    }

    public void setmIsBuildInTemp(boolean mIsBuildInTemp) {
        this.mIsBuildInTemp = mIsBuildInTemp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getDownLoadPackageUrl() {
        return downLoadPackageUrl;
    }

    public void setDownLoadPackageUrl(String downLoadPackageUrl) {
        this.downLoadPackageUrl = downLoadPackageUrl;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public String getSupportedAspectRatio() {
        return supportedAspectRatio;
    }

    public String getShotsNumber() {
        return shotsNumber;
    }

    public String getMusic() {
        return music;
    }

    public int getNeedControlMusicFading() {
        return needControlMusicFading;
    }

    public long getMusicFadingTime() {
        return musicFadingTime;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    public String getFolderPath() {
        return mFolderPath;
    }

    public void setFolderPath(String folderPath) {
        this.mFolderPath = folderPath;
    }

    public boolean isBuildInTemp() {
        return mIsBuildInTemp;
    }

    public void setIsBuildInTemp(boolean isBuildInTemp) {
        this.mIsBuildInTemp = isBuildInTemp;
    }

    public List<ShotInfo> getShotInfos() {
        return shotInfos;
    }

    public void setShotInfos(List<ShotInfo> shotInfos) {
        this.shotInfos = shotInfos;
    }

    public class Translation implements Serializable {
        private String originalText;
        private String targetLanguage;
        public String targetText;
    }

    public class ShotInfo implements Serializable {
        private String shot;
        private String source;
        private String trans;
        private String filter;
        private long duration;
        private long fileDuration;
        private String compoundCaption;
        private List<SpeedInfo> speed;
        private List<AlertInfo> alertInfo;
        private String alertImage;
        private boolean mCanPlaced;
        private long needDuration;

        public String getTrans() {
            return trans;
        }

        public void setTrans(String trans) {
            this.trans = trans;
        }

        public String getCompoundCaption() {
            return compoundCaption;
        }

        public void setCompoundCaption(String compoundCaption) {
            this.compoundCaption = compoundCaption;
        }

        public String getAlertImage() {
            return alertImage;
        }

        public void setAlertImage(String alertImage) {
            this.alertImage = alertImage;
        }

        public List<AlertInfo> getAlertInfo() {
            return alertInfo;
        }

        public void setAlertInfo(List<AlertInfo> alertInfo) {
            this.alertInfo = alertInfo;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public boolean canPlaced() {
            return mCanPlaced;
        }

        public void setCanPlaced(boolean canPlaced) {
            this.mCanPlaced = canPlaced;
        }

        public String getFilter() {
            return filter;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public long getFileDuration() {
            return fileDuration;
        }

        public void setFileDuration(long fileDuration) {
            this.fileDuration = fileDuration;
        }

        public long getNeedDuration() {
            return needDuration;
        }

        public void setNeedDuration(long needDuration) {
            this.needDuration = needDuration;
        }

        public List<SpeedInfo> getSpeed() {
            return speed;
        }

        public void setSpeed(List<SpeedInfo> speed) {
            this.speed = speed;
        }

        public class AlertInfo implements Serializable {
            String originalText;
            String targetLanguage;
            String targetText;

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

        public class SpeedInfo implements Serializable {
            String start;
            String end;
            String speed0;
            String speed1;
            long needDuration;

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public String getSpeed0() {
                return speed0;
            }

            public void setSpeed0(String speed0) {
                this.speed0 = speed0;
            }

            public String getSpeed1() {
                return speed1;
            }

            public void setSpeed1(String speed1) {
                this.speed1 = speed1;
            }

            public long getNeedDuration() {
                return needDuration;
            }

            public void setNeedDuration(long needDuration) {
                this.needDuration = needDuration;
            }
        }
    }
}

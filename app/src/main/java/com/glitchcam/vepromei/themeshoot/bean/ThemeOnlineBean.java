package com.glitchcam.vepromei.themeshoot.bean;

import java.util.List;

public class ThemeOnlineBean {
    private String errNo;
    private boolean hasNext;
    private List<ThemeOnlineDetail> list;

    public String getErrNo() {
        return errNo;
    }

    public void setErrNo(String errNo) {
        this.errNo = errNo;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<ThemeOnlineDetail> getList() {
        return list;
    }

    public void setList(List<ThemeOnlineDetail> list) {
        this.list = list;
    }

    public class ThemeOnlineDetail{

        private String coverUrl;
        private String id;
        private String packageInfo;
        private String packageUrl;
        private String videoUrl;
        private String uuid;

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPackageInfo() {
            return packageInfo;
        }

        public void setPackageInfo(String packageInfo) {
            this.packageInfo = packageInfo;
        }

        public String getPackageUrl() {
            return packageUrl;
        }

        public void setPackageUrl(String packageUrl) {
            this.packageUrl = packageUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}

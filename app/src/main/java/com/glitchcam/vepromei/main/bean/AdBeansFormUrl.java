package com.glitchcam.vepromei.main.bean;

import java.util.List;

public class AdBeansFormUrl {
    private int errNo;
    private boolean hasNext;
    private List<AdInfo> list;

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean getHasNext() {
        return hasNext;
    }

    public void setList(List<AdInfo> list) {
        this.list = list;
    }

    public List<AdInfo> getList() {
        return list;
    }

    public static class AdInfo {
        private String id;
        private String coverUrl;
        private String coverUrl2;
        private String coverUrl3;
        private String advertisementUrl;
        private String advertisementUrlEn;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getCoverUrl2() {
            return coverUrl2;
        }

        public void setCoverUrl2(String coverUrl) {
            this.coverUrl2 = coverUrl;
        }

        public String getCoverUrl3() {
            return coverUrl3;
        }

        public void setCoverUrl3(String coverUrl) {
            this.coverUrl3 = coverUrl;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setAdvertisementUrl(String advertisementUrl) {
            this.advertisementUrl = advertisementUrl;
        }

        public String getAdvertisementUrl() {
            return advertisementUrl;
        }

        public String getAdvertisementUrlEn() {
            return advertisementUrlEn;
        }

        public void setAdvertisementUrlEn(String advertisementUrlEn) {
            this.advertisementUrlEn = advertisementUrlEn;
        }
    }
}


package com.glitchcam.vepromei.photoalbum;

import java.util.List;

/**
 * Created by ms on 2019/9/25
 */
public class PhotoAlbumOnlineData {

    /**
     * errNo : 0
     * hasNext : false
     * list : [{"id":"8FB5A4C7-BAFC-4FCD-9994-F496A78F47C3","category":1,"name":"scene1","desc":"","tags":"","version":1,"minAppVersion":"","packageUrl":"https://meishesdk.meishe-app.com/material/capturescene/8FB5A4C7-BAFC-4FCD-9994-F496A78F47C3.capturescene","packageSize":5200000,"coverUrl":"https://meishesdk.meishe-app.com/material/capturescene/8FB5A4C7-BAFC-4FCD-9994-F496A78F47C3.png","supportedAspectRatio":4},{"id":"897F4258-74C0-4F89-884E-3E6C07E3EE0E","category":1,"name":"scene2","desc":"","tags":"","version":1,"minAppVersion":"","packageUrl":"https://meishesdk.meishe-app.com/material/capturescene/897F4258-74C0-4F89-884E-3E6C07E3EE0E.capturescene","packageSize":5500000,"coverUrl":"https://meishesdk.meishe-app.com/material/capturescene/897F4258-74C0-4F89-884E-3E6C07E3EE0E.png","supportedAspectRatio":4}]
     */

    private int errNo;
    private boolean hasNext;
    private List<PhotoAlbumDetails> list;

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<PhotoAlbumDetails> getList() {
        return list;
    }

    public void setList(List<PhotoAlbumDetails> list) {
        this.list = list;
    }

    public static class PhotoAlbumDetails {
        private String id;
        private String uuid;
        private String coverUrl;
        private String videoUrl;
        private String packageUrl;
        private String packageInfo;

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

        public String getPackageUrl() {
            return packageUrl;
        }
        public void setPackageUrl(String packageUrl) {
            this.packageUrl = packageUrl;
        }

        public String getPackageInfo() {
            return packageInfo;
        }
        public void setPackageInfo(String packageInfo) {
            this.packageInfo = packageInfo;
        }

        public String getUuid() {
            return uuid;
        }
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "PhotoAlbumDetails{" +
                    "id='" + id + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", coverUrl='" + coverUrl + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", packageUrl='" + packageUrl + '\'' +
                    ", packageInfo='" + packageInfo + '\'' +
                    '}';
        }
    }
}

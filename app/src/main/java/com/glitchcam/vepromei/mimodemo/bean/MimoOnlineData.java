package com.glitchcam.vepromei.mimodemo.bean;

import com.glitchcam.vepromei.photoalbum.PhotoAlbumOnlineData;

import java.io.Serializable;
import java.util.List;

public class MimoOnlineData implements Serializable {
    /**
     * errNo : 0
     * hasNext : false
     * "list": [{"id": "414","uuid": "414","coverUrl": "715B.png","videoUrl": "3715B.mp4","packageUrl": "3715B.zip","packageInfo": "{ \"id\": \"414CF0DC-5B3A-4D31-AEDF-AAA62CF3715B\",\r\n\t\"childrenIDs\": [\"1F48BECB-7F54-4A77-A992-A8128A381190\",\r\n\t                \"DA33FB9C-C081-49C6-B3BB-58CF9542DA57\",\r\n\t                \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n\t                \"AA89C22F-BB83-458C-A4C5-A4488B2A8054\"],\r\n\t\"name\": \"SHUTTLE\",\r\n\t\"cover\": \"cover.png\",\r\n\t\"preview\": \"cover.mp4\",\t\r\n\t\"shotsNumber\": 7,\r\n\t\"music\": \"music.mp3\",\r\n\t\"musicDuration\": 20760,\r\n\t\"endingFilter\": \"1F48BECB-7F54-4A77-A992-A8128A381190\",\r\n\t\"endingFilterLen\": 1600,\r\n\t\"endingWatermark\": \"DA33FB9C-C081-49C6-B3BB-58CF9542DA57\",\r\n    \"translation\": [\r\n\t\t{\r\n\t\t\t\"originalText\": \"SHUTTLE\",\r\n\t\t\t\"targetLanguage\": \"zh_CN\",\r\n\t\t\t\"targetText\": \"\u7a7f\u68ad\"\r\n\t\t}\r\n\t],\r\n\t\r\n    \"shotInfos\":[\r\n        {\r\n            \"shot\":0,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n\t\t\t\"filter\": \"AA89C22F-BB83-458C-A4C5-A4488B2A8054\",\r\n            \"duration\":3160,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 3160,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\t\r\n        {\r\n            \"shot\":1,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n            \"duration\":2560,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 2560,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\t\r\n\t\t{\r\n            \"shot\":2,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n            \"duration\":2480,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 2480,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\r\n        {\r\n            \"shot\":3,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n            \"duration\":2440,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 2440,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\r\n\t\t{\r\n            \"shot\":4,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n            \"duration\":2520,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 2520,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\r\n\t\t{\r\n            \"shot\":5,\r\n\t\t\t\"trans\": \"2D904139-4E8B-49EB-8A31-D815FBBA3F85\",\r\n            \"duration\":2520,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 2520,\r\n\t\t\t\t\"speed0\": 1,\r\n\t\t\t\t\"speed1\": 1.2\r\n\t\t\t\t}\r\n\t\t\t]\r\n        },\r\n\t\t{\r\n            \"shot\":6,\r\n            \"duration\":5080,\r\n\t\t\t\"speed\": [\r\n\t\t\t\t{\r\n\t\t\t\t\"start\": 0,\r\n\t\t\t\t\"end\": 5080,\r\n\t\t\t\t\"speed0\": 1.2,\r\n\t\t\t\t\"speed1\": 1\r\n\t\t\t\t}\r\n\t\t\t]\r\n        }\r\n    ]\r\n}\r\n"}]
     */
    private int errNo;
    private boolean hasNext;
    private List<MimoOnlineDataDetails> list;

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

    public List<MimoOnlineDataDetails> getList() {
        return list;
    }

    public void setList(List<MimoOnlineDataDetails> list) {
        this.list = list;
    }

    public class MimoOnlineDataDetails{
        String id;
        String uuid;
        String coverUrl;
        String videoUrl;
        String packageUrl;
        String packageInfo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        @Override
        public String toString() {
            return "MimoOnlineDataDetails{" +
                    "id='" + id + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", coverUrl='" + coverUrl + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", packageUrl='" + packageUrl + '\'' +
                    ", packageInfo='" + packageInfo + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MimoOnlineData{" +
                "errNo=" + errNo +
                ", hasNext=" + hasNext +
                ", list=" + list +
                '}';
    }
}

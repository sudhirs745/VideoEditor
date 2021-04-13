package com.glitchcam.vepromei.utils.license;

/**
 * @author liupanfeng
 * @desc 授权证书info
 * @date 2020/10/29 19:35
 */
public class LicenseInfo {

    private int code;

    private String enMsg;

    private String msg;

    private LicInfo data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LicInfo getData() {
        return data;
    }

    public void setData(LicInfo data) {
        this.data = data;
    }

    public static class LicInfo {

        private Long startTimestamp;

        private String authorizationFileUrl;

        private Long endTimestamp;

        public Long getStartTimestamp() {
            return startTimestamp;
        }

        public void setStartTimestamp(Long startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public String getAuthorizationFileUrl() {
            return authorizationFileUrl;
        }

        public void setAuthorizationFileUrl(String authorizationFileUrl) {
            this.authorizationFileUrl = authorizationFileUrl;
        }

        public Long getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(Long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }
    }



}

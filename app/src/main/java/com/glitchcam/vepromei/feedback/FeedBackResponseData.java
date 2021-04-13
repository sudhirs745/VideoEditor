package com.glitchcam.vepromei.feedback;

/**
 * Created by CaoZhiChao on 2018/11/29 11:10
 * errString
 */
public class FeedBackResponseData {
    private int errNo;

    public FeedBackResponseData() {
    }

    public FeedBackResponseData(int errNo) {
        this.errNo = errNo;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }
}

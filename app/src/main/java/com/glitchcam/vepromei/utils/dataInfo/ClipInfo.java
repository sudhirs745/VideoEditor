package com.glitchcam.vepromei.utils.dataInfo;

import android.graphics.RectF;
import android.text.TextUtils;

import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoStreamInfo;
import com.glitchcam.vepromei.edit.data.ChangeSpeedCurveInfo;
import com.glitchcam.vepromei.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.glitchcam.vepromei.utils.Constants.VIDEOVOLUME_DEFAULTVALUE;

public class ClipInfo {


    public boolean isRecFile = false;
    public int rotation = NvsVideoStreamInfo.VIDEO_ROTATION_0;
    private String m_filePath;
    private float m_speed;
    private boolean keepAudioPitch;
    private boolean m_mute;
    private long m_trimIn;
    private long m_trimOut;

    private double m_start_speed = 1;
    private double m_end_speed = 1;
    /*
     * 校色数据
     * Calibration data
     * */
    private float m_brightnessVal;
    private float m_contrastVal;
    private float m_saturationVal;
    /*
     * 暗角
     * Vignetting
     * */
    private float m_vignetteVal;
    /*
     * 锐度
     * Sharpness
     * */
    private float m_sharpenVal;
    //高光
    private float mHighLight;
    //阴影
    private float mShadow;
    private float fade ;
    //获取色温 色调
    private float temperature;
    private float tint;
    //噪点程度
    private float density ;
    //噪点密度
    private float denoiseDensity;
    /*
     * 音量
     * volume
     * */
    private float m_volume;

    /*
     * 旋转角度
     * Rotation angle
     * */
    private int m_rotateAngle;
    private int m_scaleX;//
    private int m_scaleY;


    /*
     * 图片展示模式
     * Picture display mode
     * */
    private int m_imgDispalyMode = Constants.EDIT_MODE_PHOTO_AREA_DISPLAY;
    /*
     * 是否开启图片运动
     * Whether to enable picture campaign
     * */
    private boolean isOpenPhotoMove = true;
    /*
     * 图片起始ROI
     * Picture starting ROI
     * */
    private RectF m_normalStartROI;
    /*
     * 图片终止ROI
     * Picture termination ROI
     * */
    private RectF m_normalEndROI;

    /*
     * 视频横向裁剪，纵向平移
     * Video cropped horizontally, panned vertically
     * */
    private float m_pan;
    private float m_scan;

    /*
     * 片段滤镜
     * Fragment filter
     * */
    private VideoClipFxInfo m_videoClipFxInfo;

    private String m_fxStoryBoardFileName;
    private String m_clipStoryBoardFileName;

    private String m_minBlurRectAspectRatio;//最小模糊区域的比例
    private String m_maxBlurRectAspectRatio;//最大模糊区域的比例

    private int m_isBlurInFront;

    private long mAnimationDuration;
    private int mAnimationType;

    private String mBackgroundValue;

    //clip设置的VideoFx transform 2D 等
    private List<VideoFx> videoFxs = new ArrayList<>();


    public List<VideoFx> getVideoFxs() {
        return videoFxs;
    }

    private ChangeSpeedCurveInfo mCurveSpeed;


    public VideoFx getVideoFx(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        for (VideoFx videoFx : videoFxs) {
            if (type.equals(videoFx.getSubType())) {
                return videoFx;
            }
        }
        return null;
    }


    private Map<String, StoryboardInfo> mStoryboardInfos = new TreeMap<>();

    public StoryboardInfo getBackgroundInfo() {
        return mStoryboardInfos.get(StoryboardInfo.SUB_TYPE_BACKGROUND);
    }

    public Map<String, StoryboardInfo> getStoryboardInfos() {
        return mStoryboardInfos;
    }

    public void addStoryboardInfo(String key, StoryboardInfo storyInfo) {
        mStoryboardInfos.put(key, storyInfo);
    }

    public void setStoryboardInfos(Map<String, StoryboardInfo> storyboardInfos) {
        mStoryboardInfos = storyboardInfos;
    }

    public void setmAnimationDuration(long mAnimationDuration) {
        this.mAnimationDuration = mAnimationDuration;
    }

    public void setmAnimationType(int mAnimationType) {
        this.mAnimationType = mAnimationType;
    }

    public long getmAnimationDuration() {
        return mAnimationDuration;
    }

    public int getmAnimationType() {
        return mAnimationType;
    }

    public float getPan() {
        return m_pan;
    }

    public void setPan(float pan) {
        this.m_pan = pan;
    }

    public float getScan() {
        return m_scan;
    }

    public void setScan(float scan) {
        this.m_scan = scan;
    }

    public RectF getNormalStartROI() {
        return m_normalStartROI;
    }

    public void setNormalStartROI(RectF normalStartROI) {
        this.m_normalStartROI = normalStartROI;
    }

    public RectF getNormalEndROI() {
        return m_normalEndROI;
    }

    public void setNormalEndROI(RectF normalEndROI) {
        this.m_normalEndROI = normalEndROI;
    }

    public boolean isOpenPhotoMove() {
        return isOpenPhotoMove;
    }

    public void setOpenPhotoMove(boolean openPhotoMove) {
        isOpenPhotoMove = openPhotoMove;
    }

    public int getImgDispalyMode() {
        return m_imgDispalyMode;
    }

    public void setImgDispalyMode(int imgDispalyMode) {
        m_imgDispalyMode = imgDispalyMode;
    }

    public int getScaleX() {
        return m_scaleX;
    }

    public void setScaleX(int scaleX) {
        this.m_scaleX = scaleX;
    }

    public int getScaleY() {
        return m_scaleY;
    }

    public void setScaleY(int scaleY) {
        this.m_scaleY = scaleY;
    }

    public int getRotateAngle() {
        return m_rotateAngle;
    }

    public void setRotateAngle(int rotateAngle) {
        this.m_rotateAngle = rotateAngle;
    }

    public float getVolume() {
        return m_volume;
    }

    public void setVolume(float volume) {
        this.m_volume = volume;
    }

    public float getBrightnessVal() {
        return m_brightnessVal;
    }

    public void setBrightnessVal(float brightnessVal) {
        this.m_brightnessVal = brightnessVal;
    }

    public float getContrastVal() {
        return m_contrastVal;
    }

    public void setContrastVal(float contrastVal) {
        this.m_contrastVal = contrastVal;
    }

    public void setDenoiseDensity(float denoiseDensity) {
        this.denoiseDensity = denoiseDensity;
    }

    public float getDenoiseDensity() {
        return denoiseDensity;
    }

    public float getSaturationVal() {
        return m_saturationVal;
    }

    public void setSaturationVal(float saturationVal) {
        this.m_saturationVal = saturationVal;
    }

    public float getVignetteVal() {
        return m_vignetteVal;
    }

    public void setVignetteVal(float vignetteVal) {
        this.m_vignetteVal = vignetteVal;
    }

    public float getSharpenVal() {
        return m_sharpenVal;
    }

    public void setSharpenVal(float sharpenVal) {
        this.m_sharpenVal = sharpenVal;
    }

    public double getStartSpeed() {
        return m_start_speed;
    }

    public void setStartSpeed(double startSpeed) {
        this.m_start_speed = startSpeed;
    }

    public double getEndSpeed() {
        return m_end_speed;
    }

    public void setEndSpeed(double endSpeed) {
        this.m_end_speed = endSpeed;
    }

    public ClipInfo() {
        m_filePath = null;
        m_speed = 1.0f;
        m_mute = false;
        m_trimIn = -1;
        m_trimOut = -1;
        m_brightnessVal = 0.0f;
        m_contrastVal = 0.0f;
        m_saturationVal = 0.0f;
        m_sharpenVal = 0;
        m_vignetteVal = 0;
        density = 0.0f;
        m_volume = VIDEOVOLUME_DEFAULTVALUE;
        m_rotateAngle = 0;
        m_scaleX = -2;//
        m_scaleY = -2;
        m_pan = 0.0f;
        m_scan = 0.0f;
        keepAudioPitch = true;
    }


    public void setFilePath(String filePath) {
        m_filePath = filePath;
    }

    public String getFilePath() {
        return m_filePath;
    }

    public void setSpeed(float speed) {
        m_speed = speed;
    }

    public float getSpeed() {
        return m_speed;
    }

    public void setKeepAudioPitch(boolean keepAudioPitch) {
        this.keepAudioPitch = keepAudioPitch;
    }

    public boolean isKeepAudioPitch() {
        return keepAudioPitch;
    }

    public void setmCurveSpeed(ChangeSpeedCurveInfo mCurveSpeed) {
        this.mCurveSpeed = mCurveSpeed;
    }

    public ChangeSpeedCurveInfo getmCurveSpeed() {
        return mCurveSpeed;
    }

    public void setMute(boolean flag) {
        m_mute = flag;
    }

    public boolean getMute() {
        return m_mute;
    }

    public void changeTrimIn(long data) {
        m_trimIn = data;
    }

    public long getTrimIn() {
        return m_trimIn;
    }

    public void changeTrimOut(long data) {
        m_trimOut = data;
    }

    public long getTrimOut() {
        return m_trimOut;
    }

    public VideoClipFxInfo getVideoClipFxInfo() {
        return m_videoClipFxInfo;
    }

    public void setVideoClipFxInfo(VideoClipFxInfo videoClipFxInfo) {
        this.m_videoClipFxInfo = videoClipFxInfo;
    }

    public String getFxStoryBoardFileName() {
        return m_fxStoryBoardFileName;
    }

    public void setFxStoryBoardFileName(String storyBoardFileName) {
        this.m_fxStoryBoardFileName = storyBoardFileName;
    }

    public String getClipStoryBoardFileName() {
        return m_clipStoryBoardFileName;
    }

    public void setClipStoryBoardFileName(String storyBoardFileName) {
        this.m_clipStoryBoardFileName = storyBoardFileName;
    }

    public String getMinBlurRectAspectRatio() {
        return m_minBlurRectAspectRatio;
    }

    public void setMinBlurRectAspectRatio(String blurRectAspectRatio) {
        this.m_minBlurRectAspectRatio = blurRectAspectRatio;
    }

    public String getMaxBlurRectAspectRatio() {
        return m_maxBlurRectAspectRatio;
    }

    public void setMaxBlurRectAspectRatio(String blurRectAspectRatio) {
        this.m_maxBlurRectAspectRatio = blurRectAspectRatio;
    }

    public int getIsBlurInFront() {
        return m_isBlurInFront;
    }

    public void setIsBlurInFront(int isBlurInFront) {
        this.m_isBlurInFront = isBlurInFront;
    }

    public String getBackgroundValue() {
        return mBackgroundValue;
    }

    public void setBackgroundValue(String colorValue) {
        this.mBackgroundValue = colorValue;
    }

    public float getmHighLight() {
        return mHighLight;
    }

    public void setmHighLight(float mHighLight) {
        this.mHighLight = mHighLight;
    }

    public float getmShadow() {
        return mShadow;
    }

    public void setmShadow(float mShadow) {
        this.mShadow = mShadow;
    }

    public float getFade() {
        return fade;
    }

    public void setFade(float fade) {
        this.fade = fade;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getTint() {
        return tint;
    }

    public void setTint(float tint) {
        this.tint = tint;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public ClipInfo clone() {
        ClipInfo newClipInfo = new ClipInfo();
        newClipInfo.isRecFile = this.isRecFile;
        newClipInfo.rotation = this.rotation;
        newClipInfo.setFilePath(this.getFilePath());
        newClipInfo.setMute(this.getMute());
        newClipInfo.setSpeed(this.getSpeed());
        newClipInfo.setKeepAudioPitch(this.isKeepAudioPitch());
        newClipInfo.setmCurveSpeed(this.getmCurveSpeed());
        newClipInfo.changeTrimIn(this.getTrimIn());
        newClipInfo.changeTrimOut(this.getTrimOut());

        //copy data
        newClipInfo.setBrightnessVal(this.getBrightnessVal());
        newClipInfo.setSaturationVal(this.getSaturationVal());
        newClipInfo.setContrastVal(this.getContrastVal());
        newClipInfo.setVignetteVal(this.getVignetteVal());
        newClipInfo.setSharpenVal(this.getSharpenVal());
        newClipInfo.setmHighLight(this.getmHighLight());
        newClipInfo.setmShadow(this.getmShadow());
        newClipInfo.setTemperature(this.getTemperature());
        newClipInfo.setTint(this.getTint());
        newClipInfo.setFade(this.getFade());
        newClipInfo.setDensity(this.getDensity());
        newClipInfo.setDenoiseDensity(this.getDenoiseDensity());
        //
        newClipInfo.setVolume(this.getVolume());
        newClipInfo.setRotateAngle(this.getRotateAngle());
        newClipInfo.setScaleX(this.getScaleX());
        newClipInfo.setScaleY(this.getScaleY());
        newClipInfo.setVideoClipFxInfo(this.getVideoClipFxInfo());

        /*
         * 图片数据
         * Picture data
         * */
        newClipInfo.setImgDispalyMode(this.getImgDispalyMode());
        newClipInfo.setOpenPhotoMove(this.isOpenPhotoMove());
        newClipInfo.setNormalStartROI(this.getNormalStartROI());
        newClipInfo.setNormalEndROI(this.getNormalEndROI());

        /*
         * 视频横向裁剪，纵向平移
         * Video cropped horizontally, panned vertically
         * */
        newClipInfo.setPan(this.getPan());
        newClipInfo.setScan(this.getScan());

        /*
         * 视屏片段
         * Video clip
         * */
        newClipInfo.setVideoClipFxInfo(this.getVideoClipFxInfo());

        /*
         * 调整裁剪
         */
        newClipInfo.setStoryboardInfos(this.getStoryboardInfos());

        return newClipInfo;
    }

    public void removeBackground(NvsVideoClip videoClip) {
        if (mStoryboardInfos.isEmpty()) {
            return;
        }
        StoryboardInfo backgroundInfo = mStoryboardInfos.get(StoryboardInfo.SUB_TYPE_BACKGROUND);
        if (backgroundInfo == null) {
            return;
        }
        if (videoClip == null) {
            return;
        }

        NvsVideoFx nvsVideoFx = backgroundInfo.getStoryboardFx(videoClip, backgroundInfo.getSubType());
        if (nvsVideoFx == null) {
            return;
        }
        videoClip.removeFx(nvsVideoFx.getIndex());
        mStoryboardInfos.remove(StoryboardInfo.SUB_TYPE_BACKGROUND);

    }
}

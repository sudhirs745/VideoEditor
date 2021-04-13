package com.glitchcam.vepromei.mimodemo.common.utils;

import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;
import com.glitchcam.vepromei.mimodemo.bean.MiMoLocalData;
import com.glitchcam.vepromei.mimodemo.common.Constants;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.CaptionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.ClipInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.CompoundCaptionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.MusicInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.RecordAudioInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.StickerInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TimelineData;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.TransitionInfo;
import com.glitchcam.vepromei.mimodemo.common.dataInfo.VideoClipFxInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.FxClipInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotDataInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.ShotVideoInfo;
import com.glitchcam.vepromei.mimodemo.common.template.model.TrackClipInfo;
import com.glitchcam.vepromei.mimodemo.common.template.utils.NvTemplateContext;
import com.glitchcam.vepromei.mimodemo.common.template.utils.TemplateFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/5/29.
 */

public class TimelineUtil {
    private static String TAG = "TimelineUtil";
    private static final float FLOAT_EPSINON = 0.000001f;

    private static boolean isFloatEqual(float speed0,float speed1){
        float result = speed0 - speed1;
        if(result >= -FLOAT_EPSINON && result<= FLOAT_EPSINON){
            return true;
        }
        return false;
    }
    //主编辑页面时间线API
    public static NvsTimeline createTimeline(){
        ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();//原始数据
        if (clipInfoData == null || clipInfoData.isEmpty()) {
            return null;
        }
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if(!buildVideoTrack(timeline)) {
            return timeline;
        }

        timeline.appendAudioTrack(); // 音乐轨道
        //timeline.appendAudioTrack(); // 录音轨道

        setTimelineData(timeline);

        return timeline;
    }

    public static String getSupportedAspectRatio() {
        MiMoLocalData template = NvTemplateContext.getInstance().getSelectedMimoData();
        return template == null ? null : template.getSupportedAspectRatio();
    }

    //主编辑页面时间线API
    public static NvsTimeline createTimeline(String clipPath){
        NvsTimeline timeline = newTimeline(CommonUtil.getVideoEditResolution(Constants.AspectRatio.AspectRatio_16v9));
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if(!buildSingleClipVideoTrackExt(timeline, clipPath)) {
            return timeline;
        }
        return timeline;
    }

    //通过myStory模板重建时间线
    public static void rebuildTimelineByTemplate(NvsTimeline timeline){
        if (timeline == null){
            return;
        }

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            return ;
        }

        context.clearCachedResources(false);

        //删除所有添加的时间线素材，包括贴纸，字幕，时间线特效，组合字幕
        removeAllTimelineAsset(timeline);
        //移除视频轨道
        removeVideoTrack(timeline);
        //移除音频轨道
        removeAudioTrack(timeline);
        MiMoLocalData selectTemplate = NvTemplateContext.getInstance().getSelectedMimoData();
        if (selectTemplate == null){
            return;
        }
        addShotVideo(timeline,selectTemplate);
    }

    private static void removeAllTimelineAsset(NvsTimeline timeline){
        if (timeline == null){
            return;
        }
        NvsTimelineAnimatedSticker animatedSticker = timeline.getFirstAnimatedSticker();
        while (animatedSticker != null){
            animatedSticker = timeline.removeAnimatedSticker(animatedSticker);
        }
        NvsTimelineVideoFx videoFx = timeline.getFirstTimelineVideoFx();
        while (videoFx != null){
            videoFx = timeline.removeTimelineVideoFx(videoFx);
        }
        NvsTimelineCaption caption = timeline.getFirstCaption();
        while (caption != null){
            caption = timeline.removeCaption(caption);
        }
        NvsTimelineCompoundCaption compoundCaption = timeline.getFirstCompoundCaption();
        while (compoundCaption != null){
            compoundCaption = timeline.removeCompoundCaption(compoundCaption);
        }
    }
    //添加镜头视频
    private static void addShotVideo(NvsTimeline timeline,MiMoLocalData selectTemplate){
        List<ShotDataInfo> shotDataInfos = selectTemplate.getShotDataInfos();
        if (shotDataInfos == null || shotDataInfos.isEmpty()){
            return;
        }
        NvsVideoTrack mainVideoTrack = timeline.getVideoTrackByIndex(0);
        if (mainVideoTrack == null){
            mainVideoTrack = timeline.appendVideoTrack();
            mainVideoTrack.setVolumeGain(0f,0f);
        }
        List<TrackClipInfo> totalClipInfos = new ArrayList<>();
        List<TrackClipInfo> totalSubClipInfos = new ArrayList<>();
        int trackClipCount = shotDataInfos.size();
        for (int index = 0;index < trackClipCount;index++){
            ShotDataInfo shotDataInfo = shotDataInfos.get(index);
            if (shotDataInfo == null){
                continue;
            }
            ShotVideoInfo mainTrackVideoInfo = shotDataInfo.getMainTrackVideoInfo();
            if (mainTrackVideoInfo == null){
                continue;
            }
            List<TrackClipInfo> mainTrackClipInfos = mainTrackVideoInfo.getTrackClipInfos();
            if (mainTrackClipInfos == null || mainTrackClipInfos.isEmpty()){
                continue;
            }
            totalClipInfos.addAll(mainTrackClipInfos);
            //计算时长比例值
            float durationRatio = getDurationRatio(mainTrackVideoInfo);
            int shot = mainTrackVideoInfo.getShot();
            String videoFilePath = mainTrackVideoInfo.getVideoClipPath();
            String converFilePath = mainTrackVideoInfo.getConverClipPath();
            int clipCount = mainTrackClipInfos.size();
            //添加主轨道片段
            long inPoint = 0;
            for (int clipIndex = 0;clipIndex < clipCount;clipIndex++){
                TrackClipInfo trackClipInfo = mainTrackClipInfos.get(clipIndex);
                if (trackClipInfo == null){
                    continue;
                }
                trackClipInfo.setDurationRatio(durationRatio);
                boolean isReverse = trackClipInfo.isReverse();
                String clipFilePath = videoFilePath;
                if (!TextUtils.isEmpty(converFilePath) && isReverse){
                    clipFilePath = converFilePath;
                }
                long clipInPoint = appendVideoClip(mainVideoTrack,
                        clipFilePath,
                        shot,
                        trackClipInfo,
                        0);
                if (clipIndex == 0){//记录当前镜头主轨道第一个片段的inPoint值
                    inPoint = clipInPoint;
                }
            }

            //添加子轨道片段
            List<ShotVideoInfo> subTrackVideoInfos = shotDataInfo.getSubTrackVideoInfos();
            if (subTrackVideoInfos == null || subTrackVideoInfos.isEmpty()){
                continue;
            }

            int subTrackCount = subTrackVideoInfos.size();
            for (int subTrackIndex = 0;subTrackIndex < subTrackCount;subTrackIndex++){
                ShotVideoInfo subShotVideoInfo = subTrackVideoInfos.get(subTrackIndex);
                if (subShotVideoInfo == null){
                    continue;
                }
                int trackIndex = subShotVideoInfo.getTrackIndex();
                NvsVideoTrack subVideoTrack = timeline.getVideoTrackByIndex(trackIndex);
                if (subVideoTrack == null){
                    subVideoTrack = timeline.appendVideoTrack();
                    subVideoTrack.setVolumeGain(0f,0f);
                }
                List<TrackClipInfo> subTrackClips = subShotVideoInfo.getTrackClipInfos();
                if (subTrackClips != null && !subTrackClips.isEmpty()){
                    totalSubClipInfos.addAll(subTrackClips);
                }
                long subInPoint = (subTrackIndex == 0) ? inPoint : 0;
                addSubTrackClip(subVideoTrack,subShotVideoInfo,subInPoint);
            }
        }

        //添加音频
        appendAudioClip(timeline,selectTemplate);
        //设置转场
        setTrackTransition(mainVideoTrack,totalClipInfos);
        NvsVideoTrack subVideoTrack = timeline.getVideoTrackByIndex(1);
        if (subVideoTrack != null){
            setTrackTransition(subVideoTrack,totalSubClipInfos);
        }
        setTimelineData(timeline,selectTemplate);
    }

    private static void addSubTrackClip(NvsVideoTrack videoTrack,ShotVideoInfo shotVideoInfo,long inPoint){
        if (videoTrack == null){
            return;
        }
        if (shotVideoInfo == null){
            return;
        }
        List<TrackClipInfo> trackClipInfos = shotVideoInfo.getTrackClipInfos();
        if (trackClipInfos == null || trackClipInfos.isEmpty()){
            return;
        }
        //计算时长比例值
        float durationRatio = getDurationRatio(shotVideoInfo);
        int shot = shotVideoInfo.getShot();
        String videoFilePath = shotVideoInfo.getVideoClipPath();
        String converFilePath = shotVideoInfo.getConverClipPath();
        int clipCount = trackClipInfos.size();
        //添加子轨道片段
        for (int clipIndex = 0;clipIndex < clipCount;clipIndex++){
            TrackClipInfo trackClipInfo = trackClipInfos.get(clipIndex);
            if (trackClipInfo == null){
                continue;
            }
            trackClipInfo.setDurationRatio(durationRatio);
            boolean isReverse = trackClipInfo.isReverse();
            String clipFilePath = videoFilePath;
            if (!TextUtils.isEmpty(converFilePath) && isReverse){
                clipFilePath = converFilePath;
            }
            appendVideoClip(videoTrack,
                    clipFilePath,
                    shot,
                    trackClipInfo,
                    inPoint);
        }
    }

    private static float getDurationRatio(ShotVideoInfo shotVideoInfo){
        long fileDuration = shotVideoInfo.getFileDuration();
        long realNeedDuration = shotVideoInfo.getRealNeedDuration();
        //计算时长比例值
        float durationRatio = Constants.DEFAULT_DURATION_RATIO;
        if (fileDuration < realNeedDuration){
            durationRatio = fileDuration / (float)realNeedDuration;
        }
        return durationRatio;
    }

    private static void setTimelineData(NvsTimeline timeline,MiMoLocalData selectTemplate){
        if (timeline == null){
            return;
        }
        if (selectTemplate == null){
            return;
        }
        //添加片头滤镜
        String titleFilter = selectTemplate.getTitleFilter();
        long titleFilterDuration = TemplateFileUtils.millisecondToMicrosecond(selectTemplate.getTitleFilterDuration());
        addTimelineFilter(timeline,titleFilter,0,titleFilterDuration);
        //添加片头组合字幕
        String titleCaption = selectTemplate.getTitleCaption();
        long titleCaptionDuration = TemplateFileUtils.millisecondToMicrosecond(selectTemplate.getTitleCaptionDuration());
        addCompoundCaption(timeline,titleCaption,0,titleCaptionDuration);
        //添加全轨滤镜
        String timelineFilter = selectTemplate.getTimelineFilter();
        addTimelineFilter(timeline,timelineFilter,0,timeline.getDuration());
        //片尾压黑滤镜
        String endingFilter = selectTemplate.getEndingFilter();
        long endingFilterLen = TemplateFileUtils.millisecondToMicrosecond(selectTemplate.getEndingFilterLen());
        long endingFilterInPoint = timeline.getDuration() - endingFilterLen;
        addTimelineFilter(timeline,endingFilter,endingFilterInPoint,endingFilterLen);
        //添加片尾水印
        addTimeWaterMark(timeline);
    }
    //添加组合字幕
    private static void addCompoundCaption(NvsTimeline timeline,
                                           String titleCaption,
                                           long inPoint,
                                           long duration){
        if (timeline == null){
            return;
        }
        if (TextUtils.isEmpty(titleCaption)) {
            return;
        }
        timeline.addCompoundCaption(inPoint,duration,titleCaption);
    }
    //添加时间线滤镜
    private static void addTimelineFilter(NvsTimeline timeline,
                                          String timelineFilter,
                                          long inPoint,
                                          long duration){
        if (timeline == null){
            return;
        }
        if (TextUtils.isEmpty(timelineFilter)) {
            return;
        }
        boolean isBuiltInFx = isBuiltInFx(timelineFilter);
        if (isBuiltInFx){
            timeline.addBuiltinTimelineVideoFx(inPoint,duration,timelineFilter);
        }else {
            timeline.addPackagedTimelineVideoFx(inPoint,duration,timelineFilter);
        }
    }

    //添加音频
    private static void appendAudioClip(NvsTimeline timeline,MiMoLocalData selectTemplate){
        if (timeline == null){
            return;
        }
        if (selectTemplate == null){
            return;
        }
        String musicFilePath = selectTemplate.getMusicFilePath();
        if (TextUtils.isEmpty(musicFilePath)){
            return;
        }
        long musicDuration = TemplateFileUtils.millisecondToMicrosecond(selectTemplate.getMusicDuration());
        NvsAudioTrack mainAudioTrack = timeline.getAudioTrackByIndex(0);
        if (mainAudioTrack == null){
            mainAudioTrack = timeline.appendAudioTrack();
        }
        mainAudioTrack.appendClip(musicFilePath,0,musicDuration);
    }
    //设置转场
    private static void setTrackTransition(NvsVideoTrack videoTrack,List<TrackClipInfo> trackClipInfos){
        if (videoTrack == null){
            return;
        }
        if (trackClipInfos == null || trackClipInfos.isEmpty()){
            return;
        }
        int trackClipCount = videoTrack.getClipCount();
        int clipInfosCount = trackClipInfos.size();
        Log.d(TAG,"clipInfosCount = " + clipInfosCount + ",trackClipCount = " + trackClipCount);
        for (int index = 0;index < clipInfosCount;index++) {
            TrackClipInfo videoClipInfo = trackClipInfos.get(index);
            if (videoClipInfo == null) {
                continue;
            }
            //置空转场
            videoTrack.setBuiltinTransition(index,null);
            videoTrack.setPackagedTransition(index,null);
            String transitionName = videoClipInfo.getTrans();
            if (!TextUtils.isEmpty(transitionName)){
                //设置转场
                boolean isBuiltInTransition = isBuiltInTransition(transitionName);
                if (isBuiltInTransition){
                    videoTrack.setBuiltinTransition(index,transitionName);
                }else {
                    videoTrack.setPackagedTransition(index,transitionName);
                }
            }
        }

        //合成时间线转场
        int clipCount = videoTrack.getClipCount();
        for (int index = 0;index < clipCount;index++){
            NvsVideoTransition videoTransition = videoTrack.getTransitionBySourceClipIndex(index);
            if (videoTransition != null){
                videoTransition.enableTimelineTransition(true);
            }
        }
    }
    private static boolean isBuiltInTransition(String transitionName){
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            return false;
        }
        if (TextUtils.isEmpty(transitionName)){
            return false;
        }
        List<String> builtInNameList = context.getAllBuiltinVideoTransitionNames();
        if (builtInNameList == null || builtInNameList.isEmpty()){
            return false;
        }
        int fxCount = builtInNameList.size();
        for (int index = 0;index < fxCount;index++){
            String builtInName = builtInNameList.get(index);
            if (TextUtils.isEmpty(builtInName)){
                continue;
            }
            if (transitionName.equals(builtInName)){
                return true;
            }
        }
        return false;
    }
    private static boolean isBuiltInFx(String filterName){
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            return false;
        }
        if (TextUtils.isEmpty(filterName)){
            return false;
        }
        List<String> builtInNameList = context.getAllBuiltinVideoFxNames();
        if (builtInNameList == null || builtInNameList.isEmpty()){
            return false;
        }
        int fxCount = builtInNameList.size();
        for (int index = 0;index < fxCount;index++){
            String builtInName = builtInNameList.get(index);
            if (TextUtils.isEmpty(builtInName)){
                continue;
            }
            if (filterName.equals(builtInName)){
                return true;
            }
        }
        return false;
    }
    private static long appendVideoClip(NvsVideoTrack videoTrack,
                                        String videoFilePath,
                                        int shot,
                                        TrackClipInfo trackClipInfo,
                                        long inPoint){
        if (videoTrack == null){
            return 0;
        }
        if (trackClipInfo == null){
            return 0;
        }
        if (TextUtils.isEmpty(videoFilePath)){
            return 0;
        }
        long clipTrimIn = trackClipInfo.getTrimIn();
        long realNeedDuration = trackClipInfo.getRealNeedDuration();
        float durationRatio = trackClipInfo.getDurationRatio();
        if ((durationRatio > 0f) && (durationRatio < Constants.DEFAULT_DURATION_RATIO)){
            clipTrimIn = (long)(clipTrimIn * durationRatio);
            realNeedDuration = (long)(realNeedDuration * durationRatio);
        }
        long clipTrimOut = clipTrimIn + realNeedDuration;
        NvsVideoClip videoClip = (inPoint > 0)
                ? videoTrack.addClip(videoFilePath,inPoint,clipTrimIn,clipTrimOut)
                : videoTrack.appendClip(videoFilePath,clipTrimIn,clipTrimOut);
        if (videoClip == null){
            Log.d(TAG,"shot = " + shot + ",videoClipPath = " + videoFilePath);
            return 0;
        }
        int videoType = videoClip.getVideoType();
        if (videoType == NvsVideoClip.VIDEO_CLIP_TYPE_AV){
            //添加的片段是视频
            videoClip.setPanAndScan(0f,1f);
            //片段变速
            changeSpeed(videoClip,trackClipInfo);
        }

        String trackFilter = trackClipInfo.getTrackFilter();
        //添加片段滤镜,trackFilter与filter目前模板里面不会同时存在
        appendFilter(videoClip,trackFilter);
        String filter = trackClipInfo.getFilter();
        appendFilter(videoClip,filter);
        return videoClip.getInPoint();
    }

    private static void appendFilter(NvsVideoClip videoClip,String filterName){
        if (videoClip == null){
            return;
        }
        if (TextUtils.isEmpty(filterName)) {
            return;
        }
        boolean isBuiltInFx = isBuiltInFx(filterName);
        if (isBuiltInFx){
            videoClip.appendBuiltinFx(filterName);
        }else {
            videoClip.appendPackagedFx(filterName);
        }
    }
    //对视频片段作变速
    private static void changeSpeed(NvsVideoClip videoClip,TrackClipInfo trackClipInfo){
        if (videoClip == null){
            return;
        }
        if (trackClipInfo == null){
            return;
        }
        float speed0 = trackClipInfo.getSpeed0();
        if (speed0 <= 0){
            speed0 = Constants.DEFAULT_SPEED_VALUE;
        }
        float speed1 = trackClipInfo.getSpeed1();
        if (speed1 <= 0){
            speed1 = Constants.DEFAULT_SPEED_VALUE;
        }
        float durationRatio = trackClipInfo.getDurationRatio();
        if ((durationRatio > 0f) && (durationRatio < Constants.DEFAULT_DURATION_RATIO)){
            speed0 = durationRatio * speed0;
            speed1 = durationRatio * speed1;
        }
        boolean isEqual = isFloatEqual(speed0,speed1);
        if (isEqual){//判断速度是否相同
            isEqual = isFloatEqual(speed0,1f);
            if (isEqual){
                return;
            }
            videoClip.changeSpeed(speed0);
        }else {
            videoClip.changeVariableSpeed(speed0,speed1,true);
        }
    }
    private static void removeVideoTrack(NvsTimeline timeline){
        if (timeline == null){
            return;
        }
        int trackCount = timeline.videoTrackCount();
        if (trackCount == 0){
            return;
        }
        for (int index = trackCount - 1;index >= 0;--index){
            timeline.removeVideoTrack(index);
        }
    }
    private static void removeAudioTrack(NvsTimeline timeline){
        if (timeline == null){
            return;
        }
        int trackCount = timeline.audioTrackCount();
        if (trackCount == 0){
            return;
        }
        for (int index = trackCount - 1;index >= 0;--index){
            timeline.removeAudioTrack(index);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //片段编辑页面时间线API
    public static NvsTimeline createSingleClipTimeline(ClipInfo clipInfo, boolean isTrimClip){
        NvsTimeline timeline = newTimeline(CommonUtil.getVideoEditResolution(Constants.AspectRatio.AspectRatio_16v9));
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrack(timeline,clipInfo,isTrimClip);
        return timeline;
    }

    public static boolean buildSingleClipVideoTrack(NvsTimeline timeline, ClipInfo clipInfo, boolean isTrimClip) {
        if(timeline == null || clipInfo == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }
        addVideoClip(videoTrack,clipInfo,isTrimClip);
        return true;
    }
    public static boolean buildSingleClipVideoTrackExt(NvsTimeline timeline, String filePath) {
        if(timeline == null || filePath == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null){
            Log.e(TAG, "failed to append video clip");
            return false;
        }
        return true;
    }
    public static void setTimelineData(NvsTimeline timeline) {
        if(timeline == null)
            return;
        // 此处注意是clone一份音乐数据，因为添加主题的接口会把音乐数据删掉
        List<MusicInfo> musicInfoClone = TimelineData.instance().cloneMusicData();
        String themeId = TimelineData.instance().getThemeData();
        applyTheme(timeline,themeId);

        if(musicInfoClone != null) {
            TimelineData.instance().setMusicList(musicInfoClone);
            buildTimelineMusic(timeline, musicInfoClone);
        }

        addTimeLineFilter(timeline);
        buildClipFilter(timeline);
        buildClipTrans(timeline);

        ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
        setSticker(timeline, stickerArray);

        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        setCaption(timeline, captionArray);

        //compound caption
        ArrayList<CompoundCaptionInfo> compoundCaptionArray = TimelineData.instance().getCompoundCaptionArray();
        setCompoundCaption(timeline,compoundCaptionArray);

        ArrayList<RecordAudioInfo> recordArray = TimelineData.instance().getRecordAudioData();
        buildTimelineRecordAudio(timeline, recordArray);
    }

    private static void addTimeLineFilter(NvsTimeline timeline) {
        MiMoLocalData template = NvTemplateContext.getInstance().getSelectedMimoData();
        long endingFilterLen = (long)template.getEndingFilterLen() * Constants.US_TIME_BASE;
        timeline.addPackagedTimelineVideoFx(timeline.getDuration() - endingFilterLen, endingFilterLen, template.getEndingFilter());
        timeline.addPackagedTimelineVideoFx(0, timeline.getDuration(), template.getTimelineFilter());
    }

    private static void addTimeWaterMark(NvsTimeline timeline) {
        MiMoLocalData template = NvTemplateContext.getInstance().getSelectedMimoData();
        long endingFilterLen = (long)template.getEndingFilterLen() * Constants.US_TIME_BASE;
        timeline.addAnimatedSticker(timeline.getDuration() - endingFilterLen, endingFilterLen, template.getEndingWatermark());
    }

    public static boolean removeTimeline(NvsTimeline timeline){
        if(timeline == null)
            return false;

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null)
            return false;

        return context.removeTimeline(timeline);
    }

    public static boolean buildVideoTrack(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }

        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0;i < videoClipArray.size();i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack,clipInfo,true);
        }
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume,videoVolume);

        return true;
    }

    //重新创建单个clip的TimeLine
    public static NvsTimeline reBuildSingleVideoTrack(NvsTimeline timeline, String clipPath){
        if(timeline == null) {
            return null;
        }
        int videoTrackCount = timeline.videoTrackCount();
        NvsVideoTrack videoTrack = videoTrackCount == 0 ? timeline.appendVideoTrack() : timeline.getVideoTrackByIndex(0);
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return null;
        }
        videoTrack.removeAllClips();
        NvsVideoClip videoClip = videoTrack.appendClip(clipPath);
        if (videoClip != null){
            Log.e(TAG, "failed to append video clip");
            return timeline;
        }
        return null;
    }

    private static void addVideoClip(NvsVideoTrack videoTrack, ClipInfo clipInfo, boolean isTrimClip){
        if(videoTrack == null || clipInfo == null)
            return;
        String filePath = clipInfo.getFilePath();
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return;
        }
        //videoClip.setSourceBackgroundMode(NvsVideoClip.ClIP_BACKGROUNDMODE_BLUR);//会造成第一次播放视频断断续续，需要注释掉
        float brightVal = clipInfo.getBrightnessVal();
        float contrastVal = clipInfo.getContrastVal();
        float saturationVal = clipInfo.getSaturationVal();
        float vignette = clipInfo.getVignetteVal();
        float sharpen = clipInfo.getSharpenVal();
        if(brightVal >= 0 || contrastVal >= 0 || saturationVal >= 0){
            NvsVideoFx videoFxColor = videoClip.appendBuiltinFx(Constants.FX_COLOR_PROPERTY);
            if(videoFxColor != null){
                if(brightVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS,brightVal);
                if(contrastVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST,contrastVal);
                if(saturationVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION,saturationVal);
            }
        }
        if(vignette >= 0) {
            NvsVideoFx vignetteVideoFx = videoClip.appendBuiltinFx(Constants.FX_VIGNETTE);
            vignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, vignette);
        }
        if(sharpen >= 0) {
            NvsVideoFx sharpenVideoFx = videoClip.appendBuiltinFx(Constants.FX_SHARPEN);
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, sharpen);
        }
        int videoType = videoClip.getVideoType();
        if(videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE){//当前片段是图片
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if(trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if(trimOut > 0 && trimOut > trimIn ) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
            int imgDisplayMode = clipInfo.getImgDispalyMode();
            if(imgDisplayMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY){//区域显示
                videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                RectF normalStartRectF = clipInfo.getNormalStartROI();
                RectF normalEndRectF = clipInfo.getNormalEndROI();
                if(normalStartRectF != null && normalEndRectF != null){
                    videoClip.setImageMotionROI(normalStartRectF,normalEndRectF);
                }
            }else {//全图显示
                videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
            }

            boolean isOpenMove = clipInfo.isOpenPhotoMove();
            videoClip.setImageMotionAnimationEnabled(isOpenMove);
            Log.e(TAG,"addVideoClip -> StartSpeed = "+ clipInfo.getStartSpeed() + " , EndSpeed = "+clipInfo.getEndSpeed()+ " , trimIn = "+ trimIn + " , trimOut = "+trimOut);
            Log.e(TAG,"addVideoClip -> duration  = "+ (trimOut - trimIn) / (clipInfo.getStartSpeed() + clipInfo.getEndSpeed()) * 2);
        } else {//当前片段是视频
            float volumeGain = clipInfo.getVolume();
            videoClip.setVolumeGain(volumeGain,volumeGain);
            float pan = clipInfo.getPan();
            float scan = 1;
            videoClip.setPanAndScan(pan,scan);
            videoClip.setExtraVideoRotation(clipInfo.getRotateAngle());
            int scaleX = clipInfo.getScaleX();
            int scaleY = clipInfo.getScaleY();
            if(scaleX >= -1 || scaleY >= -1){
                NvsVideoFx videoFxTransform = videoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                if(videoFxTransform != null){
                    if(scaleX >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X,scaleX);
                    if(scaleY >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y,scaleY);
                }
            }

            if(!isTrimClip)//如果当前是裁剪页面，不裁剪片段
                return;
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            Log.e(TAG,"addVideoClip -> StartSpeed = "+ clipInfo.getStartSpeed() + " , EndSpeed = "+clipInfo.getEndSpeed()+ " , trimIn = "+ trimIn + " , trimOut = "+trimOut);
            Log.e(TAG,"addVideoClip -> duration  = "+ (trimOut - trimIn) / (clipInfo.getStartSpeed() + clipInfo.getEndSpeed()) * 2);
            if(trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if(trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
        }
        videoClip.changeVariableSpeed(clipInfo.getStartSpeed(), clipInfo.getEndSpeed(), true);
    }

    public static boolean buildClipFilter(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            return false;
        }
        boolean result = true;
        int videoClipCount = videoTrack.getClipCount();
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        for(int i = 0;i<videoClipCount;i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if(clip == null)
                continue;
            if (clipInfos.size() - 1 < i) {
                return false;
            }
            ClipInfo clipInfo = clipInfos.get(i);
            if (clipInfo == null) {
                continue;
            }
            if (!(clipInfo instanceof FxClipInfo)) {
                return false;
            }
            VideoClipFxInfo videoClipFxInfo = ((FxClipInfo) clipInfo).getVideoClipFxInfo();
            if (videoClipFxInfo == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = null;
            if (videoClipFxInfo.getFxMode() == VideoClipFxInfo.FXMODE_BUILTIN) {
                clip.appendBuiltinFx(videoClipFxInfo.getFxId());
            } else {
                nvsVideoFx = clip.appendPackagedFx(videoClipFxInfo.getFxId());
            }
            if (nvsVideoFx != null) {
                nvsVideoFx.setFilterIntensity(1.0F);
            } else {
                result = false;
            }
        }
        return result;
    }

    public static boolean buildClipTrans(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            return false;
        }

        boolean result = true;
        int videoClipCount = videoTrack.getClipCount();
        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();
        for(int i = 0;i<videoClipCount;i++) {
            if (clipInfos.size() - 1 < i) {
                return false;
            }
            ClipInfo clipInfo = clipInfos.get(i);
            if (clipInfo == null) {
                continue;
            }
            if (!(clipInfo instanceof FxClipInfo)) {
                return false;
            }
            TransitionInfo transitionInfo = ((FxClipInfo) clipInfo).getTransitionInfo();
            if (transitionInfo == null) {
                continue;
            }
            NvsVideoFx nvsVideoFx = null;
            if (transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                videoTrack.setBuiltinTransition(i, transitionInfo.getTransitionId());
            } else {
                videoTrack.setPackagedTransition(i, transitionInfo.getTransitionId());
            }
            if (nvsVideoFx == null) {
                result = false;
            }
        }
        return result;
    }

    public static boolean applyTheme(NvsTimeline timeline, String themeId) {
        if(timeline == null)
            return false;

        timeline.removeCurrentTheme();
        if (themeId == null || themeId.isEmpty())
            return false;

        //设置主题片头和片尾
        String themeCaptionTitle = TimelineData.instance().getThemeCptionTitle();
        if(!themeCaptionTitle.isEmpty()){
            timeline.setThemeTitleCaptionText(themeCaptionTitle);
        }
        String themeCaptionTrailer = TimelineData.instance().getThemeCptionTrailer();
        if(!themeCaptionTrailer.isEmpty()){
            timeline.setThemeTrailerCaptionText(themeCaptionTrailer);
        }

        if(!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        // 应用主题之后，要把已经应用的背景音乐去掉
        TimelineData.instance().setMusicList(null);
        TimelineUtil.buildTimelineMusic(timeline, null);
        return true;
    }

    public static boolean buildTimelineMusic(NvsTimeline timeline, List<MusicInfo> musicInfos) {
        if(timeline == null) {
            return false;
        }
        NvsAudioTrack audioTrack =timeline.getAudioTrackByIndex(0);
        if(audioTrack == null) {
            return false;
        }
        if(musicInfos == null || musicInfos.isEmpty()) {
            audioTrack.removeAllClips();

            // 去掉音乐之后，要把已经应用的主题中的音乐还原
            String pre_theme_id = TimelineData.instance().getThemeData();
            if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
                timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
            return false;
        }
        audioTrack.removeAllClips();
        for(MusicInfo oneMusic: musicInfos) {
            if(oneMusic == null) {
                continue;
            }
            NvsAudioClip audioClip = audioTrack.addClip(oneMusic.getFilePath(), oneMusic.getInPoint(), oneMusic.getTrimIn(), oneMusic.getTrimOut());
            if(audioClip != null) {
                audioClip.setFadeInDuration(oneMusic.getFadeDuration());
                if(oneMusic.getExtraMusic() <= 0 && oneMusic.getExtraMusicLeft() <= 0) {
                    audioClip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
            if(oneMusic.getExtraMusic() > 0) {
                for(int i = 0; i < oneMusic.getExtraMusic(); ++i) {
                    NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                            oneMusic.getOriginalOutPoint() + i * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                            oneMusic.getOriginalTrimIn(), oneMusic.getOriginalTrimOut());
                    if(extra_clip != null) {
                        extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                        if(i == oneMusic.getExtraMusic() - 1 && oneMusic.getExtraMusicLeft() <= 0) {
                            extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                            extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                        }
                    }
                }
            }
            if(oneMusic.getExtraMusicLeft() > 0) {
                NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                        oneMusic.getOriginalOutPoint() + oneMusic.getExtraMusic() * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                        oneMusic.getOriginalTrimIn(),
                        oneMusic.getOriginalTrimIn() + oneMusic.getExtraMusicLeft());
                if(extra_clip != null) {
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
        }
        float audioVolume = TimelineData.instance().getMusicVolume();
        audioTrack.setVolumeGain(audioVolume,audioVolume);

        // 应用音乐之后，要把已经应用的主题中的音乐去掉
        String pre_theme_id = TimelineData.instance().getThemeData();
        if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
            timeline.setThemeMusicVolumeGain(0 , 0);
        }
        return true;
    }

    public static void buildTimelineRecordAudio(NvsTimeline timeline, ArrayList<RecordAudioInfo> recordAudioInfos) {
        if(timeline == null) {
            return;
        }
        NvsAudioTrack audioTrack =timeline.getAudioTrackByIndex(1);
        if(audioTrack != null) {
            audioTrack.removeAllClips();
            if(recordAudioInfos != null) {
                for (int i = 0; i < recordAudioInfos.size(); ++i) {
                    RecordAudioInfo recordAudioInfo = recordAudioInfos.get(i);
                    if (recordAudioInfo == null) {
                        continue;
                    }
                    NvsAudioClip audioClip = audioTrack.addClip(recordAudioInfo.getPath(), recordAudioInfo.getInPoint(), recordAudioInfo.getTrimIn(),
                            recordAudioInfo.getOutPoint() - recordAudioInfo.getInPoint() + recordAudioInfo.getTrimIn());
                    if(audioClip != null) {
                        audioClip.setVolumeGain(recordAudioInfo.getVolume(), recordAudioInfo.getVolume());
                        if(recordAudioInfo.getFxID() != null && !recordAudioInfo.getFxID().equals(Constants.NO_FX)) {
                            audioClip.appendFx(recordAudioInfo.getFxID());
                        }
                    }
                }
            }
            float audioVolume = TimelineData.instance().getRecordVolume();
            audioTrack.setVolumeGain(audioVolume,audioVolume);
        }
    }

    public static boolean setSticker(NvsTimeline timeline, ArrayList<StickerInfo> stickerArray) {
        if(timeline == null)
            return false;

        NvsTimelineAnimatedSticker deleteSticker = timeline.getFirstAnimatedSticker();
        while (deleteSticker != null) {
            deleteSticker = timeline.removeAnimatedSticker(deleteSticker);
        }

        for(StickerInfo sticker : stickerArray) {
            long inPoint = timeline.getDuration() - sticker.getDuration();
            boolean isCutsomSticker = sticker.isCustomSticker();
            NvsTimelineAnimatedSticker newSticker = isCutsomSticker ?
                    timeline.addCustomAnimatedSticker(sticker.getInPoint(),sticker.getDuration(),sticker.getId(),sticker.getCustomImagePath())
                    : timeline.addAnimatedSticker(inPoint, sticker.getDuration(), sticker.getId());
            if(newSticker == null)
                continue;
            newSticker.setZValue(sticker.getAnimateStickerZVal());
            newSticker.setHorizontalFlip(sticker.isHorizFlip());
            PointF translation = sticker.getTranslation();
            float scaleFactor = sticker.getScaleFactor();
            float rotation = sticker.getRotation();
            newSticker.setScale(scaleFactor);
            newSticker.setRotationZ(rotation);
            newSticker.setTranslation(translation);
            float volumeGain = sticker.getVolumeGain();
            newSticker.setVolumeGain(volumeGain,volumeGain);
        }
        return true;
    }

    public static boolean setCaption(NvsTimeline timeline, ArrayList<CaptionInfo> captionArray) {
        if(timeline == null)
            return false;

        NvsTimelineCaption deleteCaption = timeline.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            Logger.e(TAG,"capCategory = " + capCategory);
            int roleTheme = deleteCaption.getRoleInTheme();
            if(capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL){//主题字幕不作删除
                deleteCaption = timeline.getNextCaption(deleteCaption);
            }else {
                deleteCaption = timeline.removeCaption(deleteCaption);
            }
        }

        for(CaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            NvsTimelineCaption newCaption = timeline.addCaption(caption.getText(), caption.getInPoint(),
                    duration,null);
            updateCaptionAttribute(newCaption,caption);
        }
        return true;
    }

    //add compound caption
    public static boolean setCompoundCaption(NvsTimeline timeline, ArrayList<CompoundCaptionInfo> captionArray) {
        if(timeline == null || captionArray == null || captionArray.isEmpty())
            return false;

        NvsTimelineCompoundCaption deleteCaption = timeline.getFirstCompoundCaption();
        while (deleteCaption != null) {
            deleteCaption = timeline.removeCompoundCaption(deleteCaption);
        }
        ArrayList<CompoundCaptionInfo> tempData = new ArrayList<>();
        for (int index = 0; index < captionArray.size(); index++) {
            CompoundCaptionInfo caption = captionArray.get(index);
            long duration = caption.getOutPoint() - caption.getInPoint();
            if (TextUtils.isEmpty(caption.getCaptionStyleUuid())) {
                continue;
            }
            NvsTimelineCompoundCaption newCaption = timeline.addCompoundCaption(caption.getInPoint(),
                    duration,caption.getCaptionStyleUuid());
            Logger.d(TAG,"setCompoundCaption index = "+index+" ,newCaption = "+newCaption);
            CompoundCaptionInfo compoundCaptionInfo = CaptionUtil.saveCompoundCaptionData(newCaption, caption);
            if (compoundCaptionInfo != null) {
                compoundCaptionInfo.setCaptionZVal(index);
                newCaption.setZValue(index);
                tempData.add(compoundCaptionInfo);
                updateCompoundCaptionAttribute(newCaption,compoundCaptionInfo);
            }
        }
        TimelineData.instance().setCompoundCaptionArray(tempData);//更新数据
        return true;
    }

    //update compound caption attribute
    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, CompoundCaptionInfo caption){
        if(newCaption == null || caption == null)
            return;

        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttrList = caption.getCaptionAttributeList();
        if (captionAttrList == null || captionAttrList.isEmpty()) {
            return;
        }
        int captionCount = newCaption.getCaptionCount();
        for (int index = 0; index < captionCount; index ++) {
            CompoundCaptionInfo.CompoundCaptionAttr captionAttr = captionAttrList.get(index);
            if(captionAttr == null){
                continue;
            }
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionColor());
            if (textColor != null) {
                newCaption.setTextColor(index, textColor);
            }

            String fontName = captionAttr.getCaptionFontName();
            if (!TextUtils.isEmpty(fontName)) {
                newCaption.setFontFamily(index, fontName);
            }
            String captionText = captionAttr.getCaptionText();
            if(!TextUtils.isEmpty(captionText)){
                newCaption.setText(index, captionText);
            }
        }

        // 放缩字幕
        float scaleFactorX = caption.getScaleFactorX();
        float scaleFactorY = caption.getScaleFactorY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);
        float rotation = caption.getRotation();
        // 旋转字幕
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(caption.getCaptionZVal());
        PointF translation = caption.getTranslation();
        if(translation != null){
            newCaption.setCaptionTranslation(translation);
        }
    }

    private static void updateCaptionAttribute(NvsTimelineCaption newCaption, CaptionInfo caption){
        if(newCaption == null) {
            return;
        }
        if(caption == null) {
            return;
        }

        //字幕StyleUuid需要首先设置，后面设置的字幕属性才会生效，
        // 因为字幕样式里面可能自带偏移，缩放，旋转等属性，最后设置会覆盖前面的设置的。
        String styleUuid = caption.getCaptionStyleUuid();
        newCaption.applyCaptionStyle(styleUuid);

        int alignVal = caption.getAlignVal();
        if(alignVal >= 0) {
            newCaption.setTextAlignment(alignVal);
        }
        int userColorFlag = caption.getUsedColorFlag();
        if(userColorFlag == CaptionInfo.ATTRIBUTE_USED_FLAG){
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionColor());
            if(textColor != null){
                textColor.a = caption.getCaptionColorAlpha() / 100.0f;
                newCaption.setTextColor(textColor);
            }
        }

        int usedScaleFlag = caption.getUsedScaleRotationFlag();
        if(usedScaleFlag == CaptionInfo.ATTRIBUTE_USED_FLAG){
            // 放缩字幕
            float scaleFactorX = caption.getScaleFactorX();
            float scaleFactorY = caption.getScaleFactorY();
            newCaption.setScaleX(scaleFactorX);
            newCaption.setScaleY(scaleFactorY);
            float rotation = caption.getRotation();
            // 旋转字幕
            newCaption.setRotationZ(rotation);
        }

        newCaption.setZValue(caption.getCaptionZVal());
        int usedOutlineFlag = caption.getUsedOutlineFlag();
        if(usedOutlineFlag == CaptionInfo.ATTRIBUTE_USED_FLAG){
            boolean hasOutline = caption.isHasOutline();
            newCaption.setDrawOutline(hasOutline);
            if(hasOutline){
                NvsColor outlineColor = ColorUtil.colorStringtoNvsColor(caption.getOutlineColor());
                if(outlineColor != null){
                    outlineColor.a = caption.getOutlineColorAlpha() / 100.0f;
                    newCaption.setOutlineColor(outlineColor);
                    newCaption.setOutlineWidth(caption.getOutlineWidth());
                }
            }
        }

        String fontPath = caption.getCaptionFont();
        if(!fontPath.isEmpty()) {
            newCaption.setFontByFilePath(fontPath);
        }

        int usedBold = caption.getUsedIsBoldFlag();
        if(usedBold == CaptionInfo.ATTRIBUTE_USED_FLAG){
            boolean isBold = caption.isBold();
            newCaption.setBold(isBold);
        }

        int usedItalic = caption.getUsedIsItalicFlag();
        if(usedItalic == CaptionInfo.ATTRIBUTE_USED_FLAG){
            boolean isItalic = caption.isItalic();
            newCaption.setItalic(isItalic);
        }
        int usedShadow = caption.getUsedShadowFlag();
        if(usedShadow == CaptionInfo.ATTRIBUTE_USED_FLAG){
            boolean isShadow = caption.isShadow();
            newCaption.setDrawShadow(isShadow);
            if(isShadow) {
                PointF offset = new PointF(7, -7);
                NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
                newCaption.setShadowOffset(offset);  //字幕阴影偏移量
                newCaption.setShadowColor(shadowColor); // 字幕阴影颜色
            }
        }
        float fontSize = caption.getCaptionSize();
        if (fontSize >= 0) {
            newCaption.setFontSize(fontSize);
        }
        int usedTranslationFlag = caption.getUsedTranslationFlag();
        if(usedTranslationFlag == CaptionInfo.ATTRIBUTE_USED_FLAG){
            PointF translation = caption.getTranslation();
            if(translation != null) {
                newCaption.setCaptionTranslation(translation);
            }
        }
    }

    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution){
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }

        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);
        return timeline;
    }
}

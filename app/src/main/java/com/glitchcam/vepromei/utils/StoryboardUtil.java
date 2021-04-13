package com.glitchcam.vepromei.utils;

import android.text.TextUtils;
import android.util.Log;

import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoClip;
import com.glitchcam.vepromei.bean.CutData;
import com.glitchcam.vepromei.utils.asset.NvAsset;
import com.glitchcam.vepromei.utils.dataInfo.ClipInfo;
import com.glitchcam.vepromei.utils.dataInfo.StoryboardInfo;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StoryboardUtil {
    private final static String TAG = "StoryboardUtil";
    public final static String STORYBOARD_KEY_SCALE_X = "scaleX";
    public final static String STORYBOARD_KEY_SCALE_Y = "scaleY";
    public final static String STORYBOARD_KEY_ROTATION_Z = "rotationZ";
    public final static String STORYBOARD_KEY_TRANS_X = "transX";
    public final static String STORYBOARD_KEY_TRANS_Y = "transY";

    /**
     * storyboard类型
     */
    public final static int STORYBOARD_BACKGROUND_TYPE_COLOR = 0;
    public final static int STORYBOARD_BACKGROUND_TYPE_IMAGE = 1;
    public final static int STORYBOARD_BACKGROUND_TYPE_BLUR = 2;

    public static String getImageBackgroundStory(String source, int timelineWidth, int timelineHeight, Map<String, Float> clipTransData) {
        int imageSize = timelineWidth;
        if (imageSize < timelineHeight) {
            imageSize = timelineHeight;
        }
        String story = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\"" + source + "\" width=\"" + imageSize + "\" height=\"" + imageSize + "\" " +
                "clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "</track>\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_X) + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_Y) + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + clipTransData.get(STORYBOARD_KEY_ROTATION_Z) + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData.get(STORYBOARD_KEY_TRANS_X) + "\"/>\n" +
                "<param name=\"transY\" value=\"" + clipTransData.get(STORYBOARD_KEY_TRANS_Y) + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>";
        return story;
    }

    public static String getBlurBackgroundStory(int timelineWidth, int timelineHeight, String clipPath, float strength,
                                                Map<String, Float> clipTransData) {

        NvsAVFileInfo avFileInfo = NvsStreamingContext.getInstance().getAVFileInfo(clipPath);
        int imageWidth = 0;
        int imageHeight = 0;
        if (avFileInfo != null) {
            NvsSize dimension = avFileInfo.getVideoStreamDimension(0);
            int streamRotation = avFileInfo.getVideoStreamRotation(0);
            imageWidth = dimension.width;
            imageHeight = dimension.height;
            if (streamRotation == 1 || streamRotation == 3) {
                imageWidth = dimension.height;
                imageHeight = dimension.width;
            }
        }
        Map<String, Float> blurTransData = getBlurTransData(timelineWidth, timelineHeight, imageWidth, imageHeight);
        String story = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"fastBlur\">\n" +
                "<param name=\"radius\" value=\"" + strength + "\"/>\n" +
                "</effect>\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + blurTransData.get(STORYBOARD_KEY_SCALE_X) + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + blurTransData.get(STORYBOARD_KEY_SCALE_Y) + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + blurTransData.get(STORYBOARD_KEY_ROTATION_Z) + "\"/>\n" +
                "<param name=\"transX\" value=\"" + blurTransData.get(STORYBOARD_KEY_TRANS_X) + "\"/>\n" +
                "<param name=\"transY\" value=\"" + blurTransData.get(STORYBOARD_KEY_TRANS_Y) + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_X) + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_Y) + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + clipTransData.get(STORYBOARD_KEY_ROTATION_Z) + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData.get(STORYBOARD_KEY_TRANS_X) + "\"/>\n" +
                "<param name=\"transY\" value=\"" + clipTransData.get(STORYBOARD_KEY_TRANS_Y) + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>";
        return story;
    }

    public static String getCropperStory(int timelineWidth, int timelineHeight, float[] regionData) {
        if (regionData == null || regionData.length < 8) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < regionData.length; i++) {
            stringBuilder.append(regionData[i]);
            if (i < regionData.length - 1) {
                stringBuilder.append(",");
            }
        }
        String regionString = stringBuilder.toString();
        String story = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\n" +
                "    <track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "        <effect name=\"maskGenerator\">\n" +
                "            <param name=\"keepRGB\" value=\"true\"/>\n" +
                "            <param name=\"featherWidth\" value=\"0\"/>\n" +
                "            <param name=\"region\" value=\"" + regionString + "\"/>\n" +
                "        </effect>\n" +
                "    </track>\n" +
                "</storyboard>";
        return story;
    }

    public static String getTransform2DStory(int timelineWidth, int timelineHeight, Map<String, Float> clipTransData) {
        String story = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<storyboard sceneWidth=\"" + timelineWidth + "\" sceneHeight=\"" + timelineHeight + "\">\t\n" +
                "<track source=\":1\" clipStart=\"0\" clipDuration=\"1\" repeat=\"true\">\n" +
                "<effect name=\"transform\">\n" +
                "<param name=\"scaleX\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_X) + "\"/>\n" +
                "<param name=\"scaleY\" value=\"" + clipTransData.get(STORYBOARD_KEY_SCALE_Y) + "\"/>\n" +
                "<param name=\"rotationZ\" value=\"" + (-clipTransData.get(STORYBOARD_KEY_ROTATION_Z)) + "\"/>\n" +
                "<param name=\"transX\" value=\"" + clipTransData.get(STORYBOARD_KEY_TRANS_X) + "\"/>\n" +
                "<param name=\"transY\" value=\"" + (-clipTransData.get(STORYBOARD_KEY_TRANS_Y)) + "\"/>\n" +
                "</effect>\n" +
                "</track>\n" +
                "</storyboard>";
        return story;
    }

    private static Map<String, Float> getBlurTransData(int timelineWidth, int timelineHeight, int width, int height) {
        Map<String, Float> transData = new HashMap<>();
        float timelineRatio = timelineWidth * 1.0F / timelineHeight;
        float fileRatio = width * 1.0F / height;
        float scale = 1.0F;
        if (fileRatio > timelineRatio) {//此时是宽对齐，需要高对齐
            float scaleBefore = timelineWidth * 1.0F / width;
            scale = timelineHeight * 1.0F / (height * scaleBefore);
        } else {//此时是高对齐，需要宽对齐
            float scaleBefore = timelineHeight * 1.0F / height;
            scale = timelineWidth * 1.0F / (width * scaleBefore);
        }
        transData.put(STORYBOARD_KEY_SCALE_X, scale);
        transData.put(STORYBOARD_KEY_SCALE_Y, scale);
        transData.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        transData.put(STORYBOARD_KEY_TRANS_X, 0F);
        transData.put(STORYBOARD_KEY_TRANS_Y, 0F);
        return transData;
    }

    public static float getBlurStrengthFromStory(String data) {
        Document document = getDocument(data);
        if (document == null) {
            return -1;
        }
        NodeList effect = document.getElementsByTagName("param");
        if (effect.getLength() == 0) {
            return -1;
        }
        for (int index = 0; index < effect.getLength(); index++) {
            Node item = effect.item(index);

            NamedNodeMap childNodeAttributes = item.getAttributes();
            if (childNodeAttributes == null) {
                continue;
            }
            if (childNodeAttributes.getNamedItem("name") != null && "radius".equals(childNodeAttributes.getNamedItem("name").getNodeValue())) {
                return Float.parseFloat(childNodeAttributes.getNamedItem("value").getNodeValue());
            }
        }
        return -1;
    }

    public static String getSourcePathFromStory(String data) {
        Document document = getDocument(data);
        if (document == null) {
            return null;
        }
        NodeList track = document.getElementsByTagName("track");
        if (track.getLength() == 0) {
            return null;
        }
        for (int index = 0; index < track.getLength(); index++) {
            Node item = track.item(index);
            NamedNodeMap attributes = item.getAttributes();
            Node name = attributes.getNamedItem("source");
            if (name == null || (":1".equals(name.getNodeValue()))) {
                continue;
            }
            return name.getNodeValue();
        }
        return null;
    }


    public static void setDefaultBackground(ClipInfo videoClip, NvsVideoClip nvsVideoClip,
                                            int timelineWidth, int timelineHeight) {
        StoryboardInfo backgroundInfo = new StoryboardInfo();
        Map<String, Float> clipTrans = new HashMap<>();
        clipTrans.put(STORYBOARD_KEY_SCALE_X, 1.0F);
        clipTrans.put(STORYBOARD_KEY_SCALE_Y, 1.0F);
        clipTrans.put(STORYBOARD_KEY_ROTATION_Z, 0F);
        clipTrans.put(STORYBOARD_KEY_TRANS_X, 0F);
        clipTrans.put(STORYBOARD_KEY_TRANS_Y, 0F);
        backgroundInfo.setClipTrans(clipTrans);

        backgroundInfo.setSource("nobackground.png");
        backgroundInfo.setSourceDir("assets:/background");
        String backgroundStory = StoryboardUtil.getImageBackgroundStory(backgroundInfo.getSource(), timelineWidth, timelineHeight, clipTrans);
        backgroundInfo.setStringVal("Resource Dir", backgroundInfo.getSourceDir());
        backgroundInfo.setBooleanVal("No Background", true);
        backgroundInfo.setStringVal("Description String", backgroundStory);
        backgroundInfo.setBackgroundType(STORYBOARD_BACKGROUND_TYPE_COLOR);
        backgroundInfo.bindToTimelineByType(nvsVideoClip, backgroundInfo.getSubType());
        videoClip.addStoryboardInfo(StoryboardInfo.SUB_TYPE_BACKGROUND, backgroundInfo);
    }


    private static Document getDocument(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(content));
            document = db.parse(is);
        } catch (Exception e) {
            Logger.e(TAG, "getDocument error:" + e.getMessage());
        }
        return document;
    }

    public static CutData parseStoryToCatData(String cropperStory, String transformStory, float[] relativeSize) {

        Document transformDocument = getDocument(transformStory);
        if (transformDocument == null) {
            return null;
        }
        CutData cutData = new CutData();
        NodeList params = transformDocument.getElementsByTagName("param");

        NodeList transStoryboard = transformDocument.getElementsByTagName("storyboard");

        NamedNodeMap transAttributes = transStoryboard.item(0).getAttributes();
        int transSceneWidth = Integer.parseInt(transAttributes.getNamedItem("sceneWidth").getNodeValue());
        int transSceneHeight = Integer.parseInt(transAttributes.getNamedItem("sceneHeight").getNodeValue());

        if (params.getLength() == 0) {
            return null;
        }
        for (int index = 0; index < params.getLength(); index++) {
            Node item = params.item(index);

            NamedNodeMap childNodeAttributes = item.getAttributes();
            if (childNodeAttributes == null) {
                continue;
            }
            if (childNodeAttributes.getNamedItem("name") != null) {
                if (STORYBOARD_KEY_ROTATION_Z.equals(childNodeAttributes.getNamedItem("name").getNodeValue())) {
                    cutData.putTransformData(childNodeAttributes.getNamedItem("name").getNodeValue(),
                            -Float.parseFloat(childNodeAttributes.getNamedItem("value").getNodeValue()));
                } else if (STORYBOARD_KEY_TRANS_Y.equals(childNodeAttributes.getNamedItem("name").getNodeValue())) {
                    cutData.putTransformData(childNodeAttributes.getNamedItem("name").getNodeValue(),
                            -Float.parseFloat(childNodeAttributes.getNamedItem("value").getNodeValue()));
                } else {
                    cutData.putTransformData(childNodeAttributes.getNamedItem("name").getNodeValue(),
                            Float.parseFloat(childNodeAttributes.getNamedItem("value").getNodeValue()));
                }
            }
        }

        Document document = getDocument(cropperStory);
        if (document == null) {
            return cutData;
        }
        NodeList storyboard = document.getElementsByTagName("storyboard");

        NamedNodeMap attributes = storyboard.item(0).getAttributes();
        int sceneWidth = Integer.parseInt(attributes.getNamedItem("sceneWidth").getNodeValue());
        int sceneHeight = Integer.parseInt(attributes.getNamedItem("sceneHeight").getNodeValue());

        NodeList effect = document.getElementsByTagName("param");
        if (effect.getLength() > 0) {
            for (int index = 0; index < effect.getLength(); index++) {
                Node item = effect.item(index);

                NamedNodeMap childNodeAttributes = item.getAttributes();
                if (childNodeAttributes == null) {
                    continue;
                }
                Log.e(TAG, "parseStoryToCatData: value = " + childNodeAttributes.getNamedItem("name").getNodeValue());
                if (childNodeAttributes.getNamedItem("name") != null && "region".equals(childNodeAttributes.getNamedItem("name").getNodeValue())) {
                    String region = childNodeAttributes.getNamedItem("value").getNodeValue();
                    cutData.setRatio(getRationFromRegion(region, sceneWidth, sceneHeight, relativeSize));
                    cutData.setRatioValue(getRatioValueFromRegion(region, sceneWidth, sceneHeight, relativeSize));
                }
            }
        }

        cutData.setIsOldData(transSceneWidth == sceneWidth && transSceneHeight == sceneHeight);

        return cutData;
    }

    private static int getRationFromRegion(String region, int sceneWidth, int sceneHeight, float[] relativeSize) {
        if (TextUtils.isEmpty(region)) {
            return 0;
        }
        String[] split = region.split(",");
        if (split.length != 8) {
            return NvAsset.AspectRatio_NoFitRatio;
        }
        float height = (Float.parseFloat(split[3]) - Float.parseFloat(split[5])) / relativeSize[1];
        float width = (Float.parseFloat(split[2]) - Float.parseFloat(split[0])) / relativeSize[0];
        float ratio = (sceneWidth * width) / (sceneHeight * height);
        return AspectRatio.getAspect(ratio);
    }

    private static float getRatioValueFromRegion(String region, int sceneWidth, int sceneHeight, float[] relativeSize) {
        if (TextUtils.isEmpty(region)) {
            return 0;
        }
        String[] split = region.split(",");
        float height = (Float.parseFloat(split[3]) - Float.parseFloat(split[5])) / relativeSize[1];
        float width = (Float.parseFloat(split[2]) - Float.parseFloat(split[0])) / relativeSize[0];
        float ratio = (sceneWidth * width) / (sceneHeight * height);
        return ratio;
    }

    public enum AspectRatio {
        ASPECT_16V9(NvAsset.AspectRatio_16v9, 16.0f / 9),
        ASPECT_9V16(NvAsset.AspectRatio_9v16, 9.0f / 16),
        ASPECT_1V1(NvAsset.AspectRatio_1v1, 1),
        ASPECT_4V3(NvAsset.AspectRatio_4v3, 4.0f / 3),
        ASPECT_3V4(NvAsset.AspectRatio_3v4, 3.0f / 4),
        ASPECT_9V18(NvAsset.AspectRatio_9v18, 9.0f / 18),
        ASPECT_18V9(NvAsset.AspectRatio_18v9, 18.0f / 9),
        ASPECT_9V21(NvAsset.AspectRatio_9v21, 9.0f / 21),
        ASPECT_21V0(NvAsset.AspectRatio_21v9, 21.0f / 9);
        private int aspect;
        private float ratio;

        AspectRatio(int aspect, float ratio) {
            this.aspect = aspect;
            this.ratio = ratio;
        }

        public float getRatio() {
            return ratio;
        }

        public static int getAspect(float ratio) {
            AspectRatio[] values = AspectRatio.values();
            for (AspectRatio value : values) {
                if (Math.abs(value.ratio - ratio) < 0.1f) {
                    return value.aspect;
                }
            }
            return Constants.AspectRatio.AspectRatio_NoFitRatio;
        }
    }

}

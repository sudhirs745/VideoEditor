package com.glitchcam.vepromei.edit.data;


import com.glitchcam.vepromei.utils.asset.NvAsset;

public class BaseInfo {
    /**
     * 内建特效
     * Built-in effects
     */
    public static int EFFECT_MODE_BUILTIN = 0;
    /**
     * Asset中预装
     * Pre-installed in Asset
     */
    public static int EFFECT_MODE_BUNDLE = 1;
    /**
     * 包裹特效
     * Package effects
     */
    public static int EFFECT_MODE_PACKAGE = 2;

    /*
     * 表示无素材
     * Means no material
     * */
    public static final int ASSET_NONE = 1;
    /*
     * 下载到本地的素材
     * Download to local material
     * */
    public static final int ASSET_LOCAL = 2;
    /*
     * 内建素材素材
     * Built-in materials
     * */
    public static final int ASSET_BUILTIN = 3;

    public static int MENU_INDEX_LEVEL_1 = 1;
    public static int MENU_INDEX_LEVEL_2 = 2;
    public static int MENU_INDEX_LEVEL_3 = 3;

    /*
     * 不适配比例
     * Unfit ratio
     * */
    public static final int AspectRatio_NoFitRatio = 0;//
    public static final int AspectRatio_16v9 = 1;
    public static final int AspectRatio_1v1 = 2;
    public static final int AspectRatio_9v16 = 4;
    public static final int AspectRatio_4v3 = 8;
    public static final int AspectRatio_3v4 = 16;
    public static final int AspectRatio_All = AspectRatio_16v9 | AspectRatio_1v1 | AspectRatio_9v16 | AspectRatio_3v4 | AspectRatio_4v3;

    public static final int RatioArray[] = {
            AspectRatio_16v9,
            AspectRatio_1v1,
            AspectRatio_9v16,
            AspectRatio_3v4,
            AspectRatio_4v3,
            AspectRatio_All
    };

    public static final String RatioStringArray[] = {
            "16:9",
            "1:1",
            "9:16",
            "3:4",
            "4:3",
            "通用"
    };


    /*
     * 进入页面的初始状态
     * Enter the initial state of the page
     * */
    public static final int DownloadStatusNone = 0;
    /*
     * 等待状态
     * Waiting state
     * */
    public static final int DownloadStatusPending = 1;
    /*
     * 下载中
     * downloading
     * */
    public static final int DownloadStatusInProgress = 2;
    /*
     * 安装中
     * installing
     * */
    public static final int DownloadStatusDecompressing = 3;
    /*
     * 下载成功
     * download successfully
     * */
    public static final int DownloadStatusFinished = 4;
    /*
     * 下载失败
     * download failed
     * */
    public static final int DownloadStatusFailed = 5;
    /*
     * 安装失败
     * installation failed
     * */
    public static final int DownloadStatusDecompressingFailed = 6;

    /*
     * 粒子滤镜类型：触摸
     * Particle filter type: Touch
     * */
    public static final int NV_CATEGORY_ID_PARTICLE_TOUCH_TYPE = 2;

    public boolean checkAble = false;         //是否可以选择
    public int mEffectType;
    public String mName;
    public String mIconUrl;             //非内建使用网络图片
    public int mIconRcsId;              //内建特效 使用资源文件
    public int mEffectMode;             //特效类型
    public String mPackageId;
    public int mAssetMode;
    public String mType;
    public int mMenuIndex = MENU_INDEX_LEVEL_2;
    protected NvAsset mAsset;


    public BaseInfo() {
    }

    public BaseInfo(String name) {
        this.mName = name;
    }

    public BaseInfo(String name, int iconRcsId) {
        this.mName = name;
        this.mIconRcsId = iconRcsId;
    }

    public BaseInfo(String name, String iconUrl, int iconRcsId, int effectType) {
        this(name, iconRcsId);
        this.mIconUrl = iconUrl;
        this.mEffectType = effectType;
    }

    public BaseInfo(String name, String iconUrl, int iconRcsId, int effectType, int effectMode, String packageId) {
        this(name, iconUrl, iconRcsId, effectType);
        this.mEffectMode = effectMode;
        this.mPackageId = packageId;
    }

    public void setAsset(NvAsset asset) {
        this.mAsset = asset;
    }

    public NvAsset getAsset() {
        return mAsset;
    }


}

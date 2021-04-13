package com.glitchcam.vepromei;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.meicam.sdk.NvsStreamingContext;
import com.glitchcam.vepromei.utils.Logger;
import com.glitchcam.vepromei.utils.asset.NvAssetManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;


public class MSApplication extends Application {
    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate( );
        mContext = getApplicationContext( );
        /*
        * 初始化
        * initialization
        * */
        String licensePath = "assets:/meishesdk.lic";
        NvsStreamingContext.init(mContext, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT);
        NvAssetManager.init(mContext);

        /*
        * 友盟初始化
        * Umeng initialization
        * */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        /*
        *  组件化的Log是否输出 默认关闭Log输出。和集成测试是一个开关，release要关闭
        * Whether the componentized log is output The log output is turned off by default.
        *  And integration testing is a switch, release should be closed
        * */
//        UMConfigure.setLogEnabled(true);
        // isEnable: false-关闭错误统计功能；true-打开错误统计功能（默认打开）
//        public static void setCatchUncaughtExceptions(boolean isEnable)
        /*
        * 场景类型设置
        * Scene type settings
        * */
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
        Fresco.initialize(this);
    }
}

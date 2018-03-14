package com.example.npttest;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.example.npttest.constant.Constant;
import com.example.npttest.util.DBHelper;
import com.example.npttest.util.SPUtils;

import java.util.Locale;

/**
 * Created by liuji on 2017/8/12.
 */

public class App extends Application {
    public static String serverurl;//云地址
    public static String surpluscar;//剩余车位
    public static DBHelper dbHelper = null;
    public static boolean goRefresh = true;
    public static boolean outRefresh = true;
    public static boolean chaRefresh = true;
    public static boolean pvRefresh = true;
    public static boolean zcRefresh=false;
    public static boolean mdRefresh=false;
    public static double wmon;
    private int ZH=1;
    private int ZH_TW=2;
    private int ZH_HK=3;
    private int EN=4;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","Appcreate");
        //初始化数据库
        dbHelper=new DBHelper(this);
        initCloudChannel(this);
        int language= (int) SPUtils.get(this, Constant.LANGUAGE,1);
        Log.e("TAG","当前选择的语言："+language);
        switchLanguage(language);
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e("TAG", "初始化推送通道成功");
                //setConsoleText("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e("TAG", "初始化推送通道失败 -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                //setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }


    /**
     * 切换语言
     *
     * @param language
     */

    private void switchLanguage(int language) {

        //设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language==ZH) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language==EN) {
            config.locale = Locale.ENGLISH;
        } else if (language==ZH_TW){
            config.locale = Locale.TRADITIONAL_CHINESE;
        }else if (language==ZH_HK){
            config.locale = Locale.TRADITIONAL_CHINESE;
        }else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);

    }
}

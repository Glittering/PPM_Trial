package com.example.npttest.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.npttest.R;
import com.example.npttest.constant.Constant;
import com.example.npttest.manager.ActivityManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/21.
 */

public class SpalshActivity extends NoStatusbarActivity {
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        ButterKnife.bind(this);
        Constant.DSV = String.valueOf(getAPPLocalVersion(this));
        Log.e("TAG", "设备编码：" + Constant.CODE + "\n" + "设备版本：" + Constant.DSV);
        ActivityManager.getInstance().addActivity(this);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0x123);
                    }
                });
            }
        }, 0, 2000);
    }
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0x123){
                startActivity(new Intent(SpalshActivity.this,IndexActivity.class));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    //获取apk的版本号 currentVersionCode
    private int getAPPLocalVersion(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(ctx.getPackageName(), 0);
            /*localVersionName = info.versionName; // 版本名
            localVersionCode = info.versionCode; // 版本号*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionCode;
    }
}

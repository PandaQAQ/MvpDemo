package com.pandaq.mvpdemo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by PandaQ on 2017/2/6.
 * email : 767807368@qq.com
 */

public class MyApplication extends Application {

    private static MyApplication sMyApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;
    }

    /**
     * 获取全局 context
     *
     * @return 全局context
     */
    public static Context getContext() {
        return sMyApplication.getApplicationContext();
    }

    /**
     * 获取应用的版本号
     *
     * @return 应用版本号
     */
    public static int getAppVersion() {
        Context context = getContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}

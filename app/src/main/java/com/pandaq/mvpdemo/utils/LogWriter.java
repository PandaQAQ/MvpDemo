package com.pandaq.mvpdemo.utils;

import android.util.Log;

import com.pandaq.mvpdemo.GlobalConfig;

/**
 * Created by PandaQ on 2017/2/6.
 * email : 767807368@qq.com
 */

public class LogWriter {

    public static void LogStr(String Tag, String info) {
        if (GlobalConfig.isDebug) {
            Log.i(Tag, info);
        }
    }
}

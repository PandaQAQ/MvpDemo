package com.pandaq.mvpdemo.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by PandaQ on 2017/2/6.
 * email : 767807368@qq.com
 * 磁盘缓存工具类
 */

public class DiskCacheUtil {

    public static File getCachePath(Context context, String uniqueName) {
        String cachePath = null;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath+File.separator+uniqueName);
    }
}

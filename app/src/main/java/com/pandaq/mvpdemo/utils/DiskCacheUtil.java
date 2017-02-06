package com.pandaq.mvpdemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;
import com.pandaq.mvpdemo.Constants;
import com.pandaq.mvpdemo.MyApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by PandaQ on 2017/2/6.
 * email : 767807368@qq.com
 * 磁盘缓存工具类
 */

public class DiskCacheUtil {

    private static DiskLruCache mDiskLruCache = null;

    /**
     * 获取当前的DiskLruCache
     * @return 当前的DiskLruCache 不能使用此方法去得到 DiskLruCache 进行缓存操作
     */
    public static DiskLruCache getDiskLruCache() {
        return mDiskLruCache;
    }

    /**
     * 初始化DiskLruCache
     *
     * @param cacheFile 缓存文件
     * @return 初始化后的DiskLruCache 对象
     */
    public static DiskLruCache instance(File cacheFile) {
        if (mDiskLruCache == null) {
            try {
                mDiskLruCache = DiskLruCache.open(cacheFile, MyApplication.getAppVersion(), 1, Constants.CACHE_MAXSIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mDiskLruCache;
    }

    /**
     * 获取缓存的路径 两个路径在卸载程序时都会删除，因此不会在卸载后还保留乱七八糟的缓存
     * 有SD卡时获取  /sdcard/Android/data/<application package>/cache
     * 无SD卡时获取  /data/data/<application package>/cache
     *
     * @param context    上下文
     * @param uniqueName 缓存目录下的细分目录，用于存放不同类型的缓存
     * @return 缓存目录 File
     */
    public static File getCacheFile(Context context, String uniqueName) {
        String cachePath = null;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

}

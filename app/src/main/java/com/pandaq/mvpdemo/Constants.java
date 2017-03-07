package com.pandaq.mvpdemo;

/**
 * Created by PandaQ on 2017/2/6.
 * email : 767807368@qq.com
 * 保存各种常量
 */

public class Constants {
    //SecretUtil
    public static String NOSUCHALGORITHM = "不支持此种加密方式";
    public static String UNSUPPENCODING = "不支持的编码格式";

    //Cache
    public static String FLUSH_ERRO = "DiskLruCache flush 失败！";
    public static long CACHE_MAXSIZE = 10 * 1024 * 1024; //10MB的缓存大小
    public static String ZHIHUSTORY_KEY = "zhihu_latest_news";//缓存知乎日报最近一页的内容
    public static String ZHIHUCACHE = "zhihuCache";//知乎日报的缓存文件夹名

    //SMS Activity
    public static String SMS_BROADCAST_FILTER = "pandaq.mvpdemo.recevieSMS";
}

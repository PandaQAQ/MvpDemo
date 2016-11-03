package com.pandaq.mvpdemo.api;

import com.pandaq.mvpdemo.GlobalConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by PandaQ on 2016/11/2.
 * email : 767807368@qq.com
 * retrofit网络请求的管理类
 */

public class ApiManager {

    private RetrofitService mDailyApi;
    private static ApiManager sApiManager;

    public static ApiManager getInstence() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager();
                }
            }
        }
        return sApiManager;
    }
    /**
     * 封装配置知乎API
     */
    public RetrofitService getDailyService() {
        OkHttpClient client = new OkHttpClient.Builder()
                //添加应用拦截器
                .addInterceptor(new MyOkhttpInterceptor())
                //添加网络拦截器
//                .addNetworkInterceptor(new MyOkhttpInterceptor())
                .build();
        if (mDailyApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConfig.baseUrl)
                    //将client与retrofit关联
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mDailyApi = retrofit.create(RetrofitService.class);
        }
        return mDailyApi;
    }
}

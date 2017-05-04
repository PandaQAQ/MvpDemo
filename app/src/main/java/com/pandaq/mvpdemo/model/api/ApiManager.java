package com.pandaq.mvpdemo.model.api;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.pandaq.mvpdemo.GlobalConfig;
import com.pandaq.mvpdemo.enums.ClientType;
import com.pandaq.mvpdemo.utils.HttpsUtils;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
    public RetrofitService getDataService() {
        OkHttpClient client = new OkHttpClient.Builder()
                //添加应用拦截器
                .addInterceptor(new HttpInterceptor())
                //添加网络拦截器
//                .addNetworkInterceptor(new MyOkhttpInterceptor())
                .build();
        if (mDailyApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConfig.baseUrl)
                    //将client与retrofit关联
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mDailyApi = retrofit.create(RetrofitService.class);
        }
        return mDailyApi;
    }

    public RetrofitService get12306Service(Context context, ClientType type) {
        OkHttpClient client;
        if (type == ClientType.TYPE_OKHTTPCLIENT) {
            client = new OkHttpClient.Builder()
                    //添加应用拦截器
                    .addInterceptor(new HttpInterceptor())
                    //添加网络拦截器
//                .addNetworkInterceptor(new MyOkhttpInterceptor())
                    .build();
        } else {
            try {
                InputStream inputStream = context.getAssets().open("srca.cer");
                client = new HttpsUtils()
                        .getTrusClient(inputStream);
//                        .newBuilder()
//                        .addInterceptor(new MyOkhttpInterceptor())
//                        .build();
            } catch (IOException e) {
                e.printStackTrace();
                client = new OkHttpClient();
            }
        }
        if (mDailyApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConfig.baseTestUrl)
                    //将client与retrofit关联
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .build();
            mDailyApi = retrofit.create(RetrofitService.class);
        }
        return mDailyApi;
    }
}

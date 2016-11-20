package com.pandaq.mvpdemo.api;

import com.pandaq.mvpdemo.databeans.ZhiHuDaily;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by PandaQ on 2016/11/2.
 * email : 767807368@qq.com
 */

public interface RetrofitService {
    //单纯使用retrofit接口定义
    @GET("news/latest")
    Call<ZhiHuDaily> getZhihuDailyRetrofitOnly();

    //使用retrofit+RxAndroid的接口定义
    @GET("news/latest")
    Observable<ZhiHuDaily> getZhihuDaily();

    @GET("/")
    Observable<ResponseBody> get12306Test();
}

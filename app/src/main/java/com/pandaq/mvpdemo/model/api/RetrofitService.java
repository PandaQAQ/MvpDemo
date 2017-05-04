package com.pandaq.mvpdemo.model.api;

import com.pandaq.mvpdemo.model.zhihu.ZhiHuDaily;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStoryContent;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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

    @GET("news/{id}")
    Observable<ZhihuStoryContent> getStoryContent(@Path("id") String id);
}

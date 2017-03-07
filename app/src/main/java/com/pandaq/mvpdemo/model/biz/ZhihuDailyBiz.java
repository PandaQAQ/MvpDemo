package com.pandaq.mvpdemo.model.biz;

import android.os.Handler;

import com.google.gson.Gson;
import com.pandaq.mvpdemo.model.api.ApiManager;
import com.pandaq.mvpdemo.model.api.HttpServiceManager;
import com.pandaq.mvpdemo.model.zhihu.ZhiHuDaily;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStory;
import com.pandaq.mvpdemo.utils.OnEventLister;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public class ZhihuDailyBiz{

    public void getStoryData(final String url, final OnEventLister<ArrayList<ZhihuStory>> eventLister) {
        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                try {
                    String result = HttpServiceManager.httpGet(url);
                    Gson gson = new Gson();
                    ZhiHuDaily daily = gson.fromJson(result, ZhiHuDaily.class);
                    final ArrayList<ZhihuStory> stories = daily.getStories();
                    if (stories != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                eventLister.onSuccess(stories);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                eventLister.onFail("-100", "获取日报失败！");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            eventLister.onFail("-100", "获取日报失败！");
                        }
                    });
                }
            }
        }.start();
    }

    public void getStoryDataByRetrofit(final OnEventLister<ArrayList<ZhihuStory>> eventLister) {
        ApiManager apiManager = ApiManager.getInstence();
        Call<ZhiHuDaily> call = apiManager.getDataService().getZhihuDailyRetrofitOnly();
        //发送异步请求
        call.enqueue(new Callback<ZhiHuDaily>() {
            @Override
            public void onResponse(Call<ZhiHuDaily> call, Response<ZhiHuDaily> response) {
                eventLister.onSuccess(response.body().getStories());
            }

            @Override
            public void onFailure(Call<ZhiHuDaily> call, Throwable t) {
                eventLister.onFail(t.getMessage(), "");
            }
        });
    }
}

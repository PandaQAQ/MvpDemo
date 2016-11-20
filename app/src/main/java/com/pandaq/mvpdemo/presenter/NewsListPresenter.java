package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.api.ApiManager;
import com.pandaq.mvpdemo.biz.ZhihuDailyBiz;
import com.pandaq.mvpdemo.databeans.ZhiHuDaily;
import com.pandaq.mvpdemo.databeans.ZhihuStory;
import com.pandaq.mvpdemo.utils.OnEventLister;
import com.pandaq.mvpdemo.view.IViewBind.INewsListActivity;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public class NewsListPresenter extends BasePresenter {
    private INewsListActivity mINewsListActivity;
    private ZhihuDailyBiz mDailyBiz;

    public NewsListPresenter(INewsListActivity INewsListActivity) {
        mINewsListActivity = INewsListActivity;
        mDailyBiz = new ZhihuDailyBiz();
    }

    //普通封装okhttp进行网络请求
    public void loadData() {
        mINewsListActivity.showProgressBar();
        mDailyBiz.getStoryData("news/latest", new OnEventLister<ArrayList<ZhihuStory>>() {
            @Override
            public void onSuccess(ArrayList<ZhihuStory> response) {
                mINewsListActivity.hidProgressBar();
                mINewsListActivity.getDataSuccess(response);
            }

            @Override
            public void onFail(String errCode, String errMsg) {
                mINewsListActivity.hidProgressBar();
                mINewsListActivity.getDataFail(errCode, errMsg);
            }
        });
    }

    //单独只用Retrofit进行网络请求
    public void loadDataByRetrofit() {
        mINewsListActivity.showProgressBar();
        mDailyBiz.getStoryDataByRetrofit(new OnEventLister<ArrayList<ZhihuStory>>() {
            @Override
            public void onSuccess(ArrayList<ZhihuStory> response) {
                mINewsListActivity.hidProgressBar();
                mINewsListActivity.getDataSuccess(response);
            }

            @Override
            public void onFail(String errCode, String errMsg) {
                mINewsListActivity.hidProgressBar();
                mINewsListActivity.getDataFail(errCode, errMsg);
            }
        });
    }

    //使用rxandroid+retrofit进行请求
    public void loadDataByRxandroidRetrofit() {
        mINewsListActivity.showProgressBar();
        Subscription subscription = ApiManager.getInstence().getDataService()
                .getZhihuDaily()
                .map(new Func1<ZhiHuDaily, ArrayList<ZhihuStory>>() {
                    @Override
                    public ArrayList<ZhihuStory> call(ZhiHuDaily zhiHuDaily) {
                        return zhiHuDaily.getStories();
                    }
                })
                //设置事件触发在非主线程
                .subscribeOn(Schedulers.io())
                //设置事件接受在UI线程以达到UI显示的目的
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<ZhihuStory>>() {
                    @Override
                    public void onCompleted() {
                        mINewsListActivity.hidProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mINewsListActivity.getDataFail("", e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<ZhihuStory> stories) {
                        mINewsListActivity.getDataSuccess(stories);
                    }
                });
        //绑定观察对象，注意在界面的ondestory或者onpouse方法中调用presenter.unsubcription();
        addSubscription(subscription);
    }

}

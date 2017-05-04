package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.Constants;
import com.pandaq.mvpdemo.MyApplication;
import com.pandaq.mvpdemo.model.api.ApiManager;
import com.pandaq.mvpdemo.model.biz.ZhihuDailyBiz;
import com.pandaq.mvpdemo.model.zhihu.ZhiHuDaily;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStory;
import com.pandaq.mvpdemo.disklrucache.DiskCacheManager;
import com.pandaq.mvpdemo.utils.OnEventLister;
import com.pandaq.mvpdemo.ui.IViewBind.INewsListActivity;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
        ApiManager.getInstence().getDataService()
                .getZhihuDaily()
                .map(new Function<ZhiHuDaily, ArrayList<ZhihuStory>>() {
                    @Override
                    public ArrayList<ZhihuStory> apply(@NonNull ZhiHuDaily zhiHuDaily) throws Exception {
                        ArrayList<ZhihuStory> stories = zhiHuDaily.getStories();
                        if (stories != null) {
                            //加载成功后将数据缓存倒本地(demo 中只有一页，实际使用时根据需求选择是否进行缓存)
                            makeCache(zhiHuDaily.getStories());
                        }
                        return stories;
                    }
                })
                //设置事件触发在非主线程
                .subscribeOn(Schedulers.io())
                //设置事件接受在UI线程以达到UI显示的目的
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ZhihuStory>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //绑定观察对象，注意在界面的ondestory或者onpouse方法中调用presenter.unsubcription();
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<ZhihuStory> zhihuStories) {
                        mINewsListActivity.getDataSuccess(zhihuStories);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mINewsListActivity.getDataFail("", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mINewsListActivity.hidProgressBar();
                    }
                });
    }

    private void makeCache(ArrayList<ZhihuStory> stories) {
        DiskCacheManager manager = new DiskCacheManager(MyApplication.getContext(), Constants.ZHIHUCACHE);
        manager.put(Constants.ZHIHUSTORY_KEY, stories);
    }

    public void loadCache() {
        DiskCacheManager manager = new DiskCacheManager(MyApplication.getContext(), Constants.ZHIHUCACHE);
        ArrayList<ZhihuStory> stories = manager.getSerializable(Constants.ZHIHUSTORY_KEY);
        if (stories != null) {
            mINewsListActivity.getDataSuccess(stories);
        } else {
            mINewsListActivity.getDataFail("", "读取缓存失败");
        }
    }
}

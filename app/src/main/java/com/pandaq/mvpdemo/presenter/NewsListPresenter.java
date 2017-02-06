package com.pandaq.mvpdemo.presenter;

import com.jakewharton.disklrucache.DiskLruCache;
import com.pandaq.mvpdemo.Constants;
import com.pandaq.mvpdemo.MyApplication;
import com.pandaq.mvpdemo.api.ApiManager;
import com.pandaq.mvpdemo.biz.ZhihuDailyBiz;
import com.pandaq.mvpdemo.databeans.ZhiHuDaily;
import com.pandaq.mvpdemo.databeans.ZhihuStory;
import com.pandaq.mvpdemo.utils.DiskCacheUtil;
import com.pandaq.mvpdemo.utils.OnEventLister;
import com.pandaq.mvpdemo.ui.IViewBind.INewsListActivity;
import com.pandaq.mvpdemo.utils.SecretUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import rx.Observable;
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

    private void makeCache(ArrayList<ZhihuStory> stories) {
        File cacheFile = DiskCacheUtil.getCacheFile(MyApplication.getContext(), Constants.ZHIHUCACHE);
        DiskLruCache diskLruCache = DiskCacheUtil.instance(cacheFile);
        try {
            //使用MD5加密后的字符串作为key，避免key中有非法字符
            String key = SecretUtil.getMD5Result(Constants.ZHIHUSTORY_KEY);
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            if (editor != null) {
                ObjectOutputStream outputStream = new ObjectOutputStream(editor.newOutputStream(0));
                outputStream.writeObject(stories);
                outputStream.close();
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCache() {
        File cacheFile = DiskCacheUtil.getCacheFile(MyApplication.getContext(), Constants.ZHIHUCACHE);
        DiskLruCache diskLruCache = DiskCacheUtil.instance(cacheFile);
        String key = SecretUtil.getMD5Result(Constants.ZHIHUSTORY_KEY);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                InputStream in = snapshot.getInputStream(0);
                ObjectInputStream ois = new ObjectInputStream(in);
                try {
                    ArrayList<ZhihuStory> stories = (ArrayList<ZhihuStory>) ois.readObject();
                    if (stories != null) {
                        mINewsListActivity.getDataSuccess(stories);
                    } else {
                        mINewsListActivity.getDataFail("", "无数据");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

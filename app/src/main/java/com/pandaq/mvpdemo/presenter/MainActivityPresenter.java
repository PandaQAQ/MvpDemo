package com.pandaq.mvpdemo.presenter;

import android.content.Context;

import com.pandaq.mvpdemo.api.ApiManager;
import com.pandaq.mvpdemo.enums.ClientType;
import com.pandaq.mvpdemo.ui.IViewBind.IMainActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PandaQ on 2016/11/20.
 * email : 767807368@qq.com
 */

public class MainActivityPresenter extends BasePresenter {

    private IMainActivity mActivity;

    public MainActivityPresenter(IMainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    public void get12306Test(Context context, ClientType type) {
        Subscription subscription = new ApiManager() //此处直接new ApiManager对象避免缓存证书
                .get12306Service(context, type)
                .get12306Test()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.showResult(e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            mActivity.showResult(response.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        addSubscription(subscription);
    }

}

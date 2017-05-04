package com.pandaq.mvpdemo.presenter;

import android.content.Context;

import com.pandaq.mvpdemo.model.api.ApiManager;
import com.pandaq.mvpdemo.enums.ClientType;
import com.pandaq.mvpdemo.ui.IViewBind.IHttpsActivity;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by PandaQ on 2016/11/20.
 * email : 767807368@qq.com
 */

public class MainActivityPresenter extends BasePresenter {

    private IHttpsActivity mActivity;

    public MainActivityPresenter(IHttpsActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    public void get12306Test(Context context, ClientType type) {
        ApiManager.getInstence()
                .get12306Service(context, type)
                .get12306Test()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            mActivity.showResult(responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mActivity.showResult(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}

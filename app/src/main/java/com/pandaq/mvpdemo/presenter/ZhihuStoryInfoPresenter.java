package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.model.api.ApiManager;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStoryContent;
import com.pandaq.mvpdemo.ui.IViewBind.IZhihuStoryInfoActivity;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by PandaQ on 2016/10/10.
 * email : 767807368@qq.com
 */

public class ZhihuStoryInfoPresenter extends BasePresenter {

    private IZhihuStoryInfoActivity mActivity;

    public ZhihuStoryInfoPresenter(IZhihuStoryInfoActivity iZhihuStoryInfoActivity) {
        mActivity = iZhihuStoryInfoActivity;
    }

    public void loadStory(String id) {
        mActivity.showProgressBar();
        Subscription subscription = ApiManager.getInstence().getDataService()
                .getStoryContent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ZhihuStoryContent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.loadFail(e.getMessage());
                    }

                    @Override
                    public void onNext(ZhihuStoryContent zhihuStoryContent) {
                        mActivity.loadSuccess(zhihuStoryContent);
                    }
                });

        addSubscription(subscription);
    }
}

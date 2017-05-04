package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.model.api.ApiManager;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStoryContent;
import com.pandaq.mvpdemo.ui.IViewBind.IZhihuStoryInfoActivity;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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
        ApiManager.getInstence().getDataService()
                .getStoryContent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuStoryContent>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull ZhihuStoryContent zhihuStoryContent) {
                        mActivity.loadSuccess(zhihuStoryContent);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mActivity.loadFail(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

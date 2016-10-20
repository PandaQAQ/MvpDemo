package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.biz.ZhihuDailyBiz;
import com.pandaq.mvpdemo.databeans.ZhihuStory;
import com.pandaq.mvpdemo.utils.OnEventLister;
import com.pandaq.mvpdemo.view.IViewBind.IMainActivity;

import java.util.ArrayList;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public class MainPresenter {
    private IMainActivity mIMainActivity;
    private ZhihuDailyBiz mDailyBiz;

    public MainPresenter(IMainActivity IMainActivity) {
        mIMainActivity = IMainActivity;
        mDailyBiz = new ZhihuDailyBiz();
    }

    public void loadData() {
        mIMainActivity.showProgressBar();
        mDailyBiz.getStoryData("news/latest", new OnEventLister<ArrayList<ZhihuStory>>() {
            @Override
            public void onSuccess(ArrayList<ZhihuStory> response) {
                mIMainActivity.hidProgressBar();
                mIMainActivity.getDataSuccess(response);
            }

            @Override
            public void onFail(String errCode, String errMsg) {
                mIMainActivity.hidProgressBar();
                mIMainActivity.getDataFail(errCode, errMsg);
            }
        });
    }

}

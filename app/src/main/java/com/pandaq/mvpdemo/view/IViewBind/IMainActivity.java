package com.pandaq.mvpdemo.view.IViewBind;

import com.pandaq.mvpdemo.databeans.ZhihuStory;

import java.util.ArrayList;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public interface IMainActivity {

    void showProgressBar();

    void hidProgressBar();

    void loadData();
// loadMore refresh 就大家自由发挥了demo中就不写了
//    void loadMore();
//
//    void refresh();
//
//    void refreshSuccess(ArrayList<ZhihuStory> stories);
//
//    void refreshFail(String errCode, String errMsg);
//
//    void loadSuccess(ArrayList<ZhihuStory> stories);
//
//    void loadFail(String errCode, String errMsg);

    void getDataSuccess(ArrayList<ZhihuStory> stories);

    void getDataFail(String errCode, String errMsg);

}

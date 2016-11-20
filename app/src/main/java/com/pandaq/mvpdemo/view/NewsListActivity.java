package com.pandaq.mvpdemo.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.adapter.ZhihuStoryAdapter;
import com.pandaq.mvpdemo.databeans.ZhihuStory;
import com.pandaq.mvpdemo.presenter.NewsListPresenter;
import com.pandaq.mvpdemo.view.IViewBind.INewsListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsListActivity extends AppCompatActivity implements INewsListActivity {

    ZhihuStoryAdapter mAdapter;
    @BindView(R.id.zhihudaily_list)
    RecyclerView mZhihudailyList;
    @BindView(R.id.activity_main)
    RelativeLayout mActivityMain;
    @BindView(R.id.progressbar)
    ProgressBar mProgressbar;
    //将View与Presenter关联
    private NewsListPresenter mPresenter = new NewsListPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        mZhihudailyList.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    @Override
    public void loadData() {
        mPresenter.loadDataByRxandroidRetrofit();
    }

    @Override
    public void showProgressBar() {
        mProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressBar() {
        mProgressbar.setVisibility(View.GONE);
    }

    @Override
    public void getDataSuccess(ArrayList<ZhihuStory> stories) {
        //不管界面怎么改只要与presenter进行绑定都得到的是stories数据，view界面只负责展示不关心怎么获取怎么处理解析数据
        if (mAdapter != null) {
            mAdapter.addItem(stories);
        } else {
            mAdapter = new ZhihuStoryAdapter(this, stories);
            mZhihudailyList.setAdapter(mAdapter);
        }
    }

    @Override
    public void getDataFail(String errCode, String errMsg) {
        Snackbar.make(mActivityMain, errMsg, Snackbar.LENGTH_SHORT).show();
    }

    //使用RxAndroid添加的方法，用于在退出时解绑观察
    @Override
    public void unSubcription() {
        mPresenter.unsubcription();
    }
}

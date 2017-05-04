package com.pandaq.mvpdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.adapter.ZhihuStoryAdapter;
import com.pandaq.mvpdemo.model.zhihu.ZhihuStory;
import com.pandaq.mvpdemo.presenter.NewsListPresenter;
import com.pandaq.mvpdemo.ui.IViewBind.INewsListActivity;
import com.pandaq.mvpdemo.utils.NetWorkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsListActivity extends BaseActivity implements INewsListActivity, ZhihuStoryAdapter.ItemClicklistener {

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
        //无网络环境的时候读取缓存，有网的时候读取网络数据
        if (NetWorkUtil.isNetWorkAvailable(this)) {
            mPresenter.loadDataByRxandroidRetrofit();
        } else {
            mPresenter.loadCache();
        }
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
            mAdapter.addOnItemClickListener(this);
        }
    }

    @Override
    public void getDataFail(String errCode, String errMsg) {
        Snackbar.make(mActivityMain, errMsg, Snackbar.LENGTH_SHORT).show();
    }

    //使用RxAndroid添加的方法，用于在退出时解绑观察
    @Override
    public void unSubcription() {
        mPresenter.dispose();
    }

    @Override
    public void onItemClick(ZhihuStory story) {
        //跳转到其他界面
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, ZhihuStoryInfoActivity.class);
        bundle.putString("title", story.getTitle());
        bundle.putInt("id", story.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

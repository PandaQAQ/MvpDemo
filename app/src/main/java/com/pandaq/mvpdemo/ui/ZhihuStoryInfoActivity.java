package com.pandaq.mvpdemo.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.databeans.ZhihuStoryContent;
import com.pandaq.mvpdemo.presenter.ZhihuStoryInfoPresenter;
import com.pandaq.mvpdemo.ui.IViewBind.IZhihuStoryInfoActivity;
import com.pandaq.mvpdemo.utils.DensityUtil;
import com.pandaq.mvpdemo.utils.WebUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PandaQ on 2016/9/8.
 * email : 767807368@qq.com
 * 知乎日报打开详情页面
 */
public class ZhihuStoryInfoActivity extends AppCompatActivity implements IZhihuStoryInfoActivity {

    private static final float SCRIM_ADJUSTMENT = 0.075f;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.story_img)
    ImageView mStoryImg;
    @BindView(R.id.zhihudaily_webview)
    WebView mZhihudailyWebview;
    private String story_id = "";
    private ZhihuStoryInfoPresenter mPresenter = new ZhihuStoryInfoPresenter(this);
    int[] mDeviceInfo;
    int width;
    int heigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_story_info);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbarLayout.setTitle("知乎日报");
        initView();
        initData();
    }

    private void initView() {
        mDeviceInfo = DensityUtil.getDeviceInfo(this);
        width = mDeviceInfo[0];
        heigh = width * 3 / 5;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        story_id = bundle.getInt("id") + "";
        loadZhihuStory();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void loadZhihuStory() {
        mPresenter.loadStory(story_id);
    }

    @Override
    public void loadFail(String errmsg) {

    }

    @Override
    public void loadSuccess(ZhihuStoryContent zhihuStory) {
        Glide.with(this)
                .load(zhihuStory.getImage())
                .override(width, heigh)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .into(mStoryImg);
        String url = zhihuStory.getShare_url();
        boolean isEmpty = TextUtils.isEmpty(zhihuStory.getBody());
        String mBody = zhihuStory.getBody();
        String[] scc = zhihuStory.getCss();
        //如果返回的html body为空则直接 load url
        if (isEmpty) {
            mZhihudailyWebview.loadUrl(url);
        } else {
            String data = WebUtils.buildHtmlWithCss(mBody, scc, false);
            mZhihudailyWebview.loadDataWithBaseURL(WebUtils.BASE_URL, data, WebUtils.MIME_TYPE, WebUtils.ENCODING, WebUtils.FAIL_URL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubcription();
    }
}

package com.pandaq.mvpdemo.ui;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
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
import com.pandaq.mvpdemo.model.zhihu.ZhihuStoryContent;
import com.pandaq.mvpdemo.presenter.ZhihuStoryInfoPresenter;
import com.pandaq.mvpdemo.ui.IViewBind.IZhihuStoryInfoActivity;
import com.pandaq.mvpdemo.utils.ColorUtils;
import com.pandaq.mvpdemo.utils.DensityUtil;
import com.pandaq.mvpdemo.utils.GlideUtils;
import com.pandaq.mvpdemo.utils.ViewUtils;
import com.pandaq.mvpdemo.utils.WebUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PandaQ on 2016/9/8.
 * email : 767807368@qq.com
 * 知乎日报打开详情页面
 */
public class ZhihuStoryInfoActivity extends SwipeBackActivity implements IZhihuStoryInfoActivity {

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
        setSupportActionBar(mToolbar);
        mToolbarLayout.setTitle(getString(R.string.zhihu_title));
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
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZhihuStoryInfoActivity.this.finish();
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
                .listener(new GlideLoadListener())
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

    class GlideLoadListener implements RequestListener<String, GlideDrawable> {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ZhihuStoryInfoActivity.this.getResources().getDisplayMetrics());
            assert bitmap != null;
            Palette.from(bitmap)
                    //设置最大颜色数
                    .maximumColorCount(16)
                    //去除所有的Filter
                    .clearFilters()
                    //设置用于计算调色板的位图区域
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip)
                    //计算Palette
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            boolean isDark;
                            int lightness = ColorUtils.isDark(palette);
                            //判断是否是黑色主题（其实Demo中用不到，因为没做主题切换）
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }
                            //判断当前系统版本是否 API>21
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                int statusBarColor = getWindow().getStatusBarColor();
                                mToolbarLayout.setContentScrimColor(statusBarColor);
                                // 获取主色调
                                final Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
                                if (topColor != null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                    statusBarColor = ColorUtils.scrimify(topColor.getRgb(), isDark, SCRIM_ADJUSTMENT);
                                    // set a light status bar on M+
                                    if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        ViewUtils.setLightStatusBar(mStoryImg);
                                    }
                                }
                                //设置渐显动画，替换状态栏颜色
                                if (statusBarColor != getWindow().getStatusBarColor()) {
                                    mToolbarLayout.setContentScrimColor(statusBarColor);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mToolbar.setBackgroundColor(getResources().getColor(R.color.trans_toolbar_7c424141, null));
                                    } else {
                                        mToolbar.setBackgroundColor(getResources().getColor(R.color.trans_toolbar_7c424141));
                                    }
                                    ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                            getWindow().getStatusBarColor(), statusBarColor);
                                    statusBarColorAnim.addUpdateListener(new ValueAnimator
                                            .AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            getWindow().setStatusBarColor((int) animation.getAnimatedValue());
                                        }
                                    });
                                    //设置转换颜色的动画时间
                                    statusBarColorAnim.setDuration(1000L);
                                    statusBarColorAnim.setInterpolator(
                                            new AccelerateInterpolator());
                                    statusBarColorAnim.start();
                                }
                            }
                        }
                    });
            return false;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.dispose();
    }
}

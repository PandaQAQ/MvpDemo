package com.pandaq.mvpdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.enums.ClientType;
import com.pandaq.mvpdemo.presenter.MainActivityPresenter;
import com.pandaq.mvpdemo.ui.IViewBind.IHttpsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PandaQ on 2017/3/6.
 * email :767807368@qq.com
 */

public class HttpsActivity extends BaseActivity implements IHttpsActivity {
    @BindView(R.id.https_result)
    TextView mHttpsResult;
    private MainActivityPresenter mPresenter = new MainActivityPresenter(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.https_friendly, R.id.https_unfriendly})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.https_friendly:
                get12306Test(ClientType.TYPE_HTTPSUTILS);
                break;
            case R.id.https_unfriendly:
                get12306Test(ClientType.TYPE_OKHTTPCLIENT);
                break;
        }
    }

    @Override
    public void get12306Test(ClientType type) {
        mPresenter.get12306Test(this, type);
    }

    @Override
    public void showResult(String result) {
        mHttpsResult.setText(result);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.dispose();
    }

}

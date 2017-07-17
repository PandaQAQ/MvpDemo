package com.pandaq.mvpdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pandaq.mvpdemo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PandaQ on 2016/11/20.
 * email : 767807368@qq.com
 */

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tonews, R.id.https_activity, R.id.send_sms, R.id.wheel, R.id.loopers, R.id.downloader,R.id.bottom_menu})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tonews:
                intent = new Intent(this, NewsListActivity.class);
                startActivity(intent);
                break;
            case R.id.https_activity:
                intent = new Intent(this, HttpsActivity.class);
                startActivity(intent);
            case R.id.send_sms:
                intent = new Intent(this, GetSmsActivity.class);
                startActivity(intent);
                break;
            case R.id.wheel:
                intent = new Intent(this, WidgetActivity.class);
                startActivity(intent);
                break;
            case R.id.loopers:
                intent = new Intent(this, LoopersActivity.class);
                startActivity(intent);
                break;
            case R.id.downloader:
                intent = new Intent(this, DownloaderActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_menu:
                intent = new Intent(this, BottomMenuActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}

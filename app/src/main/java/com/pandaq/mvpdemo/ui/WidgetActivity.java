package com.pandaq.mvpdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.pandaq.loopscaleview.LoopScaleView;
import com.pandaq.mvpdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PandaQ on 2017/1/13.
 * email : 767807368@qq.com
 */

public class WidgetActivity extends BaseActivity {
    @BindView(R.id.lsv_scale)
    LoopScaleView mLsvScale;
    @BindView(R.id.tv_value)
    TextView mTvValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        ButterKnife.bind(this);
        mLsvScale.setOnValueChangeListener(new LoopScaleView.OnValueChangeListener() {
            @Override
            public void OnValueChange(int newValue) {
                mTvValue.setText(String.valueOf(newValue));
            }
        });
    }
}

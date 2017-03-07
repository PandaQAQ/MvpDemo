package com.pandaq.mvpdemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pandaq.mvpdemo.Constants;
import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.presenter.GetSmsPresenter;
import com.pandaq.mvpdemo.utils.SmsObserver;
import com.pandaq.mvpdemo.ui.IViewBind.IGetSmsActivity;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PandaQ on 2016/12/13.
 * email : 767807368@qq.com
 */

public class GetSmsActivity extends BaseActivity implements IGetSmsActivity {
    @BindView(R.id.et_send_sms)
    EditText mEtSendSms;
    @BindView(R.id.bt_send)
    Button mBtSend;
    @BindView(R.id.et_send_address)
    EditText mEtSendAddress;
    @BindView(R.id.tv_recevie_sms)
    TextView mTvRecevieSms;
    GetSmsPresenter mPresenter = new GetSmsPresenter(this);
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.SMS_BROADCAST_FILTER)) {
                mTvRecevieSms.setText(intent.getExtras().getString("address") + "\n" + intent.getExtras().getString("body"));

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getsms);
        ButterKnife.bind(this);
        IntentFilter filter = new IntentFilter(Constants.SMS_BROADCAST_FILTER);
        registerReceiver(mReceiver, filter);
        //注册观察者
        registSmsObserve();
    }

    @OnClick(R.id.bt_send)
    public void onClick() {
        mPresenter.sendSms();
    }

    @Override
    public String getAddress() {
        return mEtSendAddress.getText() + "";
    }

    @Override
    public String getBody() {
        return mEtSendSms.getText() + "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
        unregisterReceiver(mReceiver);
    }

    private SmsObserver mObserver;

    public void registSmsObserve() {
        mObserver = new SmsObserver(this, null);
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri, true, mObserver);
    }

    /**
     * 获取短信中的验证码
     */
    public static String getSmsCode(String smsBody) {
        String code = "";
        //这里就简单的以连续6位数字作为验证码的匹配
        Pattern pattern = Pattern.compile("(\\d{6})");
        return code;
    }
}

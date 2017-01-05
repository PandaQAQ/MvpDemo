package com.pandaq.mvpdemo.presenter;

import com.pandaq.mvpdemo.ui.IViewBind.IGetSmsActivity;

import java.util.List;

/**
 * Created by PandaQ on 2016/12/13.
 * email : 767807368@qq.com
 */

public class GetSmsPresenter extends BasePresenter {
    private IGetSmsActivity mActivity;

    public GetSmsPresenter(IGetSmsActivity activity) {
        mActivity = activity;
    }

    public void sendSms() {
        sendSMS(mActivity.getAddress(), mActivity.getBody());
    }

    /**
     * 直接调用短信接口发短信
     *
     * @param phoneNumber
     * @param message
     */
    private void sendSMS(String phoneNumber, String message) {
        //获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }
}

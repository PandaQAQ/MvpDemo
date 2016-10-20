package com.pandaq.mvpdemo.utils;

/**
 * Created by PandaQ on 2016/10/19.
 * email : 767807368@qq.com
 */

public interface OnEventLister<T> {
    void onSuccess(T response);

    void onFail(String errCode, String errMsg);
}

package com.pandaq.mvpdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by PandaQ on 2016/12/13.
 * email : 767807368@qq.com
 */

public class SmsObserver extends ContentObserver {

    private Context mContext;

    public SmsObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange);
        if (uri.toString().equals("content://sms/raw")) {
            Uri inboxUri = Uri.parse("content://sms/inbox");
            Cursor cursor = mContext.getContentResolver().query(inboxUri, null, null, null, "date desc");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    Intent intent = new Intent();
                    intent.setAction("pandaq.mvpdemo.recevieSMS");
                    Bundle bundle = new Bundle();
                    bundle.putString("address", Utils.getContactName(mContext, address));
                    bundle.putString("body", body);
                    intent.putExtras(bundle);
                    mContext.sendBroadcast(intent);
                }
                cursor.close();
            }
        }
    }
}

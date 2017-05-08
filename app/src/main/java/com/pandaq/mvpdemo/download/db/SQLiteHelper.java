package com.pandaq.mvpdemo.download.db;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pandaq.mvpdemo.MyApplication;

/**
 * Created by PandaQ on 2017/5/8.
 * 数据库帮助类
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String dbName = MyApplication.getContext().getPackageName(); //数据库名称
    private static final int version = 1; //数据库版本

    public SQLiteHelper(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

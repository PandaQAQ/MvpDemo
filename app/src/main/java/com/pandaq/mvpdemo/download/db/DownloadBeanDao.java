package com.pandaq.mvpdemo.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pandaq.mvpdemo.download.downloader.DownloadBean;

import java.util.ArrayList;

/**
 * Created by PandaQ on 2017/5/8.
 * 下载对象存储工具类
 */

public class DownloadBeanDao {
    private final String DOWNLOAD_TABLE = "download_been";
    private SQLiteHelper mSQLiteHelper;
    private SQLiteDatabase mDatabase;

    public DownloadBeanDao(Context context) {
        mSQLiteHelper = new SQLiteHelper(context);
    }

    //创建存储下载对象信息的表
    public void createTable() {
        mDatabase = mSQLiteHelper.getWritableDatabase();
        mDatabase.execSQL("create table " + DOWNLOAD_TABLE + " (id integer primary key autoincrement,taskId string,downloaded long," +
                "totalSize long,loadState int,downloadUrl string,savePath string)");
        mDatabase.close();
    }

    /**
     * judge tableName is in your data library or not
     *
     * @return 存在返回真，不存在返回假
     */
    // 判断数据库中的tableName表是否存在
    public boolean tabIsExist() {
        mDatabase = mSQLiteHelper.getWritableDatabase();
        boolean result = false;
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + DOWNLOAD_TABLE + "' ";
            cursor = mDatabase.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            mDatabase.close();
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    //删除存储下载对象信息的表
    public void deleteTable() {
        mDatabase = mSQLiteHelper.getWritableDatabase();
        mDatabase.execSQL("drop table " + DOWNLOAD_TABLE);
        mDatabase.close();
    }

    /**
     * 插入一条下载信息
     *
     * @param bean 下载信息对象
     */
    public void insert(DownloadBean bean) {
        mDatabase = mSQLiteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("taskId", bean.getTaskId());
        values.put("downloaded", bean.getDownloaded());
        values.put("totalSize", bean.getTotalSize());
        values.put("downloadUrl", bean.getDownloadUrl());
        values.put("savePath", bean.getSavePath());
        mDatabase.insert(DOWNLOAD_TABLE, null, values);
        mDatabase.close();
    }

    /**
     * 更新一条下载数据
     *
     * @param bean 被更新的下载数据
     */
    public void update(DownloadBean bean) {
        ContentValues values = new ContentValues();
        values.put("downloaded", bean.getDownloaded());
        mDatabase = mSQLiteHelper.getWritableDatabase();
        mDatabase.update(DOWNLOAD_TABLE, values, "taskId = ?", new String[]{bean.getTaskId()});
        mDatabase.close();
    }

    /**
     * 删除一条下载数据
     *
     * @param taskId 被删除的下载信息 taskId
     */
    public void delete(String taskId) {
        mDatabase = mSQLiteHelper.getWritableDatabase();
        mDatabase.delete(DOWNLOAD_TABLE, "taskId = ?", new String[]{taskId});
        mDatabase.close();
    }

    /**
     * 根据 taskId 查询出对应的下载信息
     *
     * @param taskId 查询的下载信息对应的 taskId
     * @return 查询到的下载信息实体
     */
    public DownloadBean query(String taskId) {
        mDatabase = mSQLiteHelper.getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from " + DOWNLOAD_TABLE + " where taskId = " + taskId, null);
        DownloadBean bean = null;
        if (cursor.moveToFirst()) {
            String task_id = cursor.getString(cursor.getColumnIndex("taskId"));
            long downloaded = cursor.getLong(cursor.getColumnIndex("downloaded"));
            long totalSize = cursor.getLong(cursor.getColumnIndex("totalSize"));
            String downloadUrl = cursor.getString(cursor.getColumnIndex("downloadUrl"));
            String savePath = cursor.getString(cursor.getColumnIndex("savePath"));
            bean = new DownloadBean.Builder()
                    .downloadUrl(downloadUrl)
                    .taskId(task_id)
                    .downloaded(downloaded)
                    .totalSize(totalSize)
                    .savePath(savePath)
                    .build();
        }
        cursor.close();
        return bean;
    }

    /**
     * 查询出所有的下载信息记录
     *
     * @return 下载信息列表
     */
    public ArrayList<DownloadBean> queryAll() {
        ArrayList<DownloadBean> downloadbeen = new ArrayList<>();
        mDatabase = mSQLiteHelper.getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from " + DOWNLOAD_TABLE, null);
        while (cursor.moveToNext()) {
            String task_id = cursor.getString(cursor.getColumnIndex("taskId"));
            long downloaded = cursor.getLong(cursor.getColumnIndex("downloaded"));
            long totalSize = cursor.getLong(cursor.getColumnIndex("totalSize"));
            String downloadUrl = cursor.getString(cursor.getColumnIndex("downloadUrl"));
            String savePath = cursor.getString(cursor.getColumnIndex("savePath"));
            DownloadBean bean = new DownloadBean.Builder()
                    .downloadUrl(downloadUrl)
                    .taskId(task_id)
                    .downloaded(downloaded)
                    .totalSize(totalSize)
                    .savePath(savePath)
                    .build();
            downloadbeen.add(bean);
        }
        cursor.close();
        return downloadbeen;
    }
}

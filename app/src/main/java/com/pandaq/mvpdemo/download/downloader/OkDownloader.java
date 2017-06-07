package com.pandaq.mvpdemo.download.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pandaq.mvpdemo.download.db.DownloadBeanDao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by PandaQ on 2017/5/4.
 * 下载管理类
 */

public class OkDownloader {
    /*上下文*/
    private Context mContext;
    /*下载的 client*/
    private OkHttpClient mClient;
    /*线程池中最大线程数*/
    private int mMaxTaskSize;
    /*key:taskId,value:每一项下载任务对应的 下载对象 的列表*/
    private HashMap<String, DownloadBean> downloadBeen = new HashMap<>();
    /*用于主线程消息通知的 handler*/
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DownloadBeanDao mBeanDao;

    /**
     * 设置最大线程数
     *
     * @param maxTaskSize 最大线程数
     */
    private void setMaxTaskSize(int maxTaskSize) {
        this.mMaxTaskSize = maxTaskSize;
        ThreadPoolManager.instance().setMaxPoolSize(mMaxTaskSize);
    }

    /**
     * 获取某一taskId对应的下载任务的状态
     *
     * @param taskId taskId
     * @return 任务状态
     */
    private DownloadState getDownloadState(String taskId) {
        return downloadBeen.containsKey(taskId) ? downloadBeen.get(taskId).getLoadState() : DownloadState.STATE_NEW;
    }

    public void start() {
        for (Object o : downloadBeen.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            start((DownloadBean) entry.getValue());
        }
    }

    private void start(DownloadBean bean) {
        //保存下载对象
        saveDownloadBean(bean);
        //只有新建任务，暂停任务，出错任务三种状态能创建一个新的下载
        DownloadState state = bean.getLoadState();
        if (state == DownloadState.STATE_NEW || state == DownloadState.STATE_ERROR
                || state == DownloadState.STATE_PAUSE) {
            DownloadTask downloadTask = new DownloadTask(this, bean);
            bean.setLoadState(DownloadState.STATE_WAITTING);
            notifyDownloadUpdate(bean);
            ThreadPoolManager.instance().execute(downloadTask);
            Log.d("OkDownLoader", "enqueue download task into thread pool!");
        } else {
            Log.d("OkDownLoader", "The state of current task is " + bean.getLoadState() + ",  can't be downloaded!");
        }
    }

    /**
     * 将下载对象存入数据库
     *
     * @param bean 下载对象
     */
    private void saveDownloadBean(DownloadBean bean) {
        // BeanDao 对象不存在先实例化对象
        if (mBeanDao == null) {
            mBeanDao = new DownloadBeanDao(mContext);
        }
        // 存储下载信息的表不存在先创建表
        if (!mBeanDao.tabIsExist()) {
            mBeanDao.createTable();
        }
        mBeanDao.insert(bean);
    }

    /**
     * 更新下载对象
     *
     * @param bean 下载对象
     */
    private void updateDownloadBean(DownloadBean bean) {
        // BeanDao 对象不存在先实例化对象
        if (mBeanDao == null) {
            mBeanDao = new DownloadBeanDao(mContext);
        }
        mBeanDao.update(bean);
    }

    /**
     * 根据 taskId 删除下载对象
     *
     * @param taskId 下载对象 ID
     */
    private void deleteDownloadBean(String taskId, boolean deleteFile) {
        DownloadBean bean = getDownloadBean(taskId);
        if (bean != null) {
            deleteDownloadBean(bean, deleteFile);
        }
    }

    /**
     * 删除下载对象
     *
     * @param bean 被删除的对象
     */
    private void deleteDownloadBean(DownloadBean bean, boolean deleteFile) {
        pause(bean.getTaskId());
        //数据库删除
        // BeanDao 对象不存在先实例化对象
        if (mBeanDao == null) {
            mBeanDao = new DownloadBeanDao(mContext);
        }
        mBeanDao.delete(bean.getTaskId());
        //删除下载文件
        if (deleteFile) {
            new File(bean.getSavePath()).delete();
        }
    }


    /**
     * 根据 taskId 从下载列表中获取下载对象
     *
     * @param taskId 任务ID
     * @return taskId 对应的 DownloadBean
     */
    private DownloadBean getDownloadBean(String taskId) {
        return downloadBeen.get(taskId);
    }

    /**
     * 暂停下载任务
     *
     * @param taskId 被暂停的下载任务 ID
     */
    private void pause(String taskId) {
        DownloadBean bean = getDownloadBean(taskId);
        if (bean != null) {
            bean.setLoadState(DownloadState.STATE_PAUSE);
        }
    }

    /**
     * 通知所有的监听器下载更新
     *
     * @param bean 下载对象
     */
    public void notifyDownloadUpdate(final DownloadBean bean) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                DownloadObserver observer = bean.getObserver();
                if (observer != null) {
                    observer.onDownloadUpdate(bean);
                }
            }
        });
    }

    /**
     * 下载进度回调
     */
    public interface DownloadObserver {
        void onDownloadUpdate(DownloadBean downloadInfo);

        void onFinished(DownloadBean downloadInfo);
    }

    public static class Builder {
        OkHttpClient mClien1t = new OkHttpClient();
        private int mMaxTaskSize;
        private Context mContext;
        private OkHttpClient mClient;
        /*key:taskId,value:每一项下载任务对应的 下载对象 的列表*/
        private HashMap<String, DownloadBean> downloadBeen = new HashMap<>();

        public Builder addDownloadBean(DownloadBean bean) {
            if (bean != null && !downloadBeen.containsKey(bean.getTaskId())) {
                downloadBeen.put(bean.getTaskId(), bean);
            }
            return this;
        }

        public Builder context(Context context) {
            mContext = context;
            return this;
        }

        public Builder client(OkHttpClient client) {
            mClient = client;
            return this;
        }

        public Builder maxTask(int maxTaskSize) {
            mMaxTaskSize = maxTaskSize;
            return this;
        }

        public OkDownloader build() {
            OkDownloader downloader = new OkDownloader();
            downloader.mClient = this.mClient;
            downloader.mContext = this.mContext;
            downloader.mMaxTaskSize = this.mMaxTaskSize;
            downloader.downloadBeen = this.downloadBeen;
            return downloader;
        }
    }
}

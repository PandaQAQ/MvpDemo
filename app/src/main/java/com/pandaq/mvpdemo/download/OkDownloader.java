package com.pandaq.mvpdemo.download;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

/**
 * Created by PandaQ on 2017/5/4.
 * 下载管理类
 */

public class OkDownloader {
    private static OkDownloader sLoader;
    /*最大下载线程数，默认为核心线程数*/
    private int maxTaskSize;
    /*执行下载任务的 client*/
    private OkHttpClient mOkHttpClient = null;
    /*任务ID，用于存入数据库保存某一次下载任务的状态*/
    private String mTaskId;
    /*key:taskId,value:每一项下载任务对应的 Observer 的列表*/
    private HashMap<String, ArrayList<DownloadObserver>> observerMap = new HashMap<>();
    /*key:taskId,value:每一项下载任务对应的 下载对象 的列表*/
    private HashMap<String, DownloadBean> downloadBeen = new HashMap<>();
    /*用于主线程消息通知的 handler*/
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @return OkDownLoader 的单例
     */
    public static OkDownloader create() {
        if (sLoader == null) {
            synchronized (OkDownloader.class) {
                if (sLoader == null) {
                    sLoader = new OkDownloader();
                }
            }
        }
        return sLoader;
    }

    /**
     * 设置最大线程数
     *
     * @param maxTaskSize
     */
    private void setMaxTaskSize(int maxTaskSize) {
        this.maxTaskSize = maxTaskSize;
        ThreadPoolManager.instance().setMaxPoolSize(maxTaskSize);
    }

    /**
     * 获取某一taskId对应的下载任务的状态
     *
     * @param taskId taskId
     * @return 任务状态
     */
    private int getDownloadState(String taskId) {
        return downloadBeen.containsKey(taskId) ? downloadBeen.get(taskId).getLoadState() : DownloadState.STATE_NEW.getNum();
    }


    /**
     * 获取当前正在执行的任务
     */
    public static void getCurrentTask() {

    }

    /**
     * 根据任务 ID 删除任务
     *
     * @param taskId     任务ID
     * @param deleteFile 是否删除下载文件
     */
    public static void deleteByTask(String taskId, boolean deleteFile) {

    }

    /**
     * 根据任务 ID 删除数据库记录
     *
     * @param taskId 任务ID
     */
    public static void deleteByTask(String taskId) {
        deleteByTask(taskId, false);
    }

    public void start(String taskId, String downloadUrl, String savePath) {
        DownloadBean bean = downloadBeen.get(taskId);
        if (bean != null) {
            bean.setDownloadUrl(downloadUrl);
        } else {
            bean = new DownloadBean.Builder()
                    .savePath(savePath)
                    .downloadUrl(downloadUrl)
                    .taskId(taskId)
                    .build();
            downloadBeen.put(taskId, bean);
        }
        //保存下载对象
        //saveDownloadBean();
        //只有新建任务，暂停任务，出错任务三种状态能创建一个新的下载
        int state = bean.getLoadState();
        if (state == DownloadState.STATE_NEW.getNum() || state == DownloadState.STATE_ERROR.getNum()
                || state == DownloadState.STATE_PAUSE.getNum()) {
            DownloadTask downloadTask = new DownloadTask(this, bean);
            bean.setLoadState(DownloadState.STATE_WAITTING.getNum());
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

    }

    /**
     * 更新下载对象
     *
     * @param bean 下载对象
     */
    private void updateDownloadBean(DownloadBean bean) {

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
        //downloadInfoDao.delete(bean);
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
            bean.setLoadState(DownloadState.STATE_PAUSE.getNum());
        }
    }

    /**
     * 将监听与taskId进行对应
     *
     * @param observer 监听对象
     * @param taskId   任务ID
     */
    private void addObserver(DownloadObserver observer, String taskId) {
        if (taskId == null) return;
        ArrayList<DownloadObserver> list = observerMap.get(taskId);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(observer);
        observerMap.put(taskId, list);
        Log.d("OkDownloader", "add observer successful!");
    }

    /**
     * 移除任务监听
     *
     * @param observer 监听对象
     * @param taskId   任务ID
     */
    private void removeObserver(DownloadObserver observer, String taskId) {
        if (taskId == null) return;
        if (observerMap.containsKey(taskId)) {
            ArrayList<DownloadObserver> list = observerMap.get(taskId);
            if (list != null) {
                list.remove(observer);
                observerMap.put(taskId, list);
            }
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
                ArrayList<DownloadObserver> observers = observerMap.get(bean.getTaskId());
                if (observers == null || observers.size() <= 0) return;
                for (DownloadObserver observer : observers) {
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
    }
}

package com.pandaq.mvpdemo.download;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by PandaQ on 2017/5/8.
 * 下载线程池管理类
 */

public class ThreadPoolManager {
    private static ThreadPoolManager sPoolManager;
    private ThreadPoolExecutor mPoolExecutor;
    /*核心线程数*/
    int corePoolSize;
    /*池内最大线程数*/
    int maxPoolSize;
    /*线程空闲退出时间*/
    long keepAliveTime;
    private BlockingQueue<Runnable> workers = new LinkedBlockingQueue<>();
    private RejectedExecutionHandler mHandler = new ThreadPoolExecutor.AbortPolicy();

    public static ThreadPoolManager instance() {
        if (sPoolManager == null) {
            synchronized (ThreadPoolManager.class) {
                if (sPoolManager == null) {
                    sPoolManager = new ThreadPoolManager();
                }
            }
        }
        return sPoolManager;
    }

    private ThreadPoolManager() {
        //calculate corePoolSize, which is the same to AsyncTask.
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maxPoolSize = corePoolSize;
        //we custom the threadpool.
        mPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, //is 3 in avd.
                maxPoolSize, //which is unuseless
                keepAliveTime,
                TimeUnit.HOURS,
                workers,
                Executors.defaultThreadFactory(),
                mHandler
        );
    }

    public void setMaxPoolSize(int size) {
        this.maxPoolSize = size;
    }

    /**
     * 往线程池中添加任务
     *
     * @param runnable 被添加的任务
     */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            mPoolExecutor.execute(runnable);
        }
    }

    /**
     * 从线程池中移除任务
     *
     * @param runnable 被移除的任务
     */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            mPoolExecutor.remove(runnable);
        }
    }
}

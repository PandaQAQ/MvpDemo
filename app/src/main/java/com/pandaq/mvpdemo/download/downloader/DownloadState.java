package com.pandaq.mvpdemo.download.downloader;

/**
 * Created by PandaQ on 2017/5/8.
 * 下载状态枚举类
 */

/**
 * STATE_NEW : 新建的下载任务，并且未执行
 * STATE_LOADING : 正在下载的任务
 * STATE_PAUSE : 暂停任务
 * STATE_FINISH : 任务完成
 * STATE_ERROR : 任务出错
 * STATE_WAITTING : 任务等待中
 */
public enum DownloadState {

    STATE_NEW(0),
    STATE_LOADING(1),
    STATE_PAUSE(2),
    STATE_FINISH(3),
    STATE_ERROR(4),
    STATE_WAITTING(5);

    private int num;

    DownloadState(int num) {
        this.num = num;
    }


    public int getNum() {
        return num;
    }
}

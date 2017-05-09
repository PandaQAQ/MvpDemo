package com.pandaq.mvpdemo.download.downloader;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by PandaQ on 2017/5/8.
 * 下载任务具体实现类
 */

public class DownloadTask implements Runnable {
    private OkDownloader mDownLoader;
    private DownloadBean mDownloadBean;
    private ResponseBody body;
    private long contentLength;

    public DownloadTask(OkDownloader downLoader, DownloadBean downloadBean) {
        mDownLoader = downLoader;
        mDownloadBean = downloadBean;
    }

    @Override
    public void run() {
        Log.d("DownLoadTask", "start downloading...");
        mDownloadBean.setLoadState(DownloadState.STATE_LOADING);
        mDownLoader.notifyDownloadUpdate(mDownloadBean);
        File file = new File(mDownloadBean.getSavePath());
        if (file.exists() || file.length() != mDownloadBean.getDownloaded()) {
            boolean result = file.delete();
            if (!result) {
                processErrerState();
                return;
            }
            mDownloadBean.setDownloaded(0);
        }
        //获取下载文件信息
        InputStream inputStream = downLoad();
        Log.d("DownLoadTask", contentLength + "");
        mDownloadBean.setTotalSize(contentLength);
        if (inputStream != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file, true);
                byte[] buffer = new byte[1024 * 8];
                int len;
                long currentLength = mDownloadBean.getDownloaded();
                while (mDownloadBean.getLoadState() == DownloadState.STATE_LOADING && mDownloadBean
                        .getDownloaded() < mDownloadBean.getTotalSize() && mDownloadBean.getTotalSize() > 0 &&
                        (len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    currentLength += len;
                    mDownloadBean.setDownloaded(currentLength);
                    mDownLoader.notifyDownloadUpdate(mDownloadBean);
                    //同时更新数据库中的信息，感觉会有性能损耗（待定）
                }
            } catch (IOException e) {
                e.printStackTrace();
                processErrerState();
            } finally {
                body.close();
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file.length() == mDownloadBean.getTotalSize()
                        && mDownloadBean.getDownloaded() == mDownloadBean.getTotalSize()
                        && mDownloadBean.getLoadState() == DownloadState.STATE_LOADING) {
                    mDownloadBean.setLoadState(DownloadState.STATE_FINISH);
                }
                mDownLoader.notifyDownloadUpdate(mDownloadBean);
//                mDownLoader.updateDownloadInfo(mDownloadBean);
                Log.d("download task is over: ", mDownloadBean.toString());
            }
        } else {
            processErrerState();
        }
    }

    private InputStream downLoad() {
        InputStream is = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(mDownloadBean.getDownloadUrl())
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                body = response.body();
                contentLength = body.contentLength();
                is = body.byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * process error state
     */
    public void processErrerState() {
        mDownloadBean.setLoadState(DownloadState.STATE_ERROR);
        mDownLoader.notifyDownloadUpdate(mDownloadBean);
    }
}

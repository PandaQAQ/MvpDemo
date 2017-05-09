package com.pandaq.mvpdemo.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.pandaq.mvpdemo.R;
import com.pandaq.mvpdemo.download.SecretUtils;
import com.pandaq.mvpdemo.download.downloader.DownloadBean;
import com.pandaq.mvpdemo.download.downloader.DownloadState;
import com.pandaq.mvpdemo.download.downloader.OkDownloader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PandaQ on 2017/5/9.
 * 下载功能activity
 */

public class DownloaderActivity extends BaseActivity {
    @BindView(R.id.url)
    EditText mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_download)
    public void onViewClicked() {
        String url = mUrl.getText().toString();
        DownloadBean downloadBean = new DownloadBean.Builder()
                .downloadUrl(url)
                .taskId(SecretUtils.getMD5(url))
                .savePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.apk")
                .listener(new OkDownloader.DownloadObserver() {
                    @Override
                    public void onDownloadUpdate(DownloadBean downloadInfo) {
                    }

                    @Override
                    public void onFinished(DownloadBean downloadInfo) {
                        System.out.println(downloadInfo.getDownloadUrl() + "------下载完成");
                    }
                })
                .loadState(DownloadState.STATE_NEW)
                .build();
        DownloadBean downloadBean1 = new DownloadBean.Builder()
                .downloadUrl("http://oddbiem8l.bkt.clouddn.com/AndroidScreenView.jpg")
                .taskId(SecretUtils.getMD5("http://oddbiem8l.bkt.clouddn.com/AndroidScreenView.jpg"))
                .savePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.jpg")
                .listener(new OkDownloader.DownloadObserver() {
                    @Override
                    public void onDownloadUpdate(DownloadBean downloadInfo) {
                    }

                    @Override
                    public void onFinished(DownloadBean downloadInfo) {
                        System.out.println(downloadInfo.getDownloadUrl() + "------下载完成");
                    }
                })
                .loadState(DownloadState.STATE_NEW)
                .build();
        OkDownloader downloader = new OkDownloader.Builder()
                .context(this)
                .addDownloadBean(downloadBean)
                .addDownloadBean(downloadBean1)
                .maxTask(5)
                .build();
        downloader.start();
    }
}

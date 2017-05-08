package com.pandaq.mvpdemo.download;

/**
 * Created by PandaQ on 2017/5/4.
 * 下载对象封装
 */

public class DownloadBean {
    private String taskId;//下载任务的ID，作为一次任务的唯一标识
    private long downloaded;//已下载文件的长度
    private long totalSize;//文件总大小
    private int loadState;//任务下载状态
    private String downloadUrl;//下载地址
    private String savePath;//文件保存地址

    private DownloadBean(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getLoadState() {
        return loadState;
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    static class Builder {
        private String taskId;//下载任务的ID，作为一次任务的唯一标识
        private long downloaded;//已下载文件的长度
        private long totalSize;//文件总大小
        private int loadState;//任务下载状态
        private String downloadUrl;//下载地址
        private String savePath;//文件保存地址

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder downloaded(long downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public Builder totalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        public Builder loadState(DownloadState state) {
            this.loadState = state.getNum();
            return this;
        }

        public Builder downloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public Builder savePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public DownloadBean build() {
            DownloadBean bean = new DownloadBean(this.downloadUrl);
            bean.taskId = this.taskId;
            bean.downloaded = this.downloaded;
            bean.totalSize = this.totalSize;
            bean.loadState = this.loadState;
            bean.savePath = this.savePath;
            return bean;
        }
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id='" + taskId + '\'' +
                ", downloaded=" + downloaded +
                ", totalSize=" + totalSize +
                ", loadState=" + loadState +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", path='" + savePath + '\'' +
                '}';
    }
}

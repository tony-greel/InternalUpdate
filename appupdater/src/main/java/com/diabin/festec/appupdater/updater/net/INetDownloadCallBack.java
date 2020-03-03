package com.diabin.festec.appupdater.updater.net;

import java.io.File;

/**
 * 异步中下载返回的结果
 */
public interface INetDownloadCallBack {

    void success(File apkFile); // 下载成功后的目录
    void progress(int progress); // 下载进度
    void failed(Throwable throwable);  // 下载失败
}

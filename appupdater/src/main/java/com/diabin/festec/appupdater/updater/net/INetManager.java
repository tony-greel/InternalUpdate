package com.diabin.festec.appupdater.updater.net;

import java.io.File;

/**
 * 支持get请求，下载文件，暂停下载文件
 */
public interface INetManager {

    void get(String url, INetCallBack callBack,Object tag);

    void download(String url, File targetFile,
                  INetDownloadCallBack callBack,
                  Object tag);

    void cancel(Object tag);

}

package com.diabin.festec.appupdater.updater.net;

/**
 * 异步下get请求返回的结果
 */
public interface INetCallBack {
    void success(String response);
    void failed(Throwable throwable);
}

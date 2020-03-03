package com.diabin.festec.appupdater.updater;

import com.diabin.festec.appupdater.MainActivity;
import com.diabin.festec.appupdater.updater.net.INetManager;
import com.diabin.festec.appupdater.updater.net.OkHttpNetManager;

public class AppUpdater {

    // 单例模式
    private static AppUpdater sInstance = new AppUpdater();

    public static AppUpdater getInstance(){
        return sInstance;
    }

    /**
     * 网络请求下载模块
     */
    private INetManager mNetManager = new OkHttpNetManager();

    public INetManager getNetManager(){
        return mNetManager;
    }
}

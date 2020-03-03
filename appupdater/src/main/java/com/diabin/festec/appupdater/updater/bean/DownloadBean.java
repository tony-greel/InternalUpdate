package com.diabin.festec.appupdater.updater.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DownloadBean implements Serializable {

    /**
     * title : 4.5.0更新啦！
     * content : 1. 优化了阅读体验；
     2. 上线了 hyman 的课程；
     3. 修复了一些已知问题。
     * url : http://59.110.162.30/v450_imooc_updater.apk
     * md5 : 14480fc08932105d55b9217c6d2fb90b
     * versionCode : 450
     */

    public String title;
    public String content;
    public String url;
    public String md5;
    public String versionCode;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 可单独的在bean做这个数据类型的单独解析，这样可以减少代码的耦合，
     * 当然也可以封装成解析工具类进行解析
     * @param response
     * @return
     */
    public static DownloadBean parse(String response){

        try {
            JSONObject repJson = new JSONObject(response);
            String title = repJson.optString("title");
            String content = repJson.optString("content");
            String url = repJson.optString("url");
            String md5 = repJson.optString("md5");
            String versionCode = repJson.optString("versionCode");

            DownloadBean bean = new DownloadBean();
            bean.title = title;
            bean.content = content;
            bean.url = url;
            bean.md5 = md5;
            bean.versionCode = versionCode;

            return bean;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

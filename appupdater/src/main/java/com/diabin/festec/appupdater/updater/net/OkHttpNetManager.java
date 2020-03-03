package com.diabin.festec.appupdater.updater.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpNetManager implements INetManager {

    // 尽量不要重复创建OkHttpClient，这里可以生产静态
    private static OkHttpClient sOkHttpClient;

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 如果遇到Https无法握手使用builder.sslSocketFactory设置证书相关
     */
    public OkHttpNetManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        sOkHttpClient = builder.build();
    }

    @Override
    public void get(String url, final INetCallBack callBack, Object tag) {
        //
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().tag(tag).build();
        Call call = sOkHttpClient.newCall(request);
//        Response response = call.execute(); 同步操作
        // 异步操作 开启一个线程，开启一个队列
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                // 非UI线程
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String string = response.body().string();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(string);
                        }
                    });
                }catch (Throwable e){
                    e.printStackTrace();
                    callBack.failed(e);
                }
            }
        });
    }

    @Override
    public void download(String url,
                         final File targetFile,
                         final INetDownloadCallBack callBack,Object tag) {
        // 判断文件是否存在，如果不存在则创建他的文件夹
        if (!targetFile.exists()){
            targetFile.getParentFile().mkdirs();
        }

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).get().tag(tag).build();
        Call call = sOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 保存文件
                InputStream is = null; // 输入流
                OutputStream os = null; // 输出流

                is = response.body().byteStream(); // 获得输入流
                os = new FileOutputStream(targetFile); // 获得输出流

                Log.d("WSS",is.toString());
                Log.d("WSS",os.toString());

                final long totalLen = response.body().contentLength(); //获取文件总长度
                // 通过输入流往输出流里面写入文件
                byte[] buffer = new byte[8*1024] ; // 缓存数组，buffer相当于保存的文件大小
                long curLen = 0; // 当前写入的字节数
                int bufferLen = 0; // 内容字节

                try {
                    // 在读文件中不断的往buffer中读取内容，不等于-1，就是说没有读完这个内容字节
                    while(!call.isCanceled() && (bufferLen = is.read(buffer)) != -1){
                        os.write(buffer,0,bufferLen); // 往buffer数组中写入bufferLen个字节
                        os.flush();// 每写一次都调用这个方法
                        curLen += bufferLen; // 当前写了多少个字节

                        final long finalCurLen = curLen;
                        sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 读取进度
                                callBack.progress((int) (finalCurLen * 1.0f / totalLen * 100));
                            }
                        });
                    }

                    if (call.isCanceled()){
                        return;
                    }

                    try {
                        targetFile.setExecutable(true,false);
                        targetFile.setReadable(true,false);
                        targetFile.setWritable(true,false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sHandler.post(() -> callBack.success(targetFile));
                } catch (final Throwable e) {
                    if (call.isCanceled()){
                        return;
                    }
                    e.printStackTrace();
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failed(e);
                        }
                    });
                }finally {
                    if (is != null){
                        is.close();
                    }
                    if (os != null){
                        os.close();
                    }
                }
            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalle = sOkHttpClient.dispatcher().queuedCalls();
        if (queuedCalle != null){
            for (Call call : queuedCalle){
                if (tag.equals(call.request().tag())){
                    Log.d("LJJ-queuedCalle","find call = "+tag);
                    call.cancel();
                }
            }
        }

        List<Call> runningCalls = sOkHttpClient.dispatcher().runningCalls();
        if (runningCalls != null){
            for (Call call : runningCalls ){
                if (tag.equals(call.request().tag())){
                    Log.d("LJJ-runningCalls","find call = "+tag);
                    call.cancel();
                }
            }
        }
    }
}

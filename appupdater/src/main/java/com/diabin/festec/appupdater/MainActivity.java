package com.diabin.festec.appupdater;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.diabin.festec.appupdater.updater.AppUpdater;
import com.diabin.festec.appupdater.updater.bean.DownloadBean;
import com.diabin.festec.appupdater.updater.net.INetCallBack;
import com.diabin.festec.appupdater.updater.net.INetDownloadCallBack;
import com.diabin.festec.appupdater.updater.ui.UpdateVersionShowDialog;
import com.diabin.festec.appupdater.updater.utils.AppUtils;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button_1, button_2, button_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        button_1 = findViewById(R.id.but_1);
        button_2 = findViewById(R.id.but_2);
        button_3 = findViewById(R.id.but_3);

        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
    }

    /**
     * 点击操作
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.but_1){
            requestLink();
        }

        if (v.getId() == R.id.but_2){

        }

        if (v.getId() == R.id.but_3){

        }
    }

    /**
     * 读取文件
     */
    private void getReadFile(View view){
        try {
            FileInputStream fis = openFileInput("file.txt");
            byte[] bytes = new byte[20];
            fis.read(bytes);
            System.out.println("content:" + new String(bytes));
            fis.close();
        }catch (Exception e){

        }finally {

        }
    }

    /**
     * get请求链接操作
     */
    private void requestLink() {
        String url = "http://59.110.162.30/app_updater_version.json";
        AppUpdater.getInstance().getNetManager()
                .get(url, new INetCallBack() {
                    @Override
                    public void success(String response) {
                        Log.d("LJJ",response);
                        // 解析 json
                        // 做版本匹配
                        // 如果需要更新：弹框，点击下载
                        DownloadBean downloadBean = DownloadBean.parse(response);
                        if (downloadBean == null){
                            return;
                        }
                        // 检测：是否需要弹窗
                        try {
                            long versionCode = Long.parseLong(downloadBean.versionCode);
                            if(versionCode <= AppUtils.getVersionCode(MainActivity.this)){
                                Toast.makeText(MainActivity.this, "已经是最新版本无需更新", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "版本检测版本号异常", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 弹框
                        UpdateVersionShowDialog.show(MainActivity.this,downloadBean);

                    }

                    @Override
                    public void failed(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(MainActivity.this, "版本更新接口请求失败", Toast.LENGTH_SHORT).show();
                    }
                },MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUpdater.getInstance().getNetManager().cancel(this);
    }
}

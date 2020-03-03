package com.diabin.festec.appupdater.updater.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.diabin.festec.appupdater.R;
import com.diabin.festec.appupdater.updater.AppUpdater;
import com.diabin.festec.appupdater.updater.bean.DownloadBean;
import com.diabin.festec.appupdater.updater.net.INetDownloadCallBack;
import com.diabin.festec.appupdater.updater.utils.AppUtils;
import com.diabin.festec.appupdater.updater.utils.SharePreUtil;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class UpdateVersionShowDialog extends DialogFragment {

    private static final String KEY_DOWNLOAD_BEAN = "download_bean";

    private DownloadBean mDownloadBean;

    public TextView tvTitle, tvContent;
    public Button button_update;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDownloadBean = (DownloadBean) arguments.getSerializable(KEY_DOWNLOAD_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_updater, container, false);
        bindEvents(view);
        return view;
    }

    private void bindEvents(final View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        button_update = view.findViewById(R.id.tv_update);

        tvTitle.setText(mDownloadBean.title);
        tvContent.setText(mDownloadBean.content);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false); // 禁止重复点击

                File targetFile = new File(getActivity().getCacheDir(), "ljj.apk");
                Log.d("LJJ:", "检测当前目录下是否有缓存打印出地址:" + targetFile);
                if (!SharePreUtil.getString(getContext(),"a","").equals("")) {
                    AppUtils.installApk(getActivity(), targetFile);
                    v.setEnabled(true); // 禁止重复点击
                } else {
                    successDownload(mDownloadBean, targetFile, v);
                }
            }
        });
    }

    public static void show(FragmentActivity activity, DownloadBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DOWNLOAD_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");
    }

    /**
     * 隐藏状态栏
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 当取消弹窗，则取消下载
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("LJJ", "onDismiss");
        AppUpdater.getInstance().getNetManager().cancel(this);
    }

    /**
     * 下载成功的操作
     *
     * @param downloadBean
     * @param targetFile
     */
    private void successDownload(DownloadBean downloadBean,
                                 final File targetFile, final View view) {
        AppUpdater.getInstance().getNetManager()
                .download(downloadBean.url, targetFile, new INetDownloadCallBack() {
                    @Override
                    public void success(File apkFile) {
                        // 安装代码
                        view.setEnabled(true);
                        Log.d("LJJ :下载路径", "success" + apkFile.getAbsolutePath());
                        dismiss();

                        String fileMd5 = AppUtils.getFileMd5(targetFile);
                        Log.d("LJJ", "MD5 = " + fileMd5);

                        if (fileMd5 != null && fileMd5.equals(mDownloadBean.md5)) {
                            AppUtils.installApk(getActivity(), apkFile);
                        } else {
                            Toast.makeText(getContext(), "MD5 检测失败", Toast.LENGTH_SHORT).show();
                        }
                        SharePreUtil.putString(getContext(), "a", "第一次下载完成");
                    }

                    @Override
                    public void progress(int progress) {
                        // 更新界面的代码
                        Log.d("LJJ", "success" + progress);
                        button_update.setText(progress + "%");
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        view.setEnabled(true);
                        Toast.makeText(getContext(), "文件下载失败", Toast.LENGTH_SHORT).show();
                    }
                }, UpdateVersionShowDialog.this);
    }
}

package com.diabin.festec.appupdater.updater.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.diabin.festec.appupdater.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.core.content.FileProvider;

public class AppUtils {
    /**
     * 检测当前App的版本号进行比对
     *
     * @param context
     * @return
     */
    public static long getVersionCode(Context context) {
        PackageManager packageManager =
                context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packageInfo.getLongVersionCode();
            } else {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 跳转系统界面安装APk
     * @param activity
     * @param apkFile
     */
    public static void installApk(Activity activity, File apkFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

    /**
     * Md5验证算法
     * @param targetFile
     * @return
     */
    public static String getFileMd5(File targetFile) {
        if (targetFile == null || !targetFile.isFile()) {
            return null;
        }

        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(targetFile);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        byte[] result = digest.digest();
        BigInteger bigInteger = new BigInteger(1, result);
        return bigInteger.toString(16);
    }
}

package com.mashangyou.golfprint.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


import com.blankj.utilcode.util.LogUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.interfac.UpdatePb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;


/**
 * Created by zs on 2016/7/8.
 */
public class DownLoadService extends Service {

    /**
     * 目标文件存储的文件夹路径
     */

    /**
     * 目标文件存储的文件名
     */
    private static final String savePath = Environment.getExternalStorageDirectory() + File.separator + "MyPet";
    private static String saveFileName = savePath + "/"
            + "golf_print.apk";
    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;
    private Context mContext;
    private int preProgress = 0;
    private int NOTIFY_ID = 1;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private long progress;

    private Thread downLoadThread;
    private String apkUrl = new String();
    private boolean interceptFlag = false;
    private UpdatePb updatePb;
    private DownloadBinder binder = new DownloadBinder();

    //    private MyUpdateDialog mpDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    //UpdateManager.setProgress(preProgress);
                    updateNotification(progress);
                    if (isUpdate == 1) {
                        updatePb.update(preProgress);
                    }
                    break;
                case DOWN_OVER:
                    cancelNotification();
                    if (isUpdate==1){
                        updatePb.updateSuccess(saveFileName);
                    }else{
                        installApk();
                    }

                    //TToast.showShort(mContext, "下载完成现在安装");
                    //UpdateManager.miss();

                    break;
                default:
                    break;
            }
        }

        ;
    };
    private int isUpdate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        if (intent != null) {
            apkUrl = intent.getStringExtra("url");
            loadFile();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mHandler.removeCallbacks(mdownApkRunnable);
        mHandler.removeMessages(DOWN_UPDATE);
        downLoadThread.stop();

        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mContext = this;
        if (intent != null) {
            apkUrl = intent.getStringExtra("url");
            isUpdate = intent.getIntExtra("isUpdate", 1);
            loadFile();
        }
        return binder;
    }

    public void setUpdatePb(UpdatePb updatePb) {
        this.updatePb = updatePb;
    }

    public class DownloadBinder extends Binder {
        public DownLoadService getService() {
            return DownLoadService.this;
        }
    }

    /**
     * 下载文件
     */
    private void loadFile() {
        initNotification();
        statrDownLoad();
    }

    private void statrDownLoad() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                saveFileName = savePath + "/" + System.currentTimeMillis() + ".apk";
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 初始化Notification通知
     */
    public void initNotification() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("default", "com.mahuayun", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.mipmap.icon)
                    .setContentText("0%")
                    .setContentTitle("下载中")
                    .setChannelId("default")
                    .setProgress(100, 0, false);
        } else {
            builder = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.mipmap.icon)
                    .setContentText("0%")
                    .setContentTitle("下载中")
                    .setChannelId("default")
                    .setProgress(100, 0, false);
        }
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 更新通知
     */
    public void updateNotification(long progress) {
        int currProgress = (int) progress;
        if (preProgress < currProgress) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, (int) progress, false);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
        preProgress = (int) progress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFY_ID);
    }

    /**
     * 安装apk
     *
     * @param
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        installApk(apkfile);
    }

    /**
     * 安装软件
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName()+".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
        //如果不加，最后不会提示完成、打开。
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

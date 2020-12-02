package com.mashangyou.golfprint.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.mashangyou.golfprint.R;


import java.io.File;

import androidx.core.content.FileProvider;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lulei
 * Created 2017/12/1 15:17
 * @copyright Copyright (c) 2007-2017 ShopNC Inc. All rights reserved.
 * @license http://www.shopnc.net
 * @link http://www.shopnc.net
 * <p>
 * 自定义仿IOS选择弹出窗
 */
public class InstallApkDialog extends Dialog {


    Context context;
    private String apkName;

    public InstallApkDialog(Context context, String apkName) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.apkName = apkName;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_install_apk, null);
        setContentView(view, new LinearLayout.LayoutParams(
                ConvertUtils.dp2px(315), LinearLayout.LayoutParams.WRAP_CONTENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.tv_install})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_install:
                installApk();
                break;
        }
    }

    private void installApk() {
        File apkfile = new File(apkName);
        if (!apkfile.exists()) {
            return;
        }
        installApk(apkfile);
    }


    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
        //如果不加，最后不会提示完成、打开。
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}

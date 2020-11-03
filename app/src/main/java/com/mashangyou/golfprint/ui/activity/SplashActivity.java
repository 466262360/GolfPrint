package com.mashangyou.golfprint.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static java.lang.Thread.sleep;

/**
 * Created by Administrator on 2020/9/9.
 * Des:
 */
public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
    public static final int PERMISSION_STORAGE_CODE = 10000;
    String[] PERMS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                           startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            SplashActivity.this.finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }



    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限已经被您拒绝")
                    .setRationale("如果不打开权限则无法使用该功能,点击确定去打开权限")
                    .build()
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("拒绝权限将退出App")
                    .setPositiveButton("确定", (dialogInterface, i) -> finish())
                    .show();
        }
    }

    @AfterPermissionGranted(PERMISSION_STORAGE_CODE)
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            start();
        } else {
            EasyPermissions.requestPermissions(this, PERMISSION_STORAGE_MSG, PERMISSION_STORAGE_CODE, PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //从设置页面返回，判断权限是否申请。
            if (EasyPermissions.hasPermissions(this, PERMS)) {
                Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show();
                start();
            } else {
                Toast.makeText(this, "权限申请失败!", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

package com.mashangyou.golfprint.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.mashangyou.golfprint.widget.LoadingDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2020/9/9.
 * Des:
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    protected Context context;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        setContentView(getLayoutId());
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        /** 注意：setContentView 必需在 bind 之前 */
        ButterKnife.bind(this);
        initToobar();
        initView();
        initData();
    }



    public void showLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }
        loadingDialog = new LoadingDialog(this);
        if (!isFinishing()) loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    protected abstract int getLayoutId();

    protected abstract void initToobar();

    protected abstract void initView();

    protected abstract void initData();
}

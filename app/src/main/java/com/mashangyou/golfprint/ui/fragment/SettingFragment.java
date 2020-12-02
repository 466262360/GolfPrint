package com.mashangyou.golfprint.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.res.AppVersion;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.interfac.UpdatePb;
import com.mashangyou.golfprint.service.DownLoadService;
import com.mashangyou.golfprint.ui.activity.LoginActivity;
import com.mashangyou.golfprint.ui.activity.MainActivity;
import com.mashangyou.golfprint.widget.InstallApkDialog;
import com.mashangyou.golfprint.widget.UpdateAppDialog;
import com.mashangyou.golfprint.widget.UpdateProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Administrator on 2020/10/26.
 * Des:
 */
public class SettingFragment extends BaseFragment{
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (getActivity()!=null){
            ((MainActivity)getActivity()).setTopTitle(getString(R.string.title_5));
        }
        tvVersion.setText(AppUtils.getAppVersionName());
    }

    @OnClick({R.id.cl_1,R.id.btn_exit,R.id.cl_up})
    void onClick(View view){
        switch (view.getId()){
            case R.id.cl_1:
                EventBus.getDefault().post(new EventFragment(Contant.F_PASSWORD));
                break;
            case R.id.btn_exit:
                quit();
                break;
            case R.id.cl_up:
                update();
                break;
        }
    }

    private void update() {
        ToastUtils.showShort(getString(R.string.setting_7));
        RetrofitManager.getApi()
                .update()
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<AppVersion>>() {
                    @Override
                    public void onSuccess(ResponseBody<AppVersion> response) {
                        int version = response.getData().getVersion();
                        if (version>AppUtils.getAppVersionCode()) {
                            String url = response.getData().getUrl();
                            if (!TextUtils.isEmpty(url)) {
                                showNoticeDialog(response.getData());
                            }
                        }else{
                            ToastUtils.showShort(getString(R.string.setting_6));
                        }
                    }

                    @Override
                    public void onFail(ResponseBody<AppVersion> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                    }
                });
    }

    /**
     * 显示更新对话框
     *
     * @param appVersion
     */
    private void showNoticeDialog(AppVersion appVersion) {
        UpdateAppDialog dialog = new UpdateAppDialog(getContext(), appVersion);
        dialog.show();
        dialog.setOnDownListener(new UpdateAppDialog.OnDownClickListener() {
            @Override
            public void onDownClick() {
                ToastUtils.showShort(getString(R.string.setting_8));
                Intent intent = new Intent(getContext(), DownLoadService.class);
                intent.putExtra("url", appVersion.getUrl());
                intent.putExtra("isUpdate", 1);
                getActivity().bindService(intent, conn, BIND_AUTO_CREATE);

            }
        });
        dialog.setOnCancelListener(new UpdateAppDialog.OnCancelClickListener() {
            @Override
            public void onCancelClick() {

            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownLoadService.DownloadBinder binder = (DownLoadService.DownloadBinder) service;
            DownLoadService bindService = binder.getService();
            bindService.setUpdatePb(new UpdatePb() {

                private UpdateProgressDialog updateProgressDialog;

                @Override
                public void update(int progress) {
                    if (updateProgressDialog==null){
                        updateProgressDialog = new UpdateProgressDialog(getContext());
                        updateProgressDialog.show();
                    }
                    updateProgressDialog.setProgress(progress);
                }

                @Override
                public void updateSuccess(String apkName) {
                    InstallApkDialog installApkDialog = new InstallApkDialog(getContext(),apkName);
                    installApkDialog.show();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void quit() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .quit(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        hideLoading();
                        SPUtils.getInstance().put(Contant.ACCESS_TOKEN, "");
                        SPUtils.getInstance().put(Contant.USER_NAME, "");
                        SPUtils.getInstance().put(Contant.PASS_WORD, "");
                        ActivityUtils.startActivity(LoginActivity.class);
                        ActivityUtils.finishAllActivities();
                    }

                    @Override
                    public void onFail(ResponseBody response) {
                        hideLoading();
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }
}

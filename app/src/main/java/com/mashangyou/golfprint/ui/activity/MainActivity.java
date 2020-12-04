package com.mashangyou.golfprint.ui.activity;

import android.app.Presentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaRouter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventCode;
import com.mashangyou.golfprint.bean.event.EventErrorCode;
import com.mashangyou.golfprint.bean.event.EventFragment;
import com.mashangyou.golfprint.bean.event.EventPrint;
import com.mashangyou.golfprint.bean.event.EventScreen;
import com.mashangyou.golfprint.bean.res.AppVersion;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.bean.res.VerifyRes;
import com.mashangyou.golfprint.interfac.UpdatePb;
import com.mashangyou.golfprint.scan.ScannerFragment;
import com.mashangyou.golfprint.secondScreen.BannerScreen;
import com.mashangyou.golfprint.secondScreen.CodeResultScreen;
import com.mashangyou.golfprint.secondScreen.ScanQRScreen;
import com.mashangyou.golfprint.secondScreen.ScanScreen;
import com.mashangyou.golfprint.secondScreen.VerifyScreen;
import com.mashangyou.golfprint.service.DownLoadService;
import com.mashangyou.golfprint.ui.fragment.CodeResultFragment;
import com.mashangyou.golfprint.ui.fragment.DataFragment;
import com.mashangyou.golfprint.ui.fragment.OrderFragment;
import com.mashangyou.golfprint.ui.fragment.PassWordFragment;
import com.mashangyou.golfprint.ui.fragment.PublishFragment;
import com.mashangyou.golfprint.ui.fragment.ScanErrorFragment;
import com.mashangyou.golfprint.ui.fragment.ScanShowFragment;
import com.mashangyou.golfprint.ui.fragment.SellFragment;
import com.mashangyou.golfprint.ui.fragment.SettingFragment;
import com.mashangyou.golfprint.ui.fragment.VerifyResultFragment;
import com.mashangyou.golfprint.util.USBPrinter;
import com.mashangyou.golfprint.widget.InstallApkDialog;
import com.mashangyou.golfprint.widget.UpdateAppDialog;
import com.mashangyou.golfprint.widget.UpdateProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends BaseActivity {
    @BindView(R.id.cl_scan)
    ConstraintLayout clScan;
    @BindView(R.id.cl_publish)
    ConstraintLayout clPublish;
    @BindView(R.id.cl_order)
    ConstraintLayout clOrder;
    @BindView(R.id.cl_data)
    ConstraintLayout clData;
    @BindView(R.id.cl_setting)
    ConstraintLayout clSetting;
    @BindView(R.id.cl_sell)
    ConstraintLayout clSell;

    @BindView(R.id.tv_1)
    TextView tvOrder;
    @BindView(R.id.tv_2)
    TextView tvPublish;
    @BindView(R.id.tv_3)
    TextView tvSell;
    @BindView(R.id.tv_4)
    TextView tvData;
    @BindView(R.id.tv_5)
    TextView tvSetting;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_bg_1)
    ImageView ivOrderBg;
    @BindView(R.id.iv_bg_2)
    ImageView ivPublishBg;
    @BindView(R.id.iv_bg_3)
    ImageView ivSellBg;
    @BindView(R.id.iv_bg_4)
    ImageView ivDataBg;
    @BindView(R.id.iv_bg_5)
    ImageView ivSettingBg;
    @BindView(R.id.iv_icon_1)
    ImageView ivOrderIcon;
    @BindView(R.id.iv_icon_2)
    ImageView ivPublishIcon;
    @BindView(R.id.iv_icon_3)
    ImageView ivSellIcon;
    @BindView(R.id.iv_icon_4)
    ImageView ivDataIcon;
    @BindView(R.id.iv_icon_5)
    ImageView ivSettingIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private FragmentManager fragmentManager;
    private Fragment cFragment;
    private OrderFragment orderFragment;
    private ScannerFragment scannerFragment;
    private PublishFragment publishFragment;
    private DataFragment dataFragment;
    private SettingFragment settingFragment;
    private CodeResultFragment codeResultFragment;
    private ScanErrorFragment scanErrorFragment;
    private MediaRouter mMediaRouter;
    private Presentation mPresentation;
    private boolean isExit;
    private int curType;
    private boolean verify;
    private VerifyResultFragment verifyResultFragment;
    private PassWordFragment passWordFragment;
    private SellFragment sellFragment;
    private ScanShowFragment scanShowFragment;
    Presentation lastDisplayPresentation;//用于记录上次展示的副屏的界面
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToobar() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        fragmentManager = getSupportFragmentManager();
        initOrderFragment();
        verify = true;
        USBPrinter.getInstance().initPrinter(this);
        UpAppVersion();
    }

    private void UpAppVersion() {
        RetrofitManager.getApi()
                .update()
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<AppVersion>>() {
                    @Override
                    public void onSuccess(ResponseBody<AppVersion> response) {
                        int version = response.getData().getVersion();
                        if (version > AppUtils.getAppVersionCode()) {
                            String url = response.getData().getUrl();
                            if (!TextUtils.isEmpty(url)) {
                                showNoticeDialog(response.getData());
                            }
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
        UpdateAppDialog dialog = new UpdateAppDialog(context, appVersion);
        dialog.show();
        dialog.setOnDownListener(new UpdateAppDialog.OnDownClickListener() {
            @Override
            public void onDownClick() {
                Intent intent = new Intent(MainActivity.this, DownLoadService.class);
                intent.putExtra("url", appVersion.getUrl());
                intent.putExtra("isUpdate", 1);
                bindService(intent, conn, BIND_AUTO_CREATE);

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
                    if (updateProgressDialog == null) {
                        updateProgressDialog = new UpdateProgressDialog(context);
                        updateProgressDialog.show();
                    }
                    updateProgressDialog.setProgress(progress);
                }

                @Override
                public void updateSuccess(String apkName) {
                    InstallApkDialog installApkDialog = new InstallApkDialog(context, apkName);
                    installApkDialog.show();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updatePresentation(int screenType) {
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        if (presentationDisplay != null) {
            switch (screenType) {
                case Contant.BANNER:
                    mPresentation = new BannerScreen(this, presentationDisplay);
                    curType = Contant.BANNER;
                    break;
                case Contant.CODE_RESULT:
                    mPresentation = new CodeResultScreen(this, presentationDisplay);
                    curType = Contant.CODE_RESULT;
                    reSetScanBtn();
                    break;
                case Contant.VERIFY:
                    mPresentation = new VerifyScreen(this, presentationDisplay);
                    curType = Contant.VERIFY;
                    break;
                case Contant.SCAN:
//                    mPresentation = new ScanScreen(this, presentationDisplay);
//                    curType = Contant.SCAN;
//                    reSetScanBtn();
                    mPresentation = new ScanQRScreen(this, presentationDisplay);
                    curType = Contant.SCAN;
                    reSetScanBtn();
                    break;
            }
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mPresentation = null;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (lastDisplayPresentation != null) {
                        lastDisplayPresentation.dismiss();
                        lastDisplayPresentation = null;
                    }
                    lastDisplayPresentation = mPresentation;
                }
            }, 100);
        }
    }

    public void dissmissScreen() {
        if (lastDisplayPresentation != null) {
            lastDisplayPresentation.dismiss();
        }
    }

    public boolean isShowBanner() {
        if (lastDisplayPresentation != null) {
            return lastDisplayPresentation.isShowing() && curType == Contant.BANNER;
        }
        return false;
    }

    private void initOrderFragment() {
        orderFragment = new OrderFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, orderFragment).commitAllowingStateLoss();
        if (!isShowBanner()) {
            updatePresentation(Contant.BANNER);
        }
        reSetButton(orderFragment);
    }

    private void initScannerFragment() {
        if (scannerFragment == null) {
            scannerFragment = new ScannerFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, scannerFragment).commitAllowingStateLoss();
        reSetButton(scannerFragment);
    }

    private void initPublishFragment() {
        if (publishFragment == null) {
            publishFragment = new PublishFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, publishFragment).commitAllowingStateLoss();
        if (!isShowBanner()) {
            updatePresentation(Contant.BANNER);
        }
        reSetButton(publishFragment);
    }

    private void initDataFragment() {
        if (dataFragment == null) {
            dataFragment = new DataFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, dataFragment).commitAllowingStateLoss();
        if (!isShowBanner()) {
            updatePresentation(Contant.BANNER);
        }
        reSetButton(dataFragment);
    }

    private void initSettingFragment() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, settingFragment).commitAllowingStateLoss();
        if (!isShowBanner()) {
            updatePresentation(Contant.BANNER);
        }
        reSetButton(settingFragment);
    }

    private void initScanShowFragment() {
        if (scanShowFragment == null) {
            scanShowFragment = new ScanShowFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, scanShowFragment).commitAllowingStateLoss();
        updatePresentation(Contant.SCAN);
    }

    private void initSellFragment() {
        if (sellFragment == null) {
            sellFragment = new SellFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, sellFragment).commitAllowingStateLoss();
        if (!isShowBanner()) {
            updatePresentation(Contant.BANNER);
        }
        reSetButton(sellFragment);
    }

    private void initCodeResultFragment() {
        codeResultFragment = new CodeResultFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, codeResultFragment).commitAllowingStateLoss();
        reSetButton(codeResultFragment);
    }

    private void initVerifyFragment() {
        if (verifyResultFragment == null) {
            verifyResultFragment = new VerifyResultFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, verifyResultFragment).commitAllowingStateLoss();
    }

    private void initPassWordFragment() {
        if (passWordFragment == null) {
            passWordFragment = new PassWordFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, passWordFragment).commitAllowingStateLoss();
    }

    private void initScanErrorFragment() {
        if (scanErrorFragment == null) {
            scanErrorFragment = new ScanErrorFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, scanErrorFragment).commitAllowingStateLoss();
        reSetButton(scanErrorFragment);
    }

    private void reSetButton(Fragment fragment) {
        cFragment = fragment;
        clearBg();
        reSetScanBtn();
        if (publishFragment == fragment) {
            clPublish.setBackgroundResource(R.drawable.shape_main_button_false);
            tvPublish.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivPublishBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivPublishIcon.setImageResource(R.drawable.main_4_s);
        } else if (orderFragment == fragment) {
            clOrder.setBackgroundResource(R.drawable.shape_main_button_false);
            tvOrder.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivOrderBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivOrderIcon.setImageResource(R.drawable.main_4_s);
        } else if (dataFragment == fragment) {
            clData.setBackgroundResource(R.drawable.shape_main_button_false);
            tvData.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivDataBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivDataIcon.setImageResource(R.drawable.main_6_s);
        } else if (settingFragment == fragment) {
            clSetting.setBackgroundResource(R.drawable.shape_main_button_false);
            tvSetting.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivSettingBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivSettingIcon.setImageResource(R.drawable.main_7_s);
        } else if (sellFragment == fragment) {
            clSell.setBackgroundResource(R.drawable.shape_main_button_false);
            tvSell.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivSellBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivSellIcon.setImageResource(R.drawable.main_5_s);
        }
    }

    private void reSetScanBtn() {
        if (curType == Contant.SCAN) {
            clScan.setBackgroundResource(R.drawable.shape_scan_false);
            ivScan.setImageResource(R.drawable.main_scan_2);
        } else {
            clScan.setBackgroundResource(R.drawable.shape_scan);
            ivScan.setImageResource(R.drawable.main_scan_1);
        }
    }

    public void setTopTitle(String string) {
        tvTitle.setText(string);
    }

    private void clearBg() {
        clScan.setBackgroundResource(R.drawable.shape_scan_true);
        clPublish.setBackgroundResource(R.drawable.shape_main_button_true);
        ivPublishBg.setBackgroundResource(R.drawable.shape_circle_a_40);
        ivPublishIcon.setImageResource(R.drawable.main_4);

        clOrder.setBackgroundResource(R.drawable.shape_main_button_true);
        ivOrderBg.setBackgroundResource(R.drawable.shape_circle_a_40);
        ivOrderIcon.setImageResource(R.drawable.main_4);

        clSell.setBackgroundResource(R.drawable.shape_main_button_true);
        ivSellBg.setBackgroundResource(R.drawable.shape_circle_a_40);
        ivSellIcon.setImageResource(R.drawable.main_5);

        clData.setBackgroundResource(R.drawable.shape_main_button_true);
        ivDataBg.setBackgroundResource(R.drawable.shape_circle_a_40);
        ivDataIcon.setImageResource(R.drawable.main_6);

        clSetting.setBackgroundResource(R.drawable.shape_main_button_true);
        ivSettingBg.setBackgroundResource(R.drawable.shape_circle_a_40);
        ivSettingIcon.setImageResource(R.drawable.main_7);

        tvPublish.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvOrder.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvData.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvSell.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvSetting.setTextColor(ContextCompat.getColor(this, R.color.color_white));
    }

    @OnClick({R.id.cl_scan, R.id.cl_publish, R.id.cl_order, R.id.cl_data, R.id.cl_sell, R.id.cl_setting})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_scan:
                if (curType == Contant.SCAN) {
                    initOrderFragment();
                } else {
                    initScanShowFragment();
                }
                reSetScanBtn();
                break;
            case R.id.cl_publish:
                initPublishFragment();
                break;
            case R.id.cl_order:
                initOrderFragment();
                break;
            case R.id.cl_sell:
                initSellFragment();
                break;
            case R.id.cl_data:
                initDataFragment();
                break;
            case R.id.cl_setting:
                initSettingFragment();
                break;
        }
    }


    private void verify(String code) {
        verify = false;
        String[] split = code.split(",");
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("barcode", split[0]);
        hashMap.put("brcodeo", split[1]);
        hashMap.put("brcodet", split[2]);
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .verifyTrus(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<VerifyRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        verify = true;
                        VerifyRes data = response.getData();
                        if (data != null) {
                            EventBus.getDefault().postSticky(data);
                            initCodeResultFragment();
                        }
                    }

                    @Override
                    public void onFail(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        verify = true;
                        int errorCode = response.getCode();
                        if (errorCode == 1) {
                            ToastUtils.showShort("查询失败");
                        } else {
                            updatePresentation(Contant.BANNER);
                            initScanErrorFragment();
                            EventBus.getDefault().postSticky(new EventErrorCode(errorCode));
                        }
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                        verify = true;
                        initScanShowFragment();
                    }
                });
    }



    private class PrintThread extends Thread {
        Map<String, String> printMap;

        public PrintThread(Map<String, String> map) {
            printMap = map;
        }

        public void run() {
            try {
                Looper.prepare();
                USBPrinter.getInstance().printContent(printMap);
                //USBPrinter.getInstance().printTest();
                Looper.loop();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                try {
                    sleep(4 * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveCode(EventCode data) {
        verify(data.getCode());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivePrint(EventPrint data) {
        new PrintThread(data.getPrintMap()).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveScreen(EventScreen data) {
        updatePresentation(data.getType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveFragment(EventFragment data) {
        switch (data.getType()) {
            case Contant.F_ORDER:
                initOrderFragment();
                break;
            case Contant.F_RESULT:
                initCodeResultFragment();
                break;
            case Contant.F_VERIFY:
                initVerifyFragment();
                break;
            case Contant.F_DATA:
                initDataFragment();
                break;
            case Contant.F_SETTING:
                initSettingFragment();
                break;
            case Contant.F_PUBLISH:
                initPublishFragment();
                break;
            case Contant.F_PASSWORD:
                initPassWordFragment();
                break;
            case Contant.F_SELL:
                initSellFragment();
                break;
            case Contant.F_SCANSHOW:
                initScanShowFragment();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBPrinter.getInstance().closeDevice();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        dissmissScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                finish();
            } else {
                ToastUtils.showShort("再按一次退出");
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

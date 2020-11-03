package com.mashangyou.golfprint.ui.activity;

import android.app.Presentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.api.Contant;
import com.mashangyou.golfprint.api.DefaultObserver;
import com.mashangyou.golfprint.api.RetrofitManager;
import com.mashangyou.golfprint.bean.event.EventErrorCode;
import com.mashangyou.golfprint.bean.res.ResponseBody;
import com.mashangyou.golfprint.bean.res.VerifyRes;
import com.mashangyou.golfprint.secondScreen.BannerScreen;
import com.mashangyou.golfprint.secondScreen.CodeResultScreen;
import com.mashangyou.golfprint.secondScreen.VerifyScreen;
import com.mashangyou.golfprint.ui.fragment.CodeResultFragment;
import com.mashangyou.golfprint.ui.fragment.DataFragment;
import com.mashangyou.golfprint.ui.fragment.OrderFragment;
import com.mashangyou.golfprint.ui.fragment.PublishFragment;
import com.mashangyou.golfprint.ui.fragment.ScanErrorFragment;
import com.mashangyou.golfprint.ui.fragment.SellFragment;
import com.mashangyou.golfprint.ui.fragment.SettingFragment;
import com.mashangyou.golfprint.util.SerializableMap;
import com.mashangyou.golfprint.util.USBPrinter;
import com.mashangyou.scan.ScannerFragment;

import org.greenrobot.eventbus.EventBus;

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
    @BindView(R.id.cl_sell)
    ConstraintLayout clSell;
    @BindView(R.id.cl_data)
    ConstraintLayout clData;
    @BindView(R.id.cl_setting)
    ConstraintLayout clSetting;
    @BindView(R.id.tv_1)
    TextView tvScan;
    @BindView(R.id.tv_2)
    TextView tvPublish;
    @BindView(R.id.tv_3)
    TextView tvSell;
    @BindView(R.id.tv_4)
    TextView tvData;
    @BindView(R.id.tv_5)
    TextView tvSetting;
    @BindView(R.id.iv_bg_2)
    ImageView ivPublishBg;
    @BindView(R.id.iv_bg_3)
    ImageView ivSellBg;
    @BindView(R.id.iv_bg_4)
    ImageView ivDataBg;
    @BindView(R.id.iv_bg_5)
    ImageView ivSettingBg;
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
    private SellFragment sellFragment;
    private DataFragment dataFragment;
    private SettingFragment settingFragment;
    private CodeResultFragment codeResultFragment;
    private ScanErrorFragment scanErrorFragment;
    private MediaRouter mMediaRouter;
    private Presentation mPresentation;
    private boolean isExit;

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
        registerReceiver();

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        fragmentManager = getSupportFragmentManager();
        initOrderFragment();
        USBPrinter.getInstance().initPrinter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updatePresentation(int screenType) {
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        if (mPresentation != null) {
            LogUtils.d("disssss");
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (presentationDisplay != null) {
            switch (screenType) {
                case Contant.BANNER:
                    mPresentation = new BannerScreen(this, presentationDisplay);
                    break;
                case Contant.CODE_RESULT:
                    mPresentation = new CodeResultScreen(this, presentationDisplay);
                    break;
                case Contant.VERIFY:
                    mPresentation = new VerifyScreen(this, presentationDisplay);
                    break;
            }
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {

                mPresentation = null;
            }
        }
        //updateContents();
    }

    public void dissmissScreen() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    private void initOrderFragment() {
        orderFragment = new OrderFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, orderFragment).commitAllowingStateLoss();
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
        reSetButton(publishFragment);
    }

    private void initSellFragment() {
        if (sellFragment == null) {
            sellFragment = new SellFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, sellFragment).commitAllowingStateLoss();
        reSetButton(sellFragment);
    }

    private void initDataFragment() {
        if (dataFragment == null) {
            dataFragment = new DataFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, dataFragment).commitAllowingStateLoss();
        reSetButton(dataFragment);
    }

    private void initSettingFragment() {
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, settingFragment).commitAllowingStateLoss();
        reSetButton(settingFragment);
    }

    private void initCodeResultFragment() {
        codeResultFragment = new CodeResultFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_content, codeResultFragment).commitAllowingStateLoss();
        reSetButton(codeResultFragment);
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
        if (scannerFragment == fragment) {
            clScan.setBackgroundResource(R.drawable.shape_scan_false);
            tvScan.setText(getString(R.string.main_0));
        } else {
            tvScan.setText(getString(R.string.main_1));
        }
        if (publishFragment == fragment) {
            clPublish.setBackgroundResource(R.drawable.shape_main_button_false);
            tvPublish.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivPublishBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivPublishIcon.setImageResource(R.drawable.main_4_s);
        } else if (sellFragment == fragment) {
            clSell.setBackgroundResource(R.drawable.shape_main_button_false);
            tvSell.setTextColor(ContextCompat.getColor(this, R.color.color_text_2));
            ivSellBg.setBackgroundResource(R.drawable.shape_circle_a_86);
            ivSellIcon.setImageResource(R.drawable.main_5_s);
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
        tvSell.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvData.setTextColor(ContextCompat.getColor(this, R.color.color_white));
        tvSetting.setTextColor(ContextCompat.getColor(this, R.color.color_white));
    }

    @OnClick({R.id.cl_scan, R.id.cl_publish, R.id.cl_sell, R.id.cl_data, R.id.cl_setting})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_scan:
                if (cFragment == scannerFragment) {
                    updatePresentation(Contant.BANNER);
                    initOrderFragment();
                } else {
                    initScannerFragment();
                    dissmissScreen();
                }
                break;
            case R.id.cl_publish:
                initPublishFragment();
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

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScannerFragment.MESSAGE_ACTION);
        intentFilter.addAction(Contant.PRINT_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 消息广播
            if (action.equals(ScannerFragment.MESSAGE_ACTION)) {
                String code = intent.getStringExtra(ScannerFragment.MESSAGE_BARCODE);
                verify(code);
            } else if (action.equals(Contant.PRINT_ACTION)) {
                Bundle bundle = intent.getBundleExtra(Contant.PRINT_MAP);
                SerializableMap map = (SerializableMap) bundle.get(Contant.PRINT_MAP);
                Map<String, String> printMap = map != null ? map.getMap() : null;
                new PrintThread(printMap).start();
            }
        }
    };

    private void verify(String code) {
        String[] split = code.split(",");
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("barcode", split[0]);
        hashMap.put("brcodeo", split[1]);
        hashMap.put("brcodet", split[2]);
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .verify(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<VerifyRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        VerifyRes data = response.getData();
                        if (data != null) {
                            EventBus.getDefault().postSticky(data);
                            initCodeResultFragment();
                        }
                    }

                    @Override
                    public void onFail(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        int errorCode = response.getCode();
                        if (errorCode == 1) {
                            ToastUtils.showShort("查询失败");
                        } else {
                            EventBus.getDefault().postSticky(new EventErrorCode(errorCode));
                            initScanErrorFragment();
                            updatePresentation(Contant.BANNER);
                        }
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }

    public void scan() {
        if (cFragment == scannerFragment) {
            updatePresentation(Contant.BANNER);
            initOrderFragment();
        } else {
            initScannerFragment();
            dissmissScreen();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        USBPrinter.getInstance().closeDevice();
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
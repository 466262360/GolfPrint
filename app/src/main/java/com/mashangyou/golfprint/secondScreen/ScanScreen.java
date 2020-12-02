package com.mashangyou.golfprint.secondScreen;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.event.EventCode;
import com.mashangyou.golfprint.scan.OnDecodeCompletionListener;
import com.mashangyou.golfprint.scan.ScannerView;
import com.mashangyou.golfprint.ui.activity.BaseActivity;
import com.mashangyou.golfprint.ui.activity.MainActivity;
import com.mashangyou.golfprint.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;




/**
 * Created by Administrator on 2020/11/4.
 * Des:
 */
public class ScanScreen extends Presentation implements OnDecodeCompletionListener {

    private ScannerView cScannerView;
    public static final String MESSAGE_ACTION = "com.mashangyou.wanliu.barCode";
    public static final String MESSAGE_BARCODE = "com.mashangyou.wanliu.barCodeMessage";
    public BaseFragment outerContext= null;
    public ScanScreen(Context outerContext, Display display) {
        super(outerContext, display);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_scan);
        cScannerView =findViewById(R.id.scanner_view);
        cScannerView.setOnDecodeListener(this);
    }

    @Override
    public void show() {
        super.show();
        cScannerView.onResume();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        cScannerView.onPause();

    }

    @Override
    public void onDecodeCompletion(String code) {
        if (!TextUtils.isEmpty(code)) {
            Intent intent = new Intent();
            intent.setAction(MESSAGE_ACTION);
            intent.putExtra(MESSAGE_BARCODE, code);
            EventBus.getDefault().post(new EventCode(code));
        } else {
            Toast.makeText(getContext(), "解析错误", Toast.LENGTH_SHORT).show();
        }
    }
}

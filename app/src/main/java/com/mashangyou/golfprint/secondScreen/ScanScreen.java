package com.mashangyou.golfprint.secondScreen;

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

import org.greenrobot.eventbus.EventBus;

import static com.mashangyou.golfprint.scan.ScannerFragment.MESSAGE_ACTION;
import static com.mashangyou.golfprint.scan.ScannerFragment.MESSAGE_BARCODE;


/**
 * Created by Administrator on 2020/11/4.
 * Des:
 */
public class ScanScreen extends Presentation implements OnDecodeCompletionListener {

    private ScannerView cScannerView;

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

    /**  EventBus解注册  */
    @Override
    public void dismiss() {
        cScannerView.onPause();
        super.dismiss();

    }

    @Override
    public void onDecodeCompletion(String code) {
        LogUtils.d("onDecodeCompletion","code="+code);
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

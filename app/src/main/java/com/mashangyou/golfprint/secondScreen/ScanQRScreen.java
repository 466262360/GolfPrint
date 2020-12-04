package com.mashangyou.golfprint.secondScreen;

import android.app.Presentation;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;

import com.mashangyou.golfprint.R;
import com.mashangyou.golfprint.bean.event.EventCode;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashSet;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;



/**
 * Created by Administrator on 2020/12/4.
 * Des:
 */

public class ScanQRScreen extends Presentation implements QRCodeView.Delegate{
    private ZXingView mZXingView;
    private  HashSet<String> hashSet;
    private boolean playBeep;
    private MediaPlayer mediaPlayer;

    public ScanQRScreen(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_scanqr);
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
        //mZXingView.hiddenScanRect();
        mZXingView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
        hashSet = new HashSet<>();

        playBeep = true;
        AudioManager audioService = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != 2) {
            this.playBeep = false;
        }
        initBeepSound();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(3);
            mediaPlayer.setOnCompletionListener(this.beepListener);
            try {
                AssetFileDescriptor file = getContext().getResources().openRawResourceFd(
                        R.raw.beep);
               mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.8F, 0.8F);
                mediaPlayer.prepare();
            } catch (IOException var3) {
                mediaPlayer = null;
                System.out.println(var3.getMessage());
            }
        } else {
            System.out.println("木有播放");
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void playBeepSound() {
        if (playBeep && mediaPlayer != null) {
            this.mediaPlayer.start();
        }
    }

    @Override
    public void show() {
        super.show();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpot(); // 开始识别
    }

    @Override
    public void dismiss() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.dismiss();

    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (!TextUtils.isEmpty(result)) {
            hashSet.add(result);
            if (hashSet.size()==1){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hashSet.clear();
                    }
                },10*1000);
            }
            if (hashSet.size()>2){
                StringBuffer buffer = new StringBuffer();
                for(String i : hashSet){
                    buffer.append(i+",");
                }
                playBeepSound();
                EventBus.getDefault().post(new EventCode(buffer.toString()));
                hashSet.clear();
            }else{
                mZXingView.startSpot();
            }

        } else {
            mZXingView.startSpot();
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
}

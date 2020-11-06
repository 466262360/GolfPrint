package com.mashangyou.golfprint.scan.decoder;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.mashangyou.golfprint.scan.ScannerView;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;


final class DecodeThread extends Thread {
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final ScannerView scannerView;
    private final Hashtable<DecodeHintType, Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(ScannerView scannerView, Vector<BarcodeFormat> decodeFormats, String characterSet, ResultPointCallback resultPointCallback) {
        this.scannerView = scannerView;
        this.handlerInitLatch = new CountDownLatch(1);
        this.hints = new Hashtable(3);
        if(decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        this.hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if(characterSet != null) {
            this.hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        this.hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            this.handlerInitLatch.await();
        } catch (InterruptedException var2) {
            ;
        }

        return this.handler;
    }

    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.scannerView, this.hints);
        ((DecodeHandler)this.handler).setOrientation(this.scannerView.getOrientation());
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}


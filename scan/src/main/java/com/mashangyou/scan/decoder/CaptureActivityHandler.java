package com.mashangyou.scan.decoder;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;

import com.google.zxing.Result;
import com.mashangyou.scan.ScannerView;
import com.mashangyou.scan.camera.CameraManager;
import com.mashangyou.scan.view.ViewfinderResultPointCallback;

import java.util.Vector;


import static com.mashangyou.scan.decoder.DecodeThread.BARCODE_BITMAP;


public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();
    private final ScannerView scannerView;
    private final DecodeThread decodeThread;
    private CaptureActivityHandler.State state;

    public CaptureActivityHandler(ScannerView scannerView, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.scannerView = scannerView;
        this.decodeThread = new DecodeThread(scannerView, decodeFormats, characterSet, new ViewfinderResultPointCallback(scannerView.getViewfinderView()));
        this.decodeThread.start();
        this.state = CaptureActivityHandler.State.SUCCESS;
        CameraManager.get().startPreview();
        this.restartPreviewAndDecode();
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                if (this.state == CaptureActivityHandler.State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, 1);
                }
                break;
            case 2:
                Log.d(TAG, "Got restart preview message");
                this.restartPreviewAndDecode();
                break;
            case 3:
                Log.d(TAG, "Got decode succeeded message");
                this.state = CaptureActivityHandler.State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(BARCODE_BITMAP);
                this.scannerView.handleDecode((Result) message.obj, barcode);
                break;
            case 4:
                this.state = CaptureActivityHandler.State.PREVIEW;
                CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), 7);
                break;
            case 5:
                Log.d(TAG, "Got return scan result message");
                break;
            case 6:
                Log.d(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                intent.addFlags(524288);
                break;
            case 101:
                Log.d(TAG, "Got decode succeeded message101");
                this.state = CaptureActivityHandler.State.SUCCESS;
                Bundle tBundle = message.getData();
                Bitmap tBarcode = tBundle == null ? null : (Bitmap) tBundle.getParcelable(BARCODE_BITMAP);
                this.scannerView.handleTDecode((String)message.obj);
                break;
        }

    }

    public void quitSynchronously() {
        this.state = CaptureActivityHandler.State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(this.decodeThread.getHandler(), 8);
        quit.sendToTarget();

        try {
            this.decodeThread.join();
        } catch (InterruptedException var3) {
            ;
        }

        this.removeMessages(3);
        this.removeMessages(4);
        this.removeMessages(101);
    }

    private void restartPreviewAndDecode() {
        if (this.state == CaptureActivityHandler.State.SUCCESS) {
            this.state = CaptureActivityHandler.State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), 7);
            CameraManager.get().requestAutoFocus(this, 1);
            this.scannerView.drawViewfinder();
        }

    }

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE;
    }
}


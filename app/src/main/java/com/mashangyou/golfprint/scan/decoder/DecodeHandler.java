package com.mashangyou.golfprint.scan.decoder;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.mashangyou.golfprint.scan.ScannerView;
import com.mashangyou.golfprint.scan.camera.CameraManager;
import com.mashangyou.golfprint.scan.camera.PlanarYUVLuminanceSource;

import java.util.HashSet;
import java.util.Hashtable;

import cn.bigcode.tcodedecoder.DecoderResult;
import cn.bigcode.tcodedecoder.TCodeDecoder;

import static com.mashangyou.golfprint.scan.decoder.DecodeThread.BARCODE_BITMAP;


final class DecodeHandler extends Handler {
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private final ScannerView scannerView;
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    private int orientation = 1;
    private int decodeResultCode = -1;
    String resultCode;
    private final HashSet<String> hashSet;
    private boolean isSend;


    DecodeHandler(ScannerView scannerView, Hashtable<DecodeHintType, Object> hints) {
        this.multiFormatReader.setHints(hints);
        this.scannerView = scannerView;
        hashSet = new HashSet<>();
        isSend=true;
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 7:
                this.decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case 8:
                Looper.myLooper().quit();
                break;
            case 102:

                break;
        }

    }

    private void decode(byte[] data, int width, int height) {
        if (decodeResultCode > 0) return;
        long start = System.currentTimeMillis();
        PlanarYUVLuminanceSource source = null;
        if (2 == this.orientation) {
            try {
                source = CameraManager.get().buildLuminanceSource(data, width, height);
            } catch (IllegalArgumentException var20) {
                source = null;
                Log.e(TAG, var20.getMessage());
            }
        } else {
            byte[] bitmap = new byte[data.length];

            int message;
            for (message = 0; message < height; ++message) {
                for (int ex = 0; ex < width; ++ex) {
                    bitmap[ex * height + height - message - 1] = data[ex + message * width];
                }
            }

            message = width;
            width = height;
            height = message;

            try {
                source = CameraManager.get().buildLuminanceSource(bitmap, width, height);
            } catch (IllegalArgumentException var19) {
                source = null;
                Log.e(TAG, var19.getMessage());
            }
        }
        decodeTCode(source, start);
    }

    /**
     * source解码T-Code
     *
     * @param source
     * @param start
     * @return
     */
    private void decodeTCode(PlanarYUVLuminanceSource source, long start) {
        Bitmap bitmap = source.renderCroppedGreyscaleBitmap();
        byte[] rgbBuf = getRgbBuf(bitmap, bitmap.getHeight(), bitmap.getWidth());
        try {
            DecoderResult scanResult = TCodeDecoder.decoder(rgbBuf, bitmap.getWidth(), bitmap.getHeight(), 2);
            if (scanResult != null) {
                decodeResultCode=scanResult.tcodeLen;
                if (decodeResultCode > 0) {
                    resultCode = new String(scanResult.tcode);
                    hashSet.add(resultCode);
                    //10秒扫码时间
                    if (hashSet.size()==1){
                        this.postDelayed(new Runnable() {
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
                        hashSet.clear();
                        if (isSend)
                        send(buffer,bitmap);
                        LogUtils.d("buffer"+buffer);
                        //decodeResultCode = -1;
                    }else{
                        decodeResultCode = -1;
                        Message var23 = Message.obtain(this.scannerView.getHandler(), 4);
                        var23.sendToTarget();
                    }
                } else {
                    Message var23 = Message.obtain(this.scannerView.getHandler(), 4);
                    var23.sendToTarget();
                }
            }else{
                Message var23 = Message.obtain(this.scannerView.getHandler(), 4);
                var23.sendToTarget();
            }
        } catch (Exception e) {

        }

    }

    private void send(StringBuffer buffer, Bitmap bitmap) {
        isSend =false;
        Message message1 = Message.obtain(this.scannerView.getHandler(), 101, buffer.toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable(BARCODE_BITMAP, bitmap);
        message1.setData(bundle);
        message1.sendToTarget();
    }


    /**
     * @param bitmap
     * @param height
     * @param width
     * @return bitmap转byte[]
     */
    private byte[] getRgbBuf(Bitmap bitmap, int height, int width) {
        int[] dataInt = new int[height * width];
        bitmap.getPixels(dataInt, 0, width, 0, 0, width, height);
        byte[] rgbBuf = new byte[height * width * 3];

        int argb, iData, r, g, b;
        for (int i = 0; i < dataInt.length; i++) {
            argb = dataInt[i];
            iData = i * 3;

            r = (argb & 0x00ff0000) >> 16;
            if (r < 0) r = 0;
            rgbBuf[iData] = (byte) r;

            g = (argb & 0x0000ff00) >> 8;
            if (g < 0) g = 0;
            rgbBuf[iData + 1] = (byte) g;

            b = (byte) (argb & 0x000000ff);
            if (b < 0) b = 0;
            rgbBuf[iData + 2] = (byte) b;
        }
        return rgbBuf;
    }

    void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}


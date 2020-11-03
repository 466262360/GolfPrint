package com.mashangyou.scan;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mashangyou.scan.camera.CameraManager;
import com.mashangyou.scan.decoder.CaptureActivityHandler;
import com.mashangyou.scan.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;




public class ScannerView extends FrameLayout implements Callback, OnSettingListener {
    private static final String TAG = ScannerView.class.getSimpleName();
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private boolean hasVisiable;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.8F;
    private boolean vibrate;
    private Context context;
    private OnDecodeCompletionListener decodeListener = null;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private Camera camera;
    private Parameters parameter;
    private static final long VIBRATE_DURATION = 200L;
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    ScannerView(Context context) {
        super(context);
        this.context = context;
        this.constructLayout();
    }

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.constructLayout();
    }

    private void constructLayout() {
        LayoutParams mainViewParam = new LayoutParams(-1, -1);
        mainViewParam.gravity = 17;
        this.surfaceView = new SurfaceView(this.context);
        this.surfaceView.setLayoutParams(mainViewParam);
        this.addView(this.surfaceView);
        this.viewfinderView = new ViewfinderView(this.context);
        mainViewParam = new LayoutParams(-1, -1);
        mainViewParam.gravity = 17;
        this.viewfinderView.setLayoutParams(mainViewParam);
        this.viewfinderView.setBackgroundColor(17170445);
        this.addView(this.viewfinderView);
        this.hasSurface = false;
        this.hasVisiable = false;
    }

//    private void setSurfaceViewCorner(final float radius) {
//
//        surfaceView.setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                Rect rect = new Rect();
//                view.getGlobalVisibleRect(rect);
//                int leftMargin = 0;
//                int topMargin = 0;
//                Rect selfRect = new Rect(leftMargin, topMargin,
//                        rect.right - rect.left - leftMargin,
//                        rect.bottom - rect.top - topMargin);
//                outline.setRoundRect(selfRect, radius);
//            }
//        });
//        surfaceView.setClipToOutline(true);
//    }

    public void setTing(Boolean open) {
        this.camera = CameraManager.getCamera();
        this.parameter = this.camera.getParameters();
        if (open.booleanValue()) {
            this.parameter.setFlashMode("torch");
        } else {
            this.parameter.setFlashMode("off");
        }

        this.camera.setParameters(this.parameter);
    }

    public void onResume() {
        int oldpreviewWidth = this.previewWidth;
        if (this.previewWidth == 0) {
            this.previewWidth = this.getMeasuredWidth();
            this.previewHeight = this.getMeasuredHeight();
        }

        if (oldpreviewWidth != this.previewWidth) {
            Log.d(TAG, "onResume:width=" + this.previewWidth + "��height=" + this.previewHeight);
        }
        this.hasVisiable=true;
        System.out.println("ScanViewonResume"+"hasVisiable="+hasVisiable+"hasSurface="+hasSurface);
        SurfaceHolder surfaceHolder = this.surfaceView.getHolder();
        if (this.hasSurface && this.hasVisiable&&this.previewWidth > 0) {
            this.initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(3);
        }

        this.decodeFormats = null;
        this.characterSet = null;
        this.playBeep = true;
        AudioManager audioService = (AudioManager) this.context.getSystemService("audio");
        if (audioService.getRingerMode() != 2) {
            this.playBeep = false;
        }

        this.initBeepSound();
        this.vibrate = true;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (2 == this.context.getResources().getConfiguration().orientation) {
            Log.e(TAG, "initCamera:Activity orientation is landscape,width=" + this.previewWidth + ", height=" + this.previewHeight);
        } else {
            Log.e(TAG, "initCamera:Activity orientation is portrait,width=" + this.previewWidth + ", height=" + this.previewHeight);
        }

        try {
            CameraManager.init(this.context, this.previewWidth, this.previewHeight);
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException var3) {
            Log.e(TAG, "initCamera:" + var3.getMessage());
            return;
        } catch (RuntimeException var4) {
            Log.e(TAG, "initCamera:" + var4.getMessage());
            return;
        }

        if (this.handler == null) {
            this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.characterSet);
        }

    }

    public void onPause() {
        this.hasVisiable=false;
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }

        if (CameraManager.get() != null) {
            CameraManager.get().closeDriver();
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfaceCreated");
        if (!this.hasSurface) {
            this.hasSurface = true;
            if (this.hasVisiable){
                if (this.previewWidth == 0) {
                    this.previewWidth = this.getMeasuredWidth();
                    this.previewHeight = this.getMeasuredHeight();
                }
                if (this.previewWidth > 0) {
                    this.initCamera(holder);
                }
            }
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.hasSurface = false;
    }

    private void initBeepSound() {
        if (this.playBeep && this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioStreamType(3);
            this.mediaPlayer.setOnCompletionListener(this.beepListener);

            try {
                AssetManager e = this.context.getAssets();
                AssetFileDescriptor file = e.openFd("beepbeep.ogg");
                this.mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                this.mediaPlayer.setVolume(0.8F, 0.8F);
                this.mediaPlayer.prepare();
            } catch (IOException var3) {
                this.mediaPlayer = null;
                System.out.println(var3.getMessage());
            }
        } else {
            System.out.println("________________________木有播放");
        }

        System.out.println("-------:" + this.context);
    }

    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(-1, -1);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    public ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }

    public void setOnDecodeListener(OnDecodeCompletionListener decodeListener) {
        this.decodeListener = decodeListener;
    }

    public void handleDecode(Result obj, Bitmap barcode) {
        this.playBeepSoundAndVibrate();
        if (this.decodeListener != null) {
            this.decodeListener.onDecodeCompletion( obj.getText());
        }
    }

    public void handleTDecode(String code) {
        this.playBeepSoundAndVibrate();
        if (this.decodeListener != null) {
            this.decodeListener.onDecodeCompletion(code);
        }
    }

    private void playBeepSoundAndVibrate() {
        if (Constant.openSound.booleanValue() && this.playBeep && this.mediaPlayer != null) {
            this.mediaPlayer.start();
        }

        if (Constant.openvibrator.booleanValue() && this.vibrate) {
            Vibrator vibrator = (Vibrator) this.context.getSystemService("vibrator");
            vibrator.vibrate(200L);
        }

    }

    public int getOrientation() {
        return this.previewWidth == 0 ? this.context.getResources().getConfiguration().orientation : (this.previewWidth > this.previewHeight ? 2 : 1);
    }

    public void openLight(Boolean open) {
    }

    public void openSound(Boolean open) {
    }

    public void openVibrate(Boolean open) {
    }
}
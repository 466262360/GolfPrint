package com.mashangyou.golfprint.scan.camera;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;


public final class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 360;
    private static final int MAX_FRAME_HEIGHT = 480;
    private static CameraManager cameraManager;
    static final int SDK_INT;
    private final Context context;
    private final CameraConfigurationManager configManager;
    private static Camera camera;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private final boolean useOneShotPreviewCallback;
    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(VERSION.SDK);
        } catch (NumberFormatException var2) {
            sdkInt = 10000;
        }

        SDK_INT = sdkInt;
    }

    public static void init(Context context, int previewWidth, int previewHeight) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(context, previewWidth, previewHeight);
        }

        Log.d(TAG, "init: CameraManager created.previewWidth=" + previewWidth + ",previewHeight=" + previewHeight);
        if (previewWidth < previewHeight) {
            setWidth = (int) (previewWidth * 0.6);
        } else {
            setWidth = (int) (previewHeight * 0.6);
        }
        setHeight = setWidth;
    }

    public static CameraManager get() {
        return cameraManager;
    }

    public static Camera getCamera() {
        return camera;
    }

    private CameraManager(Context context, int previewWidth, int previewHeight) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context, previewWidth, previewHeight);
        this.useOneShotPreviewCallback = Integer.parseInt(VERSION.SDK) > 3;
        this.previewCallback = new PreviewCallback(this.configManager, this.useOneShotPreviewCallback);
        this.autoFocusCallback = new AutoFocusCallback();
    }

    public void openDriver(SurfaceTexture holder) throws IOException {
        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                throw new IOException();
            }

            camera.setPreviewTexture(holder);
            if (!this.initialized) {
                this.initialized = true;
                this.configManager.initFromCameraParameters(camera);
            }

            this.configManager.setDesiredCameraParameters(camera);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
            FlashlightManager.enableFlashlight();
            Log.d(TAG, "openDriver: camera setup OK.");
        }

    }

    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }

    }

    public void startPreview() {
        if (camera != null && !this.previewing) {
            camera.startPreview();
            this.previewing = true;
        }

    }

    public void stopPreview() {
        if (camera != null && this.previewing) {
            if (!this.useOneShotPreviewCallback) {
                camera.setPreviewCallback((Camera.PreviewCallback) null);
            }

            camera.stopPreview();
            this.previewCallback.setHandler((Handler) null, 0);
            this.autoFocusCallback.setHandler((Handler) null, 0);
            this.previewing = false;
        }

    }

    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && this.previewing) {
            this.previewCallback.setHandler(handler, message);
            if (this.useOneShotPreviewCallback) {
                camera.setOneShotPreviewCallback(this.previewCallback);
            } else {
                camera.setPreviewCallback(this.previewCallback);
            }
        }

    }

    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && this.previewing) {
            this.autoFocusCallback.setHandler(handler, message);
            camera.autoFocus(this.autoFocusCallback);
        }

    }

    private static int setWidth = 480;
    private static int setHeight = 480;
    public boolean getNewRect, getNewDecodeRect;

    public void setWidthHeight(int w, int h) {
        setWidth = w;
        setHeight = h;
        getNewRect = getNewDecodeRect = true;
    }

    public Rect getFramingRect() {
        Point screenResolution = this.configManager.getScreenResolution();
        if (getNewRect || this.framingRect == null) {
            getNewRect = false;
            if (camera == null) {
                return null;
            }

            int width = screenResolution.x * 3 / 4;
            if (width < 240) {
                width = 240;
            } else if (width > setWidth) {
                width = setWidth;
            }

            int height = screenResolution.y * 3 / 4;
            if (height < 80) {
                height = 80;
            } else if (height > setHeight) {
                height = setHeight;
            }

            //扫码框位置
//            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//            int width1 = (int) (metrics.widthPixels * 0.6);
//            int height1 = (int) (width1 * 0.6);
//
//
//            int leftOffset = (screenResolution.x - width1) / 2;
//            int topOffset = (screenResolution.y - height1) / 2;
//            framingRect = new Rect(leftOffset, topOffset, leftOffset + width1,
//                    topOffset + height1);

            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            Log.d(TAG, "Calculated framing rect: " + this.framingRect);
        }

        return this.framingRect;
    }

    public Rect getFramingRectInPreview() {
        if (getNewDecodeRect || this.framingRectInPreview == null) {
            getNewDecodeRect = false;
            Rect rect = new Rect(this.getFramingRect());
            Point cameraResolution = this.configManager.getCameraResolution();
            Point screenResolution = this.configManager.getScreenResolution();
            if (screenResolution.x > screenResolution.y) {
                rect.left = rect.left * cameraResolution.x / screenResolution.x;
                rect.right = rect.right * cameraResolution.x / screenResolution.x;
                rect.top = rect.top * cameraResolution.y / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            } else {
                rect.left = rect.left * cameraResolution.y / screenResolution.x;
                rect.right = rect.right * cameraResolution.y / screenResolution.x;
                rect.top = rect.top * cameraResolution.x / screenResolution.y;
                rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            }

            this.framingRectInPreview = rect;
        }

        return this.framingRectInPreview;
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = this.getFramingRectInPreview();
        int previewFormat = this.configManager.getPreviewFormat();
        String previewFormatString = this.configManager.getPreviewFormatString();
        switch (previewFormat) {
            case 16:
            case 17:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
            default:
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height());
                } else {
                    throw new IllegalArgumentException("Unsupported picture format: " + previewFormat + '/' + previewFormatString);
                }
        }
    }
}


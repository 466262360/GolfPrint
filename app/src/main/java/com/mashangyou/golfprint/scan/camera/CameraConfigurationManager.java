package com.mashangyou.golfprint.scan.camera;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class CameraConfigurationManager {
    private static final String TAG = CameraConfigurationManager.class.getSimpleName();
    private static final int TEN_DESIRED_ZOOM = 27;
    private static final int DESIRED_SHARPNESS = 30;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    private int previewFormat;
    private String previewFormatString;
    private int orientation = 1;

    CameraConfigurationManager(Context context, int previewWidth, int previewHeight) {
        this.context = context;
        this.orientation = context.getResources().getConfiguration().orientation;
        this.screenResolution = new Point(previewWidth, previewHeight);
    }

    void initFromCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        this.previewFormat = parameters.getPreviewFormat();
        this.previewFormatString = parameters.get("preview-format");
        Log.d(TAG, "Default preview format: " + this.previewFormat + '/' + this.previewFormatString);
        Log.d(TAG, "Screen resolution: " + this.screenResolution);
        this.cameraResolution = getCameraResolution(parameters, this.screenResolution);
        Log.d(TAG, "Camera resolution: " + this.cameraResolution);
    }

    void setDesiredCameraParameters(Camera camera) {
        Parameters parameters = camera.getParameters();
        Log.d(TAG, "Setting preview size: " + this.cameraResolution);
        parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        this.setFlash(parameters);
        this.setZoom(parameters);
        if(1 == this.orientation) {
            parameters.set("orientation", "portrait");
            parameters.set("rotation", 90);

            try {
                Method downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{Integer.TYPE});
                if(downPolymorphic != null) {
                    downPolymorphic.invoke(camera, new Object[]{Integer.valueOf(90)});
                }
            } catch (Exception var5) {
                Log.d(TAG, var5.getMessage());
            }
        }

        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }

    Point getScreenResolution() {
        return this.screenResolution;
    }

    int getPreviewFormat() {
        return this.previewFormat;
    }

    String getPreviewFormatString() {
        return this.previewFormatString;
    }

    private static Point getCameraResolution(Parameters parameters, Point screenResolution) {
        String previewSizeValueString = parameters.get("preview-size-values");
        if(previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;
        if(previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if(cameraResolution == null) {
            cameraResolution = new Point(screenResolution.x >> 3 << 3, screenResolution.y >> 3 << 3);
        }

        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = 2147483647;
        int longBorder = screenResolution.x > screenResolution.y?screenResolution.x:screenResolution.y;
        int shortBorder = screenResolution.x > screenResolution.y?screenResolution.y:screenResolution.x;
        String[] var10;
        int var9 = (var10 = COMMA_PATTERN.split(previewSizeValueString)).length;

        for(int var8 = 0; var8 < var9; ++var8) {
            String previewSize = var10[var8];
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf(120);
            if(dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
            } else {
                int newX;
                int newY;
                try {
                    newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                    newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
                } catch (NumberFormatException var15) {
                    Log.w(TAG, "Bad preview-size: " + previewSize);
                    continue;
                }

                int newDiff = Math.abs(newX - longBorder) + Math.abs(newY - shortBorder);
                if(newDiff == 0) {
                    bestX = newX;
                    bestY = newY;
                    break;
                }

                if(newDiff < diff) {
                    bestX = newX;
                    bestY = newY;
                    diff = newDiff;
                }
            }
        }

        return bestX > 0 && bestY > 0?new Point(bestX, bestY):null;
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        String[] var6;
        int var5 = (var6 = COMMA_PATTERN.split(stringValues)).length;

        for(int var4 = 0; var4 < var5; ++var4) {
            String stringValue = var6[var4];
            stringValue = stringValue.trim();

            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException var10) {
                return tenDesiredZoom;
            }

            int tenValue = (int)(10.0D * value);
            if(Math.abs((double)tenDesiredZoom - value) < (double) Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }

        return tenBestValue;
    }

    private void setFlash(Parameters parameters) {
        if(Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) {
            parameters.set("flash-value", 1);
        } else {
            parameters.set("flash-value", 2);
        }

        parameters.set("flash-mode", "off");
    }

    private void setZoom(Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if(zoomSupportedString == null || Boolean.parseBoolean(zoomSupportedString)) {
            int tenDesiredZoom = 27;
            String maxZoomString = parameters.get("max-zoom");
            if(maxZoomString != null) {
                try {
                    int takingPictureZoomMaxString = (int)(10.0D * Double.parseDouble(maxZoomString));
                    if(tenDesiredZoom > takingPictureZoomMaxString) {
                        tenDesiredZoom = takingPictureZoomMaxString;
                    }
                } catch (NumberFormatException var13) {
                    Log.w(TAG, "Bad max-zoom: " + maxZoomString);
                }
            }

            String takingPictureZoomMaxString1 = parameters.get("taking-picture-zoom-max");
            if(takingPictureZoomMaxString1 != null) {
                try {
                    int motZoomValuesString = Integer.parseInt(takingPictureZoomMaxString1);
                    if(tenDesiredZoom > motZoomValuesString) {
                        tenDesiredZoom = motZoomValuesString;
                    }
                } catch (NumberFormatException var12) {
                    Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString1);
                }
            }

            String motZoomValuesString1 = parameters.get("mot-zoom-values");
            if(motZoomValuesString1 != null) {
                tenDesiredZoom = findBestMotZoomValue(motZoomValuesString1, tenDesiredZoom);
            }

            String motZoomStepString = parameters.get("mot-zoom-step");
            if(motZoomStepString != null) {
                try {
                    double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                    int tenZoomStep = (int)(10.0D * motZoomStep);
                    if(tenZoomStep > 1) {
                        tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                    }
                } catch (NumberFormatException var11) {
                    ;
                }
            }

            if(maxZoomString != null || motZoomValuesString1 != null) {
                parameters.set("zoom", String.valueOf((double)tenDesiredZoom / 10.0D));
            }

            if(takingPictureZoomMaxString1 != null) {
                parameters.set("taking-picture-zoom", tenDesiredZoom);
            }

        }
    }

    public int getScreenOrientation() {
        return this.orientation;
    }
}


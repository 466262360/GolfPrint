package com.mashangyou.scan.view;

/**
 * Created by Administrator on 2016/11/28.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.google.zxing.ResultPoint;
import com.mashangyou.scan.R;
import com.mashangyou.scan.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;



public final class ViewfinderView extends View {
    private static final String TAG = ViewfinderView.class.getSimpleName();
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 255;
    private Paint paint;
    private Bitmap resultBitmap;
    private int maskColor;
    private int resultColor;
    private int frameColor;
    private int laserColor;
    private int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private int previewWidth = 0;
    private int previewHeight = 0;

    public ViewfinderView(Context context) {
        super(context);
        this.initColor(context);
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initColor(context);
    }

    private void initColor(Context context) {
        this.paint = new Paint();
        this.maskColor = 1610612736;
        this.resultColor = -1342177280;
        this.frameColor = -16777216;
//        this.laserColor = -65536;
        this.laserColor = 0xFF244c82;
        this.resultPointColor = -1056964864;
        this.scannerAlpha = 0;
        this.possibleResultPoints = new HashSet(5);
    }

    public void onDraw(Canvas canvas) {
        invalidate();
        if(CameraManager.get() != null) {
            Rect frame = CameraManager.get().getFramingRect();
            if(frame == null) {
                return;
            }

            if(this.previewWidth == 0) {
                this.previewWidth = this.getWidth();
                this.previewHeight = this.getHeight();
                Log.d(TAG, "draw canvas. width=" + this.previewWidth + ",height=" + this.previewHeight + ",frame=" + frame);
            }

            this.paint.setColor(this.resultBitmap != null?this.resultColor:this.maskColor);
            canvas.drawRect(0.0F, 0.0F, (float)this.previewWidth, (float)frame.top, this.paint);
            canvas.drawRect(0.0F, (float)frame.top, (float)frame.left, (float)(frame.bottom + 1), this.paint);
            canvas.drawRect((float)(frame.right + 1), (float)frame.top, (float)this.previewWidth, (float)(frame.bottom + 1), this.paint);
            canvas.drawRect(0.0F, (float)(frame.bottom + 1), (float)this.previewWidth, (float)this.previewHeight, this.paint);
            if(this.resultBitmap != null) {
                this.paint.setAlpha(255);
                canvas.drawBitmap(this.resultBitmap, (float)frame.left, (float)frame.top, this.paint);
            } else {
                byte linewidht = 10;//线宽度
                this.paint.setColor(this.laserColor);
                canvas.drawRect((float)(-10 + frame.left), (float)(-10 + frame.top), (float)(-10 + linewidht + frame.left), (float)(-10 + 100 + frame.top), this.paint);
                canvas.drawRect((float)(-10 + frame.left), (float)(-10 + frame.top), (float)(-10 + 100 + frame.left), (float)(-10 + linewidht + frame.top), this.paint);
                canvas.drawRect((float)(10 + 0 - linewidht + frame.right), (float)(-10 + frame.top), (float)(10 + 1 + frame.right), (float)(-10 + 100 + frame.top), this.paint);
                canvas.drawRect((float)(10 + -100 + frame.right), (float)(-10 + frame.top), (float)(10 + frame.right), (float)(-10 + linewidht + frame.top), this.paint);
                canvas.drawRect((float)(-10 + frame.left), (float)(10 + -100 + frame.bottom), (float)(-10 + linewidht + frame.left), (float)(10 + 1 + frame.bottom), this.paint);
                canvas.drawRect((float)(-10 + frame.left), (float)(10 + 0 - linewidht + frame.bottom), (float)(-10 + 100 + frame.left), (float)(10 + 1 + frame.bottom), this.paint);
                canvas.drawRect((float)(10 + 0 - linewidht + frame.right), (float)(10 + -100 + frame.bottom), (float)(10 + 1 + frame.right), (float)(10 + 1 + frame.bottom), this.paint);
                canvas.drawRect((float)(10 + -100 + frame.right), (float)(10 + 0 - linewidht + frame.bottom), (float)(10 + frame.right), (float)(10 + linewidht - (linewidht - 1) + frame.bottom), this.paint);
                this.paint.setColor(this.frameColor);
                canvas.drawRect((float)frame.left, (float)frame.top, (float)(frame.right + 1), (float)(frame.top + 2), this.paint);
                canvas.drawRect((float)frame.left, (float)(frame.top + 2), (float)(frame.left + 2), (float)(frame.bottom - 1), this.paint);
                canvas.drawRect((float)(frame.right - 1), (float)frame.top, (float)(frame.right + 1), (float)(frame.bottom - 1), this.paint);
                canvas.drawRect((float)frame.left, (float)(frame.bottom - 1), (float)(frame.right + 1), (float)(frame.bottom + 1), this.paint);
                this.paint.setColor(this.laserColor);
                this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
                this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
                int middle = frame.height() / 2 + frame.top;
                int hmiddle = frame.width() / 2 + frame.left;
                canvas.drawRect((float)(hmiddle - 30), (float)(middle - 1), (float)(hmiddle - 10), (float)(middle + 1), this.paint);
                canvas.drawRect((float)(hmiddle + 10), (float)(middle - 1), (float)(hmiddle + 30), (float)(middle + 1), this.paint);
                canvas.drawRect((float)(hmiddle - 1), (float)(middle - 30), (float)(hmiddle + 1), (float)(middle - 10), this.paint);
                canvas.drawRect((float)(hmiddle - 1), (float)(middle + 10), (float)(hmiddle + 1), (float)(middle + 30), this.paint);
                Collection currentPossible = this.possibleResultPoints;
                Collection currentLast = this.lastPossibleResultPoints;
                ResultPoint point;
                Iterator var9;
                if(currentPossible.isEmpty()) {
                    this.lastPossibleResultPoints = null;
                } else {
                    this.possibleResultPoints = new HashSet(5);
                    this.lastPossibleResultPoints = currentPossible;
                    this.paint.setAlpha(255);
                    this.paint.setColor(this.resultPointColor);
                    var9 = currentPossible.iterator();

                    while(var9.hasNext()) {
                        point = (ResultPoint)var9.next();
                        canvas.drawCircle((float)frame.left + point.getX(), (float)frame.top + point.getY(), 6.0F, this.paint);
                    }
                }

                if(currentLast != null) {
                    this.paint.setAlpha(127);
                    this.paint.setColor(this.resultPointColor);
                    var9 = currentLast.iterator();

                    while(var9.hasNext()) {
                        point = (ResultPoint)var9.next();
                        canvas.drawCircle((float)frame.left + point.getX(), (float)frame.top + point.getY(), 3.0F, this.paint);
                    }
                }

                this.postInvalidateDelayed(100L, frame.left, frame.top, frame.right, frame.bottom);
            }
        } else {
            if(this.previewWidth == 0) {
                this.previewWidth = this.getWidth();
                this.previewHeight = this.getHeight();
            }

            if(this.previewWidth > 0) {
                this.paint.setColor(this.maskColor);
                canvas.drawRect(0.0F, 0.0F, (float)this.previewWidth, (float)this.previewHeight, this.paint);
                this.postInvalidate();
            }

            Log.d(TAG, "draw canvas. mask the whole screen.width=" + this.previewWidth + ",height=" + this.previewHeight);
        }

    }

    public void drawViewfinder() {
        this.resultBitmap = null;
        this.invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        this.invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        this.possibleResultPoints.add(point);
    }
}


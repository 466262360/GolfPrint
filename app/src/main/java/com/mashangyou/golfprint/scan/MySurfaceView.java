package com.mashangyou.golfprint.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2020/11/4.
 * Des:
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder surfaceHolder ;

    public MySurfaceView(Context context) {
        super(context);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Canvas canvas = surfaceHolder.lockCanvas();

        //绘制之前先对画布进行翻转
        canvas.scale(-1,1, getWidth()/2,getHeight()/2);
//        //开始自己的内容的绘制
//        Paint paint = new Paint();
//        canvas.drawColor(Color.WHITE);
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(50);
//        canvas.drawText("这是对SurfaceView的翻转",50,250,paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}

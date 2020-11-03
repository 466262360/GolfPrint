package com.mashangyou.golfprint.bean.event;

/**
 * Created by Administrator on 2020/10/30.
 * Des:
 */
public class EventRvOrderY {
    private int y;
    private int h;

    public EventRvOrderY(int dy, int measuredHeight) {
        this.y=dy;
        this.h=measuredHeight;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}

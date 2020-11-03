package com.mashangyou.golfprint.bean.event;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class EventErrorCode {
    private int code;

    public EventErrorCode(int errorCode) {
        this.code=errorCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

package com.mashangyou.golfprint.bean.event;

/**
 * Created by Administrator on 2020/10/27.
 * Des:
 */
public class EventVerifyResult {
    private String orderId;
    private String date;

    public EventVerifyResult(String orderId, String date) {
        this.orderId=orderId;
        this.date=date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

package com.mashangyou.golfprint.bean.event;

/**
 * Created by Administrator on 2020/10/30.
 * Des:
 */
public class EventRvOrderId {
    private String id;

    public EventRvOrderId(String orderId) {

        this.id=orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

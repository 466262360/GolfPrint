package com.mashangyou.golfprint.bean.req;

/**
 * Created by Administrator on 2020/9/9.
 * Des:
 */
public class RequestBody<T> {
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}

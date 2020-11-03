package com.mashangyou.golfprint.bean.req;

/**
 * Created by Administrator on 2020/9/15.
 * Des:
 */
public class VerifyReq {
    private String brcode;
    private String token;

    public String getBrcode() {
        return brcode;
    }

    public void setBrcode(String brcode) {
        this.brcode = brcode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

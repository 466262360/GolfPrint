package com.mashangyou.golfprint.bean.req;

/**
 * Created by Administrator on 2020/9/11.
 * Des:
 */
public class PassWordReq {
    private String npwd;
    private String opwd;
    private String token;

    public String getNpwd() {
        return npwd;
    }

    public void setNpwd(String npwd) {
        this.npwd = npwd;
    }

    public String getOpwd() {
        return opwd;
    }

    public void setOpwd(String opwd) {
        this.opwd = opwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

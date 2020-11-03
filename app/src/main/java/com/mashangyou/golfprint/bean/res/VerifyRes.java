package com.mashangyou.golfprint.bean.res;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/9/15.
 * Des:
 */
public class VerifyRes extends ResponseBody implements Serializable {
    private String img;
    private String mobile;
    private String name;
    private String tcode;
    private String memberName;
    private List<Orders> orders;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTcode() {
        return tcode;
    }

    public void setTcode(String tcode) {
        this.tcode = tcode;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public class Orders implements Serializable{
        private String caves;
        private String orderId;
        private String peoples;
        private String playTime;
        private String golfName;
        private String frequency="0";
        private boolean sel;

        public String getGolfName() {
            return golfName;
        }

        public void setGolfName(String golfName) {
            this.golfName = golfName;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public boolean isSel() {
            return sel;
        }

        public void setSel(boolean sel) {
            this.sel = sel;
        }

        public String getCaves() {
            return caves;
        }

        public void setCaves(String caves) {
            this.caves = caves;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPeoples() {
            return peoples;
        }

        public void setPeoples(String peoples) {
            this.peoples = peoples;
        }

        public String getPlayTime() {
            return playTime;
        }

        public void setPlayTime(String playTime) {
            this.playTime = playTime;
        }
    }
}

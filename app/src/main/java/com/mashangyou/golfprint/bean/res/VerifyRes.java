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
        //年度剩余
        private String interestfacy;// 会员
        private String interestGroup;//会带
        //本次核销
        private String member;//会员
        private String group;//会带
        private String guest;//嘉宾
        //参考价格
        private String memberPrice;//会员
        private String groupPrice;//会带
        private String guestPrice;//嘉宾
        private boolean sel;

        public String getInterestfacy() {
            return interestfacy;
        }

        public void setInterestfacy(String interestfacy) {
            this.interestfacy = interestfacy;
        }

        public String getInterestGroup() {
            return interestGroup;
        }

        public void setInterestGroup(String interestGroup) {
            this.interestGroup = interestGroup;
        }

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getGuest() {
            return guest;
        }

        public void setGuest(String guest) {
            this.guest = guest;
        }

        public String getMemberPrice() {
            return memberPrice;
        }

        public void setMemberPrice(String memberPrice) {
            this.memberPrice = memberPrice;
        }

        public String getGroupPrice() {
            return groupPrice;
        }

        public void setGroupPrice(String groupPrice) {
            this.groupPrice = groupPrice;
        }

        public String getGuestPrice() {
            return guestPrice;
        }

        public void setGuestPrice(String guestPrice) {
            this.guestPrice = guestPrice;
        }

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

package com.mashangyou.golfprint.bean.res;

import java.util.List;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class OrderListRes extends ResponseBody{
    private List<Order> orders;

    public List<Order> getOrderList() {
        return orders;
    }

    public void setOrderList(List<Order> orders) {
        this.orders = orders;
    }

    public class Order{
        private String dateTime;
        private String orderId;
        private String peoples;

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
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
    }
}

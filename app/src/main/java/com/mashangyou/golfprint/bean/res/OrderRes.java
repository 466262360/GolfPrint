package com.mashangyou.golfprint.bean.res;

import java.util.List;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class OrderRes extends ResponseBody{
    private List<Record> record;

    public List<Record> getRecord() {
        return record;
    }

    public void setRecord(List<Record> record) {
        this.record = record;
    }

    public static class Record{
        private String date;
        private String orderId;
        private String people;

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

        public String getPeople() {
            return people;
        }

        public void setPeople(String people) {
            this.people = people;
        }
    }
}

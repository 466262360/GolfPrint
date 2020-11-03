package com.mashangyou.golfprint.bean.res;

import java.util.List;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class SelectWriteRes extends ResponseBody{
    private List<Record> record;

    public List<Record> getRecord() {
        return record;
    }

    public void setRecord(List<Record> record) {
        this.record = record;
    }

    public class Record{
        private String date;
        private int frequ;
        private String memberName;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getFrequ() {
            return frequ;
        }

        public void setFrequ(int frequ) {
            this.frequ = frequ;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
    }
}

package com.mashangyou.golfprint.bean.res;

import java.util.List;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class CountWriteRes extends ResponseBody {

    private List<Content> currentMonth;
    private List<Content> sevendays;
    private List<Content> thisYear;

    public List<Content> getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(List<Content> currentMonth) {
        this.currentMonth = currentMonth;
    }

    public List<Content> getSevendays() {
        return sevendays;
    }

    public void setSevendays(List<Content> sevendays) {
        this.sevendays = sevendays;
    }

    public List<Content> getThisYear() {
        return thisYear;
    }

    public void setThisYear(List<Content> thisYear) {
        this.thisYear = thisYear;
    }

    public class Content {
        private int frequ;
        private String memberName;
        private int peoples;

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

        public int getPeoples() {
            return peoples;
        }

        public void setPeoples(int peoples) {
            this.peoples = peoples;
        }
    }

}

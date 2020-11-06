package com.mashangyou.golfprint.bean.res;

/**
 * Created by Administrator on 2020/11/2.
 * Des:
 */
public class PublishInfoRes implements Comparable<PublishInfoRes>{
    private long opentime;
    private String salestatus;
    private int allcanuserperson;
    private int userdpersoncont;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAllcanuserperson() {
        return allcanuserperson;
    }

    public void setAllcanuserperson(int allcanuserperson) {
        this.allcanuserperson = allcanuserperson;
    }

    public int getUserdPersoncont() {
        return userdpersoncont;
    }

    public void setUserdPersoncont(int userdPersoncont) {
        this.userdpersoncont = userdPersoncont;
    }

    public long getOpentime() {
        return opentime;
    }

    public void setOpentime(long opentime) {
        this.opentime = opentime;
    }

    public String getSalestatus() {
        return salestatus;
    }

    public void setSalestatus(String salestatus) {
        this.salestatus = salestatus;
    }

    @Override
    public int compareTo(PublishInfoRes publishInfoRes) {
        if (this.opentime>publishInfoRes.opentime){
            return 1;
        }else if(this.opentime<publishInfoRes.opentime){
            return -1;
        }else{
            return 0;
        }
    }
}

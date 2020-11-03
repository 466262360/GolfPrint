package com.mashangyou.golfprint.bean.res;

import java.util.List;

/**
 * Created by Administrator on 2020/9/9.
 * Des:
 */
public class PublishRes extends ResponseBody{
    private List<Publish> list;

    public List<Publish> getList() {
        return list;
    }

    public void setList(List<Publish> list) {
        this.list = list;
    }

    public static class Publish implements Comparable<Publish>{
        private String title;
        private List<PublishInfoRes> infos;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<PublishInfoRes> getInfos() {
            return infos;
        }

        public void setInfos(List<PublishInfoRes> infos) {
            this.infos = infos;
        }

        @Override
        public int compareTo(Publish publish) {
            return this.title.compareTo(publish.title);
        }
    }

}

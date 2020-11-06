package com.mashangyou.golfprint.bean.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/11/5.
 * Des:
 */
public class EventPrint {
    private Map<String,String> printMap;

    public EventPrint(Map<String, String> hashMap) {
        printMap=hashMap;
    }

    public Map<String, String> getPrintMap() {
        return printMap;
    }

    public void setPrintMap(Map<String, String> printMap) {
        this.printMap = printMap;
    }
}

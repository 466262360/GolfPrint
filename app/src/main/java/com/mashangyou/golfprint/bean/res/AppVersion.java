package com.mashangyou.golfprint.bean.res;

/**
 * Created by snm on 2016/8/17.
 */
public class AppVersion extends ResponseBody{

    private String url;
    private String type;
    private int version;
    private String updateContent;
    private int isUpdate;//是否强制更新1开启 0关闭

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

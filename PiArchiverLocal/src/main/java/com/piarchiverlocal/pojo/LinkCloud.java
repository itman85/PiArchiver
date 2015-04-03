package com.piarchiverlocal.pojo;

/**
 * Created by phannguyen-pc on 3/22/2014.
 */
public class LinkCloud {
    int cloudid;
    String url;
    String note;
    int categoryid;
    String createdon;//cloud created on

    public LinkCloud() {
    }

    public LinkCloud(int cloudid, String url, String note, int categoryid, String createdon) {
        this.cloudid = cloudid;
        this.url = url;
        this.note = note;
        this.categoryid = categoryid;
        this.createdon = createdon;
    }

    public int getCloudid() {
        return cloudid;
    }

    public void setCloudid(int cloudid) {
        this.cloudid = cloudid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }
}

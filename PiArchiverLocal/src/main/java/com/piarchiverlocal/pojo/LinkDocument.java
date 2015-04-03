package com.piarchiverlocal.pojo;

/**
 * Created by phan on 3/25/2014.
 */
public class LinkDocument {
    //name of properties also name of field in elasticsearch document
    int cloudid;
    String url;
    String note;
    int categoryid;
    String createdon;//cloud created on

    String content;
    String localPagePath;
    String originalPagePath;

    public LinkDocument() {
    }

    public LinkDocument(int cloudid, String url, String note, int categoryid, String createdon,
                        String content, String localPagePath, String originalPagePath) {
        this.cloudid = cloudid;
        this.url = url;
        this.note = note;
        this.categoryid = categoryid;
        this.createdon = createdon;
        this.content = content;
        this.localPagePath = localPagePath;
        this.originalPagePath = originalPagePath;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocalPagePath() {
        return localPagePath;
    }

    public void setLocalPagePath(String localPagePath) {
        this.localPagePath = localPagePath;
    }

    public String getOriginalPagePath() {
        return originalPagePath;
    }

    public void setOriginalPagePath(String originalPagePath) {
        this.originalPagePath = originalPagePath;
    }
}

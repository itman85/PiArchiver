package com.gae.piarchivercloud.pojo;


public class LinkPojo {//display link in client
	private String url;	
	private String note;
	private String categoryname;
	
	private String short_url;	
	private String short_note;
	private String short_categoryname;
	
	private int linkID;
	private int categoryID;
	
	private String createdOn; //dd/MM/yyyy HH:mm

	public LinkPojo(String url, String note, String categoryName,
			String short_url, String short_note, String short_CategoryName,
			int linkID, int categoryID, String createdOn) {
		super();
		this.url = url;
		this.note = note;
		this.categoryname = categoryName;
		this.short_url = short_url;
		this.short_note = short_note;
		this.short_categoryname = short_CategoryName;
		this.setLinkID(linkID);
		this.setCategoryID(categoryID);
		this.createdOn = createdOn;
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

	public String getCategoryName() {
		return categoryname;
	}

	public void setCategoryName(String categoryName) {
		categoryname = categoryName;
	}

	public String getShort_url() {
		return short_url;
	}

	public void setShort_url(String short_url) {
		this.short_url = short_url;
	}

	public String getShort_note() {
		return short_note;
	}

	public void setShort_note(String short_note) {
		this.short_note = short_note;
	}

	public String getShort_CategoryName() {
		return short_categoryname;
	}

	public void setShort_CategoryName(String short_CategoryName) {
		this.short_categoryname = short_CategoryName;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public int getLinkID() {
		return linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
	
	
}

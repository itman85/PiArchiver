package com.gae.piarchivercloud.pojo;

public class CategoryChangingPojo {
	private String name;
	private int key;
	private int newid;
	private int parentkey;
	private int parentnewid;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getNewid() {
		return newid;
	}
	public void setNewid(int newid) {
		this.newid = newid;
	}
	public int getParentkey() {
		return parentkey;
	}
	public void setParentkey(int parentkey) {
		this.parentkey = parentkey;
	}
	public int getParentnewid() {
		return parentnewid;
	}
	public void setParentnewid(int parentnewid) {
		this.parentnewid = parentnewid;
	}
	
}

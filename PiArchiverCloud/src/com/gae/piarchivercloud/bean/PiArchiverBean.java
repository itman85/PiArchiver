package com.gae.piarchivercloud.bean;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.search.Document;

public abstract class PiArchiverBean {
	
	
	public static final String id_property = "id";	
	public static final String createdOn_property = "createdOn";
	public static final String updatedOn_property = "updatedOn";
	
	protected Key key = null;
	protected Date createdOn;
	protected Date updatedOn;
	protected int ID;
	
	
	public PiArchiverBean() {
		super();
		createdOn = new Date();
		updatedOn = new Date();
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}

	
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public abstract  Entity createEntity();
}

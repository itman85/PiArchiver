package com.gae.piarchivercloud.bean;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class LinksBean extends PiArchiverBean{
	public static final String LINKS_ENTITY_KIND = "LinksEntity";
	public static final String url_property = "url";	
	public static final String CategoryID_property = "CategoryID";
	public static final String note_property = "note";
	
	private String url;
	private int CategoryID;
	private String note;
	

	public LinksBean() {
		super();
	}

	public LinksBean(Entity entity) {
		setID(((Long)entity.getProperty(id_property)).intValue());		
		setCategoryID(((Long) entity.getProperty(CategoryID_property)).intValue());
		setUrl((String) entity.getProperty(url_property));
		setNote((String) entity.getProperty(note_property));
		setKey(entity.getKey());// default name of key
		setUpdatedOn((Date) entity.getProperty(updatedOn_property));
		setCreatedOn((Date) entity.getProperty(createdOn_property));
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getCategoryID() {
		return CategoryID;
	}
	public void setCategoryID(int categoryID) {
		CategoryID = categoryID;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(LINKS_ENTITY_KIND);
		entity.setProperty(id_property, ID);
		entity.setProperty(url_property, url);
		entity.setProperty(CategoryID_property, CategoryID);
		entity.setProperty(note_property, note);
		entity.setProperty(createdOn_property, createdOn);
		entity.setProperty(updatedOn_property, updatedOn);			
		return entity;
	}

	
	
}

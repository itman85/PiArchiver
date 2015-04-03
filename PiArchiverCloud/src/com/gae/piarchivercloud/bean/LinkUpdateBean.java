package com.gae.piarchivercloud.bean;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class LinkUpdateBean extends PiArchiverBean{
	public static final String LINKUPDATE_ENTITY_KIND = "LinkUpdateEntity";
	public static final String CategoryID_property = "CategoryID";
	public static final String note_property = "note";
	public static final String LinkID_property = "LinkID";
	
	private int linkID;
	private int CategoryID;
	private String note;
	
	public LinkUpdateBean(){super();}
	
	public LinkUpdateBean(Entity entity) {
		setID(((Long)entity.getProperty(id_property)).intValue());		
		setCategoryID(((Long) entity.getProperty(CategoryID_property)).intValue());
		setLinkID(((Long) entity.getProperty(LinkID_property)).intValue());
		setNote((String) entity.getProperty(note_property));
		setKey(entity.getKey());// default name of key
		setCreatedOn((Date) entity.getProperty(createdOn_property));
	}
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(LINKUPDATE_ENTITY_KIND);
		entity.setProperty(id_property, ID);
		entity.setProperty(LinkID_property, linkID);
		entity.setProperty(CategoryID_property, CategoryID);
		entity.setProperty(note_property, note);
		entity.setProperty(createdOn_property, createdOn);				
		return entity;
	}

	public int getLinkID() {
		return linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
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

}

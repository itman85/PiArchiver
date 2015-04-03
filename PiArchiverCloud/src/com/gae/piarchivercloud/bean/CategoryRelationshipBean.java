package com.gae.piarchivercloud.bean;

import com.google.appengine.api.datastore.Entity;

public class CategoryRelationshipBean extends PiArchiverBean{
	public static final String CATEGORY_RELATIONSHIP_ENTITY_KIND = "CategoryRelationShipEntity";
	public static final String childID_property = "childID";	
	public static final String parentID_property = "parentID";
	
	private int childID;
	private int parentID;
	
	public CategoryRelationshipBean() {
		super();
	}
	public CategoryRelationshipBean(int childID,int parentID) {
		super();
		this.childID=childID;
		this.parentID=parentID;			
	}
	public int getChildID() {
		return childID;
	}
	public void setChildID(int childID) {
		this.childID = childID;
	}
	public int getParentID() {
		return parentID;
	}
	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(CATEGORY_RELATIONSHIP_ENTITY_KIND);
		entity.setProperty(id_property, ID);
		entity.setProperty(childID_property, childID);
		entity.setProperty(parentID_property, parentID);
		entity.setProperty(createdOn_property, createdOn);
		entity.setProperty(updatedOn_property, updatedOn);			
		return entity;
	}
	
}

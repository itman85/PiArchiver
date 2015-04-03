package com.gae.piarchivercloud.bean;

import com.google.appengine.api.datastore.Entity;

public class CategoryMasterBean extends PiArchiverBean {	
	public static final String CATEGORY_ENTITY_KIND = "CategoryEntity";
	public static final String name_property = "name";	
	
	private String category_name;

	public CategoryMasterBean() {
		super();
	}

	public CategoryMasterBean(String name,int Id) {
		super();
		this.category_name = name;
		this.ID = Id;
	}
	
	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(CATEGORY_ENTITY_KIND);
		entity.setProperty(id_property, ID);
		entity.setProperty(name_property, category_name);
		entity.setProperty(createdOn_property, createdOn);
		entity.setProperty(updatedOn_property, updatedOn);			
		return entity;
	}
	
}

package com.gae.piarchivercloud.bean;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CategoryOplogBean extends PiArchiverBean{
	public static final String CATEGORY_OPLOG_ENTITY_KIND = "CategoryOplogEntity";
	public static final String oplogjson_property = "oplogJson";	
	public static final String readDate_property = "readDate";
	public static final String readCount_property = "readCount";
	private final String Oplog_Key_IsCreated = "iscreated";
	private final String Oplog_Key_CategoryName = "name";
	private final String Oplog_Key_CategoryKey = "key";
	private final String Oplog_Key_CategoryParentKey = "parentkey";
	private String oplogJson;
	private Date readDate;
	private int readCount;
	
	public CategoryOplogBean() {
		super();		
	}
	
	public CategoryOplogBean(Entity entity) {
		setID(((Long)entity.getProperty(id_property)).intValue());		
		setOplogJson((String) entity.getProperty(oplogjson_property));
		setReadCount(((Long) entity.getProperty(readCount_property)).intValue());
		setReadDate((Date) entity.getProperty(readDate_property));
		setKey(entity.getKey());// default name of key
		setUpdatedOn((Date) entity.getProperty(updatedOn_property));
		setCreatedOn((Date) entity.getProperty(createdOn_property));
	}
	
	public String getOplogJson() {
		return oplogJson;
	}
	public void setOplogJson(String oplogJson) {
		this.oplogJson = oplogJson;
	}
	public Date getReadDate() {
		return readDate;
	}
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(CATEGORY_OPLOG_ENTITY_KIND);
		entity.setProperty(id_property, ID);
		entity.setProperty(oplogjson_property, oplogJson);
		entity.setProperty(readDate_property, readDate);
		entity.setProperty(readCount_property, readCount);	
		entity.setProperty(createdOn_property, createdOn);			
		return entity;
	}
	
	public void makeOpLogJson(String catename,String cateKey,String parentKey,boolean isCreated) throws JSONException
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(Oplog_Key_IsCreated, isCreated);
		jsonObj.put(Oplog_Key_CategoryName, catename);
		jsonObj.put(Oplog_Key_CategoryKey, cateKey);
		jsonObj.put(Oplog_Key_CategoryParentKey, parentKey);
		setOplogJson(jsonObj.toString());
	}
	
}

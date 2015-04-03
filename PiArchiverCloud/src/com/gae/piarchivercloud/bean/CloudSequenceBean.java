package com.gae.piarchivercloud.bean;

import java.util.Date;

import org.apache.jsp.ah.inboundMailBody_jsp;

import com.google.appengine.api.datastore.Entity;

public class CloudSequenceBean extends PiArchiverBean{
	public static final String SEQUENCE_ENTITY_KIND = "SequenceEntity";
	
	public static final String sequence_property = "seq";	
	public static final String kind_property = "kind";
	
	private int seq;
	private String kind;
	
	public CloudSequenceBean() {super();}
	
	public CloudSequenceBean(Entity entity) {
		//setID((int)entity.getProperty(id_property));		
		setSeq(((Long) entity.getProperty(sequence_property)).intValue());
		setKind((String) entity.getProperty(kind_property));
		setKey(entity.getKey());// default name of key
		setUpdatedOn((Date) entity.getProperty(updatedOn_property));
		setCreatedOn((Date) entity.getProperty(createdOn_property));
	}
	
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity;
		if(key!=null)
			entity = new Entity(key);
		else
			entity = new Entity(SEQUENCE_ENTITY_KIND);
		
		entity.setProperty(sequence_property, seq);
		entity.setProperty(kind_property, kind);
		entity.setProperty(createdOn_property, createdOn);
		entity.setProperty(updatedOn_property, updatedOn);
		
		
		return entity;
	}
	public void increaseSeq()
	{
		seq++;
	}
	public void RollBackSeq()
	{
		seq--;
	}
	
}

package com.gae.piarchivercloud.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.gae.piarchivercloud.bean.CategoryOplogBean;
import com.gae.piarchivercloud.bean.CategoryRelationshipBean;
import com.gae.piarchivercloud.bean.CloudSequenceBean;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CategoriesDAO extends PiArchiverDAO{

	
	
	public void UpdateSequenceForCategoriesGroups(CloudSequenceBean curSeqCateObj,CloudSequenceBean curSeqCateRelObj,CloudSequenceBean curSeqCateLogObj)
	{
		List<Entity> seqEntities = new ArrayList<Entity>();
		if(curSeqCateObj!=null)
			seqEntities.add(curSeqCateObj.createEntity());
		if(curSeqCateRelObj!=null)
			seqEntities.add(curSeqCateRelObj.createEntity());
		if(curSeqCateLogObj!=null)
			seqEntities.add(curSeqCateLogObj.createEntity());		
		//List<Entity> seqEntities = Arrays.asList(curSeqCateObj.createEntity(), curSeqCateRelObj.createEntity(), curSeqCateLogObj.createEntity());
		if(seqEntities.size()>0)
			datastore.put(seqEntities);
	}
	/**
	 * 
	 * @param cateObj
	 * @param cateRelObj
	 * @return void
	 * @throws Exception
	 */
	public void AddNewCategory(CategoryMasterBean cateObj, CategoryRelationshipBean cateRelObj,CategoryOplogBean cateLogObj, List<CloudSequenceBean> seqList) throws Exception
	{		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {			
			datastore.put(txn, cateObj.createEntity());
			datastore.put(txn, cateRelObj.createEntity());
			datastore.put(txn,cateLogObj.createEntity());
			txn.commit();	
			
		} catch (Exception e) {
			List<Entity> seqEntities = new ArrayList<Entity>();
			for(CloudSequenceBean item : seqList){
				item.RollBackSeq();
				seqEntities.add(item.createEntity());
			}
			datastore.put(seqEntities);
			throw e;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}		
	} 
	
	public void UpdateCategory(CategoryMasterBean cateObj, CategoryRelationshipBean cateRelObj, CategoryOplogBean cateLogObj) throws Exception
	{	
		Transaction txn = null;
		try {
			//find category master
			Filter IDEqualFilter = new FilterPredicate(
					CategoryMasterBean.id_property,
					FilterOperator.EQUAL, cateObj.getID());
			Query q = new Query(CategoryMasterBean.CATEGORY_ENTITY_KIND)
				            .setFilter(IDEqualFilter);
			PreparedQuery pq = datastore.prepare(q);
			Entity cateEntity = pq.asSingleEntity();
			if(cateEntity!=null){
				cateEntity.setProperty(CategoryMasterBean.name_property, cateObj.getCategory_name());			
				cateEntity.setProperty(CategoryMasterBean.updatedOn_property, new Date());	
			}

			//find category relationship by child id
			IDEqualFilter = new FilterPredicate(
					CategoryRelationshipBean.childID_property,
					FilterOperator.EQUAL, cateRelObj.getChildID());
			q = new Query(CategoryRelationshipBean.CATEGORY_RELATIONSHIP_ENTITY_KIND)
            		.setFilter(IDEqualFilter);
			pq = datastore.prepare(q);
			Entity cateRelEntity = pq.asSingleEntity();
			if(cateRelEntity!=null){
				cateRelEntity.setProperty(CategoryRelationshipBean.parentID_property, cateRelObj.getParentID());			
				cateRelEntity.setProperty(CategoryRelationshipBean.updatedOn_property, new Date());
			}
			if(cateEntity!=null && cateRelEntity!=null && cateLogObj!=null){
				TransactionOptions options = TransactionOptions.Builder.withXG(true);
				txn = datastore.beginTransaction(options);
				
				datastore.put(txn, cateEntity);
				datastore.put(txn, cateRelEntity);
				datastore.put(txn, cateLogObj.createEntity());
				txn.commit();
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (txn!=null && txn.isActive()) {
				txn.rollback();
			}
		}	
	}
	
	public List<Entity> GetAllCategories()
	{
		List<Entity> list = new ArrayList<Entity>();
		Query q = new Query(CategoryMasterBean.CATEGORY_ENTITY_KIND);
		q.addProjection(new PropertyProjection(CategoryMasterBean.name_property, String.class));
		q.addProjection(new PropertyProjection(CategoryMasterBean.id_property, Integer.class));
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {			
			list.add(result);
		}
		return  list; 
	}
	
	public List<Integer> GetAllChildCategorieIDs(int categoryParentID)
	{
		List<Integer> list = new ArrayList<Integer>();
		Filter IDEqualFilter = new FilterPredicate(
				CategoryRelationshipBean.parentID_property,
				FilterOperator.EQUAL, categoryParentID);
		Query q = new Query(CategoryRelationshipBean.CATEGORY_RELATIONSHIP_ENTITY_KIND)
					.setFilter(IDEqualFilter);					
		q.addProjection(new PropertyProjection(CategoryRelationshipBean.childID_property, Integer.class));		
		q.addSort(CategoryRelationshipBean.childID_property, Query.SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {			
			list.add(((Long) result.getProperty(CategoryRelationshipBean.childID_property)).intValue());
		}
		return  list;  
	}
	
	public List<Entity> GetAllCategoryRelationship()
	{
		List<Entity> list = new ArrayList<Entity>();
		Query q = new Query(CategoryRelationshipBean.CATEGORY_RELATIONSHIP_ENTITY_KIND);
		q.addProjection(new PropertyProjection(CategoryRelationshipBean.childID_property, Integer.class));
		q.addProjection(new PropertyProjection(CategoryRelationshipBean.parentID_property, Integer.class));
		q.addSort(CategoryRelationshipBean.parentID_property, Query.SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {			
			list.add(result);
		}
		return  list; 
	}
	
	public JSONArray GetCategoryOpLogArrFromId(int opLogID) throws JSONException
	{
		JSONArray jsonResArr = new JSONArray();
		CloudSequenceBean curSeqCateLogObj = getSequenceForEntityKind(CategoryOplogBean.CATEGORY_OPLOG_ENTITY_KIND);
		if(opLogID<curSeqCateLogObj.getSeq()){//there are something new
			Filter IDGreaterFilter = new FilterPredicate(
					CategoryOplogBean.id_property,
					FilterOperator.GREATER_THAN, opLogID);
			Query q = new Query(CategoryOplogBean.CATEGORY_OPLOG_ENTITY_KIND)
            		.setFilter(IDGreaterFilter);
			List<CategoryOplogBean> OpLogBeanlist = new ArrayList<CategoryOplogBean>();
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {			
				String jsonOplog =  (String) result.getProperty(CategoryOplogBean.oplogjson_property);
				JSONObject oplogObj = new JSONObject(jsonOplog);
				jsonResArr.put(oplogObj);
				CategoryOplogBean opLogBean = new CategoryOplogBean(result);
				opLogBean.setReadCount(opLogBean.getReadCount()+1);
				opLogBean.setReadDate(new Date());
				OpLogBeanlist.add(opLogBean);
			}
			UpdateCategoryOpLog(OpLogBeanlist);
		}
		return jsonResArr;
		
	}
	
	public void UpdateCategoryOpLog(List<CategoryOplogBean> OpLogBeanlist)
	{
		List<Entity> cateOplogEntities = new ArrayList<Entity>();
		for(CategoryOplogBean item: OpLogBeanlist){
			cateOplogEntities.add(item.createEntity());
		}
		if(cateOplogEntities.size()>0)
			datastore.put(cateOplogEntities);
	}
}

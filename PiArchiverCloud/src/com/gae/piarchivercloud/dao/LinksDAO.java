package com.gae.piarchivercloud.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.gae.piarchivercloud.bean.CategoryOplogBean;
import com.gae.piarchivercloud.bean.CategoryRelationshipBean;
import com.gae.piarchivercloud.bean.CloudSequenceBean;
import com.gae.piarchivercloud.bean.LinkUpdateBean;
import com.gae.piarchivercloud.bean.LinksBean;
import com.gae.piarchivercloud.pojo.LinkPojo;
import com.gae.piarchivercloud.util.UtilFn;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class LinksDAO extends PiArchiverDAO {	
	
	public int AddALink(LinksBean linkObj) throws Exception
	{	
		CloudSequenceBean curSeqObj = null;
		Entity seqEntity = GetCurrentSequenceForEntityKind(LinksBean.LINKS_ENTITY_KIND);
		if(seqEntity==null)
		{
			curSeqObj = new CloudSequenceBean();
			curSeqObj.setSeq(1);
			curSeqObj.setKind(LinksBean.LINKS_ENTITY_KIND);
			//curSeqObj.setCreatedOn(new Date());
			//curSeqObj.setUpdatedOn(new Date());
			seqEntity = curSeqObj.createEntity();
		}else
		{
			curSeqObj = new CloudSequenceBean(seqEntity);
			curSeqObj.setSeq(curSeqObj.getSeq()+1);
			curSeqObj.setUpdatedOn(new Date());			
			seqEntity.setProperty(CloudSequenceBean.sequence_property, curSeqObj.getSeq());			
			seqEntity.setProperty(CloudSequenceBean.updatedOn_property, curSeqObj.getUpdatedOn());
		}
		//update id for linkObj
		linkObj.setID(curSeqObj.getSeq());
		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			datastore.put(txn, seqEntity);
			datastore.put(txn, linkObj.createEntity());
			txn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		return curSeqObj.getSeq();
		
	}

	public ArrayList<LinkPojo> GetLinksHistoryFromDateToDate(String strStartDate, String strEndDate,int categoryId, String sortby) throws Exception{
		
		CategoriesDAO cateDAO = DAOFactory.getCategoriesDAO();
		Map<Integer, String> mapCategories = new HashMap<Integer, String>();					
		List<Entity> categoryEntities = cateDAO.GetAllCategories();
		for(Entity cateEntity:categoryEntities){			
			String cateName = (String) cateEntity.getProperty(CategoryMasterBean.name_property);
			int cateID = ((Long) cateEntity.getProperty(CategoryMasterBean.id_property)).intValue();						
			mapCategories.put(cateID, cateName);
		}
		List<Integer> listChildCateIDs;
		Filter categoryFilter =null;
		if(categoryId!=-1){
			listChildCateIDs = cateDAO.GetAllChildCategorieIDs(categoryId);
			listChildCateIDs.add(0, categoryId);
			categoryFilter = new FilterPredicate(
					LinksBean.CategoryID_property,
					FilterOperator.IN, listChildCateIDs);
		}
		
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd");
		Date startDate = null;
		Date endDate = null;
		Filter finalFilter =null;
		Filter maxFilter = null;
		Filter minFilter = null;
		if(!"0".equals(strStartDate))
		{
			startDate = dateformat.parse(strStartDate);
			minFilter = new FilterPredicate(
					LinksBean.createdOn_property,
					FilterOperator.GREATER_THAN_OR_EQUAL, startDate);
		}					
		if(!"1".equals(strEndDate))
		{
			endDate = dateformat.parse(strEndDate);
			maxFilter = new FilterPredicate(
					LinksBean.createdOn_property,
					FilterOperator.LESS_THAN_OR_EQUAL, endDate);
		}
		
		if(minFilter !=null && maxFilter!=null){
			finalFilter = CompositeFilterOperator.and(minFilter,	maxFilter);
		}else if(minFilter !=null){
			finalFilter = minFilter;
		}else if(maxFilter !=null){
			finalFilter = maxFilter;
		}
		
		if(categoryFilter!=null){
			if(finalFilter!=null)
				finalFilter = CompositeFilterOperator.and(finalFilter,	categoryFilter);
			else
				finalFilter = categoryFilter;
		}
		Query q = new Query(LinksBean.LINKS_ENTITY_KIND)
		 .setFilter(finalFilter);
		if(sortby.contains("date"))
			q.addSort(LinksBean.createdOn_property, SortDirection.DESCENDING);
		else if(sortby.contains("category"))
			q.addSort(LinksBean.CategoryID_property, SortDirection.ASCENDING);
		
		ArrayList<LinkPojo> results = new ArrayList<LinkPojo>() ;
		String cateNameTemp="";
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) {
			LinksBean linkObj = new LinksBean(result);	
			cateNameTemp="";
			if(mapCategories.containsKey(linkObj.getCategoryID()))
				cateNameTemp=mapCategories.get(linkObj.getCategoryID());			
			results.add(new LinkPojo(linkObj.getUrl(),
					linkObj.getNote(), 
					cateNameTemp, 
					UtilFn.truncateLetters(linkObj.getUrl(),30), 
					UtilFn.truncateWords(linkObj.getNote(),5), 
					UtilFn.truncateWords(cateNameTemp,5), 
					linkObj.getID(),
					linkObj.getCategoryID(),
					UtilFn.getDateString(linkObj.getCreatedOn(),"dd/MM/yyyy HH:mm")));						 
		}
		
		return results;
	}
	
	public boolean CheckLinkIsExisted(String url){
		//find category master
		Filter URLEqualFilter = new FilterPredicate(
				LinksBean.url_property,
				FilterOperator.EQUAL, url);
		Query q = new Query(LinksBean.LINKS_ENTITY_KIND)
			            .setFilter(URLEqualFilter);
		PreparedQuery pq = datastore.prepare(q);
		Entity linkEntity = pq.asSingleEntity();
		if(linkEntity!=null){
			return true;
		}
		return false;
		
	}
	/***
	 * get new links which have id > linkID from client
	 * @param linkID
	 * @return
	 * @throws JSONException
	 */
	public int GetLinksArrFromId(int linkID,JSONArray jsonResArr) throws JSONException
	{
		//JSONArray jsonResArr = new JSONArray();
		CloudSequenceBean curSeqObj = getSequenceForEntityKind(LinksBean.LINKS_ENTITY_KIND);
		if(linkID<curSeqObj.getSeq()){//there are something new
			Filter IDGreaterFilter = new FilterPredicate(
					LinksBean.id_property,
					FilterOperator.GREATER_THAN, linkID);
			Query q = new Query(LinksBean.LINKS_ENTITY_KIND)
            		.setFilter(IDGreaterFilter);			
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {			
				String url =  (String) result.getProperty(LinksBean.url_property);
				String note =  (String) result.getProperty(LinksBean.note_property);
				int id =  ((Long) result.getProperty(LinksBean.id_property)).intValue();
				int cateid =  ((Long) result.getProperty(LinksBean.CategoryID_property)).intValue();
				String createDate = UtilFn.getDateString((Date) result.getProperty(LinksBean.createdOn_property),"dd/MM/yyyy HH:mm:ss Z");
				
				JSONObject linkObj = new JSONObject();
				linkObj.put("cloudid", id);
				linkObj.put("url", url);
				linkObj.put("note", note);
				linkObj.put("categoryid", cateid);
				linkObj.put("createdon", createDate);
				jsonResArr.put(linkObj);				
			}			
		}
		return curSeqObj.getSeq();
		
	}
	
	/**
	 * get update links which have link id <= linkId from client
	 * @param linkID
	 * @return 
	 * @throws JSONException
	 */
	public int GetUpdateLinksArrFromId(int linkSeq,int updateLinkSeq,JSONArray jsonResArr) throws JSONException
	{
		
		CloudSequenceBean curSeqObj = getSequenceForEntityKind(LinkUpdateBean.LINKUPDATE_ENTITY_KIND);
		if(updateLinkSeq < curSeqObj.getSeq()){//there are something to update		
			//updateLinkId > updateLinkSeq: to find updated links
			Filter IDGreaterFilter = new FilterPredicate(
					LinkUpdateBean.id_property,
					FilterOperator.GREATER_THAN, updateLinkSeq);			
			
			Query q = new Query(LinkUpdateBean.LINKUPDATE_ENTITY_KIND)
	        		.setFilter(IDGreaterFilter);			
			PreparedQuery pq = datastore.prepare(q);
			for (Entity result : pq.asIterable()) {			
				
				String note =  (String) result.getProperty(LinkUpdateBean.note_property);
				int linkid =  ((Long) result.getProperty(LinkUpdateBean.LinkID_property)).intValue();
				int cateid =  ((Long) result.getProperty(LinksBean.CategoryID_property)).intValue();
				
				if(linkid<=linkSeq){//linkID<=linkSeq: to get old links
					JSONObject linkObj = new JSONObject();//for updating only need 3 fields
					linkObj.put("cloudid", linkid);			
					linkObj.put("note", note);
					linkObj.put("categoryid", cateid);
					jsonResArr.put(linkObj);
				}
			}	
		}
		return curSeqObj.getSeq();
		
	}
	
	/*
	public int getCurrentLinkSequence()throws Exception
	{
		Entity seqEntity = GetCurrentSequenceForEntityKind(LinksBean.LINKS_ENTITY_KIND);
		int res = 0;
		if(seqEntity!=null)
		{
			res = ((Long) seqEntity.getProperty(CloudSequenceBean.sequence_property)).intValue();
		}
		return res;
	}
	
	public int getCurrentUpdateLinkSequence()throws Exception
	{
		Entity seqEntity = GetCurrentSequenceForEntityKind(LinkUpdateBean.LINKUPDATE_ENTITY_KIND);
		int res = 0;
		if(seqEntity!=null)
		{
			res = ((Long) seqEntity.getProperty(CloudSequenceBean.sequence_property)).intValue();
		}
		return res;
	}*/
	
	public void UpdateLink(LinksBean linkObj,LinkUpdateBean linkUpdateObj) throws Exception
	{	
		//find link by id
		Filter IDEqualFilter = new FilterPredicate(
				LinksBean.id_property,
				FilterOperator.EQUAL, linkObj.getID());
		
		Query q = new Query(LinksBean.LINKS_ENTITY_KIND)
        		.setFilter(IDEqualFilter);			
		
		PreparedQuery pq = datastore.prepare(q);
		Entity linkEntity = pq.asSingleEntity();
		
		if(linkEntity!=null){
			linkEntity.setProperty(LinksBean.CategoryID_property, linkObj.getCategoryID());		
			linkEntity.setProperty(LinksBean.note_property, linkObj.getNote());	
			linkEntity.setProperty(LinksBean.updatedOn_property, new Date());
			//datastore.put(linkEntity);
		}
		
		//find linkupdate by link id
		IDEqualFilter = new FilterPredicate(
				LinkUpdateBean.LinkID_property,
				FilterOperator.EQUAL, linkUpdateObj.getLinkID());
		
		q = new Query(LinkUpdateBean.LINKUPDATE_ENTITY_KIND)
        		.setFilter(IDEqualFilter);			
		
		pq = datastore.prepare(q);
		Entity linkUpdateEntity = pq.asSingleEntity();
		
		//get sequence for linkUpdate
		CloudSequenceBean linkUpdateSeqObj = null;
		Entity linkUpdateSeqEntity = GetCurrentSequenceForEntityKind(LinkUpdateBean.LINKUPDATE_ENTITY_KIND);
		if(linkUpdateSeqEntity==null)
		{
			linkUpdateSeqObj = new CloudSequenceBean();
			linkUpdateSeqObj.setSeq(1);
			linkUpdateSeqObj.setKind(LinkUpdateBean.LINKUPDATE_ENTITY_KIND);			
			linkUpdateSeqEntity = linkUpdateSeqObj.createEntity();
		}else
		{
			linkUpdateSeqObj = new CloudSequenceBean(linkUpdateSeqEntity);
			linkUpdateSeqObj.setSeq(linkUpdateSeqObj.getSeq()+1);
			linkUpdateSeqObj.setUpdatedOn(new Date());			
			linkUpdateSeqEntity.setProperty(CloudSequenceBean.sequence_property, linkUpdateSeqObj.getSeq());			
			linkUpdateSeqEntity.setProperty(CloudSequenceBean.updatedOn_property, linkUpdateSeqObj.getUpdatedOn());
		}
		//update id for linkUpdateObj
		linkUpdateObj.setID(linkUpdateSeqObj.getSeq());
		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			//update link entity
			if(linkEntity!=null){			
				datastore.put(linkEntity);
			}
			//delete old linkupdate entity
			if(linkUpdateEntity!=null){			
				datastore.delete(linkUpdateEntity.getKey());
			}
			//update sequence entity for linkupdate kind
			datastore.put(txn, linkUpdateSeqEntity);
			//add new linkupdate entity
			datastore.put(txn, linkUpdateObj.createEntity());
			txn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		
	}

}

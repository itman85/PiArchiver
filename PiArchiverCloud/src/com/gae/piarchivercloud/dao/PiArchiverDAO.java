package com.gae.piarchivercloud.dao;

import java.util.Date;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.gae.piarchivercloud.bean.CloudSequenceBean;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public abstract class PiArchiverDAO {
	protected DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	
	
	
	protected Entity GetCurrentSequenceForEntityKind(String entityKind)
	{
		Filter KindEqualFilter = new FilterPredicate(
				CloudSequenceBean.kind_property,
				FilterOperator.EQUAL, entityKind);
		Query q = new Query(CloudSequenceBean.SEQUENCE_ENTITY_KIND).setFilter(KindEqualFilter);
		
		PreparedQuery pq = datastore.prepare(q);	
		return pq.asSingleEntity();	

	}
	
	public CloudSequenceBean getSequenceForEntityKind(String kind)
	{
		CloudSequenceBean curSeqObj = null;
		Entity seqCateEntity = GetCurrentSequenceForEntityKind(kind);
		if(seqCateEntity==null)
		{
			curSeqObj = new CloudSequenceBean();
			curSeqObj.setSeq(0);
			curSeqObj.setKind(kind);			
			
		}else
		{			
			curSeqObj = new CloudSequenceBean(seqCateEntity);
			curSeqObj.setUpdatedOn(new Date());
		}
		return curSeqObj;
	}
}

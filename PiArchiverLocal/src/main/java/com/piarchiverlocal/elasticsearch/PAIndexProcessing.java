package com.piarchiverlocal.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piarchiverlocal.pojo.LinkDocument;
import com.piarchiverlocal.util.PiConfig;
import com.piarchiverlocal.util.Utils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by phan on 3/25/2014.
 */
public class PAIndexProcessing {
    static Logger log4j = Logger.getLogger(PAIndexProcessing.class.getName());
    private static final String INDEX_NAME = "piaidx";//like database name
    private static final String TYPE_NAME  = "link";//like table name


    public static String AddLinkDocument(LinkDocument linkDoc) throws JsonProcessingException {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        // generate json
        String json = mapper.writeValueAsString(linkDoc);
        IndexResponse response =  ClientProvider.instance().getClient().prepareIndex(INDEX_NAME, TYPE_NAME)
                .setSource(json)
                .execute()
                .actionGet();
        return  response.getId();
    }

    public static void UpdateLinkDocument(LinkDocument linkDoc){
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(FilterBuilders.termFilter("cloudid", linkDoc.getCloudid()));
        SearchRequestBuilder searchRequestBuilder = ClientProvider.instance().getClient().prepareSearch(INDEX_NAME);
        searchRequestBuilder.setTypes(TYPE_NAME);
        searchRequestBuilder.setSearchType(SearchType.DEFAULT);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.setFrom(0).setSize(1).setExplain(true);

        SearchResponse response = searchRequestBuilder.execute().actionGet();

        if (response != null && response.getHits().getTotalHits()>0) {
            SearchHit hit = response.getHits().getAt(0);
            String docID = hit.getSource().get("id").toString();

            BulkRequestBuilder bulkRequest =  ClientProvider.instance().getClient().prepareBulk();
            //only update note and categoryid for link
            bulkRequest.add(ClientProvider.instance().getClient().prepareUpdate(INDEX_NAME, TYPE_NAME,docID)
                    .setScript("ctx._source.categoryid=" + linkDoc.getCategoryid(), ScriptService.ScriptType.INLINE));

            bulkRequest.add(ClientProvider.instance().getClient().prepareUpdate(INDEX_NAME, TYPE_NAME,docID)
                    .setScript("ctx._source.note=" + linkDoc.getNote(),ScriptService.ScriptType.INLINE));

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        }else{
            log4j.info("Cannot update document for linkid = "+linkDoc.getCloudid());
        }

    }
}

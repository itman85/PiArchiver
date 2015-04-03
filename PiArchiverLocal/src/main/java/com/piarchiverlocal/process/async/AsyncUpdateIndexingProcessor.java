package com.piarchiverlocal.process.async;

import com.piarchiverlocal.elasticsearch.PAIndexProcessing;
import com.piarchiverlocal.embeddeddb.ArchiverDAL;
import com.piarchiverlocal.pojo.LinkCloud;
import com.piarchiverlocal.pojo.LinkDocument;
import com.piarchiverlocal.process.webpage.Crawler;
import com.piarchiverlocal.util.PiConfig;
import com.piarchiverlocal.util.Utils;
import org.apache.log4j.Logger;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by phannguyen-pc on 3/30/2014.
 */
public class AsyncUpdateIndexingProcessor implements Runnable {
    static Logger log4j = Logger.getLogger(AsyncUpdateIndexingProcessor.class.getName());
    private AsyncContext asyncContext;
    private LinkCloud linkObj;

    public AsyncUpdateIndexingProcessor() {
    }

    public AsyncUpdateIndexingProcessor(AsyncContext asyncCtx, LinkCloud linkparam) {
        this.asyncContext = asyncCtx;
        this.linkObj = linkparam;
    }

    public void run() {
        log4j.info("Async Processor Start for link: " + linkObj.getUrl());
        try {
            longProcessing();
        } catch (IOException e) {
            log4j.error(e.getMessage() + " " + e.getCause() + " " + e.getStackTrace());
        }
        /*try
				 {
			PrintWriter out = asyncContext.getResponse().getWriter();
			out.write("Async Processing done for " + secs + " milliseconds!!");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        //complete the processing
        //asyncContext.complete();
        log4j.info("Async Processor End for link: " + linkObj.getUrl());
    }

    private void longProcessing() throws IOException {
        //init link document for indexing in elasticsearch
        LinkDocument linkDoc = new LinkDocument();
        linkDoc.setCloudid(linkObj.getCloudid());
        linkDoc.setCategoryid(linkObj.getCategoryid());
        linkDoc.setNote(linkObj.getNote());
        //add document into index
        log4j.info("Start update index link document For link id: " + linkObj.getCloudid());
        PAIndexProcessing.UpdateLinkDocument(linkDoc);
        log4j.info("End update index link document For link id: " + linkObj.getCloudid());
        ArchiverDAL.instance().UpdateLinkData(linkObj);
    }
}

package com.piarchiverlocal.process.async;

import com.piarchiverlocal.elasticsearch.PAIndexProcessing;
import com.piarchiverlocal.embeddeddb.ArchiverDAL;
import com.piarchiverlocal.pojo.LinkCloud;
import com.piarchiverlocal.pojo.LinkDocument;
import com.piarchiverlocal.process.webpage.Crawler;

import javax.servlet.AsyncContext;
import java.io.*;
import java.util.UUID;

import com.piarchiverlocal.util.PiConfig;
import com.piarchiverlocal.util.Utils;
import org.apache.log4j.Logger;
public class AsyncArchivingProcessor implements Runnable {
    static Logger log4j = Logger.getLogger(AsyncArchivingProcessor.class.getName());
    private String rootPath ;
	private AsyncContext asyncContext;
	private LinkCloud linkObj;

	public AsyncArchivingProcessor() {
	}

	public AsyncArchivingProcessor(AsyncContext asyncCtx, LinkCloud linkparam) {
		this.asyncContext = asyncCtx;
		this.linkObj = linkparam;
        rootPath = PiConfig.getInstance().getArchiverFolderPath();
    }

	public void run() {
        log4j.info("Async Processor Start for link: "+linkObj.getUrl());
        try {
            if(rootPath==""){
                log4j.error("rootPath for archive web page is empty");
            }else {
                longProcessing();
            }
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
        log4j.info("Async Processor End for link: "+linkObj.getUrl());
	}

	private void longProcessing() throws IOException {
        UUID uniqueFolder = UUID.randomUUID();
        if(Crawler.createFolder(rootPath + uniqueFolder))   {
            log4j.info("Start download file into folder:"+uniqueFolder + " For url "+ linkObj.getUrl());
            String filename = Crawler.DownloadWebpage(linkObj.getUrl(),rootPath+uniqueFolder);
            Crawler.DownloadResourcesInWebpage(rootPath+uniqueFolder,filename);
            log4j.info("End download file into folder:"+uniqueFolder+ " For url "+ linkObj.getUrl());

            //init link document for indexing in elasticsearch
            LinkDocument linkDoc = new LinkDocument();
            linkDoc.setCloudid(linkObj.getCloudid());
            linkDoc.setCategoryid(linkObj.getCategoryid());
            linkDoc.setUrl(linkObj.getUrl());
            linkDoc.setNote(linkObj.getNote());
            linkDoc.setCreatedon(linkObj.getCreatedon());
            linkDoc.setLocalPagePath(uniqueFolder+PiConfig.DOWNLOAD_PAGE_LOCAL_NAME);
            linkDoc.setOriginalPagePath(uniqueFolder+PiConfig.DOWNLOAD_PAGE_ORIGINAL_NAME);
            StringBuilder fileContent = Utils.ReadLargeFile(rootPath+uniqueFolder+PiConfig.EXTRACT_TEXT_FILE_NAME);
            linkDoc.setContent(fileContent.toString());
            //add document into index
            log4j.info("Start index link document For url: "+ linkObj.getUrl());
            PAIndexProcessing.AddLinkDocument(linkDoc);
            log4j.info("End index link document For url: "+ linkObj.getUrl());
            ArchiverDAL.instance().InsertLinkData(linkObj);
        }
	}
}

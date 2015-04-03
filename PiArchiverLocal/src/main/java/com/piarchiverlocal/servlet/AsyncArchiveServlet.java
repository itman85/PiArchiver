package com.piarchiverlocal.servlet;

import com.google.gson.Gson;
import com.piarchiverlocal.pojo.LinkCloud;
import com.piarchiverlocal.process.async.AsyncArchivingProcessor;
import com.piarchiverlocal.process.async.AsyncUpdateIndexingProcessor;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.log4j.Logger;

/**
 * Created by phannguyen-pc on 3/22/2014.
 */
@WebServlet(urlPatterns = "/archivelinks")//asyncSupported = true
public class AsyncArchiveServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Logger log4j = Logger.getLogger(AsyncArchiveServlet.class.getName());
        log4j.info("Get method not available.");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Logger log4j = Logger.getLogger(AsyncArchiveServlet.class.getName());
        String classp = AsyncArchiveServlet.class.getClassLoader().toString();
        log4j.info("New archive request comes");
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonparam = "";//include new list and update list for links
        if(br != null){
            jsonparam = br.readLine();
        }
        if(jsonparam!=null && !jsonparam.equals(""))
        {
            log4j.info("Data request:"+jsonparam);
            Gson GsonMapper = new Gson();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) req.getServletContext().getAttribute("executor");
             /* AsyncContext asyncCtx = request.startAsync();
                asyncCtx.addListener(new AppAsyncListener());
                asyncCtx.setTimeout(100000);*/
            try{
                JSONObject jsonLinks = new JSONObject(jsonparam);
                JSONArray newLinks = jsonLinks.getJSONArray("resultnewlist");
                JSONArray updateLinks = jsonLinks.getJSONArray("resultupdatelist");
                //archive for new links
                for(int i=0;i<newLinks.length();i++)
                {
                    JSONObject jsonObj = newLinks.getJSONObject(i);
                    LinkCloud linkObj = GsonMapper.fromJson(jsonObj.toString(), LinkCloud.class);
                    executor.execute(new AsyncArchivingProcessor(null, linkObj));
                }
                //archive for update links
                for(int i=0;i<updateLinks.length();i++)
                {
                    JSONObject jsonObj = updateLinks.getJSONObject(i);
                    LinkCloud linkObj = GsonMapper.fromJson(jsonObj.toString(), LinkCloud.class);
                    executor.execute(new AsyncUpdateIndexingProcessor(null, linkObj));
                }

            }catch (Exception ex){
                log4j.error(ex.getMessage());
            }
        }

        //finish servlet processing although downloading threads still working
        PrintWriter out = res.getWriter();
        java.util.Date date= new java.util.Date();
        out.write( "Archive Servlet finish @"+new Timestamp(date.getTime()) );
        out.flush();
        out.close();
    }
}

package com.piarchiverlocal.servlet;

import com.google.gson.Gson;
import com.piarchiverlocal.embeddeddb.ArchiverDAL;
import com.piarchiverlocal.pojo.LinkCloud;
import com.piarchiverlocal.process.async.AsyncArchivingProcessor;
import org.apache.log4j.Logger;
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

/**
 * Created by phan on 3/28/2014.
 */
@WebServlet(urlPatterns = "/archivecategory")
public class ArchiveCategoryServlet  extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        java.util.Date date= new java.util.Date();
        out.write( "Get method not available.");
        out.flush();
        out.close();
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Logger log4j = Logger.getLogger(AsyncArchiveServlet.class.getName());
        String classp = AsyncArchiveServlet.class.getClassLoader().toString();
        log4j.info("New archive category request comes");
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonparam = "";
        if(br != null){
            jsonparam = br.readLine();
        }
        if(jsonparam!=null && !jsonparam.equals(""))
        {
            ArchiverDAL.instance().InsertOrUpdateCategoryData(jsonparam);
        }

        //finish servlet processing although downloading threads still working
        PrintWriter out = res.getWriter();
        java.util.Date date= new java.util.Date();
        out.write( "Archive Servlet finish @"+new Timestamp(date.getTime()) );
        out.flush();
        out.close();
    }
}

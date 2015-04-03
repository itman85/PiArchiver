package com.gae.piarchivercloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.stylesheets.LinkStyle;

import com.gae.piarchivercloud.bean.LinksBean;
import com.gae.piarchivercloud.dao.DAOFactory;
import com.gae.piarchivercloud.dao.LinksDAO;
import com.gae.piarchivercloud.util.Constant;
import com.gae.piarchivercloud.util.UtilFn;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class LinkExtensionServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (req.getServletPath().equals(
				Constant.SUBMIT_LINK_FROM_EXTENSION_SERVLETPATH)) {
			JSONObject resJsonObj = new JSONObject();//this json obj for response 
			String resStatus = "";
			int latestLinkId = 0;
			int latestUpdateLinkId = 0;
			JSONArray jsonNewLinksArr = new JSONArray();
			JSONArray jsonUpdateLinksArr = new JSONArray();
			try
			{	    
				String linkURL = URLDecoder.decode(req.getParameter("linkurl"),"UTF-8").trim();
				String linkNote = URLDecoder.decode(req.getParameter("linknote"),"UTF-8").trim();
				int categoryID = Integer.parseInt(URLDecoder.decode(req.getParameter("categoryid"),"UTF-8").trim());
				int linkSequence = Integer.parseInt(URLDecoder.decode(req.getParameter("linksequence"),"UTF-8").trim());
				int updateLinkSequence = Integer.parseInt(URLDecoder.decode(req.getParameter("updatelinksequence"),"UTF-8").trim());
				LinksDAO linksDAOObj = DAOFactory.getLinksDAO();				
				if(!linksDAOObj.CheckLinkIsExisted(linkURL)){
					
					linksDAOObj.GetLinksArrFromId(linkSequence,jsonNewLinksArr);
					latestUpdateLinkId = linksDAOObj.GetUpdateLinksArrFromId(linkSequence,updateLinkSequence,jsonUpdateLinksArr);
					
				    LinksBean linkObj = new LinksBean();
				    linkObj.setUrl(linkURL);
				    linkObj.setNote(linkNote);
				    linkObj.setCategoryID(categoryID);				    
				    int cloudid = linksDAOObj.AddALink(linkObj);
				    
				    JSONObject linkjObj = new JSONObject();
				    linkjObj.put("cloudid", cloudid);
				    linkjObj.put("url", linkURL);
				    linkjObj.put("note", linkNote);
				    linkjObj.put("categoryid", categoryID);
				    linkjObj.put("createdon", UtilFn.getDateString(new Date(),"dd/MM/yyyy HH:mm:ss Z"));
				    jsonNewLinksArr.put(linkjObj);	
					
				    resStatus = "Submit successfully";	
				    if(jsonNewLinksArr.length()>0)
						resStatus += "\n\r"+"- Sync new " + jsonNewLinksArr.length() + " link(s) successfully";					
					
					if(jsonUpdateLinksArr.length()>0)
						resStatus += "\n\r"+"- Sync update " + jsonUpdateLinksArr.length() + " link(s) successfully";	
					
				    latestLinkId = cloudid;
				    
				}else{
					resStatus = "This link is existed!";					
					latestLinkId = 0;
					latestUpdateLinkId = 0;
				}
			}					
			catch (Exception ex) 	    
			{
				resStatus = ex.getMessage();
				jsonNewLinksArr = new JSONArray();
				jsonUpdateLinksArr = new JSONArray();
				latestLinkId = 0;
				latestUpdateLinkId = 0;
			}finally {
				try {
					resJsonObj.put("resultnewlist", jsonNewLinksArr);
					resJsonObj.put("resultupdatelist", jsonUpdateLinksArr);
					resJsonObj.put("message", resStatus);
					resJsonObj.put("currentcloudid", latestLinkId);
					resJsonObj.put("currentupdatecloudid", latestUpdateLinkId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resp.setContentType("application/json");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println(resJsonObj.toString());//send response with json string to client					
			}
		}else if (req.getServletPath().equals(
				Constant.GET_LINKS_FROM_EXTENSION_SERVLETPATH)) {
			JSONObject resJsonObj = new JSONObject();//this json obj for response 
			String resStatus = "";
			int latestLinkId = 0;
			int latestUpdateLinkId = 0;
			JSONArray jsonNewLinksArr = new JSONArray();
			JSONArray jsonUpdateLinksArr = new JSONArray();
			try
			{				
				int linkSequence = Integer.parseInt(URLDecoder.decode(req.getParameter("linksequence"),"UTF-8").trim());
				int updateLinkSequence = Integer.parseInt(URLDecoder.decode(req.getParameter("updatelinksequence"),"UTF-8").trim());
				LinksDAO linksDAOObj = DAOFactory.getLinksDAO();
				
				latestLinkId = linksDAOObj.GetLinksArrFromId(linkSequence,jsonNewLinksArr);
				latestUpdateLinkId = linksDAOObj.GetUpdateLinksArrFromId(linkSequence,updateLinkSequence,jsonUpdateLinksArr);				
				
				if(jsonNewLinksArr.length()>0)
					resStatus = "- Sync new " + jsonNewLinksArr.length() + " link(s) successfully"+"\n\r";	
				else
					resStatus = "- There is nothing new"+"\n\r";
				
				if(jsonUpdateLinksArr.length()>0)
					resStatus += "- Sync update " + jsonUpdateLinksArr.length() + " link(s) successfully";	
				else
					resStatus += "- There is nothing update";
			}					
			catch (Exception ex) 	    
			{
				resStatus = ex.getMessage();
				jsonNewLinksArr = new JSONArray();
				jsonUpdateLinksArr = new JSONArray();
				latestLinkId = 0;
				latestUpdateLinkId = 0;
			}finally {
				try {
					resJsonObj.put("resultnewlist", jsonNewLinksArr);	
					resJsonObj.put("resultupdatelist", jsonUpdateLinksArr);
					resJsonObj.put("message", resStatus);
					resJsonObj.put("currentcloudid", latestLinkId);
					resJsonObj.put("currentupdatecloudid", latestUpdateLinkId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resp.setContentType("application/json");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println(resJsonObj.toString());//send response with json string to client					
			}
		}
		
	}
}

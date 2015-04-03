package com.gae.piarchivercloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gae.piarchivercloud.bean.LinkUpdateBean;
import com.gae.piarchivercloud.bean.LinksBean;
import com.gae.piarchivercloud.bean.UserBean;
import com.gae.piarchivercloud.dao.DAOFactory;
import com.gae.piarchivercloud.dao.LinksDAO;
import com.gae.piarchivercloud.pojo.LinkPojo;
import com.gae.piarchivercloud.util.Constant;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class LinksServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Welcome to PiArchiverCloud");
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = req.getSession(true);	  
		UserBean currentUser = ((UserBean) (session.getAttribute("currentSessionUser")));
		if(currentUser==null)
		{
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(String.format(Constant.EXCEPTION,"You are not login!"));
			resp.getWriter().println("<a href='\'>Back to login page.</a>");
		}else
		{
			if (req.getServletPath().equals(
					Constant.SUBMIT_LINK_SERVLETPATH)) {
				try
				{	    
					String linkURL = URLDecoder.decode(req.getParameter("linkurl"),"UTF-8").trim();
					String linkNote = URLDecoder.decode(req.getParameter("linknote"),"UTF-8").trim();
					int categoryID = Integer.parseInt(URLDecoder.decode(req.getParameter("categoryid"),"UTF-8").trim());
					LinksDAO linksDAOObj = DAOFactory.getLinksDAO();
					if(!linksDAOObj.CheckLinkIsExisted(linkURL)){
					    LinksBean linkObj = new LinksBean();
					    linkObj.setUrl(linkURL);
					    linkObj.setNote(linkNote);
					    linkObj.setCategoryID(categoryID);				    
					    linksDAOObj.AddALink(linkObj);
					    resp.setContentType("text/plain");					
						resp.getWriter().println("Submit successfully");
					}else{
						resp.setContentType("text/plain");					
						resp.getWriter().println("This link is existed!");
					}
				}					
				catch (Exception ex) 	    
				{
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}else if(req.getServletPath().equals(
					Constant.GET_LINKS_HISTORY_SERVLETPATH)){
				JsonArray jsonLinksArr = null;
				JsonObject jsonRes = new JsonObject();
				String resStatus = "";
				try {					
					
					String strStartDate = URLDecoder.decode(
							req.getParameter("startdate"), "UTF-8").trim();
					
					String strEndDate = URLDecoder.decode(
							req.getParameter("enddate"), "UTF-8").trim();
					
					String strCateID = URLDecoder.decode(
							req.getParameter("categoryid"), "UTF-8").trim();
					String sortby = URLDecoder.decode(
							req.getParameter("sort"), "UTF-8").trim();
					int categoryId = -1;
					if(strCateID!=null && !"".equals(strCateID))
						categoryId = Integer.parseInt(strCateID);
					
					LinksDAO linksDAOObj = DAOFactory.getLinksDAO();					
					
					ArrayList<LinkPojo> arrLinks = linksDAOObj.GetLinksHistoryFromDateToDate(strStartDate, strEndDate,categoryId,sortby);
					//req.setAttribute("LinksHisResults", arrLinks);					
					//req.getRequestDispatcher("webcontent/jsp/LinksHisRes.jsp").forward(req, resp);
					
					//convert from list to json array object
					Gson GsonMapper = new Gson();
					JsonElement element = GsonMapper.toJsonTree(arrLinks, new TypeToken<List<LinkPojo>>() {}.getType());

					if (! element.isJsonArray()) {
					// fail appropriately
					    throw new Exception("Cannot convert history data to json");
					}

					jsonLinksArr = element.getAsJsonArray();					
					resStatus = "There are "+jsonLinksArr.size()+" links in this period of time.";
					
					
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					jsonLinksArr = new JsonArray();
					resStatus = ex.getMessage();
					
				}finally{
					try {
						jsonRes.add("linksarr", jsonLinksArr);						
						jsonRes.addProperty("message", resStatus);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resp.setContentType("application/json");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(jsonRes.toString());//send response with json string to client						
				}			
				
			
			}else if (req.getServletPath().equals(
					Constant.UPDATE_LINK_SERVLETPATH)) {
				try
				{	    
					int linkID =  Integer.parseInt(URLDecoder.decode(req.getParameter("linkid"),"UTF-8").trim());
					String linkNote = URLDecoder.decode(req.getParameter("linknote"),"UTF-8").trim();
					int categoryID = Integer.parseInt(URLDecoder.decode(req.getParameter("categoryid"),"UTF-8").trim());
					
					LinksBean linkObj = new LinksBean();
					linkObj.setID(linkID);
					linkObj.setNote(linkNote);
					linkObj.setCategoryID(categoryID);
					
					LinkUpdateBean linkUpdateObj = new LinkUpdateBean();
					linkUpdateObj.setCategoryID(categoryID);
					linkUpdateObj.setLinkID(linkID);
					linkUpdateObj.setNote(linkNote);
					
					LinksDAO linksDAOObj = DAOFactory.getLinksDAO();
					
					linksDAOObj.UpdateLink(linkObj, linkUpdateObj);
					
					resp.setContentType("text/plain");					
					resp.getWriter().println("Update successfully");					
					
				}					
				catch (Exception ex) 	    
				{
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}
		}
		
	}
}

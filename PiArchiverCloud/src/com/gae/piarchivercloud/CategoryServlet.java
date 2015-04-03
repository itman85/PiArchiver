package com.gae.piarchivercloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.gae.piarchivercloud.bean.CategoryOplogBean;
import com.gae.piarchivercloud.bean.CategoryRelationshipBean;
import com.gae.piarchivercloud.bean.CloudSequenceBean;
import com.gae.piarchivercloud.bean.UserBean;
import com.gae.piarchivercloud.dao.CategoriesDAO;
import com.gae.piarchivercloud.dao.DAOFactory;
import com.gae.piarchivercloud.pojo.CategoryChangingPojo;
import com.gae.piarchivercloud.util.CategoryTree;
import com.gae.piarchivercloud.util.Constant;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;


public class CategoryServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		/*String resJson = "[{\"title\": \"Item 1\",\"key\": \"1\"},"+
	        "{\"title\": \"Folder 2\", \"key\": \"2\","+
	          "\"children\": [{\"title\": \"Sub-item 2.1\", \"key\": \"3\"},"+
	            "{\"title\": \"Sub-item 2.2\", \"key\": \"4\"}]},"+
	        "{\"title\": \"Item 3\", \"key\": \"5\"}]";*/
		String resJson = InitCategoryTreeJsonFromDatastore();
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println(resJson);//send response with json string to client		
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(true);	
		UserBean currentUser = ((UserBean) (session.getAttribute("currentSessionUser")));
		if(currentUser==null)
		{
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(String.format(Constant.EXCEPTION,"You are not login!"));
			resp.getWriter().println("<a href='\'>Back to login page.</a>");
			return;
		}
		JSONArray arrResJsonObjs = new JSONArray();
		JSONObject resJsonObj = new JSONObject();//this json obj for response 
		String resStatus = "";
		CloudSequenceBean curSeqCateObj = null,curSeqCateRelObj = null,curSeqCateLogObj =null;
		 // 1. get received JSON data from request
        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String jsonparam = "";
        if(br != null){
        	jsonparam = br.readLine();
        }
        if(!jsonparam.equals(""))
        {
			try {
				JSONArray arrJsonObjs = new JSONArray(jsonparam);				
				Gson GsonMapper = new Gson();
				CategoriesDAO cateDAO = DAOFactory.getCategoriesDAO();
				List<CategoryChangingPojo> newCateChangingList = new ArrayList<CategoryChangingPojo>();
				
				//get current sequence for categories groups				
				boolean isReadSequence = false;
				boolean isReadOpLogSequence = false;
				for(int i=0;i<arrJsonObjs.length();i++)
				{					
					JSONObject jsonObj = arrJsonObjs.getJSONObject(i);					
					CategoryChangingPojo CateChangingObj = GsonMapper.fromJson(jsonObj.toString(), CategoryChangingPojo.class);
					if(CateChangingObj.getKey()==0 && CateChangingObj.getNewid()!=0){//creating new category
						if(!isReadSequence)
						{
							curSeqCateObj = cateDAO.getSequenceForEntityKind(CategoryMasterBean.CATEGORY_ENTITY_KIND);
							curSeqCateRelObj = cateDAO.getSequenceForEntityKind(CategoryRelationshipBean.CATEGORY_RELATIONSHIP_ENTITY_KIND);
							if(!isReadOpLogSequence){
								curSeqCateLogObj = cateDAO.getSequenceForEntityKind(CategoryOplogBean.CATEGORY_OPLOG_ENTITY_KIND);
								isReadOpLogSequence = true;
							}
							isReadSequence = true;
						}
						
						curSeqCateObj.increaseSeq();
						curSeqCateRelObj.increaseSeq();
						curSeqCateLogObj.increaseSeq();
						
						CategoryMasterBean cateMasObj = new CategoryMasterBean();
						cateMasObj.setCategory_name(CateChangingObj.getName());						
						cateMasObj.setID(curSeqCateObj.getSeq());
						
						CategoryRelationshipBean cateRelObj = new CategoryRelationshipBean();
						cateRelObj.setID(curSeqCateRelObj.getSeq());
						cateRelObj.setParentID(CateChangingObj.getParentkey());
						cateRelObj.setChildID(cateMasObj.getID());						
						
						if(CateChangingObj.getParentkey()==0 && CateChangingObj.getParentnewid()!=0)
						{
							for(CategoryChangingPojo item:newCateChangingList){
								if(item.getNewid()==CateChangingObj.getParentnewid()){
									cateRelObj.setParentID(item.getKey());
									break;
								}
							}
						}
						
						CategoryOplogBean cateLogObj = new CategoryOplogBean();
						cateLogObj.makeOpLogJson(cateMasObj.getCategory_name(), String.valueOf(cateRelObj.getChildID()),String.valueOf(cateRelObj.getParentID()),true);
						cateLogObj.setID(curSeqCateLogObj.getSeq());
						
						cateDAO.AddNewCategory(cateMasObj, cateRelObj,cateLogObj,
								Arrays.asList(curSeqCateObj,curSeqCateRelObj,curSeqCateLogObj));
						
						CateChangingObj.setKey(cateMasObj.getID());
						//CateChangingObj.setParentkey(cateRelObj.getParentID());
						jsonObj.put("key", CateChangingObj.getKey());
						//jsonObj.put("parentkey", CateChangingObj.getParentkey());
						arrResJsonObjs.put(jsonObj);
						newCateChangingList.add(CateChangingObj);//this cate is new and add to new list
						
					}	
				}
				for(int i=0;i<arrJsonObjs.length();i++)
				{					
					JSONObject jsonObj = arrJsonObjs.getJSONObject(i);					
					CategoryChangingPojo CateChangingObj = GsonMapper.fromJson(jsonObj.toString(), CategoryChangingPojo.class);
					if(CateChangingObj.getKey()!=0 && CateChangingObj.getNewid()==0){//update changing of category
						if(!isReadOpLogSequence){
							curSeqCateLogObj = cateDAO.getSequenceForEntityKind(CategoryOplogBean.CATEGORY_OPLOG_ENTITY_KIND);
							isReadOpLogSequence = true;
						}
						curSeqCateLogObj.increaseSeq();
						
						CategoryMasterBean cateMasObj = new CategoryMasterBean();
						cateMasObj.setCategory_name(CateChangingObj.getName());						
						cateMasObj.setID(CateChangingObj.getKey());
						
						CategoryRelationshipBean cateRelObj = new CategoryRelationshipBean();	
						cateRelObj.setChildID(cateMasObj.getID());
						cateRelObj.setParentID(CateChangingObj.getParentkey());
						if(CateChangingObj.getParentkey()==0 && CateChangingObj.getParentnewid()!=0)
						{
							for(CategoryChangingPojo item:newCateChangingList){
								if(item.getNewid()==CateChangingObj.getParentnewid()){
									cateRelObj.setParentID(item.getKey());
									break;
								}
							}
						}
						CategoryOplogBean cateLogObj = new CategoryOplogBean();
						cateLogObj.makeOpLogJson(cateMasObj.getCategory_name(), String.valueOf(cateRelObj.getChildID()),String.valueOf(cateRelObj.getParentID()),false);
						cateLogObj.setID(curSeqCateLogObj.getSeq());
						
						cateDAO.UpdateCategory(cateMasObj, cateRelObj, cateLogObj);
					}			
				}
				if(isReadSequence)
				{
					cateDAO.UpdateSequenceForCategoriesGroups(curSeqCateObj, curSeqCateRelObj, curSeqCateLogObj);
				}else
				{
					if(isReadOpLogSequence)
					{
						cateDAO.UpdateSequenceForCategoriesGroups(null, null, curSeqCateLogObj);
					}
				}
			}
			catch (Exception ex) {
				// TODO Auto-generated catch block
				resStatus = ex.getMessage();				
			}finally {
				try {
					resJsonObj.put("resultlist", arrResJsonObjs);
					resStatus = "Apply changes successfully!" + "\n" + resStatus;					
					resJsonObj.put("message", resStatus);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resp.setContentType("application/json");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println(resJsonObj.toString());//send response with json string to client					
			}
        }else
        {
        	resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println("Data from client is invalid!");
        }
	}
	
	private String InitCategoryTreeJsonFromDatastore()
	{
		String cateJsonString = "";
		try{
			CategoriesDAO cateDAO = DAOFactory.getCategoriesDAO();
			Map<Integer, CategoryMasterBean> mapCategories = new HashMap<Integer, CategoryMasterBean>();
			CategoryTree cateTree = new CategoryTree(new CategoryMasterBean("root", 0));
			List<Entity> categoryEntities = cateDAO.GetAllCategories();
			for(Entity cateEntity:categoryEntities){
				CategoryMasterBean cateObj = new CategoryMasterBean();
				cateObj.setCategory_name((String) cateEntity.getProperty(CategoryMasterBean.name_property));
				cateObj.setID(((Long) cateEntity.getProperty(CategoryMasterBean.id_property)).intValue());
				mapCategories.put(cateObj.getID(), cateObj);
			}
			List<Entity> categoryRelEntities = cateDAO.GetAllCategoryRelationship();
			HashMap <Integer,Integer> currentCateIDLevelMap = new HashMap<Integer,Integer>();
			List<CategoryRelationshipBean> CateInCorrectLevelList = new ArrayList<CategoryRelationshipBean>();
			currentCateIDLevelMap.put(0,0);
			List<Entity> tempEntities = new ArrayList<Entity>();
			while(categoryRelEntities.size()>0)
			{
				for(Entity cateRelEntity:categoryRelEntities){
					int childID = ((Long) cateRelEntity.getProperty(CategoryRelationshipBean.childID_property)).intValue();
					int parentID = ((Long) cateRelEntity.getProperty(CategoryRelationshipBean.parentID_property)).intValue();
					if(currentCateIDLevelMap.containsKey(parentID)){
						CateInCorrectLevelList.add(new CategoryRelationshipBean(childID, parentID));
						tempEntities.add(cateRelEntity);
					}					
				}
				if(tempEntities.size()>0){
					currentCateIDLevelMap.clear();
					for(Entity tempEntity:tempEntities){
						int childID = ((Long) tempEntity.getProperty(CategoryRelationshipBean.childID_property)).intValue();
						int parentID = ((Long) tempEntity.getProperty(CategoryRelationshipBean.parentID_property)).intValue();
						currentCateIDLevelMap.put(childID,parentID);
					}
					categoryRelEntities.removeAll(tempEntities);	
					tempEntities.clear();
				}
			}
			//now cate relation ship list in correct order
			if(CateInCorrectLevelList.size()>0){
				for(CategoryRelationshipBean item:CateInCorrectLevelList){
					if(mapCategories.containsKey(item.getChildID()))
					{
						cateTree.addCategoryNodeWithParentID(mapCategories.get(item.getChildID()), item.getParentID());
					}
				}
				if(!cateTree.isEmpty())
					cateJsonString = cateTree.ConvertTreeDataToJsonString();
			}
			
		}catch(Exception ex){
			cateJsonString = "";
		}
		return cateJsonString;
	}
}

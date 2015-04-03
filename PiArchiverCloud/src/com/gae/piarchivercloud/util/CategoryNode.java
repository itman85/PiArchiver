package com.gae.piarchivercloud.util;

import java.util.LinkedList;
import java.util.List;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class CategoryNode{
	public CategoryMasterBean data;
	public CategoryNode parent = null;
    public List<CategoryNode> children = null;
    
    public CategoryNode(CategoryMasterBean data) {
        this.data = data;        
        this.children = new LinkedList<CategoryNode>();      
    }
    
    public boolean isRoot() {
        return parent == null;
    }

	public boolean isLeaf() {
	        return children.size() == 0;
	}
	
	public CategoryNode addChild(CategoryMasterBean child) {
        CategoryNode childNode = new CategoryNode(child);
        childNode.parent = this;
        this.children.add(childNode);       
        return childNode;
	}
	
	public CategoryNode findCategoryNodeWithID(int nodeID){
		for(CategoryNode child : children) {
			if(child.data.getID()==nodeID)
				return child;
			CategoryNode res= child.findCategoryNodeWithID(nodeID);
			if(res!=null)
				return res;
		  }
		return null;
	}
	
	public JSONObject getJsonData() throws JSONException
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("title", this.data.getCategory_name());
		jsonObj.put("key", this.data.getID());
		jsonObj.put("newid", 0);
		JSONArray arrJsonChildrenObjs = new JSONArray();
		for(CategoryNode child : children) {
			JSONObject jsonChildObj = child.getJsonData();
			arrJsonChildrenObjs.put(jsonChildObj);
		  }
		if(arrJsonChildrenObjs.length()>0)
			jsonObj.put("children", arrJsonChildrenObjs);
		return jsonObj;
	}
}

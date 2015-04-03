package com.gae.piarchivercloud.util;

import com.gae.piarchivercloud.bean.CategoryMasterBean;
import com.google.appengine.labs.repackaged.org.json.JSONException;

public class CategoryTree {
	public CategoryNode root = null;
	public CategoryTree(CategoryMasterBean rootData)
	{
		root = new CategoryNode(rootData);
	}
	
	public void addCategoryNodeWithParentID(CategoryMasterBean childData, int parentID)
	{
		if(root.data.getID()==parentID){
			root.addChild(childData);
		}else{
			CategoryNode parentNode = root.findCategoryNodeWithID(parentID);
			if(parentNode!=null)
			{
				parentNode.addChild(childData);
			}
		}
	}
	
	public String ConvertTreeDataToJsonString() throws JSONException{
		return root.getJsonData().getString("children");
	}
	public boolean isEmpty()
	{
		return root.children.size()==0;
	}
	
}

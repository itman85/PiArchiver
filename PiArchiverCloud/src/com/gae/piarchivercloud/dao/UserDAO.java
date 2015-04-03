package com.gae.piarchivercloud.dao;

import com.gae.piarchivercloud.bean.UserBean;

public class UserDAO extends PiArchiverDAO{
	
	static String USERNAME = "admin";
	static String PASSWORD = "123";
	
	
	
	
    public UserBean login(UserBean bean) {
	
     if(bean !=null)
     {
    	 if(bean.getUsername().compareToIgnoreCase(USERNAME)==0 && bean.getPassword().compareTo(PASSWORD)==0)
    		 bean.setValid(true);
    	 else
    		 bean.setValid(false);
     }

     return bean;
	
    }	
}

package com.gae.piarchivercloud.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilFn {
	public static String truncateWords(String str,int n){
		if(str==null || "".equals(str))
			return "";
		String[] words = str.split(" ");  
		String res ="";  
		for(int i=0;i<words.length && i<n;i++)
			res+= words[i]+" ";
		if(words.length>n)
			res+="...";
		return res.trim();
	}
	
	public static String truncateLetters(String str,int n){
		if(str==null || "".equals(str) || n==0)
			return "";
		String res = n>str.length()?str:str.substring(0, n-1);		
		if(str.length()>n)
			res+="...";
		return res.trim();
	}
	
	public static String getDateString(Date date,String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
}

package com.gae.piarchivercloud.dao;

public class DAOFactory {
	public static CategoriesDAO getCategoriesDAO()
	{
		return new CategoriesDAO();
	}
	public static LinksDAO getLinksDAO()
	{
		return new LinksDAO();
	}
	public static UserDAO getUserDAO()
	{
		return new  UserDAO();
	}
}

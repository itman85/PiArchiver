package com.gae.piarchivercloud;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gae.piarchivercloud.bean.UserBean;
import com.gae.piarchivercloud.dao.DAOFactory;
import com.gae.piarchivercloud.dao.UserDAO;




public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public LoginServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try
		{	    

		     UserBean user = new UserBean();
		     user.setUserName(request.getParameter("un"));
		     user.setPassword(request.getParameter("pw"));
		     user.setFirstName("admin");
		     user.setLastName("abc");
		     user = DAOFactory.getUserDAO().login(user);
			   		    
		     if (user.isValid())
		     {
			        
		          HttpSession session = request.getSession(true);	    
		          session.setAttribute("currentSessionUser",user); 
		          response.sendRedirect("webcontent/jsp/mainPage.jsp"); //logged-in page      		
		     }
			        
		     else 
		          response.sendRedirect("webcontent/jsp/invalidLogin.jsp"); //error page 
		} 
				
				
		catch (Throwable theException) 	    
		{
		     System.out.println(theException); 
		}
	}

}


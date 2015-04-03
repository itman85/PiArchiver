<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gae.piarchivercloud.bean.UserBean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html public "-//W3C//DTD HTML 4.0 //EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<title>   User Logged Successfully   </title>
</head>
<body>
  <center>
            <% UserBean currentUser = ((UserBean) (session.getAttribute("currentSessionUser")));
            pageContext.setAttribute("UserObj", currentUser);%>
			 <c:choose>
	  <c:when test="${!empty UserObj}">
	   Welcome <%= currentUser.getFirstName() + " " + currentUser.getLastName()%>
            <p><a href="logout.jsp">Logout</a></p>
	   </c:when>

	  <c:otherwise>
	   You are not login!
	  </c:otherwise>
	</c:choose>
           
         </center>

</body>
</html>
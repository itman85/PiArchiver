<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gae.piarchivercloud.bean.UserBean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html public "-//W3C//DTD HTML 4.0 //EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PiArchiverCloud Login Page</title>

<link rel="stylesheet" href="webcontent/css/login-form.css" media="screen">
<style>body{background:url(webcontent/img/bg.png) center;margin: 0 auto;width: 960px;padding-top: 50px}.footer{margin-top:50px;text-align:center;color:#666;font:bold 14px Arial}.footer a{color:#999;text-decoration:none}.login-form{margin: 50px auto;}</style>
<script src="webcontent/js/jquery.min.js"></script>
<script src="webcontent/js/login-form.js"></script>
</head>
<body>
  <% UserBean currentUser = ((UserBean) (session.getAttribute("currentSessionUser")));
            pageContext.setAttribute("UserObj", currentUser);%>
			 <c:choose>
	  <c:when test="${!empty UserObj}">
	  <c:redirect url="webcontent/jsp/mainPage.jsp"/>
	   </c:when>

	  <c:otherwise>
	   <div class="login-form">

			<h1>Login Form</h1>
		
			<form action="/login" method="post">
		
				<input type="text" name="un" placeholder="username">
				
				<input type="password" name="pw" placeholder="password">
				
				<span>
					<input type="checkbox" name="checkbox">
					<label for="checkbox">remember</label>
				</span>
				
				<input type="submit" value="log in">
		
			</form>
	
	</div>
	  </c:otherwise>
	</c:choose>


</body>
</html>

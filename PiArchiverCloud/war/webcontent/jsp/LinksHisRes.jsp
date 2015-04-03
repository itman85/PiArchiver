<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gae.piarchivercloud.pojo.LinkPojo" %>
<%@ page import="com.gae.piarchivercloud.util.UtilFn" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
List<LinkPojo> linksResults =  (List<LinkPojo>)request.getAttribute("LinksHisResults");
%>
<table id="linkstable" >
						<tr>
							<th>No</th>
							<th>Link</th>
							<th>Note</th>
							<th>Category</th>							
							<th>Date</th>
							<th></th>
						</tr>						
						<c:forEach items="${LinksHisResults}" var="linkItem" varStatus="counter">  
						<%
							LinkPojo linkObj = (LinkPojo)pageContext.getAttribute("linkItem");						    
						    //pageContext.setAttribute("key", KeyFactory.keyToString(linkObj.getKey()),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("link", linkObj.getUrl(),PageContext.PAGE_SCOPE);						   
						    pageContext.setAttribute("note", linkObj.getNote(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("category", linkObj.getCategoryName(),PageContext.PAGE_SCOPE);						  
						    pageContext.setAttribute("link_short", linkObj.getShort_url(),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("note_short", linkObj.getShort_note(),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("category_short",linkObj.getShort_CategoryName(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("date", linkObj.getCreatedOn(),PageContext.PAGE_SCOPE);
						  %>
						<tr>
							<td><c:out value="${counter.count}"/></td>
							<td><c:out value="${link_short}"/></td>
							<td><c:out value="${note_short}"/></td>
							<td><c:out value="${category_short}"/></td>							
							<td><c:out value="${date}"/></td>
							<td><div class="arrow"></div></td>
						</tr>
						<tr>
							<td colspan="6">								
								<ul><c:out value="${link}"/></ul>								
								<h4 >Link note</h4>
								<ul>															
								<c:out value="${note}"/>								
								</ul>								
								<h4 >Category</h4>
								<ul>													
								<c:out value="${category}"/>								
								</ul>								
							</td>
						</tr>
						</c:forEach> 
						<tr>
							<td colspan="6">
							&nbsp
							</td>
						</tr>
</table>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.gae.piarchivercloud.bean.UserBean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html public "-//W3C//DTD HTML 4.0 //EN">
<html xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<title>PiArchiverCloud</title>
<link href="../css/tab.css" type="text/css" rel="stylesheet" />
<link href="../css/formstyle.css" type="text/css" rel="stylesheet" media="screen" />
<link href="../css/jquery.ui.lib.css" type="text/css" rel="stylesheet" media="screen">
<link href="../css/jquery.ui.theme.css" type="text/css" rel="stylesheet" media="screen">
<link href="../css/ui.dynatree.css" rel="stylesheet" type="text/css">
<link href="../css/loading.css" type="text/css" rel="stylesheet" media="screen" />
<link href="../css/table.css" type="text/css" rel="stylesheet" media="screen" />
<link href="../css/pagination.css" type="text/css" rel="stylesheet" media="screen" />
<link href="../css/reveal.css" type="text/css" rel="stylesheet"/>

<script src="../js/jquery.min.js"></script> 
<script src="../js/jquery.ui.core.js"></script> 
<script src="../js/jquery.ui.widget.js"></script> 
<script src="../js/jquery.ui.datepicker.js"></script> 
<script src="../js/jquery-ui.custom.min.js" type="text/javascript"></script>
<script src="../js/jquery.cookie.js" type="text/javascript"></script>
<script src="../js/jquery.dynatree.js" type="text/javascript"></script>
<script src="../js/jquery.pagination.js" type="text/javascript"></script>
<script src="../js/pagination.js"></script> 
<script src="../js/mainprocess.js"></script> 
<script type="text/javascript" src="../js/jquery.clearsearch.js"></script>


 <style type="text/css">
	 body { font-family:Arial, Helvetica, Sans-Serif; font-size:0.8em;background:#DBDBDB repeat;}
</style>
<script type="text/javascript">

$(document).ready(function() { 

	$("#first-tab").addClass('buttonHover');
});

function navigate_tabs(container, tab){	
	$(".b").css('display' , 'none');
	$(".a").css('display' , 'none');
	$(".c").css('display' , 'none');
	
	$("#first-tab").removeClass('buttonHover');
	$("#second-tab").removeClass('buttonHover');
	$("#third-tab").removeClass('buttonHover');	
	
	$("#"+tab).addClass('buttonHover');
	$("."+container).show(300);
}
</script>

</head>
<body>
<div class="modal"></div>
	 <%
	  // The JSP EL doesn't manipulate local variables of the JSP. It manipulates objects stored in one of the four scopes:
	//	pageScope
	//	requestScope
	//	sessionScope
	//	applicationScope 
	//	that why we need to keep currentUser in pageContext pageContext.setAttribute("UserObj", currentUser); and use it in JSP EL <c:when test="${!empty UserObj}">
	
	 UserBean currentUser = ((UserBean) (session.getAttribute("currentSessionUser")));
		 pageContext.setAttribute("UserObj", currentUser);%>
	<div align="center">
	
	  <c:choose>	 
	   <c:when test="${!empty UserObj}">
	   	<div id="wrap" >		
			<div style="background-color:#ffffff;">
				<a href="javascript:navigate_tabs('a','first-tab');" class="buttons" id="first-tab">History</a>			
				<a href="javascript:navigate_tabs('b','second-tab');" class="buttons" id="second-tab">Category</a>
				<a href="javascript:navigate_tabs('c','third-tab');" class="buttons" id="third-tab">Add link</a>
				<p style="position: absolute;right:60px;top:10px">Welcome <%= currentUser.getUsername() %>!</p>
				
			 	<a href="logout.jsp" style="position: absolute;right:10px;top:10px">Logout</a>	
				<br clear="all" />
			</div>	
			<div id="body" >			
				<div class="a">				
				<div class="box" >
				<div id="PaginationId" class="pagination"></div>
				<div id ="restable_div" style="display:none;"></div>				
				<h2 align="center">Bookmarks History</h2>				
                <form id="ui_element" class="sb_wrapper">
                <div id="sortgroup" style="position:absolute; top:100px; left:50px;">
                <input type="radio" name="status" value="date" checked/> Sort by date
				<input type="radio" name="status" value="category" />Sort by category
                </div>
				<table  cellspacing="50">
					<tr>
						<td>
						<p>Start date(0): <input type="text" class ="datetime" id="datepicker1"/></p> 
						</td>
						<td >
						<p>End date(1): <input type="text" class ="datetime" id="datepicker2"/></p> 
						</td>
						<td >
						<p>Select category:						
						<input type="text" class ="category clearable" id="txtcategory" data-reveal-id="categoryModal" data-animation="none" data-type-control="1" readonly/></p>												 
						<input type="hidden" id="_categorykey" value="">
						</td>						
						<td>
						<p><br/>						 
						<input class="button medium white" type="button" value="Show" id = "btnShowHistory" /> </p>
						</td>	
					</tr>
					
				</table>				
                </form>
				
				</div>	
				<div id="categoryModal" class="reveal-modal">
					<h2>Select category</h2>					
					<a class="close-reveal-modal">&#215;</a>
					<fieldset class="modal-border" >
					<div id="treemodal"> </div>
					</fieldset>
					<br/>
					<button class="button medium white"id="btnCategoryDone">&nbsp;Done&nbsp;</button>
					<button class="button medium white" id="btnCategoryCancel">Cancel</button>
					<button class="button medium white" id="btnCategoryReload">Reload</button>
				</div>			
				</div>
				
				<div class="b">	
				<div class="box">
				<h2 align="center">Category management</h2>	
				<div class="sb_wrapper1">				
				<table  >
					<tr>
						<td>
						<fieldset class="group">
						<legend class="grouptitle">Tree control</legend>				
						<button class="button medium white" id="btnExpand">Expand</button>
						<button  class="button medium white" id="btnCollapse">Collapse</button>
						<button class="button medium white" id="btnSortTree">Sort Tree</button>
						<button class="button medium white" id="btnSortCurrentNode">Sort Current Node</button>
						</fieldset>
						</td>
						<td >
						<fieldset class="group">
						<legend class="grouptitle">Tree management</legend>	
						<button class="button medium white"id="btnAdd">Add node</button>
						<button class="button medium white" id="btnAddChild">Add child node</button>
						<button class="button medium white" id="btnEdit">Edit node</button>  
						<button class="button medium gray" id="btnApply">Apply</button> 
						</fieldset>
						</td>						
					</tr>
					<tr>
					<td>
					<div id="tree"> </div>
					</td>					
					</tr>
				</table>				
				</div>	
				</div>			
				</div>					
				
				<div class="c" >
				<div class="box" >
				<h2 align="center">Add Links</h2>
				<div class="wrapper">
				<button class="button medium red"id="btnReloadTree">Reload categories</button>
					<form class="txtinput" action="/addlink" method="post" id="addlink_form">
						<fieldset class="group">
							<legend>Input a new link</legend>
								<ol class="clearfix">
								<li class="midle">
									<label class="titleform" for="content">link:</label>
									<textarea cols="32" rows="2" name="linkurl" id="linkurl"></textarea>
								</li>	
								<li class="midle">
									<label class="titleform" for="content">Note:</label>
									<textarea cols="32" rows="3" name="linknote" id="linknote"></textarea>
								</li>								
								<li class="midle" >
								<label class="titleform" for="content">Select category:</label>								
								<div align="left" id="tree1">								
								 </div>
								</li>
								<li class="last">
									<input class="txtsub" name="submit" id="submit" value="Submit" type="submit">
								</li>
							</ol>
						</fieldset>
					</form>
					<br />
					</div>
				</div>				
				</div>				
				
			</div>
		</div>	
			
	  </c:when>
	  <c:otherwise>
	   You are not login!
	   <br/>
	   <a href="\">Back to login page.</a>
	  </c:otherwise>
	</c:choose>		
	</div>
</body>
</html>
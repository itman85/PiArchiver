var LinkHistoryJsonArr = []; 
var c_items_per_page = 5;
function getDefaultOptions(){
	 var opt = {callback: pageselectCallback};
	 opt["items_per_page"] = c_items_per_page;
	 opt["num_display_entries"] = 5;
	 opt["num_edge_entries"] = 3;
	 opt["prev_text"] = "Newer";
	 opt["next_text"] = "Older";
	 return opt;
}

function initPaging(pageEleId,numOfitems){
	 var optInit = getDefaultOptions();
     $("#"+pageEleId).pagination(numOfitems, optInit);
}

function changeCategory(rowNo){
	var $CategoryIDhidden = $('#hiddenCategoryID'+rowNo);
	$("#treemodal").dynatree("getRoot").visit(function(node){
	        node.expand(false);
	});
	$("#treemodal").dynatree("getRoot").visit(function(node){
		if(node.data.key==$CategoryIDhidden.val()){//this is active node
			node.activate();
			node.focus();		    			
			return false;
		}
	});//end visit tree		
	//alert(rowNo);
	$('#categoryModal').reveal({animation:'none',typeControl:'2',rowNo:rowNo});
}

function spanDbClick(rowNo){	
	//alert(rowNo);
	var $span = $('#spanNote'+rowNo);		
	if($span.find("textarea").length > 0)//found textarea		
		return;		
		
	//alert ($span.text());
	var text = $span.text().trim();	
	
	$span.text('');
	$("<textarea>").attr('name','newname')
	.attr('rows','5')
	.attr('cols','50')
	.val(text).appendTo($span);
	
	$("<br>").appendTo($span);
	
	$("<input>").attr('type','button')
	.attr('class','button small gray')
	.attr('value','Save')
	.click(function () {
		updateNoteProcessing(rowNo);			
	}).appendTo($span);
	
	$("<input>").attr('type','button')
	.attr('class','button small gray')
	.attr('value','Cancel')
	.click(function () {
		CancelSubmitNewText('spanNote'+rowNo,text);			
	}).appendTo($span);	
	
	$span.children(':first').focus();
}

function CancelSubmitNewText(idSpan,srctext)
{		
	var $span = $('#'+idSpan);		
	$span.text(srctext);
}

function updateNoteProcessing(rowNo)
{		
	
	var $span = $('#spanNote'+rowNo);
	var $LinkIDhidden = $('#hiddenLinkID'+rowNo);
	var $CategoryIDhidden = $('#hiddenCategoryID'+rowNo);
	var $tD = $('#colNote'+rowNo);
		
	var spantxt = jQuery.trim($span.children(':first').val());
	
	var pos = spantxt.length;
	var pos1 = 0;
	var i = 0;
	var n = 5;//number of words to be displayed 
	for(;i<= n && pos1!=-1 ;i++)
	{	
		pos = pos1;
		pos1 = spantxt.indexOf(' ',pos1+1);
	}
	var shorttxt = spantxt;
	if(i>n)
		shorttxt = spantxt.substring(0,pos)+' ...';
	
	$tD.text(shorttxt);
	//alert($hidden.attr('value'));
	$span.text(spantxt);
	
	// send AJAX request update link			   
	$.ajax({
	     url :  "/updatelink",
	     type : "POST",
	     data : "linkid="+$LinkIDhidden.attr('value')+"&categoryid="+$CategoryIDhidden.attr('value')+"&linknote="+spantxt,
	     success : function(response) {	    	 
	    	 alert(response);	    	
	     },
	     error: function (request, status, error) {			  
		        alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
		    }
	   });//end ajax
}

function pageselectCallback(page_index, jq){
	$('#restable_div').html("");//reset result table
	var table = $("<table>").attr('id','linkstable');
	var hrow = $("<tr>").appendTo(table);
	$("<th>").text('No').appendTo(hrow);
    $("<th>").text('Link').appendTo(hrow);
    $("<th>").text('Note').appendTo(hrow);
    $("<th>").text('Category').appendTo(hrow);
    $("<th>").text('Date').appendTo(hrow);
    $("<th>").appendTo(hrow);
    
    var items_per_page = c_items_per_page;//default
    var max_elem = Math.min((page_index+1) * items_per_page, LinkHistoryJsonArr.length);
    var no;
    for(var i=page_index*items_per_page;i<max_elem;i++)
    {    	
    	var row1 = $("<tr>").appendTo(table);
    	no = i+1;
    	$("<td>").text(no).appendTo(row1);
    	$("<td>").text(LinkHistoryJsonArr[i].short_url).appendTo(row1);
    	$("<td>").attr('id','colNote'+no).text(LinkHistoryJsonArr[i].short_note).appendTo(row1);
    	$("<td>").attr('id','colCategory'+no).text(LinkHistoryJsonArr[i].short_categoryname).appendTo(row1);
    	$("<td>").text(LinkHistoryJsonArr[i].createdOn).appendTo(row1);
    	$("<div>").attr('class','arrow').appendTo($("<td>").appendTo(row1));
    	
    	var row2 = $("<tr>").appendTo(table);
    	var htmlcontent = "<ul>"+LinkHistoryJsonArr[i].url+"</ul>"
    					+"<input type=\"hidden\" value=\""+LinkHistoryJsonArr[i].linkID+"\" id=\"hiddenLinkID"+no+"\" />"
    					+"<input type=\"hidden\" value=\""+LinkHistoryJsonArr[i].categoryID+"\" id=\"hiddenCategoryID"+no+"\" />"
    					+"<span ondblclick=\"spanDbClick('"+no+"')\">"
    					+"<h4 >Link note</h4>"
    					+"<ul>"
    					+"<span id=\"spanNote"+no+"\" style=\"cursor:pointer\">"	
    					+LinkHistoryJsonArr[i].note
    					+"</span>"
    					+"</ul>"
    					+"</span>"
    					+"<h4 >Category&nbsp;&nbsp;&nbsp;<a href=\"javascript:changeCategory('"+no+"');\">" +
    							"<span style=\"color:#CC0000;font-weight:bold\">Change category?</span></a></h4>"
    					+"<ul>"
    					+"<span id=\"spanCategory"+no+"\">"
    					+LinkHistoryJsonArr[i].categoryname
    					+"</span>"
    					+"</ul>";
    	$("<td>").attr('colspan','6').html(htmlcontent).appendTo(row2);
    }
    $("<td>").attr('colspan','6').appendTo( $("<tr>").appendTo(table));//last row
    
    table.appendTo($("#restable_div"));			    	
	$("#linkstable tr:odd").addClass("odd");
	$("#linkstable tr:not(.odd)").hide();
	$("#linkstable tr:first-child").show();
				   
	$("#linkstable tr.odd").click(function(){
	 	$(this).next("tr").toggle();
		$(this).find(".arrow").toggleClass("up");
	});
	$('#restable_div').show();

    // Prevent click eventpropagation
    return false;
}
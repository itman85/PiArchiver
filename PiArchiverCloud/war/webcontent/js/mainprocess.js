var isloading = false;
var TreeChangeJson = [];/*[
{"name":"value","key":"value","newid":"value","parentkey":"value","parentnewid":"value"},
{}
]*/
var NodeNewId = 0;
function createLoader()
{
    if(!isloading)
    {
        isloading = true;
        $("body").addClass("loading");
    }
}

function removeLoader()
{
    if(isloading)
    {
        isloading = false;
        $("body").removeClass("loading");
    }
}

function editNode(node){
	  var prevTitle = node.data.title,
		tree = node.tree;
	 
	  // Disable dynatree mouse- and key handling
	  tree.$widget.unbind();
	  // Replace node with <input>
	  $(".dynatree-title", node.span).html("<input id='editNode' value='" + prevTitle + "'>");
	  // Focus <input> and bind keyboard handler
	  $("input#editNode")
		.focus()
		.keydown(function(event){
		  switch( event.which ) {
		  case 27: // [esc]
			// discard changes on [esc]
			$("input#editNode").val(prevTitle);
			$(this).blur();
			break;
		  case 13: // [enter]
			// simulate blur to accept new value
			  //$("#btnApply").removeAttr('disabled');
			  //alert(node.data.key);
			  //alert(node.data.newid);
			  var isFound = false;
			  $.each(TreeChangeJson, function(i, item) {
				  //alert(item["key"]);
				 // alert(item["newid"]);
				    if(item["key"]==node.data.key && item["newid"]==node.data.newid)
				    {
				    	//alert($("input#editNode").val());
				    	item["name"]=$("input#editNode").val();
				    	isFound = true;
				    	return false;
				    }
			});//end each
			if(!isFound)
			{				
				  TreeChangeJson.push({
			    	  name:$("input#editNode").val(),
			    	  key:node.data.key,
			    	  newid:node.data.newid,
			    	  parentkey:$("#tree").dynatree("getRoot")===node.getParent()?0:node.getParent().data.key,              
			    	  parentnewid:$("#tree").dynatree("getRoot")===node.getParent()?0:node.getParent().data.newid});
			}
			prevTitle = $("input#editNode").val();
			$(this).blur();
			break;
		  }
		}).blur(function(event){			
			node.setTitle(prevTitle);
		  // Re-enable mouse and keyboard handlling
		  tree.$widget.bind();
		  node.focus();
		});
}//end function

jQuery.fn.clearable = function() {	 
	  
	  return this.each(function() {		
	    	
		$(this).css({'border-width': '0px', 'outline': 'none'})
			.wrap('<div id="sq" class="divclearable"></div>')
			.parent()
			.attr('class', $(this).attr('class') + ' divclearable')
			.append('<a class="clearlink" href="javascript:"></a>');

		$('.clearlink')
			.attr('title', 'Click to clear this textbox')
			.click(function() {
				
				//$(this).prev().val('').focus();
				alert('123');

		});
	  });
}//end function


//start function	
$(function() {
	//reset every time reload
	LinkHistoryJsonArr = []; 
	//init categories for trees
	$.getJSON( "/getcategories", function( data ) {			
		$("#tree").dynatree({
		      onActivate: function(node) {
		        // A DynaTreeNode object is passed to the activation handler
		        // Note: we also get this event, if persistence is on, and the page is reloaded.
		       // alert("You activated " + node.data.key);
		      },
		      children:data,
		     /* initAjax: {url: "/getcategories1",
		    	  data: { mode: "funnyMode" }                     
	              },*/
		      /*children:[
		        {title: "Item 1",key: "0001"},
		        {title: "Folder 2", key: "002",
		          children: [
		            {title: "Sub-item 2.1", key: "003"},
		            {title: "Sub-item 2.2", key: "004"}
		          ]
		        },
		        {title: "Item 3", key: "005"}
		      ],*/
			  /*onClick: function(node, event) {
				  if( event.shiftKey ){
					editNode(node);
					return false;
				  }
			  },
			  onDblClick: function(node, event) {
				editNode(node);
				return false;
			  },
		      onKeydown: function(node, event) {
				  switch( event.which ) {
				  case 113: // [F2]
					editNode(node);
					return false;
				  case 13: // [enter]
					if( isMac ){
					  editNode(node);
					  return false;
					}
				  }
			  },*/
			  dnd: {
			      onDragStart: function(node) {
			        /** This function MUST be defined to enable dragging for the tree.
			         *  Return false to cancel dragging of node.
			         */
			        logMsg("tree.onDragStart(%o)", node);
			        return true;
			      },
			      onDragStop: function(node) {
			        // This function is optional.
			        logMsg("tree.onDragStop(%o)", node);
			      },
			      autoExpandMS: 1000,
			      preventVoidMoves: true, // Prevent dropping nodes 'before self', etc.
			      onDragEnter: function(node, sourceNode) {
			        /** sourceNode may be null for non-dynatree droppables.
			         *  Return false to disallow dropping on node. In this case
			         *  onDragOver and onDragLeave are not called.
			         *  Return 'over', 'before, or 'after' to force a hitMode.
			         *  Return ['before', 'after'] to restrict available hitModes.
			         *  Any other return value will calc the hitMode from the cursor position.
			         */
			        logMsg("tree.onDragEnter(%o, %o)", node, sourceNode);
			        return true;
			      },
			      onDragOver: function(node, sourceNode, hitMode) {
			        /** Return false to disallow dropping this node.
			         *
			         */
			        logMsg("tree.onDragOver(%o, %o, %o)", node, sourceNode, hitMode);
			        // Prevent dropping a parent below it's own child
			        if(node.isDescendantOf(sourceNode)){
			          return false;
			        }
			        // Prohibit creating childs in non-folders (only sorting allowed)
			        if( !node.data.isFolder && hitMode === "over" ){
			          return "after";
			        }
			      },
			      onDrop: function(node, sourceNode, hitMode, ui, draggable) {
			        /** This function MUST be defined to enable dropping of items on
			         * the tree.
			         */
			        logMsg("tree.onDrop(%o, %o, %s)", node, sourceNode, hitMode);		        
			        sourceNode.move(node, hitMode);
			        var isFound = false;
					$.each(TreeChangeJson, function(i, item) {
						 // alert(item["key"]);
						 // alert(item["newid"]);
						    if(item["key"]==sourceNode.data.key && item["newid"]==sourceNode.data.newid)
						    {
						    	
						    	item["parentkey"]=$("#tree").dynatree("getRoot")===sourceNode.getParent()?0:sourceNode.getParent().data.key;
						    	item["parentnewid"]=$("#tree").dynatree("getRoot")===sourceNode.getParent()?0:sourceNode.getParent().data.newid;
						    	isFound = true;
						    	return false;
						    }
					});//end each
					if(!isFound)
					{				
						 TreeChangeJson.push({
					    	  name:sourceNode.data.title,
					    	  key:sourceNode.data.key,
					    	  newid:sourceNode.data.newid,
					    	  parentkey:$("#tree").dynatree("getRoot")===sourceNode.getParent()?0:sourceNode.getParent().data.key,              
							  parentnewid:$("#tree").dynatree("getRoot")===sourceNode.getParent()?0:sourceNode.getParent().data.newid});
					}		       
				   // $("#btnApply").removeAttr('disabled');
			        // expand the drop target
		//	        sourceNode.expand(true);
			      },
			      onDragLeave: function(node, sourceNode) {
			        /** Always called if onDragEnter was called.
			         */
			        logMsg("tree.onDragLeave(%o, %o)", node, sourceNode);
			      }	    
			  }
		    });//end tree		
			 $("#tree1").dynatree({
			      onActivate: function(node) {
			        // A DynaTreeNode object is passed to the activation handler
			        // Note: we also get this event, if persistence is on, and the page is reloaded.
			       // alert("You activated " + node.data.key);
			      },
			      children:data
			     });//end tree	
			 $("#treemodal").dynatree({
			      onActivate: function(node) {
			        // A DynaTreeNode object is passed to the activation handler
			        // Note: we also get this event, if persistence is on, and the page is reloaded.
			       // alert("You activated " + node.data.key);
			      },
			      children:data
			     });//end tree	
		});
	
	//init date picker
	$( "#datepicker1" ).datepicker({ dateFormat: 'yy/mm/dd' });
	$( "#datepicker2" ).datepicker({ dateFormat: 'yy/mm/dd' });
	//disable button apply
	//$("#btnApply").attr('disabled','disabled');
	
	$("#btnEdit").click(function(){
	      var node = $("#tree").dynatree("getActiveNode");
	      if( !node ) return;
		  editNode(node);		  
	 });
	$("#btnExpand").click(function(){
	      $("#tree").dynatree("getRoot").visit(function(node){
	        node.expand(true);
	 		});
	      return false;
	 });
	$("#btnCollapse").click(function(){
	       $("#tree").dynatree("getRoot").visit(function(node){
	        node.expand(false);
	      });
	      return false;
	 });
	$("#btnSortTree").click(function(){
	      var node = $("#tree").dynatree("getRoot");
	      node.sortChildren(null, true);
	 });
	$("#btnSortCurrentNode").click(function(){
	      var node = $("#tree").dynatree("getActiveNode");	 
			if(node.getSort()===0)
				node.setSort(1);//asc
			else if(node.getSort()===1)
				node.setSort(-1);//des
			else
				node.setSort(1);//asc
			var sort = node.getSort();		
	      // Custom compare function (optional) that sorts case insensitive
	      var cmp = function(a, b) {
	        a = a.data.title.toLowerCase();
	        b = b.data.title.toLowerCase();
	        return a > b ? sort : a < b ? 0-sort : 0;
	      };
	      node.sortChildren(cmp, false);
	   });
	$("#btnAdd").click(function(){
	      // Sample: add an hierarchic branch using code.
	      // This is how we would add tree nodes programatically
		var result = confirm("Want to add new category?");
		if (result==true) {
			  NodeNewId++;
		      var rootNode = $("#tree").dynatree("getRoot");
		      var childNode = rootNode.addChild({
		        title: "New category",  
		        key:0,
		        isFolder: false,
		        newid:NodeNewId
		      }); 
		      TreeChangeJson.push({
		    	  name:"New category",
		    	  key:0,
		    	  newid:NodeNewId,
		    	  parentkey:0,              
		    	  parentnewid:0});
		      //$("#btnApply").removeAttr('disabled');
		      childNode.activate();
		}
	 });
	$("#btnAddChild").click(function(){
	      // Sample: add an hierarchic branch using code.
	      // This is how we would add tree nodes programatically
		var result = confirm("Want to add new child category?");
		if (result==true) {
		    //Logic to delete the item
			 NodeNewId++;
		      var parentNode = $("#tree").dynatree("getActiveNode");
		      var childNode = parentNode.addChild({
		        title: "New category",   
		        key:0,
		        isFolder: false,
		        newid:NodeNewId
		      });  
		      TreeChangeJson.push({
		    	  name:"New category",
		    	  key:0,
		    	  newid:NodeNewId,
		    	  parentkey:parentNode.data.key,              
		    	  parentnewid:parentNode.data.newid});
		     // $("#btnApply").removeAttr('disabled');
		      parentNode.expand(true);
		      childNode.activate();
		}
		
	 });
	
	$("#btnApply").click(function(){
		//alert('123');
		if(TreeChangeJson.length>0)
		{
			createLoader();
			var jsonText=JSON.stringify(TreeChangeJson);
	        //alert(jsonText);
	        $.ajax({
	            type: "POST",
	            url: "/submitcategories",
	            data: jsonText,
	            success : function(response) {			    	 
	            	removeLoader();	        
	            	TreeChangeJson=[];//reset tree change json	            	
	            	//console.log(response);	            	
			    	alert(response.message);
			    	$("#tree").dynatree("getRoot").visit(function(node){
			    		if(node.data.key==0 && node.data.newid!=0){//this is new node
				    		$.each(response.resultlist, function(i, item) {							
								    if(item["newid"]==node.data.newid)
								    {
								    	node.data.key =  item["key"];	
								    	node.data.newid = 0;
								    	return false;
								    }
							});//end each
			    		}
			   	    });//end visit tree
			     },
			     error: function (request, status, error) {
			    	 removeLoader();	
			    	 //TreeChangeJson=[];//reset tree change json	 
			    	 alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
				},
	            contentType: "application/json",
	            dataType: 'json'
	        });
		}else
		{
			alert("No changing to apply!");
		}
		 
		//$("#btnApply").attr('disabled','disabled');
	 });
	$("#btnReloadTree").click(function(){
	    // alert('hrhr');	 
	     $("#tree1").dynatree("getRoot").appendAjax({
	          url: "/getcategories",
	          // We don't want the next line in production code:
	          debugLazyDelay: 500
	        });	     
	 });
	var isMac = /Mac/.test(navigator.platform);
	    // Attach the dynatree widget to an existing <div id="tree"> element
	    // and pass the tree options as an argument to the dynatree() function:
	
	
		 
	    $('#addlink_form').submit(function(e) {	    	
			   e.preventDefault();		
			   //should have a message confirm select category		
			  // alert($(this).serialize()+"&categoryid="+$("#tree1").dynatree("getActiveNode").data.key);
			   if($( "#linkurl" ).val() && $( "#linknote" ).val()&& $("#tree1").dynatree("getActiveNode")!=null){
				   createLoader();
				   // a sample AJAX request			   
				   $.ajax({
				     url : this.action,
				     type : this.method,
				     data : $(this).serialize()+"&categoryid="+$("#tree1").dynatree("getActiveNode").data.key,
				     success : function(response) {			    	 
				    	 removeLoader();
				    	 alert(response);
				    	 $( "#linkurl" ).val("");
				    	 $( "#linknote" ).val("");
				    	 $("#tree1").dynatree("getActiveNode").deactivate();
				     },
				     error: function (request, status, error) {
						   removeLoader();
					        alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
					    }
				   });//end ajax
			   }else{
				alert("Please input link and note and choose category.");   
			   }		   
			   
			});//end submit form	
	    
	    $('#btnShowHistory').click(function() {
			if($( "#datepicker1" ).val() && $( "#datepicker2" ).val()){
				//alert ($( "#datepicker1" ).val());
				 createLoader();
				 //reset 
				LinkHistoryJsonArr=[];
				var startdate = $( "#datepicker1" ).val().replace(/\//ig,"-");
				var enddate = $( "#datepicker2" ).val().replace(/\//ig,"-");
				var sort='date';
				$("#sortgroup").find('input:radio').each(function(){ 
					if($(this).attr('checked')){ 
						sort = $(this).attr('value');
						//alert(sort);
					}
				});
				$('#ui_element1').find('input:password').val('');
				   $.ajax({
					     url : "/getlinkshistory",
					     type : "POST",
					     data :  "startdate="+encodeURI(startdate)+"&enddate="+ encodeURI(enddate)+"&categoryid="+encodeURI($("#_categorykey").val())+"&sort="+sort,
					     success : function(response) {			    	 
					    	 removeLoader();
					    	 if(response.linksarr.length>0){
						    	 LinkHistoryJsonArr= response.linksarr;
						    	 //alert(JSON.stringify(LinkHistoryJsonArr));
						    	 initPaging('PaginationId',LinkHistoryJsonArr.length);
					    	 }else{
					    		 alert (response.message);
					    	 }
					     },
					     error: function (request, status, error) {
							   removeLoader();
						        alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
						    }
					   });//end ajax
			}
			else{
				alert ("please input start date and end date");
			}
			});
	    
	    $('#txtcategory').click(function() {
	    	
	    	$("#treemodal").dynatree("getRoot").visit(function(node){
 	        node.expand(false);
 	        });
	    	if($("#_categorykey").val()!=null && $("#_categorykey").val()!=""){
		    	$("#treemodal").dynatree("getRoot").visit(function(node){
		    		if(node.data.key==$("#_categorykey").val()){//this is active node
		    			node.activate();
		    			node.focus();		    			
		    			return false;
		    		}
		   	    });//end visit tree
	    	}
			var modalLocation = $(this).attr('data-reveal-id');
			$('#'+modalLocation).reveal($(this).data());
	    });
	    $('.clearable').clearSearch({ callback: function() { 
		$("#_categorykey").val(''); } } );
	   
	    
	    
	    /*******************Modal Dialog Start*************************************/
	    $('#btnCategoryCancel').click(function(e) {
	    	//alert($('#categoryModal').attr('typeControl'));
			//alert($('#categoryModal').attr('rowNo'));
	    	$('#categoryModal').trigger('reveal:close');
		});
	    $('#btnCategoryReload').click(function(e) {
	    	  $("#treemodal").dynatree("getRoot").appendAjax({
		          url: "/getcategories",
		          // We don't want the next line in production code:
		          debugLazyDelay: 500
		        });	     
		});
		$('#btnCategoryDone').click(function(e) {
			
			if($('#categoryModal').attr('typeControl')==1){//category textbox open
				$("#txtcategory").val($("#treemodal").dynatree("getActiveNode").data.title);
				$("#_categorykey").val($("#treemodal").dynatree("getActiveNode").data.key);
				$("#txtcategory").focus();
			}else if($('#categoryModal').attr('typeControl') ==2){//change category
				var rowNo =$('#categoryModal').attr('rowNo');
				//alert(rowNo);
				var $span = $('#spanCategory'+rowNo);
				var $LinkIDhidden = $('#hiddenLinkID'+rowNo);
				var $CategoryIDhidden = $('#hiddenCategoryID'+rowNo);
				var $tD = $('#colCategory'+rowNo);
				
				var spantxt = jQuery.trim($("#treemodal").dynatree("getActiveNode").data.title);
				
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
				//update text
				$tD.text(shorttxt);					
				$span.text(spantxt);
				//update value for hidden category
				$CategoryIDhidden.val($("#treemodal").dynatree("getActiveNode").data.key);
				// send AJAX request update link			   
				$.ajax({
				     url :  "/updatelink",
				     type : "POST",
				     data : "linkid="+$LinkIDhidden.val()+"&categoryid="+$CategoryIDhidden.val()+"&linknote="+$('#spanNote'+rowNo).text(),
				     success : function(response) {
				    	 alert(response);				    		    	
				     },
				     error: function (request, status, error) {			  
					        alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
					    }
				   });//end ajax
			}
			$('#categoryModal').trigger('reveal:close');
		});
/*---------------------------
 Extend and Execute
----------------------------*/

    $.fn.reveal = function(options) {    
    	//data-reveal-id=>options.revaealId
        //alert(options.revealId);
    	var typeControl = options.typeControl;
    	var rowNo = options.rowNo;
    	$('#categoryModal').attr('typeControl',typeControl);
    	$('#categoryModal').attr('rowNo',rowNo);
        var defaults = {  
	    	animation: 'fadeAndPop', //fade, fadeAndPop, none
		    animationspeed: 300, //how fast animtions are
		    closeonbackgroundclick: true, //if you click background will modal close?
		    dismissmodalclass: 'close-reveal-modal' //the class of a button or element that will close an open modal
    	}; 
    	
        //Extend dem' options
        var options = $.extend({}, defaults, options); 
	
       
		
        return this.each(function() {
        
/*---------------------------
 Global Variables
----------------------------*/
        	var modal = $(this),
        		topMeasure  = parseInt(modal.css('top')),
				topOffset = modal.height() + topMeasure,
          		locked = false,
				modalBG = $('.reveal-modal-bg');

/*---------------------------
 Create Modal BG
----------------------------*/
			if(modalBG.length == 0) {
				modalBG = $('<div class="reveal-modal-bg" />').insertAfter(modal);
			}		    
     
/*---------------------------
 Open & Close Animations
----------------------------*/
			//Entrance Animations
			modal.bind('reveal:open', function () {
			  modalBG.unbind('click.modalEvent');
				$('.' + options.dismissmodalclass).unbind('click.modalEvent');
				if(!locked) {
					lockModal();
					if(options.animation == "fadeAndPop") {
						modal.css({'top': $(document).scrollTop()-topOffset, 'opacity' : 0, 'visibility' : 'visible'});
						modalBG.fadeIn(options.animationspeed/2);
						modal.delay(options.animationspeed/2).animate({
							"top": $(document).scrollTop()+topMeasure + 'px',
							"opacity" : 1
						}, options.animationspeed,unlockModal());					
					}
					if(options.animation == "fade") {
						modal.css({'opacity' : 0, 'visibility' : 'visible', 'top': $(document).scrollTop()+topMeasure});
						modalBG.fadeIn(options.animationspeed/2);
						modal.delay(options.animationspeed/2).animate({
							"opacity" : 1
						}, options.animationspeed,unlockModal());					
					} 
					if(options.animation == "none") {
						modal.css({'visibility' : 'visible', 'top':$(document).scrollTop()+topMeasure});
						modalBG.css({"display":"block"});	
						unlockModal()				
					}
				}
				modal.unbind('reveal:open');
			}); 	

			//Closing Animation
			modal.bind('reveal:close', function () {
			  if(!locked) {
					lockModal();
					if(options.animation == "fadeAndPop") {
						modalBG.delay(options.animationspeed).fadeOut(options.animationspeed);
						modal.animate({
							"top":  $(document).scrollTop()-topOffset + 'px',
							"opacity" : 0
						}, options.animationspeed/2, function() {
							modal.css({'top':topMeasure, 'opacity' : 1, 'visibility' : 'hidden'});
							unlockModal();
						});					
					}  	
					if(options.animation == "fade") {
						modalBG.delay(options.animationspeed).fadeOut(options.animationspeed);
						modal.animate({
							"opacity" : 0
						}, options.animationspeed, function() {
							modal.css({'opacity' : 1, 'visibility' : 'hidden', 'top' : topMeasure});
							unlockModal();
						});					
					}  	
					if(options.animation == "none") {
						modal.css({'visibility' : 'hidden', 'top' : topMeasure});
						modalBG.css({'display' : 'none'});	
					}		
				}
				modal.unbind('reveal:close');
			});     
   	
/*---------------------------
 Open and add Closing Listeners
----------------------------*/
        	//Open Modal Immediately
    	modal.trigger('reveal:open')
			
			//Close Modal Listeners
			var closeButton = $('.' + options.dismissmodalclass).bind('click.modalEvent', function () {
			  modal.trigger('reveal:close');			  
			});
			
			if(options.closeonbackgroundclick) {
				modalBG.css({"cursor":"pointer"})
				modalBG.bind('click.modalEvent', function () {
				  modal.trigger('reveal:close')
				});
			}
			$('body').keyup(function(e) {
        		if(e.which===27){ modal.trigger('reveal:close'); } // 27 is the keycode for the Escape key
			});			
			
/*---------------------------
 Animations Locks
----------------------------*/
			function unlockModal() { 
				locked = false;
			}
			function lockModal() {
				locked = true;
			}			
			
        });//each call
    }//orbit plugin call
    /*******************Modal Dialog End*************************************/
});//end function


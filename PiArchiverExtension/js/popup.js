// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
var isloading = false;
var cloudUrl = "http://localhost:8888";
var localUrl = "http://localhost:8080";
function click(e) {
    chrome.tabs.executeScript(null,
        {code:"document.body.style.backgroundColor='" + e.target.id + "'"});
    //window.close();
}

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

document.addEventListener('DOMContentLoaded', function () {
    var divs = document.querySelectorAll('div');
    for (var i = 0; i < divs.length; i++) {
        divs[i].addEventListener('click', click);
    }
});

//fire this function whenever popup is shown
$(document).ready(function() {
    //alert(chrome.extension.getBackgroundPage().getSelectedTabTitle()) ;
    //set title of page
    $('#link_title').val(chrome.extension.getBackgroundPage().getSelectedTabTitle());

    //init tree of categories
    var categoryJson = [];
    if(localStorage.piArchiverCategory){
        categoryJson = $.parseJSON( localStorage.piArchiverCategory );
    }
    $("#tree").dynatree({
        onActivate: function(node) {
        },
        children: categoryJson});//end tree

    $('#btnSyncCategory').click(function() {
        $("#tree").dynatree("getRoot").appendAjax({
            url: cloudUrl+"/getcategories",
            debugLazyDelay: 500,
            success:function(data,textStatus) {
                //alert(JSON.stringify(data));
                //alert(textStatus);
                var jsoncate = JSON.stringify(data);
                localStorage.setItem('piArchiverCategory', jsoncate);
                $.post( localUrl+"/archivecategory",jsoncate);
            },
            error: function (request, status, error) {
                alert("-responseText: "+request.responseText + "\n-Status: " +status+"\n-Error: "+error );
            }
        });
    });

    $('#btnSyncLink').click(function() {
        createLoader();
        var linkCloudSequence = 0;
        var updateLinkCloudSequence = 0;
        if(localStorage.piArchiverLinkSequence){
            linkCloudSequence =  parseInt(localStorage.piArchiverLinkSequence, 0);
        }
        if(localStorage.piArchiverUpdateLinkSequence){
            updateLinkCloudSequence =  parseInt(localStorage.piArchiverUpdateLinkSequence, 0);
        }
        $.ajax({
            url :  cloudUrl+"/getlinksextension",
            type : "POST",
            data : "linksequence="+linkCloudSequence+"&updatelinksequence="+updateLinkCloudSequence,
            success : function(response) {
                removeLoader();
                var newlinkslist = [];
                var updatelinkslist = [];
                if(response.resultnewlist!=null && response.resultnewlist.length>0)  {
                    //alert(response.resultnewlist.length);
                    chrome.extension.getBackgroundPage().indexedDBObject.addLinks(response.resultnewlist);
                    localStorage.setItem('piArchiverLinkSequence', response.currentcloudid);
                    localStorage.setItem('piArchiverUpdateLinkSequence', response.currentupdatecloudid);
                    chrome.extension.getBackgroundPage().changePageActionIconToBookmarkedIfLinkExisted();
                    newlinkslist = response.resultnewlist;

                }
                if(response.resultupdatelist!=null && response.resultupdatelist.length>0)  {
                    //send update links to local server
                    localStorage.setItem('piArchiverUpdateLinkSequence', response.currentupdatecloudid);
                    updatelinkslist = response.resultupdatelist;
                }
                //send new links to local server
                if(newlinkslist.length>0 || updatelinkslist.length>0)
                    $.post( localUrl+"/archive",JSON.stringify({ 'resultnewlist': newlinkslist, 'resultupdatelist': updatelinkslist }));
                alert(response.message);
            },
            error: function (request, status, error) {
                removeLoader();
                alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
            }
        });//end ajax
    });

    $('#btnSubmitlink').click(function() {
        if($( "#link_title" ).val() && $( "#link_info" ).val()&& $("#tree").dynatree("getActiveNode")!=null){
            createLoader();
            var linkCloudSequence = 0;
            var updateLinkCloudSequence = 0;
            if(localStorage.piArchiverLinkSequence){
                linkCloudSequence =  parseInt(localStorage.piArchiverLinkSequence, 0);
            }
            if(localStorage.piArchiverUpdateLinkSequence){
                updateLinkCloudSequence =  parseInt(localStorage.piArchiverUpdateLinkSequence, 0);
            }
            $.ajax({
                url :  cloudUrl+"/submitlinkextension",
                type : "POST",
                data : "linkurl="+chrome.extension.getBackgroundPage().getSelectedTabUrl()
                        +"&linknote="+$( "#link_title" ).val()+" "+ $( "#link_info" ).val()
                        +"&categoryid="+$("#tree").dynatree("getActiveNode").data.key+"&linksequence="+linkCloudSequence+"&updatelinksequence="+updateLinkCloudSequence,
                success : function(response) {
                    removeLoader();
                    var newlinkslist = [];
                    var updatelinkslist = [];
                    if(response.resultnewlist!=null && response.resultnewlist.length>0)  {
                        //alert(response.resultnewlist.length);
                       chrome.extension.getBackgroundPage().indexedDBObject.addLinks(response.resultnewlist);
                       localStorage.setItem('piArchiverLinkSequence', response.currentcloudid);
                        localStorage.setItem('piArchiverUpdateLinkSequence', response.currentupdatecloudid);
                       chrome.extension.getBackgroundPage().changePageActionIconToBookmarked();
                        newlinkslist = response.resultnewlist;
                    }
                    if(response.resultupdatelist!=null && response.resultupdatelist.length>0)  {
                        //send update links to local server
                        localStorage.setItem('piArchiverUpdateLinkSequence', response.currentupdatecloudid);
                        updatelinkslist = response.resultupdatelist;
                    }
                    //send new links to local server
                    if(newlinkslist.length>0 || updatelinkslist.length>0)
                        $.post( localUrl+"/archive",JSON.stringify({ 'resultnewlist': newlinkslist, 'resultupdatelist': updatelinkslist }));
                    alert(response.message);
                },
                error: function (request, status, error) {
                    removeLoader();
                    alert("-responseText: "+request.responseText + "\n-Status: " +request.status+"\n-Error: "+error );
                }
            });//end ajax
        }else{
            alert("Please input link title and link info and choose category.");
        }
        /* var arrLinks = [{url:"http://www.html5rocks.com/en/tutorials/indexeddb/todo/",cloudid:"1",categoryid:"2"},
              {url:"http://www.onlywebpro.com/2012/12/23/html5-storage-indexeddb/",cloudid:"2",categoryid:"3"},
              {url:"http://stackoverflow.com/questions/9269891/android-face-detection",cloudid:"3",categoryid:"2"}];
         chrome.extension.getBackgroundPage().indexedDBObject.addLinks(arrLinks);            */
        /*
        chrome.extension.getBackgroundPage().indexedDBObject.searchALink("http://www.onlywebpro.com/2012/12/23/html5-storage-indexeddb/",
        function(result){
            if(result)
            alert('found');
            else
                alert('Not found');

        });*/
    });

});
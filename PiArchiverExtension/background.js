// Copyright (c) 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
var selectedTabTitle='No title';
var selectedTabUrl="";
////Index DB operations//////////////
var indexedDBObject = {};
indexedDBObject.db = null;


indexedDBObject.isIndexedDBOk=function () {
    return "indexedDB" in window;
}

indexedDBObject.open = function() {
    if(!this.isIndexedDBOk()){
        alert('IndexedDB is not supported!');
        return;
    }
    var version = 1;
    var request = indexedDB.open("piarchiverdb", version);

    // onupgradeneeded will be called before onsuccess
    request.onupgradeneeded = function(e) {
        var thisDB = e.target.result;
        // A versionchange transaction is started automatically.
        e.target.transaction.onerror = indexedDBObject.error;
        if(thisDB.objectStoreNames.contains("links")) {
            thisDB.deleteObjectStore("links");
        }
        var store = thisDB.createObjectStore("links",{autoIncrement:true}); //key will autoincrement
        store.createIndex("url_index", "url", {unique: true});//index only 'url' field

    }
    request.onsuccess = function(e) {
        indexedDBObject.db = e.target.result;
    };
    request.onerror = function() {
        alert('piArchiver init db failed!')   ;
    };
};
indexedDBObject.addLinks = function(arrLinksJson) {
    var db = indexedDBObject.db;
    var trans = db.transaction(["links"], "readwrite");
    var store = trans.objectStore("links");

    var date = new Date();
    var month = date.getMonth()+1;
    var datetimeStr = date.getDate()+"/"+month+"/"+date.getFullYear()+" "+date.toLocaleTimeString();

    $.each(arrLinksJson, function(i, item) {
        var customItem = {'url':item.url,'cloudlinkid':item.cloudid,'cloudCreatedOn':item.createdon};
        $.extend(customItem, {'localCreatedOn': datetimeStr});
       // console.log(item);
        var request = store.put(customItem);
        request.onerror = function(e) {
            console.log("Error",e.target.error.name);
            //some type of error handler
        }

        request.onsuccess = function(e) {
            console.log("Saving OK");
        }
    });//end each

    trans.oncomplete = function() {
        // All requests have succeeded and the transaction has committed.
    };

    trans.onerror = function(e) {
        console.log(e.value);
    };
};
indexedDBObject.searchALink = function(url,resultcallback) {
    var db = indexedDBObject.db;
    var trans = db.transaction(["links"], "readwrite");
    var store = trans.objectStore("links");
    var url_index = store.index("url_index");
    var request = url_index.get(url);
    console.log("start searching");
    request.onsuccess = function(e) {
        var result = e.target.result;
        if (result) {
            // Called for each matching record.
            console.log("found");
            resultcallback(true);
            //cursor.continue();
        } else {
            // No more matching records.
            console.log("Not found");
            resultcallback(false);
        }
    };
    request.onerror = function(e) {
        console.log(e.value);
    };
};
indexedDBObject.error = function() {
    alert('There is error with indexeddb!')   ;
};
////////////////////////////////////////////////////
// Called when the url of a tab changes.
function checkForValidUrl(tabId, changeInfo, tab) {
  // If the letter 'g' is found in the tab's URL...
  //alert(changeInfo.status);
    indexedDBObject.searchALink(tab.url,function(isFound){
        if(isFound){
            if(changeInfo.status=="complete")
            {
                chrome.pageAction.setIcon({path: "img/icon-19.png",
                    tabId: tab.id});
                chrome.pageAction.show(tabId);
                chrome.pageAction.setPopup({popup: "",
                    tabId: tab.id})
            }
        }else{
            if(changeInfo.status=="complete")
            {
                selectedTabTitle = tab.title;
                selectedTabUrl = tab.url;
                chrome.pageAction.show(tabId);//only show icon popup when page is loaded completely
            }
        }
    });

};



function getSelectedTabTitle()
{
	return selectedTabTitle;
}
function getSelectedTabUrl()
{
    return selectedTabUrl;
}
function changePageActionIconToBookmarked()
{
    chrome.tabs.query({'active': true, 'windowId': chrome.windows.WINDOW_ID_CURRENT},
        function(tabs){
            var tabId = tabs[0].id;
            //alert(tabId);
            chrome.pageAction.setIcon({path: "img/icon-19.png",
                tabId: tabId});
            chrome.pageAction.show(tabId);
            chrome.pageAction.setPopup({popup: "",
                tabId: tabId});
        }
    );

   /* chrome.tabs.query(
        { currentWindow: true, active: true },
        function (tabArray) {
            var tabId = tabArray[0].id;
            alert(tabId);
            chrome.pageAction.setIcon({path: "img/icon-19.png",
                tabId: tabId});
            chrome.pageAction.show(tabId);
        }
    ); */
}

function changePageActionIconToBookmarkedIfLinkExisted(){
    chrome.tabs.query({'active': true, 'windowId': chrome.windows.WINDOW_ID_CURRENT},
        function(tabs){
            indexedDBObject.searchALink(tabs[0].url,function(isFound){
                if(isFound){
                    chrome.pageAction.setIcon({path: "img/icon-19.png",
                        tabId: tabs[0].id});
                    chrome.pageAction.show(tabs[0].id);
                    chrome.pageAction.setPopup({popup: "",
                        tabId: tabs[0].id})
                }
            });
        }
    );
}
// Listen for any changes to the URL of any tab.
chrome.tabs.onUpdated.addListener(checkForValidUrl);
chrome.tabs.onActivated.addListener(function(info) {
    var tab = chrome.tabs.get(info.tabId, function(tab) {
        selectedTabTitle = tab.title;
        selectedTabUrl = tab.url;
    });
	
});

//init indexed db
indexedDBObject.open();

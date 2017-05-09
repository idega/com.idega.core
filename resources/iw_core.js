//***********************************************************//
//
// idegaWeb Core Javascript Function library
// (c) Copyright idega hf. 2000-2008 All rights reserved.
//
// 	This file contains proprietary information owned
// 	by idega and may not be copied without prior concent.
//
//***********************************************************//

//***********************************************************//
//
//Begin Generic Functions
//
//***********************************************************//

if(IWCORE == null) var IWCORE = {};
if(IdegaResourcesHandler == null) var IdegaResourcesHandler = [];

var IE = document.all?true:false; 

function iwOpenWindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height,Xcoord,Ycoord) {  
  	// usage openwindow(addr,name,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,width,height,xcoord,ycoord) 
	var option = 'toolbar=' + ToolBar 
	+ ',location=' + Location  
	+ ',directories=' + Directories  
	+ ',status=' + Status  
	+ ',menubar=' + Menubar  
	+ ',titlebar=' + Titlebar  
	+ ',scrollbars=' + Scrollbars  
	+ ',resizable='  + Resizable  
	+ ',width=' + Width
	+ ',height=' + Height; 
	if(Xcoord) option+=',left=' + Xcoord;
	if(Ycoord) option+=',top=' + Ycoord;
	
	var new_win = window.open(Address,Name,option);
	new_win.focus();
}

//backward compatability
function openWindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height,Xcoord,Ycoord){
	iwOpenWindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height,Xcoord,Ycoord);
}

function swapImage(){ var i,j=0,x,a=swapImage.arguments; document.sr=new Array; for(i=0;i<(a.length-2);i+=3) if ((x=findObj(a[i]))!=null){document.sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}}

// old function findObj(n, d){var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {  d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=findObj(n,d.layers[i].document); return x;}

/**
* Improved finder
* To reference any frame, iframe, form, input, image or anchor (but not link) by its name, or
* positioned element by its id in the current document - it can optionally scan through any frameset
* structure to find it (searching in frames that have not loaded will cause an error):
*/
function findObj( oName, oFrame, oDoc ) {
	if( !oDoc ) {if( oFrame ) { oDoc = oFrame.document; } else { oDoc = window.document; } }
	if( oDoc[oName] ) { return oDoc[oName]; } if( oDoc.all && oDoc.all[oName] ) { return oDoc.all[oName]; }
	if( oDoc.getElementById && oDoc.getElementById(oName) ) { return oDoc.getElementById(oName); }
	for( var x = 0; x < oDoc.forms.length; x++ ) { if( oDoc.forms[x][oName] ) { return oDoc.forms[x][oName]; } }
	for( var x = 0; x < oDoc.anchors.length; x++ ) { if( oDoc.anchors[x].name == oName ) { return oDoc.anchors[x]; } }
	for( var x = 0; document.layers && x < oDoc.layers.length; x++ ) {
	var theOb = findObj( oName, null, oDoc.layers[x].document ); if( theOb ) { return theOb; } }
	if( !oFrame && window[oName] ) { return window[oName]; } if( oFrame && oFrame[oName] ) { return oFrame[oName]; }
	for( var x = 0; oFrame && oFrame.frames && x < oFrame.frames.length; x++ ) {
	var theOb = findObj( oName, oFrame.frames[x], oFrame.frames[x].document ); if( theOb ) { return theOb; } }
	return null;
}

function preLoadImages(){var d=document; if(d.images){ if(!d.p) d.p=new Array(); var i,j=d.p.length,a=preLoadImages.arguments; for(i=0; i<a.length; i++)  if (a[i].indexOf("#")!=0){ d.p[j]=new Image; d.p[j++].src=a[i];}}}

function swapImgRestore() {var i,x,a=document.sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;}

function changeInputValue (input,newValue) { input.value=newValue; }

//************************************************//
//
//End Generic Functions
//
//************************************************//


//************************************************//
//
//Begin Date/TimeInput Functions
//
//************************************************//

function iwPopulateDaysWithYear(yearInput,monthInput,dayInput,dayDisplayString) {
	var yearSelected=yearInput.selectedIndex;
	var yearValue=0;
	if(yearSelected=='0'){
		//yearValue=2000;
		//alert("1");
	}	
	else{
		yearValue = yearInput.options[yearSelected].value;
	}

	iwPopulateDaysWithMonth(yearValue,monthInput,dayInput,dayDisplayString);
}

function iwPopulateDaysWithMonth(yearValue,monthInput,dayInput,dayDisplayString) {

	var monthSelected=monthInput.selectedIndex;
	var monthValue;

	if(monthSelected=='0'){
		monthValue=1;
	}
	else{
		monthValue = monthInput.options[monthSelected].value;
	}
	iwPopulateDays(yearValue,monthValue,dayInput,dayDisplayString);
}

function iwPopulateDays(yearValue,monthValue,dayInput,dayDisplayString) {
	
	if (yearValue==-1){
		var currentYear = new Date();
		yearValue = currentYear.getFullYear();
	}
	var previousDayLength = dayInput.options.length;

	timeA = new Date(yearValue,monthValue,'01');
	
	//a trick to get the last day of the previous month compared to monthValue, 86400000 is one day (24 hours)
	timeDifference = timeA - 86400000;
	timeB = new Date(timeDifference);
	
	var oldSelectedDay = dayInput.selectedIndex;
	var daysInMonth = timeB.getDate();
	
	if(previousDayLength>=2){
		for (var i = 0; i < previousDayLength ; i++) {
			dayInput.options[i] = null;
		}
	}
	dayInput.options[0] = new Option(dayDisplayString,dayDisplayString);
	
	for (var i = 1; i <= daysInMonth; i++) {

			if (i<10){
				dayInput.options[i] = new Option(i,'0'+i);
			}
			else{
				dayInput.options[i] = new Option(i,i);
			}
	}

	if (oldSelectedDay < daysInMonth) {
		dayInput.options[oldSelectedDay].selected = true;
	}
	else {
		dayInput.options[daysInMonth].selected = true;
	}
	
}

function iwSetValueOfHiddenDateWithAllInputs(yearInput,monthInput,dayInput,hiddenInput){

	var yearValue=0;
	
	if(yearInput.selectedIndex != 0){
		yearValue=yearInput.options[yearInput.selectedIndex].value;
	}

	iwSetValueOfHiddenDateWithDay(yearValue,monthInput,dayInput,hiddenInput);
}

function iwSetValueOfHiddenDateWithDay(yearValue,monthInput,dayInput,hiddenInput){

	var dayValue=0;

	if(dayInput.selectedIndex != 0){
		dayValue=dayInput.options[dayInput.selectedIndex].value;
	}
	iwSetValueOfHiddenDateWithMonth(yearValue,monthInput,dayValue,hiddenInput);

}

function iwSetValueOfHiddenDateWithYear(yearInput,monthInput,hiddenInput){

	var yearValue=0;
	var dayValue='01';

	if(yearInput.selectedIndex != 0){
		yearValue=yearInput.options[yearInput.selectedIndex].value;
	}

	iwSetValueOfHiddenDateWithMonth(yearValue,monthInput,dayValue,hiddenInput);
}

function iwSetValueOfHiddenDateWithMonth(yearValue,monthInput,dayValue,hiddenInput){

	var monthValue=0;

	if(monthInput.selectedIndex != 0){
		monthValue=monthInput.options[monthInput.selectedIndex].value;
	}


	iwSetValueOfHiddenInput(yearValue,monthValue,dayValue,hiddenInput);
	
}

function iwSetValueOfHiddenInput(yearValue,monthValue,dayValue,hiddenInput){
	if ((yearValue == 0) || (monthValue == 0) || (dayValue == 0)){
		hiddenInput.value = '';
	}
	else{
		//hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+'';
		hiddenInput.value = yearValue+'-'+monthValue+'-'+dayValue+'';
	}
	
}

//************************************************//
//
//End Date/TimeInput Functions
//
//************************************************//


//************************************************//
//
// Begin Windowing and iframe methods
//
//************************************************//


/**
*This object windowinfo is for obtaining information such as height and width about the open window in a cross-browser manner.
**/
var windowinfo = {
  getWindowWidth: function () {
    this.width = 0;
    if (window.innerWidth){
    	//This is for Firefox and Safari
	    this.width = window.innerWidth ;
    	//alert('getWindowWidth: innerWidth');
    }
    else if (document.documentElement && document.documentElement.clientWidth) {
  		this.width = document.documentElement.clientWidth;
  		//alert('getWindowWidth: documentElement');
  	}
    else if (document.body && document.body.clientWidth) {
    	//This is for IE
  		this.width = document.body.clientWidth;
  		//alert('getWindowWidth: body');
  	}
  	return this.width;
  },
  
  getWindowHeight: function () {
    this.height = 0;
    if (window.innerHeight){
    	 //This is for Firefox and Safari
    	 this.height = window.innerHeight;
    	 //alert('getWindowHeight: innerHeight');
    }
  	else if (document.documentElement && document.documentElement.clientHeight){
  		this.height = document.documentElement.clientHeight;
  		//alert('getWindowHeight: documentElement');
  	}
  	else if (document.body && document.body.clientHeight) {
  		//This is for IE
  		this.height = document.body.clientHeight;
  		//alert('getWindowHeight: body');
  	}
  	return this.height;
  },
  
  getScrollX: function () {
    this.scrollX = 0;
  	if (typeof window.pageXOffset == "number") this.scrollX = window.pageXOffset;
  	else if (document.documentElement && document.documentElement.scrollLeft)
  		this.scrollX = document.documentElement.scrollLeft;
  	else if (document.body && document.body.scrollLeft) 
  		this.scrollX = document.body.scrollLeft; 
  	else if (window.scrollX) this.scrollX = window.scrollX;
  },
  
  getScrollY: function () {
    this.scrollY = 0;    
    if (typeof window.pageYOffset == "number") this.scrollY = window.pageYOffset;
    else if (document.documentElement && document.documentElement.scrollTop)
  		this.scrollY = document.documentElement.scrollTop;
  	else if (document.body && document.body.scrollTop) 
  		this.scrollY = document.body.scrollTop; 
  	else if (window.scrollY) this.scrollY = window.scrollY;
  },
  
  getAll: function () {
    this.getWindowWidth(); this.getWindowHeight();
    this.getScrollX();  this.getScrollY();
  }
}

/**
* This method is for creating an iframe with 'floating' height, i.e. that the frame with take 
* the height of the window minus the top and bottom margins specified in this function
**/
function setIframeHeight(iframeId,topmargin,bottommargin) {
  var theIframe = document.getElementById? document.getElementById(iframeId): document.all? document.all[iframeId]: null;
  if (theIframe) {
    windowinfo.getWindowHeight();
    //  both theIframe.height and theIframe.style.height seem to work 
	var iframeHeight = windowinfo.height - topmargin - bottommargin;
    //alert(iframeHeight);
    theIframe.style.height = iframeHeight + "px";
    theIframe.style.marginTop = topmargin + "px";
  }
}

/**
*This Function sets the current window to be in given size and center it on screen.
**/
function setWindowSizeCentered(width,height){

	window.resizeTo(width,height);
	var body = document.body;
	var body_height = 0;
	if (typeof bottom == "undefined") {
		var div = document.createElement("div");
		body.appendChild(div);
		var pos = getAbsolutePos(div);
		body_height = pos.y;
	} else {
		var pos = getAbsolutePos(bottom);
		body_height = pos.y + bottom.offsetHeight;
	}
	
	var body_height = 0;
	if (!document.all) {
	//For Gecko:
		//window.sizeToContent();
		//window.sizeToContent();	// for reasons beyond understanding,
					// only if we call it twice we get the
					// correct size.
		//window.addEventListener("unload", __dlg_onclose, true);
		window.innerWidth = body.offsetWidth + 5;
		window.innerHeight = body_height + 2;
		// center on parent
		var x = opener.screenX + (opener.outerWidth - window.outerWidth) / 2;
		var y = opener.screenY + (opener.outerHeight - window.outerHeight) / 2;
		window.moveTo(x, y);
	} else {
	//For IE:
		// window.dialogHeight = body.offsetHeight + 50 + "px";
		// window.dialogWidth = body.offsetWidth + "px";
		//window.resizeTo(body.offsetWidth, body_height);
		var ch = body.clientHeight;
		var cw = body.clientWidth;
		//window.resizeBy(body.offsetWidth - cw, body_height - ch);
		var W = body.offsetWidth;
		var H = 2 * body_height - ch;
		var x = (screen.availWidth - W) / 2;
		var y = (screen.availHeight - H) / 2;
		window.moveTo(x, y);
	}

}

//************************************************//
//
// End Windowing and iframe methods
//
//************************************************//

//************************************************//
//
//Table ruler Functions
//
//************************************************//

window.onload=function(){ tableruler();}

function tableruler()
{
	if (document.getElementById && document.createTextNode)
	{
		var tables=document.getElementsByTagName('table');
		for (var i=0;i<tables.length;i++)
		{
			if(tables[i].className.indexOf('ruler') != -1)
			{
				var trs=tables[i].getElementsByTagName('tr');
				for(var j=0;j<trs.length;j++)
				{
					if(trs[j].parentNode.nodeName=='TBODY')
					{
						var className = trs[j].className;
						trs[j].onmouseover=function() { this.className=this.className + ' ruled';return false; }
						trs[j].onmouseout=function() { this.className=this.className.substring(0, this.className.indexOf('ruled') - 1);return false; }
					}
				}
			}
		}
	}
}

IWCORE.canUsejQuery = function() {
	var canUserjQuery = false;
	try {
		if (jQuery) {
			canUserjQuery = true;
		}
	} catch(e) {}
	return canUserjQuery;
}

/*Loading Layer function: */
	
	var loaded = false;
	var image1 = new Image();
	image1.src = '/idegaweb/bundles/com.idega.core.bundle/resources/loading_notext.gif';
	
	var image2 = new Image();
	image2.src='/idegaweb/bundles/com.idega.core.bundle/resources/style/images/transparent.png';

	var image3 = new Image();
	image3.src='/idegaweb/bundles/com.idega.core.bundle/resources/style/images/whitetransparent.png';
	
	var image4 = new Image();
	image4.src='/idegaweb/bundles/com.idega.core.bundle/resources/style/images/ajax-loader.gif';

	function showLoadingMessage(sLoadingText) {	
		var outer = document.createElement('div');
		outer.setAttribute('id', 'busybuddy'); 
		outer.setAttribute('class', 'LoadLayer');
		//IE class workaround:
		outer.setAttribute('className', 'LoadLayer');
		
		var middle = document.createElement('div');
		middle.setAttribute('id', 'busybuddy-middle');
		middle.setAttribute('class', 'LoadLayerMiddle');
		//IE class workaround:
		middle.setAttribute('className', 'LoadLayerMiddle');
		outer.appendChild(middle);
		
		var inner = document.createElement('div');
		inner.setAttribute('id', 'busybuddy-contents');
		inner.setAttribute('class', 'LoadLayerContents');
		//IE class workaround:
		inner.setAttribute('className', 'LoadLayerContents');
		middle.appendChild(inner);
		
		var image = document.createElement('img');
		image.setAttribute('id', 'loadingimage');
		image.setAttribute('src',image1.src);
		image.src=image1.src;
		inner.appendChild(image);
		
		var span =  document.createElement('span');
		span.setAttribute('id', 'loadingtext');
		inner.appendChild(span);
		
		var text = document.createTextNode(sLoadingText);
		span.appendChild(text);
		
		var bodyArray = document.getElementsByTagName('body');
		var bodyTag = bodyArray[0];
		bodyTag.appendChild(outer);
		//alert('bodyTag:'+bodyTag);
		
		if( outer.style ) { 
	      outer.style.visibility = 'visible';
	    }
	    else {
	      outer.visibility = 'show' ;
	    }
	}
	
	function showElementLoading(id) {
		setLoadingLayerForElement(id, true, null, null);
	}
	
function setLoadingLayerForElement(id, removeContent, size, position) {
	var component = document.getElementById(id);
	if (component == null) {
		return false;
	}
	
	var outer = document.createElement('div');
	outer.setAttribute('id', 'busybuddy'); 
	outer.setAttribute('class', 'LocalLoadLayer');
	//IE class workaround:
	outer.setAttribute('className', 'LocalLoadLayer');
	
	var middle = document.createElement('div');
	middle.setAttribute('id', 'busybuddy-middle');
	middle.setAttribute('class', 'LoadLayerMiddle');
	//IE class workaround:
	middle.setAttribute('className', 'LoadLayerMiddle');
	outer.appendChild(middle);
	
	var inner = document.createElement('div');
	inner.setAttribute('id', 'busybuddy-contents');
	inner.setAttribute('class', 'LoadLayerContents');
	//IE class workaround:
	inner.setAttribute('className', 'LoadLayerContents');
	middle.appendChild(inner);
	
	var image = document.createElement('img');
	image.setAttribute('id', 'loadingimage');
	image.setAttribute('src',image4.src);
	image.setAttribute('class', 'LoadingImage');
	image.setAttribute('className', 'LoadingImage');
	image.src=image4.src;
	inner.appendChild(image);
			
	if (removeContent) {
		removeChildren(component);
	}
	if (size == null) {
		component.appendChild(outer);
	}
	else {
		outer.style.position = 'absolute';
		
		outer.style.left = position.x + 'px';
		outer.style.top = position.y + 'px';
		
		outer.style.width = size.size.x + 'px';
		outer.style.height = size.size.y + 'px';
		
		document.body.appendChild(outer);
	}
	
	if (outer.style) {
		outer.style.visibility = 'visible';
	}
	else {
		outer.visibility = 'show';
	}
	
	return outer;
}
	
function closeLoadingMessage() {
	var busyMessage = document.getElementById("busybuddy");
	if (busyMessage == null) {
		return;
	}
	else {
		var parentElement = busyMessage.parentNode;
		if (parentElement == null) {
			if (busyMessage.style) {
				busyMessage.style.display = "none";
				busyMessage.style.visibility = "hidden";
			}
			else {
				busyMessage.display = "none";
				busyMessage.visibility = "hidden";
			}
		}
		else {
			parentElement.removeChild(busyMessage);
			closeLoadingMessage();	//	We want to close all layers
		}
	}
}

function closeAllLoadingLayers(className) {
	var layers = getElementsByClassName(document.body, '*', className);
	if (layers == null) {
		return;
	}
	var parentNode = null;
	var layer = null;
	for (var i = 0; i < layers.length; i++) {
		layer = layers[i];
		parentNode = layer.parentNode;
		if (parentNode != null) {
			parentNode.removeChild(layer);
		}
	}
}

function closeAllLoadingMessages() {
	closeAllLoadingLayers('LoadLayer');
}

function closeAllLocalLoadingLayers() {
	closeAllLoadingLayers('LocalLoadLayer');
}

//setLinkToBold method. moved here to fix bug in UserApplication

function setLinkToBold(input) {
	input.style.fontWeight='bold';
}




//SCROLLTABLE:

/*http://www.imaputz.com/cssStuff/bigFourVersion.html*/

/* onload state is fired, append onclick action to the table's DIV */
/* container. This allows the HTML document to validate correctly. */
/* addIEonScroll added on 2005-01-28                               */
/* Terence Ordona, portal[AT]imaputz[DOT]com                       */
function addIEonScroll() {
	var thisContainer = document.getElementById('scrollContainer');
	if (!thisContainer) { return; }

	var onClickAction = 'toggleSelectBoxes();';
	thisContainer.onscroll = new Function(onClickAction);
}

/* Only WinIE will fire this function. All other browsers scroll the TBODY element and not the DIV */
/* This is to hide the SELECT elements from scrolling over the fixed Header. WinIE only.           */
/* toggleSelectBoxes added on 2005-01-28 */
/* Terence Ordona, portal[AT]imaputz[DOT]com         */
function toggleSelectBoxes() {
	var thisContainer = document.getElementById('scrollContainer');
	var thisHeader = document.getElementById('fixedHeader');
	if (!thisContainer || !thisHeader) { return; }

	var selectBoxes = thisContainer.getElementsByTagName('select');
	if (!selectBoxes) { return; }

	for (var i = 0; i < selectBoxes.length; i++) {
		if (thisContainer.scrollTop >= eval(selectBoxes[i].parentNode.offsetTop - thisHeader.offsetHeight)) {
			selectBoxes[i].style.visibility = 'hidden';
		} else {
			selectBoxes[i].style.visibility = 'visible';
		}
	}
} 

/*window.onload = function() { stripedTable(); addIEonScroll(); }*/
/*window.onload = function() { addIEonScroll(); }*/

//ScrollTable end

function openContentEditor(url){
	iwOpenWindow(url,'contentEditor','0','0','0','0','0','0','0','1','\'700\'','\'600\'');
}


function expandMinimizeContents(container){
	var expander;
	var contents;
	var divs = container.getElementsByTagName('div');
	for (var i=0;i<divs.length;i++)
	{
			if(divs[i].className.indexOf('contents') != -1)
			{
				contents = divs[i];
			}
			else if(divs[i].className.indexOf('expander') != -1)
			{
				expander = divs[i];
			}
	}
	var expanderClass = expander.getAttribute('class');
	var contentsClass = contents.getAttribute('class');
	if(contentsClass=='contents expanded'){
		expander.className='expander minimized';
		contents.className='contents minimized';
	}
	else if(contentsClass=='contents minimized'){
		expander.className='expander expanded';
		contents.className='contents expanded';
	}
}

function insertJavaScriptFileToHeader(src) {
	if (src == null || src == '') {
		return;
	}
	var script = document.createElement("script");
	script.setAttribute("type","text/javascript");
	script.setAttribute("src", src);
	document.getElementsByTagName("head")[0].appendChild(script);
}

function isEnterEvent(event) {
	if (event == null) {
		return false;
	}
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if (keyCode == 13) {
		return true;
	}
	return false;
}

function isNumberEntered(event) {
	if (event == null)
		return false;
		
	var code = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	
	//	Backspace or delete
	if (code == 8 || code == 46)
		return true;
		
	if (code >= 96 && code <= 105)
		return true;
	
	var value = String.fromCharCode(code);
	return IWCORE.isNumericValue(value);
}

function isSafariBrowser() {
	if (navigator == null) {
		return false;
	}
	var browser = navigator.userAgent;
	if (browser == null) {
		return false;
	}
	browser = browser.toLowerCase();
	if (browser.indexOf("safari") == -1) {
		return false;
	}
	return true;
}

function isChromeBrowser() {
	return navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
}

// Function to add event to any object, example: registerEvent(window, "load", foo);
function registerEvent(object, eventType, functionName){ 
	if (object.addEventListener) { 
		object.addEventListener(eventType, functionName, false); 
		return true;
	} 
	else {
		if (object.attachEvent) {
			var result = object.attachEvent("on"+eventType, functionName);
			return result;
		}
		else { 
   			return false;
		}
	} 
}

/**
 * linkToFeed: e.g. http://www.idega.com/rss/articles.xml
 * feedType: e.g. "atom" or "rss"
 * feedTitle: e.g. "Atom 1.0" or "RSS 2.0"
 */
function addFeedSymbolInHeader(linkToFeed, feedType, feedTitle) {
	if (linkToFeed == null || feedType == null || feedTitle == null) {
		return;
	}
	
	var headElement = document.getElementsByTagName('head')[0];
	if (headElement == null) {
		return;
	}

	//	Check if such feed already has been added	
	var linksInHead = headElement.getElementsByTagName('link');
	if (linksInHead != null) {
		for (var i = 0; i < linksInHead.length; i++) {
			var linkElement = linksInHead[i];
			if (linkElement.getAttribute('href') == linkToFeed) {
				return;
			}
		}
	}
	
	var linkToAtomInHeader = document.createElement('link');
	linkToAtomInHeader.setAttribute('href', linkToFeed);
	linkToAtomInHeader.setAttribute('title', feedTitle);
	linkToAtomInHeader.setAttribute('type', 'application/' + feedType + '+xml');
	linkToAtomInHeader.setAttribute('rel', 'alternate');
	headElement.appendChild(linkToAtomInHeader);
}

// Returns element postition from left (px)
function getAbsoluteLeft(objectId) {
	o = document.getElementById(objectId);
	if (o == null) {
		return 0;
	}
	oLeft = o.offsetLeft;
	while(o.offsetParent != null) {
		oParent = o.offsetParent;
		oLeft += oParent.offsetLeft;
		o = oParent;
	}
	return oLeft;
}

// Returns element postition from top (px)
function getAbsoluteTop(objectId) {
	o = document.getElementById(objectId);
	if (o == null) {
		return 0;
	}
	oTop = o.offsetTop;
	while(o.offsetParent != null) {
		oParent = o.offsetParent;
		oTop += oParent.offsetTop;
		o = oParent;
	}
	return oTop;
}

IWCORE.existElementInArray = function(array, element) {
	if (array == null || element == null) {
		return;
	}
	var found = false;
	for (var i = 0; (i < array.length && !found); i++) {
		if (element == array[i]) {
			found = true;
		}
	}
	return found;
}

function removeElementFromArray(array, elementToRemove) {
	if (array == null || elementToRemove == null) {
		return;
	}
	var index = 0;
	var found = false;
	for (var i = 0; (i < array.length && !found); i++) {
		if (elementToRemove == array[i]) {
			index = i;
			found = true;
		}
	}
	if (found) {
		array.splice(index, 1);
	}
}

function removeChildren(element) {
	if (element == null) {
		return;
	}
	var children = element.childNodes;
	if (children == null) {
		return;
	}
	var size = children.length;
	var child = null;
	for (var i = 0; i < size; i++) {
		child = children[0];
		if (child != null) {
			element.removeChild(child);
		}
	}
}

//use this function rather than innerHtml it's much faster see: http://blog.stevenlevithan.com/archives/faster-than-innerhtml
function replaceHtml(el, html) {
	var oldEl = (typeof el === "string" ? document.getElementById(el) : el);
	/*@cc_on // Pure innerHTML is slightly faster in IE
		oldEl.innerHTML = html;
		return oldEl;
	@*/
	var newEl = oldEl.cloneNode(false);
	newEl.innerHTML = html;
	oldEl.parentNode.replaceChild(newEl, oldEl);
	/* Since we just removed the old element from the DOM, return a reference
	to the new element, which can be used to restore variable references. */
	return newEl;
};

/** These functions transforms Document (received via DWR) to DOM **/
function getTransformedDocumentToDom(component) {
	var nodes = new Array();
	if (component == null) {
		return nodes;
	}
	var children = component.childNodes;
	if (children == null) {
		return nodes;
	}
	if (children.length == 0) {
		return nodes;
	}
	
	var size = children.length;
	var node = null;
	for (var i = 0; i < size; i++) {
		node = children.item(i);
		nodes.push(node);
	}
	return nodes;
}

function executeJavaScriptActionsCodedInStringInGlobalScope(code) {
	if (code == null || code == '') {
		return null;
	}
	
	//	Executing script in global scope
	var dj_global = this;
	
	if (window.execScript) {
		window.execScript(code);
		return null;
	}
	
	return dj_global.eval ? dj_global.eval(code) : eval(code);
}

IWCORE.getFixedHrefValue = function(hrefValue) {
	if (hrefValue == null) {
		return null;
	}
	
	while (hrefValue.indexOf('&#38;') != -1) {
		hrefValue = hrefValue.replace('&#38;', '&');
	}
	
	return hrefValue;
}

IWCORE.getParameterByName = function(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

IWCORE.createRealNode = function(element, scriptsToEval, resourcesToAdd) {
	var DYNAMIC_HTML_ELEMENT_FUNCTION_SEPARATOR = '%idega_separator%';
	
	//	XML
	if (element.nodeName == 'xml') {
		var fakeDiv = document.createElement('div');
		return fakeDiv;
	}
	
	//	Text
	if (element.nodeName == '#text') {
		var textNode = document.createTextNode(element.nodeValue);
		return textNode;
	}
	
	//	Comment
	if (element.nodeName == '#comment') {
		var commentNode = document.createComment(element.nodeValue);
		return commentNode;
	}
    
    //  JavaScript cnode
    if (element.nodeName == '#cdata-section' && element.parentNode.nodeName == 'script') {
        var value = element.nodeValue;
    
		if (value.indexOf('prototype') == -1 && value.indexOf('scriptac') == -1) {
			if (scriptsToEval == null) {
				scriptsToEval = new Array();
			}
			scriptsToEval.push(element.nodeValue);
		}

		return document.createElement('script');
    }
	
	//	Script
	if (element.nodeName == 'script') {
		if (element.nodeValue != null && element.nodeValue != '') {
			var action = '' + element.nodeValue;
			
			if (action.indexOf('<!--') == -1 && action.indexOf('//-->') == -1) {
				if (scriptsToEval == null) {
					scriptsToEval = new Array();
				}
				scriptsToEval.push(action);	//	Adding action needed to execute
			}
		}
		
		if (element.attributes != null) {
			for (var i = 0; i < element.attributes.length; i++) {
				var attribute = element.attributes[i];
				if (attribute.nodeName == 'src') {
					if (resourcesToAdd == null) {
						resourcesToAdd = new Array();
					}
					resourcesToAdd.push(attribute.nodeValue);	//	Adding source file
				}
			}
		}
		
		if (element.childNodes != null) {
			var allActions = '';
			for (var i = 0; i < element.childNodes.length; i++) {
				var scriptNodeValue = element.childNodes[i].nodeValue;
				
				if (scriptNodeValue.indexOf('<!--') != -1) {
					scriptNodeValue = scriptNodeValue.replace(/<!--/g, '');
				}
				
				if (scriptNodeValue.indexOf('//-->') != -1) {
                    scriptNodeValue = scriptNodeValue.replace(/\/\/-->/g, '');
                }
                
                allActions += scriptNodeValue;
			}
			if (allActions != null && allActions != '') {
				allActions = allActions.replace('\n//', '');
				if (scriptsToEval == null) {
					scriptsToEval = new Array();
				}
				scriptsToEval.push(allActions);	//	Adding actions needed to execute
			}
		}
		
		return document.createElement('script');
	}
	
	//	Link
	if (element.nodeName == 'link') {
		if (element.attributes != null) {
			var foundCSSForScreen = false;
			for (var i = 0; (i < element.attributes.length && !foundCSSForScreen); i++) {
				var attribute = element.attributes[i];
				if (attribute.nodeName == 'media' && attribute.nodeValue == 'screen') {
					foundCSSForScreen = true;
				}
			}
			
			if (foundCSSForScreen) {
				for (var i = 0; i < element.attributes.length; i++) {
					var attribute = element.attributes[i];
					if (attribute.nodeName == 'href') {
						var hrefValue = IWCORE.getFixedHrefValue(attribute.nodeValue);
						if (hrefValue != null) {
							if (resourcesToAdd == null) {
								resourcesToAdd = new Array();
							}
							resourcesToAdd.push(hrefValue);	//	Adding CSS source file
						}
					
						return document.createElement('link');	//	Fake
					}
				}
			}
		}
	}
	
	// Element
	var result = document.createElement(element.nodeName);
	if (element.attributes != null) {
		var functionsToRegister = new Array();	//	Functions for single element, like onclick, onchange etc.
		for (var i = 0; i < element.attributes.length; i++) {
			var attribute = element.attributes[i];
			if (attribute.nodeName.indexOf('on') == 0 && IE) {
				var event = attribute.nodeName.substring(attribute.nodeName.indexOf('on') + 2);
				var functionCall = attribute.nodeValue;
				functionsToRegister.push(event + DYNAMIC_HTML_ELEMENT_FUNCTION_SEPARATOR + functionCall);
			} else if (attribute.nodeName == 'checked' && IE) {
				var isChecked = attribute.nodeValue == 'true';
				if (isChecked) {
					result.setAttribute('checked', true);
					result.setAttribute('defaultChecked', true);
				}
			} else if (attribute.nodeName == 'style' && IE) {
				var styleValue = attribute.nodeValue;
				result.setAttribute('style', styleValue);
			} else if (attribute.nodeName == 'href') {
				var hrefValue = IWCORE.getFixedHrefValue(attribute.nodeValue);
				if (hrefValue != null) {
					result.setAttribute(attribute.nodeName, hrefValue);
				}
			} else if (attribute.nodeName == 'src' && element.nodeName == 'script') {
				if (resourcesToAdd == null) {
					resourcesToAdd = new Array();
				}
				resourcesToAdd.push(attribute.nodeValue);	//	Adding source file
			} else if (attribute.nodeName == 'class' && IE) {
				result.className = attribute.nodeValue;
			} else if (attribute.nodeName != null && attribute.nodeValue != null) {
				result.setAttribute(attribute.nodeName, attribute.nodeValue);
			}
		}
		
		if (functionsToRegister.length > 0) {
			var expression = null;
			for (var f = 0; f < functionsToRegister.length; f++) {
				expression = functionsToRegister[f];
				
				var splittedExpression = expression.split(DYNAMIC_HTML_ELEMENT_FUNCTION_SEPARATOR);
				if (splittedExpression.length == 2) {
					var elementFunction = function() {
						try {
							window.eval(splittedExpression[1]);
						} catch(e) {}
					};
					registerEvent(result, splittedExpression[0], elementFunction);
				}
			}
		}
	}
	
	if (element.childNodes != null) {
		for (var j = 0; j < element.childNodes.length; j++) {
			result.appendChild(IWCORE.createRealNode(element.childNodes[j], scriptsToEval, resourcesToAdd));
		}
	}
	
	return result;
};

IWCORE.includeResourcesAndExecuteActions = function(resourcesToAdd, scriptsToEval) {
	var actionsToExecute = null;
	if (scriptsToEval != null && scriptsToEval.length > 0) {
		actionsToExecute = '';
		for (var i = 0; i < scriptsToEval.length; i++) {
			actionsToExecute += scriptsToEval[i];
		}
	}
	
	if (resourcesToAdd == null || resourcesToAdd.length == 0) {
		executeJavaScriptActionsCodedInStringInGlobalScope(actionsToExecute);
		return false;
	}
	
	var callback = function() {
		executeJavaScriptActionsCodedInStringInGlobalScope(actionsToExecute);
	}
	
	LazyLoader.loadMultiple(resourcesToAdd, callback);
}

function createRealNode(element, scriptsToEval, resourcesToAdd) {
	var newElement = IWCORE.createRealNode(element, scriptsToEval, resourcesToAdd);
	return newElement;
}

function replaceNode(component, nodeToReplace, container) {
	if (component == null || nodeToReplace == null) {
		return;
	}
	
	var nodes = getTransformedDocumentToDom(component);
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	var scriptsToEval = new Array();
	var resourcesToAdd = new Array();
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode, scriptsToEval, resourcesToAdd);
		container.replaceChild(realNode, nodeToReplace);
	}
	
	IWCORE.includeResourcesAndExecuteActions(resourcesToAdd, scriptsToEval);
}

function insertNodesToContainerBefore(component, container, before) {
	if (component == null || container == null || before == null) {
		return;
	}
	
	// Making copy
	var nodes = getTransformedDocumentToDom(component);
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	var scriptsToEval = new Array();
	var resourcesToAdd = new Array();
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode, scriptsToEval, resourcesToAdd);
		container.insertBefore(realNode, before);
	}
	
	IWCORE.includeResourcesAndExecuteActions(resourcesToAdd, scriptsToEval);
}

function insertNodesToContainer(component, container) {
	IWCORE.insertHtml(component, container);
}

IWCORE.insertHtml = function(html, container) {
	if (html == null || container == null) {
		return;
	}

	// Making copy
	var nodes = getTransformedDocumentToDom(html);

	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	var scriptsToEval = new Array();
	var resourcesToAdd = new Array();
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = IWCORE.createRealNode(activeNode, scriptsToEval, resourcesToAdd);
		container.appendChild(realNode);
	}

	IWCORE.includeResourcesAndExecuteActions(resourcesToAdd, scriptsToEval);
};
/** End **/

IWCORE.stopEventBubbling = function(event) {
	if (event) {
		if (event.stopPropagation) {
			event.stopPropagation();
		}
		event.cancelBubble = true;
	}
}

IWCORE.insertRenderedComponent = function(component, options) {
	if (component == null) {
		if (options.callback) {
			options.callback();
		}
					
		reloadPage();
		return false;
	}
	
	LazyLoader.loadMultiple(component.resources, function() {
		if (component.errorMessage != null) {
			try {
				humanMsg.displayMsg(component.errorMessage);
			} catch(e) {
				alert(component.errorMessage);
			}
			
			if (options.callback) {
				options.callback();
			}
			
			return false;
		}
		
		var parentContainer = null;
		if (typeof(options.container) == 'string' && options.container != '') {
			parentContainer = jQuery('#' + options.container)
		} else {
			parentContainer = jQuery(options.container);
		}
		if (parentContainer == null || parentContainer.length == 0) {
			if (options.callback) {
				options.callback();
			}
			
			return false;
		}

		try {
			var xmlEncoding = '<?xml version="1.0" encoding="UTF-8"?>';
			while (component.html.indexOf(xmlEncoding) != -1) {
				component.html = component.html.replace(xmlEncoding, '');
			}
			var commentOpener = '&lt;';
			while (component.html.indexOf(commentOpener) != -1) {
				component.html = component.html.replace(commentOpener, '<');
			}
			var commentCloser = '&gt;';
			while (component.html.indexOf(commentCloser) != -1) {
				component.html = component.html.replace(commentCloser, '>');
			}
			
			if (options.append) {
				parentContainer.append(jQuery(component.html));
			}
			else if (options.rewrite) {
				parentContainer.html(component.html);
			}
			else {
				parentContainer.replaceWith(jQuery(component.html));
			}
		} catch(e) {
			parentContainer.replaceWith(jQuery(component.html));
		}
		
		if (options.callback) {
			options.callback();
		}
	});
}

IWCORE.renderComponent = function(uuid, container, callback, properties, options) {
	LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/BuilderService.js'], function() {
		if (!properties) {
			properties = null;
		}
		
		BuilderService.getRenderedComponentById(uuid, window.location.pathname, properties, {
			callback: function(component) {
				if (!options) {
					options = {};
					options.replace = true;
				}
				options.container = container;
				options.callback = callback;
				
				IWCORE.insertRenderedComponent(component, options);
			},
			errorHandler: function() {
				closeAllLoadingMessages();
				if (IWCORE.askIfReloadPage) {
					if (window.confirm('Ooops... Some error occurred rendering component... We recommend to reload page. Do you agree?')) {
						reloadPage();
					}
				}
			},
			timeout: 60000
		});
	});
}

IWCORE.getRenderedComponentByClassName = function(options) {
	LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/BuilderService.js'], function() {
		BuilderService.getRenderedComponentByClassName(options.className, options.properties, {
			callback: function(component) {
				IWCORE.insertRenderedComponent(component, options);
			},
			errorHandler: function() {
				closeAllLoadingMessages();
				if (IWCORE.askIfReloadPage) {
					if (window.confirm('Ooops... Some error occurred rendering component... We recommend to reload page. Do you agree?')) {
						reloadPage();
					}
				}
			},
			timeout: 60000
		});
	});
}

IWCORE.addLoadedResources = function(uris) {
	if (uris == null) {
		return;
	}
	
	for (var i = 0; i < uris.length; i++) {
		if (!existsElementInArray(IdegaResourcesHandler, uris[i])) {
			IdegaResourcesHandler.push(uris[i]);
		}
	}
}

function getNeededElements(element, className) {
	if (element == null) {
		return null;
	}
	return getNeededElementsFromList(element.childNodes, className);
}

function getNeededElementsFromList(list, className) {
	if (list == null || className == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.className != null) {
				if (childElement.className == className) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function getNeededElementsFromListById(list, id) {
	if (list == null || id == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.id != null) {
				if (childElement.id == id) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

/**
 * Be sure mootools.js is added to page!
 */
function highlightElement(element, effectTime, endColor) {
	if (element == null || effectTime == null || endColor == null) {
		return;
	}
	// Highlight
	var fx = new Fx.Styles(element, {duration: effectTime, wait: false, transition: Fx.Transitions.Quad.easeOut});
	fx.start({'background-color': ['#fff36f', endColor]});
	
	//	Setting background color back
	var setStyleBack = function() {
		element.removeAttribute('style');
	}
	var id = window.setTimeout(setStyleBack, effectTime);
}

function getElementsByClassName(oElm, strTagName, strClassName) {
	var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
	var arrReturnElements = new Array();
	strClassName = strClassName.replace(/-/g, "\-");
	var oRegExp = new RegExp("(^|\s)" + strClassName + "(\s|$)");
	var oElement;
	for(var i=0; i<arrElements.length; i++){
		oElement = arrElements[i];
		if(oRegExp.test(oElement.className)){
			arrReturnElements.push(oElement);
		}
	}
	return (arrReturnElements)
}

function setActionsForRegion() {
	$$('div.regionLabel').each(
		function(element) {
    		var parentElement = element.parentNode;
			if (parentElement == null) {
				return;
			}
			var regionLabel = "";
			var inputs = element.getElementsByTagName("input");
			if (inputs != null) {
				if (inputs.length > 0) {
					var input = inputs[0];
					if (input.type != null) {
						if (input.type == "hidden") {
							regionLabel = input.value;
						}
					}
				}
			}
			
			parentElement.style.visibility = "hidden";
			
			parentElement.onmouseover = function() {
				showAddComponentImage(parentElement, element, regionLabel);
			},
			parentElement.onmouseout = function() {
				closeAddComponentContainer(element.id);
			}
    	}
    );
}

/**
 * example: old href: '/pages/' pass attribute (newHref): 'iw_language=en_EN' results in: '/pages/?iw_language=en_EN
 */
function changeWindowLocationHref(newHref) {
	changeWindowLocationHrefAndCheckParameters(newHref, false);
}
	
function changeWindowLocationHrefAndCheckParameters(newHref, keepOldParameters) {
	if (newHref.indexOf('login_state=logoff') != -1) {
		showLoadingMessage('');
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/WebUtil.js'], function() {
			WebUtil.logOut({
				callback: function(result) {
					if (jQuery != null) {
						jQuery('input[name="login_state"]').remove();
					}
					
					var uri = null;
					if (result == null) {
						var redirectUriIndex = newHref.indexOf('logoff_redirect_uri=');
						if (redirectUriIndex == -1) {
							uri = '/pages';
						} else {
							uri = newHref.substring(redirectUriIndex + 'logoff_redirect_uri='.length);
						}
					} else {
						uri = result;
					}
					if (uri == null || uri == '') {
						uri = '/pages';
					}
					window.location.href = uri;
					closeAllLoadingMessages();
				}
			});
		}, null);
		return false;
	}
	
	var oldLocation = '' + window.location.href;
	
	if (oldLocation.indexOf('#') != -1) {
		oldLocation = oldLocation.split('#')[0];
	}
	
	var separator = '?'
	var reloadingParam = 'reloading';
	//	Removing the same parameter (if such exists)
	if (oldLocation.indexOf(separator + reloadingParam) != -1) {
		oldLocation = oldLocation.split(separator + reloadingParam)[0];
	}
	else if (oldLocation.indexOf('&' + reloadingParam) != -1) {
		oldLocation = oldLocation.split('&' + reloadingParam)[0];
	}
	
	var existsAnyParameter = oldLocation.indexOf('/?') != -1;
	if (keepOldParameters) {
		if (existsAnyParameter) {
			separator = '&';
		}
	}
	else {
		if (existsAnyParameter) {
			oldLocation = oldLocation.split('/?')[0];	//	Removing ALL parameters
		}
	}
	
	if (oldLocation.lastIndexOf('/') == -1) {
		oldLocation += '/';
	}
	
	if(newHref.indexOf('#') == 0) {
		window.location.href = oldLocation + newHref;
	} else {
		window.location.href = oldLocation + separator + newHref;
	}
}

function reloadPage() {
	window.location.reload();
	//changeWindowLocationHrefAndCheckParameters('reloading=' + new Date().getTime(), true);	// changing href to be sure the page will be reloaded
}

function addActionForMoodalBoxOnCloseEvent(actionOnClose) {
	try {
		if (MOOdalBox) {
			MOOdalBox.addEventToCloseAction(actionOnClose);
		}
		else {
			reloadPage();
		}
	} catch(ex) {
		reloadPage();
	}
}

// This code was written by Tyler Akins and has been placed in the
// public domain. It would be nice if you left this header intact.
// Base64 code from Tyler Akins -- http://rumkin.com

var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

function encode64(input) {
	var output = "";
	var chr1, chr2, chr3;
	var enc1, enc2, enc3, enc4;
	var i = 0;

	do {
		chr1 = input.charCodeAt(i++);
		chr2 = input.charCodeAt(i++);
		chr3 = input.charCodeAt(i++);

		enc1 = chr1 >> 2;
		enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
		enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
		enc4 = chr3 & 63;

		if (isNaN(chr2)) {
			enc3 = enc4 = 64;
		} else if (isNaN(chr3)) {
			enc4 = 64;
		}

		output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) +
		keyStr.charAt(enc3) + keyStr.charAt(enc4);
	} while (i < input.length);

	return output;
}

function decode64(input) {
	var output = "";
	var chr1, chr2, chr3;
	var enc1, enc2, enc3, enc4;
	var i = 0;

	// remove all characters that are not A-Z, a-z, 0-9, +, /, or =
	input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

	do {
		enc1 = keyStr.indexOf(input.charAt(i++));
		enc2 = keyStr.indexOf(input.charAt(i++));
		enc3 = keyStr.indexOf(input.charAt(i++));
		enc4 = keyStr.indexOf(input.charAt(i++));

		chr1 = (enc1 << 2) | (enc2 >> 4);
		chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
		chr3 = ((enc3 & 3) << 6) | enc4;

		output = output + String.fromCharCode(chr1);

		if (enc3 != 64) {
			output = output + String.fromCharCode(chr2);
		}
		if (enc4 != 64) {
			output = output + String.fromCharCode(chr3);
		}
	} while (i < input.length);

	return output;
}

function existsElementInArray(array, element) {
	if (array == null || element == null) {
		return false;
	}
	for (var i = 0; i < array.length; i++) {
		if (element == array[i]) {
			return true;
		}
	}
	return false;
}

//	Requires MooTools!
function initToolTipForElement(element) {
	var tip = new Tips(element, {
		initialize: function() {
			this.fx = new Fx.Style(this.toolTip, 'opacity', {duration: 500, wait: false}).set(0);
		},
		onShow: function(toolTip) {
			this.fx.start(0.75);
		},
		onHide: function(toolTip) {
			this.fx.start(0);
		}
	});
}

String.prototype.wordWrap = function(m, b, c){
    var i, j, s, r = this.split("\n");
    if(m > 0) for(i in r){
        for(s = r[i], r[i] = ""; s.length > m;
            j = c ? m : (j = s.substr(0, m).match(/\S*$/)).input.length - j[0].length
            || m,
            r[i] += s.substr(0, j) + ((s = s.substr(j)).length ? b : "")
        );
        r[i] += s;
    }
    return r.join("\n");
};

/** Functions for Builder modules starts **/
function showAllComponentsLabels(element) {
	if (element == null) {
		return;
	}
	
	var children = getElementsByClassName(element, 'div', 'moduleName');
	if (children == null) {
		return;
	}
	var child = null;
	var elementsToHighlight = null;
	for (var i = 0; i < children.length; i++) {
		child = children[i];
		child.style.visibility = 'visible';
	}
}

function hideOldLabels(container) {
	if (container == null) {
		return;
	}
	var children = getElementsByClassName(container, 'div', 'moduleName');
	if (children == null) {
		return;
	}
	var element = null;
	for (var i = 0; i < children.length; i++) {
		element = children[i];
		element.style.visibility = 'hidden';
	}
}

function hideModuleContainerTop(element) {
	var list = getElementsByClassName(element, '*', 'moduleContainerTop');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = 'hidden';
}

function showModuleContainerTop(element) {
	var list = getElementsByClassName(element, '*', 'moduleContainerTop');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = 'visible';
}

function getFirstElementFromList(elements) {
	if (elements == null) {
		return null;
	}
	if (elements.length == 0) {
		return null;
	}
	return $(elements[0]);
}

function manageComponentInfoImageVisibility(element, styleProperty) {
	if (element == null) {
		return false;
	}
	
	var list = getElementsByClassName(element, '*', 'regionInfoImageContainer');
	var container = getFirstElementFromList(list);
	if (container == null) {
		return false;
	}
	
	container.style.visibility = styleProperty;
}
/** Functions for Builder modules ends **/

var DEFAULT_DWR_PATH = '/dwr';

function getDefaultDwrPath() {
	return DEFAULT_DWR_PATH;
}


function getDwrCallType(remote) {
	var dwrCallType = dwr.engine.XMLHttpRequest;
	if (!dwrCallType) {
		dwrCallType = dwr.engine.transport.xhr;
	}
	
	if (remote) {
		dwrCallType = dwr.engine.ScriptTag;
		if (!dwrCallType) {
			dwrCallType = dwr.engine.transport.scriptTag;
		}
		
		if (dwr.engine._remoteHandleCallback == null)
			dwr.engine._remoteHandleCallback = dwr.engine.remote.handleCallback;
	}
	
	return dwrCallType;
}

/**
 * This function 'prepares' DWR to make call to the very server that point path
 * @param interfaceClass - class where our method lives (like ThemesEngine)
 * @param path - server's path (like 'http://formbuilder.idega.is/dwr')
 */
function prepareDwr(interfaceClass, path) {
	if (interfaceClass == null || path == null) {
		return false;
	}
		
	interfaceClass._path = path;
}

String.prototype.cropEnd = function(symbols_count, string_to_append) {

	if(this.length <= symbols_count)
		return this;
		
	return this.substr(0, symbols_count)+string_to_append;
}

function isCorrectFileType(id, fileType, noFileMsg, invalidFileTypeMsg) {
	var input = document.getElementById(id);
	if (input) {
		if (input.value == '') {
			if (noFileMsg != null) {
				alert(noFileMsg);
			}
			return false;
		}
		var nameParts = input.value.split('.');
		if (nameParts) {
			var lastPart = nameParts[nameParts.length - 1];
			if (lastPart.toLowerCase() != fileType) {
				if (invalidFileTypeMsg != null) {
					alert(invalidFileTypeMsg + ': ' + input.value);
				}
				return false;
			}
		}
	}
	return true;
}

var LazyLoader = {};		//	Namespace
LazyLoader.timer = {};		//	Contains timers for resources
LazyLoader.resources = [];	//	Contains called resources references
LazyLoader.loading = false;
LazyLoader.loadMultiple = function(urls, callback, parameters) {
	try {
		if (urls == null || urls.length == 0) {
			LazyLoader.executeCallback(callback);
			return false;
		}
		
		if (typeof urls == 'string') {
			LazyLoader.load(urls, callback, parameters);
			return false;
		}
		
		var url = urls[0];
		if (url == null || url == '') {
			LazyLoader.executeCallback(callback);
			return false;
		}
		
		removeElementFromArray(urls, url);
		LazyLoader.load(url, urls.length == 0 ? callback : function() {
			LazyLoader.loadMultiple(urls, callback);
		}, parameters);
	} catch(e) {
		LazyLoader.loading = false;
		//console.log('ERROR in: LazyLoader.loadMultiple: ' + e.message);
	}
}

LazyLoader.load = function(url, callback, parameters) {
	try {
		if (url == null || url == '') {
			LazyLoader.executeCallback(callback);
			return false;
		}
		
		if (LazyLoader.loading) {
			var intervalId = 'loading_' + url + '_and_executing_' + callback;
			if (LazyLoader.timer[intervalId] != null) {
				window.clearInterval(LazyLoader.timer[intervalId]);
			}
			LazyLoader.timer[intervalId] = window.setInterval(function() {
				if (!LazyLoader.loading) {
					window.clearInterval(LazyLoader.timer[intervalId]);
					LazyLoader.doRealLoading(url, callback, parameters);
				}
			}, 100);
		}
		else {
			LazyLoader.doRealLoading(url, callback, parameters);
		}
	} catch(e) {
		LazyLoader.loading = false;
		//console.log('ERROR in: LazyLoader.load: ' + e.message);
	}
}

LazyLoader.doRealLoading = function(url, callback, parameters) {
	if (LazyLoader.loading == true) {
		//	Sending back to "synchronize"
		LazyLoader.load(url, callback, parameters);
		return false;
	}
	LazyLoader.loading = true;
	
	try {
		var foundRequiredResource = false;
		
		var loadedResources = LazyLoader.resources;
		if (loadedResources != null && loadedResources.length > 0) {
			for (var i = 0; (i < loadedResources.length && !foundRequiredResource); i++) {
				if (url == loadedResources[i] || loadedResources[i].indexOf(url) == 0) {
					foundRequiredResource = true;
				}
			}
		}
		
		var resourceEnd = url.substring(url.lastIndexOf('.') + 1);
		var isCSS = resourceEnd.toLowerCase() == 'css';
		
		if (!foundRequiredResource) {
			foundRequiredResource = LazyLoader.existsResourceInDocument(url, isCSS);
		}
		
		//	Make sure we only load once
		if (foundRequiredResource) {
			LazyLoader.executeCallback(callback);
		} else {
			//	Note that we loaded already
			LazyLoader.resources.push(url);
			
			//	To avoid caching (that possibly may do browser)
			if (url.indexOf("?") == -1) {
				url += '?LazyLoaderFlag=' + new Date().getTime();
			} else {
				url += '&LazyLoaderFlag=' + new Date().getTime();
			}

			var resource = null;
			if (isCSS) {
				resource = document.createElement('link');
				resource.href = url;
				resource.type = 'text/css';
				resource.rel = 'stylesheet';
				resource.media = 'all';
			}
			else {
				//	Will treat as JavaScript
				resource = document.createElement('script');
				resource.src = url;
				if (parameters) {
					if (parameters.classValue) {
						resource.setAttribute('class', parameters.classValue);
					}
				}
				else {
					resource.type = 'text/javascript';
				}
			}
			document.getElementsByTagName('head')[0].appendChild(resource);	//	Add resource tag to head element
			
			//	Was a callback requested?
			if (callback) {
				if (isCSS) {
					LazyLoader.executeCallback(callback);
				}
				else {
					//	IE
					resource.onreadystatechange = function () {
						if (resource.readyState == 'loaded' || resource.readyState == 'complete') {
							LazyLoader.executeCallback(callback);
						}
					}
					
					//	FF, Safari...
					resource.onload = function () {
						LazyLoader.executeCallback(callback);
					}
				}
			}
			
			if (callback == null) {
				LazyLoader.loading = false;
			}
		}
	} catch (e) {
		LazyLoader.loading = false;
	}
}

LazyLoader.existsResourceInDocument = function(url, isCSS) {
	if (existsElementInArray(IdegaResourcesHandler, url)) {
		return true;
	}
	
	if (LazyLoader.existsResourceInElement('head', url, isCSS)) {
		return true;
	}
	
	return LazyLoader.existsResourceInElement('body', url, isCSS)
}

LazyLoader.existsResourceInElement = function(elementTagName, url, isCSS) {
	var element = document.getElementsByTagName(elementTagName)[0];
	if (element == null) {
		return false;
	}
	var currentResources = null;
	try {
		currentResources = element.getElementsByTagName(isCSS ? 'link' : 'script');
	} catch(e) {
		return false;
	}
	if (currentResources == null || currentResources.length == 0) {
		return false;
	}
	
	var resourceUri = null;
	for (var i = 0; i < currentResources.length; i++) {
		var resourceElement = currentResources[i];
		resourceUri = resourceElement.getAttribute(isCSS ? 'href' : 'src');
		if (resourceUri != null && resourceUri != '') {
			if (url == resourceUri || resourceUri.indexOf(url) == 0) {
				return true;
			}
		}
	}
	return false;
}

LazyLoader.executeCallback = function(callback) {
	try {
		LazyLoader.loading = false;
		if (!callback) {
			return false;
		}
		callback();
	} catch(e) {
		LazyLoader.loading = false;
	}
}

IWCORE.getSelectedFromAdvancedProperties = function(handlerUsers) {
	
	if(handlerUsers != null) {
		
		for(var index = 0; index < handlerUsers.length; index++) {
			
			if(handlerUsers[index].selected)
                return handlerUsers[index].id;
		}
	}
	
	return null;
}

IWCORE.isNumericValue = function(value) {
	if (value == null || value.length == 0)
		return false;
	
	var validChars = '0123456789';
	var isNumber = true;
	var character = null;
	
	for (i = 0; i < value.length && isNumber; i++) { 
		character = value.charAt(i); 
		if (validChars.indexOf(character) == -1) {
			isNumber = false;
		}
	}
	
	return isNumber;
}

IWCORE.activeSessionPolling = function(sleepTime, checkSleepTime) {
	try {
		if (checkSleepTime) {
			if (!IWCORE.isNumericValue(sleepTime)) {
				return;
			}
		}
		
		var id = window.setTimeout(function() {
			IWCORE.pingServer(sleepTime, id);
		}, sleepTime);
	} catch (ex) {
		ex.reloadPage = true;
		IWCORE.sendExceptionNotification('Error activating session polling!', ex, null);
	}
}

IWCORE.pingServer = function(sleepTime, id) {
	try {
		window.clearTimeout(id);
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/PageSessionPoller.js'], function() {
			PageSessionPoller.pollSession(window.location.pathname, {
				callback: function(result) {
					if (result != null) {
						IWCORE.insertRenderedComponent(result, {
							container: document.body,
							append: true
						});
					}
					
					IWCORE.activeSessionPolling(sleepTime, false);
				},
				errorHandler: function(msg, exc) {
					if (exc == null) {
						exc = {};
					}
					exc.messageToClient = 'Most probably time out occurred while pinging server';
					exc.reloadPage = true;
					IWCORE.sendExceptionNotification(msg, exc, null);
					
					IWCORE.activeSessionPolling(sleepTime, false);
				},
				timeout: 500000
			});
		}, null);
	} catch (ex) {
		ex.reloadPage = false;
		IWCORE.sendExceptionNotification('Error pinging server!', ex, null);
	}
}

IWCORE.sendingErrorMail = false;
IWCORE.userDeniedToReloadPageOnError = false;
IWCORE.askIfReloadPage = true;
IWCORE.sendExceptionNotification = function(msg, ex, reloadPageMessage) {
	if (
			!IWCORE.sendingErrorMail &&
			msg != 'Internal Server Error' &&
			msg != 'Service Temporarily Unavailable' &&
			msg != 'Timeout' &&
			msg != 'Service Unavailable' &&
			msg != 'OK' &&
			msg != 'Incomplete reply from server'
	) {
		IWCORE.sendingErrorMail = true;
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/WebUtil.js'], function() {
			if (ex == null) {
				ex = 'Unknown error';
			}
			
			var mailMessage = 'Error: ' + msg + '\nException: ' + ex + '\nBrowser: ' + navigator.userAgent;
			if (ex.fileName) {
				mailMessage += '\nFile: ' + ex.fileName;
			}
			if (ex.lineNumber) {
				mailMessage += '\nLine number: ' + ex.lineNumber;
			} else if (ex.line) {
				mailMessage += '\nLine number: ' + ex.line;
			}
			if (ex.number) {
				mailMessage += '\nError number: ' + ex.number;
			}
			if (ex.url) {
				mailMessage += '\nURL: ' + ex.url;
			}
			if (ex.stack) {
				mailMessage += '\nStack: ' + ex.stack;
			}
			if (ex.javaClassName) {
				mailMessage += '\nJava class name: ' + ex.javaClassName;
			}
			if (ex.message) {
				mailMessage += '\nMessage: ' + ex.message;
			}
			if (ex.cause) {
				mailMessage += '\nCause: ' + ex.cause;
			}
			
			WebUtil.sendEmail(null, null, '[JavaScript error] ERROR on: ' + window.location.href, mailMessage, {
				callback: function(data) {
					IWCORE.sendingErrorMail = false;
				}, errorHandler: function(tmpMsg, tmpEx) {
					IWCORE.sendingErrorMail = false;
				}
			});
		}, null);
	}
	
	if (ex != null && ex.messageToClient != null && ex.reloadPage) {
		if (IWCORE.askIfReloadPage && !IWCORE.userDeniedToReloadPageOnError) {
			if (reloadPageMessage == null) {
				reloadPageMessage = 'Sorry, some error occurred... We strongly recommend to reload the page. Do you agree?';
			}
			if (window.confirm(reloadPageMessage)) {
				reloadPage();
				return false;
			} else {
				IWCORE.userDeniedToReloadPageOnError = true;
			}
		}
	}
	
	return false;
}

String.prototype.trim = function () {
	return this.replace(/^\s*/, '').replace(/\s*$/, '');
}

IWCORE.setSelectionRange = function(input, selectionStart, selectionEnd) {
	if (input.setSelectionRange) {
		input.focus();
		input.setSelectionRange(selectionStart, selectionEnd);
	} else if (input.createTextRange) {
		var range = input.createTextRange();
		range.collapse(true);
		range.moveEnd('character', selectionEnd);
		range.moveStart('character', selectionStart);
		range.select();
	}
}

IWCORE.setCaretToPos = function(input, pos) {
	IWCORE.setSelectionRange(input, pos, pos);
}

function moveOptionElementUp(element, id) {
	var newPos = jQuery('#' + id + ' option').index(element) - 1;
	if (newPos > -1) {
		jQuery('#' + id + ' option').eq(newPos).before("<option value='"+element.val()+"' selected='selected'>"+element.text()+"</option>");
		element.remove();
	}
}
function moveOptionElementDown(element, id) {
	var countOptions = jQuery('#' + id + ' option').size();
	var newPos = jQuery('#' + id + ' option').index(element) + 1;
	if (newPos < countOptions) {
    	jQuery('#' + id + ' option').eq(newPos).after("<option value='"+element.val()+"' selected='selected'>"+element.text()+"</option>");
        element.remove();
    }
}

if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}
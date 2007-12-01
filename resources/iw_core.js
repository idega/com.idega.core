//***********************************************************//
//
// idegaWeb Core Javascript Function library
// (c) Copyright idega hf. 2000-2006 All rights reserved.
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

var IE = document.all?true:false; 

var SITE_INFO_KEYWORD_FROM_BOX = null;
var OLD_APPLICATION_PROPERTY = null;
var CLICKED_ON_PROPERTY = false;

var PARENT_ELEMENT = null;
var CHILD_ELEMENT = null;
var CHANGED_CHILDREN_LIST = null;
var ORIGINAL_ELEMENT_INDEX = 0;

var APPLICATION_PROPERTY = "application_property";
var EDIT_BOX_ID = "changeSiteInfoBox";


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


window.onload=function(){tableruler();}

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

	function showLoadingMessage(sLoadingText){
	
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

function closeAllLoadingMessages() {
	var bodyArray = document.getElementsByTagName('body');
	var bodyTag = bodyArray[0];
	var layers = getElementsByClassName(bodyTag, '*', 'LoadLayer');
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

/** Application Property logic begins **/
var SAVING_SITE_INFO_VALUE_MESSAGE_TEXT = "Saving...";
function changeSiteInfo(id, savingSiteInfoValueMessageText) {
	if (id == null) {
		return;
	}
	if (savingSiteInfoValueMessageText != null) {
		SAVING_SITE_INFO_VALUE_MESSAGE_TEXT = savingSiteInfoValueMessageText;
	}
	CLICKED_ON_PROPERTY = true;
	document.onclick = showSiteInfoValue;
	changeSiteInfoValue(id);
}

function changeSiteInfoValue(id) {
	if (id == null) {
		return;
	}
	showSiteInfoValue();
	SITE_INFO_KEYWORD_FROM_BOX = id;
	
	var element = $(id);
	if (element == null) {
		return;
	}
	CHILD_ELEMENT = element;
	
	var editBox = $(EDIT_BOX_ID);
	if (editBox == null) {
		editBox = new Element("input");
		editBox.setAttribute("type", "input");
		editBox.setAttribute("id", EDIT_BOX_ID);
		editBox.addEvents({
			'keyup': function(e) {
				e = new Event(e);
				saveSiteInfoValueWithEnter(e);
				e.stop();
			},
			'blur': function(e) {
				e = new Event(e);
				saveSiteInfoValueWithBlur(e);
				e.stop();
			}
		});
	}
	else {
		editBox.value = "";
		editBox.style.display = "inline";
		var parentNode = editBox.parentNode;
		if (parentNode != null) {
			parentNode.removeChild(editBox);
		}
	}
	
	PARENT_ELEMENT = CHILD_ELEMENT.parentNode;
	ORIGINAL_ELEMENT_INDEX = findChildIndex(PARENT_ELEMENT, CHILD_ELEMENT);
	
	var children = CHILD_ELEMENT.childNodes;
	if (children != null) {
		if (children[0] != null) {
			if (children[0].nodeValue != null) {
				editBox.value = children[0].nodeValue;
			}
		}
	}
	
	if (element.getAttribute(APPLICATION_PROPERTY) == null) {
		element.setAttribute(APPLICATION_PROPERTY, true);
	}

	appendEditBoxToExactPlace(element, editBox);
	editBox.focus();
	editBox.select();
	
	if (PARENT_ELEMENT == null) {
		element.style.visibility = "hidden";
	}
	else {
		PARENT_ELEMENT.removeChild(CHILD_ELEMENT);
		CHANGED_CHILDREN_LIST = new Array();
		for (var i = 0; i < PARENT_ELEMENT.childNodes.length; i++) {
			CHANGED_CHILDREN_LIST.push(PARENT_ELEMENT.childNodes[i]);
		}
	}
}


function appendEditBoxToExactPlace(element, edit) {
	if (element == null || edit == null) {
		return;
	}
	var container = null;
	if (element.tagName != null) {
		if ((element.tagName != "div" && element.tagName != "DIV") && (element.tagName != "img" && element.tagName != "IMG")) {
			container = document.createElement(element.tagName);
			container.setAttribute("id", EDIT_BOX_ID + "_container");
			container.appendChild(edit);
		}
	}
	var parentNode = element.parentNode;
	if (parentNode != null) {
		if (container != null) {
			parentNode.insertBefore(container, element);
		}
		else {
			parentNode.insertBefore(edit, element);
		}
	}
	else {
		if (container != null) {
			element.appendChild(container);
		}
		else {
			element.appendChild(edit);
		}
	}
}

function saveSiteInfoValueWithBlur(event) {
	if (event == null) {
		return false;
	}
	if (event.type == "blur" || event.type == "onblur") {
		mainSaveSiteInfo($(EDIT_BOX_ID).value);
	}
}

function saveSiteInfoValueWithEnter(event) {
	if (event == null) {
		return false;
	}
	
	if (event.key) {
		if ('enter' == event.key) {
			mainSaveSiteInfo($(EDIT_BOX_ID).value);
		}
	}
	return false;
}

function mainSaveSiteInfo(value) {
	if (SITE_INFO_KEYWORD_FROM_BOX == null || value == null) {
		return;
	}
	
	if (PARENT_ELEMENT == null) {
		var element = document.getElementById(SITE_INFO_KEYWORD_FROM_BOX); // Setting new value
		setElementNewValue(element, value);
	}
	else {
		setElementNewValue(CHILD_ELEMENT, value);
	}
	
	showLoadingMessage(SAVING_SITE_INFO_VALUE_MESSAGE_TEXT);
	ThemesEngine.saveSiteInfoValue(SITE_INFO_KEYWORD_FROM_BOX, value, saveSiteInfoValueCallback);
}

function saveSiteInfoValueCallback(result) {
	closeAllLoadingMessages();
	showSiteInfoValue();
}

function showSiteInfoValue() {
	var editBox = $(EDIT_BOX_ID);
	if (editBox != null) {
		var container = editBox.parentNode;
		if (container != null) {
			container.removeChild(editBox);
			if (container.id != null) {
				if (container.id == EDIT_BOX_ID + "_container") {
					var parentContainer = container.parentNode;
					if (parentContainer != null) {
						parentContainer.removeChild(container);
					}
				}
			}
		}
		else {
			editBox.style.display = "none";
		}
	}
	
	if (SITE_INFO_KEYWORD_FROM_BOX == null) {
		return;
	}
	
	if (PARENT_ELEMENT == null) {
		var element = $(SITE_INFO_KEYWORD_FROM_BOX);
		if (element != null) {
			if (element.style.visibility == "hidden") {
				CLICKED_ON_PROPERTY = false;
				element.style.visibility = "visible";
			}
		}
	}
	else {
		CLICKED_ON_PROPERTY = false;
		appendChildrenToExactPlace();
		PARENT_ELEMENT = null;
		CHILD_ELEMENT = null;
		CHANGED_CHILDREN_LIST = null;
	}
}

function findChildIndex(parentNode, element) {
	if (parentNode == null || element == null) {
		return -1;
	}
	var children = parentNode.childNodes;
	if (children == null) {
		return -1;
	}
	var i = 0;
	for (i = 0; i < children.length; i++) {
		if (element == children[i]) {
			return i;
		}
	}
	return i - 1;
}

function appendChildrenToExactPlace() {
	if (PARENT_ELEMENT == null || CHILD_ELEMENT == null || CHANGED_CHILDREN_LIST == null) {
		return;
	}
	var children = PARENT_ELEMENT.childNodes;
	if (children == null) {
		return;
	}
	if (ORIGINAL_ELEMENT_INDEX < 0) {
		ORIGINAL_ELEMENT_INDEX = 0;
	}
	var newChildrenList = new Array();
	for (var i = 0; i < CHANGED_CHILDREN_LIST.length; i++) { // Making copy of exact current structure
		newChildrenList.push(CHANGED_CHILDREN_LIST[i]);
	}
	newChildrenList.splice(ORIGINAL_ELEMENT_INDEX, 1, CHILD_ELEMENT); // Adding original element with new value
	for (var i = 0; i < children.length; i++) { // Removing old children
		PARENT_ELEMENT.removeChild(children[i]);
	}
	for (var i = 0; i < newChildrenList.length; i++) { // Appending new children
		PARENT_ELEMENT.appendChild(newChildrenList[i]);
	}
}

function setElementNewValue(element, value) {
	if (element == null || value == null) {
		return;
	}
	if (element.value != null) {
		element.value = value;
	}
	else {
		var children = element.childNodes;
		if (children != null) {
			for (var j = 0; j < children.length; j++) {
				element.removeChild(children[j]);
			}				
		}
		if (value == "               " || value == "" || value == " ") {
			for (var i = 0; i < 15; i++) {
				element.appendChild(document.createTextNode("\u00A0"));
			}
		}
		else {
			element.appendChild(document.createTextNode(value));
		}
	}
}
/** Application Property logic ends **/

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

window.onload = function() {

    IWCORE.includedScripts.load();
}

IWCORE.includedScripts = {

    load: function() {
    
        var scriptElements = document.getElementsByTagName("script");
        var scripts = new Array();
        
        if(scriptElements != null)
            for(var idx = 0; idx < scriptElements.length; idx++)
                scripts.push(scriptElements[idx].getAttribute('src'));

                
        IWCORE.includedScripts.includedScripts = scripts;
        return IWCORE.includedScripts.includedScripts;
    },
    get: function() {
    
	   if(IWCORE.includedScripts.includedScripts == null)
	       IWCORE.includedScripts.includedScripts = IWCORE.includedScripts.load();
       
       return IWCORE.includedScripts.includedScripts;
    },
    
    add: function(scriptPath) {
    
        var scripts = IWCORE.includedScripts.get();
        
        if (!IWCORE.inArray(scriptPath, scripts))
            scripts.push(scriptPath);
    },
    
    includedScripts: null,
    
    includeScript: function(scriptPath) {
    
        var scripts = IWCORE.includedScripts.get();
        
        if (!IWCORE.inArray(scriptPath, scripts)) {
            scripts.push(scriptPath);
            IWCORE.includedScripts.includeJs(scriptPath);
        }
    },
    
    includeJs: function(scriptPath) {
    
        var js = document.createElement('script');
        js.setAttribute('language', 'javascript');
        js.setAttribute('type', 'text/javascript');
        js.setAttribute('src', scriptPath);
        document.getElementsByTagName('head').item(0).appendChild(js);
    }
}

IWCORE.includeScript = function(scriptPath) {

    return IWCORE.includedScripts.includeScript(scriptPath);  
}

IWCORE.inArray = function(needle, haystack) {
    for (var i = 0; i < haystack.length; i++) {
        if (haystack[i] == needle) {
            return true;
        }
    }
    return false;
}

/* !!    REQUIRES MOOTOOLS      !!
*/
IWCORE.includeScriptBatch = function(orderedScriptPathes) {

    if(orderedScriptPathes == null)
        return;
        
    orderedScriptPathes = orderedScriptPathes.reverse();
    
    IWCORE.includeScriptBatch.addJs(orderedScriptPathes);
}

IWCORE.includeScriptBatch.addJs = function(pathes) {

    var path = pathes.pop();
    IWCORE.includedScripts.add(path);
    
    if(pathes.length == 0) {
        new Asset.javascript(path, {onload: function(){ }});
    } else {
        new Asset.javascript(path, {
            onload: function() {
                        IWCORE.includeScriptBatch.addJs(pathes);
                    }
            }
        );
    }
};

/**
 * linkToFeed: e.g. http://www.idega.com/rss/articles.xml
 * feedType: e.g. "atom" or "rss"
 * feedTitle: e.g. "Atom 1.0" or "RSS 2.0"
 */
function addFeedSymbolInHeader(linkToFeed, feedType, feedTitle) {
	if (linkToFeed == null || feedType == null || feedTitle == null) {
		return;
	}
	//check if such feed already has been added
	var headElement = document.getElementsByTagName("head")[0];
	var alreadyAdded = false;
	for (var i = 0; i < headElement.getElementsByTagName("link").length; i++){
		var linkElement = headElement.getElementsByTagName("link")[i];
		if (linkElement.getAttribute("href") == linkToFeed)
			return;
	}
	var linkToAtomInHeader = document.createElement("link");
	linkToAtomInHeader.setAttribute("href", linkToFeed);
	linkToAtomInHeader.setAttribute("title", feedTitle);
	linkToAtomInHeader.setAttribute("type", "application/"+feedType+"+xml");
	linkToAtomInHeader.setAttribute("rel", "alternate");
	document.getElementsByTagName("head")[0].appendChild(linkToAtomInHeader);
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

function createRealNode(element) {
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
    
    //  javascript cnode
    if (element.nodeName == '#cdata-section' && element.parentNode.nodeName == 'script') {
    
    var value = element.nodeValue;
    
    if(value.indexOf('prototype') == -1 && value.indexOf('scriptac') == -1) {
    
        window.eval(element.nodeValue);    
    }
        
        return document.createTextNode('');
    }
	
	//	Script
	if (element.nodeName == 'script' && IE) {
		if (element.nodeValue != null && element.nodeValue != '') {
			var action = '' + element.nodeValue;
			if (action.indexOf('<!--') == -1 && action.indexOf('//-->') == -1) {
				window.eval(action);
			}
		}
		
		if (element.attributes != null) {
			for (var i = 0; i < element.attributes.length; i++) {
				var attribute = element.attributes[i];
				if (attribute.nodeName == 'src') {
					insertJavaScriptFileToHeader(attribute.nodeValue);	//	Adding source file
				}
			}
		}
		
		if (element.childNodes != null) {
			var allActions = '';
			for (var i = 0; i < element.childNodes.length; i++) {
				var scriptNodeValue = element.childNodes[i].nodeValue;
				if (scriptNodeValue.indexOf('<!--') == -1 && scriptNodeValue.indexOf('//-->') == -1) {
					allActions += scriptNodeValue;
				}
			}
			if (allActions != null && allActions != '') {
				window.eval(allActions);	//	Executing script
			}
		}
		
		return document.createElement('script');	//	Fake, all actions are executed already
	}
	
	// Element
	var result = document.createElement(element.nodeName);
	if (element.attributes != null) {
		for (var i = 0; i < element.attributes.length; i++) {
			var attribute = element.attributes[i];
			if (attribute.nodeName == 'class' && IE) {
				result.className = attribute.nodeValue;
			}
			else if (attribute.nodeName.indexOf('on') == 0 && IE) {
				var event = attribute.nodeName.substring(attribute.nodeName.indexOf('on') + 2);
				var functionCall = attribute.nodeValue;
				var elementFunction = function() {
					window.eval(functionCall);
				};
				registerEvent(result, event, elementFunction);
			}
			else if (attribute.nodeName == 'checked' && IE) {
				var isChecked = attribute.nodeValue == 'true';
				if (isChecked) {
					if (result.type != null && result.type != '') {
						result = document.createElement('<input type="'+result.type+'" name="'+element.nodeName+'" checked>');
					}
					else {
						result = document.createElement('<input name="'+element.nodeName+'" checked>');
					}
				}
			}
			else if (attribute.nodeName == 'style' && IE) {
				var styleValue = attribute.nodeValue;
				while(true){
					var stylePropertyNameLength = styleValue.indexOf(':');
					if(stylePropertyNameLength == -1){
						break;
					}
					var stylePropertyName = styleValue.substring(0, stylePropertyNameLength);
					styleValue = styleValue.substring(stylePropertyNameLength+1);
					var stylePropertyValueLength = styleValue.indexOf(';');
					if(stylePropertyValueLength == -1){
						$(result).setStyle(stylePropertyName, styleValue);
						break;
					}
					var stylePropertyValue = styleValue.substring(0, stylePropertyValueLength);
					styleValue = styleValue.substring(stylePropertyValueLength+2);
				
					$(result).setStyle(stylePropertyName, stylePropertyValue);
				}
			}
			else if (attribute.nodeName == 'href') {
				var hrefValue = attribute.nodeValue;
				while (hrefValue.indexOf('&#38;') != -1) {
					hrefValue = hrefValue.replace('&#38;', '&');
				}
				
				result.setAttribute(attribute.nodeName, hrefValue);
			}
			else if (attribute.nodeName == 'src' && element.nodeName == 'script') {
				insertJavaScriptFileToHeader(attribute.nodeValue);	//	Adding source file
			}
			else {
				result.setAttribute(attribute.nodeName, attribute.nodeValue);
			}
		}
	}
	
	if (element.childNodes != null) {
		for (var j = 0; j < element.childNodes.length; j++) {
			result.appendChild(createRealNode(element.childNodes[j]));
		}
	}
	
	return result;
}

function getStyleAttributes(styleValue){
	var styleAttributes = new Array();
	for(key in styleAttributes){
		delete styleAttributes[key];
	}
	while(true){
		var stylePropertyNameLength = styleValue.indexOf(':');
		if(stylePropertyNameLength == -1){
			break;
		}
		var stylePropertyName = styleValue.substring(0, stylePropertyNameLength);
		styleValue = styleValue.substring(stylePropertyNameLength+1);
		var stylePropertyValueLength = styleValue.indexOf(';');
		if(stylePropertyValueLength == -1){
			break;
		}
		var stylePropertyValue = styleValue.substring(0, stylePropertyValueLength);
		styleValue = styleValue.substring(stylePropertyValueLength+2);

		styleAttributes[stylePropertyName.toString()] = stylePropertyValue;
		
	}	
	return styleAttributes;
}

function replaceNode(component, nodeToReplace, container) {
	if (component == null || nodeToReplace == null) {
		return;
	}
	
	var nodes = getTransformedDocumentToDom(component);
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode);
		container.replaceChild(realNode, nodeToReplace);
	}
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
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode);
		container.insertBefore(realNode, before);
	}
}

function insertNodesToContainer(component, container) {
	if (component == null || container == null) {
		return;
	}
	
	// Making copy
	var nodes = getTransformedDocumentToDom(component);
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode);
		container.appendChild(realNode);
	}
}
/** End **/

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

function reloadPage() {
	var oldLocation = '' + window.location.href;
	
	if (oldLocation.indexOf('#') != -1) {
		oldLocation = oldLocation.split('#')[0];
	}
	
	var separator = '?'
	var reloadingParam = 'reloading';
	if (oldLocation.indexOf(separator + reloadingParam) != -1) {
		oldLocation = oldLocation.split(separator + reloadingParam)[0];
	}
	else if (oldLocation.indexOf('&' + reloadingParam) != -1) {
		oldLocation = oldLocation.split('&' + reloadingParam)[0];
	}
	
	
	if (oldLocation.indexOf(separator) != -1) {
		separator = '&';
	}
	
	if (oldLocation.lastIndexOf('/') == -1) {
		oldLocation += '/';
	}
	
	window.location.href = oldLocation  + separator + reloadingParam + '=' + new Date().getTime();	// changing href to be sure the page will be reloaded
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
	hideOldLabels();
	
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
/** Functions for Builder modules ends **/

var DEFAULT_DWR_PATH = '/dwr';

function getDefaultDwrPath() {
	return DEFAULT_DWR_PATH;
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
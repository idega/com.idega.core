//************************************************//
//
// idegaWeb Core Javascript Function library
// (c) Copyright idega hf. 2000-2005 All rights reserved.
//
// 	This file contains proprietary information owned
// 	by idega and may not be copied without prior concent.
//
//************************************************//



//************************************************//
//
//Begin Generic Functions
//
//************************************************//

function iwOpenWindow(Address,Name,ToolBar,Location,Directories,Status,Menubar,Titlebar,Scrollbars,Resizable,Width,Height) {  

  // usage openwindow(addr,name,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,yes/no,width,height) 
var option = "toolbar=" + ToolBar 
+ ",location=" + Location  
+ ",directories=" + Directories  
+ ",status=" + Status  
+ ",menubar=" + Menubar  
+ ",titlebar=" + Titlebar  
+ ",scrollbars=" + Scrollbars  
+ ",resizable="  + Resizable  
+ ",width=" + Width  
+ ",height=" + Height; 
var new_win = window.open(Address, Name, option );
new_win.focus();
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

//************************************************//
//
// End Windowing and iframe methods
//
//************************************************//
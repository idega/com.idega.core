//************************************************//
//
// idegaWeb Core Javascript Function library
// (c) Copyright idega hf. 2000-2002 All rights reserved.
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

function findObj(n, d){var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {  d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=findObj(n,d.layers[i].document); return x;}

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

	var previousDayLength = dayInput.options.length;
	timeA = new Date(yearValue,monthValue,1);
	
	//timeA = new Date(yearInput.options[yearInput.selectedIndex].text, monthInput.options[monthInput.selectedIndex].value,1);
	timeDifference = timeA - 86400000;
	timeB = new Date(timeDifference);
	
	var oldSelectedDay = dayInput.selectedIndex;
	var daysInMonth = timeB.getDate();
	
	if(previousDayLength>=2){
		for (var i = 1; i < previousDayLength ; i++) {
			dayInput.options[i] = null;
		}
	}
	//dayInput.options[0] = new Option(dayDisplayString,dayDisplayString);
	
	for (var i = 1; i <= daysInMonth; i++) {

			if (i<10){
				dayInput.options[i] = new Option(i,'0'+i);
			}
			else{
				dayInput.options[i] = new Option(i,i);
			}
	}

	dayInput.options[oldSelectedDay].selected = true;
	
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
<?php

include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");


htmlTop("Tabelle bearbeiten");
?>
<?php print STYLESHEET ?>

<SCRIPT LANGUAGE=JavaScript>
function IsDigit()
{
  return ((event.keyCode >= 48) && (event.keyCode <= 57))
}
function IsDigitPercent()
{
  return ( ((event.keyCode >= 48) && (event.keyCode <= 57)) || event.keyCode == 37 )
}
function getAttrib(html,attrib){
	var re = new RegExp("^.*"+attrib+"=\"([^\"]+)\".*$","i");
	if(html.search(re) != -1){
		return html.replace(re,"$1");
	}else{
		re = new RegExp("^.*"+attrib+"=([^ ]+).*$","i");
		if(html.search(re) != -1){
			return html.replace(re,"$1");
		}else{
			return "";
		}
	}
}
function mA(attribname,value){
	return (value != "") ? ' '+attribname+'='+value : '';
}
function changeBgColor(obj){
	var args = new Array();
	args["bgcolor"] = obj.style.background;
	var arr = showModalDialog( "selectcolor.php",args,"font-family:Verdana; font-size:12; dialogWidth:420px; dialogHeight:380px" );
	if (arr != null) {
		obj.style.background = arr;
	}
}
function onOk(){
  window.returnValue = mA("cellPadding",Cellpadding.value)+mA("cellSpacing",Cellspacing.value)+mA("border",Border.value)+mA("width",Width.value)+mA("height",Height.value)+mA("bgColor",bgcolor.style.background)+mA("align",Alignment.value);
  window.close();
}
</SCRIPT>

<SCRIPT LANGUAGE=JavaScript FOR=window EVENT=onload>
  for ( elem in window.dialogArguments )
  {
    switch( elem )
    {
    case "TableAttrs":
    	var attribs =  window.dialogArguments["TableAttrs"];
    	
    	var bc = getAttrib(attribs,"bgcolor");
    	bc = (bc != "none") ? bc : "";
    	
    	if(bc) bgcolor.style.background = bc;
    	
    	foo = getAttrib(attribs,"cellpadding");
    	Cellpadding.value = (foo == "") ? "" : foo;
   		foo = getAttrib(attribs,"cellspacing");
    	Cellspacing.value = (foo == "") ? "" : foo;
   		foo = getAttrib(attribs,"border");
    	Border.value = (foo == "") ? "" : foo;
   		foo = getAttrib(attribs,"width");
    	Width.value = (foo == "") ? "" : foo;
   		foo = getAttrib(attribs,"height");
    	Height.value = (foo == "") ? "" : foo;
   		foo = getAttrib(attribs,"align");
    	Alignment.value = (foo == "") ? "" : foo;

      break;
    }
  }
</SCRIPT>

</HEAD>

<BODY marginwidth="16" marginheight="16" leftmargin="16" topmargin="16">

<?php
	$foo = '<INPUT ID=Width TYPE=TEXT SIZE=3 NAME=Width ONKEYPRESS="event.returnValue=IsDigitPercent();" class="defaultfont">';
	$breite = htmlFormElementTable($foo,"Breite");
	
	$foo = '<INPUT ID=Height TYPE=TEXT SIZE=3 NAME=Height ONKEYPRESS="event.returnValue=IsDigitPercent();" class="defaultfont">';
	$hoehe = htmlFormElementTable($foo,"H&ouml;he");
	
	$firstTable = '<table border="0" cellpadding="0" cellspacing="0">
<tr><td>'.$breite.'</td><td>'.$hoehe.'</td><td></td><td></td></tr>
<tr><td>'.getPixel(100,2).'</td><td>'.getPixel(100,2).'</td><td>'.getPixel(100,2).'</td><td>'.getPixel(80,2).'</td></tr>
</table>
';
	

	$foo = '<INPUT ID=Border TYPE=TEXT SIZE=3 NAME=Border ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$rand = htmlFormElementTable($foo,"Rand");

	$foo = '<DIV ID="bgcolor" onClick="changeBgColor(this)" STYLE="width:24;height:20;cursor:hand;BORDER-BOTTOM:gainsboro solid 2px;BORDER-LEFT:buttonshadow solid 2px;BORDER-RIGHT:gainsboro solid 2px;BORDER-TOP:buttonshadow solid 2px;"></DIV>';
	$farbe = htmlFormElementTable($foo,"Farbe");

	$foo = '<INPUT ID=Cellpadding TYPE=TEXT SIZE=3 NAME=Cellpadding ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$cellpad = htmlFormElementTable($foo,"Innenabstand");

	$foo = '<INPUT ID=Cellspacing TYPE=TEXT SIZE=3 NAME=Cellspacing ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$cellspace = htmlFormElementTable($foo,"Zellabstand");

	$foo = '<select class="defaultfont" id="Alignment" name="Alignment" size="1">
	<option value="" selected>Default
	<option value="left">Left
	<option value="right">Right
</select>
';
	$ausrichtung = htmlFormElementTable($foo,"Ausrichtung");



	$buttons = gifButton("abbrechen","",WE_LANGUAGE,"",92,18,"window.close()").
	getPixel(10,2).gifButton("ok","","","",92,18,"onOk()");
	
	$secondTable = '<table border="0" cellpadding="0" cellspacing="0">
<tr><td>'.$rand.'</td><td>'.$farbe.'</td><td>'.$cellpad.'</td><td>'.$cellspace.'</td></tr>
<tr><td>'.getPixel(100,4).'</td><td>'.getPixel(100,4).'</td><td>'.getPixel(100,4).'</td><td>'.getPixel(80,4).'</td></tr>
<tr><td colspan="4">'.$ausrichtung.'</td></tr>
</table>
';

	$table = '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.$firstTable.'</td>
	</tr>
	<tr>
		<td>'.getPixel(2,4).'</td>
	</tr>
	<tr>
		<td>'.$secondTable.'</td>
	</tr>
</table>
';
	print '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.htmlDialogBorder2(350,50,$table,"Tabelle bearbeiten").'</td>
	</tr>
	<tr>
		<td>'.getPixel(2,10).'</td>
	</tr>
	<tr>
		<td>'.$buttons.'</td>
	</tr>
</table>
';
?>


</BODY>
</HTML>
<?php


include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");


htmlTop("Bild bearbeiten");
?>
<?php print STYLESHEET ?>

<script language="JavaScript" src="<?php print JS_DIR ?>windows.js"></script>

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
	var re = new RegExp("<[^>]*"+attrib+"=\"?([^\" >]*)[^>]*>$");
	if(html.search(re) != -1){
		return html.replace(re,"$1");
	}else{
		return "";
	}
}

function we_cmd(){
	var args = "";
	var url = "<?php print WEBEDITION_DIR; ?>we_cmd.php?"; for(var i = 0; i < arguments.length; i++){ url += "we_cmd["+i+"]="+escape(arguments[i]); if(i < (arguments.length - 1)){ url += "&"; }}
	switch (arguments[0]){
    case "openImageEditor":
            url=url+"&we_cmd[6]=imgProp"+"&ses=<?php print session_id()?>";            
			new jsWindow(url,"we_dirChooser",80,80,750,400,true,true);            
			break;
	}
}
function mA(attribname,value){
	return (value != "") ? ' '+attribname+'="'+value+'"' : '';
}
function browse(){
	alert ("You must define a browse function!");
}
function upload(){
	alert ("You must define a upload function!");
}
function onOk(){
  var f = document.forms[0];
  var src = f.Source.value;
  var go=true;
  
  if(src=="")
   if(!confirm("<?php print $l_alert["empty_image_to_save"];?>")) go=false;

  if(go){
            html = '<IMG SRC="'+src+'"'+
			mA("WIDTH",f.Width.value)+
			mA("HEIGHT",f.Height.value)+
			mA("HSPACE",f.HSpace.value)+
			mA("VSPACE",f.VSpace.value)+
			mA("BORDER",f.Border.value)+
			mA("ALT",f.Alt.value)+
			mA("ALIGN",f.Alignment.value)+
			mA("NAME",f.Name.value)+'>';
	window.returnValue = html;
	window.close();
  }
}

function makeNewEntry(icon,id,pid,txt,offen,ct,tab){    
    window.dialogArguments["opener"].makeNewEntry(icon,id,pid,txt,offen,ct,tab);
    
}
function updateEntry(id,text,pid,tab){    
    window.dialogArguments["opener"].updateEntry(id,text,pid,tab);
    
}


</SCRIPT>

<SCRIPT LANGUAGE=JavaScript FOR=window EVENT=onload>
var f = document.forms[0];
var html = window.dialogArguments["html"];
window.BaseUrl = window.dialogArguments["BaseUrl"];
f.Source.value = getAttrib(html,"src");
f.Width.value = getAttrib(html,"width");
f.Height.value = getAttrib(html,"height");
f.HSpace.value = getAttrib(html,"hspace");
f.VSpace.value = getAttrib(html,"vspace");
f.Border.value = getAttrib(html,"border");
f.Alt.value = getAttrib(html,"alt");
f.Alignment.value = getAttrib(html,"align").toLowerCase();
f.Name.value = getAttrib(html,"name");

</SCRIPT>


</HEAD>

<BODY marginwidth="16" marginheight="16" leftmargin="16" topmargin="16">
<form name="we_form" onSubmit="return false;">
<?php
	#$button = gifButton("auswaehlen","",WE_LANGUAGE,"",92,18,"we_cmd('openImageEditor','foo','Source','');");
    $button = gifButton("auswaehlen","",WE_LANGUAGE,"",92,18,"we_cmd('openImageEditor','document.forms[\\'we_form\\'].elements[\\'foo\\'].value','document.forms[\\'we_form\\'].elements[\\'Source\\'].value','image/*',document.forms['we_form'].elements['Source'].value,'tblFile');");
	$foo = '<input type="hidden" name="foo" value=""><INPUT style="width:340px" TYPE="TEXT" ID="Source" NAME="Source" SIZE="29" class="defaultfont">';
	$imageURL = htmlFormElementTable($foo,"Bild URL","left","defaultfont",getPixel(10,2),$button);
	
    $foo = '<INPUT ID="Width" TYPE="TEXT" SIZE="5" NAME="Width" ONKEYPRESS="event.returnValue=IsDigitPercent();" class="defaultfont">';
	$breite = htmlFormElementTable($foo,"Breite");

    $foo = '<INPUT ID="Height" TYPE="TEXT" SIZE="5" NAME="Height" ONKEYPRESS="event.returnValue=IsDigitPercent();" class="defaultfont">';
	$hoehe = htmlFormElementTable($foo,"H&ouml;he");
    
	$foo = '<INPUT ID="HSpace" TYPE="TEXT" SIZE="5" NAME="HSpace" ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$hspace = htmlFormElementTable($foo,"Horizontaler Abstand");

    $foo = '<INPUT ID="VSpace" TYPE="TEXT" SIZE="5" NAME="VSpace" ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$vspace = htmlFormElementTable($foo,"Vertikaler Abstand");


	$foo = '<INPUT ID="Border" TYPE="TEXT" SIZE="5" NAME="Border" ONKEYPRESS="event.returnValue=IsDigit();" class="defaultfont">';
	$rand = htmlFormElementTable($foo,"Rand");


	$foo = '<INPUT ID="Alt" TYPE="TEXT" SIZE="29" NAME="Alt" class="defaultfont">';
	$alt = htmlFormElementTable($foo,"Alternativer Text");

	$foo = '<SELECT class="defaultfont" ID="Alignment" NAME="Alignment" SIZE="1">
						<option value="" selected>Default
						<option value="top">Top
						<option value="middle">Middle
						<option value="bottom">Bottom
						<option value="left">Left
						<option value="right">Right
						<option value="text top">Text Top
						<option value="absmiddle">Abs Middle
						<option value="baseline">Baseline
						<option value="absbottom">Abs Bottom
					</select>';
	$ausrichtung = htmlFormElementTable($foo,"Ausrichtung");

	$foo = '<INPUT ID="Name" TYPE="TEXT" SIZE="29" NAME="Name" class="defaultfont">';
	$name = htmlFormElementTable($foo,"Name");

	$buttons = gifButton("abbrechen","",WE_LANGUAGE,"",92,18,"window.close()").
	getPixel(10,2).gifButton("ok","","","",92,18,"onOk()");

	$table = '<table border="0" cellpadding="0" cellspacing="0">
<tr><td colspan="4">'.$imageURL.'</td></tr>
<tr><td colspan="4">'.getPixel(100,4).'</td></tr>
<tr><td>'.$breite.'</td><td>'.$hoehe.'</td><td>'.$hspace.'</td><td>'.$vspace.'</td></tr>
<tr><td>'.getPixel(100,4).'</td><td>'.getPixel(100,4).'</td><td>'.getPixel(150,4).'</td><td>'.getPixel(140,4).'</td></tr>
<tr><td colspan="2">'.$rand.'</td><td colspan="2">'.$name.'</td></tr>
<tr><td colspan="4">'.getPixel(100,4).'</td></tr>
<tr><td colspan="2">'.$ausrichtung.'</td><td colspan="2">'.$alt.'</td></tr>
</table>
';
	print '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.htmlDialogBorder2(350,50,$table,"Bild bearbeiten").'</td>
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

</form>
</BODY>
</HTML>
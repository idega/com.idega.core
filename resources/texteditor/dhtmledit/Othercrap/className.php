<?php


include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");


htmlTop("Stylesheet Klasse bearbeiten");
?>
<?php print STYLESHEET ?>
<SCRIPT LANGUAGE=JavaScript>

var parentClass = "."+window.dialogArguments["parentClass"];
var classNames = window.dialogArguments["classNames"];

function showClassNames(name,onCh){
	document.writeln('<select class="defaultfont" name="'+name+'" id="'+name+'" size="1"'+(onCh ? ' onChange="'+onCh+'"' : '')+'>');
	document.writeln('<option value="">none');
	for(var i=0;i<classNames.length;i++){
		document.writeln('<option value="'+classNames[i]+'">'+classNames[i]);
	}
	document.writeln('</select>');
}
function onOk(){
	arr = new Array();
	arr["className"] = clNme.value.replace(/^\.(.+)$/,"$1");
	arr["classNameRg"] = clNmeRg.value.replace(/^\.(.+)$/,"$1");
	arr["radio"] = radiob[0].checked ? "p" : "r";
	window.returnValue = arr;
	window.close();
}

</SCRIPT>

<SCRIPT LANGUAGE=JavaScript FOR=window EVENT=onload>
clNme.value = parentClass;
clNmeRg.value = "";
/*Source.value = getAttrib(html,"src");
Width.value = getAttrib(html,"width");
Height.value = getAttrib(html,"height");
HSpace.value = getAttrib(html,"hspace");
VSpace.value = getAttrib(html,"vspace");
Border.value = getAttrib(html,"border");
Alt.value = getAttrib(html,"alt");
Alignment.value = getAttrib(html,"align").toLowerCase();
Name.value = getAttrib(html,"name");*/
</SCRIPT>

</HEAD>

<BODY  marginwidth="16" marginheight="16" leftmargin="16" topmargin="16">
<?php
	$buttons = gifButton("abbrechen","",WE_LANGUAGE,"",92,18,"window.close()").
	getPixel(10,2).gifButton("ok","","","",92,18,"onOk()");

$table = '<table border="0" cellpadding="2" cellspacing="0">
	<tr>
		<td><input type="radio" id="radiob" name ="radiob" value="p"></td>
		<td class="defaultfont"><b>Parent Class</b></td>
		<td><script>showClassNames("clNme","radiob[0].checked=true")</script></td>
	</tr>
	<tr>
		<td><input type="radio" id="radiob" name ="radiob" value="r" checked></td>
		<td class="defaultfont"><b>Region Class</b></td>
		<td><script>showClassNames("clNmeRg","radiob[1].checked=true")</script></td>
	</tr>
	<tr>
		<td><spacer type="horizontal" size="30"></td>
		<td><spacer type="horizontal" size="60"></td>
		<td><spacer type="horizontal" size="150"></td>
	</tr>
</table>
';
	print '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.htmlDialogBorder2(300,70,$table,"Stylesheet Klasse bearbeiten").'</td>
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
<?php

include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");


htmlTop("Hyperlink editieren");
?>
<?php print STYLESHEET ?>
<script language="JavaScript" src="<?php print JS_DIR ?>windows.js"></script>

<SCRIPT LANGUAGE=JavaScript>
function we_cmd(){
	var args = "";
	var url = "<?php print WEBEDITION_DIR; ?>we_cmd.php?"; for(var i = 0; i < arguments.length; i++){ url += "we_cmd["+i+"]="+escape(arguments[i]); if(i < (arguments.length - 1)){ url += "&"; }}
	switch (arguments[0]){
    case "openFileDirChooser":            			
            new jsWindow(url,"we_imgEditor",80,80,650,400,true,true);
			break;
	}
}
function onOk(){
	var f = document.forms[0];
	var arr=new Array();
	arr["href"] = f.Href.value;
	arr["target"] = f.Target.value;
	window.returnValue = arr;
	window.close();

}
</SCRIPT>

<SCRIPT LANGUAGE=JavaScript FOR=window EVENT=onload>
var f = document.forms[0];
var hr = window.dialogArguments["href"]  ? window.dialogArguments["href"] : "";
if(hr.substring(0,<?php print strlen(SERVER_NAME); ?>+7) == "http://<?php print SERVER_NAME; ?>"){
	hr = hr.substring(<?php print strlen(SERVER_NAME); ?>+7,hr.length);
}
f.Href.value = hr;
f.Target.value = window.dialogArguments["target"] ? window.dialogArguments["target"] : "";
</SCRIPT>


</HEAD>

<BODY marginwidth="16" marginheight="16" leftmargin="16" topmargin="16">
<form name="we_form" onSubmit="return false;">
<?php
	#$button = gifButton("auswaehlen","",WE_LANGUAGE,"",92,18,"we_cmd('openFileDirChooser','Href','foo','tblFile','')");
    $button = gifButton("auswaehlen","",WE_LANGUAGE,"",92,18,"we_cmd('openFileDirChooser','document.forms[\\'we_form\\'].elements[\\'id\\'].value','document.forms[\\'we_form\\'].elements[\\'Href\\'].value','*',document.forms['we_form'].elements['Href'].value,'tblFile')");
	$foo = '<INPUT TYPE="TEXT" ID="Href" NAME="Href" STYLE="width:300px" SIZE="29" class="defaultfont">';
	$href = htmlFormElementTable($foo,"URL","left","defaultfont","&nbsp;&nbsp;",$button);
	
	$foo = targetBox("Target",29,300,"Target");
	$target = htmlFormElementTable($foo,"Ziel");
	
	$buttons = gifButton("abbrechen","",WE_LANGUAGE,"",92,18,"window.close()").
	getPixel(10,2).gifButton("ok","","","",92,18,"onOk()");
	
	$table = '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.$href.'</td>
	</tr>
	<tr>
		<td>'.getPixel(2,4).'</td>
	</tr>
	<tr>
		<td>'.$target.'</td>
	</tr>
</table>
';
	print '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.htmlDialogBorder2(350,50,$table,"Hyperlink editieren").'</td>
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
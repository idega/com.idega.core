<?php


include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");


htmlTop("Quellcode bearbeiten");
?>
<?php print STYLESHEET ?>
<SCRIPT LANGUAGE=JavaScript>

function onOk(){
  window.returnValue = htmlinput.value;
  window.close();
}
	
</SCRIPT>

<SCRIPT LANGUAGE=JavaScript FOR=window EVENT=onload>
      htmlinput.value = window.dialogArguments["html"];
</SCRIPT>


</HEAD>

<BODY  marginwidth="16" marginheight="16" leftmargin="16" topmargin="16">
<?php
	$buttons = gifButton("abbrechen","",WE_LANGUAGE,"",92,18,"window.close()").
	getPixel(10,2).gifButton("ok","","","",92,18,"onOk()");

$textarea = '<TEXTAREA class="defaultfont" ID="htmlinput" NAME="htmlinput" WRAP="off" STYLE="width:700;height:450"></TEXTAREA>
';

	print '<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>'.htmlDialogBorder2(700,300,$textarea,"Quellcode bearbeiten").'</td>
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
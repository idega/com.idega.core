<?php

include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_tag.php");
include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_html_tools.php");
protect();
htmlTop();
print STYLESHEET;

if($fileName){
	if($fileName!="none"){
    	include_once($GLOBALS["DOCUMENT_ROOT"]."/webEdition/we/include/"."we_classes/we_rtf2html.php"); 
    	$rtf2html=new we_rtf2html($fileName,$applyFontName,$applyFontSize,$applyFontColor);
   	}
}
?>

<script language="JavaScript"><!--
<?php if($fileName == "none"): ?>
		setTimeout('alert("<?php print $l_importrtf["no_file"]?>");',100);
<?php endif ?>
 
 function submitForm(){
    document.we_form.elements["has_preview"].value=1; 
    document.we_form.submit(self,"import_Rtf");  
 }
 function importDoc(){
  if(document.we_form.elements["has_preview"].value!=""){  
    opener.document.we_form.elements[<?php print "'".$th."'";?>].DOM.body.innerHTML+=document.we_form.textPreview.value;
    self.close();
  }
  else{
     document.we_form.submit(self,"import_Rtf");  
  }
 }







 function doClick(val){
     switch (val) {
     case 1:var a=document.forms["we_form"].elements["applyFontName"];
      break;
     case 2:var a=document.forms["we_form"].elements["applyFontSize"]; 
      break;
     case 3:var a=document.forms["we_form"].elements["applyFontColor"];
      break;
     }
     

     if(a && a.value==1) a.value=0;
     else a.value=1;
     
 }
 
 self.focus();
 
 //-->
</script>

</head>
	<body bgcolor="#ffffff"  marginwidth="16" marginheight="8" leftmargin="16" topmargin="8" >
		<form name="we_form" enctype="multipart/form-data" method="post" onsubmit="return false;">
		   <input type="hidden" name="has_preview" value="<?php print $has_preview ? $has_preview : "" ?>">   
           <?php                                     
            $content='<table border="0" cellpadding="0" cellspacing="0" width="550">			
            <tr>
				<td colspan="2">'.getPixel(5,5).'</td>
			</tr>
			<tr>
				<td colspan="2" class="defaultfont"><b>'.$l_importrtf["chose"].'</b></td>
			</tr>            
			<tr>
				<td colspan="2" ><input type="file" name="fileName" size="50" onKeyDown="return false"></td>				
			</tr>
			
            <tr>
				<td colspan="2" class="defaultfont"><input type="checkbox" name="applyFontName" value='. (isset($applyFontName) ? $applyFontName.($applyFontName==1 ? " checked" : "") : 0).' onclick="doClick(1)">'.getPixel(5,22).$l_importrtf["use_fontname"].'</td>                
			</tr>
            <tr>
				<td colspan="2" class="defaultfont"><input type="checkbox" name="applyFontSize" value='. (isset($applyFontSize) ? $applyFontSize.($applyFontSize==1 ? " checked" : "") : 0).' onclick="doClick(2)">'.getPixel(5,22).$l_importrtf["use_fontsize"].'</td>                
			</tr>
            <tr>
				<td colspan="2" class="defaultfont"><input type="checkbox" name="applyFontColor" value='. (isset($applyFontColor) ? $applyFontColor.($applyFontColor==1 ? " checked" : "") : 0).' onclick="doClick(3)">'.getPixel(5,22).$l_importrtf["use_fontcolor"].'</td>                
            </tr>
            <tr>
				<td colspan="2">'.getPixel(5,10).'</td>
			</tr>
            <tr>
                <td colspan="2">'.gifButton("importieren","javascript:submitForm()",WE_LANGUAGE,"",92,18).'</td>
            </tr>
            <tr>
				<td colspan="2">'.getPixel(5,20).'</td>
			</tr>

            <tr>
				<td colspan="2" class="defaultfont"><b>'.$l_global["preview"].'</b></td>
			</tr>

            <tr>
				<td colspan="2"><textarea id="textPreview" name="textPreview" cols="59" rows="15" style="width:550px">';            
            if($fileName && $fileName != "none") $content.=$rtf2html->htmlOut;                
            $content.='</textarea>
            </td>
            </tr>            
		   <tr>
				<td colspan="2">'.getPixel(5,22).'</td>
			</tr>
		   </table>';                          

           print '<table cellpadding="0" cellspacing="0" border="0">
<tr>
	<td>'.htmlDialogBorder2(600,300,$content,$l_importrtf["import_rtf"]).'</td>
</tr>
<tr>
	<td>'.getPixel(2,19).'</td>
</tr>
<tr>
	<td>'.gifButton("abbrechen","javascript:self.close();",WE_LANGUAGE,"").
          getPixel(10,2).
          gifButton("ok","javascript:opener.top.hot=1;importDoc();","","").'</td>
</tr></table>
';
    ?>           
		</form>
     <?php if($fileName && ($fileName != "none")&&($has_preview == "")): ?>
     <script language="JavaScript">
        opener.document.we_form.elements[<?php print "'".$th."'";?>].DOM.body.innerHTML+=document.we_form.textPreview.value;
        self.close();   
     </script>
    <?php endif?>      
	</body>

</html>
            
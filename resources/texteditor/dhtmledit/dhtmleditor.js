// a htmleditor, works only in IE 5.5 or greater for WIN 32


// global variables
var  colorChooserURL = "";
var  linkChooserURL = "";

var ROOT_DHTMLEDIT_PATH = "/idegaweb/bundles/com.idega.core.bundle/resources/texteditor/dhtmledit/";
var ROOT_TOOLBARBUTTON_FOLDER = ROOT_DHTMLEDIT_PATH + "images/";


onerror= doNix;

function doNix(){return true;}

// MICROSOFT TRANSLATED THE FORMAT NAMES THAT WILL BE USED IN THE EXECCOMMAND FUNCTION !
// These need to be translated for some languages
// ########################################################################################################################
var FormatNames;
if(navigator.browserLanguage == "de"){
	FormatNames = new Array("Normal","Überschrift 1","Überschrift 2","Überschrift 3","Überschrift 4","Überschrift 5","Überschrift 6","Adresse","Formatiert");
}else if (navigator.browserLanguage == "da"){
	FormatNames = new Array("Normal","Overskrift 1","Overskrift 2","Overskrift 3","Overskrift 4","Overskrift 5","Overskrift 6","Adresse","Formateret");
}else{
	FormatNames = new Array("Normal","Heading 1","Heading 2","Heading 3","Heading 4","Heading 5","Heading 6","Address","Formatted");
}
// ########################################################################################################################

var ss = document.styleSheets;
var r;
var iw_styleString = "";
var iw_classNames = new Array();
var s;
for(var i=0;i<ss.length;i++){
  r = ss(i).rules;
  for(var n=0;n<r.length;n++){
    s = r(n).style;
    if(r(n).selectorText.substring(0,1) == "."){
      iw_styleString += r(n).selectorText + " {font-family:"+s.fontFamily+";font-size:"+s.fontSize+";background:"+s.background+";background-color:"+s.backgroundColor+";background-image:"+s.backgroundImage+";font-style:"+s.fontStyle+";font:"+s.font+";texd-decoration:"+s.textDecoration+";font-variant:"+s.fontVariant+";color:"+s.color+";font-weight:"+s.fontWeight+"}\n";
      iw_classNames.push(r(n).selectorText);
    }
  }
}


var IW_TOOLBARBUTTON_WIDTH = 23;
var IW_TOOLBARBUTTON_HEIGHT = 22;

var DECMD_SHOWDETAILS = -5000;
var DECMD_VISIBLEBORDERS = -5001;
var DECMD_EDITSOURCECODE = -5002;
var DECMD_CLASSNAME = -5003;
var DECMD_BR = -5004;
//var DECMD_RTF = -5005;

var iw_editctrls = new Array();

var ContextMenu = new Array();
var GeneralContextMenu = new Array();
var TableContextMenu = new Array();

var lastElement = null;

// Constructor for custom object that represents an item on the context menu
function ContextMenuItem(string, cmdId) {
  this.string = string;
  this.cmdId = cmdId;
}
// include LanguageFile
document.writeln('<script language="JavaScript" src="' + ROOT_DHTMLEDIT_PATH + 'dhtmlteditor_localizable_strings.js"></script>');
// include MS Defines
document.writeln('<script language="JavaScript" src="' + ROOT_DHTMLEDIT_PATH + 'dhtmleditorconstants.js"></script>');
//include vbscripts
document.writeln('<script language="VBScript" src="' + ROOT_DHTMLEDIT_PATH + 'dhtmleditor.vbs"></script>');

document.writeln('<script LANGUAGE="javascript" FOR="window" EVENT="onload">');
document.writeln('setTimeout("iw_dhtmledit_init()",100);');
document.writeln('</script>');

//include styles for Buttons
document.writeln('<link rel="stylesheet" href="' + ROOT_DHTMLEDIT_PATH + 'buttons.css">');

//alert(lng["cut"]);

// ##################################################################################################################################
// main DHTMLEdit Class
// ##################################################################################################################################
//function iw_DHTMLEdit(name,html,width,height,baseUrl,showMenues,bgcolor){
function DHTMLEdit(name,width,height,baseUrl,showMenues,bgcolor,colorURL,linkURL){
  this.name = name;
  this.name_clean = name;

  colorChooserURL = colorURL;
  linkChooserURL = linkURL;
  //linkChooserURL = ROOT_DHTMLEDIT_PATH+"links.html";

  //this.name.replace(/[\[\]]/g,"");
  //this.html = html;
  this.firstHtml = "";
  this.hot = false;
  this.first = true;
  this.width = width;
  this.height = height;
  this.baseUrl = baseUrl;
  this.bgcolor = bgcolor;

  this.displayChanged = iw_DHTMLEdit_DisplayChanged;
  this.ShowContextMenu = iw_DHTMLEdit_ShowContextMenu;
  this.ContextMenuAction = iw_DHTMLEdit_ContextMenuAction;
  this.Complete = iw_DHTMLEdit_complete;
  this.getHTML = iw_DHTMLEdit_GetHTML;
  document.writeln('<table border="0" cellpadding="0" cellspacing="0" background="'+ROOT_DHTMLEDIT_PATH+'images/white.gif" bgcolor="buttonface" width="'+this.width+'">');
  if(showMenues){
          document.writeln('<tr><td>');
          this.menue = new iw_DHTMLEdit_Menues(this.name);
          document.writeln('</td></tr>');
          document.writeln('<tr><td><div class="tbButtonsHR"></div></td></tr>');
  }else{
          this.menue = null;
  }
  document.writeln('<tr><td>');
  this.toolbar = new iw_DHTMLEdit_Toolbar(this.name,this);
  document.writeln('</td></tr>');
  document.writeln('<tr><td><div class="tbButtonsHR"></div></td></tr>');
  document.writeln('<tr><td>');
  this.toolbar2 = new iw_DHTMLEdit_TableToolbar(this.name,this);
  document.writeln('</td></tr>');
  document.writeln('<tr><td>');
  document.writeln('<object classid="clsid:2D360201-FFF5-11d1-8D03-00A0C959BC0A" NAME="'+this.name+'_editor" ID="'+this.name+'_editor" height="'+this.height+'" width="100%" VIEWASTEXT></object>');
  document.writeln('<object id="'+this.name+'_tableinfo" classid="clsid:47B0DFC7-B7A3-11D1-ADC5-006008A5848C" height="0"></object>');
  document.writeln('<script LANGUAGE="javascript" FOR="'+this.name+'_editor" EVENT="DisplayChanged">');
  document.writeln('return '+this.name_clean+'DHTMLObject.displayChanged();');
  document.writeln('</script>');
  document.writeln('<script LANGUAGE="javascript" FOR="'+this.name+'_editor" EVENT="ShowContextMenu">');
  document.writeln('return '+this.name_clean+'DHTMLObject.ShowContextMenu()');
  document.writeln('</script>');
  document.writeln('<script LANGUAGE="javascript" FOR="'+this.name+'_editor" EVENT="ContextMenuAction(itemIndex)">');
  document.writeln('return '+this.name_clean+'DHTMLObject.ContextMenuAction(itemIndex)');
  document.writeln('</script>');
  document.writeln('<script LANGUAGE="javascript" FOR="'+this.name+'_editor" EVENT="DocumentComplete">');
  document.writeln('return '+this.name_clean+'DHTMLObject.Complete()');
  document.writeln('</script>');
  //document.writeln('<input type="hidden" name="'+this.name+'" value="'+this.html+'"');
  document.writeln('</td></tr>');
  document.writeln('</table>');

 iw_editctrls.push(this.name);



  this.obj = this.name_clean+"DHTMLObject";
  eval(this.obj + "=this");
  o = document.all[this.name+"_editor"];
  o.BaseURL = this.baseUrl;
  var parentclass = o.parentElement.parentElement.parentElement.parentElement.parentElement.className;
  if(!parentclass) parentclass = o.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement.className;
  var style = '<style type="text/css"><!--'+"\n";
  style += iw_styleString+"-->\n";
  style += '</style>'+"\n";

  document.all[this.name+'_editor'].DocumentHTML = '<html><head><base href="'+this.baseUrl+'">'+"\n"+style+'</head><body topMargin="0" leftMargin="0" rightMargin="0" bottomMargin="0" class="'+parentclass+'" '+(bgcolor ? ('bgcolor="'+this.bgcolor+'"') : '')+'>'+ document.all[this.name].value+'</body></html>'+"\n";

}
function iw_DHTMLEdit_complete(){
}

function iw_dhtmledit_init(){
  GeneralContextMenu[0] = new ContextMenuItem(lng["cut"], DECMD_CUT);
  GeneralContextMenu[1] = new ContextMenuItem(lng["copy"], DECMD_COPY);
  GeneralContextMenu[2] = new ContextMenuItem(lng["paste"], DECMD_PASTE);
  TableContextMenu[0] = new ContextMenuItem("", 0);
  TableContextMenu[1] = new ContextMenuItem(lng["insert_row"], DECMD_INSERTROW);
  TableContextMenu[2] = new ContextMenuItem(lng["delete_rows"], DECMD_DELETEROWS);
  TableContextMenu[3] = new ContextMenuItem("", 0);
  TableContextMenu[4] = new ContextMenuItem(lng["insert_colmn"], DECMD_INSERTCOL);
  TableContextMenu[5] = new ContextMenuItem(lng["delete_colmns"], DECMD_DELETECOLS);
  TableContextMenu[6] = new ContextMenuItem("", 0);
  TableContextMenu[7] = new ContextMenuItem(lng["insert_cell"], DECMD_INSERTCELL);
  TableContextMenu[8] = new ContextMenuItem(lng["delete_cells"], DECMD_DELETECELLS);
  TableContextMenu[9] = new ContextMenuItem(lng["merge_cells"], DECMD_MERGECELLS);
  TableContextMenu[10] = new ContextMenuItem(lng["split_cell"], DECMD_SPLITCELL);
}


function iw_DHTMLEdit_GetHTML(){
	return document.all[this.name].value;
}

function iw_DHTMLEdit_DisplayChanged(){
		var HTMLEdit = document.all[this.name+'_editor'] ;

		var i;
		var s;
		var pop;
		var tb = this.toolbar;

		for (i=0; i < tb.buttons.length; i++) {
			if(tb.buttons[i].cmd > 0){
		  		s = HTMLEdit.QueryStatus(tb.buttons[i].cmd);
				if (s == DECMDF_DISABLED || s == DECMDF_NOTSUPPORTED) {
					tb.buttons[i].disable();
				} else if (s == DECMDF_ENABLED  || s == DECMDF_NINCHED) {
					tb.buttons[i].enable();
					tb.buttons[i].uncheck();
				} else { // DECMDF_LATCHED
					tb.buttons[i].enable();
					tb.buttons[i].check();
				}
			}
		}
		var tb = this.toolbar2;
		for (i=0; i < tb.buttons.length; i++) {
			if(tb.buttons[i].cmd > 0){
		  		s = HTMLEdit.QueryStatus(tb.buttons[i].cmd);
				if (s == DECMDF_DISABLED || s == DECMDF_NOTSUPPORTED) {
					tb.buttons[i].disable();
				} else if (s == DECMDF_ENABLED  || s == DECMDF_NINCHED) {
					tb.buttons[i].enable();
					tb.buttons[i].uncheck();
				} else { // DECMDF_LATCHED
					tb.buttons[i].enable();
					tb.buttons[i].check();
				}
			}
		}
		if(this.menue){
			s = HTMLEdit.QueryStatus(DECMD_GETBLOCKFMT);
			pop = document.all[this.name+DECMD_SETBLOCKFMT];
			if (s == DECMDF_DISABLED || s == DECMDF_NOTSUPPORTED) {
				pop.disabled = true;
			} else {
				pop.value = HTMLEdit.ExecCommand(DECMD_GETBLOCKFMT, OLECMDEXECOPT_DODEFAULT);
				pop.disabled = false;
	  		}

			s = HTMLEdit.QueryStatus(DECMD_GETFONTNAME);
			pop = document.all[this.name+DECMD_SETFONTNAME];
			if (s == DECMDF_DISABLED || s == DECMDF_NOTSUPPORTED) {
				pop.disabled = true;
			} else {
				pop.disabled = false;
				pop.value = HTMLEdit.ExecCommand(DECMD_GETFONTNAME, OLECMDEXECOPT_DODEFAULT);
			}

			pop = document.all[this.name+DECMD_SETFONTSIZE];
			if (s == DECMDF_DISABLED || s == DECMDF_NOTSUPPORTED) {
				pop.disabled = true;
			} else {
				pop.value = HTMLEdit.ExecCommand(DECMD_GETFONTSIZE, OLECMDEXECOPT_DODEFAULT);
				pop.disabled = false;
			}
		}

		eval('var db = '+this.name_clean+'DHTMLObject.toolbar2');
		if(HTMLEdit.ShowDetails){
			db.detailsBut.check();
		}else{
			db.detailsBut.uncheck();
		}
		if(HTMLEdit.ShowBorders){
			db.visiblebordersBut.check();
		}else{
			db.visiblebordersBut.uncheck();
		}
		if(this.first == false){
			if(SelectedIsTable(HTMLEdit)){
				tb.buttons[0].enable();
			}
		}

		if(this.first){
			this.firstHtml = HTMLEdit.DOM.body.innerHTML;
			this.first = false;
		}
		if(document.all[this.name].value != HTMLEdit.DOM.body.innerHTML){
			this.hot = true;
			top.hot=1;
			// fill the hidden Field with HTML of the activex control
			document.all[this.name].value = HTMLEdit.DOM.body.innerHTML;
		}
}
function iw_DHTMLEdit_ShowContextMenu() {
  var menuStrings = new Array();
  var menuStates = new Array();
  var state;
  var i
  var idx = 0;
  var HTMLEdit = document.all[this.name+'_editor'] ;

  // Rebuild the context menu.
  ContextMenu.length = 0;

  // Always show general menu
  for (i=0; i<GeneralContextMenu.length; i++) {
    ContextMenu[idx++] = GeneralContextMenu[i];
  }

  // Is the selection inside a table? Add table menu if so
  if (HTMLEdit.QueryStatus(DECMD_INSERTROW) != DECMDF_DISABLED) {
    for (i=0; i<TableContextMenu.length; i++) {
      ContextMenu[idx++] = TableContextMenu[i];
    }
  }

  // Set up the actual arrays that get passed to SetContextMenu
  for (i=0; i<ContextMenu.length; i++) {
    menuStrings[i] = ContextMenu[i].string;
    if (menuStrings[i] != "") {
      state = HTMLEdit.QueryStatus(ContextMenu[i].cmdId);
    } else {
      state = DECMDF_ENABLED;
    }
    if (state == DECMDF_DISABLED || state == DECMDF_NOTSUPPORTED) {
      menuStates[i] = OLE_TRISTATE_GRAY;
    } else if (state == DECMDF_ENABLED || state == DECMDF_NINCHED) {
      menuStates[i] = OLE_TRISTATE_UNCHECKED;
    } else { // DECMDF_LATCHED
      menuStates[i] = OLE_TRISTATE_CHECKED;
    }
  }

  // Set the context menu
  HTMLEdit.SetContextMenu(menuStrings, menuStates);
}

function iw_DHTMLEdit_ContextMenuAction(itemIndex) {
 	var HTMLEdit = document.all[this.name+'_editor'] ;

  HTMLEdit.ExecCommand(ContextMenu[itemIndex].cmdId, OLECMDEXECOPT_DODEFAULT);

}

// ##################################################################################################################################
// Menues Class
// ##################################################################################################################################
function iw_DHTMLEdit_Menues(name){
	this.name = name;
	this.menues = new Array();
	document.write('<table border="0" cellpadding="0" cellspacing="0"><tr><td>');
	this.menues[0] = new iw_DHTMLEdit_PopupMenue(this,FormatNames,100,DECMD_SETBLOCKFMT,"ParagraphStyle",FormatNames);
	document.write('</td><td>');
	this.menues[1] = new iw_DHTMLEdit_PopupMenue(this,new Array("Arial","Arial Black","Comic Sans MS","Courier New","System","Times New Roman","Verdana","Wingdings"),130,DECMD_SETFONTNAME,"FontName");
	document.write('</td><td>');
	this.menues[2] = new iw_DHTMLEdit_PopupMenue(this,new Array(1,2,3,4,5,6,7),32,DECMD_SETFONTSIZE,"FontSize");
	document.write('</td></tr></table>');
}

// ##################################################################################################################################
// PopupMenue Class
// ##################################################################################################################################
function iw_DHTMLEdit_PopupMenue(itsMenues,entries,width,cmd,title,vals){
	this.name = "iw_DHTMLEdit_PopupMenue" + (iw_DHTMLEdit_PopupMenueCount++);
	this.itsMenues = itsMenues;
	this.entries = entries;
	this.cmd = cmd;
	this.title = title;
	this.width=width;
	this.ExecCommand = iw_DHTMLEdit_PopupMenue_ExecCommand;
	this.obj = this.name+"Object";
	this.vals = vals;
	eval(this.obj + "=this");
	document.writeln('<select id="'+this.itsMenues.name+this.cmd+'" title="'+this.title+'" language="javascript" style="width:'+this.width+'px;background:white;font:8pt verdana,arial,sans-serif" onChange="'+this.name+'Object.ExecCommand(this.options[this.selectedIndex].value)">');
	for(var i=0; i < this.entries.length;i++){
		if(vals){
			document.writeln('<option value="'+this.vals[i]+'">'+this.entries[i]);
		}else{
			document.writeln('<option value="'+this.entries[i]+'">'+this.entries[i]);
		}
	}
	document.writeln('</select>');

}
var iw_DHTMLEdit_PopupMenueCount = 0;

function iw_DHTMLEdit_PopupMenue_ExecCommand(value){
 	var o = document.all[this.itsMenues.name+"_editor"];
  	var dom = o.DOM;
  	o.ExecCommand(this.cmd,OLECMDEXECOPT_DODEFAULT,value);
    dom.focus();
}

// ##################################################################################################################################
// Toolbar Classes
// ##################################################################################################################################
function iw_DHTMLEdit_Toolbar(name,itsDHTMLEdit){
	this.name = name;
	this.itsDHTMLEdit = itsDHTMLEdit;
	this.buttons = new Array();
	document.write('<table border="0" cellpadding="0" cellspacing="0"><tr><td>');
	this.buttons[0] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_BOLD",lng["bold"]);
	document.write('</td><td>');
	this.buttons[1] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_ITALIC",lng["italic"]);
	document.write('</td><td>');
	this.buttons[2] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_UNDERLINE",lng["underline"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[3] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_JUSTIFYLEFT",lng["justify_left"]);
	document.write('</td><td>');
	this.buttons[4] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_JUSTIFYCENTER",lng["justify_center"]);
	document.write('</td><td>');
	this.buttons[5] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_JUSTIFYRIGHT",lng["justify_right"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[6] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_ORDERLIST",lng["ordered_list"]);
	document.write('</td><td>');
	this.buttons[7] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_UNORDERLIST",lng["unordered_list"]);
	document.write('</td><td>');
	this.buttons[8] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_INDENT",lng["indent"]);
	document.write('</td><td>');
	this.buttons[9] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_OUTDENT",lng["outdent"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[10] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_SETFORECOLOR",lng["fore_color"]);
	document.write('</td><td>');
	this.buttons[11] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_SETBACKCOLOR",lng["back_color"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[12] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_HYPERLINK",lng["hyperlink"] );
	document.write('</td><td>');
	this.buttons[13] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_UNLINK",lng["unlink"]);
	document.write('</td></tr></table>');
}
function iw_DHTMLEdit_TableToolbar_OLD(name,itsDHTMLEdit){
//DISABLED

}

function iw_DHTMLEdit_TableToolbar(name,itsDHTMLEdit){
	this.name = name;
	this.itsDHTMLEdit = itsDHTMLEdit;
	this.buttons = new Array();

	document.write('<table border="0" cellpadding="0" cellspacing="0"><tr><td>');
	this.buttons[0] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_INSERTTABLE",lng["insert_edit_table"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[1] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_INSERTROW",lng["insert_row2"]);
	document.write('</td><td>');
	this.buttons[2] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_INSERTCOL",lng["insert_colmn2"]);
	document.write('</td><td>');
	this.buttons[3] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_INSERTCELL",lng["insert_cell2"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[4] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_SPLITCELL",lng["split_cell2"]);
	document.write('</td><td>');
	this.buttons[5] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_MERGECELLS",lng["merge_cells2"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[6] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_DELETEROWS",lng["delete_rows2"]);
	document.write('</td><td>');
	this.buttons[7] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_DELETECOLS",lng["delete_colmns2"]);
	document.write('</td><td>');
	this.buttons[8] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_DELETECELLS",lng["delete_cells2"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.buttons[9] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_IMAGE",lng["insert_edit_image"]);
	document.write('</td><td>');
/*
    document.write('</td><td>');
	this.buttons[9] = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_RTF",lng["rtf_import"]);
	document.write('</td><td>');
*/
	separator();
	document.write('</td><td>');
	this.detailsBut = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_SHOWDETAILS",lng["show_details"]);
	document.write('</td><td>');
	this.visiblebordersBut = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_VISIBLEBORDERS",lng["visible_borders"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.sourcecodeBut = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_EDITSOURCECODE",lng["edit_sourcecode"]);
	document.write('</td><td>');
	separator();
	document.write('</td><td>');
	this.brBut = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_BR",lng["insert_br"]);
	if(iw_classNames.length){
		document.write('</td><td>');
		separator();
		document.write('</td><td>');
		this.classnameBut = new iw_DHTMLEdit_ToolbarButton(this,"DECMD_CLASSNAME",lng["edit_style_class"]);
	}
	document.write('</td></tr></table>');

}

function separator(){
	document.write('<div class="tbSeparator"></div>');
}

// ##################################################################################################################################
// Toolbar Button Class
// ##################################################################################################################################
function iw_DHTMLEdit_ToolbarButton(itsToolbar,cmd,title){
	this.name = "iw_DHTMLEdit_ToolbarButton" + (iw_g_DHTMLEdit_ToolbarButtonCount++);
	this.toolbar = itsToolbar;
	this.src = ROOT_TOOLBARBUTTON_FOLDER+cmd+".gif";
	eval("this.cmd = "+cmd);
	this.click = iw_DHTMLEdit_ToolbarButton_Click;
	this.over = iw_DHTMLEdit_ToolbarButton_Over;
	this.out = iw_DHTMLEdit_ToolbarButton_Out;
	this.check = iw_DHTMLEdit_ToolbarButton_Check;
	this.uncheck = iw_DHTMLEdit_ToolbarButton_Uncheck;
	this.disable = iw_DHTMLEdit_ToolbarButton_Disable;
	this.enable = iw_DHTMLEdit_ToolbarButton_Enable;
	this.ExecCommand = iw_DHTMLEdit_ToolbarButton_ExecCommand;
	this.title=title;

	this.checked = false;
	this.isover = false;
	this.disabled = false;

	this.obj = this.name+"Object";
	eval(this.obj + "=this");
	document.write('<div id="'+this.name+this.cmd+'Div" class="tbButton"><img src="'+this.src+'" width="'+IW_TOOLBARBUTTON_WIDTH+'" height="'+IW_TOOLBARBUTTON_HEIGHT+'" name="'+this.name+this.cmd+'" border="0" onMouseOver="return '+this.name+'Object.over();" onMouseOut="return '+this.name+'Object.out();" OnClick="return '+this.name+'Object.click();"'+(this.title ? ' alt="'+this.title+'" title="'+this.title+'"' : '')+'></div>');
}
var iw_g_DHTMLEdit_ToolbarButtonCount = 0;

function iw_DHTMLEdit_ToolbarButton_Click(){
	if(!this.disabled){
		this.ExecCommand();
	}
}
function iw_DHTMLEdit_ToolbarButton_Over(){
	if(!this.disabled){
		this.isover = true;
		document.all[this.name+this.cmd+"Div"].className = this.checked ? "tbButtonMouseOverDown" : "tbButtonMouseOverUp";
		return true;
	}
}

function iw_DHTMLEdit_ToolbarButton_Out(){
	if(!this.disabled){
		this.isover = false;
		document.all[this.name+this.cmd+"Div"].className = this.checked ? "tbButtonDown" : "tbButton";
		return true;
	}
}

function iw_DHTMLEdit_ToolbarButton_Check(){
	if((!this.disabled) && (!this.checked)){
		this.checked = true;
		document.all[this.name+this.cmd+"Div"].className = this.isover ? "tbButtonMouseOverDown" : "tbButtonDown";
		return true;
	}
}
function iw_DHTMLEdit_ToolbarButton_Uncheck(){
	if((!this.disabled) && this.checked){
		this.checked = false;
		document.all[this.name+this.cmd+"Div"].className = this.isover ? "tbButtonMouseOverUp" : "tbButton";
		return true;
	}
}

function iw_DHTMLEdit_ToolbarButton_Disable(){
	if(!this.disabled){
		this.disabled = true;
		document.all[this.name+this.cmd+"Div"].className = "tbButton";
		document.all[this.name+this.cmd].className = "tbButtonDisabled";
	}
}
function iw_DHTMLEdit_ToolbarButton_Enable(){
	if(this.disabled){
		this.disabled = false;
		document.all[this.name+this.cmd].className = "tbButtonEnabled";
	}
}

function iw_DHTMLEdit_ToolbarButton_ExecCommand(){
 	var o = document.all[this.toolbar.name+"_editor"];
  	var dom = o.DOM;
	var sel = dom.selection;
	var tr = sel.createRange();
	var html;
	switch(this.cmd){
		case DECMD_HYPERLINK:
			if(sel.type == "Text" || sel.type == "None"){
				var args = new Array();

				var a = GetLinkObjUnderInsertionPoint(o);
				if(a){
					args["href"] = a.href;
					args["target"] = a.target;
				}
				retVals = showModalDialog( linkChooserURL,args,"font-family:Verdana; font-size:12; dialogWidth:280px; dialogHeight:400px" );
				if(retVals){
					if(retVals["href"]){
						if(a){
							a.href = retVals["href"];
							if(retVals["target"]) a.target = retVals["target"];
						}else{
							o.ExecCommand(DECMD_UNLINK);
							html = '<a href="'+retVals["href"]+'"'+((retVals["target"]) ? (' target="'+retVals["target"]+'"') : '')+'>'+tr.htmlText+'</a>';
							tr.pasteHTML(html);
						}
					}
				}

			}
			break;
		case DECMD_CLASSNAME:
			if(sel.type == "Text" || sel.type == "None"){
				var cn = GetClassnameUnderInsertionPoint(o);
				var args = new Array();
				html = tr.htmlText;
				args["classNames"] = iw_classNames;
				args["parentClass"] = cn;
				var arr = showModalDialog( ROOT_DHTMLEDIT_PATH+"className.php",args,"font-family:Verdana; font-size:12; dialogWidth:360px; dialogHeight:210px" );

				if(arr){
					if(arr["radio"] == "p"){

						if(arr["className"] != cn){
							ReplaceClass(arr["className"],o);
						}
					}else if(arr["radio"] == "r"){
						html = html.replace(/<\/?span[^>]*>/gi,"");
						if(arr["classNameRg"]){
							if (html) tr.pasteHTML('<span class="'+arr["classNameRg"]+'">'+html+'</span>');
						}else{
							var foo = tr.parentElement().outerHTML;
							foo = foo.replace(/<\/?span[^>]*>/gi,"");
							tr.parentElement().outerHTML = foo; //tr.pasteHTML("");
						}
					}
				}
			}
			break;
		case DECMD_SETFORECOLOR:
		case DECMD_SETBACKCOLOR:
			args = new Array();
			args["bgcolor"] = GetCmdValue(o,((this.cmd == DECMD_SETBACKCOLOR) ? DECMD_GETBACKCOLOR : DECMD_GETFORECOLOR));
			var arr = showModalDialog(colorChooserURL,args,"font-family:Verdana; font-size:12; dialogWidth:220px; dialogHeight:220px" );
			if (arr != null) {
				o.ExecCommand(this.cmd,OLECMDEXECOPT_DODEFAULT, arr);
			}
			break;
		case DECMD_VISIBLEBORDERS:
			o.ShowBorders = !o.ShowBorders;
			break;
		case DECMD_SHOWDETAILS:
			o.ShowDetails = !o.ShowDetails;
			break;
		case DECMD_EDITSOURCECODE:
			var args = new Array();
			args["html"] = document.all[this.toolbar.name].value;
			var html = showModalDialog( ROOT_DHTMLEDIT_PATH+"source.html",args,"font-family:Verdana; font-size:12; dialogWidth:570px; dialogHeight:450px" );
			if (html != null) {
				document.all[this.toolbar.name].value = html;
				o.DOM.body.innerHTML = html;
			}
			break;
		case DECMD_IMAGE:
			if(sel.type == "Text" || sel.type == "None"){
				html = tr.htmlText;
			}else{
				html = GetElementUnderInsertionPoint(o);
			}
			var pre,post;
			var re = /(.*)(< ?img[^>]*>)(.*)$/i;
			if(html.search(re) == -1){
				html = "";
			}else{
				pre = html.replace(re,"$1");
				post = html.replace(re,"$3");
				html = html.replace(re,"$2");
			}
			var args = new Array();
			args["html"] = html;
			args["BaseUrl"] = this.toolbar.itsDHTMLEdit.baseUrl;
            args["opener"] = top;
            html = showModalDialog( ROOT_DHTMLEDIT_PATH+"images.html",args,"font-family:Verdana; font-size:12; dialogWidth:250px; dialogHeight:180px" );
			if(html != null){
				if(tr.pasteHTML) tr.pasteHTML((pre ? pre : "")+html+(post ? post : ""));
				else InsertElementUnderInsertionPoint(o,html);
			}
			break;
		case DECMD_BR:
			if(sel.type == "Text" || sel.type == "None"){
				if(tr.pasteHTML){
					tr.pasteHTML("<br>");
					tr.select();
				}
			}
			break;
		case DECMD_INSERTTABLE:
  			var ti = document.all[this.toolbar.name+'_tableinfo'];
			var args = new Array();
			var arr = null;

			if(sel.type == "Control"){
				var html = GetElementUnderInsertionPoint(o);
				var re = new RegExp("< ?table ([^>]+)>(.*)","i");
				var rest = html.replace(re,"$2");
				re.exec(html);
				args["TableAttrs"] = RegExp.$1;
				var tableAtrribs = showModalDialog( ROOT_DHTMLEDIT_PATH+"tableproperties.html",args,"font-family:Verdana; font-size:12; dialogWidth:260px; dialogHeight:350px");
				if (tableAtrribs != null) {
					html = '<TABLE'+(tableAtrribs ? (' '+tableAtrribs) : '')+'>'+rest;
					ti.TableAttrs = tableAtrribs;
				}
				InsertElementUnderInsertionPoint(o,html);
			}else{

				// Display table information dialog
				args["NumRows"] = ti.NumRows;
				args["NumCols"] = ti.NumCols;
				args["TableAttrs"] = ti.TableAttrs;
				args["CellAttrs"] = ti.CellAttrs;
				args["Caption"] = ti.Caption;
				arr = showModalDialog( ROOT_DHTMLEDIT_PATH+"tables.html",args,"font-family:Verdana; font-size:12; dialogWidth:260px; dialogHeight:350px");
				if (arr != null) {

					// Initialize table object
					for ( elem in arr ) {
						if ("NumRows" == elem && arr["NumRows"] != null) {
							ti.NumRows = arr["NumRows"];
						} else if ("NumCols" == elem && arr["NumCols"] != null) {
							ti.NumCols = arr["NumCols"];
						} else if ("TableAttrs" == elem) {
							ti.TableAttrs = arr["TableAttrs"];
						} else if ("CellAttrs" == elem) {
							ti.CellAttrs = arr["CellAttrs"];
						} else if ("Caption" == elem) {
							ti.Caption = arr["Caption"];
						}
					}
					o.ExecCommand(DECMD_INSERTTABLE,OLECMDEXECOPT_DODEFAULT, ti);
				}
			}
			break;
        /*case DECMD_RTF:
            window.open(ROOT_DHTMLEDIT_PATH+"importRtf.php?th="+o.name,"import_Rtf","width=640,height=565");
            break;*/
		default:

  			o.ExecCommand(this.cmd);
	}
   	dom.focus();
}


// ############ Helper Fns

function GetClassnameUnderInsertionPoint(o){
  	var dom = o.DOM;
	var sel = dom.selection;
	var cn = "";
	var foundElement = null;
	var rg = sel.createRange();

	if(sel.type == "Text" || sel.type == "None"){
 		foundElement = rg.parentElement();
		if(foundElement.tagName != "BODY"){
			cn = foundElement.className;
			lastElement = foundElement;
			while((!cn) && (foundElement.tagName != "TD")){
				foundElement = foundElement.parentElement;
				if(foundElement){
					if(foundElement.tagName != "BODY"){
						cn = foundElement.className;
						lastElement = foundElement;
					}else{
						break;
					}
				}
			}

		}
	}
	return cn;
}

function GetLinkObjUnderInsertionPoint(o){
  	var dom = o.DOM;
	var sel = dom.selection;
	var tn = "";
	var foundElement = null;
	var rg = sel.createRange();

	if(sel.type == "Text" || sel.type == "None"){
 		foundElement = rg.parentElement();
 		tn = foundElement.tagName;
 		while(tn != "BODY" && tn != "TD" && tn != "DIV"){
 			if(tn == "A"){
 				return foundElement;
 			}
			foundElement = foundElement.parentElement;
 			tn = foundElement.tagName;
 		}
 	}

	return null;
}

function SelectedIsTable(o){
	if(o.DOM.selection.type == "Control"){
		var html = GetElementUnderInsertionPoint(o);
		if(html.indexOf("<TABLE") != -1) return true;
	}
	return false;
}

function GetElementObjectUnderInsertionPoint(o){
  	var dom = o.DOM;
	var sel = dom.selection;
	var rg = sel.createRange();
	if(sel.type=="Control"){
		return vbs_getControlElement(rg);
	}else{
		return rg.parentElement();
	}
}

function ReplaceClass(nm,o){
	if(lastElement){
		if(lastElement.tagName != "BODY"){
			lastElement.className = nm;
		}
	}
}
function GetElementUnderInsertionPoint(o){
	return GetElementObjectUnderInsertionPoint(o).outerHTML;
}

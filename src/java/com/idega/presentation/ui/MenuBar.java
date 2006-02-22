package com.idega.presentation.ui;



import com.idega.presentation.IWContext;
import com.idega.presentation.Script;



/**

 * Title:        MenuBar<p>

 * Description:  <p>

 * Copyright:    Copyright (c) idega margmiðlun hf.<p>

 * Company:      idega margmiðlun hf.<p>

 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Á. Sæmundsson</a>

 * @version 2.0

 */



public class MenuBar extends InterfaceObject {



  Script ScriptObject;



  String initString;

  String functionString;

  String initFunction_begin;

  String initFunction_var;

  String initFunction_end;

  String varString;

  String scriptString;

  String NavBarSettings;



  String NavBar_setFonts;

  String NavBar_setColors;

  String NavBar_setSizes;



  int x_pos;

  int y_pos;

  int counter;





  private String scriptCode;

  private String scriptCodeInit;

  private String[] addFunction;

  boolean scaleNavBar;





  public MenuBar() {

    ScriptObject = new Script("javascript");



    scriptCode = "\r";

    scriptString = "\r";

    scriptCodeInit = null;

    varString = null;

    addFunction = new String[50];



    counter = 0;



    scaleNavBar = false;



    setPosition(0,0);



    set_initString();

    set_functionString();





    add(ScriptObject);

  }





  private void set_initString(){

        initString = "\r";

        initString += "var isMinNS4=(navigator.appName.indexOf(\"Netscape\")>=0&&parseFloat(navigator.appVersion)>=4)?1:0;\r";

        initString += "var isMinIE4=(document.all)?1:0;var isMinIE5=(isMinIE4&&navigator.appVersion.indexOf(\"5.\")>=0)?1:0;\r";

        initString += "\r";

  }





  private void set_functionString(){

	functionString = "\r";

	functionString += "function hideLayer(layer) {\r";

	functionString += "\r";

	functionString += "if(isMinNS4)layer.visibility=\"hide\";\r";

	functionString += "if(isMinIE4)layer.style.visibility=\"hidden\";\r";

	functionString += "\r";

	functionString += "}\r";

	functionString += "\r";

	functionString += "function showLayer(layer) {\r";

	functionString += "\r";

	functionString += "if(isMinNS4)layer.visibility=\"show\";\r";

	functionString += "if(isMinIE4)layer.style.visibility=\"visible\";\r";

	functionString += "\r";

	functionString += "}\r";

	functionString += "\r";

	functionString += "function inheritLayer(layer){if(isMinNS4)layer.visibility=\"inherit\";if(isMinIE4)layer.style.visibility=\"inherit\";}function getVisibility(layer){if(isMinNS4){if(layer.visibility==\"show\")return \"visible\";if(layer.visibility==\"hide\")return \"hidden\";return layer.visibility;}if(isMinIE4)return layer.style.visibility;return \"\";}function moveLayerTo(layer,x,y){if(isMinNS4)layer.moveTo(x,y);if(isMinIE4){layer.style.left=x;layer.style.top=y;}}function moveLayerBy(layer,dx,dy){if(isMinNS4)layer.moveBy(dx,dy);if(isMinIE4){layer.style.pixelLeft+=dx;layer.style.pixelTop+=dy;}}function getLeft(layer){if(isMinNS4)return layer.left;if(isMinIE4)return layer.style.pixelLeft;return-1;}function getTop(layer){if(isMinNS4)return layer.top;if(isMinIE4)return layer.style.pixelTop;return-1;}function getRight(layer){if(isMinNS4)return layer.left+getWidth(layer);if(isMinIE4)return layer.style.pixelLeft+getWidth(layer);return-1;}function getBottom(layer){if(isMinNS4)return layer.top+getHeight(layer);if(isMinIE4)return layer.style.pixelTop+getHeight(layer);return-1;}function getPageLeft(layer){var x;if(isMinNS4)return layer.pageX;if(isMinIE4){x=0;while(layer.offsetParent!=null){x+=layer.offsetLeft;layer=layer.offsetParent;}x+=layer.offsetLeft;return x;}return-1;}function getPageTop(layer){var y;if(isMinNS4)return layer.pageY;if(isMinIE4){y=0;while(layer.offsetParent!=null){y+=layer.offsetTop;layer=layer.offsetParent;}y+=layer.offsetTop;return y;}return-1;}function getWidth(layer){if(isMinNS4){if(layer.document.width)return layer.document.width;else return layer.clip.right-layer.clip.left;}if(isMinIE4){if(layer.style.pixelWidth)return layer.style.pixelWidth;else return layer.clientWidth;}return-1;}function getHeight(layer){if(isMinNS4){if(layer.document.height)return layer.document.height;else return layer.clip.bottom-layer.clip.top;}if(isMinIE4){if(layer.style.pixelHeight)return layer.style.pixelHeight;else return layer.clientHeight;}return-1;}function getzIndex(layer){if(isMinNS4)return layer.zIndex;if(isMinIE4)return layer.style.zIndex;return-1;}function setzIndex(layer,z){if(isMinNS4)layer.zIndex=z;if(isMinIE4)layer.style.zIndex=z;}function clipLayer(layer,clipleft,cliptop,clipright,clipbottom){if(isMinNS4){layer.clip.left=clipleft;layer.clip.top=cliptop;layer.clip.right=clipright;layer.clip.bottom=clipbottom;}if(isMinIE4)layer.style.clip=\'rect(\'+cliptop+\' \'+clipright+\' \'+clipbottom+\' \'+clipleft+\')\';}function getClipLeft(layer){if(isMinNS4)return layer.clip.left;if(isMinIE4){var str=layer.style.clip;if(!str)return 0;var clip=getIEClipValues(layer.style.clip);return(clip[3]);}return-1;}function getClipTop(layer){if(isMinNS4)return layer.clip.top;if(isMinIE4){var str=layer.style.clip;if(!str)return 0;var clip=getIEClipValues(layer.style.clip);return clip[0];}return-1;}function getClipRight(layer){if(isMinNS4)return layer.clip.right;if(isMinIE4){var str=layer.style.clip;if(!str)return layer.style.pixelWidth;var clip=getIEClipValues(layer.style.clip);return clip[1];}return-1;}function getClipBottom(layer){if(isMinNS4)return layer.clip.bottom;if(isMinIE4){var str=layer.style.clip;if(!str)return layer.style.pixelHeight;var clip=getIEClipValues(layer.style.clip);return clip[2];}return-1;}function getClipWidth(layer){if(isMinNS4)return layer.clip.width;if(isMinIE4){var str=layer.style.clip;if(!str)return layer.style.pixelWidth;var clip=getIEClipValues(layer.style.clip);return clip[1]-clip[3];}return-1;}function getClipHeight(layer){if(isMinNS4)return layer.clip.height;if(isMinIE4){var str=layer.style.clip;if(!str)return layer.style.pixelHeight;var clip=getIEClipValues(layer.style.clip);return clip[2]-clip[0];}return-1;}function getIEClipValues(str){var clip=new Array();var i;i=str.indexOf(\"(\");clip[0]=parseInt(str.substring(i+1,str.length),10);i=str.indexOf(\" \",i+1);clip[1]=parseInt(str.substring(i+1,str.length),10);i=str.indexOf(\" \",i+1);clip[2]=parseInt(str.substring(i+1,str.length),10);i=str.indexOf(\" \",i+1);clip[3]=parseInt(str.substring(i+1,str.length),10);return clip;}function scrollLayerTo(layer,x,y,bound){var dx=getClipLeft(layer)-x;var dy=getClipTop(layer)-y;scrollLayerBy(layer,-dx,-dy,bound);}function scrollLayerBy(layer,dx,dy,bound){var cl=getClipLeft(layer);var ct=getClipTop(layer);var cr=getClipRight(layer);var cb=getClipBottom(layer);if(bound){if(cl+dx<0)dx=-cl;else if(cr+dx>getWidth(layer))dx=getWidth(layer)-cr;if(ct+dy<0)dy=-ct;else if(cb+dy>getHeight(layer))dy=getHeight(layer)-cb;}clipLayer(layer,cl+dx,ct+dy,cr+dx,cb+dy);moveLayerBy(layer,-dx,-dy);}function setBgColor(layer,color){if(isMinNS4)layer.bgColor=color;if(isMinIE4)layer.style.backgroundColor=color;}function setBgImage(layer,src){if(isMinNS4)layer.background.src=src;if(isMinIE4)layer.style.backgroundImage=\"url(\"+src+\")\";}function getLayer(name){if(isMinNS4)return findLayer(name,document);if(isMinIE4)return eval(\'document.all.\'+name);return null;}function findLayer(name,doc){var i,layer;for(i=0;i<doc.layers.length;i++){layer=doc.layers[i];if(layer.name==name)return layer;if(layer.document.layers.length>0)if((layer=findLayer(name,layer.document))!=null)return layer;}return null;}function getImage(name){if(isMinNS4){return findImage(name,document);}if(isMinIE4)return eval(\'document.all.\'+name);return null;}function findImage(name,doc){var i,img;for(i=0;i<doc.images.length;i++)if(doc.images[i].name==name)return doc.images[i];for(i=0;i<doc.layers.length;i++)if((img=findImage(name,doc.layers[i].document))!=null){img.container=doc.layers[i];return img;}return null;}function getImagePageLeft(img){var x,obj;if(isMinNS4){if(img.container!=null)return img.container.pageX+img.x;else return img.x;}if(isMinIE4){x=0;obj=img;while(obj.offsetParent!=null){x+=obj.offsetLeft;obj=obj.offsetParent;}x+=obj.offsetLeft;return x;}return-1;}function getImagePageTop(img){var y,obj;if(isMinNS4){if(img.container!=null)return img.container.pageY+img.y;else return img.y;}if(isMinIE4){y=0;obj=img;while(obj.offsetParent!=null){y+=obj.offsetTop;obj=obj.offsetParent;}y+=obj.offsetTop;return y;}return-1;}function getWindowWidth(){if(isMinNS4)return window.innerWidth;if(isMinIE4)return document.body.clientWidth;return-1;}function getWindowHeight(){if(isMinNS4)return window.innerHeight;if(isMinIE4)return document.body.clientHeight;return-1;}function getPageWidth(){if(isMinNS4)return document.width;if(isMinIE4)return document.body.scrollWidth;return-1;}function getPageHeight(){if(isMinNS4)return document.height;if(isMinIE4)return document.body.scrollHeight;return-1;}function getPageScrollX(){if(isMinNS4)return window.pageXOffset;if(isMinIE4)return document.body.scrollLeft;return-1;}function getPageScrollY(){if(isMinNS4)return window.pageYOffset;if(isMinIE4)return document.body.scrollTop;return-1;}var isMinIE5_5=(isMinIE5&&navigator.appVersion.indexOf(\"5.5\")>=0)?1:0;var navBars=new Array();function NavBarMenuItem(text,link){this.text=text;this.link=link;}function NavBarMenu(hdrWidth,menuWidth){this.hdrWidth=hdrWidth;this.width=menuWidth;this.height=0;this.items=new Array();this.addItem=navBarMenuAddItem;}function navBarMenuAddItem(item){this.items[this.items.length]=item;}function NavBar(width){this.x=0;this.y=0;this.width=width;this.height=0;this.align=\"left\";this.minWidth=0;this.inverted=false;this.menus=new Array();this.created=false;this.border=2;this.padding=4;this.separator=1;this.borderColor=\"#000000\";this.hdrFgColor=\"#000000\";this.hdrBgColor=\"#999999\";this.hdrHiFgColor=\"#ffffff\";this.hdrHiBgColor=\"#666666\";this.itmFgColor=\"#000000\";this.itmBgColor=\"#cccccc\";this.itmHiFgColor=\"#ffffff\";this.itmHiBgColor=\"#000080\";this.hdrFontFamily=\"Arial,Helvetica,sans-serif\";this.hdrFontStyle=\"plain\";this.hdrFontWeight=\"bold\";this.hdrFontSize=\"10pt\";this.itmFontFamily=\"MS Sans Serif,Arial,Helvetica,sans-serif\";this.itmFontStyle=\"plain\";this.itmFontWeight=\"bold\";this.itmFontSize=\"8pt\";this.setSizes=navBarSetSizes;this.setColors=navBarSetColors;this.setFonts=navBarSetFonts;this.addMenu=navBarAddMenu;this.create=navBarCreate;this.hide=navBarHide;this.show=navBarShow;this.moveTo=navBarMoveTo;this.moveBy=navBarMoveBy;this.getzIndex=navBarGetzIndex;this.setzIndex=navBarSetzIndex;this.getWidth=navBarGetWidth;this.getMinWidth=navBarGetMinWidth;this.getAlign=navBarGetAlign;this.setAlign=navBarSetAlign;this.resize=navBarResize;this.invert=navBarInvert;this.isInverted=navBarIsInverted;this.index=navBars.length;navBars[this.index]=this;}function navBarSetSizes(border,padding,separator){if(!this.created){this.border=border;this.padding=padding;this.separator=separator;}}function navBarSetColors(bdColor,hdrFgColor,hdrBgColor,hdrHiFgColor,hdrHiBgColor,itmFgColor,itmBgColor,itmHiFgColor,itmHiBgColor){if(!this.created){this.borderColor=bdColor;this.hdrFgColor=hdrFgColor;this.hdrBgColor=hdrBgColor;this.hdrHiFgColor=hdrHiFgColor;this.hdrHiBgColor=hdrHiBgColor;this.itmFgColor=itmFgColor;this.itmBgColor=itmBgColor;this.itmHiFgColor=itmHiFgColor;this.itmHiBgColor=itmHiBgColor;}}function navBarSetFonts(hdrFamily,hdrStyle,hdrWeight,hdrSize,itmFamily,itmStyle,itmWeight,itmSize){if(!this.created){this.hdrFontFamily=hdrFamily;this.hdrFontStyle=hdrStyle;this.hdrFontWeight=hdrWeight;this.hdrFontSize=hdrSize;this.itmFontFamily=itmFamily;this.itmFontStyle=itmStyle;this.itmFontWeight=itmWeight;this.itmFontSize=itmSize;}}function navBarAddMenu(menu){if(!this.created)this.menus[this.menus.length]=menu;}function navBarCreate(){var str;var i,j;var norm,high,end;var width,height;var x,y;var scrX,scrY;if(this.created||(!isMinNS4&&!isMinIE4))return;str=\"\";if(isMinIE4&&!isMinIE5){scrX=getPageScrollX();scrY=getPageScrollY();window.scrollTo(getPageWidth(),getPageHeight());}if(isMinNS4)str+=\'<layer name=\"navBar\'+this.index+\'_filler\"></layer><layer name=\"navBar\'+this.index+\'_hdrsBase\">\';if(isMinIE4)str+=\'<div id=\"navBar\'+this.index+\'_filler\" style=\"position:absolute;\"></div><div id=\"navBar\'+this.index+\'_hdrsBase\" style=\"position:absolute;\">\';for(i=0;i<this.menus.length;i++){norm=\'<table border=0 cellpadding=\'+this.padding+\' cellspacing=0\'+(this.menus[i].hdrWidth>0?\' width=\'+this.menus[i].hdrWidth:\'\')+((isMinIE4&&!isMinIE5)?\' id=\"navBar\'+this.index+\'_tbl\'+i+\'\"\':\'\')+\'><tr><td\'+(this.menus[i].hdrWidth==0?\' nowrap=1\'+this.menus[i].hdrWidth:\'\')+\'><span style=\"color:\'+this.hdrFgColor+\';font-family:\'+this.hdrFontFamily+\';font-size:\'+this.hdrFontSize+\';font-style:\'+this.hdrFontStyle+\';font-weight:\'+this.hdrFontWeight+\';\">\';high=\'<table border=0 cellpadding=\'+this.padding+\' cellspacing=0\'+(this.menus[i].hdrWidth>0?\' width=\'+this.menus[i].hdrWidth:\'\')+\'><tr><td\'+(this.menus[i].hdrWidth==0?\' nowrap=1\'+this.menus[i].hdrWidth:\'\')+\'><span style=\"color:\'+this.hdrHiFgColor+\';font-family:\'+this.hdrFontFamily+\';font-size:\'+this.hdrFontSize+\';font-style:\'+this.hdrFontStyle+\';font-weight:\'+this.hdrFontWeight+\';\">\';end=\'</span></td></tr></table>\';if(isMinNS4)str+=\'<layer name=\"navBar\'+this.index+\'_head\'+i+\'\">\'+norm+this.menus[i].items[0].text+end+\'</layer><layer name=\"navBar\'+this.index+\'_headHigh\'+i+\'\">\'+high+this.menus[i].items[0].text+end+\'</layer><layer name=\"navBar\'+this.index+\'_headDummy\'+i+\'\"></layer>\';if(isMinIE4){str+=\'<div id=\"navBar\'+this.index+\'_head\'+i+\'\" style=\"position:absolute;\">\'+norm+this.menus[i].items[0].text+end+\'</div><div id=\"navBar\'+this.index+\'_headHigh\'+i+\'\" style=\"position:absolute;\">\'+high+this.menus[i].items[0].text+end+\'</div><div id=\"navBar\'+this.index+\'_headDummy\'+i+\'\" style=\"position:absolute;\">\';if(isMinIE5_5)str+=\'<table cellspacing=0 width=\"100%\" height=\"100%\"><tr><td></td></tr></table>\';str+=\'</div>\';}}if(isMinNS4){str+=\'</layer>\';this.baseLayer=new Layer(this.width);this.baseLayer.document.open();this.baseLayer.document.write(str);this.baseLayer.document.close();}if(isMinIE4){str+=\'</div>\';str=\'<div id=\"navBar\'+this.index+\'\" style=\"position:absolute;left:0px;top:0px;cursor:hand;\">\'+str+\'</div>\';document.body.insertAdjacentHTML(\"beforeEnd\",str);this.baseLayer=getLayer(\"navBar\"+this.index);}width=0;height=0;for(i=0;i<this.menus.length;i++){this.menus[i].hdrNormLayer=getLayer(\'navBar\'+this.index+\'_head\'+i);this.menus[i].hdrHighLayer=getLayer(\'navBar\'+this.index+\'_headHigh\'+i);this.menus[i].hdrDmmyLayer=getLayer(\'navBar\'+this.index+\'_headDummy\'+i);height=Math.max(height,getHeight(this.menus[i].hdrNormLayer));this.height=height+2*this.border;if(isMinIE4&&!isMinIE5){width=this.menus[i].hdrWidth;if(width==0)width=eval(\'document.all.navBar\'+this.index+\'_tbl\'+i+\'.clientWidth\');navBarIEResizeLayer(this.menus[i].hdrNormLayer,width,height);navBarIEResizeLayer(this.menus[i].hdrHighLayer,width,height);navBarIEResizeLayer(this.menus[i].hdrDmmyLayer,width,height);}}x=this.border;y=this.border;for(i=0;i<this.menus.length;i++){width=Math.max(this.menus[i].hdrWidth,getWidth(this.menus[i].hdrNormLayer));if(this.menus[i].width==0)this.menus[i].width=width+2*this.border;moveLayerTo(this.menus[i].hdrNormLayer,x,y);setBgColor(this.menus[i].hdrNormLayer,this.hdrBgColor);clipLayer(this.menus[i].hdrNormLayer,0,0,width,height);inheritLayer(this.menus[i].hdrNormLayer);moveLayerTo(this.menus[i].hdrHighLayer,x,y);setBgColor(this.menus[i].hdrHighLayer,this.hdrHiBgColor);clipLayer(this.menus[i].hdrHighLayer,0,0,width,height);hideLayer(this.menus[i].hdrHighLayer);moveLayerTo(this.menus[i].hdrDmmyLayer,x,y);if(isMinIE4)navBarIEResizeLayer(this.menus[i].hdrDmmyLayer,width,height);clipLayer(this.menus[i].hdrDmmyLayer,0,0,width,height);inheritLayer(this.menus[i].hdrDmmyLayer);this.menus[i].hdrDmmyLayer.highLayer=this.menus[i].hdrHighLayer;this.menus[i].hdrLeft=x;x+=width+this.border;this.menus[i].hdrRight=x;}this.minWidth=x;this.width=Math.max(this.minWidth,this.width);moveLayerTo(this.baseLayer,this.x,this.y);setBgColor(this.baseLayer,this.borderColor);if(isMinIE4)navBarIEResizeLayer(this.baseLayer,this.width,this.height);clipLayer(this.baseLayer,0,0,this.width,this.height);this.fillerLayer=getLayer(\'navBar\'+this.index+\'_filler\');moveLayerTo(this.fillerLayer,this.border,this.border);setBgColor(this.fillerLayer,this.hdrBgColor);width=this.width-2*this.border;height=this.height-2*this.border;if(isMinIE4)navBarIEResizeLayer(this.fillerLayer,width,height);clipLayer(this.fillerLayer,0,0,width,height);inheritLayer(this.fillerLayer);this.hdrsBaseLayer=getLayer(\'navBar\'+this.index+\'_hdrsBase\');if(this.align==\"left\")this.hdrsOffsetX=0;else if(this.align==\"center\")this.hdrsOffsetX=Math.round((this.width-this.minWidth)/2);else if(this.align==\"right\")this.hdrsOffsetX=this.width-this.minWidth;else this.hdrsOffsetX=Math.min(parseInt(this.align,10),this.width-this.minWidth);moveLayerTo(this.hdrsBaseLayer,this.hdrsOffsetX,0);setBgColor(this.hdrsBaseLayer,this.borderColor);if(isMinIE4)navBarIEResizeLayer(this.hdrsBaseLayer,this.minWidth,this.height);clipLayer(this.hdrsBaseLayer,0,0,this.minWidth,this.height);inheritLayer(this.hdrsBaseLayer);for(i=0;i<this.menus.length;i++){this.menus[i].hdrDmmyLayer.index=this.index;this.menus[i].hdrDmmyLayer.offsetX=this.menus[i].hdrLeft-this.border;if(this.menus[i].hdrDmmyLayer.offsetX+this.menus[i].width>this.width)this.menus[i].hdrDmmyLayer.offsetX=this.menus[i].hdrRight-this.menus[i].width;this.menus[i].hdrDmmyLayer.offsetY=this.height-this.border;this.menus[i].hdrDmmyLayer.onmouseover=navBarHeaderOn;this.menus[i].hdrDmmyLayer.onmouseout=navBarHeaderOff;if(isMinNS4){this.menus[i].hdrDmmyLayer.document.highLayer=this.menus[i].hdrHighLayer;this.menus[i].hdrDmmyLayer.document.link=this.menus[i].items[0].link;this.menus[i].hdrDmmyLayer.document.captureEvents(Event.MOUSEUP);this.menus[i].hdrDmmyLayer.document.onmouseup=navBarItemClick;}if(isMinIE4){this.menus[i].hdrDmmyLayer.highLayer=this.menus[i].hdrHighLayer;this.menus[i].hdrDmmyLayer.link=this.menus[i].items[0].link;this.menus[i].hdrDmmyLayer.onclick=navBarItemClick;}}norm=\'<table border=0 cellpadding=\'+this.padding+\' cellspacing=0 width=\"100%\"><tr><td><span style=\"color:\'+this.itmFgColor+\';font-family:\'+this.itmFontFamily+\';font-size:\'+this.itmFontSize+\';font-style:\'+this.itmFontStyle+\';font-weight:\'+this.itmFontWeight+\'; cursor: hand;\">\';high=\'<table border=0 cellpadding=\'+this.padding+\' cellspacing=0 width=\"100%\"><tr><td><span style=\"color:\'+this.itmHiFgColor+\';font-family:\'+this.itmFontFamily+\';font-size:\'+this.itmFontSize+\';font-style:\'+this.itmFontStyle+\';font-weight:\'+this.itmFontWeight+\'; cursor: hand;\">\';end=\'</span></td></tr></table>\';for(i=0;i<this.menus.length;i++){width=this.menus[i].width-2*this.border;str=\"\";for(j=1;j<this.menus[i].items.length;j++){if(isMinNS4)str+=\'<layer name=\"navBar\'+this.index+\'_menu\'+i+\'_norm\'+j+\'\" width=\'+width+\'>\'+norm+this.menus[i].items[j].text+end+\'</layer><layer name=\"navBar\'+this.index+\'_menu\'+i+\'_high\'+j+\'\" width=\'+width+\'>\'+high+this.menus[i].items[j].text+end+\'</layer><layer name=\"navBar\'+this.index+\'_menu\'+i+\'_dmmy\'+j+\'\" width=\'+width+\'></layer>\';if(isMinIE4){str+=\'<div id=\"navBar\'+this.index+\'_menu\'+i+\'_norm\'+j+\'\" style=\"position:absolute;width:\'+width+\'px;cursor:hand;\">\'+norm+this.menus[i].items[j].text+end+\'</div><div id=\"navBar\'+this.index+\'_menu\'+i+\'_high\'+j+\'\" style=\"position:absolute;width:\'+width+\'px;cursor:hand;\">\'+high+this.menus[i].items[j].text+end+\'</div><div id=\"navBar\'+this.index+\'_menu\'+i+\'_dmmy\'+j+\'\" style=\"position:absolute;width:\'+width+\'px;cursor:hand;\">\';if(isMinIE5_5)str+=\'<table cellspacing=0 width=\"100%\" height=\"100%\"><tr><td></td></tr></table>\';str+=\'</div>\';}}if(isMinNS4){this.menus[i].baseLayer=new Layer(this.menus[i].width);this.menus[i].baseLayer.document.open();this.menus[i].baseLayer.document.write(str);this.menus[i].baseLayer.document.close();}if(isMinIE4){str=\'<div id=\"navBar\'+this.index+\'_menu\'+i+\'\" style=\"position:absolute;left:0px; top:0px;width:\'+this.menus[i].width+\'px;visibility:hidden;cursor:hand;\">\'+str+\'</div>\';document.body.insertAdjacentHTML(\"beforeEnd\",str);this.menus[i].baseLayer=getLayer(\"navBar\"+this.index+\"_menu\"+i);}}if(isMinIE4&&!isMinIE5)window.scrollTo(x,y);for(i=0;i<this.menus.length;i++){moveLayerTo(this.menus[i].baseLayer,this.menus[i].hdrDmmyLayer.offsetX,this.menus[i].hdrDmmyLayer.offsetY);setBgColor(this.menus[i].baseLayer,this.borderColor);if(this.menus[i].items.length>1){this.menus[i].hdrDmmyLayer.menuLayer=this.menus[i].baseLayer;if(isMinNS4)this.menus[i].hdrDmmyLayer.document.menuLayer=this.menus[i].baseLayer;}else{this.menus[i].hdrDmmyLayer.menuLayer=null;if(isMinNS4)this.menus[i].hdrDmmyLayer.document.menuLayer=this.menus[i].baseLayer;}x=this.border;y=this.border;width=this.menus[i].width-2*this.border;for(j=1;j<this.menus[i].items.length;j++){this.menus[i].items[j].normLayer=getLayer(\'navBar\'+this.index+\'_menu\'+i+\'_norm\'+j);this.menus[i].items[j].highLayer=getLayer(\'navBar\'+this.index+\'_menu\'+i+\'_high\'+j);this.menus[i].items[j].dmmyLayer=getLayer(\'navBar\'+this.index+\'_menu\'+i+\'_dmmy\'+j);height=getHeight(this.menus[i].items[j].normLayer);moveLayerTo(this.menus[i].items[j].normLayer,x,y);setBgColor(this.menus[i].items[j].normLayer,this.itmBgColor);clipLayer(this.menus[i].items[j].normLayer,0,0,width,height);inheritLayer(this.menus[i].items[j].normLayer);moveLayerTo(this.menus[i].items[j].highLayer,x,y);setBgColor(this.menus[i].items[j].highLayer,this.itmHiBgColor);clipLayer(this.menus[i].items[j].highLayer,0,0,width,height);hideLayer(this.menus[i].items[j].highLayer);moveLayerTo(this.menus[i].items[j].dmmyLayer,x,y);if(isMinIE4)navBarIEResizeLayer(this.menus[i].items[j].dmmyLayer,width,height);clipLayer(this.menus[i].items[j].dmmyLayer,0,0,width,height);inheritLayer(this.menus[i].items[j].dmmyLayer);this.menus[i].items[j].dmmyLayer.highLayer=this.menus[i].items[j].highLayer;this.menus[i].items[j].dmmyLayer.onmouseover=navBarItemOn;this.menus[i].items[j].dmmyLayer.onmouseout=navBarItemOff;if(isMinNS4){this.menus[i].items[j].dmmyLayer.document.highLayer=this.menus[i].items[j].highLayer;this.menus[i].items[j].dmmyLayer.document.parentHighLayer=this.menus[i].hdrHighLayer;this.menus[i].items[j].dmmyLayer.document.menuLayer=this.menus[i].baseLayer;this.menus[i].items[j].dmmyLayer.document.link=this.menus[i].items[j].link;this.menus[i].items[j].dmmyLayer.document.captureEvents(Event.MOUSEUP);this.menus[i].items[j].dmmyLayer.document.onmouseup=navBarItemClick;}if(isMinIE4){this.menus[i].items[j].dmmyLayer.highLayer=this.menus[i].items[j].highLayer;this.menus[i].items[j].dmmyLayer.parentHighLayer=this.menus[i].hdrHighLayer;this.menus[i].items[j].dmmyLayer.menuLayer=this.menus[i].baseLayer;this.menus[i].items[j].dmmyLayer.link=this.menus[i].items[j].link;this.menus[i].items[j].dmmyLayer.onclick=navBarItemClick;}y+=height+this.separator;}width=this.menus[i].width;height=y-this.separator+this.border;this.menus[i].baseLayer.width=this.menus[i].width;this.menus[i].baseLayer.height=height;if(isMinIE4)navBarIEResizeLayer(this.menus[i].baseLayer,width,height);clipLayer(this.menus[i].baseLayer,0,0,width,height);this.menus[i].baseLayer.parentHighLayer=this.menus[i].hdrHighLayer;this.menus[i].baseLayer.onmouseout=navBarMenuOff;}this.created=true;this.resize(this.width);showLayer(this.baseLayer);}function navBarHide(){if(this.created)hideLayer(this.baseLayer);}function navBarShow(){if(this.created)showLayer(this.baseLayer);}function navBarMoveTo(x,y){this.x=x;this.y=y;if(this.created)moveLayerTo(this.baseLayer,this.x,this.y);}function navBarMoveBy(dx,dy){this.x+=dx;this.y+=dy;if(this.created)moveLayerTo(this.baseLayer,this.x,this.y);}function navBarGetzIndex(){if(this.created)return getzIndex(this.baseLayer);return 0;}function navBarSetzIndex(z){var i;if(this.created){setzIndex(this.baseLayer,z);for(i=0;i<this.menus.length;i++)setzIndex(this.menus[i].baseLayer,z);}}function navBarGetWidth(){return this.width;}function navBarGetMinWidth(){return this.minWidth;}function navBarGetAlign(){return this.align;}function navBarSetAlign(align){this.align=align;if(this.created)this.resize(this.width);}function navBarResize(width){if(this.created){this.width=Math.max(width,this.minWidth);if(isMinIE4){navBarIEResizeLayer(this.fillerLayer,this.width-2*this.border,this.height-2*this.border);navBarIEResizeLayer(this.baseLayer,this.width,this.height);}clipLayer(this.fillerLayer,0,0,this.width-2*this.border,this.height-2*this.border);clipLayer(this.baseLayer,0,0,this.width,this.height);if(this.align==\"left\")this.hdrsOffsetX=0;else if(this.align==\"center\")this.hdrsOffsetX=Math.round((this.width-this.minWidth)/2);else if(this.align==\"right\")this.hdrsOffsetX=this.width-this.minWidth;else this.hdrsOffsetX=Math.min(parseInt(this.align,10),this.width-this.minWidth);moveLayerTo(this.hdrsBaseLayer,this.hdrsOffsetX,0);for(i=0;i<this.menus.length;i++){this.menus[i].hdrDmmyLayer.offsetX=this.menus[i].hdrLeft-this.border;if(this.hdrsOffsetX+this.menus[i].hdrDmmyLayer.offsetX+this.menus[i].width>this.width)this.menus[i].hdrDmmyLayer.offsetX=this.menus[i].hdrRight-this.menus[i].width;}}else this.width=width;}function navBarInvert(){this.inverted=!this.inverted;}function navBarIsInverted(){return this.inverted;}function navBarIEResizeLayer(layer,width,height){layer.style.pixelWidth=width;layer.style.pixelHeight=height;}function navBarHeaderOn(e){var bar;var x,y;bar=navBars[this.index];if(this.menuLayer!=null){x=bar.x+bar.hdrsOffsetX+this.offsetX;y=bar.y+this.offsetY;if(bar.inverted)y=bar.y-this.menuLayer.height+bar.border;moveLayerTo(this.menuLayer,x,y);this.menuLayer.left=x;this.menuLayer.top=y;this.menuLayer.right=this.menuLayer.left+this.menuLayer.width;this.menuLayer.bottom=this.menuLayer.top+this.menuLayer.height;if(isMinIE4){this.menuLayer.left+=2;this.menuLayer.right-=2;if(bar.inverted){this.menuLayer.top+=2;this.menuLayer.bottom+=2;}else{this.menuLayer.top-=2;this.menuLayer.bottom-=2;}}}if(isMinIE4){if(bar.activeHeader!=null&&bar.activeHeader!=this){hideLayer(bar.activeHeader.highLayer);if(bar.activeHeader.menuLayer!=null)hideLayer(bar.activeHeader.menuLayer);}bar.activeHeader=this;}showLayer(this.highLayer);if(this.menuLayer!=null)showLayer(this.menuLayer);}function navBarHeaderOff(e){if(this.menuLayer!=null){if(isMinIE4){mouseX=window.event.clientX+document.body.scrollLeft;mouseY=window.event.clientY+document.body.scrollTop;}if(mouseX>=this.menuLayer.left&&mouseX<=this.menuLayer.right&&mouseY>=this.menuLayer.top&&mouseY<=this.menuLayer.bottom)return;hideLayer(this.menuLayer);}hideLayer(this.highLayer);}function navBarMenuOff(e){if(isMinIE4){mouseX=window.event.clientX+document.body.scrollLeft;mouseY=window.event.clientY+document.body.scrollTop;if(mouseX>=this.left&&mouseX<=this.right&&mouseY>=this.top&&mouseY<=this.bottom)return;}hideLayer(this);hideLayer(this.parentHighLayer);}function navBarItemOn(){showLayer(this.highLayer);}function navBarItemOff(){hideLayer(this.highLayer);}function navBarItemClick(e){if(this.link==\"\")return true;if(this.menuLayer!=null){hideLayer(this.menuLayer);}if(this.parentHighLayer!=null){hideLayer(this.parentHighLayer);}hideLayer(this.highLayer);if(this.link.indexOf(\"javascript:\")==0)eval(this.link);else window.location.href=this.link;return true;}var mouseX=0;var mouseY=0;if(isMinNS4)document.captureEvents(Event.MOUSEMOVE);document.onmousemove=navBarGetMousePosition;function navBarGetMousePosition(e){if(isMinNS4){mouseX=e.pageX;mouseY=e.pageY;}if(isMinIE4){mouseX=window.event.clientX+document.body.scrollLeft;mouseY=window.event.clientY+document.body.scrollTop;}}var origWidth;var origHeight;if(isMinNS4){origWidth=window.innerWidth;origHeight=window.innerHeight;}\r";

	functionString += "\r";

	functionString += "//window.onresize=navBarReload;\r";

	functionString += "\r";

	functionString += "function navBarReload(){\r";

	functionString += "if(isMinNS4&&origWidth==window.innerWidth&&origHeight==window.innerHeight)return;\r";

	functionString += "if(isMinIE4)setTimeout(\'window.location.href = window.location.href\',0);\r";

	functionString += "else window.location.href=window.location.href;\r";

	functionString += "}\r";

  }





  public void set_navBarReloadonresize(){

      functionString += "window.onresize=navBarReload;\r\r";

  }



  private void set_initFunction(){

	initFunction_begin = "\r";

	initFunction_begin += "var fullWidth;\r";

	initFunction_begin += "\r";

	initFunction_begin += "function init() {\r";

	initFunction_begin += "\r";

	initFunction_begin += "  // Get width of window, need to account for scrollbar width in Netscape.\r";

	initFunction_begin += "\r";

	initFunction_begin += "  fullWidth = getWindowWidth()\r";

	initFunction_begin += "    - (isMinNS4 && getWindowHeight() < getPageHeight() ? 16 : 0);\r";

	initFunction_begin += "\r";

	initFunction_begin += "  NavBar1.create();\r";



        initFunction_var = "\r";

	initFunction_var += "  NavBar1.moveTo("+x_pos+", "+y_pos+");\r";

	initFunction_var += "  NavBar1.setzIndex(2);\r";



        initFunction_end = "}\r";

	initFunction_end += "\r";

  }



  public void addTo_initFunction(String toAdd){

    initFunction_var += toAdd;

  }







  private String get_initString(){

    return initString;

  }



  private String get_functionString(){

    return functionString;

  }



  private String get_varString(){

    return varString;

  }



  private String get_scriptString(){

    setNavBarSettings();

    scriptString += "\r" + scriptCodeInit + "\r" + NavBarSettings + "\r" + scriptCode + "\r" + addFunctions() + "\r";

    return scriptString;

  }



  private String get_initFunction(){

    return  initFunction_begin + initFunction_var + initFunction_end;

  }





  public void setPosition(int x, int y){

    x_pos = x;

    y_pos = y;

  }



  public void addItem(String MenuName, String ItemName, String url){

    scriptCode += MenuName + ".addItem(new NavBarMenuItem( \"" + ItemName + "\" , \"" + url + "\" ));\r" ;

  }



  public void addItem(String MenuName, String ItemName){

    scriptCode += MenuName + ".addItem(new NavBarMenuItem( \"" + ItemName + "\" ));\r" ;

  }





  public void scaleNavBar(){

    set_navBarReloadonresize();

    scaleNavBar = true;

  }



  public void addMenu(String MenuName, int hdrWidth, int menuWidth){

    if (scriptCodeInit == null || varString == null){

      if(scaleNavBar)

        addNavBar();

      else

        addNavBar(0);



    }





    varString += "var " + MenuName + ";\r";



    scriptCodeInit +=  MenuName + " = new NavBarMenu("+hdrWidth+","+menuWidth+");\r";



    addFunction[counter++] = "NavBar1.addMenu(" + MenuName + ");\r";



  }



  private String addFunctions(){

    String temp = "\r";

    int i = 0;

    if (addFunction!= null){

      while (addFunction[i] != null){

        temp += addFunction[i] + "\r";

      i++;

      }

    }

    return temp;

  }





  private void addNavBar(){

    varString = "var NavBar1;\r";

    scriptCodeInit = "NavBar1 = new NavBar(getWindowWidth());\r";

  }





  private void addNavBar(int width){

    varString = "var NavBar1;\r";

    scriptCodeInit = "NavBar1 = new NavBar("+width+");\r";

  }



  private void setNavBarSettings(){

    addDefaultNavBarSettings();

    NavBarSettings = "\r";

    NavBarSettings += NavBar_setSizes;

    NavBarSettings += NavBar_setColors;

    NavBarSettings += NavBar_setFonts;

    NavBarSettings += "\r";

  }





  private void addDefaultNavBarSettings(){



    if (NavBar_setSizes == null)

      NavBar_setSizes = "NavBar1.setSizes(1, 1, 0);\r";



    if (NavBar_setColors == null)

      NavBar_setColors = "NavBar1.setColors(\"#444444\",  \"#FFFFFF\", \"#BDBDBD\", \"#444444\", \"#F2BC00\",  \"#444444\", \"#BDBDBD\", \"#F2BC00\", \"#444444\");\r";



    if ( NavBar_setFonts == null)

      NavBar_setFonts =  "NavBar1.setFonts(\"Arial, Helvetica, sans-serif\", \"normal\", \"normal\", \"8pt\",\"Arial, Helvetica, sans-serif\", \"normal\", \"normal\", \"8pt\");\r";



  }



  public void setSizes(int border, int padding, int separator){

    NavBar_setSizes = "NavBar1.setSizes("+border+", "+padding+", "+separator+");\r";

  }



  public void setColors( String bdColor, String hdrFgColor,String hdrBgColor, String hdrHiFgColor, String hdrHiBgColor, String itmFgColor, String itmBgColor, String itmHiFgColor, String itmHiBgColor ){

    NavBar_setColors = "NavBar1.setColors(\"" + bdColor + "\",\"" + hdrFgColor + "\",\"" + hdrBgColor + "\",\"" + hdrHiFgColor + "\",\"" + hdrHiBgColor + "\",\"" + itmFgColor + "\",\"" + itmBgColor + "\",\"" + itmHiFgColor + "\",\"" + itmHiBgColor + "\");\r";

  }



  public void setFonts( String hdrFamily1, String hdrFamily2, String hdrFamily3, String hdrStyle, String hdrWeight, int hdrSize, String itmFamily1, String itmFamily2, String itmFamily3, String itmStyle, String itmWeight, int itmSize){

    NavBar_setFonts = "NavBar1.setFonts(\"" + hdrFamily1 + "," +hdrFamily2+ "," +hdrFamily3+ "\",\"" +hdrStyle+ "\",\"" +hdrWeight+ "\",\"" +hdrSize+ "pt\",\"" +itmFamily1+ "," +itmFamily2+ "," +itmFamily3+ "\",\"" +itmStyle+ "\",\"" +itmWeight+ "\",\"" +itmSize+ "pt\");\r";;

  }





  private void setScriptSource(String Source){

    ScriptObject.removeFunction("menu");

    ScriptObject.addFunction("menu",Source);

  }





  public void main(IWContext iwc) throws Exception{

      set_initFunction();

      this.getParentPage().setOnLoad("init()");

      setScriptSource( get_initString() + get_functionString() + get_varString() + get_scriptString() + get_initFunction() );

  }





	/* (non-Javadoc)
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(com.idega.presentation.IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
} // Class MenuBar
/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	ADxMenu.js - AD xMenu v2.01
	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	Copyright (c) Aleksandar Vacic, aleck@sezampro.yu, www.aplus.co.yu
	## This work is licensed under the Creative Commons Attribution-ShareAlike License.
	## To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/1.0/ or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA
	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	Credits: Mike Foster for x functions (cross-browser.com)
	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	optional usability script: checks for off-viewport positioning and windowed controls
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/

/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	central object: keeps track of viewport size, initializes menus
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
var ADXM_oX = new function() {
	//	add the menu to the array - dummy for not-supporting browsers
	this.Add = function() {};

	//	deny all non-DOM browsers
	if (!document.getElementById && !document.documentElement) return null;
	//	deny non-supporting browsers
	var ua = navigator.userAgent.toLowerCase();
	var nv = (navigator.vendor) ? navigator.vendor.toLowerCase() : "";
	//	deny any Opera, PocketPC, gecko before rv:1.5, safari and omniweb before webkit 100 (should be Safari 1.1)
	//	( nv.indexOf("apple") != -1 && typeof(XMLHttpRequest) != "undefined" ) ==> Safari 1.2. saved here if the check shoudl be raised higher
	if (
		window.opera ||		
		ua.indexOf("windows ce") != -1 ||
		( ua.indexOf("gecko") != -1 && ua.indexOf("khtml") == -1 && parseFloat(ua.substring(ua.indexOf("rv:") + 3)) < 1.5 ) ||
		( nv.indexOf("apple") != -1 && parseFloat(ua.substring(ua.indexOf("applewebkit/") + 12)) < 100 )
	) return null;

	//	keeps the menu IDs (of the main UL)
	this.aMenuids = new Array();
	//	keeps starting menu layouts
	this.aLayouts = new Array();

	//	add the menu to the array
	this.Add = function(sMenuID, sLayout) {
		if ( typeof(sMenuID) == "undefined") return;
		this.aMenuids[this.aMenuids.length] = sMenuID;
		this.aLayouts[this.aLayouts.length] = sLayout;
	};

	//	sets properties, handlers and other stuff for menus
	this.Processmenu = function(oMenu, sLayout, oMainMenu) {
		var aMenuLIs = ADXM_GetChildsByTagName(oMenu, "LI");
		var oMenuLI, oUL, aUL;
		for (var i=0;i<aMenuLIs.length;i++) {
			oMenuLI = aMenuLIs[i];
			aUL = ADXM_GetChildsByTagName(oMenuLI, "UL");	//	UL elements in sub(main) item
			if (aUL.length)	{	//	has submenus
				//	save properties for positioning
				oMenuLI.submenu = aUL[0];
				oMenuLI.menuLayout = sLayout;

				//	save properties for WCH
				if (document.all) {
					oMenuLI.parentMenu = oMenu;
					oMenuLI.mainmenu = oMainMenu;
				}

				//	:hover processing
				oMenuLI.onmouseover = function() {
					//	get the submenu pointer
					var oMenu = this.submenu;
					//	submenu width
					var nW = xWidth(oMenu);
					var nH = xHeight(oMenu);
					//	where should menu be?
					var nTop = ( this.menuLayout == "H" ) ? xHeight(this) : 0;
					var nLeft = ( this.menuLayout == "H" ) ? 0 : xWidth(this);
					//	where is that in the page?
					var nPageX = xPageX(this) + nLeft;
					var nPageY = xPageY(this) + nTop;
					//	* now, check if submenu will go outside the visible window area
					//	get available client dims
					var nClientW = ADXM_oX.nCW;
					var nClientH = ADXM_oX.nCH;
					//	if we know client browser dims, re-position layer
					if ( nClientW != 0 && nClientH != 0 ) {
						var nDiffX = nClientW + xScrollLeft() - (nPageX + nW);
						if ( nDiffX < 0 ) {
							if ( this.menuLayout == "H" ) {
								//	if menu layout is horizontal, then just move to the left, with small safe-factor
								nLeft += (nDiffX - 10);
							} else {
								//	else move it to the right of the item
								nLeft = -nW;
							}
						}
						var nDiffY = nClientH + xScrollTop() - (nPageY + nH);
						if (nDiffY < 0) {
							nTop += nDiffY;
						}
						//	reposition
						xMoveTo(oMenu, nLeft, nTop);
					}

					//	hide windowed controls, if WCH is present
					if ( document.all && !window.opera && typeof(WCH_HideWndCtrl) != "undefined" ) {
						WCH_HideWndCtrl(this.submenu, this);
						this.parentMenu.shownMenu = oMenu;
					}
				};

				if ( document.all && !window.opera ) {
					oMenuLI.onmouseout = function() {
						//	show windowed controls
						if ( typeof(ADXM_HideWCH) != "undefined" )
							ADXM_HideWCH(this.mainmenu);
					};
				}

				//	process the submenu in the same manner. all submenus have vertical layout
				this.Processmenu(aUL[0], "V", oMainMenu);
			}//	if aUL
		}// for aMenuLIs
	};

	//	process all menus
	this.Process = function() {
		var nMenus = this.aMenuids.length, oMenu;
		for (var i=0;i<nMenus;i++) {
			oMenu = document.getElementById(this.aMenuids[i]);
			if (oMenu) {
				this.Processmenu( oMenu, this.aLayouts[i], oMenu );
			}
		}
	};

	//	fetch client width
	this.nCW = 0;
	this.FetchCW = function() {
		this.nCW = xClientWidth();
	};

	//	fetch client height
	this.nCH = 0;
	this.FetchCH = function() {
		this.nCH = xClientHeight();
	};

	//	call this one for window.onresize
	this.Viewport = function() {
		ADXM_oX.FetchCH();
		ADXM_oX.FetchCW();
	};

	//	call this one for window.onload
	this.Init = function() {
		ADXM_oX.Viewport();
		ADXM_oX.Process();
	};
};

/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	set event handlers
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
if (window.addEventListener) {
	window.addEventListener("load", ADXM_oX.Init, false);
} else if (window.attachEvent) {
	window.attachEvent("onload", ADXM_oX.Init);
}
if (window.addEventListener) {
	window.addEventListener("resize", ADXM_oX.Viewport, false);
} else if (window.attachEvent) {
	window.attachEvent("onresize", ADXM_oX.Viewport);
}



/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	return array of child elements with specified tag name. someone pls tell me I missed this one in the DOM2 spec
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
function ADXM_GetChildsByTagName(oNode, sNodeName) {
	var a = new Array();
	for (var i=0;i<oNode.childNodes.length;i++) {
		if (oNode.childNodes[i].nodeName == sNodeName) {
			a[a.length] = oNode.childNodes[i];
		}
	}
	return a;
}

/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	IE5+/Win: main hiding functions, that calls the actual WCH single-menu hide function.
	delete this if you don't use WCH
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
function ADXM_HideWCH(oMenu) {
	if (typeof(WCH_ShowWndCtrl) == "undefined") return;

	var curmenu = oMenu.shownMenu;
	var prevMenu;
	while ( curmenu ) {
		//	show windowed controls for current child menu
		WCH_ShowWndCtrl(curmenu);
 		//	save the pointer to current child menu
		prevMenu = curmenu;
		//	prepare the next child menu
		curmenu = curmenu.shownMenu;
		//	kill the shownMenu info for current child menu
		prevMenu.shownMenu = null;
	}
}

/*- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	these functions are taken from Mike Foster's X library (part of CBE), and simplified where possible.
	delete all this if you already use (include) the CBE or X files in your project
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -*/
function xClientWidth() {
	var w = 0;
	if (!window.opera && document.documentElement && document.documentElement.clientWidth)
		w = document.documentElement.clientWidth;
	else if (document.body && document.body.clientWidth)
		w = document.body.clientWidth;
	else if ( xDef(window.innerWidth, window.innerHeight, document.height) ) {
		w = window.innerWidth;
		if (document.height > window.innerHeight) w -= 16;
	}
	return w;
}

function xClientHeight() {
	var h = 0;
	if (!window.opera && document.documentElement && document.documentElement.clientHeight)
		h = document.documentElement.clientHeight;
	else if (document.body && document.body.clientHeight)
		h = document.body.clientHeight;
	else if ( xDef(window.innerHeight, window.innerWidth, document.width) ) {
		h = window.innerHeight;
		if (document.width > window.innerWidth) h -= 16;
	}
	return h;
}

function xScrollLeft() {
	var offset = 0;
	if ( xDef(window.pageXOffset) )
		offset = window.pageXOffset;
	else if ( document.documentElement && document.documentElement.scrollLeft )
		offset = document.documentElement.scrollLeft;
	else if ( document.body && xDef(document.body.scrollLeft ) )
		offset = document.body.scrollLeft;
	return offset;
}

function xScrollTop() {
	var offset = 0;
	if ( xDef(window.pageYOffset) )
		offset = window.pageYOffset;
	else if ( document.documentElement && document.documentElement.scrollTop )
		offset = document.documentElement.scrollTop;
	else if ( document.body && xDef(document.body.scrollTop ) )
		offset = document.body.scrollTop;
	return offset;
}

function xWidth(e, uW) {
	if ( !e ) return 0;
	uW = e.offsetWidth;
	return uW;
}

function xHeight(e, uH) {
	if ( !e ) return 0;
	uH=e.offsetHeight;
	return uH;
}

function xMoveTo(e, iX, iY) {
	xLeft(e,iX);
	xTop(e,iY);
}

function xLeft(e, iX) {
	if ( !(e = xGetElementById(e)) ) return 0;
    if ( xDef(iX) )
		e.style.left = iX + "px";
	else {
    	if ( xDef(e.offsetLeft) )
			iX = e.offsetLeft;
		else
			iX = parseInt(e.style.left);
    	if (isNaN(iX))
			iX = 0;
	}
	return iX;
}

function xTop(e, iY) {
	if ( !(e = xGetElementById(e)) ) return 0;
    if ( xDef(iY) )
		e.style.top = iY + "px";
	else {
		if ( xDef(e.offsetTop) )
			iY = e.offsetTop;
		else
			iY = parseInt(e.style.top);
		if (isNaN(iY))
			iY = 0;
	}
	return iY;
}

function xPageX(e) {
	if ( !(e = xGetElementById(e)) ) return 0;
	var x = 0;
	while (e) {
		if ( xDef(e.offsetLeft) ) x += e.offsetLeft;
		else break;
		e = e.offsetParent;
	}
	return x;
}

function xPageY(e) {
	if ( !(e = xGetElementById(e)) ) return 0;
	var y = 0;
	while (e) {
		if ( xDef(e.offsetTop) ) y += e.offsetTop;
		else break;
		e = e.offsetParent;
	}
	return y;
}

function xDef() {
	for (var i=0; i<arguments.length; ++i) {
		if ( typeof(arguments[i]) == "undefined" )
			return false;
	}
	return true;
}

function xGetElementById(e) {
	if (typeof(e) != "string") return e;
	if (document.getElementById)
		e = document.getElementById(e);
	else
		e = null;
	return e;
}

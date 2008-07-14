package com.idega.block.web2.business;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/07/14 12:49:54 $ by $Author: valdas $
 */
public enum JQueryUIType  {
	
	UI_TABS {public String getFileName() { return "ui.tabs.js"; }},
	UI_TABS_CSS {public String getFileName() { return "themes/flora/flora.tabs.css"; }},
	UI_TABS_EXT {public String getFileName() { return "ui.tabs.ext.js"; }},
	UI_ACCORDION {public String getFileName() { return "ui.accordion.js"; }},
	UI_EDITABLE { public String getFileName() { return "ui.editable.js"; } },
	UI_EASING { public String getFileName() { return "ui.easing.js"; } };
	
	public abstract String getFileName();
}

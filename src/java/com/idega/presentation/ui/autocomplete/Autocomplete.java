package com.idega.presentation.ui.autocomplete;

import java.util.List;
import java.util.logging.Logger;

import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.util.PresentationUtil;

public abstract class Autocomplete  extends IWBaseComponent{

	private String itemsFunction = null;
	private StringBuilder scriptOnLoad = null;
	
	public String getItemsFunction() {
		return itemsFunction;
	}

	public void setItemsFunction(String itemsFunction) {
		this.itemsFunction = itemsFunction;
	}

	protected void addFiles(IWContext iwc){
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, getScripts(iwc));
		PresentationUtil.addStyleSheetsToHeader(iwc, getStyleSheets(iwc));
	}
	
	protected void addActions(IWContext iwc){
		Layer scriptLayer = new Layer();
		this.add(scriptLayer);
		
		String action = getScriptOnLoad().append("\n});").toString();
		action = PresentationUtil.getJavaScriptAction(action);
		scriptLayer.add(action);
	}
	
	public abstract List<String> getScripts(IWContext iwc);
	
	public abstract List<String> getStyleSheets(IWContext iwc);

	protected StringBuilder getScriptOnLoad() {
		if(scriptOnLoad == null){
			scriptOnLoad = new StringBuilder("jQuery(document).ready(function(){");
		}
		return scriptOnLoad;
	}

	protected void setScriptOnLoad(StringBuilder scriptOnLoad) {
		this.scriptOnLoad = scriptOnLoad;
	}
	
	protected Logger getLogger(){
		return Logger.getLogger(getClass().getName());
	}
	
}

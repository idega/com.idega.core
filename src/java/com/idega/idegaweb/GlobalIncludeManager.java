/*
 * Created on 4.8.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.idegaweb;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages resources such as stylesheets that are supposed to be
 * included in all pages.
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil </a>
 * @version 1.0
 */
public class GlobalIncludeManager {

	private static GlobalIncludeManager instance;
	private String standardIWStyleSheetURL = "/idegaweb/style/style.css";

	private List styleSheets;

	private GlobalIncludeManager() {
		addStyleSheet(standardIWStyleSheetURL);
	}

	/**
	 * Gets the instance to this singleton
	 * @return
	 */
	public static GlobalIncludeManager getInstance() {
		if (instance == null) {
			instance = new GlobalIncludeManager();
		}
		return instance;
	}
	
	public List getStyleSheets(){
		if(styleSheets==null){
			styleSheets=new ArrayList();
		}
		return styleSheets;
	}
	
	/**
	 * Adds a StyleSheet file url that is relative to the webapp root.
	 * @param url
	 */
	public void addStyleSheet(String url){
		getStyleSheets().add(url);
	}
}
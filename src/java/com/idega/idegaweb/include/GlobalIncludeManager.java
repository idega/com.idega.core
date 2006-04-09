/*
 * Created on 4.8.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.idegaweb.include;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * This class manages resources such as stylesheets that are supposed to be
 * included in all pages.
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil </a>
 * @version 1.0
 */
public class GlobalIncludeManager implements Singleton {
	
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() {return new GlobalIncludeManager();}};

	private String standardIWStyleSheetURL = "/idegaweb/style/style.css";
	private String coreIWStyleSheetURL = "/idegaweb/bundles/com.idega.core.bundle/resources/style/iw_core.css";
	
	private IWMainApplication iwma;

	private List styleSheets;


	protected GlobalIncludeManager() {
		this.iwma =  IWMainApplication.getDefaultIWMainApplication();
		addStyleSheet(this.coreIWStyleSheetURL);
		addStyleSheet(this.standardIWStyleSheetURL);
	}

	/**
	 * Gets the instance to this singleton
	 * @return
	 */
	public static GlobalIncludeManager getInstance() {
		return (GlobalIncludeManager) SingletonRepository.getRepository().getInstance(GlobalIncludeManager.class, instantiator);
	  }
	
	
	/**
	 * Returns a list of StylesheetLink objects
	 * @return
	 */
	public List getStyleSheets(){
		if(this.styleSheets==null){
			this.styleSheets=new ArrayList();
		}
		return this.styleSheets;
	}
	
	/**
	 * Adds a StyleSheet file url that is relative to the webapp root.
	 * @param url
	 */
	public void addStyleSheet(String url){
		StyleSheetLink link = new StyleSheetLink(url);
		getStyleSheets().add(link);
	}
	
	
	public boolean containsStyleSheet(String url){
		Iterator iterator = getStyleSheets().iterator();
		while (iterator.hasNext()) {
			StyleSheetLink link = (StyleSheetLink) iterator.next();
			if(link.getUrl().equals(url)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a stylesheet where the bundleIdentifier is a reference to the bundle where the stylesheet file is located.
	 * @param bundleIdentifier the bundle where the stylesheet is located
	 * @param url the URL within the bundle (relative to the resources subfolder)
	 */
	public void addBundleStyleSheet(String bundleIdentifier,String url){
		IWBundle iwb = getIWMainApplication().getBundle(bundleIdentifier);
		String cssRealUrl = iwb.getResourceURIWithoutContextPath(url);
		if (cssRealUrl != null && !containsStyleSheet(cssRealUrl)) {
			addStyleSheet(cssRealUrl);
		}
	}
	
	
	public IWMainApplication getIWMainApplication(){
		return this.iwma;
	}
	
	public void setIWMainApplication(IWMainApplication iwma){
		this.iwma=iwma;
	}
	
	
}
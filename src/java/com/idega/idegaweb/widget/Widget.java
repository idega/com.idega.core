/*
 * $Id: Widget.java,v 1.5 2007/01/05 11:29:15 laddi Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.widget;

import java.util.Locale;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;

/**
 * The base object that all widgets should extend.  Has the standard methods most of them need.
 * 
 * Last modified: 14.10.2004 10:24:30 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public abstract class Widget extends Block {
	
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb.widget";
	
	private IWBundle iwb;
	private IWResourceBundle iwrb;
	private Locale locale;
	
	private String styleClass;
	
	public void main(IWContext iwc) {
		this.iwb = getBundle(iwc);
		this.iwrb = getResourceBundle(iwc);
		this.locale = iwc.getCurrentLocale();

		Layer layer = new Layer(Layer.DIV);
		if (this.styleClass != null) {
			layer.setStyleClass(this.styleClass);
		}
		layer.setStyleClass(this.locale.toString());

		PresentationObject widget = getWidget(iwc);
		if (widget != null) {
			layer.add(widget);
		}
		
		add(layer);
	}
	
	protected abstract PresentationObject getWidget(IWContext iwc);
	
	protected IWBundle getBundle() {
		return this.iwb;
	}
	
	protected IWResourceBundle getResourceBundle() {
		return this.iwrb;
	}
	
	protected Locale getLocale() {
		return this.locale;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
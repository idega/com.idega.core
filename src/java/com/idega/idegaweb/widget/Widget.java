/*
 * $Id: Widget.java,v 1.2.2.1 2007/01/12 19:32:59 idegaweb Exp $
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
import com.idega.presentation.PresentationObject;

/**
 * The base object that all widgets should extend.  Has the standard methods most of them need.
 * 
 * Last modified: 14.10.2004 10:24:30 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2.2.1 $
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

		PresentationObject widget = getWidget(iwc);
		if (widget != null) {
			if (this.styleClass != null) {
				widget.setStyleClass(this.styleClass);
			}
			add(widget);
		}
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
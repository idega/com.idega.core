/*
 * $Id: Widget.java,v 1.1 2004/10/14 12:11:56 laddi Exp $
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
 * Last modified: 14.10.2004 10:24:30 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public abstract class Widget extends Block {
	
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb.widget";
	
	private IWBundle iwb;
	private IWResourceBundle iwrb;
	private Locale locale;
	
	private String styleClass;
	
	public void main(IWContext iwc) {
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		locale = iwc.getCurrentLocale();

		PresentationObject widget = getWidget(iwc);
		if (widget != null) {
			if (styleClass != null) {
				widget.setStyleClass(styleClass);
			}
			add(widget);
		}
	}
	
	protected abstract PresentationObject getWidget(IWContext iwc);
	
	protected IWBundle getBundle() {
		return iwb;
	}
	
	protected IWResourceBundle getResourceBundle() {
		return iwrb;
	}
	
	protected Locale getLocale() {
		return locale;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
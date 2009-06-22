/*
 * $Id$
 * Created on May 5, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import java.util.Locale;


public class Abbreviation extends Text {

	public Abbreviation() {
		super();
	}

	public Abbreviation(String text) {
		super(text);
	}

	protected String getTag() {
		return "abbr";
	}

	protected boolean showTag() {
		return true;
	}
	
	public void setTitle(String title) {
		setMarkupAttribute("title", title);
	}
	
	public void setLanguage(Locale language) {
		setMarkupAttribute("lang", language.getLanguage());
	}
}
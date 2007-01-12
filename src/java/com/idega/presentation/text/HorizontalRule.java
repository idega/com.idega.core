/*
 * $Id: HorizontalRule.java,v 1.8.2.1 2007/01/12 19:32:02 idegaweb Exp $
 *
 * Copyright (C) 2001-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import java.io.IOException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * <p>
 * Component to render out a Horizontal Rule or &lt;HR&gt; tag;
 * </p>
 *  Last modified: $Date: 2007/01/12 19:32:02 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:laddi@idega.is">��rhallur Helgason</a>,<a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.8.2.1 $
 */
public class HorizontalRule extends PresentationObject {

	public static final String ALIGN_LEFT = "left";
	public static final String ALIGN_RIGHT = "right";
	public static final String ALIGN_CENTER = "center";

	public HorizontalRule() {
		setTransient(false);
	}

	public HorizontalRule(int width) {
		this(Integer.toString(width));
	}

	public HorizontalRule(String width) {
		setWidth(width);
	}

	public HorizontalRule(int width, int height) {
		this(Integer.toString(width), height);
	}

	public HorizontalRule(String width, int height) {
		setWidth(width);
		setHeight(height);
	}

	public HorizontalRule(int width, int height, String style) {
		this(Integer.toString(width), height, style);
	}

	public HorizontalRule(String width, int height, String style) {
		setWidth(width);
		setHeight(height);
		setStyle(style);
	}

	public HorizontalRule(int width, int height, String style, boolean noShade) {
		this(Integer.toString(width), height, style, noShade);
	}

	public HorizontalRule(String width, int height, String style, boolean noShade) {
		setWidth(width);
		setHeight(height);
		setStyle(style);
		setNoShade(noShade);
	}

	public void setTitle(String title) {
		setMarkupAttribute("title", title);
	}

	public void setWidth(String width) {
		setMarkupAttribute("width", width);
	}

	public void setWidth(int width) {
		setMarkupAttribute("width", Integer.toString(width));
	}

	public void setHeight(int height) {
		setMarkupAttribute("size", Integer.toString(height));
	}

	public void setNoShade(boolean noShade) {
		if (noShade) {
			setMarkupAttributeWithoutValue("noshade");
		}
	}

	public void setColor(String color) {
		setMarkupAttribute("color", color);
	}

	public void setNoShade() {
		setNoShade(true);
	}

	public void setStyleClass(String style) {
		setMarkupAttribute("class", style);
	}

	public void setStyle(String style) {
		setMarkupAttribute("style", style);
	}

	public void setAlignment(String alignment) {
		setMarkupAttribute("align", alignment);
	}

	public void print(IWContext iwc) throws IOException {
		if (getMarkupLanguage().equals("HTML")) {
			print("<hr " + getMarkupAttributesString() + " >");
		}
	}
}

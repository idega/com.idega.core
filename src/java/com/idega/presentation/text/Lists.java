// idega 2000 - Tryggvi Larusson

/*
 * 
 * Copyright 2000 idega.is All Rights Reserved.
 *  
 */

package com.idega.presentation.text;

import java.util.Iterator;
import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * @version 1.2
 *  
 */

public class Lists extends PresentationObjectContainer {

	public static final String ARABIC_NUMBERS = "1";
	public static final String LOWER_ALPHA = "a";
	public static final String UPPER_ALPHA = "A";
	public static final String LOWER_ROMAN = "i";
	public static final String UPPER_ROMAN = "I";

	public static final String DISC = "disc";
	public static final String CIRCLE = "circle";
	public static final String SQUARE = "square";

	private boolean compact = false;
	private boolean ordered = false;

	private Image bullet;

	public Lists() {
		super();
	}

	public void setClass(String s) {
		setMarkupAttribute("class", s);
	}

	public void setStyle(String s) {
		setMarkupAttribute("style", s);
	}

	public void setType(String type) {
		setMarkupAttribute("type", type);
	}

	public void setCompact(boolean compact) {
		this.compact = compact;
	}

	public void setStartNumber(int number) {
		setMarkupAttribute("start", Integer.toString(number));
	}

	public void setListOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public void setBulletImage(Image image) {
		bullet = image;
	}

	private String getListType() {
		if (ordered) {
			return "ol";
		}

		return "ul";

	}

	private void getBullet() {
		if (bullet != null) {
			/** todo use MediaBusiness.getMediaURL(fileid,iwma);* */
			String styleString = "list-style-image: url(" + bullet.getMediaURL() + ");";
			setStyle(styleString);
		}
	}

	public void print(IWContext iwc) throws Exception {
		getBullet();

		if (getMarkupLanguage().equals("HTML")) {
			if (!compact) {
				println("<" + getListType() + " " + getMarkupAttributesString() + ">");
			}

			List theObjects = this.getChildren();
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					if (item instanceof Lists) {
						item._print(iwc);
					}
					else {
						if (compact) {
							StringBuffer buffer = new StringBuffer();
							buffer.append("<li");
							if (getMarkupAttribute("style") != null) {
								buffer.append(" style=\"" + getMarkupAttribute("style") + "\"");
							}
							if (getMarkupAttribute("class") != null) {
								buffer.append(" class=\"" + getMarkupAttribute("class") + "\"");
							}
							buffer.append(">");
							print(buffer.toString());
						}
						else {
							print("<li>");
						}
						item._print(iwc);
						println("</li>");
					}
				}
			}
			if (!compact) {
				println("</" + getListType() + ">");
			}
		}
		else if (getMarkupLanguage().equals("WML")) {
			println("<ul>");
			super.print(iwc);
			println("</ul>");
		}
	}
}
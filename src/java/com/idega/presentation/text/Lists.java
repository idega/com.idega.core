/*
 * $Id: Lists.java,v 1.13 2005/02/17 11:12:12 tryggvil Exp $
 * Created in 2000 Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import java.util.Iterator;
import java.util.List;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;

/**
 * <p>
 * A UIComponent class to render out a "List" or <code>ul</code> or <code>ol</code> html tag.
 * </p>
 *  Last modified: $Date: 2005/02/17 11:12:12 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.13 $
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

	/**
	 * @deprecated replaced with setStyleClass
	 * @param sClass the Styleclass
	 */
	public void setClass(String sClass) {
		//setMarkupAttribute("class", s);
		super.setStyleClass(sClass);
	}
	/**
	 * @deprecated replaced with setStyleAttribute
	 * @param sClass the Styleclass
	 */
	public void setStyle(String styleAttribute) {
		//setMarkupAttribute("style", s);
		super.setStyleAttribute(styleAttribute);
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
						//item._print(iwc);
						renderChild(iwc,item);
					}
					else if (item instanceof ListItem) {
						//item._print(iwc);
						renderChild(iwc,item);
					}
					else {
						if (compact) {
							StringBuffer buffer = new StringBuffer();
							buffer.append("<li");
							String styleAttributes = getListItemStyleAttributes();
							if(styleAttributes!=null){
								buffer.append(" style=\"" + styleAttributes + "\"");
							}
							String styleClass = getListItemStyleClass();
							if (styleClass != null) {
								buffer.append(" class=\"" + styleClass + "\"");
							}
							buffer.append(">");
							print(buffer.toString());
						}
						else {
							print("<li>");
						}
						//item._print(iwc);
						renderChild(iwc,item);
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
	
	/**
	 * Sets the style attributes on all list items.
	 * @param attributeString
	 */
	public void setListItemStyleAttributes(String attributeString){
		getAttributes().put("listitemstyleattributes",attributeString);
	}
	
	public String getListItemStyleAttributes(){
		return (String)getAttributes().get("listitemstyleattributes");
	}

	/**
	 * Sets the style attributes on all list items.
	 * @param attributeString
	 */
	public void setListItemStyleClass(String attributeString){
		getAttributes().put("listitemstyleclass",attributeString);
	}
	
	public String getListItemStyleClass(){
		return (String)getAttributes().get("listitemstyleclass");
	}

}
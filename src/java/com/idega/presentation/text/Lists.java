/*
 * $Id: Lists.java,v 1.15 2006/04/09 12:13:16 laddi Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.util.WebUtil;
import com.idega.util.expression.ELUtil;

/**
 * <p>
 * A UIComponent class to render out a "List" or <code>ul</code> or <code>ol</code> html tag.
 * </p>
 *  Last modified: $Date: 2006/04/09 12:13:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.15 $
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
	
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[3];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this.compact);
		values[2] = Boolean.valueOf(this.ordered);
		return values;
	}
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.compact = ((Boolean)values[1]).booleanValue();
		this.ordered = ((Boolean)values[2]).booleanValue();
	}
	
	public Lists() {
		super();
		setTransient(false);
	}

	/**
	 * @deprecated replaced with setStyleClass
	 * @param sClass the Styleclass
	 */
	@Deprecated
	public void setClass(String sClass) {
		//setMarkupAttribute("class", s);
		super.setStyleClass(sClass);
	}
	/**
	 * @deprecated replaced with setStyleAttribute
	 * @param sClass the Styleclass
	 */
	@Deprecated
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

	//TODO remove this variable declaration and move totally to facets:
	//This variable is kept because of legacy reasons but should be replaced with a Facet
	private Image oldBullet;
	public void setBulletImage(Image image) {
		if(IWMainApplication.useJSF){
			getFacets().put("bulletimage",image);
		}
		else{
			this.oldBullet=image;
		}
	}
	protected Image getBulletImage(){
		if(IWMainApplication.useJSF){
			return (Image)getFacet("bulletimage");
		}
		else{
			return this.oldBullet;
		}
	}
	
	private String getListType() {
		if (this.ordered) {
			return "ol";
		}

		return "ul";

	}

	
	
	private void initializeBulletImage() {
		Image bullet = getBulletImage();
		if (bullet != null) {
			/** todo use MediaBusiness.getMediaURL(fileid,iwma);* */
			String styleString = "list-style-image: url(" + bullet.getMediaURL() + ");";
			setStyle(styleString);
		}
	}

	private Boolean modernNavigation;
	
	public Boolean getModernNavigation() {
		return modernNavigation;
	}
	public void setModernNavigation(Boolean modernNavigation) {
		this.modernNavigation = modernNavigation;
	}

	@Autowired
	private WebUtil webUtil;
	
	private WebUtil getWebUtil() {
		if (webUtil == null)
			ELUtil.getInstance().autowire(this);
		return webUtil;
	}
	
	@Override
	public void print(IWContext iwc) throws Exception {
		initializeBulletImage();

		if (modernNavigation == null)
			modernNavigation = getWebUtil().isLatestNavigationUsed();
		if (modernNavigation)
			println("<nav>");
			
		if (getMarkupLanguage().equals("HTML")) {
			if (!this.compact) {
				println("<" + getListType() + " " + getMarkupAttributesString() + ">");
			}

			List<UIComponent> theObjects = this.getChildren();
			if (theObjects != null) {
				for (Iterator<UIComponent> iter = theObjects.iterator(); iter.hasNext();) {
					PresentationObject item = (PresentationObject) iter.next();
					if (item instanceof Lists) {
						renderChild(iwc,item);
					} else if (item instanceof ListItem) {
						renderChild(iwc,item);
					} else {
						if (this.compact) {
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
						renderChild(iwc,item);
						println("</li>");
					}
				}
			}
			if (!this.compact) {
				println("</" + getListType() + ">");
			}
		} else if (getMarkupLanguage().equals("WML")) {
			println("<ul>");
			super.print(iwc);
			println("</ul>");
		}
		
		if (modernNavigation)
			println("</nav>");
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
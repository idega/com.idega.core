/*
 * $Id: IFrame.java,v 1.17 2005/03/08 18:42:29 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.io.IOException;
import javax.faces.context.FacesContext;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.presentation.IWContext;


/**
 * <p>
 * Component to render out an "iframe" or Inline Frame element.
 * </p>
 *  Last modified: $Date: 2005/03/08 18:42:29 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.17 $
 */
public class IFrame extends InterfaceObject {

	//constants:
	public static final String ALIGN_TOP = "top";
	public static final String ALIGN_MIDDLE = "middle";
	public static final String ALIGN_BOTTOM = "bottom";
	public static final String ALIGN_LEFT = "left";
	public static final String ALIGN_RIGHT = "right";
	public static final String ALIGN_CENTER = "center";
	public static final String SCROLLING_YES = "yes";
	public static final String SCROLLING_NO = "no";
	public static final String SCROLLING_AUTO = "auto";
	public static final int FRAMEBORDER_ON = 1;
	public static final int FRAMEBORDER_OFF = 0;
	//instance variables:
	private boolean transparent = false;
	private int ibPageId = 0;
	private boolean addLocaleID = false;
	private Class classToInstanciate;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[5];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(transparent);
		values[2] = new Integer(ibPageId);
		values[3] = Boolean.valueOf(addLocaleID);
		values[4] = classToInstanciate;
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		transparent = ((Boolean)values[1]).booleanValue();
		ibPageId = ((Integer)values[2]).intValue();
		addLocaleID = ((Boolean)values[3]).booleanValue();
		classToInstanciate = (Class)values[4];
	}
	
	public IFrame() {
		this("untitled");
	}

	public IFrame(String name) {
		this(name, "");
	}

	public IFrame(String name, Class classToInstanciate) {
		//	this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate));
		this(name);
		setSrc(classToInstanciate);
	}
	/*
	public IFrame(String name,String classToInstanciate,String template){
		this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
	}
	
	public IFrame(String name,Class classToInstanciate,Class template){
		this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
	}
	
	public IFrame(String name,Class classToInstanciate,String template){
		this(name,IWMainApplication.getObjectInstanciatorURL(classToInstanciate,template));
	}*/

	public IFrame(String name, String URL) {
		super();
		setName(name);
		setSrc(URL);
		setTransient(false);
	}

	public IFrame(String name, int width, int height) {
		this(name, "");
		setWidth(width);
		setHeight(height);
	}

	public IFrame(String name, String URL, int width, int height) {
		this(name, URL);
		setWidth(width);
		setHeight(height);
	}

	public void setTitle(String title) {
		setMarkupAttribute("title", title);
	}

	public void setToAddLocaleID(boolean addLocaleID) {
		this.addLocaleID = addLocaleID;
	}

	public void setSrc(String source) {
		setMarkupAttribute("src", source);
	}

	public void setIBPage(int id) {
		ibPageId = id;
	}

	public void setSrc(Class classToAdd) {
		//setSrc(IWMainApplication.getObjectInstanciatorURL(classToAdd));
		this.classToInstanciate = classToAdd;
	}

	private void setClassToInstanciateAsSource(IWContext iwc) {
		if (classToInstanciate != null) {
			this.setSrc(iwc.getIWMainApplication().getObjectInstanciatorURI(classToInstanciate));
		}
	}

	/*public void setSrc(Class classToAdd, Class templateClass) {
	  setSrc(IWMainApplication.getObjectInstanciatorURL(classToAdd,templateClass));
	}*/

	public void setWidth(String width) {
		setMarkupAttribute("width", width);
	}

	public String getWidth() {
		return getMarkupAttribute("width");
	}

	public void setWidth(int width) {
		setMarkupAttribute("width", Integer.toString(width));
	}

	public void setHeight(String height) {
		setMarkupAttribute("height", height);
	}

	public void setHeight(int height) {
		setMarkupAttribute("height", Integer.toString(height));
	}

	public void setStyleClass(String style) {
		setMarkupAttribute("class", style);
	}

	public void setStyle(String style) {
		setMarkupAttribute("style", style);
	}

	public void setBorder(int border) {
		setMarkupAttribute("frameborder", Integer.toString(border));
	}

	public int getBorder() {
		return Integer.parseInt(this.getMarkupAttribute("frameborder"));
	}

	public void setMarginWidth(int width) {
		setMarkupAttribute("marginwidth", Integer.toString(width));
	}

	public void setMarginHeight(int height) {
		setMarkupAttribute("marginheight", Integer.toString(height));
	}

	public void setScrolling(String scrolling) {
		setMarkupAttribute("scrolling", scrolling);
	}

	public String getScrolling() {
		return this.getMarkupAttribute("scrolling");
	}

	public void setAlignment(String alignment) {
		setMarkupAttribute("align", alignment);
	}

	public void setAsTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public void print(IWContext iwc) throws IOException {
		setClassToInstanciateAsSource(iwc);

		String src = getMarkupAttribute("src");
		if (src != null) {
			if (src.indexOf("?") != -1) {
				setMarkupAttribute("src", src + "&" + LocaleSwitcher.languageParameterString + "=" + iwc.getCurrentLocale().toString());
			}
			else {
				setMarkupAttribute("src", src + "?" + LocaleSwitcher.languageParameterString + "=" + iwc.getCurrentLocale().toString());
			}
		}

		if (transparent)
			setMarkupAttribute("ALLOWTRANSPARENCY", "true");
		if (ibPageId > 0) {
			BuilderService bservice = getBuilderService(iwc);
			//setAttribute("src",iwc.getRequestURI()+"?"+com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER+"="+ibPageId+"");
			this.setSrc(bservice.getPageURI(ibPageId));
		}

		if (getMarkupLanguage().equals("HTML")) {
			print("<iframe name=\"" + getName() + "\"" + getMarkupAttributesString() + " >");
			String content = super.getContent();
			if (content != null) {
				print(content);
			}
			println("</iframe>\n");
		}
	}

	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	public boolean isContainer() {
		return false;
	}
}
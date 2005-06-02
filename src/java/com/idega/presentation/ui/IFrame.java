/*
 * $Id: IFrame.java,v 1.19 2005/06/02 18:06:50 tryggvil Exp $
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
 *  Last modified: $Date: 2005/06/02 18:06:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.19 $
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
	private boolean addLanguageParameter = true;

	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[6];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(transparent);
		values[2] = new Integer(ibPageId);
		values[3] = Boolean.valueOf(addLocaleID);
		values[4] = classToInstanciate;
		values[5] = Boolean.valueOf(addLanguageParameter);
		return values;
	}
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		transparent = ((Boolean)values[1]).booleanValue();
		ibPageId = ((Integer)values[2]).intValue();
		addLocaleID = ((Boolean)values[3]).booleanValue();
		classToInstanciate = (Class)values[4];
		addLanguageParameter = ((Boolean) values[5]).booleanValue();
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
			String langAddition = "";
			if (addLanguageParameter) {
				if (src.indexOf("?") != -1) {
					langAddition = "&" + LocaleSwitcher.languageParameterString + "=" + iwc.getCurrentLocale().toString();
				}
				else {
					langAddition = "?" + LocaleSwitcher.languageParameterString + "=" + iwc.getCurrentLocale().toString();
				}
			}
			setMarkupAttribute("src", src + langAddition);
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
			
			if(getFrameMarginTop()!=null && getFrameMarginBottom()!=null){
				println("<script type=\"text/javascript\">");
				String frameId = this.getID();
				//this is a reference to a method declared in iw_core.js:
				println("setIframeHeight('"+frameId+"',"+getFrameMarginTop()+","+getFrameMarginBottom()+");");
				println("window.onresize = function() { setIframeHeight('"+frameId+"',"+getFrameMarginTop()+","+getFrameMarginBottom()+")}");
				println("</script>");
			}
			
		}
	}


	public void addLanguageParameter(boolean add) {
		this.addLanguageParameter = add;
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
	
	
	/**
	 * <p>
	 * This method is for creating an iframe with 'floating' height, i.e. that the frame with take 
	 * the height of the window minus the top and bottom margins specified in this function. This is
	 * implemented by an added javascript call.
	 * </p>
	 * @param marginTop space for the margin from the top in pixels;
	 * @param marginBottom space for the margin from the bottom in pixels
	 */
	public void setFrameHeight(int marginTop,int marginBottom){
		setFrameMarginTop(new Integer(marginTop));
		setFrameMarginBottom(new Integer(marginBottom));
	}
	
	/**
	 * @return Returns the frameMarginBottom.
	 */
	protected Integer getFrameMarginBottom() {
		return (Integer)getAttributes().get("iframeMarginBottom");
	}

	
	/**
	 * @param frameMarginBottom The frameMarginBottom to set.
	 */
	protected void setFrameMarginBottom(Integer frameMarginBottom) {
		getAttributes().put("iframeMarginBottom",frameMarginBottom);
	}

	
	/**
	 * @return Returns the frameMarginTop.
	 */
	protected Integer getFrameMarginTop() {
		return (Integer)getAttributes().get("iframeMarginTop");
	}

	
	/**
	 * @param frameMarginTop The frameMarginTop to set.
	 */
	protected void setFrameMarginTop(Integer frameMarginTop) {
		getAttributes().put("iframeMarginTop",frameMarginTop);
	}
}
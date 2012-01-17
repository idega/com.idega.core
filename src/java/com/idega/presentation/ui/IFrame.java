/*
 * $Id: IFrame.java,v 1.22 2009/04/17 10:43:33 valdas Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;


/**
 * <p>
 * Component to render out an "iframe" or Inline Frame element.
 * </p>
 *  Last modified: $Date: 2009/04/17 10:43:33 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.22 $
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
	public static final String CLASS_TO_INSTANCIATE_PARAMETER = "classToInstanciateParameter",
								PARAMETER_NOT_WORKSPACE_WINDOW = "not_workpace",
								EXTERNAL_PARAMETERS = "externalParameters";
	
	//instance variables:
	private boolean transparent = false;
	private int ibPageId = 0;
	private boolean addLocaleID = false;
	private Class<? extends UIComponent> classToInstanciate;
	private boolean addLanguageParameter = true;
	private Map<String, String> parameters;

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[6];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(this.transparent);
		values[2] = new Integer(this.ibPageId);
		values[3] = Boolean.valueOf(this.addLocaleID);
		values[4] = this.classToInstanciate;
		values[5] = Boolean.valueOf(this.addLanguageParameter);
		return values;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.transparent = ((Boolean)values[1]).booleanValue();
		this.ibPageId = ((Integer)values[2]).intValue();
		this.addLocaleID = ((Boolean)values[3]).booleanValue();
		this.classToInstanciate = (Class<? extends UIComponent>) values[4];
		this.addLanguageParameter = ((Boolean) values[5]).booleanValue();
	}
	
	public IFrame() {
		this("untitled");
	}

	public IFrame(String name) {
		this(name, "");
	}

	public IFrame(String name, Class<? extends UIComponent> classToInstanciate) {
		this(name);
		setSrc(classToInstanciate);
	}

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

	@Override
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
		this.ibPageId = id;
	}

	public void setSrc(Class<? extends UIComponent> classToAdd) {
		this.classToInstanciate = classToAdd;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	@SuppressWarnings("unchecked")
	private void setClassToInstanciateAsSource(IWContext iwc) {
		if (iwc.isParameterSet(CLASS_TO_INSTANCIATE_PARAMETER)) {
			try {
				this.classToInstanciate = (Class<? extends UIComponent>) Class.forName(iwc.getParameter(CLASS_TO_INSTANCIATE_PARAMETER));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.classToInstanciate != null) {
			String src = null;
			try {
				List<AdvancedProperty> params = null;
				try {
					String externalParams = iwc.getParameter(EXTERNAL_PARAMETERS);
					externalParams = URLDecoder.decode(externalParams, CoreConstants.ENCODING_UTF8);
					if (!StringUtil.isEmpty(externalParams)) {
						String[] parameters = externalParams.split(CoreConstants.NUMBER_SIGN);
						params = new ArrayList<AdvancedProperty>();
						for (String parameter: parameters) {
							String[] keyAndValue = parameter.split(CoreConstants.AT);
							if (keyAndValue != null && keyAndValue.length == 2)
								params.add(new AdvancedProperty(keyAndValue[0], keyAndValue[1]));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				src = Boolean.TRUE.toString().equals(iwc.getParameter(PARAMETER_NOT_WORKSPACE_WINDOW)) ?
						getBuilderService(iwc).getUriToObject(classToInstanciate, params) :
						iwc.getIWMainApplication().getObjectInstanciatorURI(this.classToInstanciate);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			this.setSrc(src);
		}
	}

	@Override
	public void setWidth(String width) {
		setMarkupAttribute("width", width);
	}

	@Override
	public String getWidth() {
		return getMarkupAttribute("width");
	}

	public void setWidth(int width) {
		setMarkupAttribute("width", Integer.toString(width));
	}

	@Override
	public void setHeight(String height) {
		setMarkupAttribute("height", height);
	}

	public void setHeight(int height) {
		setMarkupAttribute("height", Integer.toString(height));
	}

	@Override
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

	@Override
	public void print(IWContext iwc) throws IOException {
		setClassToInstanciateAsSource(iwc);

		String src = getMarkupAttribute("src");
		if (src != null) {
			URIUtil uri = new URIUtil(src);
			if (this.addLanguageParameter) {
				uri.setParameter(LocaleSwitcher.languageParameterString, iwc.getCurrentLocale().toString());
			}
			if (parameters != null) {
				for (String parameterKey : parameters.keySet()) {
					uri.setParameter(parameterKey, parameters.get(parameterKey));
				}
			}
			
			if (Boolean.TRUE.toString().equals(iwc.getParameter(CoreConstants.PARAMETER_CHECK_HTML_HEAD_AND_BODY)))
				uri.setParameter(CoreConstants.PARAMETER_CHECK_HTML_HEAD_AND_BODY, Boolean.TRUE.toString());
			
			setMarkupAttribute("src", uri.getUri());
		}
		
		String height = iwc.getParameter("height");
		if (!StringUtil.isEmpty(height))
			setHeight(height);

		String width = iwc.getParameter("width");
		if (!StringUtil.isEmpty(width))
			setWidth(width);
		
		if (this.transparent) {
			setMarkupAttribute("ALLOWTRANSPARENCY", "true");
		}
		if (this.ibPageId > 0) {
			BuilderService bservice = getBuilderService(iwc);
			this.setSrc(bservice.getPageURI(this.ibPageId));
		}

		if (getMarkupLanguage().equals("HTML")) {
			print("<iframe name=\"" + getName() + "\"" + getMarkupAttributesString() + " >");
			String content = super.getContent();
			if (!StringUtil.isEmpty(content)) {
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
	@Override
	public void handleKeepStatus(IWContext iwc) {
	}

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#isContainer()
	 */
	@Override
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
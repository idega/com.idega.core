/*
 * $Id: IWBaseComponent.java,v 1.23 2009/03/12 01:59:46 idegaweb Exp $
 * Created on 20.2.2004 by Tryggvi Larusson in project com.project
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.idega.core.cache.CacheableUIComponent;
import com.idega.core.cache.UIComponentCacher;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.util.CoreUtil;
import com.idega.util.RenderUtils;
import com.idega.util.text.TextStyler;

/**
 * <p>
 * This is a base UI component for JSF that adds extended functionality for idegaWeb.<br/>
 * This is supposed to be a convenient replacement for PresentationObject for new
 * pure JSF solutions and doesn't have the some of the legacy burdens that PresentationObject has
 * such as the old style idegaWeb main(IWContext) and print(IWContext) methods and event systems.
 * </p>
 * Copyright (C) idega software 2004-2006 <br/>
 * Last modified: $Date: 2009/03/12 01:59:46 $ by $Author: idegaweb $
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.23 $
 *
 */
public class IWBaseComponent extends UIComponentBase implements CacheableUIComponent {

	protected static Logger LOGGER = null;

	protected static final String divTag = 			"div";
	protected static final String renderedAtt = 	"rendered";

	public static final String EXPRESSION_BEGIN = "#{";
	public static final String EXPRESSION_END = "}";

	private TextStyler _styler;
	private String styleAttribute;
	private boolean isInitialized = false;
	private long iSystemTime = 0;

	protected Logger getLogger() {
		if (LOGGER == null)
			LOGGER = Logger.getLogger(getClass().getName());
		return LOGGER;
	}

	protected Page getParentPage() {
		return getPage(this);
	}

	private Page getPage(UIComponent parent) {
		if (parent == null) {
			return null;
		}

		if (parent instanceof Page) {
			return (Page) parent;
		}
		return getPage(parent.getParent());
	}

	/**
	 * This is an old idegaWeb style add method.
	 * Does the same as getChildren().add(comp) in JSF>
	 * @param comp
	 */
	public void add(UIComponent comp){
		getChildren().add(comp);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
	 */
	@Override
	public void decode(FacesContext arg0) {
		super.decode(arg0);
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
	 */
	@Override
	public void processDecodes(FacesContext arg0) {
		super.processDecodes(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		CoreUtil.doEnsureScopeIsSet(context);

		UIComponentCacher cacher = getCacher(context);
		if(cacher.existsInCache(this,context)){
			// do nothing:
		}
		else{
			if(cacher.isCacheEnbled(this,context)){
				cacher.beginCache(this,context);
			}

			this.iSystemTime = System.currentTimeMillis();
			if(!isInitialized()){
				initializeComponent(context);
				//TODO: Remove call to older method:
				initializeContent();
				setInitialized();
			}
			else{
				updateComponent(context);
			}
			super.encodeBegin(context);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		/*if(getRendersChildren()){
			Iterator children = this.getChildren().iterator();
			while (children.hasNext()) {
				UIComponent element = (UIComponent) children.next();
				renderChild(context,element);
			}
		}*/
		/*Iterator children = this.getChildren().iterator();
			while (children.hasNext()) {
				UIComponent element = (UIComponent) children.next();
				renderChild(context,element);
			}*/

		UIComponentCacher cacher = getCacher(context);
		if(cacher.existsInCache(this,context)){
			// do nothing:
		}
		else{
			super.encodeChildren(context);
		}

	}


	/**
	 * <p>
	 * Renders a child component for the current component. This operation is handy when implementing
	 * renderes that perform child rendering themselves (eg. a layout renderer/grid renderer/ etc..).
	 * Passes on any IOExceptions thrown by the child/child renderer.
	 * </p>
	 * @param context the current FacesContext
	 * @param child which child to render
	 */
	public void renderChild(FacesContext context, UIComponent child) throws IOException {
		RenderUtils.renderChild(context,child);
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(FacesContext context) throws IOException {


		UIComponentCacher cacher = getCacher(context);
		if(cacher.existsInCache(this,context)){
			// encode the cached version:
			cacher.encodeCached(this,context);
		}
		else{

			long endTime = System.currentTimeMillis();
			String renderingText = (endTime - this.iSystemTime) + " ms";
			context.getResponseWriter().writeComment(renderingText);
			super.encodeEnd(context);

			if(cacher.isCacheEnbled(this,context)){
				cacher.endCache(this,context);
			}
		}

	}

	@Override
	public boolean getRendersChildren() {
		try {
			return super.getRendersChildren();
		} catch (Exception e) {
			getLogger().warning("Error while resolving whether to render or not children (" + getChildren() + ") of " + getClass().getName());
		}
		return false;
	}

	/**
	 *
	 * @uml.property name="styleAttribute"
	 */
	public void setStyleAttribute(String style) {
		if (this._styler == null) {
			this._styler = new TextStyler();
		}
		this._styler.parseStyleString(style);
		this.styleAttribute = style;
		//this.set("style", _styler.getStyleString());

	}

	public void setStyleAttribute(String attribute, String value) {
		if (this._styler == null) {
			this._styler = new TextStyler();
		}
		this._styler.setStyleValue(attribute, value);
		setStyleAttribute( this._styler.getStyleString());
	}

	/**
	 *
	 * @uml.property name="styleAttribute"
	 */
	public String getStyleAttribute() {
		return this.styleAttribute;
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getFamily()
	 */
	@Override
	public String getFamily() {
		return "idegaweb";
	}
	/**
	 * <p>
	 * This method was refactored and replaced with initializeComponent
	 * </p>
	 * @deprecated Replaced with initializeComponent
	 */
	@Deprecated
	protected void initializeContent() {
		//does nothing by default
	}

	/**
	 * <p>
	 * This is a method that is ensured that is only called once in initalization in a
	 * state saved component. This method is intended to be implemented in subclasses for example to add components.<br/>
	 * This method is called from the standard encodeBegin() method.
	 * </p>
	 * @param context the FacesContext for the request
	 */
	protected void initializeComponent(FacesContext context) {
		//does nothing by default
	}

	/**
	 * <p>
	 * This method is called when the component is already initialized (i.e. the second time and onwards when a faces rendering
	 * is called upon this component when it is state saved) and usually happens when the component is restored after a "POST".<br/>
	 * This callback method could be overrided in sublcasses if something is meant to happen when a new
	 * request is sent on an already initialized component.<br/>
	 * This method is called from the standard encodeBegin() method.
	 * </p>
	 * @param context
	 */
	protected void updateComponent(FacesContext context) {
		//Does nothing by default
	}

	/**
	 * <p>
	 * Returns if this component instance has been initialized, i.e. the initializeComponent() method called.
	 * </p>
	 * @return
	 */
	protected boolean isInitialized(){
		return this.isInitialized;
	}

	protected void setInitialized(){
		this.isInitialized=true;
	}

	protected void setInitialized(boolean initialized) {
		this.isInitialized = initialized;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[4];
		values[0] = super.saveState(ctx);
		values[1] = this.styleAttribute;
		values[2] = this._styler;
		values[3] = new Boolean(this.isInitialized);
		return values;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.styleAttribute = ((String) values[1]);
		this._styler = (TextStyler) values[2];
		this.isInitialized = ((Boolean) values[3]).booleanValue();
	}

	/**
	 * <p>
	 * Get the IWMainapplication from the context
	 * </p>
	 * @param context
	 * @return
	 */
	protected static IWMainApplication getIWMainApplication(FacesContext context){
		return IWMainApplication.getIWMainApplication(context);
	}

	/**
	 * <p>
	 * Get the IWBundle from the bundleIdentifier.
	 * </p>
	 * @param context
	 * @param bundleIdentifier
	 * @return
	 */
	protected static IWBundle getIWBundle(FacesContext context,String bundleIdentifier){
		IWMainApplication iwma = getIWMainApplication(context);
		return iwma.getBundle(bundleIdentifier);
	}

	/**
	 * <p>
	 * Get the IWResourceBundle from the context and bundleIdentifier.
	 * It gets the locale from the context.
	 * </p>
	 * @param context
	 * @param bundleIdentifier
	 * @return
	 */
	protected static IWResourceBundle getIWResourceBundle(FacesContext context,String bundleIdentifier){
		IWBundle bundle = getIWBundle(context,bundleIdentifier);
		Locale locale = null;
		UIViewRoot viewRoot = context.getViewRoot();
		if(viewRoot!=null){
			locale = viewRoot.getLocale();
		}
		else{
			locale = context.getExternalContext().getRequestLocale();
		}
		return bundle.getResourceBundle(locale);
	}


	@Override
	public UIComponentCacher getCacher(FacesContext context){
		return UIComponentCacher.getDefaultCacher(context);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.CacheableUIComponent#getViewState(javax.faces.context.FacesContext)
	 */
	@Override
	public String getViewState(FacesContext context) {
		return "view";
	}

	@Override
	public List<UIComponent> getChildren() {
		List<UIComponent> children = super.getChildren();
		return children;
	}

	//@Override
	//public Map<Object, UIComponent> getFacets() {
	//	Map<Object, UIComponent> facets = super.getFacets();
	//	return facets;
	//}

	/**
	 * <p>
	 * A method to check if the passed String is a valuebinding expression,
	 * i.e. a string in the format '#{MyBeanId.myProperty}'
	 * </p>
	 * @param any String
	 * @return
	 */
    public boolean isValueBinding(String value)
    {
        if (value == null) {
					return false;
				}

        int start = value.indexOf(EXPRESSION_BEGIN);
        if (start < 0) {
					return false;
				}

        int end = value.lastIndexOf(EXPRESSION_END);
        return (end >=0 && start < end);
    }

    /**
     * Creates the expression syntax, i.e. wraps the beanReference String around with #{ and },
     * if it does not have them already
	 *
     * @param beanReference
     * @return
     */
    public String getExpression(String beanReference){
    	String exp = beanReference;
    	if (!isValueBinding(beanReference)) {
    		exp = EXPRESSION_BEGIN+beanReference+EXPRESSION_END;
    	}
    	return exp;
    }

    @SuppressWarnings("unchecked")
    protected <T>T getExpressionValue(FacesContext ctx, String expression) {

    	ValueExpression vexp = getValueExpression(expression);
    	return vexp != null ? (T)vexp.getValue(ctx.getELContext()) : null;
    }

    /**
     * <p>
     * This method finds a bean instance from a given beanId.<br/>
     *
     * </p>
     * @param beanId - could be either expression like #{bean.method} or just beanId like beanId
     * @return
     */
    public <T>T getBeanInstance(String beanId) {
	    FacesContext context = FacesContext.getCurrentInstance();

	    String expr;
	    if (isValueBinding(beanId))
	    	expr = beanId;
	    else
	    	expr = getExpression(beanId);

	    ELContext elContext = context.getELContext();
	    ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(elContext, expr, Object.class);
	    @SuppressWarnings("unchecked")
		T bean = (T) ve.getValue(elContext);
    	return bean;
    }

    public IWBundle getBundle(FacesContext ctx, String bundleIdentifier) {
    	IWMainApplication iwma = IWMainApplication.getIWMainApplication(ctx);
		return iwma.getBundle(bundleIdentifier);
    }

    public IWBundle getBundle(IWUserContext iwuc, String bundleIdentifier) {
		IWMainApplication iwma = iwuc.getApplicationContext().getIWMainApplication();
		return iwma.getBundle(bundleIdentifier);
	}
    
}

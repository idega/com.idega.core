/*
 * $Id: IWBaseComponent.java,v 1.9 2005/10/26 17:34:35 tryggvil Exp $
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
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.util.RenderUtils;
import com.idega.util.text.TextStyler;

/**
 * <p>
 * This is a base UI component for JSF that adds IW utility methods.<br/>
 * This is supposed to be a convenient replacement for PresentationObject for new
 * pure JSF solutions and doesn't have the some of the legacy burdens that PresentationObject has
 * such as the old style idegaWeb main(IWContext) and print(IWContext) methods and event systems.
 * </p>
 * Copyright (C) idega software 2004-2005 <br/>
 * Last modified: $Date: 2005/10/26 17:34:35 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.9 $
 * 
 */
public class IWBaseComponent extends UIComponentBase {
	
	private TextStyler _styler;
	private String styleAttribute;
	private boolean isInitialized = false;
	
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
	public void decode(FacesContext arg0) {
		super.decode(arg0);
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
	 */
	public void processDecodes(FacesContext arg0) {
		super.processDecodes(arg0);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {
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

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
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
		super.encodeChildren(context);
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
	public void encodeEnd(FacesContext arg0) throws IOException {
		// TODO Auto-generated method stub
		super.encodeEnd(arg0);
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getRendersChildren()
	 */
	public boolean getRendersChildren() {
		//return true;
		return super.getRendersChildren();
	}

	/**
	 * 
	 * @uml.property name="styleAttribute"
	 */
	public void setStyleAttribute(String style) {
		if (_styler == null) {
			_styler = new TextStyler();
		}
		_styler.parseStyleString(style);
		styleAttribute = style;
		//this.set("style", _styler.getStyleString());

	}

	public void setStyleAttribute(String attribute, String value) {
		if (_styler == null) {
			_styler = new TextStyler();
		}
		_styler.setStyleValue(attribute, value);
		setStyleAttribute( _styler.getStyleString());
	}

	/**
	 * 
	 * @uml.property name="styleAttribute"
	 */
	public String getStyleAttribute() {
		return styleAttribute;
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#getFamily()
	 */
	public String getFamily() {
		return "idegaweb";
	}
	/**
	 * <p>
	 * This method was refactored and replaced with initializeComponent
	 * </p>
	 * @deprecated Replaced with initializeComponent
	 */
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
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[4];
		values[0] = super.saveState(ctx);
		values[1] = styleAttribute;
		values[2] = _styler;
		values[3] = new Boolean(isInitialized);
		return values;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		styleAttribute = ((String) values[1]);
		_styler = (TextStyler) values[2];
		isInitialized = ((Boolean) values[3]).booleanValue();
	}
	
	/**
	 * <p>
	 * Get the IWMainapplication from the context
	 * </p>
	 * @param context
	 * @return
	 */
	protected IWMainApplication getIWMainApplication(FacesContext context){
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
	protected IWBundle getIWBundle(FacesContext context,String bundleIdentifier){
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
	protected IWResourceBundle getIWResourceBundle(FacesContext context,String bundleIdentifier){
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
	
}

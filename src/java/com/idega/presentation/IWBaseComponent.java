/*
 * $Id: IWBaseComponent.java,v 1.5 2004/12/15 14:54:45 gimmi Exp $
 * Created on 20.2.2004 by  tryggvil in project com.project
 * 
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import com.idega.util.RenderUtils;
import com.idega.util.text.TextStyler;

/**
 * This is a base component for JSF that adds IW utility methods.<br>
 * This is supposed to be a convenient replacement for PresentationObject
 * but doesn't have the some of the legacy burdens that PresentationObject has
 * such as the old style idegaWeb event systems.
 * 
 * Copyright (C) idega software 2004
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
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
			initializeContent();
			setInitialized();
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
	 * Renders a child component for the current component. This operation is handy when implementing
	 * renderes that perform child rendering themselves (eg. a layout renderer/grid renderer/ etc..).
	 * Passes on any IOExceptions thrown by the child/child renderer.
	 * 
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
	 * This is a method that is ensured that is only called once in initalization in a
	 * state saved component. This method is intended to be implemented in subclasses for example to add components.
	 */
	protected void initializeContent() {
		//does nothing by default
	}
	
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
	 * @see javax.faces.component.UIPanel#saveState(javax.faces.context.FacesContext)
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
	 * @see javax.faces.component.UIPanel#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		styleAttribute = ((String) values[1]);
		_styler = (TextStyler) values[2];
		isInitialized = ((Boolean) values[3]).booleanValue();
	}
	
	
}

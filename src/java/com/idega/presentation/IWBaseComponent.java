/*
 * Created on 20.2.2004 by  tryggvil in project com.project
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.Iterator;
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

	/**
	 * 
	 * @uml.property name="_styler"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private TextStyler _styler;

	private String styleAttribute;

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
		// TODO Auto-generated method stub
		super.decode(arg0);
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
	 */
	public void processDecodes(FacesContext arg0) {
		// TODO Auto-generated method stub
		super.processDecodes(arg0);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext arg0) throws IOException {
		// TODO Auto-generated method stub
		super.encodeBegin(arg0);
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
}

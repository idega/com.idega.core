// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWConstants;

/**
 * This component renders a layer or a 'div' tag in HTML around its children.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class Layer extends PresentationObjectContainer {
	
	public static final String DIV = "div";
	public static final String SPAN = "span";

	String layerType = DIV;

	public Layer() {
		this(DIV);
	}

	public Layer(String layerType) {
		super();
		this.layerType = layerType;
		setTransient(false);
	}

	public void print(IWContext iwc) throws Exception {
		encodeBegin(iwc);
		encodeChildren(iwc);
		encodeEnd(iwc);
	}
	
	public void encodeBegin(FacesContext context)throws IOException{
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			print("<" + this.layerType + " ");
			print(getMarkupAttributesString() + ">");
		}
	}
	
	public void encodeChildren(FacesContext context) throws IOException{
		if(!goneThroughRenderPhase()){
			Iterator children = this.getChildren().iterator();
			while (children.hasNext()) {
				UIComponent element = (UIComponent) children.next();
				renderChild(context,element);
			}
		}
	}
	
	public void encodeEnd(FacesContext context)throws IOException{
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML)) {
			println("</" + this.layerType + ">");
		}
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public void setHeight(int height) {
		setHeightStyle(Integer.toString(height));
	}
	
	public void setHeight(String height) {
		setHeightStyle(height);
	}
	
	public void setWidth(int width) {
		setWidthStyle(Integer.toString(width));
	}
	
	public void setWidth(String width) {
		setWidthStyle(width);
	}
	
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this.layerType = (String)values[1];
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = this.layerType;
		return values;
	}
	
	
}
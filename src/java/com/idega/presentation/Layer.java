// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWConstants;
import com.idega.util.StringUtil;

/**
 * This component renders a layer or a 'div' tag in HTML around its children.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class Layer extends PresentationObjectContainer {

	public static final String DIV = "div";
	public static final String SPAN = "span";
	public static final String ZINDEX = "z-index";

	String layerType = DIV;

	public Layer() {
		this(DIV);
	}

	public Layer(String layerType) {
		super();
		this.layerType = layerType;
		setTransient(false);
	}

	@Override
	public void print(IWContext iwc) throws Exception {
		encodeBegin(iwc);
		encodeChildren(iwc);
		encodeEnd(iwc);
	}

	@Override
	public void encodeBegin(FacesContext context)throws IOException{
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML) && !StringUtil.isEmpty(layerType)){
			print("<" + this.layerType + " ");
			print(getMarkupAttributesString() + ">");
		}
	}

	@Override
	public void encodeChildren(FacesContext context) throws IOException{
		if(!goneThroughRenderPhase()){
			for (UIComponent element : getChildren()) {
				renderChild(context,element);
			}
		}
	}

	@Override
	public void encodeEnd(FacesContext context)throws IOException{
		if (getMarkupLanguage().equals(IWConstants.MARKUP_LANGUAGE_HTML) && !StringUtil.isEmpty(layerType)) {
			println("</" + this.layerType + ">");
		}
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public void setHeight(int height) {
		setHeightStyle(Integer.toString(height));
	}

	@Override
	public void setHeight(String height) {
		setHeightStyle(height);
	}

	public void setWidth(int width) {
		setWidthStyle(Integer.toString(width));
	}

	@Override
	public void setWidth(String width) {
		setWidthStyle(width);
	}

	/* Readded for backwards compatability */
	@Override
	@Deprecated
	public void setZIndex(int index) {
		setZIndex(String.valueOf(index));
	}

	/* Readded for backwards compatability */
	@Deprecated
	public void setZIndex(String index) {
		setStyleAttribute(ZINDEX, index);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this.layerType = (String)values[1];
	}

	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = this.layerType;
		return values;
	}
}
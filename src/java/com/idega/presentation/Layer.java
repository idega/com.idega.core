// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation;

import javax.faces.context.FacesContext;
import com.idega.util.text.TextStyler;

/**
 * This component renders a layer or a 'div' tag in HTML around its children.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.2
 */
public class Layer extends PresentationObjectContainer {
	
	public static final String ABSOLUTE = "absolute";
	public static final String FIXED = "fixed";
	public static final String DEFAULT_UNIT = "px";
	public static final String NOWRAP = "nowrap";
	public static final String PADDING = "padding";
	public static final String RELATIVE = "relative";

	public static final String DIV = "div";
	public static final String SPAN = "span";

	public static final String BACKGROUND_COLOR = "background-color";
	public static final String BACKGROUND_IMAGE = "background-image";
	public static final String LEFT = "left";
	public static final String MOUSE_OUT = "onmouseout";
	public static final String MOUSE_OVER = "onmouseover";
	public static final String OVERFLOW = "overflow";
	public static final String POSITION = "position";
	public static final String TOP = "top";
	public static final String VISIBILITY = "visibility";
	public static final String WHITESPACE = "white-space";
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

	private boolean isPositionSet() {
		TextStyler styler = new TextStyler(getStyleAttribute());
		return styler.isStyleSet(POSITION);
	}

	public void print(IWContext iwc) throws Exception {
		if (doPrint(iwc)) {
			if (getMarkupLanguage().equals("HTML")) {
				print("<" + layerType + " ");
				print(getMarkupAttributesString() + ">");
				super.print(iwc);
				println("</" + layerType + ">");
			}
		}
	}

	public void setBackgroundColor(String backgroundColor) {
		setStyleAttribute(BACKGROUND_COLOR, backgroundColor);
	}

	public void setBackgroundImage(Image image) {
		setBackgroundImage(image.getURL());
	}

	public void setBackgroundImage(String url) {
		setStyleAttribute(BACKGROUND_IMAGE, "url(" + url + ")");
	}

	public void setHeight(int height) {
		setHeightStyle(Integer.toString(height));
	}

	public void setHeight(String height) {
		setHeightStyle(height);
	}

	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	public void setLeftPosition(int xpos) {
		setLeftPosition(xpos, DEFAULT_UNIT);
	}
	
	public void setLeftPosition(int xpos, String unit) {
		setLeftPosition(String.valueOf(xpos), unit);
	}
	
	public void setLeftPosition(String xpos) {
		setLeftPosition(xpos, DEFAULT_UNIT);
	}

	public void setLeftPosition(String xpos, String unit) {
		setStyleAttribute(LEFT, xpos+unit);
		if (!isPositionSet()) {
			setStyleAttribute(POSITION, ABSOLUTE);
		}
	}

	public void setNoWrap() {
		setNoWrap(true);
	}

	public void setNoWrap(boolean noWrap) {
		if (noWrap) {
			setStyleAttribute(WHITESPACE, NOWRAP);
		}
		else {
			TextStyler styler = new TextStyler(this.getStyleAttribute());
			styler.removeStyleValue(WHITESPACE);
		}
	}

	public void setOnMouseOut(String action) {
		setMarkupAttributeMultivalued(MOUSE_OUT, action);
	}
	
	public void setOnMouseOver(String action) {
		setMarkupAttributeMultivalued(MOUSE_OVER, action);
	}

	public void setOverflow(String overflowType) {
		setStyleAttribute(OVERFLOW, overflowType);
	}
	
	public void setPadding(int padding, String unit) {
		setStyleAttribute(PADDING, padding+unit);
	}
	
	public void setPadding(int padding) {
		setPadding(padding, DEFAULT_UNIT);
	}

	public void setPositionType(String absoluteOrRelative) {
		setStyleAttribute(POSITION, absoluteOrRelative);
	}

	public void setTopPosition(int ypos) {
		setTopPosition(ypos, DEFAULT_UNIT);
	}

	public void setTopPosition(int ypos, String unit) {
		setTopPosition(String.valueOf(ypos), unit);
	}

	public void setTopPosition(String ypos) {
		setTopPosition(ypos, DEFAULT_UNIT);
	}

	public void setTopPosition(String ypos, String unit) {
		setStyleAttribute(TOP, ypos+unit);
		if (!isPositionSet()) {
			setStyleAttribute(POSITION, ABSOLUTE);
		}
	}

	public void setVisibility(String visibilityType) {
		setStyleAttribute(VISIBILITY, visibilityType);
	}

	public void setWidth(int width) {
		setWidthStyle(Integer.toString(width));
	}

	public void setWidth(String width) {
		setWidthStyle(width);
	}

	public void setZIndex(int index) {
		setZIndex(String.valueOf(index));
	}

	public void setZIndex(String index) {
		setStyleAttribute(ZINDEX, index);
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
		values[1] = layerType;
		return values;
	}
	
	
}
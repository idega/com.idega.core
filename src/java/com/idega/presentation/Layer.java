// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */
package com.idega.presentation;

import java.util.Iterator;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class Layer extends PresentationObjectContainer
{
	public static final String RELATIVE = "relative";
	public static final String ABSOLUTE = "absolute";
	public static final String DIV = "div";
	public static final String SPAN = "span";
	public static final String LEFT = "left";
	public static final String TOP = "top";
	public static final String ZINDEX = "z-index";
	public static final String POSITION = "position";
	public static final String _ATTRIB_NOWRAP = "nowrap";
	public boolean _nowrap = false;
	private boolean noStyle = false;
	String align;
	String onMouseOut;
	String absoluteOrRelative;
	String layerType;
	public Layer()
	{
		this(DIV);
	}
	public Layer(String layerType)
	{
		super();
		this.layerType = layerType;
	}
	public String getMarkupAttributesString()
	{
		if (noStyle)
		{
			return super.getMarkupAttributesString();
		}
		else
		{
			String returnString = "";
			if (getMarkupAttributes() != null)
			{
				for (Iterator iter = getMarkupAttributes().keySet().iterator(); iter.hasNext();)
				{
					Object Attribute = iter.next();
					String AttributeString = (String) Attribute;
					returnString = returnString + " " + AttributeString + ": " + (String) attributes.get(Attribute);
					if (AttributeString.equals(LEFT) || AttributeString.equals(TOP))
						returnString = returnString + "px";
					returnString = returnString + ";";
				}
			}
			return returnString;
		}
	}
	public void setLeftPosition(String xpos)
	{
		setMarkupAttribute(LEFT, xpos);
		if (absoluteOrRelative == null)
			setMarkupAttribute("position", ABSOLUTE);
	}
	public void setLeftPosition(int xpos)
	{
		setLeftPosition(String.valueOf(xpos));
	}
	public void setNoWrap()
	{
		_nowrap = true;
	}
	public void setNoWrap(boolean value)
	{
		_nowrap = value;
	}
	public void setTopPosition(String ypos)
	{
		setMarkupAttribute(TOP, ypos);
		if (absoluteOrRelative == null)
			setMarkupAttribute("position", ABSOLUTE);
	}
	public void setTopPosition(int ypos)
	{
		setTopPosition(String.valueOf(ypos));
	}
	public void setVisibility(String visibilityType)
	{
		setMarkupAttribute("visibility", visibilityType);
	}
	public void setWidth(int width)
	{
		setMarkupAttribute("width", Integer.toString(width) + "px");
	}
	public void setHeight(int height)
	{
		setMarkupAttribute("height", Integer.toString(height) + "px");
	}
	public void setOverflow(String overflowType)
	{
		setMarkupAttribute("overflow", overflowType);
	}
	public void setZIndex(int index)
	{
		setZIndex(String.valueOf(index));
	}
	public void setZIndex(String index)
	{
		setMarkupAttribute(ZINDEX, index);
	}
	public void setBackgroundColor(String backgroundColor)
	{
		setMarkupAttribute("background-color", backgroundColor);
		setMarkupAttribute("layer-background-color", backgroundColor);
	}
	public void setLayerType(String layerType)
	{
		this.layerType = layerType;
	}
	/*
	 * public void setBorder(int borderWidth,String borderColor){
	 *  }
	 * 
	 * public void setBorder(int borderWidth){
	 *  }
	 * 
	 * 
	 * public void setBorderColor(String color){
	 *  }
	 */
	public void setPositionType(String absoluteOrRelative)
	{
		this.absoluteOrRelative = absoluteOrRelative;
		setMarkupAttribute(POSITION, absoluteOrRelative);
	}
	public void setBackgroundImage(String url)
	{
		setMarkupAttribute("background-image", "url(" + url + ")");
		setMarkupAttribute("layer-background-image", "url(" + url + ")");
	}
	public void setBackgroundImage(Image image)
	{
		setBackgroundImage(image.getURL());
	}
	public void setOnMouseOut(String action)
	{
		onMouseOut = action;
	}
	public void setNoStyle(boolean value)
	{
		noStyle = value;
	}
	private String getOnMouseOut()
	{
		if (onMouseOut != null)
		{
			return "onMouseOut=\"" + onMouseOut + "\"";
		}
		return "";
	}
	public void print(IWContext iwc) throws Exception
	{
		if (doPrint(iwc))
		{
			if (getLanguage().equals("HTML"))
			{
				boolean alignSet = isMarkupAttributeSet(HORIZONTAL_ALIGNMENT);
				print("<" + layerType + " ");
				if (_nowrap)
				{
					print(_ATTRIB_NOWRAP + " ");
				}
				if (alignSet)
				{
					print("align=\"" + getHorizontalAlignment() + "\" ");
					removeMarkupAttribute(HORIZONTAL_ALIGNMENT); //does this slow
														   // things down?
				}
				if (noStyle)
				{
					print(getMarkupAttributesString() + " ");
				}
				else
				{
					print("id=\"" + getID() + "\" ");
					print("style=\"" + getMarkupAttributesString() + "\" ");
				}
				println(getOnMouseOut() + ">");
				super.print(iwc);
				println("\n</" + layerType + ">");
			} //end if (getLanguage(...
		}
	}
}

/*
 * $Id: TableType.java,v 1.1 2004/09/24 13:50:34 thomas Exp $
 * Created on Sep 23, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import javax.faces.component.UIComponent;
import com.idega.util.IWColor;


/**
 * 
 *  Last modified: $Date: 2004/09/24 13:50:34 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 */
public interface TableType {
	
	
	/**
	 * @param beginxpos
	 * @param beginypos
	 * @param endxpos
	 * @param endypos
	 */
	void mergeCells(int beginxpos, int beginypos, int endxpos,
			int endypos);
	/**
	 * @return
	 */
	int getColumns();
	/**
	 * @return
	 */
	int getRows();
	/**
	 * @param modObject
	 * @param xpos
	 * @param ypos
	 */
	void add(PresentationObject modObject, int xpos, int ypos);
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String)
	 */
	void add(String theText);
	
	/**
	 * @param text
	 * @param xpos
	 * @param ypos
	 */
	void add(String text, int xpos, int ypos);

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String[])
	 */
	void add(String[] theTextArray);

	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(javax.faces.component.UIComponent)
	 */
	void add(UIComponent component);
	
	/**
	 * @param comp
	 * @param xpos
	 * @param ypos
	 */
	void add(UIComponent comp, int xpos, int ypos);
	
	/**
	 * @param height
	 */
	void setHeight(int height);
	
	/**
	 * @param width
	 */
	void setWidth(int width);
	
	/**
	 * @param width
	 */
	void setWidth(String width);
		
	/**
	 * @param backgroundImage
	 */
	void setBackgroundImage(Image backgroundImage);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImage
	 */
	void setBackgroundImage(int xpos, int ypos, Image backgroundImage);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImageURL
	 */
	void setBackgroundImageURL(int xpos, int ypos,
			String backgroundImageURL);
	
	/**
	 * @param backgroundImageURL
	 */
	void setBackgroundImageURL(String backgroundImageURL);
	
	/**
	 * @param i
	 */
	void setBorder(int i);
	
	/**
	 * @param border
	 */
	void setBorder(String border);
	
	/**
	 * @param color
	 */
	void setBorderColor(String color);
	
	/**
	 * @param value
	 */
	void setBottomLine(boolean value);
	
	/**
	 * @param column
	 * @param row
	 * @param borderWidth
	 * @param borderColor
	 * @param borderStyle
	 */
	void setCellBorder(int column, int row, int borderWidth,
			String borderColor, String borderStyle);
	
	/**
	 * @param column
	 * @param row
	 * @param borderColor
	 */
	void setCellBorderColor(int column, int row, String borderColor);
	
	/**
	 * @param column
	 * @param row
	 * @param borderStyle
	 */
	void setCellBorderStyle(int column, int row, String borderStyle);
	
	/**
	 * @param column
	 * @param row
	 * @param borderWidth
	 */
	void setCellBorderWidth(int column, int row, int borderWidth);
	
	/**
	 * @param i
	 */
	void setCellpadding(int i);

	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpadding(int column, int row, int padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpadding(int column, int row, String padding);
	
	/**
	 * @param s
	 */
	void setCellpadding(String s);
	/**
	 * @param i
	 */
	void setCellpaddingAndCellspacing(int i);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingBottom(int column, int row, int padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingBottom(int column, int row, String padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingLeft(int column, int row, int padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingLeft(int column, int row, String padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingRight(int column, int row, int padding);

	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingRight(int column, int row, String padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingTop(int column, int row, int padding);
	
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	void setCellpaddingTop(int column, int row, String padding);
	
	/**
	 * @param i
	 */
	void setCellspacing(int i);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	void setColor(int xpos, int ypos, IWColor color);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	void setColor(int xpos, int ypos, String color);
	
	/**
	 * @param color
	 */
	void setColor(IWColor color);
	
	/**
	 * @param color
	 */
	void setColor(String color);
	
	/**
	 * @param xpos
	 * @param alignment
	 */
	void setColumnAlignment(int xpos, String alignment);
	/**
	 * @param xpos
	 * @param attributeName
	 * @param attributeValue
	 */
	void setColumnAttribute(int xpos, String attributeName,
			String attributeValue);
	
	/**
	 * @param xpos
	 * @param color
	 */
	void setColumnColor(int xpos, String color);
	
	/**
	 * @param xpos
	 * @param height
	 */
	void setColumnHeight(int xpos, String height);
	
	/**
	 * @param column
	 * @param padding
	 */
	void setColumnPadding(int column, int padding);
	
	/**
	 * @param columns
	 */
	void setColumns(int columns);
	
	/**
	 * @param xpos
	 * @param styleAttribute
	 * @param styleValue
	 */
	void setColumnStyle(int xpos, String styleAttribute,
			String styleValue);
	
	/**
	 * @param xpos
	 * @param styleClass
	 */
	void setColumnStyleClass(int xpos, String styleClass);
	
	/**
	 * @param xpos
	 * @param alignment
	 */
	void setColumnVerticalAlignment(int xpos, String alignment);
	
	/**
	 * @param xpos
	 * @param width
	 */
	void setColumnWidth(int xpos, String width);
	
	/**
	 * @param ypos
	 * @param height
	 */
	void setHeight(int ypos, int height);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	void setHeight(int xpos, int ypos, int height);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	void setHeight(int xpos, int ypos, String height);
	
	/**
	 * @param ypos
	 * @param height
	 */
	void setHeight(int ypos, String height);
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#setHeight(java.lang.String)
	 */
	void setHeight(String height);
	
	/**
	 * @param Color1
	 * @param Color2
	 */
	void setHorizontalZebraColored(String Color1, String Color2);
	
	/**
	 * @param label
	 * @param xpos
	 * @param ypos
	 */
	void setLabel(String label, int xpos, int ypos);
	
	/**
	 * @param value
	 */
	void setLeftLine(boolean value);
	
	/**
	 * @param column
	 */
	void setLineAfterColumn(int column);
	
	/**
	 * @param column
	 * @param value
	 */
	void setLineAfterColumn(int column, boolean value);
	
	/**
	 * @param row
	 */
	void setLineAfterRow(int row);
	
	/**
	 * @param row
	 * @param value
	 */
	void setLineAfterRow(int row, boolean value);

	/**
	 * @param color
	 */
	void setLineColor(String color);
	
	/**
	 * @param value
	 */
	void setLineFrame(boolean value);
	
	/**
	 * @param height
	 */
	void setLineHeight(String height);
	
	/**
	 * @param value
	 */
	void setLinesBetween(boolean value);
	
	/**
	 * @param width
	 */
	void setLineWidth(String width);
	
	/**
	 * 
	 */
	void setNoWrap();
	/**
	 * @param xpos
	 * @param ypos
	 */
	void setNoWrap(int xpos, int ypos);
	
	/**
	 * @param resizable
	 */
	void setResizable(boolean resizable);
	
	/**
	 * @param value
	 */
	void setRightLine(boolean value);
	
	/**
	 * @param ypos
	 * @param alignment
	 */
	void setRowAlignment(int ypos, String alignment);
	
	/**
	 * @param ypos
	 * @param attributeName
	 * @param attributeValue
	 */
	void setRowAttribute(int ypos, String attributeName,
			String attributeValue);
	
	/**
	 * @param ypos
	 * @param color
	 */
	void setRowColor(int ypos, String color);
	
	/**
	 * @param ypos
	 * @param height
	 */
	void setRowHeight(int ypos, String height);
	
	/**
	 * @param row
	 * @param padding
	 */
	void setRowPadding(int row, int padding);
	
	/**
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	void setRowStyle(int ypos, String styleAttribute, String styleValue);
	
	/**
	 * @param ypos
	 * @param styleClass
	 */
	void setRowStyleClass(int ypos, String styleClass);
	
	/**
	 * @param ypos
	 * @param alignment
	 */
	void setRowVerticalAlignment(int ypos, String alignment);
	
	/**
	 * @param ypos
	 * @param width
	 */
	void setRowWidth(int ypos, String width);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	void setStyle(int xpos, int ypos, String styleAttribute,
			String styleValue);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleName
	 */
	void setStyleClass(int xpos, int ypos, String styleName);
	
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	void setTableBorder(int width, String color, String style);
	
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	void setTableBorderBottom(int width, String color, String style);
	
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	void setTableBorderTop(int width, String color, String style);
	
	/**
	 * @param value
	 */
	void setTopLine(boolean value);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param alignment
	 */
	void setVerticalAlignment(int xpos, int ypos, String alignment);
	
	/**
	 * @param verticalAlignment
	 */
	void setVerticalAlignment(String verticalAlignment);
	
	/**
	 * @param Color1
	 * @param Color2
	 */
	void setVerticalZebraColored(String Color1, String Color2);
	
	/**
	 * @param value
	 */
	void setVerticatLinesBetween(boolean value);
	
	/**
	 * @param xpos
	 * @param width
	 */
	void setWidth(int xpos, int width);
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	void setWidth(int xpos, int ypos, int width);
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	void setWidth(int xpos, int ypos, String width);
	
	/**
	 * @param xpos
	 * @param width
	 */
	void setWidth(int xpos, String width);

	void setAlignment(int xpos, int ypos, String alignment);
	
}

/*
 * Created on 12.7.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.presentation.ui;

import javax.faces.component.UIComponent;

import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.TableType;
import com.idega.util.IWColor;


/**
 * @author aron
 *
 * ScrollTable displays the table content in a predefined size (width and height)
 * with scrollbars to browse the whole content
 */
public class ScrollTable extends Block implements TableType {
	
	private int scrollLayerHeaderRowThickness = -23;
	private int scrollLayerWidthPadding = 15;
	private int scrollLayerHeightPadding = 15;
	private int numberOfHeaderRows = 1;
	private int scrollLayerWidth = -1;
	
	private Table theTable = null;
	private Table headerTable = null;	
	
	public ScrollTable(){
		theTable = new Table();
		headerTable = new Table();
	}

	/**
	 * @return Returns the scrollLayerFirstRowOffset.
	 */
	public int getScrollLayerHeaderRowThickness() {
		return scrollLayerHeaderRowThickness;
	}
	/**
	 * @param offset The scrollLayerFirstRowOffset to set.
	 * default set to -23
	 */
	public void setScrollLayerHeaderRowThickness(int thickness) {
		this.scrollLayerHeaderRowThickness = thickness;
	}
	/**
	 * @return Returns the scrollLayerHeightPadding.
	 * Default set to 15
	 */
	public int getScrollLayerHeightPadding() {
		return scrollLayerHeightPadding;
	}
	/**
	 * @param scrollLayerHeightPadding The scrollLayerHeightPadding to set.
	 */
	public void setScrollLayerHeightPadding(int scrollLayerHeightPadding) {
		this.scrollLayerHeightPadding = scrollLayerHeightPadding;
	}
	/**
	 * @return Returns the scrollLayerWidthPadding.
	 */
	public int getScrollLayerWidthPadding() {
		return scrollLayerWidthPadding;
	}
	/**
	 * @param scrollLayerWidthPadding The scrollLayerWidthPadding to set.
	 * Default set to 15
	 */
	public void setScrollLayerWidthPadding(int scrollLayerWidthPadding) {
		this.scrollLayerWidthPadding = scrollLayerWidthPadding;
	}
	
	
	
	/**
	 * @return Returns the numberOfHeaderRows.
	 */
	public int getNumberOfHeaderRows() {
		return numberOfHeaderRows;
	}
	/**
	 * @param numberOfHeaderRows The numberOfHeaderRows to set.
	 */
	public void setNumberOfHeaderRows(int numberOfHeaderRows) {
		this.numberOfHeaderRows = numberOfHeaderRows;
	}
	
	/**
	 * @return Returns the scrollLayerWidth.
	 */
	public int getScrollLayerWidth() {
		return scrollLayerWidth;
	}
	/**
	 * @param scrollLayerWidth The scrollLayerWidth to set.
	 */
	public void setScrollLayerWidth(int scrollLayerWidth) {
		this.scrollLayerWidth = scrollLayerWidth;
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		
			numberOfHeaderRows = numberOfHeaderRows<1?1:numberOfHeaderRows;
			Table headerTable = (Table) theTable.clone(null,false,1,numberOfHeaderRows);
			headerTable.resize(theTable.getColumns(),numberOfHeaderRows);
			headerTable.removeStyleAttribute(Table.HEIGHT);
			
			StringBuffer scriptSource1 = new StringBuffer("\t if(document.getElementById){");
			StringBuffer scriptSource2 = new StringBuffer("\t else if(document.all){");
			Image heightImage = new Image("blank.gif");
			heightImage.setHeight("100%");
			heightImage.setWidth(1);
			/*for(int row=1;row<=numberOfHeaderRows;row++){
			
				for (int col = 1; col <= theTable.getColumns(); col++) {
					TableCell cell = theTable.getCellAt(col,row);
					headerTable.add(cell,col,row);
					headerTable.setStyleAttribute(cell.getStyleAttribute());
				}
			}*/
			Table table = (Table)theTable.clone(null,false);
			int layerWidth = -1,layerHeight=-1;
			if(theTable.getWidth()!=null){
				try {
					layerWidth = Integer.parseInt(table.getWidth());
				} catch (NumberFormatException e) {
				}
			}
			if(theTable.getHeight()!=null){
				try {
					layerHeight = Integer.parseInt(table.getHeight());
				} catch (NumberFormatException e) {
				}
			}
	
			theTable = null;
			
			Image image = new Image("blank.gif");
			image.setWidth("100%");
			image.setHeight(1);
			int testrow = table.getRows()+1;
			String generatedID = "_"+generateID();
			for (int col = 1; col <= table.getColumns(); col++) {
				String name = "tstimg"+col+generatedID;
				String cellName = "cll"+col+generatedID;
				Image img = (Image)image.clone();
				img.setName(name);
				img.setID(name);
				table.add(img,col,testrow);
				headerTable.getCellAt(col,numberOfHeaderRows).setID(cellName);
				scriptSource1.append("\n\t\t document.getElementById('").append(cellName).append("').width=document.images['").append(name).append("'].width;");
				scriptSource2.append("\n\t\t document.all.").append(cellName).append(".width=document.all.").append(name).append(".width;");
			}
			testrow++;
			table.mergeCells(1,testrow,table.getColumns(),testrow);
			Image img = (Image)image.clone();
			img.setName("widthImage"+generatedID);
			img.setID("widthImage"+generatedID);
			table.add(img,1,testrow);
			
			
			layerWidth += scrollLayerWidthPadding;
			layerHeight += scrollLayerHeightPadding;
			headerTable.setWidth(table.getWidth());
			
			Layer headerLayer = new Layer(Layer.DIV);
			headerLayer.setWidth(table.getWidth());
			headerLayer.add(headerTable);
			
			Layer layer = new Layer(Layer.DIV);
			layer.setOverflow("auto");
			layer.setPositionType(Layer.RELATIVE);
			if(scrollLayerWidth>0)
				layer.setWidth(scrollLayerWidth);
			else if(layerWidth>0)
				layer.setWidth(layerWidth);
			if(layerHeight>0)
				layer.setHeight(layerHeight);
			table.setStyleAttribute(Layer.POSITION,Layer.RELATIVE);
			if(scrollLayerHeaderRowThickness>0)
				scrollLayerHeaderRowThickness *= -1;
			table.setStyleAttribute(Layer.TOP,String.valueOf(scrollLayerHeaderRowThickness*numberOfHeaderRows));
			layer.add(table);
			
			String scriptName = "measureCells";
			StringBuffer script = new StringBuffer("var tomt;\nfunction "+scriptName+generatedID+"(){\n");
			script.append(scriptSource1.append("\n\t }\n"));
			script.append(scriptSource2.append("\n\t }\n"));
			script.append("\n}\n");
			
			getParentPage().getAssociatedScript().addFunction(scriptName+generatedID,script.toString());
			getParentPage().setOnLoad(scriptName+generatedID+"()");
			
			super.add(headerLayer);
			super.add(layer);
		
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	
	public synchronized Object clone() {
		ScrollTable obj = null;
		try {
			obj = (ScrollTable) super.clone();
			obj.theTable = (Table)theTable.clone();
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}
	
	
	
	
	/**
	 * @param beginxpos
	 * @param beginypos
	 * @param endxpos
	 * @param endypos
	 */
	public void mergeCells(int beginxpos, int beginypos, int endxpos,
			int endypos) {
		theTable.mergeCells(beginxpos, beginypos, endxpos, endypos);
	}
	/**
	 * @return
	 */
	public int getColumns() {
		return theTable.getColumns();
	}
	/**
	 * @return
	 */
	public int getRows() {
		return theTable.getRows();
	}
	/**
	 * @param modObject
	 * @param xpos
	 * @param ypos
	 */
	public void add(PresentationObject modObject, int xpos, int ypos) {
		theTable.add(modObject, xpos, ypos);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String)
	 */
	public void add(String theText) {
		theTable.add(theText);
	}
	/**
	 * @param text
	 * @param xpos
	 * @param ypos
	 */
	public void add(String text, int xpos, int ypos) {
		theTable.add(text, xpos, ypos);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String[])
	 */
	public void add(String[] theTextArray) {
		theTable.add(theTextArray);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(javax.faces.component.UIComponent)
	 */
	public void add(UIComponent component) {
		theTable.add(component);
	}
	/**
	 * @param comp
	 * @param xpos
	 * @param ypos
	 */
	public void add(UIComponent comp, int xpos, int ypos) {
		theTable.add(comp, xpos, ypos);
	}
	
	/**
	 * @param height
	 */
	public void setHeight(int height) {
		theTable.setHeight(height);
	}
	
	/**
	 * @param width
	 */
	public void setWidth(int width) {
		theTable.setWidth(width);
	}
	
	/**
	 * @param width
	 */
	public void setWidth(String width){
		theTable.setWidth(width);
	}
	
	
	
	
	/**
	 * @param backgroundImage
	 */
	public void setBackgroundImage(Image backgroundImage) {
		theTable.setBackgroundImage(backgroundImage);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImage
	 */
	public void setBackgroundImage(int xpos, int ypos, Image backgroundImage) {
		theTable.setBackgroundImage(xpos, ypos, backgroundImage);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImageURL
	 */
	public void setBackgroundImageURL(int xpos, int ypos,
			String backgroundImageURL) {
		theTable.setBackgroundImageURL(xpos, ypos, backgroundImageURL);
	}
	/**
	 * @param backgroundImageURL
	 */
	public void setBackgroundImageURL(String backgroundImageURL) {
		theTable.setBackgroundImageURL(backgroundImageURL);
	}
	/**
	 * @param i
	 */
	public void setBorder(int i) {
		theTable.setBorder(i);
	}
	/**
	 * @param border
	 */
	public void setBorder(String border) {
		theTable.setBorder(border);
	}
	/**
	 * @param color
	 */
	public void setBorderColor(String color) {
		theTable.setBorderColor(color);
	}
	/**
	 * @param value
	 */
	public void setBottomLine(boolean value) {
		theTable.setBottomLine(value);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderWidth
	 * @param borderColor
	 * @param borderStyle
	 */
	public void setCellBorder(int column, int row, int borderWidth,
			String borderColor, String borderStyle) {
		theTable.setCellBorder(column, row, borderWidth, borderColor,
				borderStyle);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderColor
	 */
	public void setCellBorderColor(int column, int row, String borderColor) {
		theTable.setCellBorderColor(column, row, borderColor);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderStyle
	 */
	public void setCellBorderStyle(int column, int row, String borderStyle) {
		theTable.setCellBorderStyle(column, row, borderStyle);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderWidth
	 */
	public void setCellBorderWidth(int column, int row, int borderWidth) {
		theTable.setCellBorderWidth(column, row, borderWidth);
	}
	/**
	 * @param i
	 */
	public void setCellpadding(int i) {
		theTable.setCellpadding(i);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpadding(int column, int row, int padding) {
		theTable.setCellpadding(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpadding(int column, int row, String padding) {
		theTable.setCellpadding(column, row, padding);
	}
	/**
	 * @param s
	 */
	public void setCellpadding(String s) {
		theTable.setCellpadding(s);
	}
	/**
	 * @param i
	 */
	public void setCellpaddingAndCellspacing(int i) {
		theTable.setCellpaddingAndCellspacing(i);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingBottom(int column, int row, int padding) {
		theTable.setCellpaddingBottom(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingBottom(int column, int row, String padding) {
		theTable.setCellpaddingBottom(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingLeft(int column, int row, int padding) {
		theTable.setCellpaddingLeft(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingLeft(int column, int row, String padding) {
		theTable.setCellpaddingLeft(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingRight(int column, int row, int padding) {
		theTable.setCellpaddingRight(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingRight(int column, int row, String padding) {
		theTable.setCellpaddingRight(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingTop(int column, int row, int padding) {
		theTable.setCellpaddingTop(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingTop(int column, int row, String padding) {
		theTable.setCellpaddingTop(column, row, padding);
	}
	/**
	 * @param i
	 */
	public void setCellspacing(int i) {
		theTable.setCellspacing(i);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	public void setColor(int xpos, int ypos, IWColor color) {
		theTable.setColor(xpos, ypos, color);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	public void setColor(int xpos, int ypos, String color) {
		theTable.setColor(xpos, ypos, color);
	}
	/**
	 * @param color
	 */
	public void setColor(IWColor color) {
		theTable.setColor(color);
	}
	/**
	 * @param color
	 */
	public void setColor(String color) {
		theTable.setColor(color);
	}
	/**
	 * @param xpos
	 * @param alignment
	 */
	public void setColumnAlignment(int xpos, String alignment) {
		theTable.setColumnAlignment(xpos, alignment);
	}
	/**
	 * @param xpos
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setColumnAttribute(int xpos, String attributeName,
			String attributeValue) {
		theTable.setColumnAttribute(xpos, attributeName, attributeValue);
	}
	/**
	 * @param xpos
	 * @param color
	 */
	public void setColumnColor(int xpos, String color) {
		theTable.setColumnColor(xpos, color);
	}
	/**
	 * @param xpos
	 * @param height
	 */
	public void setColumnHeight(int xpos, String height) {
		theTable.setColumnHeight(xpos, height);
	}
	/**
	 * @param column
	 * @param padding
	 */
	public void setColumnPadding(int column, int padding) {
		theTable.setColumnPadding(column, padding);
	}
	/**
	 * @param columns
	 */
	public void setColumns(int columns) {
		theTable.setColumns(columns);
	}
	/**
	 * @param xpos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setColumnStyle(int xpos, String styleAttribute,
			String styleValue) {
		theTable.setColumnStyle(xpos, styleAttribute, styleValue);
	}
	/**
	 * @param xpos
	 * @param styleClass
	 */
	public void setColumnStyleClass(int xpos, String styleClass) {
		theTable.setColumnStyleClass(xpos, styleClass);
	}
	/**
	 * @param xpos
	 * @param alignment
	 */
	public void setColumnVerticalAlignment(int xpos, String alignment) {
		theTable.setColumnVerticalAlignment(xpos, alignment);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setColumnWidth(int xpos, String width) {
		theTable.setColumnWidth(xpos, width);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int ypos, int height) {
		theTable.setHeight(ypos, height);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int xpos, int ypos, int height) {
		theTable.setHeight(xpos, ypos, height);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int xpos, int ypos, String height) {
		theTable.setHeight(xpos, ypos, height);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int ypos, String height) {
		theTable.setHeight(ypos, height);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#setHeight(java.lang.String)
	 */
	public void setHeight(String height) {
		theTable.setHeight(height);
	}
	
	/**
	 * @param Color1
	 * @param Color2
	 */
	public void setHorizontalZebraColored(String Color1, String Color2) {
		theTable.setHorizontalZebraColored(Color1, Color2);
	}
	
	/**
	 * @param label
	 * @param xpos
	 * @param ypos
	 */
	public void setLabel(String label, int xpos, int ypos) {
		theTable.setLabel(label, xpos, ypos);
	}
	
	/**
	 * @param value
	 */
	public void setLeftLine(boolean value) {
		theTable.setLeftLine(value);
	}
	/**
	 * @param column
	 */
	public void setLineAfterColumn(int column) {
		theTable.setLineAfterColumn(column);
	}
	/**
	 * @param column
	 * @param value
	 */
	public void setLineAfterColumn(int column, boolean value) {
		theTable.setLineAfterColumn(column, value);
	}
	/**
	 * @param row
	 */
	public void setLineAfterRow(int row) {
		theTable.setLineAfterRow(row);
	}
	/**
	 * @param row
	 * @param value
	 */
	public void setLineAfterRow(int row, boolean value) {
		theTable.setLineAfterRow(row, value);
	}
	/**
	 * @param color
	 */
	public void setLineColor(String color) {
		theTable.setLineColor(color);
	}
	/**
	 * @param value
	 */
	public void setLineFrame(boolean value) {
		theTable.setLineFrame(value);
	}
	/**
	 * @param height
	 */
	public void setLineHeight(String height) {
		theTable.setLineHeight(height);
	}
	/**
	 * @param value
	 */
	public void setLinesBetween(boolean value) {
		theTable.setLinesBetween(value);
	}
	/**
	 * @param width
	 */
	public void setLineWidth(String width) {
		theTable.setLineWidth(width);
	}
	/**
	 * 
	 */
	public void setNoWrap() {
		theTable.setNoWrap();
	}
	/**
	 * @param xpos
	 * @param ypos
	 */
	public void setNoWrap(int xpos, int ypos) {
		theTable.setNoWrap(xpos, ypos);
	}
	/**
	 * @param resizable
	 */
	public void setResizable(boolean resizable) {
		theTable.setResizable(resizable);
	}
	/**
	 * @param value
	 */
	public void setRightLine(boolean value) {
		theTable.setRightLine(value);
	}
	/**
	 * @param ypos
	 * @param alignment
	 */
	public void setRowAlignment(int ypos, String alignment) {
		theTable.setRowAlignment(ypos, alignment);
	}
	/**
	 * @param ypos
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setRowAttribute(int ypos, String attributeName,
			String attributeValue) {
		theTable.setRowAttribute(ypos, attributeName, attributeValue);
	}
	/**
	 * @param ypos
	 * @param color
	 */
	public void setRowColor(int ypos, String color) {
		theTable.setRowColor(ypos, color);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setRowHeight(int ypos, String height) {
		theTable.setRowHeight(ypos, height);
	}
	/**
	 * @param row
	 * @param padding
	 */
	public void setRowPadding(int row, int padding) {
		theTable.setRowPadding(row, padding);
	}
	/**
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setRowStyle(int ypos, String styleAttribute, String styleValue) {
		theTable.setRowStyle(ypos, styleAttribute, styleValue);
	}
	/**
	 * @param ypos
	 * @param styleClass
	 */
	public void setRowStyleClass(int ypos, String styleClass) {
		theTable.setRowStyleClass(ypos, styleClass);
	}
	/**
	 * @param ypos
	 * @param alignment
	 */
	public void setRowVerticalAlignment(int ypos, String alignment) {
		theTable.setRowVerticalAlignment(ypos, alignment);
	}
	/**
	 * @param ypos
	 * @param width
	 */
	public void setRowWidth(int ypos, String width) {
		theTable.setRowWidth(ypos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setStyle(int xpos, int ypos, String styleAttribute,
			String styleValue) {
		theTable.setStyle(xpos, ypos, styleAttribute, styleValue);
	}
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleName
	 */
	public void setStyleClass(int xpos, int ypos, String styleName) {
		theTable.setStyleClass(xpos, ypos, styleName);
	}
	
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorder(int width, String color, String style) {
		theTable.setTableBorder(width, color, style);
	}
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorderBottom(int width, String color, String style) {
		theTable.setTableBorderBottom(width, color, style);
	}
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorderTop(int width, String color, String style) {
		theTable.setTableBorderTop(width, color, style);
	}
	/**
	 * @param value
	 */
	public void setTopLine(boolean value) {
		theTable.setTopLine(value);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param alignment
	 */
	public void setVerticalAlignment(int xpos, int ypos, String alignment) {
		theTable.setVerticalAlignment(xpos, ypos, alignment);
	}
	/**
	 * @param verticalAlignment
	 */
	public void setVerticalAlignment(String verticalAlignment) {
		theTable.setVerticalAlignment(verticalAlignment);
	}
	/**
	 * @param Color1
	 * @param Color2
	 */
	public void setVerticalZebraColored(String Color1, String Color2) {
		theTable.setVerticalZebraColored(Color1, Color2);
	}
	/**
	 * @param value
	 */
	public void setVerticatLinesBetween(boolean value) {
		theTable.setVerticatLinesBetween(value);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setWidth(int xpos, int width) {
		theTable.setWidth(xpos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	public void setWidth(int xpos, int ypos, int width) {
		theTable.setWidth(xpos, ypos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	public void setWidth(int xpos, int ypos, String width) {
		theTable.setWidth(xpos, ypos, width);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setWidth(int xpos, String width) {
		theTable.setWidth(xpos, width);
	}

	public void setAlignment(int xpos, int ypos, String alignment) {
		theTable.setAlignment(xpos, ypos, alignment);
	}
}

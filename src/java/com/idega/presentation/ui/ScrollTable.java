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
	
	public ScrollTable(){
		this.theTable = new Table();
	}

	/**
	 * @return Returns the scrollLayerFirstRowOffset.
	 */
	public int getScrollLayerHeaderRowThickness() {
		return this.scrollLayerHeaderRowThickness;
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
		return this.scrollLayerHeightPadding;
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
		return this.scrollLayerWidthPadding;
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
		return this.numberOfHeaderRows;
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
		return this.scrollLayerWidth;
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
		
			this.numberOfHeaderRows = this.numberOfHeaderRows<1?1:this.numberOfHeaderRows;
			
			//Steal the header rows from the theTable object later we need to copy the rest of the rows
			Table headerTable = (Table) this.theTable.clone(null,false);
			headerTable.resize(this.theTable.getColumns(),this.numberOfHeaderRows);
			headerTable.removeStyleAttribute(PresentationObject.HEIGHT);

			StringBuffer scriptSource1 = new StringBuffer("\t if(document.getElementById){");
			StringBuffer scriptSource2 = new StringBuffer("\t else if(document.all){");
			Image heightImage = Table.getTransparentCell(iwc);
			heightImage.setHeight("100%");
			heightImage.setWidth(1);
			
			//here we need to get just the added data OR HACKIT!
			//Hide the first row! Hahahahahahaa....fucking crap class...it worked before jsf but now we need to do this...
			theTable.setRowStyle(1, "display", "none");
			for (int i = 1 ; i <= this.theTable.getColumns(); i++) {
				theTable.emptyCell(i, 1);
			}
			theTable.setStyleAttribute("[if [IE] alert('gimi[endif]");
			
			int layerWidth = -1,layerHeight=-1;
			if(this.theTable.getWidth()!=null){
				try {
					layerWidth = Integer.parseInt(theTable.getWidth());
				} catch (NumberFormatException e) {
				}
			}
			if(this.theTable.getHeight()!=null){
				try {
					layerHeight = Integer.parseInt(theTable.getHeight());
				} catch (NumberFormatException e) {
				}
			}
			
			Image image = Table.getTransparentCell(iwc);
			image.setWidth("100%");
			image.setHeight(1);
			int testrow = theTable.getRows()+1;
			String generatedID = "_"+generateID();
			for (int col = 1; col <= theTable.getColumns(); col++) {
				String name = "tstimg"+col+generatedID;
				String cellName = "cll"+col+generatedID;
				Image img = (Image)image.clone();
				img.setName(name);
				img.setID(name);

				Image img2 = (Image)image.clone();
				img2.setName(name+"_1");
				img2.setID(name+"_1");

				headerTable.add(img2,col,1);
				
				theTable.add(img,col,testrow);
				headerTable.getCellAt(col,this.numberOfHeaderRows).setID(cellName);
				theTable.getCellAt(col,1).setID(cellName+"_1");
				scriptSource1.append("\n\t\t document.getElementById('").append(cellName).append("').width=document.images['").append(name).append("'].width;");
				scriptSource2.append("\n\t\t document.all.").append(cellName).append(".width=document.all.").append(name).append(".width;");
			}
			for (int col = 1; col <= theTable.getColumns(); col++) {
				String name = "tstimg"+col+generatedID;
				scriptSource1.append("\n\t\t document.images['").append(name).append("'].width=document.images['").append(name+"_1").append("'].width;");
			}
			testrow++;
			theTable.mergeCells(1,testrow,theTable.getColumns(),testrow);
			Image img = (Image)image.clone();
			img.setName("widthImage"+generatedID);
			img.setID("widthImage"+generatedID);
			theTable.add(img,1,testrow);
			
			
			layerWidth += this.scrollLayerWidthPadding;
			layerHeight += this.scrollLayerHeightPadding;
			headerTable.setWidth(theTable.getWidth());
			
			Layer headerLayer = new Layer(Layer.DIV);
			headerLayer.setWidth(theTable.getWidth());
			headerLayer.add(headerTable);
			
			Layer layer = new Layer(Layer.DIV);
			layer.setStyleAttribute("overflow", "auto");
			layer.setStyleAttribute("position", "relative");
			if(this.scrollLayerWidth>0) {
				layer.setStyleAttribute("width", this.scrollLayerWidth + "px");
			}
			else if(layerWidth>0) {
				layer.setStyleAttribute("width", layerWidth + "px");
			}
			if(layerHeight>0) {
				layer.setStyleAttribute("height", layerHeight + "px");
			}
			theTable.setStyleAttribute("position","relative");
			if(this.scrollLayerHeaderRowThickness>0) {
				this.scrollLayerHeaderRowThickness *= -1;
			}
			theTable.setStyleAttribute("top",String.valueOf(this.scrollLayerHeaderRowThickness*this.numberOfHeaderRows));
			layer.add(theTable);
			
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
			obj.theTable = (Table)this.theTable.clone();
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
		this.theTable.mergeCells(beginxpos, beginypos, endxpos, endypos);
	}
	/**
	 * @return
	 */
	public int getColumns() {
		return this.theTable.getColumns();
	}
	/**
	 * @return
	 */
	public int getRows() {
		return this.theTable.getRows();
	}
	/**
	 * @param modObject
	 * @param xpos
	 * @param ypos
	 */
	public void add(PresentationObject modObject, int xpos, int ypos) {
		this.theTable.add(modObject, xpos, ypos);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String)
	 */
	public void add(String theText) {
		this.theTable.add(theText);
	}
	/**
	 * @param text
	 * @param xpos
	 * @param ypos
	 */
	public void add(String text, int xpos, int ypos) {
		this.theTable.add(text, xpos, ypos);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(java.lang.String[])
	 */
	public void add(String[] theTextArray) {
		this.theTable.add(theTextArray);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectContainer#add(javax.faces.component.UIComponent)
	 */
	public void add(UIComponent component) {
		this.theTable.add(component);
	}
	/**
	 * @param comp
	 * @param xpos
	 * @param ypos
	 */
	public void add(UIComponent comp, int xpos, int ypos) {
		this.theTable.add(comp, xpos, ypos);
	}
	
	/**
	 * @param height
	 */
	public void setHeight(int height) {
		this.theTable.setHeight(height);
	}
	
	/**
	 * @param width
	 */
	public void setWidth(int width) {
		this.theTable.setWidth(width);
	}
	
	/**
	 * @param width
	 */
	public void setWidth(String width){
		this.theTable.setWidth(width);
	}
	
	
	
	
	/**
	 * @param backgroundImage
	 */
	public void setBackgroundImage(Image backgroundImage) {
		this.theTable.setBackgroundImage(backgroundImage);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImage
	 */
	public void setBackgroundImage(int xpos, int ypos, Image backgroundImage) {
		this.theTable.setBackgroundImage(xpos, ypos, backgroundImage);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param backgroundImageURL
	 */
	public void setBackgroundImageURL(int xpos, int ypos,
			String backgroundImageURL) {
		this.theTable.setBackgroundImageURL(xpos, ypos, backgroundImageURL);
	}
	/**
	 * @param backgroundImageURL
	 */
	public void setBackgroundImageURL(String backgroundImageURL) {
		this.theTable.setBackgroundImageURL(backgroundImageURL);
	}
	/**
	 * @param i
	 */
	public void setBorder(int i) {
		this.theTable.setBorder(i);
	}
	/**
	 * @param border
	 */
	public void setBorder(String border) {
		this.theTable.setBorder(border);
	}
	/**
	 * @param color
	 */
	public void setBorderColor(String color) {
		this.theTable.setBorderColor(color);
	}
	/**
	 * @param value
	 */
	public void setBottomLine(boolean value) {
		this.theTable.setBottomLine(value);
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
		this.theTable.setCellBorder(column, row, borderWidth, borderColor,
				borderStyle);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderColor
	 */
	public void setCellBorderColor(int column, int row, String borderColor) {
		this.theTable.setCellBorderColor(column, row, borderColor);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderStyle
	 */
	public void setCellBorderStyle(int column, int row, String borderStyle) {
		this.theTable.setCellBorderStyle(column, row, borderStyle);
	}
	/**
	 * @param column
	 * @param row
	 * @param borderWidth
	 */
	public void setCellBorderWidth(int column, int row, int borderWidth) {
		this.theTable.setCellBorderWidth(column, row, borderWidth);
	}
	/**
	 * @param i
	 */
	public void setCellpadding(int i) {
		this.theTable.setCellpadding(i);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpadding(int column, int row, int padding) {
		this.theTable.setCellpadding(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpadding(int column, int row, String padding) {
		this.theTable.setCellpadding(column, row, padding);
	}
	/**
	 * @param s
	 */
	public void setCellpadding(String s) {
		this.theTable.setCellpadding(s);
	}
	/**
	 * @param i
	 */
	public void setCellpaddingAndCellspacing(int i) {
		this.theTable.setCellpaddingAndCellspacing(i);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingBottom(int column, int row, int padding) {
		this.theTable.setCellpaddingBottom(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingBottom(int column, int row, String padding) {
		this.theTable.setCellpaddingBottom(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingLeft(int column, int row, int padding) {
		this.theTable.setCellpaddingLeft(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingLeft(int column, int row, String padding) {
		this.theTable.setCellpaddingLeft(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingRight(int column, int row, int padding) {
		this.theTable.setCellpaddingRight(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingRight(int column, int row, String padding) {
		this.theTable.setCellpaddingRight(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingTop(int column, int row, int padding) {
		this.theTable.setCellpaddingTop(column, row, padding);
	}
	/**
	 * @param column
	 * @param row
	 * @param padding
	 */
	public void setCellpaddingTop(int column, int row, String padding) {
		this.theTable.setCellpaddingTop(column, row, padding);
	}
	/**
	 * @param i
	 */
	public void setCellspacing(int i) {
		this.theTable.setCellspacing(i);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	public void setColor(int xpos, int ypos, IWColor color) {
		this.theTable.setColor(xpos, ypos, color);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param color
	 */
	public void setColor(int xpos, int ypos, String color) {
		this.theTable.setColor(xpos, ypos, color);
	}
	/**
	 * @param color
	 */
	public void setColor(IWColor color) {
		this.theTable.setColor(color);
	}
	/**
	 * @param color
	 */
	public void setColor(String color) {
		this.theTable.setColor(color);
	}
	/**
	 * @param xpos
	 * @param alignment
	 */
	public void setColumnAlignment(int xpos, String alignment) {
		this.theTable.setColumnAlignment(xpos, alignment);
	}
	/**
	 * @param xpos
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setColumnAttribute(int xpos, String attributeName,
			String attributeValue) {
		this.theTable.setColumnAttribute(xpos, attributeName, attributeValue);
	}
	/**
	 * @param xpos
	 * @param color
	 */
	public void setColumnColor(int xpos, String color) {
		this.theTable.setColumnColor(xpos, color);
	}
	/**
	 * @param xpos
	 * @param height
	 */
	public void setColumnHeight(int xpos, String height) {
		this.theTable.setColumnHeight(xpos, height);
	}
	/**
	 * @param column
	 * @param padding
	 */
	public void setColumnPadding(int column, int padding) {
		this.theTable.setColumnPadding(column, padding);
	}
	/**
	 * @param columns
	 */
	public void setColumns(int columns) {
		this.theTable.setColumns(columns);
	}
	/**
	 * @param xpos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setColumnStyle(int xpos, String styleAttribute,
			String styleValue) {
		this.theTable.setColumnStyle(xpos, styleAttribute, styleValue);
	}
	/**
	 * @param xpos
	 * @param styleClass
	 */
	public void setColumnStyleClass(int xpos, String styleClass) {
		this.theTable.setColumnStyleClass(xpos, styleClass);
	}
	/**
	 * @param xpos
	 * @param alignment
	 */
	public void setColumnVerticalAlignment(int xpos, String alignment) {
		this.theTable.setColumnVerticalAlignment(xpos, alignment);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setColumnWidth(int xpos, String width) {
		this.theTable.setColumnWidth(xpos, width);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int ypos, int height) {
		this.theTable.setHeight(ypos, height);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int xpos, int ypos, int height) {
		this.theTable.setHeight(xpos, ypos, height);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int xpos, int ypos, String height) {
		this.theTable.setHeight(xpos, ypos, height);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setHeight(int ypos, String height) {
		this.theTable.setHeight(ypos, height);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObject#setHeight(java.lang.String)
	 */
	public void setHeight(String height) {
		this.theTable.setHeight(height);
	}
	
	/**
	 * @param Color1
	 * @param Color2
	 */
	public void setHorizontalZebraColored(String Color1, String Color2) {
		this.theTable.setHorizontalZebraColored(Color1, Color2);
	}
	
	/**
	 * @param label
	 * @param xpos
	 * @param ypos
	 */
	public void setLabel(String label, int xpos, int ypos) {
		this.theTable.setLabel(label, xpos, ypos);
	}
	
	/**
	 * @param value
	 */
	public void setLeftLine(boolean value) {
		this.theTable.setLeftLine(value);
	}
	/**
	 * @param column
	 */
	public void setLineAfterColumn(int column) {
		this.theTable.setLineAfterColumn(column);
	}
	/**
	 * @param column
	 * @param value
	 */
	public void setLineAfterColumn(int column, boolean value) {
		this.theTable.setLineAfterColumn(column, value);
	}
	/**
	 * @param row
	 */
	public void setLineAfterRow(int row) {
		this.theTable.setLineAfterRow(row);
	}
	/**
	 * @param row
	 * @param value
	 */
	public void setLineAfterRow(int row, boolean value) {
		this.theTable.setLineAfterRow(row, value);
	}
	/**
	 * @param color
	 */
	public void setLineColor(String color) {
		this.theTable.setLineColor(color);
	}
	/**
	 * @param value
	 */
	public void setLineFrame(boolean value) {
		this.theTable.setLineFrame(value);
	}
	/**
	 * @param height
	 */
	public void setLineHeight(String height) {
		this.theTable.setLineHeight(height);
	}
	/**
	 * @param value
	 */
	public void setLinesBetween(boolean value) {
		this.theTable.setLinesBetween(value);
	}
	/**
	 * @param width
	 */
	public void setLineWidth(String width) {
		this.theTable.setLineWidth(width);
	}
	/**
	 * 
	 */
	public void setNoWrap() {
		this.theTable.setNoWrap();
	}
	/**
	 * @param xpos
	 * @param ypos
	 */
	public void setNoWrap(int xpos, int ypos) {
		this.theTable.setNoWrap(xpos, ypos);
	}
	/**
	 * @param resizable
	 */
	public void setResizable(boolean resizable) {
		this.theTable.setResizable(resizable);
	}
	/**
	 * @param value
	 */
	public void setRightLine(boolean value) {
		this.theTable.setRightLine(value);
	}
	/**
	 * @param ypos
	 * @param alignment
	 */
	public void setRowAlignment(int ypos, String alignment) {
		this.theTable.setRowAlignment(ypos, alignment);
	}
	/**
	 * @param ypos
	 * @param attributeName
	 * @param attributeValue
	 */
	public void setRowAttribute(int ypos, String attributeName,
			String attributeValue) {
		this.theTable.setRowAttribute(ypos, attributeName, attributeValue);
	}
	/**
	 * @param ypos
	 * @param color
	 */
	public void setRowColor(int ypos, String color) {
		this.theTable.setRowColor(ypos, color);
	}
	/**
	 * @param ypos
	 * @param height
	 */
	public void setRowHeight(int ypos, String height) {
		this.theTable.setRowHeight(ypos, height);
	}
	/**
	 * @param row
	 * @param padding
	 */
	public void setRowPadding(int row, int padding) {
		this.theTable.setRowPadding(row, padding);
	}
	/**
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setRowStyle(int ypos, String styleAttribute, String styleValue) {
		this.theTable.setRowStyle(ypos, styleAttribute, styleValue);
	}
	/**
	 * @param ypos
	 * @param styleClass
	 */
	public void setRowStyleClass(int ypos, String styleClass) {
		this.theTable.setRowStyleClass(ypos, styleClass);
	}
	/**
	 * @param ypos
	 * @param alignment
	 */
	public void setRowVerticalAlignment(int ypos, String alignment) {
		this.theTable.setRowVerticalAlignment(ypos, alignment);
	}
	/**
	 * @param ypos
	 * @param width
	 */
	public void setRowWidth(int ypos, String width) {
		this.theTable.setRowWidth(ypos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleAttribute
	 * @param styleValue
	 */
	public void setStyle(int xpos, int ypos, String styleAttribute,
			String styleValue) {
		this.theTable.setStyle(xpos, ypos, styleAttribute, styleValue);
	}
	
	/**
	 * @param xpos
	 * @param ypos
	 * @param styleName
	 */
	public void setStyleClass(int xpos, int ypos, String styleName) {
		this.theTable.setStyleClass(xpos, ypos, styleName);
	}
	
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorder(int width, String color, String style) {
		this.theTable.setTableBorder(width, color, style);
	}
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorderBottom(int width, String color, String style) {
		this.theTable.setTableBorderBottom(width, color, style);
	}
	/**
	 * @param width
	 * @param color
	 * @param style
	 */
	public void setTableBorderTop(int width, String color, String style) {
		this.theTable.setTableBorderTop(width, color, style);
	}
	/**
	 * @param value
	 */
	public void setTopLine(boolean value) {
		this.theTable.setTopLine(value);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param alignment
	 */
	public void setVerticalAlignment(int xpos, int ypos, String alignment) {
		this.theTable.setVerticalAlignment(xpos, ypos, alignment);
	}
	/**
	 * @param verticalAlignment
	 */
	public void setVerticalAlignment(String verticalAlignment) {
		this.theTable.setVerticalAlignment(verticalAlignment);
	}
	/**
	 * @param Color1
	 * @param Color2
	 */
	public void setVerticalZebraColored(String Color1, String Color2) {
		this.theTable.setVerticalZebraColored(Color1, Color2);
	}
	/**
	 * @param value
	 */
	public void setVerticatLinesBetween(boolean value) {
		this.theTable.setVerticatLinesBetween(value);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setWidth(int xpos, int width) {
		this.theTable.setWidth(xpos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	public void setWidth(int xpos, int ypos, int width) {
		this.theTable.setWidth(xpos, ypos, width);
	}
	/**
	 * @param xpos
	 * @param ypos
	 * @param width
	 */
	public void setWidth(int xpos, int ypos, String width) {
		this.theTable.setWidth(xpos, ypos, width);
	}
	/**
	 * @param xpos
	 * @param width
	 */
	public void setWidth(int xpos, String width) {
		this.theTable.setWidth(xpos, width);
	}

	public void setAlignment(int xpos, int ypos, String alignment) {
		this.theTable.setAlignment(xpos, ypos, alignment);
	}
}

/*
 * $Id: Table.java,v 1.93 2006/05/08 13:51:09 laddi Exp $
 *
 * Copyright (C) 2001-2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.text.Text;
import com.idega.util.IWColor;
import com.idega.util.text.TextStyler;

/**
 * A class to use for presentation of 2 dimensional (grid) layout.
 * <br>
 * <br>
 *   <b>NOTE:</b> xpos is in [1:cols]
 *    ,ypos is in [1:rows]
 * <br>
 * <br>
 * This object maps to and renders the 
 * <code><pre>
 * <TABLE><TR><TD>...</TD></TR></TABLEL>
 * </pre></code>
 * tabs in HTML> and renders the children inside the <code><pre><TD>...</TD></pre></code>
 * tags for each cell.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class Table extends PresentationObjectContainer implements TableType{

	//Static variables:
	public static final String FRAME_VOID = "void";
	public static final String FRAME_ABOVE = "above";
	public static final String FRAME_BELOW = "below";
	public static final String FRAME_TOP_BOTTOM = "hsides";
	public static final String FRAME_LEFT_RIGHT = "vsides";
	public static final String FRAME_LEFT = "lhs";
	public static final String FRAME_RIGHT = "rhs";
	public static final String FRAME_BOX = "box";
	public static final String FRAME_BORDER = "border";
	
	public static final String RULES_NONE = "none";
	public static final String RULES_GROUPS = "groups";
	public static final String RULES_ROWS = "rows";
	public static final String RULES_COLUMNS = "cols";
	public static final String RULES_ALL = "all";
	
	protected static final String HTML_TABLE_TAG_START = "<table ";
	protected static final String HTML_TABLE_TAG_END = "</table>";
	protected static final String HTML_CELL_TAG_START = "<td ";
	protected static final String HTML_CELL_TAG_END = "</td>";
	protected static final String HTML_TR_START = "<tr>";
	protected static final String HTML_TR_END = "</tr>";
	
	protected static final String WML_TABLE_TAG_START = "<table ";
	protected static final String WML_TABLE_TAG_END = "</table>";
	protected static final String WML_CELL_TAG_START = "<td ";
	protected static final String WML_CELL_TAG_END = "</td>";
	protected static final String WML_TR_START = "<tr>";
	protected static final String WML_TR_END = "</tr>";
	
	protected static final String PDF_XML_TABLE_TAG_START = "<table ";
	protected static final String PDF_XML_TABLE_TAG_END = "</table>";
	protected static final String PDF_XML_CELL_TAG_START = "<cell ";
	protected static final String PDF_XML_CELL_TAG_END = "</cell>";
	protected static final String PDF_XML_TR_START = "<row>";
	protected static final String PDF_XML_TR_END = "</row>";

	protected static final String LINE_BREAK = "\n";
	protected static final String TAG_END = ">";
	
	protected static Image transparentcell;

	protected static final String COLOR_ATTRIBUTE = "bgcolor";
	public static final String HUNDRED_PERCENT = "100%";
	public static final String VERTICAL_ALIGN_TOP = "top";
	public static final String VERTICAL_ALIGN_MIDDLE = "middle";
	public static final String VERTICAL_ALIGN_BOTTOM = "bottom";
	public static final String HORIZONTAL_ALIGN_LEFT = "left";
	public static final String HORIZONTAL_ALIGN_RIGHT = "right";
	public static final String HORIZONTAL_ALIGN_CENTER = "center";
	
	//Member variables:
	/**
	 * @deprecated replaced with calling getCellAt(x,y)
	 */
	protected TableCell theCells[][];
	protected int cols = 0;
	protected int rows = 0;
	protected String _width = null;
	protected String _height = null;

	protected boolean forceToRenderAsTableInWML = false;
	
	//Variables to hold coordinates of merge point of cells
	//Initialized only if needed
	protected Vector beginMergedxpos;
	protected Vector beginMergedypos;
	protected Vector endMergedxpos;
	protected Vector endMergedypos;

	protected boolean isResizable;
	protected boolean cellsAreMerged;
	protected boolean addLineTop = false;
	protected boolean addLinesBetween = false;
	protected boolean addLinesBottom = false;
	protected boolean addLineLeft = false;
	protected boolean addVerticalLinesBetween = false;
	protected boolean addLineRight = false;
	protected String lineColor = "#000000";
	protected String lineHeight = "1";
	protected String lineWidth = "1";
	protected int lineColspan = 0;
	protected int[] lineRows = new int[0];
	protected int[] lineCols = new int[0];
	
	//Temporary legacy variables:
	protected transient String markupLanguage = null;

	/**
	 * Constructor that defaults with 1 column and 1 row
	 */
	public Table() {
		this(1, 1);
		//setWidth("100%");
		//setHeight("100%");
		this.isResizable = true;
	}

	/**
	 * Constructor that takes in the initial rows and columns of the table
	 */
	public Table(int cols, int rows) {
		super();
		this.isResizable = false;
		if(!useFacetBasedCells()){
			this.theCells = new TableCell[cols][rows];
		}
		this.cols = cols;
		this.rows = rows;
		setBorder("0");
		this.cellsAreMerged = false;
		setTransient(false);
	}

	/**
	 * Returns weather use the new JSF based Facets to reference the table cells opposed to the old TableCells[] array.
	 * This method will be used in the JSF transition and will be removed when the theCells[] array is finally removed.
	 * @return true if Facets are used
	 */
	protected boolean useFacetBasedCells(){
		return IWMainApplication.useJSF;
	}
	
	/**
	*Add an object inside this Table in cell with coordinates 1,1 - same as the add() function
	*@deprecated replaced by the add function
	*/
	/*public void addObject(PresentationObject modObject) {
		addObject(modObject, 1, 1);
	}*/

	/**
	*Add an object inside this Table in cell with coordinates 1,1
	*- overrided from PresentationObject to maintain compatability
	*/
	public void add(PresentationObject modObject) {
		add(modObject, 1, 1);
	}
	
	/**
	*Add an object inside this Table in cell with coordinates 1,1 
	*- overrided from PresentationObject to maintain compatability
	*/
	public void add(UIComponent component) {
		add(component, 1, 1);
	}
	
	/*public void addText(String theText, String format, int xpos, int ypos) {
		if (isResizable) {
			if (xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if (ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		if (theObjects[xpos - 1][ypos - 1] == null) {
			theObjects[xpos - 1][ypos - 1] = new PresentationObjectContainer();
			//super.add(theObjects);
		}
		theObjects[xpos - 1][ypos - 1].addText(theText, format);
	}*/
	
	/**
	*Add an object inside this Table in cell with coordinates x,y from top right
	*/
	public void add(PresentationObject modObject, int xpos, int ypos) {
		/*if (modObject != null) {
			try {
				if (isResizable) {
					if (xpos > this.getColumns()) {
						setColumns(xpos);
					}
					if (ypos > this.getRows()) {
						setRows(ypos);
					}
				}
				if (theCells[xpos - 1][ypos - 1] == null) {
					theCells[xpos - 1][ypos - 1] = new TableCell();
					//super.add(theObjects);
				}
				theCells[xpos - 1][ypos - 1].add(modObject);
				modObject.setParentObject(this);
				modObject.setLocation(this.getLocation());
			}
			catch (Exception ex) {
				add(new ExceptionWrapper(ex, this));
			}
		}*/
		add((UIComponent)modObject,xpos,ypos);
	}

	/**
	*Add an object inside this Table in cell with coordinates x,y from top right
	*/
	public void add(UIComponent comp, int xpos, int ypos) {
		if (comp != null) {
			try {
				if (this.isResizable) {
					if (xpos > this.getColumns()) {
						setColumns(xpos);
					}
					if (ypos > this.getRows()) {
						setRows(ypos);
					}
				}
				getCellAt(xpos,ypos).add(comp);
				comp.setParent(this);
				if(comp instanceof PresentationObject){
					PresentationObject pObject = (PresentationObject)comp;
					pObject.setLocation(this.getLocation());
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//add(new ExceptionWrapper(ex, this));
			}
		}
	}
	
	
	/*public void addObject(PresentationObject modObject, int xpos, int ypos) {
		add(modObject, xpos, ypos);
	}*/

	public void add(String text, int xpos, int ypos) {
		addText(text, xpos, ypos);
	}
	
	public void addText(String theText, int xpos, int ypos) {
		add(new Text(theText), xpos, ypos);
	}
	
	public void addText(int integerToInsert, int xpos, int ypos) {
		addText(Integer.toString(integerToInsert), xpos, ypos);
	}
	
	/**@todo : this must implemented in the print method...like in the Link class
	 * IMPORTANT! for this to work you must have an application property called
	 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
	 *
	 */
	public void setBackgroundImage(int xpos, int ypos, Image backgroundImage) {
		if (backgroundImage != null) {
			setBackgroundImageURL(xpos, ypos, getImageUrl(backgroundImage));
		}
	}

	/**@todo : this must implemented in the print method...like in the Link class
	 * IMPORTANT! for this to work you must have an application property called
	 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
	 *
	 */
	public void setBackgroundImage(Image backgroundImage) {
		if (backgroundImage != null) {
			setBackgroundImageURL(getImageUrl(backgroundImage));
		}
	}

	/**@todo : replace this with a implementation in print
	 * IMPORTANT! for this to work you must have an application property called
	 * IW_USES_OLD_MEDIA_TABLES   (set to anything)
	 * @todo implement in main or print with getMediaURL(IWContext)
	 */
	protected String getImageUrl(Image image) {
		return image.getMediaURL();
	}

	public void setBackgroundImageURL(String backgroundImageURL) {
		//this.setAttribute("background", backgroundImageURL);
		if(!"".equals(backgroundImageURL)) {
			setStyleAttribute("background: url('"+backgroundImageURL+"');");
		}
	}
	
	public void setBackgroundRepeat(String repeat) {
		setStyleAttribute("background-repeat", repeat);
	}
	
	public void setBackgroundImageURL(int xpos, int ypos, String backgroundImageURL) {
		//this.setAttribute(xpos, ypos, "background", backgroundImageURL);
		if(!"".equals(backgroundImageURL)) {
			setStyle(xpos, ypos, "background","url('"+backgroundImageURL+"')");
		}
	}
	
	public void setVerticalAlignment(int xpos, int ypos, String alignment) {
		setAttribute(xpos, ypos, "valign", alignment);
	}
	
	public void setAlignment(int xpos, int ypos, String alignment) {
		setAttribute(xpos, ypos, "align", alignment);
	}
	
	public void setRowAlignment(int ypos, String alignment) {
		setRowAttribute(ypos, "align", alignment);
	}
	
	public void setColumnAlignment(int xpos, String alignment) {
		setColumnAttribute(xpos, "align", alignment);
	}
	
	public void setRowVerticalAlignment(int ypos, String alignment) {
		setRowAttribute(ypos, "valign", alignment);
	}
	
	public void setColumnVerticalAlignment(int xpos, String alignment) {
		setColumnAttribute(xpos, "valign", alignment);
	}
	
	public void resize(int columns, int rows) {
		if (columns != this.cols || rows != this.rows) {
			int minCols = Math.min(columns, this.cols);
			int minRows = Math.min(rows, this.rows);
			if(!useFacetBasedCells()){
				TableCell theNewObjects[][];
				theNewObjects = new TableCell[columns][rows];
				for (int x = 0; x < minCols; x++) {
					System.arraycopy(this.theCells[x], 0, theNewObjects[x], 0, minRows);
				}
				this.theCells = theNewObjects;
			}
			this.cols = columns;
			this.rows = rows;
		}
	}
	
	/**
	*Empties the Table of all objects stored inside
	*/
	public void empty() {
		for (int x = 0; x < this.cols; x++) {
			for (int y = 0; y < this.rows; y++) {
				/*if (theCells[x][y] != null) {
					theCells[x][y].empty();
				}*/
				int xpos=x+1;
				int ypos=y+1;
				getCellAt(xpos,ypos).empty();
			}
		}
	}
	
	public void emptyCell(int xpos, int ypos) {
		/*if (theCells[xpos - 1][ypos - 1] != null) {
			theCells[xpos - 1][ypos - 1].empty();
		}*/
		getCellAt(xpos,ypos).empty();
	}
	
	public int getColumns() {
		return this.cols;
	}
	
	public void setColumns(int columns) {
		resize(columns, this.rows);
	}
	
	public void setRows(int rows) {
		resize(this.cols, rows);
	}

	/**
	 * 
	 * @uml.property name="rows"
	 */
	public int getRows() {
		return this.rows;
	}

	
	public void mergeCells(int beginxpos, int beginypos, int endxpos, int endypos) {
		if (this.beginMergedxpos == null && this.beginMergedypos == null && this.endMergedxpos == null && this.endMergedypos == null) {
			this.beginMergedxpos = new Vector(1);
			this.beginMergedypos = new Vector(1);
			this.endMergedxpos = new Vector(1);
			this.endMergedypos = new Vector(1);
		}
		this.cellsAreMerged = true;
		//Do nothing if the either of the cells are already merged with something else
		if (!(isInMergedCell(beginxpos, beginypos) && isInMergedCell(endxpos, endypos))) {
			this.beginMergedxpos.addElement(new Integer(beginxpos));
			this.beginMergedypos.addElement(new Integer(beginypos));
			this.endMergedxpos.addElement(new Integer(endxpos));
			this.endMergedypos.addElement(new Integer(endypos));
		}
	}
	
	public void setWidth(String width) {
		//setAttribute("width", s);
		this._width = width;
		setWidthStyle(width);
	}
	
	public void setWidth(int width) {
		setWidth(Integer.toString(width));
	}
	
	public void setHeight(int height) {
		setHeight(Integer.toString(height));
	}
	
	public void setWidthAndHeight(String widthAndHeight) {
		setWidth(widthAndHeight);
		setHeight(widthAndHeight);
	}
	
	public void setWidthAndHeightToHundredPercent() {
		setWidth(HUNDRED_PERCENT);
		setHeight(HUNDRED_PERCENT);
	}
	
	public String getWidth() {
		return this._width;
	}
	
	public String getHeight() {
		return this._height;
	}
	
	public void setHeight(String height) {
		//setAttribute("height", s);
		this._height = height;
		setHeightStyle(height);
	}
	
	public void setHeight(int xpos, int ypos, String height) {
		//setAttribute(xpos, ypos, "height", height);
		/*
		Image spacer = (Image) transparentcell.clone();
		spacer.setHeight(height);
		add(spacer,xpos,ypos);*/
		getCellAt(xpos, ypos).setHeightStyle(height);
	}

	public void setHeight(int xpos, int ypos, int height) {
		setHeight(xpos, ypos, String.valueOf(height));
	}

	private static String DOT = ".";
	
	public TableCell getCellAt(int xpos, int ypos) {
		if (this.isResizable) {
			if (xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if (ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		if(useFacetBasedCells()){
			String facetKey = xpos+DOT+ypos;
			UIComponent facet = getFacet(facetKey);
			TableCell cell = (TableCell)facet;
			if(cell==null){
				cell = new TableCell();
				getFacets().put(facetKey,cell);
			}
			return cell;
		}
		else{
			//The old array way:
			if (this.theCells[xpos - 1][ypos - 1] == null) {
				TableCell cell = new TableCell();
				cell.setParentObject(this);
				this.theCells[xpos - 1][ypos - 1]=cell;
				// super.add(theObjects);
			}
			return this.theCells[xpos - 1][ypos - 1];
		}
	}
	
	public void setWidth(int xpos, int ypos, int width) {
		//setAttribute(xpos, ypos, "width", width);
		setWidth(xpos, ypos, String.valueOf(width));
	}
	
	public void setWidth(int xpos, int ypos, String width) {
		//setAttribute(xpos, ypos, "width", width);
		getCellAt(xpos, ypos).setWidthStyle(width);
	}
	
	public void setColor(String color) {
		//setAttribute("bgcolor", s);
		setStyleAttribute("background-color: "+color+";");
	}
	
	public void setColor(IWColor color) {
		setColor(color.getHexColorString());
	}
	
	public void setBorder(String border) {
		setMarkupAttribute("border", border);
	}
	
	public void setBorderColor(String color) {
		setMarkupAttribute("bordercolor", color);
	}
	
	public void setBorder(int i) {
		setBorder(Integer.toString(i));
	}
	
	public void setCellID(int column, int row, String ID) {
		getCellAt(column, row).setID(ID);
	}
	
	public void setCellBorderWidth(int column, int row, int borderWidth) {
		setStyle(column, row, "border-width", borderWidth+"px");
	}
	
	public void setCellBorderColor(int column, int row, String borderColor) {
		setStyle(column, row, "border-color", borderColor);
	}
	
	public void setCellBorderStyle(int column, int row, String borderStyle) {
		setStyle(column, row, "border-style", borderStyle);
	}
	
	public void setCellBorder(int column, int row, int borderWidth, String borderColor, String borderStyle) {
		setCellBorderWidth(column, row, borderWidth);
		setCellBorderColor(column, row, borderColor);
		setCellBorderStyle(column, row, borderStyle);
	}
	
	public void setTopCellBorderWidth(int column, int row, int borderWidth) {
		setStyle(column, row, "border-top-width", borderWidth+"px");
	}
	
	public void setTopCellBorderColor(int column, int row, String borderColor) {
		setStyle(column, row, "border-top-color", borderColor);
	}
	
	public void setTopCellBorderStyle(int column, int row, String borderStyle) {
		setStyle(column, row, "border-top-style", borderStyle);
	}
	
	public void setTopCellBorder(int column, int row, int borderWidth, String borderColor, String borderStyle) {
		setTopCellBorderWidth(column, row, borderWidth);
		setTopCellBorderColor(column, row, borderColor);
		setTopCellBorderStyle(column, row, borderStyle);
	}
	
	public void setBottomCellBorderWidth(int column, int row, int borderWidth) {
		setStyle(column, row, "border-bottom-width", borderWidth+"px");
	}
	
	public void setBottomCellBorderColor(int column, int row, String borderColor) {
		setStyle(column, row, "border-bottom-color", borderColor);
	}
	
	public void setBottomCellBorderStyle(int column, int row, String borderStyle) {
		setStyle(column, row, "border-bottom-style", borderStyle);
	}
	
	public void setBottomCellBorder(int column, int row, int borderWidth, String borderColor, String borderStyle) {
		setBottomCellBorderWidth(column, row, borderWidth);
		setBottomCellBorderColor(column, row, borderColor);
		setBottomCellBorderStyle(column, row, borderStyle);
	}
	
	public void setLeftCellBorderWidth(int column, int row, int borderWidth) {
		setStyle(column, row, "border-left-width", borderWidth+"px");
	}
	
	public void setLeftCellBorderColor(int column, int row, String borderColor) {
		setStyle(column, row, "border-left-color", borderColor);
	}
	
	public void setLeftCellBorderStyle(int column, int row, String borderStyle) {
		setStyle(column, row, "border-left-style", borderStyle);
	}
	
	public void setLeftCellBorder(int column, int row, int borderWidth, String borderColor, String borderStyle) {
		setLeftCellBorderWidth(column, row, borderWidth);
		setLeftCellBorderColor(column, row, borderColor);
		setLeftCellBorderStyle(column, row, borderStyle);
	}
	
	public void setRightCellBorderWidth(int column, int row, int borderWidth) {
		setStyle(column, row, "border-right-width", borderWidth+"px");
	}
	
	public void setRightCellBorderColor(int column, int row, String borderColor) {
		setStyle(column, row, "border-right-color", borderColor);
	}
	
	public void setRightCellBorderStyle(int column, int row, String borderStyle) {
		setStyle(column, row, "border-right-style", borderStyle);
	}
	
	public void setRightCellBorder(int column, int row, int borderWidth, String borderColor, String borderStyle) {
		setRightCellBorderWidth(column, row, borderWidth);
		setRightCellBorderColor(column, row, borderColor);
		setRightCellBorderStyle(column, row, borderStyle);
	}
	
	/**
	 * displays top and bottom edges
	 */
	public void setFrameHsides() {
		setFrame("hsides");
	}
	
	/**
	 * displays left and right edges
	 */
	public void setFrameVsides() {
		setFrame("vsides");
	}
	
	/**
	 * displays no border
	 */
	public void setFrameVoid() {
		setFrame("void");
	}
	
	/**
	 * displays top edge only
	 */
	public void setFrameAbove() {
		setFrame("above");
	}

	/**
	 * below: displays bottom edge only
	 * border: displays all four sides (default)
	 * box: displays all four sides (like border)
	 * hsides: displays top and bottom edges
	 * lhs: displays left edge only
	 * rhs: displays right edge only
	 * void: displays no border
	 * vsides: displays left and right edges
	 */
	public void setFrame(String frame) {
		setMarkupAttribute("frame", frame);
	}
	
	public void setRules(String rules) {
		setMarkupAttribute("rules", rules);
	}
	
	public void setCellspacing(int i) {
		setCellspacing(Integer.toString(i));
	}
	
	public void setCellpadding(int i) {
		setCellpadding(Integer.toString(i));
	}
	
	public void setCellpadding(int column, int row, int padding) {
		setCellpadding(column, row, String.valueOf(padding));
	}
	
	public void setRowPadding(int row, int padding) {
		setRowStyle(row, "padding", padding+"px");
	}
	
	public void setColumnPadding(int column, int padding) {
		setColumnStyle(column, "padding", padding+"px");
	}
	
	public void setCellpadding(int column, int row, String padding) {
		setStyle(column, row, "padding", padding+"px");
	}
	
	public void setCellpaddingLeft(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingLeft(column, temp, padding);
			temp++;
		}
	}
	
	public void setCellpaddingLeft(int column, int row, int padding) {
		setCellpaddingLeft(column, row, String.valueOf(padding));
	}
	
	public void setCellpaddingLeft(int column, int row, String padding) {
		setStyle(column, row, "padding-left", padding+"px");
	}
	
	public void setCellpaddingRight(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingRight(column, temp, padding);
			temp++;
		}
	}
	
	public void setCellpaddingRight(int column, int row, int padding) {
		setCellpaddingRight(column, row, String.valueOf(padding));
	}
	
	public void setCellpaddingRight(int column, int row, String padding) {
		setStyle(column, row, "padding-right", padding+"px");
	}
	
	public void setCellpaddingTop(int row, int padding) {
		for (int temp = 1; temp <= this.cols;) {
			setCellpaddingTop(temp, row, padding);
			temp++;
		}
	}
	
	public void setCellpaddingTop(int column, int row, int padding) {
		setCellpaddingTop(column, row, String.valueOf(padding));
	}
	
	public void setCellpaddingTop(int column, int row, String padding) {
		setStyle(column, row, "padding-top", padding+"px");
	}
	
	public void setCellpaddingBottom(int row, int padding) {
		for (int temp = 1; temp <= this.cols;) {
			setCellpaddingBottom(temp, row, padding);
			temp++;
		}
	}
	
	public void setCellpaddingBottom(int column, int row, int padding) {
		setCellpaddingBottom(column, row, String.valueOf(padding));
	}
	
	public void setCellpaddingBottom(int column, int row, String padding) {
		setStyle(column, row, "padding-bottom", padding+"px");
	}
	
	public void setColumnPaddingLeft(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingLeft(column, temp, padding);
			temp++;
		}
	}
	
	public void setColumnPaddingRight(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingRight(column, temp, padding);
			temp++;
		}
	}
	
	public void setColumnPaddingTop(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingTop(column, temp, padding);
			temp++;
		}
	}
	
	public void setColumnPaddingBottom(int column, int padding) {
		for (int temp = 1; temp <= this.rows;) {
			setCellpaddingBottom(column, temp, padding);
			temp++;
		}
	}
	
	public void setRowPaddingLeft(int row, int padding) {
		for (int column = 1; column <= this.cols;) {
			setCellpaddingLeft(column, row, padding);
			column++;
		}
	}
	
	public void setRowPaddingRight(int row, int padding) {
		for (int column = 1; column <= this.cols;) {
			setCellpaddingRight(column, row, padding);
			column++;
		}
	}
	
	public void setRowPaddingTop(int row, int padding) {
		for (int column = 1; column <= this.cols;) {
			setCellpaddingTop(column, row, padding);
			column++;
		}
	}
	
	public void setRowPaddingBottom(int row, int padding) {
		for (int column = 1; column <= this.cols;) {
			setCellpaddingBottom(column, row, padding);
			column++;
		}
	}
	
	public void setCellpaddingAndCellspacing(int i) {
		setCellpadding(i);
		setCellspacing(i);
	}
	
	public void setCellspacing(String s) {
		setMarkupAttribute("cellspacing", s);
	}
	
	public void setCellpadding(String s) {
		setMarkupAttribute("cellpadding", s);
	}
	
	public void setColor(int xpos, int ypos, String color) {
		//setAttribute(xpos, ypos, COLOR_ATTRIBUTE, s);
		setStyle(xpos, ypos, "background-color", color);
	}
	
	public void setColor(int xpos, int ypos, IWColor color) {
		setColor(xpos, ypos, color.getHexColorString());
	}
	
	public void setRowColor(int ypos, String color) {
		//setRowAttribute(ypos, COLOR_ATTRIBUTE, s);
		setRowStyle(ypos, "background-color", color);
	}
	
	public void setColumnColor(int xpos, String color) {
		//setColumnAttribute(xpos, COLOR_ATTRIBUTE, s);
		setColumnStyle(xpos, "background-color", color);
	}
	
	public void setWidth(int xpos, String width) {
		//setColumnAttribute(xpos, "width", s);
		setColumnWidth(xpos, width);
	}
	
	public void setWidth(int xpos, int width) {
		//setColumnAttribute(xpos, "width", s);
		setWidth(xpos, String.valueOf(width));
	}
	
	public void setHeight(int ypos, String height) {
		//setRowAttribute(ypos, "height", s);
		setRowHeight(ypos, height);
	}
	
	public void setHeight(int ypos, int height) {
		//setRowAttribute(ypos, "height", String.valueOf(height));
		setHeight(ypos, String.valueOf(height));
	}
	
	/**
	 * @deprecated  Should be set parent object.
	 */
	public void setAlignment(String align) {
		setMarkupAttribute("align", align);
	}
	
	/**
	 * @deprecated  Should be set parent object.
	 */
	public void setVerticalAlignment(String verticalAlignment) {
		setMarkupAttribute("valign", verticalAlignment);
	}
	
	public void setColumnAttribute(int xpos, String attributeName, String attributeValue) {
		for (int temp = 1; temp <= this.rows;) {
			setAttribute(xpos, temp, attributeName, attributeValue);
			temp++;
		}
	}
	
	public void setColumnStyleClass(int xpos, String styleClass) {
		setColumnAttribute(xpos, "class", styleClass);
	}
	
	public void setColumnStyle(int xpos, String styleAttribute, String styleValue) {
		for (int temp = 1; temp <= this.rows;) {
			setStyle(xpos, temp, styleAttribute, styleValue);
			temp++;
		}
	}
	
	public void setColumnWidth(int xpos, String width) {
		for (int temp = 1; temp <= this.rows;) {
			setWidth(xpos, temp, width);
			temp++;
		}
	}
	
	public void setColumnHeight(int xpos, String height) {
		for (int temp = 1; temp <= this.rows;) {
			setHeight(xpos, temp, height);
			temp++;
		}
	}
	
	public void setRowAttribute(int ypos, String attributeName, String attributeValue) {
		for (int temp = 1; temp <= this.cols;) {
			setAttribute(temp, ypos, attributeName, attributeValue);
			temp++;
		}
	}
	
	public void setRowStyle(int ypos, String styleAttribute, String styleValue) {
		for (int temp = 1; temp <= this.cols;) {
			setStyle(temp, ypos, styleAttribute, styleValue);
			temp++;
		}
	}
	
	public void setRowStyleClass(int ypos, String styleClass) {
		setRowAttribute(ypos, "class", styleClass);
	}
	
	public void setRowWidth(int ypos, String width) {
		for (int temp = 1; temp <= this.cols;) {
			setWidth(temp, ypos, width);
			temp++;
		}
	}
	
	public void setRowHeight(int ypos, String height) {
		for (int temp = 1; temp <= this.cols;) {
			setHeight(temp, ypos, height);
			temp++;
		}
	}
	
	public void setAttribute(int xpos, int ypos, String attributeName, String attributeValue) {
		if (this.isResizable) {
			if (xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if (ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		/*if (this.theCells[xpos - 1][ypos - 1] == null) {
			this.theCells[xpos - 1][ypos - 1] = new TableCell();
			// super.add(theObjects);
		}
		this.theCells[xpos - 1][ypos - 1].setMarkupAttribute(attributeName, attributeValue);
		*/
		getCellAt(xpos,ypos).setMarkupAttribute(attributeName,attributeValue);
	}
	
	public void setStyle(int xpos, int ypos, String styleAttribute, String styleValue) {
		if (this.isResizable) {
			if (xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if (ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		/*if (this.theCells[xpos - 1][ypos - 1] == null) {
			theCells[xpos - 1][ypos - 1] = new TableCell();
			// super.add(theObjects);
		}*/
		TableCell cell = getCellAt(xpos,ypos);
		TextStyler styler = new TextStyler(cell.getStyleAttribute());
		styler.setStyleValue(styleAttribute, styleValue);
		cell.setStyleAttribute(styler.getStyleString());
		
		
	}
	//added for setting a styleClass for a specific cell in a table
	public void setStyleClass(int xpos, int ypos, String styleName) {
		if(this.isResizable) {
			if(xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if(ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		/*if (this.theCells[xpos - 1][ypos - 1] == null) {
			theCells[xpos - 1][ypos - 1] = new TableCell();
		}
		this.theCells[xpos - 1][ypos - 1].setMarkupAttribute("class",styleName);
		*/
		getCellAt(xpos,ypos).setMarkupAttribute("class",styleName);
	}
	
	public void setAttribute(int xpos, int ypos, String attribute) {
		if (this.isResizable) {
			if (xpos > this.getColumns()) {
				setColumns(xpos);
			}
			if (ypos > this.getRows()) {
				setRows(ypos);
			}
		}
		/*if (this.theCells[xpos - 1][ypos - 1] == null) {
			this.theCells[xpos - 1][ypos - 1] = new TableCell();
			// super.add(theObjects);
		}
		this.theCells[xpos - 1][ypos - 1].setMarkupAttributeWithoutValue(attribute);
		*/
		getCellAt(xpos,ypos).setMarkupAttributeWithoutValue(attribute);
	}
	
	public void setNoWrap(int xpos, int ypos) {
		setStyle(xpos, ypos, "white-space","nowrap");
	}
	
	public void setNoWrap() {
		setStyleAttribute("white-space:nowrap;");
	}
	
	/*Tells if a cell in a table is merged with another*/
	protected boolean isInMergedCell(int xpos, int ypos) {
		boolean theReturn = false;
		boolean xcheck = false;
		boolean ycheck = false;
		if (!this.cellsAreMerged) {
			theReturn = false;
		}
		else {
			int i = 0;
			for (Enumeration e = this.beginMergedxpos.elements(); e.hasMoreElements();) {
				ycheck = false;
				xcheck = false;
				Integer temp1 = (Integer) e.nextElement();
				Integer temp2 = (Integer) this.endMergedxpos.elementAt(i);
				int xlength;
				Integer temp3 = (Integer) this.beginMergedypos.elementAt(i);
				Integer temp4 = (Integer) this.endMergedypos.elementAt(i);
				int ylength;
				int lowerx = 0;
				int lowery = 0;
				if (temp1.intValue() <= temp2.intValue()) {
					lowerx = temp1.intValue();
					xlength = temp2.intValue() - temp1.intValue();
				}
				else {
					lowerx = temp2.intValue();
					xlength = temp1.intValue() - temp2.intValue();
				}
				if (temp3.intValue() <= temp4.intValue()) {
					lowery = temp3.intValue();
					ylength = temp4.intValue() - temp3.intValue();
				}
				else {
					lowery = temp4.intValue();
					ylength = temp3.intValue() - temp4.intValue();
				}
				//Check the x coordinate
				if (xpos >= (lowerx)) {
					if (xpos <= (lowerx + xlength)) {
						xcheck = true;
					}
					else {
						xcheck = false;
					}
				}
				//Check the y coordinate
				if (ypos >= (lowery)) {
					if (ypos <= (lowery + ylength)) {
						ycheck = true;
					}
					else {
						ycheck = false;
					}
				}
				if (xcheck && ycheck) {
					theReturn = true;
				}
				i++;
			}
		}
		return theReturn;
	}
	
	public void addBreak(int xpos, int ypos) {
		Text myText = Text.getBreak();
		this.add(myText, xpos, ypos);
	}
	
	protected int getWidthOfMergedCell(int startxpos, int startypos) {
		int returnint = 1;
		int i = 0;
		for (Enumeration e = this.beginMergedxpos.elements(); e.hasMoreElements();) {
			Integer temp1 = (Integer) e.nextElement();
			Integer temp2 = (Integer) this.endMergedxpos.elementAt(i);
			int xlength;
			Integer temp3 = (Integer) this.beginMergedypos.elementAt(i);
			Integer temp4 = (Integer) this.endMergedypos.elementAt(i);
			int lowerx = 0;
			int lowery = 0;
			if (temp1.intValue() <= temp2.intValue()) {
				lowerx = temp1.intValue();
				xlength = temp2.intValue() - temp1.intValue();
			}
			else {
				lowerx = temp2.intValue();
				xlength = temp1.intValue() - temp2.intValue();
			}
			if (temp3.intValue() <= temp4.intValue()) {
				lowery = temp3.intValue();
			}
			else {
				lowery = temp4.intValue();
			}
			if (lowerx == startxpos && lowery == startypos) {
				returnint = xlength + 1;
			}
			i++;
		}
		return returnint;
	}
	
	protected int getHeightOfMergedCell(int startxpos, int startypos) {
		int returnint = 1;
		int i = 0;
		for (Enumeration e = this.beginMergedxpos.elements(); e.hasMoreElements();) {
			Integer temp1 = (Integer) e.nextElement();
			Integer temp2 = (Integer) this.endMergedxpos.elementAt(i);
			Integer temp3 = (Integer) this.beginMergedypos.elementAt(i);
			Integer temp4 = (Integer) this.endMergedypos.elementAt(i);
			int ylength;
			int lowerx = 0;
			int lowery = 0;
			if (temp1.intValue() <= temp2.intValue()) {
				lowerx = temp1.intValue();
			}
			else {
				lowerx = temp2.intValue();
			}
			if (temp3.intValue() <= temp4.intValue()) {
				lowery = temp3.intValue();
				ylength = temp4.intValue() - temp3.intValue();
			}
			else {
				lowery = temp4.intValue();
				ylength = temp3.intValue() - temp4.intValue();
			}
			if (lowerx == startxpos && lowery == startypos) {
				returnint = ylength + 1;
			}
			i++;
		}
		return returnint;
	}
	
	protected boolean isTopLeftOfMergedCell(int xpos, int ypos) {
		boolean theReturn = false;
		if (!this.cellsAreMerged) {
			theReturn = false;
		}
		else {
			int i = 0;
			for (Enumeration e = this.beginMergedxpos.elements(); e.hasMoreElements();) {
				Integer temp1 = (Integer) e.nextElement();
				Integer temp2 = (Integer) this.endMergedxpos.elementAt(i);
				Integer temp3 = (Integer) this.beginMergedypos.elementAt(i);
				Integer temp4 = (Integer) this.endMergedypos.elementAt(i);
				int lowerx = 0;
				int lowery = 0;
				if (temp1.intValue() <= temp2.intValue()) {
					lowerx = temp1.intValue();
				}
				else {
					lowerx = temp2.intValue();
				}
				if (temp3.intValue() <= temp4.intValue()) {
					lowery = temp3.intValue();
				}
				else {
					lowery = temp4.intValue();
				}
				if ((lowerx == xpos) && (lowery == ypos)) {
					theReturn = true;
				}
				i++;
			}
		}
		return theReturn;
	}
	
	/**
	* Sets the table to be horizontally striped
	*/
	public void setHorizontalZebraColored(String Color1, String Color2) {
		int y = 1;
		boolean theEnd = false;
		while (!theEnd) {
			setRowColor(y, Color1);
			y++;
			if (y > this.rows) {
				theEnd = true;
			}
			else {
				setRowColor(y, Color2);
				y++;
				if (y > this.rows) {
					theEnd = true;
				}
			}
		}
	}
	
	/**
	*
	*Sets the table to be vertically striped
	*/
	public void setVerticalZebraColored(String Color1, String Color2) {
		int x = 1;
		boolean theEnd = false;
		while (!theEnd) {
			setColumnColor(x, Color1);
			x++;
			if (x > this.cols) {
				theEnd = true;
			}
			else {
				setColumnColor(x, Color2);
				x++;
				if (x > this.cols) {
					theEnd = true;
				}
			}
		}
	}
	
	/**
	 * index lies from [0,xlength-1] , puts in yindex=0
	 */
	public Object set(int index, PresentationObject o) {
		return set(index, 0, o);
	}
	
	/**
	 * xindex lies from [0,xlength-1],yindex lies from [0,ylength-1]
	 */
	public Object set(int xindex, int yindex, PresentationObject o) {
		return set(xindex, yindex, 0, o);
	}

	/**
	 * xindex lies from 0,[xlength-1],yindex lies from [0,ylength-1],innercontainerindex lies from [0,lengthofcontainer-1]
	 */
	public Object set(int xindex, int yindex, int innercontainerindex, PresentationObject o) {
		//return set(index,0,o);
		/*TableCell moc = theCells[xindex][yindex];
		if (moc == null) {
			moc = new TableCell();
			theCells[xindex][yindex] = moc;
		}*/
		int xpos = xindex+1;
		int ypos = yindex+1;
		TableCell moc = getCellAt(xpos,ypos);
		return moc.set(innercontainerindex, o);
	}

	public List getChildrenRecursive() {
		if (this.allObjects == null) {
			List toReturn = null;
			Iterator iter = null;
			if(useFacetBasedCells()){
				iter = getFacetsAndChildren();
			}
			else{
				//Legacy implementation, see the getChildren() method:
				iter = this.getChildren().iterator();
			}
			//if (theCells != null) {
				toReturn = new Vector();
				//toReturn.containsAll(containedObjects);
				//Iterator iter = containedObjects.iterator();
				while (iter.hasNext()) {
					Object item = iter.next();
					if (item instanceof PresentationObjectContainer) {
						toReturn.add(item);
						//if(!toReturn.contains(item)){
						List tmp = ((PresentationObjectContainer) item).getChildrenRecursive();
						if (tmp != null) {
							toReturn.addAll(tmp);
						}
						//}
					}
					else {
						toReturn.add(item);
					}
				}
			//}
			this.allObjects = toReturn;
		}
		return this.allObjects;
	}

	public List getChildren() {
		if(useFacetBasedCells()){
			return super.getChildren();
		}
		else{
			//TODO: Remove this legacy implementation 
			// - as the table cells are now JSF Facets they cannot be children as well
			List theReturn = new ArrayList();
			if(this.theCells!=null){
				for (int x = 0; x < this.theCells.length; x++) {
					for (int y = 0; y < this.theCells[x].length; y++) {
						if (this.theCells[x][y] != null) {
							theReturn.add(this.theCells[x][y]);
						}
					}
				}
			}
			return theReturn;
		}
	}

	//Prints out the no-breaking-space for cells
	protected void printNbsp(IWContext iwc, int xpos, int ypos) {
		TableCell cell = getCellAt(xpos,ypos);
		//if (theCells[xpos - 1][ypos - 1] != null) {
		if(cell!=null){
			//if (theCells[xpos - 1][ypos - 1].isEmpty()) {
			if(cell.isEmpty()){
				String width = "1";
				String height = "1";
				
				TextStyler styler = new TextStyler(cell.getStyleAttribute());
				if (styler.isStyleSet("width")) {
					width = styler.getStyleValue("width");
					if (width.indexOf("px") != -1) {
						width = width.substring(0,width.indexOf("px"));
					}
				}
				if (styler.isStyleSet("height")) {
					height = styler.getStyleValue("height");
					if (height.indexOf("px") != -1) {
						height = height.substring(0,height.indexOf("px"));
					}
				}
				
				/*if (false) {
					//not implemented
				}
				else {
					String sPadding = getAttribute("cellpadding");
					int iPadding = (sPadding != null)? Integer.parseInt(sPadding):0;
					int iWidth = 1;
					int iHeight = 1;
					boolean withInPercentsOrNoPadding = false;
					boolean heightInPercentsOrNoPadding = false;
					
					if(iPadding!=0){
						try {
							iWidth = Integer.parseInt(width);
						} catch (NumberFormatException e) {
							withInPercentsOrNoPadding = true;
						}
						
						
						try {
							iHeight = Integer.parseInt(height);
						} catch (NumberFormatException e1) {
							heightInPercentsOrNoPadding = true;
						}
					}
					
					//print(Text.getNonBrakingSpace().getText());
					//print("<img src=\"" + transparentcell.getURL() + "\" width=\""+((withInPercentsOrNoPadding)?width:Integer.toString(getNbspWidthAndHeight(iWidth,iPadding)))+"\" height=\""+((heightInPercentsOrNoPadding)?height:Integer.toString(getNbspWidthAndHeight(iHeight,iPadding)))+"\" alt=\"\" />");
				}*/
				if (!IWMainApplication.useJSF) {
					print("<img src=\"" + transparentcell.getURL() + "\" width=\""+width+"\" height=\""+height+"\" alt=\"\" />");
				}
			}
		}
		else {
			if (!IWMainApplication.useJSF) {
				print("<img src=\"" + transparentcell.getURL() + "\" width=\"1\" height=\"1\" alt=\"\" />");
			}
			//print(Text.getNonBrakingSpace().getText());
		}
	}
	
	/*private int getNbspWidthAndHeight(int dimension, int padding) {
		dimension = dimension - 2 * padding;
		if (dimension < 1)
			dimension = 1;
		return dimension;
	}*/

	protected void printLine(IWContext iwc) throws Exception {
		//println("\n<tr>");
		print(LINE_BREAK);
		println(this.getRowStartTag(iwc));
		//    for(int x=1;x<=cols;){
		//println("\n<td "+"height="+this.lineHeight+" colspan="+cols+" "+COLOR_ATTRIBUTE+"="+this.lineColor+" >");
		print(LINE_BREAK);
		print(getCellStartTag(iwc) + "height=\"" + this.lineHeight + ((this.lineColspan > 1) ? ("\" colspan=\"" + this.lineColspan + "\" ") : ("\" ")) + ((this.lineColor!=null)? (COLOR_ATTRIBUTE + "=\"" + this.lineColor):"") + "\" >");
		//if(!iwc.isOpera()){
		transparentcell._print(iwc);
		print(getCellEndTag(iwc));
		//} else {
		//println("</td>");  // ?????
		//}
		//    }
		//println("</tr>");
		print(LINE_BREAK);
		println(this.getRowEndTag(iwc));
	}

	protected void printVerticalLine(IWContext iwc) throws Exception {
		print(LINE_BREAK);
		println(getCellStartTag(iwc)+" width=\"" + this.lineWidth + "\" " + ((this.lineColor!=null)? (COLOR_ATTRIBUTE + "=\"" + this.lineColor):"") + "\" "+TAG_END);
		if (!iwc.isOpera()) {
			transparentcell._print(iwc);
			println(getCellEndTag(iwc));
		}
		else {
			//println("</td>");  // ?????
		}
	}

	public void print(IWContext iwc) throws Exception {
		transparentcell = getTransparentCell(iwc);
		//if( doPrint(iwc)){
		this.markupLanguage = iwc.getMarkupLanguage();
		if ( IWConstants.MARKUP_LANGUAGE_HTML.equals(this.markupLanguage) ||  IWConstants.MARKUP_LANGUAGE_PDF_XML.equals(this.markupLanguage) ) {
			String theErrorMessage = getErrorMessage();
			if (theErrorMessage == null) {
				//if (getInterfaceStyle().equals("something")){
				//}
				//else{
				StringBuffer printString = new StringBuffer();
				printString.append(getTableStartTag(iwc));
				printString.append(getMarkupAttributesString());
				printString.append(" ");
				printString.append(TAG_END);
				println(printString.toString());
				
				//Lines initialization
				this.lineColspan = this.cols;
				if (this.addVerticalLinesBetween) {
					this.lineColspan += (this.cols - 1);
				}
				else {
					this.lineColspan += this.lineRows.length;
				}
				if (this.addLineLeft) {
					this.lineColspan++;
				}
				if (this.addLineRight) {
					this.lineColspan++;
				}
				if (this.addLineTop) {
					printLine(iwc);
				}

				
				if (!this.cellsAreMerged) {
					for (int y = 1; y <= this.rows;) {
						//println("\n<tr>");
						print(LINE_BREAK);
						println(this.getRowStartTag(iwc));
						for (int x = 1; x <= this.cols;) {
							if (this.addLineLeft && x == 1) {
								printVerticalLine(iwc);
							}
							TableCell cell = getCellAt(x,y);
							//if (theCells[x - 1][y - 1] != null) {
							if(cell!=null){
							  //if (theCells[x - 1][y - 1].getMarkupAttributesString().indexOf("align") == -1) {
								if (cell.getMarkupAttributesString().indexOf("align") == -1) {
									setAlignment(x, y, "left");
								}
								//if (theCells[x - 1][y - 1].getMarkupAttributesString().indexOf("valign") == -1) {
								if (cell.getMarkupAttributesString().indexOf("align") == -1) {
									setVerticalAlignment(x, y, "middle");
								}
								if (printString == null) {
									printString = new StringBuffer();
								}
								else {
									printString.delete(0, printString.length());
								}
								print(LINE_BREAK);
								printString.append(getCellStartTag(iwc,x,y));
								printString.append(cell.getMarkupAttributesString());
								printString.append(TAG_END);
								println(printString.toString());

								UIComponent child = cell;
								renderChild(iwc,child);
								//theObjects[x - 1][y - 1]._print(iwc);
								printNbsp(iwc, x, y);
							}
							else {
								print(LINE_BREAK);
								println(getCellStartTag(iwc,x,y)+TAG_END);
								printNbsp(iwc, x, y);
							}
							print(getCellEndTag(iwc,x,y));
							if ((this.addLineRight && x == this.cols) || (this.addVerticalLinesBetween && x != this.cols)) {
								printVerticalLine(iwc);
							}
							else {
								for (int i = 0; i < this.lineCols.length; i++) {
									if (x == this.lineCols[i]) {
										printVerticalLine(iwc);
										break;
									}
								}
							}
							x++;
						}
						//println("\n</tr>");
						print(LINE_BREAK);
						println(this.getRowEndTag(iwc));
						if (this.addLinesBetween && y != this.rows) {
							printLine(iwc);
						}
						else {
							for (int i = 0; i < this.lineRows.length; i++) {
								if (y == this.lineRows[i]) {
									printLine(iwc);
									break;
								}
							}
						}
						y++;
					}
					if (this.addLinesBottom) {
						printLine(iwc);
					}
				}
				else // if merged
					{
					for (int y = 1; y <= this.rows;) {
						//println("\n<tr>");
						print(LINE_BREAK);
						println(this.getRowStartTag(iwc));
						for (int x = 1; x <= this.cols;) {
							if (this.addLineLeft && x == 1) {
								printVerticalLine(iwc);
							}
							if (isInMergedCell(x, y)) {
								if (isTopLeftOfMergedCell(x, y)) {
									TableCell cell = getCellAt(x,y);
									//if (theCells[x - 1][y - 1] == null) {
									//	theCells[x - 1][y - 1] = new TableCell();
									//}
									if (printString == null) {
										printString = new StringBuffer();
									}
									else {
										printString.delete(0, printString.length());
									}
									print(LINE_BREAK);
									printString.append(getCellStartTag(iwc,x,y));
									int mergeWidth = getWidthOfMergedCell(x, y);
									int mergeHeight = getHeightOfMergedCell(x, y);
									cell.setMarkupAttribute("colspan",mergeWidth);
									cell.setMarkupAttribute("rowspan",mergeHeight);
									printString.append(cell.getMarkupAttributesString());
									/*
									printString.append(" colspan=\"");
									printString.append(mergeWidth);
									printString.append("\" rowspan=\"");
									printString.append(mergeHeight);
									
									printString.append("\" ");
									*/
									printString.append(TAG_END);
									println(printString.toString());
									
									UIComponent child = cell;
									renderChild(iwc,child);
									//theObjects[x - 1][y - 1]._print(iwc);

									printNbsp(iwc, x, y);
									println(getCellEndTag(iwc,x,y));
								}
							}
							else {
								TableCell cell = getCellAt(x,y);
								if (cell != null) {
									if (printString == null) {
										printString = new StringBuffer();
									}
									else {
										printString.delete(0, printString.length());
									}
									print(LINE_BREAK);
									printString.append(getCellStartTag(iwc,x,y));
									printString.append(cell.getMarkupAttributesString());
									printString.append(" ");
									printString.append(TAG_END);
									println(printString.toString());
									
									UIComponent child = cell;
									renderChild(iwc,child);
									//theObjects[x - 1][y - 1]._print(iwc);
									
									printNbsp(iwc, x, y);
								}
								else {
									print(LINE_BREAK);
									println(getCellStartTag(iwc,x,y)+TAG_END);
									printNbsp(iwc, x, y);
								}
								println(getCellEndTag(iwc,x,y));
								if ((this.addLineRight && x == this.cols) || (this.addVerticalLinesBetween && x != this.cols)) {
									printVerticalLine(iwc);
								}
								else {
									for (int i = 0; i < this.lineCols.length; i++) {
										if (x == this.lineCols[i]) {
											printVerticalLine(iwc);
											break;
										}
									}
								}
							}
							x++;
						}
						print(LINE_BREAK);
						println(this.getRowEndTag(iwc));
						// println("\n</tr>");
						if (this.addLinesBetween && y != this.rows) {
							printLine(iwc);
						}
						else {
							for (int i = 0; i < this.lineRows.length; i++) {
								if (y == this.lineRows[i]) {
									printLine(iwc);
									break;
								}
							}
						}
						y++;
					}
					if (this.addLinesBottom) {
						printLine(iwc);
					}
				}
				print(LINE_BREAK);
				println(getTableEndTag(iwc));
				//}
			}
			else {
				println("<pre>");
				println("Exception:");
				println(theErrorMessage);
				println("</pre>");
			}
		}
		else if (IWConstants.MARKUP_LANGUAGE_WML.equals(this.markupLanguage) ){
			
			String tableTagStart = "";
			String tableTagEnd = "";
			String cellTagStart = "";
			String cellTabEnd = "";
			String trStart = "";
			String trEnd = "";
			
			if(this.forceToRenderAsTableInWML){
				tableTagStart = WML_TABLE_TAG_START;
				tableTagEnd = WML_TABLE_TAG_END;
				cellTagStart = WML_CELL_TAG_START+TAG_END;
				cellTabEnd = WML_CELL_TAG_END;
				trStart = WML_TR_START;
				trEnd = WML_TR_END;
				if(this.rows>0){
					print(tableTagStart+" columns=\""+this.cols+"\""+TAG_END);
				}
			}
			
			for (int y = 1; y <= this.rows;) {
				print(trStart);
				for (int x = 1; x <= this.cols;) {
					print(cellTagStart);
					TableCell cell = getCellAt(x,y);
					if (cell != null) {
						UIComponent child = cell;
						renderChild(iwc,child);
						//theObjects[x - 1][y - 1]._print(iwc);
					}
					print(cellTabEnd);
					x++;
				}
				print(trEnd);
				y++;
			}
			if(this.rows>0){
				print(tableTagEnd);
			}
		} //end if (getLanguage(...
		else {
			for (int y = 1; y <= this.rows;) {
				for (int x = 1; x <= this.cols;) {
					TableCell cell = getCellAt(x,y);
					if (cell != null) {
						UIComponent child = cell;
						renderChild(iwc,child);
						//theObjects[x - 1][y - 1]._print(iwc);
					}
					x++;
				}
				y++;
			}
		}
		//}//end doPrint(iwc)
	}

	protected String getTableEndTag(IWContext iwc) {
		return HTML_TABLE_TAG_END;
	}

	protected String getTableStartTag(IWContext iwc) {
		return HTML_TABLE_TAG_START;
	}

	protected String getCellEndTag(IWContext iwc) {
		if(this.markupLanguage == null) {
			this.markupLanguage = iwc.getMarkupLanguage();
		}
		
		if(IWConstants.MARKUP_LANGUAGE_HTML.equals(this.markupLanguage)){
			return HTML_CELL_TAG_END;
		}
		else if(IWConstants.MARKUP_LANGUAGE_PDF_XML.equals(this.markupLanguage)){
			return PDF_XML_CELL_TAG_END;
		}
		else{//default value (backward compatabilty safety if language is null)
			return HTML_CELL_TAG_END;
		}
		
	}


	protected String getCellEndTag(IWContext iwc, int column, int row) {
		return getCellEndTag(iwc);
	}
	
	protected String getCellStartTag(IWContext iwc) {
		if(this.markupLanguage == null) {
			this.markupLanguage = iwc.getMarkupLanguage();
		}
		
		if(IWConstants.MARKUP_LANGUAGE_HTML.equals(this.markupLanguage)){
			return HTML_CELL_TAG_START;
		}
		else if(IWConstants.MARKUP_LANGUAGE_PDF_XML.equals(this.markupLanguage)){
			return PDF_XML_CELL_TAG_START;
		}
		else{
			return HTML_CELL_TAG_START;
		}
		
	}

	protected String getCellStartTag(IWContext iwc, int column, int row) {
		return getCellStartTag(iwc);
	}
	
	protected String getRowStartTag(IWContext iwc, int numberOfRow) {
		return getRowStartTag(iwc);
	}

	protected  String getRowEndTag(IWContext iwc, int numberOfRow) {
		return getRowEndTag(iwc);
	}

	protected  String getRowStartTag(IWContext iwc) {
		if(this.markupLanguage == null) {
			this.markupLanguage = iwc.getMarkupLanguage();
		}
		
		if(IWConstants.MARKUP_LANGUAGE_HTML.equals(this.markupLanguage)){
			return HTML_TR_START;
		}
		else if(IWConstants.MARKUP_LANGUAGE_PDF_XML.equals(this.markupLanguage)){
			return PDF_XML_TR_START;
		}
		else{
			return HTML_TR_START;
		}
	}

	protected  String getRowEndTag(IWContext iwc) {
		if(this.markupLanguage == null) {
			this.markupLanguage = iwc.getMarkupLanguage();
		}
		
		if(IWConstants.MARKUP_LANGUAGE_HTML.equals(this.markupLanguage)){
			return HTML_TR_END;
		}
		else if(IWConstants.MARKUP_LANGUAGE_PDF_XML.equals(this.markupLanguage)){
			return PDF_XML_TR_END;
		}
		else{
			return HTML_TR_END;
		}
	}
	
	

	public int numberOfObjects() {
		//if (theCells != null) {
			return this.cols * this.rows;
		//}
		//else {
		//	return 0;
		//}
	}

	public UIComponent objectAt(int index) {
		//if (theCells != null) {
			if (this.rows != 0) {
				int x = Math.round(index / this.rows);
				int y = index - x * this.rows;
				int xpos = x+1;
				int ypos = y+1;
				return getCellAt(xpos,ypos);
			}
			else {
				return null;
			}
		//}
		//else {
		//	return null;
		//}
	}

	public boolean isEmpty() {
		/*if (theCells != null) {
			return false;
		}
		else {
			return true;
		}*/
		return false;
	}

	/*public int[] getTableIndex(PresentationObject o) {
		if (theCells == null)
			return (null);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				PresentationObjectContainer cont = (PresentationObjectContainer) theCells[j][i];
				if (cont != null) {
					int index = cont.getIndex(o);
					if (index > -1) {
						int ret[] = { i + 1, j + 1 };
						return ret;
					}
				}
			}
		return (null);
	}*/

	public boolean isEmpty(int x, int y) {
		/*if (theCells != null) {
			if (theCells[x - 1][y - 1] == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}*/
		return getCellAt(x,y).isEmpty();
	}

	public void setProperty(String key, String values[]) {
		if (key.equalsIgnoreCase("border")) {
			setBorder(Integer.parseInt(values[0]));
		}
		else if (key.equalsIgnoreCase("width")) {
			setWidth(values[0]);
		}
		else if (key.equalsIgnoreCase("height")) {
			setHeight(values[0]);
		}
		else if (key.equalsIgnoreCase("columns")) {
			setColumns(Integer.parseInt(values[0]));
		}
		else if (key.equalsIgnoreCase("rows")) {
			setRows(Integer.parseInt(values[0]));
		}
	}
	

	public Object clone(IWUserContext iwc, boolean askForPermission) {
		return clone(iwc,askForPermission,1,getRows());
	}
	public Object clone(IWUserContext iwc,boolean askForPermission,int startRow,int endRow){
		Table obj = null;
		try {
			obj = (Table) super.clone(iwc, askForPermission);
			if(useFacetBasedCells()){
				//Facets have already been cloned here in super.clone(iwc, askForPermission);
				//cloneJSFFacets(obj,iwc,askForPermission);
			}
			else{
				if (this.theCells != null) {
					obj.theCells = new TableCell[this.cols][this.rows];
					for (int x = 0; x < this.theCells.length; x++) {
						for (int y = (startRow-1); y < endRow; y++) {
							if (this.theCells[x][y] != null) {
								obj.theCells[x][y] = (TableCell) this.theCells[x][y].clonePermissionChecked(iwc, askForPermission);
								obj.theCells[x][y].setParentObject(obj);
								obj.theCells[x][y].setLocation(this.getLocation());
								//obj.theObjects[x][y].remove(NULL_CLONE_OBJECT);
							}
						}
					}
				}
			}
			obj.cols = this.cols;
			obj.rows = this.rows;
			if (this.beginMergedxpos != null) {
				obj.beginMergedxpos = (Vector) this.beginMergedxpos.clone();
			}
			if (this.beginMergedypos != null) {
				obj.beginMergedypos = (Vector) this.beginMergedypos.clone();
			}
			if (this.endMergedxpos != null) {
				obj.endMergedxpos = (Vector) this.endMergedxpos.clone();
			}
			if (this.endMergedypos != null) {
				obj.endMergedypos = (Vector) this.endMergedypos.clone();
			}
			obj.isResizable = this.isResizable;
			obj.cellsAreMerged = this.cellsAreMerged;
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	public PresentationObjectContainer containerAt(int x, int y) {
		TableCell cont = null;
		try {
			/*cont = theCells[x - 1][y - 1];
			if (cont == null) {
				cont = new TableCell();
				theCells[x - 1][y - 1] = cont;
				cont.setParentObject(this);
			}*/
			return getCellAt(x,y);
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
		return cont;
	}

	public boolean remove(PresentationObject obj) {
		/*if (theCells != null) {
			for (int x = 0; x < theCells.length; x++) {
				for (int y = 0; y < theCells[x].length; y++) {
					if (theCells[x][y] != null) {
						if (theCells[x][y].remove(obj)) {
							return true;
						}
					}
				}
			}
		}*/
		for (int y = 1; y <= this.rows;) {
			for (int x = 1; x <= this.cols;) {
				TableCell cell = getCellAt(x,y);
				if (cell.remove(obj)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the color of a Cell.
	 * Returns NULL if no color set
	 */
	public String getColor(int xpos, int ypos) {
		PresentationObjectContainer cont = getCellAt(xpos,ypos);
		if (cont == null) {
			return null;
		}
		else {
			TextStyler styler = new TextStyler(cont.getStyleAttribute());
			if (styler.isStyleSet("background-color")) {
				return styler.getStyleValue("background-color");
			}
			return null;
		}
	}
	/**
	 * Returns the style of a Cell
	 * Returns NULL of no style is set
	 */
	public String getStyle(int xpos, int ypos) {
		PresentationObjectContainer cont = getCellAt(xpos,ypos);
		if (cont != null) {
			String styleAtt = cont.getStyleAttribute();
			if (!"".equals(styleAtt)) {
				return styleAtt;
			} 
		}
		return null;
	}
	/**
	 * Returns the styleClass of a Cell
	 * Returns NULL of no style is set
	 */
	public String getClass(int xpos, int ypos) {
		PresentationObjectContainer cont = getCellAt(xpos,ypos);
		if (cont != null) {
			String classAtt = cont.getMarkupAttribute("class");
			if (!"".equals(classAtt)) {
				return classAtt;
			}
		}
		return null;
	}

	public void setResizable(boolean resizable) {
		this.isResizable = resizable;
	}

	public static Image getTransparentCell(IWContext iwc) {
		if (transparentcell == null) {
			transparentcell = iwc.getIWMainApplication().getBundle(PresentationObject.CORE_IW_BUNDLE_IDENTIFIER).getImage("transparentcell.gif");
		}
		return (Image) transparentcell.clone();
	}

	/**
	 *
	 */
	public void lock(int xpos, int ypos) {
		PresentationObjectContainer cont = containerAt(xpos, ypos);
		if (cont != null) {
			cont.lock();
		}
	}

	/**
	 *
	 */
	public void unlock(int xpos, int ypos) {
		PresentationObjectContainer cont = containerAt(xpos, ypos);
		if (cont != null) {
			cont.unlock();
		}
	}

	/**
	 *
	 */
	public boolean isLocked(int xpos, int ypos) {
		PresentationObjectContainer cont = containerAt(xpos, ypos);
		if (cont != null) {
			return (cont.isLocked());
		}
		else {
			return (true);
		}
	}

	/**
	 *
	 */
	public void setLabel(String label, int xpos, int ypos) {
		PresentationObjectContainer cont = containerAt(xpos, ypos);
		if (cont != null) {
			cont.setLabel(label);
		}
	}

	/**
	 *
	 */
	public String getLabel(int xpos, int ypos) {
		PresentationObjectContainer cont = containerAt(xpos, ypos);
		if (cont != null) {
			return (cont.getLabel());
		}
		else {
			return (null);
		}
	}

	public void setLinesBetween(boolean value) {
		this.addLinesBetween = value;
	}

	public void setTopLine(boolean value) {
		this.addLineTop = value;
	}
	
	public void setTableBorder(int width, String color, String style) {
		setStyleAttribute("border-width", width+"px");
		setStyleAttribute("border-color", color);
		setStyleAttribute("border-style", style);
	}

	public void setTableBorderTop(int width, String color, String style) {
		setStyleAttribute("border-top-width", width+"px");
		setStyleAttribute("border-top-color", color);
		setStyleAttribute("border-top-style", style);
	}

	public void setTableBorderBottom(int width, String color, String style) {
		setStyleAttribute("border-bottom-width", width+"px");
		setStyleAttribute("border-bottom-color", color);
		setStyleAttribute("border-bottom-style", style);
	}

	public void setBottomLine(boolean value) {
		this.addLinesBottom = value;
	}

	/**
	 * 
	 * @uml.property name="lineColor"
	 */
	public void setLineColor(String color) {
		this.lineColor = color;
	}

	/**
	 * 
	 * @uml.property name="lineHeight"
	 */
	public void setLineHeight(String height) {
		this.lineHeight = height;
	}


	public void setVerticatLinesBetween(boolean value) {
		this.addVerticalLinesBetween = value;
	}

	public void setRightLine(boolean value) {
		this.addLineRight = value;
	}

	public void setLeftLine(boolean value) {
		this.addLineLeft = value;
	}

	/**
	 * 
	 * @uml.property name="lineWidth"
	 */
	public void setLineWidth(String width) {
		this.lineWidth = width;
	}

	public void setLineFrame(boolean value) {
		this.setLeftLine(value);
		this.setRightLine(value);
		this.setTopLine(value);
		this.setBottomLine(value);
	}

	/**
	 * @deprecated: only for builderuse until handler has been implemented
	 */
	public void setLineAfterRow(int row, boolean value) {
		if (value) {
			setLineAfterRow(row);
		}
		else {
			for (int i = 0; i < this.lineRows.length; i++) {
				if (this.lineRows[i] == row) {
					this.lineRows[i] = -1; // should decrease array to
				}
			}
		}
	}

	/**
	 * @deprecated: only for builderuse until handler has been implemented
	 */
	public void setLineAfterColumn(int column, boolean value) {
		if (value) {
			setLineAfterColumn(column);
		}
		else {
			for (int i = 0; i < this.lineCols.length; i++) {
				if (this.lineCols[i] == column) {
					this.lineCols[i] = -1; // should decrease array to
				}
			}
		}
	}

	public void setLineAfterRow(int row) {
		// increase length
		int[] newLines = new int[this.lineRows.length + 1];
		System.arraycopy(this.lineRows, 0, newLines, 0, this.lineRows.length);
		this.lineRows = newLines;
		// done
		this.lineRows[this.lineRows.length - 1] = row;
	}

	public void setLineAfterColumn(int column) {
		// increase length
		int[] newLines = new int[this.lineCols.length + 1];
		System.arraycopy(this.lineCols, 0, newLines, 0, this.lineCols.length);
		this.lineCols = newLines;
		// done
		this.lineCols[this.lineCols.length - 1] = column;
	}
	
	public void removeLineColor(boolean value){
		if(value){
			this.lineColor = null;	
		} else {
			this.lineColor = "#000000";
		}
	}

	

	public void encodeBegin(FacesContext fc)throws IOException{
		//Does nothing here. insted encodeChildren calles the print(iwc) method.
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#encodeChildren(javax.faces.context.FacesContext)
	 */
	public void encodeChildren(FacesContext context) throws IOException {
		super.encodeChildren(context);
	}
	
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(context, values[0]);
		this.cols = ((Integer) values[1]).intValue();
		this.rows = ((Integer) values[2]).intValue();
		this.beginMergedxpos = (Vector)values[3];
		this.beginMergedypos = (Vector)values[4];
		this.endMergedxpos = (Vector)values[5];
		this.endMergedypos = (Vector)values[6];
		this.isResizable = ((Boolean) values[7]).booleanValue();
		this.cellsAreMerged = ((Boolean) values[8]).booleanValue();
		this._width=(String)values[9];
		this._height=(String)values[10];
		this.forceToRenderAsTableInWML = ((Boolean) values[11]).booleanValue();
		this.addLineTop = ((Boolean) values[12]).booleanValue();
		this.addLinesBetween = ((Boolean) values[13]).booleanValue();
		this.addLinesBottom = ((Boolean) values[14]).booleanValue();
		this.addLineLeft = ((Boolean) values[15]).booleanValue();
		this.addVerticalLinesBetween = ((Boolean) values[16]).booleanValue();
		this.addLineRight = ((Boolean) values[17]).booleanValue();
		this.lineColor = (String) values[18];
		this.lineHeight = (String) values[19];
		this.lineWidth = (String) values[20];
		this.lineColspan = ((Integer)values[21]).intValue();
		this.lineRows = (int[])values[22];
		this.lineCols = (int[])values[23];
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext context) {
		Object values[] = new Object[24];
		values[0] = super.saveState(context);
		values[1] = new Integer(this.cols);
		values[2] = new Integer(this.rows);
		values[3] = this.beginMergedxpos;
		values[4] = this.beginMergedypos;
		values[5] = this.endMergedxpos;
		values[6] = this.endMergedypos;
		values[7] = Boolean.valueOf(this.isResizable);
		values[8] = Boolean.valueOf(this.cellsAreMerged);
		values[9] = this._width;
		values[10] = this._height;
		values[11] = Boolean.valueOf(this.forceToRenderAsTableInWML);
		values[12] = Boolean.valueOf(this.addLineTop);
		values[13] = Boolean.valueOf(this.addLinesBetween);
		values[14] = Boolean.valueOf(this.addLinesBottom);
		values[15] = Boolean.valueOf(this.addLineLeft);
		values[16] = Boolean.valueOf(this.addVerticalLinesBetween);
		values[17] = Boolean.valueOf(this.addLineRight);
		values[18] = this.lineColor;
		values[19] = this.lineHeight;
		values[20] = this.lineWidth;
		values[21] = new Integer(this.lineColspan);
		values[22] = this.lineRows;
		values[23] = this.lineCols;
		return values;
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processRestoreState(javax.faces.context.FacesContext, java.lang.Object)
	 */
	public void processRestoreState(FacesContext fc, Object arg1) {
		// TODO Auto-generated method stub
		super.processRestoreState(fc, arg1);
	}
	/* (non-Javadoc)
	 * @see javax.faces.component.UIComponent#processSaveState(javax.faces.context.FacesContext)
	 */
	public Object processSaveState(FacesContext arg0) {
		// TODO Auto-generated method stub
		return super.processSaveState(arg0);
	}
	/*
	 * End JSF SPECIFIC IMPLEMENTAION METHODS
	 */	

	/**
	 * @param forceToRenderAsTableInWML The forceToRenderAsTableInWML to set.
	 */
	public void setToForceToRenderAsTableInWML(boolean forceToRenderAsTableInWML) {
		this.forceToRenderAsTableInWML = forceToRenderAsTableInWML;
	}
	
	//debug:
	public void _main(IWContext iwc) throws Exception{
		super._main(iwc);
	}
}
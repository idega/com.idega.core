/*
 * $Id: TableCell2.java,v 1.1 2005/08/07 16:07:33 laddi Exp $
 * Created on Aug 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.util.Iterator;
import java.util.List;


/**
 * Last modified: $Date: 2005/08/07 16:07:33 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class TableCell2 extends PresentationObjectContainer {

	/**
	 * The current cell provides header information for the rest of the column that contains it.
	 * @see #setScope(java.lang.String)
	 */
  public static final String SCOPE_COLUMN = "col";
	/**
	 * The current cell provides header information for the rest of the row that contains it.
	 * @see #setScope(java.lang.String)
	 */
  public static final String SCOPE_ROW = "row";
	/**
	 * The header cell provides header information for the rest of the column group that contains it.
	 * @see #setScope(java.lang.String)
	 */
  public static final String SCOPE_COLUMN_GROUP = "colgroup";
	/**
	 * The header cell provides header information for the rest of the row group that contains it.
	 * @see #setScope(java.lang.String)
	 */
  public static final String SCOPE_ROW_GROUP = "rowgroup";
  
  private static final String MARKUP_ATTRIBUTE_ABBREVIATION = "abbr";
  private static final String MARKUP_ATTRIBUTE_AXIS = "axis";
  private static final String MARKUP_ATTRIBUTE_HEADERS = "headers";
  private static final String MARKUP_ATTRIBUTE_ROW_SPAN = "rowspan";
  private static final String MARKUP_ATTRIBUTE_COLUMN_SPAN = "colspan";
  private static final String MARKUP_ATTRIBUTE_SCOPE = "scope";

	/**
	 * Sets the alignment of data and the justification of text in the cell.
	 * 
	 * @param alignment		The alignment to set.
	 */
	public void setCellHorizontalAlignment(String alignment) {
		setMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_HORIZONTAL_ALIGNMENT, alignment);
	}
	
	/**
	 * Gets the horizontal alignment set on the cell.
	 * 
	 * @return	The alignment set for the cell.  Returns the default value (HORIZONTAL_ALIGNMENT_LEFT) if not set.
	 */
	public String getCellHorizontalAlignment() {
		String alignment = getMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_HORIZONTAL_ALIGNMENT);
		if (alignment != null) {
			return alignment;
		}
		return Table2.HORIZONTAL_ALIGNMENT_LEFT;
	}
	
	/**
	 * Sets the vertical position of data within a cell.
	 * 
	 * @param alignment		The alignment to set.
	 */
	public void setCellVerticalAlignment(String alignment) {
		setMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_VERTICAL_ALIGNMENT, alignment);
	}
	
	/**
	 * Gets the vertical alignment set on the cell.
	 * 
	 * @return	The alignment set for the cell.  Returns the default value (VERTICAL_ALIGNMENT_MIDDLE) if not set.
	 */
	public String getCellVerticalAlignment() {
		String alignment = getMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_VERTICAL_ALIGNMENT);
		if (alignment != null) {
			return alignment;
		}
		return Table2.VERTICAL_ALIGNMENT_MIDDLE;
	}
	
	/**
	 * Sets the number of rows spanned by the current cell.
	 * The value zero ("0") means that the cell spans all rows from the current row to the last row of the table section
	 * (TableHeaderRowGroup, TableBodyRowGroup, or TableFooterRowGroup) in which the cell is defined.
	 * 
	 * @param span	The span value to set.
	 */
	public void setRowSpan(int span) {
		if (span < 0) {
			throw new IllegalArgumentException("Row span value must be greater than or equal to 0");
		}
		setMarkupAttribute(MARKUP_ATTRIBUTE_ROW_SPAN, span);
	}
	
	/**
	 * Gets the row span set on the cell.
	 * 
	 * @return	The row span set for the cell.  Returns the default value (1) if not set.
	 */
	public int getRowSpan() {
		String span = getMarkupAttribute(MARKUP_ATTRIBUTE_ROW_SPAN);
		if (span != null) {
			return Integer.parseInt(span);
		}
		return 1;
	}
	
	/**
	 * Sets the number of columns spanned by the current cell.
	 * The value zero ("0") means that the cell spans all columns from the current column to the last column of the column group
	 * (TableColumnGroup) in which the cell is defined.
	 * 
	 * @param span	The span value to set.
	 */
	public void setColumnSpan(int span) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_COLUMN_SPAN, span);
	}
	
	/**
	 * Gets the column span set on the cell.
	 * 
	 * @return	The column span set for the cell.  Returns the default value (1) if not set.
	 */
	public int getColumnSpan() {
		String span = getMarkupAttribute(MARKUP_ATTRIBUTE_COLUMN_SPAN);
		if (span != null) {
			return Integer.parseInt(span);
		}
		return 1;
	}
	
	/**
	 * Specifies the list of header cells that provide header information for the current data cell.
	 * The value of this attribute is a space-separated list of cell names; those cells must be named by setting their id attribute.
	 * 
	 * @param headers	The headers value to set.
	 */
	public void setHeaders(String headers) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_HEADERS, headers);
	}
	
	/**
	 * Gets the headers value set on the cell.
	 * 
	 * @return	The headers value set for the cell.  Returns null if not set.
	 */
	public String getHeaders() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_HEADERS);
	}
	
	/**
	 * Sets an abbreviated form of the cell's content.
	 * 
	 * @param abbreviation	The abbreviation to set.
	 */
	public void setAbbreviation(String abbreviation) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_ABBREVIATION, abbreviation);
	}
	
	/**
	 * Gets the abbreviation value set on the cell.
	 * 
	 * @return	The abbreviation set for the cell.  Returns null if not set.
	 */
	public String getAbbreviation() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_ABBREVIATION);
	}
	
	/**
	 * Sets a cell into conceptual categories that can be considered to form axes in an n-dimensional space.
	 * The value of this attribute is a comma-separated list of category names.
	 * 
	 * @param axis	The axis value to set.
	 */
	public void setAxis(String axis) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_AXIS, axis);
	}
	
	/**
	 * Gets the axis value set on the cell.
	 * 
	 * @return	The axis set for the cell.  Returns null if not set.
	 */
	public String getAxis() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_AXIS);
	}
	
	/**
	 * Sets the set of data cells for which the current header cell provides header information.
	 * @see #SCOPE_COLUMN
	 * @see #SCOPE_COLUMN_GROUP
	 * @see #SCOPE_ROW
	 * @see #SCOPE_ROW_GROUP
	 * 
	 * @param scope	The scope value to set.
	 */
	public void setScope(String scope) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_SCOPE, scope);
	}
	
	/**
	 * Gets the scope value set on the cell.
	 * 
	 * @return	The scope set for the cell.  Returns null if not set.
	 */
	public String getScope() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_SCOPE);
	}
	
  protected String getTag() {
		return "td";
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<" + getTag() + getMarkupAttributesString() + ">");

			List theObjects = getChildren();
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					renderChild(iwc,item);
				}
			}
			
			println("</" + getTag() + ">");
		}
	}
}
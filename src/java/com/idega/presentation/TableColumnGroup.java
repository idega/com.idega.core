/*
 * $Id: TableColumnGroup.java,v 1.1 2005/08/07 16:07:33 laddi Exp $
 * Created on Aug 6, 2005
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
public class TableColumnGroup extends PresentationObject {

	private static final String MARKUP_ATTRIBUTE_SPAN = "span";
	private static final String MARKUP_ATTRIBUTE_WIDTH = "width";

	/**
	 * Creates a new <code>TableColumn</code> within the current <code>TableColumnGroup</code>.
	 * 
	 * @return A new <code>TableColumn</code> object.
	 */
	public TableColumn createColumn() {
		TableColumn column = new TableColumn();
		getChildren().add(column);
		
		return column;
	}
	
	/**
	 * Sets the alignment of data and the justification of text in the cells contained.
	 * 
	 * @param alignment		The alignment to set.
	 */
	public void setCellHorizontalAlignment(String alignment) {
		setMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_HORIZONTAL_ALIGNMENT, alignment);
	}
	
	/**
	 * Gets the horizontal alignment set on the cells contained.
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
	 * Sets the vertical position of data within contained cells.
	 * 
	 * @param alignment		The alignment to set.
	 */
	public void setCellVerticalAlignment(String alignment) {
		setMarkupAttribute(Table2.MARKUP_ATTRIBUTE_CELL_VERTICAL_ALIGNMENT, alignment);
	}
	
	/**
	 * Gets the vertical alignment set on the cells contained.
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
	 * Sets the number of columns in the column group.
	 * 
	 * @param span	The span value to set, must be an integer > 0.
	 */
	public void setSpan(int span) {
		if (span < 1) {
			throw new IllegalArgumentException("Span value must be greater than 0.");
		}
		setMarkupAttribute(MARKUP_ATTRIBUTE_SPAN, span);
	}
	
	/**
	 * Gets the span set on the column group.
	 * 
	 * @return	The span set for the column group.  Returns the default value (1) if not set.
	 */
	public int getSpan() {
		String span = getMarkupAttribute(MARKUP_ATTRIBUTE_SPAN);
		if (span != null) {
			return Integer.parseInt(span);
		}
		return 1;
	}
	
	/**
	 * Sets the default width for each column in the current column group.
	 * 
	 * @param width	The width to set.
	 */
	public void setWidth(String width) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_WIDTH, width);
	}
	
	/**
	 * Gets the width set on the column group.  The value must be either a number (150) or a percentage (66%).
	 * 
	 * @return	The width set for the column group.  Returns null if not set.
	 */
	public String getWidth() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_WIDTH);
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			List theObjects = getChildren();

			if (theObjects != null && !theObjects.isEmpty()) {
				println("<colgroup" + getMarkupAttributesString() + ">");
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					renderChild(iwc,item);
				}
				println("</colgroup>");
			}
			else {
				println("<colgroup " + getMarkupAttributesString() + " />");
			}
		}
	}
}
/*
 * $Id: TableRowGroup.java,v 1.1 2005/08/07 16:07:32 laddi Exp $
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
 * Last modified: $Date: 2005/08/07 16:07:32 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public abstract class TableRowGroup extends PresentationObject {
	
	/**
	 * Creates a new <code>TableRow</code> within the current <code>TableRowGroup</code> with the first available row number.
	 * 
	 * @return A new <code>TableRow</code> object.
	 */
	public TableRow createRow() {
		TableRow row = new TableRow();
		getChildren().add(row);
		
		return row;
	}
	
	/**
	 * Creates a new <code>TableRow</code> within the current <code>TableRowGroup</code> with the index (row number) specified.
	 * 
	 * @param index	The index (row number) the <code>TableRow</code> should be in.
	 * @return A new <code>TableRow</code> object.
	 */
	public TableRow createRow(int index) {
		TableRow row = new TableRow();
		getChildren().add(index, row);
		
		return row;
	}
	
	/**
	 * Gets the <code>TableRow</code> in the specified index (row number).
	 * 
	 * @param index	The index (row number) the <code>TableRow</code> is in.
	 * @return	The <code>TableRow</code> object or null if it doesn't exist.
	 */
	public TableRow getRow(int index) {
		return (TableRow) getChildren().get(index);
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
	
	public abstract String getTag();

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			println("<" + getTag() + getMarkupAttributesString() + ">");

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
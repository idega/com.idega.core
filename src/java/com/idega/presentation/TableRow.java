/*
 * $Id: TableRow.java,v 1.3 2005/09/19 15:00:22 laddi Exp $
 * Created on Aug 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;


/**
 * Last modified: $Date: 2005/09/19 15:00:22 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class TableRow extends PresentationObject {

	/**
	 * Creates a new <code>TableCell2</code> within the current <code>TableRow</code> with the index (column number) specified.
	 * 
	 * @param index	The index (column number) the <code>TableCell2</code> should be in.
	 * @return A new <code>TableCell2</code> object.
	 */
	public TableCell2 createCell(int index) {
		TableCell2 cell = new TableCell2();
		
		int amount = getChildren().size();
		if (amount < index + 1) {
			for (int i = 0; i < ((index + 1) - amount); i++) {
				getChildren().add(new TableCell2());
			}
		}
		
		getChildren().add(index, cell);
		
		return cell;
	}
	
	/**
	 * Creates a new <code>TableCell2</code> within the current <code>TableRow</code> in the first possible column available.
	 * 
	 * @return A new <code>TableCell2</code> object.
	 */
	public TableCell2 createCell() {
		TableCell2 cell = new TableCell2();
		getChildren().add(cell);
		
		return cell;
	}
	
	/**
	 * Creates a new <code>TableHeaderCell</code> within the current <code>TableRow</code> with the index (column number) specified.
	 * 
	 * @param index	The index (column number) the <code>TableHeaderCell</code> should be in.
	 * @return A new <code>TableHeaderCell</code> object.
	 */
	public TableHeaderCell createHeaderCell(int index) {
		TableHeaderCell cell = new TableHeaderCell();
		getChildren().add(index, cell);
		
		return cell;
	}
	
	/**
	 * Creates a new <code>TableHeaderCell</code> within the current <code>TableRow</code> in the first possible column available.
	 * 
	 * @return A new <code>TableHeaderCell</code> object.
	 */
	public TableHeaderCell createHeaderCell() {
		TableHeaderCell cell = new TableHeaderCell();
		getChildren().add(cell);
		
		return cell;
	}
	
	/**
	 * Gets the <code>TableCell2</code> in the specified index (column number).
	 * 
	 * @param index	The index (column number) the <code>TableCell2</code> is in.
	 * @return	The <code>TableCell2</code> object or null if it doesn't exist.
	 */
	public TableCell2 getCell(int index) {
		return (TableCell2) getChildren().get(index);
	}
	
	public Collection getCells() {
		return getChildren();
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
	
	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			println("<tr" + getMarkupAttributesString() + ">");

			List theObjects = getChildren();
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					renderChild(iwc,item);
				}
			}
			
			println("</tr>");
		}
	}

	public void encodeBegin(FacesContext context) throws IOException {
		println("<tr" + getMarkupAttributesString() + ">");
	}

	public void encodeEnd(FacesContext arg0) throws IOException {
		println("</tr>");
	}
}
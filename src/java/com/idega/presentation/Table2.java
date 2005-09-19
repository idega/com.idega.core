/*
 * $Id: Table2.java,v 1.2 2005/09/19 12:48:32 laddi Exp $
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * Last modified: $Date: 2005/09/19 12:48:32 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class Table2 extends PresentationObject {
	
	/**
	 * Left-flush data/Left-justify text. This is the default value for table data.
	 * 
	 * @see #TableRow.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumn.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableCell2.setCellHorizontalAlignment(java.lang.String);
	 */
	public static final String HORIZONTAL_ALIGNMENT_LEFT = "left";
	/**
	 * Center data/Center-justify text. This is the default value for table headers.
	 * 
	 * @see #TableRow.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumn.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableCell2.setCellHorizontalAlignment(java.lang.String);
	 */
	public static final String HORIZONTAL_ALIGNMENT_CENTER = "center";
	/**
	 * Right-flush data/Right-justify text.
	 * 
	 * @see #TableRow.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumn.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableCell2.setCellHorizontalAlignment(java.lang.String);
	 */
	public static final String HORIZONTAL_ALIGNMENT_RIGHT = "right";
	/**
	 * Double-justify text.
	 * 
	 * @see #TableRow.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumn.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableCell2.setCellHorizontalAlignment(java.lang.String);
	 */
	public static final String HORIZONTAL_ALIGNMENT_JUSTIFY = "justify";
	/**
	 * Align text around a specific character, setCharacter(char) must also be set (may not work in all user agents).
	 * 
	 * @see #TableRow.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumn.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellHorizontalAlignment(java.lang.String);
	 * @see #TableCell2.setCellHorizontalAlignment(java.lang.String);
	 */
	public static final String HORIZONTAL_ALIGNMENT_CHAR = "char";

	/**
	 * Cell data is flush with the top of the cell.
	 * 
	 * @see #TableRow.setCellVerticalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumn.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableCell2.setCellVerticalAlignment(java.lang.String);
	 */
	public static final String VERTICAL_ALIGNMENT_TOP = "top";
	/**
	 * Cell data is centered vertically within the cell. This is the default value.
	 * 
	 * @see #TableRow.setCellVerticalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumn.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableCell2.setCellVerticalAlignment(java.lang.String);
	 */
	public static final String VERTICAL_ALIGNMENT_MIDDLE = "middle";
	/**
	 * Cell data is flush with the bottom of the cell.
	 * 
	 * @see #TableRow.setCellVerticalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumn.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableCell2.setCellVerticalAlignment(java.lang.String);
	 */
	public static final String VERTICAL_ALIGNMENT_BOTTOM = "bottom";
	/**
	 * All cells in the same row as a cell whose valign attribute has this value should have their textual data positioned so that the first text line occurs on a baseline common to all cells in the row.
	 * This constraint does not apply to subsequent text lines in these cells.
	 * 
	 * @see #TableRow.setCellVerticalAlignment(java.lang.String);
	 * @see #TableRowGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumn.setCellVerticalAlignment(java.lang.String);
	 * @see #TableColumnGroup.setCellVerticalAlignment(java.lang.String);
	 * @see #TableCell2.setCellVerticalAlignment(java.lang.String);
	 */
	public static final String VERTICAL_ALIGNMENT_BASELINE = "baseline";
	
	/**
	 * No sides. This is the default value.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_VOID = "void";
	/**
	 * The top side only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_ABOVE = "above";
	/**
	 * The bottom side only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_BELOW = "below";
	/**
	 * The top and bottom sides only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_HORIZONTAL_SIDES = "hsides";
	/**
	 * The right and left sides only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_VERTICAL_SIDES = "vsides";
	/**
	 * The left-hand side only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_LEFT_HAND_SIDE = "lhs";
	/**
	 * The right-hand side only.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_RIGHT_HAND_SIDE = "rhs";
	/**
	 * All four sides.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_BOX = "box";
	/**
	 * All four sides.
	 * @see #setFrame(java.lang.String)
	 */
	public static final String FRAME_BORDER = "border";

	/**
	 * No rules. This is the default value.
	 * @see #setRules(java.lang.String)
	 */
	public static final String RULES_NONE = "none";
	/**
	 * Rules will appear between row groups (see THEAD, TFOOT, and TBODY) and column groups (see COLGROUP and COL) only.
	 * @see #setRules(java.lang.String)
	 */
	public static final String RULES_GROUPS = "groups";
	/**
	 * Rules will appear between rows only.
	 * @see #setRules(java.lang.String)
	 */
	public static final String RULES_ROWS = "rows";
	/**
	 * Rules will appear between columns only.
	 * @see #setRules(java.lang.String)
	 */
	public static final String RULES_COLUMNS = "cols";
	/**
	 * Rules will appear between all rows and columns.
	 * @see #setRules(java.lang.String)
	 */
	public static final String RULES_ALL = "all";

	private static final String MARKUP_ATTRIBUTE_BORDER = "border";
  private static final String MARKUP_ATTRIBUTE_CELL_PADDING = "cellpadding";
  private static final String MARKUP_ATTRIBUTE_CELL_SPACING = "cellspacing";
  private static final String MARKUP_ATTRIBUTE_FRAME = "frame";
  private static final String MARKUP_ATTRIBUTE_RULES = "rules";
	private static final String MARKUP_ATTRIBUTE_SUMMARY = "summary";
	private static final String MARKUP_ATTRIBUTE_WIDTH = "width";
  
	public static final String MARKUP_ATTRIBUTE_CELL_HORIZONTAL_ALIGNMENT = "align";
	public static final String MARKUP_ATTRIBUTE_CELL_VERTICAL_ALIGNMENT = "valign";
	public static final String MARKUP_ATTRIBUTE_CHARACTER = "char";
	public static final String MARKUP_ATTRIBUTE_CHARACTER_OFFSET = "charoff";

	/**
	 * Creates a <code>TableCaption</code> within the <code>Table2</code> object.
	 * Only one <code>TableCaption</code> can exist for each <code>Table2</code> object so the already created one is returned if it already exists.
	 * 
	 * @param caption	The string to use as the caption for the table
	 * @return A new <code>TableCaption</code> object or the one already created.
	 */
	public TableCaption createCaption(String caption) {
		TableCaption tableCaption = getCaption();
		if (tableCaption == null) {
			tableCaption = new TableCaption();
			getChildren().add(tableCaption);
		}
		if (caption != null) {
			tableCaption.setCaption(caption);
		}
		return tableCaption;
	}
	
	/**
	 * Creates a <code>TableCaption</code> (with no text) within the <code>Table2</code> object.
	 * 
	 * @return A new <code>TableCaption</code> object or the one already created.
	 */
	public TableCaption createCaption() {
		return createCaption(null);
	}
	
	/**
	 * Gets the <code>TableCaption</code> object associated with the <code>Table2</code> object.
	 * 
	 * @return The <code>TableCaption</code> already created or null if none exists.
	 */
	private TableCaption getCaption() {
		Collection children = getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			UIComponent element = (UIComponent) iter.next();
			if (element instanceof TableCaption) {
				return (TableCaption) element;
			}
		}
		return null;
	}
	
	/**
	 * Creates a new <code>TableColumnGroup</code> within the <code>Table2</code> object.
	 * 
	 * @return A new <code>TableColumnGroup</code> object.
	 */
	public TableColumnGroup createColumnGroup() {
		TableColumnGroup group = new TableColumnGroup();
		getChildren().add(group);
		
		return group;
	}
	
	/**
	 * Creates a <code>TableHeaderRowGroup</code> within the <code>Table2</code> object.
	 * Only one <code>TableHeaderRowGroup</code> can exist for each <code>Table2</code> object so the already created one is returned if it already exists.
	 * 
	 * @return A new <code>TableHeaderRowGroup</code> object or the one already created.
	 */
	public TableHeaderRowGroup createHeaderRowGroup() {
		TableHeaderRowGroup rowGroup = getHeaderRowGroup();
		if (rowGroup == null) {
			rowGroup = new TableHeaderRowGroup();
			getChildren().add(rowGroup);
		}
		return rowGroup;
	}
	
	private TableHeaderRowGroup getHeaderRowGroup() {
		Collection children = getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			UIComponent element = (UIComponent) iter.next();
			if (element instanceof TableHeaderRowGroup) {
				return (TableHeaderRowGroup) element;
			}
		}
		return null;
	}
	
	/**
	 * Creates a <code>TableFooterRowGroup</code> within the <code>Table2</code> object.
	 * Only one <code>TableFooterRowGroup</code> can exist for each <code>Table2</code> object so the already created one is returned if it already exists.
	 * 
	 * @return A new <code>TableFooterRowGroup</code> object or the one already created.
	 */
	public TableFooterRowGroup createFooterRowGroup() {
		TableFooterRowGroup rowGroup = getFooterRowGroup();
		if (rowGroup == null) {
			rowGroup = new TableFooterRowGroup();
			getChildren().add(rowGroup);
		}
		return rowGroup;
	}
	
	private TableFooterRowGroup getFooterRowGroup() {
		Collection children = getChildren();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			UIComponent element = (UIComponent) iter.next();
			if (element instanceof TableFooterRowGroup) {
				return (TableFooterRowGroup) element;
			}
		}
		return null;
	}
	
	/**
	 * Creates a new <code>TableBodyRowGroup</code> within the <code>Table2</code> object.
	 * 
	 * @return A new <code>TableBodyRowGroup</code> object.
	 */
	public TableBodyRowGroup createBodyRowGroup() {
		TableBodyRowGroup rowGroup = new TableBodyRowGroup();
		getChildren().add(rowGroup);
		
		return rowGroup;
	}
	
	/**
	 * Creates a <code>TableRow</code> within the <code>Table2</code> object.
	 * 
	 * @return A new <code>TableRow</code> object.
	 */
	public TableRow createRow() {
		TableRow row = new TableRow();
		getChildren().add(row);
		
		return row;
	}
	
	/**
	 * Creates a <code>TableRow</code> within the <code>Table2</code> object with the specified index (row number).
	 * 
	 * @param	The index (row number) of the new <code>TableRow</code>.
	 * @return A new <code>TableRow</code> object.
	 */
	public TableRow createRow(int index) {
		TableRow row = new TableRow();
		getChildren().add(index, row);
		
		return row;
	}
	
	/**
	 * Gets the <code>TableRow</code> object with the specified index (row number).
	 * 
	 * @param	The index (row number) of the new <code>TableRow</code>.
	 * @return The <code>TableRow</code> object with the given index (or null if it doesn't exist).
	 */
	public TableRow getRow(int index) {
		return (TableRow) getChildren().get(index);
	}
	
	//Getters
	/**
	 * Gets the border value set for the table.
	 * 
	 * @return The border value set for the table.  Returns the default value (0) if not set.
	 */
  public int getBorder() {
  		String border = getMarkupAttribute(MARKUP_ATTRIBUTE_BORDER);
  		if (border != null) {
  			return Integer.parseInt(border);
  		}
  		return 0;
  }

	/**
	 * Gets the cell padding value set for the table.
	 * 
	 * @return The cell padding value set for the table.  Returns the default value (1) if not set.
	 */
  public int getCellpadding() {
		String border = getMarkupAttribute(MARKUP_ATTRIBUTE_CELL_PADDING);
		if (border != null) {
			return Integer.parseInt(border);
		}
		return 1;
  }

	/**
	 * Gets the cell spacing value set for the table.
	 * 
	 * @return The cell spacing value set for the table.  Returns the default value (2) if not set.
	 */
  public int getCellspacing() {
		String border = getMarkupAttribute(MARKUP_ATTRIBUTE_CELL_SPACING);
		if (border != null) {
			return Integer.parseInt(border);
		}
		return 2;
  }

	/**
	 * Gets the frame value set for the table.
	 * 
	 * @return The frame value set for the table.  Returns the default value if not set.
	 */
  public String getFrame() {
		String frame = getMarkupAttribute(MARKUP_ATTRIBUTE_FRAME);
		if (frame == null) {
			if (getBorder() > 0) {
				return FRAME_BORDER;
			}
			else {
				return FRAME_VOID;
			}
		}
		return frame;
  }

	/**
	 * Gets the rules value set for the table.
	 * 
	 * @return The rules value set for the table.  Returns the default value if not set.
	 */
  public String getRules() {
		String rules = getMarkupAttribute(MARKUP_ATTRIBUTE_RULES);
		if (rules == null) {
			if (getBorder() > 0) {
				return RULES_ALL;
			}
			else {
				return RULES_NONE;
			}
		}
		return rules;
  }

	/**
	 * Gets the summary value set for the table.
	 * 
	 * @return The summary value set for the table.  Returns null if not set.
	 */
  public String getSummary() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_SUMMARY);
  }

	/**
	 * Gets the width value set for the table.
	 * 
	 * @return The width value set for the table.  Returns null if not set.
	 */
  public String getWidth() {
		return getMarkupAttribute(MARKUP_ATTRIBUTE_WIDTH);
  }
	
	//Setters
  /**
   * Sets the border value of the table.  The number must be an integer greater than or equal to 0.
   * 
   * @param border	The border value to set.
   */
  public void setBorder(int border) {
  		if (border < 0) {
  			throw new IllegalArgumentException("Border value must be an integer greater than or equal to 0.");
  		}
		setMarkupAttribute(MARKUP_ATTRIBUTE_BORDER, border);
  }

  /**
   * Sets the cell padding value of the table.  The number must be an integer greater than or equal to 0.
   * 
   * @param cellpadding	The cell padding value to set.
   */
  public void setCellpadding(int cellpadding) {
		if (cellpadding < 0) {
			throw new IllegalArgumentException("Cell padding value must be an integer greater than or equal to 0.");
		}
		setMarkupAttribute(MARKUP_ATTRIBUTE_CELL_PADDING, cellpadding);
  }

  /**
   * Sets the cell spacing value of the Ttable.  The number must be an integer greater than or equal to 0.
   * 
   * @param cellspacing	The cell spacing value to set.
   */
  public void setCellspacing(int cellspacing) {
		if (cellspacing < 0) {
			throw new IllegalArgumentException("Cell padding value must be an integer greater than or equal to 0.");
		}
		setMarkupAttribute(MARKUP_ATTRIBUTE_CELL_SPACING, cellspacing);
  }

  /**
   * Sets the frame value of the table.
   * 
   * @param frame	The frame value to set.
   */
  public void setFrame(String frame) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_FRAME, frame);
  }

  /**
   * Sets the rules value of the table.
   * 
   * @param rules	The rules value to set.
   */
  public void setRules(String rules) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_RULES, rules);
  }

  /**
   * Sets the summary value of the table.
   * 
   * @param summary	The summary value to set.
   */
  public void setSummary(String summary) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_SUMMARY, summary);
}

  /**
   * Sets the width value of the table.  The value must be either a number (150) or a percentage (66%).
   * 
   * @param width	The width value to set.
   */
  public void setWidth(String width) {
		setMarkupAttribute(MARKUP_ATTRIBUTE_WIDTH, width);
  }

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			println("<table" + getMarkupAttributesString() + ">");

			List theObjects = getChildren();
			Collections.sort(theObjects, new TableElementComparator());
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					renderChild(iwc,item);
				}
			}
			
			println("</table>");
		}
	}
	
	public void encodeBegin(FacesContext context) throws IOException {
		print("<table" + getMarkupAttributesString() + ">");
	}

	public void encodeEnd(FacesContext arg0) throws IOException {
		println("</table>");
	}

	public class TableElementComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			UIComponent obj1 = (UIComponent) o1;
			UIComponent obj2 = (UIComponent) o2;
			
			if (obj1 instanceof TableCaption) {
				return -1;
			}
			else if (obj2 instanceof TableCaption) {
				return 1;
			}
			
			if (obj1 instanceof TableColumnGroup) {
				return -1;
			}
			else if (obj2 instanceof TableColumnGroup) {
				return 1;
			}
			
			if (obj1 instanceof TableHeaderRowGroup) {
				return -1;
			}
			else if (obj2 instanceof TableHeaderRowGroup) {
				return 1;
			}
			
			if (obj1 instanceof TableFooterRowGroup) {
				return -1;
			}
			else if (obj2 instanceof TableFooterRowGroup) {
				return 1;
			}
			
			return 0;
		}
	}
}
package com.idega.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.data.IBPage;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a> & <a href="mailto:laddi@idega.is">Thorhallur Helgason</a>
 * @version 1.0
 */

public class NavigationJSMenu extends Block {

	protected int cols = 0;
	protected int rows = 0;

	private int rootNode = -1;
	private String linkStyle = "FONT-FAMILY: Verdana,Arial,sans-serif;FONT-SIZE: 10px;FONT-WEIGHT: BOLD;TEXT-DECORATION:none;COLOR:#000000;";
	private String linkHoverStyle = "COLOR:#FF0000;";

	private int xPosition = 0;
	private int yPosition = 0;
	private String backgroundColor = "#FFFFFF";
	private String borderColor = "#000000";
	private int borderWidth = 1;
	private int itemHeight = 20;
	private int xOffset = -1;
	private int yOffset = 1;
	private int itemWidth = 120;
	
	private Map menuWidth;
	private Map menuXPosition;
	private Map menuYPosition;
	
	private int row = 0;
	
	public NavigationJSMenu() {
	}

	public void main(IWContext iwc) {
		if (!iwc.isInEditMode()) {
			if (rootNode == -1) {
				rootNode = BuilderLogic.getStartPageId(iwc);
			}
			
			getParentPage().setStyleDefinition("A.subMenu", linkStyle);
			getParentPage().setStyleDefinition("A.subMenu:hover", linkHoverStyle);

			StringBuffer buffer = new StringBuffer();
			buffer.append("var initX  = ").append(xPosition).append(";").append("\n");
			buffer.append("var initY  = ").append(yPosition).append(";").append("\n");
			buffer.append("var backColor  = '").append(backgroundColor).append("';").append("\n");
			buffer.append("var borderColor  = '").append(borderColor).append("';").append("\n");
			buffer.append("var borderSize  = '").append(borderWidth).append("';").append("\n");
			buffer.append("var itemHeight  = ").append(itemHeight).append(";").append("\n");
			buffer.append("var xOverlap  = ").append(xOffset).append(";").append("\n");
			buffer.append("var yOverlap  = ").append(yOffset).append(";").append("\n\n");
			buffer.append("menuContent = new Array ();\n\n");
			
			PageTreeNode node = new PageTreeNode(rootNode, iwc);
			Iterator iterator = node.getChildren();
			row = 0;
			int parentRow = -1;
			while (iterator.hasNext()) {
				parentRow = -1;
				PageTreeNode child = (PageTreeNode) iterator.next();
				addRow(iwc, parentRow, 0, child, buffer);
			}
			buffer.append("\n").append("createMenuTree();").append("\n\n");
			
			Script script = getParentPage().getAssociatedScript();
			script.addFunction("menuScript", buffer.toString());

			getParentPage().addJavascriptURL(iwc.getApplication().getBundle("com.idega.core").getResourcesVirtualPath() + "/navigation_menu/menu.js");

		}
	}
	
	public void addRow(IWContext iwc, int parentRow, int subRow, PageTreeNode parent, StringBuffer buffer) {
		int subSubRow = 0;
		if (parent.getChildCount() > 0) {
			buffer.append("menuContent [").append(row++).append("] = new Array(");
			buffer.append(parentRow).append(",");
			if (parentRow == -1)
				buffer.append(0).append(",");
			else
				buffer.append(subRow).append(",");
			
			if (menuWidth != null && menuWidth.get(new Integer(row - 1)) != null)
				buffer.append(menuWidth.get(new Integer(row - 1))).append(",");
			else
				buffer.append(itemWidth).append(",");
	
			if (menuXPosition != null && menuXPosition.get(new Integer(row - 1)) != null)
				buffer.append(menuXPosition.get(new Integer(row - 1))).append(",");
			else
				buffer.append(-1).append(",");
	
			if (menuYPosition != null && menuYPosition.get(new Integer(row - 1)) != null)
				buffer.append(menuYPosition.get(new Integer(row - 1))).append(",");
			else
				buffer.append(-1).append(",");
	
			buffer.append("new Array(");
			
			Iterator iterator = parent.getChildren();
			while (iterator.hasNext()) {
				PageTreeNode grandChild = (PageTreeNode) iterator.next();
				buffer.append("'").append(grandChild.getLocalizedNodeName(iwc)).append("',");
				buffer.append("'").append(BuilderLogic.getInstance().getIBPageURL(iwc, grandChild.getNodeID())).append("'");
				if (iterator.hasNext())
					buffer.append(",");
				else
					buffer.append(")");
			}
			buffer.append(");").append("\n");
	
			Iterator iterator2 = parent.getChildren();
			while (iterator2.hasNext()) {
				addRow(iwc, (row - 1), subSubRow, (PageTreeNode) iterator2.next(), buffer);
				subSubRow++;
			}
		}
	}

	public void setMenuWidth(int menu, int width) {
		setMenuAttributes(menu, width, -1, -1);
	}
	
	public void setMenuAttributes(int menu, int width, int xPosition, int yPosition) {
		if (menuWidth == null) {
			menuWidth = new HashMap();
			menuXPosition = new HashMap();
			menuYPosition = new HashMap();
		}
		menuWidth.put(new Integer(menu), String.valueOf(width));
		menuXPosition.put(new Integer(menu), String.valueOf(xPosition));
		menuYPosition.put(new Integer(menu), String.valueOf(yPosition));
	}
	
	public void setRootNode(IBPage page) {
		rootNode = page.getID();
	}

	public void setRootNode(int rootId) {
		rootNode = rootId;
	}
	/**
	 * @param string
	 */
	public void setBackgroundColor(String string) {
		backgroundColor = string;
	}

	/**
	 * @param string
	 */
	public void setBorderColor(String string) {
		borderColor = string;
	}

	/**
	 * @param i
	 */
	public void setBorderWidth(int i) {
		borderWidth = i;
	}

	/**
	 * @param i
	 */
	public void setItemHeight(int i) {
		itemHeight = i;
	}

	/**
	 * @param i
	 */
	public void setItemWidth(int i) {
		itemWidth = i;
	}

	/**
	 * @param string
	 */
	public void setLinkHoverStyle(String string) {
		linkHoverStyle = string;
	}

	/**
	 * @param string
	 */
	public void setLinkStyle(String string) {
		linkStyle = string;
	}

	/**
	 * @param i
	 */
	public void setXOffset(int i) {
		xOffset = i;
	}

	/**
	 * @param i
	 */
	public void setXPosition(int i) {
		xPosition = i;
	}

	/**
	 * @param i
	 */
	public void setYOffset(int i) {
		yOffset = i;
	}

	/**
	 * @param i
	 */
	public void setYPosition(int i) {
		yPosition = i;
	}

}
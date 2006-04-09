package com.idega.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;

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

	public void main(IWContext iwc) throws Exception {
		if (!iwc.isInEditMode()) {
			BuilderService bservice = getBuilderService(iwc);
			if (this.rootNode == -1) {
				this.rootNode = bservice.getRootPageId();
			}
			
			getParentPage().setStyleDefinition("A.subMenu", this.linkStyle);
			getParentPage().setStyleDefinition("A.subMenu:hover", this.linkHoverStyle);

			StringBuffer buffer = new StringBuffer();
			buffer.append("var initX  = ").append(this.xPosition).append(";").append("\n");
			buffer.append("var initY  = ").append(this.yPosition).append(";").append("\n");
			buffer.append("var backColor  = '").append(this.backgroundColor).append("';").append("\n");
			buffer.append("var borderColor  = '").append(this.borderColor).append("';").append("\n");
			buffer.append("var borderSize  = '").append(this.borderWidth).append("';").append("\n");
			buffer.append("var itemHeight  = ").append(this.itemHeight).append(";").append("\n");
			buffer.append("var xOverlap  = ").append(this.xOffset).append(";").append("\n");
			buffer.append("var yOverlap  = ").append(this.yOffset).append(";").append("\n\n");
			buffer.append("menuContent = new Array ();\n\n");
			
			int userId = -1;
			try{
				userId = iwc.getCurrentUserId();
			}
			catch(NotLoggedOnException nle){}
			ICTreeNode node = bservice.getPageTree(this.rootNode,userId);
			Iterator iterator = node.getChildrenIterator();
			this.row = 0;
			int parentRow = -1;
			while (iterator.hasNext()) {
				parentRow = -1;
				ICTreeNode child = (ICTreeNode) iterator.next();
				addRow(iwc, parentRow, 0, child, buffer);
			}
			buffer.append("\n").append("createMenuTree();").append("\n\n");
			
			Script script = getParentPage().getAssociatedScript();
			script.addFunction("menuScript", buffer.toString());

			getParentPage().addJavascriptURL(iwc.getIWMainApplication().getBundle("com.idega.core").getResourcesVirtualPath() + "/navigation_menu/menu.js");

		}
	}
	
	public void addRow(IWContext iwc, int parentRow, int subRow, ICTreeNode parent, StringBuffer buffer) throws Exception{
		int subSubRow = 0;
		if (parent.getChildCount() > 0) {
			BuilderService bs = getBuilderService(iwc);
			buffer.append("menuContent [").append(this.row++).append("] = new Array(");
			buffer.append(parentRow).append(",");
			if (parentRow == -1) {
				buffer.append(0).append(",");
			}
			else {
				buffer.append(subRow).append(",");
			}
			
			if (this.menuWidth != null && this.menuWidth.get(new Integer(this.row - 1)) != null) {
				buffer.append(this.menuWidth.get(new Integer(this.row - 1))).append(",");
			}
			else {
				buffer.append(this.itemWidth).append(",");
			}
	
			if (this.menuXPosition != null && this.menuXPosition.get(new Integer(this.row - 1)) != null) {
				buffer.append(this.menuXPosition.get(new Integer(this.row - 1))).append(",");
			}
			else {
				buffer.append(-1).append(",");
			}
	
			if (this.menuYPosition != null && this.menuYPosition.get(new Integer(this.row - 1)) != null) {
				buffer.append(this.menuYPosition.get(new Integer(this.row - 1))).append(",");
			}
			else {
				buffer.append(-1).append(",");
			}
	
			buffer.append("new Array(");
			
			Iterator iterator = parent.getChildrenIterator();
			while (iterator.hasNext()) {
				ICTreeNode grandChild = (ICTreeNode) iterator.next();
				buffer.append("'").append(grandChild.getNodeName(iwc.getCurrentLocale())).append("',");
				buffer.append("'").append(bs.getPageURI(grandChild.getNodeID())).append("'");
				if (iterator.hasNext()) {
					buffer.append(",");
				}
				else {
					buffer.append(")");
				}
			}
			buffer.append(");").append("\n");
	
			Iterator iterator2 = parent.getChildrenIterator();
			while (iterator2.hasNext()) {
				addRow(iwc, (this.row - 1), subSubRow, (ICTreeNode) iterator2.next(), buffer);
				subSubRow++;
			}
		}
	}

	public void setMenuWidth(int menu, int width) {
		setMenuAttributes(menu, width, -1, -1);
	}
	
	public void setMenuAttributes(int menu, int width, int xPosition, int yPosition) {
		if (this.menuWidth == null) {
			this.menuWidth = new HashMap();
			this.menuXPosition = new HashMap();
			this.menuYPosition = new HashMap();
		}
		this.menuWidth.put(new Integer(menu), String.valueOf(width));
		this.menuXPosition.put(new Integer(menu), String.valueOf(xPosition));
		this.menuYPosition.put(new Integer(menu), String.valueOf(yPosition));
	}
	
	public void setRootNode(ICPage page) {
		this.rootNode = page.getID();
	}

	public void setRootNode(int rootId) {
		this.rootNode = rootId;
	}
	/**
	 * @param string
	 */
	public void setBackgroundColor(String string) {
		this.backgroundColor = string;
	}

	/**
	 * @param string
	 */
	public void setBorderColor(String string) {
		this.borderColor = string;
	}

	/**
	 * @param i
	 */
	public void setBorderWidth(int i) {
		this.borderWidth = i;
	}

	/**
	 * @param i
	 */
	public void setItemHeight(int i) {
		this.itemHeight = i;
	}

	/**
	 * @param i
	 */
	public void setItemWidth(int i) {
		this.itemWidth = i;
	}

	/**
	 * @param string
	 */
	public void setLinkHoverStyle(String string) {
		this.linkHoverStyle = string;
	}

	/**
	 * @param string
	 */
	public void setLinkStyle(String string) {
		this.linkStyle = string;
	}

	/**
	 * @param i
	 */
	public void setXOffset(int i) {
		this.xOffset = i;
	}

	/**
	 * @param i
	 */
	public void setXPosition(int i) {
		this.xPosition = i;
	}

	/**
	 * @param i
	 */
	public void setYOffset(int i) {
		this.yOffset = i;
	}

	/**
	 * @param i
	 */
	public void setYPosition(int i) {
		this.yPosition = i;
	}

}
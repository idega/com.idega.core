package com.idega.presentation;

import java.util.Iterator;
import java.util.Vector;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * Title: Description: Copyright: Copyright (c) 2000-2001 idega.is All Rights
 * Reserved Company: idega
 * 
 * @author <a href="mailto:aron@idega.is">Aron Birkir </a>& <a
 *         href="mailto:laddi@idega.is">Thorhallur Helgason </a>
 * @version 1.0
 */
public class NavigationPulldownMenu extends Block {

	private int rootNode = -1;
	private boolean withRootAsHome = false;
	private boolean rootLinks = true;
	private boolean left = false;
	private String _fontStyle = "FONT-FAMILY: Verdana,Arial,sans-serif;FONT-SIZE: 10px;FONT-WEIGHT: BOLD;TEXT-DECORATION:none;COLOR:#FFFFFF;";

	public NavigationPulldownMenu() {
	}

	public void main(IWContext iwc) throws Exception {
		if (!iwc.isInEditMode()) {
			BuilderService bservice = getBuilderService(iwc);
			if (rootNode == -1) {
				rootNode = bservice.getRootPageId();
			}
			int currentUserId = -1;
			try {
				currentUserId = iwc.getCurrentUserId();
			} catch (NotLoggedOnException nle) {
			}
			ICTreeNode node = bservice.getPageTree(rootNode, currentUserId);
			if (iwc.isIE()) {
				getParentPage().setOnLoad("InitMenu()");
				getParentPage().setMarkupAttribute("onClick", "HideMenu(menuBar)");
				getParentPage().setID("Bdy");
			}
			getParentPage().addStyleSheetURL(
					iwc.getIWMainApplication().getBundle("com.idega.core").getResourcesVirtualPath()
							+ "/navigation_menu/CascadeMenu.css");
			getParentPage().addJavascriptURL(
					iwc.getIWMainApplication().getBundle("com.idega.core").getResourcesVirtualPath()
							+ "/navigation_menu/CascadeMenu.js");
			Vector nodeVector = new Vector();
			if (withRootAsHome && left) {
				nodeVector.add(node);
				withRootAsHome = false;
			}
			Iterator iter = node.getChildrenIterator();
			while (iter.hasNext())
				nodeVector.add((ICTreeNode) iter.next());
			if (withRootAsHome && !left)
				nodeVector.add(node);
			Table table = new Table();
			table.setCellpaddingAndCellspacing(0);
			add(table);
			Layer layer = new Layer(Layer.DIV);
			layer.setID("menuBar");
			layer.setMarkupAttribute("class", "menuBar");
			if (iwc.isIE()) {
				table.add(layer);
				table.add(Text.NON_BREAKING_SPACE);
			}
			Iterator iterator = nodeVector.iterator();
			int column = 1;
			while (iterator.hasNext()) {
				ICTreeNode n = (ICTreeNode) iterator.next();
				Layer subLayer = new Layer(Layer.DIV);
				subLayer.setID("page" + String.valueOf(n.getNodeID()));
				subLayer.setMarkupAttribute("class", "Bar");
				subLayer.setMarkupAttribute("title", n.getNodeName(iwc.getCurrentLocale()));
				subLayer.setMarkupAttribute("cmd", bservice.getPageURI(n.getNodeID()));
				if (rootLinks) {
					Link link = new Link(n.getNodeName(iwc.getCurrentLocale()));
					link.setPage(n.getNodeID());
					link.setFontStyle(_fontStyle);
					subLayer.add(link);
				} else {
					subLayer.add(n.getNodeName(iwc.getCurrentLocale()));
				}
				if (iwc.isIE()) {
					if (n.getChildCount() > 0 && n.getNodeID() != rootNode) {
						subLayer.setMarkupAttribute("menu", "menu" + String.valueOf(n.getNodeID()));
						addSubMenu(iwc, table, n);
					}
					layer.add(subLayer);
				} else {
					table.add(subLayer, column++, 1);
				}
			}
		}
	}

	private void addSubMenu(IWContext iwc, Table table, ICTreeNode node) throws Exception {
		BuilderService bservice = getBuilderService(iwc);
		Layer layer = new Layer(Layer.DIV);
		layer.setID("menu" + String.valueOf(node.getNodeID()));
		layer.setMarkupAttribute("class", "menu");
		table.add(layer);
		Iterator iterator = node.getChildrenIterator();
		while (iterator.hasNext()) {
			ICTreeNode n = (ICTreeNode) iterator.next();
			Layer subLayer = new Layer(Layer.DIV);
			subLayer.setID("page" + String.valueOf(n.getNodeID()));
			subLayer.setMarkupAttribute("class", "menuItem");
			subLayer.setMarkupAttribute("title", n.getNodeName(iwc.getCurrentLocale()));
			subLayer.setMarkupAttribute("cmd", bservice.getPageURI(n.getNodeID()));
			subLayer.add(n.getNodeName(iwc.getCurrentLocale()));
			layer.add(subLayer);
			if (n.getChildCount() > 0) {
				subLayer.setMarkupAttribute("menu", "menu" + String.valueOf(n.getNodeID()));
				addSubMenu(iwc, table, n);
			}
		}
	}

	public void setRootNode(ICPage page) {
		rootNode = page.getID();
	}

	public void setRootNode(int rootId) {
		rootNode = rootId;
	}

	public void setRootAsHome(boolean rootAsHome) {
		withRootAsHome = rootAsHome;
	}

	public void setHomeAtLeft(boolean homeAtLeft) {
		left = homeAtLeft;
	}

	public void setFontStyle(String style) {
		_fontStyle = style;
	}

	public void setRootsAsLinks(boolean rootsAsLinks) {
		rootLinks = rootsAsLinks;
	}
}

package com.idega.presentation;

import java.util.Iterator;
import java.util.Vector;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.data.IBPage;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a> & <a href="mailto:laddi@idega.is">Thorhallur Helgason</a>
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

  public void main(IWContext iwc){
    if ( !iwc.isInEditMode() ) {
      if ( rootNode == -1 ) {
	rootNode = BuilderLogic.getStartPageId(iwc);
      }

      PageTreeNode node = new PageTreeNode(rootNode, iwc);

      if ( iwc.isIE() ) {
	getParentPage().setOnLoad("InitMenu()");
	getParentPage().setAttribute("onClick","HideMenu(menuBar)");
	getParentPage().setID("Bdy");
      }
      getParentPage().addStyleSheetURL(iwc.getApplication().getBundle("com.idega.core").getResourcesVirtualPath()+"/navigation_menu/CascadeMenu.css");
      getParentPage().addJavascriptURL(iwc.getApplication().getBundle("com.idega.core").getResourcesVirtualPath()+"/navigation_menu/CascadeMenu.js");

      Vector nodeVector = new Vector();
      if ( withRootAsHome && left ) {
	nodeVector.add(node);
	withRootAsHome = false;
      }
      Iterator iter = node.getChildren();
      while (iter.hasNext())
	nodeVector.add((PageTreeNode) iter.next());
      if ( withRootAsHome && !left )
	nodeVector.add(node);

      Table table = new Table();
	table.setCellpaddingAndCellspacing(0);
      add(table);

      Layer layer = new Layer(Layer.DIV);
	layer.setID("menuBar");
	layer.setAttribute("class","menuBar");
	layer.setNoStyle(true);
      if ( iwc.isIE() ) {
	table.add(layer);
	table.add(Text.NON_BREAKING_SPACE);
      }

      Iterator iterator = nodeVector.iterator();
      int column = 1;
      while (iterator.hasNext()) {
	PageTreeNode n = (PageTreeNode) iterator.next();
	Layer subLayer = new Layer(Layer.DIV);
	  subLayer.setID("page"+String.valueOf(n.getNodeID()));
	  subLayer.setAttribute("class","Bar");
	  subLayer.setAttribute("title",n.getLocalizedNodeName(iwc));
	  subLayer.setAttribute("cmd",BuilderLogic.getInstance().getIBPageURL(iwc,n.getNodeID()));
	  subLayer.setNoStyle(true);

	if ( rootLinks ) {
	  Link link = new Link(n.getLocalizedNodeName(iwc));
	    link.setPage(n.getNodeID());
	    link.setFontStyle(_fontStyle);
	  subLayer.add(link);
	}
	else {
	  subLayer.add(n.getLocalizedNodeName(iwc));
	}

	if ( iwc.isIE() ) {
	  if ( n.getChildCount() > 0 && n.getNodeID() != rootNode ) {
	    subLayer.setAttribute("menu","menu"+String.valueOf(n.getNodeID()));
	    addSubMenu(iwc,table,n);
	  }
	  layer.add(subLayer);
	}
	else {
	  table.add(subLayer,column++,1);
	}
      }
    }
  }

  private void addSubMenu(IWContext iwc,Table table,PageTreeNode node) {
    Layer layer = new Layer(Layer.DIV);
      layer.setID("menu"+String.valueOf(node.getNodeID()));
      layer.setAttribute("class","menu");
      layer.setNoStyle(true);
    table.add(layer);

    Iterator iterator = node.getChildren();
    while (iterator.hasNext()) {
      PageTreeNode n = (PageTreeNode) iterator.next();
      Layer subLayer = new Layer(Layer.DIV);
	subLayer.setID("page"+String.valueOf(n.getNodeID()));
	subLayer.setAttribute("class","menuItem");
	subLayer.setAttribute("title",n.getLocalizedNodeName(iwc));
	subLayer.setAttribute("cmd",BuilderLogic.getInstance().getIBPageURL(iwc,n.getNodeID()));
	subLayer.setNoStyle(true);
	subLayer.add(n.getLocalizedNodeName(iwc));
      layer.add(subLayer);

      if ( n.getChildCount() > 0 ) {
	subLayer.setAttribute("menu","menu"+String.valueOf(n.getNodeID()));
	addSubMenu(iwc,table,n);
      }
    }
  }

  public  void setRootNode(IBPage page){
    rootNode = page.getID();
  }

  public void setRootNode(int rootId){
    rootNode  = rootId;
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

/*
 * $Id: ViewNode.java,v 1.1 2004/11/14 23:24:47 tryggvil Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.Locale;
import javax.faces.application.ViewHandler;


/**
 * Base interface for "view nodes".<br>
 * A view node is a node in a hierarchial (tree) structure that represents both URLs and references to
 * UserInterface functions.<br>
 * There is an instance of a view node for each part of a URL structure and is separated by the '/' character.
 * So that for example in the URL "/myapp/workspace/builder" there is one ViewNode instance for both
 * 'workspace' and 'builder parts of the URL. <br>
 * ViewNodes are accessed and managed by the ViewManager instance.
 * 
 *  Last modified: $Date: 2004/11/14 23:24:47 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface ViewNode {
	
	//ViewNode tree strucuture
	public String getViewId();
	public void addChildViewNode(ViewNode node);
	public Collection getChildren();
	/**
	 * Returns the primary URI up the tree hierarchy and includes the webapplications context path if any.
	 */
	public String getURI();
	/**
	 * This method returns the child ViewNode instance hierarchially down in the tree from this node.<br>
	 * The '/' character acts as a separator. This means that the value 'idegaweb' will try tro return the direct child of this node. 
	 * The value 'idegaweb/login' will try to get the child of with id 'login' from the child 'idegaweb' of this node.<br>
	 * The special value '/' will return this node instance and otherwise the '/' characters in the beginning and end of the string are stripped away.
	 * 
	 * @param childViewId
	 * @return The child node found under this node or null if nothing found.
	 */
	public ViewNode getChild(String childViewId);
	public ViewNode getParent();
	
	//ViewHandler/JSF properties
	public ViewHandler getViewHandler();
	public boolean isJSP();
	public boolean isCBP();
	public String getJSPURI();
	public Class getComponentClass();
	
	//Accesscontrol properties
	public Collection getAuthorizedRoles();
	
	//UI properties
	public Icon getIcon();
	public String getName();
	public String getLocalizedName(Locale locale);
	public KeyboardShortcut getKeyboardShortcut();
	public ToolTip getToolTip(Locale locale);
	
}

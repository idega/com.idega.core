/*
 * $Id: ViewNode.java,v 1.9 2007/07/27 15:43:48 civilis Exp $
 * Created on 2.9.2004
 *
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import java.util.Collection;
import java.util.Locale;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * <p>
 * Base interface for "view nodes".<br>
 * A view node is a node in a hierarchial (tree) structure that represents both URLs and references to
 * UserInterface functions.<br>
 * There is an instance of a view node for each part of a URL structure and is separated by the '/' character.
 * So that for example in the URL "/myapp/workspace/builder" there is one ViewNode instance for both
 * 'workspace' and 'builder parts of the URL. <br>
 * ViewNodes are accessed and managed by the ViewManager instance.<br>
 * </p>
 *  Last modified: $Date: 2007/07/27 15:43:48 $ by $Author: civilis $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.9 $
 */
public interface ViewNode {
	
	//ViewNode tree strucuture
	/**
	 * Gets the ViewId of this node.<br>
	 * This node must return an id that is unique within this node's parent.
	 */
	public String getViewId();
	/**
	 * Adds a child under this node.
	 * @param node
	 */
	public void addChildViewNode(ViewNode node);
	/**
	 * Gets the children of this Node.<br>
	 * This should be a Collection of ViewNode instances.
	 * @return
	 */
	public Collection getChildren();
	
	/**
	 * Returns the primary URI up the tree hierarchy and does NOT include the webapplications context path if any.
	 */
	public String getURI();
	
	/**
	 * Returns the primary URI up the tree hierarchy and includes the webapplications context path if any.
	 */
	public String getURIWithContextPath();
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
	public void setParent(ViewNode parent);
	//ViewHandler/JSF properties
	public ViewHandler getViewHandler();
	
	/**
	 * @return Returns what the ViewNode is based upon (e.g. jsp, facelet, or component) 
	 */
	public abstract ViewNodeBase getViewNodeBase();
	
	/**
	 * Returns true if the node represents a component (UIComponent) and will be created with createComponent() 
	 * typically called from a ViewHandler.
	 * A viewNode can not be both resource based or component based at the same time.
	 * @deprecated - use getViewNodeBase instead
	 * @return
	 */
	@Deprecated
	public boolean isComponentBased();
	/**
	 * Gets the URI to the resource that this node is a virtual node for, this is only applicable if the node is ResourceBased.
	 * @see ViewNode.isResourceBased() 
	 * @return the URI
	 */
	public String getResourceURI();
	//public Class getComponentClass();
	/**
	 * Creates a new instance of the component for this node. This is only applicable if the node is ComponentBased.
	 */
	public UIComponent createComponent(FacesContext context);
	
	
	//Accesscontrol properties
	/**
	 * Method that returns a collection of roles needed to access this ViewNode.<br>
	 * This should be a collection of Strings.
	 */
	public Collection getAuthorizedRoles();
	
	//UI properties
	public Icon getIcon();
	public String getName();
	public String getLocalizedName(Locale locale);
	public KeyboardShortcut getKeyboardShortcut();
	public ToolTip getToolTip(Locale locale);
	
	/**
	 * Controls wheather the node will be rendered in User Interface, menus and such.
	 * @return
	 */
	public boolean isVisibleInMenus();
	
	/**
	 * <p>
	 * Returns if this viewnode sends a redirect to the resourceUri.<br/>
	 * This is default false as the default behaviour is to send a dispatch to the 
	 * underlying resource.
	 * </p>
	 * @return
	 */
	public boolean getRedirectsToResourceUri();
	
}

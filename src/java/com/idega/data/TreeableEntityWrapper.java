/*
 * Created on 24.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.data.ICTreeNode;
import com.idega.core.localisation.data.ICLocale;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:		TreeableEntityWrapper
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public abstract class TreeableEntityWrapper extends IDOEntityWrapper implements TreeableEntity, ICTreeNode {

	/**
	 * @param primaryKey
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	public TreeableEntityWrapper(Object primaryKey) throws IDOLookupException, FinderException {
		super(primaryKey);
	}

	/**
	 * @param primaryKey
	 * @param locale
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	public TreeableEntityWrapper(Object primaryKey, ICLocale locale) throws IDOLookupException, FinderException {
		super(primaryKey, locale);
	}

	

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#addChild(com.idega.data.TreeableEntity)
	 */
	public void addChild(TreeableEntity p0) throws SQLException {
		((TreeableEntity)this.getMainEntity()).addChild(p0);

	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return ((TreeableEntity)this.getMainEntity()).getAllowsChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildAtIndex(int)
	 */
	public ICTreeNode getChildAtIndex(int p0) {
		return ((TreeableEntity)this.getMainEntity()).getChildAtIndex(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildCount()
	 */
	public int getChildCount() {
		return ((TreeableEntity)this.getMainEntity()).getChildCount();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getChildrenItarator<<s(java.lang.String)
	 */
	public Iterator getChildrenIterator(String p0) {
		return ((TreeableEntity)this.getMainEntity()).getChildrenIterator(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildrenIterator()
	 */
	public Iterator getChildrenIterator() {
		return ((TreeableEntity)this.getMainEntity()).getChildrenIterator();
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	public Collection getChildren() {
		return ((TreeableEntity)this.getMainEntity()).getChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getIndex(com.idega.core.ICTreeNode)
	 */
	public int getIndex(ICTreeNode p0) {
		return ((TreeableEntity)this.getMainEntity()).getIndex(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeID()
	 */
	public int getNodeID() {
		return ((TreeableEntity)this.getMainEntity()).getNodeID();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	public String getNodeName() {
		return ((TreeableEntity)this.getMainEntity()).getNodeName();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	public String getNodeName(Locale locale) {
		return ((TreeableEntity)this.getMainEntity()).getNodeName(locale);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return ((TreeableEntity)this.getMainEntity()).getNodeName(locale, iwac);
	}


	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getParentEntity()
	 */
	public TreeableEntity getParentEntity() {
		return ((TreeableEntity)this.getMainEntity()).getParentEntity();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getParentNode()
	 */
	public ICTreeNode getParentNode() {
		return ((TreeableEntity)this.getMainEntity()).getParentNode();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getSiblingCount()
	 */
	public int getSiblingCount() {
		return ((TreeableEntity)this.getMainEntity()).getSiblingCount();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getTreeRelationshipChildColumnName(com.idega.data.TreeableEntity)
	 */
	public String getTreeRelationshipChildColumnName(TreeableEntity p0) {
		return ((TreeableEntity)this.getMainEntity()).getTreeRelationshipChildColumnName(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getTreeRelationshipTableName(com.idega.data.TreeableEntity)
	 */
	public String getTreeRelationshipTableName(TreeableEntity p0) {
		return ((TreeableEntity)this.getMainEntity()).getTreeRelationshipTableName(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return ((TreeableEntity)this.getMainEntity()).isLeaf();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#moveChildrenFrom(com.idega.data.TreeableEntity)
	 */
	public void moveChildrenFrom(TreeableEntity p0) throws SQLException {
		((TreeableEntity)this.getMainEntity()).moveChildrenFrom(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#removeChild(com.idega.data.TreeableEntity)
	 */
	public void removeChild(TreeableEntity p0) throws SQLException {
		((TreeableEntity)this.getMainEntity()).removeChild(p0);
	}

}

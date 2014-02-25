/*
 * Created on 24.6.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.data;

import java.sql.SQLException;
import java.util.ArrayList;
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
public abstract class TreeableEntityWrapper<Node extends ICTreeNode<?>> extends IDOEntityWrapper implements TreeableEntity<Node> {

	private static final long serialVersionUID = -1645039583184616618L;

	/**
	 * @param primaryKey
	 * @throws IDOLookupException
	 * @throws FinderException
	 */
	public TreeableEntityWrapper(Object primaryKey) throws IDOLookupException, FinderException {
		super(primaryKey);
	}

	@Override
	public Integer decode(String pk) {
		return Integer.valueOf(pk);
	}

	@Override
	public Collection<Integer> decode(String[] pks) {
		Collection<Integer> decoded = new ArrayList<Integer>();
		for (String pk: pks) {
			decoded.add(decode(pk));
		}
		return decoded;
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
	@Override
	public void addChild(TreeableEntity<Node> p0) throws SQLException {
		this.getMainEntity().addChild(p0);

	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return this.getMainEntity().getAllowsChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildAtIndex(int)
	 */
	@Override
	public Node getChildAtIndex(int p0) {
		return this.getMainEntity().getChildAtIndex(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		return this.getMainEntity().getChildCount();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getChildrenItarator<<s(java.lang.String)
	 */
	@Override
	public Iterator<TreeableEntity<Node>> getChildrenIterator(String p0, boolean p1) {
		return this.getMainEntity().getChildrenIterator(p0, p1);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getChildrenItarator<<s(java.lang.String)
	 */
	@Override
	public Iterator<TreeableEntity<Node>> getChildrenIterator(String p0) {
		return this.getMainEntity().getChildrenIterator(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildrenIterator()
	 */
	@Override
	public Iterator<Node> getChildrenIterator() {
		return this.getMainEntity().getChildrenIterator();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getChildren()
	 */
	@Override
	public Collection<Node> getChildren() {
		return this.getMainEntity().getChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getIndex(com.idega.core.ICTreeNode)
	 */
	@Override
	public int getIndex(Node p0) {
		return this.getMainEntity().getIndex(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeID()
	 */
	@Override
	public int getNodeID() {
		return this.getMainEntity().getNodeID();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return this.getMainEntity().getNodeName();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName(Locale locale) {
		return this.getMainEntity().getNodeName(locale);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return this.getMainEntity().getNodeName(locale, iwac);
	}


	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getParentEntity()
	 */
	@Override
	public TreeableEntity<Node> getParentEntity() {
		return this.getMainEntity().getParentEntity();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected TreeableEntity<Node> getMainEntity() {
		return (TreeableEntity<Node>) super.getMainEntity();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getParentNode()
	 */
	@Override
	public Node getParentNode() {
		return this.getMainEntity().getParentNode();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#getSiblingCount()
	 */
	@Override
	public int getSiblingCount() {
		return this.getMainEntity().getSiblingCount();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getTreeRelationshipChildColumnName(com.idega.data.TreeableEntity)
	 */
	@Override
	public String getTreeRelationshipChildColumnName(TreeableEntity<Node> p0) {
		return this.getMainEntity().getTreeRelationshipChildColumnName(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#getTreeRelationshipTableName(com.idega.data.TreeableEntity)
	 */
	@Override
	public String getTreeRelationshipTableName(TreeableEntity<Node> p0) {
		return this.getMainEntity().getTreeRelationshipTableName(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.ICTreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return this.getMainEntity().isLeaf();
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#moveChildrenFrom(com.idega.data.TreeableEntity)
	 */
	@Override
	public void moveChildrenFrom(TreeableEntity<Node> p0) throws SQLException {
		this.getMainEntity().moveChildrenFrom(p0);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.TreeableEntity#removeChild(com.idega.data.TreeableEntity)
	 */
	@Override
	public void removeChild(TreeableEntity<Node> p0) throws SQLException {
		this.getMainEntity().removeChild(p0);
	}

	@Override
	public String getId() {
		return this.getMainEntity().getId();
	}

}
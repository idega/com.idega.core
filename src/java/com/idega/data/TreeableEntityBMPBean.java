package com.idega.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.idega.core.business.ICTreeNodeLeafComparator;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class TreeableEntityBMPBean<Node extends ICTreeNode<?>> extends GenericEntity
																		implements TreeableEntity<Node>, IDOLegacyEntity, ICTreeNode<Node> {

	private static final long serialVersionUID = -7018051419205886687L;

	protected boolean _sortLeafs = false;
	protected boolean _leafsFirst = false;

	public TreeableEntityBMPBean() {
		super();
	}

	public TreeableEntityBMPBean(int id) throws java.sql.SQLException {
		super(id);
	}

	@Override
	protected void beforeInitializeAttributes() {
		addTreeRelationShip();
	}

	@Override
	public int getNodeID() {
		return getID();
	}

	/**
	 * Default implementation just calls getName()
	 */
	@Override
	public String getNodeName() {
		return getName();
	}

	/**
	 * Default implementation just calls getNodeName()
	 */
	@Override
	public String getNodeName(Locale locale) {
		return getNodeName();
	}

	/**
	 * Default implementation just calls getNodeName()
	 */
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	/**
	 * Returns the children of the reciever as an Iterator. Returns null if no children found
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Node> getChildrenIterator() {
		Iterator<TreeableEntity<Node>> childrenIter = getChildrenIterator(null);
		return (Iterator<Node>) childrenIter;
	}
	@Override
	public Iterator<TreeableEntity<Node>> getChildrenIterator(String orderBy) {
		return getChildrenIterator(orderBy, false);
	}
	@Override
	public Iterator<TreeableEntity<Node>> getChildrenIterator(String orderBy, boolean orderDescending) {
	    Iterator<TreeableEntity<Node>> it = null;
	    Collection<TreeableEntity<Node>> children = getChildren(orderBy, orderDescending);
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Node> getChildren() {
		Collection<TreeableEntity<Node>> children = getChildren(null);
		return (Collection<Node>) children;
	}
	public Collection<TreeableEntity<Node>> getChildren(String orderBy) {
		return getChildren(orderBy, false);
	}
	public Collection<TreeableEntity<Node>> getChildren(String orderBy, boolean orderDescending) {
		try {
			String thisTable = this.getTableName();
			String treeTable = EntityControl.getTreeRelationShipTableName(this);
			String idColumnName = this.getIDColumnName();
			String childIDColumnName = EntityControl.getTreeRelationShipChildColumnName(this);
			StringBuffer buffer = new StringBuffer();

			if(this.getPrimaryKey() instanceof Integer){
				buffer.append("select " ).append( thisTable ).append(".* from ").append( thisTable).append(",").append(treeTable).append(" where ").append(thisTable).append(".").append(idColumnName).append(" = ").append(treeTable).append(".").append(childIDColumnName).append(" and ").append(treeTable).append(".").append(idColumnName).append( " = ").append(this.getPrimaryKey().toString());
			}
			else{//add the ' for strings, dates etc.
				buffer.append("select " ).append( thisTable ).append(".* from ").append( thisTable).append(",").append(treeTable).append(" where ").append(thisTable).append(".").append(idColumnName).append(" = ").append(treeTable).append(".").append(childIDColumnName).append(" and ").append(treeTable).append(".").append(idColumnName).append( " = '").append(this.getPrimaryKey().toString()).append("'");
			}


			if (orderBy != null && !orderBy.equals("")) {
				buffer.append(" order by ").append(thisTable).append( ".").append(orderBy);
				if (orderDescending) {
					buffer.append(" ").append("desc");
				}
			}
			//System.out.println(buffer.toString());
			List<TreeableEntity<Node>> list = EntityFinder.findAll(this, buffer.toString());
			if (list != null) {
				if (this._sortLeafs) {
					Collections.sort(list, new ICTreeNodeLeafComparator<TreeableEntity<Node>>(this._leafsFirst));
				}
				return list;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildren() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *  Returns true if the receiver allows children.
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 *  Returns the child TreeNode at index childIndex.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Node getChildAtIndex(int childIndex) {
		try {
			GenericEntity entity = this.getClass().newInstance();
			entity.findByPrimaryKey(childIndex);
			return (Node) entity;
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildAtIndex() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	@Override
	public int getChildCount() {
		String treeTableName = EntityControl.getTreeRelationShipTableName(this);

		if(this.getPrimaryKey() instanceof Integer){
			return EntityControl.returnSingleSQLQuery(this, "select count(*) from " + treeTableName + " where " + this.getIDColumnName() + "=" + this.getPrimaryKey().toString() + "");
		}
		else{//string etc
			return EntityControl.returnSingleSQLQuery(this, "select count(*) from " + treeTableName + " where " + this.getIDColumnName() + "='" + this.getPrimaryKey().toString() + "'");
		}
	}

	/**
	 * Returns the index of node in the receivers children.
	 */
	@Override
	public int getIndex(Node node) {
		return ((GenericEntity)node).getID();
	}

	/**
	 *
	 *  Returns the parent TreeNode of the receiver. Return null if none
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Node getParentNode() {
		boolean isInteger = false;
		String sql = null;

		if(this.getPrimaryKey() instanceof Integer){
			 sql = "select " + this.getIDColumnName() + " from " + EntityControl.getTreeRelationShipTableName(this) + " where " + EntityControl.getTreeRelationShipChildColumnName(this) + "=" + this.getPrimaryKey();
			 isInteger = true;
		}
		else{
			sql = "select " + this.getIDColumnName() + " from " + EntityControl.getTreeRelationShipTableName(this) + " where " + EntityControl.getTreeRelationShipChildColumnName(this) + "='" + this.getPrimaryKey()+"'";
		}

		//List list;
		try {
			//list = EntityFinder.findAll(this, sql);
			String arr[] = SimpleQuerier.executeStringQuery(sql);
			//well presume that the first result is the primary key of the parent:
			if(arr.length>0){
				String parentPk = arr[0];
			    if (isInteger) {
			    	IDOEntity parentNode = ((IDOHome) getEJBLocalHome()).findByPrimaryKeyIDO(Integer.valueOf(parentPk));
			    	return (Node) parentNode;
			    }
			    else {
			    	IDOEntity parentNode = ((IDOHome)getEJBLocalHome()).findByPrimaryKeyIDO(parentPk);
					return (Node) parentNode;
			    }
			}
			/*
			if (list != null && !list.isEmpty()) {
				return (TreeableEntity)list.iterator().next();
			}
			else{
				return null;
			}*/

		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeableEntity<Node> getParentEntity() {
		Node node = getParentNode();
		if (node instanceof TreeableEntity) {
			return (TreeableEntity<Node>) node;
		}
		return null;
	}

	@Override
	public boolean isLeaf() {
		int children = getChildCount();
		if (children > 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String getTreeRelationshipTableName(TreeableEntity<Node> entity) {
		return EntityControl.getTreeRelationShipTableName(entity);
	}

	@Override
	public String getTreeRelationshipChildColumnName(TreeableEntity<Node> entity) {
		return EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean<Node>)entity);
	}

	@Override
	public void addChild(TreeableEntity<Node> entity) throws java.sql.SQLException {
		addToTree((TreeableEntityBMPBean<Node>)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean<Node>)entity), getTreeRelationshipTableName(entity));
	}

	@Override
	public void removeChild(TreeableEntity<Node> entity) throws java.sql.SQLException {
		removeFrom((TreeableEntityBMPBean<Node>)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean<Node>)entity));
	}

	@Override
	public void moveChildrenFrom(TreeableEntity<Node> entityFrom) throws java.sql.SQLException {
		moveChildrenToCurrent((TreeableEntityBMPBean<Node>)entityFrom, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean<Node>)entityFrom));
	}

	/**
	 *
	 */
	@Override
	public int getSiblingCount() {
		Node parent = getParentNode();
		if (parent == null) {
			return (0);
		}

		return (parent.getChildCount() - 1);
	}

	/**
	 * @see com.idega.core.ICTreeNode#getNodeType()
	 */
	public int getNodeType(){
		return -1;
	}


	/**
	 * @return
	 */
	@Override
	public boolean leafsFirst() {
		return this._leafsFirst;
	}

	/**
	 * @return
	 */
	@Override
	public boolean sortLeafs() {
		return this._sortLeafs;
	}

	/**
	 * @param b
	 */
	@Override
	public void setLeafsFirst(boolean b) {
		this._leafsFirst = b;
	}

	/**
	 * @param b
	 */
	@Override
	public void setToSortLeafs(boolean b) {
		this._sortLeafs = b;
	}

	@Override
	public String getId(){
		return getPrimaryKey().toString();
	}

}

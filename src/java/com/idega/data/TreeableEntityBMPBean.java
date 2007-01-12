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

public abstract class TreeableEntityBMPBean extends com.idega.data.GenericEntity implements com.idega.data.TreeableEntity, IDOLegacyEntity, com.idega.core.data.ICTreeNode {

	protected boolean _sortLeafs = false;
	protected boolean _leafsFirst = false;

	public TreeableEntityBMPBean() {
		super();
	}

	public TreeableEntityBMPBean(int id) throws java.sql.SQLException {
		super(id);
	}

	protected void beforeInitializeAttributes() {
		addTreeRelationShip();
	}

	public int getNodeID() {
		return getID();
	}
	
	/**
	 * Default implementation just calls getName()
	 */
	public String getNodeName() {
		return getName();
	}
	
	/**
	 * Default implementation just calls getNodeName()
	 */
	public String getNodeName(Locale locale) {
		return getNodeName();
	}
	
	/**
	 * Default implementation just calls getNodeName()
	 */
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

	/**
	 * Returns the children of the reciever as an Iterator. Returns null if no children found
	 */
	public Iterator getChildrenIterator() {
		return getChildrenIterator(null);
	}
	public Iterator getChildrenIterator(String orderBy) {
		return getChildrenIterator(orderBy, false);
	}
	public Iterator getChildrenIterator(String orderBy, boolean orderDescending) {
	    Iterator it = null;
	    Collection children = getChildren(orderBy, orderDescending);
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it; 
	}
	public Collection getChildren() {
	    return getChildren(null);
	}
	public Collection getChildren(String orderBy) {
		return getChildren(orderBy, false);
	}
	public Collection getChildren(String orderBy, boolean orderDescending) {
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
			List list = EntityFinder.findAll(this, buffer.toString());
			if (list != null) {
				if (this._sortLeafs) {
					ICTreeNodeLeafComparator c = new ICTreeNodeLeafComparator(this._leafsFirst);
					Collections.sort(list, c);
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
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 *  Returns the child TreeNode at index childIndex.
	 */
	public ICTreeNode getChildAtIndex(int childIndex) {
		try {
			GenericEntity entity = (GenericEntity)this.getClass().newInstance();
			entity.findByPrimaryKey(childIndex);
			return (TreeableEntity)entity;
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildAtIndex() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	public int getChildCount() {
		String treeTableName = EntityControl.getTreeRelationShipTableName(this);
		
		if(this.getPrimaryKey() instanceof Integer){
			return EntityControl.returnSingleSQLQuery(this, "select count(" + this.getIDColumnName() + ") from " + treeTableName + " where " + this.getIDColumnName() + "=" + this.getPrimaryKey().toString() + "");
		}
		else{//string etc
			return EntityControl.returnSingleSQLQuery(this, "select count(" + this.getIDColumnName() + ") from " + treeTableName + " where " + this.getIDColumnName() + "='" + this.getPrimaryKey().toString() + "'");
		}
	}

	/**
	 * Returns the index of node in the receivers children.
	 */
	public int getIndex(ICTreeNode node) {
		return ((GenericEntity)node).getID();
	}

	/**
	 * 
	 *  Returns the parent TreeNode of the receiver. Return null if none
	 */
	public ICTreeNode getParentNode() {
		
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
			    	return (ICTreeNode)((IDOHome)getEJBLocalHome()).findByPrimaryKeyIDO(Integer.valueOf(parentPk));
			    } 
			    else {
					return (ICTreeNode)((IDOHome)getEJBLocalHome()).findByPrimaryKeyIDO(parentPk);
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

	public TreeableEntity getParentEntity() {
		return (TreeableEntity)getParentNode();
	}

	public boolean isLeaf() {
		int children = getChildCount();
		if (children > 0) {
			return false;
		} else {
			return true;
		}
	}

	public String getTreeRelationshipTableName(TreeableEntity entity) {
		return EntityControl.getTreeRelationShipTableName(entity);
	}

	public String getTreeRelationshipChildColumnName(TreeableEntity entity) {
		return EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity);
	}

	public void addChild(TreeableEntity entity) throws java.sql.SQLException {
		addToTree((TreeableEntityBMPBean)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity), getTreeRelationshipTableName(entity));
	}

	public void removeChild(TreeableEntity entity) throws java.sql.SQLException {
		removeFrom((TreeableEntityBMPBean)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity));
	}

	public void moveChildrenFrom(TreeableEntity entityFrom) throws java.sql.SQLException {
		moveChildrenToCurrent((TreeableEntityBMPBean)entityFrom, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entityFrom));
	}

	/**
	 *
	 */
	public int getSiblingCount() {
		ICTreeNode parent = getParentNode();
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
	public boolean leafsFirst() {
		return this._leafsFirst;
	}

	/**
	 * @return
	 */
	public boolean sortLeafs() {
		return this._sortLeafs;
	}

	/**
	 * @param b
	 */
	public void setLeafsFirst(boolean b) {
		this._leafsFirst = b;
	}

	/**
	 * @param b
	 */
	public void setToSortLeafs(boolean b) {
		this._sortLeafs = b;
	}

}

package com.idega.data;

import com.idega.core.ICTreeNode;


import java.util.Iterator;
import java.util.List;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class TreeableEntity extends GenericEntity implements ICTreeNode{

  public TreeableEntity(){
    super();
  }


  public TreeableEntity(int id)throws java.sql.SQLException{
    super(id);
  }

  protected void afterInitializeAttributes(){
    addTreeRelationShip();
  }

    public int getNodeID(){
      return getID();
    }

    public String getNodeName(){
      return getName();
    }

    /**
     * Returns the children of the reciever as an Iterator. Returns null if no children found
     */
    public Iterator getChildren(){
      return getChildren(null);
    }

    public Iterator getChildren(String orderBy){
      try{
        String thisTable=this.getTableName();
        String treeTable = EntityControl.getTreeRelationShipTableName(this);
        String idColumnName = this.getIDColumnName();
        String childIDColumnName=EntityControl.getTreeRelationShipChildColumnName(this);
        StringBuffer buffer = new StringBuffer();
          buffer.append("select "+thisTable+".* from "+thisTable+","+treeTable+" where "+thisTable+"."+idColumnName+"="+treeTable+"."+childIDColumnName+" and "+treeTable+"."+idColumnName+"='"+this.getID()+"'");
          if (orderBy != null && !orderBy.equals("")) {
            buffer.append(" order by "+thisTable+"."+orderBy);
          }


        List list = EntityFinder.findAll(this,buffer.toString());
        if(list != null){
          return list.iterator();
        }
        else{
          return null;
        }
      }
      catch(Exception e){
          System.err.println("There was an error in TreeableEntity.getChildren() "+e.getMessage());
          e.printStackTrace(System.err);
          return null;
      }
    }

    /**
     *  Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren(){
      return true;
    }

    /**
     *  Returns the child TreeNode at index childIndex.
     */
    public ICTreeNode getChildAtIndex(int childIndex){
      try{
        TreeableEntity entity = (TreeableEntity)this.getClass().newInstance();
        entity.findByPrimaryKey(childIndex);
        return entity;
      }
      catch(Exception e){
          System.err.println("There was an error in TreeableEntity.getChildAtIndex() "+e.getMessage());
          e.printStackTrace(System.err);
          return null;
      }
    }

    /**
     *    Returns the number of children TreeNodes the receiver contains.
     */
    public int getChildCount(){
        String treeTableName= EntityControl.getTreeRelationShipTableName(this);
        return EntityControl.returnSingleSQLQuery(this,"select count(*) from "+treeTableName+" where "+this.getIDColumnName()+"='"+this.getID()+"'");
    }

    /**
     * Returns the index of node in the receivers children.
     */
    public int getIndex(ICTreeNode node){
      return ((TreeableEntity)node).getID();
    }

  /**
   *  Returns the parent TreeNode of the receiver. Return null if none
   */
  public ICTreeNode getParentNode(){
    try{
      int parent_id = EntityControl.returnSingleSQLQuery(this,"select "+this.getIDColumnName()+" from "+EntityControl.getTreeRelationShipTableName(this)+" where "+EntityControl.getTreeRelationShipChildColumnName(this)+"='"+this.getID()+"'");
      if(parent_id!=-1){
        TreeableEntity entity = (TreeableEntity)this.getClass().newInstance();
        entity.findByPrimaryKey(parent_id);
        return entity;
      }
      else{
        return null;
      }
    }
    catch(Exception e){
        System.err.println("There was an error in TreeableEntity.getParentNode() "+e.getMessage());
        e.printStackTrace(System.err);
        return null;
    }
  }

  public TreeableEntity getParent() {
    return (TreeableEntity) getParentNode();
  }

  public boolean isLeaf(){
    int children = getChildCount();
    if (children > 0){
      return false;
    }
    else{
      return true;
    }
  }

  public String getTreeRelationshipTableName(TreeableEntity entity) {
    return EntityControl.getTreeRelationShipTableName(entity);
  }

  public String getTreeRelationshipChildColumnName(TreeableEntity entity) {
    return EntityControl.getTreeRelationShipChildColumnName(entity);
  }

  public void addChild(TreeableEntity entity) throws java.sql.SQLException {
    addTo(entity,EntityControl.getTreeRelationShipChildColumnName(entity));
  }

  public void removeChild(TreeableEntity entity) throws java.sql.SQLException {
    removeFrom(entity,EntityControl.getTreeRelationShipChildColumnName(entity));
  }

  public void moveChildrenFrom(TreeableEntity entityFrom) throws java.sql.SQLException {
    moveChildrenToCurrent(entityFrom,EntityControl.getTreeRelationShipChildColumnName(entityFrom));
  }

  /**
   *
   */
  public int getSiblingCount() {
    ICTreeNode parent = getParentNode();
    if (parent == null)
      return(0);

    return(parent.getChildCount() - 1);
  }

}
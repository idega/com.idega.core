package com.idega.builder.data;

import com.idega.data.*;
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
      try{
        String thisTable=this.getTableName();
        String treeTable = thisTable+"_tree";
        String idColumnName = this.getIDColumnName();
        String childIDColumnName="child_"+idColumnName;
        List list = EntityFinder.findAll(this,"select "+thisTable+".* from "+thisTable+","+treeTable+" where "+thisTable+"."+idColumnName+"="+treeTable+"."+childIDColumnName+" and "+treeTable+"."+idColumnName+"='"+this.getID()+"'");
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
        String treeTableName=this.getTableName()+"_tree";
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
      int parent_id = EntityControl.returnSingleSQLQuery(this,"select "+this.getIDColumnName()+" from "+this.getEntityName()+" where child_"+this.getIDColumnName()+"='"+this.getID()+"'");
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

  public boolean isLeaf(){
    int children = getChildCount();
    if (children > 0){
      return false;
    }
    else{
      return true;
    }
  }


  public void addChild(TreeableEntity entity)throws java.sql.SQLException{
    this.addTo(entity,"child_"+entity.getIDColumnName());
  }


}
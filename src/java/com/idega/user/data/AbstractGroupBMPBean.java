package com.idega.user.data;

import com.idega.data.*;
import java.sql.SQLException;
import com.idega.core.ICTreeNode;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */



public abstract class AbstractGroupBMPBean extends GenericEntity implements Group{

  protected Group _group;

  /**
   * Returns a unique Key to identify this GroupType
   */
  public abstract String getGroupTypeKey();
//  public String getGroupTypeKey(){return null;}
  /**
   * Returns a description for the GroupType
   */
  public abstract String getGroupTypeDescription();
//  public String getGroupTypeDescription(){return null;}

  /**
   * Returns the visibility of the GroupType
   */
  public boolean getGroupTypeVisibility(){
    return true;
  }


//  public void initializeAttributes() {
//    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
//  }
//  public String getEntityName() {
//    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
//    return null;
//  }


  public void addGeneralGroupRelation(){
    this.addManyToOneRelationship(getIDColumnName(),"Group ID",Group.class);
    this.getAttribute(getIDColumnName()).setAsPrimaryKey(true);
  }

  public Object ejbCreate()throws CreateException{
    try{
      _group = this.getGroupHome().create();
      _group.setGroupType(this.getGroupTypeKey());
      _group.store();
      this.setPrimaryKey(_group.getPrimaryKey());

      return super.ejbCreate();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }


  public void setDefaultValues(){
    try{
      setGroupType(getGroupTypeKey());
    }
    catch(RemoteException e){
      throw new EJBException(e.getMessage());
    }
  }

  public void insertStartData(){
    try{
      GroupTypeHome tghome = (GroupTypeHome)IDOLookup.getHome(GroupType.class);

      GroupType type = tghome.create();
      type.setType(getGroupTypeKey());
      type.setDescription(getGroupTypeDescription());
      type.setVisibility(getGroupTypeVisibility());
      type.store();

    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  protected boolean doInsertInCreate(){
    return true;
  }
/*
  public Object ejbFindByPrimaryKey(Object key)throws FinderException{
    try{
      _group = this.getGroupHome().findByPrimaryKey(key);
      return super.ejbFindByPrimaryKey(key);
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
*/
  public void store()throws IDOStoreException{
    try{
      if(this.getGroupType()==null  ){ // || getGroupHome().getGroupType().equals(this.getGroupType())
	this.setGroupType(this.getGroupTypeKey());
      }
      getGeneralGroup().store();
//      System.out.println("User before/st primaryKey = " + this.getPrimaryKey());
      super.store();
//      System.out.println("User after/st primaryKey = " + this.getPrimaryKey());
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }


  public void remove()throws RemoveException{
    try{
      super.remove();
      getGeneralGroup().remove();
    }
    catch(RemoteException rme){
      throw new RemoveException(rme.getMessage());
    }
  }

  protected GroupHome getGroupHome()throws RemoteException{
    return (GroupHome)com.idega.data.IDOLookup.getHome(Group.class);
  }

  protected Group getGeneralGroup()throws RemoteException{
    if(_group == null){
      try{
	_group = getGroupHome().findByPrimaryKey(this.getPrimaryKey());
      }
      catch(FinderException fe){
	throw new EJBException(fe.getMessage());
      }
    }
    return _group;
  }




  //
  //
  //

  public String getName(){
    try {
      return getGeneralGroup().getName();
    }
    catch (Exception ex) {
     throw new EJBException(ex);
    }
  }

  public void setName(String name){
    try {
      getGeneralGroup().setName(name);
    }
    catch (Exception ex) {
     throw new EJBException(ex);
    }
  }

  public String getGroupType() throws java.rmi.RemoteException {
    return getGeneralGroup().getGroupType();
  }

  public void setGroupType(String type) throws java.rmi.RemoteException {
    getGeneralGroup().setGroupType(type);
  }

  public String getDescription() throws java.rmi.RemoteException {
    return getGeneralGroup().getDescription();
  }

  public void setDescription(String description) throws java.rmi.RemoteException {
    getGeneralGroup().setDescription(description);
  }

  public String getExtraInfo() throws java.rmi.RemoteException {
    return getGeneralGroup().getExtraInfo();
  }

  public void setExtraInfo(String extraInfo) throws java.rmi.RemoteException {
    getGeneralGroup().setExtraInfo(extraInfo);
  }

  public Timestamp getCreated() throws java.rmi.RemoteException {
    return getGeneralGroup().getCreated();
  }

  public void setCreated(Timestamp created) throws java.rmi.RemoteException {
    getGeneralGroup().setCreated(created);
  }

  //
  //
  //







  //
  //
  //



  /**
   * Returns a collection of Group objects that are related by the relation type relationType with this Group
   */
  public Collection getRelatedBy(GroupRelationType relationType)throws FinderException,RemoteException{
    return this.getGeneralGroup().getRelatedBy(relationType);
  }

  /**
   * Returns a collection of Group objects that are related by the relation type relationType with this Group
   */
  public Collection getRelatedBy(String relationType)throws FinderException,RemoteException{
    return this.getGeneralGroup().getRelatedBy(relationType);
  }

  public void addRelation(Group groupToAdd,String relationType)throws CreateException,RemoteException{
    this.getGeneralGroup().addRelation(groupToAdd, relationType);
  }

  public void addRelation(Group groupToAdd,GroupRelationType relationType)throws CreateException,RemoteException{
    this.getGeneralGroup().addRelation(groupToAdd, relationType);
  }

  public void addRelation(int relatedGroupId,GroupRelationType relationType)throws CreateException,RemoteException{
    this.getGeneralGroup().addRelation(relatedGroupId,relationType);
  }

  public void addRelation(int relatedGroupId,String relationType)throws CreateException,RemoteException{
    this.getGeneralGroup().addRelation(relatedGroupId,relationType);
  }

  public void removeRelation(Group relatedGroup,String relationType)throws RemoveException,RemoteException{
    this.getGeneralGroup().removeRelation(relatedGroup,relationType);
  }

  public void removeRelation(int relatedGroupId,String relationType)throws RemoveException,RemoteException{
    this.getGeneralGroup().removeRelation(relatedGroupId,relationType);
  }

  public java.util.List getListOfAllGroupsContainingThis()throws javax.ejb.EJBException, java.rmi.RemoteException{
    return this.getGeneralGroup().getListOfAllGroupsContainingThis();
  }


  public void removeGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException{
    this.getGeneralGroup().removeGroup(p0);
  }

  public void addGroup(com.idega.user.data.Group p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException{
    this.getGeneralGroup().addGroup(p0);
  }

  public java.util.List getGroupsContained(java.lang.String[] p0,boolean p1)throws javax.ejb.EJBException, java.rmi.RemoteException{
    return this.getGeneralGroup().getGroupsContained(p0,p1);
  }

  public java.util.List getListOfAllGroupsContaining(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException{
    return this.getGeneralGroup().getListOfAllGroupsContaining(p0);
  }

  public void addGroup(int p0)throws javax.ejb.EJBException, java.rmi.RemoteException{
    this.getGeneralGroup().addGroup(p0);
  }

  public java.util.List getListOfAllGroupsContained()throws javax.ejb.EJBException, java.rmi.RemoteException{
    return this.getGeneralGroup().getListOfAllGroupsContained();
  }

  public java.util.Collection getAllGroupsContainingUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.EJBException, java.rmi.RemoteException{
    return this.getGeneralGroup().getAllGroupsContainingUser(p0);
  }

  public void removeGroup(int p0,boolean p1)throws javax.ejb.EJBException, java.rmi.RemoteException{
    this.getGeneralGroup().removeGroup(p0,p1);
  }

  public java.lang.String getGroupTypeValue() throws java.rmi.RemoteException{
    return this.getGroupTypeKey();
  }

  public void removeUser(com.idega.user.data.User p0)throws java.rmi.RemoteException, java.rmi.RemoteException{
    this.getGeneralGroup().removeUser(p0);
  }

  public void removeGroup()throws javax.ejb.EJBException, java.rmi.RemoteException{
    this.getGeneralGroup().removeGroup();
  }

  public boolean equals(com.idega.user.data.Group obj) throws java.rmi.RemoteException{
//    System.out.println("AbstractPratyBMPBean in equals(com.idega.user.data.Group p0)");
    if(obj instanceof AbstractGroupBMPBean){
      try {
	return this.getGeneralGroup().equals(((AbstractGroupBMPBean)obj).getGeneralGroup());
      }
      catch (RemoteException ex) {
	throw new EJBException(ex);
      }
    } else {
      return this.getGeneralGroup().equals(obj);
    }
  }

  public boolean equals(Object obj){
//    System.out.println("AbstractPratyBMPBean in equals(Object obj)");
    if(obj instanceof AbstractGroupBMPBean){
      try {
	return this.equals(((AbstractGroupBMPBean)obj).getGeneralGroup());
      }
      catch (RemoteException ex) {
	throw new EJBException(ex);
      }
    } else if(obj instanceof Group){
      return super.equals((Group)obj);
    } else {
      return super.equals(obj);
    }
  }

  public boolean equals(IDOLegacyEntity obj){
    if(obj instanceof AbstractGroupBMPBean){
      try {
	return this.equals(((AbstractGroupBMPBean)obj).getGeneralGroup());
      }
      catch (RemoteException ex) {
	throw new EJBException(ex);
      }
    } else if(obj instanceof Group){
      return super.equals((Group)obj);
    } else {
      return super.equals(obj);
    }
  }





  //
  //
  //

  /**
   * ICTreeNode implementation begins
   */

  public Iterator getChildren() {
    try{
      return this.getGeneralGroup().getChildren();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }

  public boolean getAllowsChildren() {
    try{
      return this.getGeneralGroup().getAllowsChildren();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public ICTreeNode getChildAtIndex(int childIndex) {
    try{
      return this.getGeneralGroup().getChildAtIndex(childIndex);
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public int getChildCount() {
    try{
      return this.getGeneralGroup().getChildCount();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public int getIndex(ICTreeNode node) {
    try{
      return this.getGeneralGroup().getIndex(node);
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public ICTreeNode getParentNode() {
    try{
      return this.getGeneralGroup().getParentNode();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public boolean isLeaf() {
    try{
      return this.getGeneralGroup().isLeaf();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public String getNodeName() {
    try{
      return this.getGeneralGroup().getNodeName();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public int getNodeID() {
    try{
      return this.getGeneralGroup().getNodeID();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }
  public int getSiblingCount() {
    try{
      return this.getGeneralGroup().getSiblingCount();
    }
    catch(RemoteException rme){
      throw new EJBException(rme.getMessage());
    }
  }

  /**
   * ICTreeNode implementation ends
   */

}
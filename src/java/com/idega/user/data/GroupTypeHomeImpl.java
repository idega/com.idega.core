package com.idega.user.data;


public class GroupTypeHomeImpl extends com.idega.data.IDOFactory implements GroupTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GroupType.class;
 }


 public GroupType create() throws javax.ejb.CreateException{
  return (GroupType) super.createIDO();
 }


public java.util.Collection findAllGroupTypes()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupTypeBMPBean)entity).ejbFindAllGroupTypes();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public GroupType findGroupTypeByGroupTypeString(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((GroupTypeBMPBean)entity).ejbFindGroupTypeByGroupTypeString(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public java.util.Collection findVisibleGroupTypes()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((GroupTypeBMPBean)entity).ejbFindVisibleGroupTypes();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public GroupType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GroupType) super.findByPrimaryKeyIDO(pk);
 }


public java.lang.String getAliasGroupTypeString(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetAliasGroupTypeString();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getGeneralGroupTypeString(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetGeneralGroupTypeString();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getNumberOfGroupTypes()throws javax.ejb.FinderException,com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetNumberOfGroupTypes();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getNumberOfVisibleGroupTypes()throws javax.ejb.FinderException,com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetNumberOfVisibleGroupTypes();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getPermissionGroupTypeString(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetPermissionGroupTypeString();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getVisibleGroupTypesSQLString(){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((GroupTypeBMPBean)entity).ejbHomeGetVisibleGroupTypesSQLString();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}
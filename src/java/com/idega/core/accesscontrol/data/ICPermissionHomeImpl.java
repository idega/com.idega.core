package com.idega.core.accesscontrol.data;

import java.util.Collection;

import javax.ejb.FinderException;


public class ICPermissionHomeImpl extends com.idega.data.IDOFactory implements ICPermissionHome
{
 protected Class getEntityInterfaceClass(){
  return ICPermission.class;
 }


 public ICPermission create() throws javax.ejb.CreateException{
  return (ICPermission) super.createIDO();
 }

//need for now
 public ICPermission createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException{
	return (ICPermission) super.idoFindByPrimaryKey(id);
 }
 
 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }
 
 // temporary ends

public java.util.Collection findAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(java.lang.String p0,java.lang.String p1,com.idega.user.data.Group p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionBMPBean)entity).ejbFindAllPermissionsByContextTypeAndContextValueAndPermissionGroupOrdered(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPermissionsByContextTypeAndContextValue(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionBMPBean)entity).ejbFindAllPermissionsByContextTypeAndContextValue(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(com.idega.user.data.Group p0,java.lang.String p1,java.lang.String p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionBMPBean)entity).ejbFindAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(java.lang.String p0,com.idega.user.data.Group p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionBMPBean)entity).ejbFindAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection groups,String permissionString, String contextType) throws FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionBMPBean)entity).ejbFindAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(groups,permissionString,contextType);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPermission) super.findByPrimaryKeyIDO(pk);
 }



}
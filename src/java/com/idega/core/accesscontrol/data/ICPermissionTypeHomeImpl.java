package com.idega.core.accesscontrol.data;


public class ICPermissionTypeHomeImpl extends com.idega.data.IDOFactory implements ICPermissionTypeHome
{
 protected Class getEntityInterfaceClass(){
  return ICPermissionType.class;
 }


 public ICPermissionType create() throws javax.ejb.CreateException{
  return (ICPermissionType) super.createIDO();
 }


public java.util.Collection findAllPermissionsTypes()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICPermissionTypeBMPBean)entity).ejbFindAllPermissionsTypes();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICPermissionType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPermissionType) super.findByPrimaryKeyIDO(pk);
 }



}
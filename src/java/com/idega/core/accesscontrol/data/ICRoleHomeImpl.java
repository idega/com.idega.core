package com.idega.core.accesscontrol.data;


public class ICRoleHomeImpl extends com.idega.data.IDOFactory implements ICRoleHome
{
 protected Class getEntityInterfaceClass(){
  return ICRole.class;
 }


 public ICRole create() throws javax.ejb.CreateException{
  return (ICRole) super.createIDO();
 }


public java.util.Collection findAllRoles()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICRoleBMPBean)entity).ejbFindAllRoles();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICRole findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICRole) super.findByPrimaryKeyIDO(pk);
 }



}
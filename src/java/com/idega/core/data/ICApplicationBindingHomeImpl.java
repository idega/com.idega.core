package com.idega.core.data;


public class ICApplicationBindingHomeImpl extends com.idega.data.IDOFactory implements ICApplicationBindingHome
{
 protected Class getEntityInterfaceClass(){
  return ICApplicationBinding.class;
 }


 public ICApplicationBinding create() throws javax.ejb.CreateException{
  return (ICApplicationBinding) super.createIDO();
 }


public java.util.Collection findByBindingType(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICApplicationBindingBMPBean)entity).ejbFindByBindingType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICApplicationBinding findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICApplicationBinding) super.findByPrimaryKeyIDO(pk);
 }



}
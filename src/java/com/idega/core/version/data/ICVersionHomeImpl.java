package com.idega.core.version.data;


public class ICVersionHomeImpl extends com.idega.data.IDOFactory implements ICVersionHome
{
 protected Class getEntityInterfaceClass(){
  return ICVersion.class;
 }


 public ICVersion create() throws javax.ejb.CreateException{
  return (ICVersion) super.createIDO();
 }


public java.util.Collection findChildrens(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICVersionBMPBean)entity).ejbFindChildrens(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICVersion findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICVersion) super.findByPrimaryKeyIDO(pk);
 }


public int getChildrenCount(com.idega.core.version.data.ICVersion p0)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ICVersionBMPBean)entity).ejbHomeGetChildrenCount(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}
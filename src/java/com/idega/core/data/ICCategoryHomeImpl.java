package com.idega.core.data;


public class ICCategoryHomeImpl extends com.idega.data.IDOFactory implements ICCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICCategory.class;
 }


 public ICCategory create() throws javax.ejb.CreateException{
  return (ICCategory) super.createIDO();
 }


public java.util.Collection findRootsByType(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICCategoryBMPBean)entity).ejbFindRootsByType(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICCategory) super.findByPrimaryKeyIDO(pk);
 }


public boolean setOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	boolean theReturn = ((ICCategoryBMPBean)entity).ejbHomeSetOrderNumber(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ICCategoryBMPBean)entity).ejbHomeGetOrderNumber(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.util.List getListOfCategoryForObjectInstance(com.idega.core.data.ICObjectInstance p0,boolean p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.List theReturn = ((ICCategoryBMPBean)entity).ejbHomeGetListOfCategoryForObjectInstance(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}
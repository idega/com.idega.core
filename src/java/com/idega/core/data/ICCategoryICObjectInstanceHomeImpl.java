package com.idega.core.data;


public class ICCategoryICObjectInstanceHomeImpl extends com.idega.data.IDOFactory implements ICCategoryICObjectInstanceHome
{
 protected Class getEntityInterfaceClass(){
  return ICCategoryICObjectInstance.class;
 }


 public ICCategoryICObjectInstance create() throws javax.ejb.CreateException{
  return (ICCategoryICObjectInstance) super.createIDO();
 }


 public ICCategoryICObjectInstance findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICCategoryICObjectInstance) super.findByPrimaryKeyIDO(pk);
 }


public int getOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ICCategoryICObjectInstanceBMPBean)entity).ejbHomeGetOrderNumber(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.util.List getListOfCategoryForObjectInstance(com.idega.core.data.ICObjectInstance p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.List theReturn = ((ICCategoryICObjectInstanceBMPBean)entity).ejbHomeGetListOfCategoryForObjectInstance(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getRelatedSQL(int p0){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((ICCategoryICObjectInstanceBMPBean)entity).ejbHomeGetRelatedSQL(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.lang.String getRelatedSQL(int p0, String p1){
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.lang.String theReturn = ((ICCategoryICObjectInstanceBMPBean)entity).ejbHomeGetRelatedSQL(p0, p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public boolean setOrderNumber(com.idega.core.business.Category p0,com.idega.core.data.ICObjectInstance p1,int p2)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	boolean theReturn = ((ICCategoryICObjectInstanceBMPBean)entity).ejbHomeSetOrderNumber(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}
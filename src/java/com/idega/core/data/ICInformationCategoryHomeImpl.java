package com.idega.core.data;


public class ICInformationCategoryHomeImpl extends com.idega.data.IDOFactory implements ICInformationCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationCategory.class;
 }


 public ICInformationCategory create() throws javax.ejb.CreateException{
  return (ICInformationCategory) super.createIDO();
 }


public java.util.Collection findAvailableCategories(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICInformationCategoryBMPBean)entity).ejbFindAvailableCategories(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAvailableTopNodeCategories(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICInformationCategoryBMPBean)entity).ejbFindAvailableTopNodeCategories(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationCategory) super.findByPrimaryKeyIDO(pk);
 }



}
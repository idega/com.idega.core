package com.idega.core.category.data;


public class ICCategoryTranslationHomeImpl extends com.idega.data.IDOFactory implements ICCategoryTranslationHome
{
 protected Class getEntityInterfaceClass(){
  return ICCategoryTranslation.class;
 }


 public ICCategoryTranslation create() throws javax.ejb.CreateException{
  return (ICCategoryTranslation) super.createIDO();
 }


public java.util.Collection findAllByCategory(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICCategoryTranslationBMPBean)entity).ejbFindAllByCategory(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public ICCategoryTranslation findByCategoryAndLocale(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICCategoryTranslationBMPBean)entity).ejbFindByCategoryAndLocale(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public ICCategoryTranslation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICCategoryTranslation) super.findByPrimaryKeyIDO(pk);
 }



}
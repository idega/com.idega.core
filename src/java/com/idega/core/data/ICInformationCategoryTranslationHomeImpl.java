package com.idega.core.data;


public class ICInformationCategoryTranslationHomeImpl extends com.idega.data.IDOFactory implements ICInformationCategoryTranslationHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationCategoryTranslation.class;
 }


 public ICInformationCategoryTranslation create() throws javax.ejb.CreateException{
  return (ICInformationCategoryTranslation) super.createIDO();
 }


public java.util.Collection findAllByCategory(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICInformationCategoryTranslationBMPBean)entity).ejbFindAllByCategory(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public ICInformationCategoryTranslation findByCategoryAndLocale(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICInformationCategoryTranslationBMPBean)entity).ejbFindByCategoryAndLocale(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public ICInformationCategoryTranslation findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationCategoryTranslation) super.findByPrimaryKeyIDO(pk);
 }



}
package com.idega.core.data;


public interface ICInformationCategoryTranslationHome extends com.idega.data.IDOHome
{
 public ICInformationCategoryTranslation create() throws javax.ejb.CreateException;
 public ICInformationCategoryTranslation findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllByCategory(int p0)throws javax.ejb.FinderException;
 public ICInformationCategoryTranslation findByCategoryAndLocale(int p0,int p1)throws javax.ejb.FinderException;

}
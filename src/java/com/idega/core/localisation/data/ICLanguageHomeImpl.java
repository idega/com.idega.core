package com.idega.core.localisation.data;


public class ICLanguageHomeImpl extends com.idega.data.IDOFactory implements ICLanguageHome
{
 protected Class getEntityInterfaceClass(){
  return ICLanguage.class;
 }


 public ICLanguage create() throws javax.ejb.CreateException{
  return (ICLanguage) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICLanguageBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICLanguage) super.findByPrimaryKeyIDO(pk);
 }



}
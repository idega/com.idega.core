package com.idega.core.localisation.data;


public class ICLanguageHomeImpl extends com.idega.data.IDOFactory implements ICLanguageHome
{
 @Override
protected Class getEntityInterfaceClass(){
  return ICLanguage.class;
 }


 @Override
public ICLanguage create() throws javax.ejb.CreateException{
  return (ICLanguage) super.createIDO();
 }


@Override
public java.util.Collection findAll()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICLanguageBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public ICLanguage findByDescription(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICLanguageBMPBean)entity).ejbFindByDescription(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

@Override
public ICLanguage findByISOAbbreviation(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICLanguageBMPBean)entity).ejbFindByISOAbbreviation(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 @Override
public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICLanguage) super.findByPrimaryKeyIDO(pk);
 }


public java.util.Collection<ICLanguage> findManyByISOAbbreviation(java.util.Collection<java.lang.String> p0) throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICLanguageBMPBean)entity).ejbFindManyByISOAbbreviation(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

}
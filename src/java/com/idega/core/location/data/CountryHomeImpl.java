package com.idega.core.location.data;


public class CountryHomeImpl extends com.idega.data.IDOFactory implements CountryHome
{
 protected Class getEntityInterfaceClass(){
  return Country.class;
 }


 public Country create() throws javax.ejb.CreateException{
  return (Country) super.createIDO();
 }

 public java.util.Collection findAll()throws javax.ejb.FinderException{
		 com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		 java.util.Collection ids = ((CountryBMPBean)entity).ejbFindAll();
		 this.idoCheckInPooledEntity(entity);
		 return this.getEntityCollectionForPrimaryKeys(ids);
 }
 
public Country findByIsoAbbreviation(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((CountryBMPBean)entity).ejbFindByIsoAbbreviation(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public Country findByCountryName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((CountryBMPBean)entity).ejbFindByCountryName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Country) super.findByPrimaryKeyIDO(pk);
 }



}
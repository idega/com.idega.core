package com.idega.core.contact.data;


public class CountryCodeHomeImpl extends com.idega.data.IDOFactory implements CountryCodeHome
{
 protected Class getEntityInterfaceClass(){
  return CountryCode.class;
 }

 public CountryCode create() throws javax.ejb.CreateException{
  return (CountryCode) super.idoCreate();
 }

 public CountryCode createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public CountryCode findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (CountryCode) super.idoFindByPrimaryKey(id);
 }

 public CountryCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (CountryCode) super.idoFindByPrimaryKey(pk);
 }

 public CountryCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.data;


public class CountryHomeImpl extends com.idega.data.IDOFactory implements CountryHome
{
 protected Class getEntityInterfaceClass(){
  return Country.class;
 }

 public Country create() throws javax.ejb.CreateException{
  return (Country) super.idoCreate();
 }

 public Country createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Country findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Country) super.idoFindByPrimaryKey(id);
 }

 public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Country) super.idoFindByPrimaryKey(pk);
 }

 public Country findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
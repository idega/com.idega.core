package com.idega.core.data;


public class ICLocaleHomeImpl extends com.idega.data.IDOFactory implements ICLocaleHome
{
 protected Class getEntityInterfaceClass(){
  return ICLocale.class;
 }

 public ICLocale create() throws javax.ejb.CreateException{
  return (ICLocale) super.idoCreate();
 }

 public ICLocale createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICLocale findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICLocale) super.idoFindByPrimaryKey(id);
 }

 public ICLocale findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICLocale) super.idoFindByPrimaryKey(pk);
 }

 public ICLocale findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
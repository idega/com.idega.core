package com.idega.core.data;


public class ICLanguageHomeImpl extends com.idega.data.IDOFactory implements ICLanguageHome
{
 protected Class getEntityInterfaceClass(){
  return ICLanguage.class;
 }

 public ICLanguage create() throws javax.ejb.CreateException{
  return (ICLanguage) super.idoCreate();
 }

 public ICLanguage createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICLanguage findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICLanguage) super.idoFindByPrimaryKey(id);
 }

 public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICLanguage) super.idoFindByPrimaryKey(pk);
 }

 public ICLanguage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
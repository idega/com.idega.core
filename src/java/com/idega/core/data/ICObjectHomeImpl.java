package com.idega.core.data;


public class ICObjectHomeImpl extends com.idega.data.IDOFactory implements ICObjectHome
{
 protected Class getEntityInterfaceClass(){
  return ICObject.class;
 }

 public ICObject create() throws javax.ejb.CreateException{
  return (ICObject) super.idoCreate();
 }

 public ICObject createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICObject) super.idoFindByPrimaryKey(id);
 }

 public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObject) super.idoFindByPrimaryKey(pk);
 }

 public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
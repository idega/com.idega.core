package com.idega.data;


public class CacheableEntityHomeImpl extends com.idega.data.IDOFactory implements CacheableEntityHome
{
 protected Class getEntityInterfaceClass(){
  return CacheableEntity.class;
 }

 public CacheableEntity create() throws javax.ejb.CreateException{
  return (CacheableEntity) super.idoCreate();
 }

 public CacheableEntity createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public CacheableEntity findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (CacheableEntity) super.idoFindByPrimaryKey(id);
 }

 public CacheableEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (CacheableEntity) super.idoFindByPrimaryKey(pk);
 }

 public CacheableEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
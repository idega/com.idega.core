package com.idega.core.data;


public class GenericTypeHomeImpl extends com.idega.data.IDOFactory implements GenericTypeHome
{
 protected Class getEntityInterfaceClass(){
  return GenericType.class;
 }

 public GenericType create() throws javax.ejb.CreateException{
  return (GenericType) super.idoCreate();
 }

 public GenericType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public GenericType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GenericType) super.idoFindByPrimaryKey(id);
 }

 public GenericType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GenericType) super.idoFindByPrimaryKey(pk);
 }

 public GenericType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
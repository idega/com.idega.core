package com.idega.core.data;


public class PostalCodeHomeImpl extends com.idega.data.IDOFactory implements PostalCodeHome
{
 protected Class getEntityInterfaceClass(){
  return PostalCode.class;
 }

 public PostalCode create() throws javax.ejb.CreateException{
  return (PostalCode) super.idoCreate();
 }

 public PostalCode createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PostalCode findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PostalCode) super.idoFindByPrimaryKey(id);
 }

 public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PostalCode) super.idoFindByPrimaryKey(pk);
 }

 public PostalCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.user.data;


public class GenderHomeImpl extends com.idega.data.IDOFactory implements GenderHome
{
 protected Class getEntityInterfaceClass(){
  return Gender.class;
 }

 public Gender create() throws javax.ejb.CreateException{
  return (Gender) super.idoCreate();
 }

 public Gender createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Gender findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Gender) super.idoFindByPrimaryKey(id);
 }

 public Gender findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Gender) super.idoFindByPrimaryKey(pk);
 }

 public Gender findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.idegaweb.employment.data;


public class DivisionHomeImpl extends com.idega.data.IDOFactory implements DivisionHome
{
 protected Class getEntityInterfaceClass(){
  return Division.class;
 }

 public Division create() throws javax.ejb.CreateException{
  return (Division) super.idoCreate();
 }

 public Division createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Division findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Division) super.idoFindByPrimaryKey(id);
 }

 public Division findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Division) super.idoFindByPrimaryKey(pk);
 }

 public Division findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.data;


public class EmailHomeImpl extends com.idega.data.IDOFactory implements EmailHome
{
 protected Class getEntityInterfaceClass(){
  return Email.class;
 }

 public Email create() throws javax.ejb.CreateException{
  return (Email) super.idoCreate();
 }

 public Email createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Email) super.idoFindByPrimaryKey(id);
 }

 public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Email) super.idoFindByPrimaryKey(pk);
 }

 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.accesscontrol.data;


public class LoginInfoHomeImpl extends com.idega.data.IDOFactory implements LoginInfoHome
{
 protected Class getEntityInterfaceClass(){
  return LoginInfo.class;
 }

 public LoginInfo create() throws javax.ejb.CreateException{
  return (LoginInfo) super.idoCreate();
 }

 public LoginInfo createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public LoginInfo findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (LoginInfo) super.idoFindByPrimaryKey(id);
 }

 public LoginInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (LoginInfo) super.idoFindByPrimaryKey(pk);
 }

 public LoginInfo findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
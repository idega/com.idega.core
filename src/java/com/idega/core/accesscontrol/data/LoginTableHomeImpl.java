package com.idega.core.accesscontrol.data;


public class LoginTableHomeImpl extends com.idega.data.IDOFactory implements LoginTableHome
{
 protected Class getEntityInterfaceClass(){
  return LoginTable.class;
 }

 public LoginTable create() throws javax.ejb.CreateException{
  return (LoginTable) super.idoCreate();
 }

 public LoginTable createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public LoginTable findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (LoginTable) super.idoFindByPrimaryKey(id);
 }

 public LoginTable findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (LoginTable) super.idoFindByPrimaryKey(pk);
 }

 public LoginTable findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
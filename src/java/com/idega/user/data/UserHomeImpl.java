package com.idega.user.data;


public class UserHomeImpl extends com.idega.data.IDOFactory implements UserHome
{
 protected Class getEntityInterfaceClass(){
  return User.class;
 }

 public User create() throws javax.ejb.CreateException{
  return (User) super.idoCreate();
 }

 public User createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public User findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (User) super.idoFindByPrimaryKey(id);
 }

 public User findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (User) super.idoFindByPrimaryKey(pk);
 }

 public User findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.user.data;


public class UserGroupRepresentativeHomeImpl extends com.idega.data.IDOFactory implements UserGroupRepresentativeHome
{
 protected Class getEntityInterfaceClass(){
  return UserGroupRepresentative.class;
 }

 public UserGroupRepresentative create() throws javax.ejb.CreateException{
  return (UserGroupRepresentative) super.idoCreate();
 }

 public UserGroupRepresentative createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public UserGroupRepresentative findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (UserGroupRepresentative) super.idoFindByPrimaryKey(id);
 }

 public UserGroupRepresentative findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (UserGroupRepresentative) super.idoFindByPrimaryKey(pk);
 }

 public UserGroupRepresentative findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
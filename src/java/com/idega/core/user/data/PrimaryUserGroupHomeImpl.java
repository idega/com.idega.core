package com.idega.core.user.data;


public class PrimaryUserGroupHomeImpl extends com.idega.data.IDOFactory implements PrimaryUserGroupHome
{
 protected Class getEntityInterfaceClass(){
  return PrimaryUserGroup.class;
 }

 public PrimaryUserGroup create() throws javax.ejb.CreateException{
  return (PrimaryUserGroup) super.idoCreate();
 }

 public PrimaryUserGroup createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PrimaryUserGroup findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PrimaryUserGroup) super.idoFindByPrimaryKey(id);
 }

 public PrimaryUserGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PrimaryUserGroup) super.idoFindByPrimaryKey(pk);
 }

 public PrimaryUserGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.accesscontrol.data;


public class ICPermissionHomeImpl extends com.idega.data.IDOFactory implements ICPermissionHome
{
 protected Class getEntityInterfaceClass(){
  return ICPermission.class;
 }

 public ICPermission create() throws javax.ejb.CreateException{
  return (ICPermission) super.idoCreate();
 }

 public ICPermission createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICPermission findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICPermission) super.idoFindByPrimaryKey(id);
 }

 public ICPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICPermission) super.idoFindByPrimaryKey(pk);
 }

 public ICPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.accesscontrol.data;


public class ICObjectPermissionHomeImpl extends com.idega.data.IDOFactory implements ICObjectPermissionHome
{
 protected Class getEntityInterfaceClass(){
  return ICObjectPermission.class;
 }

 public ICObjectPermission create() throws javax.ejb.CreateException{
  return (ICObjectPermission) super.idoCreate();
 }

 public ICObjectPermission createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICObjectPermission findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICObjectPermission) super.idoFindByPrimaryKey(id);
 }

 public ICObjectPermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObjectPermission) super.idoFindByPrimaryKey(pk);
 }

 public ICObjectPermission findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
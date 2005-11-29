package com.idega.core.accesscontrol.data;


//public class PermissionGroupHomeImpl extends com.idega.user.data.GroupHomeImpl implements PermissionGroupHome
public class PermissionGroupHomeImpl extends com.idega.data.IDOFactory implements PermissionGroupHome
{
 protected Class getEntityInterfaceClass(){
  return PermissionGroup.class;
 }

 public PermissionGroup create()throws javax.ejb.CreateException{
  return (PermissionGroup)super.createIDO();
 }

 public PermissionGroup createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PermissionGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PermissionGroup) super.findByPrimaryKeyIDO(pk);
 }

 public PermissionGroup findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PermissionGroup) super.findByPrimaryKeyIDO(id);
 }


 public PermissionGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}
package com.idega.core.accesscontrol.data;


public class PermissionGroupHomeImpl extends com.idega.user.data.GroupHomeImpl implements PermissionGroupHome
{
 protected Class getEntityInterfaceClass(){
  return PermissionGroup.class;
 }

 public PermissionGroup createLegacy(){
	try{
		return (PermissionGroup)create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

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
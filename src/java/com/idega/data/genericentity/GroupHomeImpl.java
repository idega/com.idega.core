package com.idega.data.genericentity;


public class GroupHomeImpl extends com.idega.data.IDOFactory implements GroupHome
{
 protected Class getEntityInterfaceClass(){
  return Group.class;
 }

 public Group create() throws javax.ejb.CreateException{
  return (Group) super.idoCreate();
 }

 public Group createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Group findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Group) super.idoFindByPrimaryKey(id);
 }

 public Group findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Group) super.idoFindByPrimaryKey(pk);
 }

 public Group findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
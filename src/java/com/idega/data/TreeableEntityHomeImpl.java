package com.idega.data;


public class TreeableEntityHomeImpl extends com.idega.data.IDOFactory implements TreeableEntityHome
{
 protected Class getEntityInterfaceClass(){
  return TreeableEntity.class;
 }

 public TreeableEntity create() throws javax.ejb.CreateException{
  return (TreeableEntity) super.idoCreate();
 }

 public TreeableEntity createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public TreeableEntity findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (TreeableEntity) super.idoFindByPrimaryKey(id);
 }

 public TreeableEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (TreeableEntity) super.idoFindByPrimaryKey(pk);
 }

 public TreeableEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
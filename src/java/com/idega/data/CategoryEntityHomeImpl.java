package com.idega.data;


public class CategoryEntityHomeImpl extends com.idega.data.IDOFactory implements CategoryEntityHome
{
 protected Class getEntityInterfaceClass(){
  return CategoryEntity.class;
 }

 public CategoryEntity create() throws javax.ejb.CreateException{
  return (CategoryEntity) super.idoCreate();
 }

 public CategoryEntity createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public CategoryEntity findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (CategoryEntity) super.idoFindByPrimaryKey(id);
 }

 public CategoryEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (CategoryEntity) super.idoFindByPrimaryKey(pk);
 }

 public CategoryEntity findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.file.data;


public class ICFileCategoryHomeImpl extends com.idega.data.IDOFactory implements ICFileCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICFileCategory.class;
 }

 public ICFileCategory create() throws javax.ejb.CreateException{
  return (ICFileCategory) super.idoCreate();
 }

 public ICFileCategory createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICFileCategory findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICFileCategory) super.idoFindByPrimaryKey(id);
 }

 public ICFileCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICFileCategory) super.idoFindByPrimaryKey(pk);
 }

 public ICFileCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
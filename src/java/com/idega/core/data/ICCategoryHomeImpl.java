package com.idega.core.data;


public class ICCategoryHomeImpl extends com.idega.data.IDOFactory implements ICCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICCategory.class;
 }

 public ICCategory create() throws javax.ejb.CreateException{
  return (ICCategory) super.idoCreate();
 }

 public ICCategory createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICCategory findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICCategory) super.idoFindByPrimaryKey(id);
 }

 public ICCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICCategory) super.idoFindByPrimaryKey(pk);
 }

 public ICCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
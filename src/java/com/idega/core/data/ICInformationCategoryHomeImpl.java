package com.idega.core.data;


public class ICInformationCategoryHomeImpl extends com.idega.data.IDOFactory implements ICInformationCategoryHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationCategory.class;
 }

 public ICInformationCategory create() throws javax.ejb.CreateException{
  return (ICInformationCategory) super.idoCreate();
 }

 public ICInformationCategory createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICInformationCategory findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICInformationCategory) super.idoFindByPrimaryKey(id);
 }

 public ICInformationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationCategory) super.idoFindByPrimaryKey(pk);
 }

 public ICInformationCategory findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
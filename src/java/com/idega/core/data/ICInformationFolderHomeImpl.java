package com.idega.core.data;


public class ICInformationFolderHomeImpl extends com.idega.data.IDOFactory implements ICInformationFolderHome
{
 protected Class getEntityInterfaceClass(){
  return ICInformationFolder.class;
 }

 public ICInformationFolder create() throws javax.ejb.CreateException{
  return (ICInformationFolder) super.idoCreate();
 }

 public ICInformationFolder createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICInformationFolder findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICInformationFolder) super.idoFindByPrimaryKey(id);
 }

 public ICInformationFolder findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICInformationFolder) super.idoFindByPrimaryKey(pk);
 }

 public ICInformationFolder findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
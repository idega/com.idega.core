package com.idega.data;


public class MetaDataHomeImpl extends com.idega.data.IDOFactory implements MetaDataHome
{
 protected Class getEntityInterfaceClass(){
  return MetaData.class;
 }

 public MetaData create() throws javax.ejb.CreateException{
  return (MetaData) super.idoCreate();
 }

 public MetaData createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public MetaData findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (MetaData) super.idoFindByPrimaryKey(id);
 }

 public MetaData findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (MetaData) super.idoFindByPrimaryKey(pk);
 }

 public MetaData findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
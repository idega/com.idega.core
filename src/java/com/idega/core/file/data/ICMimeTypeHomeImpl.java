package com.idega.core.file.data;


public class ICMimeTypeHomeImpl extends com.idega.data.IDOFactory implements ICMimeTypeHome
{
 protected Class getEntityInterfaceClass(){
  return ICMimeType.class;
 }

 public ICMimeType create() throws javax.ejb.CreateException{
  return (ICMimeType) super.idoCreate();
 }

 public ICMimeType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICMimeType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICMimeType) super.idoFindByPrimaryKey(id);
 }

 public ICMimeType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICMimeType) super.idoFindByPrimaryKey(pk);
 }

 public ICMimeType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
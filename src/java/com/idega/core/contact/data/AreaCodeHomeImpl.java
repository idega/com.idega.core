package com.idega.core.contact.data;


public class AreaCodeHomeImpl extends com.idega.data.IDOFactory implements AreaCodeHome
{
 protected Class getEntityInterfaceClass(){
  return AreaCode.class;
 }

 public AreaCode create() throws javax.ejb.CreateException{
  return (AreaCode) super.idoCreate();
 }

 public AreaCode createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public AreaCode findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (AreaCode) super.idoFindByPrimaryKey(id);
 }

 public AreaCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (AreaCode) super.idoFindByPrimaryKey(pk);
 }

 public AreaCode findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
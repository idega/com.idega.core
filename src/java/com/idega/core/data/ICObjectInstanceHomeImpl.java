package com.idega.core.data;


public class ICObjectInstanceHomeImpl extends com.idega.data.IDOFactory implements ICObjectInstanceHome
{
 protected Class getEntityInterfaceClass(){
  return ICObjectInstance.class;
 }

 public ICObjectInstance create() throws javax.ejb.CreateException{
  return (ICObjectInstance) super.idoCreate();
 }

 public ICObjectInstance createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICObjectInstance findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICObjectInstance) super.idoFindByPrimaryKey(id);
 }

 public ICObjectInstance findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObjectInstance) super.idoFindByPrimaryKey(pk);
 }

 public ICObjectInstance findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
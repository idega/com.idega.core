package com.idega.core.net.data;


public class ICNetworkHomeImpl extends com.idega.data.IDOFactory implements ICNetworkHome
{
 protected Class getEntityInterfaceClass(){
  return ICNetwork.class;
 }

 public ICNetwork create() throws javax.ejb.CreateException{
  return (ICNetwork) super.idoCreate();
 }

 public ICNetwork createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICNetwork findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICNetwork) super.idoFindByPrimaryKey(id);
 }

 public ICNetwork findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICNetwork) super.idoFindByPrimaryKey(pk);
 }

 public ICNetwork findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.net.data;


public class ICProtocolHomeImpl extends com.idega.data.IDOFactory implements ICProtocolHome
{
 protected Class getEntityInterfaceClass(){
  return ICProtocol.class;
 }

 public ICProtocol create() throws javax.ejb.CreateException{
  return (ICProtocol) super.idoCreate();
 }

 public ICProtocol createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICProtocol findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICProtocol) super.idoFindByPrimaryKey(id);
 }

 public ICProtocol findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICProtocol) super.idoFindByPrimaryKey(pk);
 }

 public ICProtocol findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
package com.idega.core.data;


public class AddressHomeImpl extends com.idega.data.IDOFactory implements AddressHome
{
 protected Class getEntityInterfaceClass(){
  return Address.class;
 }

 public Address create() throws javax.ejb.CreateException{
  return (Address) super.idoCreate();
 }

 public Address createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Address findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Address) super.idoFindByPrimaryKey(id);
 }

 public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Address) super.idoFindByPrimaryKey(pk);
 }

 public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
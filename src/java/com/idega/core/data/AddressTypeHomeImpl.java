package com.idega.core.data;


public class AddressTypeHomeImpl extends com.idega.data.IDOFactory implements AddressTypeHome
{
 protected Class getEntityInterfaceClass(){
  return AddressType.class;
 }

 public AddressType create() throws javax.ejb.CreateException{
  return (AddressType) super.idoCreate();
 }

 public AddressType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public AddressType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (AddressType) super.idoFindByPrimaryKey(id);
 }

 public AddressType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (AddressType) super.idoFindByPrimaryKey(pk);
 }

 public AddressType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}
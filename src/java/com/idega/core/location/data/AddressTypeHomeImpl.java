package com.idega.core.location.data;


public class AddressTypeHomeImpl extends com.idega.data.IDOFactory implements AddressTypeHome
{
 protected Class getEntityInterfaceClass(){
  return AddressType.class;
 }


 public AddressType create() throws javax.ejb.CreateException{
  return (AddressType) super.createIDO();
 }


 public AddressType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public AddressType findAddressType2()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((AddressTypeBMPBean)entity).ejbFindAddressType2();
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public AddressType findAddressType1()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((AddressTypeBMPBean)entity).ejbFindAddressType1();
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public AddressType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (AddressType) super.findByPrimaryKeyIDO(pk);
 }


 public AddressType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (AddressType) super.findByPrimaryKeyIDO(id);
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
package com.idega.core.location.data;

import java.util.Collection;

import javax.ejb.FinderException;


public class AddressHomeImpl extends com.idega.data.IDOFactory implements AddressHome
{
 protected Class getEntityInterfaceClass(){
  return Address.class;
 }


 public Address create() throws javax.ejb.CreateException{
  return (Address) super.createIDO();
 }


 public Address createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


 public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Address) super.findByPrimaryKeyIDO(pk);
 }


 public Address findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Address) super.findByPrimaryKeyIDO(id);
 }


 public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


public com.idega.core.location.data.AddressType getAddressType2()throws java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	com.idega.core.location.data.AddressType theReturn = ((AddressBMPBean)entity).ejbHomeGetAddressType2();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public com.idega.core.location.data.AddressType getAddressType1()throws java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	com.idega.core.location.data.AddressType theReturn = ((AddressBMPBean)entity).ejbHomeGetAddressType1();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public Address findPrimaryUserAddress(int userID)throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((AddressBMPBean)entity).ejbFindPrimaryUserAddress(userID);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}


public Address findUserAddressByAddressType(int userID,AddressType type)throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((AddressBMPBean)entity).ejbFindUserAddressByAddressType( userID,type);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}
public java.util.Collection findPrimaryUserAddresses(String[] userIDs)throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AddressBMPBean)entity).ejbFindPrimaryUserAddresses(userIDs);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findPrimaryUserAddresses(com.idega.data.IDOQuery query)throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AddressBMPBean)entity).ejbFindPrimaryUserAddresses(query);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findUserAddressesByAddressType(int userID,AddressType type) throws javax.ejb.FinderException {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((AddressBMPBean)entity).ejbFindUserAddressesByAddressType(userID,type);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

	/* (non-Javadoc)
	 * @see com.idega.core.location.data.AddressHome#findByPostalCode(java.lang.Integer)
	 */
	public Collection findByPostalCode(Integer postalCodeID)throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AddressBMPBean)entity).ejbFindByPostalCode(postalCodeID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
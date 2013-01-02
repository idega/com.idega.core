package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOQuery;

public class AddressHomeImpl extends IDOFactory implements AddressHome {

	private static final long serialVersionUID = -3649183726541993776L;

	@Override
	public Class<Address> getEntityInterfaceClass() {
		return Address.class;
	}

	@Override
	public Address create() throws CreateException {
		return (Address) super.createIDO();
	}

	 @Override
	public Address createLegacy(){
			try{
				return create();
			}
			catch(javax.ejb.CreateException ce){
				throw new RuntimeException("CreateException:"+ce.getMessage());
			}

		 }

	@Override
	public Address findByPrimaryKey(Object pk) throws FinderException {
		return (Address) super.findByPrimaryKeyIDO(pk);
	}

	 @Override
	public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
			try{
				return findByPrimaryKey(id);
			}
			catch(javax.ejb.FinderException fe){
				throw new java.sql.SQLException(fe.getMessage());
			}

		 }

	@Override
	public AddressType getAddressType1() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType1();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public AddressType getAddressType2() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType2();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	@Override
	public Address findPrimaryUserAddress(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindPrimaryUserAddress(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Address findUserAddressByAddressType(int userID, AddressType type)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindUserAddressByAddressType(
				userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findPrimaryUserAddresses(String[] userIDs)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(userIDs);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findPrimaryUserAddresses(IDOQuery query)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(query);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findUserAddressesByAddressType(int userID,
			AddressType type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindUserAddressesByAddressType(userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findByPostalCode(Integer postalCodeID)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindByPostalCode(postalCodeID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Address findByStreetAddress(String address) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindByStreetAddress(address);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
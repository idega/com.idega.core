/**
 * 
 */
package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
import com.idega.data.IDOQuery;

/**
 * @author bluebottle
 *
 */
public class AddressHomeImpl extends IDOFactory implements AddressHome {
	protected Class getEntityInterfaceClass() {
		return Address.class;
	}

	public Address create() throws javax.ejb.CreateException {
		return (Address) super.createIDO();
	}

	public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Address) super.findByPrimaryKeyIDO(pk);
	}

	public Address createLegacy() {
		try {
			return create();
		} catch (javax.ejb.CreateException ce) {
			throw new RuntimeException("CreateException:" + ce.getMessage());
		}
	}

	public Address findByPrimaryKey(int id) throws javax.ejb.FinderException {
		return (Address) super.findByPrimaryKeyIDO(id);
	}

	public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException {
		try {
			return findByPrimaryKey(id);
		} catch (javax.ejb.FinderException fe) {
			throw new java.sql.SQLException("FinderException:"
					+ fe.getMessage());
		}
	}

	public AddressType getAddressType1() throws RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType1();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public AddressType getAddressType2() throws RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType2();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Address findPrimaryUserAddress(int userID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindPrimaryUserAddress(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Address findUserAddressByAddressType(int userID, AddressType type)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindUserAddressByAddressType(
				userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findPrimaryUserAddresses(String[] userIDs)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(userIDs);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findPrimaryUserAddresses(IDOQuery query)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(query);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findUserAddressesByAddressType(int userID,
			AddressType type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AddressBMPBean) entity)
				.ejbFindUserAddressesByAddressType(userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCode(Integer postalCodeID)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AddressBMPBean) entity)
				.ejbFindByPostalCode(postalCodeID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}

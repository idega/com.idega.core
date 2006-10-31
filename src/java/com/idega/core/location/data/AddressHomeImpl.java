package com.idega.core.location.data;


import com.idega.data.IDOQuery;
import java.util.Collection;
import javax.ejb.CreateException;
import java.sql.SQLException;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class AddressHomeImpl extends IDOFactory implements AddressHome {
	public Class getEntityInterfaceClass() {
		return Address.class;
	}

	public Address create() throws CreateException {
		return (Address) super.createIDO();
	}

	public Address findByPrimaryKey(Object pk) throws FinderException {
		return (Address) super.findByPrimaryKeyIDO(pk);
	}

	public Address createLegacy() {
		try {
			return create();
		} catch (CreateException ce) {
			throw new RuntimeException(ce.getMessage());
		}
	}

	public Address findByPrimaryKey(int id) throws FinderException {
		return (Address) super.findByPrimaryKeyIDO(id);
	}

	public Address findByPrimaryKeyLegacy(int id) throws SQLException {
		try {
			return findByPrimaryKey(id);
		} catch (FinderException fe) {
			throw new SQLException(fe.getMessage());
		}
	}

	public AddressType getAddressType1() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity).ejbHomeGetAddressType1();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public AddressType getAddressType2() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity).ejbHomeGetAddressType2();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Address findPrimaryUserAddress(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindPrimaryUserAddress(userID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Address findUserAddressByAddressType(int userID, AddressType type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindUserAddressByAddressType(userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findPrimaryUserAddresses(String[] userIDs) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity).ejbFindPrimaryUserAddresses(userIDs);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findPrimaryUserAddresses(IDOQuery query) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity).ejbFindPrimaryUserAddresses(query);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findUserAddressesByAddressType(int userID, AddressType type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity).ejbFindUserAddressesByAddressType(userID, type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCode(Integer postalCodeID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity).ejbFindByPostalCode(postalCodeID);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
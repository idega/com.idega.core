package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.data.IDOQuery;
import com.idega.data.IDOStoreException;
import com.idega.util.StringUtil;

public class AddressHomeImpl extends IDOFactory implements AddressHome {
	
	private static final long serialVersionUID = -3649183726541993776L;

	@Override
	public Class<Address> getEntityInterfaceClass() {
		return Address.class;
	}

	public Address create() throws CreateException {
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

	public Address findByPrimaryKey(Object pk) {
		if (pk != null) {
			try {
				return (Address) super.findByPrimaryKeyIDO(pk);
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, 
						"Failed to get " + getEntityBeanClass().getSimpleName() + 
						" by primary key: " + pk);
			}

		}

		return null;
	}
	
	 public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
		return findByPrimaryKey(id);
	 }

	public AddressType getAddressType1() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType1();
		return theReturn;
	}

	public AddressType getAddressType2() throws RemoteException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		AddressType theReturn = ((AddressBMPBean) entity)
				.ejbHomeGetAddressType2();
		return theReturn;
	}

	public Address findPrimaryUserAddress(int userID) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindPrimaryUserAddress(userID);
		return this.findByPrimaryKey(pk);
	}

	public Address findUserAddressByAddressType(int userID, AddressType type)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindUserAddressByAddressType(
				userID, type);
		return this.findByPrimaryKey(pk);
	}

	public Collection findPrimaryUserAddresses(String[] userIDs)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(userIDs);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findPrimaryUserAddresses(IDOQuery query)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((AddressBMPBean) entity)
				.ejbFindPrimaryUserAddresses(query);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findUserAddressesByAddressType(int userID,
			AddressType type) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<?> ids = ((AddressBMPBean) entity)
				.ejbFindUserAddressesByAddressType(userID, type);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection<Address> findByPostalCode(Integer postalCodeID)
			throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<?> ids = ((AddressBMPBean) entity)
				.ejbFindByPostalCode(postalCodeID);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Address findByStreetAddress(String address) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressBMPBean) entity).ejbFindByStreetAddress(address);
		return this.findByPrimaryKey(pk);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.location.data.AddressHome#update(java.lang.String, java.lang.String)
	 */
	@Override
	public Address update(String primaryKey, String streetName) {
		return update(findByPrimaryKey(primaryKey), null, streetName, null, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.location.data.AddressHome#update(com.idega.core.location.data.Address, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Address update(
			Address address, 
			String streetNumber,
			String streetName,
			String city, 
			PostalCode postalCode) {
		if (address == null) {
			try {
				address = createEntity();
				Logger.getLogger(getClass().getName()).info(
						"New " + Address.class.getSimpleName() + 
						" sucessfully created!");
			} catch (CreateException e) {
				Logger.getLogger(getClass().getName()).log(
						Level.WARNING, 
						"Failed to create " + Address.class.getSimpleName() + 
						" cause of: ", e);
				return null;
			}
		}

		if (!StringUtil.isEmpty(streetNumber)) {
			address.setStreetNumber(streetNumber);
		}

		if (!StringUtil.isEmpty(streetName)) {
			address.setStreetName(streetName);
		}

		if (!StringUtil.isEmpty(city)) {
			address.setCity(city);
		}

		if (postalCode != null) {
			address.setPostalCode(postalCode);
		}

		try {
			address.store();
			Logger.getLogger(getClass().getName()).info(
					Address.class.getSimpleName() + " sucessfully updated!");
		} catch (IDOStoreException e) {
			Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to update " + Address.class.getSimpleName() + 
					" cause of: ", e);
		}

		return address;
	}
}
package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.data.IDOQuery;

public interface AddressHome extends IDOHome {
	public Address create() throws CreateException;

	/**
	 * 
	 * <p>Creates or updates {@link Address} entity.</p>
	 * @param primaryKey is {@link Address#getPrimaryKey()}, tries to update 
	 * existing one, when provided;
	 * @param streetName is {@link Address#getStreetAddress()}, 
	 * skipped if <code>null</code>;
	 * @return created/updated {@link Address} or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Address update(String primaryKey, String streetName);

	public Address findByPrimaryKey(Object pk) throws FinderException;

	public AddressType getAddressType1() throws RemoteException;

	public AddressType getAddressType2() throws RemoteException;

	public Address findPrimaryUserAddress(int userID) throws FinderException;

	public Address findUserAddressByAddressType(int userID, AddressType type)
			throws FinderException;

	public Collection findPrimaryUserAddresses(String[] userIDs)
			throws FinderException;

	public Collection findPrimaryUserAddresses(IDOQuery query)
			throws FinderException;

	public Collection findUserAddressesByAddressType(int userID,
			AddressType type) throws FinderException;

	public Collection findByPostalCode(Integer postalCodeID)
			throws FinderException;
	public Address createLegacy();
	
	public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
	 
	public Address findByStreetAddress(String address) throws FinderException;
}
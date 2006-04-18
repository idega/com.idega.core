/**
 * 
 */
package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.data.IDOQuery;

/**
 * @author bluebottle
 *
 */
public interface AddressHome extends IDOHome {
	public Address create() throws javax.ejb.CreateException;
	
	public Address createLegacy();

	public Address findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	public Address findByPrimaryKey(int id) throws javax.ejb.FinderException;

	public Address findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbHomeGetAddressType1
	 */
	public AddressType getAddressType1() throws RemoteException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbHomeGetAddressType2
	 */
	public AddressType getAddressType2() throws RemoteException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindPrimaryUserAddress
	 */
	public Address findPrimaryUserAddress(int userID) throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindUserAddressByAddressType
	 */
	public Address findUserAddressByAddressType(int userID, AddressType type)
			throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindPrimaryUserAddresses
	 */
	public Collection findPrimaryUserAddresses(String[] userIDs)
			throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindPrimaryUserAddresses
	 */
	public Collection findPrimaryUserAddresses(IDOQuery query)
			throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindUserAddressesByAddressType
	 */
	public Collection findUserAddressesByAddressType(int userID,
			AddressType type) throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#ejbFindByPostalCode
	 */
	public Collection findByPostalCode(Integer postalCodeID)
			throws FinderException;

}

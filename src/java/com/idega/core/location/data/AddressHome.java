package com.idega.core.location.data;


import com.idega.data.IDOQuery;
import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;
import java.rmi.RemoteException;

public interface AddressHome extends IDOHome {
	public Address create() throws CreateException;

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
}
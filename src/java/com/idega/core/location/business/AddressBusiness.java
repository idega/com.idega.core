/*
 * $Id: AddressBusiness.java,v 1.7 2006/02/25 16:18:05 laddi Exp $
 * Created on Nov 1, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.business.IBOService;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.AddressType;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;


/**
 * 
 *  Last modified: $Date: 2006/02/25 16:18:05 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.7 $
 */
public interface AddressBusiness extends IBOService {

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCountryHome
	 */
	public CountryHome getCountryHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCommuneHome
	 */
	public CommuneHome getCommuneHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getPostalCodeHome
	 */
	public PostalCodeHome getPostalCodeHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getEmailHome
	 */
	public EmailHome getEmailHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getAddressHome
	 */
	public AddressHome getAddressHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getPostalCodeAndCreateIfDoesNotExist
	 */
	public PostalCode getPostalCodeAndCreateIfDoesNotExist(String postCode, String name)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getPostalCodeAndCreateIfDoesNotExist
	 */
	public PostalCode getPostalCodeAndCreateIfDoesNotExist(String postCode, String name, Country country)
			throws CreateException, RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#connectPostalCodeToCommune
	 */
	public void connectPostalCodeToCommune(PostalCode postalCode, String Commune) throws RemoteException,
			CreateException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#createCommuneIfNotExisting
	 */
	public Commune createCommuneIfNotExisting(String Commune) throws RemoteException, CreateException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getOtherCommuneCreateIfNotExist
	 */
	public Commune getOtherCommuneCreateIfNotExist() throws CreateException, FinderException, RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#changePostalCodeNameWhenOnlyOneAddressRelated
	 */
	public PostalCode changePostalCodeNameWhenOnlyOneAddressRelated(PostalCode postalCode, String newName)
			throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getStreetNameFromAddressString
	 */
	public String getStreetNameFromAddressString(String addressString) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getStreetNumberFromAddressString
	 */
	public String getStreetNumberFromAddressString(String addressString) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getFullAddressString
	 */
	public String getFullAddressString(Address address) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getUpdatedAddressByFullAddressString
	 */
	public Address getUpdatedAddressByFullAddressString(Address address, String fullAddressString)
			throws RemoteException, CreateException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCountry
	 */
	public Country getCountry(String countryISOAbbreviation) throws RemoteException, FinderException;
	
	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCountryAndCreateIfDoesNotExist
	 */
	public Country getCountryAndCreateIfDoesNotExist(String countryName, String countryISOAbbr) throws RemoteException,
			CreateException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCommuneAndCreateIfDoesNotExist
	 */
	public Commune getCommuneAndCreateIfDoesNotExist(String communeName, String communeCode) throws RemoteException,
			CreateException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getMainAddressType
	 */
	public AddressType getMainAddressType() throws RemoteException;

	/**
	 * @see com.idega.core.location.business.AddressBusinessBean#getCOAddressType
	 */
	public AddressType getCOAddressType() throws RemoteException;
}

/*
 * $Id: PostalCodeHome.java,v 1.8 2008/06/06 00:08:05 eiki Exp $
 * Created on 2.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws CreateException;

	/**
	 * 
	 * @param primaryKey is {@link PostalCode#getPrimaryKey()};
	 * @param postalCode is {@link PostalCode#getPostalCode()}, not <code>null</code>;
	 * @param name is {@link PostalCode#getName()}, what is city of that postal code;
	 * @return created or found {@link PostalCode}s by given 
	 * {@link PostalCode#getPostalCode()} or {@link Collections#emptyList()}
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public List<PostalCode> update(Object primaryKey, String postalCode, String name);

	public PostalCode findByPrimaryKey(Object pk) throws FinderException;

	public Collection findByCommune(Commune commune) throws FinderException;

	/**
	 * 
	 * @param codes is {@link Collection} of {@link PostalCode#getPostalCode()},
	 * not <code>null</code>;
	 * @return {@link PostalCode}s from data source or {@link Collections#emptyList()}
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Collection<PostalCode> findByPostalCode(Collection<String> codes);

	public PostalCode findByPostalCode(String code) throws FinderException;

	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException;

	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException;

	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException;

	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException;

	public Collection findByCountry(Object countryPK) throws FinderException;

	public Collection findAllUniqueNames() throws RemoteException, FinderException;

	public Collection findAll() throws FinderException;

	public Collection findAllOrdererByCode() throws FinderException;

	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException;

	public Collection findByPostalCodeFromTo(String[] codeFrom, String[] codeTo) throws FinderException;
}
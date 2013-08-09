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

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws CreateException;

	public PostalCode findByPrimaryKey(Object pk) throws FinderException;

	public Collection findByCommune(Commune commune) throws FinderException;

	public Collection<PostalCode> findByPostalCode(Collection codes) throws FinderException;

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
/*
 * $Id: PostalCodeHomeImpl.java,v 1.8 2008/06/06 00:08:05 eiki Exp $
 * Created on 2.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class PostalCodeHomeImpl extends IDOFactory implements PostalCodeHome {

	public Class getEntityInterfaceClass() {
		return PostalCode.class;
	}

	public PostalCode create() throws CreateException {
		return (PostalCode) super.createIDO();
	}

	public PostalCode findByPrimaryKey(Object pk) throws FinderException {
		return (PostalCode) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findByCommune(Commune commune) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByCommune(commune);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.location.data.PostalCodeHome#findByPostalCode(java.util.Collection)
	 */
	@Override
	public Collection<PostalCode> findByPostalCode(Collection<String> codes) {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection<Object> ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCode(codes);
		this.idoCheckInPooledEntity(entity);

		try {
			return this.getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Failed to get postal codes by primary keys:" + 
					ids);
		}

		return Collections.emptyList();
	}

	public PostalCode findByPostalCode(String code) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCode(code);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeAndCountryId(code, countryId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllByCountryIdOrderedByPostalCode(countryId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(countryId);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByNameAndCountry(name, countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByCountry(Object countryPK) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByCountry(countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllUniqueNames() throws RemoteException, FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllUniqueNames();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllOrdererByCode() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllOrdererByCode();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String[] codeFrom, String[] codeTo) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.location.data.PostalCodeHome#update(java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public List<PostalCode> update(Object primaryKey, String postalCode, String name) {
		List<PostalCode> postalCodes = new ArrayList<PostalCode>();
		
		/* Trying by primary key */
		if (primaryKey != null) {
			try {
				PostalCode code = findByPrimaryKey(primaryKey);
				if (code != null) {
					postalCodes.add(code);
				}
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, 
						"Failed to find postal code by primary key: " + primaryKey.toString());
			}
		}

		/* Trying by postal code */
		if (ListUtil.isEmpty(postalCodes) && !StringUtil.isEmpty(postalCode)) {
			Collection<PostalCode> existingPostals = findByPostalCode(Arrays.asList(postalCode));
			if (!ListUtil.isEmpty(existingPostals)) {
				postalCodes.addAll(existingPostals);
			}
		}
		
		/* If nothing found, creating new one */
		if (ListUtil.isEmpty(postalCodes) && !StringUtil.isEmpty(postalCode)) {
			PostalCode postal = null;
			try {
				postal = create();
			} catch (CreateException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Failed to create postal code cause of: ", e);
			}

			postalCodes.add(postal);
		}

		/* Updating data of postal codes */
		if (!StringUtil.isEmpty(postalCode)) {
			for (PostalCode pc : postalCodes) {
				pc.setPostalCode(postalCode);
				pc.setName(name);
				pc.store();
			}
		}

		return postalCodes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.location.data.PostalCodeHome#findUpdatedByPostalCode(java.util.Collection)
	 */
	@Override
	public List<PostalCode> findUpdatedByPostalCode(Collection<String> codes) {
		if (ListUtil.isEmpty(codes)) {
			return Collections.emptyList();
		}

		/* Collections are mutable, so better copy it */
		ArrayList<String> zipCodes = new ArrayList<String>(codes);

		/* Getting existing postal codes */
		Collection<PostalCode> postalCodeEntities = findByPostalCode(codes);

		/* Removing found postal codes */
		for (PostalCode code : postalCodeEntities) {
			zipCodes.remove(code.getPostalCode());
		}

		/* Creating postal codes, which not existed */
		for (String zipCode: zipCodes) {
			postalCodeEntities.addAll(update(null, zipCode, null));
		}

		return new ArrayList<PostalCode>(postalCodeEntities);
	}
}
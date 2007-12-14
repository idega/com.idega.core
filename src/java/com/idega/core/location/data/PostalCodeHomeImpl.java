/*
 * $Id: PostalCodeHomeImpl.java,v 1.7 2007/12/14 13:40:19 alexis Exp $
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

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2007/12/14 13:40:19 $ by $Author: alexis $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.7 $
 */
public class PostalCodeHomeImpl extends IDOFactory implements PostalCodeHome {

	protected Class getEntityInterfaceClass() {
		return PostalCode.class;
	}

	public PostalCode create() throws javax.ejb.CreateException {
		return (PostalCode) super.createIDO();
	}

	public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (PostalCode) super.findByPrimaryKeyIDO(pk);
	}
	
	public Collection findByPostalCode(Collection codes) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCode(codes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public PostalCode findByPostalCode(String code) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCode(code);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeAndCountryId(code, countryId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllByCountryIdOrderedByPostalCode(countryId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(countryId);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByNameAndCountry(name, countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByCountry(Object countryPK) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByCountry(countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllUniqueNames() throws RemoteException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllUniqueNames();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllOrdererByCode() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllOrdererByCode();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String[] codeFrom, String[] codeTo) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}

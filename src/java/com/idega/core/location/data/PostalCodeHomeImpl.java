/*
 * $Id: PostalCodeHomeImpl.java,v 1.2 2004/09/13 15:09:50 joakim Exp $
 * Created on 13.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2004/09/13 15:09:50 $ by $Author: joakim $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.2 $
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

	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeAndCountryId(code, countryId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllByCountryIdOrderedByPostalCode(countryId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeFindByName(name);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findAllUniqueNames() throws RemoteException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeFindAllUniqueNames();
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findAll() throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllOrdererByCode() throws FinderException, RemoteException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllOrdererByCode();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}
}

/*
 * $Id: PostalCodeHome.java,v 1.3 2004/11/23 13:51:03 gimmi Exp $
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
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2004/11/23 13:51:03 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.3 $
 */
public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws javax.ejb.CreateException;

	public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByPostalCodeAndCountryId
	 */
	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException, RemoteException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllByCountryIdOrderedByPostalCode
	 */
	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException, RemoteException;

	
  public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId)throws FinderException,RemoteException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeFindByName
	 */
	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeFindAllUniqueNames
	 */
	public Collection findAllUniqueNames() throws RemoteException, FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException, RemoteException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllOrdererByCode
	 */
	public Collection findAllOrdererByCode() throws FinderException, RemoteException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeFindByPostalCodeFromTo
	 */
	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException;
}

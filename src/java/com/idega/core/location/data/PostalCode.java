/*
 * $Id: PostalCode.java,v 1.6 2005/06/02 16:14:28 gimmi Exp $
 * Created on 2.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import java.util.Collection;
import com.idega.data.IDOEntity;
import com.idega.data.IDOStoreException;


/**
 * 
 *  Last modified: $Date: 2005/06/02 16:14:28 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.6 $
 */
public interface PostalCode extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setPostalCode
	 */
	public void setPostalCode(String code);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getPostalCode
	 */
	public String getPostalCode();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCommuneID
	 */
	public void setCommuneID(String commune);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCommuneID
	 */
	public String getCommuneID();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCommune
	 */
	public void setCommune(Commune commune);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCommune
	 */
	public Commune getCommune();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCountry
	 */
	public void setCountry(Country country);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCountry
	 */
	public Country getCountry();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCountryID
	 */
	public void setCountryID(int country_id);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCountryID
	 */
	public int getCountryID();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getPostalAddress
	 */
	public String getPostalAddress();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getAddresses
	 */
	public Collection getAddresses();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#isEqualTo
	 */
	public boolean isEqualTo(PostalCode postal);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#store
	 */
	public void store() throws IDOStoreException;
}

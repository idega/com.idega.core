/*
 * $Id: PostalCode.java,v 1.4 2004/09/13 15:09:50 joakim Exp $
 * Created on 13.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import java.util.Collection;
import com.idega.data.IDOEntity;


/**
 * 
 *  Last modified: $Date: 2004/09/13 15:09:50 $ by $Author: joakim $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.4 $
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
}

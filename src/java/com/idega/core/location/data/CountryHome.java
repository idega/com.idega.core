package com.idega.core.location.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * @author gimmi
 */
public interface CountryHome extends IDOHome {

	public Country create() throws javax.ejb.CreateException;

	public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#ejbFindByIsoAbbreviation
	 */
	public Country findByIsoAbbreviation(String abbreviation) throws FinderException;

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#ejbFindByCountryName
	 */
	public Country findByCountryName(String name) throws FinderException;

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#ejbFindAllFromPostalCodes
	 */
	public Collection findAllFromPostalCodes(Collection postalCodes) throws FinderException;
}

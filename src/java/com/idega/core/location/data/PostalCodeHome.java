package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * @author gimmi
 */
public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws javax.ejb.CreateException;

	public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByPostalCodeAndCountryId
	 */
	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllByCountryIdOrderedByPostalCode
	 */
	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName
	 */
	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByNameAndCountry
	 */
	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByCountry
	 */
	public Collection findByCountry(Object countryPK) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllUniqueNames
	 */
	public Collection findAllUniqueNames() throws RemoteException, FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllOrdererByCode
	 */
	public Collection findAllOrdererByCode() throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeFindByPostalCodeFromTo
	 */
	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException;
}

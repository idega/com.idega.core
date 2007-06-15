package com.idega.core.location.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;
import java.rmi.RemoteException;

public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws CreateException;

	public PostalCode findByPrimaryKey(Object pk) throws FinderException;

	public Collection findByCommune(Commune commune) throws FinderException;

	public Collection findByPostalCode(Collection codes) throws FinderException;

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
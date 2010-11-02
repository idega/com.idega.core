/**
 * 
 */
package com.idega.core.location.dao;

import java.util.List;

import com.idega.business.SpringBeanName;
import com.idega.core.location.data.bean.Address;
import com.idega.core.location.data.bean.AddressType;
import com.idega.core.location.data.bean.Commune;
import com.idega.core.location.data.bean.Country;
import com.idega.core.location.data.bean.PostalCode;
import com.idega.core.location.data.bean.Province;
import com.idega.user.data.bean.Group;

@SpringBeanName("addressDAO")
public interface AddressDAO {

	public Address createAddress(String streetName, String streetNumber, String streetAddressNominative, String postalBox, AddressType addressType, PostalCode postalCode, Commune commune, Country country);

	public Address getAddress(Integer addressID);

	public AddressType getMainAddressType();

	public AddressType getCOAddressType();

	public AddressType getAddressType(String uniqueName);

	public PostalCode createPostalCode(String postalCode, String name, Commune commune, Country country);

	public PostalCode getPostalCode(Integer postalCodeID);

	public List<PostalCode> getAllPostalCodes();

	public PostalCode getPostalCode(String postalCode);

	public Commune createCommune(String name, String code, Province province, Group group, boolean isDefault, String websiteURL);

	public Commune getCommune(Integer communeID);

	public List<Commune> getAllCommunes();

	public Commune getCommuneByName(String name);

	public Commune getCommuneByCode(String code);

	public Commune getDefaultCommune();

	public Province createProvince(String name, Country country);

	public Province getProvince(Integer provinceID);

	public List<Province> getAllProvinces();

	public List<Province> getAllProvinces(Country country);

	public Province getProvince(String name, Country country);

	public Country createCountry(String name, String isoAbbreviation, String description);

	public Country getCountry(Integer countryID);

	public List<Country> getAllCountries();

	public Country getCountryByName(String name);

	public Country getCountryByISOAbbreviation(String isoAbbreviation);

}
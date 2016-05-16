/**
 *
 */
package com.idega.core.location.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.location.dao.AddressDAO;
import com.idega.core.location.data.bean.Address;
import com.idega.core.location.data.bean.AddressType;
import com.idega.core.location.data.bean.Commune;
import com.idega.core.location.data.bean.Country;
import com.idega.core.location.data.bean.PostalCode;
import com.idega.core.location.data.bean.Province;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.data.bean.Group;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("addressDAO")
@Transactional(readOnly = true)
public class AddressDAOImpl extends GenericDaoImpl implements AddressDAO {

	@Override
	public Collection<Address> findBy(Integer userId, String addressTypeName) {
		if (userId != null && !StringUtil.isEmpty(addressTypeName)) {
			return getResultList(
					Address.QUERY_FIND_BY_USER_AND_TYPE, 
					Address.class, 
					new Param("userID", userId), 
					new Param("uniqueName", addressTypeName));
		}

		return Collections.emptyList();
	}
	

	@Override
	@Transactional(readOnly = false)
	public Address createAddress(String streetName, String streetNumber, String streetAddressNominative, String postalBox, AddressType addressType, PostalCode postalCode, Commune commune, Country country) {
		Address address = new Address();
		address.setStreetName(streetName);
		address.setStreetNumber(streetNumber);
		address.setStreetAddressNominative(streetAddressNominative);
		address.setPostalBox(postalBox);
		address.setAddressType(addressType);
		address.setPostalCode(postalCode);
		address.setCommune(commune);
		address.setCountry(country);
		persist(address);

		return address;
	}

	@Override
	public Address getAddress(Integer addressID) {
		return find(Address.class, addressID);
	}

	@Override
	public AddressType getMainAddressType() {
		return getAddressType(AddressType.MAIN_ADDRESS_TYPE);
	}

	@Override
	public AddressType getCOAddressType() {
		return getAddressType(AddressType.CO_ADDRESS_TYPE);
	}

	@Override
	public AddressType getAddressType(String uniqueName) {
		Param param = new Param("uniqueName", uniqueName);
		return getSingleResult("addressType.findByUniqueName", AddressType.class, param);
	}

	@Override
	@Transactional(readOnly = false)
	public PostalCode createPostalCode(String postalCode, String name, Commune commune, Country country) {
		PostalCode code = new PostalCode();
		code.setPostalCode(postalCode);
		code.setName(name);
		code.setPostalAddress(getPostalAddress(postalCode, name));
		code.setCommune(commune);
		code.setCountry(country);
		persist(code);

		return code;
	}

	@Override
	public PostalCode getPostalCode(Integer postalCodeID) {
		return find(PostalCode.class, postalCodeID);
	}

	@Override
	public List<PostalCode> getAllPostalCodes() {
		return getResultList("postalCode.findAll", PostalCode.class);
	}

	@Override
	public PostalCode getPostalCode(String postalCode) {
		Param param = new Param("postalCode", postalCode);
		return getSingleResult("postalCode.findByPostalCode", PostalCode.class, param);
	}

	private String getPostalAddress(String postalCode, String name) {
		StringBuffer buffer = new StringBuffer();

		boolean addSpace = false;
		if (postalCode != null && postalCode.length() > 0) {
			buffer.append(postalCode);
			addSpace = true;
		}
		if (name != null && name.length() > 0) {
			if (addSpace) {
				buffer.append(" ");
			}
			buffer.append(name);
		}

		return buffer.toString();
	}

	@Override
	@Transactional(readOnly = false)
	public Commune createCommune(String name, String code, Province province, Group group, boolean isDefault, String websiteURL) {
		Commune commune = new Commune();
		commune.setName(name);
		commune.setCommune(name.toUpperCase());
		commune.setCode(code);
		commune.setProvince(province);
		commune.setGroup(group);
		commune.setDefault(isDefault);
		commune.setWebsiteURL(websiteURL);
		persist(commune);

		return commune;
	}

	@Override
	public Commune getCommune(Integer communeID) {
		return find(Commune.class, communeID);
	}

	@Override
	public List<Commune> getAllCommunes() {
		return getResultList("commune.findAll", Commune.class);
	}

	@Override
	public Commune getCommuneByName(String name) {
		Param param = new Param("commune", name.toUpperCase());
		return getSingleResult("commune.findByName", Commune.class, param);
	}

	@Override
	public Commune getCommuneByCode(String code) {
		Param param = new Param("code", code);
		return getSingleResult("commune.findByCode", Commune.class, param);
	}

	@Override
	public Commune getDefaultCommune() {
		return getSingleResult("commune.findDefaultCommune", Commune.class);
	}

	@Override
	@Transactional(readOnly = false)
	public Province createProvince(String name, Country country) {
		Province province = new Province();
		province.setName(name);
		province.setCountry(country);
		persist(province);

		return province;
	}

	@Override
	public Province getProvince(Integer provinceID) {
		return find(Province.class, provinceID);
	}

	@Override
	public List<Province> getAllProvinces() {
		return getResultList("province.findAll", Province.class);
	}

	@Override
	public List<Province> getAllProvinces(Country country) {
		Param param = new Param("country", country);
		return getResultList("province.findAllByCountry", Province.class, param);
	}

	@Override
	public Province getProvince(String name, Country country) {
		Param paramName = new Param("name", name);
		Param paramCountry = new Param("country", country);
		return getSingleResult("province.findByNameAndCountry", Province.class, paramName, paramCountry);
	}

	@Override
	@Transactional(readOnly = false)
	public Country createCountry(String name, String isoAbbreviation, String description) {
		Country country = new Country();
		country.setName(name);
		country.setISOAbbreviation(isoAbbreviation);
		country.setDescription(description);
		persist(country);

		return country;
	}

	@Override
	public Country getCountry(Integer countryID) {
		return find(Country.class, countryID);
	}

	@Override
	public List<Country> getAllCountries() {
		return getResultList("country.findAll", Country.class);
	}

	@Override
	public Country getCountryByName(String name) {
		Param param = new Param("name", name);
		return getSingleResult("country.findByName", Country.class, param);
	}

	@Override
	public Country getCountryByISOAbbreviation(String isoAbbreviation) {
		Param param = new Param("isoAbbreviation", isoAbbreviation);
		return getSingleResult("country.findByISOAbbreviation", Country.class, param);
	}
	
	@Override
	@Transactional(readOnly = false)
	public Address update(Address entity) {
		if (entity != null) {
			if (getAddress(entity.getId()) == null) {
				persist(entity);
				if (entity.getId() != null) {
					return entity;
				}
			} else {
				entity = merge(entity);
				if (entity != null) {
					return entity;
				}
			}
		}

		return null;
	}
}
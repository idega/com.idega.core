//idega 2000 - eiki

package com.idega.core.data;

import java.sql.*;
import java.util.Collection;
import java.rmi.RemoteException;
import com.idega.data.*;
import com.idega.util.text.TextSoap;

import javax.ejb.*;
import com.idega.core.user.data.User;

public class AddressBMPBean extends com.idega.data.GenericEntity implements Address {
	private transient AddressTypeHome addressTypeHome;
	private static AddressType type1; //for caching
	private static AddressType type2; //for caching

	public AddressBMPBean() {
		super();
	}

	public AddressBMPBean(int id) throws SQLException {
		super(id);
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(getColumnNameAddressTypeId(), "Address type", AddressType.class);
		addAttribute("street_name", "Street Name", true, true, String.class, 80);
		addAttribute("street_number", "Street number", true, true, String.class, 30);
		addAttribute("city", "City", true, true, String.class, 50);
		addAttribute("province", "Province", true, true, String.class, 50);
		addAttribute("p_o_box", "PostBox", true, true, String.class, 50);
		addManyToOneRelationship("postal_code_id", "PostalCode", PostalCode.class);
		addManyToOneRelationship("ic_country_id", "Country", Country.class);
		this.addManyToManyRelationShip(User.class, "ic_user_address");
	}

	public static String getColumnNameAddressTypeId() {
		return "ic_address_type_id";
	}

	public String getEntityName() {
		return "ic_address";
	}

	public void setDefaulValues() {
	}

	public String getName() {
		return getStreetName() + " " + getStreetNumber();
	}

	public String getStreetName() {
		return (String) getColumnValue("street_name");
	}

	/**
	 * All names are stored in uppercase, uses String.toUpperCase();
	 */
	public void setStreetName(String street_name) {
		setColumn("street_name", street_name.toUpperCase());
	}

	public String getStreetNumber() {
		return getStringColumnValue("street_number");
	}

	public void setStreetNumber(String street_number) {
		setColumn("street_number", street_number);
	}

	public void setStreetNumber(int street_number) {
		setColumn("street_number", street_number);
	}

	public String getCity() {
		return (String) getColumnValue("city");
	}

	public void setCity(String city) {
		setColumn("city", city);
	}

	public String getProvince() {
		return (String) getColumnValue("province");
	}

	public void setProvince(String province) {
		setColumn("province", province);
	}

	public String getPOBox() {
		return (String) getColumnValue("p_o_box");
	}

	public void setPOBox(String p_o_box) {
		setColumn("p_o_box", p_o_box);
	}

	public PostalCode getPostalCode() throws SQLException {
		return (PostalCode) getColumnValue("postal_code_id");
	}

	public int getPostalCodeID() {
		return getIntColumnValue("postal_code_id");
	}

	public void setPostalCode(PostalCode postalCode) {
		setColumn("postal_code_id", postalCode);
	}
	public void setPostalCodeID(int postal_code_id) {
		setColumn("postal_code_id", postal_code_id);
	}

	public AddressType getAddressType() {
		return (AddressType) getColumnValue("ic_address_type_id");
	}

	public void setAddressTypeID(int address_type_id) {
		setColumn("ic_address_type_id", address_type_id);
	}

	public void setAddressType(AddressType type) {
		setColumn("ic_address_type_id", type);
	}
	public int getAddressTypeID() {
		return getIntColumnValue("ic_address_type_id");
	}

	public Country getCountry() {
		return (Country) getColumnValue("ic_country_id");
	}

	public int getCountryId() {
		return getIntColumnValue("ic_country_id");
	}

	public void setCountry(Country country) {
		setColumn("ic_country_id", country);
	}
	public void setCountryId(int country_id) {
		setColumn("ic_country_id", country_id);
	}
	public AddressTypeHome getAddressTypeHome() throws RemoteException {
		if (addressTypeHome == null) {
			addressTypeHome = (AddressTypeHome) IDOLookup.getHome(AddressType.class);
		}
		return addressTypeHome;
	}

	public AddressType ejbHomeGetAddressType1() throws RemoteException {
		if (this.type1 == null) {
			try {
				type1 = getAddressTypeHome().findAddressType1();
			}
			catch (FinderException e) {
				throw new RemoteException(e.getMessage());
			}
		}

		return type1;

	}

	public AddressType ejbHomeGetAddressType2() throws RemoteException {
		if (this.type2 == null) {
			try {
				type2 = getAddressTypeHome().findAddressType2();
			}
			catch (FinderException e) {
				throw new RemoteException(e.getMessage());
			}
		}
		return type2;
	}
	
	/**
	* Gets the street name together with the street number
	* @return Returns the combined address string with capitalization.
	*/
	public String getStreetAddress() {
		StringBuffer addr = new StringBuffer();
		String street = getStreetName();
		if (street != null)
			addr.append(street).append(" ");
		String number = this.getStreetNumber();
		if (number != null)
			addr.append(number);
		return TextSoap.capitalize(addr.toString());
	}

	/**
	* Gets the postal code together with its name
	*/
	public String getPostalAddress() {
		try {
			return getPostalCode().getPostalAddress();
		}
		catch (Exception ex) {

		}
		return "";
	}

	public Integer ejbFindPrimaryUserAddress(int userID) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.*").appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iua.ic_user_id = ").append(userID).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return (Integer) super.idoFindOnePKBySQL(query.toString());
	}

	public Collection ejbFindPrimaryUserAddresses(String[] userIDs) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.*").appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iua.ic_user_id").appendInArray(userIDs).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindPrimaryUserAddresses(IDOQuery query) throws FinderException {
		IDOQuery sqlquery = idoQuery();
		sqlquery.appendSelect().append("a.*").appendFrom().append(getEntityName()).append(" a, ");
		sqlquery.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		sqlquery.appendAnd().append("iua.ic_user_id").appendIn(query).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(sqlquery.toString());
	}
}

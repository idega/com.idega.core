// idega 2000 - eiki

package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.user.data.User;
import com.idega.data.EntityAttribute;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.text.TextSoap;

public class AddressBMPBean extends com.idega.data.GenericEntity implements Address {

	private static final long serialVersionUID = 4314572105071916583L;

	public static final String SYNCHRONIZATION_KEY = "com.idega.core.location.data.Address";

	public static final String COLUMN_IC_COMMUNE_ID = "IC_COMMUNE_ID";
	public static final String ENTITY_NAME = "IC_ADDRESS";
	public static final String STREET_NAME = "STREET_NAME";
	public static final String ORIGINAL_STREET_NAME = "STREET_NAME_ORIGINAL";
	public static final String STREET_NUMBER = "STREET_NUMBER";
	public static final String CITY = "CITY";
	public static final String PROVINCE = "PROVINCE";
	public static final String P_O_BOX = "P_O_BOX";
	public static final String APPARTMENT_NUMBER = "APPARTMENT_NUMBER";
	public static final String POSTAL_CODE_ID = "POSTAL_CODE_ID";
	public static final String IC_ADDRESS_TYPE_ID = "IC_ADDRESS_TYPE_ID";
	public static final String IC_COUNTRY_ID = "IC_COUNTRY_ID";
	public static final String COORDINATE = "COORDINATE";
	public static final String COORDINATE_ID = "IC_ADDRESS_COORDINATE_ID";
	private transient AddressTypeHome addressTypeHome;
	private static AddressType type1; //for caching
	private static AddressType type2; //for caching
	public static final String STREET_ADDRESS_NOMINATIVE = "street_address_nominative";
	private boolean synchronizationEnabled = true;

	public AddressBMPBean() {
		super();
	}

	public AddressBMPBean(int id) throws SQLException {
		super(id);
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(getColumnNameAddressTypeId(), "Address type", AddressType.class);

		addAttribute(STREET_NAME, "Street Name", true, true, String.class, 150);
		EntityAttribute streetNameAttr = (EntityAttribute)getEntityDefinition().findFieldByUniqueName(STREET_NAME);
		streetNameAttr.setUniqueFieldName(Address.FIELD_STREET_NAME);

		addAttribute(STREET_NUMBER, "Street number", true, true, String.class, 30);
		EntityAttribute streetNumberAttr = (EntityAttribute) getEntityDefinition()
				.findFieldByUniqueName(STREET_NUMBER);
		streetNumberAttr.setUniqueFieldName(Address.FIELD_STREET_NUMBER);

		addAttribute(CITY, "City", true, true, String.class, 50);
		addAttribute(PROVINCE, "Province", true, true, String.class, 50);
		addAttribute(P_O_BOX, "PostBox", true, true, String.class, 50);
		addAttribute(APPARTMENT_NUMBER, "Appartment number", true, true, String.class, 50);
		addManyToOneRelationship(POSTAL_CODE_ID, "PostalCode", PostalCode.class);
		addManyToOneRelationship(IC_COUNTRY_ID, "Country", Country.class);
		this.addManyToManyRelationShip(User.class, "ic_user_address");
		this.addManyToOneRelationship(getColumnNameCommuneID(), Commune.class);
		addAttribute(ORIGINAL_STREET_NAME, "Original Street Name (before uppercase)", true, true, String.class, 150);

		addAttribute(STREET_ADDRESS_NOMINATIVE, "Address in nominative form", String.class);

		this.addManyToOneRelationship(COORDINATE_ID, AddressCoordinate.class);

		addIndex("IDX_ADDRESS_TYPE", getColumnNameAddressTypeId());
		addIndex("IDX_ADDRESS_STREET_NAME", STREET_NAME);
		addIndex("IDX_ADDR_ADDR_TYPE", new String[]{getIDColumnName(), getColumnNameAddressTypeId()});
	}

	public static String getColumnNameAddressTypeId() {
		return IC_ADDRESS_TYPE_ID;
	}

	public static String getColumnNameCommuneID() {
		return COLUMN_IC_COMMUNE_ID;
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	@Override
	public void setDefaulValues() {
	}

	@Override
	public String getName() {
		return getStreetAddress();
	}

	@Override
	public String getStreetAddressNominative() {
		return getStringColumnValue(STREET_ADDRESS_NOMINATIVE);
	}

	@Override
	public void setStreetAddressNominative(String address) {
		setColumn(STREET_ADDRESS_NOMINATIVE, address);
	}

	/**
	 * @return The street name as it was entered/save, before it was uppercased
	 */
	@Override
	public String getStreetNameOriginal() {
		return getStringColumnValue(ORIGINAL_STREET_NAME);
	}

	@Override
	public String getStreetName() {
		return (String) getColumnValue(STREET_NAME);
	}

	/**
	 * All names are stored in uppercase, uses String.toUpperCase();
	 * To get the unchaged street name use the method <code>getStreetNameOriginal()</code>
	 */
	@Override
	public void setStreetName(String street_name) {
		setColumn(ORIGINAL_STREET_NAME, street_name);
		if (!StringUtil.isEmpty(street_name)) {
			setColumn(STREET_NAME, street_name.toUpperCase());
		}
	}

	@Override
	public String getStreetNumber() {
		return getStringColumnValue(STREET_NUMBER);
	}

	@Override
	public void setStreetNumber(String street_number) {
		setColumn(STREET_NUMBER, street_number);
	}

	@Override
	public void setStreetNumber(int street_number) {
		setColumn(STREET_NUMBER, street_number);
	}

	@Override
	public String getCity() {
		return (String) getColumnValue(CITY);
	}

	@Override
	public void setCity(String city) {
		setColumn(CITY, city);
	}

	@Override
	public String getProvince() {
		return (String) getColumnValue(PROVINCE);
	}

	@Override
	public void setProvince(String province) {
		setColumn(PROVINCE, province);
	}

	@Override
	public String getPOBox() {
		return (String) getColumnValue(P_O_BOX);
	}

	@Override
	public void setPOBox(String p_o_box) {
		setColumn(P_O_BOX, p_o_box);
	}

	@Override
	public PostalCode getPostalCode() {
		return (PostalCode) getColumnValue(POSTAL_CODE_ID);
	}

	@Override
	public int getPostalCodeID() {
		return getIntColumnValue(POSTAL_CODE_ID);
	}

	@Override
	public void setPostalCode(PostalCode postalCode) {
		setColumn(POSTAL_CODE_ID, postalCode);
		if (postalCode != null) {
			if(postalCode.getCountryID()>0 && getCountry() == null){
				setColumn(IC_COUNTRY_ID,postalCode.getCountryID());
			}
		}
	}

	@Override
	public void setPostalCodeID(int postal_code_id) {
		setColumn(POSTAL_CODE_ID, postal_code_id);
	}

	@Override
	public AddressType getAddressType() {
		return (AddressType) getColumnValue(IC_ADDRESS_TYPE_ID);
	}

	@Override
	public void setAddressTypeID(int address_type_id) {
		setColumn(IC_ADDRESS_TYPE_ID, address_type_id);
	}

	@Override
	public void setAddressType(AddressType type) {
		setColumn(IC_ADDRESS_TYPE_ID, type);
	}

	@Override
	public int getAddressTypeID() {
		return getIntColumnValue(IC_ADDRESS_TYPE_ID);
	}

	@Override
	public AddressCoordinate getCoordinate() {
		String oldVal = getStringColumnValue(COORDINATE);
		if (oldVal != null) {
			try {
				AddressCoordinateHome acd = (AddressCoordinateHome) IDOLookup.getHome(AddressCoordinate.class);
				AddressCoordinate ac = null;
				try {
					ac = acd.findByCoordinate(oldVal);
				} catch (FinderException f) {
					ac = acd.create();
					ac.setCoordinate(oldVal);
					ac.store();
				}

				if (ac != null) {
					setCoordinate(ac);
				}
				removeFromColumn(COORDINATE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (AddressCoordinate) getColumnValue(COORDINATE_ID);
	}

	@Override
	public void setCoordinate(AddressCoordinate coordinate) {
		this.setColumn(COORDINATE_ID, coordinate);
	}

	@Override
	public Country getCountry() {
		Country country = (Country) getColumnValue(IC_COUNTRY_ID);
		if(country==null){
			PostalCode code = getPostalCode();
			if(code!=null){
				return code.getCountry();
			}
		}

		return country;
	}

	@Override
	public int getCountryId() {
		int id = getIntColumnValue(IC_COUNTRY_ID);
		if(id==-1){
			Country country = getCountry();
			if(country!=null){
				id = ((Integer)country.getPrimaryKey()).intValue();
			}
		}

		return id;
	}

	@Override
	public void setCountry(Country country) {
		setColumn(IC_COUNTRY_ID, country);
	}

	@Override
	public void setCountryId(int country_id) {
		setColumn(IC_COUNTRY_ID, country_id);
	}

	@Override
	public int getCommuneID() {
		return getIntColumnValue(getColumnNameCommuneID());
	}

	@Override
	public void setCommuneID(int communeId) {
		setColumn(getColumnNameCommuneID(), communeId);
	}

	@Override
	public void setCommune(Commune commune) {
		setColumn(getColumnNameCommuneID(), commune);
	}

	@Override
	public Commune getCommune() {
		return (Commune) getColumnValue(getColumnNameCommuneID());
	}

	@Override
	public AddressTypeHome getAddressTypeHome() throws RemoteException {
		if (this.addressTypeHome == null) {
			this.addressTypeHome = (AddressTypeHome) IDOLookup.getHome(AddressType.class);
		}
		return this.addressTypeHome;
	}

	public AddressType ejbHomeGetAddressType1() throws RemoteException {
		if (AddressBMPBean.type1 == null) {
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
		if (AddressBMPBean.type2 == null) {
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
	 *
	 * @return Returns the combined address string with capitalization.
	 */
	@Override
	public String getStreetAddress() {
		StringBuilder addr = new StringBuilder();
		String street = getStreetName();
		if (street != null) {
			addr.append(street).append(CoreConstants.SPACE);
		}
		String number = this.getStreetNumber();
		if (number != null) {
			addr.append(number);
		}
		String address = TextSoap.capitalize(addr.toString(), CoreConstants.SPACE);
		if (!StringUtil.isEmpty(number) && !StringHandler.isNumeric(number)) {
			String numberInLowerCase = number.toLowerCase();
			if (address.indexOf(numberInLowerCase) != -1) {
				address = StringHandler.replace(address, numberInLowerCase, number);
			}
		}
		return address;
	}

	/**
	 * Gets the postal code together with its name
	 */
	@Override
	public String getPostalAddress() {
		try {
			PostalCode postalCode = getPostalCode();
			if (postalCode == null) {
				return "";
			}
			return postalCode.getPostalAddress();
		}
		catch (Exception ex) {

		}
		return "";
	}

	public Integer ejbFindPrimaryUserAddress(int userID) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iat.ic_address_type_id = a.ic_address_type_id");
		query.appendAnd().append("iua.ic_user_id = ").append(userID).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return (Integer) super.idoFindOnePKBySQL(query.toString());
	}

	public Integer ejbFindUserAddressByAddressType(int userID, AddressType type) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ")
		.append("ic_user_address iua ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id")
		.appendAnd().append("iua.ic_user_id = ").append(userID).appendAnd().append("a.ic_address_type_id = ").append(((Integer) type.getPrimaryKey()).intValue());

		return (Integer) super.idoFindOnePKBySQL(query.toString());
	}

	public Integer ejbFindByStreetAddress(String address) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a ").appendWhereEquals("a." + STREET_ADDRESS_NOMINATIVE, "'" + address + "'");
		query.appendOrEquals("a." + ORIGINAL_STREET_NAME, "'" + address + "'");
		return (Integer) super.idoFindOnePKBySQL(query.toString());
	}

	public Collection<?> ejbFindPrimaryUserAddresses(String[] userIDs) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iat.ic_address_type_id").appendEqualSign().append("a.ic_address_type_id");
		query.appendAnd().append("iua.ic_user_id").appendInArray(userIDs).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(query.toString());
	}

	public Collection<?> ejbFindPrimaryUserAddresses(IDOQuery query) throws FinderException {
		IDOQuery sqlquery = idoQuery();
		sqlquery.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		sqlquery.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		sqlquery.appendAnd().append("iat.ic_address_type_id").appendEqualSign().append("a.ic_address_type_id");
		sqlquery.appendAnd().append("iua.ic_user_id").appendIn(query).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(sqlquery.toString());
	}

	public Collection<?> ejbFindUserAddressesByAddressType(int userID, AddressType type) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iua.ic_user_id = ").append(userID).appendAnd().append("a.ic_address_type_id = ").append(((Integer) type.getPrimaryKey()).intValue());

		return super.idoFindPKsBySQL(query.toString());
	}

	public Collection<?> ejbFindByPostalCode(Integer postalCodeID)throws FinderException{
		return idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(POSTAL_CODE_ID,postalCodeID));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.core.location.data.Address#isSame(com.idega.core.location.data.Address)
	 */
	@Override
	public boolean isEqualTo(Address address) {
		boolean returner = false;
		if (address != null) {
			returner = getStreetAddress().equalsIgnoreCase(address.getStreetAddress());
			if (returner) {
				if (getPostalCode() != null && address.getPostalCode() != null) {
					returner = getPostalCode().isEqualTo(address.getPostalCode());
				}
			}
			if (returner) {
				returner =  getCountryId() == address.getCountryId();
			}
		}
		return returner;
	}

	@Override
	public String getAddress() {
		String name = getStreetNameOriginal();
		String number = getStreetNumber();
		StringBuilder address = new StringBuilder();
		if (name != null) {
			address.append(name);
		}
		if (!StringUtil.isEmpty(number)) {
			if (name != null) {
				address.append(CoreConstants.SPACE);
			}
			address.append(number);
		}
		return address.toString();
	}

	@Override
	public String getSynchronizerKey() {
		return SYNCHRONIZATION_KEY;
	}

	@Override
	public void setSynchronizationEnabled(boolean enabled) {
		synchronizationEnabled = enabled;
	}

	@Override
	public boolean isSynchronizationEnabled() {
		return synchronizationEnabled;
	}

	@Override
	public String getAppartmentNumber() {
		return getStringColumnValue(APPARTMENT_NUMBER);
	}

	@Override
	public void setAppartmentNumber(String appartmentNumber) {
		setColumn(APPARTMENT_NUMBER, appartmentNumber);
	}

}
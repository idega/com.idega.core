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
import com.idega.util.text.TextSoap;

public class AddressBMPBean extends com.idega.data.GenericEntity implements Address {

	private static final String COLUMN_IC_COMMUNE_ID = "IC_COMMUNE_ID";
	private static final String ENTITY_NAME = "IC_ADDRESS";
	private static final String STREET_NAME = "STREET_NAME";
	private static final String ORIGINAL_STREET_NAME = "STREET_NAME_ORIGINAL";
	private static final String STREET_NUMBER = "STREET_NUMBER";
	private static final String CITY = "CITY";
	private static final String PROVINCE = "PROVINCE";
	private static final String P_O_BOX = "P_O_BOX";
	private static final String POSTAL_CODE_ID = "POSTAL_CODE_ID";
	private static final String IC_ADDRESS_TYPE_ID = "IC_ADDRESS_TYPE_ID";
	private static final String IC_COUNTRY_ID = "IC_COUNTRY_ID";
	private static final String COORDINATE = "COORDINATE";
	private static final String COORDINATE_ID = "IC_ADDRESS_COORDINATE_ID";
	private transient AddressTypeHome addressTypeHome;
	private static AddressType type1; //for caching
	private static AddressType type2; //for caching
	private static final String STREET_ADDRESS_NOMINATIVE = "street_address_nominative";

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

	public void setDefaulValues() {
	}

	@Override
	public String getName() {
		return getStreetAddress();
	}

	public String getStreetAddressNominative() {
		return getStringColumnValue(STREET_ADDRESS_NOMINATIVE);
	}
	
	public void setStreetAddressNominative(String address) {
		setColumn(STREET_ADDRESS_NOMINATIVE, address);
	}
	
	/**
	 * @return The street name as it was entered/save, before it was uppercased
	 */
	public String getStreetNameOriginal() {
		return getStringColumnValue(ORIGINAL_STREET_NAME);
	}
	
	public String getStreetName() {
		return (String) getColumnValue(STREET_NAME);
	}

	/**
	 * All names are stored in uppercase, uses String.toUpperCase();
	 * To get the unchaged street name use the method <code>getStreetNameOriginal()</code>
	 */
	public void setStreetName(String street_name) {
		setColumn(ORIGINAL_STREET_NAME, street_name);
		setColumn(STREET_NAME, street_name.toUpperCase());
	}

	public String getStreetNumber() {
		return getStringColumnValue(STREET_NUMBER);
	}

	public void setStreetNumber(String street_number) {
		setColumn(STREET_NUMBER, street_number);
	}

	public void setStreetNumber(int street_number) {
		setColumn(STREET_NUMBER, street_number);
	}

	public String getCity() {
		return (String) getColumnValue(CITY);
	}

	public void setCity(String city) {
		setColumn(CITY, city);
	}

	public String getProvince() {
		return (String) getColumnValue(PROVINCE);
	}

	public void setProvince(String province) {
		setColumn(PROVINCE, province);
	}

	public String getPOBox() {
		return (String) getColumnValue(P_O_BOX);
	}

	public void setPOBox(String p_o_box) {
		setColumn(P_O_BOX, p_o_box);
	}

	public PostalCode getPostalCode() {
		return (PostalCode) getColumnValue(POSTAL_CODE_ID);
	}

	public int getPostalCodeID() {
		return getIntColumnValue(POSTAL_CODE_ID);
	}

	public void setPostalCode(PostalCode postalCode) {
		setColumn(POSTAL_CODE_ID, postalCode);
		if (postalCode != null) {
			if(postalCode.getCountryID()>0 && getCountry() == null){
				setColumn(IC_COUNTRY_ID,postalCode.getCountryID());
			}
		}
	}

	public void setPostalCodeID(int postal_code_id) {
		setColumn(POSTAL_CODE_ID, postal_code_id);
	}

	public AddressType getAddressType() {
		return (AddressType) getColumnValue(IC_ADDRESS_TYPE_ID);
	}

	public void setAddressTypeID(int address_type_id) {
		setColumn(IC_ADDRESS_TYPE_ID, address_type_id);
	}

	public void setAddressType(AddressType type) {
		setColumn(IC_ADDRESS_TYPE_ID, type);
	}

	public int getAddressTypeID() {
		return getIntColumnValue(IC_ADDRESS_TYPE_ID);
	}

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
	
	public void setCoordinate(AddressCoordinate coordinate) {
		this.setColumn(COORDINATE_ID, coordinate);
	}
	
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

	public void setCountry(Country country) {
		setColumn(IC_COUNTRY_ID, country);
	}

	public void setCountryId(int country_id) {
		setColumn(IC_COUNTRY_ID, country_id);
	}

	public int getCommuneID() {
		return getIntColumnValue(getColumnNameCommuneID());
	}

	public void setCommuneID(int communeId) {
		setColumn(getColumnNameCommuneID(), communeId);
	}

	public void setCommune(Commune commune) {
		setColumn(getColumnNameCommuneID(), commune);
	}

	public Commune getCommune() {
		return (Commune) getColumnValue(getColumnNameCommuneID());
	}

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
	public String getStreetAddress() {
		StringBuffer addr = new StringBuffer();
		String street = getStreetName();
		if (street != null) {
			addr.append(street).append(" ");
		}
		String number = this.getStreetNumber();
		if (number != null) {
			addr.append(number);
		}
		return TextSoap.capitalize(addr.toString(), " ");
	}

	/**
	 * Gets the postal code together with its name
	 */
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

	public Collection ejbFindPrimaryUserAddresses(String[] userIDs) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iat.ic_address_type_id").appendEqualSign().append("a.ic_address_type_id");
		query.appendAnd().append("iua.ic_user_id").appendInArray(userIDs).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(query.toString());
	}

	public Collection ejbFindPrimaryUserAddresses(IDOQuery query) throws FinderException {
		IDOQuery sqlquery = idoQuery();
		sqlquery.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		sqlquery.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		sqlquery.appendAnd().append("iat.ic_address_type_id").appendEqualSign().append("a.ic_address_type_id");
		sqlquery.appendAnd().append("iua.ic_user_id").appendIn(query).appendAnd().append("iat.unique_name = ").appendWithinSingleQuotes(AddressTypeBMPBean.ADDRESS_1);

		return super.idoFindPKsBySQL(sqlquery.toString());
	}

	public Collection ejbFindUserAddressesByAddressType(int userID, AddressType type) throws FinderException {
		IDOQuery query = idoQuery();
		query.appendSelect().append("a.").append(getIDColumnName()).appendFrom().append(getEntityName()).append(" a, ");
		query.append("ic_user_address iua, ic_address_type iat ").appendWhereEquals("a.ic_address_id", "iua.ic_address_id");
		query.appendAnd().append("iua.ic_user_id = ").append(userID).appendAnd().append("a.ic_address_type_id = ").append(((Integer) type.getPrimaryKey()).intValue());

		return super.idoFindPKsBySQL(query.toString());
	}
	
	public Collection ejbFindByPostalCode(Integer postalCodeID)throws FinderException{
		return idoFindPKsByQuery(idoQueryGetSelect().appendWhereEquals(POSTAL_CODE_ID,postalCodeID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.core.location.data.Address#isSame(com.idega.core.location.data.Address)
	 */
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

}

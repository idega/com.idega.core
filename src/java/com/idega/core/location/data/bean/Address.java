/**
 *
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.user.data.bean.User;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

@Entity
@Cacheable
@Table(name = Address.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "address.findByPostalCode", query = "select a from Address a where a.postalCode = :postalCode"),
	@NamedQuery(name = "address.findByUserAndAddressType", query = "select a from Address a join a.users u where u.userID = :userID and a.addressType = :addressType"),
	@NamedQuery(
			name = Address.QUERY_FIND_BY_USER_AND_TYPE, 
			query = 	"SELECT DISTINCT a FROM Address a "
					+ 	"JOIN a.users u "
					+ 	"ON u.userID = :userID "
					+ 	"JOIN a.addressType t ON t.uniqueName = :uniqueName")
})
public class Address implements Serializable {

	private static final long serialVersionUID = 2192075177636648876L;

	public static final String ENTITY_NAME = "ic_address";
	public static final String COLUMN_ADDRESS_ID = "ic_address_id";
	private static final String COLUMN_ADDRESS_TYPE = "ic_address_type_id";
	private static final String COLUMN_STREET_NAME = "street_name";
	private static final String COLUMN_ORIGINAL_STREET_NAME = "street_name_original";
	private static final String COLUMN_STREET_NUMBER = "street_number";
	private static final String COLUMN_ROAD_NUMBER = "road_number";
	private static final String COLUMN_ROOM_NUMBER = "room_number";
	private static final String COLUMN_STREET_ADDRESS_NOMINATIVE = "street_address_nominative";
	private static final String COLUMN_PO_BOX = "p_o_box";
	private static final String COLUMN_COMMUNE = "ic_commune_id";
	private static final String COLUMN_POSTAL_CODE = "postal_code_id";
	private static final String COLUMN_COUNTRY = "ic_country_id";
	private static final String COLUMN_ADDRESS_COORDINATE = "ic_address_coordinate_id";
	private static final String COLUMN_CITY = "city";
	
	public static final String QUERY_FIND_BY_USER_AND_TYPE = "address.findByUserAndType";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ADDRESS_ID)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_ADDRESS_TYPE)
	private AddressType addressType;

	@Column(name = COLUMN_STREET_NAME, length = 150)
	private String streetName;

	@Column(name = COLUMN_ORIGINAL_STREET_NAME, length = 150)
	private String streetNameOriginal;

	@Column(name = COLUMN_STREET_NUMBER, length = 30)
	private String streetNumber;

	@Column(name = COLUMN_ROAD_NUMBER, length = 30)
	private String roadNumber;

	@Column(name = COLUMN_ROOM_NUMBER, length = 30)
	private String roomNumber;

	@Column(name = COLUMN_STREET_ADDRESS_NOMINATIVE)
	private String streetAddressNominative;

	@Column(name = COLUMN_PO_BOX, length = 50)
	private String postalBox;

	@Column(name = COLUMN_CITY, length = 50)
	private String city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COMMUNE)
	private Commune commune;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_POSTAL_CODE)
	private PostalCode postalCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COUNTRY)
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_ADDRESS_COORDINATE)
	private AddressCoordinate addressCoordinate;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = User.class)
	@JoinTable(name = "ic_user_address", joinColumns = { @JoinColumn(name = COLUMN_ADDRESS_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> users;

	/**
	 * @return the addressID
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param addressID
	 *          the addressID to set
	 */
	public void setId(Integer addressID) {
		this.id = addressID;
	}

	/**
	 * @return the addressType
	 */
	public AddressType getAddressType() {
		return this.addressType;
	}

	/**
	 * @param addressType
	 *          the addressType to set
	 */
	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return this.streetName;
	}

	/**
	 * @param streetName
	 *          the streetName to set
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName != null ? streetName.toUpperCase() : null;
		this.streetNameOriginal = streetName;
	}

	/**
	 * @return the streetNameOriginal
	 */
	public String getStreetNameOriginal() {
		return this.streetNameOriginal;
	}

	/**
	 * @return the streetNumber
	 */
	public String getStreetNumber() {
		return this.streetNumber;
	}

	/**
	 * @param streetNumber
	 *          the streetNumber to set
	 */
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getRoadNumber() {
		return roadNumber;
	}

	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	/**
	 * @return the streetAddressNominative
	 */
	public String getStreetAddressNominative() {
		return this.streetAddressNominative;
	}

	/**
	 * @param streetAddressNominative
	 *          the streetAddressNominative to set
	 */
	public void setStreetAddressNominative(String streetAddressNominative) {
		this.streetAddressNominative = streetAddressNominative;
	}

	/**
	 * @return the postalBox
	 */
	public String getPostalBox() {
		return this.postalBox;
	}

	/**
	 * @param postalBox
	 *          the postalBox to set
	 */
	public void setPostalBox(String postalBox) {
		this.postalBox = postalBox;
	}

	/**
	 * @return the commune
	 */
	public Commune getCommune() {
		return this.commune;
	}

	/**
	 * @param commune
	 *          the commune to set
	 */
	public void setCommune(Commune commune) {
		this.commune = commune;
	}

	/**
	 * @return the postalCode
	 */
	public PostalCode getPostalCode() {
		return this.postalCode;
	}

	/**
	 * @param postalCode
	 *          the postalCode to set
	 */
	public void setPostalCode(PostalCode postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return this.country;
	}

	/**
	 * @param country
	 *          the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the addressCoordinate
	 */
	public AddressCoordinate getAddressCoordinate() {
		return this.addressCoordinate;
	}

	/**
	 * @param addressCoordinate
	 *          the addressCoordinate to set
	 */
	public void setAddressCoordinate(AddressCoordinate addressCoordinate) {
		this.addressCoordinate = addressCoordinate;
	}

	/**
	 * @return the users
	 */
	public List<User> getUsers() {
		return this.users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
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
}
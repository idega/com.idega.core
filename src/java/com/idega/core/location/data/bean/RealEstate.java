/**
 * 
 */
package com.idega.core.location.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Cacheable
@Table(name = RealEstate.ENTITY_NAME)
@NamedQuery(name = "realEstate.findByIdentifier", query = "select r from RealEstate r where r.landRegisterMapNumber = :landRegisterMapNumber and r.realEstateNumber = :number and  r.realEstateUnit = :unit and r.realEstateCode = :code")
public class RealEstate implements Serializable {

	private static final long serialVersionUID = -8323770435741559942L;

	public static final String ENTITY_NAME = "ic_real_estate";
	public static final String COLUMN_REAL_ESTATE_ID = "ic_real_estate_id";
	private static final String COLUMN_REAL_ESTATE_NUMBER = "real_estate_number";
	private static final String COLUMN_REAL_ESTATE_UNIT = "real_estate_unit";
	private static final String COLUMN_REAL_ESTATE_CODE = "real_estate_code";
	private static final String COLUMN_LAND_REGISTER_MAP_NUMBER = "land_register";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_USE = "real_estate_use";
	private static final String COLUMN_COMMENT = "real_estate_comment";
	private static final String COLUMN_STREET_NUMBER = "street_number";
	private static final String COLUMN_STREET = "street_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_REAL_ESTATE_ID)
	private Integer realEstateID;

	@Column(name = COLUMN_REAL_ESTATE_NUMBER)
	private String realEstateNumber;

	@Column(name = COLUMN_REAL_ESTATE_UNIT)
	private String realEstateUnit;

	@Column(name = COLUMN_REAL_ESTATE_CODE)
	private String realEstateCode;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_USE)
	private String use;

	@Column(name = COLUMN_COMMENT)
	private String comment;

	@Column(name = COLUMN_STREET_NUMBER)
	private String streetNumber;

	@Column(name = COLUMN_LAND_REGISTER_MAP_NUMBER, length = 22)
	private String landRegisterMapNumber;

	@ManyToOne
	@JoinColumn(name = COLUMN_STREET)
	private Street street;

	/**
	 * @return the realEstateID
	 */
	public Integer getId() {
		return this.realEstateID;
	}

	/**
	 * @param realEstateID
	 *          the realEstateID to set
	 */
	public void setId(Integer realEstateID) {
		this.realEstateID = realEstateID;
	}

	/**
	 * @return the realEstateNumber
	 */
	public String getRealEstateNumber() {
		return this.realEstateNumber;
	}

	/**
	 * @param realEstateNumber
	 *          the realEstateNumber to set
	 */
	public void setRealEstateNumber(String realEstateNumber) {
		this.realEstateNumber = realEstateNumber;
	}

	/**
	 * @return the realEstateUnit
	 */
	public String getRealEstateUnit() {
		return this.realEstateUnit;
	}

	/**
	 * @param realEstateUnit
	 *          the realEstateUnit to set
	 */
	public void setRealEstateUnit(String realEstateUnit) {
		this.realEstateUnit = realEstateUnit;
	}

	/**
	 * @return the realEstateCode
	 */
	public String getRealEstateCode() {
		return this.realEstateCode;
	}

	/**
	 * @param realEstateCode
	 *          the realEstateCode to set
	 */
	public void setRealEstateCode(String realEstateCode) {
		this.realEstateCode = realEstateCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the use
	 */
	public String getUse() {
		return this.use;
	}

	/**
	 * @param use
	 *          the use to set
	 */
	public void setUse(String use) {
		this.use = use;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * @param comment
	 *          the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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

	/**
	 * @return the landRegisterMapNumber
	 */
	public String getLandRegisterMapNumber() {
		return this.landRegisterMapNumber;
	}

	/**
	 * @param landRegisterMapNumber
	 *          the landRegisterMapNumber to set
	 */
	public void setLandRegisterMapNumber(String landRegisterMapNumber) {
		this.landRegisterMapNumber = landRegisterMapNumber;
	}

	/**
	 * @return the street
	 */
	public Street getStreet() {
		return this.street;
	}

	/**
	 * @param street
	 *          the street to set
	 */
	public void setStreet(Street street) {
		this.street = street;
	}
}
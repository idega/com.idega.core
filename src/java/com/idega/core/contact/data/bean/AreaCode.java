/**
 * 
 */
package com.idega.core.contact.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = AreaCode.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name = "areaCode.findAll", query = "select a from AreaCode a")
})
public class AreaCode implements Serializable {

	private static final long serialVersionUID = 6878733832777813933L;

	public static final String ENTITY_NAME = "ic_area_code";
	public static final String COLUMN_AREA_CODE_ID = "ic_area_code_id";
	private static final String COLUMN_COUNTRY_CODE = "ic_country_code_id";
	private static final String COLUMN_AREA_CODE = "area_code";
	private static final String COLUMN_NAME = "area_name";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_AREA_CODE_ID)
	private Integer areaCodeID;

	@Column(name = COLUMN_AREA_CODE, length = 10)
	private String areaCode;

	@Column(name = COLUMN_NAME)
	private String name;

	@ManyToOne
	@JoinColumn(name = COLUMN_COUNTRY_CODE)
	private CountryCode countryCode;

	/**
	 * @return the areaCodeID
	 */
	public Integer getId() {
		return this.areaCodeID;
	}

	/**
	 * @param areaCodeID
	 *          the areaCodeID to set
	 */
	public void setId(Integer areaCodeID) {
		this.areaCodeID = areaCodeID;
	}

	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return this.areaCode;
	}

	/**
	 * @param areaCode
	 *          the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
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
	 * @return the countryCode
	 */
	public CountryCode getCountryCode() {
		return this.countryCode;
	}

	/**
	 * @param countryCode
	 *          the countryCode to set
	 */
	public void setCountryCode(CountryCode countryCode) {
		this.countryCode = countryCode;
	}
}
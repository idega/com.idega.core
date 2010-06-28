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

import com.idega.core.location.data.bean.Country;

@Entity
@Table(name=CountryCode.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name="countryCode.findAll", query="select c from CountryCode c")
})
public class CountryCode implements Serializable {

	private static final long serialVersionUID = 6878733832777813933L;

	public static final String ENTITY_NAME = "ic_country_code";
	public static final String COLUMN_COUNTRY_CODE_ID = "ic_country_code_id";
	private static final String COLUMN_COUNTRY_CODE = "country_code";
	private static final String COLUMN_COUNTRY = "ic_country_id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_COUNTRY_CODE_ID)
	private Integer countryCodeID;
	
	@Column(name = COLUMN_COUNTRY_CODE, length = 10)
	private String countryCode;

	@ManyToOne
	@JoinColumn(name = COLUMN_COUNTRY)
	private Country country;

	/**
	 * @return the countryCodeID
	 */
	public Integer getId() {
		return this.countryCodeID;
	}
	
	/**
	 * @param countryCodeID the countryCodeID to set
	 */
	public void setId(Integer countryCodeID) {
		this.countryCodeID = countryCodeID;
	}
	
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return this.countryCode;
	}
	
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	/**
	 * @return the country
	 */
	public Country getCountry() {
		return this.country;
	}
	
	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}
}
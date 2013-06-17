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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Cacheable
@Table(name=Country.ENTITY_NAME)
@NamedQueries({
	@NamedQuery(name="country.findAll", query="select c from Country c order by c.name"),
	@NamedQuery(name="country.findByISOAbbreviation", query="select c from Country c where c.ISOAbbreviation = :isoAbbreviation"),
	@NamedQuery(name="country.findByName", query="select c from Country c where c.name = :name")
})
@XmlTransient
public class Country implements Serializable {

	private static final long serialVersionUID = -1327585207316065093L;

	public static final String ENTITY_NAME = "ic_country";
	public static final String COLUMN_COUNTRY_ID = "ic_country_id";
	private static final String COLUMN_NAME = "country_name";
	private static final String COLUMN_DESCRIPTION = "country_description";
	private static final String COLUMN_ISO_ABBREVIATION = "iso_abbreviation";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = Country.COLUMN_COUNTRY_ID)
	private Integer countryID;

	@Column(name = Country.COLUMN_NAME)
	private String name;

	@Column(name = Country.COLUMN_DESCRIPTION)
	private String description;

	@Column(name = Country.COLUMN_ISO_ABBREVIATION, unique = true, nullable = false)
	private String ISOAbbreviation;

	/**
	 * @return the countryID
	 */
	public Integer getId() {
		return this.countryID;
	}

	/**
	 * @param countryID the countryID to set
	 */
	public void setId(Integer countryID) {
		this.countryID = countryID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the ISOAbbreviation
	 */
	public String getISOAbbreviation() {
		return this.ISOAbbreviation;
	}

	/**
	 * @param abbreviation the ISOAbbreviation to set
	 */
	public void setISOAbbreviation(String abbreviation) {
		this.ISOAbbreviation = abbreviation;
	}

	@Override
	public String toString() {
		return getName();
	}
}
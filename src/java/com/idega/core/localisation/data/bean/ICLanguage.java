/**
 * 
 */
package com.idega.core.localisation.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = ICLanguage.ENTITY_NAME)
public class ICLanguage implements Serializable {

	private static final long serialVersionUID = 2677403944146484634L;

	public static final String ENTITY_NAME = "ic_language";
	public static final String COLUMN_LANGUAGE_ID = "ic_language_id";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_ISO_ABBREVIATION = "iso_abbreviation";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_LANGUAGE_ID)
	private Integer languageID;

	@Column(name = COLUMN_NAME)
	private String name;

	@Column(name = COLUMN_DESCRIPTION, length = 510)
	private String description;

	@Column(name = COLUMN_ISO_ABBREVIATION, length = 10)
	private String isoAbbreviation;

	/**
	 * @return the languageID
	 */
	public Integer getId() {
		return this.languageID;
	}

	/**
	 * @param languageID
	 *          the languageID to set
	 */
	public void setId(Integer languageID) {
		this.languageID = languageID;
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
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *          the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the isoAbbreviation
	 */
	public String getISOAbbreviation() {
		return this.isoAbbreviation;
	}

	/**
	 * @param isoAbbreviation
	 *          the isoAbbreviation to set
	 */
	public void setISOAbbreviation(String isoAbbreviation) {
		this.isoAbbreviation = isoAbbreviation;
	}
}

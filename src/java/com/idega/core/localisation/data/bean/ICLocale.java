/**
 * 
 */
package com.idega.core.localisation.data.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.idega.core.location.data.bean.Country;

@Entity
@Table(name = ICLocale.ENTITY_NAME)
public class ICLocale implements Serializable {

	private static final long serialVersionUID = -6482804106633989994L;

	public static final String ENTITY_NAME = "ic_locale";
	public static final String COLUMN_LOCALE_ID = "ic_locale_id";
	private static final String COLUMN_LOCALE = "locale";
	private static final String COLUMN_LANGUAGE_ID = "ic_language_id";
	private static final String COLUMN_COUNTRY_ID = "ic_country_id";
	private static final String COLUMN_IN_USE = "in_use";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_LOCALE_ID)
	private Integer localeID;

	@Column(name = COLUMN_LOCALE, length = 20)
	private String locale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_LANGUAGE_ID)
	private ICLanguage language;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COLUMN_COUNTRY_ID)
	private Country country;

	@Column(name = COLUMN_IN_USE, length = 1)
	private Character inUse;

	/**
	 * @return the localeID
	 */
	public Integer getId() {
		return this.localeID;
	}

	/**
	 * @param localeID
	 *          the localeID to set
	 */
	public void setId(Integer localeID) {
		this.localeID = localeID;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return this.locale;
	}

	/**
	 * @param locale
	 *          the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the language
	 */
	public ICLanguage getLanguage() {
		return this.language;
	}

	/**
	 * @param language
	 *          the language to set
	 */
	public void setLanguage(ICLanguage language) {
		this.language = language;
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
	 * @return the inUse
	 */
	public boolean getInUse() {
		if (this.inUse == null) {
			return false;
		}
		return this.inUse == 'Y';
	}

	/**
	 * @param inUse
	 *          the inUse to set
	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse ? 'Y' : 'N';
	}
}
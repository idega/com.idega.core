/**
 *
 */
package com.idega.core.localisation.data.bean;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.location.data.bean.Country;
import com.idega.util.CoreConstants;
import com.idega.util.DBUtil;
import com.idega.util.StringUtil;

@Entity
@Table(name = ICLocale.ENTITY_NAME)
@XmlTransient
@Cacheable
@NamedQueries({
	@NamedQuery(
			name = ICLocale.QUERY_GET_BY_LOCALES,
			query = "SELECT l FROM ICLocale l "
					+ "WHERE l.locale in (:locales) "
	)
})
public class ICLocale implements Serializable {

	private static final long serialVersionUID = -6482804106633989994L;

	public static final String	ENTITY_NAME = "ic_locale",
								COLUMN_LOCALE_ID = "ic_locale_id",
								COLUMN_LOCALE = "locale";
	private static final String COLUMN_LANGUAGE_ID = "ic_language_id",
								COLUMN_COUNTRY_ID = "ic_country_id",
								COLUMN_IN_USE = "in_use";
	
	public static final String QUERY_GET_BY_LOCALES = "ICLocale.getByLocale";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_LOCALE_ID)
	private Integer localeID;

	@Column(name = COLUMN_LOCALE, length = 100)
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
		language = DBUtil.getInstance().lazyLoad(language);
		return this.language;
	}

	private String languageId;

	public String getLanguageId() {
		if (!StringUtil.isEmpty(languageId))
			return languageId;

		if (StringUtil.isEmpty(locale)) {
			languageId = CoreConstants.EMPTY;
			return languageId;
		}

		Locale localeObj = ICLocaleBusiness.getLocaleFromLocaleString(locale);
		if (localeObj == null) {
			languageId = CoreConstants.EMPTY;
			return languageId;
		}

		languageId = localeObj.getLanguage();
		return languageId;
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
		country = DBUtil.getInstance().lazyLoad(country);
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

	@Override
	public String toString() {
		return "Locale ID: " + getId() + ", " + getLocale();
	}
}
package com.idega.core.location.data;

import java.util.Locale;

import com.idega.data.IDOEntity;


/**
 * @author gimmi
 */
public interface Country extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#getName
	 */
	public String getName();

	public String getName(Locale locale);

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#getIsoAbbreviation
	 */
	public String getIsoAbbreviation();

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#setName
	 */
	public void setName(String Name);

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#setDescription
	 */
	public void setDescription(String Description);

	/**
	 * @see com.idega.core.location.data.CountryBMPBean#setIsoAbbreviation
	 */
	public void setIsoAbbreviation(String IsoAbbreviation);

	public String getLocalizedName();
	public String getLocalizedName(Locale locale);

}
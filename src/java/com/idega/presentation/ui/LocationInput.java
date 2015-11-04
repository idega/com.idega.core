package com.idega.presentation.ui;

import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.remotescripting.RemoteScriptHandler;
import com.idega.presentation.ui.util.SelectorUtility;

/**
 * @author gimmi
 */
public class LocationInput extends InterfaceObject {

	public static final String PARAMETER_ACTION = "p_act";
	public static final String ACTION_UPDATE_CITIES = "a_uc";
	public static final String ACTION_UPDATE_POSTAL_CODE = "a_upc";

	public static final String PARAMETER_COUNTRY_ID = "pcd_";

	private String specifiedCountryID = null;
	private String specifiedCityName =  null;
	private String specifiedZipID = null;

	private String parCountryID = null;
	private String parCityName = null;
	private String parZipID = null;

	private DropdownMenu countryDrop = null;
	private DropdownMenu cityDrop = null;
	private DropdownMenu zipDrop = null;

	private Collection availableCountries = null;

	private String iframeName = "tmpFrame";
	private String separator = " ";

	public LocationInput() {
		this("lipc", "lipci", "lipz");
	}

	public LocationInput(String countryParameterName, String cityParameterName, String postalCodeParameterName) {
		this.parCountryID = countryParameterName;
		this.parCityName = cityParameterName;
		this.parZipID = postalCodeParameterName;

		this.iframeName = this.parCountryID + "_" + this.parCityName + "_" + this.zipDrop;

		setName(this.iframeName);

		this.countryDrop = new DropdownMenu(this.parCountryID);
		this.cityDrop = new DropdownMenu(this.parCityName);
		this.zipDrop = new DropdownMenu(this.parZipID);
	}

	@Override
	public Object clone() {
		LocationInput inp = (LocationInput) super.clone();
		if (this.countryDrop != null) {
			inp.countryDrop = (DropdownMenu) this.countryDrop.clone();
		}
		if (this.cityDrop != null) {
			inp.cityDrop = (DropdownMenu) this.cityDrop.clone();
		}
		if (this.zipDrop != null) {
			inp.zipDrop = (DropdownMenu) this.zipDrop.clone();
		}
		if (this.availableCountries != null) {
			inp.availableCountries = this.availableCountries;
		}
		return inp;
	}

	public DropdownMenu getCountryDropdown() {
		return this.countryDrop;
	}

	public DropdownMenu getCityDropdown() {
		return this.cityDrop;
	}

	public DropdownMenu getPostalCodeDropdown() {
		return this.zipDrop;
	}

	@Override
	public void main(IWContext iwc) throws Exception {

		String usedCountryID = iwc.getParameter(this.parCountryID);
		String usedCityName = iwc.getParameter(this.parCityName);
		String usedZipID = iwc.getParameter(this.parZipID);

		if (this.specifiedCountryID != null) {
			usedCountryID = this.specifiedCountryID;
		}
		if (this.specifiedCityName != null) {
			usedCityName = this.specifiedCityName;
		}
		if (this.specifiedZipID != null) {
			usedZipID = this.specifiedZipID;
		}

		CountryHome countryHome = (CountryHome) IDOLookup.getHome(Country.class);
		PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
		if ( this.availableCountries == null ) {
			this.availableCountries = countryHome.findAll();
		}
		Collection postalCodes = null;
		Collection cities = null;

		SelectorUtility su = new SelectorUtility();

		this.countryDrop.addMenuElement("-1", "Select a country");
		this.countryDrop.addMenuElements(this.availableCountries);
		if (usedCountryID != null) {
			cities = pcHome.getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(Integer.parseInt(usedCountryID));
			this.countryDrop.setSelectedElement(usedCountryID);
		}

		if (cities != null) {
			Iterator iter = cities.iterator();
			while (iter.hasNext()) {
				String city = (String) iter.next();
				this.cityDrop.addMenuElement(city, city);
			}
		}

		this.cityDrop.addFirstOption(new SelectOption("Select a city", "-1"));

		if (usedCityName != null) {
			postalCodes = pcHome.findByNameAndCountry(usedCityName,new Integer(usedCountryID));
			this.cityDrop.setSelectedElement(usedCityName);
		}

		this.zipDrop = (DropdownMenu) su.getSelectorFromIDOEntities(this.zipDrop, postalCodes, "getPostalAddress");
		this.zipDrop.addFirstOption(new SelectOption("Select a postal code", "-1"));
		if (usedZipID != null) {
			this.zipDrop.setSelectedElement(usedZipID);
		}

		boolean addSeparator = false;
		if (this.countryDrop.getParent() == null) {
			add(this.countryDrop);
			addSeparator = true;
		}
		if (this.cityDrop.getParent() == null) {
			if (addSeparator) {
				add(this.separator);
			}
			add(this.cityDrop);
		}
		if (this.zipDrop.getParent() == null) {
			if (addSeparator) {
				add(this.separator);
			}
			add(this.zipDrop);
		}

		RemoteScriptHandler rsh = new RemoteScriptHandler(this.countryDrop, this.cityDrop);
		rsh.setRemoteScriptCollectionClass(LocationInputCollectionHandler.class);
		rsh.addParameter(PARAMETER_ACTION, ACTION_UPDATE_CITIES);
		rsh.setToClear(this.zipDrop, "Select a city");
		add(rsh);

		RemoteScriptHandler rsh2 = new RemoteScriptHandler(this.cityDrop, this.zipDrop);
		rsh2.setRemoteScriptCollectionClass(LocationInputCollectionHandler.class);
		rsh2.addParameter(PARAMETER_ACTION, ACTION_UPDATE_POSTAL_CODE);
		rsh2.addParameter(PARAMETER_COUNTRY_ID, this.parCountryID);
		add(rsh2);

	}

	public void setSelectedPostalCode(Object postalCodePK) throws FinderException {
		try {
			PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
			PostalCode pc = pcHome.findByPrimaryKey(postalCodePK);

			this.specifiedCountryID = new Integer(pc.getCountryID()).toString();
			this.specifiedCityName = pc.getName();
			this.specifiedZipID = postalCodePK.toString();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setStyleClass(String styleClass) {
		this.countryDrop.setStyleClass(styleClass);
		this.cityDrop.setStyleClass(styleClass);
		this.zipDrop.setStyleClass(styleClass);
	}

	public void setAvailableCountries(Collection countries) {
		this.availableCountries = countries;
	}

	@Override
	public boolean isContainer() {
		return false;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public void handleKeepStatus(IWContext iwc) {
		try {
			super.handleKeepStatus(iwc);
		} catch (AssertionError e) {
			return;
		}
		// Done in main
	}

}

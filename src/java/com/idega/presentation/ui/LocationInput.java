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
	
	private String iframeName = "tmpFrame";
	private String separator = " ";
	
	public LocationInput() {
		this("lipc", "lipci", "lipz");
	}
	
	public LocationInput(String countryParameterName, String cityParameterName, String postalCodeParameterName) {
		parCountryID = countryParameterName;
		parCityName = cityParameterName;
		parZipID = postalCodeParameterName;
		
		iframeName = parCountryID + "_" + parCityName + "_" + zipDrop;
		
		countryDrop = new DropdownMenu(parCountryID);
		cityDrop = new DropdownMenu(parCityName);
		zipDrop = new DropdownMenu(parZipID);
	}
	
	public Object clone() {
		LocationInput inp = (LocationInput) super.clone();
		if (countryDrop != null) {
			inp.countryDrop = (DropdownMenu) countryDrop.clone();
		}
		if (cityDrop != null) {
			inp.cityDrop = (DropdownMenu) cityDrop.clone();
		}
		if (zipDrop != null) {
			inp.zipDrop = (DropdownMenu) zipDrop.clone();
		}
		
		return inp;
	}
	
	public DropdownMenu getCountryDropdown() {
		return countryDrop;
	}
	
	public DropdownMenu getCityDropdown() {
		return cityDrop;
	}
	
	public DropdownMenu getPostalCodeDropdown() {
		return zipDrop;
	}
	
	public void main(IWContext iwc) throws Exception {

		String usedCountryID = iwc.getParameter(parCountryID);
		String usedCityName = iwc.getParameter(parCityName);
		String usedZipID = iwc.getParameter(parZipID);
		
		if (specifiedCountryID != null) {
			usedCountryID = specifiedCountryID;
		}
		if (specifiedCityName != null) {
			usedCityName = specifiedCityName;
		}
		if (specifiedZipID != null) {
			usedZipID = specifiedZipID;
		}

		CountryHome countryHome = (CountryHome) IDOLookup.getHome(Country.class);
		PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
		Collection countries = countryHome.findAll();
		Collection postalCodes = null;
		Collection cities = null;

		SelectorUtility su = new SelectorUtility();
		
		countryDrop.addMenuElements(countries);
		if (usedCountryID != null) {
			cities = pcHome.getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(Integer.parseInt(usedCountryID));
			countryDrop.setSelectedElement(usedCountryID);
		}
		
		if (cities != null) {
			Iterator iter = cities.iterator();
			while (iter.hasNext()) {
				String city = (String) iter.next();
				cityDrop.addMenuElement(city, city);
			}
		}
		if (cities == null) {
			cityDrop.addFirstOption(new SelectOption("Select a country", "-1"));
		} 
		
		if (usedCityName != null) {
			postalCodes = pcHome.findByNameAndCountry(usedCityName,new Integer(usedCountryID));
			cityDrop.setSelectedElement(usedCityName);
		}

		zipDrop = (DropdownMenu) su.getSelectorFromIDOEntities(zipDrop, postalCodes, "getPostalAddress");
		if (postalCodes == null) {
			zipDrop.addFirstOption(new SelectOption("Select a city", "-1"));
		}
		if (usedZipID != null) {
			zipDrop.setSelectedElement(usedZipID);
		}
				
		boolean addSeparator = false;
		if (countryDrop.getParent() == null) {
			add(countryDrop);
			addSeparator = true;
		}
		if (cityDrop.getParent() == null) {
			if (addSeparator) {
				add(separator);
			}
			add(cityDrop);
		}
		if (zipDrop.getParent() == null) {
			if (addSeparator) {
				add(separator);
			}
			add(zipDrop);
		}

		RemoteScriptHandler rsh = new RemoteScriptHandler(countryDrop, cityDrop);
		rsh.setRemoteScriptCollectionClass(LocationInputCollectionHandler.class);
		rsh.addParameter(PARAMETER_ACTION, ACTION_UPDATE_CITIES);
		rsh.setToClear(zipDrop, "Select a city");
		add(rsh);
			
		RemoteScriptHandler rsh2 = new RemoteScriptHandler(cityDrop, zipDrop);
		rsh2.setRemoteScriptCollectionClass(LocationInputCollectionHandler.class);
		rsh2.addParameter(PARAMETER_ACTION, ACTION_UPDATE_POSTAL_CODE);
		rsh2.addParameter(PARAMETER_COUNTRY_ID, parCountryID);
		add(rsh2);
		
	}
	
	public void setSelectedPostalCode(Object postalCodePK) throws FinderException {
		try {
			PostalCodeHome pcHome = (PostalCodeHome) IDOLookup.getHome(PostalCode.class);
			PostalCode pc = pcHome.findByPrimaryKey(postalCodePK);
			
			specifiedCountryID = new Integer(pc.getCountryID()).toString();
			specifiedCityName = pc.getName();
			specifiedZipID = postalCodePK.toString();
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} 
		
	}

	public boolean isContainer() {
		return false;
	}	
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void handleKeepStatus(IWContext iwc) {
		// Done in main
	}
	
}

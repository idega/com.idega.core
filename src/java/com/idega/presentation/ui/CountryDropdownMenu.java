package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.business.AddressBusiness;
import com.idega.core.data.Country;
//import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;


/**
 * A UI element that can display country names (localized) from the database. <br>
 * The default parameter it submits is the public static string IW_COUNTRY_MENU_PARAM_NAME and is the id for the Country
 * 
 * @author <a href= "eiki@idega.is">Eirikur S. Hrafnsson</a>
 */

public class CountryDropdownMenu extends DropdownMenu {
	
	private String selectedCountryName = null;
	private Country selectedCountry = null;
	public static final String IW_COUNTRY_MENU_PARAM_NAME="iw_country_id";
	
	public CountryDropdownMenu(){
		super(IW_COUNTRY_MENU_PARAM_NAME); 
	}
	
	public CountryDropdownMenu(String parameterName){
		super(parameterName); 
	}
	
	public void main(IWContext iwc) throws Exception{
		//TODO eiki cache countries
		super.main(iwc);
		List localeCountries = Arrays.asList(Locale.getISOCountries());
		//List locales = Arrays.asList( java.util.Locale.getAvailableLocales());
		//List locales = ICLocaleBusiness.listOfAllLocalesJAVA();
		Locale currentLocale = iwc.getCurrentLocale();

		//Iterator iter = locales.iterator();
		Iterator iter = localeCountries.iterator();
		
		Country country = null;
		String countryDisplayName = null;
		Map countries = new HashMap();
		String lang = currentLocale.getISO3Language();
		Locale locale;
		while (iter.hasNext()) {
			//Locale locale = (Locale) iter.next();
			String ISOCountry = (String ) iter.next();
			try {
				locale = new Locale(lang,ISOCountry);
				
				countryDisplayName = locale.getDisplayCountry(currentLocale);
				country = getAddressBusiness(iwc).getCountryHome().findByIsoAbbreviation(locale.getCountry());	
				
				if( countryDisplayName!=null && country!=null && !countries.containsKey(country.getPrimaryKey())){
					countries.put(country.getPrimaryKey(),country);//cache
					addMenuElement(((Integer)country.getPrimaryKey()).intValue(),countryDisplayName);
				}
			}
			catch (RemoteException e1) {
				e1.printStackTrace();
			}
			catch (EJBException e1) {
				e1.printStackTrace();
			}
			catch (FinderException e1) {
				//e1.printStackTrace();
			}
		}
		
		try {
			if( selectedCountryName!=null){
				selectedCountry = getAddressBusiness(iwc).getCountryHome().findByCountryName(selectedCountryName);	
			}
			// we must ensure no external selected country is set
			else if(selectedCountry==null){
				selectedCountry = getAddressBusiness(iwc).getCountryHome().findByIsoAbbreviation(currentLocale.getCountry());	
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		if(selectedCountry!=null){
			setSelectedElement(((Integer)selectedCountry.getPrimaryKey()).intValue());
		}
		
	}
	
	
	public void setSelectedCountry(Country country){
		this.selectedCountry = country;
	}

	public void setSelectedCountry(String countryName){
		this.selectedCountryName = countryName;
	}


	private AddressBusiness getAddressBusiness(IWApplicationContext iwc) throws RemoteException{
		return (AddressBusiness) IBOLookup.getServiceInstance(iwc,AddressBusiness.class);
	}
	
}

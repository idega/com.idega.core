package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
//import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;


/**
 * A UI element that can display country names (localized) from the database. <br>
 * The default parameter it submits is the public static string IW_COUNTRY_MENU_PARAM_NAME and is the id for the Country
 * 
 * @author <a href= "eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @author <a href= "aron@idega.is">Aron Birkir</a>
 */

public class CountryDropdownMenu extends DropdownMenu {
	
	private String selectedCountryName = null;
	private Country selectedCountry = null;
	public static final String IW_COUNTRY_MENU_PARAM_NAME="iw_country_id";
	private static Map countries = null;
	
	public CountryDropdownMenu(){
		super(IW_COUNTRY_MENU_PARAM_NAME); 
	}
	
	public CountryDropdownMenu(String parameterName){
		super(parameterName); 
	}
	
	private Country getCountryByISO(String ISO){
		if(countries==null) {
			initCountries();
		}
		if(countries.containsKey(ISO)) {
			return (Country) countries.get(ISO);
		}
		return null;
	}
	
	private void initCountries(){
		
		try {
			CountryHome countryHome = (CountryHome) IDOLookup.getHome(Country.class);
			Collection counts = countryHome.findAll();
			countries = new Hashtable(counts.size());
			for (Iterator iter = counts.iterator(); iter.hasNext();) {
				Country element = (Country) iter.next();
				countries.put(element.getIsoAbbreviation(),element);
			}
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
	}
	
	public void main(IWContext iwc) throws Exception{
		//TODO eiki cache countries 
		// some caching made by aron
		super.main(iwc);
		//System.out.println( "country dropdown main start "+ com.idega.util.IWTimestamp.RightNow().toString());
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
		List smallCountries = new Vector();
		//CountryHome countryHome = getAddressBusiness(iwc).getCountryHome();
		while (iter.hasNext()) {
			//Locale locale = (Locale) iter.next();
			
			String ISOCountry = (String ) iter.next();
			try {
				locale = new Locale(lang,ISOCountry);
				
				countryDisplayName = locale.getDisplayCountry(currentLocale);
				//country = countryHome.findByIsoAbbreviation(locale.getCountry());	
				country = getCountryByISO(locale.getCountry());
				
				if( countryDisplayName!=null && country!=null && !countries.containsKey(country.getPrimaryKey())){
					countries.put(country.getPrimaryKey(),country);//cache
					SmallCountry sCountry = new SmallCountry((Integer)country.getPrimaryKey(),countryDisplayName,ISOCountry,currentLocale);
					smallCountries.add(sCountry);
					//addMenuElement(((Integer)country.getPrimaryKey()).intValue(),countryDisplayName);
				}
			}
			catch (Exception e1) {
				//e1.printStackTrace();
			}
		}
		Collections.sort(smallCountries);
		
		for (Iterator iterator = smallCountries.iterator(); iterator.hasNext();) {
			SmallCountry sCountry = (SmallCountry) iterator.next();
			// we dont want the ISO code into the list
			if(!sCountry.name .equalsIgnoreCase(sCountry.code)) {
				addMenuElement(sCountry.getID().intValue(),sCountry.getName());
			}
		}
		
		try {
			if( this.selectedCountryName!=null){
				this.selectedCountry = getAddressBusiness(iwc).getCountryHome().findByCountryName(this.selectedCountryName);	
			}
			// we must ensure no external selected country is set
			else if(this.selectedCountry==null){
				this.selectedCountry = getAddressBusiness(iwc).getCountryHome().findByIsoAbbreviation(currentLocale.getCountry());	
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
		
		if(this.selectedCountry!=null){
			setSelectedElement(((Integer)this.selectedCountry.getPrimaryKey()).intValue());
		}
		//System.out.println( "country dropdown main end "+ com.idega.util.IWTimestamp.RightNow().toString());
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
	
	private class SmallCountry implements Comparable{
		String name;
		private Integer ID;
		String code;
		private Locale locale;
		
		public SmallCountry(Integer ID,String name,String code,Locale locale){
			this.name = name;
			this.ID = ID;
			this.code = code;
			this.locale = locale;
		}
		
		
		
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			Collator coll = Collator.getInstance(this.locale);
			return coll.compare( this.name,((SmallCountry)o).name);
				
		}

		/**
		 * @return
		 */
		public Integer getID() {
			return this.ID;
		}

		/**
		 * @return
		 */
		public String getName() {
			return this.name;
		}

	}
	
}

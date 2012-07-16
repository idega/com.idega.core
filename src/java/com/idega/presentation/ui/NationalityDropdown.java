package com.idega.presentation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

public class NationalityDropdown  extends DropdownMenu {
	
	 private Map nationalityLocaleMap = null;
	 
	 private CountryHome countryHome = null;
	  
	  private  Map createNationalityLocaleMap(){
		  String[][] array = {{"Afghan","Afghanistan"},{"Albanian","Albania"},{"Algerian","Algeria"},{"Andorran","Andorra"},{"Angolan","Angola"},{"Argentinian","Argentina"},{"Armenian","Armenia"},
		    		{"Australian","Australia"},{"Austrian","Austria"},{"Azerbaijani","Azerbaijan"},{"Bahamian","Bahamas"},{"Bahraini","Bahrain"},{"Bangladeshi","Bangladesh"},{"Barbadian","Barbados"},
		    		{"Belarusian","Belarus"},{"Belgian","Belgium"},{"Belizian","Belize"},{"Beninese","Benin"},{"Bhutanese","Bhutan"},{"Bolivian","Bolivia"},
		    		{"Bosnian","Bosnia and Herzegovina"},{"Botswanan","Botswana"},{"Brazilian","Brazil"},{"British","Britain"},{"Bruneian","Brunei"},{"Bulgarian","Bulgaria"},{"Burkinese","Burkina"},
		    		{"Burmese","Burma"},{"Burundian","Burundi"},{"Cambodian","Cambodia"},{"Cameroonian","Cameroon"},{"Canadian","Canada"},{"Cape Verdean","Cape Verde Islands"},
		    		{"Chadian","Chad"},{"Chilean","Chile"},{"Chinese","China"},{"Colombian","Colombia"},{"Congolese","Congo"},{"Costa Rican","Costa Rica"},{"Croatian","Croatia"},{"Cuban","Cuba"},
		    		{"Cypriot","Cyprus"},{"Czech","Czech Republic"},{"Danish","Denmark"},{"Djiboutian","Djibouti"},{"Dominican","Dominica"},{"Dominican","Dominican Republic"},{"Ecuadorean","Ecuador"},
		    		{"Egyptian","Egypt"},{"Salvadorean","El Salvador"},{"English","England"},{"Eritrean","Eritrea"},{"Estonian","Estonia"},{"Ethiopian","Ethiopia"},{"Fijian","Fiji"},{"Finnish","Finland"},
		    		{"French","France"},{"Gabonese","Gabon"},{"Gambian","Gambia, the"},{"Georgian","Georgia"},{"German","Germany"},{"Ghanaian","Ghana"},{"Greek","Greece"},{"Grenadian","Grenada"},
		    		{"Guatemalan","Guatemala"},{"Guinean","Guinea"},{"Guyanese","Guyana"},{"Haitian","Haiti"},{"Dutch","Holland"},{"Honduran","Honduras"},{"Hungarian","Hungary"},
		    		{"Icelandic","Iceland"},{"Indian","India"},{"Indonesian","Indonesia"},{"Iranian","Iran"},{"Iraqi","Iraq"},{"Irish","Ireland"},{"Israeli","Israel"},{"Italian","Italy"},
		    		{"Jamaican","Jamaica"},{"Japanese","Japan"},{"Jordanian","Jordan"},{"Kazakh","Kazakhstan"},{"Kenyan","Kenya"},{"Kuwaiti","Kuwait"},{"Laotian","Laos"},{"Latvian","Latvia"},
		    		{"Lebanese","Lebanon"},{"Liberian","Liberia"},{"Libyan","Libya"},{"Lithuanian","Lithuania"},{"Macedonian","Macedonia"},{"Madagascan","Madagascar"},
		    		{"Malawian","Malawi"},{"Malaysian","Malaysia"},{"Maldivian","Maldives"},{"Malian","Mali"},{"Maltese","Malta"},{"Mauritanian","Mauritania"},{"Mauritian","Mauritius"},
		    		{"Mexican","Mexico"},{"Moldovan","Moldova"},{"Monacan","Monaco"},{"Mongolian","Mongolia"},{"Montenegrin","Montenegro"},{"Moroccan","Morocco"},
		    		{"Mozambican","Mozambique"},{"Namibian","Namibia"},{"Nepalese","Nepal"},{"Dutch","Netherlands"},
		    		{"New Zealand","New Zealand"},{"Nicaraguan","Nicaragua"},
		    		{"Nigerien","Niger"},{"Nigerian","Nigeria"},{"North Korean","North Korea"},{"Norwegian","Norway"},{"Omani","Oman"},{"Pakistani","Pakistan"},
		    		{"Panamanian","Panama"},{"Guinean","Papua New Guinea"},{"Paraguayan","Paraguay"},{"Peruvian","Peru"},{"Philippine","the Philippines"},
		    		{"Polish","Poland"},{"Portuguese","Portugal"},{"Qatari","Qatar"},{"Romanian","Romania"},{"Russian","Russia"},{"Rwandan","Rwanda"},{"Saudi Arabian","Saudi Arabia"},
		    		{"Scottish","Scotland"},{"Senegalese","Senegal"},{"Serbian","Serbia"},{"Seychellois","Seychelles, the"},{"Sierra Leonian","Sierra Leone"},{"Singaporean","Singapore"},
		    		{"Slovak","Slovakia"},{"Slovenian","Slovenia"},{"Somali","Somalia"},{"South African","South Africa"},{"South Korean","South Korea"},{"Spanish","Spain"},
		    		{"Sri Lankan","Sri Lanka"},{"Sudanese","Sudan"},{"Surinamese","Suriname"},{"Swazi","Swaziland"},{"Swedish","Sweden"},{"Swiss","Switzerland"},{"Syrian","Syria"},{"Taiwanese","Taiwan"},
		    		{"Tajik","Tajikistan"},{"Tanzanian","Tanzania"},{"Thai","Thailand"},{"Togolese","Togo"},{"Trinidadian Tobagan/Tobagonian","Trinidad and Tobago"},{"Tunisian","Tunisia"},
		    		{"Turkish","Turkey"},{"Turkmen","Turkmenistan"},{"Tuvaluan","Tuvali"},{"Ugandan","Uganda"},{"Ukrainian","Ukraine"},
		    		{"Emirati","United Arab Emirates"},
		    		{"British","United Kingdom"},
		    		{"US","United States"},
		    		{"Uruguayan","Uruguay"},{"Uzbek","Uzbekistan"},{"Vanuatuan","Vanuata"},{"Venezuelan","Venezuela"},{"Vietnamese","Vietnam"},{"Welsh","Wales"},
		    		{"Western Samoan","Western Samoa"},{"Yemeni","Yemen"},{"Yugoslav","Yugoslavia"},{"Za•rean","Zaire"},{"Zambian","Zambia"},{"Zimbabwean","Zimbabwe"}};
			
		  HashMap map = new HashMap(array.length);
			for (int i = 0; i < array.length; i++) {
		    	map.put(array[i][0], array[i][1]);
		    }
			return map;
	  }
	private  Map getNationalityLocaleMap(){
		  if(nationalityLocaleMap == null){
			  nationalityLocaleMap = createNationalityLocaleMap();
		  }
		  return nationalityLocaleMap;
	 }
	  
	public NationalityDropdown(String name) {
		super(name);
	}
	
	private String getCountryNameByNationality(String nationality){
		return (String) getNationalityLocaleMap().get(nationality);
	}
	private String getCountryIdByNationality(String nationality) throws IDOLookupException, FinderException {
		String countryName = getCountryNameByNationality(nationality);
		Country country = getCountryHome().findByCountryName(countryName);
		return String.valueOf(country.getPrimaryKey());
	}
	public void main(IWContext iwc) throws Exception {

		super.main(iwc);
		IWResourceBundle iwrb = CoreUtil.getCoreBundle().getResourceBundle(iwc);
		String[] priorityLocales = new String[]{"Icelandic", "British", "German", "Swedish", "Danish", "Norwegian"};

		
		this.addMenuElementFirst("-1", "Select");

		Collection namesP = new ArrayList(priorityLocales.length);
		for (int i = 0; i < priorityLocales.length; i++) {
			String nationality = priorityLocales[i];
			namesP.add(nationality);
			addMenuElement(getCountryIdByNationality(nationality), iwrb.getLocalizedString(StringHandler.stripNonRomanCharacters(nationality), nationality));
		}
		
		if (priorityLocales.length > 0) {
			this.addMenuElement("-1", "------------");
		}
		Collection nationalities = getNationalityLocaleMap().keySet();
		HashMap items = new HashMap();
		for (Iterator iter = nationalities.iterator();iter.hasNext();) {
			String nationality = (String) iter.next();
			if (!StringUtil.isEmpty(nationality) && !namesP.contains(nationality)) {
				try {
					items.put(iwrb.getLocalizedString(StringHandler.stripNonRomanCharacters(nationality), nationality), getCountryIdByNationality(nationality));
				}catch (FinderException e) {
					getLogger().info("country " + getCountryNameByNationality(nationality) + " of nationality " + nationality +" not found.");
				} 
				catch (Exception e) {
					getLogger().log(Level.WARNING, "Failed adding nationality " + nationality + " of country " +
				getCountryNameByNationality(nationality) , e);
				}
			}
		}
		TreeSet sorted = new TreeSet(items.keySet());
		for (Iterator iter = sorted.iterator();iter.hasNext();) {
			String localized = (String) iter.next();
			addMenuElement((String)items.get(localized), localized);
		}
		
		
		this.addMenuElement("-1", "Other");
		
	}
	
	private CountryHome getCountryHome() throws IDOLookupException{
		if(countryHome == null){
			countryHome = (CountryHome)IDOLookup.getHome(Country.class);
		}
		return countryHome;
	}

}

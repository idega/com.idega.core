package com.idega.core.localisation.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.idega.core.cache.IWCacheManager2;
import com.idega.core.localisation.data.ICLocale;
import com.idega.core.localisation.data.ICLocaleHome;
import com.idega.core.localisation.data.dao.ICLocaleDAO;
import com.idega.core.location.data.bean.Country;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.repository.data.MutableClass;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ICLocaleBusiness implements MutableClass {

	private static Hashtable<String, ICLocale> LocaleHashByString = null, LocaleHashInUseByString = null;
	private static Hashtable<Object, ICLocale> LocaleHashById = null, LocaleHashInUseById = null;
	private static List<ICLocale> allIcLocales = null,usedIcLocales = null,notUsedIcLocales = null;
	private static IWTimestamp reloadStamp = null;

  @SuppressWarnings("unchecked")
  private static List<ICLocale> listOfAllICLocales(){
    try {
      return new ArrayList<ICLocale>(((ICLocaleHome)IDOLookup.getHome(ICLocale.class)).findAll());
    }
    catch (Exception ex) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static List<ICLocale> listOfICLocalesInUse(){
    try {
     return new ArrayList<ICLocale>(((ICLocaleHome)IDOLookup.getHome(ICLocale.class)).findAllInUse());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List<ICLocale> listLocaleCreateIsEn(){
    List<ICLocale> L = listOfLocales();
    if(L == null){
      try {
        List<ICLocale> V = new ArrayList<ICLocale>();
        ICLocale is= ((com.idega.core.localisation.data.ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class)).create();
        is.setLocale("is_IS");
        is.store();

        ICLocale en= ((com.idega.core.localisation.data.ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class)).create();
        en.setLocale("en");
        en.store();
        V.add(is);
        V.add(en);
        return V;
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return null;
      }
    }
		else {
			return L;
		}
  }

  /**
   * @return a list of ICLocales that are in use
   */
  public static List<ICLocale> listOfLocales(){
    if(usedIcLocales ==null) {
			reload();
		}
      return usedIcLocales;
  }

  public static List<ICLocale> listOfAllLocales(){
    if(allIcLocales==null) {
			reload();
		}
    return allIcLocales;
  }

  public static List<ICLocale> listOfLocales(boolean inUse){
    if(inUse){
      if(usedIcLocales == null) {
				reload();
			}
      return  usedIcLocales;
    }
    else{
      if(notUsedIcLocales == null) {
				reload();
			}
      return notUsedIcLocales;
    }
  }

  /**
   * @return a list of Locale object of the locales in use
   */
  public static List<Locale> getListOfLocalesJAVA(){
    List<ICLocale> list = listOfLocales();
    List<Locale> localeList = new ArrayList<Locale>();

    if ( list != null ) {
      Iterator<ICLocale> iter = list.iterator();
      while (iter.hasNext()) {
       ICLocale item = iter.next();
       Locale locale = getLocaleFromLocaleString(item.getLocale());
       if ( locale != null ) {
				localeList.add(locale);
			}
      }
    }
    return localeList;
  }

  /**
   * @return a list of Locale object of all locales
   */
	public static List<Locale> getListOfAllLocalesJAVA(){
		List<ICLocale> list = listOfAllLocales();
		List<Locale> localeList = new ArrayList<Locale>();

		if ( list != null ) {
			Iterator<ICLocale> iter = list.iterator();
			while (iter.hasNext()) {
			 ICLocale item = iter.next();
			 Locale locale = getLocaleFromLocaleString(item.getLocale());
			 if ( locale != null ) {
				localeList.add(locale);
			}
			}
		}
		return localeList;
	}

  private static void makeHashtables(){
    List<ICLocale> L = listOfAllLocales();
    if(L!=null){
      int len = L.size();
      LocaleHashById = new Hashtable<Object, ICLocale>(len);
      LocaleHashByString  = new Hashtable<String, ICLocale>(len);
      LocaleHashInUseByString = new Hashtable<String, ICLocale>();
      LocaleHashInUseById = new Hashtable<Object, ICLocale>();
      for (int i = 0; i < len; i++) {
        ICLocale ICL = L.get(i);
        if (ICL == null) {
        	continue;
        }

        Object pk = ICL.getPrimaryKey();
        if (pk != null) {
        	LocaleHashById.put(pk, ICL);
        }

        String locale = ICL.getLocale();
        if (locale != null) {
        	LocaleHashByString.put(locale, ICL);
        }

        if (ICL.getInUse()) {
        	if (pk != null) {
        		LocaleHashInUseById.put(pk, ICL);
        	}
        	if (locale != null) {
        		LocaleHashInUseByString.put(locale, ICL);
        	}
        }
      }
    }
  }

  private static void makeLists(){
    allIcLocales = listOfAllICLocales();
    usedIcLocales = listOfICLocalesInUse();
    notUsedIcLocales = new ArrayList<ICLocale>();
    notUsedIcLocales.addAll(allIcLocales);
    notUsedIcLocales.removeAll(usedIcLocales);
  }

  public static Map<Object, ICLocale> mapOfLocalesInUseById(){
    if(LocaleHashInUseById == null) {
			reload();
		}
    return LocaleHashInUseById;
  }

  public static Map<String, ICLocale> mapOfLocalesInUseByString(){
   if(LocaleHashInUseByString == null) {
		reload();
	}
    return LocaleHashInUseByString;
  }

  public static void reload(){
    makeLists();
    makeHashtables();

    reloadStamp = IWTimestamp.RightNow();
  }

  public static void unload(){
  	allIcLocales=null;
  	LocaleHashById=null;
  	LocaleHashByString=null;
  	 LocaleHashInUseById=null;
  	 LocaleHashInUseByString=null;
  	 notUsedIcLocales=null;
  	 usedIcLocales=null;
  }

  public static IWTimestamp getReloadStamp(){
    if(reloadStamp == null) {
			reload();
		}
    return reloadStamp;
  }

  public static Map<Object, ICLocale> getMapOfLocalesById(){
    return getLocaleHashById();
  }

  public static Map<String, ICLocale> getMapOfLocalesByString(){
    return getLocaleHashByString();
  }

  public static Hashtable<Object, ICLocale> getLocaleHashById(){
    if(LocaleHashById == null) {
			reload();
		}
    return LocaleHashById;
  }
  public static Hashtable<String, ICLocale> getLocaleHashByString(){
    if(LocaleHashByString == null) {
			reload();
		}
    return LocaleHashByString;
  }

  public static int getLocaleId(Locale locale){
    int r = -1;
    if(LocaleHashByString == null) {
			reload();
		}
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = LocaleHashByString.get(locale.toString());
      r = ((Integer)ICL.getPrimaryKey()).intValue();
    }
    return r;
  }

	public static ICLocale getICLocale(Locale locale){
    if(LocaleHashByString == null) {
			reload();
		}
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = LocaleHashByString.get(locale.toString());
      return ICL;
    }
    return null;
  }

  /**
   *  returns ICLocale from Locale string identifier
   */
  public static ICLocale getICLocale(String localeString){
    if(localeString != null){
      if(LocaleHashByString == null){
        reload();
      }
      if( LocaleHashByString!=null && LocaleHashByString.containsKey(localeString) ){
        ICLocale ICL = LocaleHashByString.get(localeString);
        return ICL;
      }
    }
    return null;
  }

  /**
   * Returns a Locale from a Locale string like Locale.toString();
   * returns null if not found
   */
  public static Locale getLocaleFromLocaleString(String localeString){
	  if (StringUtil.isEmpty(localeString)) {
		  return null;
	  }

	  if (localeString.length() == 2) {
	      return new Locale(localeString, CoreConstants.EMPTY);

	  } else if (localeString.length() == 5 && localeString.indexOf(CoreConstants.UNDER) == 2) {
	      return new Locale(localeString.substring(0, 2), localeString.substring(3, 5).toUpperCase());

	  } else if (localeString.length() > 5 && localeString.indexOf(CoreConstants.UNDER) == 2 && localeString.indexOf(CoreConstants.UNDER, 3) == 5) {
	      return new Locale(localeString.substring(0, 2), localeString.substring(3, 5).toUpperCase(), localeString.substring(6, localeString.length()));

	  }

	  return null;
  }

  public static Locale getLocaleReturnIcelandicLocaleIfNotFound(int iLocaleId) {
  	Locale icelandicLocale =  new Locale("is","IS");
  	return getLocale(iLocaleId, icelandicLocale);
  }

  public static Locale getLocale(int iLocaleId) {
  	return getLocale(iLocaleId, null);
  }

  public static Locale getLocale(int iLocaleId, Locale returnValueIfNotFound){
  	if(LocaleHashById == null ) {
        reload();
  	}
    Integer i = new Integer(iLocaleId);
     if(LocaleHashById != null && LocaleHashById.containsKey(i)){
     	ICLocale ICL = LocaleHashById.get(i);
        return getLocaleFromLocaleString(ICL.getLocale());
     }
     return returnValueIfNotFound;
  }

  public static void makeLocalesInUse(List<String> listOfStringIds){
    if(listOfStringIds != null){
      Iterator<String> I = listOfStringIds.iterator();
      try{
        ICLocaleHome home = (ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class);
        List<ICLocale> currentLocales = listOfICLocalesInUse();
        List<ICLocale> oldCurrentLocales = new ArrayList<ICLocale>();
        oldCurrentLocales.addAll(currentLocales);
        while (I.hasNext()) {
          ICLocale locale = home.findByPrimaryKey(Integer.valueOf(I.next()));
          locale.setInUse(true);
          locale.store();
          oldCurrentLocales.remove(locale);
        }

        Iterator<ICLocale> iter = oldCurrentLocales.iterator();
        while (iter.hasNext()) {
          ICLocale locale = iter.next();
          locale.setInUse(false);
          locale.store();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }

      /*if(I.hasNext()){
        id = (String) I.next();
        ids.append(id);
      }
      while(I.hasNext()){
        id = (String) I.next();
        ids.append(",");
        ids.append(id);
      }
      try {
        String sqlA = "update ic_locale set in_use = 'Y' where ic_locale_id in ("+ids.toString()+")";
        System.err.println(sqlA);
        String sqlB = "update ic_locale set in_use = 'N' where ic_locale_id not in ("+ids.toString()+")";
        System.err.println(sqlB);
        com.idega.data.SimpleQuerier.execute(sqlA);
        com.idega.data.SimpleQuerier.execute(sqlB);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }*/
      reload();
    }
  }


  /**
 * In the DropdownMenu the keys (values) are the locale-stringrepresentations
 * e.g. "en_US" for English/US
 */
  public static DropdownMenu getAvailableLocalesDropdownStringKeyed(IWMainApplication iwma,String name){
	  return getAvailableLocalesDropdownStringKeyed(iwma, name, false);
  }

  public static DropdownMenu getAvailableLocalesDropdownStringKeyed(IWMainApplication iwma, String name, boolean useLanguageOnly){
	  List<Locale> locales = ICLocaleBusiness.getListOfLocalesJAVA();
	  DropdownMenu down = new DropdownMenu(name);
	  if (locales == null) {
		  return down;
	  }

	  Locale l = null;
	  for (int i = 0; i < locales.size(); i++) {
		  l = locales.get(i);
		  if (useLanguageOnly) {
			  down.addMenuElement(l.getLanguage(), l.getDisplayLanguage());
		  }
		  else {
			  down.addMenuElement(l.toString(), l.getDisplayLanguage());
		  }
	  }
	  return down;
  }

  	private static Map<String, String> isoLanguages = null;
  	public static final Map<String, String> getISOLanguages() {
  		if (!MapUtil.isEmpty(isoLanguages))
  			return isoLanguages;

  		isoLanguages = new HashMap<String, String>();
  		synchronized (isoLanguages) {
			Map<String, Boolean> addedLanguages = new HashMap<String, Boolean>();
			String[] languages = Locale.getISOLanguages();
			for (String language: languages) {
				Locale locale = getLocaleFromLocaleString(language);
				if (locale == null)
					continue;

				String languageId = locale.getLanguage();
				if (StringUtil.isEmpty(languageId) || addedLanguages.containsKey(languageId))
					continue;

				addedLanguages.put(languageId, Boolean.TRUE);

				String displayLanguage = locale.getDisplayLanguage(locale);
				isoLanguages.put(languageId, displayLanguage);
			}
		}
  		return isoLanguages;
	}

  	public static List<Locale> getLocalesForCountry(Country country) {
  		if (country == null)
  			return Collections.emptyList();

  		Locale[] locales = Locale.getAvailableLocales();
  		if (ArrayUtil.isEmpty(locales))
  			return Collections.emptyList();

  		List<Locale> countryLocales = new ArrayList<Locale>();
  		String isoAbbreviation = country.getISOAbbreviation();
  		for (Locale locale: locales) {
  			if (locale.toString().endsWith(CoreConstants.UNDER.concat(isoAbbreviation))) {
  				countryLocales.add(locale);
  			}
  		}
  		return countryLocales;
  	}

  	public static List<Locale> getLocalesForLanguage(String language) {
  		if (StringUtil.isEmpty(language)) {
  			return Collections.emptyList();
  		}

  		Map<String, List<Locale>> localesByLanguageCache = IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(
  				"localesByLanguageCache", 500, Boolean.TRUE, Boolean.FALSE, 604800);

  		List<Locale> localesByLanguage = localesByLanguageCache.get(language);
  		if (!ListUtil.isEmpty(localesByLanguage)) {
  			return localesByLanguage;
  		}

  		if (localesByLanguage == null) {
  			localesByLanguage = new ArrayList<Locale>();
  			localesByLanguageCache.put(language, localesByLanguage);
  		}

  		ICLocaleDAO localeDAO = ELUtil.getInstance().getBean(ICLocaleDAO.class);
  		List<com.idega.core.localisation.data.bean.ICLocale> icLocales = localeDAO.doFindLocalesByLanguage(language + CoreConstants.PERCENT);
		if (!ListUtil.isEmpty(icLocales)) {
			for (com.idega.core.localisation.data.bean.ICLocale icLocale: icLocales) {
				String localeValue = icLocale.getLocale();
				if (!language.equals(localeValue)) {
					Locale locale = getLocaleFromLocaleString(localeValue);
					if (locale != null && !localesByLanguage.contains(locale)) {
						localesByLanguage.add(locale);
					}
				}
			}
		}

		localesByLanguageCache.put(language, localesByLanguage);
		return localesByLanguage;
  	}
}
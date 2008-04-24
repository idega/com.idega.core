package com.idega.core.localisation.business;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.idega.core.localisation.data.ICLocale;
import com.idega.core.localisation.data.ICLocaleHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.repository.data.MutableClass;
import com.idega.util.IWTimestamp;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ICLocaleBusiness  implements MutableClass {
  private static Hashtable LocaleHashByString = null, LocaleHashById = null;
  private static Hashtable LocaleHashInUseByString = null, LocaleHashInUseById = null;
  private static List allIcLocales = null,usedIcLocales = null,notUsedIcLocales = null;
  private static IWTimestamp reloadStamp = null;


  private static List listOfAllICLocales(){
    try {
      //return EntityFinder.getInstance().findAll(ICLocale.class);
      return new Vector(((ICLocaleHome)IDOLookup.getHome(ICLocale.class)).findAll());
    }
    catch (Exception ex) {
      return null;
    }
  }

  private static List listOfICLocalesInUse(){
    try {
     //return  EntityFinder.getInstance().findAllByColumn(ICLocale.class,com.idega.core.localisation.data.ICLocaleBMPBean.getColumnNameInUse(),"Y");
     return new Vector(((ICLocaleHome)IDOLookup.getHome(ICLocale.class)).findAllInUse());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List listLocaleCreateIsEn(){
    List L = listOfLocales();
    if(L == null){
      try {
        Vector V = new Vector();
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
  public static List listOfLocales(){
    if(usedIcLocales ==null) {
			reload();
		}
      return usedIcLocales;
  }

  public static List listOfAllLocales(){
    if(allIcLocales==null) {
			reload();
		}
    return allIcLocales;
  }

  public static List listOfLocales(boolean inUse){
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
    List list = listOfLocales();
    List<Locale> localeList = new Vector<Locale>();

    if ( list != null ) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
       ICLocale item = (ICLocale) iter.next();
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
	public static List getListOfAllLocalesJAVA(){
		List list = listOfAllLocales();
		List localeList = new Vector();

		if ( list != null ) {
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
			 ICLocale item = (ICLocale) iter.next();
			 Locale locale = getLocaleFromLocaleString(item.getLocale());
			 if ( locale != null ) {
				localeList.add(locale);
			}
			}
		}
		return localeList;
	}

  private static void makeHashtables(){
    List L = listOfAllLocales();
    if(L!=null){
      int len = L.size();
      LocaleHashById = new Hashtable(len);
      LocaleHashByString  = new Hashtable(len);
      LocaleHashInUseByString = new Hashtable();
      LocaleHashInUseById = new Hashtable();
      for (int i = 0; i < len; i++) {
        ICLocale ICL = (ICLocale) L.get(i);
        LocaleHashById.put(ICL.getPrimaryKey(),ICL);
        LocaleHashByString.put(ICL.getLocale(),ICL);
        if(ICL.getInUse()){
          LocaleHashInUseById.put(ICL.getPrimaryKey(),ICL);
          LocaleHashInUseByString.put(ICL.getLocale(),ICL);
        }
      }
    }
  }

  private static void makeLists(){
    allIcLocales = listOfAllICLocales();
    usedIcLocales = listOfICLocalesInUse();
    notUsedIcLocales = new Vector();
    notUsedIcLocales.addAll(allIcLocales);
    notUsedIcLocales.removeAll(usedIcLocales);
  }

  public static Map mapOfLocalesInUseById(){
    if(LocaleHashInUseById == null) {
			reload();
		}
    return LocaleHashInUseById;
  }

  public static Map mapOfLocalesInUseByString(){
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

  public static Map getMapOfLocalesById(){
    return getLocaleHashById();
  }

  public static Map getMapOfLocalesByString(){
    return getLocaleHashByString();
  }

  public static Hashtable getLocaleHashById(){
    if(LocaleHashById == null) {
			reload();
		}
    return LocaleHashById;
  }
  public static Hashtable getLocaleHashByString(){
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
      ICLocale ICL = (ICLocale) LocaleHashByString.get(locale.toString());
      r = ((Integer)ICL.getPrimaryKey()).intValue();
    }
    return r;
  }

	public static ICLocale getICLocale(Locale locale){
    if(LocaleHashByString == null) {
			reload();
		}
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = (ICLocale) LocaleHashByString.get(locale.toString());
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
        ICLocale ICL = (ICLocale) LocaleHashByString.get(localeString);
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
    if(localeString.length() == 2){
      return new Locale(localeString,"");
    }
    else if(localeString.length()==5 && localeString.indexOf("_")==2){
      return new Locale(localeString.substring(0,2),localeString.substring(3,5));
    }
    else if(localeString.length() > 5 && localeString.indexOf("_")==2 && localeString.indexOf("_",3)== 5){
      return new Locale(localeString.substring(0,2),localeString.substring(3,5),localeString.substring(6,localeString.length()));
    }
		else {
			return null;
      //return Locale.getDefault();
		}
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
     	ICLocale ICL = (ICLocale) LocaleHashById.get(i);
        return getLocaleFromLocaleString(ICL.getLocale());
     }
     return returnValueIfNotFound;
  }

  public static void makeLocalesInUse(List<String> listOfStringIds){
    if(listOfStringIds != null){
      Iterator<String> I = listOfStringIds.iterator();
      try{
        ICLocaleHome home = (ICLocaleHome)com.idega.data.IDOLookup.getHome(ICLocale.class);
        List currentLocales = listOfICLocalesInUse();
        List oldCurrentLocales = new Vector();
        oldCurrentLocales.addAll(currentLocales);
        while (I.hasNext()) {
          ICLocale locale = home.findByPrimaryKey(Integer.valueOf(I.next()));
          locale.setInUse(true);
          locale.store();
          oldCurrentLocales.remove(locale);
        }

        Iterator iter = oldCurrentLocales.iterator();
        while (iter.hasNext()) {
          ICLocale locale = (ICLocale)iter.next();
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


}

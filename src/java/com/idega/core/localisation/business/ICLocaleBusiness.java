package com.idega.core.localisation.business;

import com.idega.data.EntityFinder;
import com.idega.core.data.ICLocale;
import com.idega.util.LocaleUtil;
import com.idega.util.idegaTimestamp;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Locale;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ICLocaleBusiness {
  private static Hashtable LocaleHashByString = null, LocaleHashById = null;
  private static idegaTimestamp reloadStamp = null;

  public static List listLocaleCreateIsEn(){
    List L = listOfLocales();
    if(L == null){
      try {
        Vector V = new Vector();
        ICLocale is= new ICLocale();
        is.setLocale("is_IS");
        is.insert();

        ICLocale en= new ICLocale();
        en.setLocale("en");
        en.insert();
        V.add(is);
        V.add(en);
        return V;
      }
      catch (SQLException ex) {
        ex.printStackTrace();
        return null;
      }
    }
    else
      return L;
  }

  public static List listOfLocales(){
    try {
      return EntityFinder.findAll(new ICLocale());
    }
    catch (SQLException ex) {
      return null;
    }
  }

  public static List listOfLocalesJAVA(){
    List list = listOfLocales();
    List localeList = new Vector();

    if ( list != null ) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
       ICLocale item = (ICLocale) iter.next();
       Locale locale = getLocaleFromLocaleString(item.getLocale());
       if ( locale != null )
        localeList.add(locale);
      }
    }

    return localeList;
  }

  private static void makeHashtables(){
    List L = listOfLocales();
    if(L!=null){
      int len = L.size();
      LocaleHashById = new Hashtable(len);
      LocaleHashByString  = new Hashtable(len);
      for (int i = 0; i < len; i++) {
        ICLocale ICL = (ICLocale) L.get(i);
        LocaleHashById.put(new Integer(ICL.getID()),ICL);
        LocaleHashByString.put(ICL.getLocale(),ICL);
      }
    }
  }

  public static void reload(){
    makeHashtables();
    reloadStamp = idegaTimestamp.RightNow();
  }

  public static idegaTimestamp getReloadStamp(){
    if(reloadStamp == null)
      reload();
    return reloadStamp;
  }

  public static Hashtable getLocaleHashById(){
    if(LocaleHashById == null)
      reload();
    return LocaleHashById;
  }
  public static Hashtable getLocaleHashByString(){
    if(LocaleHashByString == null)
      reload();
    return LocaleHashByString;
  }

  public static int getLocaleId(Locale locale){
    int r = -1;
    if(LocaleHashByString == null)
      reload();
    if( LocaleHashByString!=null && LocaleHashByString.containsKey(locale.toString()) ){
      ICLocale ICL = (ICLocale) LocaleHashByString.get(locale.toString());
      r = ICL.getID();
    }
    return r;
  }

  /**
   * Returns a Locale from a Locale string like Locale.toString();
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
    else
      return Locale.getDefault();
  }

  public static Locale getLocale(int iLocaleId){
    try {
      if(LocaleHashById == null )
        reload();
      Integer i = new Integer(iLocaleId);
      if(LocaleHashById != null && LocaleHashById.containsKey(i)){
        ICLocale ICL = (ICLocale) LocaleHashById.get(i);
        return getLocaleFromLocaleString(ICL.getLocale());
      }
      else return null;
    }
    catch(Exception ex){
      ex.printStackTrace();
      return null;

    }
  }
}
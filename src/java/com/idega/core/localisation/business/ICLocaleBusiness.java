package com.idega.core.localisation.business;

import com.idega.data.EntityFinder;
import com.idega.core.data.ICLocale;
import com.idega.util.LocaleUtil;
import com.idega.util.idegaTimestamp;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Map;
import java.util.Locale;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ICLocaleBusiness {
  private static Hashtable LocaleHashByString = null, LocaleHashById = null;
  private static Hashtable LocaleHashInUseByString = null, LocaleHashInUseById = null;
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
    return listOfLocalesInUse();
    /*
    try {
      return EntityFinder.findAll(new ICLocale());
    }
    catch (SQLException ex) {
      return null;
    }
    */
  }

  public static List listOfAllLocales(){
    try {
      return EntityFinder.findAll(new ICLocale());
    }
    catch (SQLException ex) {
      return null;
    }
  }

  public static List listOfLocalesInUse(){
    try {
     return  EntityFinder.findAllByColumn(new ICLocale(),ICLocale.getColumnNameInUse(),"Y");
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static List listOfLocales(boolean inUse){
    try {
      if(inUse)
        return  EntityFinder.findAllByColumn(new ICLocale(),ICLocale.getColumnNameInUse(),"Y");
      else
        return EntityFinder.findAllByColumn(new ICLocale(),ICLocale.getColumnNameInUse(),"N");
    }
    catch (SQLException ex) {
      ex.printStackTrace();
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
    List L = listOfAllLocales();
    if(L!=null){
      int len = L.size();
      LocaleHashById = new Hashtable(len);
      LocaleHashByString  = new Hashtable(len);
      LocaleHashInUseByString = new Hashtable();
      LocaleHashInUseById = new Hashtable();
      for (int i = 0; i < len; i++) {
        ICLocale ICL = (ICLocale) L.get(i);
        LocaleHashById.put(new Integer(ICL.getID()),ICL);
        LocaleHashByString.put(ICL.getLocale(),ICL);
        if(ICL.getInUse()){
          LocaleHashInUseById.put(new Integer(ICL.getID()),ICL);
          LocaleHashInUseByString.put(ICL.getLocale(),ICL);
        }
      }
    }
  }

  public static Map mapOfLocalesInUseById(){
    if(LocaleHashInUseById == null)
      reload();
    return LocaleHashInUseById;
  }

  public static Map mapOfLocalesInUseByString(){
   if(LocaleHashInUseByString == null)
      reload();
    return LocaleHashInUseByString;
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

  public static Map getMapOfLocalesById(){
    return getLocaleHashById();
  }

  public static Map getMapOfLocalesByString(){
    return getLocaleHashByString();
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

  public static void makeLocalesInUse(List listOfStringIds){
    if(listOfStringIds != null){
      StringBuffer ids = new StringBuffer();
      Iterator I = listOfStringIds.iterator();
      String id;
      if(I.hasNext()){
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
      }
      reload();
    }
  }
}
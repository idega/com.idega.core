package com.idega.core.business;

import com.idega.data.EntityFinder;
import com.idega.core.data.ICLocale;
import com.idega.util.LocaleUtil;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Locale;
import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ICLocaleBusiness {

  public ICLocaleBusiness() {
  }

  public static List listLocaleCreateNew(){
    List L = listOfLocales();
    if(L == null){
      try {
        Vector V = new Vector();
        ICLocale is= new ICLocale();
        is.setLocale("is_IS");
        is.insert();

        ICLocale en= new ICLocale();
        en.setLocale("en_EN");
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

  public static Hashtable hashOfLocaleByLocaleName(){
    List L = listOfLocales();
    if(L!=null){
      int len = L.size();
      Hashtable ht = new Hashtable(len);
      for (int i = 0; i < len; i++) {
        ICLocale ICL = (ICLocale) L.get(i);
        ht.put(ICL.getLocale(),ICL);
      }
      return ht;
    }
    else
      return null;
  }

  public static int getLocaleId(Locale locale){
    int r = -1;

    Hashtable ht = hashOfLocaleByLocaleName();
    if( ht!=null && ht.containsKey(locale.toString()) ){
      ICLocale ICL = (ICLocale) ht.get(locale.toString());
      r = ICL.getID();
    }
    return r;
  }

  public static Locale getLocale(int iLocaleId){
    try {
      ICLocale ICL = new ICLocale(iLocaleId);
      return LocaleUtil.getLocale(ICL.getLocale());
    }
    catch (SQLException ex) {
      return null;
    }
    catch(Exception ex){
      return null;
    }
  }
}
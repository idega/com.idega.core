package com.idega.core.localisation.presentation;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.data.ICLocale;
import com.idega.presentation.ui.DropdownMenu;
import java.util.List;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Enumeration;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ICLocalePresentation {

  public ICLocalePresentation() {
  }

  public static  DropdownMenu getLocaleDropdownStringKeyed(String name){
    DropdownMenu drp = new DropdownMenu(name);
    Hashtable H = ICLocaleBusiness.getLocaleHashByString();
    if(H!= null){
      ICLocale ICL;
      Enumeration E = H.keys();
      while(E.hasMoreElements()){
        String key = (String) E.nextElement();
        ICL = (ICLocale) H.get(key);
        String locale = ICLocaleBusiness.getLocaleFromLocaleString(ICL.getLocale()).getDisplayLanguage();
        drp.addMenuElement(key,locale);
      }
    }
    return drp;
  }

  public static  DropdownMenu getLocaleDropdownIdKeyed(String name){
    DropdownMenu drp = new DropdownMenu(name);
    Hashtable H = ICLocaleBusiness.getLocaleHashById();
    if(H!= null){
      ICLocale ICL;
      Enumeration E = H.keys();
      while(E.hasMoreElements()){
        Integer key = (Integer) E.nextElement();
        ICL = (ICLocale) H.get(key);
        String locale = ICLocaleBusiness.getLocaleFromLocaleString(ICL.getLocale()).getDisplayLanguage();
        drp.addMenuElement(key.toString(),locale);
      }
    }
    return drp;
  }


}
package com.idega.core.localisation.presentation;

import java.util.Map;

import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.presentation.ui.DropdownMenu;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ICLocalePresentation {

  public ICLocalePresentation() {
  }

  public static  DropdownMenu getLocaleDropdownStringKeyed(String name){
    DropdownMenu drp = new DropdownMenu(name);
    java.util.Map H = ICLocaleBusiness.mapOfLocalesInUseByString();
    if(H!= null){
      ICLocale ICL;
      java.util.Iterator I = H.entrySet().iterator();
      while(I.hasNext()){
        Map.Entry me = (Map.Entry)I.next();
        String key = (String) me.getKey();
        ICL = (ICLocale) me.getValue();
        String locale = ICLocaleBusiness.getLocaleFromLocaleString(ICL.getLocale()).getDisplayLanguage();
        drp.addMenuElement(key,locale);
      }
    }
    return drp;
  }

  public static  DropdownMenu getLocaleDropdownIdKeyed(String name){
    DropdownMenu drp = new DropdownMenu(name);
    java.util.Map H = ICLocaleBusiness.mapOfLocalesInUseById();
    if(H!= null){
      ICLocale ICL;
      java.util.Iterator I = H.entrySet().iterator();
      while(I.hasNext()){
        Map.Entry me = (Map.Entry)I.next();
        //System.err.println(me.getKey().toString());
        Integer key = (Integer) me.getKey();
        ICL = (ICLocale) me.getValue();
        String locale = ICLocaleBusiness.getLocaleFromLocaleString(ICL.getLocale()).getDisplayLanguage();
        drp.addMenuElement(key.toString(),locale);
      }
    }
    return drp;
  }


}

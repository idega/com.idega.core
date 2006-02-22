package com.idega.util;

import java.util.Locale;
import com.idega.core.localisation.business.ICLocaleBusiness;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleUtil {

  	private static Locale icelandicLocale;
	private static Locale swedishLocale;

  private static final String ICELANDIC_IDENTIFIER="is_IS";
  private static final String ENGLISH_IDENTIFIER="en";
  private static final String US_IDENTIFIER="en_US";
  private static final String UK_IDENTIFIER="en_UK";
  private LocaleUtil() {
  }

  public static Locale getIcelandicLocale(){
  	if(icelandicLocale==null){
  		icelandicLocale=new Locale("is","IS");
  	}
    return icelandicLocale;
  }
  
  public static Locale getSwedishLocale(){
  	if(swedishLocale==null){
  		swedishLocale=new Locale("sv","SE");
  	}
    return swedishLocale;
  }

  public static Locale getLocale(String localeIdentifier){
    if(localeIdentifier.equals(ICELANDIC_IDENTIFIER)){
      return getIcelandicLocale();
    }
    else if(localeIdentifier.equals(ENGLISH_IDENTIFIER)){
      return Locale.ENGLISH;
    }
    else if(localeIdentifier.equals(US_IDENTIFIER)){
      return Locale.US;
    }
    else if(localeIdentifier.equals(UK_IDENTIFIER)){
      return Locale.UK;
    }
    else {
      if ( localeIdentifier.length() > 0 ) {
        return ICLocaleBusiness.getLocaleFromLocaleString(localeIdentifier);
      }
    }

    return null;
  }

}

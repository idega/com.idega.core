/*
 * $Id: ErrorChecker.java,v 1.5 2002/04/03 17:49:38 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

/**
 *
 * @author <a href="mailto:?@idega.is">?</a>
 * @version 1.0
 */
public class ErrorChecker {

  /**
   * Check your spelling
   */
  public static boolean isValidSosialSecurityNumber(String socialSecurityNumber) {
      boolean check = isValidIcelandicSocialSecurityNumber(socialSecurityNumber);
      if (check)
        return(true);
      else {  //for others. example: socialSecurityNumber = "250278"
        if (socialSecurityNumber.length() == 6) {
         check = isInteger(socialSecurityNumber);
        }
      }

    return(check);
  }

  /**
   *
   */
  public static boolean isValidIcelandicSocialSecurityNumber(String socialSecurityNumber) {
    return com.idega.util.text.SocialSecurityNumber.isValidIcelandicSocialSecurityNumber(socialSecurityNumber);
  }

  /**
   *
   */
  public static boolean isInteger(String value) {
    try {
      Integer.parseInt(value);
    }
    catch(Exception e) {
      return(false);
    }

    return(true);
  }
}

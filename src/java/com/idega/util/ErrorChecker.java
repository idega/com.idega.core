/*
 * $Id: ErrorChecker.java,v 1.3 2001/07/26 12:40:36 palli Exp $
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
    if (socialSecurityNumber.length() != 10) {
      return(false);
    }

    try {
      int var1 = Integer.parseInt(socialSecurityNumber.substring(0,1));
      int var2 = Integer.parseInt(socialSecurityNumber.substring(1,2));
      int var3 = Integer.parseInt(socialSecurityNumber.substring(2,3));
      int var4 = Integer.parseInt(socialSecurityNumber.substring(3,4));
      int var5 = Integer.parseInt(socialSecurityNumber.substring(4,5));
      int var6 = Integer.parseInt(socialSecurityNumber.substring(5,6));
      int var7 = Integer.parseInt(socialSecurityNumber.substring(6,7));
      int var8 = Integer.parseInt(socialSecurityNumber.substring(7,8));
      int var9 = Integer.parseInt(socialSecurityNumber.substring(8,9));

      int sum = (3 * var1) +
                (2 * var2) +
                (7 * var3) +
                (6 * var4) +
                (5 * var5) +
                (4 * var6) +
                (3 * var7) +
                (2*var8);

      int res = sum % 11;
      int vartala = 11 - res;

      if (vartala == 10) {
        vartala = 1;
      }
      else if (vartala == 11) {
        vartala = 0;
      }

      if(var9 == vartala) {
        return(true);
      }
      else {
        return(false);
      }
    }
    catch(Exception e) {
      return(false);
    }
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

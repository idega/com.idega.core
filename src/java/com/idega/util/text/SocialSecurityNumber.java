package com.idega.util.text;

import java.util.*;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author       <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

 public class SocialSecurityNumber {
    private String sSSN = null;
    private int iSSN = 0;

    public SocialSecurityNumber() {
    }

    public SocialSecurityNumber(String SSN) {
      this.sSSN = SSN;
    }

    public void setSocialSecurityNumber(String SSN){
      this.sSSN = SSN;
    }

    public int getAge(){
      if(this.sSSN != null)
        return getAge(this.sSSN);
      else
        return 0;
    }

    public static int getAge(String kt){
      int thisYear = new GregorianCalendar().YEAR;
      int age;
      int ktYear = Integer.parseInt(kt.substring(4,6));
      if(kt.length() == 10){
        if( kt.endsWith("9")) ktYear += 1900;
        if( kt.endsWith("0")) ktYear += 2000;
        if( kt.endsWith("1")) ktYear += 2100; // in the future
        if( kt.endsWith("2")) ktYear += 2200;
      }
      else
        ktYear += 1900;
      age = thisYear - ktYear ;
      return age;

  }

  /**
   *  Checks for validity of an icelandic ssn
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
                (2 * var8);

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


}
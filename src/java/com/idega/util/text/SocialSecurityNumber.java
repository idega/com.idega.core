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

}
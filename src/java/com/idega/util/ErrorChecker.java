package com.idega.util;



public class ErrorChecker{


  /**
   * Check your spelling
   */
  public static boolean isValidSosialSecurityNumber(String sosialSecurityNumber){
    return isValidIcelandicSocialSecurityNumber(sosialSecurityNumber);
  }




  public static boolean isValidIcelandicSocialSecurityNumber(String socialSecurityNumber)
  {
      if(socialSecurityNumber.length() != 10) {
          return false;
      }

      try {
          int var1 = Integer.parseInt(socialSecurityNumber.substring(0, 1));
          int var2 = Integer.parseInt(socialSecurityNumber.substring(1, 2));
          int var3 = Integer.parseInt(socialSecurityNumber.substring(2, 3));
          int var4 = Integer.parseInt(socialSecurityNumber.substring(3, 4));
          int var5 = Integer.parseInt(socialSecurityNumber.substring(4, 5));
          int var6 = Integer.parseInt(socialSecurityNumber.substring(5, 6));
          int var7 = Integer.parseInt(socialSecurityNumber.substring(6, 7));
          int var8 = Integer.parseInt(socialSecurityNumber.substring(7, 8));
          int var9 = Integer.parseInt(socialSecurityNumber.substring(8, 9));
          //int var10 = Integer.parseInt(socialSecurityNumber.substring(9, 10));

          //if ( (0 < var10) && (var10 < 9))
          //    return false;

          int sum = (3*var1) + (2*var2) + (7*var3) + (6*var4) + (5*var5) + (4*var6) + (3*var7) + (2*var8);// + (1*var9);

          //System.out.println("Sum:"+sum);

          int res = sum % 11;
          //System.out.println("Res:"+res);

          int vartala;

          vartala = 11 - res;

          if(vartala==10){
            vartala=1;
          }
          else if(vartala==11){
            vartala=0;
          }

          //System.out.println("Vartala:"+vartala);

          /*if(res == 0 ){
            vartala = 0;
          }
          else if(res == 1 ){
            vartala = 1;
          }
          else{
            vartala = 11 - res;
          }*/

          if(var9==vartala){
            return true;
          }
          else{
            return false;
          }

      }
      catch(Exception e) {
          return false;
      }
  }

  public static boolean isInteger(String value) {
      try {
          Integer.parseInt(value);
      }
      catch(Exception e) {
          return false;
      }
      return true;
  }
}


/**
 * Title:        idegaTimeStamp<p>
 * Description:  <p>
 * Copyright:    Copyright (c) idega<p>
 * Company:      idega margmiðlun<p>
 * @author idega 2000 - idega team
 * @version 1.0
 */
package com.idega.util;

import java.util.*;
import java.lang.*;
import java.sql.*;
import java.io.*;
import com.idega.jmodule.object.ModuleInfo;
import java.text.DateFormat;

public class idegaTimestamp{

  private GregorianCalendar Calendar;
  private boolean isDate;
  private boolean isTime;

  public idegaTimestamp() {
    Calendar = new GregorianCalendar();
  }


  /**
   * utfærir smiðinn Timestamp(long time).
   */

  public idegaTimestamp(long time) {
    this(new Timestamp(time));
  }

  public idegaTimestamp(idegaTimestamp time) {
    Calendar = (GregorianCalendar)time.getGregorianCalendar().clone();
  }

  /**
   * GregorianCalendar geymir manuði frá 0 til 11 en idegaTimestamp frá 1 til 12. Ekki þarf að taka tillit til þess þvi klasinn leiðrettir það sjalfur.
   */

  public idegaTimestamp(GregorianCalendar theCalendar ) {
    Calendar = theCalendar;
  }

  public idegaTimestamp(Timestamp time) {

    String TimeString = time.toString();

    StringTokenizer tokens = new StringTokenizer(TimeString);
    //yyyy-mm-dd hh:mm:ss.fffffffff

    int year = Integer.parseInt(tokens.nextToken("-"));
    int month = Integer.parseInt(tokens.nextToken("-"));
    int date = Integer.parseInt(tokens.nextToken(" -"));
    int hour = Integer.parseInt(tokens.nextToken(" :"));
    int minute = Integer.parseInt(tokens.nextToken(":"));
    int second = Integer.parseInt(tokens.nextToken(":."));

    Calendar = new GregorianCalendar( year, month-1, date, hour, minute, second);
  }

  /**
   * setur 0 fyrir hour, minute og second og lætur is_SQLDate() skila true. toString() skilar einnig aðeins Date-hlutanum
   */

  public idegaTimestamp(java.sql.Date date) {
    isDate = true;

    String TimeString = date.toString();

    int year = Integer.parseInt(TimeString.substring(0,4));
    int month = Integer.parseInt(TimeString.substring(5,7));
    int day = Integer.parseInt(TimeString.substring(8,10));

    Calendar = new GregorianCalendar( year, month-1, day, 0, 0, 0);
  }

  public idegaTimestamp(int day, int month, int year) {
    isDate = true;

    Calendar = new GregorianCalendar( year, month-1, day, 0, 0, 0);
  }

  public idegaTimestamp(String day, String month, String year) {
    isDate = true;

    Calendar = new GregorianCalendar(Integer.parseInt(year), (Integer.parseInt(month)-1), Integer.parseInt(day), 0, 0, 0);
  }


  /**
   * setur 1 fyrir year, month og date og lætur is_SQLTime() skila true. toString() skilar einnig aðeins Time-hlutanum
   */

  public idegaTimestamp(Time time) {
    isTime = true;

    String TimeString = time.toString();

    int hour = Integer.parseInt(TimeString.substring(0,2));
    int minute = Integer.parseInt(TimeString.substring(3,5));
    int second = Integer.parseInt(TimeString.substring(6,7));

    Calendar = new GregorianCalendar( 1, 1, 1, hour, minute, second);
  }



  /**
   * String-format 'yyyy-mm-dd hh:mm:ss' || 'yyyy-mm-dd' || 'hh:mm:ss'
   */

  public idegaTimestamp(String SQLFormat) {
    this();
    String TimeString = SQLFormat;

    if (TimeString.length() == 10){
      Calendar = new idegaTimestamp(java.sql.Date.valueOf(SQLFormat)).getGregorianCalendar();
      isDate = true;
      }else if (TimeString.length() == 8){
          Calendar = new idegaTimestamp(Time.valueOf(SQLFormat)).getGregorianCalendar();
          isTime = true;
        }else
            Calendar = new idegaTimestamp(Timestamp.valueOf(SQLFormat)).getGregorianCalendar();

  }


  /**
   *   Her skal skrifa numer manaðar frá 1 til 12
   */

  public idegaTimestamp( int year, int month, int date, int hour, int minute, int second) {
    Calendar = new GregorianCalendar( year, month-1, date, hour, minute, second);
  }

    // ###########################
   /// ####  Private - Föll #####
  //// #########################

  /**
  *breytir x < 10 í "0X" ;
  */

  private String addZeros( int numberToFix){
  	String FixedNumber;
	if (numberToFix <10 )
		FixedNumber = "0";
	else
		FixedNumber = "";

	return FixedNumber + numberToFix;
  }


    // ###########################
   /// ####  Public - Föll  #####
  //// #########################


  public boolean is_SQLDate(){
    if (isDate)
      return true;
    else
      return false;
  }

  public boolean is_SQLTime(){
    if (isTime)
      return true;
    else
      return false;
  }

  public boolean is_idegaTimestamp(){
    if (isTime || isDate)
      return false;
    else
      return true;
  }

  public void setAsDate(){
    isDate= true;
    isTime=false;
  }

  public void setAsTime(){
    isDate= false;
    isTime=true;
  }


  public boolean isLaterThan( idegaTimestamp compareStamp ){


    if(!this.isTime){
      if (this.getYear() > compareStamp.getYear())
        return true;
      if (this.getYear() < compareStamp.getYear())
        return false;
      if (this.getMonth() > compareStamp.getMonth())
        return true;
      if (this.getMonth() < compareStamp.getMonth())
        return false;
      if (this.getDate() > compareStamp.getDate())
        return true;
      if (this.getDate() < compareStamp.getDate())
        return false;
    }

    if(!this.isDate){
    if (this.getHour() > compareStamp.getHour())
      return true;
    if (this.getHour() < compareStamp.getHour())
      return false;
    if (this.getMinute() > compareStamp.getMinute())
      return true;
    if (this.getMinute() < compareStamp.getMinute())
      return false;
    if (this.getSecond() > compareStamp.getSecond())
      return true;
    if (this.getSecond() < compareStamp.getSecond())
      return false;
    }
      return false;
  }


  public idegaTimestamp isLater( idegaTimestamp time, idegaTimestamp time2 ){

    if (time.getYear() > time2.getYear())
      return time;
    if (time.getMonth() > time2.getMonth())
      return time;
    if (this.getDate() > time2.getDate())
      return time;
    if (time.getHour() > time2.getHour())
      return time;
    if (time.getMinute() > time2.getMinute())
      return time;
    if (time.getSecond() > time2.getSecond())
      return time;

      return time2;
  }

 public idegaTimestamp isEarlier( idegaTimestamp time, idegaTimestamp time2 ){

    if (time.getYear() < time2.getYear())
      return time;
    if (time.getMonth() < time2.getMonth())
      return time;
    if (this.getDate() < time2.getDate())
      return time;
    if (time.getHour() < time2.getHour())
      return time;
    if (time.getMinute() < time2.getMinute())
      return time;
    if (time.getSecond() < time2.getSecond())
      return time;

      return time2;
  }




 public Time isEarlier( Time time_, Time time2_ ){
    idegaTimestamp time = new idegaTimestamp(time_);
    idegaTimestamp time2 = new idegaTimestamp(time2_);

    if (time.getHour() < time2.getHour())
      return time.getSQLTime();
    if (time.getMinute() < time2.getMinute())
      return time.getSQLTime();
    if (time.getSecond() < time2.getSecond())
      return time.getSQLTime();

      return time2.getSQLTime();
  }



 public java.sql.Date isEarlier( java.sql.Date date, java.sql.Date date2 ){
    idegaTimestamp time = new idegaTimestamp(date);
    idegaTimestamp time2 = new idegaTimestamp(date2);


    if (time.getYear() < time2.getYear())
      return time.getSQLDate();
    if (time.getMonth() < time2.getMonth())
      return time.getSQLDate();
    if (this.getDate() < time2.getDate())
      return time.getSQLDate();

      return time2.getSQLDate();
  }


  /**
   * til að berasaman Date-hlutann er sett '????.eqals(?????.getSQLDate());
   */

  public boolean equals(java.sql.Date date){
    return toSQLDateString().equals(date.toString());
   }

  /**
   * til að berasaman Time-hlutann er sett '????.eqals(?????.getSQLTime());
   */

  public boolean equals(Time time){
    return toSQLTimeString().equals(time.toString());
  }



  public GregorianCalendar getGregorianCalendar(){
    return Calendar;
  }




  /*public Date getSQLDate(){
    //Date date = new Date();
    String M, D;

    if (getMonth() < 10)
      M = "0" + getMonth();
    else
      M = "" + getMonth();

    if (getDate() < 10)
      D = "0" + getDate();
    else
      D = "" + getDate();
    return Date.valueOf(getYear() + "-" + M + "-" + D);
  }*/



  public Timestamp getTimestamp(){
    //Timestamp newStamp = new Timestamp(0);

    String M, D, H, MIN, S;

    if (getMonth() < 10)
      M = "0" + getMonth();
    else
      M = "" + getMonth();

    if (getDate() < 10)
      D = "0" + getDate();
    else
      D = "" + getDate();

    if (getHour() < 10)
      H = "0" + getHour();
    else
      H = "" + getHour();

    if (getMinute() < 10)
      MIN = "0" + getMinute();
    else
      MIN = "" + getMinute();

    if (getSecond() < 10)
      S = "0" + getSecond();
    else
      S = "" + getSecond();

    return Timestamp.valueOf(getYear() + "-" + M + "-" + D + " "+ H + ":" + MIN + ":" + S + ".0");
  }



  public java.sql.Date getSQLDate(){
    //java.sql.Date newDate = new java.sql.Date(0);
    return java.sql.Date.valueOf(toSQLDateString());
  }


  public Time getSQLTime(){
    //Time newTime = new Time(0);
    return Time.valueOf(toSQLTimeString());
  }




  public int getYear(){
   return Calendar.get(Calendar.YEAR);
  }

  public int getMonth(){
   return Calendar.get(Calendar.MONTH)+1;
  }

  public int getDay(){
   return Calendar.get(Calendar.DAY_OF_MONTH);
  }

  public int getDate(){
   return Calendar.get(Calendar.DATE);
  }

  public int getHour(){
   return Calendar.get(Calendar.HOUR_OF_DAY);
  }

  public int getMinute(){
   return Calendar.get(Calendar.MINUTE);
  }

  public int getSecond(){
   return Calendar.get(Calendar.SECOND);
  }

  public int getDayOfWeek() {
    return Calendar.get(Calendar.DAY_OF_WEEK);
  }

  public int getWeekOfYear() {
    return Calendar.get(Calendar.WEEK_OF_YEAR);
  }

  public void setYear( int year ){
    Calendar = new GregorianCalendar( year, getMonth(), getDate(), getHour(), getMinute(), getSecond());
  }

  public void setMonth( int month ){
    Calendar = new GregorianCalendar( getYear(), month-1, getDate(), getHour(), getMinute(), getSecond());
  }

  public void setDate( int date ){
    Calendar = new GregorianCalendar( getYear(), getMonth() - 1, date, getHour(), getMinute(), getSecond());
  }

  public void setHour( int hour ){
    Calendar = new GregorianCalendar( getYear(), getMonth() - 1, getDate(), hour, getMinute(), getSecond());
  }

  public void setMinute( int minute ){
    Calendar = new GregorianCalendar( getYear(), getMonth() - 1, getDate(), getHour(), minute, getSecond());
  }

  public void setSecond( int second ){
    Calendar = new GregorianCalendar( getYear(), getMonth() - 1, getDate(), getHour(), getMinute(), second );
  }

/*
  //  Virkar ekki alveg rett, leggur við hlutinn en skilar ekki bara.
  public idegaTimestamp getNextDay(){
    GregorianCalendar myCalendar = Calendar;
    myCalendar.add(myCalendar.DATE, 1 );
    return new idegaTimestamp( myCalendar );
  }
*/


  /**
   * Gefur dagsetingu + eða - dögum fra gildi idegaTimestampsins
   */

  public void addDays( int num_of_days ){
    Calendar.add(Calendar.DATE, num_of_days );
  }

  public void addMinutes( int num_of_minutes ){
    Calendar.add(Calendar.MINUTE, num_of_minutes );
  }

  public void addMonths( int num_of_months ){
    Calendar.add(Calendar.MONTH, num_of_months );
  }

  public void addYears( int num_of_years ){
    Calendar.add(Calendar.YEAR, num_of_years );
  }

  public String getLocaleDate(ModuleInfo modinfo) {
    Locale currentLocale = modinfo.getCurrentLocale();
    return getLocaleDate(currentLocale);
  }

  public String getLocaleDate(Locale currentLocale) {
    String returner = getENGDate();

    if(currentLocale.equals(com.idega.util.LocaleUtil.getIcelandicLocale())){
        returner = getISLDate();
    }else if (currentLocale.equals(Locale.ENGLISH)){
        returner = getENGDate();
    }else if (currentLocale.equals(Locale.UK)){
        returner = getENGDate();
    }else if (currentLocale.equals(Locale.US)){
        returner = getENGDate();
    }else {
        returner = getENGDate();
    }

    return returner;
  }

  /**
   * @Deprecated
   */
  public String getISLDate(){
    return getDate() + "."+getNameOfMonth(getMonth())+" "+getYear();
  }

  /**
   * @Deprecated
   */
  public String getENGDate(){

    String englishDate = getEnglishNameOfMonth(getMonth())+" "+getDate();

    if ( getDate() == 1 ) {
      englishDate = englishDate + "st "+getYear();
    }
    else if ( getDate() == 2 ) {
      englishDate = englishDate + "nd "+getYear();
    }
    else if ( getDate() == 3 ) {
      englishDate = englishDate + "rd "+getYear();
    }
    else if ( getDate() == 21 ) {
      englishDate = englishDate + "st"+getYear();
    }
    else if ( getDate() == 22 ) {
      englishDate = englishDate + "nd "+getYear();
    }
    else if ( getDate() == 31 ) {
      englishDate = englishDate + "st "+getYear();
    }
    else {
      englishDate = englishDate + "th "+getYear();
    }

    return englishDate;
  }

  public String getISLDate(String spacer, boolean withYear){
    if (withYear)
      return getDate() + spacer + getMonth() + spacer + getYear();
    else
      return getDate() + spacer + getMonth();
  }



/*
  public int getYear_int( int num_of_last_char ){
    if (num_of_last_char < 1)
      return -1;

    String year = "" + getYear();
    String temp = "";
    for (int i = 1; i <= num_of_last_char; i++ ){
      if ( i > year.length())
        continue;
      temp = year.charAt(year.length() - i) + temp;
    }
    return Integer.parseInt(temp);
  }*/

  public String getYear( int num_of_last_char ){

    if (num_of_last_char < 1)
      return "Error";

    String year = "" + getYear();
    String temp = "";
    for (int i = 1; i <= num_of_last_char; i++ ){
      if ( i > year.length())
        continue;
      temp = year.charAt(year.length() - i) + temp;
    }
    return temp;
  }


//  public String toISLString(){}



  //  ###########  Gimma - föll ########


	private int getLengthOfMonth(int mon, int year){

	    int dagarman = 31;

		switch (mon) {
			case  0 :
		          dagarman = 31;
		          break;
                        case 1 :
                          dagarman=31;
                          break;
			case 2 :
      			        if (Calendar.isLeapYear(year)) {
                                  dagarman=29;
                                }
                                else {
                                  dagarman=28;
				}
                              break;
                        case 3 :
                          dagarman=31;
                          break;
			case 4 :
	                  dagarman = 30;
			  break;
                        case 5 :
                          dagarman=31;
                          break;
                        case 6 :
                          dagarman=30;
                          break;
                        case 7 :
                          dagarman=31;
                          break;
                        case 8 :
                          dagarman=31;
                          break;
                        case 9 :
                          dagarman=30;
                          break;
                        case 10 :
                          dagarman=31;
                          break;
                        case 11 :
                          dagarman=30;
                          break;
                        case 12 :
                          dagarman=31;
                          break;
                        case 13 :
                          dagarman=31;
                          break;

		}
		return dagarman;
	}



  private String getNameOfMonth(int month){
	String manudurnafn ="";

		switch (month) {
			case 0  :
					manudurnafn=("desember");
				break;
			case 01 :
					manudurnafn=("janúar");
				break;
			case 02 :
					manudurnafn=("febrúar");
				break;
			case 03 :
					manudurnafn=("mars");
				break;
			case 04 :
					manudurnafn=("apríl");
				break;
			case 05 :
					manudurnafn=("maí");
				break;
			case 06 :
					manudurnafn=("júní");
				break;
			case 07 :
					manudurnafn=("júlí");
				break;
			case 8 :
					manudurnafn=("ágúst");
				break;
			case 9 :
					manudurnafn=("september");
				break;
			case 10 :
					manudurnafn=("október");
				break;
			case 11 :
					manudurnafn=("nóvember");
				break;
			case 12 :
					manudurnafn=("desember");
				break;
			case 13 :
					manudurnafn=("janúar");
				break;
		}
    return manudurnafn;
  }

  private String getEnglishNameOfMonth(int month){
	String manudurnafn ="";

		switch (month) {
			case 0  :
					manudurnafn=("December");
				break;
			case 01 :
					manudurnafn=("January");
				break;
			case 02 :
					manudurnafn=("February");
				break;
			case 03 :
					manudurnafn=("March");
				break;
			case 04 :
					manudurnafn=("April");
				break;
			case 05 :
					manudurnafn=("May");
				break;
			case 06 :
					manudurnafn=("June");
				break;
			case 07 :
					manudurnafn=("July");
				break;
			case 8 :
					manudurnafn=("August");
				break;
			case 9 :
					manudurnafn=("September");
				break;
			case 10 :
					manudurnafn=("October");
				break;
			case 11 :
					manudurnafn=("November");
				break;
			case 12 :
					manudurnafn=("December");
				break;
			case 13 :
					manudurnafn=("January");
				break;
		}
    return manudurnafn;
  }

  /*
  public String getNameOfMonth(int month) {
      if(month < 1 || month > 12)
          return "";
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getMonths()[month-1];
  }*/


	private String getNameOfDay(int dagur) {
		String nafn="";

		switch (dagur) {
			case 1:
				nafn=("Sunnudagur");
				break;
			case 2:
				nafn=("Mánudagur");
				break;
			case 3:
				nafn=("Þriðjudagur");
				break;
			case 4:
				nafn=("Miðvikudagur");
				break;
			case 5:
				nafn=("Fimmtudagur");
				break;
			case 6:
				nafn=("Föstudagur");
				break;
			case 7:
				nafn=("Laugardagur");
				break;
		}
		return nafn;
	}

	private String getEnglishNameOfDay(int dagur) {
		String nafn="";

		switch (dagur) {
			case 1:
				nafn=("Sunday");
				break;
			case 2:
				nafn=("Monday");
				break;
			case 3:
				nafn=("Tuesday");
				break;
			case 4:
				nafn=("Wednesday");
				break;
			case 5:
				nafn=("Thursday");
				break;
			case 6:
				nafn=("Friday");
				break;
			case 7:
				nafn=("Saturday");
				break;
		}
		return nafn;
	}

	private int getDayOfWeek(int ar,int manudur,int dagur) {

		GregorianCalendar myCalendar = new GregorianCalendar(ar,manudur-1,dagur);

		int vdagur = myCalendar.get(Calendar.DAY_OF_WEEK);


                return vdagur;
	}


	private boolean getHoliday(int ar, int manudur, int dagur) {

		boolean svara =false;

/*			if ( (getDayOfWeek(ar,manudur,dagur) == 1) || (getDayOfWeek(ar,manudur,dagur) == 7) )  {
				svara = true;
			}*/

			if  (getDayOfWeek(ar,manudur,dagur) == 1) {
				svara = true;
			}


			if (!(svara))
			switch (manudur) {
				case 1:
					if (dagur==1) svara=true;
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					if (dagur==1) svara=true;
					break;
				case 6:
					if (dagur==17) svara=true;
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
				case 11:
					break;
				case 12:
					break;

			}

		return svara;
	}



        //  ####  Gimma - föll enda  ####

/*
	public static Timestamp getTimestampRightNow(){
		//Needs to change because Timestamp constructor is depricated
	  GregorianCalendar myCalendar = new GregorianCalendar();
	  return new Timestamp(myCalendar.get(myCalendar.YEAR)-1900, myCalendar.get(myCalendar.MONTH), myCalendar.get(myCalendar.DATE), myCalendar.get(myCalendar.HOUR_OF_DAY), myCalendar.get(myCalendar.MINUTE), myCalendar.get(myCalendar.SECOND),0);
	}
*/
 	public static Timestamp getTimestampRightNow(){
          idegaTimestamp stamp = RightNow();
          return stamp.getTimestamp();
	}

	public static idegaTimestamp RightNow(){
	  GregorianCalendar myCalendar = new GregorianCalendar();
	  //return new idegaTimestamp(myCalendar.get(myCalendar.YEAR), myCalendar.get(myCalendar.MONTH)+1, myCalendar.get(myCalendar.DATE), myCalendar.get(myCalendar.HOUR_OF_DAY), myCalendar.get(myCalendar.MINUTE), myCalendar.get(myCalendar.SECOND));
          return new idegaTimestamp(myCalendar);
	}


        public String toSQLString(){
          if(is_SQLDate())
            return toSQLDateString();
          else if (is_SQLTime())
                return toSQLTimeString();
              else
                return getTimestamp().toString();
        }

        public String toOracleString(){
	  String toReturn = " TO_DATE('";
        //  TO_DATE('1998 05 20 12:12','YYYY MM DD HH:MI'))
       //date
          toReturn += getYear()+" "+ addZeros(getMonth()) + " " + addZeros(getDate());
       //time
          toReturn += " " + addZeros(getHour()) + ":" + addZeros(getMinute());

  	//if (Sec) toReturn += "." + addZeros(getSecond());
        return toReturn+"','YYYY MM DD HH24:MI') ";
        }

        public String toString(){
          return this.getTimestamp().toString();
        }

        public String toSQLDateString(){
           StringTokenizer tokens = new StringTokenizer(getTimestamp().toString());
           return tokens.nextToken();
        }

        public String toSQLTimeString(){
          StringTokenizer tokens = new StringTokenizer(getTimestamp().toString());
          String temp = tokens.nextToken();

          return tokens.nextToken();
        }

	public String toString( boolean Days_before_Months, boolean FourDigitYear, boolean Sec){

		String toReturn = "";

		if (Days_before_Months){
			toReturn += addZeros(getDate()) + "." + addZeros(getMonth());
		} else {
			toReturn += addZeros(getMonth()) + "." + addZeros(getDate());
		 }

		if (FourDigitYear){
			toReturn += "." + getYear();
		} else {
		 	toReturn += "." + getYear(2);
		 }

		toReturn += " " + addZeros(getHour()) + ":" + addZeros(getMinute());

		if (Sec)
			toReturn += "." + addZeros(getSecond());

		return toReturn;
	}

  public static Locale getIceland() {
      return new Locale("is", "IS");
  }


  public static int getDaysBetween(idegaTimestamp before, idegaTimestamp after){
    long lBefore = before.getGregorianCalendar().getTime().getTime();
    long lAfter = after.getGregorianCalendar().getTime().getTime();

    long diff = lAfter - lBefore;

    return (int)(diff/86400000);
  }

  public static int getMinutesBetween(idegaTimestamp before, idegaTimestamp after){
    if(before.isTime || after.isTime){
      before.setDate(1);
      before.setMonth(2);
      before.setYear(1);
      after.setDate(1);
      after.setMonth(2);
      after.setYear(1);
    }
    long lBefore = before.getGregorianCalendar().getTime().getTime();
    long lAfter = after.getGregorianCalendar().getTime().getTime();

    long diff = lAfter - lBefore;

    return (int)(diff/60000);
  }


}   // class idegaTimestamp

package com.idega.util;

import java.util.*;
import java.text.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DateManipulator {

  GregorianCalendar m_cal;
  public DateManipulator(int day, int month, int year) {
      m_cal = new GregorianCalendar(year, month, day);
  }

  public DateManipulator() {
  }

  public static void main(String[] args) {
    DateManipulator date = new DateManipulator();
    //Date pars = DateManipulator.getDateFromSocialSecurityNumber("150176-4769");
    java.sql.Date date1 = DateManipulator.parseSQLDate(new Date());
    DayWeekMonthYear[] dates = DateManipulator.getWholeMonth(2, 2001);

    for(int i = 0; i < dates.length; i++) {
        System.out.println("Númer dagsins er "+dates[i].getDayOfWeek()+" "+dates[i].getShortDay()+" "+dates[i].getDayOfMonth()+". "+dates[i].getShortMonth()+" vika mánaðar "+dates[i].getWeekOfMonht());
    }
    int day = 29, month = 2, year = 2004;

  }

  public Date[] getDatesComming(int days) {
      Date toDaysDate = new Date();
      Date[] dateReturn = new Date[days];
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDaysDate);
      for(int i = 0; i < days; i++) {
          dateReturn[i] = getFutureDate(i);
      }
      return dateReturn;
  }

  public String[] getStringDatesComming(int days) {
      Date toDaysDate = new Date();
      String[] dateReturn = new String[days];
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDaysDate);
      for(int i = 0; i < days; i++) {
          dateReturn[i] = getStringFutureDate(i);
      }
      return dateReturn;
  }

  public String[] getStringDatesComming(int daysFromToDay, int dateFormatStyle) {
      Date toDaysDate = new Date();
      String[] dateReturn = new String[daysFromToDay];
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDaysDate);
      for(int i = 0; i < daysFromToDay; i++) {
          dateReturn[i] = getStringFutureDate(i, dateFormatStyle);
      }
      return dateReturn;
  }


  public static java.util.Date getFutureDate(int days) {
      Date toDaysDate = new Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(toDaysDate);
      cal.add(Calendar.DATE, days);
      return cal.getTime();
  }

  public String getStringFutureDate(int daysFromToDay) {
      DateFormat dFormat = DateFormat.getDateInstance();
      return dFormat.format(getFutureDate(daysFromToDay));
  }

  public String getStringFutureDate(int daysFromToDay, int dateFormatStyle) {
      DateFormat dFormat = DateFormat.getDateInstance(dateFormatStyle);
      return dFormat.format(getFutureDate(daysFromToDay));
  }

  public Date parseDate(String date) {
      if(isDigitOnly(date) && date.length() > 5)
          date = formatSosialSecurityNumber(date, false);

      DateFormat dFormat = DateFormat.getDateInstance(DateFormat.HOUR_OF_DAY0_FIELD, getIceland());
      try {
        return dFormat.parse(date);
      }
      catch(Exception e) {
          System.out.println("Error in DateManipulator.getDateFromSocialSecurityNumber "+e.getMessage());
      }
      return null;
  }

  private static boolean isDigitOnly(String value) {
      if(value.length() == 0)
          return false;
      char[] arr = value.toCharArray();
      for(int i = 0; i < arr.length; i++) {
          if(! Character.isDigit(arr[i]))
              return false;
      }
      return true;
  }



  private static String formatSosialSecurityNumber(String toForm, boolean sql) {
      String returnString = "";
      if(toForm.length() < 6)
          return returnString;
      String day = toForm.substring(0, 2);
      String month = toForm.substring(2, 4);
      String year = toForm.substring(4, 6);
      if(year.charAt(0) == '0')
          year = "20"+year;
      else
          year = "19"+year;
      if(sql)
          returnString = year+"-"+month+"-"+day;
      else
          returnString = day+"."+month+"."+year;
      return returnString;
  }

  public static Date getDateFromSocialSecurityNumber(String num) {
      if(num.length() < 6) return null;
      else num = num.substring(0, 6);

      if(isDigitOnly(num))
          num = formatSosialSecurityNumber(num, false);
      else return null;

      DateFormat dFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD, getIceland());
      try {
        Date date = dFormat.parse(num);
        return date;
      }
      catch(Exception e) {
          System.out.println("Error in IdegaTimestamp.getDateFromSocialSecurityNumber "+e.getMessage());
      }
      return null;
  }

  public static java.sql.Date getDateSQLFromSocialSecurityNumber(String num) {
      if(num.length() < 6) return null;
      else num = num.substring(0, 6);

      if(isDigitOnly(num))
          return java.sql.Date.valueOf(formatSosialSecurityNumber(num, true));

      return null;
  }

  public static Locale getIceland() {
      return new Locale("is", "IS");
  }

  public static java.sql.Date parseSQLDate(java.util.Date utilDate) {
      DateFormat dFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD, getIceland());
      String dateVal = dFormat.format(utilDate);
      dateVal = dateStringToSqlDateString(dateVal);
      return java.sql.Date.valueOf(dateVal);
  }

  public static DayWeekMonthYear parseDayWeekMonthYear(java.util.Date utilDate) {
      DateFormat dFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD, getIceland());
      String dateVal = dFormat.format(utilDate);
      int[] intArr = getYearMonthDay(dateVal);
      return new DayWeekMonthYear(intArr[2], intArr[1], intArr[0]);
  }

  public static DayWeekMonthYear parseDayWeekMonthYear(java.sql.Date sqlDate) {
      int[] intArr = getYearMonthDay(sqlDate.toString());
      return new DayWeekMonthYear(intArr[0], intArr[1], intArr[2]);
  }

  public static String[] getIslandicMonths() {
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      String[] monthsArr = months.getMonths();
      return monthsArr;
  }

  public static String getNameOfMonth(int month, Locale locale) {
      if(month < 1 || month > 12)
          return "";
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getMonths()[month-1];
  }

  public static String getShortNameOfMonth(int month, Locale locale) {
      if(month < 1 || month > 12)
          return "";
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getShortMonths()[month-1];
  }

  public static String getShortNameOfMonth(int month) {
      if(month < 1 || month > 12)
          return "";
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getShortMonths()[month-1];
  }

  public static String[] getShortMonths(Locale locale) {
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getShortMonths();
  }

  public static String[] getMonths(Locale locale) {
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getMonths();
  }

  /** returns the icelandic syntax for the months */
  public static String[] getShortMonths() {
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getShortMonths();
  }

  /** returns the icelandic syntax for the month */
  public static String getNameOfMonth(int month) {
      if(month < 1 || month > 12)
          return "";
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getMonths()[month-1];
  }

  /** returns the icelandic syntax for the day */
  public static String getNameOfDay(int dayInWeek) {
      if(dayInWeek < 1 || dayInWeek > 7)
          return "";

      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getWeekdays()[dayInWeek-1];
  }

  public static String getNameOfDay(int dayInWeek, Locale locale) {
      if(dayInWeek < 1 || dayInWeek > 7)
          return "";

      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getWeekdays()[dayInWeek-1];
  }

  /** default returns icelandic name of days*/
  public static String[] getWeekDays() {
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getWeekdays();
  }

  public static String[] getWeekDays(Locale locale) {
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getWeekdays();
  }

  public static String[] getShortWeekDays() {
      DateFormatSymbols months = new DateFormatSymbols(getIceland());
      return months.getShortWeekdays();
  }

  public static String[] getShortWeekDays(Locale locale) {
      DateFormatSymbols months = new DateFormatSymbols(locale);
      return months.getShortWeekdays();
  }

  private static String dateStringToSqlDateString(String IS_formatUtilDate) {
      //intArr[0] = year, intArr[1] = month, intArr[2] = day
      int[] intArr = getYearMonthDay(IS_formatUtilDate);
      StringBuffer buffer = new StringBuffer();
      buffer = buffer.append(intArr[0]);
      buffer = buffer.append('-');
      buffer = buffer.append(intArr[1]);
      buffer = buffer.append('-');
      buffer.append(intArr[2]);
      return buffer.toString();
  }

  /*
  private static String dateStringToSqlDateString(String IS_formatUtilDate) {
      StringTokenizer token = new StringTokenizer(IS_formatUtilDate, ".");
      String day = (String)token.nextElement();
      String month = (String)token.nextElement();
      StringBuffer buffer = new StringBuffer((String)token.nextElement());
      buffer = buffer.append('-');
      buffer = buffer.append(month);
      buffer = buffer.append('-');
      buffer.append(day);
      return buffer.toString();
  }
  */

  private static int[] getYearMonthDay(String IS_formatUtilDate) {
      IS_formatUtilDate = IS_formatUtilDate.replace('-', '.');
      StringTokenizer token = new StringTokenizer(IS_formatUtilDate, ".");
      int[] intArr = new int[3];
      String day = (String)token.nextElement();
      String month = (String)token.nextElement();
      String year = (String)token.nextElement();
      intArr[0] = Integer.parseInt(year);
      intArr[1] = Integer.parseInt(month);
      intArr[2] = Integer.parseInt(day);
      return intArr;
  }

  public static int getIntDaysOfTheMonth(int month, int year) {
      GregorianCalendar cal = new GregorianCalendar(year, month-1, 1);
      int dayOfMonth = 0;
      for(int i = 0; i < 31; i++) {
          cal.add(Calendar.DATE, 1);
          dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
          if(dayOfMonth < i) {
              return i+1;
          }
      }
      return -1;
  }

  public static DayWeekMonthYear[] getWholeMonth(int month, int year) {
      int months = getIntDaysOfTheMonth(month, year);
      DayWeekMonthYear[] dates = new DayWeekMonthYear[months];
      for(int i = 0; i < months; i++) {
          dates[i] = new DayWeekMonthYear(i+1, month, year);
      }
      return dates;
  }

  public GregorianCalendar getGregorianCalendar() {
      return m_cal;
  }




}

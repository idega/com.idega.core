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



public class DayWeekMonthYear {



  private int dayOfWeek;

  private int dayOfMonth;

  private int weekOfMonth;

  private int weekOfYear;

  private int month;

  private int year;

  private int dayOfYear;

  private Locale locale;

  Calendar m_cal;

  DateFormatSymbols symbol;





  public DayWeekMonthYear(int dayOfMonth, int month, int year) {

      locale = new Locale("is", "IS");

      symbol = new DateFormatSymbols(locale);

      m_cal = new GregorianCalendar(year, month-1, dayOfMonth);

      this.dayOfMonth = m_cal.get(Calendar.DATE);

      this.month = m_cal.get(Calendar.MONTH)+1;

      this.year = m_cal.get(Calendar.YEAR);

      this.dayOfWeek = m_cal.get(Calendar.DAY_OF_WEEK);

      this.weekOfMonth = m_cal.get(Calendar.WEEK_OF_MONTH);

      this.weekOfYear = m_cal.get(Calendar.WEEK_OF_YEAR);

      this.dayOfYear = m_cal.get(Calendar.DAY_OF_YEAR);

  }



  public DayWeekMonthYear(int dayOfMonth, int month, int year, Locale locale) {

      this.locale = locale;

      symbol = new DateFormatSymbols(locale);

      m_cal = new GregorianCalendar(year, month-1, dayOfMonth);

      this.dayOfMonth = m_cal.get(Calendar.DATE);

      this.month = m_cal.get(Calendar.MONTH);

      this.year = m_cal.get(Calendar.YEAR);

      this.dayOfWeek = m_cal.get(Calendar.DAY_OF_WEEK);

       this.dayOfYear = m_cal.get(Calendar.DAY_OF_YEAR);

      this.weekOfMonth = m_cal.get(Calendar.WEEK_OF_MONTH);

      this.weekOfYear = m_cal.get(Calendar.WEEK_OF_YEAR);

  }



  public DayWeekMonthYear(Locale locale) {

      this.locale = locale;

      symbol = new DateFormatSymbols(locale);

      m_cal = Calendar.getInstance(locale);

      this.dayOfMonth = m_cal.get(Calendar.DATE);

      this.month = m_cal.get(Calendar.MONTH);

      this.year = m_cal.get(Calendar.YEAR);

      this.dayOfWeek = m_cal.get(Calendar.DAY_OF_WEEK);

      this.weekOfMonth = m_cal.get(Calendar.WEEK_OF_MONTH);

      this.weekOfYear = m_cal.get(Calendar.WEEK_OF_YEAR);

      this.dayOfYear = m_cal.get(Calendar.DAY_OF_YEAR);

  }





  public static void main(String[] args) {

      DayWeekMonthYear date = new DayWeekMonthYear(31, 12, 2003);

      System.out.println("mánaðardagur "+date.dayOfMonth);

      System.out.println("vikudagur "+date.getDayOfWeek());

      System.out.println("vika mánaðar "+date.getWeekOfMonht());

      System.out.println("vika árs "+date.getWeekOfYear());

      System.out.println("mánuður "+date.getMonth());

      System.out.println("ár "+date.getYear());

      System.out.println("vikudagur "+date.getStringDayOfWeek());

      System.out.println("mánuður "+date.getStringMonth());

      System.out.println("dagur ars "+date.getDayOfYear());

  }



  public int getDayOfWeek() {

      if(this.dayOfWeek == 0)

          return 7;

      return this.dayOfWeek;

  }



  public int getDayOfMonth() {

      return this.dayOfMonth;

  }



  public int getDayOfYear() {

      return dayOfYear;

  }



  public int getWeekOfMonht() {

      return weekOfMonth;

  }



  public int getWeekOfYear() {

      return this.weekOfYear;

  }



  public int getMonth() {

      return this.month;

  }



  public int getYear() {

      return this.year;

  }



  public String getShortMonth() {

      return symbol.getShortMonths()[this.month-1];

  }



  public String getShortDay() {

      return symbol.getShortWeekdays()[this.dayOfWeek];

  }



  public String getStringMonth() {

      return symbol.getMonths()[this.month-1];

  }



  public String getStringDayOfWeek() {

      return symbol.getWeekdays()[this.dayOfWeek+1];

  }



  public boolean isPassed() {

      DayWeekMonthYear date = DateManipulator.parseDayWeekMonthYear(new Date());



      if(date.getYear() == this.getYear()) {

          if(date.getMonth() == this.getMonth())

              return this.getDayOfMonth() < date.getDayOfMonth();

          else if(date.getMonth() > this.getMonth())

              return true;

          else return false;

      }

      else if(date.getYear() > this.getYear())

          return true;



      return false;

  }



  public java.sql.Date toSQLDate() {

      StringBuffer buffer = new StringBuffer();

       buffer.append(getYear());

       buffer.append('-');

       buffer.append(getMonth());

       buffer.append('-');

       buffer.append(getDayOfMonth());

      return java.sql.Date.valueOf(buffer.toString());

  }



}

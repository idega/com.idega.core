/*
 * Created on 16.12.2003 by  tryggvil in project com.project
 */
package com.idega.util;

import java.sql.Date;

/**
 * CalendarMonth A class which represents a month of a year
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CalendarMonth extends TimePeriod{

	/**
	 * Constructs a month instance of the current month and year.
	 */
	public CalendarMonth(){
		//Cheating because of java limitations to only allow call to constructor first
		super(IWTimestamp.RightNow(),IWTimestamp.RightNow());
		IWTimestamp first = getFirstTimestampOfMonth(IWTimestamp.RightNow());
		IWTimestamp end = getLastTimestampOfMonthFromFirstDate(first);
		super.setFirstTimestamp(first);
		super.setLastTimestamp(end);
	}
	
	
	/**
	 * Constructs a month instance of a given month and the current year.
	 * @param month month in the range (1-12)
	 * @throws RuntimeException if the month is outside of range 1-12
	 */
	public CalendarMonth(int month){
		this();
		if(0<month && month<13){
			IWTimestamp first = getFirstTimestampOfMonth(IWTimestamp.RightNow());
			first.setMonth(month);
			IWTimestamp end = getLastTimestampOfMonthFromFirstDate(first);
			super.setFirstTimestamp(first);
			super.setLastTimestamp(end);
		}
		else{
			throw new RuntimeException("Month number "+month+" does not exist");
		}
	}
	/**
	 * Constructs a month instance of a given month and the given year.
	 * @param month month in the range (1-12)
	 * @param year year ( in format YYYY e.g.)
	 * @throws RuntimeException if the month is outside of range 1-12
	 */
	public CalendarMonth(int month,int year){
		this();
		if(0<month && month<13){
			IWTimestamp first = getFirstTimestampOfMonth(IWTimestamp.RightNow());
			first.setMonth(month);
			first.setMonth(year);
			IWTimestamp end = getLastTimestampOfMonthFromFirstDate(first);
			super.setFirstTimestamp(first);
			super.setLastTimestamp(end);
		}
		else{
			throw new RuntimeException("Month number "+month+" does not exist");
		}
	}
	
	/**
	 * Constructs a month instance of the month and year of the given date.
	 * @param dateOfMonth date to instanciate the month of
	 */
	public CalendarMonth(Date dateOfMonth){
		this();
		IWTimestamp first = getFirstTimestampOfMonth(new IWTimestamp(dateOfMonth));
		IWTimestamp end = getLastTimestampOfMonthFromFirstDate(first);
		super.setFirstTimestamp(first);
		super.setLastTimestamp(end);
	}
	
	/**
	 * Constructs a month instance of the month and year of the given timestamp.
	 * @param dateOfMonth timstamp to instanciate the month of
	 */
	public CalendarMonth(IWTimestamp dateOfMonth){
		this();
		IWTimestamp first = getFirstTimestampOfMonth(dateOfMonth);
		IWTimestamp end = getLastTimestampOfMonthFromFirstDate(first);
		super.setFirstTimestamp(first);
		super.setLastTimestamp(end);
	}
	
	/**
	 * Gets the absolute first Timestamp of the the month which the given timestamp is in  in (e.g. 2003-12-01 00:00:00)
	 * @param anyTimestampOfMonth any timestamp in the month to get the first timestamp from
	 * @return
	 */
	public static IWTimestamp getFirstTimestampOfMonth(IWTimestamp anyTimestampOfMonth){
		IWTimestamp first = new IWTimestamp(anyTimestampOfMonth);
		first.setAsDate();
		first.setDay(1);
		first.setHour(0);
		first.setMinute(0);
		first.setSecond(0);
		first.setMilliSecond(0);
		
		return first;
	}	
	
	/**
	 * Gets the absolute last Timestamp of the the month which the given timestamp is in (e.g. 2003-12-31 23:59:59.0000)
	 * @param anyTimestampOfMonth any timestamp in the month to get the last timestamp from
	 * @return
	 */
	public static IWTimestamp getLastTimestampOfMonth(IWTimestamp anyTimestampOfMonth){
		IWTimestamp first = getFirstTimestampOfMonth(anyTimestampOfMonth);
		return getLastTimestampOfMonthFromFirstDate(first);
	}
	
	/**
	 * Gets the absolute first Timestamp of the the month which the given timestamp is in (e.g. 2003-12-31 23:59:59.9999)
	 * @param firstTimestamp
	 * @return
	 */
	public static IWTimestamp getLastTimestampOfMonthFromFirstDate(IWTimestamp firstTimestamp){
		IWTimestamp end = new IWTimestamp(firstTimestamp);
		end.setAsDate();
		end.addMonths(1);
		//the absolute last millisecond int the month:
		end.getGregorianCalendar().setTimeInMillis(end.getGregorianCalendar().getTimeInMillis()-1);
		return end;
	}
	
	public Date getFirstDateOfMonth(){
		return super.getFirstTimestamp().getDate();
	}
	
	public Date getLastDateOfMonth(){
		return super.getLastTimestamp().getDate();
	}
	
	/**
	 * Gets the month in range [1-12]
	 * @return int number of the month
	 */
	public int getMonth(){
		return getFirstTimestamp().getMonth();
	}
	/**
	 * Sets the month in range [1-12]
	 */
	public void setMonth(int month){
		getFirstTimestamp().setMonth(month);
	}
	
	
	/**
	 * Gets the month in format YYYY)
	 * @return int number of the year
	 */
	public int getYear(){
		return getFirstTimestamp().getYear();
	}
	/**
	 * Sets the month in format YYYY)
	 */
	public void setYear(int year){
		getFirstTimestamp().setYear(year);
	}	
	
	public boolean isJanuary(){
		return getMonth()==1;
	}
	public boolean isFebruary(){
		return getMonth()==2;
	}
	public boolean isMarch(){
		return getMonth()==3;
	}
	public boolean isApril(){
		return getMonth()==4;
	}
	public boolean isMay(){
		return getMonth()==5;
	}
	public boolean isJune(){
		return getMonth()==6;
	}
	public boolean isJuly(){
		return getMonth()==7;
	}
	public boolean isAugust(){
		return getMonth()==8;
	}	
	public boolean isSeptember(){
		return getMonth()==9;
	}
	public boolean isOctober(){
		return getMonth()==10;
	}
	public boolean isNovember(){
		return getMonth()==11;
	}
	public boolean isDecember(){
		return getMonth()==12;
	}
	public String toString(){
		//DateFormat df = DateFormat.getInstance();
		//df.
		return getMonth()+"-"+getYear();
	}
	
	public CalendarMonth getNextCalendarMonth(){
		IWTimestamp newStamp = new IWTimestamp(this.getFirstDateOfMonth());
		newStamp.addMonths(1);
		return new CalendarMonth(newStamp);
	}
	
	public CalendarMonth getPreviousCalendarMonth(){
		IWTimestamp newStamp = new IWTimestamp(this.getFirstDateOfMonth());
		newStamp.addMonths(-1);
		return new CalendarMonth(newStamp);
	}
	public boolean equals(Object o){
		return toString().equals(o.toString());
	}
	
	/**
	 * Testing method:
	 * @author tryggvil
	 */
	public static void main(String ars[]){
		CalendarMonth month = new CalendarMonth();
		System.out.println("CurrentMonth: "+month.toString());
		System.out.println("CurrentMonth.firstTimeStamp: "+month.getFirstTimestamp().toString());
		System.out.println("CurrentMonth.lastTimeStamp: "+month.getLastTimestamp().toString());
		System.out.println("NextMonth: "+month.getNextCalendarMonth().toString());
		System.out.println("PreviousMonth: "+month.getPreviousCalendarMonth().toString());
		CalendarMonth july = new CalendarMonth(7);
		System.out.println("july: "+july.toString());
		System.out.println("july.firstTimeStamp: "+july.getFirstTimestamp().toString());
		System.out.println("july.lastTimeStamp: "+july.getLastTimestamp().toString());
	}
}

package com.idega.util;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class Age {

  private GregorianCalendar startDate;


  public Age(Date startdate) {
    this.startDate = new GregorianCalendar();
    startDate.setTime(startdate);
  }

  public Age(long date){
    this.startDate = new GregorianCalendar();
    this.startDate.setTime(new Date(date));
  }

  public int getYears(){
    GregorianCalendar now = new GregorianCalendar();
    return now.get(now.YEAR)- startDate.get(startDate.YEAR);
  }

	public int getMonths(){
		GregorianCalendar now = new GregorianCalendar();
		return now.get(now.MONTH)- startDate.get(startDate.MONTH);
	}

  public int getDays(){
    GregorianCalendar now = new GregorianCalendar();
    return now.get(now.DATE)- startDate.get(startDate.DATE);
  }

  public long getMinutes(){
    GregorianCalendar now = new GregorianCalendar();
    return now.get(now.MINUTE)- startDate.get(startDate.MINUTE);
  }

  public long getSeconds(){
    GregorianCalendar now = new GregorianCalendar();
    return now.get(now.SECOND)- startDate.get(startDate.SECOND);
  }

  public long getMilliSeconds(){
    GregorianCalendar now = new GregorianCalendar();
    return now.get(now.MILLISECOND)- startDate.get(startDate.MILLISECOND);
  }

  public Date getStartDate(){
    return this.startDate.getTime();
  }
  
  public boolean isOlder(Age age) {
  	boolean isOlder = false;

  	if (getStartDate().before(age.getStartDate()))
  		isOlder = true;
  	
  	return isOlder;
  }
}
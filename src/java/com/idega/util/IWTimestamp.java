package com.idega.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idega.presentation.IWContext;

/**
 * <code>IWTimestamp</code> is a class that can be used when working with SQL date/time
 * strings and dates/time in general.  Has numerous features to compare dates and can also
 * return a vast number of different date and time based objects.  It can also return
 * rightly formatted SQL date strings.
 *
 * Copyright (C) 2000 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 **
 * @author idega 2002 - idega team
 * @version 1.1
 */
public class IWTimestamp implements Comparable<IWTimestamp>, Cloneable {
	
	/**
	 * This field sets if the toString and toSQLString methods should cut off their milliseconds part in the returned String.
	 * This is default true but is set to false for certain databases such as Informix.
	 */
	public static boolean CUT_MILLISECONDS_OFF_IN_TOSTRING=true;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * date string like the following: 1970-10-06
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String DATE_PATTERN = IWTimestamp.YEAR + "-" + IWTimestamp.MONTH + "-" + IWTimestamp.DAY;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit day.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String DAY = "dd";
	
	/**
	 * A time string representing the time at the first second of a day.
	 */
	public static final String FIRST_SECOND_OF_DAY = "00:00:00.0000";
	
	/**
	 * A type setting for use with getLocaleDate.<br>
	 * FULL is pretty completely specified, such as Tuesday, April 12, 1952 AD or 3:30:42pm PST.
	 * @see IWTimestamp#getLocaleDate(Locale locale, int format)
	 */
	public static final int FULL = DateFormat.FULL;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit hour (0-23).
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String HOUR = "HH";
	
	/**
	 * A time string representing the time at the last second of a day.
	 */
	public static final String LAST_SECOND_OF_DAY = "23:59:59.9999";
	
	/**
	 * A type setting for use with getLocaleDate.<br>
	 * getLocaleDate: LONG is longer, such as January 12, 1952 or 3:30:32pm<br>
	 * getDayName/getMonthName: LONG is full name, such as January or December.
	 * @see IWTimestamp#getLocaleDate(Locale locale, int format)
	 */
	public static final int LONG = DateFormat.LONG;
	
	/**
	 * A type setting for use with getLocaleDate.<br>
	 * MEDIUM is longer, such as Jan 12, 1952
	 * @see IWTimestamp#getLocaleDate(Locale locale, int format)
	 */
	public static final int MEDIUM = DateFormat.MEDIUM;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * x digit milliseconds.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String MILLISECOND = "S";
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit minute.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String MINUTE = "mm";
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit month.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String MONTH = "MM";
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit second.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String SECOND = "ss";
	
	/**
	 * A type setting for use with getLocaleDate.<br>
	 * getLocaleDate: SHORT is completely numeric, such as 12.13.52 or 3:30pm.<br>
	 * getDayName/getMonthName: SHORT is abbreviated name, such as Jan or Dec.
	 * @see IWTimestamp#getLocaleDate(Locale locale, int format)
	 */
	public static final int SHORT = DateFormat.SHORT;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * date string like the following: 12:45:30.323
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String TIME_PATTERN = HOUR + ":" + MINUTE + ":" + SECOND + "." + MILLISECOND;
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * four digit year.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String YEAR = "yyyy";
	
	/**
	 * A format setting for use with getDateString. Represents the pattern to display a
	 * two digit year.
	 * @see IWTimestamp#getDateString(String pattern)
	 */
	public static final String YEAR_TWO_DIGITS = "yy";

	private GregorianCalendar calendar;
	private boolean isDate;
	private boolean isTime;

///////////////////////////////////////////////////
//      adders      
///////////////////////////////////////////////////

	/**
	 * Adds days to the current date setting.
	 * @param numberOfDays	The number of days to add.
	 */
	public void addDays(int numberOfDays) {
		this.calendar.add(Calendar.DAY_OF_MONTH, numberOfDays);
	}
	
	/**
	 * Adds weeks to the current date setting.
	 * @param numberOfWeeks	The number of weeks to add.
	 */
	public void addWeeks(int numberOfWeeks) {
		addDays(numberOfWeeks * 7);
	}

	/**
	 * Adds hours to the current date and time setting.
	 * @param numberOfHours	The number of hours to add.
	 */
	public void addHours(int numberOfHours) {
		this.calendar.add(Calendar.HOUR_OF_DAY, numberOfHours);
	}

	/**
	 * Adds minutes to the current date and time setting.
	 * @param numberOfMinutes	The number of minutes to add.
	 */
	public void addMinutes(int numberOfMinutes) {
		this.calendar.add(Calendar.MINUTE, numberOfMinutes);
	}

	/**
	 * Adds months to the current date setting.
	 * @param numberOfMonths	The number of months to add.
	 */
	public void addMonths(int numberOfMonths) {
		this.calendar.add(Calendar.MONTH, numberOfMonths);
	}

	/**
	 * Adds seconds to the current date and time setting.
	 * @param numberOfSeconds	The number of seconds to add.
	 */
	public void addSeconds(int numberOfSeconds) {
		this.calendar.add(Calendar.SECOND, numberOfSeconds);
	}

	/**
	 * Adds years to the current date setting.
	 * @param numberOfYears	The number of years to add.
	 */
	public void addYears(int numberOfYears) {
		this.calendar.add(Calendar.YEAR, numberOfYears);
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(IWTimestamp compareStamp) {
	  if (isEarlierThan(compareStamp)) {
			return -1;
		}
		else if (isLaterThan(compareStamp)) {
			return 1;
		}
		else {
			return 0;
		}
	}

	/**
	 * Returns true if the current date setting is identical to the date setting of the
	 * <code>Date</code> object.
	 * @param date		The date to compare with the current date setting.
	 * @return boolean
	 */
	public boolean equals(Date date) {
		return toSQLDateString().equals(date.toString());
	}

	/**
	 * Returns true if the current date and time settings are identical to the date 
	 * settings of the <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with the current date setting.
	 * @return boolean
	 */
	public boolean equals(IWTimestamp compareStamp) {
		if (!this.isTime() && !this.isDate()) {
			return (this.getYear() == compareStamp.getYear() && this.getMonth() == compareStamp.getMonth() && this.getDay() == compareStamp.getDay() && this.getHour() == compareStamp.getHour() && this.getMinute() == compareStamp.getMinute() && this.getSecond() == compareStamp.getSecond());
		}
		if (!this.isTime()) {
			return (this.getYear() == compareStamp.getYear() && this.getMonth() == compareStamp.getMonth() && this.getDay() == compareStamp.getDay());
		}
		if (!this.isDate()) {
			return (this.getHour() == compareStamp.getHour() && this.getMinute() == compareStamp.getMinute() && this.getSecond() == compareStamp.getSecond());
		}
		return false;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 * @see IWTimestamp#equals(IWTimestamp compareStamp)
	 */
	public boolean equals(Object object) {
		if (object instanceof IWTimestamp) {
			return equals((IWTimestamp) object);
		}
		else {
			return super.equals(object);
		}
	}

	/**
	 * Returns true if the current time setting is identical to the time setting of the
	 * <code>Time</code> object.
	 * @param time		The time to compare with the current time setting.
	 * @return boolean
	 */
	public boolean equals(Time time) {
		return toSQLTimeString().equals(time.toString());
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object is between to the given 
	 * <code>IWTimestamp</code> objects.
	 * @param stampBefore	The first IWTimestamp to compare with this object.
	 * @param stampAfter		The second IWTimestamp to compare with this object.
	 * @return boolean	True if this object is between the given objects, false otherwise.
	 */
	public boolean isBetween(IWTimestamp stampBefore, IWTimestamp stampAfter) {
		if (this.isLaterThanOrEquals(stampBefore) && this.isEarlierThan(stampAfter)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object only contains a date setting.
	 * @return boolean
	 */
	public boolean isDate() {
		if (this.isDate) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the <code>Date</code> object of the one who is earlier of the two given
	 * Date objects.
	 * @param date1		The first date to compare.
	 * @param date2		The second date to compare.
	 * @return Date
	 */
	public Date isEarlier(Date date1, Date date2) {
		return isEarlier(new IWTimestamp(date1),new IWTimestamp(date2)).getDate();
	}

	/**
	 * Returns the <code>IWTimestamp</code> object of the one who is earlier of the two
	 * given <code>IWTimestamp</code> objects.
	 * @param timestamp1		The first IWTimestamp to compare.
	 * @param timestamp2		The second IWTimestamp to compare.
	 * @return IWTimestamp
	 */
	public IWTimestamp isEarlier(IWTimestamp timestamp1, IWTimestamp timestamp2) {
		if ( isDate() ) {
			if (timestamp1.getYear() < timestamp2.getYear()) {
				return timestamp1;
			}
			if (timestamp1.getMonth() < timestamp2.getMonth()) {
				return timestamp1;
			}
			if (this.getDay() < timestamp2.getDay()) {
				return timestamp1;
			}
		}
		if ( isTime() ) {
			if (timestamp1.getHour() < timestamp2.getHour()) {
				return timestamp1;
			}
			if (timestamp1.getMinute() < timestamp2.getMinute()) {
				return timestamp1;
			}
			if (timestamp1.getSecond() < timestamp2.getSecond()) {
				return timestamp1;
			}
		}

		return timestamp2;
	}

	/**
	 * Returns the <code>Time</code> object of the one who is earlier of the two given
	 * Time objects.
	 * @param time1		The first date to compare.
	 * @param time2		The second date to compare.
	 * @return Time
	 */
	public Time isEarlier(Time time1, Time time2) {
		return isEarlier(new IWTimestamp(time1),new IWTimestamp(time2)).getTime();
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object is earlier than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isEarlierThan(IWTimestamp compareStamp) {
		if (!this.isTime()) {
			if (this.getYear() < compareStamp.getYear()) {
				return true;
			}
			if (this.getYear() > compareStamp.getYear()) {
				return false;
			}
			if (this.getMonth() < compareStamp.getMonth()) {
				return true;
			}
			if (this.getMonth() > compareStamp.getMonth()) {
				return false;
			}
			if (this.getDay() < compareStamp.getDay()) {
				return true;
			}
			if (this.getDay() > compareStamp.getDay()) {
				return false;
			}
		}

		if (!this.isDate()) {
			if (this.getHour() < compareStamp.getHour()) {
				return true;
			}
			if (this.getHour() > compareStamp.getHour()) {
				return false;
			}
			if (this.getMinute() < compareStamp.getMinute()) {
				return true;
			}
			if (this.getMinute() > compareStamp.getMinute()) {
				return false;
			}
			if (this.getSecond() < compareStamp.getSecond()) {
				return true;
			}
			if (this.getSecond() > compareStamp.getSecond()) {
				return false;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns true if this <code>IWTimestamp</code> object is earlier than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isDatePartEarlierThan(IWTimestamp compareStamp) {
		if (this.getYear() < compareStamp.getYear()) {
			return true;
		}
		if (this.getYear() > compareStamp.getYear()) {
			return false;
		}
		if (this.getMonth() < compareStamp.getMonth()) {
			return true;
		}
		if (this.getMonth() > compareStamp.getMonth()) {
			return false;
		}
		if (this.getDay() < compareStamp.getDay()) {
			return true;
		}
		if (this.getDay() > compareStamp.getDay()) {
			return false;
		}
		return false;
	}
	
	/**
	 * Returns true if this <code>IWTimestamp</code> object is earlier than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isTimePartEarlierThan(IWTimestamp compareStamp) {
		if (this.getHour() < compareStamp.getHour()) {
			return true;
		}
		if (this.getHour() > compareStamp.getHour()) {
			return false;
		}
		if (this.getMinute() < compareStamp.getMinute()) {
			return true;
		}
		if (this.getMinute() > compareStamp.getMinute()) {
			return false;
		}
		if (this.getSecond() < compareStamp.getSecond()) {
			return true;
		}
		if (this.getSecond() > compareStamp.getSecond()) {
			return false;
		}
		return false;
	}
	

	/**
	 * Returns true if this <code>IWTimestamp</code> object is equal to the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isEqualTo(IWTimestamp compareStamp) {
		if (isEarlierThan(compareStamp) || isLaterThan(compareStamp)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object contains a date and/or
	 * time setting.
	 * @return boolean
	 */
	public boolean isIWTimestamp() {
		if (isTime() || isDate()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the <code>IWTimestamp</code> object of the one who is later of the two
	 * given <code>IWTimestamp</code> objects.
	 * @param timestamp1		The first IWTimestamp to compare.
	 * @param timestamp2		The second IWTimestamp to compare.
	 * @return IWTimestamp
	 */
	public IWTimestamp isLater(IWTimestamp timestamp1, IWTimestamp timestamp2) {
		if (timestamp1.getYear() > timestamp2.getYear()) {
			return timestamp1;
		}
		if (timestamp1.getMonth() > timestamp2.getMonth()) {
			return timestamp1;
		}
		if (this.getDay() > timestamp2.getDay()) {
			return timestamp1;
		}
		if (timestamp1.getHour() > timestamp2.getHour()) {
			return timestamp1;
		}
		if (timestamp1.getMinute() > timestamp2.getMinute()) {
			return timestamp1;
		}
		if (timestamp1.getSecond() > timestamp2.getSecond()) {
			return timestamp1;
		}

		return timestamp2;
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object is later than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isLaterThan(IWTimestamp compareStamp) {
		if (!this.isTime()) {
			if (this.getYear() > compareStamp.getYear()) {
				return true;
			}
			if (this.getYear() < compareStamp.getYear()) {
				return false;
			}
			if (this.getMonth() > compareStamp.getMonth()) {
				return true;
			}
			if (this.getMonth() < compareStamp.getMonth()) {
				return false;
			}
			if (this.getDay() > compareStamp.getDay()) {
				return true;
			}
			if (this.getDay() < compareStamp.getDay()) {
				return false;
			}
		}

		if (!this.isDate()) {
			if (this.getHour() > compareStamp.getHour()) {
				return true;
			}
			if (this.getHour() < compareStamp.getHour()) {
				return false;
			}
			if (this.getMinute() > compareStamp.getMinute()) {
				return true;
			}
			if (this.getMinute() < compareStamp.getMinute()) {
				return false;
			}
			if (this.getSecond() > compareStamp.getSecond()) {
				return true;
			}
			if (this.getSecond() < compareStamp.getSecond()) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if this <code>IWTimestamp</code> object is later than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isDatePartLaterThan(IWTimestamp compareStamp) {
		if (this.getYear() > compareStamp.getYear()) {
			return true;
		}
		if (this.getYear() < compareStamp.getYear()) {
			return false;
		}
		if (this.getMonth() > compareStamp.getMonth()) {
			return true;
		}
		if (this.getMonth() < compareStamp.getMonth()) {
			return false;
		}
		if (this.getDay() > compareStamp.getDay()) {
			return true;
		}
		if (this.getDay() < compareStamp.getDay()) {
			return false;
		}
		return false;
	}
	
	/**
	 * Returns true if this <code>IWTimestamp</code> object is later than the given 
	 * <code>IWTimestamp</code> object.
	 * @param compareStamp		The IWTimestamp to compare with this object.
	 * @return IWTimestamp
	 */
	public boolean isTimePartLaterThan(IWTimestamp compareStamp) {
		if (this.getHour() > compareStamp.getHour()) {
			return true;
		}
		if (this.getHour() < compareStamp.getHour()) {
			return false;
		}
		if (this.getMinute() > compareStamp.getMinute()) {
			return true;
		}
		if (this.getMinute() < compareStamp.getMinute()) {
			return false;
		}
		if (this.getSecond() > compareStamp.getSecond()) {
			return true;
		}
		if (this.getSecond() < compareStamp.getSecond()) {
			return false;
		}
		return false;
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object is later than or equal to 
	 * the given <code>IWTimestamp</code> object.
	 * @param compareStamp
	 * @return boolean
	 */
	public boolean isLaterThanOrEquals(IWTimestamp compareStamp) {
		return (isLaterThan(compareStamp) || equals(compareStamp));
	}

	/**
	 * Returns true if this <code>IWTimestamp</code> object only contains a time setting.
	 * @return boolean
	 */
	public boolean isTime() {
		if (this.isTime) {
			return true;
		}
		return false;
	}

///////////////////////////////////////////////////
//      constructors      
///////////////////////////////////////////////////

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the current date and time settings.
	 */
	public IWTimestamp() {
		this.calendar = new GregorianCalendar();
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the current date and time settings and the locale provided.
	 */
	public IWTimestamp(Locale locale) {
		if (locale != null) {
			this.calendar = new GregorianCalendar(locale);
		}
		else {
			this.calendar = new GregorianCalendar();
		}
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date settings of the given <code>Date</code> object.  Time settings are
	 * disgarded.
	 */
	public IWTimestamp(java.util.Date date) {
		this();
		setAsDate();
		this.calendar.setTime(date);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date settings of the given <code>Date</code> object.  Time settings are
	 * disgarded.
	 */
	public IWTimestamp(Locale locale, java.util.Date date) {
		this(locale);
		setAsDate();
		this.calendar.setTime(date);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and time settings of the given <code>GregorianCalendar</code> object.
	 */
	public IWTimestamp(GregorianCalendar theCalendar) {
		this.calendar = theCalendar;
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date settings given in the constructor.  All time settings will be set to 0.
	 */
	public IWTimestamp(int day, int month, int year) {
		setAsDate();
		this.calendar = new GregorianCalendar(year, month - 1, day, 0, 0, 0);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and time settings given in the constructor.
	 */
	public IWTimestamp(int year, int month, int date, int hour, int minute, int second) {
		this.calendar = new GregorianCalendar(year, month - 1, date, hour, minute, second);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and/or time settings of the given <code>IWTimestamp</code> object.
	 */
	public IWTimestamp(IWTimestamp time) {
		this.calendar = (GregorianCalendar) time.getGregorianCalendar().clone();
		this.isDate=time.isDate;
		this.isTime=time.isTime;
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and time settings of the given <code>long</code> number.
	 */
	public IWTimestamp(long time) {
		this(new Timestamp(time));
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and/or time settings given in the constructor.<br>
	 * Available string formats are: 'yyyy-mm-dd hh:mm:ss' || 'yyyy-mm-dd' || 'hh:mm:ss || ddmmyy'
	 */
	public IWTimestamp(String SQLFormat) {
		this(null, SQLFormat);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and/or time settings given in the constructor.<br>
	 * Available string formats are: 'yyyy-mm-dd hh:mm:ss' || 'yyyy-mm-dd' || 'hh:mm:ss || ddmmyy'
	 */
	public IWTimestamp(Locale locale, String SQLFormat) {
		this(locale);
		
		if (SQLFormat.length() == 10) {
			//SQLFormat = SQLFormat + " " + getDateString(TIME_PATTERN);
			this.calendar.setTime(Date.valueOf(SQLFormat));
			setAsDate();
		}
		else if (SQLFormat.length() == 8) {
			//SQLFormat = getDateString(DATE_PATTERN) + " " + SQLFormat;
			this.calendar.setTime(Time.valueOf(SQLFormat));
			setAsTime();
		}
		else if(SQLFormat.length() == 6) {
			DateFormat dateFormatddmmyy = new SimpleDateFormat("ddMMyy");
			try {
				this.calendar.setTime(dateFormatddmmyy.parse(SQLFormat));
			}
			catch (ParseException e) {
				throw (IllegalArgumentException)new IllegalArgumentException().initCause(e);
			}
		}
		else {
			this.calendar.setTime(Timestamp.valueOf(SQLFormat));
		}
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the time settings of the given <code>Time</code> object.  Date settings are
	 * disgarded.
	 */
	public IWTimestamp(Time time) {
		this();
		setAsTime();
		this.calendar.setTime(time);
	}

	/**
	 * Construct a new <code>IWTimestamp</code> object that is initialized to
	 * the date and time settings of the given <code>Timestamp</code> object.
	 */
	public IWTimestamp(Timestamp time) {
		this();
		this.calendar.setTime(time);
	}

///////////////////////////////////////////////////
//      getters      
///////////////////////////////////////////////////

	/**
	 * Returns a <code>Date</code> object with the current date/time settings.
	 * @return Date
	 */
	public Date getDate() {
		return new Date(getTimestamp().getTime());
	}

	/**
	 * Returns a date string according to the supplied pattern.<br><br>
	 * Examples:<br>
	 * yyyy-MM-dd kk:mm:ss.S => 1970-10-06 03:00:00.0<br>
	 * "yyyy.MM.dd G 'at' HH:mm:ss z" => 2001.07.04 AD at 12:08:56 PDT<br>
	 * "EEE, MMM d, ''yy" => Wed, Jul 4, '01<br>
	 * "h:mm a" => 12:08 PM<br>
	 * "hh 'o''clock' a, zzzz" => 12 o'clock PM, Pacific Daylight Time<br>
	 * "K:mm a, z" => 0:08 PM, PDT<br>
	 * "yyyyy.MMMMM.dd GGG hh:mm aaa" => 02001.July.04 AD 12:08 PM <br>
	 * "EEE, d MMM yyyy HH:mm:ss Z" => Wed, 4 Jul 2001 12:08:56 -0700<br> 
	 * "yyMMddHHmmssZ" => 010704120856-0700<br><br>
	 * See <code>SimpleDateFormat</code> for more details on available pattern symbols.
	 * 
	 * @param pattern		The pattern to use to format the current date
	 * @return String
	 * @see SimpleDateFormat
	 */
	public String getDateString(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(this.calendar.getTime());
	}

	/**
	 * Returns a date string according to the supplied pattern.<br><br>
	 * Examples:<br>
	 * yyyy-MM-dd kk:mm:ss.S => 1970-10-06 03:00:00.0<br>
	 * "yyyy.MM.dd G 'at' HH:mm:ss z" => 2001.07.04 AD at 12:08:56 PDT<br>
	 * "EEE, MMM d, ''yy" => Wed, Jul 4, '01<br>
	 * "h:mm a" => 12:08 PM<br>
	 * "hh 'o''clock' a, zzzz" => 12 o'clock PM, Pacific Daylight Time<br>
	 * "K:mm a, z" => 0:08 PM, PDT<br>
	 * "yyyyy.MMMMM.dd GGG hh:mm aaa" => 02001.July.04 AD 12:08 PM <br>
	 * "EEE, d MMM yyyy HH:mm:ss Z" => Wed, 4 Jul 2001 12:08:56 -0700<br> 
	 * "yyMMddHHmmssZ" => 010704120856-0700<br><br>
	 * See <code>SimpleDateFormat</code> for more details on available pattern symbols.
	 * 
	 * @param pattern		The pattern to use to format the current date
	 * @param locale		The locale to use to format the current date
	 * @return String
	 * @see SimpleDateFormat
	 */
	public String getDateString(String pattern, Locale locale) {
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		return format.format(this.calendar.getTime());
	}

	/**
	 * Returns the day (1-31) from the default date setting.
	 * @return int
	 */
	public int getDay() {
		return this.calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the day of the week (Sunday = 1) from the default date setting.
	 * @return int
	 */
	public int getDayOfWeek() {
		return this.calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Returns the day of the year (1-366) from the default date setting.
	 * @return int
	 */
	public int getDayOfYear() {
		return this.calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Returns a <code>GregorianCalendar</code> object with the current date/time settings.
	 * @return GregorianCalendar
	 */
	public GregorianCalendar getGregorianCalendar() {
		return this.calendar;
	}
	
	/**
	 * Returns a <code>Calendar</code> object with the current date/time settings.
	 * @return Calendar
	 */
	public Calendar getCalendar() {
		return this.calendar;
	}

	/**
	 * Returns the minute (0-23) from the default time setting.
	 * @return int
	 */
	public int getHour() {
		return this.calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * @deprecated
	 * Get a date string with the icalandic data format: DAY MONTH (YEAR)
	 * @return IWTimestamp#getDateString(String pattern)
	 */
	public String getISLDate(String spacer, boolean withYear) {
		String pattern = DAY + spacer + MONTH;
		if (withYear) {
			pattern = pattern + spacer + YEAR;
		}
		return getDateString(pattern);
	}

	/**
	 * @deprecated
	 * Get a date string for the current locale. Will be deprecated soon.
	 * @param iwc		The <code>IWContext</code> to get the current locale from.
	 * @return String
	 */
	public String getLocaleDate(IWContext iwc) {
		Locale currentLocale = iwc.getCurrentLocale();
		return getLocaleDate(currentLocale);
	}

	/**
 	 * Get a date string for the locale.
	 * @param locale		The locale to use to format the current date
	 * @return String
	 */
	public String getLocaleDate(Locale locale) {
		return getLocaleDate(locale, LONG);
	}

	/**
	 * Get a date string for the locale
	 * @param locale		The locale to use to format the current date
	 * @param format		The format of the date string
	 * @return String
	 */
	public String getLocaleDate(Locale locale, int format) {
		DateFormat dateFormat = DateFormat.getDateInstance(format, locale);
		return dateFormat.format(getTime());
	}

	/**
	 * Get a date and time string for the locale.
	 * @param locale		The locale to use to format the current date and time
	 * @return String
	 */
	public String getLocaleDateAndTime(Locale locale) {
		return getLocaleDateAndTime(locale, LONG, LONG);
	}

	/**
	 * Get a date and time string for the locale
	 * @param locale				The locale to use to format the current date and time
	 * @param dateFormat		The format of the date string
	 * @param timeFormat		The format of the time string
	 * @return String
	 */
	public String getLocaleDateAndTime(Locale locale, int dateFormat, int timeFormat) {
		DateFormat format = DateFormat.getDateTimeInstance(dateFormat, timeFormat, locale);
		return format.format(getTime());
	}

	/**
	 * Get a time string for the locale.
	 * @param locale		The locale to use to format the current time
	 * @return String
	 */
	public String getLocaleTime(Locale locale) {
		return getLocaleTime(locale, LONG);
	}

	/**
	 * Get a time string for the locale
	 * @param locale				The locale to use to format the current time
	 * @param format				The format of the time string
	 * @return String
	 */
	public String getLocaleTime(Locale locale, int format) {
		DateFormat timeFormat = DateFormat.getTimeInstance(format, locale);
		return timeFormat.format(getTime());
	}

	/**
	 * Returns the millisecond from the default time setting.
	 * @return int
	 */
	public int getMilliSecond() {
		return this.calendar.get(Calendar.MILLISECOND);
	}

	/**
	 * Returns the minute (0-59) from the default time setting.
	 * @return int
	 */
	public int getMinute() {
		return this.calendar.get(Calendar.MINUTE);
	}

	/**
	 * Returns the month (1-12) from the default date setting.
	 * @return int
	 */
	public int getMonth() {
		return this.calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * Returns an <code>IWTimestamp</code> with the next day from the current date setting.
	 * @return IWTimestamp
	 */
	public IWTimestamp getNextDay() {
		GregorianCalendar myCalendar = (GregorianCalendar) this.calendar.clone();
		myCalendar.add(Calendar.DATE, 1);
		return new IWTimestamp(myCalendar);
	}

	/**
	 * Returns the second (0-59) from the default time setting.
	 * @return int
	 */
	public int getSecond() {
		return this.calendar.get(Calendar.SECOND);
	}

	/**
	 * @deprecated
	 * @see IWTimestamp#getDate()
	 */
	public Date getSQLDate() {
		return getDate();
	}

	/**
	 * Returns a <code>Time</code> object with the current date/time settings.
	 * @return Time
	 */
	public Time getTime() {
		return new Time(getTimestamp().getTime());
	}

	/**
	 * Returns a <code>TimeStamp</code> object with the current date/time settings.
	 * @return TimeStamp
	 */
	public Timestamp getTimestamp() {
		String pattern = DATE_PATTERN + " " + TIME_PATTERN;
		return Timestamp.valueOf(getDateString(pattern));
	}

	/**
	 * Returns the week of the year from the current date setting (1-52/53).
	 * @return int
	 */
	public int getWeekOfYear() {
		return this.calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Returns the year (????) from the default date setting.
	 * @return int
	 */
	public int getYear() {
		return this.calendar.get(Calendar.YEAR);
	}

///////////////////////////////////////////////////
//      setters      
///////////////////////////////////////////////////

	/**
	 * Sets the <code>IWTimestamp</code> to use only the date settings.
	 */
	public void setAsDate() {
		this.isDate = true;
		this.isTime = false;
	}

	/**
	 * Sets the <code>IWTimestamp</code> to use only the time settings.
	 */
	public void setAsTime() {
		this.isDate = false;
		this.isTime = true;
	}

	/**
	 * Sets the date of the timestamp according to the inStr. The format is YYYYMMDD
	 * @param inStr String representation of a date
	 */
	public void setDate(String inStr) throws DateFormatException
	{
		try{
			setYear(Integer.parseInt(inStr.substring(0, 4)));
			setMonth(Integer.parseInt(inStr.substring(4, 6)));
			setDay(Integer.parseInt(inStr.substring(6, 8)));
		} catch (Exception e){
			System.out.println("Could not parse the following date: "+inStr+". Expected format is YYYYMMDD.");
			throw new DateFormatException();
		}
	}

	/**
	 * Sets the hour of the date setting.
	 * @param day		The day to set
	 */
	public void setDay(int day) {
		this.calendar.set(Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * Sets the hour of the time setting.
	 * @param hour		The hour to set
	 */
	public void setHour(int hour) {
		this.calendar.set(Calendar.HOUR_OF_DAY, hour);
	}

	/**
	 * Sets the millisecond of the time setting.
	 * @param millisecond		The millisecond to set
	 */
	public void setMilliSecond(int millisecond) {
		this.calendar.set(Calendar.MILLISECOND, millisecond);
	}

	/**
	 * Sets the minute of the time setting.
	 * @param minute		The minute to set
	 */
	public void setMinute(int minute) {
		this.calendar.set(Calendar.MINUTE, minute);
	}

	/**
	 * Sets the month of the date setting.
	 * @param month		The month to set in the range [1-12]
	 */
	public void setMonth(int month) {
		this.calendar.set(Calendar.MONTH, month - 1);
	}

	/**
	 * Sets the second of the time setting.
	 * @param second		The second to set
	 */
	public void setSecond(int second) {
		this.calendar.set(Calendar.SECOND, second);
	}

	/**
		 * Sets the time of the time setting. If the IWTimestamp is a date setting it will not longer be set.
		 * @param hour		The hour to set
		 * @param minute	The minute to set
		 * @param second The second to set
		*/
	public void setTime(int hour,int minute,int second) {
		setTime(hour,minute,second,0);
	}

	/**
		 * Sets the time of the time setting. If the IWTimestamp is a date setting it will not longer be set.
		 * @param hour		The hour to set
		 * @param minute	The minute to set
		 * @param second The second to set
		 * @param millisecond	The millisecond to set
		 */
	public void setTime(int hour,int minute,int second,int millisecond) {
		if(isDate()) {
			this.isDate = false;
		}
		this.calendar.set(Calendar.HOUR_OF_DAY, hour);
		this.calendar.set(Calendar.MINUTE, minute);
		this.calendar.set(Calendar.SECOND, second);
		this.calendar.set(Calendar.MILLISECOND, millisecond);
	}

	/**
	 * Sets the year of the date setting.
	 * @param year		The year to set
	 */
	public void setYear(int year) {
		this.calendar.set(Calendar.YEAR, year);
	}

	
	public void setMinimalDaysInFirstWeek(int days) {
		this.calendar.setMinimalDaysInFirstWeek(days);
	}
///////////////////////////////////////////////////
//      static methods      
///////////////////////////////////////////////////

	/**
	 * Returns the days between the given <code>IWTimestamp</code> objects.  If
	 * the first one is later than the second a negative value is returned.
	 * @param before		The first IWTimestamp to use.
	 * @param after		The second IWTimestamp to use.
	 * @return int
	 */
	public static int getDaysBetween(IWTimestamp before, IWTimestamp after) {
		return (int) (getMilliSecondsBetween(before, after) / 86400000);
	}

	/**
	 * Returns the hours between the given <code>IWTimestamp</code> objects.  If
	 * the first one is later than the second a negative value is returned.
	 * @param before		The first IWTimestamp to use.
	 * @param after		The second IWTimestamp to use.
	 * @return int
	 */
	public static int getHoursBetween(IWTimestamp before, IWTimestamp after) {
		return (int) (getMilliSecondsBetween(before, after) / 3600000);
	}
	
	/**
	 * Returns the milliseconds between the given <code>IWTimestamp</code> objects.  If
	 * the first one is later than the second a negative value is returned.
	 * @param before		The first IWTimestamp to use.
	 * @param after		The second IWTimestamp to use.
	 * @return int
	 */
	public static long getMilliSecondsBetween(IWTimestamp before, IWTimestamp after) {
		if (before.isTime() || after.isTime()) {
			before.setDay(1);
			before.setMonth(2);
			before.setYear(1);
			after.setDay(1);
			after.setMonth(2);
			after.setYear(1);
			//System.out.println(before.isTime()+"/"+after.isTime());
		}
		
		long lBefore = before.getGregorianCalendar().getTime().getTime();
		long lAfter = after.getGregorianCalendar().getTime().getTime();

		long diff = lAfter - lBefore;

		return diff;
	}

	/**
	 * Returns the minutes between the given <code>IWTimestamp</code> objects.  If
	 * the first one is later than the second a negative value is returned.
	 * @param before		The first IWTimestamp to use.
	 * @param after		The second IWTimestamp to use.
	 * @return int
	 */
	public static int getMinutesBetween(IWTimestamp before, IWTimestamp after) {
		return (int) (getMilliSecondsBetween(before, after) / 60000);
	}

	/**
	 * Returns an <code>Timestamp</code> object with date and time settings set to
	 * match the current date and time.
	 * @return IWTimestamp
	 */
	public static Timestamp getTimestampRightNow() {
		IWTimestamp stamp = RightNow();
		return stamp.getTimestamp();
	}

	/**
	 * Returns an <code>IWTimestamp</code> object with date and time settings set to
	 * match the current date and time.
	 * @return IWTimestamp
	 */
	public static IWTimestamp RightNow() {
		GregorianCalendar myCalendar = new GregorianCalendar();
		return new IWTimestamp(myCalendar);
	}

///////////////////////////////////////////////////
//      toString      
///////////////////////////////////////////////////

	/**
	 * Returns an Oracle SQL string for the given date and time setting. The format is: 
	 * "TO_DATE('1970 10 06 03:00','YYYY MM DD HH:MI')"
	 * @return String
	 */
	public String toOracleString() {
		String pattern = YEAR + " " + MONTH + " " + DAY + " " + HOUR + ":" + MINUTE;
		StringBuffer toReturn = new StringBuffer();
		toReturn.append(" TO_DATE('");
		toReturn.append(getDateString(pattern));
		toReturn.append("','YYYY MM DD HH24:MI') ");
		return toReturn.toString();
	}

	/**
	 * Returns an SQL string for the given date setting.
	 * @return String
	 */
	public String toSQLDateString() {
		return getDateString(DATE_PATTERN);
	}

	/**
	 * Returns an SQL string for the given settings.  If the IWTimestamp is a date setting
	 * it will return a date string, if it's a time setting, a time string.
	 * @return String
	 */
	public String toSQLString(boolean cutOfMilliseconds) {
		if (isDate()) {
			return toSQLDateString();
		}
		else if (isTime()) {
			return toSQLTimeString();
		}
		else{
			String theTimestampString = getTimestamp().toString();
			if(cutOfMilliseconds){
				return theTimestampString.substring(0,19);
			}
			else{
				return theTimestampString;
			}
		}
	}
	
	/**
	 * Returns an SQL string for the given settings.  If the IWTimestamp is a date setting
	 * it will return a date string, if it's a time setting, a time string.
	 * @return String
	 */
	public String toSQLString() {
		return toSQLString(CUT_MILLISECONDS_OFF_IN_TOSTRING);
	}

	/**
	 * Returns an SQL string for the given time setting.
	 * @return String
	 */
	public String toSQLTimeString() {
		return getDateString(TIME_PATTERN);
	}

	/**
	 * Returns an SQL string for the given date and time settings.
	 * @return String
	 * @see java.lang.Object#toString()
	 */
	public String toString(boolean cutOfMilliseconds) {
		String theString = this.getTimestamp().toString();
		if(cutOfMilliseconds){
			return theString.substring(0,19);
		}
		else{
			return theString;
		}
	}
	
	/**
	 * Returns an SQL string for the given date and time settings.
	 * @return String
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString(CUT_MILLISECONDS_OFF_IN_TOSTRING);
	}	
	public Object clone(){
		try {
			IWTimestamp obj = (IWTimestamp)super.clone();
			obj.calendar = (GregorianCalendar) getGregorianCalendar().clone();
			obj.isDate=this.isDate;
			obj.isTime=this.isTime;
			return obj;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
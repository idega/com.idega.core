package com.idega.util;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.ibm.icu.impl.CalendarAstronomer;
import com.ibm.icu.util.Holiday;

/**
 * <code>IWCalendar</code> is a class that can give localized names of days, months
 * and holidays for a specific locale.  It also can give moonphases, times of sun/moon
 * rises and sets.  It can also give certain information about the specific calendar,
 * i.e. length of months, week numbers and day numbers.<br><br>
 * Uses the ICU4J classes from IBM (@link http://www-124.ibm.com/icu4j)
 *
 * @author idega 2002 - idega team
 * @version 1.1
 */
public class IWCalendar {

	/**
	  * Construct a new <code>IWCalendar</code> object that is initialized to
	  * the default calendar and locale.
	  */
	public IWCalendar() {
		this(LocaleUtil.getIcelandicLocale(), new GregorianCalendar());
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given locale and default calendar.
	 */
	public IWCalendar(Locale locale) {
		this(locale, new GregorianCalendar());
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given calendar and default locale.
	 */
	public IWCalendar(GregorianCalendar calendar) {
		this(LocaleUtil.getIcelandicLocale(), calendar);
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given calendar from the constructed IWTimestamp and default locale.
	 */
	public IWCalendar(IWTimestamp timestamp) {
		this(LocaleUtil.getIcelandicLocale(), timestamp.getGregorianCalendar());
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given date and default locale.
	 */
	public IWCalendar(Date date) {
		_calendar = new GregorianCalendar();
		_calendar.setTime(date);
		_locale = LocaleUtil.getIcelandicLocale();
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given locale and calendar from the constructed IWTimestamp.
	 */
	public IWCalendar(Locale locale, IWTimestamp timestamp) {
		this(locale, timestamp.getGregorianCalendar());
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given calendar and locale.
	 */
	public IWCalendar(Locale locale, GregorianCalendar calendar) {
		_calendar = calendar;
		_locale = locale;
	}

	/**
	 * Construct a new <code>IWCalendar</code> object that is initialized to
	 * the given date and locale.
	 */
	public IWCalendar(Locale locale, Date date) {
		_calendar = new GregorianCalendar();
		_calendar.setTime(date);
		_locale = locale;
	}

	/**
	 * A type setting for use with getDayName, getMonthName and getLocaleDate.<br>
	 * getLocaleDate: SHORT is completely numeric, such as 12.13.52 or 3:30pm.<br>
	 * getDayName/getMonthName: SHORT is abbreviated name, such as Jan or Dec.
	 * @see IWCalendar#getDayName(int day)
	 * @see IWCalendar#getMonthName(int month)
	 * @see IWCalendar#getLocaleDate(Locale locale, int format, int year, int month, int day)
	 */
	public static final int SHORT = DateFormat.SHORT;

	/**
	 * A type setting for use with getLocaleDate.<br>
	 * MEDIUM is longer, such as Jan 12, 1952
	 * @see IWCalendar#getLocaleDate(Locale locale, int format, int year, int month, int day)
	 */
	public static final int MEDIUM = DateFormat.MEDIUM;

	/**
	 * A type setting for use with getDayName, getMonthName and getLocaleDate.<br>
	 * getLocaleDate: LONG is longer, such as January 12, 1952 or 3:30:32pm<br>
	 * getDayName/getMonthName: LONG is full name, such as January or December.
	 * @see IWCalendar#getDayName(int day)
	 * @see IWCalendar#getMonthName(int month)
	 * @see IWCalendar#getLocaleDate(Locale locale, int format, int year, int month, int day)
	 */
	public static final int LONG = DateFormat.LONG;

	/**
	 * A type setting for use with getLocaleDate.<br>
	 * FULL is pretty completely specified, such as Tuesday, April 12, 1952 AD or 3:30:42pm PST.
	 * @see IWCalendar#getLocaleDate(Locale locale, int format, int year, int month, int day)
	 */
	public static final int FULL = DateFormat.FULL;

	/**
	 * The value of the moonphase when the moon is new.
	 * @see IWCalendar#getMoonPhase()
	 */
	public static final double NEW_MOON = 0.00;

	/**
	 * The value of the moonphase when the moon is in its first quarter.
	 * @see IWCalendar#getMoonPhase()
	 */
	public static final double FIRST_QUARTER = 0.25;

	/**
	 * The value of the moonphase when the moon is full.
	 * @see IWCalendar#getMoonPhase()
	 */
	public static final double FULL_MOON = 0.50;

	/**
	 * The value of the moonphase when the moon is in its last quarter.
	 * @see IWCalendar#getMoonPhase()
	 */
	public static final double LAST_QUARTER = 0.75;

	private Locale _locale;
	private GregorianCalendar _calendar;

	/**
	 * Returns the day for the default date setting.
	 * @return int
	 */
	public int getDay() {
		return _calendar.get(_calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the month for the default date setting (January = 1).
	 * @return int
	 */
	public int getMonth() {
		return _calendar.get(_calendar.MONTH) + 1;
	}

	/**
	 * Returns the year for the default date setting.
	 * @return int
	 */
	public int getYear() {
		return _calendar.get(_calendar.YEAR);
	}

	/**
	 * Returns the day of the week (Sunday = 1). Uses the default date settings.
	 * @return int
	 */
	public int getDayOfWeek() {
		return _calendar.get(_calendar.DAY_OF_WEEK);
	}

	/**
	 * Returns the day of the week for the specified date (Sunday = 1).
	 * @param year					The year to use.
	 * @param month				The month to use (January = 1).
	 * @param day					The day to use.
	 * @return int
	 */
	public int getDayOfWeek(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	/**
	 * Returns the week number (1-52).  Uses the default date settings.
	 * @return int
	 */
	public int getWeekOfYear() {
		return getWeekOfYear(getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the week number (1-52) of the given date.
	 * @param year							The year to use.
	 * @param month						The month to use (January = 1).
	 * @param day							The day to use.
	 * @return int
	 */
	public int getWeekOfYear(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(day, month - 1, day);
		return calendar.get(calendar.WEEK_OF_YEAR);
	}

	/**
	 * Returns the length of the given month of the given year.
	 * @param month						The month to use (January = 1).
	 * @param year							The year to use.
	 * @return int
	 */
	public int getLengthOfMonth(int month, int year) {
		int daysInMonth = 31;

		switch (month) {
			case 0 :
				daysInMonth = 31;
				break;
			case 1 :
				daysInMonth = 31;
				break;
			case 2 :
				if (_calendar.isLeapYear(year)) {
					daysInMonth = 29;
				}
				else {
					daysInMonth = 28;
				}
				break;
			case 3 :
				daysInMonth = 31;
				break;
			case 4 :
				daysInMonth = 30;
				break;
			case 5 :
				daysInMonth = 31;
				break;
			case 6 :
				daysInMonth = 30;
				break;
			case 7 :
				daysInMonth = 31;
				break;
			case 8 :
				daysInMonth = 31;
				break;
			case 9 :
				daysInMonth = 30;
				break;
			case 10 :
				daysInMonth = 31;
				break;
			case 11 :
				daysInMonth = 30;
				break;
			case 12 :
				daysInMonth = 31;
				break;
			case 13 :
				daysInMonth = 31;
				break;

		}
		return daysInMonth;
	}

	/**
	 * Returns the name of the specified month. Uses default locale settings and uses 
	 * LONG as default type.
	 * @param month			The month (January = 1).
	 * @return String
	 */
	public String getMonthName(int month) {
		return getMonthName(month, _locale, LONG);
	}

	/**
	 * Returns the name of the specified month. Uses default locale settings.
	 * @param month			The month (January = 1).
	 * @param type				The type of string to return.  LONG returns the full
	 * 												localized name whereas SHORT returns abbreviated names
	 * 												(three letters).  All other values return LONG.
	 * @return String
	 */
	public String getMonthName(int month, int type) {
		return getMonthName(month, _locale, type);
	}

	/**
	 * Returns the name of the specified month for the given locale.
	 * @param month			The month (January = 1).
	 * @param locale			The locale to use.
	 * @param type				The type of string to return.  LONG returns the full
	 * 										localized name whereas SHORT returns abbreviated names
	 * 										(three letters).  All other values return LONG.
	 * @return String
	 */
	public String getMonthName(int month, Locale locale, int type) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] months = null;
		switch (type) {
			case SHORT :
				months = dfs.getShortMonths();
				break;
			case LONG :
				months = dfs.getMonths();
				break;
			default :
				months = dfs.getMonths();
				break;
		}

		if (months != null) {
			returner = months[month - 1];
		}

		return returner;
	}

	/**
	 * Returns the name of the specified day. Uses default locale settings and uses LONG
	 * as default type.
	 * @param day				The day of the week (Sunday = 1).
	 * @return String
	 */
	public String getDayName(int day) {
		return getDayName(day, _locale, LONG);
	}

	/**
	 * Returns the name of the specified day. Uses default locale settings.
	 * @param day				The day of the week (Sunday = 1).
	 * @param type				The type of string to return.  LONG (2) returns the full
	 * 										localized name whereas SHORT (1) returns abbreviated names
	 * 										(three letters).  All other values return LONG.
	 * @return String
	 */
	public String getDayName(int day, int type) {
		return getDayName(day, _locale, type);
	}

	/**
	 * Returns the name of the specified day for the given locale.
	 * @param day				The day of the week (Sunday = 1).
	 * @param locale			The locale to use.
	 * @param type				The type of string to return.  LONG (2) returns the full
	 * 										localized name whereas SHORT (1) returns abbreviated names
	 * 										(three letters).  All other values return LONG.
	 * @return String
	 */
	public String getDayName(int day, Locale locale, int type) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] days = dfs.getWeekdays();
		switch (type) {
			case SHORT :
				days = dfs.getShortWeekdays();
				break;
			case LONG :
				days = dfs.getWeekdays();
				break;
			default :
				days = dfs.getWeekdays();
				break;
		}

		if (days != null) {
			returner = days[day];
		}

		return returner;
	}

	/**
	 * Returns the default date as a localized date string with the default locale.
	 * Uses the default format (LONG).
	 * @return String
	 */
	public String getLocaleDate() {
		return getLocaleDate(_locale, DateFormat.LONG, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the default date as a localized date string with the specified locale.
	 * Uses the default format (LONG).
	 * @param locale		The locale to use.
	 * @return String
	 */
	public String getLocaleDate(Locale locale) {
		return getLocaleDate(locale, DateFormat.LONG, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the default date as a localized date string with the default locale and
	 * specified format.
	 * @param format		The format to use (SHORT/MEDIUM/LONG/FULL).
	 * @return String
	 */
	public String getLocaleDate(int format) {
		return getLocaleDate(_locale, format, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the default date as a localized date string with the specified locale and
	 * format.
	 * @param locale		The locale to use.
	 * @param format		The format to use (SHORT/MEDIUM/LONG/FULL).
	 * @return String
	 */
	public String getLocaleDate(Locale locale, int format) {
		return getLocaleDate(locale, format, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the specified date as a localized date string with the default locale and
	 * specified format.
	 * @param format		The format to use (SHORT/MEDIUM/LONG/FULL).
	 * @param year			The year to use.
	 * @param month		The month to use.
	 * @param day			The day to use.
	 * @return String
	 */
	public String getLocaleDate(int format, int year, int month, int day) {
		return getLocaleDate(_locale, format, year, month, day);
	}

	/**
	 * Returns the specified date as a localized date string with the given locale and
	 * format.
	 * @param locale		The locale to use.
	 * @param format		The format to use (SHORT/MEDIUM/LONG/FULL).
	 * @param year			The year to use.
	 * @param month		The month to use.
	 * @param day			The day to use.
	 * @return String
	 */
	public String getLocaleDate(Locale locale, int format, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		DateFormat dateFormat = DateFormat.getDateInstance(format, locale);
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * Checks whether a specific date is a locale holiday. Uses default locale and date 
	 * settings.
	 * @return boolean		Returns true if the default Locale has a holiday specified	
	 * 										for the given date.
	 */
	public boolean isHoliday() {
		return isHoliday(_locale, getYear(), getMonth(), getDay());
	}

	/**
	 * Checks whether the specific date is a locale holiday. Uses default locale settings.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return boolean		Returns true if the default Locale has a holiday specified
	 * 										for the given date.
	 */
	public boolean isHoliday(int year, int month, int day) {
		return isHoliday(_locale, year, month, day);
	}

	/**
	 * Checks whether the specific date is a locale holiday for the the given locale.
	 * @param locale 		The locale to use.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return boolean		Returns true if the given Locale has a holiday specified
	 * 										for the given date.
	 */
	public boolean isHoliday(Locale locale, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);

		Holiday[] holidays = Holiday.getHolidays(locale);

		for (int a = 0; a < holidays.length; a++) {
			Holiday holiday = holidays[a];
			if (holiday.isOn(calendar.getTime()))
				return true;
		}
		return false;
	}

	/**
	 * Returns the holiday for a date.  Uses default locale and date settings.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return String		Returns null if the current date has no holiday for the 
	 * 										given Locale.
	 */
	public Holiday getHoliday() {
		return getHoliday(_locale, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the holiday for the given date.  Uses default locale settings.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return String		Returns null if the current date has no holiday for the 
	 * 										given Locale.
	 */
	public Holiday getHoliday(int year, int month, int day) {
		return getHoliday(_locale, year, month, day);
	}

	/**
	 * Returns the locale holiday for the given date.
	 * @param locale			The locale to use.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return String		Returns null if the current date has no holiday for the 
	 * 										given Locale.
	 */
	public Holiday getHoliday(Locale locale, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);

		Holiday[] holidays = Holiday.getHolidays(locale);

		for (int a = 0; a < holidays.length; a++) {
			Holiday holiday = holidays[a];
			if (holiday.isOn(calendar.getTime())) {
				return holiday;
			}
		}
		return null;
	}

	/**
	 * Returns whether there is a full moon on the given date. Uses default locale and 
	 * date settings.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return boolean
	 */
	public boolean isFullMoon() {
		return isFullMoon(_locale, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns whether there is a full moon on the given date. Uses default locale settings.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return boolean
	 */
	public boolean isFullMoon(int year, int month, int day) {
		return isFullMoon(_locale, year, month, day);
	}

	/**
	 * Returns whether there is a full moon on the given date.
	 * @param locale			The locale to use.
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return boolean
	 */
	public boolean isFullMoon(Locale locale, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(locale);
		calendar.set(year, month - 1, day);

		CalendarAstronomer moonCalendar = new CalendarAstronomer(calendar.getTime());
		if (moonCalendar.getMoonPhase() == FULL_MOON)
			return true;
		return false;
	}

	/**
	 * Returns a Date object with the specified date for the next full moon.  Uses the 
	 * default locale and date settings.
	 * @return Date
	 */
	public Date getNextFullMoon() {
		return getNextFullMoon(_locale, getYear(), getMonth(), getDay());

	}

	/**
	 * Returns a Date object with the specified date for the next full moon from the 
	 * given date.  Uses the default locale setting.
	 * @param year					The year to use.
	 * @param month				The month to use.
	 * @param day					The day to use.
	 * @return Date
	 */
	public Date getNextFullMoon(int year, int month, int day) {
		return getNextFullMoon(_locale, year, month, day);
	}

	/**
	 * Returns a Date object with the specified date for the next full moon from the 
	 * given date.
	 * @param locale				The locale to use.
	 * @param year					The year to use.
	 * @param month				The month to use.
	 * @param day					The day to use.
	 * @return Date
	 */
	public Date getNextFullMoon(Locale locale, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(locale);
		calendar.set(year, month - 1, day);

		CalendarAstronomer moonCalendar = new CalendarAstronomer(calendar.getTime());
		return new Date(moonCalendar.getMoonTime(FULL_MOON, true));
	}

	/**
	 * Returns the current phase of the moon.  Uses default locale and date settings.
	 * @return double
	 * @see IWCalendar#getMoonPhase(Locale locale, int year, int month, int day)
	 */
	public double getMoonPhase() {
		return getMoonPhase(_locale, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns the current phase of the moon according to the specified date settings.
	 * Uses default locale settings.
	 * @param year			The year to use.
	 * @param month		The month to use (1-based).
	 * @param day			The day to use.
	 * @return double
	 * @see IWCalendar#getMoonPhase(Locale locale, int year, int month, int day)
	 */
	public double getMoonPhase(int year, int month, int day) {
		return getMoonPhase(_locale, year, month, day);
	}

	/**
	 * Returns the current phase of the moon according to the specified date and locale
	 * settings.
	 * @param locale			The locale to use.
	 * @param year				The year to use.
	 * @param month			The month to use (1-based).
	 * @param day				The day to use.
	 * @return double		The returned phase is a <code>double</code> in the range
	 * 										<code>0 <= phase < 1</code>, interpreted as follows:
	 * 										<ul>
	 * 										<li>0.00: New moon
	 * 										<li>0.25: First quarter
	 * 										<li>0.50: Full moon
	 * 										<li>0.75: Last quarter
	 * 										</ul>
	 */
	public double getMoonPhase(Locale locale, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(locale);
		calendar.set(year, month - 1, day);

		CalendarAstronomer moonCalendar = new CalendarAstronomer(calendar.getTime());
		return moonCalendar.getMoonPhase();
	}

	/**
	 * Returns a Time object with h/m/s on the moonrise/moonset of the specified date. 
	 * Uses the default locale and date settings.
	 * @param rise				Set whether to return sunrise or sunset (true = sunrise)
	 * @return Time
	 */
	public Time getMoonRiseSet(boolean rise) {
		return getMoonRiseSet(_locale, rise, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns a Time object with h/m/s on the moonrise/moonset of the specified date.
	 * Uses the default locale setting.
	 * @param rise				Set whether to return sunrise or sunset (true = sunrise)
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return Time
	 */
	public Time getMoonRiseSet(boolean rise, int year, int month, int day) {
		return getMoonRiseSet(_locale, rise, year, month, day);
	}

	/**
	 * Returns a Time object with h/m/s on the moonrise/moonset of the specified date.
	 * @param locale			The locale to use.
	 * @param rise				Set whether to return sunrise or sunset (true = moonrise)
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return Time
	 */
	public Time getMoonRiseSet(Locale locale, boolean rise, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(locale);
		calendar.set(year, month - 1, day);

		CalendarAstronomer moonCalendar = new CalendarAstronomer(calendar.getTime());
		return new Time(moonCalendar.getMoonRiseSet(rise));
	}

	/**
	 * Returns a Time object with h/m/s on the sunrise/sunset of the specified date. 
	 * Uses the default locale and date settings.
	 * @param rise				Set whether to return sunrise or sunset (true = sunrise)
	 * @return Time
	 */
	public Time getSunRiseSet(boolean rise) {
		return getSunRiseSet(_locale, rise, getYear(), getMonth(), getDay());
	}

	/**
	 * Returns a Time object with h/m/s on the sunrise/sunset of the specified date. 
	 * Uses the default locale setting.
	 * @param rise				Set whether to return sunrise or sunset (true = sunrise)
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return Time
	 */
	public Time getSunRiseSet(boolean rise, int year, int month, int day) {
		return getSunRiseSet(_locale, rise, year, month, day);
	}

	/**
	 * Returns a Time object with h/m/s on the sunrise/sunset of the specified date.
	 * @param locale	The locale to use.
	 * @param rise				Set whether to return sunrise or sunset (true = sunrise)
	 * @param year				The year to use.
	 * @param month			The month to use.
	 * @param day				The day to use.
	 * @return Time
	 */
	public Time getSunRiseSet(Locale locale, boolean rise, int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(locale);
		calendar.set(year, month - 1, day, 12, 0);

		CalendarAstronomer moonCalendar = new CalendarAstronomer(calendar.getTime());
		return new Time(moonCalendar.getSunRiseSet(rise));
	}

	/**
	 * Returns the current date settings as Date
	 * @return Date
	 */
	public Date toDate() {
		return _calendar.getTime();
	}

	/**
	 * Returns the calendar.
	 * @return GregorianCalendar
	 */
	public GregorianCalendar getCalendar() {
		return _calendar;
	}

	/**
	 * Returns the locale.
	 * @return Locale
	 */
	public Locale getLocale() {
		return _locale;
	}

	/**
	 * Sets the calendar.
	 * @param calendar The calendar to set
	 */
	public void setCalendar(GregorianCalendar calendar) {
		_calendar = calendar;
	}

	/**
	 * Sets the calendar from the construced IWTimestamp.
	 * @param timestamp The timestamp to get the calendar from
	 */
	public void setCalendar(IWTimestamp timestamp) {
		_calendar = timestamp.getGregorianCalendar();
	}

	/**
	 * Sets the locale.
	 * @param locale The locale to set
	 */
	public void setLocale(Locale locale) {
		_locale = locale;
	}
	
	/**
	 * Sets the day of the current date.
	 * @param day The day to set
	 */
	public void setDay(int day) {
		_calendar.set(_calendar.DATE, day);	
	}

	/**
	 * Sets the month of the current date (January = 1).
	 * @param month The month to set
	 */
	public void setMonth(int month) {
		_calendar.set(_calendar.MONTH, month - 1);	
	}

	/**
	 * Sets the year of the current date.
	 * @param year The year to set
	 */
	public void setYear(int year) {
		_calendar.set(_calendar.YEAR, year);	
	}

	/**
	 * Sets the current date (January = 1).
	 * @param year The day to set
	 * @param month The month to set
	 * @param day The day to set
	 */
	public void setDate(int year, int month, int day) {
		_calendar.set(_calendar.YEAR, year);	
		_calendar.set(_calendar.MONTH, month - 1);	
		_calendar.set(_calendar.DATE, day);	
	}
	
	/**
	 * Sets the current date according to the date setting of the Date object.
	 * @param date	The date to set
	 */
	public void setDate(Date date) {
		_calendar.setTime(date);	
	}
}
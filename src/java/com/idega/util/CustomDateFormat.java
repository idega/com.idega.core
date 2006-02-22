package com.idega.util;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Title:        A class for formatting presentation of Dates
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class CustomDateFormat {
	private static DateFormat swedishDateFormat;
	private CustomDateFormat() {
	}
	private static DateFormat getSwedishDateTimeFormat(){
		if(swedishDateFormat==null){
			swedishDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return swedishDateFormat;
	}
	/**
	 * Accepts a java.util.Date object
	 * @return A formatted version of the date with time
	 **/
	public static String formatDateTime(Date date, Locale locale) {
		return getDateTimeInstance(locale).format(date);
	}
	/**
	 * Accepts an input Locale
	 * @return A default DateFormat instance for the locale
	 **/
	public static DateFormat getDateTimeInstance(Locale locale){
		if (locale.equals(LocaleUtil.getSwedishLocale())) {
			return getSwedishDateTimeFormat();
		}
		else{
			return DateFormat.getDateTimeInstance(2, 2, locale);
		}
	}
	
	
	public static void main(String[] args) {
		test(args);
	}
	
	public static void test(String[] args) {
		String localeString = null;
		try {
			localeString = args[0];
		} catch (RuntimeException rme) {
		}
		if (localeString == null) {
			localeString = "sv_SE";
		}
		System.out.println(
			"Output: "
				+ formatDateTime(new Date(), LocaleUtil.getLocale(localeString)));
	}
}

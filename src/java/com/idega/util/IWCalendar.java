/*
 * $Id:$
 *
 * Copyright (C) 2000 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.idega.presentation.IWContext;

/**
 * @author idega 2000 - idega team
 * @version 1.0
 */
public class IWCalendar {
	private boolean bIsHoliday = false;
	private String temp_holiday_name = "";
	private int[] easterDay = { 0, 0, 0 };

	/*public boolean isFullMoon(int ar, int manudur, int dagur) {
		boolean returner = false;
		if (getMoonStatus(ar, manudur, dagur) == 1) {
			returner = true;
		}
		return false;
	}

	public boolean isFullMoon(GregorianCalendar calendar) {
		boolean returner = false;
		if (getMoonStatus(calendar.YEAR, calendar.MONTH + 1, calendar.DAY_OF_MONTH) == 0) {
			returner = true;
		}
		return false;
	}

	public int[] getNextFullMoon(int ar, int manudur, int dagur) {
		int return_year = 0;
		int return_month = 0;
		int return_day = 0;
		int temp_year = ar;
		int temp_month = manudur;
		int temp_day = dagur;

		boolean done = false;
		int teljari = 0;

		while ((!done) && (teljari < 100)) {
			++teljari;

			//                correctDate(temp_year,temp_month,temp_day);

			if (temp_day == getLengthOfMonth(temp_month, temp_year) + 1) {
				++temp_month;
				if (temp_month == 13) {
					++temp_year;
					temp_month = 1;
				}
				temp_day = 1;
			}

			if (getMoonStatus(temp_year, temp_month, temp_day) == 1) {
				return_month = temp_month;
				return_day = temp_day;
				return_year = temp_year;
				done = true;
			}

			temp_day++;
		}
		int[] returner = { return_year, return_month, return_day };
		System.out.println("Næsta fulla tungl : " + return_year + " " + return_month + " " + return_day);

		return returner;

	}

	public int[] getLastFullMoon(int ar, int manudur, int dagur) {
		int return_year = 0;
		int return_month = 0;
		int return_day = 0;
		int temp_year = ar;
		int temp_month = manudur;
		int temp_day = dagur;

		boolean done = false;
		int teljari = 0;

		while ((!done) && (teljari < 100)) {
			++teljari;

			correctDate(temp_year, temp_month, temp_day);
			if (getMoonStatus(temp_year, temp_month, temp_day) == 1) {
				return_month = temp_month;
				return_day = temp_day;
				return_year = temp_year;
				done = true;
			}

			temp_day--;
		}
		int[] returner = { return_year, return_month, return_day };
		System.out.println(return_year + " " + return_month + " " + return_day);

		return returner;

	}*/

	/*public int getMoonStatus(int ar, int manudur, int dagur) {
		int returner = -1;

		if (ar == 2001) {
			if ((manudur == 3) && (dagur == 9)) {
				returner = 1;
			}
			else if ((manudur == 4) && (dagur == 7)) {
				returner = 1;
			}
			else if ((manudur == 5) && (dagur == 7)) {
				returner = 1;
			}
			else if ((manudur == 6) && (dagur == 5)) {
				returner = 1;
			}
			else if ((manudur == 7) && (dagur == 5)) {
				returner = 1;
			}
			else if ((manudur == 8) && (dagur == 3)) {
				returner = 1;
			}
			else if ((manudur == 9) && (dagur == 2)) {
				returner = 1;
			}
			else if ((manudur == 10) && (dagur == 1)) {
				returner = 1;
			}
			else if ((manudur == 10) && (dagur == 30)) {
				returner = 1;
			}
			else if ((manudur == 11) && (dagur == 29)) {
				returner = 1;
			}
			else if ((manudur == 12) && (dagur == 28)) {
				returner = 1;
			}
		}
		else if (ar == 2002) {
			if ((manudur == 1) && (dagur == 27)) {
				returner = 1;
			}
			else if ((manudur == 2) && (dagur == 26)) {
				returner = 1;
			}
			else if ((manudur == 3) && (dagur == 27)) {
				returner = 1;
			}
			else if ((manudur == 4) && (dagur == 26)) {
				returner = 1;
			}
			else if ((manudur == 5) && (dagur == 25)) {
				returner = 1;
			}
			else if ((manudur == 6) && (dagur == 24)) {
				returner = 1;
			}
			else if ((manudur == 7) && (dagur == 23)) {
				returner = 1;
			}
			else if ((manudur == 8) && (dagur == 22)) {
				returner = 1;
			}
			else if ((manudur == 9) && (dagur == 20)) {
				returner = 1;
			}
			else if ((manudur == 10) && (dagur == 20)) {
				returner = 1;
			}
			else if ((manudur == 11) && (dagur == 18)) {
				returner = 1;
			}
			else if ((manudur == 12) && (dagur == 18)) {
				returner = 1;
			}
		}
		else if (ar == 2003) {
			if ((manudur == 1) && (dagur == 16)) {
				returner = 1;
			}
		}

		return returner;
	}*/

	public int getLengthOfMonth(int month, int year) {
		GregorianCalendar calendar = new GregorianCalendar();

		int daysInMonth = 31;

		switch (month) {
			case 0 :
				daysInMonth = 31;
				break;
			case 1 :
				daysInMonth = 31;
				break;
			case 2 :
				if (calendar.isLeapYear(year)) {
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

	public String getNameOfMonth(int month) {
		return getNameOfMonth(month,new Locale("is","IS"));
	}

	public String getNameOfMonth(int month, Locale locale) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] months = dfs.getMonths();
		if (months != null) {
			returner = months[month - 1];
		}

		return returner;
	}

	public String getENGNameOfMonth(int month) {
		return getNameOfMonth(month,Locale.ENGLISH);
	}

	public int getWeekOfYear(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(day, month - 1, day);
		return calendar.get(calendar.WEEK_OF_YEAR) + 2;
	}

	public String getShortNameOfDay(int day, Locale locale) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] days = dfs.getShortWeekdays();
		if (days != null) {
			returner = days[day];
		}

		return returner;
	}

	public String getShortNameOfMonth(int month, Locale locale) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] months = dfs.getShortMonths();
		if (months != null) {
			returner = months[month - 1];
		}

		return returner;
	}

	public String getShortISLNameOfMonth(int month) {
		return getShortNameOfMonth(month,new Locale("is","IS"));
	}

	public String getShortENGNameOfMonth(int month) {
		return getShortNameOfMonth(month,Locale.ENGLISH);
	}

	public String getNameOfDay(int day) {
		return getNameOfDay(day,new Locale("is","IS"));
	}

	public String getISLNameOfDay(int day) {
		return getNameOfDay(day,new Locale("is","IS"));
	}

	public String getNameOfDay(int day, Locale locale) {
		String returner = "";

		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] days = dfs.getWeekdays();
		if (days != null) {
			returner = days[day];
		}

		return returner;
	}

	public String getENGNameOfDay(int day) {
		return getNameOfDay(day,Locale.ENGLISH);
	}

	public int getDayOfWeek(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		return calendar.get(calendar.DAY_OF_WEEK);
	}

	/**
	 * @deprecated
	 */
	public boolean getHoliday(int year, int month, int day) {
		return isHoliday(year, month, day);
	}

	public boolean isHoliday(int year, int month, int day) {
		temp_holiday_name = checkForHoliday(year,month,day);
		//System.out.println("Frídagur " + ar + "/" + manudur + "/" + dagur + " heitir : " + temp_holiday_name);
		boolean returner = bIsHoliday;
		bIsHoliday = false;
		return returner;
	}

	/**
	 * Returns int[]
	 * int[0] = year, int[1] = month, int[2] = day
	 */

	public int[] getEasterDay(int year) {
		int a, b, c, d, e, f, g, h, i, j, k, m, n, p = 0;

		a = (int) year % 19;
		b = (int) year / 100;
		c = (int) year % 100;
		d = (int) b / 4;
		e = (int) b % 4;
		f = (int) (b + 8) / 25;
		g = (int) (b - f + 1) / 3;
		h = (int) ((19 * a) + b - d - g + 15) % 30;
		i = (int) c / 4;
		j = (int) c % 4;
		k = (int) (32 + (2 * e) + (2 * i) - h - j) % 7;
		m = (int) (a + (11 * h) + (22 * k)) / 451;
		n = (int) (h + k - (7 * m) + 114) / 31;
		p = (int) (h + k - (7 * m) + 114) % 31;

		int[] returner = { year, n, (p + 1)};

		return returner;
	}

	public String checkForHoliday(int ar, int manudur, int dagur) {
		boolean svara = false;
		String nameOfDay = null;

		int dayOfWeek = getDayOfWeek(ar, manudur, dagur);
		easterDay = getEasterDay(ar);
		IWTimestamp stamp = new IWTimestamp(easterDay[2], easterDay[1], easterDay[0]);

		//  Finna Sumardaginn fyrsti
		if (!(svara)) {
			if ((manudur == 4) && (dagur >= 19) && (dagur <= 25)) {
				if (dayOfWeek == 5) {
					svara = true;
					nameOfDay = "Sumardagurinn fyrsti";
				}
			}
		}

		if (!(svara)) {
			if ((manudur == 8) && (dagur >= 1) && (dagur <= 7)) {
				if (dayOfWeek == 2) {
					svara = true;
					nameOfDay = "Frídagur verslunarmanna";
				}
			}
		}

		if (!(svara))
			switch (manudur) {
				case 1 :
					if (dagur == 1) {
						svara = true;
						nameOfDay = "Nýársdagur";
					}
					break;
				case 2 :
					break;
				case 3 :
					if (dagur == 20) {
						nameOfDay = "Vorjafndægur";
					}
					break;
				case 4 :
					break;
				case 5 :
					if (dagur == 1) {
						svara = true;
						nameOfDay = "Verkalýðsdagurinn";
					}
					break;
				case 6 :
					if (dagur == 17) {
						svara = true;
						nameOfDay = "Lýðveldisdagurinn";
					}
					else if (dagur == 21) {
						nameOfDay = "Sumarsólstöður";
					}
					break;
				case 7 :
					break;
				case 8 :
					break;
				case 9 :
					break;
				case 10 :
					if (dagur == 22) {
						nameOfDay = "Haustjafndægur";
					}
					break;
				case 11 :
					break;
				case 12 :
					if (dagur == 1) {
						nameOfDay = "Fullveldisdagurinn";
					}
					else if (dagur == 21) {
						nameOfDay = "Vetrarsólstöður";
					}
					else if (dagur == 23) {
						nameOfDay = "Þorláksmessa";
					}
					else if (dagur == 24) {
						svara = true; // eftir 12:00
						nameOfDay = "Aðfangadagur";
					}
					else if (dagur == 25) {
						svara = true;
						nameOfDay = "Jóladagur";
					}
					else if (dagur == 26) {
						svara = true;
						nameOfDay = "Annar í jólum";
					}
					else if (dagur == 31) {
						svara = true; // eftir 12:00
						nameOfDay = "Gamlársdagur";
					}
					break;
			}

		//  Finna páskadag
		if (!(svara)) {
			if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
				nameOfDay = "Páskadagur";
				svara = true;
			}

		}

		// checka á dögum eftir páska
		if (!(svara)) {
			stamp.addDays(1);
			if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
				nameOfDay = "Annar í páskum";
				svara = true;
			}
			if (!(svara)) {
				stamp.addDays(38);
				if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
					nameOfDay = "Uppstigningardagur";
					svara = true;
				}
				if (!(svara)) {
					stamp.addDays(10);
					if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
						nameOfDay = "Hvítasunna";
						svara = true;
					}
					if (!(svara)) {
						stamp.addDays(1);
						if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
							nameOfDay = "Annar í hvítasunnu";
							svara = true;
						}
					}
				}
			}
		}
		// stilla aftur á páskadag
		stamp.addDays(-50);

		// checka á dögum fyrir páskadag
		if (!(svara)) {
			stamp.addDays(-1);
			if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
				nameOfDay = "";
				svara = true;
			}
			if (!(svara)) {
				stamp.addDays(-1);
				if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
					nameOfDay = "Föstudagurinn langi";
					svara = true;
				}
				if (!(svara)) {
					stamp.addDays(-1);
					if ((ar == stamp.getYear()) && (manudur == stamp.getMonth()) && (dagur == stamp.getDate())) {
						nameOfDay = "Skírdagur";
						svara = true;
					}
				}
			}
		}

		//  Laugadagur og sunnudagur
		if ((dayOfWeek == 1) || (dayOfWeek == 7)) {
			if (!svara) {
				svara = true;
				nameOfDay = "";
			}
		}

		bIsHoliday = svara;
		return nameOfDay;
	}

	public int getMonth() {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.get(calendar.MONTH) + 1;
	}

	public int getDay() {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.get(calendar.DAY_OF_MONTH);
	}

	public int getYear() {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.get(calendar.YEAR);
	}
}
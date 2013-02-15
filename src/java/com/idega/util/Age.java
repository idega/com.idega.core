package com.idega.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Copyright: Copyright (c) 2002-2004 idega Software
 * @author <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir </a> <br>
 * @version 1.0
 */

public class Age {

	private GregorianCalendar startDate;

	public Age(Date startdate) {
		this.startDate = new GregorianCalendar();
		this.startDate.setTime(startdate);
	}

	public Age(long date) {
		this.startDate = new GregorianCalendar();
		this.startDate.setTime(new Date(date));
	}

	/**
	 * Gets the exact age calculated from birth day.
	 * @return
	 */
	public int getYears() {
		GregorianCalendar now = new GregorianCalendar();
		int yearAge = now.get(Calendar.YEAR) - this.startDate.get(Calendar.YEAR);
		if (now.get(Calendar.MONTH) < this.startDate.get(Calendar.MONTH)) {
			yearAge--;
		} else if (now.get(Calendar.MONTH) == this.startDate.get(Calendar.MONTH)) {
			if (now.get(Calendar.DAY_OF_MONTH) < this.startDate
					.get(Calendar.DAY_OF_MONTH)) {
				yearAge--;
			}
		}
		return yearAge;
	}
	
	/**
	 * Gets the exact age on the day specified calculated from birth day.
	 * @return
	 */
	public int getYears(Date date) {
		//IWTimestamp stamp = new IWTimestamp(date);
		GregorianCalendar now = new GregorianCalendar();//stamp.getYear(), stamp.getMonth(), stamp.getDay());
		now.setTime(date);
		int yearAge = now.get(Calendar.YEAR) - this.startDate.get(Calendar.YEAR);
		if (now.get(Calendar.MONTH) < this.startDate.get(Calendar.MONTH)) {
			yearAge--;
		} else if (now.get(Calendar.MONTH) == this.startDate.get(Calendar.MONTH)) {
			if (now.get(Calendar.DAY_OF_MONTH) < this.startDate
					.get(Calendar.DAY_OF_MONTH)) {
				yearAge--;
			}
		}
		return yearAge;
	}

	public Date getStartDate() {
		return this.startDate.getTime();
	}

	public boolean isOlder(Age age) {
		boolean isOlder = false;

		if (getStartDate().before(age.getStartDate())) {
			isOlder = true;
		}

		return isOlder;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.valueOf(getYears());
    }
}
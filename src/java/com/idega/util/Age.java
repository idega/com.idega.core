package com.idega.util;

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
		startDate.setTime(startdate);
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
		int yearAge = now.get(now.YEAR) - startDate.get(startDate.YEAR);
		if (now.get(now.MONTH) < startDate.get(startDate.MONTH)) {
			yearAge--;
		} else if (now.get(now.MONTH) == startDate.get(startDate.MONTH)) {
			if (now.get(now.DAY_OF_MONTH) < startDate
					.get(startDate.DAY_OF_MONTH)) {
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

		if (getStartDate().before(age.getStartDate()))
			isOlder = true;

		return isOlder;
	}
}
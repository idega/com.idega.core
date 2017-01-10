package com.idega.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.business.IBOLookup;
import com.idega.core.user.data.User;
import com.idega.idegaweb.IWMainApplication;
import com.idega.user.business.UserBusiness;

/**
 * Copyright: Copyright (c) 2002-2004 idega Software
 * @author <br>
 *         <a href="mailto:aron@idega.is">Aron Birkir </a> <br>
 * @version 1.0
 */

public class Age {

	private GregorianCalendar startDate;

	private Age() {
		this.startDate = new GregorianCalendar();
	}

	private Age(Date dateOfBirth, String personalId) {
		this();

		if (dateOfBirth == null) {
			try {
				UserBusiness userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
				dateOfBirth = userBusiness.getUserDateOfBirthFromPersonalId(personalId);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error computing age for person with personal ID: " + personalId, e);
			}
		}

		if (dateOfBirth != null) {
			this.startDate.setTime(dateOfBirth);
		}
	}

	public Age(User user) {
		this(user.getDateOfBirth(), user.getPersonalID());
	}

	public Age(com.idega.user.data.bean.User user) {
		this(user.getDateOfBirth(), user.getPersonalID());
	}

	public Age(Date startdate) {
		this();
		this.startDate.setTime(startdate);
	}

	public Age(long date) {
		this(new Date(date));
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
    @Override
	public String toString() {
        return String.valueOf(getYears());
    }
}
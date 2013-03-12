package com.idega.util.timer;

import java.util.Calendar;
import java.util.Date;

/**
  * This class represents the attributs of a timer.
  *
  * @author  Olivier Dedieu, David Sims, Simon Bécot, Jim Lerner
  * @version 1.4, 01/02/2002
  * @modified Eirikur Hrafnsson eiki@idega.is
  */
public class TimerEntry implements Comparable, java.io.Serializable {
  public int minute = -1;
  public int hour = -1;
  public int dayOfMonth = -1;
  public int month = -1;
  public int dayOfWeek = -1;
  public int year = -1;
  public boolean isRelative;
  public boolean isRepetitive;
  public long timerTime;
  public String timerName = "";
  public transient TimerListener listener;
  public boolean canRun = true;
  
/**
 * @return Returns the dayOfMonth.
 */
public int getDayOfMonth() {
	return this.dayOfMonth;
}
/**
 * @param dayOfMonth The dayOfMonth to set.
 */
public void setDayOfMonth(int dayOfMonth) {
	this.dayOfMonth = dayOfMonth;
}
/**
 * @return Returns the dayOfWeek.
 */
public int getDayOfWeek() {
	return this.dayOfWeek;
}
/**
 * @param dayOfWeek The dayOfWeek to set.
 */
public void setDayOfWeek(int dayOfWeek) {
	this.dayOfWeek = dayOfWeek;
}
/**
 * @return Returns the hour.
 */
public int getHour() {
	return this.hour;
}
/**
 * @param hour The hour to set.
 */
public void setHour(int hour) {
	this.hour = hour;
}
/**
 * @return Returns the if the handleTime method can be called.
 */
public boolean canRun() {
	return this.canRun;
}
/**
 * @param canRun
 */
public void setCanRun(boolean canRun) {
	this.canRun = canRun;
}
/**
 * @return Returns the isRelative.
 */
public boolean isRelative() {
	return this.isRelative;
}
/**
 * @param isRelative The isRelative to set.
 */
public void setRelative(boolean isRelative) {
	this.isRelative = isRelative;
}
/**
 * @return Returns the isRepetitive.
 */
public boolean isRepetitive() {
	return this.isRepetitive;
}
/**
 * @param isRepetitive The isRepetitive to set.
 */
public void setRepetitive(boolean isRepetitive) {
	this.isRepetitive = isRepetitive;
}
/**
 * @return Returns the minute.
 */
public int getMinute() {
	return this.minute;
}
/**
 * @param minute The minute to set.
 */
public void setMinute(int minute) {
	this.minute = minute;
}
/**
 * @return Returns the month.
 */
public int getMonth() {
	return this.month;
}
/**
 * @param month The month to set.
 */
public void setMonth(int month) {
	this.month = month;
}
/**
 * @return Returns the timerName.
 */
public String getTimerName() {
	return this.timerName;
}
/**
 * @param timerName The timerName to set.
 */
public void setTimerName(String timerName) {
	this.timerName = timerName;
}
/**
 * @return Returns the timerTime.
 */
public long getTimerTime() {
	return this.timerTime;
}
/**
 * @param timerTime The timerTime to set.
 */
public void setTimerTime(long timerTime) {
	this.timerTime = timerTime;
}
/**
 * @return Returns the year.
 */
public int getYear() {
	return this.year;
}
/**
 * @param year The year to set.
 */
public void setYear(int year) {
	this.year = year;
}
  private transient boolean debug = false;

  private void debug(String s) {
    if (this.debug) {
			System.out.println("[" + Thread.currentThread().getName() + "] TimerEntry: " + s);
		}
  }

  /**
    * Creates a new TimerEntry.
    *
    * @param date the timer date to be added.
    * @param listener the timer listener.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public TimerEntry(Date date, TimerListener listener) throws PastDateException {
    this.listener = listener;
    Calendar timer = Calendar.getInstance();
    timer.setTime(date);
    this.minute = timer.get(Calendar.MINUTE);
    this.hour = timer.get(Calendar.HOUR_OF_DAY);
    this.dayOfMonth = timer.get(Calendar.DAY_OF_MONTH);
    this.month = timer.get(Calendar.MONTH);
    this.year = timer.get(Calendar.YEAR);
    this.isRepetitive = false;
    this.isRelative = false;
    this.timerTime = date.getTime();
    checkTimerTime();
  }


  /**
    * Creates a new TimerEntry.
    *
    * @param delay the timer delay in minute (relative to now).
    * @param isRepetitive <code>true</code> if the timer must be
    * reactivated, <code>false</code> otherwise.
    * @param listener the timer listener.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public TimerEntry(int delay, boolean isRepetitive, TimerListener listener)
    throws PastDateException {
    if (delay < 1) {
      throw new PastDateException();
    }

    this.minute = delay;
    this.listener = listener;
    this.isRepetitive = isRepetitive;
    if (this.minute < 1) {
			throw new IllegalArgumentException();
		}

    this.isRelative = true;
    updateTimerTime();
  }
 
  /**
    * Creates a new TimerEntry.
    *
    * @param minute minute of the timer. Allowed values 0-59.
    * @param hour hour of the timer. Allowed values 0-23.
    * @param dayOfMonth day of month of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfWeek</code>. Allowed values 1-31.
    * @param month month of the timer (-1 if every month). Allowed values
    * 0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
    * constants can be used.
    * @param dayOfWeek day of week of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfMonth</code>. Allowed values 1-7 (1 = Sunday, 2 =
    * Monday, ...). <code>java.util.Calendar</code> constants can  be used.
    * @param year year of the timer. When this field is not set
    * (i.e. -1) the timer is repetitive (i.e. it is rescheduled when
    *  reached).
    * @param listener the timer listener.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public TimerEntry(int minute, int hour, int dayOfMonth, int month,
                    int dayOfWeek, int year, TimerListener listener)
    throws PastDateException {
    this.minute = minute;
    this.hour = hour;
    this.dayOfMonth = dayOfMonth;
    this.month = month;
    this.dayOfWeek = dayOfWeek;
    this.year = year;
    this.listener = listener;
    this.isRepetitive = (year == -1);
    this.isRelative = false;
    updateTimerTime();
    checkTimerTime();
  }
  /**
   * @param timerName the timer name.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    **/

  public TimerEntry(int minute, int hour, int dayOfMonth, int month,
                    int dayOfWeek, int year, TimerListener listener, String timerName)throws PastDateException {
    this(minute, hour, dayOfMonth, month, dayOfWeek, year, listener);
    this.timerName = timerName;
  }

  /**
    * Updates the timer time for repetitive timers.
    */
  public void updateTimerTime() {
    if (this.isRelative) {
      this.timerTime = System.currentTimeMillis() + (this.minute * 60000);
      return;
    }

    Calendar now = Calendar.getInstance();
    Calendar timer = (Calendar)now.clone();
    debug("now: " + now.getTime());

    if (this.year != -1) {
      timer.set(Calendar.YEAR, this.year);
    }

    if (this.month != -1) {
      timer.set(Calendar.MONTH, this.month);
    }

    if (this.hour != -1) {
	timer.set(Calendar.HOUR_OF_DAY, this.hour);
    }

    if (this.minute != -1) {
	timer.set(Calendar.MINUTE, this.minute);
    }

    timer.set(Calendar.SECOND, 0);


    // Increments minute if now >= timer (for every minute timers)
    if (this.minute == -1) {
      timer.add(Calendar.MINUTE, 1);
    }

    // Increments hour if now >= timer (for every hour timers)
    if (this.hour == -1 && this.minute != -1 && now.get(Calendar.MINUTE) >= this.minute) {
      timer.add(Calendar.HOUR_OF_DAY, 1);
    }


   // Incrementes dayOfYear if now >= timer (for everyday timers)
    if (this.dayOfMonth == -1 &&
        this.dayOfWeek == -1 &&
        this.hour != -1 && this.minute != -1 &&
        (now.get(Calendar.HOUR_OF_DAY) > this.hour ||
         (now.get(Calendar.HOUR_OF_DAY) == this.hour &&
          now.get(Calendar.MINUTE) >= this.minute))) {
      timer.roll(Calendar.DAY_OF_YEAR, true);
    }

    // Incrementes year if now >= timer (for monthly or yearly timers)
    if (this.month != -1 && this.year == -1 &&
        (now.get(Calendar.MONTH) > this.month ||
         (now.get(Calendar.MONTH) == this.month &&
          (now.get(Calendar.DAY_OF_MONTH) > this.dayOfMonth ||
           (now.get(Calendar.DAY_OF_MONTH) == this.dayOfMonth &&
            (now.get(Calendar.HOUR_OF_DAY) > this.hour ||
             (now.get(Calendar.HOUR_OF_DAY) == this.hour &&
              (now.get(Calendar.MINUTE) >= this.minute)))))))) {
        timer.add(Calendar.YEAR, 1);
      }

    // Weekly timers
    if (this.dayOfWeek != -1) {
      int deltaOfDay = (7 + (this.dayOfWeek - now.get(Calendar.DAY_OF_WEEK))) % 7;
      debug("deltaOfDay: " + deltaOfDay);
      if (deltaOfDay != 0) {
	timer.add(Calendar.DAY_OF_YEAR, deltaOfDay);
      }
      // Incrementes week if now >= timer
      else if (now.get(Calendar.HOUR_OF_DAY) > this.hour ||
	       (now.get(Calendar.HOUR_OF_DAY) == this.hour &&
		now.get(Calendar.MINUTE) >= this.minute)) {
	timer.add(Calendar.WEEK_OF_YEAR, 1);
      }
    }

    // Monthly timers
    else if (this.dayOfMonth != -1) {
      timer.set(Calendar.DAY_OF_MONTH, this.dayOfMonth);

      // Incrementes month if now >= timer (for weekly timers)
      if (this.month == -1 &&
          (now.get(Calendar.DAY_OF_MONTH) > this.dayOfMonth ||
           (now.get(Calendar.DAY_OF_MONTH) == this.dayOfMonth &&
            (now.get(Calendar.HOUR_OF_DAY) > this.hour ||
             (now.get(Calendar.HOUR_OF_DAY) == this.hour &&
              now.get(Calendar.MINUTE) >= this.minute))))) {
        timer.roll(Calendar.MONTH, true);
      }
    }

    debug("timer: " + timer.getTime());

    this.timerTime = timer.getTime().getTime();
  }

  /**
    * Checks that timer is not in the past (or less than 1 second
    * closed to the current date).
    *
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  void checkTimerTime() throws PastDateException {
    long delay = this.timerTime - System.currentTimeMillis();
    if (delay <= 1000) {
      throw new PastDateException();
    }
  }

  /**
    * Returns a string representation of this timer.
    *
    * @return the string.
    */
  public String toString() {
    if (this.year != -1) {
      return "Timer at " + new Date(this.timerTime);
    }
    StringBuffer sb = new StringBuffer("Timer params");
    sb.append(" minute="); sb.append(this.minute);
    sb.append(" hour="); sb.append(this.hour);
    sb.append(" dayOfMonth="); sb.append(this.dayOfMonth);
    sb.append(" month="); sb.append(this.month);
    sb.append(" dayOfWeek="); sb.append(this.dayOfWeek);
    sb.append(" (next timer date=" + new Date(this.timerTime) + ")");
    sb.append(" name="+ this.timerName);

    return sb.toString();
  }

  // ----------------------------------------------------------------------
  //                      Comparable interface
  // ----------------------------------------------------------------------

  /**
    * Compares this TimerEntry with the specified TimerEntry for order.
    *
    * @param obj the TimerEntry with which to compare.
    * @return a negative integer, zero, or a positive integer as this
    * TimerEntry is less than, equal to, or greater than the given
    * TimerEntry.
    * @exception ClassCastException if the specified Object's type
    * prevents it from being compared to this TimerEntry.
    */
  public int compareTo(Object obj) {
    TimerEntry entry = (TimerEntry)obj;
    if (this.timerTime < entry.timerTime) {
			return -1;
		}
    if (this.timerTime > entry.timerTime) {
			return 1;
		}
    return 0;
  }


  /**
    * Indicates whether some other TimerEntry is "equal to" this one.
    *
    * @param obj the TimerEntry with which to compare.
    * @return <code>true if this TimerEntry has the same
    * <code>timerTime</code> as the <code>timerTime</code> of the obj
    * argument; <code>false</code> otherwise.
    */
  public boolean equals(Object obj) {
    TimerEntry entry = (TimerEntry)obj;
    if ( (this.timerTime == entry.timerTime) && (this.timerName.equals(entry.timerName)) ){
      return true;
    }
    return false;
  }
}










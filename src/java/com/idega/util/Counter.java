/*
 * Created on 21.8.2004
 */
package com.idega.util;


/**
 * @author laddi
 */
public class Counter implements Comparable {
	
	public Counter() {
	}
	
	public Counter(int seconds) {
		this.seconds = seconds;
	}

	private int seconds = 0;
	
	public static final int SECONDS_IN_MINUTE = 60;
	public static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
	
	public void addSeconds(int seconds) {
		this.seconds += seconds;
	}
	
	public void addMinutes(int minutes) {
		addSeconds(minutes * SECONDS_IN_MINUTE);
	}
	
	public void addHours(int hours) {
		addSeconds(hours * SECONDS_IN_HOUR);
	}
	
	public int getHours() {
		return seconds / SECONDS_IN_HOUR;
	}
	
	public int getMinutes() {
		int remainder = seconds % SECONDS_IN_HOUR;
		return remainder / SECONDS_IN_MINUTE;
	}
	
	public int getSeconds() {
		int remainder = seconds % SECONDS_IN_HOUR;
		return remainder % SECONDS_IN_MINUTE;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		int hours = getHours();
		int minutes = getMinutes();
		int seconds = getSeconds();
		
		if (this.seconds < 0) {
			hours = Math.abs(hours);
			minutes = Math.abs(minutes);
			seconds = Math.abs(seconds);
			buffer.append("-");
		}
		
		if (hours < 10) {
			buffer.append("0");
		}
		buffer.append(hours).append(":");
		if (minutes < 10) {
			buffer.append("0");
		}
		buffer.append(minutes).append(":");
		if (seconds < 10) {
			buffer.append("0");
		}
		buffer.append(seconds);
		
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object instanceof Counter) {
			Counter counter = (Counter) object;
			if (getHours() == counter.getHours()) {
				if (getMinutes() == counter.getMinutes()) {
					if (getSeconds() == counter.getSeconds()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		if (object instanceof Counter) {
			Counter counter = (Counter) object;
			int returner = getHours() - counter.getHours();
			if (returner == 0) {
				returner = getMinutes() - counter.getMinutes();
				if (returner == 0) {
					returner = getSeconds() - counter.getSeconds();
				}
			}
			return returner;
		}
		return 0;
	}
}
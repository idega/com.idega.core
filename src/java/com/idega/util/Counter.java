/*
 * Created on 21.8.2004
 */
package com.idega.util;


/**
 * @author laddi
 */
public class Counter {

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
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		int hours = seconds / SECONDS_IN_HOUR;
		int remainder = seconds % SECONDS_IN_HOUR;
		
		int minutes = remainder / SECONDS_IN_MINUTE;
		int seconds = remainder % SECONDS_IN_MINUTE;
		
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
}

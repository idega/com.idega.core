package com.idega.util;

/**
 * <p>
 * Title: Timer
 * </p>
 * <p>
 * Description: A simple "stopwatch" class with millisecond accuracy
 * </p>
 * <p>
 * Copyright (c) 2002
 * </p>
 * <p>
 * Company: Idega Software
 * </p>
 * 
 * @author <a href="mailto:eiki@idega.is"> Eirikur Sveinn Hrafnsson </a>
 * @version 1.0
 */
public class Timer {

	private long startTime, endTime;

	private boolean hasStopped = false;

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		hasStopped = true;
		endTime = System.currentTimeMillis();
	}

	public long getTime() {
		if(hasStopped){
			return endTime - startTime;
		}
		else{
			return System.currentTimeMillis()-startTime;
		}
	}

	public void reset() {
		startTime = 0;
		endTime = 0;
		hasStopped = false;
	}

	public String getTimeString() {
		return new IWTimestamp(getTime()).getDateString("m:ss.SSS");
	}
}
package com.idega.util;

import java.util.logging.Level;
import java.util.logging.Logger;

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

	private long startTime, endTime, breakPointTime;

	private boolean hasStopped = false;
	private int counter = 0;

	public void start() {
		hasStopped = false;
		startTime = System.currentTimeMillis();
		breakPointTime = startTime;
		counter=0;
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
		return getTimeString(getTime());
	}

	private String getTimeString(long time) {
		return new IWTimestamp(time).getDateString("m:ss.SSS");
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param level The log level
	 * @param identifier The message to log out
	 */
	public void logTime(Level level, String identifier) {
		long current = endTime;
		if(!hasStopped){
			current = System.currentTimeMillis();
		}
		
		long fromBegining = current-startTime;
		long fromLastBreak = current-breakPointTime;
		log(level,"[TIMER-"+identifier+"{"+(counter++)+"}]: "+getTimeString(fromLastBreak)+" since last log, total time "+getTimeString(fromBegining));
		breakPointTime = current;
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param identifier The message to log out
	 */
	public void logTime(String identifier) {
		logTime(getDefaultLogLevel(),identifier);		
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param identifier The message to log out
	 */
	public void logTime() {
		logTime(getDefaultLogLevel(),"");		
	}
	
	/**
	 * Logs out to the specified log level to the default Logger
	 * @param level The log level
	 * @param msg The message to log out
	 */
	protected void log(Level level,String msg) {
		//System.out.println(msg);
		getLogger().log(level,msg);
	}
	
	/**
	 * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	 * This behaviour can be overridden in subclasses.
	 * @return the default Logger
	 */
	protected Logger getLogger(){
		return Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 * @return the Level
	 */
	protected Level getDefaultLogLevel(){
		return Level.INFO;
	}
	/**
	 * Gets the log level which debug messages are sent to.
	 * @return the Level
	 */
	protected Level getDebugLogLevel(){
		return Level.FINER;
	}
	/**
	 * Gets the log level which error messages are sent to.
	 * @return the Level
	 */
	protected Level getErrorLogLevel(){
		return Level.WARNING;
	}

}
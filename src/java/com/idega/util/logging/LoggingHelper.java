/*
 * Created on Nov 22, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.util.logging;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A class which has helper methods for the standard Java 1.4 logging API
 * Copyright (c) 2003 idega software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class LoggingHelper {
	
    private static String NEWLINE="\n";
    private static String TAB="\t";
    private static String COLON = ":";
    private static String COLON_WITH_SPACE = " : ";
    private static String DOT = ".";
    /**
     * Logs the exception to the anonymous logger with loglevel Warning:
     * @param e the Exception to log
     */
	public static void logException(Exception e){
	    Logger logger = Logger.getAnonymousLogger();
		logException(e,null,logger,Level.WARNING);
	}
    /**
     * Logs the exception to the default logger of Object catcher with loglevel Warning:
     * @param e the Exception to log
     */
	public static void logException(Exception e,Object catcher){
	    Logger logger = Logger.getLogger(catcher.getClass().getName());
		logException(e,catcher,logger,Level.WARNING);
	}
    /**
     * Logs the exception to the specified logger with loglevel Warning:
     * @param e the Exception to log
     */
	public static void logException(Exception e,Object catcher,Logger logger){
		logException(e,catcher,logger,Level.WARNING);
	}
	
	public static void logException(Exception e,Object catcher,Logger logger,Level level){
		//e.printStackTrace();
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer buf = new StringBuffer(e.getClass().getName());
		buf.append(COLON_WITH_SPACE);
		buf.append(e.getMessage());
		buf.append(NEWLINE);
		if(ste.length>0){
		    buf.append(TAB);
			buf.append(ste[0].getClassName());
			buf.append(DOT);
			buf.append(ste[0].getMethodName());
			buf.append(COLON);
			buf.append(ste[0].getLineNumber());
			buf.append(NEWLINE);
		}
		String str = buf.toString();
		logger.log(level,str);
	}

	public static void getLoggerInfo() {
	    // Get the Log Manager
	    LogManager manager = LogManager.getLogManager();
	    // Get all defined loggers
	    java.util.Enumeration names = manager.getLoggerNames();
	    
	    System.out.println("***Begin Logger Information");
	    // For each logger: show name, level, handlers etc. 
	    while (names.hasMoreElements()) {
	      String loggername = (String)names.nextElement();
	      java.util.logging.Logger logger = manager.getLogger(loggername);
	      System.out.println("-----------------------");
	      System.out.println("Logger name: >" + logger.getName() + "<");
	      System.out.println("Logger level: " + logger.getLevel());
	      
	      // See if a filter is defined
	      if (logger.getFilter() != null) {
			System.out.println("Using a filter");
		}
		else {
			System.out.println("No filter used");
		}
	      
	      // For each handler: show formatter, level, etc. 
	      java.util.logging.Handler[] h = logger.getHandlers();
	      if (h.length == 0) {
			System.out.println("No handlers defined");
		}
	      for (int i = 0; i < h.length; i++) {
	        if (i == 0) {
				System.out.println("Handlers:");
			}
	        java.util.logging.Formatter f = h[i].getFormatter();
	        System.out.println(h[i].getClass().getName());
	        System.out.println("  using formatter: " + f.getClass().getName());
	        System.out.println("  using level: " + h[i].getLevel());
	        if (h[i].getFilter() != null) {
				System.out.println("  using a filter");
			}
			else {
				System.out.println("  no filter");
			}
	      }
	
	      // See if a parent exists 
	      java.util.logging.Logger parent = logger.getParent();
	      if (parent != null) {
			System.out.println("Parent: >" + parent.getName() + "<");
		}
		else {
			System.out.println("No parent");
		}
	    }
	    System.out.println("*** End Logger Information");
	  }
	
}

/*
 * Created on Nov 22, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which has helper methods for the standard Java 1.4 logging API
 * Copyright (c) 2003 idega software
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class LoggingHelper {
	
	
	public static void logException(Exception e,Object catcher,Logger logger){
		logException(e,catcher,logger,Level.WARNING);
	}
	
	public static void logException(Exception e,Object catcher,Logger logger,Level level){
		//e.printStackTrace();
		StackTraceElement[] ste = e.getStackTrace();
		StringBuffer buf = new StringBuffer(e.getClass().getName());
		buf.append(" : ");
		if(ste.length>0){
			buf.append(ste[0].getMethodName());
			buf.append(":");
			buf.append(ste[0].getLineNumber());
		}
		String str = buf.toString();
		logger.log(level,str);
	}
	
}

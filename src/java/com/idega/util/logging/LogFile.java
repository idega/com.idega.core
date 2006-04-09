/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.util.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import com.idega.util.FileUtil;


/**
 * The LogFile writes messages to a specified file using 
 * java.util.logging.Logger, java.util.logging.FileHandler and
 * java.util.logging.SimpleFormatter.
 * 
 * The LogFile should be closed by a call of the method close() 
 * if not longer needed.
 *  
 * @author thomas
 *
 */
public class LogFile {
	
	private Logger logger;
	
	public LogFile(File file) throws IOException {
		initialize(file);
	}
	
	public LogFile(String pattern) throws IOException {
		initialize(pattern);
	}
	
	private void initialize(File file) throws IOException {
		if (! FileUtil.createFileIfNotExistent(file)) {
			throw new IOException("[Logfile] Could not create logfile");
		}
		String canonicalPath = file.getCanonicalPath();
		initialize(canonicalPath);
	}
	
	private void initialize(String pattern) throws IOException {
		FileHandler handler = new FileHandler(pattern);
		handler.setFormatter(new SimpleFormatter());
		this.logger = Logger.getAnonymousLogger();
		this.logger.setLevel(Level.ALL);
		this.logger.addHandler(handler);
	}
		
	public void log(Level level, String msg) {
		this.logger.log(level, msg);
	}
	
	public void logInfo(String msg) {
		log(Level.INFO, msg);
	}
	
	public void close() {
		Handler[] handlers = this.logger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].close();
		}
	}
}

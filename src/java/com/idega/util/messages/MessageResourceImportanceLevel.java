package com.idega.util.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0 
 *
 * Last modified: Oct 17, 2008 by Author: Anton 
 *
 */

public class MessageResourceImportanceLevel extends Level {
	public static final Level FIRST_ORDER = new MessageResourceImportanceLevel("FIRST_ORDER", 1000);
	public static final Level MIDDLE_ORDER = new MessageResourceImportanceLevel("MIDDLE_ORDER", 500);
	public static final Level LAST_ORDER = new MessageResourceImportanceLevel("LAST_ORDER", 1);
	public static final Level OFF = Level.OFF;
	
	private static final long serialVersionUID = 1992189795342514783L;

	protected MessageResourceImportanceLevel(String name, int value) {
		super(name, value);
	}
	
	public static List<Level> levelList() {
		List<Level> levels = new ArrayList<Level>(4);
		levels.add(FIRST_ORDER);
		levels.add(MIDDLE_ORDER);
		levels.add(LAST_ORDER);
		levels.add(OFF);
		
		return levels;
	}
	
	public static Level getLevel(int value) {
		Level ifNotFoundLevel = Level.OFF;
		for(Level level : levelList()) {
			if(level.intValue() == value) {
				return level;
			}
		}
		return ifNotFoundLevel;
	}
}

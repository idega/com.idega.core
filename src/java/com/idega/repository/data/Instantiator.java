/*
 * Created on Jan 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.repository.data;


/**
 * Use an Instantiator for using the SingletonRepository
 *  
 * @author thomas
 * 
 * 
 */
public abstract class Instantiator {
	
	public Object getInstance() {
		// do nothing by default
		return null;
	}
	
	public Object getInstance(Object parameter) {
		// do nothing by default
		return null;
	}
	
	public void unload() {
		// do nothing by default
	}
}

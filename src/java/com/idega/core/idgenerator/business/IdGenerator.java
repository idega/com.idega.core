/*
 * Created on 30.6.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.core.idgenerator.business;

/**
 * This is a general Id for Id generators
 * @author tryggvil
 */
public interface IdGenerator {

	/**
	 * Generates a unique new Id
	 * @return
	 */
	public String generateId();
}

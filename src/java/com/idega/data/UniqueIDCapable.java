/*
 * Created on Jun 30, 2004
 */
package com.idega.data;

/**
 * A simply requires the getUniqueID() and setUniqueID() methods in your IDO that GenericEntity implements.<br>
 * A convenience interface so you don't have to code a call to the GenericEntity method if you already extend it and your interface extends this class. 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 *
 **/
public interface UniqueIDCapable {
	/**
	 * @return The unique id string of the entity if it has it, otherwise null
	 */
	public String getUniqueId();
	
	/**
	 * Sets the Unique ID column.
	 * This method should generally never be called manually
	 * @param uniqueId
	 */
	public void setUniqueId(String uniqueId);
}

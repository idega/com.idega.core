package com.idega.io.serialization;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 31, 2004
 */
public class StorableHolder {
	
	private Storable storable;
	private String value;
	
	/**
	 * @return Returns the storable.
	 */
	public Storable getStorable() {
		return storable;
	}
	/**
	 * @param storable The storable to set.
	 */
	public void setStorable(Storable storable) {
		this.storable = storable;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
}

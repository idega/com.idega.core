/*
 * Created on 19.5.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.data;


/**
 * A class that data classes should implement to declare that they implement metata property behaviour.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public interface MetaDataCapable
{
	/**
	 *Sets all the metadata key/values for this instance with the given map where the is keys and values of String type.
	 */
	public void setMetaDataAttributes(java.util.Map map);
	/**
	 *Gets all the metadata key/values for this instance with the given map where the keys and values of String type.
	 */
	public java.util.Map getMetaDataAttributes();

	/**
	 *Sets all the metadata types this instance with the given map which is keys and values of String type.
	 */
	public java.util.Map getMetaDataTypes();
	
	/**
	 *Set the metadata set for the key metaDataKey to value value
	 */
	public void setMetaData(String metaDataKey,String value);
	/**
	 *Set the metadata set for the key metaDataKey to value value
	 */
	public void setMetaData(String metaDataKey,String value,String type);
	/**
	 * Gets the metadata set for the key metaDataKey
	 */
	public String getMetaData(String metaDataKey);

	/**
	 * Rename a metadata key
	 */
	public void renameMetaData(String oldKeyName, String newKeyName);

	/**
	 * Rename a metadata key, and change the value
	 */
	public void renameMetaData(String oldKeyName, String newKeyName, String value);
	
	/**
	 * Gets the metadata for the key metaDataKey
	 */
	public boolean removeMetaData(String metaDataKey);
	
	/**
	 * @deprecated legacy
	 * @throws java.sql.SQLException
	 */
	public void updateMetaData() throws java.sql.SQLException;
}

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
	 *TODO: change data type to java.util.Map
	 */
	public void setMetaDataAttributes(java.util.Hashtable map);
	/**
	 *TODO: change data type to java.util.Map
	 */
	public java.util.Hashtable getMetaDataAttributes();
	
	/*
	 *Set the metadata set for the key metaDataKey to value value
	 */
	public void setMetaData(String metaDataKey,String value);
	/*
	 * Gets the metadata set for the key metaDataKey
	 */
	public String getMetaData(String metaDataKey);
}

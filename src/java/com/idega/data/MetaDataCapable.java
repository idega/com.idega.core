/*
 * $Id: MetaDataCapable.java,v 1.11 2009/05/22 04:56:09 laddi Exp $ Created
 * on 19.5.2003 by Tryggvi Larusson
 * 
 * Copyright (C) 2003-2006 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.data;

import java.util.Map;

/**
 * <p>
 * Entity beans should implement this interfaceto declare that they implement
 * metata property behaviour, i.e. to add and store arbitrary key/value pairs.
 * </p>
 * Last modified: $Date: 2009/05/22 04:56:09 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.11 $
 */
public interface MetaDataCapable {

	/**
	 *Sets all the metadata key/values for this instance with the given map where
	 * the is keys and values of String type.
	 */
	public void setMetaDataAttributes(Map<String, String> map);

	/**
	 *Gets all the metadata key/values for this instance with the given map where
	 * the keys and values of String type.
	 */
	public Map<String, String> getMetaDataAttributes();

	/**
	 *Sets all the metadata types this instance with the given map which is keys
	 * and values of String type.
	 */
	public Map<String, String> getMetaDataTypes();

	/**
	 *Set the metadata set for the key metaDataKey to value value
	 */
	public void setMetaData(String metaDataKey, String value);

	/**
	 *Set the metadata set for the key metaDataKey to value value
	 */
	public void setMetaData(String metaDataKey, String value, String type);

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
/*
 * Created on 7.7.2003
 */
package com.idega.idegaweb;

import java.sql.Timestamp;
import java.util.Date;

import com.idega.presentation.text.Link;

/**
 * @author laddi
 */
public class IWMetaDataConstants {

	public static final String METADATA_TYPE_STRING = String.class.getName();
	public static final String METADATA_TYPE_INTEGER = Integer.class.getName();
	public static final String METADATA_TYPE_FLOAT = Float.class.getName();
	public static final String METADATA_TYPE_BOOLEAN = Boolean.class.getName();
	public static final String METADATA_TYPE_DATE = Date.class.getName();
	public static final String METADATA_TYPE_TIMESTAMP = Timestamp.class.getName();
	public static final String METADATA_TYPE_LINK = Link.class.getName();
	
	public static final String METADATA_TYPE_MULTIVALUED = "multivalued";
	public static final String METADATA_TYPE_MULTIVALUED_SINGLE_SELECT = "multivalued_single";

	public static String[] getMetaDataTypes() {
		String[] types = { METADATA_TYPE_STRING, METADATA_TYPE_INTEGER, METADATA_TYPE_FLOAT, METADATA_TYPE_BOOLEAN, METADATA_TYPE_DATE, METADATA_TYPE_TIMESTAMP, METADATA_TYPE_LINK, METADATA_TYPE_MULTIVALUED, METADATA_TYPE_MULTIVALUED_SINGLE_SELECT }; 
		return types;
	}
}
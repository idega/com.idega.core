/*
 * $Id: MetaDataBMPBean.java,v 1.8.2.1 2008/06/05 20:36:54 eiki Exp $
 * 
 * Copyright (C) 2001-2006 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 * 
 */
package com.idega.data;

import java.sql.SQLException;

/**
 * <p>
 * This entity class for a general Meta-data table. IDO Entities can easily add
 * a many-to-many relation to this entity and thus gaining the functionality to
 * store arbitary key/value pairs.
 * <p>
 * Last modified: $Date: 2008/06/05 20:36:54 $ by $Author: eiki $
 * 
 * @author <a href="eiki@idega.is">Eirikur S. Hrafnsson</a>,<a
 *         href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.8.2.1 $
 */
public class MetaDataBMPBean extends com.idega.data.GenericEntity implements com.idega.data.MetaData {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6936023370927884237L;
	public static final String COLUMN_META_KEY ="METADATA_NAME";
	public static final String COLUMN_META_VALUE = "METADATA_VALUE";
	public static final String COLUMN_META_TYPE = "META_DATA_TYPE";
	
	public static final String TABLE_NAME="IC_METADATA";

	protected MetaDataBMPBean() {
		super();
	}

	protected MetaDataBMPBean(int id) throws SQLException {
		super(id);
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_META_KEY, "The key name", true, true, String.class, 255);
		addAttribute(COLUMN_META_VALUE, "The key's value", true, true, String.class, 2000);
		addAttribute(COLUMN_META_TYPE, "The value's type", true, true, String.class, 255);
		addIndex("IDX_IC_METADATA_1", new String[] { COLUMN_META_KEY, COLUMN_META_VALUE });
		addIndex("IDX_IC_METADATA_2", new String[] { COLUMN_META_KEY });
		addIndex("IDX_IC_METADATA_3", new String[] { COLUMN_META_VALUE });
	}

	public String getEntityName() {
		return (TABLE_NAME);
	}

	public String getName() {
		return (String) getColumnValue(COLUMN_META_KEY);
	}

	public String getMetaDataName() {
		return getName();
	}

	public String getMetaDataValue() {
		return getValue();
	}

	public String getValue() {
		return (String) getColumnValue(COLUMN_META_VALUE);
	}

	public String getMetaDataType() {
		return getType();
	}

	public String getType() {
		return (String) getColumnValue(COLUMN_META_TYPE);
	}

	public void setValue(String value) {
		setColumn(COLUMN_META_VALUE, value);
	}

	public void setMetaDataValue(String value) {
		setValue(value);
	}

	public void setMetaDataName(String name) {
		setName(name);
	}

	public void setMetaDataNameAndValue(String name, String value) {
		setName(name);
		setValue(value);
	}

	public void setName(String name) {
		setColumn(COLUMN_META_KEY, name);
	}

	public void setMetaDataType(String type) {
		setType(type);
	}

	public void setType(String type) {
		setColumn(COLUMN_META_TYPE, type);
	}
}

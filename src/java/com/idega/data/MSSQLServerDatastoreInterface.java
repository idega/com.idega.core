/*
 * Created on 28.2.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.data;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Title:        MSSQLServerDatastoreInterface
 * Description:  A class to handle Microsoft SQL Server specific jdbc implementations.
 * Copyright:  (C) 2003 idega software All Rights Reserved.
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0  
 */
public class MSSQLServerDatastoreInterface extends DatastoreInterface
{
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#getSQLType(java.lang.String, int)
	 */
	public String getSQLType(String javaClassName, int maxlength)
	{
		String theReturn;
		if (javaClassName.equals("java.lang.Integer"))
		{
			theReturn = "INTEGER";
		}
		else if (javaClassName.equals("java.lang.String"))
		{
			if (maxlength < 0)
			{
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 8000)
			{
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else
			{
				theReturn = "CLOB";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean"))
		{
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float"))
		{
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.lang.Double"))
		{
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.sql.Timestamp"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("java.sql.Blob"))
		{
			theReturn = "IMAGE";
		}
		else if (javaClassName.equals("java.sql.Time"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender"))
		{
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper"))
		{
			theReturn = "IMAGE";
		}
		else
		{
			theReturn = "";
		}
		return theReturn;
	}
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#createTrigger(com.idega.data.IDOLegacyEntity)
	 */
	public void createTrigger(IDOLegacyEntity entity) throws Exception
	{
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * @param entity
	 * @param conn
	 */
	protected void updateNumberGeneratedValue(IDOLegacyEntity entity, Connection conn)
	{
		try{
			if(((IDOEntityBean)entity).getPrimaryKeyClass().equals(Integer.class)){
				if (entity.isNull(entity.getIDColumnName())) {
					Object value = this.executeQuery(entity,"select @@IDENTITY");
					entity.setID(Integer.parseInt(value.toString()));
				}	
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * @return boolean
	 */
	protected boolean updateNumberGeneratedValueAfterInsert()
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public String getIDColumnType() {
		return "INTEGER IDENTITY";
	}
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#executeAfterInsert(com.idega.data.IDOLegacyEntity)
	 */
	protected void executeBeforeInsert(IDOLegacyEntity entity) throws Exception
	{
		try{
			if(((IDOEntityBean)entity).getPrimaryKeyClass().equals(Integer.class)){
				if (!entity.isNull(entity.getIDColumnName())) {
					String tableName = entity.getTableName();
					executeUpdate(entity,"set IDENTITY_INSERT "+tableName+" on");
				}	
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		super.executeBeforeInsert(entity);
	}

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#executeBeforeInsert(com.idega.data.IDOLegacyEntity)
	 */
	protected void executeAfterInsert(IDOLegacyEntity entity) throws Exception
	{
		try{
			if(((IDOEntityBean)entity).getPrimaryKeyClass().equals(Integer.class)){
				String tableName = entity.getTableName();
				executeUpdate(entity,"set IDENTITY_INSERT "+tableName+" off");
			}
			}
		catch(Exception e){
			e.printStackTrace();
		}
		super.executeAfterInsert(entity);
	}

}

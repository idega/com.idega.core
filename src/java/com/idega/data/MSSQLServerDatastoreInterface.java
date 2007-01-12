/*
 * Created on 28.2.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.idega.util.database.ConnectionBroker;
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
	public MSSQLServerDatastoreInterface(){
		super.useTransactionsInEntityCreation=false;
	}
	
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
				theReturn = "NTEXT";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean"))
		{
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float"))
		{
			theReturn = "REAL";
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
	public void createTrigger(GenericEntity entity) throws Exception
	{
	}
	/**
	 * @param entity
	 * @param conn
	 */
	protected void updateNumberGeneratedValue(GenericEntity entity, Connection conn)
	{
		try
		{
			//if (((GenericEntity) entity).getPrimaryKeyClass().equals(Integer.class))
			//{
			boolean pkIsNull = entity.isNull(entity.getIDColumnName());
			if (pkIsNull)
			{
				//Object value = this.executeQuery(entity, "select @@IDENTITY");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select @@IDENTITY");
				rs.next();
				int id = rs.getInt(1);
				entity.setID(id);
				rs.close();
				stmt.close();
				//String tableName = entity.getTableName();
				//Statement stmt2 = conn.createStatement();
				//stmt2.executeUpdate("set IDENTITY_INSERT " + tableName + " off");
				//stmt2.close();
			}
			//}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * @return boolean
	 */
	protected boolean updateNumberGeneratedValueAfterInsert()
	{
		return true;
	}
	public String getIDColumnType(GenericEntity entity)
	{
		if (entity.getIfAutoIncrement()) {
			return "INTEGER IDENTITY";
		} else {
			return "INTEGER";
		}
	}
	/*protected void executeBeforeInsert(IDOLegacyEntity entity) throws Exception
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				boolean pkIsNull = entity.isNull(entity.getIDColumnName()) && entity.getPrimaryKey() == null;
				if (!pkIsNull)
				{
					String tableName = entity.getTableName();
					executeUpdate(entity, "set IDENTITY_INSERT " + tableName + " on");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.executeBeforeInsert(entity);
	}
	
	protected void executeAfterInsert(IDOLegacyEntity entity) throws Exception
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				String tableName = entity.getTableName();
				executeUpdate(entity, "set IDENTITY_INSERT " + tableName + " off");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.executeAfterInsert(entity);
	}*/
	/**
	 * 
	 * Hacked version of the insert method.
	 * @todo: Implement in a better way.
	 */
	public void insert(GenericEntity entity, Connection conn) throws Exception
	{
		executeBeforeInsert(entity);
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try
		{
			boolean entityInsertModeIsOn = false;
			if (entity.getIfAutoIncrement()) {
				entityInsertModeIsOn = turnOnIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
			}
			else {
				entityInsertModeIsOn = true;
			}
			StringBuffer statement = new StringBuffer("");
			statement.append("insert into ");
			statement.append(entity.getTableName());
			statement.append("(");
			statement.append(getCommaDelimitedColumnNamesForInsert(entity));
			statement.append(") values (");
			statement.append(getQuestionmarksForColumns(entity));
			statement.append(")");
			//if (isDebugActive())
			//	debug(statement.toString());
			String sql = statement.toString();
			logSQL(sql);
			Stmt = conn.prepareStatement(sql);
			setForPreparedStatement(STATEMENT_INSERT, Stmt, entity);
			Stmt.execute();
			Stmt.close();
			
			if (entity.getIfAutoIncrement()) {
				if (updateNumberGeneratedValueAfterInsert())
				{
					updateNumberGeneratedValue(entity, conn);
				}
				turnOffIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
			}
		}
		finally
		{
			if (RS != null)
			{
				RS.close();
			}
			if (Stmt != null)
			{
				Stmt.close();
			}
		}
		executeAfterInsert(entity);
		entity.setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
	private boolean turnOnIdentityInsertFlag(GenericEntity entity, Connection conn, boolean entityInsertModeIsOn)
	{
		try
		{
			if ((entity).getPrimaryKeyClass().equals(Integer.class))
			{
				boolean pkIsNull = entity.isNull(entity.getIDColumnName());
				if (!pkIsNull)
				{
					String tableName = entity.getTableName();
					Statement stmt2 = conn.createStatement();
					String sql = "set IDENTITY_INSERT " + tableName + " on";
					stmt2.executeUpdate(sql);
					//debug(sql);
					stmt2.close();
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	private boolean turnOffIdentityInsertFlag(GenericEntity entity, Connection conn, boolean entityInsertModeIsOn)
	{
		if (entityInsertModeIsOn)
		{
			try
			{
				//if (((GenericEntity) entity).getPrimaryKeyClass().equals(Integer.class))
				//{
				String tableName = entity.getTableName();
				Statement stmt2 = conn.createStatement();
				String sql = "set IDENTITY_INSERT " + tableName + " off";
				stmt2.executeUpdate(sql);
				//debug(sql);
				return false;
				//}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return true;
			}
		}
		return false;
	}
/*
	public void update(GenericEntity entity, Connection conn) throws Exception
	{
		executeBeforeUpdate(entity);
		PreparedStatement Stmt = null;
		try
		{
			boolean entityInsertModeIsOn = false;
			entityInsertModeIsOn = turnOnIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
			conn = entity.getConnection();
			//			Stmt = conn.createStatement();

			String statement =
				"update "
					+ entity.getEntityName()
					+ " set "
					+ entity.getAllColumnsAndQuestionMarks()
					+ " where "
					+ entity.getIDColumnName()
					+ "="
					+ entity.getID();
			//System.out.println(statement);
			Stmt = conn.prepareStatement(statement);
			setForPreparedStatement(STATEMENT_UPDATE, Stmt, entity);
			Stmt.execute();
			if (updateNumberGeneratedValueAfterInsert())
			{
				updateNumberGeneratedValue(entity, conn);
			}
			turnOffIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
			//int i = Stmt.executeUpdate("update "+entity.getEntityName()+" set "+entity.getAllColumnsAndValues()+" where "+entity.getIDColumnName()+"="+entity.getID());
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
		}
		executeAfterUpdate(entity);
		entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
*/
	
	public HashMap getTableIndexes(String dataSourceName, String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		Statement Stmt = null;
		HashMap hm = new HashMap();
		try {
			conn = ConnectionBroker.getConnection(dataSourceName);
			Stmt = conn.createStatement();

			rs = Stmt.executeQuery("select i.name as INDEX_NAME, c.name as COLUMN_NAME from sysobjects o,  sysindexkeys ik, sysindexes i, syscolumns c  where i.indid = ik.indid and ik.id = i.id AND ik.colid = c.colid AND c.id = i.id and i.id = o.id and o.name = '"+tableName.toUpperCase()+"' order by i.name");
			//			Check for upper case
			handleIndexRS(rs, hm);
			rs.close();

			//			Check for lower case
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select i.name as INDEX_NAME, c.name as COLUMN_NAME from sysobjects o,  sysindexkeys ik, sysindexes i, syscolumns c  where i.indid = ik.indid and ik.id = i.id AND ik.colid = c.colid AND c.id = i.id and i.id = o.id and o.name = '"+tableName.toLowerCase()+"' order by i.name");
				handleIndexRS(rs, hm);
				rs.close();
			}

			//			Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select i.name as INDEX_NAME, c.name as COLUMN_NAME from sysobjects o,  sysindexkeys ik, sysindexes i, syscolumns c  where i.indid = ik.indid and ik.id = i.id AND ik.colid = c.colid AND c.id = i.id and i.id = o.id and o.name = '"+tableName+"' order by i.name");
				handleIndexRS(rs, hm);
				rs.close();
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (Stmt != null) {
					Stmt.close();
				}
			} catch (Exception e) {
				logError("Failed to close ResultSet or Statement ("+e.getMessage()+")");
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}

		return hm;
		/*
		 * if(v!=null && !v.isEmpty()) return (String[])v.toArray(new String[0]);
		 * return null;
		 */
	}	
	
	public boolean isCabableOfRSScroll(){
		return true;
	}
	
	/**
	 * returns the optimal or allowed fetch size when going to database to load IDOEntities using 'where primarikey_name in (list_of_priamrykeys)'
	 */
	public int getOptimalEJBLoadFetchSize(){
		return 1000;
	}
	
}

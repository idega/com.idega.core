package com.idega.util.dbschema;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idega.util.ThreadContext;
import com.idega.util.database.PoolManager;

/**
 * 
 * 
 *  Last modified: $Date: 2007/01/12 19:31:31 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1.2.1 $
 */
public class InterbaseSchemaAdapter extends SQLSchemaAdapter
{
	private static String infoKey = "interbase_datastoreinterface_connection_info";
	InterbaseSchemaAdapter()
	{
		this.useTransactionsInSchemaCreation = true;
	}
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
			else if (maxlength <= 30000)
			{
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else
			{
				theReturn = "BLOB";
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
			theReturn = "FLOAT(15)";
		}
		else if (javaClassName.equals("java.sql.Timestamp"))
		{
			theReturn = "TIMESTAMP";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date"))
		{
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob"))
		{
			theReturn = "BLOB";
		}
		else if (javaClassName.equals("java.sql.Time"))
		{
			theReturn = "TIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender"))
		{
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper"))
		{
			theReturn = "BLOB";
		}
		else
		{
			theReturn = "";
		}
		return theReturn;
	}
	
	private String getTriggerName(Schema entity) {
		return entity.getSQLName()+"_trig";
	}
	
	public void createTrigger(Schema entity) throws Exception {
		createTrigger(entity, true);
	}

	public void createTrigger(Schema entity, boolean createGenerator) throws Exception {	
		if (createGenerator) {
			createGenerator(entity);
		}
		String idColumnName = entity.getPrimaryKey().getColumn().getSQLName();
		String s =
			"CREATE TRIGGER "
				+ getTriggerName(entity)
				+" for "
				+ entity.getSQLName()
				+ " ACTIVE BEFORE INSERT POSITION 0 AS BEGIN IF (NEW."
				+ idColumnName
				+ " IS NULL) THEN NEW."
				+ idColumnName
				+ " = GEN_ID("
				+ getInterbaseGeneratorName(entity)
				+ ", 1); END";
		executeUpdate(s);
		
	}
	public void createGenerator(Schema entity) throws Exception
	{
		String s = "CREATE GENERATOR " + getInterbaseGeneratorName(entity);
		executeUpdate(s);
	
	}
	
	public boolean updateTriggers(Schema entity, boolean createIfNot) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		boolean returner = false;
		try {
			conn = getConnection();
			Stmt = conn.createStatement();
			boolean triggerExists = false;
			boolean generatorExists = false;
			String trigSQL = "select * from RDB$TRIGGERS where RDB$TRIGGER_NAME = '"+getTriggerName(entity)+"'";
			try {
				rs = Stmt.executeQuery(trigSQL.toUpperCase());
				if (rs != null && rs.next()) {					
					triggerExists = true;
				}
			} catch (Exception e) {
				log("Error finding trigger table");
			} finally {
				rs.close();
			}
			String genSQL = "select * from RDB$GENERATORS where RDB$GENERATOR_NAME = '"+getInterbaseGeneratorName(entity)+"'";
			try {
				rs = Stmt.executeQuery(genSQL.toUpperCase());	
				if (rs != null && rs.next()) {
					generatorExists = true;
				}
			} catch (Exception e) {
				log("Error finding generator table");
			} finally {
				rs.close();
			}
			
			
			if (generatorExists && triggerExists) {
				returner = true; 
			}
			else if (createIfNot) {
				String maxSQL = "select max ("+entity.getPrimaryKey().getColumn().getSQLName()+") as MAX_ID from "+entity.getSQLName();
				
				if (!triggerExists) {
					createTrigger(entity, !generatorExists);
				}
				int valueToSet = 1;
				try {
					rs = Stmt.executeQuery(maxSQL);
					if (rs != null && rs.next()) {
						String sMax = rs.getString("MAX_ID");
						if (sMax != null) {
							valueToSet = Integer.parseInt(sMax);
						}
					}
					System.out.println("SET GENERATOR "+getInterbaseGeneratorName(entity)+" TO "+valueToSet);
					Stmt.executeUpdate("SET GENERATOR "+getInterbaseGeneratorName(entity)+" TO "+valueToSet);
				}
				catch (NumberFormatException e) {
					//UPDATE TRIGGER ignored
					//Not numeric value in primary key field in table "+entity.getEntityName());
				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
				returner = true;
			}
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return returner;
	}

	
	protected void deleteTrigger(Schema entity) throws Exception
	{
		String s = "delete from RDB$TRIGGERS where RDB$TRIGGER_NAME='" + entity.getSQLName() + "_trig" + "'";
		executeUpdate(s);
		
	}
	public void removeSchema(Schema entity) throws Exception
	{
		deleteGenerator(entity);
		super.removeSchema(entity);
	}
	protected void deleteGenerator(Schema entity) throws Exception
	{
		String s = "delete from RDB$GENERATORS WHERE RDB$GENERATOR_NAME='" + getInterbaseGeneratorName(entity) + "'";
		executeUpdate(s);
		
	}
	public boolean isConnectionOK(Connection conn)
	{
		Statement testStmt = null;
		ResultSet RS = null;
		try
		{
			if (!conn.isClosed())
			{
				// Try to createStatement to see if it's really alive
				testStmt = conn.createStatement();
				testStmt.close();
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			if (testStmt != null)
			{
				if (RS != null)
				{
					try
					{
						RS.close();
					}
					catch (SQLException se)
					{
					}
				}
				try
				{
					testStmt.close();
				}
				catch (SQLException se)
				{
				}
			}
			return false;
		}
		return true;
	}
	protected String getCreateUniqueIDQuery(Schema entity)
	{
		return "SELECT GEN_ID(" + getInterbaseGeneratorName(entity) + ", 1) FROM RDB$DATABASE";
	}
	
	
	protected void createForeignKey( String baseTableName, String columnName, String refrencingTableName, String referencingColumnName) throws Exception {
		try {
			super.createForeignKey( baseTableName, columnName, refrencingTableName, referencingColumnName);
	  }
	  catch(Exception e){
	  	log("IDOTableCreator : Error caught trying to createForeignKey in for table "+baseTableName+" in InterbaseDatastoreInterface ("+e.getMessage()+")");
	  }
	}
	
	private String getInterbaseGeneratorName(Schema entity)
	{
		String entityName = entity.getSQLName();
		if (entityName.endsWith("_"))
		{
			return (entityName + "gen").toUpperCase();
		}
		else
		{
			return (entityName + "_gen").toUpperCase();
		}
	}
	/**
	 * Interbase workaraound because only one connection can be to the database when altering tables
	 */
	public void executeBeforeCreateEntityRecord(String dataSource,Schema entity) throws Exception
	{
		String datasource = dataSource;
		InterbaseConnectionInfo info = new InterbaseConnectionInfo();
		PoolManager pmgr = PoolManager.getInstance();
		int size = pmgr.getCurrentConnectionCount(datasource);
		int min = pmgr.getMinimumConnectionCount(datasource);
		int max = pmgr.getMaximumConnectionCount(datasource);
		pmgr.trimTo(datasource, 1, 1, 1);
		info.size = size;
		info.min = min;
		info.max = max;
		info.datasource = datasource;
		ThreadContext.getInstance().setAttribute(infoKey, info);
		//System.out.println();
		//System.out.println("ConnectionPool trimmed and datasource "+datasource+" contains "+pmgr.getCurrentConnectionCount(datasource)+" connections");
		//System.out.println();
	}
	/**
	 * Interbase workaraound because only one connection can be to the database when altering tables
	 */
	public void executeAfterSchemaCreation(Schema entity) throws Exception
	{
		String datasource = getDataSourceName();
		PoolManager pmgr = PoolManager.getInstance();
		InterbaseConnectionInfo info = (InterbaseConnectionInfo) ThreadContext.getInstance().getAttribute(infoKey);
		int size = info.size;
		int min = info.min;
		int max = info.max;
		//pmgr.trimTo(datasource,1,1,1);
		pmgr.enlargeTo(datasource, size, min, max);
		//System.out.println();
		//System.out.println("ConnectionPool enlarged and datasource "+datasource+" contains "+pmgr.getCurrentConnectionCount(datasource)+" connections");
		//System.out.println();
		ThreadContext.getInstance().removeAttribute(infoKey);
	}
	private class InterbaseConnectionInfo
	{
		public String datasource;
		public int size;
		public int min;
		public int max;
	}
	public void setNumberGeneratorValue( Schema entity, int value)
	{
		//throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());
		String statement = "set generator " + this.getInterbaseGeneratorName(entity) + " to " + value;
		try
		{
			this.executeUpdate( statement);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#doesTableExist(java.lang.String, java.lang.String)
	 */
	public boolean doesTableExist( String tableName) throws Exception {
		try{
	 
		  StringBuffer query = new StringBuffer("SELECT COUNT(RDB$RELATION_NAME) ");
		  query.append("	FROM RDB$RELATIONS where RDB$RELATION_NAME = '");
		  query.append(tableName.toUpperCase());
		  query.append("'");
		  //System.err.println(query.toString());
		  //int count = entity.getNumberOfRecords(query.toString());
		  //System.err.println("count was "+count);
		  Integer obj = new Integer(executeQuery(query.toString()).toString());
		  return obj.intValue() > 0;
	  }
	  catch(Exception e){
		  //e.printStackTrace();
		  return false;
	  }
			
	}

	
}

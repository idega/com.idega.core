/*
 * $Id: InterbaseDatastoreInterface.java,v 1.35 2006/02/20 11:04:34 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idega.util.ThreadContext;
import com.idega.util.database.PoolManager;

/**
 * Title:        InterbaseDatastoreInterface
 * Description:  A class to handle Interbase/Firebird specific jdbc implementations.
 * Copyright:  (C) 2000-2002 idega software All Rights Reserved.
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0  
 */
public class InterbaseDatastoreInterface extends DatastoreInterface
{
	private static String infoKey = "interbase_datastoreinterface_connection_info";
	InterbaseDatastoreInterface()
	{
		useTransactionsInEntityCreation = false;
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
	
	private String getTriggerName(GenericEntity entity) {
		return entity.getTableName()+"_trig";
	}
	
	public void createTrigger(GenericEntity entity) throws Exception {
		createTrigger(entity, true);
	}

	public void createTrigger(GenericEntity entity, boolean createGenerator) throws Exception {	
		if (createGenerator) {
			createGenerator(entity);
		}
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			String s =
				"CREATE TRIGGER "
					+ getTriggerName(entity)
					+" for "
					+ entity.getTableName()
					+ " ACTIVE BEFORE INSERT POSITION 0 AS BEGIN IF (NEW."
					+ entity.getIDColumnName()
					+ " IS NULL) THEN NEW."
					+ entity.getIDColumnName()
					+ " = GEN_ID("
					+ getInterbaseGeneratorName(entity)
					+ ", 1); END";
			System.out.println(s);
			Stmt.executeUpdate(s);
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				entity.freeConnection(conn);
			}
		}
	}
	public void createGenerator(GenericEntity entity) throws Exception
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			String s = "CREATE GENERATOR " + getInterbaseGeneratorName(entity);
			System.out.println(s);
			Stmt.executeUpdate(s);
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				entity.freeConnection(conn);
			}
		}
	}
	
	public boolean updateTriggers(GenericEntity entity, boolean createIfNot) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		boolean returner = false;
		try {
			conn = entity.getConnection();
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
				String maxSQL = "select max ("+entity.getIDColumnName()+") as MAX_ID from "+entity.getEntityName();
				
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
				entity.freeConnection(conn);
			}
		}
		return returner;
	}

	/* public void createForeignKeys(IDOLegacyEntity entity) throws Exception {
	   Connection conn = null;
	   Statement Stmt = null;
	   try {
	     conn = entity.getConnection();
	     conn.commit();
	     String[] names = entity.getColumnNames();
	     for (int i = 0; i < names.length; i++) {
	       if (!entity.getRelationShipClassName(names[i]).equals("")) {
	         Stmt = conn.createStatement();
	         int n = Stmt.executeUpdate("ALTER TABLE " + entity.getTableName() + " ADD FOREIGN KEY (" + names[i] + ") REFERENCES " + ((IDOLegacyEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName() + " ");
	         if (Stmt != null) {
	           Stmt.close();
	         }
	       }
	     }
	   }
	   finally {
	     if (Stmt != null) {
	       Stmt.close();
	     }
	     if (conn != null) {
	       entity.freeConnection(conn);
	     }
	   }
	 }*/
	protected void deleteTrigger(GenericEntity entity) throws Exception
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from RDB$TRIGGERS where RDB$TRIGGER_NAME='" + entity.getTableName() + "_trig" + "'");
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				entity.freeConnection(conn);
			}
		}
	}
	public void deleteEntityRecord(GenericEntity entity) throws Exception
	{
		deleteGenerator(entity);
		super.deleteEntityRecord(entity);
	}
	protected void deleteGenerator(GenericEntity entity) throws Exception
	{
		Connection conn = null;
		Statement Stmt = null;
		try
		{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from RDB$GENERATORS WHERE RDB$GENERATOR_NAME='" + getInterbaseGeneratorName(entity) + "'");
		}
		finally
		{
			if (Stmt != null)
			{
				Stmt.close();
			}
			if (conn != null)
			{
				entity.freeConnection(conn);
			}
		}
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
	protected String getCreateUniqueIDQuery(GenericEntity entity)
	{
		return "SELECT GEN_ID(" + getInterbaseGeneratorName(entity) + ", 1) FROM RDB$DATABASE";
	}
	protected void executeBeforeInsert(GenericEntity entity) throws Exception
	{
		if (entity.isNull(entity.getIDColumnName()))
		{
			entity.setID(createUniqueID(entity));
		}
	}
	/*
	  protected void insertBlob(IDOLegacyEntity entity)throws Exception{
	    String statement ;
	    Connection Conn = null;
	    try{
	      statement = "update " + entity.getTableName() + " set " + entity.getLobColumnName() + "=? where " + entity.getIDColumnName() + " = " + entity.getID();
	      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
	      if(wrapper!=null){
	        //Conn.setAutoCommit(false);
	        InputStream instream = wrapper.getInputStreamForBlobWrite();
	        if(instream!=null){
	          Conn = entity.getConnection();
	          if(Conn== null) return;
	          //BufferedInputStream bin = new BufferedInputStream(instream);
	          PreparedStatement PS = Conn.prepareStatement(statement);
	          //PS.setBinaryStream(1, bin, bin.available() );
	          PS.setBinaryStream(1, instream, instream.available() );
	          PS.execute();
	          PS.close();
	          instream.close();
	        }
	        //Conn.commit();
	        //Conn.setAutoCommit(true);
	      }
	    }
	    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error uploading blob to db for "+entity.getClass().getName());}
	    catch(Exception ex){ex.printStackTrace();}
	    finally{
	      if(Conn != null) entity.freeConnection(Conn);
	    }
	  }
	*/
	protected void createForeignKey(GenericEntity entity, String baseTableName, String columnName, String refrencingTableName, String referencingColumnName) throws Exception {
		try {
			super.createForeignKey(entity, baseTableName, columnName, refrencingTableName, referencingColumnName);
	  }
	  catch(Exception e){
	  	log("IDOTableCreator : Error caught trying to createForeignKey in for table "+baseTableName+" in InterbaseDatastoreInterface ("+e.getMessage()+")");
	  }
	}
	
	private static String getInterbaseGeneratorName(GenericEntity entity)
	{
		String entityName = entity.getTableName();
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
	public void executeBeforeCreateEntityRecord(GenericEntity entity) throws Exception
	{
		String datasource = entity.getDatasource();
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
	public void executeAfterCreateEntityRecord(GenericEntity entity) throws Exception
	{
		String datasource = entity.getDatasource();
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
	public void setNumberGeneratorValue(GenericEntity entity, int value)
	{
		//throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());
		String statement = "set generator " + InterbaseDatastoreInterface.getInterbaseGeneratorName(entity) + " to " + value;
		try
		{
			this.executeUpdate(entity, statement);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#doesTableExist(java.lang.String, java.lang.String)
	 */
	public boolean doesTableExist(String dataSourceName, String tableName) throws Exception {
		try{
	 
		  StringBuffer query = new StringBuffer("SELECT COUNT(RDB$RELATION_NAME) ");
		  query.append("	FROM RDB$RELATIONS where RDB$RELATION_NAME = '");
		  query.append(tableName.toUpperCase());
		  query.append("'");
		  //System.err.println(query.toString());
		  //int count = entity.getNumberOfRecords(query.toString());
		  //System.err.println("count was "+count);
		  Integer obj = new Integer(executeQuery(dataSourceName,query.toString()).toString());
		  return obj.intValue() > 0;
	  }
	  catch(Exception e){
		  //e.printStackTrace();
		  return false;
	  }
			
	}

	
}

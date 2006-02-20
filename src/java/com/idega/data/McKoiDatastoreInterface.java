//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000-2002 idega.is All Rights Reserved.
*/
package com.idega.data;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.idega.util.IWTimestamp;
import com.idega.util.database.ConnectionBroker;

/**
*A class for database abstraction for the McKoi Database.
* This is an implemention that ovverrides implementations from com.idega.data.DatastoreInterface 
* and performs specific functionality to the Oracle JDBC driver and database.
* Copyright 2003 idega software All Rights Reserved.
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class McKoiDatastoreInterface extends DatastoreInterface {
	
	protected McKoiDatastoreInterface(){
		super.useTransactionsInEntityCreation=false;
	}

	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "NUMERIC";
		}
		else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 1000000000) {
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else {
				theReturn = "CLOB";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "FLOAT(15)";
		}
		else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BLOB";
		}
		else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "TIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "BLOB";
		}
		else {
			theReturn = "";
		}
		return theReturn;
	}
	public void createTrigger(GenericEntity entity) throws Exception {
		createSequence(entity);
		/*Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate(
					"CREATE TRIGGER "
						+ entity.getTableName()
						+ "_trig BEFORE INSERT ON "
						+ entity.getTableName()
						+ " FOR EACH ROW WHEN (NEW."
						+ entity.getIDColumnName()
						+ " is null) DECLARE TEMP INTEGER; BEGIN SELECT "
						+ entity.getTableName()
						+ "_seq.NEXTVAL INTO TEMP FROM DUAL; :NEW."
						+ entity.getIDColumnName()
						+ ":=TEMP;END;");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}*/
	}
	
	
	
	public void createSequence(GenericEntity entity) throws Exception {
		createSequence(entity, 0);
	}
	public void createSequence(GenericEntity entity, int startNumber) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			String seqCreate =
				"CREATE SEQUENCE "
					+ getSequenceName(entity)
					+ " START "
					+ startNumber
					+ " CACHE 20";
			Stmt.executeUpdate(seqCreate);
			System.out.println(seqCreate);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	
	/*
	public void createSequence(IDOLegacyEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i =
				Stmt.executeUpdate(
					"create sequence "
						+ entity.getTableName()
						+ "_seq INCREMENT BY 1 START WITH 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE CACHE 20 NOORDER");
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
	public void deleteEntityRecord(GenericEntity entity) throws Exception {
		super.deleteEntityRecord(entity);
		deleteTrigger(entity);
		deleteSequence(entity);
	}
	protected void deleteTrigger(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop trigger " + entity.getTableName() + "_trig");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	protected void deleteSequence(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop sequence " + getSequenceName(entity));
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	/*public void createForeignKeys(IDOLegacyEntity entity)throws Exception{
			Connection conn= null;
			Statement Stmt= null;
			try{
				conn = entity.getConnection();
	 		        String[] names = entity.getColumnNames();
			        for (int i = 0; i < names.length; i++){
	                        if (!entity.getRelationShipClassName(names[i]).equals("")){
	                          Stmt = conn.createStatement();
	                          String statement = "ALTER TABLE "+entity.getTableName()+" ADD FOREIGN KEY ("+names[i]+") REFERENCES "+((IDOLegacyEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName()+" ";
	                          System.out.println(statement);
				    int n = Stmt.executeUpdate(statement);
	                  	    if(Stmt != null){
					Stmt.close();
				    }
	                        }
	                      }	
			}
			finally{
				if(Stmt != null){
					Stmt.close();
				}
				if (conn != null){
					entity.freeConnection(conn);
				}
			}
	}*/
	protected void executeBeforeInsert(GenericEntity entity) throws Exception {
		if (entity.isNull(entity.getIDColumnName())) {
			entity.setID(createUniqueID(entity));
		}
	}
	
	/*
	protected void insertBlob(IDOLegacyEntity entity) throws Exception {
		Connection Conn = null;
		BLOB blob;
		try {
			Conn = entity.getConnection();
			if (Conn == null)
				return;
			//Conn.setAutoCommit(false);
			Statement stmt2 = Conn.createStatement();
			String cmd =
				"SELECT "
					+ entity.getLobColumnName()
					+ " FROM "
					+ entity.getEntityName()
					+ " WHERE "
					+ entity.getIDColumnName()
					+ " ='"
					+ entity.getPrimaryKey()
					+ "' FOR UPDATE ";
			ResultSet RS2 = stmt2.executeQuery(cmd);
			RS2.next();
			//insert into empty_blob()
			
			blob = ((OracleResultSet) RS2).getBLOB(1);
			// write the array of binary data to a BLOB
			OutputStream outstream = blob.getBinaryOutputStream();
			int size = blob.getBufferSize();
			byte[] buffer = new byte[size];
			int length = -1;
			BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
			if (wrapper != null) {
				BufferedInputStream in = new BufferedInputStream(wrapper.getInputStreamForBlobWrite());
				while ((length = in.read(buffer)) != -1)
					outstream.write(buffer, 0, length);
				in.close();
			}
			outstream.flush();
			outstream.close();
			stmt2.close();
			RS2.close();
			//Conn.commit();
			//Conn.setAutoCommit(true);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("error saving to db");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if (Conn != null)
				entity.freeConnection(Conn);
		}
	}
	
	
	*/
	protected String getCreateUniqueIDQuery(GenericEntity entity) {
		return "SELECT NEXTVAL('" + getSequenceName(entity) + "')";
	}

	private static String getSequenceName(GenericEntity entity) {
		String entityName = entity.getTableName();
		String theReturn = entityName + "_SEQ";
		return theReturn.toUpperCase();
		/*if (entityName.endsWith("_")){
		return entityName+"seq";
		}
		else{
		return entityName+"_seq";
		}*/
	}


	public void setNumberGeneratorValue(GenericEntity entity, int value) {
		//throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());
		//String statement = "update sequences set last_number="+value+" where sequence_name='"+this.getSequenceName(entity)+"'";
		String statement = "drop sequence " + McKoiDatastoreInterface.getSequenceName(entity);
		try {
			this.executeUpdate(entity, statement);
			this.createSequence(entity, value + 1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#getTableColumnNames(java.lang.String, java.lang.String)
	 */
	public String[] getTableColumnNames(String dataSourceName, String tableName) {
		
		Connection conn = null;
		Statement stmnt = null;
		ResultSet rs = null;
		java.util.Vector v = new java.util.Vector();
		try{
		  conn = ConnectionBroker.getConnection(dataSourceName);
		
		  stmnt = conn.createStatement();
		  rs = stmnt.executeQuery("DESCRIBE "+tableName);
		  while (rs.next()) {
		  	 String column = rs.getString("NAME");
			 v.add(column);
			 //System.out.println("\t\t"+column);
		 }
		 rs.close();
		 return (String[])v.toArray(new String[0]);
		 }
		catch(SQLException e){
		   e.printStackTrace();
		 }
		finally{
		   if(conn!=null){
			 ConnectionBroker.freeConnection(conn);
		   }
		}
		if(v!=null && !v.isEmpty())
			return (String[])v.toArray(new String[0]);
		return null;
			   
	}

	public boolean useIndexes() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#format(java.sql.Date)
	 */
	public String format(Date date) {
		IWTimestamp stamp = new IWTimestamp(date);
		return " {d '"+(stamp.toSQLString())+"'} ";
	}

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#format(java.sql.Timestamp)
	 */
	public String format(Timestamp timestamp) {
		IWTimestamp stamp = new IWTimestamp(timestamp);
		return " {d '"+(stamp.toSQLString())+"'} ";
	}

}

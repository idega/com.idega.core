package com.idega.util.dbschema;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.idega.data.EntityAttribute;
import com.idega.util.IWTimestamp;

/**
 * 
 * 
 *  Last modified: $Date: 2006/05/26 16:51:49 $ by $Author: thomas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public class McKoiSchemaAdapter extends SQLSchemaAdapter {
	
	protected McKoiSchemaAdapter(){
		super.useTransactionsInSchemaCreation=false;
	}

	public String getSQLType(String javaClassName, int maxlength) {
		if (javaClassName.equals("java.lang.Integer")) {
			return "NUMERIC";
		}
		if (javaClassName.equals("java.lang.String")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "CLOB";
			}
			if (maxlength < 0) {
				return "VARCHAR(255)";
			}
			if (maxlength <= 1000000000) {
				return "VARCHAR(" + maxlength + ")";
			}
			return "CLOB";
		}
		if (javaClassName.equals("java.lang.Boolean")) {
			return "CHAR(1)";
		}
		if (javaClassName.equals("java.lang.Float")) {
			return "FLOAT";
		}
		if (javaClassName.equals("java.lang.Double")) {
			return  "FLOAT(15)";
		}
		if (javaClassName.equals("java.sql.Timestamp")) {
			return "DATE";
		}
		if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			return "DATE";
		}
		if (javaClassName.equals("java.sql.Blob")) {
			return "BLOB";
		}
		if (javaClassName.equals("java.sql.Time")) {
			return "TIME";
		}
		if (javaClassName.equals("com.idega.util.Gender")) {
			return "VARCHAR(1)";
		}
		if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			return "BLOB";
		}
		return "";
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.data.store.DatastoreInterface#createTrigger(java.lang.String, com.idega.data.IDOEntityDefinition)
	 */
	public void createTrigger(Schema entityDefinition) throws Exception {
		createSequence(  entityDefinition);

	}
	
	public void createSequence(Schema entityDefinition) throws Exception {
		createSequence(entityDefinition, 0);
	}
	public void createSequence(Schema entityDefinition, int startNumber) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection();
			Stmt = conn.createStatement();
			String seqCreate =
				"CREATE SEQUENCE "
					+ getSequenceName(entityDefinition)
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
				freeConnection(conn);
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
	/* (non-Javadoc)
	 * @see com.idega.data.store.DatastoreInterface#deleteEntityRecord(java.lang.String, com.idega.data.EntityDefinition)
	 */
	public void removeSchema(Schema entity) throws Exception {
		super.removeSchema(entity);
		deleteTrigger(entity);
		deleteSequence(entity);
	}
	protected void deleteTrigger(Schema entity) throws Exception {
		executeUpdate("drop trigger " + entity.getSQLName() + "_trig");
	}
	protected void deleteSequence(Schema entity) throws Exception {
		executeUpdate("drop sequence " + getSequenceName(entity));
	}
	
	protected String getCreateUniqueIDQuery(Schema entity) {
		return "SELECT NEXTVAL('" + getSequenceName(entity) + "')";
	}

	private String getSequenceName(Schema entity) {
		String entityName = entity.getSQLName();
		String theReturn = entityName + "_SEQ";
		return theReturn.toUpperCase();
		/*if (entityName.endsWith("_")){
		return entityName+"seq";
		}
		else{
		return entityName+"_seq";
		}*/
	}


	public void setNumberGeneratorValue(Schema entity, int value) {
		//throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());
		//String statement = "update sequences set last_number="+value+" where sequence_name='"+this.getSequenceName(entity)+"'";
		String statement = "drop sequence " + this.getSequenceName(entity);
		try {
			this.executeUpdate( statement);
			this.createSequence(entity, value + 1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#getTableColumnNames(java.lang.String, java.lang.String)
	 */
	public String[] getTableColumnNames( String tableName) {
		
		Connection conn = null;
		Statement stmnt = null;
		ResultSet rs = null;
		java.util.Vector v = new java.util.Vector();
		try{
		  conn = getConnection();
		
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
			 freeConnection(conn);
		   }
		}
		if(v!=null && !v.isEmpty()) {
			return (String[])v.toArray(new String[0]);
		}
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

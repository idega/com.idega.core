package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class OracleSchemaAdapter extends SQLSchemaAdapter {

	protected OracleSchemaAdapter() {
		super();
	}

	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "NUMBER";
		} else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR2(255)";
			} else if (maxlength <= 4000) {
				theReturn = "VARCHAR2(" + maxlength + ")";
			} else {
				theReturn = "CLOB";
			}
		} else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		} else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "FLOAT";
		} else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "FLOAT(15)";
		} else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "DATE";
		} else if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		} else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BLOB";
		} else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "TIME";
		} else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		} else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "BLOB";
		} else {
			theReturn = "";
		}
		return theReturn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.store.DatastoreInterface#createTrigger(java.lang.String,
	 *      com.idega.data.IDOEntityDefinition)
	 */
	public void createTrigger(Schema entity) throws Exception {
		createSequence(entity);
		String s = "CREATE TRIGGER " + entity.getSQLName()
				+ "_trig BEFORE INSERT ON " + entity.getSQLName()
				+ " FOR EACH ROW WHEN (NEW."
				+ entity.getPrimaryKey().getColumn().getSQLName()
				+ " is null) DECLARE TEMP INTEGER; BEGIN SELECT "
				+ entity.getSQLName()
				+ "_seq.NEXTVAL INTO TEMP FROM DUAL; :NEW."
				+ entity.getPrimaryKey().getColumn().getSQLName()
				+ ":=TEMP;END;";
		executeUpdate(s);

	}

	/*
	 * Not Tested public boolean updateTriggers(GenericEntity entity, boolean
	 * createIfNot) throws Exception { Connection conn = null; Statement Stmt =
	 * null; ResultSet rs = null; ResultSet rs2 = null; boolean returner =
	 * false; try { conn = entity.getConnection(); Stmt =
	 * conn.createStatement(); String seqSQL = "select LAST_NUMBER from
	 * user_sequences where SEQUENCE_NAME like '" + entity.getTableName() + "'";
	 * rs = Stmt.executeQuery(seqSQL); if (rs != null && rs.next()) { returner =
	 * true; } if (!returner && createIfNot) { String maxSQL = "select max
	 * ("+entity.getIDColumnName()+" as MAX from "+entity.getEntityName(); int
	 * valueToSet = 1; rs2 = Stmt.executeQuery(maxSQL); if (rs2 != null &&
	 * rs2.next()) { valueToSet = Integer.parseInt(rs2.getString("MAX")); }
	 * 
	 * createSequence(entity, valueToSet); returner = true; } } finally { if
	 * (Stmt != null) { Stmt.close(); } if (rs != null) { rs.close(); } if (rs2 !=
	 * null) { rs2.close(); } if (conn != null) { entity.freeConnection(conn); } }
	 * return returner; }
	 */

	public void createSequence(Schema entity) throws Exception {
		createSequence(entity, 1);
	}

	public void createSequence(Schema entity, int startNumber)
			throws Exception {
		String seqCreate = "create sequence " + entity.getSQLName()
				+ "_seq INCREMENT BY 1 START WITH " + startNumber
				+ " MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";
		executeUpdate(seqCreate);

	}

	public void removeSchema(Schema entity) throws Exception {
		super.removeSchema(entity);
		deleteTrigger(entity);
		deleteSequence(entity);
	}

	protected void deleteTrigger(Schema entity) throws Exception {
		executeUpdate("drop trigger " + entity.getSQLName() + "_trig");
	}

	protected void deleteSequence(Schema entity) throws Exception {
		executeUpdate("drop sequence " + entity.getSQLName() + "_seq");

	}

	protected String getCreateUniqueIDQuery(Schema entity) {
		return "SELECT " + getOracleSequenceName(entity) + ".nextval FROM dual";
	}

	private String getSequenceName(Schema entity) {
		return getOracleSequenceName(entity);
	}

	private static String getOracleSequenceName(Schema entity) {
		String entityName = entity.getSQLName();
		return entityName + "_seq";
	}

	public void setNumberGeneratorValue(Schema entity, int value) {
		String statement = "drop sequence " + this.getSequenceName(entity);
		try {
			this.executeUpdate(statement);
			this.createSequence(entity, value + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Override in subclasses
	 */
	public void onConnectionCreate(Connection newConn) {
		try {
			Statement stmt = newConn.createStatement();
			stmt
					.execute("ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
			stmt.close();
			stmt = newConn.createStatement();
			stmt
					.execute("ALTER SESSION SET NLS_TIMESTAMP_FORMAT='YYYY-MM-DD HH24:MI:SS'");
			stmt.close();
			System.out
					.println("OracleDatastoreInterface: Setting date format environment variable for Oracle.");
			/*
			 * This parameter is set for the OCI driver in a shell script
			 * usually but could be set here also stmt =
			 * newConn.createStatement(); stmt.execute("ALTER SESSION SET
			 * NLS_LANG='.AL32UTF8'"); stmt.close();
			 * System.out.println("OracleDatastoreInterface: Setting language
			 * environment variable for Oracle to NLS_LANG=.UTF8 for Unicode
			 * support.");
			 */
		} catch (SQLException sqle) {
			System.err
					.println("OracleDatastoreInterface: Error when changing environment variable: "
							+ sqle.getMessage());
			sqle.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.DatastoreInterface#getTableColumnNames(java.lang.String,
	 *      java.lang.String)
	 */
	public String[] getTableColumnNames(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List columns = new Vector();
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"
							+ tableName.toUpperCase() + "'");
			while (rs.next()) {
				columns.add(rs.getString("COLUMN_NAME"));
			}
			rs.close();
			return (String[]) columns.toArray(new String[0]);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				freeConnection(conn);
			}
		}
		if (columns != null && !columns.isEmpty())
			return (String[]) columns.toArray(new String[0]);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.DatastoreInterface#doesTableExist(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean doesTableExist(String tableName) throws Exception {
		String checkQuery = "select count(*) from " + tableName;
		try {
			executeQuery(checkQuery);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}

	public Index[] getTableIndexes(String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		Statement Stmt = null;
		HashMap hm = new HashMap();
		try {
			conn = getConnection();
			Stmt = conn.createStatement();

			rs = Stmt
					.executeQuery("select * from user_ind_columns where TABLE_NAME = '"
							+ tableName.toUpperCase() + "'");
			//			Check for upper case
			handleIndexRS(rs, hm);
			rs.close();

			//			Check for lower case
			if (hm.isEmpty()) {
				rs = Stmt
						.executeQuery("select * from user_ind_columns where TABLE_NAME = '"
								+ tableName.toLowerCase() + "'");
				handleIndexRS(rs, hm);
				rs.close();
			}

			//			Check without any case manipulating, this can be removed if we
			// always
			// force uppercase
			if (hm.isEmpty()) {
				rs = Stmt
						.executeQuery("select * from user_ind_columns where TABLE_NAME = '"
								+ tableName + "'");
				handleIndexRS(rs, hm);
				rs.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (Stmt != null) {
					Stmt.close();
				}
			} catch (Exception e) {
				logError("Failed to close ResultSet or Statement ("
						+ e.getMessage() + ")");
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		Index[] defs = new Index[hm.size()];
		Iterator iter = hm.entrySet().iterator();
		int j = 0;
		for (; iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            IndexImpl index = new IndexImpl(entry.getKey().toString(),tableName);
            String[] cols = (String[]) entry.getValue();
            for (int i = 0; i < cols.length; i++) {
                index.addField(cols[i]);
            }
            defs[j++]=index;
        }
		
		return defs;
		/*
		 * if(v!=null && !v.isEmpty()) return (String[])v.toArray(new
		 * String[0]); return null;
		 */
	}

	public boolean isCabableOfRSScroll() {
		return true;
	}

	/**
	 * returns the optimal or allowed fetch size when going to database to load
	 * IDOEntities using 'where primarikey_name in (list_of_priamrykeys)'
	 */
	public int getOptimalEJBLoadFetchSize() {
		return 1000;
	}

}
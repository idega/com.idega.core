package com.idega.util.dbschema;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class MSSQLServerSchemaAdapter extends SQLSchemaAdapter
{
	public MSSQLServerSchemaAdapter(){
		super.useTransactionsInSchemaCreation=false;
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
	 * @see com.idega.data.store.DatastoreInterface#createTrigger(java.lang.String, com.idega.data.EntityDefinition)
	 */
	public void createTrigger(Schema entityDefinition) throws Exception {
		
	}

	public String getIDColumnType(Schema entity)
	{
		if (entity.hasAutoIncrementColumn()) {
			return "INTEGER IDENTITY";
		} else {
			return "INTEGER";
		}
	}
	
	

	
	public Index[] getTableIndexes( String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		Statement Stmt = null;
		HashMap hm = new HashMap();
		try {
			conn = getConnection();
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

/*
 * Created on Sep 10, 2003
 *
 */
package com.idega.util;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.idega.util.database.ConnectionBroker;
/**
 * DataDumper dumps data extracted from database to a file. 
 * @author aron 
 * @version 1.0
 */
public class SQLDataDumper {
	private String datasource = "default";
	private String sql = null;
	private String dumpFolder = null;
	private String delimiter = ",";
	private String dumpFile = null;
	private int type = TYPE_CSV;
	public final static int TYPE_CSV = 1;
	public final static int TYPE_SQL_INSERT = 2;
	public final static int TYPE_DELIMITED = 3;
	public final static int TYPE_SQL_UPDATE = 4;
	public SQLDataDumper() {
	}
	public void setDumpFolder(String url) {
		this.dumpFolder = url;
	}
	public void setDumpFile(String file) {
		this.dumpFile = file;
	}
	public void setQuery(String sql) {
		this.sql = sql;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * Dumps the result from the given query to a file with the specified format
	 * @return
	 * @throws Exception
	 */
	public File dump() throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		File file = null;
		try {
			conn = ConnectionBroker.getConnection(datasource);
			Stmt = conn.createStatement();
			rs = Stmt.executeQuery(sql);
			file = writeToFile(rs);
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}
		return file;
	}
	/**
	 * Writes the rowset to the specified file of the specified type
	 * @param rs
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private File writeToFile(ResultSet rs) throws IOException, SQLException {
		
		File file = FileUtil.getFileAndCreateIfNotExists(dumpFolder,dumpFile);
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		switch (type) {
			case TYPE_CSV :
				writeCSV(rs, writer);
				break;
			case TYPE_SQL_INSERT :
				writeSQLInsert(rs, writer);
				break;
			case TYPE_DELIMITED :
				writeDelimited(rs, writer);
				break;
			case TYPE_SQL_UPDATE:
				writeSQLUpdates(rs,writer);
				break;
			default :
			writeCSV(rs, writer);
		}
		writer.flush();
		return file;
	}
	/**
	 * Writes a rowset
	 * @param rs
	 * @param writer
	 * @throws SQLException
	 */
	private void writeDelimited(ResultSet rs, PrintWriter writer) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount() + 1;
		StringBuffer line;
		String item;
		if (rs != null && rs.next()) {
			line = new StringBuffer();
			for (int i = 1; i < columns; i++) {
				item = rs.getString(i);
				if (i != 1)
					line.append(delimiter);
				if (item != null)
					line.append(item);
			}
			writer.println(line.toString());
		}
	}
	/**
	 * Writes a rowset out in comma delimited format
	 * @param rs
	 * @param writer
	 * @throws SQLException
	 */
	private void writeCSV(ResultSet rs, PrintWriter writer) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount() + 1;
		StringBuffer line =new StringBuffer();
		String item;
		for(int i = 1; i < columns; i++){
			if(i != 1)
				line.append(", ");
			line.append(meta.getColumnName(i));
		}
		writer.println(line.toString());
		while (rs.next()) {
			line = new StringBuffer();
			for (int i = 1; i < columns; i++) {
				item = rs.getString(i);
				if (i != 1)
					line.append(", ");
				if (item != null)
					line.append(item);
			}
			writer.println(line.toString());
		}
	}
	
	/**
	 * Writes a rowset as insert statements 
	 * @param rs
	 * @param writer
	 * @throws SQLException
	 */
	private void writeSQLInsert(ResultSet rs, PrintWriter writer) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount() + 1;
		StringBuffer insertInto = new StringBuffer("INSERT INTO ");
		insertInto.append(meta.getTableName(1));
		insertInto.append("(");
		for(int i = 1; i < columns; i++){
			if(i != 1)
				insertInto.append(", ");
			insertInto.append(meta.getColumnName(i));
		}
		insertInto.append(")");
		insertInto.append(" VALUES (");
		StringBuffer line;
		String item;
		while (rs.next()) {				
			line = new StringBuffer(insertInto.toString());
			for (int i = 1; i < columns; i++) {
				item = rs.getString(i);
				if (i != 1)
					line.append(", ");
				if(item!=null){
					String className = meta.getColumnClassName(i);
					if(className.equalsIgnoreCase(Integer.class.getName()))
						line.append(item);
					else if(className.equalsIgnoreCase(Long.class.getName()))
						line.append(item);
					else
						line.append("'").append(item).append("'");
				}
				else{
					line.append(item);
				}
				
			}
			line.append(")");
			writer.println(line.toString());
		}
	}
	/**
	 * Writes a rowset as insert statements 
	 * @param rs
	 * @param writer
	 * @throws SQLException
	 */
	private void writeSQLUpdates(ResultSet rs, PrintWriter writer) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columns = meta.getColumnCount() + 1;
		StringBuffer insertInto = new StringBuffer("UPDATE ");
		insertInto.append(meta.getTableName(1));
		insertInto.append(" SET ");
		
		StringBuffer line;
		String item, firstValue = null;
		while (rs.next()) {				
			line = new StringBuffer(insertInto.toString());
			for (int i = 1; i < columns; i++) {
				item = rs.getString(i);
				if (i != 1)
					line.append(", ");
				else
					firstValue = item;
				
				line.append(meta.getColumnName(i)).append(" = ");
				if(item!=null){
					String className = meta.getColumnClassName(i);
					line.append(getStringPresentation(className,item));
				}
				else{
					line.append(item);
				}
			}
			if(firstValue!=null)
				line.append(" WHERE ").append(meta.getColumnName(1)).append( " = ").append(firstValue);
			firstValue =null;
			writer.println(line.toString());
		}
	}
	
	private String getStringPresentation(String className,String value){
		StringBuffer line = new StringBuffer(value);
		if(value!=null){
		if(className.equalsIgnoreCase(Integer.class.getName()))
			return line.toString();
		else if(className.equalsIgnoreCase(Long.class.getName()))
			return line.toString();
		else if(className.equalsIgnoreCase(BigDecimal.class.getName()))
			return line.toString();
		else if(className.equalsIgnoreCase(BigInteger.class.getName()))
			return line.toString();
		else
			line.insert(0,"'").append("'");
		}
		return line.toString();
	}
}

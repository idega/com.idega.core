//idega 2001 - Eirikur Hrafnsson , eiki@idega.is
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.data;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.idega.util.database.ConnectionBroker;
/**
 * This is a class to read from BLOB columns in GenericEntity classes to be used as a regular InputStream
*@author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a> @modified by <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class BlobInputStream extends InputStream {
	private InputStream input;
	private Connection connection;
	private GenericEntity entity;
	private ResultSet RS;
	private Statement Stmt;
	private String columnName;
	private String dataSource;
	private boolean readFromEntityBlob=false;
	private boolean isClosed=true;
	/**
	 * Takes the first BLOB column found in the entity
	 * @param entity
	 */
	public BlobInputStream(GenericEntity entity){
		this(entity,entity._lobColumnName);
	}
	public BlobInputStream(GenericEntity entity, String tableColumnName){
		this.setEntity(entity);
		this.setTableColumnName(tableColumnName);
		//initConnection();
		//getInputStreamForBlobRead();
	}
	public BlobInputStream(InputStream in) {
		setInternalInputStream(in);
		isClosed=false;
	}
	public void setEntity(GenericEntity entity) {
		this.entity = entity;
		setDataSource(entity.getDatasource());
		
		//For Safetys sake if this BlobInputStream has been previously used with another entity entity instanse or inputstream:
		closeInternalInputStream();
		closeSQLVariables();
		
		readFromEntityBlob=true;
		isClosed=false;
	}
	public void setDataSource(String dataSourceName) {
		this.dataSource = dataSourceName;
	}
	public String getDataSource() {
		return this.dataSource;
	}
	public void setTableColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int read() throws IOException {
		InputStream in = getInternalInputStream();
		if (in!= null)
			return in.read();
		//else throw new IOException("BlobInputStream: read() inputstream is null!");
		else
			return -1;
	}
	public int read(byte b[], int off, int len) throws IOException {
		InputStream in =getInternalInputStream();
		if (in != null)
			return in.read(b, off, len);
		//else throw new IOException("BlobInputStream:  read(byte b[], int off, int len) inputstream is null!");
		else
			return -1;
	}
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}
	/**
	
	*<STRONG>Mandatory</STRONG> to call this function using this class
	
	**/
	public void close() throws IOException {
		try{
			InputStream in = getInternalInputStream();
			if (in != null) {
				in.close();
				in = null;
			}
			//debug for interbase...not closing the stream
			// if( in!=null) in.close();
		} finally {
			closeInternalInputStream();
			closeSQLVariables();
		}
	}
	// basic inputstream functions
	public int available() throws IOException {
		InputStream in = getInternalInputStream();
		if (in != null)
			return in.available();
		//else throw new IOException("BlobInputStream:  available() inputstream is null!");
		else {
			return 0;
		}
	}
	public boolean markSupported() {
		InputStream in = getInternalInputStream();
		if (in != null)
			return in.markSupported();
		else
			return false;
	}
	public synchronized void mark(int readlimit) {
		InputStream in = getInternalInputStream();
		if (in != null)
			in.mark(readlimit);
	}
	public long skip(long n) throws IOException {
		InputStream in = getInternalInputStream();
		if (in != null)
			return in.skip(n);
		else
			return -1;
		//else throw new IOException("BlobInputStream: skip() inputstream is null!");
	}

	public synchronized void reset() throws IOException {
		InputStream in = getInternalInputStream();
		if (in != null)
			in.reset();
		else
			throw new IOException("BlobInputStream: reset() inputstream is null!");
		/*else{
		
		  close();
		
		  this.getInputStreamForBlobRead();
		
		}*/
	}
	protected InputStream getInputStreamForBlobRead() {
		InputStream in=null;
		try {
			if (in == null) {
				Connection conn = getConnection();
				if (connection != null) {
					DatastoreInterface dsi = DatastoreInterface.getInstance(conn);
					Stmt = connection.createStatement();
					StringBuffer statement = new StringBuffer();
					statement.append("select ");
					statement.append(getTableColumnName());
					statement.append(" from ");
					statement.append(entity.getTableName());
					dsi.appendPrimaryKeyWhereClause(entity,statement);
					String sql = statement.toString();
					RS = Stmt.executeQuery(sql);
					/*
					RS = Stmt.executeQuery(
							"select "
								+ getTableColumnName()
								+ " from "
								+ entity.getTableName()
								+ " where "
								+ entity.getIDColumnName()
								+ "='"
								+ entity.getID()
								+ "'");*/
					if ((RS != null) && (RS.next())) {
						in = RS.getBinaryStream(1);
						//System.out.println("in is set for "+entity.getClass().getName()+", id="+entity.getID());
					} else {
						closeSQLVariables();
					}
				}
			}
		} catch (SQLException ex) {
			System.err.println("SQLException in BlobInputStream.getInputStreamForBlobRead(): " + ex.getMessage());
			ex.printStackTrace(System.err);
		}
		return in;
	}
	public GenericEntity getEntity() {
		return entity;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
	private String getTableColumnName() {
		return this.columnName;
	}
	
	private Connection getConnection(){
		if(connection==null){
			connection=ConnectionBroker.getConnection(getDataSource());
		}
		return connection;
	}
	
	private InputStream getInternalInputStream(){
		if(readFromEntityBlob&& (!isClosed) && input==null){
			input = getInputStreamForBlobRead();
		}
		return input;
	}
	
	private void setInternalInputStream(InputStream in){
		input=in;
	}
	
	private void closeInternalInputStream(){
		if (input != null) {
			try
			{
				input.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			input = null;
		}
		isClosed=true;
	}
	
	private void closeSQLVariables(){
		if (RS != null) {
			try {
				RS.close();
			} catch (SQLException sqle) {
				System.err.println("BlobInputStream : error closing SQL ResultSet");
				sqle.printStackTrace(System.err);
			}
			RS = null;
		}
		if (Stmt != null) {
			try {
				Stmt.close();
			} catch (SQLException sqle) {
				System.err.println("BlobInputStream : error closing SQL Statement");
				sqle.printStackTrace(System.err);
			}
			Stmt = null;
		}
		if (connection != null) {
			ConnectionBroker.freeConnection(getDataSource(), connection);
			connection = null;
		}
	}
	
	/*
	  protected void finalize()throws Throwable{
	    close();
	    super.finalize();
	  }
	*/
}

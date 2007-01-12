//idega 2002 - Tryggvi Larusson
/*
*Copyright 2002 idega software All Rights Reserved.
*/
package com.idega.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.idega.util.IWTimestamp;
import com.idega.util.database.ConnectionBroker;
import com.informix.jdbc.IfxPreparedStatement;
/**
*A class for database abstraction for the Informix Database.
* This is an implemention that ovverrides implementations from com.idega.data.DatastoreInterface 
* and performs specific functionality to the Interbase JDBC driver and database.
* Copyright 2002 idega software All Rights Reserved.
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class InformixDatastoreInterface extends DatastoreInterface {
	private boolean checkedBlobTable = false;
	InformixDatastoreInterface() {
		this.useTransactionsInEntityCreation = false;
		IWTimestamp.CUT_MILLISECONDS_OFF_IN_TOSTRING=false;
	}
	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "INTEGER";
		}
		else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 255) {
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else if (maxlength <= 2000) {
				theReturn = "LVARCHAR";
			}
			else {
				theReturn = "TEXT";
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
			theReturn = "DATETIME YEAR TO FRACTION";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BYTE";
		}
		else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "DATETIME HOUR TO FRACTION";
		}
		else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "BYTE";
		}
		else {
			theReturn = "";
		}
		return theReturn;
	}
	public String getIDColumnType(GenericEntity entity) {
		return "SERIAL";
	}
	/**
	 * On Informix the generated ID column is implemented as a serial column and no Trigger not used yet
	 */
	public void createTrigger(GenericEntity entity) throws Exception {
		createSequence(entity);
		/*
			Connection conn= null;
			Statement Stmt= null;
			try{
				conn = entity.getConnection();
				Stmt = conn.createStatement();
				int i = Stmt.executeUpdate("CREATE TRIGGER "+entity.getTableName()+"_trig BEFORE INSERT ON "+entity.getTableName()+" FOR EACH ROW WHEN (NEW."+entity.getIDColumnName()+" is null) DECLARE TEMP INTEGER; BEGIN SELECT "+entity.getTableName()+"_seq.NEXTVAL INTO TEMP FROM DUAL; :NEW."+entity.getIDColumnName()+":=TEMP;END;");
			}
			finally{
				if(Stmt != null){
					Stmt.close();
				}
				if (conn != null){
					entity.freeConnection(conn);
				}
			}
		*/
	}
	public void createSequence(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate(
					"create table " + InformixDatastoreInterface.getInformixSequenceTableName(entity) + "(" + entity.getIDColumnName() + " serial)");
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
	public void deleteEntityRecord(GenericEntity entity) throws Exception {
		/**
		 * @todo change
		 */
		//deleteTrigger(entity);
		deleteSequence(entity);
		super.deleteEntityRecord(entity);
	}
	protected void deleteSequence(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop table " + InformixDatastoreInterface.getInformixSequenceTableName(entity));
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
	/*  public void createForeignKeys(IDOLegacyEntity entity)throws Exception{
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
	  protected void insertBlob(IDOLegacyEntity entity)throws Exception{
	    Connection Conn = null;
	    oracle.sql.BLOB blob;
	    try{
	      Conn = entity.getConnection();
	      if(Conn == null) return;
	      //Conn.setAutoCommit(false);
	      Statement stmt2 = Conn.createStatement();
	      String cmd = "SELECT "+entity.getLobColumnName()+" FROM "+entity.getEntityName()+" WHERE "+entity.getIDColumnName()+" ='"+entity.getID()+"' FOR UPDATE ";
	      ResultSet RS2 =  stmt2.executeQuery(cmd);
	      RS2.next();
	      blob = ((OracleResultSet)RS2).getBLOB(1);
	        // write the array of binary data to a BLOB
	      OutputStream outstream = blob.getBinaryOutputStream();
	      int size = blob.getBufferSize();
	      byte[] buffer = new byte[size];
	      int length = -1;
	      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
	      if(wrapper!=null){
	        BufferedInputStream in = new BufferedInputStream( wrapper.getInputStreamForBlobWrite() );
	        while ((length = in.read(buffer)) != -1)
	            outstream.write(buffer, 0, length );
	        in.close();
	      }
	      outstream.flush();
	      outstream.close();
	      stmt2.close();
	      RS2.close();
	      //Conn.commit();
	      //Conn.setAutoCommit(true);
	    }
	    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}
	    catch(Exception ex){ex.printStackTrace();}
	    finally{
	      if(Conn != null) entity.freeConnection(Conn);
	    }
	  }
	  */
	/**
	**Creates a unique ID for the ID column
	**/
	public int createUniqueID(GenericEntity entity) throws Exception {
		int returnInt = -1;
		String query = "insert into " + InformixDatastoreInterface.getInformixSequenceTableName(entity) + "(" + entity.getIDColumnName() + ") values (0)";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = entity.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			com.informix.jdbc.IfxStatement ifxStatement = (com.informix.jdbc.IfxStatement) stmt;
			returnInt = ifxStatement.getSerial();
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}
	private static String getInformixSequenceTableName(GenericEntity entity) {
		String entityName = entity.getTableName();
		return entityName + "_seq";
	}
	/*
	  protected void insertBlob(IDOLegacyEntity entity)throws Exception{
	    String statement ;
	    Connection Conn = null;
	    try{
	      statement = "update " + entity.getTableName() + " set " + entity.getLobColumnName() + "=? where " + entity.getIDColumnName() + " = '" + entity.getID()+"'";
	      System.out.println(statement);
	      //System.out.println("In insertBlob() in DatastoreInterface");
	      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
	      if(wrapper!=null){
	        //System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
	        //Conn.setAutoCommit(false);
	        InputStream instream = wrapper.getInputStreamForBlobWrite();
	        if(instream!=null){
	          //System.out.println("In insertBlob() in DatastoreInterface instream != null");
	          Conn = entity.getConnection();
	          //if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
	          BufferedInputStream bin = new BufferedInputStream(instream);
	          PreparedStatement PS = Conn.prepareStatement(statement);
	          System.out.println("bin.available(): "+bin.available());
	          PS.setBinaryStream(1, bin, bin.available());
	          //PS.setBinaryStream(1, instream, instream.available() );
	          PS.executeUpdate();
	          PS.close();
	          //System.out.println("bin.available(): "+bin.available());
	          instream.close();
	         // bin.close();
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
	
	        public void handleBlobUpdate(String columnName,PreparedStatement statement, int index,IDOLegacyEntity entity){
	          BlobWrapper wrapper = entity.getBlobColumnValue(columnName);
	          System.out.println("InformixDatastoreInterface, in handleBlobUpdate, columnName="+columnName+" index="+index);
	          if(wrapper!=null){
	            InputStream fin = wrapper.getInputStreamForBlobWrite();
	            //System.out.println("DatastoreInterface, in handleBlobUpdate wrapper!=null");
	            if(fin!=null){
	              try{
	                //System.out.println("in handleBlobUpdate, stream != null");
	                Connection myConn = statement.getConnection();
	                byte[] buffer = new byte[200];;
	                IfxLobDescriptor loDesc = new IfxLobDescriptor(myConn);
	                IfxLocator loPtr = new IfxLocator();
	                IfxSmartBlob smb = new IfxSmartBlob(myConn);
	                // Create a smart large object in server
	                int loFd = smb.IfxLoCreate(loDesc, smb.LO_RDWR, loPtr);
	                System.out.println("A smart-blob has been created ");
	                int n = fin.read(buffer);
	                if (n > 0)
	                n = smb.IfxLoWrite(loFd, buffer);
	                smb.IfxLoClose(loFd);
	                System.out.println("Wrote: " + n +" bytes into it");
	                System.out.println("Smart-blob is closed " );
	                Blob blb = new IfxBblob(loPtr);
	                statement.setBlob(1, blb); // set the blob column
	                
	                //statement.setBinaryStream(index, fin, fin.available() );
	              }
	              catch(Exception e){
	                //System.err.println("Error updating BLOB field in "+entity.getClass().getName());
	                e.printStackTrace(System.err);
	              }
	            }
	          }
	        }
	
	*/
	public void setBlobstreamForStatement(PreparedStatement statement, InputStream stream, int index) throws SQLException, IOException {
		IfxPreparedStatement infstmt = (IfxPreparedStatement) statement;
		//infstmt.setBinaryStream(index, stream, stream.available(),com.informix.lang.IfxTypes.IFX_TYPE_BLOB);
		infstmt.setBinaryStream(index, stream, stream.available());
	}
	public boolean supportsBlobInUpdate() {
		return true;
	}
	private String getBlobTableName() {
		return "iw_blobs_temp";
	}
	/*protected void insertBlob(IDOLegacyEntity entity)throws Exception{
	  checkBlobTable();
	  int id = insertIntoBlobTable(entity);
	  System.out.print("id from blob = "+id);
	  //this.updateRealTable(id,entity);
	}*/
	private int insertIntoBlobTable(GenericEntity entity) throws Exception {
		String statement;
		Connection Conn = null;
		int returnInt = -1;
		try {
			statement = "insert into " + this.getBlobTableName() + "(blob_value) values(?)";
			System.out.println(statement);
			//System.out.println("In insertBlob() in DatastoreInterface");
			BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
			if (wrapper != null) {
				//System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
				//Conn.setAutoCommit(false);
				InputStream instream = wrapper.getInputStreamForBlobWrite();
				if (instream != null) {
					//System.out.println("In insertBlob() in DatastoreInterface instream != null");
					Conn = entity.getConnection();
					//if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
					//BufferedInputStream bin = new BufferedInputStream(instream);
					PreparedStatement PS = Conn.prepareStatement(statement);
					//System.out.println("bin.available(): "+bin.available());
					//PS.setBinaryStream(1, bin, 0 );
					PS.setBinaryStream(1, instream, instream.available());
					PS.executeUpdate();
					com.informix.jdbc.IfxStatement ifxStatement = (com.informix.jdbc.IfxStatement) PS;
					returnInt = ifxStatement.getSerial();
					PS.close();
					//System.out.println("bin.available(): "+bin.available());
					instream.close();
					// bin.close();
				}
				//Conn.commit();
				//Conn.setAutoCommit(true);
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("error uploading blob to db for " + entity.getClass().getName());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if (Conn != null) {
				entity.freeConnection(Conn);
			}
		}
		return returnInt;
	}
	private void updateRealTable(int blobID, GenericEntity entity) throws Exception {
		String blobColumn = entity.getLobColumnName();
		super.executeUpdate(
			entity,
			"update " + entity.getTableName() + " n set " + blobColumn + "xxx where " + this.getBlobTableName() + ".id=" + blobID);
	}
	private void checkBlobTable() {
		if (!this.checkedBlobTable) {
			createBlobTable();
		}
		this.checkedBlobTable = true;
	}
	private void createBlobTable() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ConnectionBroker.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("create table " + this.getBlobTableName() + " (id serial,blob_value byte)");
			stmt.close();
			System.out.println("Created blob table");
		}
		catch (SQLException e) {
			System.out.println("Did not create blob table");
		}
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			}
			catch (SQLException sql) {
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}
	}
	protected void createForeignKey(
		GenericEntity entity,
		String baseTableName,
		String columnName,
		String refrencingTableName,
		String referencingColumnName)
		throws Exception {
		String SQLCommand =
			"ALTER TABLE "
				+ baseTableName
				+ " ADD CONSTRAINT FOREIGN KEY ("
				+ columnName
				+ ") REFERENCES "
				+ refrencingTableName
				+ "("
				+ referencingColumnName
				+ ")";
		executeUpdate(entity, SQLCommand);
	}
	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add constraint primary key (";
	}
	protected void setStringForPreparedStatement(String columnName, PreparedStatement statement, int index, GenericEntity entity)
		throws SQLException {
		try {
			int maxlength = entity.getMaxLength(columnName);
			if (maxlength <= 2000) {
				statement.setString(index, entity.getStringColumnValue(columnName));
			}
			else {
				String stringValue = entity.getStringColumnValue(columnName);
				InputStream stream = new IDOInformixStringStream(stringValue);
				statement.setAsciiStream(index, stream, stringValue.length());
				/*if(stringValue!=null){
				  Reader reader = new StringReader(stringValue);
				  com.informix.jdbc.IfxPreparedStatement ifxStatement = (com.informix.jdbc.IfxPreparedStatement)statement;
				  ifxStatement.setCharacterStream(index,reader,stringValue.length(),com.informix.lang.IfxTypes.IFX_TYPE_CLOB);
				  //statement.setCharacterStream(index,reader,stringValue.length());
				
				}
				else{
				  statement.setCharacterStream(index,null,0);
				}*/
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class IDOInformixStringStream extends InputStream {
		private Reader reader;
		private int available;
		protected IDOInformixStringStream(String stringValue) {
			this.reader = new StringReader(stringValue);
			this.available = stringValue.length();
		}
		public int available() {
			return this.available;
		}
		public void close() throws IOException {
			this.reader.close();
		}
		public void mark(int readlimit) {
			try {
				this.reader.mark(readlimit);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		public boolean markSupported() {
			return this.reader.markSupported();
		}
		public int read() throws IOException {
			return this.reader.read();
		}
		public int read(byte[] b) throws IOException {
			char[] c = new char[b.length];
			int theReturn = this.reader.read(c);
			convertCharArrayToByteArray(c, b);
			return theReturn;
		}
		public int read(byte[] b, int off, int len) throws IOException {
			char[] c = new char[b.length];
			int theReturn = this.reader.read(c, off, len);
			convertCharArrayToByteArray(c, b);
			return theReturn;
		}
		public void reset() throws IOException {
			this.reader.reset();
		}
		public long skip(long n) throws IOException {
			return this.reader.skip(n);
		}
	}
	protected void fillStringColumn(GenericEntity entity, String columnName, ResultSet rs) throws SQLException {
		//int maxlength = entity.getMaxLength(columnName);
		if (true) {
			//if(maxlength<=2000){
			//System.out.println("Informix: Filling column for varchar field:"+columnName);
			String string = rs.getString(columnName);
			if (string != null) {
				entity.setColumn(columnName, string);
			}
		}
		else {
			try {
				System.out.println("Informix: 1 Filling column for clob field:" + columnName);
				//com.informix.jdbc.IfmxResultSet ifrs;
				//Clob clob = rs.getClob(columnName);
				//Reader reader = clob.getCharacterStream();
				//if(!(clob==null||rs.wasNull())){
				//Reader reader = rs.getCharacterStream(columnName);
				InputStream stream = rs.getAsciiStream(columnName);
				//System.out.println("Informix: 2 Filling column for clob field:"+columnName);
				if (!(stream == null || rs.wasNull())) {
					//if(!(reader==null||rs.wasNull())){
					StringBuffer sbuffer = new StringBuffer();
					//InputStream stream = clob.getAsciiStream();
					System.out.println("clob field was not empty");
					int buffersize = 1000;
					/*char[] charArray = new char[buffersize];
					while (reader.ready()) {
					  System.out.println("yes!");
					  reader.read(charArray);
					  sbuffer.append(charArray);
					}*/
					byte[] charArray = new byte[buffersize];
					stream.read(charArray);
					sbuffer.append(convertToCharArray(charArray));
					while (stream.read(charArray) != -1) {
						//System.out.println("yes!");
						sbuffer.append(convertToCharArray(charArray));
					}
					//System.out.println("StringBuffer:"+sbuffer.toString()+"!");
					entity.setColumn(columnName, sbuffer.toString());
				}
				else {
					System.out.println("clob field was empty");
				}
			}
			catch (IOException io) {
				throw new SQLException("IOException: " + io.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*protected String getColumnStringForSelectList(IDOLegacyEntity entity,String columnName){
	  int columnType = entity.getAttribute(columnName).getStorageClassType();
	  int maxLength = entity.getMaxLength(columnName);
	  if(columnType==EntityAttribute.TYPE_JAVA_LANG_STRING){
	     if(maxLength<=2000){
	      return columnName;
	    }
	    else{
	      return columnName+"::LVARCHAR";
	    }
	
	  }
	  else{
	    return columnName;
	  }
	}*/
	private char[] convertToCharArray(byte[] byteArray) {
		char[] charArray = new char[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			charArray[i] = (char) byteArray[i];
		}
		return charArray;
	}
	private void convertCharArrayToByteArray(char[] charArray, byte[] byteArray) {
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
	}
}

/*
 * $Id: BlobWrapper.java,v 1.6 2002/03/14 21:53:22 tryggvil Exp $
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
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Blob;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.sql.PreparedStatement;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryOutputStream;
import com.idega.io.MemoryInputStream;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class BlobWrapper {
  private int status;
  private InputStream writeInputStream;
  private InputStream readInputStream;
  private OutputStream readOutputStream;
  private OutputStream writeOutputStream;
  private String columnName;
  private String tableName;
  private String dataSourceName;
  private Connection conn;
  private GenericEntity entity;
  private ResultSet RS;
  private Statement Stmt;

  public final static int INSERT_STATUS = 1;
  public final static int SELECT_STATUS = 2;
  public final static int DELETE_STATUS = 3;
  public final static int IS_CLOSED = 3;

	public BlobWrapper(GenericEntity entity, String tableColumnName) {
      this.setEntity(entity);
      this.setTableColumnName(tableColumnName);
      setDatasource(entity.getDatasource());
	}


	public void setInputStreamForBlobWrite(InputStream stream) {
	  writeInputStream = stream;
	}


  protected InputStream getInputStreamForBlobWrite() {
    return writeInputStream;
  }

  /*
  public OutputStream getOutputStreamForBlobRead() throws IOException {
    byte buffer[] = new byte[1024];
    int noRead	= 0;

    InputStream myInputStream = this.getInputStreamForBlobRead();

    noRead = myInputStream.read(buffer,0,1023);

    //Write out the blob to the outputStream
    while (noRead != -1) {
      readOutputStream.write(buffer,0,noRead);
      noRead = myInputStream.read(buffer,0,1023);
    }
    return readOutputStream;
  }
*/
	public OutputStream getOutputStreamForBlobWrite() {
		if (writeOutputStream == null) {
                    /*writeOutputStream=new StreamConnectorOutputStream();
                    writeInputStream = ((StreamConnectorOutputStream)writeOutputStream).getNewConnectedInputStream();
		    */
                    MemoryFileBuffer buf = new MemoryFileBuffer();
                    writeOutputStream = new MemoryOutputStream(buf);
                    writeInputStream = new MemoryInputStream(buf);
                }
		return writeOutputStream;

	}
/*
	public void setOutputStreamForBlobWrite(OutputStream stream) {
		writeOutputStream = stream;
	}

	public InputStream getInputStreamForBlobRead() {
    try {
      if (readInputStream == null) {
          Stmt = conn.createStatement();
          RS = Stmt.executeQuery("select " + getTableColumnName() + " from " + entity.getTableName() + " where " + entity.getIDColumnName() + "='" + entity.getID() + "'");
          Blob myBlob = null;
          readInputStream = null;
          if (RS.next()) {
            readInputStream = RS.getBinaryStream(1);
          }

      }
    }
    catch(Exception ex) {
      System.err.println("Error in BlobWrapper getInputStreamForBlobRead(): " + ex.getMessage());
      ex.printStackTrace(System.err);
    }

    return readInputStream;
  }*/


  public BlobInputStream getBlobInputStream() throws SQLException, IOException {
    return(new BlobInputStream(this.entity,this.getTableColumnName()));
  }

  //public void populate() {
  //  DatastoreInterface.getInstance(conn).populateBlob(this);
  //}

  protected void setEntity(GenericEntity entity) {
    this.entity = entity;
  }

  public GenericEntity getEntity() {
    return this.entity;
  }

  public void setStatus(int status) {
          this.status = status;
  }

  public int getStatus() {
          return status;
  }

  protected void setTableColumnName(String columnName) {
          this.columnName = columnName;
  }

  protected String getTableColumnName() {
          return this.columnName;
  }

  protected void setTableName(String tableName) {
    this.tableName = tableName;
  }

  protected String getTableName() {
    return this.tableName;
  }

  private void setDatasource(String datasourceName) {
    this.dataSourceName = datasourceName;
  }

  private String getDatasource() {
    return this.dataSourceName;
  }

  protected Connection getConnection() {
    return conn;
  }

  /**
   *<STRONG>Mandatory</STRONG> to call this function after instanciating this class
   */
  public void close() {
    try {
      if (entity != null) {
        if (RS != null) {
          RS.close();
        }
        if (Stmt != null) {
          Stmt.close();
        }
        if (conn != null){
          conn.setAutoCommit(true);
          entity.freeConnection(dataSourceName,conn);
        }
      }
    }
    catch(Exception ex) {
      System.err.println("Error in BlobWrapper close(): " + ex.getMessage());
      ex.printStackTrace(System.err);
    }

    this.setStatus(this.IS_CLOSED);
  }


  /*
   public  int insertBlob(){
    int id = -1;

    Connection Conn = null;

    try{
      String dataBaseType = "";
      Conn = entity.getConnection();

      if (Conn!=null) dataBaseType = com.idega.data.DatastoreInterface.getDataStoreType(Conn);
      else dataBaseType="oracle";

//      if( dataBaseType.equals("oracle") ) {
//        id = saveToOracleDB();
//      }//other databases
//      else {
//        id = saveToDB();
//      }
      saveToDB();
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      return -1;
    }
    finally{
      if(Conn != null ) entity.freeConnection(Conn);
    }

    return id;
  }*/

  /*public void saveToDB(){

    String statement ;
    Connection Conn = null;

    try{
      Conn = entity.getConnection();
      if(Conn== null) return;

      statement = "update " + entity.getTableName() + " set " + getTableColumnName() + "=? where " + entity.getIDColumnName() + " = " + entity.getID();
      //Conn.setAutoCommit(false);
      BufferedInputStream bin = new BufferedInputStream(writeInputStream);
      PreparedStatement PS = Conn.prepareStatement(statement);
      PS.setBinaryStream(1, bin, bin.available() );
      PS.execute();
      PS.close();
      //Conn.commit();
      //Conn.setAutoCommit(true);

    }
    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}
    catch(Exception ex){ex.printStackTrace();}
    finally{
      if(Conn != null) entity.freeConnection(Conn);
    }
  }*/

/*  public  int saveToOracleDB(){
    int id = -1;
    String statement ;
    Connection Conn = null;
    boolean dimensions = false;
    if(width.equalsIgnoreCase("-1")) dimensions = false;


    try{
      Conn = entity.getConnection();
      if(Conn == null) return id;

       if(NewImage){
        id = com.idega.data.EntityControl.createUniqueID(entity);
        if(dimensions) statement = "insert into image (image_id,image_value,content_type,image_name,width,height,date_added,from_file,parent_id) values("+id+",?,?,?,?,?,"+idegaTimestamp.RightNow().toOracleString()+",'N',"+parentImageId+")";
        else statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id) values("+id+",?,?,?,"+idegaTimestamp.RightNow().toOracleString()+",'N',"+parentImageId+")";
      }
      else{
        if(dimensions) statement = "update image set image_value=?,content_type=?,image_name=?,width=?,height=? where image_id='"+imageId+"'";
        else statement = "update image set image_value=?,content_type=?,image_name=? where image_id='"+imageId+"'";
      }

        oracle.sql.BLOB blob;
        System.out.println(statement);

	Conn.setAutoCommit(false);
	PreparedStatement myPreparedStatement = Conn.prepareStatement ( statement );
        myPreparedStatement.setString(1, "00000001");// 00000001 i stað hins venjulega empty_blob()
        myPreparedStatement.setString(2, ContentType );
        myPreparedStatement.setString(3, FileName );
        if(dimensions){
          myPreparedStatement.setString(4, width );
          myPreparedStatement.setString(5, height );
        }
        myPreparedStatement.execute();
        myPreparedStatement.close();

        Conn.commit();

        Conn.setAutoCommit(false);
        Statement stmt2 = Conn.createStatement();
        if(!NewImage) id = imageId;
        String cmd = "SELECT image_value FROM image WHERE image_id ='"+id+"' FOR UPDATE ";
        ResultSet RS2 =  stmt2.executeQuery(cmd);

        RS2.next();
        blob = ((OracleResultSet)RS2).getBLOB(1);

          // write the array of binary data to a BLOB
        OutputStream outstream = blob.getBinaryOutputStream();

        int size = blob.getBufferSize();
        byte[] buffer = new byte[size];
        int length = -1;

        while ((length = in.read(buffer)) != -1)
            outstream.write(buffer, 0, length );

        outstream.flush();
        outstream.close();
        in.close();

        stmt2.close();
        RS2.close();

        Conn.commit();
        Conn.setAutoCommit(true);

    }
    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}
    catch(Exception ex){ex.printStackTrace();}
    finally{
      if(Conn != null) entity.freeConnection(Conn);
    }
    return id;
  }*/


	public String toString() {
    if (entity != null) {
      return entity.toString();
    }
    else {
      return "-1";
    }
	}

  protected void finalize() throws Throwable {
    if (!(this.getStatus() == this.IS_CLOSED)) {
      close();
    }
    super.finalize();
  }

  public boolean isReadyForUpdate(){
    return (!(writeInputStream==null));
  }
}

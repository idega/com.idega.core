//idega 2001 - Eirikur Hrafnsson , eiki@idega.is
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.data;


import java.sql.*;
import java.io.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;

/**
*@author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
*@version 1.0
*/
public class BlobInputStream extends InputStream{

  private InputStream in;
  private Connection conn;
  private GenericEntity entity;
  private ResultSet RS;
  private Statement Stmt;

  private String columnName;
  private String tableName;
  private String dataSourceName;
  private int status;

  public BlobInputStream(GenericEntity entity,String tableColumnName) throws SQLException,IOException {
    this.setEntity(entity);
    this.setTableColumnName(tableColumnName);
    setDatasource(entity.getDatasource());
    initConnection();
    in = getInputStreamForBlobRead();
  }

  public BlobInputStream(InputStream in){
    this.in = in;
  }

  public void setEntity(GenericEntity entity){
    this.entity=entity;
  }


  public void setTableColumnName(String columnName){
          this.columnName=columnName;
  }

  public int read() throws IOException {
    return in.read();
  }

  public int read(byte b[], int off, int len) throws IOException {
    return in.read(b, off, len);
  }

  public int read(byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  /**
  *<STRONG>Mandatory</STRONG> to call this function after instanciating this class
  **/
  public void close() throws IOException {
    try{
      if( in!=null) in.close();
      if (entity!= null){
        if (RS != null){
          RS.close();
        }
        if(Stmt!= null){
          Stmt.close();
        }
        if (conn!= null){
          conn.commit();
          conn.setAutoCommit(true);
          entity.freeConnection(dataSourceName,conn);
        }
      }
    }
    catch(Exception ex){
      System.err.println("Error in BlobInputStream: "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
  //  this.setStatus(this.IS_CLOSED);

  }

  // basic inputstream functions
  public int available() throws IOException {
      return in.available();
  }

  public boolean markSupported() {
      return in.markSupported();
  }

  public synchronized void mark(int readlimit) {
    in.mark(readlimit);
  }

  public long skip(long n) throws IOException {
    return in.skip(n);
  }

  private void ensureOpen() throws IOException {
    if (in == null)
        throw new IOException("Stream closed");
  }

  public synchronized void reset() throws IOException {
      in.reset();
  }

  public InputStream getInputStreamForBlobRead(){
    try{
      if (in == null){
        Stmt = conn.createStatement();
        RS = Stmt.executeQuery("select "+getTableColumnName()+" from "+entity.getTableName()+" where "+entity.getIDColumnName()+"='"+entity.getID()+"'");
        if (RS.next()) {
          in = RS.getBinaryStream(1);
        }

      }
      }
    catch(Exception ex){
      System.err.println("Error in BlobInputStream: "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
    return in;
  }


  public GenericEntity getEntity(){
    return entity;
  }

  public void setStatus(int status){
    this.status=status;
  }

  public int getStatus(){
    return status;
  }

  private String getTableColumnName(){
    return this.columnName;
  }

  private void setTableName(String tableName){
    this.tableName=tableName;
  }

  private String getTableName(){
    return this.tableName;
  }

  private void setDatasource(String datasourceName){
    this.dataSourceName=datasourceName;
  }

  private String getDatasource(){
    return this.dataSourceName;
  }

  private Connection getConnection(){
    return conn;
  }

  private void initConnection() throws SQLException{
    if(entity!=null){
      conn=entity.getConnection();
    //  if ( conn!=null ) conn.setAutoCommit(false);
    }
  }

  protected void finalize()throws Throwable{
    close();
    super.finalize();
  }

  ////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////

  /*


  public void populate(){
    DatastoreInterface.getInstance(conn).populateBlob(this);
  }

  public String toString(){
    if (entity!= null){
      return entity.toString();
    }
    else{
      return "-1";
    }
  }


  protected void finalize()throws Throwable{
    if(!(this.getStatus()==this.IS_CLOSED)){
      close();
    }
    super.finalize();
  }


*/


 // public OutputStream getOutputStreamForBlobRead()throws IOException{
     /* if (readOutputStream == null){
        readOutputStream = new OutputStream();
      }*/
/*
    byte	buffer[]= new byte[1024];
    int		noRead	= 0;

    InputStream myInputStream = this.getInputStreamForBlobRead();

    noRead = myInputStream.read( buffer, 0, 1023 );

    //Write out the blob to the outputStream
    while ( noRead != -1 ){
      readOutputStream.write( buffer, 0, noRead );
      noRead	= myInputStream.read( buffer, 0, 1023 );
    }
    return readOutputStream;
  }


  public OutputStream getOutputStreamForBlobWrite(){
          if (writeOutputStream== null){


          }
          return writeOutputStream;
  }

  public void setOutputStreamForBlobWrite(OutputStream stream){
          writeOutputStream=stream;
  }




  public void setInputStreamForBlobWrite(InputStream stream){
    writeInputStream=stream;
  }

  public InputStream getInputStreamForBlobWrite(){
    return writeInputStream;
  }

  */
}

//idega 2001 - Eirikur Hrafnsson , eiki@idega.is
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.data;


import java.sql.*;
import java.io.*;
//import javax.sql.*;
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
  private int status; //not used
  private String columnName;
  private String tableName;

  public BlobInputStream(GenericEntity entity,String tableColumnName) throws SQLException,IOException {
    this.setEntity(entity);
    this.setTableColumnName(tableColumnName);
    initConnection();
    getInputStreamForBlobRead();
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
    if ( in!=null ) return in.read();
    else throw new IOException("BlobInputStream: read() inputstream is null!");
  }

  public int read(byte b[], int off, int len) throws IOException {
    if ( in!=null ) return in.read(b, off, len);
    else throw new IOException("BlobInputStream:  read(byte b[], int off, int len) inputstream is null!");
  }

  public int read(byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  /**
  *<STRONG>Mandatory</STRONG> to call this function using this class
  **/
  public void close() throws IOException {
    try{
      if (RS != null){
          RS.close();
       }
      if(Stmt!= null){
          Stmt.close();
      }

      //debug for interbase...not closing the stream
     // if( in!=null) in.close();

    }
    catch(Exception ex){
      System.err.println("BlobInputStream : error closing stmt or rs");
      ex.printStackTrace(System.err);
    }
    finally{
      if (conn!= null){
       ConnectionBroker.freeConnection(conn);
      }
    }
  }

  // basic inputstream functions
  public int available() throws IOException {
    if( in != null) return in.available();
    else throw new IOException("BlobInputStream:  available() inputstream is null!");
  }

  public boolean markSupported() {
    if( in != null) return in.markSupported();
    else return false;
  }

  public synchronized void mark(int readlimit) {
    if(in!=null) in.mark(readlimit);
  }

  public long skip(long n) throws IOException {
    if ( in!=null ) return in.skip(n);
    else throw new IOException("BlobInputStream: skip() inputstream is null!");
  }

  private void ensureOpen() throws IOException {
    if (in == null)
        throw new IOException("BlobInputStream: ensureOpen() InputStream is closed(null)!");
  }

  public synchronized void reset() throws IOException {
    if ( in!=null ) in.reset();
    else throw new IOException("BlobInputStream: reset() inputstream is null!");
  }

  public InputStream getInputStreamForBlobRead(){
    try{
      if (in == null){
        if( conn != null ){
          Stmt = conn.createStatement();
          RS = Stmt.executeQuery("select "+getTableColumnName()+" from "+entity.getTableName()+" where "+entity.getIDColumnName()+"='"+entity.getID()+"'");

          if ( (RS!=null) && (RS.next()) ) {
            in = RS.getBinaryStream(1);
          }
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

  private void initConnection() throws SQLException{
    conn = ConnectionBroker.getConnection();
  }
/*
  protected void finalize()throws Throwable{
    close();
    super.finalize();
  }
*/
}

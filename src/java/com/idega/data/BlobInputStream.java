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
  private int status; //not used
  private String columnName;
  private String tableName;

  public BlobInputStream(GenericEntity entity,String tableColumnName) throws SQLException,IOException {
    this.setEntity(entity);
    this.setTableColumnName(tableColumnName);
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

      if( in!=null) in.close();

    }
    catch(Exception ex){
      System.err.println("BlobInputStream : error closing stmt or rs");
      ex.printStackTrace(System.err);
    }
    finally{
      if (conn!= null){
       // ConnectionBroker.freeConnection(conn);
        PoolManager.getInstance().freeConnection(conn);
      }
    }
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
    //  conn = ConnectionBroker.getConnection();
    conn = PoolManager.getInstance().getConnection();
  }
/*
  protected void finalize()throws Throwable{
    close();
    super.finalize();
  }
*/
}

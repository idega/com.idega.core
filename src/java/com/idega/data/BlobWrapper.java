//idega 2000 - Tryggvi Larusson
//modified by Eirikur Hrafnsson , eiki@idega.is
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;

import java.sql.*;
import java.io.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class BlobWrapper{


private int status;
private InputStream writeInputStream;
private InputStream readInputStream;
private OutputStream readOutputStream;
private OutputStream writeOutputStream;
public final static int INSERT_STATUS=1;
public final static int SELECT_STATUS=2;
public final static int DELETE_STATUS=3;
public final static int IS_CLOSED=3;
private String columnName;
private String tableName;
private String dataSourceName;
private Connection conn;
private GenericEntity entity;
ResultSet RS;
Statement Stmt;


	/*public BlobWrapper(){
	}*/

        /*
	public BlobWrapper(GenericEntity entity){
          setEntity(entity);
	}*/

//was protected
	public BlobWrapper(GenericEntity entity,String tableColumnName){
          this.setEntity(entity);
          this.setTableColumnName(tableColumnName);
          setDatasource(entity.getDatasource());
	}

	public void setInputStreamForBlobWrite(InputStream stream){
	  writeInputStream=stream;
	}

        public InputStream getInputStreamForBlobWrite(){
          return writeInputStream;
        }


        public OutputStream getOutputStreamForBlobRead()throws IOException{
           /* if (readOutputStream == null){
              readOutputStream = new OutputStream();
            }*/

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


	public InputStream getInputStreamForBlobRead(){
          try{
		if (readInputStream == null){
				Stmt = conn.createStatement();
				RS = Stmt.executeQuery("select "+getTableColumnName()+" from "+entity.getTableName()+" where "+entity.getIDColumnName()+"='"+entity.getID()+"'");
				Blob myBlob=null;
				readInputStream = null;
				if (RS.next()) {
					readInputStream = RS.getBinaryStream(1);
				}

		}
            }
          catch(Exception ex){
            System.err.println("Error in BlobWrapper getInputStreamForBlobRead(): "+ex.getMessage());
            ex.printStackTrace(System.err);
          }
	  return readInputStream;
	}

        public BlobInputStream getBlobInputStream() throws SQLException,IOException{
	  return (new BlobInputStream(this.entity,this.getTableColumnName()));
	}

        public void populate(){
          DatastoreInterface.getInstance(conn).populateBlob(this);
        }

        protected void setEntity(GenericEntity entity){
//         try{
          this.entity=entity;
         // conn=this.entity.getConnection();
         // conn.setAutoCommit(false);
/*         }
        catch(Exception ex){
            System.err.println("Error in BlobWrapper setEntity(): "+ex.getMessage());
            ex.printStackTrace(System.err);
        }
*/
      }

        public GenericEntity getEntity(){
          return this.entity;
        }

	public void setStatus(int status){
		this.status=status;
	}

	public int getStatus(){
		return status;
	}

	protected void setTableColumnName(String columnName){
		this.columnName=columnName;
	}

	protected String getTableColumnName(){
		return this.columnName;
	}

        protected void setTableName(String tableName){
          this.tableName=tableName;
        }

        protected String getTableName(){
          return this.tableName;
        }

        private void setDatasource(String datasourceName){
          this.dataSourceName=datasourceName;
        }

        private String getDatasource(){
          return this.dataSourceName;
        }

        protected Connection getConnection(){
          return conn;
        }

        /**
        *<STRONG>Mandatory</STRONG> to call this function after instanciating this class
        **/
        public void close(){
          try{
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
            System.err.println("Error in BlobWrapper close(): "+ex.getMessage());
            ex.printStackTrace(System.err);
          }
          this.setStatus(this.IS_CLOSED);

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

}

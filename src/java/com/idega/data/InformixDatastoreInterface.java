//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;


import com.idega.util.database.ConnectionBroker;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Blob;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import com.informix.jdbc.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class InformixDatastoreInterface extends DatastoreInterface{


  private boolean checkedBlobTable=false;

  InformixDatastoreInterface(){
    //useTransactionsInEntityCreation=false;
  }

  public String getSQLType(String javaClassName,int maxlength){
    String theReturn;
    if (javaClassName.equals("java.lang.Integer")){
      theReturn = "INTEGER";
    }
    else if (javaClassName.equals("java.lang.String")){
      	if (maxlength<0){
			theReturn = "VARCHAR(255)";
		}
      	else if (maxlength<=255){
			theReturn = "VARCHAR("+maxlength+")";

		}
		else{
			theReturn = "LVARCHAR";
		}


    }
    else if (javaClassName.equals("java.lang.Boolean")){
      theReturn = "CHAR(1)";
    }
    else if (javaClassName.equals("java.lang.Float")){
      theReturn = "FLOAT";
    }
    else if (javaClassName.equals("java.lang.Double")){
      theReturn = "FLOAT";
    }
    else if (javaClassName.equals("java.sql.Timestamp")){
      theReturn = "DATETIME YEAR TO FRACTION";
    }
    else if (javaClassName.equals("java.sql.Date")){
      theReturn = "DATE";
    }
    else if (javaClassName.equals("java.sql.Blob")){
      theReturn = "BYTE";
    }
    else if (javaClassName.equals("java.sql.Time")){
      theReturn = "DATETIME HOUR TO FRACTION";
    }
    else if (javaClassName.equals("com.idega.util.Gender")) {
      theReturn = "VARCHAR(1)";
    }
    else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
      theReturn = "BYTE";
    }
    else{
      theReturn = "";
    }
    return theReturn;
  }


  public String getIDColumnType(){
    return "SERIAL";
  }

  /**
   * On Informix the generated ID column is implemented as a serial column and no Trigger not used
   */
  public void createTrigger(GenericEntity entity)throws Exception{

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


  public void createSequence(GenericEntity entity)throws Exception{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("create table "+this.getInformixSequenceTableName(entity)+"("+entity.getIDColumnName()+" serial)");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
  }


    public void deleteEntityRecord(GenericEntity entity)throws Exception{
      /**
       * @todo change
       */
      //deleteTrigger(entity);
      deleteSequence(entity);
      super.deleteEntityRecord(entity);
    }

      protected void deleteSequence(GenericEntity entity)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("drop table "+this.getInformixSequenceTableName(entity));
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
    }


  /*  public void createForeignKeys(GenericEntity entity)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();

   		        String[] names = entity.getColumnNames();
		        for (int i = 0; i < names.length; i++){

                          if (!entity.getRelationShipClassName(names[i]).equals("")){

                            Stmt = conn.createStatement();
                            String statement = "ALTER TABLE "+entity.getTableName()+" ADD FOREIGN KEY ("+names[i]+") REFERENCES "+((GenericEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName()+" ";
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

  protected void executeBeforeInsert(GenericEntity entity)throws Exception{
				if ( entity.isNull(entity.getIDColumnName()) ){
					entity.setID(createUniqueID(entity));
				}
  }

/*
  protected void insertBlob(GenericEntity entity)throws Exception{
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
	public int createUniqueID(GenericEntity entity) throws Exception{
		int returnInt = -1;
        String query = "insert into "+this.getInformixSequenceTableName(entity)+"("+entity.getIDColumnName()+") values (0)";
		Connection conn = null;
		Statement stmt = null;
		try{

			conn = entity.getConnection();
				stmt = conn.createStatement();
                stmt.executeUpdate(query);
                com.informix.jdbc.IfxStatement ifxStatement = (com.informix.jdbc.IfxStatement)stmt;
                returnInt = ifxStatement.getSerial();
        }
		finally{
			if (stmt != null){
				stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}



  private static String getInformixSequenceTableName(GenericEntity entity){
		String entityName = entity.getTableName();
		return entityName+"_seq";
  }






/*

  protected void insertBlob(GenericEntity entity)throws Exception{

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




        public void handleBlobUpdate(String columnName,PreparedStatement statement, int index,GenericEntity entity){

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

  //public void setBlobstreamForStatement(PreparedStatement statement,InputStream stream,int index)throws SQLException,IOException{
  //  IfxPreparedStatement infstmt = (IfxPreparedStatement)statement;
  //  infstmt.setBinaryStream(index, stream, stream.available(),com.informix.lang.IfxTypes.IFX_TYPE_BLOB);
  //}


  public boolean supportsBlobInUpdate(){
    return true;
  }

  private String getBlobTableName(){
    return "iw_blobs_temp";
  }

  /*protected void insertBlob(GenericEntity entity)throws Exception{
    checkBlobTable();
    int id = insertIntoBlobTable(entity);
    System.out.print("id from blob = "+id);
    //this.updateRealTable(id,entity);
  }*/

  private int insertIntoBlobTable(GenericEntity entity)throws Exception{

    String statement ;
    Connection Conn = null;
    int returnInt = -1;

    try{

      statement = "insert into "+this.getBlobTableName()+"(blob_value) values(?)";
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
          //BufferedInputStream bin = new BufferedInputStream(instream);
          PreparedStatement PS = Conn.prepareStatement(statement);
          //System.out.println("bin.available(): "+bin.available());
          //PS.setBinaryStream(1, bin, 0 );
          PS.setBinaryStream(1, instream, instream.available() );
          PS.executeUpdate();
          com.informix.jdbc.IfxStatement ifxStatement = (com.informix.jdbc.IfxStatement)PS;
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
    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error uploading blob to db for "+entity.getClass().getName());}
    catch(Exception ex){ex.printStackTrace();}
    finally{
      if(Conn != null) entity.freeConnection(Conn);
    }
    return returnInt;
  }


  private void updateRealTable(int blobID,GenericEntity entity)throws Exception{
      String blobColumn= entity.getLobColumnName();
      super.executeUpdate(entity,"update "+entity.getTableName()+" n set "+blobColumn+"xxx where "+this.getBlobTableName()+".id="+blobID);
  }

  private void checkBlobTable(){
    if(!this.checkedBlobTable){
      createBlobTable();
    }
    checkedBlobTable=true;
  }

  private void createBlobTable(){

    Connection conn = null;
    Statement stmt = null;
    try{
      conn = ConnectionBroker.getConnection();
      stmt = conn.createStatement();
      stmt.executeUpdate("create table "+this.getBlobTableName()+" (id serial,blob_value byte)");
      stmt.close();
      System.out.println("Created blob table");
    }
    catch(SQLException e){
      System.out.println("Did not create blob table");
    }
    finally{
      try{
      if(stmt!=null){
        stmt.close();
      }
      }
      catch(SQLException sql){}
      if(conn!=null){
        ConnectionBroker.freeConnection(conn);
      }
    }
  }

}

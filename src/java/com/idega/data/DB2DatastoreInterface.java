//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.data;



import java.sql.Connection;

import java.sql.Statement;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/

public class DB2DatastoreInterface extends DatastoreInterface{







  /*public String getSQLType(String javaClassName){

    String theReturn;

    if (javaClassName.equals("java.lang.Integer")){

      theReturn = "NUMBER";

    }

    else if (javaClassName.equals("java.lang.String")){

      theReturn = "VARCHAR2";

    }

    else if (javaClassName.equals("java.lang.Boolean")){

      theReturn = "CHAR";

    }

    else if (javaClassName.equals("java.lang.Float")){

      theReturn = "DOUBLE";

    }

    else if (javaClassName.equals("java.lang.Double")){

      theReturn = "DOUBLE";

    }

    else if (javaClassName.equals("java.sql.Timestamp")){

      theReturn = "DATE";

    }

    else if (javaClassName.equals("java.sql.Date")){

      theReturn = "DATE";

    }

    else if (javaClassName.equals("java.sql.Blob")){

      theReturn = "BLOB";

    }

    else if (javaClassName.equals("java.sql.Time")){

      theReturn = "TIME";

    }

    else{

      theReturn = "";

    }

    return theReturn;

  }*/







  public String getSQLType(String javaClassName,int maxlength){

    String theReturn;

    if (javaClassName.equals("java.lang.Integer")){

      theReturn = "NUMBER";

    }

    else if (javaClassName.equals("java.lang.String")){

      	if (maxlength<0){

			theReturn = "VARCHAR(255)";

		}

      	else if (maxlength<=30000){

			theReturn = "VARCHAR("+maxlength+")";



		}

		else{

			theReturn = "LONG VARCHAR";

		}

    }

    else if (javaClassName.equals("java.lang.Boolean")){

      theReturn = "CHAR(1)";

    }

    else if (javaClassName.equals("java.lang.Float")){

      theReturn = "FLOAT";

    }

    else if (javaClassName.equals("java.lang.Double")){

      theReturn = "FLOAT(15)";

    }

    else if (javaClassName.equals("java.sql.Timestamp")){

      theReturn = "TIMESTAMP";

    }

    else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {

      theReturn = "DATE";

    }

    else if (javaClassName.equals("java.sql.Blob")){

      theReturn = "BLOB";

    }

    else if (javaClassName.equals("java.sql.Time")){

      theReturn = "TIME";

    }

    else if (javaClassName.equals("com.idega.util.Gender")) {

      theReturn = "VARCHAR(1)";

    }

    else if (javaClassName.equals("com.idega.data.BlobWrapper")) {

      theReturn = "BLOB";

    }

    else{

      theReturn = "";

    }

    return theReturn;

  }





  /**

   * Only creates the sequence, not the trigger

   * @todo implement trigger creation

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

			Stmt.executeUpdate("create sequence "+entity.getTableName()+"_seq INCREMENT BY 1 START WITH 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE CACHE 20 NOORDER");

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

      super.deleteEntityRecord(entity);

      /**

       * @todo change

       */

      //deleteTrigger(entity);

      deleteSequence(entity);

    }



      protected void deleteTrigger(GenericEntity entity)throws Exception{

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

			Stmt.executeUpdate("drop trigger "+entity.getTableName()+"_trig");

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



      protected void deleteSequence(GenericEntity entity)throws Exception{

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

			Stmt.executeUpdate("drop sequence "+entity.getTableName()+"_seq");

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



  protected void executeBeforeInsert(GenericEntity entity)throws Exception{

				if ( entity.isNull(entity.getIDColumnName()) ){

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



  protected String getCreateUniqueIDQuery(GenericEntity entity){

    return "SELECT "+getSequenceName(entity)+".nextval FROM dual";

  }





	private static String getSequenceName(GenericEntity entity){

		String entityName = entity.getTableName();

		return entityName+"_seq";

                /*if (entityName.endsWith("_")){

			return entityName+"seq";

		}

		else{

			return entityName+"_seq";

		}*/

	}

}

//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;



import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class OracleDatastoreInterface extends DatastoreInterface{



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
			theReturn = "VARCHAR2(255)";
		}
      	else if (maxlength<=4000){
			theReturn = "VARCHAR2("+maxlength+")";

		}
		else{
			theReturn = "CLOB";
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
  }



  public void createTrigger(GenericEntity entity)throws Exception{

                createSequence(entity);

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
  }


  public void createSequence(GenericEntity entity)throws Exception{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("create sequence "+entity.getTableName()+"_seq INCREMENT BY 1 START WITH 1 MAXVALUE 1.0E28 MINVALUE 1 NOCYCLE CACHE 20 NOORDER");
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
      deleteTrigger(entity);
      deleteSequence(entity);
      deleteTable(entity);

    }

      protected void deleteTrigger(GenericEntity entity)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("drop trigger "+entity.getTableName()+"_trig");
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
			int i = Stmt.executeUpdate("drop sequence "+entity.getTableName()+"_seq");
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


    public void createForeignKeys(GenericEntity entity)throws Exception{
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
  }
}
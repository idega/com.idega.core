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
public class MySQLDatastoreInterface extends DatastoreInterface{



 /*public String getSQLType(String javaClassName){
    String theReturn;
    if (javaClassName.equals("java.lang.Integer")){
      theReturn = "INTEGER";
    }
    else if (javaClassName.equals("java.lang.String")){
      theReturn = "VARCHAR";
    }
    else if (javaClassName.equals("java.lang.Boolean")){
      theReturn = "CHAR(1)";
    }
    else if (javaClassName.equals("java.lang.Float")){
      theReturn = "DOUBLE";
    }
    else if (javaClassName.equals("java.lang.Double")){
      theReturn = "DOUBLE";
    }
    else if (javaClassName.equals("java.sql.Timestamp")){
      theReturn = "TIMESTAMP";
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
        theReturn = "INTEGER";
      }
      else if (javaClassName.equals("java.lang.String")){
        if (maxlength<0){
  			theReturn = "VARCHAR(255)";
  		}
        else if (maxlength<30000){
  			theReturn = "VARCHAR("+maxlength+")";

  		}
  		else{
  			theReturn = "TEXT";
  		}

      }
      else if (javaClassName.equals("java.lang.Boolean")){
        theReturn = "CHAR(1)";
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






  public void createTrigger(GenericEntity entity)throws Exception{
  }

  public void createForeignKeys(GenericEntity entity)throws Exception{
  }


    protected String getCreationStatement(GenericEntity entity){
		String returnString = "create table "+entity.getTableName()+"(";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){

                    /*if (entity.getMaxLength(names[i]) == -1){
                      if (entity.getStorageClassName(names[i]).equals("java.lang.String")){
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"(255)";
                      }
                      else{
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]));
                      }

                    }
                    else{
                      returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"("+entity.getMaxLength(names[i])+")";
                    }*/
                    returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]),entity.getMaxLength(names[i]));

                    if (entity.isPrimaryKey(names[i])){
                      returnString = 	returnString + " PRIMARY KEY auto_increment";
                    }
                    if (i!=names.length-1){
                      returnString = returnString+",";
                    }
		}
                returnString = returnString +")";
                System.out.println(returnString);
		return returnString;
}

  protected void insertBlob(GenericEntity entity)throws Exception{
  }


  protected void executeAfterInsert(GenericEntity entity)throws Exception{
    Connection conn= null;
    Statement Stmt= null;
		ResultSet RS = null;
		try{
			conn = entity.getConnection();
      if (entity.getID() == -1){
					Stmt = conn.createStatement();
					RS = Stmt.executeQuery("select last_insert_id()");
					RS.next();
					entity.setID(RS.getInt(1));
			}

		}
		finally{
			if (RS != null){
				RS.close();
			}
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
  }

}

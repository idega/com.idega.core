/*
 * $Id: InterbaseDatastoreInterface.java,v 1.2 2001/05/17 13:27:46 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class InterbaseDatastoreInterface extends DatastoreInterface {
  public String getSQLType(String javaClassName,int maxlength) {
    String theReturn;
    if (javaClassName.equals("java.lang.Integer")) {
      theReturn = "INTEGER";
    }
    else if (javaClassName.equals("java.lang.String")) {
      if (maxlength < 0) {
        theReturn = "VARCHAR(255)";
      }
      else if (maxlength < 30000) {
        theReturn = "VARCHAR("+maxlength+")";
      }
      else {
        theReturn = "CLOB";
      }
    }
    else if (javaClassName.equals("java.lang.Boolean")) {
      theReturn = "CHAR(1)";
    }
    else if (javaClassName.equals("java.lang.Float")) {
      theReturn = "FLOAT";
    }
    else if (javaClassName.equals("java.lang.Double")) {
      theReturn = "FLOAT";
    }
    else if (javaClassName.equals("java.sql.Timestamp")) {
      theReturn = "TIMESTAMP";
    }
    else if (javaClassName.equals("java.sql.Date")) {
      theReturn = "DATE";
    }
    else if (javaClassName.equals("java.sql.Blob")) {
      theReturn = "BLOB";
    }
    else if (javaClassName.equals("java.sql.Time")) {
      theReturn = "TIME";
    }
    else {
      theReturn = "";
    }

    return theReturn;
  }

  public void createTrigger(GenericEntity entity) throws Exception {
    createGenerator(entity);

    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      Stmt = conn.createStatement();
      String s = "CREATE TRIGGER " + entity.getTableName() + "_trig for " + entity.getTableName() + " ACTIVE BEFORE INSERT POSITION 0 AS BEGIN IF (NEW." + entity.getIDColumnName() + " IS NULL) THEN NEW." + entity.getIDColumnName() + " = GEN_ID(" + EntityControl.getInterbaseGeneratorName(entity) + ", 1); END";
      System.out.println(s);
      int i = Stmt.executeUpdate(s);
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

  public void createGenerator(GenericEntity entity) throws Exception {
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      Stmt = conn.createStatement();
      String s = "CREATE GENERATOR " + EntityControl.getInterbaseGeneratorName(entity);
      System.out.println(s);
      int i = Stmt.executeUpdate(s);

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

  public void createForeignKeys(GenericEntity entity) throws Exception {
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      conn.commit();

      String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++) {
        if (!entity.getRelationShipClassName(names[i]).equals("")) {
          Stmt = conn.createStatement();
          int n = Stmt.executeUpdate("ALTER TABLE " + entity.getTableName() + " ADD FOREIGN KEY (" + names[i] + ") REFERENCES " + ((GenericEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName() + " ");
          if (Stmt != null) {
            Stmt.close();
          }
        }
      }
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

  protected void deleteTrigger(GenericEntity entity) throws Exception {
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      Stmt = conn.createStatement();
      int i = Stmt.executeUpdate("delete from RDB$TRIGGERS where RDB$TRIGGER_NAME='" + entity.getTableName() + "_trig" + "'");
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
    deleteTrigger(entity);
    deleteGenerator(entity);
    deleteTable(entity);
  }

  protected void deleteGenerator(GenericEntity entity) throws Exception {
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      Stmt = conn.createStatement();
      int i = Stmt.executeUpdate("delete from RDB$GENERATORS WHERE RDB$GENERATOR_NAME='" + EntityControl.getInterbaseGeneratorName(entity) + "'");
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

 public boolean isConnectionOK(Connection conn) {
    Statement testStmt = null;
    ResultSet RS = null;
    try {
      if (!conn.isClosed()) {
        // Try to createStatement to see if it's really alive
        testStmt = conn.createStatement();
        testStmt.close();
      }
      else {
        return false;
      }
    }
    catch(SQLException e) {
      if (testStmt != null) {
        if (RS != null) {
          try {
             RS.close();
          }
          catch (SQLException se) {}
        }

        try {
          testStmt.close();
        }
        catch(SQLException se) {}
      }
      return false;
    }
    return true;
  }
}

package com.idega.core.accesscontrol.data;

import com.idega.data.*;
import java.sql.*;

/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */

public class IBPermission extends GenericEntity {


  private static String[] Columns = {"ib_permission_context_type", "ib_permission_context_value", "ib_permission_string", "ib_permission_string_value", "ib_permission_value", "group_id"};

  private static String sClassName = "com.idega.builder.accesscontrol.data.IBPermission";


  public IBPermission() {
    super();
  }

  public IBPermission(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(Columns[0],"Context type",true,true,"java.lang.String");
    addAttribute(Columns[1],"Context value",true,true,"java.lang.String");
    addAttribute(Columns[2],"Permission string",true,true,"java.lang.String");
    addAttribute(Columns[3],"Permission string value",true,true,"java.lang.String");
    addAttribute(Columns[4],"Permission value",true,true,"java.lang.Boolean");
    addAttribute(Columns[5],"GroupID",true,true,"java.lang.Integer");

  }
  public String getEntityName() {
    return "ib_permission";
  }
/*
  public void setDefaultValues(){
    this.setPermissionStringValue("");
  }
*/

  public static String getContextTypeColumnName(){
    return Columns[0];
  }

  public String getContextType(){
    return getStringColumnValue(Columns[0]);
  }

  public void setContextType( String ContextType){
    setColumn(Columns[0],ContextType);
  }


  public static String getContextValueColumnName(){
    return Columns[1];
  }

  public String getContextValue(){
    return getStringColumnValue(Columns[1]);
  }

  public void setContextValue( String ContextValue){
    setColumn(Columns[1],ContextValue);
  }

  public static String getPermissionStringColumnName(){
    return Columns[2];
  }

  public String getPermissionString(){
    return getStringColumnValue(Columns[2]);
  }

  public void setPermissionString( String PermissionString){
    setColumn(Columns[2],PermissionString);
  }


  public static String getPermissionStringValueColumnName(){
    return Columns[3];
  }

  public String getPermissionStringValue(){
    return getStringColumnValue(Columns[3]);
  }

  public void setPermissionStringValue( String PermissionStringValue){
    setColumn(Columns[3],PermissionStringValue);
  }


  public static String getPermissionValueColumnName(){
    return Columns[4];
  }

  public boolean getPermissionValue(){
    return getBooleanColumnValue(Columns[4]);
  }

  public void setPermissionValue( Boolean PermissionStringValue){
    setColumn(Columns[4],PermissionStringValue);
  }




  public static String getGroupIDColumnName(){
    return Columns[5];
  }

  public int getGroupID(){
    return getIntColumnValue(Columns[5]);
  }

  public void setGroupID( Integer GroupID){
    setColumn(Columns[5],GroupID);
  }




  public static IBPermission getStaticInstance(){
    return (IBPermission)getStaticInstance(sClassName);
  }


  }  // Class ArPermission
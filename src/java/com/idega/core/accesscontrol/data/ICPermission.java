package com.idega.core.accesscontrol.data;

import com.idega.data.*;
import com.idega.core.data.GenericGroup;
import java.sql.*;

/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */

public class ICPermission extends GenericEntity {


  private static String sClassName = ICPermission.class.getName();


  public ICPermission() {
    super();
  }

  public ICPermission(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getContextTypeColumnName(),"Context type",true,true,"java.lang.String");
    addAttribute(getContextValueColumnName(),"Context value",true,true,"java.lang.String");
    addAttribute(getPermissionStringColumnName(),"Permission string",true,true,"java.lang.String");
    addAttribute(getPermissionStringValueColumnName(),"Permission string value",true,true,"java.lang.String");
    addAttribute(getPermissionValueColumnName(),"Permission value",true,true,"java.lang.Boolean");
    addAttribute(getGroupIDColumnName(),"GroupID",true,true,"java.lang.Integer");

  }
  public String getEntityName() {
    return "ic_permission";
  }
/*
  public void setDefaultValues(){
    this.setPermissionStringValue("");
  }
*/

  public static String getContextTypeColumnName(){
    return "permission_context_type";
  }

  public String getContextType(){
    return getStringColumnValue(getContextTypeColumnName());
  }

  public void setContextType( String ContextType){
    setColumn(getContextTypeColumnName(),ContextType);
  }


  public static String getContextValueColumnName(){
    return "permission_context_value";
  }

  public String getContextValue(){
    return getStringColumnValue(getContextValueColumnName());
  }

  public void setContextValue( String ContextValue){
    setColumn(getContextValueColumnName(),ContextValue);
  }

  public static String getPermissionStringColumnName(){
    return "permission_string";
  }

  public String getPermissionString(){
    return getStringColumnValue(getPermissionStringColumnName());
  }

  public void setPermissionString( String PermissionString){
    setColumn(getPermissionStringColumnName(),PermissionString);
  }


  public static String getPermissionStringValueColumnName(){
    return "permission_string_value";
  }

  public String getPermissionStringValue(){
    return getStringColumnValue(getPermissionStringValueColumnName());
  }

  public void setPermissionStringValue( String PermissionStringValue){
    setColumn(getPermissionStringValueColumnName(),PermissionStringValue);
  }


  public static String getPermissionValueColumnName(){
    return "permission_value";
  }

  public boolean getPermissionValue(){
    return getBooleanColumnValue(getPermissionValueColumnName());
  }

  public void setPermissionValue( Boolean PermissionStringValue){
    setColumn(getPermissionValueColumnName(),PermissionStringValue);
  }




  public static String getGroupIDColumnName(){
    return "group_id";
  }

  public int getGroupID(){
    return getIntColumnValue(getGroupIDColumnName());
  }

  public void setGroupID( Integer GroupID){
    setColumn(getGroupIDColumnName(),GroupID);
  }




  public static ICPermission getStaticInstance(){
    return (ICPermission)getStaticInstance(ICPermission.class);
  }


  }  // Class ArPermission
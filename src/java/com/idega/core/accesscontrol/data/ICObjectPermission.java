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

public class ICObjectPermission extends GenericEntity {

  private static String sClassName = "com.idega.core.accesscontrol.data.ICObjectPermission";

  public ICObjectPermission() {
    super();
  }

  public ICObjectPermission(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getArObjectIDColumnName(),"Object",true,true,"java.lang.Integer","many-to-one","com.idega.builder.data.ICObject");
    addAttribute(getPermissionTypeColumnName(),"Permission Type",true,true,"java.lang.String");
    addAttribute(getDescripionColumnName(),"Description",true,true,"java.lang.String");

  }

  public String getEntityName() {
    return "ic_object_permission";
  }


  public static String getArObjectIDColumnName(){
    return "ic_object_id";
  }

  public int getArObjectID(){
    return getIntColumnValue(getArObjectIDColumnName());
  }

  public void setArObjectID( Integer ObjectID){
    setColumn(getArObjectIDColumnName(),ObjectID);
  }


  public static String getPermissionTypeColumnName(){
    return "permission_type";
  }

  public String getPermissionType(){
    return getStringColumnValue(getPermissionTypeColumnName());
  }

  public void setPermissionType( String thePermissionType){
    setColumn(getPermissionTypeColumnName(),thePermissionType);
  }


  public static String getDescripionColumnName(){
    return "description";
  }

  public String getDescription(){
    return getStringColumnValue(getDescripionColumnName());
  }

  public void setDescription( String theDescription){
    setColumn(getDescripionColumnName(),theDescription);
  }


  public static ICObjectPermission getStaticInstance(){
    return (ICObjectPermission)getStaticInstance(sClassName);
  }



} // Class ArObjectPermission
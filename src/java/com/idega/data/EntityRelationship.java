package com.idega.data;

import java.util.Map;
import java.util.Hashtable;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

class EntityRelationship {

  private boolean isImplementedWithTable=true;
  private Map columnsMap;
  private String tableName;

  EntityRelationship() {
  }

  void setTableName(String tableName){
    this.tableName=tableName;
  }

  String getTableName(){
    return tableName;
  }

  boolean isImplementedWithTable(){
    return isImplementedWithTable;
  }

  Map getColumnsAndReferencingClasses(){
    return columnsMap;
  }


  void addColumn(String columnName,Class referencingClass){
    if(columnsMap==null){
      columnsMap = new Hashtable();
    }
    columnsMap.put(columnName,referencingClass);
  }
}
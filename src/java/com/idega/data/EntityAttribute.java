//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;



import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class EntityAttribute{

private String name;
//private Object value;
private String longName;
private String relationShip;
//private String storageClassName;
private int storageClassType;
private boolean editable;
private boolean visible;
private String relationShipClassName;
private int maxLength;
private boolean nullable=true;
private String attributeType;
private boolean isPrimaryKey=false;
public static int TYPE_JAVA_LANG_INTEGER=1;
public static int TYPE_JAVA_LANG_STRING=2;
public static int TYPE_JAVA_LANG_BOOLEAN=3;
public static int TYPE_JAVA_LANG_FLOAT=4;
public static int TYPE_JAVA_LANG_DOUBLE=5;
public static int TYPE_JAVA_SQL_DATE=6;
public static int TYPE_JAVA_SQL_TIMESTAMP=7;
public static int TYPE_JAVA_SQL_TIME=8;
public static int TYPE_COM_IDEGA_UTIL_GENDER=9;
protected static int TYPE_COM_IDEGA_DATA_BLOBWRAPPER=10;


	public EntityAttribute(){

	}

	public EntityAttribute(String columnName){
		setName(columnName);
		setLongName(columnName);
		setStorageClassName("java.lang.Integer");
		setRelationShipType("unconnected");
		setEditable(false);
		setVisible(false);
		setRelationShipClassName("");
                setAttributeType("column");
    	setMaxLength(-1);
  	}


	public EntityAttribute(String columnName,Object columnValue){
		setName(columnName);
		setLongName(columnName);
		setStorageClassName(columnValue.getClass().getName());
		setRelationShipType("unconnected");
		setEditable(false);
		setVisible(false);
		setRelationShipClassName("");
   		setAttributeType("column");
		setMaxLength(-1);
   }

	public void setName(String name){
		this.name=name.toLowerCase();
	}

	public String getName(){
		return this.name;
	}


        public void setAttributeType(String attributeType){
          this.attributeType=attributeType;
        }


        public String getAttributeType(){
          return attributeType;
        }


	public void setLongName(String longName){
		this.longName=longName;
	}

	public String getLongName(){
		return longName;
	}

	public void setRelationShipType(String type){
		relationShip=type;
	}

	public String getRelationShipType(){
		return relationShip;
	}


        /**
         * @deprecated replaced with getStorageClassType
         */
	public String getStorageClassName(){
          String className=null;
          int classType=getStorageClassType();
          if(classType==TYPE_JAVA_LANG_INTEGER){
            className="java.lang.Integer";
          }
          else if(classType==TYPE_JAVA_LANG_STRING){
            className="java.lang.String";
          }
          else if(classType==TYPE_JAVA_LANG_BOOLEAN){
            className="java.lang.Boolean";
          }
          else if(classType==TYPE_JAVA_LANG_FLOAT){
            className="java.lang.Float";
          }
          else if(classType==TYPE_JAVA_LANG_DOUBLE){
            className="java.lang.Double";
          }
          else if(classType==TYPE_JAVA_SQL_TIMESTAMP){
            className="java.sql.Timestamp";
          }
          else if(classType==TYPE_JAVA_SQL_DATE){
            className="java.sql.Date";
          }
          else if(classType==TYPE_JAVA_SQL_TIME){
            className="java.sql.Time";
          }
          else if(classType==TYPE_COM_IDEGA_UTIL_GENDER){
            className="com.idega.util.Gender";
          }
          else if(classType==TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
            className="com.idega.data.BlobWrapper";
          }
          return className;

		//return storageClassName;
	}

        /**
         * @deprecated replaced with setStorageClassType
         */
	public void setStorageClassName(String className){
          if(className.equals("java.lang.Integer")){
            setStorageClassType(TYPE_JAVA_LANG_INTEGER);
          }
          else if(className.equals("java.lang.String")){
            setStorageClassType(TYPE_JAVA_LANG_STRING);
          }
          else if(className.equals("java.lang.Boolean")){
            setStorageClassType(TYPE_JAVA_LANG_BOOLEAN);
          }
          else if(className.equals("java.lang.Double")){
            setStorageClassType(TYPE_JAVA_LANG_DOUBLE);
          }
          else if(className.equals("java.lang.Float")){
            setStorageClassType(TYPE_JAVA_LANG_FLOAT);
          }
          else if(className.equals("java.sql.Timestamp")){
            setStorageClassType(TYPE_JAVA_SQL_TIMESTAMP);
          }
          else if(className.equals("java.sql.Date")){
            setStorageClassType(TYPE_JAVA_SQL_DATE);
          }
          else if(className.equals("java.sql.Time")){
            setStorageClassType(TYPE_JAVA_SQL_TIME);
          }
          else if(className.equals("com.idega.util.Gender")){
            setStorageClassType(TYPE_COM_IDEGA_UTIL_GENDER);
          }
          else if(className.equals("com.idega.data.BlobWrapper")){
            setStorageClassType(TYPE_COM_IDEGA_DATA_BLOBWRAPPER);
          }

	  //storageClassName=className;
	}


        public void setStorageClassType(int class_type){
          storageClassType=class_type;
        }

        public int getStorageClassType(){
          return storageClassType;
        }

	public void setEditable(boolean ifEditable){
		editable=ifEditable;
	}

	public boolean getIfEditable(){
		return editable;
	}

	public void setVisible(boolean ifVisible){
		visible=ifVisible;
	}

	public boolean getIfVisible(){
		return visible;
	}

	public String getRelationShipClassName(){
		return relationShipClassName;
	}

	public void setRelationShipClassName(String className){
		relationShipClassName=className;
	}

	public void setMaxLength(int maxLength){
		this.maxLength=maxLength;
	}

	public int getMaxLength(){
		return maxLength;
	}

	public void setNullable(boolean ifNullable){
		nullable=ifNullable;
	}

	public boolean getIfNullable(){
		return nullable;
	}

	public void setAsPrimaryKey(boolean primaryKey){
		isPrimaryKey= primaryKey;
	}

	public boolean isPrimaryKey(){
        return isPrimaryKey;
	}

    public String getColumnName(){
    	return getName();
    }

}

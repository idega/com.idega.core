/*
 * $Id: GenericEntity.java,v 1.29 2001/07/17 19:24:17 tryggvil Exp $
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
import com.idega.util.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public abstract class GenericEntity implements java.io.Serializable {

  private String dataStoreType;
  public Hashtable columns=new Hashtable();
  private static Hashtable theAttributes=new Hashtable();
  private static Hashtable allStaticClasses=new Hashtable();
  private String dataSource;
  private static String defaultString="default";
  private String cachedColumnNameList;
  private String lobColumnName;
  private static boolean useEntityCacher=false;

  private int state;

  protected static int STATE_NEW=0;
  protected static int STATE_IN_SYNCH_WITH_DATASTORE=1;
  protected static int STATE_NOT_IN_SYNCH_WITH_DATASTORE=2;
  protected static int STATE_DELETED=3;


	public GenericEntity() {
		this(defaultString);
	}

	public GenericEntity(String dataSource) {
		setDatasource(dataSource);
                firstLoadInMemoryCheck();
		setDefaultValues();
	}

	public GenericEntity(int id) throws SQLException {
		this(id,defaultString);
	}

	public GenericEntity(int id,String dataSource) throws SQLException {
		//this(dataSource);
                setDatasource(dataSource);
		//setColumn(getIDColumnName(),new Integer(id));
                firstLoadInMemoryCheck();
		//findByPrimaryKey(getID());
                findByPrimaryKey(id);
	}


  private void firstLoadInMemoryCheck() {
    Vector theReturn = (Vector)theAttributes.get(this.getClass().getName());
    if (theReturn == null) {
      theReturn = new Vector();
      theAttributes.put(this.getClass().getName(),theReturn);

      //First store a static instance of this class
      String className = this.getClass().getName();
      try {
        this.allStaticClasses.put(className,(GenericEntity)Class.forName(className).newInstance());
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      //call the initializeAttributes that stores information about columns and relationships
      initializeAttributes();
      setLobColumnName();
      if(EntityControl.getIfEntityAutoCreate()){
         try{
          DatastoreInterface.getInstance(this).createEntityRecord(this);
         }
         catch(Exception ex){
          ex.printStackTrace();
         }
      }
    }
  }

  /**
   * Override this function to set staring Data into the record of the entity at creation time
   */
  public void insertStartData()throws Exception{

  }

	protected String getTableName() {
		return getEntityName();
	}

  /**
   * Subclasses have to implement this method
	 */
	public abstract String getEntityName();

	public abstract void initializeAttributes();

  protected Vector getAttributes() {
    //ties the attribute vector to the subclass of GenericEntity because
    //the theAttributes variable is static.
    Vector theReturn = (Vector)theAttributes.get(this.getClass().getName());

    /*if (theReturn == null) {
      theReturn = new Vector();
      theAttributes.put(this.getClass().getName(),theReturn);
      firstLoadInMemory();
    }*/

    return theReturn;
  }

	public void setID(int id) {
		setColumn(getIDColumnName(),new Integer(id));
	}

	public int getID() {
		return getIntColumnValue(getIDColumnName());
	}

	public Integer getIDInteger() {
		return (Integer)getColumnValue(getIDColumnName());
	}

	/**
	 * default unimplemented function, gets the name of the record from the datastore
	 */
	public String getName() {
		return null;
	}

  public BlobWrapper getEmptyBlob(String columnName) {
    return new BlobWrapper(this,columnName);
  }

	/**
	 * default unimplemented function, sets the name of the record in the datastore
	 */
	public void setName(String name) {
		//does nothing
	}

	public String toString() {
		return Integer.toString(getID());
	}

	/**
	 * @deprecated Replaced with addAttribute()
	 */
	public void addColumnName(String columnName) {
		addAttribute(columnName);
	}

	public void addAttribute(String attributeName) {
            EntityAttribute attribute;
            attribute = new EntityAttribute(attributeName.toLowerCase());
            attribute.setAsPrimaryKey(true);
            attribute.setNullable(false);
            addAttribute(attribute);
	}

	/**
	 * @deprecated Replaced with addAttribute()
	 */
	public void addColumnName(String columnName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName) {
                addAttribute(columnName,longName,ifVisible,ifEditable,storageClassName);
        }

	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName) {
          try{
            addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName));
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
        }

	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass) {
            EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
            attribute.setLongName(longName);
            attribute.setVisible(ifVisible);
            attribute.setEditable(ifEditable);
            attribute.setStorageClass(storageClass);
            addAttribute(attribute);
	}

	/**
	 * Added by Eirikur Hrafnsson
	 *
	 */
       public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,int maxLength) {
          try{
            addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),maxLength);
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
       }


	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,int maxLength) {
		EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		addAttribute(attribute);
	}

	/**
          * @deprecated Replaced with addAttribute()
 	  */
	public void addColumnName(String columnName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,String relationShipType,String relationShipClassName) {
		addAttribute(columnName,longName,ifVisible,ifEditable,storageClassName,relationShipType,relationShipClassName);
	}


	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,String relationShipType,String relationShipClassName) {
          try{
            addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),relationShipType,Class.forName(relationShipClassName));
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
        }

	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,String relationShipType,Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}

	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,int maxLength,String relationShipType,String relationShipClassName){
          try{
            addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),maxLength,relationShipType,Class.forName(relationShipClassName));
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
        }

	public void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,int maxLength,String relationShipType,Class relationShipClass){
		EntityAttribute attribute = new EntityAttribute(attributeName.toLowerCase());
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		addAttribute(attribute);
	}


	public void addAttribute(EntityAttribute attribute){
            getAttributes().addElement(attribute);
	}

        public void addLanguageAttribute(){
          this.addAttribute(getLanguageIDColumnName(),"Tungumál", true, true, "java.lang.Integer","one_to_one","com.idega.core.localisation.data.Language");
        }


	/**
	 * @deprecated Replaced with getAttribute()
	 */
	public EntityAttribute getColumn(String columnName){
		return getAttribute(columnName);
	}

	public EntityAttribute getAttribute(String attributeName){
		//return (EntityAttribute) columns.get(columnName.toLowerCase());
		EntityAttribute theReturn = null;
		EntityAttribute tempColumn = null;
		for (Enumeration enumeration=getAttributes().elements();enumeration.hasMoreElements();){
			tempColumn = (EntityAttribute)enumeration.nextElement();
			if (tempColumn.getColumnName().toLowerCase().equals(attributeName.toLowerCase())){
				theReturn = tempColumn;
			}
		}
/*    if(theReturn==null){
      System.err.println("Error in "+this.getClass().getName()+".getAttribute(): ColumnName='"+attributeName+"' exists in table but not in Entity Class");
    }*/
		return theReturn;
	}

	public void addRelationship(String relationshipName,String relationshipType,String relationshipClassName){
          try{
                EntityAttribute attribute = new EntityAttribute();
		attribute.setName(relationshipName);
		attribute.setAttributeType("relationship");
		attribute.setRelationShipClass(Class.forName(relationshipClassName));
		addAttribute(attribute);
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
	}

	/**
	*Constructs an array of GenericEntities through an int Array (containing rows of id's from the datastore) - uses the findByPrimaryKey method to instanciate a new object fetched from the database
	**/
	public GenericEntity[] constructArray(int[] id_array){
		GenericEntity[] returnArr = null;
		try{
			returnArr =  (GenericEntity[]) java.lang.reflect.Array.newInstance(this.getClass(),id_array.length);
			for (int i = 0; i < id_array.length ; i++){
				returnArr[i] = (GenericEntity) Class.forName(this.getClass().getName()).newInstance();
				returnArr[i].findByPrimaryKey(id_array[i]);
			}
		}
		catch(Exception ex){
			System.err.println("There was an error in GenericEntity.constructArray(int[] id_array): "+ex.getMessage());
		}
		return returnArr;
	}


	/**
	*Constructs an array of GenericEntities through a String Array (containing only int's for the rows of id's from the datastore) - uses the findByPrimaryKey method to instanciate a new object fetched from the database
	**/
	public GenericEntity[] constructArray(String[] id_array){

		GenericEntity[] returnArr = null;
		try{
			returnArr = (GenericEntity[]) java.lang.reflect.Array.newInstance(this.getClass(),id_array.length);
			for (int i = 0; i < id_array.length ; i++){
				returnArr[i] = (GenericEntity) Class.forName(this.getClass().getName()).newInstance();
				returnArr[i].findByPrimaryKey(Integer.parseInt(id_array[i]));
			}
		}
		catch(Exception ex){
			System.err.println("There was an error in GenericEntity.constructArray(String[] id_array): "+ex.getMessage());
		}
		return returnArr;
	}


	protected void setValue(String columnName,Object columnValue){
		if (columnValue!=null){
			columns.put(columnName.toLowerCase(),columnValue);
		        this.setEntityState(this.STATE_NOT_IN_SYNCH_WITH_DATASTORE);
                }
	}

	protected Object getValue(String columnName){
		return columns.get(columnName.toLowerCase());
	}

        public void removeFromColumn(String columnName){
          columns.remove(columnName.toLowerCase());
        }

	public void setColumn(String columnName,Object columnValue){
		if (this.getRelationShipClassName(columnName).equals("")){
			setValue(columnName,columnValue);
		}
		else{
			setValue(columnName,((GenericEntity)columnValue).getIDInteger());
		}
	}

	public void setColumn(String columnName,int columnValue){
		setValue(columnName,new Integer(columnValue));
	}

	public void setColumn(String columnName,Integer columnValue){
		setValue(columnName,columnValue);
	}

	public void setColumn(String columnName,float columnValue){
		setValue(columnName,new Float(columnValue));
	}

	public void setColumn(String columnName,Float columnValue){
		setValue(columnName,columnValue);
	}

	public void setColumn(String columnName,boolean columnValue){
		setValue(columnName,new Boolean(columnValue));
	}

	public void setColumn(String columnName,Boolean columnValue){
		setValue(columnName,columnValue);
	}

  /**
   * The Outputstream must be completele written to when insert() or update() is executed on the Entity Class
   */
  public OutputStream getColumnOutputStream(String columnName){
    BlobWrapper wrapper = getBlobColumnValue(columnName);
    if(wrapper==null){
      wrapper = new BlobWrapper(this,columnName);
      setColumn(columnName,wrapper);
    }
    return wrapper.getOutputStreamForBlobWrite();
  }


  public void setColumn(String columnName,InputStream streamForBlobWrite){
    BlobWrapper wrapper = getBlobColumnValue(columnName);
    if(wrapper!=null){
       wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
    }
    else{
      wrapper = new BlobWrapper(this,columnName);
      wrapper.setInputStreamForBlobWrite(streamForBlobWrite);
      setColumn(columnName,wrapper);
    }
  }

  public BlobWrapper getBlobColumnValue(String columnName){
    return (BlobWrapper) getColumnValue(columnName);
  }

  public InputStream getInputStreamColumnValue(String columnName)throws Exception{
    BlobWrapper wrapper = getBlobColumnValue(columnName);
    if(wrapper==null){
      wrapper = new BlobWrapper(this,columnName);
      this.setColumn(columnName,wrapper);
    }
    return wrapper.getBlobInputStream();
  }

	public Object getColumnValue(String columnName){
		Object returnObj = null;
		Object value = getValue(columnName);
		if (value instanceof com.idega.data.GenericEntity){
			returnObj = value;
		}
		else if (value instanceof java.lang.Integer){
			if (!(getRelationShipClassName(columnName).equals(""))){
				if (getRelationShipClassName(columnName).indexOf("idega") != -1){
					try{
						returnObj = Class.forName(getRelationShipClassName(columnName)).newInstance();
						((GenericEntity)returnObj).findByPrimaryKey(((Integer)value).intValue());
					}
					catch(Exception ex){
						System.err.println("There was an error in GenericEntity.getColumnValue(String columnName): "+ex.getMessage());
						ex.printStackTrace(System.err);
					}
					finally{

					}
				}
				else{
				}
			}
			else{
				returnObj = value;
			}
		}
		else{
			returnObj= value;
		}
		if (returnObj== null){
		}
		else{
		}

		return returnObj;
	}

	public String getStringColumnValue(String columnName){
		if (getColumnValue(columnName) != null){
			if(getStorageClassName(columnName).equals("java.lang.Boolean")){
				if (((Boolean)getColumnValue(columnName)).booleanValue() == true){
					return "Y";
				}
				else{
					return "N";
				}
			}
			else{
				return getColumnValue(columnName).toString();
			}
		}
		else{
			return null;
		}
	}

	public char getCharColumnValue(String columnName){
		Character tempChar = (Character) getColumnValue(columnName);

		return tempChar.charValue();
	}

	public float getFloatColumnValue(String columnName){
		Float tempFloat = (Float) getColumnValue(columnName);
		if (tempFloat != null){
			return tempFloat.floatValue();
		}
		else{
			return -1;
		}
	}

	public double getDoubleColumnValue(String columnName){
		Double tempDouble = (Double) getColumnValue(columnName);
		if (tempDouble != null){
			return tempDouble.doubleValue();
		}
		else{
			return -1;
		}
	}


        public Integer getIntegerColumnValue(String columnName){
            return (Integer) getValue(columnName);
        }

	public int getIntColumnValue(String columnName){
		Integer tempInt = (Integer) getValue(columnName);
    if (tempInt != null){
      return tempInt.intValue();
    }
    else{
      return -1;
    }
	}

	public boolean getBooleanColumnValue(String columnName){
		Boolean tempBool = (Boolean) getValue(columnName);
    if (tempBool != null){
      return tempBool.booleanValue();
    }
    else{
      return false;
    }
	}


	public void setLongName(String columnName,String longName){
		getColumn(columnName).setLongName(longName);
	}

	public String getLongName(String columnName){
		return getColumn(columnName).getLongName();
	}

	public void setRelationShipType(String columnName,String type){
		getColumn(columnName).setRelationShipType(type);
	}

	public String getRelationShipType(String columnName){
		return getColumn(columnName).getRelationShipType();
	}

  public int getStorageClassType(String columnName){
    EntityAttribute attribute = getColumn(columnName);
    if(attribute!=null){
      return attribute.getStorageClassType();
    }
    else{
      return 0;
    }
  }

  /**
   * @deprecated replaced with getStorageClassType
   */
	public String getStorageClassName(String columnName){
            String theReturn = "";
            if (getColumn(columnName) != null){
  		  theReturn = getColumn(columnName).getStorageClassName();
          }
            return theReturn;
	}

        /**
         * @deprecated replaced with setStorageClassType
         */
	public void setStorageClassName(String columnName,String className){
            try{
		getColumn(columnName).setStorageClass(Class.forName(className));
            }
            catch(ClassNotFoundException e){
              throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
            }
	}

        public void setStorageClassType(String columnName,int classType){
            getColumn(columnName).setStorageClassType(classType);
        }

	public void setEditable(String columnName,boolean ifEditable){
		getColumn(columnName).setEditable(ifEditable);
	}

	public boolean getIfEditable(String columnName){
		return getColumn(columnName).getIfEditable();
	}

	public void setVisible(String columnName,boolean ifVisible){
		getColumn(columnName).setVisible(ifVisible);
	}

	public boolean getIfVisible(String columnName){
		return getColumn(columnName).getIfVisible();
	}

	public String getRelationShipClassName(String columnName){
                String theReturn = "";
                if (getColumn(columnName) != null){
  		  theReturn = getColumn(columnName).getRelationShipClassName();
                }
            return theReturn;
	}

	public void setRelationShipClassName(String columnName,String className){
          try{
		getColumn(columnName).setRelationShipClass(Class.forName(className));
          }
          catch(ClassNotFoundException e){
            throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
          }
	}


 	public void setMaxLength(String columnName,int maxLength){
		getColumn(columnName).setMaxLength(maxLength);
	}

	public int getMaxLength(String columnName){
		return getColumn(columnName).getMaxLength();
	}

	public void setNullable(String columnName,boolean ifNullable){
		getColumn(columnName).setNullable(ifNullable);
	}

	public boolean getIfNullable(String columnName){
		return getColumn(columnName).getIfNullable();
	}

          public boolean getIfUnique(String columnName){
              return getColumn(columnName).getIfUnique();
          }

          public void setUnique(String columnName,boolean ifUnique){
            getColumn(columnName).setUnique(ifUnique);
          }

        public void setAsPrimaryKey(String columnName,boolean ifPrimaryKey){
          getColumn(columnName).setAsPrimaryKey(ifPrimaryKey);
        }

        public boolean isPrimaryKey(String columnName){
            return getColumn(columnName).isPrimaryKey();
        }

	/**
	 * Gets a databaseconnection identified by the datasourceName
	 */
	public Connection getConnection(String datasourceName)throws SQLException{
		return ConnectionBroker.getConnection(datasourceName);
	}

	/**
	 * Gets the default database connection
	 */
	public Connection getConnection()throws SQLException{
		return ConnectionBroker.getConnection(getDatasource());
	}

	/**
	 * Frees the connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(String datasourceName,Connection connection){
		ConnectionBroker.freeConnection(datasourceName,connection);
	}

	/**
	 * Frees the default connection used, must be done after using a databaseconnection
	 */
	public void freeConnection(Connection connection){
		ConnectionBroker.freeConnection(getDatasource(),connection);
	}


	public void setDatasource(String dataSource){
		this.dataSource=dataSource;
	}

	public String getDatasource(){
		return dataSource;
	}

	public String getIDColumnName(){
		if (getTableName().endsWith("_")){
			return getTableName()+"id";
		}
		else{
			return getTableName()+"_id";
		}
	}

        public static String getLanguageIDColumnName(){
          return "language_id";
        }

	protected String getAllColumnsAndValues(){
		String returnString="";
		for (Enumeration e = columns.keys(); e.hasMoreElements();){
		//for (Enumeration e = columns.elements(); e.hasMoreElements();){
			String ColumnName = (String)e.nextElement();
			if (isValidColumnForUpdateList(ColumnName)){
				if (returnString.equals("")){
					returnString = returnString + ColumnName + "='"+getStringColumnValue(ColumnName)+"'";
				}
				else{
					returnString = returnString + "," + ColumnName + "='"+getStringColumnValue(ColumnName)+"'";
				}
			}
		}

		return returnString;
	}


	protected String getAllColumnsAndQuestionMarks(){
		StringBuffer returnString= new StringBuffer("");
                String[] names = getColumnNames();
                String questionmark = "=?";

		for(int i=0;i<names.length;i++){
                //for (Enumeration e = columns.keys(); e.hasMoreElements();){
		//for (Enumeration e = columns.elements(); e.hasMoreElements();){
			//String ColumnName = (String)e.nextElement();
                        String ColumnName = names[i];

			if (isValidColumnForUpdateList(ColumnName)){
				if (returnString.toString().equals("")){
					returnString.append(ColumnName);
                                        returnString.append(questionmark);
				}
				else{
					returnString.append(',');
                                        returnString.append(ColumnName);
                                        returnString.append(questionmark);;
				}
			}
		}

		return returnString.toString();
	}


	public String[] getColumnNames(){

		Vector vector = new Vector();
		int i=0;
		//for (Enumeration e = columns.keys(); e.hasMoreElements();i++){
		for (Enumeration e = getAttributes().elements(); e.hasMoreElements();i++){
			EntityAttribute temp = (EntityAttribute)e.nextElement();
                        if (temp.getAttributeType().equals("column")){
                          vector.addElement(temp.getColumnName().toLowerCase());
		        }
                }
		if (vector != null){
			vector.trimToSize();
			return (String[]) vector.toArray(new String[0]);
			//return vector.toArray(new GenericEntity[0]);
		}
		else{
			return new String[0];
		}
	}

	public String[] getVisibleColumnNames(){

		Vector theColumns = new Vector();
                Vector theAttributes = getAttributes();
		for (Enumeration e = getAttributes().elements(); e.hasMoreElements();){
		//for (Enumeration e = columns.elements(); e.hasMoreElements();){

			String tempName = (String)((EntityAttribute)e.nextElement()).getColumnName();
			if (getIfVisible(tempName)){
				theColumns.addElement(tempName);
			}
		}
		return (String[])theColumns.toArray(new String[0]);
	}

	public String[] getEditableColumnNames(){

		Vector theColumns = new Vector();

		for (Enumeration e = columns.keys(); e.hasMoreElements();){
		//for (Enumeration e = columns.elements(); e.hasMoreElements();){

			String tempName = (String)e.nextElement();
			if (getIfEditable(tempName)){
				theColumns.addElement(tempName);
			};
		}
		return (String[])theColumns.toArray(new String[0]);
	}

	/**
	*Used to generate the ?,? mark list for preparedstatement
	**/
	protected String getQuestionmarksForColumns(){
		String returnString = "";
		String[] names = getColumnNames();
		for (int i = 0; i < names.length; i++){
			if(isValidColumnForInsertList(names[i])){
      //if (!isNull(names[i])){
				if (returnString.equals("")){
					returnString = 	"?";
				}
				else{
					returnString = 	returnString + ",?";
				}
			}
		}
		return returnString;
	}


  boolean isValidColumnForUpdateList(String columnName){
      if (isNull(columnName)){
        return false;
      }
      else{
        if(getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
          BlobWrapper wrapper = (BlobWrapper)getColumnValue(columnName);
          if(wrapper==null){
            return false;
          }
          else{
            return wrapper.isReadyForUpdate();
          }
        }
        return true;
      }
  }

  boolean isValidColumnForInsertList(String columnName){
      if (isNull(columnName)){
        return false;
      }
      else{
        if(getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
          return false;
        }
        return true;
      }
  }


	protected String getCommaDelimitedColumnNames(){
    String newCachedColumnNameList = getStaticInstance().cachedColumnNameList;
		if(newCachedColumnNameList==null){
      String returnString = "";
      String[] names = getColumnNames();
      for (int i = 0; i < names.length; i++){
        if (isValidColumnForInsertList(names[i])){
          if (returnString.equals("")){
            returnString = 	names[i];
          }
          else{
            returnString = 	returnString + "," + names[i];
          }
        }
      }
      newCachedColumnNameList = returnString;
    }
		return newCachedColumnNameList;
	}




	protected String getCommaDelimitedColumnValues(){
		String returnString = "";
		String[] names = getColumnNames();
		for (int i = 0; i < names.length; i++){
			if (isValidColumnForInsertList(names[i])){
				if (returnString.equals("")){
					returnString = 	"'"+getStringColumnValue(names[i])+"'";
				}
				else{
					returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
				}
			}
		}
		return returnString;
	}


	public boolean isNull(String columnName){
		/*if (columns.get(columnName) instanceof java.lang.String){
			String tempString = (String)columns.get(columnName);
			if (tempString.equals("idega_special_null")){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}*/
/*
		if (getColumnValue(columnName) == null){
			return true;
		}
		else{
			return false;
		}
*/
                if (columns.get(columnName.toLowerCase())== null){
                  return true;
                }
                else{
                  return false;
                }

	}

	/*
	 * Returns the type of the underlying datastore - returns: "mysql", "interbase", "oracle", "unimplemented"
	 */
	 /*
	public String getDataStoreType(Connection connection){
		if (dataStoreType == null){
			if (connection != null){
				if (connection.getClass().getName().indexOf("oracle") != -1 ){
					dataStoreType = "oracle";
				}
				else if (connection.getClass().getName().indexOf("interbase") != -1 ){
					dataStoreType = "interbase";
				}
				else if (connection.getClass().getName().indexOf("mysql") != -1 ){
					dataStoreType =  "mysql";
				}

				else{
					dataStoreType = "unimplemented";
				}
			}
			else{
				dataStoreType =  "";
			}
		}
		return dataStoreType;
	}*/


	/**
	**Override this function to set default values to columns if they have no set values
	**/
	public void setDefaultValues(){
		//default implementation does nothing
	}


	/**
	*Inserts this entity as a record into the datastore
	*/
  public void insert()throws SQLException{
    try{
      DatastoreInterface.getInstance(this).insert(this);
    }
    catch(Exception ex){
      if(ex instanceof SQLException){
        ex.printStackTrace();
        throw (SQLException)ex.fillInStackTrace();
      }
      else{
        ex.printStackTrace();
      }
    }
  }

    /**
    *Inserts this entity as a record into the datastore
    */
  public void insert(Connection c)throws SQLException{
    try{
      DatastoreInterface.getInstance(c).insert(this,c);
    }
    catch(Exception ex){
      if(ex instanceof SQLException){
        ex.printStackTrace();
        throw (SQLException)ex.fillInStackTrace();
      }
    }
  }


  /*
  public void insert()throws SQLException{
		EntityControl.insert(this);
	}*/

	/**
	*Updates the entity in the datastore
	*/
  public void update()throws SQLException{
      try{
        DatastoreInterface.getInstance(this).update(this);
      }
      catch(Exception ex){
        if(ex instanceof SQLException){
          ex.printStackTrace();
          throw (SQLException)ex.fillInStackTrace();
        }
      }
  }

	/**
	*Updates the entity in the datastore
	*/
  public void update(Connection c) throws SQLException {
    try {
      DatastoreInterface.getInstance(c).update(this,c);
    }
    catch(Exception ex) {
      if(ex instanceof SQLException) {
        ex.printStackTrace();
        throw (SQLException)ex.fillInStackTrace();
      }
    }
  }

	/*
  public void update()throws SQLException{
		EntityControl.update(this);
	}
  */

	public void delete()throws SQLException{
		EntityControl.delete(this);
	}

        public void delete(Connection c) throws SQLException {
          try {
            EntityControl.delete(this,c);
          }
          catch(Exception ex) {
            if(ex instanceof SQLException) {
              ex.printStackTrace();
              throw (SQLException)ex.fillInStackTrace();
            }
          }

        }

	public void deleteMultiple(String columnName,String stringColumnValue)throws SQLException{
		EntityControl.deleteMultiple(this,columnName,stringColumnValue);
	}

	/**
	*Deletes everything from the table of this entity
	**/
	public void clear()throws SQLException{
		EntityControl.clear(this);
	}


	/**
	**Function to populate a column through a string representation
	**/
	public void setStringColumn(String columnName,String columnValue){
                int classType=getStorageClassType(columnName);
		if (classType==EntityAttribute.TYPE_JAVA_LANG_INTEGER){
			if (columnValue != null){
				setColumn(columnName,new Integer(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_STRING){
			if (columnValue != null){
				setColumn(columnName,columnValue);
			}

		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_BOOLEAN){
			if (columnValue != null){
				if (columnValue.equals("Y")){
					setColumn(columnName,new Boolean(true));
				}
				else if (columnValue.equals("N")){
					setColumn(columnName,new Boolean(false));
				}
				else{
					setColumn(columnName,new Boolean(false));
				}
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_FLOAT){
			if (columnValue != null){
				setColumn(columnName,new Float(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_DOUBLE){
			if (columnValue != null){
				setColumn(columnName,new Double(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP){
			if (columnValue != null){
				setColumn(columnName,java.sql.Timestamp.valueOf(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_DATE){
			if (columnValue != null){
				setColumn(columnName,java.sql.Date.valueOf(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIME){
			if (columnValue != null){
				setColumn(columnName,java.sql.Time.valueOf(columnValue));
			}
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER){
			if (columnValue != null){
				setColumn(columnName,columnValue.toString());
			}
		}
	}


	protected void fillColumn(String columnName,ResultSet RS)throws SQLException{
		int classType = getStorageClassType(columnName);

                if (classType==EntityAttribute.TYPE_JAVA_LANG_INTEGER){
			//if (RS.getInt(columnName) != -1){
				setColumn(columnName.toLowerCase(),new Integer(RS.getInt(columnName)));
			//}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_STRING){
			if (RS.getString(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getString(columnName));
			}

		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_BOOLEAN){
			String theString = RS.getString(columnName);
                        if (theString != null){
				if (theString.equals("Y")){
					setColumn(columnName.toLowerCase(),new Boolean(true));
				}
				else if (theString.equals("N")){
					setColumn(columnName.toLowerCase(),new Boolean(false));
				}
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_FLOAT){
			//if (RS.getFloat(columnName) != 0){
				setColumn(columnName.toLowerCase(),new Float(RS.getFloat(columnName)));
			//}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_DOUBLE){
			double doble = RS.getDouble(columnName);
                        //if (doble != 0){
				setColumn(columnName.toLowerCase(),new Double(doble));
			//}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP){
			if (RS.getTimestamp(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getTimestamp(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_DATE){
			if (RS.getDate(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getDate(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIME){
			java.sql.Date date = RS.getDate(columnName);
                        if (date != null){
				setColumn(columnName.toLowerCase(),date);
			}
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
			/*if (RS.getDate(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getTime(columnName));
			}*/
                        setColumn(columnName.toLowerCase(),getEmptyBlob(columnName));
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER){
			String gender = RS.getString(columnName);
                        if (gender != null){
				setColumn(columnName.toLowerCase(),new Gender(gender));
			}
		}

	}


	public void findByPrimaryKey(int id)throws SQLException{
                setColumn(getIDColumnName(),new Integer(id));
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("select * from ");
                        buffer.append(getTableName());
                        buffer.append(" where ");
                        buffer.append(getIDColumnName());
                        buffer.append("=");
                        buffer.append(id);

                        ResultSet RS = Stmt.executeQuery(buffer.toString());

			//ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+" where "+getIDColumnName()+"="+id);

                        RS.next();
			String[] columnNames = getColumnNames();
			for (int i = 0; i < columnNames.length; i++){
				try{
                                  if (RS.getString(columnNames[i]) != null){
				  	fillColumn(columnNames[i],RS);
			          }
                                }
                                catch(SQLException ex){
                                  //NOCATH
                                  try{
                                    if (RS.getString(columnNames[i].toUpperCase()) != null){
				  	fillColumn(columnNames[i],RS);
			            }
                                  }
                                  catch(SQLException exe){
                                    try{
                                      if (RS.getString(columnNames[i].toLowerCase()) != null){
				    	  fillColumn(columnNames[i],RS);
			                }
                                      }
                                    catch(SQLException exep){
                                         System.err.println("Exception in "+this.getClass().getName()+" findByPrimaryKey, RS.getString( "+columnNames[i]+" ) not found: "+exep.getMessage());
                                          //exep.printStackTrace(System.err);
                                    }
                                  }

                                }

			}
			RS.close();

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}
	}



	public String getNameOfMiddleTable(GenericEntity entity1,GenericEntity entity2){
            return EntityControl.getNameOfMiddleTable(entity1,entity2);

        }



	public GenericEntity[] findRelated(GenericEntity entity)throws SQLException{
		String tableToSelectFrom = getNameOfMiddleTable(entity,this);
                StringBuffer buffer=new StringBuffer();
                buffer.append("select * from ");
                buffer.append(tableToSelectFrom);
                buffer.append(" where ");
                buffer.append(this.getIDColumnName());
                buffer.append("=");
                buffer.append(this.getID());
                String SQLString=buffer.toString();

                return findRelated(entity,SQLString);
	}

	public GenericEntity[] findReverseRelated(GenericEntity entity)throws SQLException{
		String tableToSelectFrom = getNameOfMiddleTable(this,entity);
		String SQLString="select * from "+tableToSelectFrom+" where "+this.getIDColumnName()+"="+this.getID();
		return findRelated(entity,SQLString);
	}

	protected GenericEntity[] findRelated(GenericEntity entity,String SQLString)throws SQLException{
		Connection conn= null;
		Statement Stmt= null;
		Vector vector = new Vector();
		String tableToSelectFrom = "";
		if (entity.getTableName().endsWith("_")){
			tableToSelectFrom = entity.getTableName()+this.getTableName();
		}
		else{
			tableToSelectFrom = entity.getTableName()+"_"+this.getTableName();
		}

		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS.next()){

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(entity.getClass().getName()).newInstance();
					tempobj.findByPrimaryKey(RS.getInt(entity.getIDColumnName()));
				}
				catch(Exception ex){
					System.err.println("There was an error in GenericEntity.findRelated(GenericEntity entity,String SQLString): "+ex.getMessage());

				}

				vector.addElement(tempobj);

			}
			RS.close();

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

		if (vector != null){
			vector.trimToSize();
			return (GenericEntity[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(entity.getClass(),0));
			//return vector.toArray(new GenericEntity[0]);
		}
		else{
			return null;
		}
	}

	/**
	*Finds all instances of the current object in the otherEntity
	**/
	public GenericEntity[] findAssociated(GenericEntity otherEntity)throws SQLException{
		return otherEntity.findAll("select * from "+otherEntity.getTableName()+" where "+this.getIDColumnName()+"='"+this.getID()+"'");
	}

	public GenericEntity[] findAssociatedOrdered(GenericEntity otherEntity,String column_name)throws SQLException{
		return otherEntity.findAll("select * from "+otherEntity.getTableName()+" where "+this.getIDColumnName()+"='"+this.getID()+"' order by "+column_name+"");
	}

	public GenericEntity[] findAll()throws SQLException{
		return findAll("select * from "+getTableName());
	}


	public GenericEntity[] findAllOrdered(String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName);
	}

 	public GenericEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnDescendingOrdered(String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName+" desc");
	}

 	public GenericEntity[] findAllByColumnDescendingOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName+" desc");
	}

 	public GenericEntity[] findAllDescendingOrdered(String orderByColumnName)throws SQLException{
		return findAll("select * from "+getTableName()+" order by "+orderByColumnName+" desc");
	}

	public GenericEntity[] findAllByColumn(String columnName, String toFind)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName+" like '"+toFind+"'");
	}

	public GenericEntity[] findAllByColumn(String columnName, int toFind)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName+" like '"+Integer.toString(toFind)+"'");
	}

        public GenericEntity[] findAllByColumn(String columnName1, String toFind1,String columnName2, String toFind2, String columnName3, String toFind3)throws SQLException{
 		return findAll("select * from "+getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' and "+columnName3+" like '"+toFind3+"'");
 	}

	public GenericEntity[] findAllByColumn(String columnName1, String toFind1,String columnName2, String toFind2)throws SQLException{
		return findAll("select * from "+getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"'");
	}

        public int getNumberOfRecords(String columnName, String columnValue)throws SQLException{
		return getNumberOfRecords("select count(*) from "+getTableName()+" where "+columnName+" like '"+columnValue+"'");
	}

        public int getNumberOfRecordsRelated(GenericEntity entity)throws SQLException{
            String tableToSelectFrom = getNameOfMiddleTable(entity,this);
            String SQLString="select count(*) from "+tableToSelectFrom+" where "+this.getIDColumnName()+"="+this.getID();
            //System.out.println(SQLString);
            return getNumberOfRecords(SQLString);
	}

        public int getNumberOfRecordsReverseRelated(GenericEntity entity)throws SQLException{
            String tableToSelectFrom = getNameOfMiddleTable(this, entity);
            String SQLString="select count(*) from "+tableToSelectFrom+" where "+this.getIDColumnName()+"="+this.getID();
            //System.out.println(SQLString);
            return getNumberOfRecords(SQLString);
	}

        public int getNumberOfRecords()throws SQLException{
            return getNumberOfRecords("select count(*) from "+getTableName());
        }

        public int getNumberOfRecords(String CountSQLString)throws SQLException {
            return getIntTableValue(CountSQLString);
        }

        public int getNumberOfRecords(String columnName, String Operator ,String columnValue)throws SQLException{
		return getNumberOfRecords("select count(*) from "+getTableName()+" where "+columnName+" "+Operator+" "+columnValue);
	}

        public int getMaxColumnValue(String columnName)throws SQLException {
            return getIntTableValue("select max("+columnName+") from "+getTableName());
        }

        public int getMaxColumnValue(String columnToGetMaxFrom, String columnCondition, String columnConditionValue)throws SQLException {
            return getIntTableValue("select max("+columnToGetMaxFrom+") from "+getTableName()+" where "+columnCondition+" = '"+columnConditionValue+"'");
        }

        public int getIntTableValue(String CountSQLString)throws SQLException {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            int recordCount = -1;
            try {
                conn = getConnection(this.getDatasource());
                stmt = conn.createStatement();
                rs = stmt.executeQuery(CountSQLString);
                if(rs.next())
                    recordCount = rs.getInt(1);
                rs.close();

                //System.out.println(SQLString+"\n");

            }
            catch(SQLException e) {
                throw new SQLException("There was an error in GenericEntity.getNumberOfRecords \n"+e.getMessage());
            }
            catch(Exception e) {
                System.err.println("There was an error in GenericEntity.getNumberOfRecords "+e.getMessage());
            }
            finally{
                if(stmt != null){
                    stmt.close();
                }
                if (conn != null){
                    freeConnection(getDatasource(),conn);
                }
            }
            return recordCount;
        }


        public GenericEntity[] findAll(String SQLString)throws SQLException{
          return findAll(SQLString,-1);
        }

	public GenericEntity[] findAll(String SQLString,int returningNumberOfRecords)throws SQLException{
	/*
        	Connection conn= null;
		Statement Stmt= null;
		ResultSetMetaData metaData;
		Vector vector = new Vector();
                boolean check=true;
		//Vector theIDs = new Vector();
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			metaData = RS.getMetaData();
                        int count = 1;
			while (RS.next() && check){
                          count++;
                          if(returningNumberOfRecords!=-1){
                            if(count>returningNumberOfRecords){
                              check=false;
                            }
                          }

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(this.getClass().getName()).newInstance();
				}
				catch(Exception ex){
					System.err.println("There was an error in GenericEntity.findAll "+ex.getMessage());
					ex.printStackTrace(System.err);
				}
				if(tempobj != null){
					for (int i = 1; i <= metaData.getColumnCount(); i++){


						if ( RS.getObject(metaData.getColumnName(i)) != null){

							//System.out.println("ColumName "+i+": "+metaData.getColumnName(i));
							tempobj.fillColumn(metaData.getColumnName(i),RS);
						}
					}

				}
				vector.addElement(tempobj);

			}
			RS.close();

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}
		/*
		for (Enumeration enum = theIDs.elements();enum.hasMoreElements();){
			Integer tempInt = (Integer) enum.nextElement();
			vector.addElement(new GenericEntity(tempInt.intValue()));
		}*/


                List list = EntityFinder.findAll(this,SQLString,returningNumberOfRecords);

		if (list != null){
			return (GenericEntity[]) list.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
			//return vector.toArray(new GenericEntity[0]);
		}
		else{
                        //Provided for backwards compatability where there was almost never returned null if
                        //there was nothing found
                        return (GenericEntity[]) java.lang.reflect.Array.newInstance(this.getClass(),0);
		}
	}


	/**
	**Default insert behavior with a many-to-many relationship
	**/
	public void addTo(GenericEntity entityToAddTo)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+") values("+getID()+","+entityToAddTo.getID()+")");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}



	public void addTo(GenericEntity entityToAddTo,String extraColumnName,String extraColumnValue)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+","+extraColumnName+") values('"+getID()+"','"+entityToAddTo.getID()+"','"+extraColumnValue+"')");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}

	public void addTo(GenericEntity entityToAddTo,String extraColumnName,String extraColumnValue,String extraColumnName1,String extraColumnValue1)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+","+extraColumnName+","+extraColumnName1+") values('"+getID()+"','"+entityToAddTo.getID()+"','"+extraColumnValue+"','"+extraColumnValue1+"')");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}


	public void addTo(GenericEntity entityToAddTo,String extraColumnName,String extraColumnValue,String extraColumnName1,String extraColumnValue1,String extraColumnName2,String extraColumnValue2)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+","+extraColumnName+","+extraColumnName1+","+extraColumnName2+") values('"+getID()+"','"+entityToAddTo.getID()+"','"+extraColumnValue+"','"+extraColumnValue1+"','"+extraColumnValue2+"')");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}

	/**
	**Default remove behavior with a many-to-many relationship
        ** deletes only one line in middle table if the genericentity wa consructed with a value
	**/
	public void removeFrom(GenericEntity entityToRemoveFrom)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
                String qry = "";
		try{
                        int i = 0;
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();

                        if( (entityToRemoveFrom.getID()==-1) || (entityToRemoveFrom.getID()==0))//removing all in middle table
                          qry = "delete from "+getNameOfMiddleTable(entityToRemoveFrom,this)+" where "+this.getIDColumnName()+"='"+this.getID()+"'";
                        else// just removing this particular one
                          qry = "delete from "+getNameOfMiddleTable(entityToRemoveFrom,this)+" where "+this.getIDColumnName()+"='"+this.getID()+"' AND "+entityToRemoveFrom.getIDColumnName()+"='"+entityToRemoveFrom.getID()+"'";

                        //  System.out.println("GENERIC ENTITY: "+ qry);
                          i = Stmt.executeUpdate(qry);

                }
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}

        public void removeFrom(GenericEntity[] entityToRemoveFrom)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
                        String idColumnName = this.getIDColumnName();
                        int id = this.getID();

			int count = 0;
                        for (int i = 0; i < entityToRemoveFrom.length; i++) {
                            count += Stmt.executeUpdate("delete from "+getNameOfMiddleTable(entityToRemoveFrom[i],this)+" where "+idColumnName+"='"+id+"'");
                        if( (entityToRemoveFrom[i].getID()==-1) || (entityToRemoveFrom[i].getID()==0))//removing all in middle table
			  count +=  Stmt.executeUpdate("delete from "+getNameOfMiddleTable(entityToRemoveFrom[i],this)+" where "+idColumnName+"='"+id+"'");
                        else// just removing this particular one
                          count +=  Stmt.executeUpdate("delete from "+getNameOfMiddleTable(entityToRemoveFrom[i],this)+" where "+idColumnName+"='"+id+"' AND "+entityToRemoveFrom[i].getIDColumnName()+"='"+entityToRemoveFrom[i].getID()+"'");



                        }


		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}



	public void reverseRemoveFrom(GenericEntity entityToRemoveFrom)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("delete from "+getNameOfMiddleTable(entityToRemoveFrom,this)+" where "+entityToRemoveFrom.getIDColumnName()+"='"+entityToRemoveFrom.getID()+"'");
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

	}

        public boolean equals(Object obj){
          if(obj instanceof GenericEntity){
            return equals((GenericEntity)obj);
          }
          else{
            return super.equals(obj);
          }
        }


        public boolean equals(GenericEntity entity){
          if(entity!=null){
            if(entity.getClass().equals(this.getClass())){
              if(entity.getID()==this.getID()){
                return true;
              }
              return false;
            }
            return false;
          }
          return false;
        }

        public void empty(){
          this.columns.clear();
        }


        private GenericEntity getStaticInstance(){
          return getStaticInstance(this.getClass().getName());
        }

        protected boolean hasLobColumn()throws Exception{
          String lobColumnName = this.getStaticInstance().lobColumnName;
          if(lobColumnName==null){
            return false;
          }
          return true;
        }


        private void setLobColumnName(){
          if( this.getStaticInstance().lobColumnName == null ) {
            String[] columnNames = this.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
              if( EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER == this.getStorageClassType(columnNames[i]) ){
                this.getStaticInstance().lobColumnName = columnNames[i];
              }
            }
          }
        }

        public String getLobColumnName(){
          return  this.getStaticInstance().lobColumnName;
        }

        public static GenericEntity getStaticInstance(String entityClassName){
            if (allStaticClasses==null){
              allStaticClasses=new Hashtable();
            }
            GenericEntity theReturn = (GenericEntity)allStaticClasses.get(entityClassName);
            if(theReturn==null){
              try{
                theReturn = (GenericEntity)Class.forName(entityClassName).newInstance();
                allStaticClasses.put(entityClassName,theReturn);
              }
              catch(Exception ex){
              }
            }
            return theReturn;
        }

        public static GenericEntity getStaticInstance(Class entityClass){
            return getStaticInstance(entityClass.getName());
        }

      public void addManyToManyRelationShip(GenericEntity relatingEntity,String relationShipTableName){
            addManyToManyRelationShip(relatingEntity.getClass().getName(),relationShipTableName);
      }



      public void addManyToManyRelationShip(String relatingEntityClassName,String relationShipTableName){
            EntityControl.addManyToManyRelationShip(this.getClass().getName(),relatingEntityClassName,relationShipTableName);
      }


      public void addManyToManyRelationShip(String relatingEntityClassName){
            String relationShipTableName;
            try{
              String tableName1 = ((GenericEntity)getClass().newInstance()).getTableName();
              String tableName2 = ((GenericEntity)Class.forName(relatingEntityClassName).newInstance()).getTableName();

              relationShipTableName = StringHandler.concatAlphabetically(tableName1,tableName2);
              addManyToManyRelationShip(relatingEntityClassName,relationShipTableName);
            }
            catch(Exception ex){
              ex.printStackTrace();
            }
      }

      public void addManyToManyRelationShip(Class relatingEntityClass){
                addManyToManyRelationShip(relatingEntityClass.getName());
      }

      public void addTreeRelationShip(){
        EntityControl.addTreeRelationShip(this);
      }

      protected int getEntityState(){
        return this.state;
      }

      protected void setEntityState(int state){
        this.state=state;
      }

      public boolean isInSynchWithDatastore(){
        return (getEntityState()==this.STATE_IN_SYNCH_WITH_DATASTORE);
      }


      public static GenericEntity getEntityInstance(Class entityClass, int id){
        GenericEntity entity = null;
        try{
          entity = (GenericEntity) entityClass.newInstance();
          entity.findByPrimaryKey(id);
        }
        catch(Exception e){
         e.printStackTrace(System.err);
         System.err.println("GenericEntity: error initializing entity");
        }
       return entity;
      }

      public static GenericEntity getEntityInstance(Class entityClass){
        GenericEntity entity = null;
        try{
          entity = (GenericEntity) entityClass.newInstance();
        }
        catch(Exception e){
         e.printStackTrace(System.err);
         System.err.println("GenericEntity: error initializing entity");
        }
       return entity;
      }
}

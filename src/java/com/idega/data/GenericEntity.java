/*
<<<<<<< GenericEntity.java
<<<<<<< GenericEntity.java
 * $Id: GenericEntity.java,v 1.85 2002/03/14 21:46:30 tryggvil Exp $
=======
 * $Id: GenericEntity.java,v 1.85 2002/03/14 21:46:30 tryggvil Exp $
>>>>>>> 1.83
=======
 * $Id: GenericEntity.java,v 1.85 2002/03/14 21:46:30 tryggvil Exp $
>>>>>>> 1.84
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;

import com.idega.idegaweb.IWMainApplicationSettings;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.List;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.Gender;
import com.idega.util.StringHandler;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * A class to serve as a base class for objects mapped to persistent data.
 *
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.4
 * @modified <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 */
public abstract class GenericEntity implements java.io.Serializable, IDOLegacyEntity, javax.ejb.EntityBean {
  public static final String MANY_TO_ONE = "many-to-one";
  public static final String ONE_TO_MANY = "one-to-many";
  public static final String MANY_TO_MANY = "many-to-many";
  public static final String ONE_TO_ONE = "one-to-one";

  private String _dataStoreType;
  private Hashtable _columns = new Hashtable();
  private Hashtable _updatedColumns;
  private static Hashtable _theAttributes = new Hashtable();
  private static Hashtable _allStaticClasses = new Hashtable();

  //private static NullColumnValue nullColumnValue = new NullColumnValue();

  private Hashtable _theMetaDataAttributes;
  private Vector _insertMetaDataVector;
  private Vector _updateMetaDataVector;
  private Vector _deleteMetaDataVector;
  private Hashtable _theMetaDataIds;
  private boolean _hasMetaDataRelationship = false;
  private boolean _metaDataHasChanged = false;

  private String _dataSource;
  private static String DEFAULT_DATASOURCE = "default";
  String _cachedColumnNameList;
  private String _lobColumnName;
  private boolean insertStartData=true;

  private int _state;

  protected static int STATE_NEW = 0;
  protected static int STATE_IN_SYNCH_WITH_DATASTORE = 1;
  protected static int STATE_NOT_IN_SYNCH_WITH_DATASTORE = 2;
  protected static int STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE = 3;
  protected static int STATE_DELETED = 4;

  public GenericEntity() {
	  this(DEFAULT_DATASOURCE);
  }

  public GenericEntity(String dataSource) {
	  setDatasource(dataSource);
	  firstLoadInMemoryCheck();
	  setDefaultValues();
  }

  public GenericEntity(int id) throws SQLException {
	  this(id,DEFAULT_DATASOURCE);
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
    Vector theReturn = (Vector)_theAttributes.get(this.getClass().getName());
    if (theReturn == null) {
      theReturn = new Vector();
      _theAttributes.put(this.getClass().getName(),theReturn);

      //First store a static instance of this class
      String className = this.getClass().getName();
      try {
	_allStaticClasses.put(className,(GenericEntity)Class.forName(className).newInstance());
      }
      catch(Exception ex) {
	ex.printStackTrace();
      }
      //call the ializeAttributes that stores information about columns and relationships
      beforeInitializeAttributes();
      initializeAttributes();
      afterInitializeAttributes();
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
   * Meant to be overrided in subclasses to add default attributes
   */
  protected void beforeInitializeAttributes(){
  }



  /**
   * Meant to be overrided in subclasses to add default attributes
   */
  protected void afterInitializeAttributes(){
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
   * Subclasses have to implement this method - this should return the name of the underlying table
	 */
	public abstract String getEntityName();

	public abstract void initializeAttributes();

  protected Vector getAttributes() {
    //ties the attribute vector to the subclass of GenericEntity because
    //the theAttributes variable is static.
    Vector theReturn = (Vector)_theAttributes.get(this.getClass().getName());

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

	public void setID(Integer id) {
		setColumn(getIDColumnName(),id);
	}

	public int getID() {
		return getIntColumnValue(getIDColumnName());
	}

	public Object getPrimaryKeyValue(){
	    return getColumnValue(getIDColumnName());
	}

	public Integer getIDInteger() {
		return (Integer)getPrimaryKeyValue();
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
	    attribute = new EntityAttribute(attributeName);
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
       protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,int maxLength) {
	  try{
	    addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),maxLength);
	  }
	  catch(ClassNotFoundException e){
	    throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
	  }
       }


	protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,int maxLength) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
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
	protected void addColumnName(String columnName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,String relationShipType,String relationShipClassName) {
		addAttribute(columnName,longName,ifVisible,ifEditable,storageClassName,relationShipType,relationShipClassName);
	}


	protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,String relationShipType,String relationShipClassName) {
	  try{
	    addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),relationShipType,Class.forName(relationShipClassName));
	  }
	  catch(ClassNotFoundException e){
	    throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
	  }
	}

	protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,String relationShipType,Class relationShipClass) {
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		addAttribute(attribute);
	}

	protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,String storageClassName,int maxLength,String relationShipType,String relationShipClassName){
	  try{
	    addAttribute(attributeName,longName,ifVisible,ifEditable,Class.forName(storageClassName),maxLength,relationShipType,Class.forName(relationShipClassName));
	  }
	  catch(ClassNotFoundException e){
	    throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
	  }
	}

	protected void addAttribute(String attributeName,String longName,boolean ifVisible,boolean ifEditable,Class storageClass,int maxLength,String relationShipType,Class relationShipClass){
		EntityAttribute attribute = new EntityAttribute(attributeName);
		attribute.setLongName(longName);
		attribute.setVisible(ifVisible);
		attribute.setEditable(ifEditable);
		attribute.setRelationShipType(relationShipType);
		attribute.setRelationShipClass(relationShipClass);
		attribute.setStorageClass(storageClass);
		attribute.setMaxLength(maxLength);
		addAttribute(attribute);
	}


	protected void addAttribute(EntityAttribute attribute){
	    getAttributes().addElement(attribute);
	}

	protected void addLanguageAttribute(){
	  this.addAttribute(getLanguageIDColumnName(),"Tungumál", true, true, "java.lang.Integer","one_to_one","com.idega.core.localisation.data.Language");
	}


	/**
	 * @deprecated Replaced with getAttribute()
	 */
	public EntityAttribute getColumn(String columnName){
		return getAttribute(columnName);
	}

	protected EntityAttribute getAttribute(String attributeName){
		//return (EntityAttribute) columns.get(columnName.toLowerCase());
		EntityAttribute theReturn = null;
		EntityAttribute tempColumn = null;
		for (Enumeration enumeration=getAttributes().elements();enumeration.hasMoreElements();){
			tempColumn = (EntityAttribute)enumeration.nextElement();
			if (tempColumn.getColumnName().equalsIgnoreCase(attributeName)){
				theReturn = tempColumn;
			}
		}
/*    if(theReturn==null){
      System.err.println("Error in "+this.getClass().getName()+".getAttribute(): ColumnName='"+attributeName+"' exists in table but not in Entity Class");
    }*/
		return theReturn;
	}

	protected void addOneToOneRelationship(String relationshipColumnName,Class relatingEntityClass){
      addOneToOneRelationship(relationshipColumnName,relatingEntityClass.getName(),relatingEntityClass);
	}

	protected void addOneToOneRelationship(String relationshipColumnName,String description,Class relatingEntityClass){
      addAttribute(relationshipColumnName,description,true,true, Integer.class,GenericEntity.ONE_TO_ONE,relatingEntityClass);
	}

	protected void addManyToOneRelationship(String relationshipColumnName,Class relatingEntityClass){
      addManyToOneRelationship(relationshipColumnName,relatingEntityClass.getName(),relatingEntityClass);
	}

	protected void addManyToOneRelationship(String relationshipColumnName,String description,Class relatingEntityClass){
      addAttribute(relationshipColumnName,description,true,true, Integer.class,GenericEntity.ONE_TO_MANY,relatingEntityClass);
	}

	protected void addRelationship(String relationshipName,String relationshipType,String relationshipClassName){
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
	    //_columns.put(columnName.toLowerCase(),columnValue);
			_columns.put(columnName.toUpperCase(),columnValue);
            this.flagColumnUpdate(columnName);
                        if((getEntityState()==STATE_NEW)||(getEntityState()==STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)){
                          setEntityState(STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
                        }
                        else{
		          this.setEntityState(STATE_NOT_IN_SYNCH_WITH_DATASTORE);
                        }
                }

	}

	protected Object getValue(String columnName){
	//return _columns.get(columnName.toLowerCase());
		return _columns.get(columnName.toUpperCase());
	}


        /**
         * Sets the column null
         */
	public void removeFromColumn(String columnName){
	  //_columns.remove(columnName.toLowerCase());
	  _columns.remove(columnName.toUpperCase());
          this.flagColumnUpdate(columnName);
          //setValue(columnName,this.getNullColumnValue());
        }


	public void setColumn(String columnName,Object columnValue){
		if (this.getRelationShipClass(columnName)==null){
			setValue(columnName,columnValue);
		}
		else{
			if(columnValue instanceof Integer){
			  setValue(columnName,(Integer)columnValue);
			}
			else if(columnValue instanceof String){
			  setValue(columnName,(String)columnValue);
			}
			//else if (columnValue instanceof GenericEntity){
			else {
			  setValue(columnName,((GenericEntity)columnValue).getPrimaryKeyValue());
			}
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
	**Sets a column value to null
	**/
  public void setColumnAsNull(String columnName)throws SQLException{
    Connection Conn= null;
		try{
			Conn = getConnection(getDatasource());
      String sql = "update "+this.getEntityName()+" set "+columnName+" = null where "+this.getIDColumnName()+" = "+Integer.toString(this.getID());
			Conn.createStatement().executeUpdate(sql);
      Conn.commit();
		}
    catch (SQLException e) {
      e.printStackTrace(System.err);
    }
		finally{
			if (Conn != null){
				freeConnection(getDatasource(),Conn);
			}
		}

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
    BlobWrapper wrapper =  (BlobWrapper) getColumnValue(columnName);
    if(wrapper==null){
      wrapper = new BlobWrapper(this,columnName);
      this.setColumn(columnName,wrapper);
    }
    return wrapper;
  }

  public InputStream getInputStreamColumnValue(String columnName)throws Exception{
    BlobWrapper wrapper = getBlobColumnValue(columnName);
    /*if(wrapper==null){
      wrapper = new BlobWrapper(this,columnName);
      this.setColumn(columnName,wrapper);
    }*/
    return wrapper.getBlobInputStream();
  }

	public Object getColumnValue(String columnName){
		Object returnObj = null;
		Object value = getValue(columnName);
		if (value instanceof com.idega.data.GenericEntity){
			returnObj = value;
		}
		else if (value instanceof java.lang.Integer){
			if ((getRelationShipClass(columnName)!=null)){
				if (getRelationShipClass(columnName).getName().indexOf("idega") != -1){
					try{
						returnObj =getRelationShipClass(columnName).newInstance();
                        GenericEntity returnEntity = (GenericEntity)returnObj;
                        returnEntity.setDatasource(this.getDatasource());
						returnEntity.findByPrimaryKey(((Integer)value).intValue());
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

	/*
	public String getRelationShipClassName(String columnName){
		String theReturn = "";
		if (getColumn(columnName) != null){
		  theReturn = getColumn(columnName).getRelationShipClassName();
		}
	    return theReturn;
	}
	*/


    /**
     * Returns null if the specified column does have a relationship Class
     */
    public Class getRelationShipClass(String columnName){
	  EntityAttribute column = getColumn(columnName);
	  if(column!=null){
	    return column.getRelationShipClass();
	  }
	  return null;
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
        if(!dataSource.equals(this._dataSource)){
          try{
            //Connect the blob fields if the datasource is changed
            if(getEntityState()==this.STATE_IN_SYNCH_WITH_DATASTORE && this.hasLobColumn()){
              BlobWrapper wrapper = this.getBlobColumnValue(this.getLobColumnName());
              BlobInputStream inStream = wrapper.getBlobInputStream();
              inStream.setDataSource(this._dataSource);
              wrapper.setInputStreamForBlobWrite(inStream);

              //System.out.println(this.getClass().getName()+".setDatasource("+dataSource+"), connecting blob fields");
            }
            setEntityState(this.STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE);
          }
          catch(Exception e){
            System.err.println("Exception in connecting blob fields for "+this.getClass().getName()+", id="+this.getID());
            e.printStackTrace();
          }

        }
		_dataSource=dataSource;
	}

	public String getDatasource(){
		return _dataSource;
	}

	/**
	 * @todo add:
	public String getPKColumnName(){
	  String entityName = getEntityName();
	  if (entityName.endsWith("_")){
		  return entityName+"id";
	  }
	  else{
		  return entityName+"_id";
	  }
	}
	*/

	public String getIDColumnName(){
	  String entityName = getEntityName();
	  if (entityName.endsWith("_")){
		  return entityName+"ID";
		  //return entityName+"id";
	  }
	  else{
		  return entityName+"_ID";
		  //return entityName+"_id";
	  }
	}

	public static String getLanguageIDColumnName(){
	  return "language_id";
	}


/**@todo this should not be done every time cache!!**/
	public String[] getColumnNames(){

		Vector vector = new Vector();
		int i=0;
		//for (Enumeration e = columns.keys(); e.hasMoreElements();i++){
		for (Enumeration e = getAttributes().elements(); e.hasMoreElements();i++){
			EntityAttribute temp = (EntityAttribute)e.nextElement();
			if (temp.getAttributeType().equals("column")){
			  //vector.addElement(temp.getColumnName().toLowerCase());
				vector.addElement(temp.getColumnName());
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
/**@todo this should not be done every time cache!!**/
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

		for (Enumeration e = _columns.keys(); e.hasMoreElements();){
		//for (Enumeration e = columns.elements(); e.hasMoreElements();){

			String tempName = (String)e.nextElement();
			if (getIfEditable(tempName)){
				theColumns.addElement(tempName);
			};
		}
		return (String[])theColumns.toArray(new String[0]);
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

		//if (_columns.get(columnName.toLowerCase())== null){
        Object o = _columns.get(columnName.toUpperCase());
		if (o == null){
		  return true;
		}
		else{
		  return false;
		}


	}

    boolean hasBeenSetNull(String columnName){
      if(this.hasColumnBeenUpdated(columnName)){
        return this.isNull(columnName);
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
  *Inserts/update/removes this entity's metadata into the datastore
  */
  public void updateMetaData()throws SQLException{
    try{
      if(this.getID()!=-1) DatastoreInterface.getInstance(this).crunchMetaData(this);
      else System.err.println("GenericEntity: updateMetaData() getID = -1 !");
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
  *Inserts/update/removes this entity's metadata into the datastore
  */
  public void insertMetaData()throws SQLException{
   updateMetaData();
  }
  /**
  *deletes all of this entity's metadata
  */
  public void deleteMetaData()throws SQLException{
    try {
      DatastoreInterface.getInstance(this).deleteMetaData(this);
    }
    catch(Exception ex) {
      if(ex instanceof SQLException) {
	ex.printStackTrace();
	throw (SQLException)ex.fillInStackTrace();
      }
    }
  }

    /**
    *Inserts this entity as a record into the datastore
    */
  protected void insert(Connection c)throws SQLException{
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
  public synchronized void update()throws SQLException{
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
  protected void update(Connection c) throws SQLException {
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


  public void delete()throws SQLException{
    try {
      DatastoreInterface.getInstance(this).delete(this);
    }
    catch(Exception ex) {
      if(ex instanceof SQLException) {
	ex.printStackTrace();
	throw (SQLException)ex.fillInStackTrace();
      }
   }
  }

  protected void delete(Connection c) throws SQLException {
    try {
      DatastoreInterface.getInstance(c).delete(this,c);
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

	public void deleteMultiple(String columnName1,String stringColumnValue1,String columnName2,String stringColumnValue2)throws SQLException{
		EntityControl.deleteMultiple(this,columnName1,stringColumnValue1,columnName2,stringColumnValue2);
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
			int theInt = RS.getInt(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Integer(theInt));
			    //setColumn(columnName.toLowerCase(),new Integer(theInt));
			}

			//}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_STRING){
			if (RS.getString(columnName) != null){
				setColumn(columnName,RS.getString(columnName));
			}

		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_BOOLEAN){
			String theString = RS.getString(columnName);
			if (theString != null){
				if (theString.equals("Y")){
					setColumn(columnName,new Boolean(true));
				}
				else if (theString.equals("N")){
					setColumn(columnName,new Boolean(false));
				}
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_FLOAT){
			float theFloat = RS.getFloat(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Float(theFloat));
			    //setColumn(columnName.toLowerCase(),new Float(theFloat));
			}

		}
		else if (classType==EntityAttribute.TYPE_JAVA_LANG_DOUBLE){
			double theDouble = RS.getFloat(columnName);
			boolean wasNull = RS.wasNull();
			if(!wasNull){
			    setColumn(columnName,new Double(theDouble));
			    //setColumn(columnName.toLowerCase(),new Double(theDouble));
			}

			double doble = RS.getDouble(columnName);
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP){
			if (RS.getTimestamp(columnName) != null){
				setColumn(columnName,RS.getTimestamp(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_DATE){
			if (RS.getDate(columnName) != null){
				setColumn(columnName,RS.getDate(columnName));
			}
		}
		else if (classType==EntityAttribute.TYPE_JAVA_SQL_TIME){
			java.sql.Date date = RS.getDate(columnName);
			if (date != null){
				setColumn(columnName,date);
		//setColumn(columnName.toLowerCase(),date);
			}
		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
			/*if (RS.getDate(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getTime(columnName));
			}*/
			setColumn(columnName,getEmptyBlob(columnName));
			//setColumn(columnName.toLowerCase(),getEmptyBlob(columnName));

		}
		else if (classType==EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER){
			String gender = RS.getString(columnName);
			if (gender != null){
				setColumn(columnName,new Gender(gender));
		//setColumn(columnName.toLowerCase(),new Gender(gender));

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
      //buffer.append("select * from ");
      buffer.append("select ");
      //System.out.println("COLUMN NAMES : "+DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));/**@is this where it is supposed to be?**/
      buffer.append(DatastoreInterface.getCommaDelimitedColumnNamesForSelect(this));
      buffer.append(" from ");//skips lob colums
      buffer.append(getEntityName());
      buffer.append(" where ");
      buffer.append(getIDColumnName());
      buffer.append("=");
      buffer.append(id);

      ResultSet RS = Stmt.executeQuery(buffer.toString());


      //ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+" where "+getIDColumnName()+"="+id);
    //eiki added null check
      if (( RS==null) || !RS.next())
	throw new SQLException("Record with id="+id+" not found");

      String[] columnNames = getColumnNames();
      for (int i = 0; i < columnNames.length; i++){
	try{
	  //if (RS.getString(columnNames[i]) != null){
	  fillColumn(columnNames[i],RS);
	//}
	}
	catch(Exception ex){
	/*//NOCATH
	  try{
	  //if (RS.getString(columnNames[i].toUpperCase()) != null){
		  fillColumn(columnNames[i],RS);
	  //}
	  }
	  catch(SQLException exe){
	    try{
		    //if (RS.getString(columnNames[i].toLowerCase()) != null){
	    fillColumn(columnNames[i],RS);
	    //}
	    }
	    catch(SQLException exep){

	     System.err.println("Exception in "+this.getClass().getName()+" findByPrimaryKey, RS.getString( "+columnNames[i]+" ) not found: "+exep.getMessage());
		    //exep.printStackTrace(System.err);
	    }
	  }*/
	  System.err.println("Exception in "+this.getClass().getName()+" findByPrimaryKey, RS.getString( "+columnNames[i]+" ) not found: "+ex.getMessage());
	  if(!(ex instanceof NullPointerException))
	    ex.printStackTrace(System.err);
	}

      }

      if( RS != null ) RS.close();

    }
    finally{
      if(Stmt != null){
	Stmt.close();
      }
      if (conn != null){
	freeConnection(getDatasource(),conn);
      }
    }
    setEntityState(STATE_IN_SYNCH_WITH_DATASTORE);
  }



  public String getNameOfMiddleTable(GenericEntity entity1,GenericEntity entity2){
      return EntityControl.getNameOfMiddleTable(entity1,entity2);

  }


  public GenericEntity[] findRelated(GenericEntity entity)throws SQLException{
    return findRelated(entity, "", "");
  }

  public int[] findRelatedIDs(GenericEntity entity)throws SQLException{
    return findRelatedIDs(entity, "", "");
  }

  public GenericEntity[] findRelated(GenericEntity entity, String entityColumnName, String entityColumnValue)throws SQLException{
		String tableToSelectFrom = getNameOfMiddleTable(entity,this);
		StringBuffer buffer=new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, "+entity.getEntityName()+" e");
		buffer.append(" where ");
		buffer.append("middle."+this.getIDColumnName());
		buffer.append("=");
		buffer.append(this.getID());
		buffer.append(" and ");
		buffer.append("middle."+entity.getIDColumnName());
		buffer.append("=");
		buffer.append("e."+entity.getIDColumnName());
		if ( entity.getID() != -1 ) {
		  buffer.append(" and ");
		  buffer.append("middle."+entity.getIDColumnName());
		  buffer.append("=");
		  buffer.append(entity.getID());
		}
		if (entityColumnName != null)
		if (!entityColumnName.equals("")) {
		  buffer.append(" and ");
		  buffer.append("e."+entityColumnName);
		  if (entityColumnValue != null) {
		    buffer.append(" = ");
		    buffer.append("'"+entityColumnValue+"'");
		  }else {
		    buffer.append(" is null");
		  }
		}
		String SQLString=buffer.toString();

		return findRelated(entity,SQLString);
	}

	public GenericEntity[] findReverseRelated(GenericEntity entity)throws SQLException{
		return findRelated(entity);
	}

	protected GenericEntity[] findRelated(GenericEntity entity,String SQLString)throws SQLException{
		Connection conn= null;
		Statement Stmt= null;
		Vector vector = new Vector();
		String tableToSelectFrom = "";
		if (entity.getEntityName().endsWith("_")){
			tableToSelectFrom = entity.getEntityName()+this.getEntityName();
		}
		else{
			tableToSelectFrom = entity.getEntityName()+"_"+this.getEntityName();
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


	  public int[] findRelatedIDs(GenericEntity entity, String entityColumnName, String entityColumnValue)throws SQLException{
		String tableToSelectFrom = getNameOfMiddleTable(entity,this);
		StringBuffer buffer=new StringBuffer();
		buffer.append("select e.* from ");
		buffer.append(tableToSelectFrom + " middle, "+entity.getEntityName()+" e");
		buffer.append(" where ");
		buffer.append("middle."+this.getIDColumnName());
		buffer.append("=");
		buffer.append(this.getID());
		buffer.append(" and ");
		buffer.append("middle."+entity.getIDColumnName());
		buffer.append("=");
		buffer.append("e."+entity.getIDColumnName());
		if ( entity.getID() != -1 ) {
		  buffer.append(" and ");
		  buffer.append("middle."+entity.getIDColumnName());
		  buffer.append("=");
		  buffer.append(entity.getID());
		}
		if (entityColumnName != null)
		if (!entityColumnName.equals("")) {
		  buffer.append(" and ");
		  buffer.append("e."+entityColumnName);
		  if (entityColumnValue != null) {
		    buffer.append(" = ");
		    buffer.append("'"+entityColumnValue+"'");
		  }else {
		    buffer.append(" is null");
		  }
		}
		String SQLString=buffer.toString();

		return findRelatedIDs(entity,SQLString);
	}


	protected int[] findRelatedIDs(GenericEntity entity,String SQLString)throws SQLException{
		Connection conn= null;
		Statement Stmt= null;
		int[] toReturn = null;
		int length;
		Vector vector = new Vector();
		String tableToSelectFrom = "";
		if (entity.getEntityName().endsWith("_")){
			tableToSelectFrom = entity.getEntityName()+this.getEntityName();
		}
		else{
			tableToSelectFrom = entity.getEntityName()+"_"+this.getEntityName();
		}

		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			length = 0;
			while (RS.next()){
				try{
				  vector.addElement(RS.getObject(entity.getIDColumnName()));
				  length++;
				}
				catch(Exception ex){
					System.err.println("There was an error in GenericEntity.findRelatedIDs(GenericEntity entity,String SQLString): "+ex.getMessage());

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

		if(length > 0){
		  toReturn = new int[length];
		  Iterator iter = vector.iterator();
		  int index = 0;
		  while (iter.hasNext()) {
		    Integer item = (Integer)iter.next();
		    toReturn[index++] = item.intValue();
		  }
		} else {
		  toReturn = new int[0];
		}

		return toReturn;
	}


	/**
	*Finds all instances of the current object in the otherEntity
	**/
	public GenericEntity[] findAssociated(GenericEntity otherEntity)throws SQLException{
		return otherEntity.findAll("select * from "+otherEntity.getEntityName()+" where "+this.getIDColumnName()+"='"+this.getID()+"'");
	}

	public GenericEntity[] findAssociatedOrdered(GenericEntity otherEntity,String column_name)throws SQLException{
		return otherEntity.findAll("select * from "+otherEntity.getEntityName()+" where "+this.getIDColumnName()+"='"+this.getID()+"' order by "+column_name+"");
	}

	public GenericEntity[] findAll()throws SQLException{
		return findAll("select * from "+getEntityName());
	}


	public GenericEntity[] findAllOrdered(String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName, String condition)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName+" "+condition+" '"+toFind+"' order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnOrdered(String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName, String condition1, String condition2)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" "+condition1+" '"+toFind1+"' and "+columnName2+" "+condition2+" '"+toFind2+"' order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName);
	}

	public GenericEntity[] findAllByColumnDescendingOrdered(String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName+" desc");
	}

	public GenericEntity[] findAllByColumnDescendingOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName+" desc");
	}

	public GenericEntity[] findAllDescendingOrdered(String orderByColumnName)throws SQLException{
		return findAll("select * from "+getEntityName()+" order by "+orderByColumnName+" desc");
	}

	public GenericEntity[] findAllByColumn(String columnName, String toFind, String condition)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName+" "+condition+" '"+toFind+"'");
	}

	public GenericEntity[] findAllByColumn(String columnName1, String toFind1, char condition1, String columnName2, String toFind2, char condition2)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" "+String.valueOf(condition1)+" '"+toFind1+"' and "+columnName2+" "+String.valueOf(condition2)+" '"+toFind2+"'");
	}

	public GenericEntity[] findAllByColumn(String columnName, String toFind)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName+" like '"+toFind+"'");
	}

	public GenericEntity[] findAllByColumn(String columnName, int toFind)throws SQLException{
		return findAllByColumn(columnName,Integer.toString(toFind));
	}

  public GenericEntity[] findAllByColumn(String columnName1, String toFind1,String columnName2, String toFind2, String columnName3, String toFind3)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' and "+columnName3+" like '"+toFind3+"'");
	}

	public GenericEntity[] findAllByColumn(String columnName1, String toFind1,String columnName2, String toFind2)throws SQLException{
		return findAll("select * from "+getEntityName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"'");
	}

  public int getNumberOfRecords(String columnName, String columnValue)throws SQLException{
		return getNumberOfRecords("select count(*) from "+getEntityName()+" where "+columnName+" like '"+columnValue+"'");
	}

	 public int getNumberOfRecords(String columnName, int columnValue)throws SQLException{
		return getNumberOfRecords("select count(*) from "+getEntityName()+" where "+columnName+" = "+columnValue);
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
	    return getNumberOfRecords("select count(*) from "+getEntityName());
	}

	public int getNumberOfRecords(String CountSQLString)throws SQLException {
	    return getIntTableValue(CountSQLString);
	}

	public int getNumberOfRecords(String columnName, String Operator ,String columnValue)throws SQLException{
		return getNumberOfRecords("select count(*) from "+getEntityName()+" where "+columnName+" "+Operator+" "+columnValue);
	}

	public int getMaxColumnValue(String columnName)throws SQLException {
	    return getIntTableValue("select max("+columnName+") from "+getEntityName());
	}

	public int getMaxColumnValue(String columnToGetMaxFrom, String columnCondition, String columnConditionValue)throws SQLException {
	    return getIntTableValue("select max("+columnToGetMaxFrom+") from "+getEntityName()+" where "+columnCondition+" = '"+columnConditionValue+"'");
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
	  //System.out.println(SQLString);
	  return findAll(SQLString,-1);
	}

	public GenericEntity[] findAll(String SQLString,int returningNumberOfRecords)throws SQLException{
	  //System.err.println("GenericEntity.findAll(\""+SQLString+"\");");
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
			String sql = "insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+") values("+getID()+","+entityToAddTo.getID()+")";
			//System.out.println("statement: "+sql);
			int i = Stmt.executeUpdate(sql);
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
   * Default move behavior with a tree relationship
	 */
	public void moveChildrenToCurrent(GenericEntity entityFrom, String entityFromColumName) throws SQLException {
		Connection conn= null;
		Statement Stmt= null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
      String sql = "update "  + getNameOfMiddleTable(entityFrom,this) +
		   " set " + getIDColumnName() + " = " + getID() +
		   " where " + getIDColumnName() + " = " + entityFrom.getID();
			int i = Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(),conn);
			}
		}
	}


	/**
	 * Default relationship adding behavior with a many-to-many relationship
	 */
	public void addTo(GenericEntity entityToAddTo, String entityToAddToColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
      String sql = "insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddToColumName+") values("+getID()+","+entityToAddTo.getID()+")";
			int i = Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(),conn);
			}
		}
	}

  public void addToTree(GenericEntity entityToAddTo, String entityToAddToColumName, String middleTableName) throws SQLException {
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = getConnection(getDatasource());
      Stmt = conn.createStatement();
      String sql = "insert into "+middleTableName+"("+getIDColumnName()+","+entityToAddToColumName+") values("+getID()+","+entityToAddTo.getID()+")";
      System.out.println(sql);
      int i = Stmt.executeUpdate(sql);
    }
    finally {
      if (Stmt != null) {
	Stmt.close();
      }
      if (conn != null) {
	freeConnection(getDatasource(),conn);
      }
    }
  }

	/**
	 * Default delete behavior with a tree relationship
	 */
	public void removeFrom(GenericEntity entityToDelete, String entityToDeleteColumName) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
      String sql = "delete from " + getNameOfMiddleTable(entityToDelete,this) + " where " + entityToDeleteColumName+" = " + entityToDelete.getID();
			int i = Stmt.executeUpdate(sql);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(getDatasource(),conn);
			}
		}
	}


	/**
	**Default insert behavior with a many-to-many relationship and EntityBulkUpdater
	**/
	public void addTo(GenericEntity entityToAddTo, Connection conn)throws SQLException{
	  Statement Stmt= null;
	  try{
	    Stmt = conn.createStatement();
	    int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable(entityToAddTo,this)+"("+getIDColumnName()+","+entityToAddTo.getIDColumnName()+") values("+getID()+","+entityToAddTo.getID()+")");
	  }
	  finally{
	    if(Stmt != null){
	      Stmt.close();
	    }
	  }

	}

	/**
	* Attention: Beta implementation
	*/
	public void addTo(Class entityToAddTo, int id)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into "+getNameOfMiddleTable((GenericEntity)GenericEntity.getStaticInstance(entityToAddTo),this)+"("+getIDColumnName()+","+((GenericEntity)GenericEntity.getStaticInstance(entityToAddTo)).getIDColumnName()+") values("+getID()+","+id+")");
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


	/**
	* Attention: Beta implementation
	*/
	public void removeFrom(Class entityToRemoveFrom, int id)throws SQLException{
	  Connection conn= null;
	  Statement Stmt= null;
	  String qry = "";
	  try{
	    int i = 0;
	    conn = getConnection(getDatasource());
	    Stmt = conn.createStatement();

	    qry = "delete from "+getNameOfMiddleTable(GenericEntity.getStaticInstance(entityToRemoveFrom),this)+" where "+this.getIDColumnName()+"='"+this.getID()+"' AND "+GenericEntity.getStaticInstance(entityToRemoveFrom).getIDColumnName()+"='"+id+"'";

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


	/**
	* Attention: Beta implementation
	*/
	public void removeFrom(Class entityToRemoveFrom)throws SQLException{
	  Connection conn= null;
	  Statement Stmt= null;
	  String qry = "";
	  try{
	    int i = 0;
	    conn = getConnection(getDatasource());
	    Stmt = conn.createStatement();

	    qry = "delete from "+getNameOfMiddleTable(GenericEntity.getStaticInstance(entityToRemoveFrom),this)+" where "+this.getIDColumnName()+"='"+this.getID()+"'";;

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


	/**
	**Default remove behavior with a many-to-many relationship
	** deletes only one line in middle table if the genericentity wa consructed with a value
	** Takes in a connection but does not close it.
	**/
	protected void removeFrom(GenericEntity entityToRemoveFrom, Connection conn)throws SQLException{
		Statement Stmt= null;
		String qry = "";
		try{
		  int i = 0;
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
	  _columns.clear();
	}


	GenericEntity getStaticInstance(){
	  return getStaticInstance(this.getClass().getName());
	}

	protected boolean hasLobColumn()throws Exception{
	  String lobColumnName = this.getStaticInstance()._lobColumnName;
	  if(lobColumnName==null){
	    return false;
	  }
	  return true;
	}


	private void setLobColumnName(){
	  if( this.getStaticInstance()._lobColumnName == null ) {
	    String[] columnNames = this.getColumnNames();
	    for (int i = 0; i < columnNames.length; i++) {
	      if( EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER == this.getStorageClassType(columnNames[i]) ){
		this.getStaticInstance()._lobColumnName = columnNames[i];
	      }
	    }
	  }
	}

	public String getLobColumnName(){
	  return  this.getStaticInstance()._lobColumnName;
	}

	public static GenericEntity getStaticInstance(String entityClassName){
	    if (_allStaticClasses==null){
	      _allStaticClasses=new Hashtable();
	    }
	    GenericEntity theReturn = (GenericEntity)_allStaticClasses.get(entityClassName);
	    if(theReturn==null){
	      try{
		theReturn = (GenericEntity)Class.forName(entityClassName).newInstance();
		_allStaticClasses.put(entityClassName,theReturn);
	      }
	      catch(Exception ex){
		ex.printStackTrace();
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



      public void addManyToManyRelationShip(Class relatingEntityClass,String relationShipTableName){
	    EntityControl.addManyToManyRelationShip(this.getClass().getName(),relatingEntityClass.getName(),relationShipTableName);
      }


      public void addManyToManyRelationShip(String relatingEntityClassName,String relationShipTableName){
	  try{
	    addManyToManyRelationShip(Class.forName(relatingEntityClassName),relationShipTableName);
	  }
	  catch(ClassNotFoundException e){
	    throw new RuntimeException("Exception in "+this.getClass().getName()+e.getMessage());
	  }
      }


      public void addManyToManyRelationShip(String relatingEntityClassName){
	    String relationShipTableName;
	    try{


	      String tableName1 = this.getEntityName();
	      String tableName2 = ((GenericEntity)Class.forName(relatingEntityClassName).newInstance()).getEntityName();

	      relationShipTableName = StringHandler.concatAlphabetically(tableName1,tableName2,"_");
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
	return _state;
      }

      protected void setEntityState(int state){
	_state=state;
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

  public void addMetaDataRelationship(){
    addManyToManyRelationShip(MetaData.class);
    this.getStaticInstance(this.getClass())._hasMetaDataRelationship=true;
  }

  public boolean hasMetaDataRelationship(){
   return this.getStaticInstance(this.getClass())._hasMetaDataRelationship;
  }


// fetches the metadata for this id and puts it in a HashTable
  private void getMetaData(){
    Connection conn= null;
    Statement Stmt= null;
    _theMetaDataAttributes = new Hashtable();
    _theMetaDataIds = new Hashtable();

    try{
      conn = getConnection(getDatasource());
      Stmt = conn.createStatement();

      MetaData metadata = (MetaData) getStaticInstance(MetaData.class);
      String metadataIdColumn = metadata.getIDColumnName();

      String tableToSelectFrom = getNameOfMiddleTable(metadata,this);
      StringBuffer buffer = new StringBuffer();
      buffer.append("select ic_metadata.ic_metadata_id,metadata_name,metadata_value from ");
      buffer.append(tableToSelectFrom);
      buffer.append(",ic_metadata where ");
      buffer.append(tableToSelectFrom);
      buffer.append(".");
      buffer.append(getIDColumnName());
      buffer.append("=");
      buffer.append(this.getID());
      buffer.append(" and ");
      buffer.append(tableToSelectFrom);
      buffer.append(".");
      buffer.append(metadataIdColumn);
      buffer.append("=");
      buffer.append(metadata.getEntityName());
      buffer.append(".");
      buffer.append(metadataIdColumn);


      String query = buffer.toString();

      //System.out.println("MetadataQuery="+query);
      ResultSet RS = Stmt.executeQuery(query);


      while(RS.next()){
	_theMetaDataAttributes.put(RS.getString("metadata_name"),RS.getString("metadata_value"));
	_theMetaDataIds.put(RS.getString("metadata_name"),new Integer(RS.getInt("ic_metadata_id")));
      }

      RS.close();

    }
    catch(SQLException ex){
      System.err.println("Exception in "+this.getClass().getName()+" gettingMetaData "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
    finally{
    try{
     if(Stmt != null){
      Stmt.close();
      }
    }
    catch(SQLException ex){
      System.err.println("Exception in "+this.getClass().getName()+" gettingMetaData "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
    if (conn != null){
      freeConnection(getDatasource(),conn);
    }

    }

  }

  public String getMetaData(String metaDataKey){
    if( _theMetaDataAttributes==null ) getMetaData();//get all meta data first if null
    return (String) _theMetaDataAttributes.get(metaDataKey);
  }

  public void setMetaDataAttributes(Hashtable metaDataAttribs){
    String metaDataKey;
    for (Enumeration e = metaDataAttribs.keys(); e.hasMoreElements();){
       metaDataKey = (String)e.nextElement();
       addMetaData(metaDataKey,(String) metaDataAttribs.get(metaDataKey));
    }
  }

  public void setMetaData(String metaDataKey, String metaDataValue){
     addMetaData(metaDataKey,metaDataValue);
  }

  public void addMetaData(String metaDataKey, String metaDataValue){
    if( _theMetaDataAttributes==null ) getMetaData();//get all meta data first if null

    if( metaDataValue!=null ){
      Object obj = _theMetaDataAttributes.put(metaDataKey,metaDataValue);
      metaDataHasChanged(true);

      if( obj == null ){//is new

	if( _insertMetaDataVector == null ){
	  _insertMetaDataVector = new Vector();
	}

	_insertMetaDataVector.add(metaDataKey);

      }
      else{//is old
	if( _updateMetaDataVector == null ){
	  _updateMetaDataVector = new Vector();
	}


      if( _insertMetaDataVector!=null ){
	if(_insertMetaDataVector.indexOf(metaDataKey) == -1) {//is old and not in the insertlist
	  _updateMetaDataVector.add(metaDataKey);
	}
      }
      else{
	_updateMetaDataVector.add(metaDataKey);
      }

      }
    }
  }

  /**
  * return true if the metadata to delete already exists
  */
  public boolean removeMetaData(String metaDataKey){
    if( _theMetaDataAttributes==null ) getMetaData();//get all meta data first if null

    if( _deleteMetaDataVector == null ){
      _deleteMetaDataVector = new Vector();
    }

    if( _theMetaDataAttributes.get(metaDataKey) != null ) {
      _deleteMetaDataVector.add(metaDataKey);
      if( _insertMetaDataVector != null ) _insertMetaDataVector.remove(metaDataKey);
      if( _updateMetaDataVector != null ) _updateMetaDataVector.remove(metaDataKey);
      metaDataHasChanged(true);

      return true;
    }
    else return false;
  }

  public Hashtable getMetaDataAttributes(){
    return _theMetaDataAttributes;
  }

  public Hashtable getMetaDataIds(){
    return _theMetaDataIds;
  }

  public Vector getMetaDataUpdateVector(){
    return _updateMetaDataVector;
  }

  public Vector getMetaDataInsertVector(){
    return _insertMetaDataVector;
  }

  public Vector getMetaDataDeleteVector(){
    return _deleteMetaDataVector;
  }

  public boolean metaDataHasChanged(){
    return _metaDataHasChanged;
  }

  public void metaDataHasChanged(boolean metaDataHasChanged){
    _metaDataHasChanged = metaDataHasChanged;
  }

  /**
   * not implemented
   * @todo: implement
   */
  public javax.ejb.EJBHome getEJBHome(){
    return null;
  }

  /**
   * Not implemented
   * @todo: implement
   */
  public javax.ejb.Handle getHandle(){
    return null;
  }

  public Object getPrimaryKey(){
    return new Integer(getID());
  }

  public boolean isIdentical(javax.ejb.EJBObject ejbo){
    if(ejbo!=null){
      try{
	return ejbo.getPrimaryKey().equals(this.getPrimaryKey());
      }
      catch(java.rmi.RemoteException rme){
	rme.printStackTrace();
      }
    }
    return false;
  }

  public void remove(){
    try{
      delete();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }


  public void store(){
    try{
      if((getEntityState()==STATE_NEW)||(getEntityState()==STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE)){
	insert();
      }
      else if(this.getEntityState()==STATE_NOT_IN_SYNCH_WITH_DATASTORE){
	update();
      }
    }
    catch(Exception e){
      e.printStackTrace();
      throw new javax.ejb.EJBException(e.getMessage());
    }
  }

  public void ejbActivate(){}

  public void ejbLoad(){}

  public void ejbPassivate(){
    if(_columns!=null){
      _columns.clear();
    }
  }

  public void ejbRemove(){remove();}

  public void ejbStore(){store();}

  public void setEntityContext(javax.ejb.EntityContext ctx){}

  public void unsetEntityContext(){}

  void flagColumnUpdate(String columnName){
    if(this._updatedColumns==null){
      _updatedColumns = new Hashtable();
    }
    _updatedColumns.put(columnName.toUpperCase(),Boolean.TRUE);
  }

  boolean hasColumnBeenUpdated(String columnName){
    if(this._updatedColumns==null){
      return false;
    }
    else{
      return (_updatedColumns.get(columnName.toUpperCase())!=null);
    }
  }

  boolean columnsHaveChanged(){
    return (_updatedColumns!=null);
  }

   /**
    * This method outputs the outputString to System.out if the Application property
    * "debug" is set to "TRUE"
    */
   public void debug(String outputString){
    if( IWMainApplicationSettings.isDebugActive() ){
      System.out.println("[DEBUG] \""+outputString+"\" : "+this.getEntityName());

    }
   }



   public void setToInsertStartData(boolean ifTrue){
    this.insertStartData=ifTrue;
   }

   public boolean getIfInsertStartData(){
    return insertStartData;
   }


}

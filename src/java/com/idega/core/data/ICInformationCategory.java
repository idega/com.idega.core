package com.idega.core.data;

import java.sql.*;
import com.idega.data.CacheableEntity;
import com.idega.util.idegaTimestamp;
import com.idega.core.user.data.User;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.presentation.InformationCategory;


public class ICInformationCategory extends CacheableEntity implements InformationCategory{

  private final static String DELETED_COLUMN = "DELETED";
  private final static String DELETED_BY_COLUMN = "DELETED_BY";
  private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";

  public final static String DELETED = "Y";
  public final static String NOT_DELETED = "N";


  public ICInformationCategory(){
    super();
  }

  public ICInformationCategory(int id)throws SQLException{
    super(id);
  }

  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getColumnName(),"Name", true, true, String.class);
    addAttribute(getColumnDescription(),"Description", true, true, String.class);
    addAttribute(getColumnType(),"Type", true, true, String.class);
    addAttribute(getColumnCreated(),"Created", true, true, java.sql.Timestamp.class);
    addAttribute(getColumnValid(),"Valid", true, true, Boolean.class);
    addAttribute(getColumnDeleted(),"Deleted",true,true,String.class,1);
    addManyToOneRelationship(getColumnDeletedBy(),"Deleted by", User.class);
    addAttribute(getColumnDeletedWhen(),"Deleted when",true,true,Timestamp.class);
    addManyToOneRelationship(getColumnFolderSpecific(),"Owner folder", ICInformationFolder.class);
    addManyToOneRelationship(getColumnObjectId(),"Object id", ICObject.class);

    addManyToManyRelationShip(com.idega.core.data.ICObjectInstance.class,"IC_OBJ_INST_INFO_CAT");
  }

  public static String getEntityTableName(){return "IC_INFO_CATEGORY";}
  public static String getColumnName(){return "NAME";}
  public static String getColumnDescription(){return "DESCRIPTION";}
  public static String getColumnType(){return "CAT_TYPE";}
  public static String getColumnCreated(){return "CREATED";}
  public static String getColumnValid(){return "VALID";}
  public static String getColumnFolderSpecific(){return "FOLDER_SPECIFIC";}
  public static String getColumnObjectId(){return "IC_OBJECT_ID";}
  public static String getColumnDeleted() {return(DELETED_COLUMN);}
  public static String getColumnDeletedBy() {return(DELETED_BY_COLUMN);}
  public static String getColumnDeletedWhen() {return(DELETED_WHEN_COLUMN);}

  public String getEntityName(){
    return getEntityTableName();
  }

  public String getName(){
    return getStringColumnValue(getColumnName());
  }
  public void setName(String name){
    setColumn(getColumnName(),name);
  }
  public String getDescription(){
    return getStringColumnValue(getColumnDescription());
  }
  public void setDescription(String description){
    setColumn(getColumnDescription(), description);
  }
  public boolean getValid(){
    return getBooleanColumnValue(getColumnValid());
  }
  public void setValid(boolean valid){
    setColumn(getColumnValid(), valid);
  }
  public java.sql.Timestamp getCreated(){
    return (java.sql.Timestamp) getColumnValue(getColumnCreated());
  }
  public void setCreated(java.sql.Timestamp created){
    setColumn(getColumnCreated(), created);
  }
  public String getType(){
    return getStringColumnValue(getColumnType());
  }
  public void setType(String type){
    setColumn(getColumnType(),type);
  }
  public int getICObjectId(){
    return getIntColumnValue(getColumnObjectId());
  }
  public void setICObjectId(int id){
    setColumn(getColumnObjectId(),id);
  }

  public int getOwnerFolderId(){
    return getIntColumnValue(getColumnFolderSpecific());
  }
  public void setFolderSpecific(int folderId){
    setColumn(getColumnFolderSpecific(),folderId);
  }
  public void setGlobal() throws SQLException {
    this.setColumnAsNull(getColumnFolderSpecific());
  }


  /**
   *
   */
  public boolean getDeleted() {
    String deleted = getStringColumnValue(getColumnDeleted());

    if ((deleted == null) || (deleted.equals(NOT_DELETED)))
      return(false);
    else if (deleted.equals(DELETED))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public void setDeleted(boolean deleted) {
    if (deleted) {
      setColumn(getColumnDeleted(),DELETED);
      setDeletedWhen(idegaTimestamp.getTimestampRightNow());
//      setDeletedBy(iwc.getUserId());
    }
    else {
      setColumn(getColumnDeleted(),NOT_DELETED);
//      setDeletedBy(-1);
//      setDeletedWhen(null);
    }
  }

  /**
   *
   */
  public int getDeletedBy() {
    return(getIntColumnValue(getColumnDeletedBy()));
  }

  /**
   *
   */
  private void setDeletedBy(int userID) {
      setColumn(getColumnDeletedBy(),userID);
  }

  /**
   *
   */
  public Timestamp getDeletedWhen() {
    return((Timestamp)getColumnValue(getColumnDeletedWhen()));
  }

  /**
   *
   */
  private void setDeletedWhen(Timestamp when) {
    setColumn(getColumnDeletedWhen(),when);
  }



}

package com.idega.core.data;

import java.sql.*;
import com.idega.data.CacheableEntity;
import com.idega.util.idegaTimestamp;
import com.idega.core.user.data.User;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.presentation.InformationFolder;


public class ICInformationFolder extends CacheableEntity implements InformationFolder{

  private final static String DELETED_COLUMN = "DELETED";
  private final static String DELETED_BY_COLUMN = "DELETED_BY";
  private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";

  public final static String DELETED = "Y";
  public final static String NOT_DELETED = "N";


  public ICInformationFolder(){
    super();
  }
  public ICInformationFolder(int id)throws SQLException{
    super(id);
  }
  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addManyToOneRelationship(getColumnOwnerGroup(),"Owner group",GenericGroup.class);
    addManyToOneRelationship(getColumnLocaleId(),"Locale id", ICLocale.class);
    addAttribute(getColumnName(),"Name", true, true, String.class);
    addAttribute(getColumnDescription(),"Description", true, true, String.class);
    addAttribute(getColumnType(),"Type", true, true, String.class);
    addAttribute(getColumnCreated(),"Created", true, true, java.sql.Timestamp.class);
    addAttribute(getColumnValid(),"Valid", true, true, Boolean.class);
    addAttribute(getColumnDeleted(),"Deleted",true,true,String.class,1);
    addManyToOneRelationship(getColumnDeletedBy(),"Deleted by", User.class);
    addAttribute(getColumnDeletedWhen(),"Deleted when",true,true,Timestamp.class);
    addManyToOneRelationship(getColumnParentFolderId(),"Parent", ICInformationFolder.class);
    addManyToOneRelationship(getColumnObjectId(),"Object id", ICObject.class);

    addManyToManyRelationShip(com.idega.core.data.ICObjectInstance.class, "IC_OBJ_INST_INFO_FOLDER");
  }

  public static String getEntityTableName(){return "IC_INFO_FOLDER";}
  public static String getColumnName(){return "NAME";}
  public static String getColumnDescription(){return "DESCRIPTION";}
  public static String getColumnType(){return "CAT_TYPE";}
  public static String getColumnCreated(){return "CREATED";}
  public static String getColumnValid(){return "VALID";}
  public static String getColumnOwnerGroup(){return "OWNER_GROUP";}
  public static String getColumnDeleted() {return(DELETED_COLUMN);}
  public static String getColumnDeletedBy() {return(DELETED_BY_COLUMN);}
  public static String getColumnDeletedWhen() {return(DELETED_WHEN_COLUMN);}
  public static String getColumnObjectId(){return "IC_OBJECT_ID";}
  public static String getColumnLocaleId() {return("IC_LOCALE_ID");}
  public static String getColumnParentFolderId() {return("PARENT_FOLDER_ID");}

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

  public int getICObjectId(){
    return getIntColumnValue(getColumnObjectId());
  }
  public void setICObjectId(int id){
    setColumn(getColumnObjectId(),id);
  }

  public int getParentId(){
    return getIntColumnValue(getColumnParentFolderId());
  }
  public void setParentId(int id){
    setColumn(getColumnParentFolderId(),id);
  }

  public int getLocaleId(){
    return getIntColumnValue(getColumnLocaleId());
  }
  public void setLocaleId(int id){
    setColumn(getColumnLocaleId(),id);
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


  public int getOwnerGroupID() {
    int id = getIntColumnValue(getColumnOwnerGroup());
    return id;
  }

  public void setOwnerGroupID(int id) {
    setColumn(getColumnOwnerGroup(), id);
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
//    if (id == -1)
//      setColumn(getColumnDeletedBy(),(Object)null);
//    else
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

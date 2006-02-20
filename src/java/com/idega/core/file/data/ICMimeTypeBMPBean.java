package com.idega.core.file.data;

import java.sql.SQLException;

import com.idega.idegaweb.IWMainApplication;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ICMimeTypeBMPBean extends com.idega.data.CacheableEntityBMPBean implements com.idega.core.file.data.ICMimeType {

  public static String IC_MIME_TYPE_FOLDER = "application/vnd.iw-folder";
  public static String IC_MIME_TYPE_XML = "text/xml";//for ibxml this should be application/vnd.iw-ibxml"


  public ICMimeTypeBMPBean() {
    super();
  }

  public ICMimeTypeBMPBean(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Mime type",true,true, String.class,100);
    addAttribute(getColumnNameDescription(),"Description",true,true, String.class,255);
   addManyToOneRelationship(getColumnNameFileType(),"File type",ICFileType.class);

    setAsPrimaryKey(getIDColumnName(),true);
    setNullable(getIDColumnName(),false);
  }

  public String getEntityName() {
    return("ic_mime_type");
  }

  public static String getColumnNameMimeType(){return "MIME_TYPE";}
  public static String getColumnNameFileType(){return "IC_FILE_TYPE_ID";}
  public static String getColumnNameDescription(){return "DESCRIPTION";}



  public String getMimeType(){
    return (String) getColumnValue(getColumnNameMimeType());
  }

  public void setMimeType(String mimeType){
    setColumn(getColumnNameMimeType(), mimeType);
  }

  public void setMimeTypeAndDescription(String mimeType, String description){
    setMimeType(mimeType);
    setDescription(description);
  }

  public String getDescription(){
    return (String) getColumnValue(getColumnNameDescription());
  }

  public void setDescription(String description){
    setColumn(getColumnNameDescription(), description);
  }

  public int getFileTypeID(){
    return getIntColumnValue(getColumnNameFileType());
  }

  public void setFileTypeId(int fileTypeId){
    setColumn(getColumnNameFileType(), fileTypeId);
  }

  public String getIDColumnName(){
   return  getColumnNameMimeType();
  }

 /**
  *Inserts this entity as a record into the datastore and cache
  */
  public void insert()throws SQLException{
    super.insert();
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().cacheEntity(IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileType.class,Integer.toString(getFileTypeID())),getMimeType());
  }

  /**
  *deletes this entity as a record in the datastore and cache
  */
  public void delete()throws SQLException{
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().removeCachedEntity(getMimeType());
    super.delete();
  }

  /**
  *updates this entity as a record in the datastore and cache
  */
  public void update()throws SQLException{
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().removeCachedEntity(getMimeType());
    super.update();
    IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().cacheEntity(IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileType.class,Integer.toString(getFileTypeID())) ,getMimeType());
  }

  public Class getPrimaryKeyClass(){
      return String.class;
  }
}

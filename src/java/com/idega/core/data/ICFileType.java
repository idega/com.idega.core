package com.idega.core.data;

import com.idega.data.CacheableEntity;
import java.lang.String;
import java.lang.Integer;
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
public class ICFileType extends CacheableEntity {

  public static String IC_FILE_TYPE_APPLICATION = "ic_application";
  public static String IC_FILE_TYPE_AUDIO = "ic_audio";
  public static String IC_FILE_TYPE_DOCUMENT = "ic_document";
  public static String IC_FILE_TYPE_IMAGE = "ic_image";
  public static String IC_FILE_TYPE_VECTOR_GRAPHICS = "ic_vector";
  public static String IC_FILE_TYPE_VIDEO = "ic_video";
  public static String IC_FILE_TYPE_SYSTEM = "ic_system";//idegaWeb database file system (type)


  public ICFileType() {
    super();
  }

    public ICFileType(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameDisplayName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Lýsing",true,true,String.class,500);
    this.addAttribute(getColumnNameUniqueName(),"unique name",true,true,String.class,255);
    addAttribute(getColumnNameFileTypeHandler(),"Filetypehandler",true,true, Integer.class,"many-to-one",ICFileTypeHandler.class);
  }

  public String getEntityName() {
    return("ic_file_type");
  }

  public static String getColumnNameDisplayName(){return "type_display_name";}
  public static String getColumnNameUniqueName() {return "unique_name";}
  public static String getColumnNameDescription(){return "type_description";}
  public static String getColumnNameFileTypeHandler(){return "ic_file_type_handler_id";}


  public String getName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  public String getDisplayName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }

  public void setDisplayName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  public void setName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  public void setDescription(String typeDescription){
    this.setColumn(getColumnNameDescription(),typeDescription);
  }

  public String getUniqueName() {
    return this.getStringColumnValue(getColumnNameUniqueName());
  }

  public void setUniqueName(String uniqueName) {
    this.setColumn(getColumnNameUniqueName(),uniqueName);
  }

  public void setType(String uniqueName) {
    setUniqueName(uniqueName);
  }

  public void setFileTypeHandlerId(int fileTypeId) {
    this.setColumn(getColumnNameFileTypeHandler(),fileTypeId);
  }

  public void setFileTypeHandler(ICFileTypeHandler handler) {
    setFileTypeHandlerId(handler.getID());
  }

  public void insertStartData() {
    try {
      ICFileType type;
      ICFile file;
      ICFileTypeHandler handler = new ICFileTypeHandler();

      type = new ICFileType();
      type.setName("iW system");
      type.setUniqueName(IC_FILE_TYPE_SYSTEM);
      type.setDescription("IdegaWeb database file system types such as ic_folder");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_SYSTEM));
      type.insert();

      type = new ICFileType();
      type.setName("Applications");
      type.setUniqueName(IC_FILE_TYPE_APPLICATION);
      type.setDescription("Applications or executables");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_APPLICATION));
      type.insert();

      type = new ICFileType();
      type.setName("Audio");
      type.setType(IC_FILE_TYPE_AUDIO);
      type.setDescription("Audio files such as .mp3 .au");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_AUDIO));
      type.insert();

      type = new ICFileType();
      type.setName("Documents");
      type.setType(IC_FILE_TYPE_DOCUMENT);
      type.setDescription("Documents or textfiles such as .doc .xls .txt .html");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_DOCUMENT));
      type.insert();

      type = new ICFileType();
      type.setName("Images");
      type.setType(IC_FILE_TYPE_IMAGE);
      type.setDescription("Image files");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_IMAGE));
      type.insert();

      type = new ICFileType();
      type.setName("Vector graphics");
      type.setType(IC_FILE_TYPE_VECTOR_GRAPHICS);
      type.setDescription("Vector graphic files such as .swf (Flash) .dir (Shockwave)");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS));
      type.insert();

      type = new ICFileType();
      type.setName("Video");
      type.setType(IC_FILE_TYPE_VIDEO);
      type.setDescription("Video or movie files such as .mov .mpg .avi");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,ICFileTypeHandler.IC_FILE_TYPE_HANDLER_VIDEO));
      type.insert();

      this.cacheEntity();

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
     }
  }

  public String getCacheKey(){
    return getColumnNameUniqueName();
  }

}
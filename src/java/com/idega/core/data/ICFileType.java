package com.idega.core.data;

import com.idega.data.CacheableEntity;
import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;


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
    addAttribute("ic_file_handler_id","File handler",true,true, Integer.class,"many-to-one",ICFileHandler.class);
  }

  public String getEntityName() {
    return("ic_file_type");
  }

  public static String getColumnNameDisplayName(){return "type_display_name";}
  public static String getColumnNameUniqueName() {return "unique_name";}
  public static String getColumnNameDescription(){return "type_description";}


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

  public void insertStartData() {
    try {
      ICFileType type;
      ICFile file;

      type = new ICFileType();
      type.setName("Applications");
      type.setUniqueName(IC_FILE_TYPE_APPLICATION);
      type.setDescription("Applications or executables");
      type.insert();

      type = new ICFileType();
      type.setName("Audio");
      type.setType(IC_FILE_TYPE_AUDIO);
      type.setDescription("Audio files such as .mp3 .au");
      type.insert();

      type = new ICFileType();
      type.setName("Documents");
      type.setType(IC_FILE_TYPE_DOCUMENT);
      type.setDescription("Documents or textfiles such as .doc .xls .txt .html");
      type.insert();

      type = new ICFileType();
      type.setName("Images");
      type.setType(IC_FILE_TYPE_IMAGE);
      type.setDescription("Image files");
      type.insert();

      type = new ICFileType();
      type.setName("Vector graphics");
      type.setType(IC_FILE_TYPE_VECTOR_GRAPHICS);
      type.setDescription("Vector graphic files such as .swf (Flash) .dir (Shockwave)");
      type.insert();

      type = new ICFileType();
      type.setName("Video");
      type.setType(IC_FILE_TYPE_VIDEO);
      type.setDescription("Video or movie files such as .mov .mpg .avi");
      type.insert();

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

  public String getCacheKey(){
    return getColumnNameUniqueName();
  }

}
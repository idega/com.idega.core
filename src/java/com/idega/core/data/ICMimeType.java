package com.idega.core.data;

import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;
import com.idega.data.CacheableEntity;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ICMimeType extends CacheableEntity {

  public static String IC_MIME_TYPE_FOLDER = "application/vnd.iw-folder";
  public static String IC_MIME_TYPE_XML = "text/xml";//for ibxml this should be application/vnd.iw-ibxml"


  public ICMimeType() {
    super();
  }

  public ICMimeType(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Mime type",true,true, String.class,100);
    addAttribute(getColumnNameDescription(),"Description",true,true, String.class,255);
    addAttribute(getColumnNameFileType(),"File type",true,true, Integer.class,"many-to-one",ICFileType.class);

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

//initial data is inserted in com.idega.block.media.business.MediaBundleStarter

}


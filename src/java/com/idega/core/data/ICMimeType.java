package com.idega.core.data;

import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;
import com.idega.data.GenericEntity;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */

public class ICMimeType extends GenericEntity {


  public ICMimeType() {
    super();
  }

  public ICMimeType(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName(),"Mime type",true,true, String.class,100);
    addAttribute(getColumnNameFileType(),"File type",true,true, Integer.class,"many-to-one",ICFileType.class);
    setAsPrimaryKey(getIDColumnName(),true);
    setNullable(getIDColumnName(),false);
  }

  public String getEntityName() {
    return("ic_mime_type");
  }

  public static String getColumnNameMimeType(){return "MIME_TYPE";}
  public static String getColumnNameFileType(){return "IC_FILE_TYPE_ID";}


  public String getMimeType(){
    return (String) getColumnValue(getColumnNameMimeType());
  }

  public void setMimeType(String mimeType){
    setColumn(getColumnNameMimeType(), mimeType);
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


}
package com.idega.core.data;

import com.idega.data.BlobWrapper;
import com.idega.core.data.ICLanguage;
import java.sql.Timestamp;
import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;
import com.idega.data.TreeableEntity;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Vilhjalmsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class ICFile extends TreeableEntity {

  private static final String FILE_VALUE = "file_value";

  public ICFile() {
    super();
  }

  public ICFile(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameLanguageId(),"Language",true,true, Integer.class,"many-to-one",ICLanguage.class);
    addAttribute(getColumnNameMimeType(),"Mime Type of file",true,true, String.class,100,"many-to-one",ICMimeType.class);
    addAttribute(getColumnNameName(),"File name",true,true, String.class, 255);
    addAttribute(getColumnNameDescription(),"Description",true,true, String.class, 1000);
    addAttribute(getColumnNameFileValue(),"The file value",true,true, com.idega.data.BlobWrapper.class);
    addAttribute(getColumnNameCreationDate(),"Creation date",true,true, java.sql.Timestamp.class);
    addAttribute(getColumnNameModificationDate(),"Modification date",true,true, java.sql.Timestamp.class);
    addAttribute(getColumnNameFileSize(),"file size in bytes",true,true,java.lang.Integer.class);

    addMetaDataRelationship();//can have extra info in the ic_metadata table

  }

  public String getEntityName() {
    return("ic_file");
  }

  public static String getEntityTableName(){ return "IC_FILE";}

  public static String getColumnNameMimeType(){return "MIME_TYPE";}
  public static String getColumnNameName(){return "NAME";}
  public static String getColumnNameDescription(){return "DESCRIPTION";}
  public static String getColumnNameFileValue(){return "FILE_VALUE";}
  public static String getColumnNameCreationDate(){return "CREATION_DATE";}
  public static String getColumnNameModificationDate(){return "MODIFICATION_DATE";}
  public static String getColumnNameFileSize(){return "FILE_SIZE";}
  public static String getColumnNameLanguageId(){return "IC_LANGUAGE_ID";}

  public static String getColumnFileValue(){
    return FILE_VALUE;
  }

  public int getLanguage(){
    return getIntColumnValue(getColumnNameLanguageId());
  }

  public String getMimeType(){
    return (String) getColumnValue(getColumnNameMimeType());
  }

  public String getName(){
    return (String) getColumnValue(getColumnNameName());
  }

  public String getDescription(){
    return (String) getColumnValue(getColumnNameDescription());
  }
/*
  public BlobWrapper getFileValue(){
    return (BlobWrapper) getColumnValue("file_value");
  }
*/

  public InputStream getFileValue()throws Exception{
    return getInputStreamColumnValue(getColumnFileValue());
  }

  public Timestamp getCreationDate(){
    return (Timestamp) getColumnValue(getColumnNameCreationDate());
  }

  public Timestamp getModificationDate(){
    return (Timestamp) getColumnValue(getColumnNameModificationDate());
  }

  public Long getFileSize(){
    return (Long) getColumnValue(getColumnNameFileSize());
  }

  public void setLanguage(int language){
    setColumn(getColumnNameLanguageId(), new Integer(language));
  }

  public void setMimeType(String mimeType){
    setColumn(getColumnNameMimeType(), mimeType);
  }

  public void setName(String Name){
    setColumn(getColumnNameName(), Name);
  }

  public void setDescription(String description){
    setColumn(getColumnNameDescription(), description);
  }

  public void setFileSize(Integer fileSize){
    setColumn(getColumnNameFileSize(), fileSize);
  }


  public void setFileSize(int fileSize){
    setColumn(getColumnNameFileSize(), fileSize);
  }
/*
  public void setFileValue(BlobWrapper fileValue){
    setColumn("file_value", fileValue);
  }
*/
  public void setFileValue(InputStream fileValue){
    setColumn(getColumnFileValue(), fileValue);
  }

  public OutputStream getFileValueForWrite(){
    return getColumnOutputStream(getColumnFileValue());
  }

  public void setCreationDate(Timestamp creationDate){
    setColumn(getColumnNameCreationDate(), creationDate);
  }

  public void setModificationDate(Timestamp modificationDate){
    setColumn(getColumnNameModificationDate(), modificationDate);
  }

  public void insert()throws SQLException{
    this.setCreationDate(com.idega.util.idegaTimestamp.getTimestampRightNow());
    super.insert();
  }

  public void update()throws SQLException{
    this.setModificationDate(com.idega.util.idegaTimestamp.getTimestampRightNow());
    super.update();
  }
}
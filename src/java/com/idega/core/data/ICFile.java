package com.idega.core.data;

import com.idega.data.GenericEntity;
import com.idega.data.BlobWrapper;
import com.idega.core.data.ICLanguage;
import java.sql.Timestamp;
import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ICFile extends GenericEntity {

  private static final String file_value = "file_value";

  public ICFile() {
    super();
  }

  public ICFile(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("ic_language_id","Language",true,true, Integer.class,"many-to-one",ICLanguage.class);
    addAttribute("mime_type","Type of file",true,true, String.class,255);
    addAttribute("name","File name",true,true, String.class, 255);
    addAttribute("description","Description",true,true, String.class, 1000);
    addAttribute(getColumnFileValue(),"The file value",true,true, com.idega.data.BlobWrapper.class);
    addAttribute("creation_date","Creation date",true,true, java.sql.Timestamp.class);
    addAttribute("modification_date","Modification date",true,true, java.sql.Timestamp.class);
    addAttribute("parent_id","Parent",true,true, Integer.class,"many-to-one",ICFile.class);

    setNullable("parent_id",true);
    addMetaDataRelationship();//can have extra info in the ic_metadata table

    addManyToManyRelationShip(ICFileCategory.class,"ic_file_file_category");

  }

  public String getEntityName() {
    return("ic_file");
  }

  public static String getColumnFileValue(){
    return file_value;
  }

  public int getLanguage(){
    return getIntColumnValue("ic_language_id");
  }

  public String getMimeType(){
    return (String) getColumnValue("mime_type");
  }

  public String getName(){
    return (String) getColumnValue("name");
  }

  public String getDescription(){
    return (String) getColumnValue("description");
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
    return (Timestamp) getColumnValue("creation_date");
  }

  public Timestamp getModificationDate(){
    return (Timestamp) getColumnValue("modification_date");
  }


  public void setLanguage(int language){
    setColumn("ic_language_id", new Integer(language));
  }

  public void setMimeType(String mimeType){
    setColumn("mime_type", mimeType);
  }

  public void setName(String Name){
    setColumn("name", Name);
  }

  public void setDescription(String description){
    setColumn("description", description);
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
    setColumn("creation_date", creationDate);
  }

  public void setModificationDate(Timestamp modificationDate){
    setColumn("modification_date", modificationDate);
  }

  public void setParentID(int parentID){
    setColumn("parent_id", new Integer(parentID));
  }

  public int getParentID(){
    return getIntColumnValue("parent_id");
  }
}
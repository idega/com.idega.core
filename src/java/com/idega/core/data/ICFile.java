package com.idega.core.data;

import com.idega.data.GenericEntity;
import com.idega.data.BlobWrapper;
import com.idega.core.data.ICLanguage;
//import com.idega.core.data.Ic_File;
import java.sql.Timestamp;
import java.lang.String;
import java.lang.Integer;
import java.sql.SQLException;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class ICFile extends GenericEntity {

  public ICFile() {
    super();
  }

    public ICFile(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("ic_language_id","ic_tungumála_id",true,true, "java.lang.Integer","many-to-one","com.idega.core.data.Language");
    addAttribute("mime_type","stöðlun",true,true, "java.lang.String",20);
    addAttribute("name","nafn",true,true, "java.lang.String", 20);
    addAttribute("description","lýsing",true,true, "java.lang.String", 1000);
    addAttribute("file_value","skráar-gildi",true,true, "com.idega.data.BlobWrapper");
    addAttribute("creation_date","stofn_tími",true,true, "java.sql.Timestamp");
    addAttribute("modification_date","tími_breytingar",true,true, "java.sql.Timestamp");
    addAttribute("parent_id","föður_id",true,true, "java.lang.Integer","many-to-one","com.idega.core.data.ICFile");
  }

  public String getEntityName() {
    return("ic_file");
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

  public BlobWrapper getFileValue(){
    return (BlobWrapper) getColumnValue("file_value");
  }

  public Timestamp getCreationDate(){
    return (Timestamp) getColumnValue("creation_date");
  }

  public Timestamp getModificationDate(){
    return (Timestamp) getColumnValue("modification_date");
  }

  public int getParentID(){
    return getIntColumnValue("parent_id");
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

  public void setFileValue(BlobWrapper fileValue){
    setColumn("file_value", fileValue);
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
}
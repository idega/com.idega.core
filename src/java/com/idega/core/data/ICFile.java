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
import com.idega.core.user.data.User;
import com.idega.presentation.IWContext;
import com.idega.util.idegaTimestamp;
import java.util.Iterator;
import com.idega.idegaweb.IWCacheManager;
import java.util.Locale;

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
  public static String IC_ROOT_FOLDER_CACHE_KEY = "ic_root_folder";
  public static String IC_ROOT_FOLDER_NAME = "ICROOT";

  public final static String DELETED = "Y";
  public final static String NOT_DELETED = "N";


  public ICFile() {
    super();
    _sortLeafs = true;
  }

  public ICFile(int id) throws SQLException{
    super(id);
    _sortLeafs = true;
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    //Removed LanguageIDColumn in favor of Locale
    //addAttribute(getColumnNameLanguageId(),"Language",true,true, Integer.class,"many-to-one",ICLanguage.class);
    addManyToOneRelationship(getColumnNameLocale(),"Locale",ICLocale.class);
//    addManyToOneRelationship(getColumnNameMimeType(),"Mime Type of file",ICMimeType.class);
    addAttribute(getColumnNameMimeType(),"Mime Type of file",true,true, String.class,100,"many-to-one",ICMimeType.class);
    addAttribute(getColumnNameName(),"File name",true,true, String.class, 255);
    addAttribute(getColumnNameDescription(),"Description",true,true, String.class, 1000);
    addAttribute(getColumnNameFileValue(),"The file value",true,true, com.idega.data.BlobWrapper.class);
    addAttribute(getColumnNameCreationDate(),"Creation date",true,true, java.sql.Timestamp.class);
    addAttribute(getColumnNameModificationDate(),"Modification date",true,true, java.sql.Timestamp.class);
    addAttribute(getColumnNameFileSize(),"file size in bytes",true,true,java.lang.Integer.class);
/*
    addAttribute(getColumnDeleted(),"Deleted",true,true,String.class,1);
    addAttribute(getColumnDeletedBy(),"Deleted by",true,true,Integer.class,"many-to-one",User.class);
    addAttribute(getColumnDeletedWhen(),"Deleted when",true,true,Timestamp.class);*/


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
  public static String getColumnNameLocale() {return "IC_LOCALE_ID";}
  public static String getColumnDeleted() {return "DELETED";}
  public static String getColumnDeletedBy() {return "DELETED_BY";}
  public static String getColumnDeletedWhen() {return "DELETED_WHEN";}
  /**
   * @deprecated Replaced with getColumnLocale()
   */
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

  public Integer getFileSize(){
    return (Integer) getColumnValue(getColumnNameFileSize());
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

  public Locale getLocale(){
    ICLocale icLocale = this.getICLocale();
    if(icLocale!=null){
      return icLocale.getLocaleObject();
    }
    return null;
  }

  public ICLocale getICLocale(){
    return (ICLocale)super.getColumnValue(this.getColumnNameLocale());
  }

  public int getLocaleId(){
    return super.getIntColumnValue(this.getColumnNameLocale());
  }

  public void setLocale(){
    super.getIntColumnValue(this.getColumnNameLocale());
  }

// and here are the delete functions

  public boolean isLeaf(){
    if(ICMimeType.IC_MIME_TYPE_FOLDER.equalsIgnoreCase(this.getMimeType())){
      return false;
    }
    return true;
  }


  public boolean getDeleted() {
    String deleted = getStringColumnValue(getColumnDeleted());

    if ((deleted == null) || (deleted.equals(NOT_DELETED)))
      return(false);
    else if (deleted.equals(DELETED))
      return(true);
    else
      return(false);
  }

  public void setDeleted(boolean deleted) {
    if (deleted) {
      setColumn(getColumnDeleted(),DELETED);
    }
    else {
      setColumn(getColumnDeleted(),NOT_DELETED);
    }
  }

  public int getDeletedByUserId() {
    return(getIntColumnValue(getColumnDeletedBy()));
  }

  private void setDeletedByUserId(int id) {
    setColumn(getColumnDeletedBy(),id);
  }


  public Timestamp getDeletedWhen() {
    return((Timestamp)getColumnValue(getColumnDeletedWhen()));
  }

  private void setDeletedWhen(Timestamp when) {
    setColumn(getColumnDeletedWhen(),when);
  }

  /**
   * Overrides the delete function because we keep all files
   * throws every child into the trash also. Recursive function if the file has children
   */
  public void delete() throws SQLException {
    setDeleted(true);
    setDeletedWhen(idegaTimestamp.getTimestampRightNow());
    try{
      IWContext iwc = IWContext.getInstance();
      setDeletedByUserId(iwc.getUserId());
    }
    catch(Exception e){
     e.printStackTrace(System.err);
    }

    ICFile file = (ICFile) getParentNode();
    if( file!= null ) file.removeChild(this);

    Iterator iter = getChildren();
    if( iter != null ){
      while (iter.hasNext()) {
        ICFile item = (ICFile) iter.next();
        item.delete();
      }
    }

    update();

  }
  /** undeletes this file **/
  public void unDelete(boolean setICRootAsParent) throws SQLException {
    setDeleted(false);
    setDeletedWhen(idegaTimestamp.getTimestampRightNow());
    try{
      IWContext iwc = IWContext.getInstance();
      setDeletedByUserId(iwc.getUserId());
      if( setICRootAsParent ){
        IWCacheManager cm = iwc.getApplication().getIWCacheManager();
        ICFile parent = (ICFile) cm.getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);
        if( parent!= null ) parent.addChild(this);
      }
    }
    catch(Exception e){
     e.printStackTrace(System.err);
    }

    update();

  }

  /**
   * This method delete the file from the database permenantly. Recursive function if the file has children.
   * Use with caution!
   */
  public void superDelete() throws SQLException {
    ICFile file = (ICFile) getParentNode();
    if( file!= null ) file.removeChild(this);

    Iterator iter = getChildren();
    if( iter != null ){
      while (iter.hasNext()) {
        ICFile item = (ICFile) iter.next();
        item.superDelete();
      }
    }
    super.delete();
  }

}
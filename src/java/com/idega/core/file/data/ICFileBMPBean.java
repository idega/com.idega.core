package com.idega.core.file.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.localisation.data.ICLocale;
import com.idega.core.user.data.User;
import com.idega.core.version.data.ICItem;
import com.idega.core.version.data.ICVersion;
import com.idega.core.version.util.ICVersionQuery;
import com.idega.data.BlobWrapper;
import com.idega.data.IDOQuery;
import com.idega.data.MetaDataCapable;
import com.idega.data.TreeableEntity;
import com.idega.data.TreeableEntityBMPBean;
import com.idega.idegaweb.IWCacheManager;
import com.idega.presentation.IWContext;
import com.idega.util.IWTimestamp;

/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Vilhjalmsson</a>,<a href="tryggvi@idega.is">Tryggvi Larusson</a>,<a href="aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */

public class ICFileBMPBean extends TreeableEntityBMPBean implements ICFile,TreeableEntity,MetaDataCapable {

  private static final String ENTITY_NAME = "IC_FILE";
private static final String FILE_VALUE = "FILE_VALUE";
  public static String IC_ROOT_FOLDER_CACHE_KEY = "IC_ROOT_FOLDER";
  public static String IC_ROOT_FOLDER_NAME = "ICROOT";

  public final static String DELETED = "Y";
  public final static String NOT_DELETED = "N";
  
  private static final String TABLENAME_ICFILE_ICITEM = "ic_file_ic_item";
  private static final String TABLENAME_ICFILE_ICVERSION = "ic_file_ic_version";


  public ICFileBMPBean() {
    super();
    _sortLeafs = true;
  }

  public ICFileBMPBean(int id) throws SQLException{
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

    addAttribute(getColumnDeleted(),"Deleted",true,true,String.class,1);
    addAttribute(getColumnDeletedBy(),"Deleted by",true,true,Integer.class,"many-to-one",User.class);
    addAttribute(getColumnDeletedWhen(),"Deleted when",true,true,Timestamp.class);
	
	
	addManyToManyRelationShip(ICItem.class,TABLENAME_ICFILE_ICITEM);
	addManyToManyRelationShip(ICVersion.class,TABLENAME_ICFILE_ICVERSION);

    addMetaDataRelationship();//can have extra info in the ic_metadata table

  }

  public String getEntityName() {
    return(ENTITY_NAME);
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
    return (String) getStringColumnValue(getColumnNameMimeType());
  }

  public String getName(){
    return (String) getStringColumnValue(getColumnNameName());
  }

  public String getDescription(){
    return (String) getStringColumnValue(getColumnNameDescription());
  }

  public BlobWrapper getBlobWrapperFileValue(){
    return (BlobWrapper) getColumnValue(getColumnFileValue());
  }


  public InputStream getFileValue(){
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

  public void setBlobWrapperFileValue(BlobWrapper fileValue){
    setColumn(getColumnFileValue(), fileValue);
  }

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
    this.setCreationDate(com.idega.util.IWTimestamp.getTimestampRightNow());
    super.insert();
  }

  public void update()throws SQLException{
    this.setModificationDate(com.idega.util.IWTimestamp.getTimestampRightNow());
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
    if(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER.equalsIgnoreCase(this.getMimeType())){
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
    setDeletedWhen(IWTimestamp.getTimestampRightNow());
    try{
      IWContext iwc = IWContext.getInstance();
      int userId = iwc.getUserId();
      if(userId != -1){
        setDeletedByUserId(userId);
      }
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
    setDeletedWhen(IWTimestamp.getTimestampRightNow());
    try{
      IWContext iwc = IWContext.getInstance();
      setDeletedByUserId(iwc.getUserId());
      if( setICRootAsParent ){
        IWCacheManager cm = iwc.getApplication().getIWCacheManager();
        ICFile parent = (ICFile) cm.getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
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
    this.removeAllMetaData();
    this.update();
    super.delete();
  }
  
  
   public Integer ejbFindByFileName(String name)throws FinderException{
    Collection files = idoFindAllIDsByColumnBySQL(this.getColumnNameName(),name);
    if(!files.isEmpty()){
      return (Integer)files.iterator().next();
    }
    else throw new FinderException("File was not found");
  }
  
  
  public Integer ejbFindEntityOfSpecificVersion(ICVersion version) throws FinderException{
  	ICVersionQuery query = new ICVersionQuery();
  	query.appendFindEntityOfSpecificVersionQuery(TABLENAME_ICFILE_ICVERSION,version);
  	
  	return (Integer)this.idoFindOnePKByQuery(query);
  }
  
  /**
   * @deprecated Legacy
   * @return
   * @throws FinderException
   */
  public  Collection ejbFindAllDescendingOrdered() throws FinderException {
	String query = "select * from "+this.getTableName()+" order by "+getIDColumnName()+" desc";
	return idoFindPKsBySQL(query);
  }
  
  public Object ejbFindRootFolder() throws FinderException {
		//EntityFinder.findAllByColumn(file,com.idega.core.data.ICFileBMPBean.getColumnNameName(),com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME,com.idega.core.data.ICFileBMPBean.getColumnNameMimeType(),com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		IDOQuery query = idoQuery();
		query.appendSelectAllFrom(this);
		query.appendWhereEqualsQuoted(ICFileBMPBean.getColumnNameName(), ICFileBMPBean.IC_ROOT_FOLDER_NAME);
		query.appendAndEqualsQuoted(com.idega.core.file.data.ICFileBMPBean.getColumnNameMimeType(),com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);

		return idoFindOnePKByQuery(query);
  }

/* (non-Javadoc)
 * @see com.idega.core.data.ICFile#isFolder()
 */
public boolean isFolder()
{
	if (getMimeType().equals(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER)) {
		return true;
	} else {
		return false;
	}
}
  

}

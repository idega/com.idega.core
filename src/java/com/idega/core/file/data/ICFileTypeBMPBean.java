package com.idega.core.file.data;

import java.sql.SQLException;

import javax.ejb.FinderException;

import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title: idegaWeb Classes Description: Copyright: Copyright (c) 2001 Company:
 * idega
 *
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class ICFileTypeBMPBean extends com.idega.data.CacheableEntityBMPBean implements com.idega.core.file.data.ICFileType {

	private static final long serialVersionUID = 4380418851240588228L;

  public static String IC_FILE_TYPE_APPLICATION = "ic_application";
  public static String IC_FILE_TYPE_AUDIO = "ic_audio";
  public static String IC_FILE_TYPE_DOCUMENT = "ic_document";
  public static String IC_FILE_TYPE_IMAGE = "ic_image";
  public static String IC_FILE_TYPE_VECTOR_GRAPHICS = "ic_vector";
  public static String IC_FILE_TYPE_VIDEO = "ic_video";
  public static String IC_FILE_TYPE_SYSTEM = "ic_system";//idegaWeb database file system (type)
  public static String IC_FILE_TYPE_ZIP = "ic_zip";
  private static final String ENTITY_NAME = "IC_FILE_TYPE";
  private static final String COLUMN_IC_FILE_TYPE_HANDLER_ID = "IC_FILE_TYPE_HANDLER_ID";
  private static final String COLUMN_TYPE_DESCRIPTION = "TYPE_DESCRIPTION";
  private static final String COLUMN_TYPE_DISPLAY_NAME = "TYPE_DISPLAY_NAME";
  private static final String COLUMN_UNIQUE_NAME = "UNIQUE_NAME";


  public ICFileTypeBMPBean() {
    super();
  }

    public ICFileTypeBMPBean(int id) throws SQLException{
    super(id);
  }

  @Override
public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getColumnNameDisplayName(),"Nafn",true,true,String.class,255);
    this.addAttribute(getColumnNameDescription(),"Lýsing",true,true,String.class,500);
    this.addAttribute(getColumnNameUniqueName(),"unique name",true,true,String.class,255);
    addManyToOneRelationship(getColumnNameFileTypeHandler(),"Filetypehandler",ICFileTypeHandler.class);
  }

  @Override
public String getEntityName() {
    return(ENTITY_NAME);
  }

  public static String getColumnNameDisplayName(){return COLUMN_TYPE_DISPLAY_NAME;}
  public static String getColumnNameUniqueName() {return COLUMN_UNIQUE_NAME;}
  public static String getColumnNameDescription(){return COLUMN_TYPE_DESCRIPTION;}
  public static String getColumnNameFileTypeHandler(){return COLUMN_IC_FILE_TYPE_HANDLER_ID;}


  @Override
public String getName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  @Override
public String getDisplayName(){
    return this.getStringColumnValue(getColumnNameDisplayName());
  }

  @Override
public String getDescription(){
    return this.getStringColumnValue(getColumnNameDescription());
  }

  @Override
public void setDisplayName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  @Override
public void setName(String typeName){
    this.setColumn(getColumnNameDisplayName(),typeName);
  }

  @Override
public void setDescription(String typeDescription){
    this.setColumn(getColumnNameDescription(),typeDescription);
  }

  @Override
public String getUniqueName() {
    return this.getStringColumnValue(getColumnNameUniqueName());
  }

  @Override
public void setUniqueName(String uniqueName) {
    this.setColumn(getColumnNameUniqueName(),uniqueName);
  }

  @Override
public void setType(String uniqueName) {
    setUniqueName(uniqueName);
  }

  @Override
public void setFileTypeHandlerId(int fileTypeId) {
    this.setColumn(getColumnNameFileTypeHandler(),fileTypeId);
  }

  @Override
public void setFileTypeHandler(ICFileTypeHandler handler) {
    setFileTypeHandlerId(handler.getID());
  }

  @Override
public int getFileTypeHandlerID(){
    return getIntColumnValue(getColumnNameFileTypeHandler());
  }

  @Override
public void insertStartData() {
    try {
      ICFileType type;

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("iW system");
      type.setUniqueName(IC_FILE_TYPE_SYSTEM);
      type.setDescription("IdegaWeb database file system types such as ic_folder");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_SYSTEM));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Applications");
      type.setUniqueName(IC_FILE_TYPE_APPLICATION);
      type.setDescription("Applications or executables");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_APPLICATION));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Audio");
      type.setType(IC_FILE_TYPE_AUDIO);
      type.setDescription("Audio files such as .mp3 .au");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_AUDIO));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Documents");
      type.setType(IC_FILE_TYPE_DOCUMENT);
      type.setDescription("Documents or textfiles such as .doc .xls .txt .html");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_DOCUMENT));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Images");
      type.setType(IC_FILE_TYPE_IMAGE);
      type.setDescription("Image files");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_IMAGE));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Vector graphics");
      type.setType(IC_FILE_TYPE_VECTOR_GRAPHICS);
      type.setDescription("Vector graphic files such as .swf (Flash) .dir (Shockwave)");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Video");
      type.setType(IC_FILE_TYPE_VIDEO);
      type.setDescription("Video or movie files such as .mov .mpg .avi");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_VIDEO));
      type.insert();

      type = ((com.idega.core.file.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
      type.setName("Zip");
      type.setType(IC_FILE_TYPE_ZIP);
      type.setDescription("Zip archive files .zip");
      type.setFileTypeHandler((ICFileTypeHandler) IWMainApplication.getDefaultIWMainApplication().getIWCacheManager().getFromCachedTable(ICFileTypeHandler.class,com.idega.core.file.data.ICFileTypeHandlerBMPBean.IC_FILE_TYPE_HANDLER_ZIP));
      type.insert();

      this.cacheEntity();

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
     }
	    catch(Exception e){
	    		System.err.println("ICFileTypeBMPBEan: Error inserting start data: "+e);
	    }
  }

  @Override
  public String getCacheKey(){
    return getColumnNameUniqueName();
  }

  	public Object ejbFindByUniqueName(String uniqueName) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(getColumnNameUniqueName()), MatchCriteria.EQUALS, uniqueName));
		return idoFindOnePKByQuery(query);
	}
}

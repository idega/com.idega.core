package com.idega.core.data;

import com.idega.data.CacheableEntity;
import java.lang.String;
import java.sql.SQLException;
import com.idega.block.media.business.*;

/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
/**@todo : add localization support for category names
 *
 */
public class ICFileTypeHandlerBMPBean extends com.idega.data.CacheableEntityBMPBean implements com.idega.core.data.ICFileTypeHandler {
  public static String SUFFIX = "_type_handler";
  public static String IC_FILE_TYPE_HANDLER_APPLICATION = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_APPLICATION+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_AUDIO = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_AUDIO+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_DOCUMENT = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_DOCUMENT+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_IMAGE = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_IMAGE+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_VECTOR_GRAPHICS+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_VIDEO = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_VIDEO+SUFFIX;
  public static String IC_FILE_TYPE_HANDLER_SYSTEM = com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_SYSTEM+SUFFIX;//idegaWeb database file system (type)

  public ICFileTypeHandlerBMPBean() {
    super();
  }

    public ICFileTypeHandlerBMPBean(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnNameHandlerName(),"Filetypehandler name",true,true, String.class,255);
    addAttribute(getColumnNameHandlerClass(),"Filetypehandler class",true,true, String.class,500);
  }

  public String getEntityName() {
    return("ic_file_type_handler");
  }

  public static String getColumnNameHandlerName(){return "type_handler_name";}
  public static String getColumnNameHandlerClass() {return "type_handler_class";}


  public String getName(){
    return this.getStringColumnValue(getColumnNameHandlerName());
  }

  public String getHandlerClass(){
    return this.getStringColumnValue(getColumnNameHandlerClass());
  }

  public String getHandlerName(){
    return this.getStringColumnValue(getColumnNameHandlerName());
  }

  public void setName(String name){
    this.setColumn(getColumnNameHandlerName(),name);
  }

  public void setHandlerName(String name){
    setName(name);
  }

  public void setHandlerClass(String classString){
    this.setColumn(getColumnNameHandlerClass(),classString);
  }

  public void setHandlerClass(Class theClass){
    this.setColumn(getColumnNameHandlerClass(),theClass.getName());
  }

  public void setNameAndHandlerClass(String name, String classString){
    setName(name);
    setHandlerClass(classString);
  }


  public void setNameAndHandlerClass(String name, Class theClass){
    setName(name);
    setHandlerClass(theClass);
  }

  public void insertStartData() {
    try {
      ICFileTypeHandler handler;

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_SYSTEM, SystemTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_APPLICATION, ApplicationTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_AUDIO, AudioTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_DOCUMENT, DocumentTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_IMAGE, ImageTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS, VectorTypeHandler.class );
      handler.insert();

      handler = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
      handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_VIDEO, VideoTypeHandler.class );
      handler.insert();

      handler.cacheEntity();

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

  public String getCacheKey(){
    return getColumnNameHandlerName();
  }


  }

package com.idega.core.file.data;
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
public class ICFileTypeHandlerBMPBean extends com.idega.data.CacheableEntityBMPBean implements com.idega.core.file.data.ICFileTypeHandler
{
	public static String SUFFIX = "_type_handler";
	public static String IC_FILE_TYPE_HANDLER_APPLICATION = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_APPLICATION + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_AUDIO = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_AUDIO + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_DOCUMENT = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_DOCUMENT + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_IMAGE = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_IMAGE + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_VECTOR_GRAPHICS + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_VIDEO = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_VIDEO + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_SYSTEM = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_SYSTEM + SUFFIX;
	public static String IC_FILE_TYPE_HANDLER_ZIP = com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_ZIP+SUFFIX;
	private static final String ENTITY_NAME = "IC_FILE_TYPE_HANDLER";
	private static final String COLUMN_TYPE_HANDLER_CLASS = "TYPE_HANDLER_CLASS";
	private static final String COLUMN_TYPE_HANDLER_NAME = "TYPE_HANDLER_NAME";
	//idegaWeb database file system (type)
	public ICFileTypeHandlerBMPBean()
	{
		super();
	}
	public ICFileTypeHandlerBMPBean(int id) throws Exception
	{
		super(id);
	}
	public void initializeAttributes()
	{
		addAttribute(getIDColumnName());
		addAttribute(getColumnNameHandlerName(), "Filetypehandler name", true, true, String.class, 255);
		addAttribute(getColumnNameHandlerClass(), "Filetypehandler class", true, true, String.class, 500);
	}
	public String getEntityName()
	{
		return (ENTITY_NAME);
	}
	public static String getColumnNameHandlerName()
	{
		return COLUMN_TYPE_HANDLER_NAME;
	}
	public static String getColumnNameHandlerClass()
	{
		return COLUMN_TYPE_HANDLER_CLASS;
	}
	public String getName()
	{
		return this.getStringColumnValue(getColumnNameHandlerName());
	}
	public String getHandlerClass()
	{
		return this.getStringColumnValue(getColumnNameHandlerClass());
	}
	public String getHandlerName()
	{
		return this.getStringColumnValue(getColumnNameHandlerName());
	}
	public void setName(String name)
	{
		this.setColumn(getColumnNameHandlerName(), name);
	}
	public void setHandlerName(String name)
	{
		setName(name);
	}
	public void setHandlerClass(String classString)
	{
		this.setColumn(getColumnNameHandlerClass(), classString);
	}
	public void setHandlerClass(Class theClass)
	{
		this.setColumn(getColumnNameHandlerClass(), theClass.getName());
	}
	public void setNameAndHandlerClass(String name, String classString)
	{
		setName(name);
		setHandlerClass(classString);
	}
	public void setNameAndHandlerClass(String name, Class theClass)
	{
		setName(name);
		setHandlerClass(theClass);
	}
	public void insertStartData()
	{
		try
		{
			ICFileTypeHandlerHome ftHome = (ICFileTypeHandlerHome) getIDOHome(ICFileTypeHandler.class);
			ICFileTypeHandler handler;
			handler = ftHome.create();
			handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_SYSTEM, Class.forName("com.idega.block.media.business.SystemTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(
				IC_FILE_TYPE_HANDLER_APPLICATION,
				Class.forName("com.idega.block.media.business.ApplicationTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_AUDIO, Class.forName("com.idega.block.media.business.AudioTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(
				IC_FILE_TYPE_HANDLER_DOCUMENT,
				Class.forName("com.idega.block.media.business.DocumentTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_IMAGE, Class.forName("com.idega.block.media.business.ImageTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(
				IC_FILE_TYPE_HANDLER_VECTOR_GRAPHICS,
				Class.forName("com.idega.block.media.business.VectorTypeHandler"));
			handler.store();
			handler = ftHome.create();
			handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_VIDEO, Class.forName("com.idega.block.media.business.VideoTypeHandler"));
			handler.store();
			
			handler = ftHome.create();
			handler.setNameAndHandlerClass(IC_FILE_TYPE_HANDLER_ZIP, Class.forName("com.idega.block.media.business.ZipTypeHandler"));
			handler.store();
			handler.cacheEntity();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
	public String getCacheKey()
	{
		return getColumnNameHandlerName();
	}
}

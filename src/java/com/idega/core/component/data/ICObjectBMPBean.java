//idega 2001 - Tryggvi Larusson
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.core.component.data;
//import java.util.*;
import java.sql.SQLException;
import java.text.Collator;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.core.file.data.ICFile;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.PresentationObject;
/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.3

*/
public class ICObjectBMPBean extends com.idega.data.GenericEntity implements com.idega.core.component.data.ICObject
{
	public static final String COMPONENT_TYPE_ELEMENT = "iw.element";
	public static final String COMPONENT_TYPE_BLOCK = "iw.block";
	public static final String COMPONENT_TYPE_APPLICATION = "iw.application";
	public static final String COMPONENT_TYPE_APPLICATION_COMPONENT = "iw.application.component";
	public static final String COMPONENT_TYPE_DATA = "iw.data";
	public static final String COMPONENT_TYPE_HOME = "iw.home";
	public static final String COMPONENT_TYPE_PROPERTYHANDLER = "iw.propertyhandler";
	public static final String COMPONENT_TYPE_INPUTHANDLER = "iw.inputhandler";
	private static final String object_type_column_name = "OBJECT_TYPE";
	private static final String class_name_column_name = "CLASS_NAME";
	private final static String BUNDLE_COLUMN_NAME = "BUNDLE";
	private final static String class_value_column_name = "CLASS_VALUE";
	private final static String icon_file = "ICON_FILE";
	private static final String COLUMN_OBJECT_NAME = "OBJECT_NAME";
	public ICObjectBMPBean()
	{
		super();
	}
	public ICObjectBMPBean(int id) throws SQLException
	{
		super(id);
	}
	public void initializeAttributes()
	{
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute(getColumnObjectName(), "Name", true, true, java.lang.String.class);
		addAttribute(getClassNameColumnName(), "Class Name", true, true, java.lang.String.class);
		addAttribute(getObjectTypeColumnName(), "Class Name", true, true, java.lang.String.class, "one-to-many", ICObjectType.class);
		addAttribute(getBundleColumnName(), "Bundle", true, true, java.lang.String.class, 1000);
		addManyToOneRelationship(getColumnClassValue(), "Class File", ICFile.class);
		addManyToOneRelationship(getColumnIcon(), "Icon", ICFile.class);
		//addAttribute("settings_url","Sl?? stillingas??u",true,true,"java.lang.String");
		//addAttribute("class_value","Klasi sj?lfur",true,true,"java.sql.Blob");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer","many-to-one","com.idega.data.genericentity.Image");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,java.lang.Integer.class);
		//addAttribute("image_id","MyndN?mer",false,false,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.ImageEntity");
		getEntityDefinition().setBeanCachingActiveByDefault(true);
	}
	private String getColumnObjectName() {
		return COLUMN_OBJECT_NAME;
	}
	public static String getObjectTypeColumnName()
	{
		return object_type_column_name;
	}
	public static String getClassNameColumnName()
	{
		return class_name_column_name;
	}
	public static String getColumnClassValue()
	{
		return class_value_column_name;
	}
	public static String getColumnIcon()
	{
		return icon_file;
	}
	private static List componentList;
	public static List getAvailableComponentTypes()
	{
		try
		{
			ICObjectTypeHome gtHome = (ICObjectTypeHome) IDOLookup.getHome(ICObjectType.class);
			List list = new Vector(gtHome.findAll());
			return list;
		}
		catch (IDOLookupException e)
		{
			e.printStackTrace();
		}
		catch (FinderException e1)
		{
			e1.printStackTrace();
		}
		return getAvailableComponentTypesOld();
	}
	private static List getAvailableComponentTypesOld()
	{
		if (componentList == null)
		{
			componentList = new Vector();
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_ELEMENT);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_BLOCK);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_APPLICATION);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_APPLICATION_COMPONENT);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_DATA);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_HOME);
			componentList.add(com.idega.core.component.data.ICObjectBMPBean.COMPONENT_TYPE_PROPERTYHANDLER);
		}
		return componentList;
	}
	public static ICObject getICObject(String className)
	{
		try
		{
			List l = EntityFinder.findAllByColumn(getStaticInstance(ICObject.class), getClassNameColumnName(), className);
			return (ICObject) l.get(0);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public static void removeICObject(String className)
	{
		try
		{
			ICObject instance = (ICObject) com.idega.data.GenericEntity.getStaticInstance(ICObject.class);
			instance.deleteMultiple(getClassNameColumnName(), className);
		}
		catch (Exception e)
		{
		}
	}
	public void insertStartData() throws Exception
	{
		/*ICObject obj = ((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
		obj.setName("Table");
		obj.setObjectClass(Table.class);
		obj.setObjectType("iw.element");
		obj.insert();
		
		obj = ((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
		obj.setName("Image");
		obj.setObjectClass(com.idega.presentation.Image.class);
		obj.setObjectType("iw.element");
		obj.insert();
		
		obj = ((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
		obj.setName("NewsModule");
		obj.setObjectClass(NewsReader.class);
		obj.setObjectType("iw.block");
		obj.insert();
		
		obj = ((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
		obj.setName("TextModule");
		obj.setObjectClass(TextReader.class);
		obj.setObjectType("iw.block");
		obj.insert();
		
		obj = ((com.idega.core.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();
		obj.setName("LoginModule");
		obj.setObjectClass(Login.class);
		obj.setObjectType("iw.block");
		obj.insert();*/
	}
	public String getEntityName()
	{
		return "IC_OBJECT";
	}
	public void setDefaultValues()
	{
		//setColumn("image_id",1);
		//setColumn("small_icon_image_id",1);
		//setObjectType("iw.block");
	}
	public String getName()
	{
		return getStringColumnValue(getColumnObjectName());
	}
	public void setName(String object_name)
	{
		setColumn(getColumnObjectName(), object_name);
	}
	public String getClassName()
	{
		return getStringColumnValue(getClassNameColumnName());
	}
	public void setClassName(String className)
	{
		setColumn(getClassNameColumnName(), className);
	}
	public Class getObjectClass() throws ClassNotFoundException
	{
		String className = getClassName();
		if (className != null)
		{
			return Class.forName(className);
		}
		else
		{
			//throw new ClassNotFoundException("Class NULL not found");
			return null;
		}
	}
	public void setObjectClass(Class c)
	{
		setClassName(c.getName());
	}
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		return (PresentationObject) getObjectClass().newInstance();
	}
	public String getObjectType()
	{
		return getStringColumnValue(getObjectTypeColumnName());
	}
	public void setObjectType(String objectType)
	{
		setColumn(getObjectTypeColumnName(), objectType);
	}
	public String getBundleIdentifier()
	{
		return getStringColumnValue(getBundleColumnName());
	}
	public void setBundleIdentifier(String bundleIdentifier)
	{
		setColumn(getBundleColumnName(), bundleIdentifier);
	}
	public void setBundle(IWBundle bundle)
	{
		setBundleIdentifier(bundle.getBundleIdentifier());
	}
	public IWBundle getBundle(IWMainApplication iwma)throws IWBundleDoesNotExist
	{
		return iwma.getBundle(getBundleIdentifier());
	}
	public static String getBundleColumnName()
	{
		return BUNDLE_COLUMN_NAME;
	}
	public Collection ejbFindAllByObjectType(String type) throws FinderException
	{
		//return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhere().appendEqualsQuoted(this.getObjectTypeColumnName(), type));
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new WildCardColumn());
	    query.addCriteria(new MatchCriteria(table,this.getObjectTypeColumnName(),MatchCriteria.EQUALS,type,true));
	    return idoFindPKsByQuery(query);
	}
	public Collection ejbFindAllByObjectTypeAndBundle(String type, String bundle) throws FinderException
	{
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new WildCardColumn());
	    query.addCriteria(new MatchCriteria(table,this.getObjectTypeColumnName(),MatchCriteria.EQUALS,type,true));
	    query.addCriteria(new MatchCriteria(table,this.getBundleColumnName(),MatchCriteria.EQUALS,bundle,true));
	    
	    /*
		IDOQuery query =
			super.idoQueryGetSelect().appendWhere().appendEqualsQuoted(this.getObjectTypeColumnName(), type)
			.appendAndEqualsQuoted(this.getBundleColumnName(),bundle);
		*/
		//System.out.println(query.toString());
		return super.idoFindPKsByQuery(query);
	}
	public Collection ejbFindAllByBundle(String bundle) throws FinderException{
		//return super.idoFindPKsByQuery(super.idoQueryGetSelect().appendWhere().appendEqualsQuoted(this.getBundleColumnName(), bundle));
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new WildCardColumn());
	    query.addCriteria(new MatchCriteria(table,this.getBundleColumnName(),MatchCriteria.EQUALS,bundle,true));
	    return idoFindPKsByQuery(query);
	}
	public Object ejbFindByClassName(String className) throws FinderException{
		//return super.idoFindOnePKByQuery(super.idoQueryGetSelect().appendWhere().appendEqualsQuoted(getClassNameColumnName(), className));
	    Table table = new Table(this);
	    SelectQuery query = new SelectQuery(table);
	    query.addColumn(new WildCardColumn());
	    query.addCriteria(new MatchCriteria(table,this.getClassNameColumnName(),MatchCriteria.EQUALS,className,true));
	    return idoFindOnePKByQuery(query);
	}
	public Collection ejbFindAllBlocksByBundle(String bundle) throws FinderException
	{
		return ejbFindAllByObjectTypeAndBundle(COMPONENT_TYPE_BLOCK, bundle);
	}
	public Collection ejbFindAllBlocks() throws FinderException
	{
		return ejbFindAllByObjectType(COMPONENT_TYPE_BLOCK);
	}
	public Collection ejbFindAllElementsByBundle(String bundle) throws FinderException
	{
		return ejbFindAllByObjectTypeAndBundle(COMPONENT_TYPE_ELEMENT, bundle);
	}
	public Collection ejbFindAllElements() throws FinderException
	{
		return ejbFindAllByObjectType(COMPONENT_TYPE_ELEMENT);
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		if (obj instanceof ICObject) {
			return Collator.getInstance().compare(this.getName(), ((ICObject)obj).getName());
		}
		return super.compareTo(obj);
	}
}

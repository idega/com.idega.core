//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.presentation.*;
import com.idega.block.news.presentation.NewsReader;
import com.idega.block.text.presentation.TextReader;
import com.idega.block.login.presentation.Login;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import java.util.List;
import java.util.Vector;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class ICObject extends GenericEntity{

        public static final String COMPONENT_TYPE_ELEMENT = "iw.element";
        public static final String COMPONENT_TYPE_BLOCK = "iw.block";
        public static final String COMPONENT_TYPE_APPLICATION = "iw.application";
        public static final String COMPONENT_TYPE_APPLICATION_COMPONENT = "iw.application.component";
        public static final String COMPONENT_TYPE_DATA = "iw.data";
        public static final String COMPONENT_TYPE_PROPERTYHANDLER = "iw.propertyhandler";

        private static final String object_type_column_name = "object_type";
        private static final String class_name_column_name = "class_name";
        private final static String BUNDLE_COLUMN_NAME="bundle";
        private final static String class_value_column_name = "class_value";
        private final static String icon_file = "icon_file";

	public ICObject(){
		super();
	}

	public ICObject(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());
		addAttribute("object_name","Name",true,true,java.lang.String.class);
		addAttribute(getClassNameColumnName(),"Class Name",true,true,java.lang.String.class);
                addAttribute(getObjectTypeColumnName(),"Class Name",true,true,java.lang.String.class,1000);
                addAttribute(getBundleColumnName(),"Bundle",true,true,java.lang.String.class,1000);
                addManyToOneRelationship(getColumnClassValue(),"Class File",ICFile.class);
                addManyToOneRelationship(getColumnIcon(),"Icon",ICFile.class);

                //addAttribute("settings_url","Slóð stillingasíðu",true,true,"java.lang.String");
		//addAttribute("class_value","Klasi sjálfur",true,true,"java.sql.Blob");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer","many-to-one","com.idega.data.genericentity.Image");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,java.lang.Integer.class);
		//addAttribute("image_id","MyndNúmer",false,false,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.ImageEntity");

        }

        public static String getObjectTypeColumnName(){
          return object_type_column_name;
        }

        public static String getClassNameColumnName(){
          return class_name_column_name;
        }

        public static String getColumnClassValue(){
          return class_value_column_name;
        }

        public static String getColumnIcon(){
          return icon_file;
        }

        private static List componentList;

        public static List getAvailableComponentTypes(){
          if(componentList==null){
            componentList = new Vector();
            componentList.add(ICObject.COMPONENT_TYPE_ELEMENT);
            componentList.add(ICObject.COMPONENT_TYPE_BLOCK);
            componentList.add(ICObject.COMPONENT_TYPE_APPLICATION);
            componentList.add(ICObject.COMPONENT_TYPE_APPLICATION_COMPONENT);
            componentList.add(ICObject.COMPONENT_TYPE_DATA);
            componentList.add(ICObject.COMPONENT_TYPE_PROPERTYHANDLER);
          }
          return componentList;
        }

        public static ICObject getICObject(String className){
          try{
            List l = EntityFinder.findAllByColumn(getStaticInstance(ICObject.class),getClassNameColumnName(),className);
            return (ICObject)l.get(0);
          }
          catch(Exception e){
            return null;
          }
        }

        public static void removeICObject(String className){
          try{
            ICObject instance = (ICObject)GenericEntity.getStaticInstance(ICObject.class);
            instance.deleteMultiple(getClassNameColumnName(),className);
          }
          catch(Exception e){
          }
        }

        public void insertStartData()throws Exception{
          /*ICObject obj = new ICObject();
          obj.setName("Table");
          obj.setObjectClass(Table.class);
          obj.setObjectType("iw.element");
          obj.insert();

          obj = new ICObject();
          obj.setName("Image");
          obj.setObjectClass(com.idega.presentation.Image.class);
          obj.setObjectType("iw.element");
          obj.insert();

          obj = new ICObject();
          obj.setName("NewsModule");
          obj.setObjectClass(NewsReader.class);
          obj.setObjectType("iw.block");
          obj.insert();

          obj = new ICObject();
          obj.setName("TextModule");
          obj.setObjectClass(TextReader.class);
          obj.setObjectType("iw.block");
          obj.insert();

          obj = new ICObject();
          obj.setName("LoginModule");
          obj.setObjectClass(Login.class);
          obj.setObjectType("iw.block");
          obj.insert();*/

        }

	public String getEntityName(){
		return "ic_object";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
//                setColumn("small_icon_image_id",1);
            //setObjectType("iw.block");
	}

	public String getName(){
		return getStringColumnValue("object_name");
	}

        public void setName(String object_name) {
                setColumn("object_name",object_name);
        }


	public String getClassName(){
		return getStringColumnValue(getClassNameColumnName());
	}

        public void setClassName(String className){
            setColumn(getClassNameColumnName(),className);
        }

        public Class getObjectClass()throws ClassNotFoundException{
          return Class.forName(getClassName());
        }

        public void setObjectClass(Class c){
          setClassName(c.getName());
        }

	public PresentationObject getNewInstance()throws ClassNotFoundException,IllegalAccessException,InstantiationException{
		return (PresentationObject)getObjectClass().newInstance();
	}


        public String getObjectType(){
          return getStringColumnValue(getObjectTypeColumnName());
        }

        public void setObjectType(String objectType){
          setColumn(getObjectTypeColumnName(),objectType);
        }


        public String getBundleIdentifier(){
          return getStringColumnValue(getBundleColumnName());
        }

        public void setBundleIdentifier(String bundleIdentifier){
          setColumn(getBundleColumnName(),bundleIdentifier);
        }

        public void setBundle(IWBundle bundle){
          setBundleIdentifier(bundle.getBundleIdentifier());
        }

        public IWBundle getBundle(IWMainApplication iwma){
          return iwma.getBundle(getBundleIdentifier());
        }

        public static String getBundleColumnName(){
          return BUNDLE_COLUMN_NAME;
        }

}

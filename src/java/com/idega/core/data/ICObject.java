//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.jmodule.object.*;
import com.idega.block.news.presentation.NewsReader;
import com.idega.block.text.presentation.TextReader;
import com.idega.block.login.presentation.Login;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class ICObject extends GenericEntity{

        private static final String object_type_column_name = "object_type";
        private final static String BUNDLE_COLUMN_NAME="bundle";

	public ICObject(){
		super();
	}

	public ICObject(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());
		addAttribute("object_name","Name",true,true,"java.lang.String");
		addAttribute("class_name","Class Name",true,true,"java.lang.String");
                addAttribute(getObjectTypeColumnName(),"Class Name",true,true,"java.lang.String",1000);
                addAttribute(getBundleColumnName(),"Bundle",true,true,"java.lang.String",1000);
		//addAttribute("settings_url","Slóð stillingasíðu",true,true,"java.lang.String");
		//addAttribute("class_value","Klasi sjálfur",true,true,"java.sql.Blob");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer","many-to-one","com.idega.data.genericentity.Image");
		addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer");
		//addAttribute("image_id","MyndNúmer",false,false,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.ImageEntity");
	}

        public static String getObjectTypeColumnName(){
          return object_type_column_name;
        }

        public void insertStartData()throws Exception{
          ICObject obj = new ICObject();
          obj.setName("Table");
          obj.setObjectClass(Table.class);
          obj.setObjectType("iw.element");
          obj.insert();

          obj = new ICObject();
          obj.setName("Image");
          obj.setObjectClass(com.idega.jmodule.object.Image.class);
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
          obj.insert();

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

        public static String getClassNameColumnName(){
          return "class_name";
        }

	public String getClassName(){
		return getStringColumnValue("class_name");
	}

        public void setClassName(String className){
            setColumn("class_name",className);
        }

        public Class getObjectClass()throws ClassNotFoundException{
          return Class.forName(getClassName());
        }

        public void setObjectClass(Class c){
          setClassName(c.getName());
        }

	public ModuleObject getNewInstance()throws ClassNotFoundException,IllegalAccessException,InstantiationException{
		return (ModuleObject)getObjectClass().newInstance();
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

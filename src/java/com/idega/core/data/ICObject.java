//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.jmodule.object.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class ICObject extends GenericEntity{

	public ICObject(){
		super();
	}

	public ICObject(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());
		addAttribute("object_name","Nafn",true,true,"java.lang.String");
		addAttribute("class_name","Klasanafn",true,true,"java.lang.String");
		//addAttribute("settings_url","Slóð stillingasíðu",true,true,"java.lang.String");
		//addAttribute("class_value","Klasi sjálfur",true,true,"java.sql.Blob");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer","many-to-one","com.idega.data.genericentity.Image");
		addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer");
		//addAttribute("image_id","MyndNúmer",false,false,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.ImageEntity");
	}

	public String getEntityName(){
		return "ic_object";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
                setColumn("small_icon_image_id",1);
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

	public ModuleObject getNewInstance()throws Exception{
		return (ModuleObject)Class.forName(getClassName()).newInstance();
	}

}

package com.idega.core.data;



import com.idega.data.IDOLegacyEntity;

import java.lang.String;

import java.lang.Integer;

import java.sql.SQLException;





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

public class ICFileCategoryBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.ICFileCategory {



  public ICFileCategoryBMPBean() {

    super();

  }



    public ICFileCategoryBMPBean(int id) throws SQLException{

    super(id);

  }



  public void initializeAttributes() {

    addAttribute(getIDColumnName());

    addAttribute("category_type","Type of category",true,true, String.class,255);

    addAttribute("category_name","File category name",true,true, String.class, 255);

    //Temporary Soloution

    //addManyToManyRelationShip(ICFile.class,"ic_file_file_category");

  }



  public String getEntityName() {

    return("ic_file_category");

  }



  public String getName(){

    return (String) getColumnValue("category_name");

  }



  public String getFileCategoryName(){

    return getName();

  }



  public void setFileCategoryName(String fileCategoryName){

    setColumn("category_name", fileCategoryName);

  }



  public void setName(String fileCategoryName){

    setFileCategoryName(fileCategoryName);

  }



  public String getType(){

    return (String) getColumnValue("category_type");

  }



  public String getFileCategoryType(){

    return getType();

  }



  public void setFileCategoryType(String fileCategoryType){

    setColumn("category_type", fileCategoryType);

  }



  public void setType(String fileCategoryType){

    setFileCategoryType(fileCategoryType);

  }





  public void insertStartData() {

    try {

      ICFileCategory cat;

      ICFile file;



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Applications");

      cat.setType("IC_CATEGORY_APPLICATIONS");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Applications");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for applications");

      file.insert();

      file.addTo(cat);



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Audio");

      cat.setType("IC_CATEGORY_AUDIO");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Audio");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for audio and music");

      file.insert();

      file.addTo(cat);



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Documents");

      cat.setType("IC_CATEGORY_DOCUMENTS");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Documents");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for documents");

      file.insert();

      file.addTo(cat);



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Flash");

      cat.setType("IC_CATEGORY_FLASH");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Flash");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for flash movies");

      file.insert();

      file.addTo(cat);



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Images");

      cat.setType("IC_CATEGORY_IMAGES");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Images");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for images");

      file.insert();

      file.addTo(cat);



      cat = ((com.idega.core.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Movies");

      cat.setType("IC_CATEGORY_MOVIES");

      cat.insert();



      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();

      file.setName("Movies");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for movies");

      file.insert();

      file.addTo(cat);



    }

    catch (SQLException sql) {

      sql.printStackTrace(System.err);

    }

  }

}

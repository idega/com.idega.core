package com.idega.core.file.data;



import java.sql.SQLException;

import javax.ejb.CreateException;

import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookupException;





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

public class ICFileCategoryBMPBean extends com.idega.data.GenericEntity implements com.idega.core.file.data.ICFileCategory {



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





  public void insertStartData() throws IDOAddRelationshipException {

    try {

      ICFileCategory cat;

      ICFile file;



      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Applications");

      cat.setType("IC_CATEGORY_APPLICATIONS");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Applications");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for applications");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Audio");

      cat.setType("IC_CATEGORY_AUDIO");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Audio");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for audio and music");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Documents");

      cat.setType("IC_CATEGORY_DOCUMENTS");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Documents");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for documents");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Flash");

      cat.setType("IC_CATEGORY_FLASH");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Flash");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for flash movies");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Images");

      cat.setType("IC_CATEGORY_IMAGES");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Images");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for images");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


      cat = ((com.idega.core.file.data.ICFileCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileCategory.class)).createLegacy();

      cat.setName("Movies");

      cat.setType("IC_CATEGORY_MOVIES");

      cat.insert();



      file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();

      file.setName("Movies");

      file.setMimeType("IC_FOLDER");

      file.setDescription("The default folder for movies");

      file.store();

      //file.addTo(cat);
	  ((ICFileCategoryBMPBean)cat).idoAddTo(file);


    } catch (SQLException sql) {
      sql.printStackTrace(System.err);
    } catch (IDOLookupException e) {
		e.printStackTrace(System.err);
	} catch (CreateException e) {
		e.printStackTrace(System.err);
	}

  }

}

package com.idega.core.data;

import com.idega.data.GenericEntity;
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
public class ICFileType extends GenericType {

  public ICFileType() {
    super();
  }

    public ICFileType(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    super.initializeAttributes();
    addAttribute("ic_file_handler_id","File handler",true,true, Integer.class,"many-to-one",ICFileHandler.class);
  }

  public String getEntityName() {
    return("ic_file_type");
  }
/*

  public void insertStartData() {
    try {
      ICFileType cat;
      ICFile file;

      cat = new ICFileType();
      cat.setName("Applications");
      cat.setType("IC_TYPE_APPLICATIONS");
      cat.insert();

      file = new ICFile();
      file.setName("Applications");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for applications");
      file.insert();
      file.addTo(cat);

      cat = new ICFileType();
      cat.setName("Audio");
      cat.setType("IC_TYPE_AUDIO");
      cat.insert();

      file = new ICFile();
      file.setName("Audio");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for audio and music");
      file.insert();
      file.addTo(cat);

      cat = new ICFileType();
      cat.setName("Documents");
      cat.setType("IC_TYPE_DOCUMENTS");
      cat.insert();

      file = new ICFile();
      file.setName("Documents");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for documents");
      file.insert();
      file.addTo(cat);

      cat = new ICFileType();
      cat.setName("Flash");
      cat.setType("IC_TYPE_FLASH");
      cat.insert();

      file = new ICFile();
      file.setName("Flash");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for flash movies");
      file.insert();
      file.addTo(cat);

      cat = new ICFileType();
      cat.setName("Images");
      cat.setType("IC_TYPE_IMAGES");
      cat.insert();

      file = new ICFile();
      file.setName("Images");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for images");
      file.insert();
      file.addTo(cat);

      cat = new ICFileType();
      cat.setName("Movies");
      cat.setType("IC_TYPE_MOVIES");
      cat.insert();

      file = new ICFile();
      file.setName("Movies");
      file.setMimeType("IC_FOLDER");
      file.setDescription("The default folder for movies");
      file.insert();
      file.addTo(cat);

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }*/
}
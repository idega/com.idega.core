package com.idega.core.data;

import com.idega.data.GenericEntity;
import java.lang.String;
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
public class ICFileHandler extends GenericEntity {

  public ICFileHandler() {
    super();
  }

    public ICFileHandler(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("handler_name","Filehandler name",true,true, String.class,255);
    addAttribute("handler_class","Filehandler class",true,true, String.class,500);
  }

  public String getEntityName() {
    return("ic_file_handler");
  }

/**@todo insert default fileHandler
  public void insertStartData() {
    try {

    }
    catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

       *
     */
}
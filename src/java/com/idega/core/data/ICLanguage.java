package com.idega.core.data;

import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        idegaWeb Classes
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="bjarni@idega.is">Bjarni Viljhalmsson</a>
 * @version 1.0
 */

public class ICLanguage extends GenericEntity {

  public ICLanguage() {
    super();
  }

  public ICLanguage(int id) throws SQLException{
    super(id);
  }


  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("name","nafn",true,true, "java.lang.String");
    addAttribute("description","lýsing",true,true, "java.lang.String", 510);
    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
  }
  public String getEntityName() {
    return "ic_language";
    /**@todo: implement this com.idega.data.GenericEntity abstract method*/
  }

  public String getName(){
    return (String) getColumnValue("name");
  }

  public String getDescription(){
    return (String) getColumnValue("description");
  }

  public void setName(String Name){
    setColumn("name", Name);
  }

  public void setDescription(String description){
    setColumn("description", description);
  }
}
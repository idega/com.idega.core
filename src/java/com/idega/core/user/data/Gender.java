package com.idega.core.user.data;

import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class Gender extends GenericEntity {

  public Gender() {
    super();
  }

  public Gender(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getNameColumnName(),"Nafn",true,true,"java.lang.String");
    this.addAttribute(getDescriptionColumnName(),"Description",true,true,"java.lang.String",1000);
  }
  public String getEntityName() {
    return "ic_gender";
  }

  public void insertStartData() throws SQLException {
    Gender male = new Gender();
    male.setName("male");
    male.insert();

    Gender female = new Gender();
    female.setName("female");
    female.insert();

  }

  public static String getNameColumnName(){
    return "name";
  }

  public static String getDescriptionColumnName(){
    return "description";
  }

  public void setName(String name){
    this.setColumn(getNameColumnName(),name);
  }

  public void setDescription(String description){
    this.setColumn(getDescriptionColumnName(),description);
  }

  public String getName(){
    return this.getStringColumnValue(getNameColumnName());
  }

  public String getDescription(){
    return this.getStringColumnValue(getDescriptionColumnName());
  }

  public Gender getStaticInstance(){
    return (Gender)this.getStaticInstance(Gender.class);
  }
}
package com.idega.core.user.data;

import java.sql.SQLException;
import com.idega.data.GenericEntity;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GenderBMPBean extends com.idega.data.GenericEntity implements com.idega.core.user.data.Gender {

  public static final String NAME_MALE="male";
  public static final String NAME_FEMALE="female";

  public GenderBMPBean() {
    super();
  }

  public GenderBMPBean(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getNameColumnName(),"Nafn",true,true,"java.lang.String");
    this.addAttribute(getDescriptionColumnName(),"Description",true,true,"java.lang.String",1000);
    getEntityDefinition().setBeanCachingActiveByDefault(true);
  }
  public String getEntityName() {
    return "ic_gender";
  }

  public void insertStartData() throws SQLException {
    Gender male = ((com.idega.core.user.data.GenderHome)com.idega.data.IDOLookup.getHomeLegacy(Gender.class)).createLegacy();
    male.setName(NAME_MALE);
    male.insert();

    Gender female = ((com.idega.core.user.data.GenderHome)com.idega.data.IDOLookup.getHomeLegacy(Gender.class)).createLegacy();
    female.setName(NAME_FEMALE);
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
    return (Gender)GenericEntity.getStaticInstance(Gender.class);
  }
}

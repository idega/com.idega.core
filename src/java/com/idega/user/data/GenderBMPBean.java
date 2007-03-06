package com.idega.user.data;

import javax.ejb.FinderException;
import java.util.Iterator;
import java.util.Collection;
import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class GenderBMPBean extends com.idega.data.GenericEntity implements Gender{

  public static final String NAME_MALE="male";
  public static final String NAME_FEMALE="female";

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getNameColumnName(),"Nafn",true,true,"java.lang.String");
    this.addAttribute(getDescriptionColumnName(),"Description",true,true,"java.lang.String",1000);
  }
  
  public String getEntityName() {
    return "ic_gender";
  }

  public void insertStartData() throws SQLException {

    try{
      Gender male = ((GenderHome)IDOLookup.getHome(Gender.class)).create();
      male.setName(NAME_MALE);
      male.store();

      Gender female = ((GenderHome)IDOLookup.getHome(Gender.class)).create();
      female.setName(NAME_FEMALE);
      female.store();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }
  
  public boolean isFemaleGender() {
  		return getName().equals(NAME_FEMALE);
  }

  public boolean isMaleGender() {
		return getName().equals(NAME_MALE);
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


  public Gender ejbHomeGetMaleGender() throws FinderException {
    return ((GenderHome)this.getEJBLocalHome()).findByGenderName(NAME_MALE);
  }

  public Gender ejbHomeGetFemaleGender() throws FinderException {
    return ((GenderHome)this.getEJBLocalHome()).findByGenderName(NAME_FEMALE);
  }

  public Integer ejbFindByGenderName(String name) throws FinderException {
   Collection genders = super.idoFindAllIDsByColumnBySQL(getNameColumnName(),name);
   Iterator iter = genders.iterator();
   Integer gender = null;
    if( iter.hasNext() ) {
       gender = (Integer) iter.next();
    }
    else{
     throw new FinderException("Gender named : "+name+" not found");
    }

    return gender;
  }

  public Collection ejbFindAllGenders()throws FinderException{
    return super.idoFindAllIDsBySQL();
  }
}
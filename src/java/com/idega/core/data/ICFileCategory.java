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

public class ICFileCategory extends GenericEntity {

  public ICFileCategory() {
    super();
  }

    public ICFileCategory(int id) throws SQLException{
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute("category_type","Type of category",true,true, String.class,255);
    addAttribute("category_name","File category name",true,true, String.class, 255);
    addAttribute("parent_id","Parent",true,true, Integer.class,"many-to-one",ICFileCategory.class);
    setNullable("parent_id",true);

    addManyToManyRelationShip(ICFile.class,"ic_file_file_category");
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

  public void setParentID(int parentID){
    setColumn("parent_id", new Integer(parentID));
  }

  public int getParentID(){
    return getIntColumnValue("parent_id");
  }
}
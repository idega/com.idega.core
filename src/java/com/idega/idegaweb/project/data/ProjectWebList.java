package com.idega.idegaweb.project.data;

import com.idega.data.*;
import java.sql.SQLException;


/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */

public class ProjectWebList extends GenericEntity {

  private static String[] sColumns = {"project_name"};
  private static String sClassName = "com.idega.idegaweb.project.data.ProjectWebList";

  public ProjectWebList(){
    super();
  }

  public ProjectWebList(int id) throws SQLException {
    super(id);
  }


  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(getProjectNameColumnName(),"Nafn verkefnis", true, true, "java.lang.String");
  }

  public String getEntityName() {
    return "pw_weblist";
  }

  public String getName(){
    return getProjectName();
  }


  public static String getProjectNameColumnName(){
    return sColumns[0];
  }

  public void setProjectName(String name){
    this.setColumn(getProjectNameColumnName(), name);
  }

  public String getProjectName(){
    return this.getStringColumnValue(getProjectNameColumnName());
  }


  public static ProjectWebList getStaticInstance(){
    return (ProjectWebList)ProjectWebList.getStaticInstance(sClassName);
  }



}